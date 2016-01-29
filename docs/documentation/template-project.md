# Description of template project

Description of the various files included when you create a project using Maven

## webapp/src/conf

Contains database connection and other project specific configuration. Configuration in this file is merged with configuration from Aksess when you build your project.

## webapp/src/resources/

Property files for locale specific text in templates.  A default file for Norwegian and English is included.

## webapp/src/webapp/

Web application main directory. This is where all display templates, CSS, static images and other files which create a site are placed.  This folder is merged with OpenAksess files to create a WAR file when you build your project.

### 403.jsp
File displayed for HTTP 403 (Permission denied) error message. Replace content with your own custom content.

### 404.jsp
File displayed for HTTP 404 (File not found) error message. Replace content with your own custom content.

### 500.jsp
File displayed for HTTP 500 (Server error) error message. Replace content with your own custom content.

### WEB-INF/web.xml
Standard Java web application descriptor.

### WEB-INF/applicationContext-project.xml
Defines Spring beans used by the project and FlytCMS. Put project specific Spring beans here.

### WEB-INF/aksess-templateconfig.xml
This is where you configure content templates, display templates and other information used by FlytCMS.  See Developing templates for more information

### WEB-INF/jsp/common/templates/
Contains standard templates which can be used as a base for creating your own site. See Standard templates for more information about these templates.

### WEB-INF/jsp/site/
Contains site specific files, such as templates and JSP files used for design. This folder can be renamed to a more suitable name, e.g "intranet". If you rename this folder, remember to rename the alias of this site in aksess-templateconfig.xml.  E.g rename "/site/" to "/intranet/".

### WEB-INF/jsp/site/index.jsp
Display template for frontpage.

### WEB-INF/jsp/site/include/design/
JSP design files for frontpage (frontpage.jsp) and other templates (standard.jsp). Modify these files and CSS files to implement your own design.
