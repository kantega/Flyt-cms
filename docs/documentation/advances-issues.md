# Advanced issues


## Using the MVC pattern

Some times it is necessary to create webpages with a lot of business logic. It can be smart to separate your presentation from your business logic using the MVC pattern (Model, View, Controller).

Adding business logic to display templates can be done by implementing the AksessController interface which has two methods:

```
public interface AksessController {

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    public String getDescription();
}
```

The method handleRequest performs all your business logic and return all data needed by the view in a Map. The elements in this map will be available as request attributes in your JSP view.

### Example

**Implement a controller class**

```
package com.company.control;

import no.kantega.publishing.controls.AksessController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

public class MyController implements AksessController {
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map map = new HashMap();

        // Add your business logic here

         // Add information needed by view into map by calling: map.put("key", object);

        return map;
    }

    public String getDescription() {
        return "MyController";
    }
}
```

**Add the controller to your Spring configuration**

Create a instance of the MyController class by adding it to your Spring configuration (applicationContext-project.xml):

```
<bean id="myController" class="com.company.control.MyController"/>
```

**Add the controller to your display template**

Add the controller to your display template by editing the aksess-templateconfig.xml file:

```
<displayTemplate databaseId="12" id="My template">
    <contentTemplate id="My template"/>
    <name>My template</name>
    <description>Description for editors</description>
    <view>/WEB-INF/jsp/templates/mytemplate.jsp</view>
    <controllers>
        <controller>**myController<**/controller>
    </controllers>
</displayTemplate>
```