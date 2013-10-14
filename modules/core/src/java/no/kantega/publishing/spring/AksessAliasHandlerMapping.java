package no.kantega.publishing.spring;

import no.kantega.publishing.api.content.ContentAliasDao;
import no.kantega.publishing.client.ContentRequestHandler;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class AksessAliasHandlerMapping extends ContentEventListenerAdapter implements HandlerMapping {

    public static final String HANDLED_OA_ALIAS = AksessAliasHandlerMapping.class.getName() + "_HANDLED_OA_ALIAS";

    @Autowired
    private ContentRequestHandler requestHandler;

    @Autowired
    private ContentAliasDao contentAliasDao;

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private Set<String> aliases;

    @PostConstruct
    public void init(){
        aliases = new HashSet<>(contentAliasDao.getAllAliases());

    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        HandlerExecutionChain handlerExecutionChain = null;
        String lookupPath = urlPathHelper.getPathWithinServletMapping(request);
        lookupPath = addTrailingSlash(lookupPath);

        if(aliases.contains(lookupPath)){
            handlerExecutionChain = new HandlerExecutionChain(requestHandler);
            request.setAttribute(HANDLED_OA_ALIAS, lookupPath);
        }
        return handlerExecutionChain;
    }

    private String addTrailingSlash(String lookupPath) {
        if(!lookupPath.endsWith("/")){
            lookupPath += "/";
        }
        return lookupPath;
    }
}
