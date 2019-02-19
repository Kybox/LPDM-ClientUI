// Button
let btnUpdate;

// User id
let userId;
let currentUserId;
let currentUser;

// Modal
let modalResult;
let modalResultSuccess;
let modalResultError;

// Result
let result;

$(document).ready(function() {


    // Button
    btnUpdate = $("[id^='btnUpdate_']");
    btnUpdate.on("click", function () {
        updateUser();
    });

    checkInputsOnLoad();

    let btnDropdown = $("#btnMenuUsers");
    $(".dropdown-menu li a").click(function(){

        if(currentUserId != null) currentUser.fadeOut(500);

        btnDropdown.text($(this).text());
        btnDropdown.val($(this).text());
        btnDropdown.append("<span class='caret' style='margin-left: 10px;'></span>");

        let currentId = $.trim($(this).attr("id"));
        currentUserId = currentId.substr(currentId.indexOf("_") + 1, currentId.length);
        currentUser = $("#user_" + currentUserId);
        currentUser.delay(500).fadeIn();
        userId = currentId;

        checkInputsOnLoad(userId);
    });

    // Checbox control
    $(":input[type='checkbox']").on("change", function () {
        if(!checkAllCheckBoxes()) $(this).prop("checked", true);
    });

    // Inputs control
    $('[id^="user_"]').on("change paste input keyup focus", function () {
        let btn = $(this).next("div").find("button");
        if($(this).val() !== "") btn.attr("class", "btn btn-success");
        else btn.attr("class", "btn btn-danger");
        userId = $(this).attr("id");
        userId = userId.substr(userId.lastIndexOf("_") + 1);

        checkAllInputs(userId);
    });

    // Datepicker
    let datePickerInput = $(":input[data-provide='datepicker']");
    datePickerInput.datepicker({
        language: "fr",
        autoclose: true
    });

    modalResult = $("#modal");
    modalResultSuccess = $("#modal_result_body_success");
    modalResultError = $("#modal_result_body_error");

    // On modal result close
    modalResult.on('hidden.bs.modal', function () {
        if(result){
            $(":button").prop("disabled", true);
            window.location.href = "/admin/auth/update";
        }
    });
});

function checkInputsOnLoad(id){

    if(id == null){
        $(':input[id^="user_"]').each(function () {
            if(!$(this).attr("id").match("^user_registrationDate")){
                let btn = $(this).next("div").find("button");
                if($(this).val() !== "") btn.attr("class", "btn btn-success");
                else btn.attr("class", "btn btn-danger");

            }
        });
    }
    else{
        $(':input[id^="user_"]').each(function () {
            if(!$(this).attr("id").match("^user_registrationDate")){
                if($(this).attr("id").match(id)){
                    let btn = $(this).next("div").find("button");
                    if($(this).val() !== "") btn.attr("class", "btn btn-success");
                    else btn.attr("class", "btn btn-danger");
                }
            }
        });
    }

    checkAllInputs(id);
}

function checkAllInputs(id) {

    let checkup = true;

    if(id == null){
        $('[id^="check_"]').each(function(){
            if($(this).attr("class") === "btn btn-danger") {
                checkup = false;
                return false;
            }
        });
        if(checkup) btnUpdate.prop("disabled", false);
        else btnUpdate.prop("disabled", true);
    }
    else{
        $('[id^="check_"]').each(function(){
            if($(this).attr("id").match(id)){
                if($(this).attr("class") === "btn btn-danger") {
                    checkup = false;
                    return false;
                }
            }
        });
        if(checkup) $("#btnUpdate_" + id).prop("disabled", false);
        else $("#btnUpdate_" + id).prop("disabled", true);
    }
}

function checkAllCheckBoxes(){

    let checkup = false;
    $(":input[type='checkbox']").each(function () {
        if($(this).prop("checked") === true) {
            checkup = true;
            return true;
        }
    });

    return checkup;
}

function updateUser(){

    let roles = [];
    $(":input[type='checkbox']").each(function () {
        if($(this).prop("checked") === true){
            let userRole = {};
            userRole.id = $(this).val();
            roles.push(userRole);
        }
    });

    let active = $("#user_status_" + userId).val() === "1";

    let jsonObj = {};
    jsonObj.id = userId;
    jsonObj.email = $("#user_email_" + userId).val();
    jsonObj.appRole = roles;
    jsonObj.name = $("#user_name_" + userId).val();
    jsonObj.firstName = $("#user_firstname_" + userId).val();
    jsonObj.tel = $("#user_tel_" + userId).val();
    jsonObj.birthday = moment($("#user_birthday_" + userId).val(), "DD/MM/YYYY")
        .format("YYYY-MM-DD");
    jsonObj.registrationDate = moment.utc(
        $("#user_registrationDate_" + userId).val(), "DD/MM/YYYY h:mm:ss").format();

    // Registration date not define
    if(jsonObj.registrationDate === "Invalid date")
        jsonObj.registrationDate = null;

    jsonObj.addressId = $("#user_address_" + userId).val();
    jsonObj.active = active;

    $.ajax({
        url: "/admin/auth/update/user",
        type: "post",
        data: JSON.stringify(jsonObj),
        contentType: "application/json",
        dataType : "json",
        success: function (data) {
            result = true;
            showUpdateResult(result);
        },
        error: function (data) {
            result = false;
            showUpdateResult(result);
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