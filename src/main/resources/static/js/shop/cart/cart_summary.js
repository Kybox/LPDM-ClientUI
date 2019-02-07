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

    let orderedProduct = data.orderedProducts;

    $.each(orderedProduct, function(i, item){

        console.log("item id = " + item.product.id + "/ product id = " + productId);

        if(item.product.id === parseInt(productId)){

            console.log("Item found : " + JSON.stringify(item));
            let id = item.product.id;
            let quantity = item.quantity;

            $("#quantity_text_" + id).text(quantity);

            let productTotal = item.totalAmount;
            $("#total_product_amount_" + id).text(productTotal + " €");

            return false;
        }
    });

    displayTotalAmount(data);
}

function displayTotalAmount(data){

    let totalAmount = data.total;

    $("#cart-total").text("Montant total du panier : " + totalAmount + " €");

    updateCart(data);
}