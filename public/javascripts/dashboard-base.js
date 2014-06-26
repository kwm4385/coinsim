$(document).ready(function() {
	$("#menu-toggle").click(function(e) {
	    e.preventDefault();
	    $("#wrapper").toggleClass("active");
	    $("#sidebar-expand").toggleClass("glyphicon-chevron-right glyphicon-chevron-left");
	    console.log("click");
	});
});

function updatePrice() {
	$.getJSON("/dashboard/price.json", function(data) {
		$("#nav-loader").show();
		$("#nav-ticker").css('visibility', 'hidden');
		$("#nav-ticker").text("$" + data.price + " USD/BTC");
		$("#nav-ticker").attr("title", "Last updated: " + data.last_updated);
		$("#nav-loader").hide();
		$("#nav-ticker").css('visibility', 'visible');
		
		var bank = accounting.unformat($("#bank").text());
		var coins = $("#coins").text();
		var net = bank + (coins * data.price);
		$(".networth").each(function() {
			$(this).text(accounting.formatMoney(net));
		});
		if(net >= startingFunds) {
			$("#networth-container").addClass("money-container-green");
			$("#networth-container").removeClass("money-container-red");
		} else {
			$("#networth-container").removeClass("money-container-green");
			$("#networth-container").addClass("money-container-red");
		}
	});
}

$(document).ready(function() {
	updatePrice();
	setInterval("updatePrice()", 60000);
});

$(document).ready(function() {
  $(".money").each(function() {
	  $(this).text(accounting.formatMoney($(this).text()));
  });
});