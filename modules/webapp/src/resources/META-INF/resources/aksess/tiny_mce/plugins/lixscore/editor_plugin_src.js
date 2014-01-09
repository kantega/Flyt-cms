(function() {
    // Load plugin specific language pack - not needed
    // tinymce.PluginManager.requireLangPack('aksess_insertlink');

    tinymce.create('tinymce.plugins.LixScore', {
        init : function(ed, url) {
            this.editor = ed;
            this.url = url;
            // Register command
            ed.addCommand('LixScoreCmd', this._openPopup, this);

            // Register button
            ed.addButton('lixscore', {
                title : 'lix.desc',
                cmd : 'LixScoreCmd',
                image: url + '/img/lixscore.png'
            });

            // Register shortcut
            // ed.addShortcut('ctrl+k', 'advlink.advlink_desc', 'insertLinkCmd');
        },

        getInfo : function() {
            return {
                longname : 'Lix Score',
                author : 'Kantega AS',
                authorurl : 'http://www.kantega.no',
                version : tinymce.majorVersion + "." + tinymce.minorVersion
            };
        },

        _openPopup : function() {

            try{


                var s = this.editor.getContent();


                var longwords = 0;
                var words = 0;
                var lix = 0;


                //s = se.getContent();
                //strip the html
                s = s.replace(/(<([^>]+)>)/ig,"");
                s = s.replace(/&nbsp;/g,"");

                // Replace newlines with spaces.
                s = s.replace(/\/n/, ' ');
                // Remove trailing spaces.
                s = s.replace(/ $/, '');
                // Remove everything that isn't a space, period or regular character.
                s = s.replace(/[^A-Za-z0-9\.\?!: ]/g, '');

                // norwegian chars
                s = s.replace(/oslash/gi, "o");
                s = s.replace(/aring/gi, "a");
                s = s.replace(/aelig/gi, "e");

                if (s.length == 0) {
                    return false;
                }
                // If the last character isn't an end-of-sentence character we add one
                var last = s.charAt(s.length-1);
                if (last != '.' && last != '?' && last != '!' && last != ':') {
                    s = s+'.';
                }
                // Get gross length
                var length = s.length;
                // Remove end-of-sentence characters.
                s = s.replace(/[\.\?!:]/g, '');
                // Get number of sentences and word count
                var sentences = length-s.length;
                words = s.split(' ');
                wcount = words.length;

                // Check for long words.
                for (i in words) {
                    if (words[i].length > 6) {
                        longwords++;
                    }
                }

                lix = Math.round(wcount / sentences + 100 * longwords / wcount);

                openaksess.common.modalWindow.open({
                    title: "Lix",
                    iframe: true,
                    href: "popups/Lix.action?lix="+lix+"&wc="+wcount+"&lwc="+longwords+"&sent="+sentences,
                    width: 400,
                    height: 200
                });


            } catch (e){
                alert(e);
            }
        }

    });

    // Register plugin
    tinymce.PluginManager.add('lixscore', tinymce.plugins.LixScore);
})();
