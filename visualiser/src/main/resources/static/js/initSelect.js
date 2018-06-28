function myInitSelect(filename) {
	$.getJSON(filename, function(json) {
		var medikamenteArr = [];
		medikamenteArr = json.results;

		// Initialize select2		
		$("#selMedikament").select2({
			width: 'resolve',
			data: medikamenteArr,
			minimumInputLength: 3,
//			allowClear: true,
			placeholder: "Medikament..."
		});

	});
}

$("#selMedikament").bind("select2:select", function (e) { 
	$("#selMedikamentName").attr("value", e.params.data.text); 
});


