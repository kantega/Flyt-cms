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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MultimediaImageMap {
    private static final Logger log = LoggerFactory.getLogger(MultimediaImageMap.class);
    private List<CoordUrlMap> coordUrlMap = new ArrayList<CoordUrlMap>();
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

    public CoordUrlMap[] getCoordUrlMap(){
        return coordUrlMap.toArray(new CoordUrlMap[coordUrlMap.size()]);
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

        public int getStartX() {
            StringTokenizer st = new StringTokenizer(coord, ",");
            return Integer.parseInt(st.nextToken(), 10);
        }

        public int getStartY() {
            StringTokenizer st = new StringTokenizer(coord, ",");
            st.nextToken();
            return Integer.parseInt(st.nextToken(), 10);
        }

        public int getStopX() {
            StringTokenizer st = new StringTokenizer(coord, ",");
            st.nextToken();
            st.nextToken();
            return Integer.parseInt(st.nextToken(), 10);
        }
        
        public int getStopY() {
            StringTokenizer st = new StringTokenizer(coord, ",");
            st.nextToken();
            st.nextToken();
            st.nextToken();
            return Integer.parseInt(st.nextToken(), 10);
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
                    log.error("", e);
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

        public boolean isOpenInNewWindow() {
            return newWindow;
        }
    }
}
