package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.ConflictException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.webdav.resourcehandlers.util.WebDavMultimediaHelper;

/**
 *
 */
public class AksessMediaFolderResource extends AbstractAksessMultimediaResource implements LockingCollectionResource, FolderResource {
    public AksessMediaFolderResource(Multimedia media, WebDavMultimediaHelper webDavMultimediaHelper) {
        super(media, webDavMultimediaHelper);
    }

    public Resource child(String name) {
        System.out.println("child:" + name);
        return null;
    }

    public List<? extends Resource> getChildren() {
        List<Resource> children = new ArrayList<Resource>();


        List<Multimedia> multimedia = webDavMultimediaHelper.getMultimediaList(media.getId());
        for (Multimedia m : multimedia) {
            if (m.getType() == MultimediaType.FOLDER) {
                children.add(new AksessMediaFolderResource(m, webDavMultimediaHelper));
            } else {
                children.add(new AksessMediaFileResource(m, webDavMultimediaHelper));
            }

        }
        return children;
    }

    public LockToken createAndLock(String fileName, LockTimeout lockTimeout, LockInfo lockInfo) throws NotAuthorizedException {
        LockToken token = new LockToken();
        token.info = lockInfo;
        token.timeout = LockTimeout.parseTimeout("30");
        token.tokenId = UUID.randomUUID().toString();            

        return token;
    }

    public CollectionResource createCollection(String folderName) throws NotAuthorizedException, ConflictException {
        Log.info(this.getClass().getName(), "Create new file:" + folderName);
        return webDavMultimediaHelper.createNewFolder(media, folderName);
    }

    public Resource createNew(String fileName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException {
        if (fileName.startsWith(".") || !fileName.contains(".")) {
            Log.info(this.getClass().getName(), "Filename without extension or hidden ignored:" + fileName);
            return null;
        } else {
            Log.info(this.getClass().getName(), "Create new file:" + fileName);
            return webDavMultimediaHelper.createNewFile(media, fileName, inputStream, length);
        }
    }

    public void copyTo(CollectionResource collectionResource, String s) {
        System.out.println("copyTo:" + s);
    }

    public void delete() {
        Log.info(this.getClass().getName(), "Delete not supported");
    }

    public void sendContent(OutputStream outputStream, Range range, Map<String, String> stringStringMap, String s) throws IOException, NotAuthorizedException, BadRequestException {

    }

    public Long getMaxAgeSeconds(Auth auth) {
        return (long)60;
    }

    public String getContentType(String s) {
        return "mediafolder";
    }

    public Long getContentLength() {
        return (long)0;
    }

    public void moveTo(CollectionResource collectionResource, String s) throws ConflictException {
        System.out.println("moveTo:" + s);
    }
}
