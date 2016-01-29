# Developing templates

## Introduction

To publish content types of information with FlytCMS you need to create your own templates.

Creating a new template for FlytCMS consists of the following steps:

1.  create a [content template](content-templates.md) (XML files which specify the content elements)
2.  create a [display template](display-templates.md) (JSP files which determine the presentation)
3.  [register the templates](registering-templates.md) and other information in the template configuration file

Aksess includes a set of standard templates, which may be used as a base to get you started.

## Glossary

### Association categories

It is often necessary to separate sub pages into different categories.  These categories are called "association categories" in Aksess.

**Example:**
A site may have a global menu and articles may have a list of related links.  This is implemented by creating the association categories "Global menu" and "Related links":

Each content template may have their own set of association categories.  E.g. all templates might have the global menu, but only the "article" template has "related links".

### Document types

Each page may be assigned a document type, e.g. "News article". Document types may be used in queries for pages, e.g. find all pages with document type "News article".

### Sites

Each Aksess installation may consist of several websites, e.g. intranet and Internet site. Each website may have it's own set of templates (or share templates).
