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

package no.kantega.publishing.search.extraction.impl;

import no.kantega.publishing.search.extraction.TextExtractor;
import no.kantega.commons.log.Log;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

public class PdfExtractor implements TextExtractor {
    private static final String SOURCE = "aksess.PdfExtractor";

    PDFTextStripper stripper;

    public PdfExtractor() {
        try {
            stripper = new PDFTextStripper();
        } catch (IOException e) {
            Log.error(SOURCE, e, null, null);
        }
    }

    public String extractText(InputStream is) {
        PDDocument doc = null;
        try {
            doc = PDDocument.load(is);
            String text = stripper.getText(doc);
            return text == null ? ""  :text;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if(doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                    // We don't care;
                }
            }
        }
        return "";
    }
}
