package no.kantega.publishing;

import no.kantega.commons.log.Log;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException("POST is not supported.");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cacheKey = "";
        String cacheFile = "";
        String content = "";
        String enc;
        String suffix;
        String cachePath;
        String[] plugins;
        String[] languages;
        String[] themes;
        boolean diskCache;
        boolean supportsGzip;
        boolean isJS;
        boolean compress;
        boolean core;
        int i;
        int x;
        int bytes;
        int expiresOffset;
        OutputStreamWriter bow;
        ByteArrayOutputStream bos;
        GZIPOutputStream gzipStream;
        FileOutputStream fout;
        FileInputStream fin;
        byte buff[];
        ServletOutputStream outputStream = response.getOutputStream();

        // Get input
        plugins = getParam(request, "plugins", "").split(",");
        languages = getParam(request, "languages", "").split(",");
        themes = getParam(request, "themes", "").split(",");
        diskCache = getParam(request, "diskcache", "").equals("true");
        isJS = getParam(request, "js", "").equals("true");
        compress = getParam(request, "compress", "true").equals("true");
        core = getParam(request, "core", "true").equals("true");
        suffix = getParam(request, "suffix", "").equals("_src") ? "_src" : "";
        cachePath = getCachePath(request, "."); // Cache path, this is where the .gz files will be stored
        expiresOffset = 3600 * 24; // Cache for 1 days in browser cache

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
            cacheKey = getParam(request, "plugins", "") + getParam(request, "languages", "") + getParam(request, "themes", "");

            cacheKey = md5(cacheKey);

            if (compress) {
                cacheFile = cachePath + "tiny_mce_" + cacheKey + ".gz";
            } else {
                cacheFile = cachePath + "tiny_mce_" + cacheKey + ".js";
            }
        }

        // Check if it supports gzip
        supportsGzip = false;
        enc = request.getHeader("Accept-Encoding");
        if (enc != null) {
            enc = enc.replaceAll("\\s+", "").toLowerCase();
            supportsGzip = enc.contains("gzip") || request.getHeader("---------------") != null;
            enc = enc.contains("x-gzip") ? "x-gzip" : "gzip";
        }

        URL coreUrl = null;
        if (core) {
            coreUrl = mapUrl(request, "tiny_mce" + suffix + ".js");
        }
        List<URL> urls = getUrlList(request, languages, themes, plugins, suffix);

        // Use cached file disk cache
        if (diskCache && supportsGzip && new File(cacheFile).exists()) {
            if (!isOutdated(cacheFile, coreUrl, urls)) {
                if (compress)
                    response.addHeader("Content-Encoding", enc);

                fin = new FileInputStream(cacheFile);
                buff = new byte[1024];

                while ((bytes = fin.read(buff, 0, buff.length)) != -1) {
                    outputStream.write(buff, 0, bytes);
                }

                fin.close();
                outputStream.close();
                return;
            }
        }

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
            if (compress)
                response.addHeader("Content-Encoding", enc);

            if (diskCache && isNotBlank(cacheKey)) {
                bos = new ByteArrayOutputStream();

                // Gzip compress
                if (compress) {
                    gzipStream = new GZIPOutputStream(bos);
                    gzipStream.write(content.getBytes("iso-8859-1"));
                    gzipStream.close();
                } else {
                    bow = new OutputStreamWriter(bos);
                    bow.write(content);
                    bow.close();
                }

                // Write to file
                try {
                    fout = new FileOutputStream(cacheFile);
                    fout.write(bos.toByteArray());
                    fout.close();
                } catch (IOException e) {
                    Log.info(getClass().getSimpleName(), "IOException while trying to write to cache file: " + cacheFile + ". This does not affect functionality.", null, null);
                }

                // Write to stream
                outputStream.write(bos.toByteArray());
                outputStream.close();
            } else {
                gzipStream = new GZIPOutputStream(outputStream);
                gzipStream.write(content.getBytes("iso-8859-1"));
                gzipStream.close();
            }
        } else
            outputStream.write(content.getBytes());
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

    private boolean isOutdated(String cacheFile, URL coreUrl, List<URL> urls) {
        boolean outdated = false;
        long cacheLastModified = new File(cacheFile).lastModified();
        try {
            if (coreUrl != null) {
                if (cacheLastModified < coreUrl.openConnection().getLastModified()) {
                    outdated = true;
                }
            }
        } catch (Exception e) {
            Log.debug(getClass().getSimpleName(), e.getMessage(), null, null);
        }

        if (!outdated) {
            for (URL url : urls) {
                try {
                    if (cacheLastModified < url.openConnection().getLastModified()) {
                        outdated = true;
                        break;
                    }
                } catch (Exception e) {
                    Log.debug(getClass().getSimpleName(), e.getMessage(), null, null);
                }
            }
        }

        return outdated;
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
                int avail = is.available();
                byte[] basdas = new byte[avail];
                is.read(basdas);
                retVal = new String(basdas);
            } catch (IOException e) {
                Log.info(getClass().getName(), e.getMessage());
            }
        }
        return retVal;
    }

    private String getCachePath(HttpServletRequest request, String s) {
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
            Log.info(getClass().getName(), e.getMessage());
        }
        Log.info(getClass().getName(), "url='" + url + "' for path='" + path + "' (translated to: '" + relPath + ").");
        return url;
    }

    private String md5(String str) {
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
