package no.kantega.publishing.admin.taglib;

import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.*;

public class PrintOrgUnitNavigatorTag  extends SimpleTagSupport {
    private static final Logger log = LoggerFactory.getLogger(PrintOrgUnitNavigatorTag.class);

    private static final String SOURCE = "no.kantega.publishing.admin.taglib.PrintOrgUnitNavigatorTag";

    private Set openUnits;
    private String cssClass;


    /**
     * Prints a orgunit
     *
     * @param orgUnit
     * @param manager
     * @throws java.io.IOException
     */
    private void printUnit(OrgUnit orgUnit, OrganizationManager manager) throws IOException {
        JspWriter out = getJspContext().getOut();

        out.write("<ul class=\"navigator");
        if (cssClass != null && cssClass.trim().length() > 0) {
            out.write(" " + cssClass);
        }
        out.write("\">");
        out.write("<li>");

        List<OrgUnit> childUnits = new ArrayList<OrgUnit>();
        if (orgUnit == null || (openUnits != null && openUnits.contains(orgUnit.getExternalId()))) {
            if (manager != null) {
                childUnits = manager.getChildUnits(orgUnit);
            }
        }

        StringBuilder href = new StringBuilder();
        String name;
        if (orgUnit != null) {
            name = orgUnit.getName();
            href.append("?");
            href.append(AdminRequestParameters.ITEM_IDENTIFIER).append("=").append(orgUnit.getExternalId());
        } else {
            name = "Organisasjon";
        }

        String openState = childUnits.size() > 0 ? "open": "closed";
        out.write("<span class=\"openState\"><a href=\"" + href + "\" class=\"" + openState + "\"></a></span>");
        out.write("<span class=\"icon\"><a href=\"" + href + "\" class=\"orgunit\"></a></span>");
        out.write("<span class=\"title\"><a href=\""+ href +"\" class=\"orgunit\" title=\"" + name + "\">" + name +"</a></span>");

        if (childUnits.size() > 0) {
            for (OrgUnit childUnit : childUnits) {
                printUnit(childUnit, manager);
            }
        }

        out.write("</li>");
        out.write("</ul>");
    }


    @Override
    public void doTag() throws JspException, IOException {
        try {
            if (openUnits == null) {
                openUnits = new TreeSet();
            }

            ApplicationContext context = RootContext.getInstance();
            Map managers = context.getBeansOfType(OrganizationManager.class);
            Iterator i  = managers.values().iterator();
            OrganizationManager manager = null;
            if (i.hasNext()) {
                manager = (OrganizationManager) i.next();
            }
            printUnit(null, manager);
        } catch (Exception e) {
            log.error("Could not print org unit", e);
            throw new JspTagException(SOURCE, e);
        }
        openUnits = null;
    }

    public void setOpenUnits(Set openUnits) {
        this.openUnits = openUnits;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}
