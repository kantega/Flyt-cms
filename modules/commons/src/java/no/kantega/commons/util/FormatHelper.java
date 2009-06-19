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

/**
 * User: Anders Skar, Kantega AS
 * Date: Apr 4, 2007
 * Time: 9:50:09 AM
 */
public class FormatHelper {
    private final static int MB = 1048576;
    private final static int KB = 1024;
    private final static int B = 1;

    public static String formatSize(int size){
        String unit = "B";
        int div = B;
        float sizeF;
        if (size > MB) {
            div = MB;
            unit = "MB";
        } else if (size > KB) {
            div = KB;
            unit = "KB";
        }

        sizeF = (float)size/div;
        sizeF = (float)Math.round(sizeF*10)/10;

        return "" + sizeF + " " + unit;
    }
}
