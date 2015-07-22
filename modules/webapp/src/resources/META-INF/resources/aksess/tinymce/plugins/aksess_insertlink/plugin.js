/**
 * Created by rubfag on 15.06.2015.
 */

tinymce.PluginManager.add('aksess_insertlink', function(editor, url){

    function makeLinkPopupWindow() {
        var selection = editor.selection;
        // No selection and not in link
        if (selection.isCollapsed() && !editor.dom.getParent(selection.getNode(), 'A')) {
            return;
        }
        var href = '';
        var anchor = '';
        var newWindow = false;
        var elm = editor.selection.getNode();
        elm = editor.dom.getParent(elm, "A"); // A?
        if (elm != null && elm.nodeName == "A") {
            href = editor.dom.getAttrib(elm, 'href');
            var onclick = editor.dom.getAttrib(elm, 'onclick');
            if (onclick.indexOf('window.open') != -1) {
                newWindow = true;
            }
        }

        // IE 7 & 8 looses selection. Must be kept and restored manually.
        editor.focus();
        editor.windowManager.bookmark = editor.selection.getBookmark(1);

        openaksess.common.modalWindow.open({
            title:"Sett inn lenke",
            href: properties.contextPath + "publish/popups/InsertLink.action?url=" + encodeURI(href) + "&isOpenInNewWindow=" + encodeURI(newWindow) + "&isMiniAdminMode=" + encodeURI(miniAdminMode),
            width: 650,
            height:300
        });
    }

    // Add a button that opens a window
    editor.addButton('aksess_insertlink', {
        //image: url + '/img/uploadimage.png',
        icon: 'link',
        shortcut:"Meta+K",
        onclick: makeLinkPopupWindow
    });

    editor.addButton("unlink", {
        icon:"unlink",
        tooltip:"Remove link",
        cmd:"unlink",
        stateSelector:"editor[href]"
    });

    // Adds a new item to the insert menu
    editor.addMenuItem('aksess_insertlink', {
        text: 'Insert image',
        //image: url + '/img/uploadimage.png',
        icon: 'link',
        shortcut:"Meta+K",
        context: 'insert',
        stateSelector:"editor[href]",
        onclick: makeLinkPopupWindow
    });
});