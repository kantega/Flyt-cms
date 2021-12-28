// jQuery Context Menu Plugin
//
// Version 1.01-openaksess
//
// Cory S.N. LaViska
// A Beautiful Site (http://abeautifulsite.net/)
//
// More info: http://abeautifulsite.net/2008/09/jquery-context-menu-plugin/
//
// Terms of Use
//
// This plugin is dual-licensed under the GNU General Public License
//   and the MIT License and is copyright A Beautiful Site, LLC.
//
// Modified for better performance since original menu performs badly with many elements
//

if(jQuery)( function() {
    $.extend($.fn, {

        contextMenu: function(o, callback) {
            // Defaults
            if( o.menu == undefined ) return false;
            if( o.itemClass == undefined ) return false;
            if( o.itemTagName == undefined ) return false;
            if( o.inSpeed == undefined ) o.inSpeed = 0;
            if( o.outSpeed == undefined ) o.outSpeed = 0;
            // 0 needs to be -1 for expected results (no fade)
            if( o.inSpeed == 0 ) o.inSpeed = -1;
            if( o.outSpeed == 0 ) o.outSpeed = -1;
            // Loop each context menu
            $(this).each( function() {
                var el = $(this);
                // Add contextMenu class
                $('#' + o.menu).addClass('contextMenu');
                // Simulate a true right click
                $(this).mousedown( function(e) {
                    var evt = e;
                    var activeElement = null;
                    if (evt.target.tagName == o.itemTagName) {
                        var clicked = $(evt.target);
                        if (clicked.hasClass(o.itemClass)) {
                            activeElement = clicked;
                        }
                    }
                    evt.stopPropagation();

                    if (evt.button == 2 && activeElement != null) {
                        var offset = $(activeElement).offset();
                        evt.stopPropagation();

                        $(this).mouseup( function(e) {
                            e.stopPropagation();
                            $(this).unbind('mouseup');

                            //if( evt.button == 2 ) {
                            // Hide context menus that may be showing
                            $(".contextMenu").hide();
                            // Get this context menu
                            var menu = $('#' + o.menu);

                            if( activeElement.hasClass('disabled') ) return false;

                            // Detect mouse position
                            var d = {}, x, y;
                            if( self.innerHeight ) {
                                d.pageYOffset = self.pageYOffset;
                                d.pageXOffset = self.pageXOffset;
                                d.innerHeight = self.innerHeight;
                                d.innerWidth = self.innerWidth;
                            } else if( document.documentElement &&
                                    document.documentElement.clientHeight ) {
                                d.pageYOffset = document.documentElement.scrollTop;
                                d.pageXOffset = document.documentElement.scrollLeft;
                                d.innerHeight = document.documentElement.clientHeight;
                                d.innerWidth = document.documentElement.clientWidth;
                            } else if( document.body ) {
                                d.pageYOffset = document.body.scrollTop;
                                d.pageXOffset = document.body.scrollLeft;
                                d.innerHeight = document.body.clientHeight;
                                d.innerWidth = document.body.clientWidth;
                            }

                            (e.pageX) ? x = e.pageX : x = e.clientX;
                            (e.pageY) ? y = e.pageY : x = e.clientY;

                            // Prevent menu from going outside view
                            var menuHeight = $(menu).height();
                            if (y + menuHeight > d.innerHeight) {
                                y = d.innerHeight - menuHeight;
                            }

                            // Show the menu

                            $(document).unbind('click.contextMenu');

                            $(menu).css({ top: y, left: x }).fadeIn(o.inSpeed);
                            // Hover events
                            $(menu).find('A').mouseover( function() {
                                $(menu).find('LI.hover').removeClass('hover');
                                activeElement.parent().addClass('hover');
                            }).mouseout( function() {
                                $(menu).find('LI.hover').removeClass('hover');
                            });

                            // Keyboard
                            $(document).bind('keypress.contextMenu', function(e) {
                                switch( e.keyCode ) {
                                    case 38: // up
                                        if( $(menu).find('LI.hover').size() == 0 ) {
                                            $(menu).find('LI:last').addClass('hover');
                                        } else {
                                            $(menu).find('LI.hover').removeClass('hover').prevAll('LI:not(.disabled)').eq(0).addClass('hover');
                                            if( $(menu).find('LI.hover').size() == 0 ) $(menu).find('LI:last').addClass('hover');
                                        }
                                        break;
                                    case 40: // down
                                        if( $(menu).find('LI.hover').size() == 0 ) {
                                            $(menu).find('LI:first').addClass('hover');
                                        } else {
                                            $(menu).find('LI.hover').removeClass('hover').nextAll('LI:not(.disabled)').eq(0).addClass('hover');
                                            if( $(menu).find('LI.hover').size() == 0 ) $(menu).find('LI:first').addClass('hover');
                                        }
                                        break;
                                    case 13: // enter
                                        $(menu).find('LI.hover A').trigger('click');
                                        break;
                                    case 27: // esc
                                        $(document).trigger('click');
                                        break
                                }
                            });

                            // When items are selected
                            menu.find('A').unbind('click');
                            menu.find('LI:not(.disabled) A').click( function() {
                                $(document).unbind('click.contextMenu').unbind('keypress.contextMenu');
                                $(".contextMenu").hide();
                                // Callback
                                if( callback ) callback( $(this).attr('href').substr(1), activeElement, {x: x - offset.left, y: y - offset.top, docX: x, docY: y} );
                                return false;
                            });


                            // Feilen ligger i at det brukes unbind og bind p� click p� document

                            // Hide bindings
                            setTimeout( function() { // Delay for Mozilla
                                $(document).bind('click.contextMenu', function() {
                                    $(document).unbind('click.contextMenu').unbind('keypress.contextMenu');
                                    $(menu).fadeOut(o.outSpeed);
                                    return false;
                                });
                            }, 0);
                            //}
                        });
                    }
                });

                // Disable text selection
                if( $.browser.mozilla ) {
                    $('#' + o.menu).each( function() { $(this).css({ 'MozUserSelect' : 'none' }); });
                } else if( $.browser.msie ) {
                    $('#' + o.menu).each( function() { $(this).bind('selectstart.disableTextSelect', function() { return false; }); });
                } else {
                    $('#' + o.menu).each(function() { $(this).bind('mousedown.disableTextSelect', function() { return false; }); });
                }
                // Disable browser context menu (requires both selectors to work in IE/Safari + FF/Chrome)
                $(el).add($('UL.contextMenu')).bind('contextmenu', function() { return false; });

            });
            return $(this);
        },

        // Disable context menu items on the fly
        disableContextMenuItems: function(o) {
            if( o == undefined ) {
                // Disable all
                $(this).find('LI').addClass('disabled');
                return( $(this) );
            }
            $(this).each( function() {
                if( o != undefined ) {
                    var d = o.split(',');
                    for( var i = 0; i < d.length; i++ ) {
                        $(this).find('A[href="' + d[i] + '"]').parent().addClass('disabled');

                    }
                }
            });
            return( $(this) );
        },

        // Enable context menu items on the fly
        enableContextMenuItems: function(o) {
            if( o == undefined ) {
                // Enable all
                $(this).find('LI.disabled').removeClass('disabled');
                return( $(this) );
            }
            $(this).each( function() {
                if( o != undefined ) {
                    var d = o.split(',');
                    for( var i = 0; i < d.length; i++ ) {
                        $(this).find('A[href="' + d[i] + '"]').parent().removeClass('disabled');

                    }
                }
            });
            return( $(this) );
        },

        // Disable context menu(s)
        disableContextMenu: function() {
            $(this).each( function() {
                $(this).addClass('disabled');
            });
            return( $(this) );
        },

        // Enable context menu(s)
        enableContextMenu: function() {
            $(this).each( function() {
                $(this).removeClass('disabled');
            });
            return( $(this) );
        },

        // Destroy context menu(s)
        destroyContextMenu: function() {
            // Destroy specified context menus
            $(this).each( function() {
                // Disable action
                $(this).unbind('mousedown').unbind('mouseup');
            });
            return( $(this) );
        }

    });
})(jQuery);