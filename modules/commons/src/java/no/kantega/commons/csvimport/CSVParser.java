/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.commons.csvimport;

import java.io.*;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class CSVParser {
    private String separator = "\t";
    private LineHandler lineHandler;
    private String charset = "iso-8859-1";
    private int fields = -1;
    private int skipFirst = 0;
    private int maxLines = 0;

    public void parse(String s) throws IOException {
        parse(new File(s));
    }

    public void parse(File f) throws IOException {
        parse(new FileInputStream(f));
    }

    public void parse(InputStream stream) throws IOException {
        if(lineHandler == null) {
            throw new RuntimeException("Line handler must be set before parsing starts");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(stream, charset));
        String line = null;
        int lineCount = 0;
        int addCount = 0;
        while((line = br.readLine()) != null && (maxLines <= 0 || addCount <   maxLines)) {
            if(skipFirst <= 0  || lineCount  >= skipFirst) {
                String[] values = line.split(separator, -2);
                if(fields >= 0 && values.length != fields) {
                    throw new RuntimeException("Error at line " +lineCount +", expected " + fields +" fields, got " + values.length +": " + line);
                }
                lineHandler.handleLine(values);
                addCount++;
            }
            lineCount++;
        }

    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setLineHandler(LineHandler lineHandler) {
        this.lineHandler = lineHandler;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public static void main(String[] args) throws IOException {
        CSVParser parser  = new CSVParser();
        parser.setLineHandler(new LineHandler() {
            public void handleLine(String[]line) {
                System.out.println("##################");
                for (int i = 0; i < line.length; i++) {
                    System.out.println(" " + i + ": " + line[i]);
                }
            }
        });
        //parser.setFields(15);
        parser.setSkipFirst(1);
        System.out.println("parsing " + args[0]);
        parser.parse(args[0]);
    }


    public void setFields(int fields) {
        this.fields = fields;
    }

    public void setSkipFirst(int skipFirst) {
        this.skipFirst = skipFirst;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }
}
