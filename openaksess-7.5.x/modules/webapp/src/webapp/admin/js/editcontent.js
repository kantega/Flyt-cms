/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/********************************************************************************
 * Namespace for the publish layout
 ********************************************************************************/






/********************************************************************************
 * Overridden functions from inherited namespaces.
 ********************************************************************************/

/**
 * Publish layout specific implementation. Overrides the default openaksess.admin.setLayoutSpecificSizes.
 *
 * @param elementProperties
 */
openaksess.admin.setLayoutSpecificSizes =  function(elementProperties) {
    openaksess.common.debug("editcontent.setLayoutSpecificSizes()");

    var $sidebar = $("#SideBar"),
    $mainPane = $('#MainPane'),
    mainPaneHeight = elementProperties.window.height-elementProperties.top.height,
    sidebarWidth = $sidebar.outerWidth(true),
    mainPaneWidth = elementProperties.window.width-elementProperties.framesplit.width-sidebarWidth,
    editContentButtonsHeight = parseInt($("#EditContentButtons").outerHeight(true));

    $mainPane.height(mainPaneHeight).width( mainPaneWidth);
    $('#EditContentMain').height( mainPaneHeight-editContentButtonsHeight).width(mainPaneWidth);
};