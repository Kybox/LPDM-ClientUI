let btnValidate;
let deliveryAmount;
let deliveryId;

$(document).ready(function(){

    deliveryAmount = $("#delivery_amount");

    btnValidate = $("#btn_validate");
    btnValidate.click(function () {
        validateDeliveryMethod();
    });

    $(":input[type='radio']").click(function () {

        deliveryId = $(this).attr("value");
        btnValidate.prop("disabled", false);
        updateDeliveryMethod(deliveryId);
    });
});

function updateDeliveryMethod(id) {

    console.log("current id = " + id);

    deliveryAmount.text($("#delivery_amount" + id).text());

    let rightSubAmount = $("#sub_amount").text();
    rightSubAmount = rightSubAmount.substr(0, rightSubAmount.lastIndexOf("€") - 1);
    rightSubAmount = parseFloat(rightSubAmount);

    let rightDeliveryAmount = deliveryAmount.text();
    rightDeliveryAmount = rightDeliveryAmount.substr(0, rightDeliveryAmount.lastIndexOf("€") - 1);
    rightDeliveryAmount = parseFloat(rightDeliveryAmount);

    let rightCouponAmount = $("#coupon_amount").text();
    rightCouponAmount = rightCouponAmount.substr(0, rightCouponAmount.lastIndexOf("€") - 1);
    rightCouponAmount = parseFloat(rightCouponAmount);

    let rightTaxAmount = $("#tax_amount").text();
    rightTaxAmount = rightTaxAmount.substr(0, rightTaxAmount.lastIndexOf("€") - 1);
    rightTaxAmount = parseFloat(rightTaxAmount);

    let totalAmout = rightSubAmount + rightDeliveryAmount + rightCouponAmount + rightTaxAmount;
    totalAmout = totalAmout.toFixed(2);

    $("#total_amount").text(totalAmout + " €");

    $('#total_table_line').effect("highlight", {color: "#b8ffac"}, 500).fadeIn();
}

function validateDeliveryMethod() {

    let jsonObj = {};
    jsonObj.id = deliveryId;

    $.ajax({
        url: "/shop/order/process/2",
        type: "post",
        data: JSON.stringify(jsonObj),
        contentType: "application/json",
        dataType : "json",
        success: function (data) {
            console.log("Success msg : " + JSON.stringify(data));
            window.location.href = "/shop/order/process/3";
        },
        error: function (data) {
            console.log("Error msg : " + JSON.stringify(data));
        }
    });
}