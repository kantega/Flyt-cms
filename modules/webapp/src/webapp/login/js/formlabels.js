$(document).ready(function(){
    var $inputFields = $("input[type='text'], input[type='password']");
    $inputFields.each(function(index){
        var $element = $(this); 
        if ( $element.val() ) {
            $element.prev().hide();    
        }
    })
    $("input[type='text'], input[type='password']").focusin(function(){
        var $activeElement = $(this);
        $activeElement.prev().hide();
    })
    $("input[type='text'], input[type='password']").focusout(function(){
        var $activeElement = $(this);
        if (!$activeElement.val()){
            $activeElement.prev().show();
        }
    })
})