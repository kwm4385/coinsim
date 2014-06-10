function selectRow(id) {
	$("tr#"+id).addClass("success");
	$("tr").not("#"+id).removeClass("success");
}

$(document).ready(function () {
    $("#newSim").hide();
    $('#showForm').click(function () {
      $('#newSim').slideToggle("slow");
    });
    $('#cancelNew').click(function () {
        $('#newSim').slideToggle("slow");
      });
});

$(document).ready(function () {
	$(':radio').each(function() {	
		$(this).on('change', function() {
		   $('#active-sim').submit();
		});
	});
});