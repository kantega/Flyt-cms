(function() {
    // Load plugin specific language pack - not needed
    tinymce.PluginManager.requireLangPack('aksess_inserttable');

	tinymce.create('tinymce.plugins.InsertTable', {
		init : function(ed, url) {
            this.editor = ed;
            this.url = url;
            this.baseUrl = url.replace("aksess/tiny_mce/plugins/aksess_inserttable", "");

			// Register command
            ed.addCommand('insertTableCmd', this._openPopup, this);

			// Register button
			ed.addButton('aTable', {
                title : 'aksess_inserttable.desc',
                cmd : 'insertTableCmd',
                'class': 'mce_table'
            });
		},

		getInfo : function() {
			return {
				longname : 'Insert Table',
				author : 'Kantega AS',
				authorurl : 'http://www.kantega.no',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},

        _openPopup : function() {
            var modifyExisting = false;
            var tblElm = this.editor.dom.getParent(this.editor.selection.getNode(), 'table');
            if (tblElm) {
                modifyExisting = true;
            }

            openaksess.common.modalWindow.open({
                title: this.editor.getLang('aksess_inserttable.popup_title', 'Sett inn tabell'),
                iframe:true,
                href: this.baseUrl + "publish/popups/InsertTable.action?edit=" + encodeURI(modifyExisting),
                width: 600,
                height:400});
        }

	});

	// Register plugin
	tinymce.PluginManager.add('aksess_inserttable', tinymce.plugins.InsertTable);
})();
