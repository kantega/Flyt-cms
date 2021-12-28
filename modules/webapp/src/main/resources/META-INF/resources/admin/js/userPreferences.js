var UserPreferencesHandler = new function (){
    var contextPath = properties.contextPath;
    /**
     * Saves a user preference. Delegates the responsibility of setting this preference to the UserPreferencesManager
     * @param preference
     * @param callback to call with the result.
     */
    this.setPreference = function(preference, callback){
        openaksess.common.debug("UserPreferencesHandler.setPreference(): "+ preference );
        $.ajax({
            url: contextPath + "/admin/publish/UserPreferences.action",
            data: JSON.stringify( preference ),
            contentType : 'application/json',
            type: "PUT"
        })
            .done(function(){
                openaksess.common.debug("UserPreferencesHandler.setPreference(): invoking callback" );
                callback();
            })
            .fail(function(jqXHR, textStatus, errorThrown){
                openaksess.common.debug("UserPreferencesHandler.getPreference(): Failed!" + textStatus + " " + errorThrown );
            })
    };

    /**
     * Returns the user's preference for a given preference key.
     * @param key - Preference identifier.
     * @param callback to call with the result.
     * @return UserPreference.
     */
    this.getPreference = function(key, callback){
        openaksess.common.debug("UserPreferencesHandler.getPreference(): " + key );
        $.ajax({
            url: contextPath + "/admin/publish/UserPreferences.action",
            data: { key: key },
            type: "GET",
            dataType: "json"
        })
            .done(function(data){
                openaksess.common.debug("UserPreferencesHandler.getPreference(): invoking callback" );
                callback(data);
            })
            .fail(function(jqXHR, textStatus, errorThrown){
                openaksess.common.debug("UserPreferencesHandler.getPreference(): Failed!" + textStatus + " " + errorThrown );
            })
    };

    /**
     * Removes a preference.
     * @param key - Preference identifier.
     * @param callback to call with the result.
     */
    this.deletePreference = function(key, callback){
        openaksess.common.debug("UserPreferencesHandler.deletePreference(): " + key );
        $.ajax({
            url: contextPath + "/admin/publish/UserPreferences.action",
            data: JSON.stringify({ key: key  }),
            contentType : 'application/json',
            type: "DELETE"
        })
            .done(function(data){
                openaksess.common.debug("UserPreferencesHandler.deletePreference(): invoking callback" );
                callback(data);
            })
            .fail(function(jqXHR, textStatus, errorThrown){
                openaksess.common.debug("UserPreferencesHandler.deletePreference(): Failed!" + textStatus + " " + errorThrown );
            })
    }
};