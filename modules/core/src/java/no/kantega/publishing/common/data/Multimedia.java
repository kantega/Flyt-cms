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
import no.kantega.commons.media.MimeTypes;
import no.kantega.commons.media.MimeType;
import no.kantega.publishing.common.data.util.GeoCoordinateConverter;
import no.kantega.publishing.common.util.PrettyURLEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Multimedia extends BaseObject {
    private int parentId = 0;
    private MultimediaType type = MultimediaType.MEDIA;

    private String name = "";
    private String altname = "";
    private String author = "";
    private String description = "";
    private String filename = "";
    private String usage = "";

    private int size = 0;
    private int width = 0;
    private int height = 0;

    private Date originalDate;
    private String cameraMake;
    private String cameraModel;
    private String gpsLatitudeRef;
    private String gpsLatitude;
    private String gpsLongitudeRef;
    private String gpsLongitude;


    byte[] data = null;

    private Date lastModified = new Date();
    private String modifiedBy = null;
    private String profileImageUserId = null;


    private int noFiles = 0; // Number of files in folder
    private int noSubFolders = 0; // Number of sub folders in folder

    private boolean hasImageMap = false;


    private List<ExifMetadata> exifMetadata;

    public Multimedia() {

    }

    public int getObjectType() {
        return ObjectType.MULTIMEDIA;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public MultimediaType getType() {
        return type;
    }

    public void setType(MultimediaType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAltname() {
        return altname;
    }

    public void setAltname(String altname) {
        if (altname == null) altname = "";
        this.altname = altname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null) author = "";
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) description = "";
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        if (usage == null) usage = "";
        this.usage = usage;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public MimeType getMimeType() {
        return MimeTypes.getMimeType(filename);
    }

    public String getFileType() {
        if (type == MultimediaType.FOLDER) {
            return "Folder";
        } else {
            return MimeTypes.getMimeType(filename).getDescription();
        }
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.size = data.length;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        if (modifiedBy == null) modifiedBy = "";
        this.modifiedBy = modifiedBy;
    }

    public String getUrl() {
        if (filename == null) {
            return Aksess.getContextPath() + "/multimedia.ap?id=" + id;
        } else {
            return Aksess.getContextPath() + PrettyURLEncoder.createMultimediaUrl(id, filename);
        }
    }

    public String getOwner() {
        return null;
    }

    public String getOwnerPerson() {
        return null;
    }

    public String getProfileImageUserId() {
        return profileImageUserId;
    }

    public void setProfileImageUserId(String profileImageUserId) {
        this.profileImageUserId = profileImageUserId;
    }
    
    public int getNoFiles() {
        return noFiles;
    }

    public void setNoFiles(int noFiles) {
        this.noFiles = noFiles;
    }

    public int getNoSubFolders() {
        return noSubFolders;
    }

    public void setNoSubFolders(int noSubFolders) {
        this.noSubFolders = noSubFolders;
    }

    public Date getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(Date originalDate) {
        this.originalDate = originalDate;
    }

    public String getCameraMake() {
        return cameraMake;
    }

    public void setCameraMake(String cameraMake) {
        this.cameraMake = cameraMake;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public double getGpsLatitudeAsDouble() {
        double lat = -1;
        if (gpsLatitude != null  && gpsLatitudeRef != null) {
            lat = GeoCoordinateConverter.convertHoursMinutesAndSecondsStringToDouble(gpsLatitude);
            if ("S".equals(gpsLatitudeRef)) lat = lat*(-1);
        }
        return lat;
    }

    public double getGpsLongitudeAsDouble() {
        double lon = -1;
        if (gpsLongitude != null  && gpsLongitudeRef != null) {
            lon = GeoCoordinateConverter.convertHoursMinutesAndSecondsStringToDouble(gpsLongitude);
            if ("E".equals(gpsLatitudeRef)) lon = lon*(-1);
        }
        return lon;
    }

    public String getGpsLatitudeRef() {
        return gpsLatitudeRef;
    }

    public void setGpsLatitudeRef(String gpsLatitudeRef) {
        this.gpsLatitudeRef = gpsLatitudeRef;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitudeRef() {
        return gpsLongitudeRef;
    }

    public void setGpsLongitudeRef(String gpsLongitudeRef) {
        this.gpsLongitudeRef = gpsLongitudeRef;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public List<ExifMetadata> getExifMetadata() {
        if (exifMetadata == null) {
            exifMetadata = new ArrayList<ExifMetadata>();
        }
        return exifMetadata;
    }

    public void setExifMetadata(List<ExifMetadata> exifMetadata) {
        this.exifMetadata = exifMetadata;
    }

    public boolean hasImageMap() {
        return hasImageMap;
    }

    public void setHasImageMap(boolean hasImageMap) {
        this.hasImageMap = hasImageMap;
    }
}
