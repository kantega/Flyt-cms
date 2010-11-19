/*
 * Copyright 2010 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


$(document).ready(function(){
    openaksess.navigate.updateNavigator(undefined, true)
});

/********************************************************************************
 *
 * Content layout specific implementation of the openaksess.admin.setLayoutSpecificSizes.
 * Overrides the default implementation. See admin.js for further details.
 *
 ********************************************************************************/

openaksess.admin.setLayoutSpecificSizes = function (elementProperties){
    $("html, body").css("overflow", "hidden");
    var filteroptionsHeight = $("#Filteroptions").height(),
    statusbarHeight = $("#Statusbar").height(),
    navigationWidth = elementProperties.navigation.width,
    $navigator = $("#Navigator"),
    navigatorPaddingTop = $navigator.css("paddingTop"),
    navigatorPaddingBottom = $navigator.css("paddingBottom"),
    $buttons = $('#EditContentButtons'),
    $mainPane = $('#MainPane'),
    mainPaneHeight = (elementProperties.window.height-elementProperties.top.height),
    $content = $('#Content'),
    $contentHints = $('#NavigateContentHints'),
    $mainPaneContent = $("#MainPaneContent"),
    mainPaneContentPaddingTop = 0,
    mainPaneContentPaddingBottom = 0;
    var $mainContentIframe = $("#Maincontent");

    if ($mainPaneContent) {
        mainPaneContentPaddingTop = $mainPaneContent.css("paddingTop");
        mainPaneContentPaddingBottom = $mainPaneContent.css("paddingBottom");
    }

    var buttonsHeight = 0;
    if ($buttons && !$buttons.is(":hidden")) {
        buttonsHeight = $buttons.height();
    }

    var contentHintsHeight = 0;
    if ($contentHints && !$contentHints.is(":hidden")) {
        contentHintsHeight = $contentHints.height();
    }

    var preferredNavigationWidth = openaksess.admin.userpreferences.getPreference(openaksess.admin.userpreferences.keys.formadmin.navigationwidth);
    if (preferredNavigationWidth) {
        var $navigation = $("#Navigation");
        $navigation.width(preferredNavigationWidth + "px");
        navigationWidth = preferredNavigationWidth;
    }
    var mainPaneWidth = (elementProperties.window.width-navigationWidth-elementProperties.framesplit.width);


    openaksess.common.debug("openaksess.admin.setLayoutSpecificSizes(): filteroptionsHeight: "+filteroptionsHeight+", statusbarHeight"+statusbarHeight + ", buttonsHeight: " + buttonsHeight);

    $navigator.height(elementProperties.window.height-elementProperties.top.height-filteroptionsHeight-parseInt(navigatorPaddingTop)-parseInt(navigatorPaddingBottom));
    $content.height(elementProperties.window.height-elementProperties.top.height-statusbarHeight);
    $mainPane.height(mainPaneHeight).width(mainPaneWidth);
    $mainContentIframe.height(elementProperties.window.height-elementProperties.top.height-statusbarHeight-buttonsHeight-contentHintsHeight).width(mainPaneWidth);

    if ($mainPaneContent) {
        $mainPaneContent.height(mainPaneHeight-parseInt(mainPaneContentPaddingTop)-parseInt(mainPaneContentPaddingBottom)-statusbarHeight-buttonsHeight);
    }

};



openaksess.navigate.navigatorResizeOnStart = function() {
    openaksess.common.debug("openaksess.formadmin.navigatorResizeOnStart(): Resize start");
    openaksess.admin.userpreferences.deletePreference(openaksess.admin.userpreferences.keys.formadmin.navigationwidth);
};

openaksess.navigate.navigatorResizeOnStop = function() {
    openaksess.common.debug("openaksess.formadmin.navigatorResizeOnStop(): Resize stop");
    openaksess.admin.userpreferences.setPreference(openaksess.admin.userpreferences.keys.formadmin.navigationwidth, $("#Navigation").width());
};

openaksess.navigate.navigatorResizeOnResize = function() {
    $.event.trigger("resize");
};

openaksess.navigate.getNavigatorAction = function() {
    var navigatorAction = properties.contextPath + "/admin/formadmin/navigator";
    openaksess.common.debug("formadmin.openaksess.navigate.getNavigatorAction(): Action: " + navigatorAction);
    return navigatorAction;
};

openaksess.navigate.getItemIdentifierFromNavigatorHref = function(href) {
    return openaksess.common.getQueryParam("itemIdentifier", href);
};