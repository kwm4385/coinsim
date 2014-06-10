$( document ).ready(function() {
  $(".money").each(function() {
	  $(this).text(accounting.formatMoney($(this).text()));
  });
});