// Button
let btnValidate;

// Select
let selectCity;
let cityId;

$(document).ready(function() {

    selectCity = $("#address_cities");
    cityId = selectCity.value;
        btnValidate = $(":button[id^='btn_validate']");
    btnValidate.on("click", function () {
        if($(this).attr("id") === "btn_validate_update"){ updateAddress("put") }
        else{ updateAddress("post") }
    });

    $(':input[id^="address"]').on("input paste", function () {

        if($(this).attr("id") !== "address_complement"){

            let btn = $(this).next("div").find("button");
            if($(this).val() !== "") btn.attr("class", "btn btn-success");
            else btn.attr("class", "btn btn-danger");
            if(selectCity != null){
                if(selectCity.value === "0") btn.attr("class", "btn btn-danger");
            }
        }

        checkAllInputs();

        if($(this).attr("id") === "address_zipCode"){
            if($(this).val().length === 5) loadCities($(this).val());
            else selectCity.prop("disabled", true);
        }
    });
});

function checkAllInputs() {

    let checkup = true;
    $('[id^="check_"]').each(function(){
        if($(this).attr("class") === "btn btn-danger") {
            checkup = false;
            return false;
        }
    });

    if(checkup) btnValidate.prop("disabled", false);
    else btnValidate.prop("disabled", true);
}

function loadCities(zipCode){

    console.log("load cities for " + zipCode);

    $.ajax({
        url: "/shop/address/cities",
        type: "post",
        data: "zipCode=" + zipCode,
        success: function (data) {
            console.log("Success msg : " + data);
            displayCities(data);
        },
        error: function (data) {
            console.log("Error msg : " + data);
        }
    });
}

function displayCities(data) {

    console.log("display cities");

    selectCity.find("option").remove();
    selectCity.append("<option value='0' selected disabled>Sélectionner une ville</option>");

    $(data).each(function (index, value) {
        selectCity.append("<option value='" + value.id + "'>" + value.name + "</option>");
    });

    selectCity.prop("disabled", false);

    selectCity.on("change", function () {
        cityId = this.value;
        console.log("selected value = " + cityId);
    });
}

function updateAddress(mode) {

    let jsonCity = {};
    jsonCity.id = cityId;

    let jsonObj = {};
    jsonObj.streetNumber = $("#address_streetNumber").val();
    jsonObj.streetName = $("#address_streetName").val();
    jsonObj.complement = $("#address_complement").val();
    jsonObj.city = jsonCity;

    console.log("Json : " + JSON.stringify(jsonObj));

    $.ajax({
        url: "/shop/order/process/1",
        type: mode,
        data: JSON.stringify(jsonObj),
        contentType: "application/json",
        dataType : "json",
        success: function (data) {
            console.log("Success msg : " + JSON.stringify(data));
            window.location.href = "/shop/order/process/2";
        },
        error: function (data) {
            console.log("Error msg : " + JSON.stringify(data));
        }
    });
}