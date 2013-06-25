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

package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateFormAttributeFromRequestBehaviour;
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Deprecated
public class FormAttribute extends ListAttribute {
    private static final Logger log = LoggerFactory.getLogger(FormAttribute.class);
    private static final String SOURCE = "aksess.FormAttribute";

    public String getRenderer() {
        return "form";
    }

    public List getListOptions(int language) {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            ResultSet rs = SQLHelper.getResultSet(c, "select * from form order by name");
            while(rs.next()) {
                ListOption option = new ListOption();
                option.setValue("" + rs.getInt("id"));
                option.setText(rs.getString("name"));
                options.add(option);
            }
        } catch (SystemException e) {
            log.error("", e);
            return options;
        } catch (SQLException e) {
            log.error("", e);
            return options;
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {

                }
            }
        }

        return options;
    }

    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateFormAttributeFromRequestBehaviour();
    }

}
