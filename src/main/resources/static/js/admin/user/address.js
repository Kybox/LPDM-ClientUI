// Button
let btnUpdate;

// Select
let selectCity;
let cityId;

// User
let userId;

// Modal
let modalResult;
let modalResultSuccess;
let modalResultError;

$(document).ready(function() {

    selectCity = null;
    btnUpdate = $(":button[id^='btnUpdate']");
    btnUpdate.on("click", function () {
        let id = $(this).attr("id");
        id = id.substr(id.lastIndexOf("_") + 1);
        updateAddress(id);
    });

    $(':input[id^="address"]').on("input paste", function () {
        let btn = $(this).next("div").find("button");
        if($(this).val() !== "") btn.attr("class", "btn btn-success");
        else btn.attr("class", "btn btn-danger");
        if(selectCity != null){
            if(selectCity.value === "0") btn.attr("class", "btn btn-danger");
        }

        userId = $(this).attr("id");
        userId = userId.split("address")[1].split("_")[0];

        checkAllInputs(userId);

        selectCity = $("#address" + userId + "_cities");

        if($(this).attr("id") === "address" + userId + "_zipCode"){
            if($(this).val().length === 5) loadCities($(this).val());
            else selectCity.prop("disabled", true);
        }
    });

    modalResult = $("#modal");
    modalResultSuccess = $("#modal_result_body_success");
    modalResultError = $("#modal_result_body_error");
});

function checkAllInputs(id) {

    let checkup = true;
    $('[id^="check_"]').each(function(){
        if($(this).attr("id").match(id)){
            if($(this).attr("class") === "btn btn-danger") {
                checkup = false;
                return false;
            }
        }
    });

    if(checkup) btnUpdate.prop("disabled", false);
    else btnUpdate.prop("disabled", true);
}

function loadCities(zipCode){

    $.ajax({
        url: "/admin/auth/search/address/cities",
        type: "post",
        data: "zipCode=" + zipCode,
        success: function (data) {
            displayCities(data);
        },
        error: function (data) {
        }
    });
}

function displayCities(data) {

    selectCity.find("option").remove();
    selectCity.append("<option value='0' selected disabled>Sélectionner une ville</option>");

    $(data).each(function (index, value) {
        selectCity.append("<option value='" + value.id + "'>" + value.name + "</option>");
    });

    selectCity.prop("disabled", false);

    selectCity.on("change", function () {
        cityId = this.value;
    });
}

function updateAddress(id) {

    let jsonCity = {};
    jsonCity.id = cityId;

    let jsonObj = {};
    jsonObj.streetNumber = $("#address" + userId + "_streetNumber").val();
    jsonObj.streetName = $("#address" + userId + "_streetName").val();
    jsonObj.complement = $("#address" + userId + "_complement").val();
    jsonObj.city = jsonCity;

    $.ajax({
        url: "/admin/auth/add/address?user=" + id,
        type: "post",
        data: JSON.stringify(jsonObj),
        contentType: "application/json",
        dataType : "json",
        success: function (data) {
            showUpdateResult(true);
        },
        error: function (data) {
            showUpdateResult(false);
        }
    });
}

function showUpdateResult(success){

    if(success){
        modalResultSuccess.show();
        modalResultError.hide();
    }
    else{
        modalResultSuccess.hide();
        modalResultError.show();
    }

    modalResult.modal();
}