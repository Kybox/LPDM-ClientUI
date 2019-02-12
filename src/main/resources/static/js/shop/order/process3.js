let btnValidate;

$(document).ready(function(){

    btnValidate = $("#btn_validate");
    btnValidate.click(function () {
        document.location.href = "/shop/order/process/4";
    });

    $(":input[id^='payment_']").change(function () {

        btnValidate.prop("disabled" , false);
    });
});