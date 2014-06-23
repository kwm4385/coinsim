$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("active");
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
	});
}

$(document).ready(function() {
	updatePrice();
	setInterval("updatePrice()", 30000);
});

function toggleArrow() {
	$("#sidebar-expand").toggleClass("glyphicon-chevron-right glyphicon-chevron-left");
}

$(document).ready(function() {
  $(".money").each(function() {
	  $(this).text(accounting.formatMoney($(this).text()));
  });
});