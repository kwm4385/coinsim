$(document).ready(function() {
  $(".money").each(function() {
	  $(this).text(accounting.formatMoney($(this).text()));
  });
});

function selectRow(id) {
	$("tr#"+id).addClass("success");
	$("tr").not("#"+id).removeClass("success");
}