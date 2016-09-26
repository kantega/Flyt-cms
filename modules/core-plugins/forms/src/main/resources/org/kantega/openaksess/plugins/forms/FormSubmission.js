;function delete_submission(row_id, contextPath, confirmMessage){
    if (window.confirm(confirmMessage)){
        $.ajax({
            type: 'POST',
            url: contextPath + "/admin/publish/DeleteSubmission.action",
            data : { 'id' : row_id }
        }).done(function() {
            $("#" + row_id).remove();
        });
    }
}