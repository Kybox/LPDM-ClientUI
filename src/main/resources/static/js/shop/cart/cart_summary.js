let productId;

$(document).ready(function() {

    $("span[id^='quantity_less_']").click(function () {

        productId = $(this).attr("id");
        productId = productId.substr(productId.lastIndexOf("_") + 1);

        let quantity = parseInt($("#quantity_text_" + productId).text());

        if(quantity > 1){
            updateQuantity(productId, "less");
        }
    });

    $("span[id^='quantity_more_']").click(function () {
        productId = $(this).attr("id");
        productId = productId.substr(productId.lastIndexOf("_") + 1);
        updateQuantity(productId, "more");
    });

    $("span[id^='delete_product_']").click(function () {
        productId = $(this).attr("id");
        productId = productId.substr(productId.lastIndexOf("_") + 1);
        deleteProduct(productId);
    });
});

function deleteProduct(id){

    $("#product_" + id).fadeOut();

    $.ajax({
        url: "/shop/cart/delete/",
        type: "delete",
        data: "id=" + id,
        success: function (data) {
            displayTotalAmount(data);
        },
        error: function (data) {
            console.log("Error msg : " + JSON.stringify(data));
        }
    });
}

function updateQuantity(id, mode){

    $.ajax({
        url: "/shop/cart/quantity/" + mode,
        type: "post",
        data: "id=" + id,
        success: function (data) {
            displayProductUpdated(data);
        },
        error: function (data) {
            console.log("Error msg : " + JSON.stringify(data));
        }
    });
}

function displayProductUpdated(data){

    let productList = data.productList;

    $.each(productList, function(i, item){

        if(item.id === parseInt(productId)){

            $("#quantity_text_" + item.id).text(item.quantity);
            $("#total_product_amount_" + item.id).text(item.priceWithTax + " €");

            return false;
        }
    });

    displayTotalAmount(data);
}

function displayTotalAmount(data){

    let totalProducts = data.productList.length;
    if(totalProducts === 0){
        $("#cart_summary").fadeOut();
        $("#alert_cart_empty").fadeIn();
        $("#top_infos").fadeOut();
    }
    else{
        $("#cart-total").text("Montant total du panier : " + data.amountWithTax + " €");
    }

    updateCart(data);
}