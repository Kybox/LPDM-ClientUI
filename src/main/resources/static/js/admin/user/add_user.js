// Select
let status;
let role;

// Button
let btnAdd;

// Modal
let modalResult;
let modalResultSuccess;
let modalResultError;

// Result
let result;

$(document).ready(function() {

    // Select
    status = $("#user_status");
    role = $("#user_role");
    $("select").on("change", function () {
        let optionSelected = $(this).find("option:selected");
        let valueSelected  = optionSelected.val();
        let check_btn = $(this).closest("div").find("button");
        if(valueSelected > 0) check_btn.attr("class", "btn btn-success");
        else check_btn.attr("class", "btn btn-danger");
        checkAllInputs();
    });

    // Button
    btnAdd = $("#btn_add");
    btnAdd.on("click", function (e) {
        e.preventDefault();
        addNewUser();
    });

    $(":input").on("input change past keyup", function () {

        let btn = $(this).next("div").find("button");
        if($(this).val() !== "") btn.attr("class", "btn btn-success");
        else btn.attr("class", "btn btn-danger");
        checkAllInputs();
    });

    // Date picker
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
            window.location.href = "/admin/auth/add";
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

    if(checkup) btnAdd.prop("disabled", false);
    else btnAdd.prop("disabled", true);
}

function addNewUser() {

    let userRole = {};
    userRole.id = role.val();

    let roles = [];
    roles.push(userRole);

    let jsonObj = {};
    jsonObj.name = $("#user_lastName").val();
    jsonObj.firstName = $("#user_firstName").val();
    jsonObj.email = $("#user_email").val();
    jsonObj.tel = $("#user_tel").val();
    jsonObj.birthday = moment($("#user_birthday").val(), "DD/MM/YYYY").format("YYYY-MM-DD");
    jsonObj.password = $("#user_password").val();
    jsonObj.active = status.val() === 1;
    jsonObj.appRole = roles;

    $.ajax({
        url: "/admin/auth/add",
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