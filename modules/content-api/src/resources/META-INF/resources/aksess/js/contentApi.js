window.ContentApi = (function(path){
    var contextPath = path;

    var ContentApi = {
        getCurrent: function(url, callback){
            $.ajax({
                dataType: "json",
                url: contextPath + "/content-api/current",
                data: url,
                success: function(data){
                    if(typeof data === 'undefined'){
                        callback('Could not fetch data');
                    }else{
                        ContentApi.contentObject = data;
                        callback(null, data);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown){
                    callback(errorThrown);
                }
            });
        },

        getAttributes: function(callback){
            if(typeof ContentApi.contentObject === 'undefined'){
                ContentApi.getCurrent({url:window.location.href}, function(err, data){
                    if(err){
                        callback(err);
                    }else{
                        var attributes = data.contentAttributes;
                        callback(null, attributes);
                    }
                })
            }else{
                callback(null, ContentApi.data.contentAttributes);
            }
        },

        queryContent: function(queryObject, callback){
            $.ajax({
                dataType: "json",
                url: contextPath + "/content/query",
                data: queryObject,
                success: function(data){
                    callback(null, data);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    callback(errorThrown);
                }
            });
        }
    };

    return ContentApi;
});