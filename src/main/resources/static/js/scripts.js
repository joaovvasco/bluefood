function isNumberKey(evt){
	var key = evt.key;
	if(isOnlyNumbers(key) || isAllowedKeys(key) )
		return true;
	
	return false;
}

function isOnlyNumbers(key){
	return Number.isInteger(parseInt(key,10));
}

function isAllowedKeys(key){
	return key === 'Delete'
		|| key === 'Backspace'
		|| key === 'ArrowLeft'
		|| key === 'ArrowRight';
}

function searchRest(categoriaId){
	
	var t = document.getElementById("searchType");
	
	if(categoriaId == null)
		t.value = "TEXTO";
	else{
		t.value = "CATEGORIA";
		document.getElementById("categoriaId").value = categoriaId;
	}
	
	document.getElementById("form").submit();
}

function setCmd(cmd){
	document.getElementById("cmd").value = cmd;
	document.getElementById("form").submit();
}

function filterCardapio(categoria){
	document.getElementById("categoria").value = categoria;
	document.getElementById("filterForm").submit();
}