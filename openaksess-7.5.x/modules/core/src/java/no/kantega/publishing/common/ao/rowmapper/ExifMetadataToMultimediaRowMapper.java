/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.ao.rowmapper;

import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExifMetadataToMultimediaRowMapper implements RowMapper<ExifMetadata> {
    private Map<Integer, Multimedia> multimediaMap;

    public ExifMetadataToMultimediaRowMapper(List<Multimedia> multimedia) {
        multimediaMap = new HashMap<Integer, Multimedia>();
        for (Multimedia m : multimedia) {
            multimediaMap.put(m.getId(), m);
        }
    }

    public ExifMetadata mapRow(ResultSet resultSet, int i) throws SQLException {
        int multimediaId = resultSet.getInt("MultimediaId");

        String directory = resultSet.getString("Directory");
        String key = resultSet.getString("ValueKey");
        String value = resultSet.getString("Value");

        Multimedia multimedia = multimediaMap.get(multimediaId);

        ExifMetadata metadata = new ExifMetadata();

        if (multimedia != null) {
            metadata = createOrGetExistingMetadata(multimedia, directory, key);
            metadata.addValue(value);
        }

        return metadata;
    }

    private ExifMetadata createOrGetExistingMetadata(Multimedia multimedia, String directory, String key) {
        for (ExifMetadata metadata : multimedia.getExifMetadata()) {
            if (metadata.getDirectory().equals(directory) && metadata.getKey().equals(key)) {
                return metadata;
            }
        }

        ExifMetadata metadata = new ExifMetadata();
        metadata.setDirectory(directory);
        metadata.setKey(key);

        multimedia.getExifMetadata().add(metadata);

        return metadata;
    }
}
