// Modals
let modalUpload;
let modalFormDiv;
let modalResult;
let modalResultSuccess;
let modalResultError;

// Form
let inputId;
let searchForm;
let searchInput;
let searchSelect;

// Picture
let currentPictureInput;
let currentPicture;

// Product
let currentProduct;
let currentProductId;

$( document ).ready(function() {

    // Update buttons
    let btnUpdate = $(".btn-success");
    let btnDeactivate = $(".btn-danger");
    let btnActivate = $(".btn-info");

    // Picture URL input
    let inputPic = $(":input[type=text][readonly='readonly']");

    // Form
    searchForm = $("#mainSearchForm");
    searchInput = $("#mainSearchInput");
    searchSelect = $("#select");

    // Modals
    modalUpload = $("#modal_upload");
    modalFormDiv = $("#uploadForm");
    modalResult = $("#modal_result");
    modalResultSuccess = $("#modal_result_body_success");
    modalResultError = $("#modal_result_body_error");

    // Dropdown
    let btnDropdown = $("#btnMenuProducts");

    // FocusIN picture URL input
    inputPic.focusin(function (e) {
        e.preventDefault();
        $(this).focusout();
        currentPictureInput = $(this);
        inputId = $(this).attr("id");
        inputId = inputId.substr(inputId.indexOf("_") + 1, inputId.length);
        getUploadForm(inputId);
    });

    // On modal upload picture close
    modalUpload.on('hidden.bs.modal', function () {
        modalFormDiv.empty();
        getLatestPicture();
        $("#mainSearchBtn").prop("disabled", false);
    });

    // On modal result close
    modalResult.on('hidden.bs.modal', function () {
        window.location.href = "/admin/products/search/" + currentProductId;
    });

    btnUpdate.on("click", function () {
        let btnId = $(this).attr("id");
        currentProductId = btnId.substr(btnId.indexOf("_") + 1, btnId.length);
        updateProduct(currentProductId, false);
    });

    btnDeactivate.on("click", function () {
        let btnId = $(this).attr("id");
        currentProductId = btnId.substr(btnId.indexOf("_") + 1, btnId.length);
        updateProduct(currentProductId, true);
    });

    btnActivate.on("click", function () {
        let btnId = $(this).attr("id");
        currentProductId = btnId.substr(btnId.indexOf("_") + 1, btnId.length);
        updateProduct(currentProductId, false);
    });

    $(".dropdown-menu li a").click(function(){

        if(currentProductId != null) currentProduct.fadeOut(500);

        btnDropdown.text($(this).text());
        btnDropdown.val($(this).text());
        btnDropdown.append("<span class='caret' style='margin-left: 10px;'></span>");

        let currentId = $.trim($(this).attr("id"));
        currentProductId = currentId.substr(currentId.indexOf("_") + 1, currentId.length);
        currentProduct = $("#product_" + currentProductId);
        currentProduct.delay(500).fadeIn();
    });
});

function getUploadForm(id) {

    $.ajax({
        url: "/admin/products/upload",
        type: "post",
        data: "id=" + id + "&restricted=true",
        success: function (data) {
            insertUploadForm(data);
        },
        error: function (error) {
            modalUpload.focus();
        },
        complete: function () {
            modalUpload.modal();
            modalUpload.focus();
        }
    });
}

function insertUploadForm(uploadForm) {

    let content = $.parseHTML(uploadForm, document, true);
    modalFormDiv.append(content[17]);
}

function getLatestPicture(){

    $.ajax({
        url: "/admin/products/picture/owner/" + inputId,
        type: "get",
        success: function (data) {
            displayUploadedPic(data);
        },
        error: function (error) {
            alert("ERROR : " + error);
        },
    });
}

function displayUploadedPic(data){
    currentPictureInput.val(data.url);
    currentPicture = currentPictureInput.parents('ul').next().find("img");
    currentPicture.attr("src", data.url);
}

function updateProduct(id, deactivate){

    let name = $("#name_" + id).val();
    let label = $("#label_" + id).val();
    let price = $("#price_" + id).val();
    let tva = $("#tva_" + id).val();
    let picture;
    if(currentPictureInput == null) picture = null;
    else picture = currentPictureInput.val();
    let categorySelect = $("#categoryList_" + id + " option:selected");
    let categoryValue = categorySelect.val();
    let categoryText = categorySelect.text();

    let jsonCategory = {};
    jsonCategory.id = categoryValue;
    jsonCategory.name = categoryText;

    let jsonObj = {};
    jsonObj.id = id;
    jsonObj.name = name;
    jsonObj.label = label;
    jsonObj.tax = tva;
    jsonObj.price = price;
    jsonObj.picture = picture;
    jsonObj.category = jsonCategory;
    jsonObj.deactivate = deactivate;

    console.log(JSON.stringify(jsonObj));

    $.ajax({
        url: "/admin/products/update",
        type: "post",
        data: JSON.stringify(jsonObj),
        dataType: "json",
        contentType: "application/json",
        success: function () {
            showUpdateResult(true);
        },
        error: function () {
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

