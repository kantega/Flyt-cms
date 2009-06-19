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

package no.kantega.publishing.common.data;

import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.Aksess;
import no.kantega.commons.media.MimeType;
import no.kantega.commons.media.MimeTypes;
import no.kantega.commons.log.Log;

import java.util.Date;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;

public class MultimediaImageMap {
    private static final String SOURCE = "aksess.MultimediaImageMap";
    private List coordUrlMap = new ArrayList();
    private int multimediaId;

    public int getMultimediaId() {
        return multimediaId;
    }

    public void setMultimediaId(int multimediaId) {
        this.multimediaId = multimediaId;
    }

    public void addCoordUrlMap(String coord, String url, String altName, int newWindow){
        boolean nw = (newWindow == 1);
        if (coord!=null & url!=null){
            coordUrlMap.add(new CoordUrlMap(coord, url, altName, nw));
        }
    }

    public String generateJavascript(){
        String js = "";
        int i=0;
        for (i=0; i < getCoordUrlMap().length; i++){
            StringTokenizer st = new StringTokenizer(getCoordUrlMap()[i].getCoord(), ",");
            String startX = st.nextToken();
            String startY = st.nextToken();
            String stopX = st.nextToken();
            String stopY = st.nextToken();

            String strBoxes = "boxes[" + i + "]";
            js += strBoxes + " = new CoordUrlMap();\n";
            js += strBoxes + ".startX = " + startX + ";\n";
            js += strBoxes + ".startY = " + startY + ";\n";
            js += strBoxes + ".stopX = " + stopX + ";\n";
            js += strBoxes + ".stopY = " + stopY + ";\n";
            js += strBoxes + ".url = '" + getCoordUrlMap()[i].getUrl() + "';\n";
            js += strBoxes + ".altName = '" + getCoordUrlMap()[i].getAltName() + "';\n";

            js += "drawRectangle(" + i + ");\n";
            js += "addRow();\n";

        }
        return js;
    }

    public CoordUrlMap[] getCoordUrlMap(){
        return (CoordUrlMap[]) coordUrlMap.toArray(new CoordUrlMap[0]);
    }

    public class CoordUrlMap {
        String coord;
        String url;
        String altName;
        boolean newWindow;

        public CoordUrlMap(String coord, String url, String altName, boolean newWindow){
            this.coord = coord;
            this.url = url;
            this.altName = altName;
            this.newWindow = newWindow;
        }

        public String getCoord() {
            return coord;
        }

        public String getResizedCoord(int newWidth, int orgW, int newHeight, int orgH) {
            if (newWidth != -1 && newWidth < orgW || newHeight != -1 && newHeight < orgH) {
                try {
                    StringTokenizer st = new StringTokenizer(coord, ",");
                    int startX = Integer.parseInt(st.nextToken(), 10);
                    int startY = Integer.parseInt(st.nextToken(), 10);
                    int stopX = Integer.parseInt(st.nextToken(), 10);
                    int stopY = Integer.parseInt(st.nextToken(), 10);

                    if (newWidth == -1) {
                        newWidth = orgW;
                    }

                    if (newHeight == -1) {
                        newHeight = orgH;
                    }

                    double thumbRatio = (double) newWidth / (double) newHeight;
                    double imageRatio = (double) orgW / (double) orgH;
                    if (thumbRatio < imageRatio) {
                        newHeight = (int) (newWidth / imageRatio);
                    } else {
                        newWidth = (int) (newHeight * imageRatio);
                    }

                    startX = (startX*newWidth)/orgW;
                    stopX = (stopX*newWidth)/orgW;

                    startY = (startY*newHeight)/orgH;
                    stopY = (stopY*newHeight)/orgH;

                    return "" + startX + "," + startY + "," + stopX + "," + stopY;

                } catch (NumberFormatException e) {
                    Log.error(SOURCE, e, null, null);
                    return null;
                }

            } else {
                return coord;
            }
        }

        public String getUrl() {
            return url;
        }

        public String getAltName() {
            return altName;
        }

        public boolean openInNewWindow() {
            return newWindow;
        }
    }
}
