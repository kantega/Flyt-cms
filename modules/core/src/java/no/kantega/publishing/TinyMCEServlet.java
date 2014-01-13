package no.kantega.publishing;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Compressor for TinyMCE JavaScript files.
 * Based on: tiny_mce_gzip.jsp 535 2008-01-14 15:01:34Z spocke by Moxiecode
 *
 * Date: Aug 24, 2010
 * Time: 12:28:19 PM
 *
 * @author tarkil
 *
 */
public class TinyMCEServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(TinyMCEServlet.class);
    private final long MAX_DISK_CACHE_AGE = 1000*60*60*24;

    private boolean shouldRecreateDiskCache = true;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException("POST is not supported.");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cacheKey = "";
        String cacheFile = "";

        ServletOutputStream outputStream = response.getOutputStream();

        // Get input
        String[] plugins = getParam(request, "plugins", "").split(",");
        String[] languages = getParam(request, "languages", "").split(",");
        String[] themes = getParam(request, "themes", "").split(",");
        boolean diskCache = getParam(request, "diskcache", "").equals("true");
        boolean isJS = getParam(request, "META-INF/resources/aksess/js", "").equals("true");
        boolean compress = getParam(request, "compress", "true").equals("true");
        boolean core = getParam(request, "core", "true").equals("true");
        String suffix = getParam(request, "suffix", "").equals("_src") ? "_src" : "";
        String cachePath = getCachePath(); // Cache path, this is where the .gz files will be stored
        int expiresOffset = 3600 * 24; // Cache for 1 days in browser cache

        // Headers
        response.setContentType("text/javascript");
        response.addHeader("Vary", "Accept-Encoding"); // Handle proxies
        response.setDateHeader("Expires", System.currentTimeMillis() + (expiresOffset * 1000));

        // Is called directly then auto init with default settings
        if (!isJS) {
            outputStream.print(getFileContents(mapUrl(request, "tiny_mce_gzip.js")));
            outputStream.print("tinyMCE_GZ.init({});");
            return;
        }

        // Setup cache info
        if (diskCache) {
            cacheKey = createCacheKey(getParam(request, "plugins", "") + getParam(request, "languages", "") + getParam(request, "themes", ""));

            if (compress) {
                cacheFile = cachePath + "tiny_mce_" + cacheKey + ".gz";
            } else {
                cacheFile = cachePath + "tiny_mce_" + cacheKey + ".js";
            }
        }

        // Check if it supports gzip
        boolean supportsGzip = false;
        if (request.getHeader("Accept-Encoding") != null) {
            String encoding = request.getHeader("Accept-Encoding");
            encoding = encoding.replaceAll("\\s+", "").toLowerCase();
            supportsGzip = encoding.contains("gzip") || request.getHeader("---------------") != null;
            encoding = encoding.contains("x-gzip") ? "x-gzip" : "gzip";

            if (supportsGzip && compress) {
                response.addHeader("Content-Encoding", encoding);
            }
        }


        URL coreUrl = null;
        if (core) {
            coreUrl = mapUrl(request, "tiny_mce" + suffix + ".js");
        }

        List<URL> urls = getUrlList(request, languages, themes, plugins, suffix);

        // Use cached file disk cache
        if (diskCache && supportsGzip && new File(cacheFile).exists()) {
            if (cacheFileIsUpdated(cacheFile)) {
                writeContentFromCacheFile(cacheFile, outputStream);
                outputStream.close();
                return;
            }
        }

        String content = "";
        // Add core
        if (core) {
            content += getFileContents(coreUrl);

            // Patch loading functions
            content += "tinyMCE_GZ.start();";
        }

        for (URL url : urls) {
            content += getFileContents(url);
        }

        // Restore loading functions
        if (core) {
            content += "tinyMCE_GZ.end();";
        }

        // Generate GZIP'd content
        if (supportsGzip) {
            if (diskCache && isNotBlank(cacheKey)) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                // Gzip compress
                if (compress) {
                    GZIPOutputStream gzipStream = new GZIPOutputStream(bos);
                    gzipStream.write(content.getBytes("iso-8859-1"));
                    gzipStream.close();
                } else {
                    OutputStreamWriter bow = new OutputStreamWriter(bos);
                    bow.write(content);
                    bow.close();
                }

                // Write to file
                try {
                    log.info( "Creating diskcache:" + cacheFile);

                    FileOutputStream fout = new FileOutputStream(cacheFile);
                    fout.write(bos.toByteArray());
                    fout.close();
                } catch (IOException e) {
                    log.error( "IOException while trying to write to cache file: " + cacheFile + ". This does not affect functionality.", e);
                }

                // Write to stream
                outputStream.write(bos.toByteArray());
                outputStream.close();

                shouldRecreateDiskCache = false;
            } else {
                log.info( "Sending content without using diskcache");
                GZIPOutputStream gzipStream = new GZIPOutputStream(outputStream);
                gzipStream.write(content.getBytes("iso-8859-1"));
                gzipStream.close();
            }
        } else {
            log.info( "Sending content without using diskcache and without zip compression");
            outputStream.write(content.getBytes());
        }
    }

    private void writeContentFromCacheFile(String cacheFile, ServletOutputStream outputStream) throws IOException {
        FileInputStream fin;
        byte[] buffer;
        int bytes;
        log.info( "Writing content from cache:" + cacheFile);

        fin = new FileInputStream(cacheFile);
        buffer = new byte[1024];

        while ((bytes = fin.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, bytes);
        }

        fin.close();
    }

    private List<URL> getUrlList(HttpServletRequest request, String[] languages, String[] themes, String[] plugins, String suffix) {
        List<URL> urls = new ArrayList<URL>();

        addLanguages(request, languages, urls);

        addThemes(request, languages, themes, suffix, urls);

        addPlugins(request, languages, plugins, suffix, urls);
        return urls;
    }

    private void addPlugins(HttpServletRequest request, String[] languages, String[] plugins, String suffix, List<URL> urls) {
        for (String plugin : plugins) {
            urls.add(mapUrl(request, "plugins/" + plugin + "/editor_plugin" + suffix + ".js"));

            for (String language : languages) {
                urls.add(mapUrl(request, "plugins/" + plugin + "/langs/" + language + ".js"));
            }
        }
    }

    private void addThemes(HttpServletRequest request, String[] languages, String[] themes, String suffix, List<URL> urls) {
        for (String theme : themes) {
            urls.add(mapUrl(request, "themes/" + theme + "/editor_template" + suffix + ".js"));

            for (String language : languages) {
                urls.add(mapUrl(request, "themes/" + theme + "/langs/" + language + ".js"));
            }
        }
    }

    private void addLanguages(HttpServletRequest request, String[] languages, List<URL> urls) {
        for (String language : languages) {
            urls.add(mapUrl(request, "langs/" + language + ".js"));
        }
    }

    private boolean cacheFileIsUpdated(String cacheFile) {
        if (shouldRecreateDiskCache) {
            return false;
        }

        long cacheLastModified = new File(cacheFile).lastModified();
        long now = new Date().getTime();
        return !(now - cacheLastModified > MAX_DISK_CACHE_AGE);
    }

    private String getParam(HttpServletRequest request, String name, String def) {
        String value = request.getParameter(name) != null ? "" + request.getParameter(name) : def;
        return value.replaceAll("[^0-9a-zA-Z\\-_,]+", "");
    }

    private String getFileContents(URL url) {
        String retVal = "";
        if (url != null) {
            try {
                InputStream is = url.openStream();
                retVal = IOUtils.toString(is, "iso-8859-1");
            } catch (IOException e) {
                log.error( e.getMessage(), e);
            }
        }
        return retVal;
    }

    private String getCachePath() {
        String tempdir = System.getProperty("java.io.tmpdir");
        if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
            tempdir = tempdir + File.separator;
        }
        return tempdir;
    }

    private URL mapUrl(HttpServletRequest request, String path) {
        URL url = null;
        String relPath = "/aksess/tiny_mce/" + path;
        try {
            url = request.getSession().getServletContext().getResource(relPath);
        } catch (MalformedURLException e) {
            log.error( e.getMessage(), e);
        }
        return url;
    }

    private String createCacheKey(String str) {
        try {
            java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");

            char[] charArray = str.toCharArray();
            byte[] byteArray = new byte[charArray.length];

            for (int i=0; i<charArray.length; i++)
                byteArray[i] = (byte) charArray[i];

            byte[] md5Bytes = md5.digest(byteArray);
            StringBuilder hexValue = new StringBuilder();

            for (byte md5Byte : md5Bytes) {
                int val = ((int) md5Byte) & 0xff;

                if (val < 16)
                    hexValue.append("0");

                hexValue.append(Integer.toHexString(val));
            }

            return hexValue.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // Ignore
        }

        return "";
    }
}
