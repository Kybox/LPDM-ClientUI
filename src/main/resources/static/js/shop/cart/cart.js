$(document).ready(function() {

    $("#btn_add_product").click(function () {
        //$(this).prop("disabled", true);
        addProduct();
    });
});

function addProduct() {

    let category = {};
    category.id = parseInt($("#category_id").val());

    let product = {};
    product.id = parseInt($("#product_id").val());
    product.productId = parseInt($("#product_id").val());
    product.category = category;
    product.quantity = parseInt($("#product_quantity").val());

    console.log(JSON.stringify(product));

    $.ajax({
        url: "/shop/cart/add",
        type: "post",
        data: JSON.stringify(product),
        contentType: "application/json",
        dataType : "json",
        success: function (data) {
            console.log("Success msg : " + data);
            updateCart(data);
        },
        error: function (data) {
            console.log("Error msg : " + JSON.stringify(data));
        }
    });
}



function cartAnim(){

    let cart = $('#cart_menu');
    let imgtodrag = $("#product_img");
    if (imgtodrag) {
        var imgclone = imgtodrag.clone()
            .offset({
                top: imgtodrag.offset().top,
                left: imgtodrag.offset().left
            })
            .css({
                'opacity': '0.5',
                'position': 'absolute',
                'height': '75px',
                'width': '75px',
                'z-index': '100'
            })
            .appendTo($('body'))
            .animate({
                'top': cart.offset().top,
                'left': cart.offset().left,
                'width': 75,
                'height': 75
            }, 200);

        setTimeout(function () {
            cart.effect("pulsate", {
                times: 2
            }, 200);
        }, 1000);

        imgclone.animate({
            'width': 0,
            'height': 0
        }, function () {
            $(this).detach()
        });
    }
}