$(document).ready(function(){

    $("#status_select").change(function () {
        console.log(this.value);
        window.location.href = "/shop/account/orders?status=" + this.value;
    });
});