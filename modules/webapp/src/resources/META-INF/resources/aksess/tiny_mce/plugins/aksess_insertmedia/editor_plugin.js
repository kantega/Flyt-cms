(function() {
    // Load plugin specific language pack - not needed
//    tinymce.PluginManager.requireLangPack('aksess_insertmedia');

	tinymce.create('tinymce.plugins.InsertMedia', {
		init : function(ed, url) {
            this.editor = ed;
            this.url = url;

			// Register command
            ed.addCommand('insertMediaCmd', this._openPopup, this);

			// Register button
			ed.addButton('image', {
                title : 'InsertMedia.button',
                cmd : 'insertMediaCmd'
            });
		},

		getInfo : function() {
			return {
				longname : 'Insert Media',
				author : 'Kantega AS',
				authorurl : 'http://www.kantega.no',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},

        _openPopup : function() {
            var id = -1;
            var elm = this.editor.selection.getNode();
            if (elm != null && elm.nodeName == "IMG") {
                var src = this.editor.dom.getAttrib(elm, 'src');
                var newMultimediaStartToken = "/multimedia/";
                if (src.indexOf(newMultimediaStartToken) != -1) {
                    src = src.substring(src.indexOf(newMultimediaStartToken) + newMultimediaStartToken.length, src.length);
                    id = src.substring(0, src.indexOf("/"));
                }
            }

            openaksess.editcontext.doInsertTag = true;
            // IE 7 & 8 looses selection. Must be kept and restored manually.
            this.editor.focus();
            this.editor.windowManager.bookmark = this.editor.selection.getBookmark(1);

            var href;
            if (id == -1) {
                href = "/admin/multimedia/Navigate.action";
            } else {
                href = "/admin/multimedia/EditMultimedia.action?id=" + id;
            }

            openaksess.common.modalWindow.open({
                title : "Sett inn multimedia",
                iframe : true,
                href : properties.contextPath + href,
                width : 860,
                height : 560});
        }

	});

	// Register plugin
	tinymce.PluginManager.add('aksess_insertmedia', tinymce.plugins.InsertMedia);
})();

tinyMCE.addI18n({no:{InsertMedia: {"button": "Sett inn bilde / mediafil fra mediaarkiv"}}});
tinyMCE.addI18n({en:{InsertMedia: {"button": "Insert image / mediafile from media archive"}}});