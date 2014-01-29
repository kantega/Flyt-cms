function Clipboard(name){
    this.name = name;
}

Function.prototype.method = function (name, func) {
    this.prototype[name] = func;
    return this;
};

Clipboard.prototype.copy =  function(id){
    openaksess.common.debug(this.name + ".copy(): " + id );
    $.ajax({
        url: properties.contextPath + "/admin/publish/" + this.name + "/copy.action",
        data: { id: id },
        type: "POST"
    })
        .done(function(){
            openaksess.common.debug("Clipboard.copy(): success!");
        }).fail(function(jqXHR, textStatus, errorThrown){
            openaksess.common.debug("Clipboard.copy(): Failed!" + textStatus + " " + errorThrown );
        })
};

Clipboard.prototype.cut =  function(id){
    openaksess.common.debug(this.name + ".cut(): " + id );
    $.ajax({
        url: properties.contextPath + "/admin/publish/" + this.name + "/cut.action",
        data: { id: id },
        type: "POST"
    })
        .done(function(){
            openaksess.common.debug("Clipboard.cut(): success!");
        }).fail(function(jqXHR, textStatus, errorThrown){
            openaksess.common.debug("Clipboard.cut(): Failed!" + textStatus + " " + errorThrown );
        })
};

Clipboard.prototype.isClipboardEmpty =  function(callback){
    openaksess.common.debug(this.name + ".isClipboardEmpty()" );
    $.ajax({
        url: properties.contextPath + "/admin/publish/" + this.name + "/isEmpty.action",
        type: "GET"
    })
        .done(function(data){
            openaksess.common.debug("Clipboard.isEmpty(): " + data);
            callback(data)
        }).fail(function(jqXHR, textStatus, errorThrown){
            openaksess.common.debug("Clipboard.isEmpty(): Failed!" + textStatus + " " + errorThrown );
        })
};

var ContentClipboardHandler = new Clipboard("ContentClipboard");

var MultimediaClipboardHandler = new Clipboard("MultimediaClipboard");
