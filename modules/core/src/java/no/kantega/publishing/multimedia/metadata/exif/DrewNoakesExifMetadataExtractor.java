package no.kantega.publishing.multimedia.metadata.exif;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import no.kantega.commons.log.Log;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DrewNoakesExifMetadataExtractor implements ExifMetadataExtractor {
    public List<ExifMetadata> getMetadataForImage(byte[] imageData) {
        List<ExifMetadata> exifMetadatas = new ArrayList<ExifMetadata>();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);

        try {
            exifMetadatas = extractMetadata(inputStream);
        } catch (JpegProcessingException e) {
            Log.error(getClass().getName(), e);
        }

        return exifMetadatas;
    }

    private List<ExifMetadata> extractMetadata(ByteArrayInputStream inputStream) throws JpegProcessingException {
        List<ExifMetadata> exifMetadatas = new ArrayList<ExifMetadata>();

        Metadata metadata = JpegMetadataReader.readMetadata(inputStream);
        Iterator directories = metadata.getDirectoryIterator();

        while (directories.hasNext()) {
            Directory directory = (Directory)directories.next();
            Iterator tags = directory.getTagIterator();
            while (tags.hasNext()) {
                Tag tag = (Tag)tags.next();
                try {
                    exifMetadatas.add(getMetadataFromTag(tag));
                } catch (MetadataException e) {
                    Log.error(this.getClass().getName(), e);
                }
            }
        }

        return exifMetadatas;
    }

    private ExifMetadata getMetadataFromTag(Tag tag) throws MetadataException {
        ExifMetadata metadata = new ExifMetadata();
        metadata.setDirectory(tag.getDirectoryName());
        metadata.setKey(tag.getTagName());
        metadata.setValue(tag.getDescription());

        return metadata;
    }
}
