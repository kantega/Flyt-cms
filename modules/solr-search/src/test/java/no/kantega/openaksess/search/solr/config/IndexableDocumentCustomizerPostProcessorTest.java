package no.kantega.openaksess.search.solr.config;

import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.search.api.IndexableDocumentCustomizer;
import no.kantega.search.api.IndexableDocumentCustomizerAdapter;
import no.kantega.search.api.provider.DocumentTransformer;
import no.kantega.search.api.provider.DocumentTransformerAdapter;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IndexableDocumentCustomizerPostProcessorTest {

    @Test
    public void testMapCustomizersToTransformers() {
        T1 t1 = new T1();
        T2 t2 = new T2();
        List<DocumentTransformer<?>> transformers = Arrays.<DocumentTransformer<?>>asList(t1, t2);
        C1 c1 = new C1();
        C2 c2 = new C2();
        List<IndexableDocumentCustomizer<?>> customizers = Arrays.<IndexableDocumentCustomizer<?>>asList(c1, c2);
        Map<DocumentTransformer<?>,Collection<IndexableDocumentCustomizer<?>>> mapping = IndexableDocumentCustomizerPostProcessor.mapCustomizersToTransformers(transformers, customizers);

        assertEquals("Wrong map size", 2, mapping.size());

        assertTrue("T1 did not contain C1", mapping.get(t1).contains(c1));
        assertEquals("Wrong number of map entries", 1, mapping.get(t1).size());

        assertTrue("T2 did not contain C2", mapping.get(t2).contains(c2));
        assertEquals("Wrong number of map entries", 1, mapping.get(t2).size());

        for (DocumentTransformer<?> transformer : transformers) {
            List<? extends IndexableDocumentCustomizer<?>> indexableDocumentCustomizers = ((DocumentTransformerAdapter<?>) transformer).getIndexableDocumentCustomizers();
            assertEquals("Transformer did not have customizer", 1, indexableDocumentCustomizers.size());
        }
    }

    private class T1 extends DocumentTransformerAdapter<Content> {

        protected T1() {
            super(Content.class);
        }
    }

    private class T2 extends DocumentTransformerAdapter<Attachment> {

        protected T2() {
            super(Attachment.class);
        }
    }

    private class C1 extends IndexableDocumentCustomizerAdapter<Content> {

        protected C1() {
            super(Content.class);
        }
    }

    private class C2 extends IndexableDocumentCustomizerAdapter<Attachment> {

        protected C2() {
            super(Attachment.class);
        }
    }
}
