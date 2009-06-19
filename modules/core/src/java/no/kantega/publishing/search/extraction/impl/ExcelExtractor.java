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
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ExcelExtractor implements TextExtractor {
    private Logger logger = Logger.getLogger(getClass());

    public String extractText(InputStream is) {
        try {
            StringBuffer buffer = new StringBuffer();

            POIFSFileSystem fileSystem = new POIFSFileSystem(is);
            HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

            for(int s = 0; s < workbook.getNumberOfSheets(); s++) {
                HSSFSheet sheet = workbook.getSheetAt(s);

                for(Iterator rows = sheet.rowIterator(); rows.hasNext(); ) {
                    HSSFRow row = (HSSFRow) rows.next();
                    for(Iterator cells= row.cellIterator(); cells.hasNext(); ) {
                        HSSFCell cell = (HSSFCell) cells.next();
                        if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            buffer.append(cell.getRichStringCellValue().getString()).append(" ");
                        }
                    }

                }
            }
            return buffer.toString();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return "";
        }
    }
}
