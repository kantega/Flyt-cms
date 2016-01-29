# Display templates

Display templates are JSP files used for generating HTML to end-users. Display templates are built using HTML and JSP Tag libraries.


The following FlytCMS tag libraries (and other standard/third party tag libraries such as JSTL) are used to create display templates:

| Prefix | URI | Description |
| --- | --- | --- |
| aksess | http://www.kantega.no/aksess/tags/aksess | Main library |
| kantega | http://www.kantega.no/aksess/tags/commons | Utility library |
| menu | http://www.kantega.no/aksess/tags/menu | Menu library |
| photo | http://www.kantega.no/aksess/tags/photo | Library for generating photo album |

## Example: Basic template

The following example shows a simple display template. The tag <aksess:getattribute> from the Aksess tag library is used to get two content attributes: "title" and "body text":

```
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<html>
<head>
<title>
		<aksess:getattribute name="title"/>
</title>
</head>
<body>
		<aksess:getattribute name="body text"/>
</body>
</html>
```

See [Taglib API reference](http://opensource.kantega.no/aksess/api/) for a complete reference on FlytCMS tag libraries

See [JSTL](https://jstl.java.netl) for a reference on JSTL.

**Setting encoding of generated page and source:**

```
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
```

**Including the FlytCMS main tag library:**

```
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
```

**Retrieve and display content attribute:**

```
<aksess:getattribute name="title"/>
```

## <aksess:getattribute> - retrive and display content attribute

The most important tag in the Aksess tab libraries is <aksess:getattribute> which retrieves a content attribute for a page. As default this page will return the named content attribute of the current page:

Example: Get attribute with name "text" from current page:

```
<aksess:getattribute name="text"/>
```

It is also possible to get a content attribute from another page than the current page by specifying a page alias or id of another page: Example: Get attribute with name "phonenumber" from frontpage:

```
<aksess:getattribute name="phonenumber" contentid="/"/>
```

For simple attributes such as text attributes <aksess:getattribute/> will return the value (content) of the attribute. For more complex attributes such as media attributes <aksess:getattribute/> will return a HTML tag as default (e.g. img or object). To get other properties of attributes set the "property" attribute on the <aksess:getattribute/> tag:

Example: Get width of media attribute with name "picture":

```
<aksess:getattribute name="picture" property="width"/>
```

## <aksess:exists> and <aksess:notexists> - checking if attributes exists

It if often necessary to check if a attribute exists and has a value. This is done using the <aksess:exists> tag. Example: Display attribute "text" if it exists and has a value:

```
<aksess:exists name="text">
    <div>
        <aksess:getattribute name="text"/>
    </div>
</aksess:exists>
```

Example: Display default text if attribute "text" does not exists or has no value:

```
<aksess:notexists name="text">
    ...default text...
</aksess:notexists>
```

## <aksess:getcollection> - generating lists of pages

To generate a list of pages, use the <aksess:getcollection> tag. As default this tag will list a sub pages for the current page.

Example: List all sub pages of current page and display a link to each page using the <aksess:link> tag:

```
<aksess:getcollection name="list">
    <p>
  	<aksess:link collection="list">
        <aksess:getattribute name="title" collection="list"/>
    </aksess:link>
    </p>
</aksess:getcollection>
```

## <menu:printlistmenu> - generating multilevel list menus

The easiest way to generate an expanding multilevel list menu is by using the <menu:printlistmenu> tag. This tag will generate a menu using ul and li tags, which can be styled using CSS.

Example: Print a menu using lists (UL+LI) elements consisting of pages in "Global menu"

```
<menu:printlistmenu associationcategory="Global menu">
    <a href="<${entry.url}" class="selected">${entry.title}></a>
</menu:printlistmenu>
```