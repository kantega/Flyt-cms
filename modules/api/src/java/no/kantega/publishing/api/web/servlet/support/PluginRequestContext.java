package no.kantega.publishing.api.web.servlet.support;

/**
 *
 */
public interface PluginRequestContext {

    /**
     * Request attribute that the RequestContext will be exposed as
     */
    static final String PLUGIN_REQUEST_CONTEXT_ATTRIBUTE = "pluginRequestContext";

    /**
     * Request attribute name for setting a list of priority MessageSources for a plugin request context
     */
    static final String PRIORITY_MESSAGE_SOURCE_ATTR = PluginRequestContext.class.getName() +"_PRIORITY_MESSAGE_SOURCE_ATTRIBUTE";


    /**
     * Return a message for the given code
     * @param code code of the message
     * @return the message
     */
    public String getMessage(String code);

    /**
     * Return a message for the given code and arguments
     * @param code code of the message
     * @param args arguments
     * @param htmlEscape HTML escape the message? 
     * @return the message
     */
	public String getMessage(String code, Object[] args, boolean htmlEscape);
}
