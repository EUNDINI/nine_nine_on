$("#list-bar").keyup(function(e) {
	if (e.keyCode == 13) {
		$("#list-recommend-box").css("display", "none");
		loadAjaxFromAPI($("#list-bar").val(), "/book/search.do", "POST", "#book-list-result");
	}
});

$("#list-icon-box").click(function(e) {
	loadAjaxFromAPI($("#search-bar").val(), "/book/search.do", "POST", "#book-list-result");
});

$("#popular-sort").click(function(e) {
	$("#popular-sort").css("color", "#1C1B1F");
	$("#recent-sort").css("color", "#9A9A9A");
	loadAjaxFromAPI("2", "/book/list/sort.do", "POST", "#book-list-result");
});

$("#recent-sort").click(function(e) {
	$("#popular-sort").css("color", "#9A9A9A");
	$("#recent-sort").css("color", "#1C1B1F");
	loadAjaxFromAPI("3", "/book/list/sort.do", "POST", "#book-list-result");
});

/**
 * 검색조건 설정
 */
$('label[class^=condition-label-region]').unbind("click").on("click", function(e) {
	e.preventDefault();

	$(this).css("display", "none");
	$(this).next().css("display", "inline");
	$(this).next().children(".condition-checkbox").attr("checked", true);

	var checkedList = [];
	$("input:checkbox[name=addressCondition]:checked").each(function(i) {
		checkedList.push($(this).val());
	});
	console.log(checkedList);

	loadAjaxAboutArray(checkedList, "/book/list/condition/region.do", "POST", "#book-list-result");
});

$('label[class^=condition-label-genre]').unbind("click").on("click", function(e) {
	e.preventDefault();

	$(this).css("display", "none");
	$(this).next().css("display", "inline");
	$(this).next().children(".condition-checkbox").attr("checked", true);

	var checkedList = [];
	$("input:checkbox[name=genreCondition]:checked").each(function(i) {
		checkedList.push($(this).val());
	});
	console.log(checkedList);

	loadAjaxAboutArray(checkedList, "/book/list/condition/genre.do", "POST", "#book-list-result");
});

$('label[class^=condition-label-region-checked]').unbind("click").on("click", function(e) {
	e.preventDefault();
	console.log($(this));
	var conditionValue = $(this).children(".condition-checkbox").val();


	$(this).css("display", "none");
	$(this).prev().css("display", "inline");
	$(this).children(".condition-checkbox").attr("checked", false);

	var checkedList = [];
	$("input:checkbox[name=addressCondition]:checked").each(function(i) {
		checkedList.push($(this).val());
	});
	console.log(checkedList);

	loadAjaxAboutArray(checkedList, "/book/list/condition/region.do", "POST", "#book-list-result");
});

$('label[class^=condition-label-genre-checked]').unbind("click").on("click", function(e) {
	e.preventDefault();
	console.log($(this));
	var conditionValue = $(this).children(".condition-checkbox").val();

	$(this).css("display", "none");
	$(this).prev().css("display", "inline");
	$(this).children(".condition-checkbox").attr("checked", false);

	var checkedList = [];
	$("input:checkbox[name=genreCondition]:checked").each(function(i) {
		checkedList.push($(this).val());
	});
	console.log(checkedList);

	loadAjaxAboutArray(checkedList, "/book/list/condition/genre.do", "POST", "#book-list-result");
});

/*
$(".condition-label.region").click(function() {
	console.log($(this));
	var conditionValue = $(this).children(".condition-checkbox").val();
	
	$(this).css("display", "none");
	$(this).next().css("display", "inline");
	
	loadAjaxFromAPI(conditionValue, "/book/list/condition/region.do", "POST", "#book-list-result");
}); 

$(".condition-label.genre").click(function() {
	console.log($(this));
	var conditionValue = $(this).children(".condition-checkbox").val();
	
	$(this).css("display", "none");
	$(this).next().css("display", "inline");
	
	loadAjaxFromAPI(conditionValue, "/book/list/condition/genre.do", "POST", "#book-list-result");
}); 
*/
