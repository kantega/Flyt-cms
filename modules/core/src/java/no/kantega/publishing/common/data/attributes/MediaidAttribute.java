package no.kantega.publishing.common.data.attributes;

import org.w3c.dom.Element;

import java.util.Map;

import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.commons.exception.SystemException;

/**
 * User: Terje Røstum, Kantega AS
 * Date: Jan 12, 2010
 * Time: 10:12:15 AM
 */

public class MediaidAttribute extends Attribute {
    protected boolean multiple = false;
    protected int maxitems = Integer.MAX_VALUE;

    public void setConfig(Element config, Map model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            String multiple = config.getAttribute("multiple");
            if ("true".equalsIgnoreCase(multiple)) {
                this.multiple = true;
            }
            String maxitemsS = config.getAttribute("maxitems");
            if(maxitemsS != null && maxitemsS.trim().length() > 0) {
                maxitems = Integer.parseInt(maxitemsS);
            }
        }
    }

    public String getRenderer() {
        if (multiple) {
            return "media_multiple";
        } else {
            return "media";
        }
    }

    public int getMaxitems() {
        return maxitems;
    }
}
