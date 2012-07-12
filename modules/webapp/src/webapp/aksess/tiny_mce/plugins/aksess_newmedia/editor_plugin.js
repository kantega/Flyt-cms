(function() {
    // Load plugin specific language pack - not needed
//    tinymce.PluginManager.requireLangPack('aksess_insertmedia');

	tinymce.create('tinymce.plugins.NewMedia', {
		init : function(ed, url) {
            this.editor = ed;
            this.url = url;

			// Register command
            ed.addCommand('newMediaCmd', this._openPopup, this);

			// Register button
			ed.addButton('aksess_newmedia', {
                title : 'NewMedia.button',
                cmd : 'newMediaCmd',
                image: url + '/img/uploadimage.png'
            });
		},

		getInfo : function() {
			return {
				longname : 'Insert New Media',
				author : 'Kantega AS',
				authorurl : 'http://www.kantega.no',
				version : "1.0"
			};
		},

        _openPopup : function() {
            openaksess.editcontext.doInsertTag = true;
            // IE 7 & 8 looses selection. Must be kept and restored manually.
            this.editor.focus();
            this.editor.windowManager.bookmark = this.editor.selection.getBookmark(1);

            openaksess.common.modalWindow.open({
                title : "Last opp nytt bilde",
                iframe : true,
                href : properties.contextPath + "/admin/multimedia/ViewUploadMultimediaForm.action?fileUploadedFromEditor=true",
                width : 450,
                height : 450});
        }

	});

	// Register plugin
	tinymce.PluginManager.add('aksess_newmedia', tinymce.plugins.NewMedia);
})();

tinyMCE.addI18n({no:{NewMedia: {"button": "Last opp nytt bilde/mediafil"}}});
tinyMCE.addI18n({en:{NewMedia: {"button": "Upload new image/media"}}});