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
                title : 'media.desc',
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
            openaksess.editcontext.doInsertTag = true;
            openaksess.common.modalWindow.open({
                title:"Sett inn multimedia",
                iframe:true,
                href: "../multimedia/EditMultimedia.action",
                width: 840,
                height:600});
        }

	});

	// Register plugin
	tinymce.PluginManager.add('aksess_insertmedia', tinymce.plugins.InsertMedia);
})();
