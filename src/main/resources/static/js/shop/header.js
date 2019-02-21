$(document).ready(function(){

    $(".dropdown").hover(
        function() {
            $('.dropdown-menu', this).not('.in .dropdown-menu').stop(true,true).slideDown("400");
            $(this).toggleClass('open');
        },
        function() {
            $('.dropdown-menu', this).not('.in .dropdown-menu').stop(true,true).slideUp("400");
            $(this).toggleClass('open');
        }
    );

    $(":button[id^='remove_']").on("click", function () {
        //$(this).closest("li").remove();
        deleteProductFromCart(extractProductId($(this).attr("id")));
    });
});

function updateCart(order){

    $('#cart_menu').effect("highlight", {color: "#b8ffac"}, 500).fadeIn();

    let cart_label = $("#cart_label");
    let label_content;
    let cart_list = $("#cart_list");
    let validateCart = $("#cart_list li:last-child");

    cart_list.empty();

    let totalProduct = 0;

    $.each(order.productList, function (i, product) {

        let list = $("<li/>");
        let spanItem = $("<span/>").attr("class", "item");
        let spanItemLeft = $("<span/>").attr("class", "item-left");
        let productImg = $("<img/>")
            .prop("src", product.picture)
            .prop("alt", "productImg")
            .attr("style", "width:50px; height:50px;");


        let spanItemInfo = $("<span/>").attr("class", "item-info");
        let spanProductName = $("<span/>").html(product.name);
        let spanProductQuantity = $("<span/>").html("x " + product.quantity);

        let spanItemRight = $("<span/>").attr("class", "item-right");
        //let btn = "<button class='btn btn-xs btn-danger pull-right'>x</button>";
        let btn = "<button type='button' class='btn btn-danger pull-right' id='remove_"+ product.id + "' style='width: 30px; height: 30px; padding: 0;'><span class='glyphicon glyphicon-remove'></span></button>";

        spanItemRight.append(btn);

        spanItemInfo.append(spanProductName);
        spanItemInfo.append(spanProductQuantity);

        spanItemLeft.append(productImg);
        spanItemLeft.append(spanItemInfo);

        spanItem.append(spanItemLeft);
        spanItem.append(spanItemRight);

        list.append(spanItem);

        cart_list.append(list);

        totalProduct = totalProduct + product.quantity;
    });

    cart_list.append("<li class='divider'></li>");
    cart_list.append(validateCart);

    label_content = "<span class='glyphicon glyphicon-shopping-cart'></span>";
    label_content += " " + totalProduct + " - Articles ";
    label_content += "<span class='caret'></span>";

    cart_label.html(label_content);

    $(":button[id^='remove_']").on("click", function () {
        //$(this).closest("li").remove();
        deleteProductFromCart(extractProductId($(this).attr("id")));
    });
}

function extractProductId(button){

    return button.substr(button.lastIndexOf("_") + 1);
}

function deleteProductFromCart(id){

    console.log("id = " + id);

    $.ajax({
        url: "/shop/cart/remove",
        type: "post",
        data: "product=" + id,
        success: function (data) {
            updateCart(data);
            console.log("Success msg : " + data);
        },
        error: function (data) {
            console.log("Error msg : " + data);
        }
    });
}