package no.kantega.openaksess.search.taglib.label.resolver;

public interface LabelResolver {
    public String handledPrefix();
    public String resolveLabel(String key);
}
