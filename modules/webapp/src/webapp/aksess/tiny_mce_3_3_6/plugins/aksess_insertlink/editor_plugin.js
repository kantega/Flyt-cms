(function() {
    // Load plugin specific language pack - not needed
//    tinymce.PluginManager.requireLangPack('aksess_insertlink');

	tinymce.create('tinymce.plugins.InsertLink', {
		init : function(ed, url) {
            this.editor = ed;
            this.url = url;

			// Register command
            ed.addCommand('insertLinkCmd', this._openPopup, this);

			// Register button
			ed.addButton('link', {
                title : 'advlink.link_desc',
                cmd : 'insertLinkCmd'
            });

            // Register shortcut
            ed.addShortcut('ctrl+k', 'advlink.advlink_desc', 'insertLinkCmd');
		},

		getInfo : function() {
			return {
				longname : 'Insert Link',
				author : 'Kantega AS',
				authorurl : 'http://www.kantega.no',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},

        _openPopup : function() {
            var se = this.editor.selection;
            // No selection and not in link
            if (se.isCollapsed() && !this.editor.dom.getParent(se.getNode(), 'A')) {
                return;
            }

            var href = '';
            var anchor = '';
            var newWindow = false;
            var elm = this.editor.selection.getNode();
            elm = this.editor.dom.getParent(elm, "A");
            if (elm != null && elm.nodeName == "A") {
                href = this.editor.dom.getAttrib(elm, 'href');
                var onclick = this.editor.dom.getAttrib(elm, 'onclick');
                if (onclick.indexOf('window.open') != -1) {
                    newWindow = true;
                }
            }

            // IE 7 & 8 looses selection. Must be kept and restored manually.
            this.editor.focus();
            this.editor.windowManager.bookmark = this.editor.selection.getBookmark(1);
            
            openaksess.common.modalWindow.open({
                title:"Sett inn lenke",
                iframe:true,
                href: "popups/InsertLink.action?url=" + encodeURI(href) + "&isOpenInNewWindow=" + encodeURI(newWindow),
                width: 600,
                height:300});
        }

	});

	// Register plugin
	tinymce.PluginManager.add('aksess_insertlink', tinymce.plugins.InsertLink);
})();
