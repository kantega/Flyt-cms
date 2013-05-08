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

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.XPathHelper;
import no.kantega.publishing.admin.content.behaviours.attributes.*;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.spring.RootContext;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class Attribute {
    private final String FILE_TOKEN = "file:";

    protected Attribute parent = null;

    protected String name = null;  // Navn på attributt, lagres i databasen, og refereres med getattribute
    protected String title = null; // Tittel, vises for brukeren i skjermbilder, valgfritt
    protected String field = null; // Mapping til content felt, f.eks title, description, image etc

    protected String helpText = null; // Hjelpetekst som vises for brukeren i skjermbilder
    protected String script = null; // Hook for script in editpage

    protected int type = AttributeDataType.CONTENT_DATA;

    protected String value = null;
    protected String regexp = null;

    protected boolean mandatory = false; // Angir om attributten må fylles ut
    protected boolean isCData   = false;

    protected int maxLength = 128;

    protected int tabIndex = 1;
    protected boolean editable = true;
    protected boolean inheritsFromAncestors = false;

    private String[] showInSites = null; // Angir alias for siter hvor denne attributten skal vises (null = vis for alle)
    private String[] hideInSites = null; // Angir alias for siter hvor denne attributten ikke skal vises (null = vis for alle)

    private String[] editableByRole = null; // Roles which can edit this element

    public Attribute() {

    }

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        if (config != null) {
            name = config.getAttribute("name");
            if (name == null) {
                throw new InvalidTemplateException("name mangler i mal fil", "", null);
            }

            if (name.contains("[") || name.contains("]")) {
                throw new InvalidTemplateException("[ og ] er ikke tillatt i navn på attributter", "", null);
            }

            editable = !(config.getAttribute("editable").equals("false"));
            regexp = config.getAttribute("regexp");
            String mapto = config.getAttribute("mapto");
            if (mapto == null || mapto.length() == 0) {
                mapto = config.getAttribute("field");
            }
            setField(mapto);
            title  = config.getAttribute("title");
            if (title == null || title.length() == 0) {
                title = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
            }

            String isMandatory = config.getAttribute("mandatory");
            if ("true".equalsIgnoreCase(isMandatory)) {
                mandatory = true;
            }

            String doesInheritFromAncestors = config.getAttribute("inheritsfromancestors");
            if ("true".equalsIgnoreCase(doesInheritFromAncestors)) {
                inheritsFromAncestors = true;
            }

            String strMaxlength = config.getAttribute("maxlength");
            if (strMaxlength != null && strMaxlength.length() > 0) {
                this.maxLength = Integer.parseInt(strMaxlength);
            }

            String strEditableByRole = config.getAttribute("editablebyrole");
            if (strEditableByRole != null && strEditableByRole.length() > 0) {
                editableByRole = strEditableByRole.split(",");
                for (int i = 0; i < editableByRole.length; i++) {
                    editableByRole[i] = editableByRole[i].trim();
                }
            } else {
                editableByRole = new String[]{Aksess.getEveryoneRole()};
            }

            String strShowInSites = config.getAttribute("showinsites");
            if (strShowInSites != null && strShowInSites.length() > 0) {
                showInSites = strShowInSites.split(",");
                for (int i = 0; i < showInSites.length; i++) {
                    showInSites[i] = showInSites[i].trim();
                }
            }

            String strHideInSites = config.getAttribute("hideinsites");
            if (strHideInSites != null && strHideInSites.length() > 0) {
                hideInSites = strHideInSites.split(",");
                for (int i = 0; i < hideInSites.length; i++) {
                    hideInSites[i] = hideInSites[i].trim();
                }
            }

            String defaultValue = config.getAttribute("default");
            if (value == null || value.length() == 0 && defaultValue != null) {
                // Hent defaultverdi fra en fil
                if (defaultValue.contains(FILE_TOKEN)) {
                    int inx = defaultValue.indexOf(FILE_TOKEN) + FILE_TOKEN.length();
                    String file = defaultValue.substring(inx, defaultValue.length());

                    ResourceLoader source = RootContext.getInstance().getBean("contentTemplateResourceLoader", ResourceLoader.class);
                    Resource resource = source.getResource("defaults/" + file);

                    try {
                        value = IOUtils.toString(resource.getInputStream());
                    } catch (IOException e) {
                        throw new SystemException("", "Feil ved lesing av default fil:" + file, e);
                    }
                } else {
                    if (model != null && model.size() > 0) {
                        for (String key : model.keySet()) {
                            String value = model.get(key);
                            if (value == null) {
                                value = "";
                            }
                            String keyToken = "\\$\\{" + key + "\\}";

                            String tmp = defaultValue.replaceAll(keyToken, value);
                            if (tmp.equals(defaultValue)) {
                                defaultValue = defaultValue.replaceAll(key, value);
                            } else {
                                defaultValue = tmp;
                            }

                        }

                        defaultValue = defaultValue.replaceAll("\\$\\{(.*)\\}", "");

                        value = defaultValue;
                    }
                }
            }

            helpText = XPathHelper.getString(config, "helptext");
            script= XPathHelper.getString(config, "script");
        }
    }


    public Attribute getParent() {
        return parent;
    }


    public void setParent(Attribute parent) {
        this.parent = parent;
    }


    public String getNameIncludingPath() {
        String id = name;

        if (parent != null) {
            int offset = parent.getOffset(this);
            if (offset != -1) {
                id = parent.getNameIncludingPath() + "[" + offset + "]." + name;
            } else {
                id = parent.getNameIncludingPath() + "." + name;
            }
        }

        return id;
    }


    public int getOffset(Attribute a) {
        return -1;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (title == null) {
            title = name;
        }
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public String getHelpText() {
        return helpText;
    }
    
    public String getScript(){
    	return script;
    }

    public boolean inheritsFromAncestors() {
        return inheritsFromAncestors;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getValue() {
        return value;
    }

    public String getProperty(String property) {
        return getValue();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isCData() {
        return isCData;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        if (ContentProperty.TITLE.equalsIgnoreCase(field)) {
            // Attributten er mappet til tittel, må alltid fylles ut
            mandatory = true;
        }
        this.field = field;
    }

    public String getRenderer() {
        return "text";
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public  void validate(ValidationErrors errors) throws no.kantega.commons.exception.RegExpSyntaxException {
        if (mandatory && editable && (value == null || value.length() == 0)) {
            Map<String, Object> objects = new HashMap<String, Object>();
            objects.put("field", title);
            errors.add(name, "aksess.feil.mandatoryfield", objects);
        }
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public void cloneValue(Attribute attribute) {
        setValue(attribute.getValue());
    }

    /**
     * Return behaviour to save attribute in database
     * @return - behaviour
     */
    public PersistAttributeBehaviour getSaveBehaviour() {
        return new PersistSimpleAttributeBehaviour();
    }

    /**
     * Return behaviour to fetch attribute from database
     * @return - behaviour
     */
    public UnPersistAttributeBehaviour getFetchBehaviour() {
        return new UnPersistSimpleAttributeBehaviour();
    }

    /**
     * Returnerer behaviour for å oppdatere attributt basert på request
     * @return - behaviour
     */
    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateSimpleAttributeFromRequestBehaviour();
    }

    /**
     * Returnerer behaviour for mapping av attributt verdi til spesialfelt i content objekt, f.eks utløpsdato
     * @return - behaviour
     */
    public MapAttributeValueToContentPropertyBehaviour getMapAttributeValueToContentPropertyBehaviour() {
        return new MapSimpleAttributeValueToContentPropertyBehaviour();
    }

    /**
     * Brukes for å sjekke om en attributt skal vises eller skjules for content objekt
     * @param content - content objekt som attributten ligger i
     * @return true/false
     */
    public boolean isHidden(Content content) {

        // Som standard skal alle attributter vises med mindre hideInSites eller showInSites er angitt
        boolean isHidden = false;
        if (showInSites != null && showInSites.length > 0) {
            isHidden = true;
        }

        try {
            Association association = content.getAssociation();
            int siteId =  association.getSiteId();
            Site site = SiteCache.getSiteById(siteId);
            // Dersom site er angitt i hideInSites skal den ikke vises
            if (site != null && hideInSites != null) {
                for (String alias : hideInSites) {
                    if (alias.equalsIgnoreCase(site.getAlias())) {
                        return true;
                    }
                }
            }
            // Dersom site er angitt i showInSites skal den vises
            if (site != null && showInSites != null) {
                for (String alias : showInSites) {
                    if (alias.equalsIgnoreCase(site.getAlias())) {
                        return false;
                    }
                }
            }

        } catch (SystemException e) {
            Log.error("Attribute:" + name, e, null, null);
        }
        return isHidden;
    }

    public void addIndexFields(Document d) {

    }

    public boolean isSearchable() {
        return false;
    }

    public String[] getEditableByRoles() {
        return editableByRole;
    }

    public XMLAttributeValueExporter getXMLAttributeValueExporter() {
        return new SimpleAttributeValueXMLExporter();
    }

}
