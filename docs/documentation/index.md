
# Getting started


This tutorial will show you how to get started using FlytCMS by using Maven 3 to create a project with standard templates.


## Before you start

Before you start you will to have the software installed on your development machine:

*   JDK 8 or newer
*   [Maven 3](http://maven.apache.org/download.html)
*   A Java IDE (or text editor)

## Setting up a database

You will also need to have a database server running on your local machine or on a remote server. The following database servers are recommended:

*   MS SQL Server
*   MySQL

Create an empty database and a user with read/write access and rights to create tables.

## Create a blank project from a template

Create a blank FlytCMS project by using mvn archetype:generate

```
mvn archetype:generate -DarchetypeGroupId=org.kantega.archetypes -DarchetypeArtifactId=openaksess-project -DarchetypeVersion=2.5 -DarchetypeRepository=https://opensource.kantega.no/nexus/content/repositories/releases
```

You will now be asked to enter some configuration parameters:

```
> **databaseType**
> Type of database server.  Allowed values are  sqlserver or mysql.
>
> **databaseHost**
> Hostname or IP address of database server, e.g database.company.com
>
> **databaseName**
> Name of the database you created earlier, e.g mydatabase
>
> **databaseUsername**:
> Username to login to database
>
> **databasePassword:**
> Password to login to database
>
> **aksessVersion:**
> The version of Flyt CMS to use, see what the latest is here: opensource.kantega.no/nexus/content/repositories/releases/org/kantega/openaksess/openaksess-webapp/
```

After you have entered the configuration parameters, Maven will ask you to confirm your settings, press Y to continue.

### webapp
Web module - this is where you create your JSP view templates, CSS and other web files.

For a further description of all the files in template project,  see [template project files](template-project.md).

## Building and running your site with maven

During development the far easiest way to run your site is using the Maven Aksess plugin.

Launch FlytCMS using mvn:

```
> mvn aksess:run
```

This will start FlytCMS in Jetty and open a web browser to display your site.

## Creating an initial admin user and logging in

When your web browser opens you will get a 403 page, since no pages have been published yet.

Log in to the administration interface by appending: "admin/" to the URL in your web browser, e.g: [http://localhost:8080/myproject-webapp/admin/](http://localhost:8080/myproject-webapp/admin/)

The first time you attempt to login to the administration system no roles and users will exists, so the system will ask you to create a user with administrative privileges.

Enter a username and password and the system will create a user with the specified user id and password.  The role "admin" will also be created and the user will get membership to this role.

**NB!** For security reasons you must access the administrative interface from the same machine which is running FlytCMS to create an initial user or you will be asked to enter a code saved in the file **security/initialusertoken.txt** on the server.

After the user has been created, press continue to log in to the administrative interface with the user you just created.

## Create a home page

Create a home page:

*   Press the "admin" tab
*   Click "Sites and domains"
*   Click "Create homepage"


## Edit the home page

Go to the "Content" tab to edit the homepage.  Press "Publish" to save your changes.

## Create a news archive

Next we want to create a news archive directly under the frontpage.  Press the "New subpage" link:

Select the "News archive" template and continue.

Enter the title "News archive" for the news archive and press "Publish".

## Publish a news item

Using the left navigation menu, make sure you have the "News archive" page selected.  Press the "New page"link.

The "News article" template should now be selected since it is the only one available.  Press "Continue".

Enter a title and some text in your news arcticle and press "Publish".

## Whats next

Congratulations! You have now setup your first installation of FlytCMS.  Your next steps should be one of the following:

*   Try out the standard templates by adding more pages to your site
*   Modify the [standard templates](standard-templates.md) to suit your needs
*   Implement a design by modifying the files in "WEB-INF/jsp/site/design/
*   Learn how to [develop your own templates](developing-templates.md).

