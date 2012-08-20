package no.kantega.openaksess.search.taglib.label;

import no.kantega.commons.util.LocaleLabels;
import no.kantega.openaksess.search.taglib.label.resolver.LabelResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * AksessSearchTermTranslator
 */
@Component
public class FacetLabelResolver {

    private Map<String, LabelResolver> labelResolvers;

    private Map<String,String> facetValueToLabelKeys;


    @Cacheable(value = "FacetLabelCache", key = "{#p0, #p1, #p2.language}")
    public String resolveLabel(String key, String bundle, Locale locale) {
        String label;

        if(facetValueToLabelKeys.containsKey(key)){
            key = facetValueToLabelKeys.get(key);
            label = LocaleLabels.getLabel(key, bundle, locale);
        }else {
            label = resolveLabelFromLabelResolvers(key);
        }

        return label;
    }

    private String resolveLabelFromLabelResolvers(String key) {
        String[] labelTypeAndKey = key.split("\\.");

        throwIfNotLengthTwo(key, labelTypeAndKey);

        LabelResolver labelResolver = labelResolvers.get(labelTypeAndKey[0]);
        if(labelResolver == null){
            return null;
        }

        return labelResolver.resolveLabel(labelTypeAndKey[1]);
    }

    @Autowired
    public void setLabelResolvers(Collection<LabelResolver> labelResolvers){
        this.labelResolvers = new HashMap<String, LabelResolver>();
        for (LabelResolver labelResolver : labelResolvers) {
            this.labelResolvers.put(labelResolver.handledPrefix(), labelResolver);
        }
    }

    private void throwIfNotLengthTwo(String key, String[] labelTypeAndKey) {
        if(labelTypeAndKey.length != 2){
            throw new IllegalArgumentException(String.format("Label key %s was not on the form type.key", key));
        }
    }

    @Resource(name = "facetValueToLabelKeys")
    public void setFacetValueToLabelKeys(Map<String, String> facetValueToLabelKeys) {
        this.facetValueToLabelKeys = facetValueToLabelKeys;
    }
}
