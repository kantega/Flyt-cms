package no.kantega.publishing.spring;

import org.apache.velocity.context.Context;
import org.springframework.web.servlet.view.velocity.VelocityView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class CustomVelocityView extends VelocityView {
    @Override
    protected void exposeHelpers(Context context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        context.put("contextPath", request.getContextPath());
    }
}
