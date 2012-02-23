function jumpPage(pageNo) {
	$("#pageNo").val(pageNo);
	$("#mainForm").submit();
}

function sort(orderBy, defaultOrder) {
	if ($("#orderBy").val() == orderBy) {
		if ($("#orderDir").val() == "") {
			$("#orderDir").val(defaultOrder);
		}
		else if ($("#orderDir").val() == "desc") {
			$("#orderDir").val("asc");
		}
		else if ($("#orderDir").val() == "asc") {
			$("#orderDir").val("desc");
		}
	}
	else {
		$("#orderBy").val(orderBy);
		$("#orderDir").val(defaultOrder);
	}

	$("#mainForm").submit();
}

function search() {
	$("#orderDir").val("");
	$("#orderBy").val("");
	$("#pageNo").val("1");

	$("#mainForm").submit();
}