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

package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.attributes.Attribute;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 19, 2009
 * Time: 4:09:51 PM
 */
public interface UnPersistAttributeBehaviour {
    public void unpersistAttribute(ResultSet rs, Attribute attribute) throws SQLException, SystemException;
}
