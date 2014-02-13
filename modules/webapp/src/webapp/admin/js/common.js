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

/*
 * This script expects the following properties to be set:
 * * debug
 * * contextPath
 * * contentRequestHandler
 * * thisId
 */

/********************************************************************************
 * Common OpenAksess functions. Declares the global namespace for all objects.
 *
 * Used both in standard admin context and by MiniAksess.
 ********************************************************************************/

var openaksess =  {
    // All Javascript objects should use this namespace
};

openaksess.common = {
    columnize : function() {
        var columnized = $(".columnized");
        openaksess.common.debug("openaksess.common(): Number of elements found to columnize: " + columnized.size());
        if (columnized.size() > 0) {
            var columncount = 1;
            var classes = columnized.attr("class").split(" ");
            for (var i = 0; i < classes.length; i++) {
                var c = classes[i];
                if (c.indexOf("columnCount") > -1) {
                    columncount = parseInt(c.substring("columnCount".length, c.length));
                }
            }
            openaksess.common.debug("openaksess.common(): Column count: " + columncount);
            columnized.makeacolumnlists({cols:columncount,colWidth:0,equalHeight:false,startN:1});
        }
    },

    /**
     * Returns the value of a query string parameter
     *
     * @param paramName Query parameter name
     * @param queryString The total query string
     */
    getQueryParam : function(paramName, queryString) {
        var val = null;
        var inx = queryString.indexOf(paramName + "=");
        if (inx != -1) {
            val = queryString.substring(inx + paramName.length + 1, queryString.length);
            if (val.indexOf("&") != -1) {
                val = val.substring(0, val.indexOf("&"));
            }
        }
        return val;
    },

    /**
     * Returns the uri for a content with a given associationId
     *
     * @param associationId
     */
    getContentUrlFromAssociationId : function(associationId) {
        var url = " " + properties.contextPath + "/" + properties.contentRequestHandler + "?" + properties.thisId + "=" + associationId;
        openaksess.common.debug("openaksess.common.getContentUrlFromAssociationId(): associationId: " + associationId + ", returns: " + url);
        return url;
    },

    /**
     * Prints a debug message to Firebug's debug console.
     *
     * @param msg Debug message. Convention: 'functionName(): value'
     */
    debug : function (msg) {
        var debugEnabled = properties.debug;
        if (debugEnabled && typeof console != 'undefined') {
            var now = new Date();
            console.log(now.getHours()+":"+now.getMinutes()+":"+now.getSeconds()+now.getMilliseconds()+ " - " + msg);
        }
    },

    isPopup : function() {
        return window.parent != window || window.name == 'openAksessPopup';
    },


    /**
     * Responsible for drawing pop-ups and modals.
     */
    modalWindow : function() {

        var $content = '';
        var $overlay = $("<div/>").css({
                    position : 'absolute',
                    background : '#ffffff',
                    opacity: '0'
                })
                .attr("id", "Contentoverlay");
        var $iframe = $('<iframe id="externalSite" frameborder="0" src="" />');

        var config = {
            title: 'OpenAksess',
            titlebar: true,
            resizable: true,
            modal: true,
            iframe: false,
            closeOnEscape: true,
            height: 400,
            width: 600,
            horizontalPadding: 0,
            verticalPadding: 0,
            open: function(){
                $(window).trigger("resize");
            },
            autoResize: true,
            autoOpen: true,
            close: function() {
                openaksess.common.debug("openaksess.common.modalWindow.close()");
            },
            overlay: {
                opacity: 0.2,
                background: "black"
            }
        };

        config.resizeStart = function(){
            openaksess.common.debug("openaksess.common.modalWindow.resizeStart()");
            if (config.iframe) {
                openaksess.common.debug("openaksess.common.modalWindow.resizeStart(): Has iframe. Adding overlay");
                var height = $iframe.height();
                var width = $iframe.width();
                $overlay.css({height : height + "px", width : width + "px"});
                $iframe.before($overlay);
            }
        };
        config.resize = function() {
            if (config.iframe) {
                var height = $iframe.height();
                var width = $iframe.width();
                $overlay.css({height : height + "px", width : width + "px"});
            }
        };

        config.resizeStop = function(){
            if (config.iframe) {
                $overlay.remove();
            }
        };

        return {
            /**
             * Opens a modal with the desired properties.
             *
             * @param additionalConfig
             */
            open : function(additionalConfig){
                openaksess.common.debug("openaksess.common.modalWindow.open(): additionalConfig: " + additionalConfig);

                if (additionalConfig && typeof(additionalConfig) == 'object') {
                    openaksess.common.debug("openaksess.common.modalWindow.open(): Extending default config with user supplied config.");
                    $.extend(config, additionalConfig);
                }

                if (!config.position) {
                    config.position = [(($(window).width()-config.width)/2),(($(window).height()-config.height)/2)];
                }


                if (config.iframe) {
                    var href = config.href;
                    if (config.href.indexOf("?") == -1) {
                        href = href + "?";
                    } else {
                        href = href + "&";
                    }
                    href = href + "dummy=" + new Date().getTime();
                    $content = $iframe.attr("src", href);
                } else {
                    $content = $("<div/>").load(config.href);
                }

                openaksess.common.debug("openaksess.common.modalWindow.open(): Opening modal window.");

                $content.dialog(config);

                if (config.iframe) {
                    $content.width(config.width - config.horizontalPadding).height(config.height - config.verticalPadding);
                }

                openaksess.common.debug("openaksess.common.modalWindow.open(): Modal window opened.");

            },

            /**
             * Closes the current modal.
             */
            close : function(){
                $content.dialog('close');
            },

            /**
             * Sets the modal window's title (top bar)
             */
            setTitle: function(title) {
                $content.dialog( "option", "title", title );
            }
        };

    }(),

    /**
     * Functionality for dealing with browser cookies.
     */
    cookies : {
        /**
         * Creates a cookie
         * @param name - cookie name
         * @param value - Cookie data
         * @param days - Time to live in days. If omitted, the cookie is killed when the browser session ends.
         */
        create : function (name,value,days) {
            var expires = "";
            if (days) {
                var date = new Date();
                date.setTime(date.getTime()+(days*24*60*60*1000));
                expires = "; expires="+date.toGMTString();
            }

            document.cookie = name+"="+value+expires+"; path=/";
        },

        /**
         * Returns the value of a cookie
         * @param name - cookie name.
         */
        read : function(name) {
            var nameEQ = name + "=";
            var ca = document.cookie.split(';');
            for(var i=0;i < ca.length;i++) {
                var c = ca[i];
                while (c.charAt(0)==' ') c = c.substring(1,c.length);
                if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
            }
            return null;
        },

        /**
         * Deletes a cookie.
         * @param name
         */
        erase : function (name) {
            openaksess.common.cookies.create(name,"",-1);
        }
    },

    /**
     * Triggers an OpenAksess specific event.
     * All events are prefixed with openaksess.[eventname], and
     * may possibly have data associated.
     *
     * If no context is given, the event is bound to the html element.
     *
     * Known openaksess events are:
     * - openaksess.navigatorSelect: When an item in the navigator is selected.
     * - openaksess.navigatorOpen: When a navigator folder is opened.
     * - openaksess.navigatorClose: When a navigator folder is closed.
     *
     * @param eventName - Name of the event to trigger, without prefix
     * @param data - Arbitrary data to send with the event.
     * @param context - The context within which the event is visible, e.g. "body".
     * Must be a jQuery selector string or a jQuery object.
     */
    triggerEvent: function(eventName, data, context){
        if (!context) {
            context = $("html");
        }
        if (!(context instanceof jQuery)) {
            context = $(context);
        }
        context.trigger("openaksess." + eventName, data);
    },

    abbreviate: function(str, maxlen, suffix) {
        if (str.length <= maxlen) {
            return str;
        }
        if (!suffix) {
            suffix = "...";
        }
        return str.substring(0, maxlen) + suffix;
    },

    addTouchScrollToIFrame: function (iFrame, container) {
        if(!navigator.userAgent.match(/iPad|iPhone/i)) {
            return;
        }

        var mouseY = 0;
        var mouseX = 0;

        var iframeBody = $(iFrame).get(0).contentWindow.document.body;

        if (iframeBody) {
            iframeBody.addEventListener('touchstart', function(e) {
                mouseY = e.targetTouches[0].pageY;
                mouseX = e.targetTouches[0].pageX;
            });

            //update scroll position based on initial drag position
            iframeBody.addEventListener('touchmove', function(e) {
                e.preventDefault(); //prevent whole page dragging

                var mask = $(container);
                mask.scrollLeft(mask.scrollLeft() + mouseX-e.targetTouches[0].pageX);
                mask.scrollTop(mask.scrollTop() + mouseY-e.targetTouches[0].pageY);
            });

        }
    }
};







