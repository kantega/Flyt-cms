package no.kantega.publishing.common.util;

import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.client.device.DeviceCategory;

import java.util.regex.Pattern;

public class TemplateMacroHelper {
    private static final Pattern SITE_PATTERN = Pattern.compile("\\$SITE");
    private static final Pattern DEVICE_PATTERN = Pattern.compile("\\$DEVICE");
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\$LANGUAGE");

    public static boolean containsMacro(String templateFilename) {
        return templateFilename.contains("$SITE") || templateFilename.contains("$DEVICE") || templateFilename.contains("$LANGUAGE");
    }

    public static String replaceMacros(String templateFilename, Site site, DeviceCategory deviceCategory) {
        return replaceMacros(templateFilename, site, deviceCategory, null);
    }

    public static String replaceMacros(String templateFilename, Site site, DeviceCategory deviceCategory, Integer language) {

        if (templateFilename.contains("$SITE")) {
            String alias = site.getAlias();
            templateFilename = SITE_PATTERN.matcher(templateFilename).replaceAll(alias.substring(0, alias.length() - 1));
        }

        if (templateFilename.contains("$DEVICE")) {
            templateFilename = DEVICE_PATTERN.matcher(templateFilename).replaceAll(deviceCategory.toString());
        }

        if (language != null && templateFilename.contains("$LANGUAGE")) {
            templateFilename = LANGUAGE_PATTERN.matcher(templateFilename).replaceAll(Language.getLanguageAsISOCode(language));
        }

        return templateFilename;
    }

}
