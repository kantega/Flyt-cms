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
 * Overridden functions from inherited namespaces.
 ********************************************************************************/

/**
 * Publish layout specific implementation. Overrides the default openaksess.admin.setLayoutSpecificSizes.
 *
 * @param elementProperties
 */
openaksess.admin.setLayoutSpecificSizes =  function(elementProperties) {
    openaksess.common.debug("editmultimedia.setLayoutSpecificSizes()");

    var $sidebar = $("#SideBar");
    var $mainPane = $('#MainPane');
    $mainPane
            .height( (elementProperties.window.height-elementProperties.top.height) + 'px')
            .width( (elementProperties.window.width-elementProperties.framesplit.width-$sidebar.outerWidth(true)) + 'px');

    $('#MultimediaMain')
            .height( (parseInt($mainPane.height())-parseInt($("#EditMultimediaButtons").outerHeight(true))) + 'px')
            .width($mainPane.outerWidth());
};