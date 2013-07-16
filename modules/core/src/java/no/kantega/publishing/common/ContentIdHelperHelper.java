package no.kantega.publishing.common;

import no.kantega.publishing.common.util.PrettyURLEncoder;

public class ContentIdHelperHelper {
    public static final String basePattern = "(?<protocol>https?://)?(?<hostname>[\\w\\.\\-]+)(?<port>:\\d+)?";
    public static final String prettyPattern = "/content/(?<PrettythisId>\\d+)/" + PrettyURLEncoder.LEGAL_URL_PATTERN;
    public static final String contentApPattern = "/content.ap" +
            "((&|\\?)(" +
               "(thisId=(?<thisId>\\d+))" +
               "|(contentId=(?<contentId>\\d+))" +
               "|(version=(?<version>\\d+))" +
               "|(language=(?<language>\\d+))" +
            "))+";
}
