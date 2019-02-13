let btnValidate;
let paymentId;

$(document).ready(function(){

    btnValidate = $("#btn_validate");
    btnValidate.click(function () {
        addPayment();
    });

    $(":input[id^='payment_']").change(function () {

        paymentId = $(this).attr("id");
        paymentId = paymentId.substr(paymentId.lastIndexOf("_") + 1);
        btnValidate.prop("disabled" , false);
    });
});

function addPayment() {

    let jsonObj = {};
    jsonObj.id = paymentId;

    $.ajax({
        url: "/shop/order/process/3",
        type: "post",
        data: JSON.stringify(jsonObj),
        contentType: "application/json",
        dataType : "json",
        success: function (data) {
            console.log("Success msg : " + JSON.stringify(data));
            window.location.href = "/shop/order/process/4";
        },
        error: function (data) {
            console.log("Error msg : " + JSON.stringify(data));
        }
    });
}