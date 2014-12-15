var mobileLayout = false;

$(document).ready(function(){
    mobileLayout = $("#TopMenu").is(":hidden");
    if(mobileLayout) {

    } else {

        var $inputFields = $("input[type='text'], input[type='password']")

        // Check on page load if any fields has values and remove label if they do
        checkAllInputsForValues($inputFields);

        // Whenever a field changes all other fields must be searched for a change in value to fix browserbased autofill.
        $inputFields.change(function () {
            checkAllInputsForValues($inputFields);
        })

        $inputFields.focusin(function () {
            hideLabel($(this));
        })
        $inputFields.focusout(function () {
            checkAllInputsForValues($inputFields)
            var $activeElement = $(this);
            if (!$activeElement.val()) {
                $activeElement.siblings("label").show();
            }
        })
    }
})
function checkAllInputsForValues(inputElements){
    inputElements.each(function(){
        var inputElement = $(this);
        if(inputElement.val()){
            hideLabel(inputElement);
        }
    })
}
function hideLabel(inputElement) {
    inputElement.siblings("label").hide();
}

