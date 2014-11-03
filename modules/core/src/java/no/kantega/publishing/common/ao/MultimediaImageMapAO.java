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

package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.MultimediaImageMap;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MultimediaImageMapAO {
    private static final Logger log = LoggerFactory.getLogger(MultimediaImageMapAO.class);

    /**
     * Get MultimediaImageMap for multimedia object
     * @param multimediaId - id of multimedia object
     * @return - MultimediaImageMap
     * @throws SystemException -
     */
    public static MultimediaImageMap loadImageMap(int multimediaId) throws SystemException {
        try (Connection c = dbConnectionFactory.getConnection();
             ResultSet rs = SQLHelper.getResultSet(c, "SELECT Coords, Url, AltName, NewWindow from multimediaimagemap WHERE MultimediaId = ? ORDER BY Id", new Object[]{multimediaId})){

            MultimediaImageMap mim = new MultimediaImageMap();
            mim.setMultimediaId(multimediaId);
            while(rs.next()) {
                mim.addCoordUrlMap(rs.getString("Coords"), rs.getString("Url"), rs.getString("AltName"), rs.getInt("NewWindow"));
            }

            return mim;
        } catch (SQLException e) {
            log.error("", e);
            throw new SystemException("SQL feil ved henting av imagemap", e);
        }
    }

    /**
     * Save MultimediaImageMap
     * @param mim  - MultimediaImageMap
     * @throws SystemException -
     */
    public static void storeImageMap(MultimediaImageMap mim) throws SystemException {
        try (Connection c = dbConnectionFactory.getConnection()) {
            try(PreparedStatement st = c.prepareStatement("delete from multimediaimagemap where MultimediaId=?")) {
                // Delete old mappings
                st.setInt(1, mim.getMultimediaId());
            }

            MultimediaImageMap.CoordUrlMap[] coordUrlMapArray = mim.getCoordUrlMap();
            try(PreparedStatement ps = c.prepareStatement("insert into multimediaimagemap (MultimediaId, Coords, Url, AltName, NewWindow) VALUES(?, ?, ?, ?, ?)")) {
                for (MultimediaImageMap.CoordUrlMap aCoordUrlMapArray : coordUrlMapArray) {
                    ps.setInt(1, mim.getMultimediaId());
                    ps.setString(2, aCoordUrlMapArray.getCoord());
                    ps.setString(3, aCoordUrlMapArray.getUrl());
                    ps.setString(4, aCoordUrlMapArray.getAltName());
                    ps.setInt(5, aCoordUrlMapArray.isOpenInNewWindow() ? 1 : 0);
                    ps.execute();
                }
            }

            try(PreparedStatement hasImageMapSt = c.prepareStatement("UPDATE multimedia SET hasImageMap=? WHERE Id=?")) {
                hasImageMapSt.setInt(1, coordUrlMapArray.length > 0 ? 1 : 0);
                hasImageMapSt.setInt(2, mim.getMultimediaId());
                hasImageMapSt.execute();
            }
        } catch (SQLException e) {
            StringBuilder errormessage = new StringBuilder("Error saving. mmid: ");
            errormessage.append(mim.getMultimediaId());
            errormessage.append(" Coordinates: [");
            for (MultimediaImageMap.CoordUrlMap coordUrlMap : mim.getCoordUrlMap()) {
                errormessage.append("{");
                errormessage.append("url: ").append(coordUrlMap.getUrl());
                errormessage.append(", name: ").append(coordUrlMap.getAltName());
                errormessage.append(", coordinate: ").append(coordUrlMap.getCoord());
                errormessage.append("},");
            }
            errormessage.append("]");

            log.error(errormessage.toString(), e);
            throw new SystemException("SQL error saving imagemap", e);
        }
    }

}
