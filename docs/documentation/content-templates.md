# Content templates

A page created with FlytCMS consists of several elements. These elements are referred to as attributes.

Examples of such attributes can be: a page title, an image, body text etc.

The illustration below shows a page that consists of 4 attributes: title, summary, body text and an image:

A content template specifies all the attributes used in a page. The input screen used to edit a page based a content template displays the attributes from the content template in the same order as they are specified in the template. Content templates are XML files and placed in the **"WEB-INF/templates/content"** directory of your project.

There are three variants of content templates:

*   Templates for page (must have at least one display template)
*   Templates for file (has no display template)
*   Template for link (has no display template)

**Example content template:**
The following is an example of a content template with 5 attributes:

*   heading (text)
*   summary (text, max 300 characters)
*   body (HTML text)
*   image
*   image text (with help text displayed for editors)

```
<?xml version="1.0" encoding="ISO-8859-1"? >
 <template xmlns="http://www.kantega.no"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://www.kantega.no http://www.kantega.no/aksess/aksess-template.xsd" >
     <attributes >
         <attribute name="heading" type="text" mandatory="true" mapto="title"/ >
         <attribute name="summary" type="text" maxlength="300"/ >
         <attribute name="body" type="htmltext"/ >
         <attribute name="image" type="media"/ >
         <attribute name="image text" type="text" >
             <helptext >

             <![CDATA[If no image text is specified the name of the image will be used.]] >

             </helptext >
         </attribute >
     </attributes >
 </template >
```

Description of tags used in example:

<dl>

<dt>template</dt>

<dd>Specifies start and end of template</dd>

<dt>attributes</dt>

<dd>Specifies start and end of attributes</dd>

<dt>attribute</dt>

<dd>Specifies an attribute</dd>

<dt>helptext</dt>

<dd>Used to create a helptext displayed when user edits page</dd>

</dl>

## Attribute types

Aksess comes with a set of standard attribute types:

**contentid**
Attribute where editors can select one or more pages using the page navigator. Typically used to present one or more pages (articles) on a front page.

Example: A field where editors can select multiple pages

```
<attribute name="related pages" type="contentid" multiple="true"/>
```

**contentlist**
Attribute where editors can select one or more pages from a dropdown list.

Example: Displays a list of all pages with documenttype "Team" in a select list

```
<attribute name="team" type="contentlist" documenttype="Team/>
```

**date**
Attribute where editors can specify / select a date. Date attributes can be mapped to content properties "publishdate" and "expiredate", see content properties.

**datetime**
Attribute where editors can specifiy date and time.

**documenttype**
Attribute where editors can select a document type from all available document types. Document types are defined in the template configuration.

**editablelist**
Attribute where editors can select elements from a list. Editors can edit list choices.

Example: Editable list where choices only can be edited by the role "Role"

```
<attribute name="list" type="editablelist" editableby="Role"/>
```

**email**
Attribute where editors can type an email address. (Is not automatically converted to a link, unlike text attributes)

**file**
Attribute for uploading a file. Always used with file content templates.

Example: File attribute in a file content template. Note mapto="url", see content properties.

```
<attribute name="document" type="file" mapto="url"/>
```

**htmltext**
Attribute with HTML editor. HTML editor can be configured in Aksess configuration file.

**list**
Attribute with choices which the editor can select from. Choices are specified in content template. Also see editablelist.

**media**
Attribute where user can select an image, Flash or other file from mediaarchive.

**mediafolder**
Attribute where user can select a folder from mediaarchive. Typically used to select a folder with images to display in a photoalbum.

**number**
Attribute which only accepts numbers as input.

**role**
Attribute where editors can select a role.

**text**
Attribute where editors can enter text. Displayed as HTML input tag if maxlength < 255 and textarea if maxlength is > 255 characters. Default maxlength is 128.

**topic**
Field where editors can select a topic from a topic map. Typically used in a template to list all pages tagged with selected topic.

**topicmap**
Field where editors can select a topic from a topic map.

**topictype**
Field where editors can select a topic type.

**url**
Field where editors can enter an URL manually or select a page with page navigator.

**user**
Field where editors can select a user.

## Attribute properties

Attributes have a number of properties which can be set, some are global and some are attribute type specific:
Mandatory settings are marked with bold text.

| Property | Description | Applies to |
| --- | --- | --- |
| contenttemplate | Name of contenttemplate used to populate lists | "contentlist" attribute |
| default | Default text value for attribute. Can refer to file with default text using prefix file:
Example: default="Untitled"
Example: default="file:timetable.txt"
timetable.txt must be placed in directory "WEB-INF/templates/content/defaults"

Available macros: YEAR/MONTH/DAY = Todays year/month/day USER.ID = Id of logged in user USER.NAME = Name of logged in user USER.EMAIL = Email of logged in user USER.DEPARTMENT = Department of logged in user | All attributes |
| hideinsite | Hide attribute for specific site(s) by specifying alias for site(s). Several sites may be entered using comma as separator.
Example: hideinsite="/intranet/" | All attributes |
| mapto | Map attribute to special fields.
Example mapping date attribute to expire date:
mapto="expiredate"

Example mapping text attribute to title of page:
mapto="title" | Most attributes |
| mandatory | Set attribute as mandatory | All attributes |
| mandatory | Set attribute as mandatory | All attributes |
| maxitems | Maximums item which can be selected | "contentid" attribute |
| maxlength | Maximum characters allowed in input field | "text" attribute |
| multiple | Allow multiple values to be selected | "list", "contentlist", "contented", "user" attributes |
| name | Name of attribute. Used in display templates to retrieve value. | All attributes |
| regexp | Regular expression for validating input. | Most attributes |
| showinsite | Only show attribute in specified site(s) by specifying alias for site(s). Several sites may be entered using comma as separator.
Example: showinsite="/intranet/,/extranet/" | Most attributes |
| showinsite | Only show attribute in specified site(s) by specifying alias for site(s). Several sites may be entered using comma as separator.
Example: showinsite="/intranet/,/extranet/" | Most attributes |
| title | Title of field (used in input screen). If no value is specified "name" of attribute is used. | All attributes |
| type | Attribute type, e.g. text or image. | All attributes |

## Content properties

There exists a number of content properties in FlytCMS which attributes can be mapped to. By mapping an attribute to a content property, that property will automatically get the same value as the attribute.

**Example:**
A template has an attribute "heading" and you want that attribute to be used as title of all pages in search etc. This is done by mapping the attribute "heading" to the content property "title":

```
<attribute name="heading" mapto="title" type="text"/>
```

**Example:**
A template for publishing job posting has an attribute "deadline" and you want the job posting to be removed when "deadline" is reached. This is done my mapping the "deadline" to the content property "expiredate":

```
<attribute name="deadline" mapto="expiredate" type="date"/>
```

**Example:**
A template for publishing links has an attribute "link". Pages published with this template should not have a display template, but lead to an external URL:

```
<attribute name="link" mapto="url" type="url"/>
```

**Available content properties:**

| Property | Description |
| --- | --- |
| alttitle | Alternative title |
| description | Description of page / ingress |
| expiredate | Expire date - arcticle is archived or removed after this date |
| image | Image (generic name for use in frontpage etc) |
| owner | Owner (department) |
| ownerperson | Owner (user) |
| publishdate | Publish date |
| title | Title |
| url | URL (must be specified for file and URL templates) |