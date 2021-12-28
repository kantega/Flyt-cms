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

package no.kantega.commons.util;

import java.text.DecimalFormat;


public class FormatHelper {

    private static final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };

    /**
     * Formats a file size in bytes into a human readable file size, e.g 10,4 MB etc.
     * @param size file size in bytes.
     * @return file size with denomination.
     */
    public static String formatSize(int size){
        if(size <= 0) return "0";
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
