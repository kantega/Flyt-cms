package no.kantega.publishing.spring;

import no.kantega.publishing.api.content.ContentAliasDao;
import no.kantega.publishing.client.AliasRequestHandler;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

public class AksessAliasHandlerMapping extends ContentEventListenerAdapter implements HandlerMapping, Ordered {
    private static Logger LOG = LoggerFactory.getLogger(AksessAliasHandlerMapping.class);

    public static final String HANDLED_OA_ALIAS = AksessAliasHandlerMapping.class.getName() + "_HANDLED_OA_ALIAS";

    @Autowired
    private AliasRequestHandler requestHandler;

    @Autowired
    private ContentAliasDao contentAliasDao;

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private Set<String> aliases;
    private int order;

    @PostConstruct
    public void init(){
        aliases = new HashSet<>(contentAliasDao.getAllAliases());
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        LOG.debug("Checking request {} for match on alias", request.getRequestURI());
        HandlerExecutionChain handlerExecutionChain = null;
        String lookupPath = urlPathHelper.getLookupPathForRequest(request);
        lookupPath = addTrailingSlash(lookupPath);

        if(aliases.contains(lookupPath)){
            LOG.debug("{} matches alias", lookupPath);
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

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
