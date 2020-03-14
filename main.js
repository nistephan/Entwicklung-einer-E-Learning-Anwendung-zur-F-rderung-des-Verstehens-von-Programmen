//0:line, 1:type, 2:method, 3:signatur
var invokes = [];

function getMode() {
	return document.getElementById("cb_mode").selectedIndex;
}

function setMode() {
	var mode = getMode();
	//Browse
	if (mode == 0) {
		reset();
		//document.getElementById("next").style.display = "none";
	} else if (mode == 1) {
		//Quiz
		reset();
		//document.getElementById("next").style.display = "";
	}
}

function next() {
	var rnd = Math.floor(Math.random() * invokes.length);
	alert("Zeile: " + invokes[rnd][0]);

	var methodType = prompt("Please enter the Method Type:", "invoke");
	if (methodType == invokes[rnd][1]) {
		var signature = prompt("Please enter the Method Signature:", "()");
		if (signature == invokes[rnd][3]) {
			var classMethod = prompt("Please enter the Class of the Method:", "()");
			if (classMethod == invokes[rnd][2].substring(0, invokes[rnd][2].lastIndexOf("."))) {
				alert("Correct Answer!");
			} else {
				alert("wrong input");
			}
		} else {
			alert("wrong input");
		}
	} else {
		alert("wrong input");
	}
}

function readInvokes() {
	var str = document.getElementById("resultInvokes").innerHTML;
	var lines = str.split("\n");
	for (var i = 0; i < lines.length; i++) {
		var col = lines[i].split(",");
		invokes.push(col);
	}
	convertInvokesReadable();
}

function convertInvokesReadable() {
	for (var i = 0; i < invokes.length; i++) {
		if (invokes[i][0] != null) {
			if (invokes[i][3] != null) {
				//TODO length ist nicht korrekt
				for (var j = 0; j < invokes.length; j++) {
					invokes[i][3] = invokes[i][3].replace("/", ",");
				}
			}
		}
	}
}

