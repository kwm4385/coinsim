var currentPrice;

function updatePrice() {
	$.getJSON("/dashboard/price.json", function(data) {
		$("#nav-loader").show();
		$("#nav-ticker").css('visibility', 'hidden');
		$("#nav-ticker").text("$" + data.price + " USD/BTC");
		$("#nav-ticker").attr("title", "Last updated: " + data.last_updated);
		$("#nav-loader").hide();
		$("#nav-ticker").css('visibility', 'visible');
		
		currentPrice = data.price;
		$("#display-price").text("$" + data.price + " USD/BTC");
	});
}

$(document).ready(function() {
	$("#amount").on('change keyup paste', function() {
		var a = $("#amount").val();
		var sub = a * currentPrice;
		var feeAmount = sub * (fee / 100);
		$("#feeAmount").val(feeAmount.toFixed(2));
		$("#total").val((sub - feeAmount).toFixed(2));
	});
});

$(document).ready(function() {
	var form = $("#sell-form");
	form.submit(function(event) {
		if(form.data("prevented") == true) {
	        form.data("prevented", false);
	        return true;
	    }
		event.preventDefault();
	    $('#amount-confirm').text($('#amount').val());
		$("#confirm").show();
	    $('#confirm').modal({ backdrop: 'static', keyboard: false })
	        .one('click', '#confirm-button', function (e) {
	        	form.data("prevented", true);
	        	form.submit();
	        });
	});
});