# Registering templates


Content templates and display templates must be added to the template configuration before they are available to users.

## aksess-templateconfig.xml

The template configuration contains information about templates as well as other data needed by templates, such as: sites, association categories and document types.

All setup of templates is done by editing the template configuration file: **WEB-INF/aksess-templateconfig.xml**.

## Sites

A minimum of one site must be configured. Sites are configured using the <sites> element.

Example:

```
<sites>
    <site databaseId="1" alias="/intranet/" id="intranet">
        <name>Intranett</name>
    </site>
</sites>
```

**Attributes / elements**

| Attribute/Element | Description |
| --- | --- |
| id | Unique id used to reference this site within configuration file |
| database-id | Unique integer value used as key in database. **NB!** Should never be changed once site is created |
| alias | Alias used for site. Can be used in templates to reference site. |
| name | Name of site, displayed for editors. |

## Association categories

A minimum of one association category must be created. Association categories are created using the <associationCategories> element.

Example:

```
<associationCategories>
    <associationCategory databaseId="1" id="Global menu">
        <name>Global menu</name>
        <description>Use this to publish a page in global / left menu</description>
    </associationCategory>
    <associationCategory databaseId="2" id="Related links">
        <name>Related links</name>
    </associationCategory>
</associationCategories>
```

**Attributes / elements**

| Attribute/Element | Description |
| --- | --- |
| id | Unique id used to reference this association category within configuration file and in templates |
| database-id | Unique integer value used as key in database. **NB!** Should never be changed once content is published using this category |
| name | Name of association category, displayed for editors. |
| description | Description of association category (optional). |

## Document types

Document types are created using the <documentTypes> element:

```
<documentTypes>
    <documentType databaseId="1" id="News article">
        <name>News article</name>
    </documentType>
</documentTypes>
```

**Attributes / elements**

| Attribute/Element | Description |
| --- | --- |
| id | Unique id used to reference this document type within configuration file and in templates |
| database-id | Unique integer value used as key in database. **NB!** Should never be changed once documenttype is used |
| name | Name of association category, displayed for editors. |
| description | Name of document type, displayed for editors. |

## Content templates

Content templates are registered using the <contentTemplates> element.

Example:

```
<contentTemplates>
    <contentTemplate contentType="PAGE" databaseId="2" id="News archive">
        <name>News archive</name>
        <templateFile>newsarchive.xml</templateFile>
        <allowedParentTemplates>
            <contentTemplate id="Front page"/>
        </allowedParentTemplates>
        <associationCategories>
            <associationCategory id="Main column"/>
        </associationCategories>
    </contentTemplate>
    <contentTemplate contentType="PAGE" databaseId="3" id="News article">
        <name>News article</name>
        <templateFile>article.xml</templateFile>
        <allowedParentTemplates>
            <contentTemplate id="News archive"/>
        </allowedParentTemplates>
    </contentTemplate>
</contentTemplates>
```

Attributes / elements

| Attribute/Element | Description |
| --- | --- |
| id | Unique id used to reference this content template within configuration file and in display templates |
| database-id | Unique integer value used as key in database. **NB!** Should never be changed once content is used published using this template |
| name | Name of content template. (Only displayed for editors if contentType=LINK or contentType=FILE) |
| contentType | Type of content template. Legal values: PAGE, FILE, LINK. Content templates with contentType = PAGE must have display template. |
| templateFile | Filename of content template file (XML file with attribute definition). |
| templateFile | Filename of content template file (XML file with attribute definition). |
| allowedParentTemplates | Specify where this template can be used. |
| associationCategories | Which association categories to publish subpages in. If not present, no subpages can be published under pages using this template. |

A few important things to configure for content templates are:

**Allowed parent templates**
Setting up "allowed parent templates" enables you to configure what templates are available. In the previous example the "news article" template should only be allowed as a child of the "News archive" template:

```
<allowedParentTemplates>
    <contentTemplate id="News archive"/>
</allowedParentTemplates>
```

**Association categories**
If a page should have sub pages, at least one association category must be defined. For the "News archive" template the association category "Main column" is defined:

```
<associationCategories>
    <associationCategory id="Main column"/>
</associationCategories>
```

## Display templates

Display templates are registered using the <displayTemplates> element.

All display templates must be mapped to a content template and specify a view. (The view is normally either a JSP file or the URL to a [Spring controller](http://static.springframework.org/spring/docs/2.5.6/reference/mvc.html)).

Example:

```
<displayTemplate databaseId="12" id="News arcticle">
        <contentTemplate id="News article"/>
        <name>News article</name>
        <description>Description for editors</description>
        <view>/WEB-INF/jsp/news.jsp</view>
        <sites>
            <site id="intranet"/>
        </sites>
</displayTemplate>
```

## Attributes / elements

| id | Unique id used to reference this display template within configuration file and in display templates |
| --- | --- |
| database-id | Unique integer value used as key in database. **NB!** Should never be changed once content is published using this template |
| name | Name of display template. (Displayed for editors) |
| description | Description of display template (Displayed for editors) |
| description | Description of display template (Displayed for editors) |
| view | JSP for view (or Spring MVC controller) |
| miniView | JSP for mini view. See <aksess:miniview> tag. |
| controllers | Controllers needed for this view, see advanced topics |
| sites | Specify at least one site which should be able to use this template or omit this element to allow template to be used in all sites. |
| allowMultipleUsages | If set to false, it will only be possible to create 1 page using this template.. |

## Reloading template configuration

The template configuration is read when your web application is started. If any errors are detected they will be logged to the log file.

If you want to make changes to the configuration without restarting the application this can be done by logging is as an administrator and selecting: "Admin" -> "Update templateconfig".

If there are any errors in your template configuration file they will be displayed when you try to reload the configuration and the old configuration will be kept.