function clickedLine(elmnt) {
	var countInvokes = 0;
	var invokeIndices = [];
	if (getMode() == 0) {
		//Browse
		if (document.getElementById(elmnt.id + "panel").innerHTML == "") {
			for (var i = 0; i < invokes.length; i++) {
				if (invokes[i][0] == elmnt.id) {
					invokeIndices[countInvokes] = i;
					countInvokes++;
				}
			}
			for (var i = 0; i < countInvokes; i++) {
				var para = document.createElement("P");
				var h = document.createElement("H3");
				var t = document.createTextNode("Methodenaufruf " + (i + 1) + " von " + (countInvokes));
				h.appendChild(t);
				para.appendChild(h);
				para.innerHTML = para.innerHTML + "Art der Methode: " + invokes[invokeIndices[i]][1] + "<br>" + "Klassenname: " + invokes[invokeIndices[i]][2] + "<br>Methodensignatur: " + invokes[invokeIndices[i]][3];
				document.getElementById(elmnt.id + "panel").appendChild(para);
			}
		}
	} else if (getMode() == 1) {
		//Quiz 1
		if (document.getElementById(elmnt.id + "panel").innerHTML == "") {
			for (var i = 0; i < invokes.length; i++) {
				if (invokes[i][0] == elmnt.id) {
					invokeIndices[countInvokes] = i;
					countInvokes++;
				}
			}
			for (var i = 0; i < countInvokes; i++) {
				var para1 = document.createElement("P");
				
				var h = document.createElement("H3");
				var t = document.createTextNode("Methodenaufruf " + (i + 1) + " von " + (countInvokes));
				h.appendChild(t);
				para1.appendChild(h);
				
				//------------------- Input Form 1 -----------------------
				var divFormGroup1 = document.createElement("div");
				divFormGroup1.setAttribute("class", "form-group");
				
				var labelInput1= document.createElement("label");
				labelInput1.setAttribute("for", i + "input1i" + elmnt.id);
				labelInput1.innerHTML = "Art der Methode";
				
				var input1 = document.createElement("select");
				input1.appendChild(new Option("", ""));
				input1.appendChild(new Option("Konstruktor", "Konstruktor"));
				input1.appendChild(new Option("Instanzmethode", "Instanzmethode"));
				input1.appendChild(new Option("Klassenmethode", "Klassenmethode"));
				input1.setAttribute("class", "form-control");
				input1.setAttribute("id", i + "input1i" + elmnt.id)
				
				var img1 = document.createElement('img');
				img1.setAttribute("id", i + "img1i" + elmnt.id);
				img1.setAttribute("heigth", "26px");
				img1.setAttribute("width", "26px");
				
				divFormGroup1.appendChild(labelInput1);
				divFormGroup1.appendChild(img1);
				divFormGroup1.appendChild(input1);
				para1.appendChild(divFormGroup1);
				
				//------------------- Input Form 2 -----------------------
				var divFormGroup2 = document.createElement("div");
				divFormGroup2.setAttribute("class", "form-group");
				
				var labelInput2= document.createElement("label");
				labelInput2.setAttribute("for", i + "input2i" + elmnt.id);
				labelInput2.innerHTML = "Klassenname";
				
				var input2 = document.createElement("INPUT");
				input2.setAttribute("size", "50");
				input2.setAttribute("type", "text");
				input2.setAttribute("id", i + "input2i" + elmnt.id)
				input2.setAttribute("class", "form-control");

				var img2 = document.createElement('img');
				img2.setAttribute("id", i + "img2i" + elmnt.id);
				img2.setAttribute("heigth", "26px");
				img2.setAttribute("width", "26px");
				
				divFormGroup2.appendChild(labelInput2);
				divFormGroup2.appendChild(img2);
				divFormGroup2.appendChild(input2);
				
				para1.appendChild(divFormGroup2);

				//------------------- Input Form 3 -----------------------
				var divFormGroup3 = document.createElement("div");
				divFormGroup3.setAttribute("class", "form-group");
				
				var labelInput3 = document.createElement("label");
				labelInput3.setAttribute("for", i + "input3i" + elmnt.id);
				labelInput3.innerHTML = "Methodensignatur";
				
				var input3 = document.createElement("INPUT");
				input3.setAttribute("size", "50");
				input3.setAttribute("type", "text");
				input3.setAttribute("id", i + "input3i" + elmnt.id);
				input3.setAttribute("class", "form-control");

				var img3 = document.createElement('img');
				img3.setAttribute("id", i + "img3i" + elmnt.id);
				img3.setAttribute("heigth", "26px");
				img3.setAttribute("width", "26px");
				
				divFormGroup3.appendChild(labelInput3);
				divFormGroup3.appendChild(img3);
				divFormGroup3.appendChild(input3);
				
				para1.appendChild(divFormGroup3);

				var btn = document.createElement("BUTTON");
				btn.setAttribute("class", "btn btn-primary");
				btn.innerHTML = "Check";
				btn.setAttribute("onClick", "check(" + i + ", " + invokeIndices[i] + ", " + elmnt.id + ")");
				para1.appendChild(btn);

				document.getElementById(elmnt.id + "panel").appendChild(para1);
			}
		}
	}
}

function check(i, invokeIndex, elmntId) {
	console.log(invokes[invokeIndex]);
	if (document.getElementById(i + "input1i" + elmntId).value.replace(/ /g,'') == invokes[invokeIndex][1].replace(/ /g,'')) {
		document.getElementById(i + "img1i" + elmntId).src = "correct.png";
	} else {
		document.getElementById(i + "img1i" + elmntId).src = "false.png";
	}
	if (document.getElementById(i + "input2i" + elmntId).value.replace(/ /g,'') == invokes[invokeIndex][2].replace(/ /g,'')) {
		document.getElementById(i + "img2i" + elmntId).src = "correct.png";
	} else {
		document.getElementById(i + "img2i" + elmntId).src = "false.png";
	}
	if (document.getElementById(i + "input3i" + elmntId).value.replace(/\s/g,'') == html2text(invokes[invokeIndex][3]).replace(/\s/g,'')) {
		document.getElementById(i + "img3i" + elmntId).src = "correct.png";
	} else {
		document.getElementById(i + "img3i" + elmntId).src = "false.png";
	}
}

function reset() {
	var acc = document.getElementsByClassName("accordion");
	for (var i = 0; i < acc.length; i++) {
		var panel = acc[i].nextElementSibling;
		if (panel.innerHTML != "") {
			acc[i].classList.toggle("active");
			panel.innerHTML = "";
			if (panel.style.display === "block") {
				panel.style.display = "none";
			}
		}
	}
}

function html2text(html) {
    var tag = document.createElement('div');
    tag.innerHTML = html;
    return tag.innerText;
}

function onLoadPage() {
	readInvokes();
}