package no.kantega.openaksess.search.solr.config;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import no.kantega.search.api.IndexableDocumentCustomizer;
import no.kantega.search.api.provider.DocumentTransformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class IndexableDocumentCustomizerPostProcessor {

    @SuppressWarnings("unchecked")
    public static Map<DocumentTransformer<?>, Collection<IndexableDocumentCustomizer<?>>>
            mapCustomizersToTransformers(List<DocumentTransformer<?>> transformers, List<IndexableDocumentCustomizer<?>> customizers){

        Multimap<Class<?>,IndexableDocumentCustomizer<?>> customizersByClass = Multimaps.index(customizers, new Function<IndexableDocumentCustomizer<?>, Class<?>>() {
            @Override
            public Class<?> apply(IndexableDocumentCustomizer<?> input) {

                return input.typeHandled();
            }
        });

        Multimap<DocumentTransformer<?>, IndexableDocumentCustomizer<?>> mapping = ArrayListMultimap.create();

        for (DocumentTransformer<?> transformer : transformers) {
            Class<?> typeHandled = transformer.typeHandled();
            List  indexableDocumentCustomizers = new ArrayList(customizersByClass.get(typeHandled));
            transformer.setIndexableDocumentCustomizers(indexableDocumentCustomizers);
            mapping.putAll(transformer, indexableDocumentCustomizers);
        }

        return mapping.asMap();
    }
}
