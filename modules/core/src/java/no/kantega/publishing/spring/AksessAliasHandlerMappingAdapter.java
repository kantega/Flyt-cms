package no.kantega.publishing.spring;

import no.kantega.publishing.client.AliasRequestHandler;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AksessAliasHandlerMappingAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return handler instanceof AliasRequestHandler;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return ((AliasRequestHandler)handler).handleAlias(request, response);
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1L;
    }
}
