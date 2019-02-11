let btnValidate;

$(document).ready(function(){

    btnValidate = $("#btn_validate");
    btnValidate.click(function () {

    });

    $(":input[id^='payment_']").change(function () {

        btnValidate.prop("disabled" , false);
    });
});