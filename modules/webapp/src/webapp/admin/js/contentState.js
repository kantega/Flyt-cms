var ContentStateHandler = new function() {
    var contextPath = properties.contextPath;
    /**
     * Updates the user's session with the currently viewed content.
     *
     * @param url Url of currently viewed page.
     */
    this.notifyContentUpdate = function(url){
        openaksess.common.debug("ContentStateHandler.notifyContentUpdate(): " + url );
        $.ajax({
            url: contextPath + "/admin/publish/ContentState/notifyContentUpdate.action",
            data: { url: url },
            type: "POST"
        })
        .done(function(){
                openaksess.common.debug("ContentStateHandler.notifyContentUpdate(): Success!");
        }).fail(function(jqXHR, textStatus, errorThrown){
                openaksess.common.debug("ContentStateHandler.notifyContentUpdate(): Failed!" + textStatus + " " + errorThrown );
            })
    }
};