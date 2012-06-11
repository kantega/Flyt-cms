package no.kantega.publishing.common.util;

import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.client.device.DeviceCategory;
import no.kantega.publishing.common.data.enums.Language;

public class TemplateMacroHelper {

    public static boolean containsMacro(String templateFilename) {
        return templateFilename.contains("$SITE") || templateFilename.contains("$DEVICE") || templateFilename.contains("$LANGUAGE");
    }

    public static String replaceMacros(String templateFilename, Site site, DeviceCategory deviceCategory) {
        return replaceMacros(templateFilename, site, deviceCategory, null);
    }

    public static String replaceMacros(String templateFilename, Site site, DeviceCategory deviceCategory, Integer language) {

        if (templateFilename.contains("$SITE")) {
            String alias = site.getAlias();
            templateFilename = templateFilename.replaceAll("\\$SITE", alias.substring(0, alias.length() - 1));
        }

        if (templateFilename.contains("$DEVICE")) {
            templateFilename = templateFilename.replaceAll("\\$DEVICE", deviceCategory.toString());
        }

        if (language != null && templateFilename.contains("$LANGUAGE")) {
            templateFilename = templateFilename.replaceAll("\\$LANGUAGE", Language.getLanguageAsISOCode(language));
        }

        return templateFilename;
    }

}
