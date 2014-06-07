$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("active");
});

$.getJSON("/dashboard/price.json", function(data) {
	$("#nav-ticker").text("$" + data.price + " USD/BTC");
	$("#nav-loader").hide();
	$("#nav-ticker").css('visibility', 'visible');
});