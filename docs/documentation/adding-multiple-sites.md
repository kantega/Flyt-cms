# Adding multiple sites

It is possible to have as many sites as you like in one OpenAksess installation.  E.g. one internet and one extranet site in the same installation.

When working with multiple sites you can choose from reusing templates across all sites or using a site specific template.

## Adding a site

To add another site you need to do the following:

*   Create a folder for your site under "WEB-INF/jsp/", eg "WEB-INF/jsp/**intranet**/"
*   Create a frontpage content template or reuse the template from another site.
*   Create a frontpage display template named "index.jsp", eg "WEB-INF/jsp/**intranet**/index.jsp".
*   Add the site and templates to "WEB-INF/aksess-templateconfig.xml" using the alias "**/intranet/**". The alias should have the same name as the folder you created your frontpage display template in.
*   Restart FlytCMS or reload the template configuration
*   Create the "Front page"

