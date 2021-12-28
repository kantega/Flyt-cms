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

package no.kantega.publishing.api.multimedia;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.util.InputStreamHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface MultimediaAO {

    /**
     * Henter multimedia objekt fra basen (unntatt data)
     *
     * @param id - Id til objekt som skal hentes
     * @return
     * @throws SystemException
     */
    Multimedia getMultimedia(int id) throws SystemException;

    /**
     * Henter multimedia objekt fra basen (unntatt data)
     *
     * @param parentId - parentId til objekt som skal hentes
     * @param name     - navn på objekt til som skal hentes
     * @return
     * @throws SystemException
     */
    @Deprecated
    Multimedia getMultimediaByParentIdAndName(int parentId, String name) throws SystemException;

    /**
     * Sender multimedia til klienten
     *
     * @param id  - Id på objekt som skal streames
     * @param ish - Inputhandler som håndterer stream
     * @throws SystemException
     */
    void streamMultimediaData(int id, InputStreamHandler ish) throws SystemException;


    /**
     * Henter alle objekter i multimediaarkiv med angitt parentId
     *
     * @param parentId - id til foreldremappe
     * @return
     * @throws SystemException
     */
    List<Multimedia> getMultimediaList(int parentId) throws SystemException;

    /**
     * Henter antall objekter i multimediaarkiv.
     *
     * @return antall objekter i multimediaarkiv.
     * @throws SystemException
     */
    int getMultimediaCount() throws SystemException;

    /**
     * Flytter et multimediaobjekt
     *
     * @param mmId        - Id til objekt som skal flyttes
     * @param newParentId - Ny plassering for objekt
     * @throws SystemException
     */
    void moveMultimedia(int mmId, int newParentId) throws SystemException;

    /**
     * Lagre multimedia objekt i basen
     *
     * @param mm Multimediaobjekt
     * @return
     * @throws SystemException
     */
    int setMultimedia(Multimedia mm) throws SystemException;

    /**
     * TODO: These methods should be moved to a new non static MultimediaPermissionsDao class
     * Setter securityId til angitt objekt, samt alle underobjekter lik angitt objekts id
     *
     * @param c      - Databasekopling
     * @param object - objekt som det skal settes ny securityid for
     * @throws SQLException -
     */
    void setSecurityId(Connection c, BaseObject object, boolean setFromParent) throws SQLException;
}
