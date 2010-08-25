package no.kantega.publishing;

import no.kantega.commons.log.Log;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

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
        expiresOffset = 3600 * 24 * 10; // Cache for 10 days in browser cache

        // Custom extra javascripts to pack
        String custom[] = {/*
		"some custom .js file",
		"some custom .js file"
	    */};

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

            for (i=0; i<custom.length; i++) {
                cacheKey += custom[i];
            }

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
            enc.replaceAll("\\s+", "").toLowerCase();
            supportsGzip = enc.indexOf("gzip") != -1 || request.getHeader("---------------") != null;
            enc = enc.indexOf("x-gzip") != -1 ? "x-gzip" : "gzip";
        }

        // Use cached file disk cache
        if (diskCache && supportsGzip && new File(cacheFile).exists()) {
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

        // Add core
        if (core) {
            content += getFileContents(mapUrl(request, "tiny_mce" + suffix + ".js"));

            // Patch loading functions
            content += "tinyMCE_GZ.start();";
        }

        // Add core languages
        for (x=0; x<languages.length; x++)
            content += getFileContents(mapUrl(request, "langs/" + languages[x] + ".js"));

        // Add themes
        for (i=0; i<themes.length; i++) {
            content += getFileContents(mapUrl(request, "themes/" + themes[i] + "/editor_template" + suffix + ".js"));

            for (x=0; x<languages.length; x++)
                content += getFileContents(mapUrl(request, "themes/" + themes[i] + "/langs/" + languages[x] + ".js"));
        }

        // Add plugins
        for (i=0; i<plugins.length; i++) {
            content += getFileContents(mapUrl(request, "plugins/" + plugins[i] + "/editor_plugin" + suffix + ".js"));

            for (x=0; x<languages.length; x++)
                content += getFileContents(mapUrl(request, "plugins/" + plugins[i] + "/langs/" + languages[x] + ".js"));
        }

        // Add custom files
        for (i=0; i<custom.length; i++)
            content += getFileContents(mapUrl(request, custom[i]));

        // Restore loading functions
        if (core)
            content += "tinyMCE_GZ.end();";

        // Generate GZIP'd content
        if (supportsGzip) {
            if (compress)
                response.addHeader("Content-Encoding", enc);

            if (diskCache && cacheKey != "") {
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
                OutputStream responseOutputStream = outputStream;
                responseOutputStream.write(bos.toByteArray());
                responseOutputStream.close();
            } else {
                gzipStream = new GZIPOutputStream(outputStream);
                gzipStream.write(content.getBytes("iso-8859-1"));
                gzipStream.close();
            }
        } else
            outputStream.write(content.getBytes());
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
                e.printStackTrace();
            }
        }
        return retVal;
    }

    private String getCachePath(HttpServletRequest request, String s) {
//        String tempdir = System.getProperty("java.io.tmpdir");
//        if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
//            tempdir = tempdir + File.pathSeparator;
//        }


        String tempdir = "/var/place/with/no/permission/to/write/";

        return tempdir;
    }

    private String mapPath2(HttpServletRequest request, String path) {
        try {
            return request.getSession().getServletContext().getResource(path).getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new File("").getAbsolutePath();
        }
    }

    private URL mapUrl(HttpServletRequest request, String path) {
        URL url = null;
        String relPath = "/aksess/tiny_mce/" + path;
        try {
            url = request.getSession().getServletContext().getResource(relPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("url='" + url + "' for path='" + path + "' (translated to: '" + relPath + ").");
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
            StringBuffer hexValue = new StringBuffer();

            for (int i=0; i<md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i] ) & 0xff;

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
