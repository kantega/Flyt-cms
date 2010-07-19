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

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xpath.XPathAPI;
import no.kantega.commons.util.XMLHelper;
import no.kantega.commons.util.XPathHelper;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateFormAttributeFromRequestBehaviour;

import javax.xml.transform.TransformerException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FormAttribute extends ListAttribute {
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
            Log.error(SOURCE, e, null, null);
            return options;
        } catch (SQLException e) {
            Log.error(SOURCE, e, null, null);
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
