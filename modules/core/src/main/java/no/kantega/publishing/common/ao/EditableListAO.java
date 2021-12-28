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

import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class EditableListAO {

    private static Locale defaultLocale = new Locale("no", "NO");

    /**
     * Fetches all list options from for a list with a given key.
     *
     * @param attributeKey Attribute's name or value from parameter "key"
     * @param locale Norwegian Bokmål is assumed is assumed if null
     * @param ignoreVariant Locale variant is ignored if true.
     * @return List of ListOption
     */
    public static List<ListOption> getOptions(String attributeKey, Locale locale, boolean ignoreVariant) {
        if(attributeKey == null) {
            return null;
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionFactory.getDataSource());
        String language = (ignoreVariant)? getLocaleAsString(locale, true) + '%' : getLocaleAsString(locale, false);

        List<ListOption> options = jdbcTemplate.query("SELECT * FROM attribute_editablelist WHERE AttributeKey = ? AND Language LIKE ? ORDER BY DefaultSelected DESC, Value", new Object[]{attributeKey.toLowerCase(), language}, new RowMapper<ListOption>(){
            public ListOption mapRow(ResultSet rs, int i) throws SQLException {
                ListOption option = new ListOption();
                option.setText(rs.getString("Value"));
                option.setValue(rs.getString("Value"));
                option.setDefaultSelected(rs.getInt("DefaultSelected") == 1);
                return option;
            }
        });
        return options;
    }


    /**
     * Inserts a list option
     * @param attributeKey Attribute's name or key
     * @param value Option value
     * @param defaultSelected Whether or not the option should be selected by default.
     * @param locale Norwegian Bokmål is assumed is assumed if null
     */
    public static void saveOption(String attributeKey, String value, boolean defaultSelected, Locale locale) {
        if(attributeKey == null || attributeKey.trim().length() == 0 || value == null || value.trim().length() == 0) {
            return;
        }

        String language = getLocaleAsString(locale, false);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionFactory.getDataSource());
        if(defaultSelected) {
            jdbcTemplate.update("UPDATE attribute_editablelist SET DefaultSelected = 0 WHERE AttributeKey = ? AND Language = ?", attributeKey.toLowerCase(), language);
        }
        int intDefaultSelected = (defaultSelected)? 1 : 0;
        jdbcTemplate.update("INSERT INTO attribute_editablelist(AttributeKey, Value, DefaultSelected, Language) VALUES(?, ?, ?, ?)", attributeKey.toLowerCase(), value, intDefaultSelected, language);
    }



    /**
     * Deletes a list option
     *
     * @param attributeKey Attribute's name or key
     * @param value Option value
     * @param locale Norwegian Bokmål is assumed is assumed if null
     */
    public static void deleteOption(String attributeKey, String value, Locale locale) {
        if(attributeKey == null || attributeKey.trim().length() == 0 || value == null || value.trim().length() == 0) {
            return;
        }
        new JdbcTemplate(dbConnectionFactory.getDataSource()).update("DELETE FROM attribute_editablelist WHERE AttributeKey = ? AND Value = ? AND Language = ?", attributeKey, value, getLocaleAsString(locale, false));
    }


    /**
     * @param locale Norwegian Bokmål is assumed is assumed if null
     * @param ignoreVariant Locale variant is ignored if true
     * @return Locale as a string formatted as "country_language_variant"
     */
    private static String getLocaleAsString(Locale locale, boolean ignoreVariant) {
        if(locale == null) {
            locale = defaultLocale;
        }
        String language = locale.getCountry() + "_" + locale.getLanguage();
        if(!ignoreVariant && locale.getVariant() != null && locale.getVariant().trim().length() > 0) {
            language = language + "_" + locale.getVariant();
        }
        return language;
    }
}
