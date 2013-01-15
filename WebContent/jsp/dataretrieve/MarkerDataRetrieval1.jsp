<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<html:html>
<head>

<title>GDMS</title>
<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
<style>
body
 {
 background-image:url('jsp/Images/1.jpg');

 }
 </style>
</head>
<body>
	<html:form action="/retrieveData.do" method="POST">
		<div class="heading" align="center">Marker Data Retrieval</div>
		<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
		--><br>
		<br>
		<center>				
				
				<Table border=0 width="100%" height="100%" cellpadding=0 cellspacing=0>
					<tr><td align="center">	
					<table width=50% border=0 bgcolor="#7B6F6B" cellpadding="10" cellspacing="10">
						<tr>
						<td valign="top" width=49% nowrap="nowrap">
							<font color="white" face="Arial" size="2"> <b>Select fields to be displayed:</b></font>
							<br>
							<select name="choiceBox"  multiple="true" size="7" style="COLOR:black; FONT-SIZE: 11px;width:96%; " >
									<option value="All">All</option>
									<option value="0">Principal Investigator</option>
									<option value="1">Contact</option>
									<option value="2">Institute</option>
									<option value="3">Incharge Person</option>
									<option value="4">Publication</option>
									<option value="5">Crop</option>
									<option value="6">Accession Id</option>
									<option value="7">Genotype</option>
							</select>
						</td>	
						<td align="center"><input type="button" value=">>>" onclick="moveOver();"><br><br><input type="button" value="<<<" onclick="removeMe();"></td>					
						<td valign="top" width=49%>
						<font color="white" face="Arial" size="2"><b>Your Choices:</b></font>
						<br>
						<select multiple name="FieldsList" style="COLOR:black; FONT-SIZE: 11px;;width:96%;" size="7">
																<option value="All">All</option>
						</select>
						</td>
						</tr>
												
					</table>										
				<br>
				
					<table>
						<tr>
							<td><img src="jsp/Images/arrow_right.png" height="15px" width="15px">&nbsp;&nbsp;&nbsp;</td>
							<td class="displayText" ><b>Quick Search By</b></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td class="displayText">
								<input type="radio" name="QuickSearch" value="accession_id" onclick="CheckPage(this)"/> GenBank Accession ID  (or)  
								<input type="radio" name="QuickSearch" value="marker_name" onclick="CheckPage(this)"/> Marker (or)
								<input type="radio" name="QuickSearch" value="genotype" onclick="CheckPage(this)"/> Genotype ID &nbsp;&nbsp;&nbsp;&nbsp;
								<input type="text" name="SearchMark" onfocus="test('QuickSearch')" onclick="this.focus();this.select();"  value="Accession/Marker Name" style="COLOR:#666; FONT-SIZE: 11px"/>
							</td>
						</tr>
						<tr><td colspan=2>&nbsp;</td></tr>
						<tr>
							<td><img src="jsp/Images/arrow_right.png" height="15px" width="15px">&nbsp;&nbsp;&nbsp;</td>
							<td class="displayText" ><b>Conditional Search By</b></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td class="displayText" >
								<input type="checkbox" name="main" value="SearchMarker" onclick="CheckPage(this)"/> Marker Name &nbsp;&nbsp;&nbsp;&nbsp;
								<input type="text" name="MarkNm" onfocus="test('SearchMarker')" onclick="this.focus();this.select();"  value="Any part of Marker name" style="COLOR: #666; FONT-SIZE: 11px"/>&nbsp;&nbsp;&nbsp;&nbsp;
									<select name="MarkerOption" style="COLOR: #666; FONT-SIZE: 11px">
										<option value="And" style="COLOR: #666; FONT-SIZE: 11px">And</option>
										<option value="or" style="COLOR: #666; FONT-SIZE: 11px">Or</option>
									</select>
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td class="displayText" >
								<input type="checkbox" name="main" value="Amplified" onclick="CheckPage(this)" /> Amplification type &nbsp;&nbsp;&nbsp;&nbsp;
								<input type="radio" name="Amp" value="Amplified" onfocus="test('Amplified')"/>Amplified &nbsp;&nbsp;&nbsp;&nbsp;
								<input type="radio" name="Amp" value="unAmplified" onfocus="test('Amplified')"/>Unamplified&nbsp;&nbsp;&nbsp;&nbsp;
									<select name="AmplificationOption" style="COLOR: #666; FONT-SIZE: 11px">
										<option value="And" style="COLOR: #666; FONT-SIZE: 11px">And</option>
										<option value="or" style="COLOR: #666; FONT-SIZE: 11px">Or</option>
									</select>	
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td class="displayText" >						
								<input type="checkbox" name="main" value="Annealing" onclick="CheckPage(this)"/> Annealing Temprature Between&nbsp;
								<input type="text" name="StartRange" style="COLOR: #666; FONT-SIZE: 11px" onfocus="test('Annealing')"/> and <input type="text" name="EndRange" style="COLOR: #666; FONT-SIZE: 11px" onfocus="test('Annealing')"/>
							</td>
						</tr>
						<tr><td colspan=2>&nbsp;</td></tr>
						<tr>
							<td><img src="jsp/Images/arrow_right.png" height="15px" width="15px">&nbsp;&nbsp;&nbsp;</td>
							<td class="displayText" ><b>Search By</b></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td class="displayText" >
								<input type="radio" name="type" value="Crop" onclick="CheckPage(this)"/>Crop &nbsp;&nbsp;&nbsp;&nbsp;
								<!--<html:radio property="type" value="MarkerName" onclick="retrieveData(this.value)">Marker </html:radio>&nbsp;&nbsp;&nbsp;&nbsp;-->
								<input type="radio" name="type" value="Accession_ID" onclick="CheckPage(this)"/>Genbank Accession ID &nbsp;&nbsp;&nbsp;&nbsp;
								<input type="radio" name="type" value="Marker_Type" onclick="CheckPage(this)"/>Marker Type &nbsp;&nbsp;&nbsp;&nbsp;
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td class="displayText" >
								<span style="visibility: hidden;" id="Firstlist">					
									<table>
										<tr>
											<td class="displayText"><div id="text1"></div></td>
											<td>&nbsp;&nbsp;<select name="List1"  onchange="select(this.value)" style="COLOR: #666; FONT-SIZE: 11px">
													<option value="">- Select -</option>
<%-- 													<%for(int i=0;i<al.size();i++){%>												 --%>
<%-- 														<option value="<%=al.get(i)%>"><%=al.get(i)%></option> --%>
<%-- 													<%} %> --%>
												</select>
											</td>
										</tr>
									</table>																		
									<span style="visibility: hidden;" id="sec">
										<table>
											<tr>
												<td class="displayText"><div id="text2"></div></td>
												<td>&nbsp;&nbsp;
													<select name="List2"  multiple="true" size="4" style="COLOR: #666; FONT-SIZE: 11px">
														<option value="">- Select -</option>
														
													</select>
												</td>
											</tr>
										</table>																										
									</span>							
								</span>	
							</td>
						</tr>
					</table>	
					</td>
					</tr>
					</Table>				
					<br>
				
				<input type="submit" value="Get Data"  onclick="return validatePage();"/>
				<input type="hidden" name="SelectedOption" value=""/>
				<input type="hidden" name="optionType" value=""/>
				<input type="hidden" name="SelectedFeilds" value=""/>
				<br><br>
											
			</center>
	
	</html:form>

</body>
</html:html>
<script type="text/javascript">

var httpRequest;
var SelectedValue;
var val1;
var Query;
function CheckPage(a){
	alert(a.name+"="+a.value);
	document.forms[0].elements['SelectedOption'].value=a.name;
	document.forms[0].elements['optionType'].value=a.value;
	if((a.name)=="QuickSearch"){
		val1=a.value;
		k=document.forms[0].Amp.length;
		for(p=0;p<document.forms[0].main.length;p++){
			document.forms[0].main[p].checked=false;
			document.forms[0].type[p].checked=false;
			if(p<k){
				document.forms[0].Amp[p].checked=false;
			}
		}
		document.forms[0].MarkNm.value="Any part of Marker name";
		document.forms[0].StartRange.value="";
		document.forms[0].EndRange.value="";
		document.getElementById("Firstlist").style.visibility="hidden";
		document.getElementById("sec").style.visibility="hidden";					
	}else if((a.name)=="main"){
		if(a.checked){
			k=document.forms[0].QuickSearch.length;
			for(p=0;p<document.forms[0].type.length;p++){
				document.forms[0].type[p].checked=false;
				if(p<k){
					document.forms[0].QuickSearch[p].checked=false;
				}
			}					
			document.forms[0].SearchMark.value="Accession/Marker Name";
			document.getElementById("Firstlist").style.visibility="hidden";
			document.getElementById("sec").style.visibility="hidden";
		}else{
			if(document.forms[0].main[0].checked==false && document.forms[0].main[1].checked==false && document.forms[0].main[2].checked==false){
				document.forms[0].elements['SelectedOption'].value="";
				document.forms[0].MarkNm.value="Any part of Marker name";
				document.forms[0].Amp[0].checked=false;
				document.forms[0].Amp[1].checked=false;
				document.forms[0].StartRange.value=="";
				document.forms[0].EndRange.value=="";
			}else if(document.forms[0].main[0].checked==false){
				document.forms[0].MarkNm.value="Any part of Marker name";
			}else if(document.forms[0].main[1].checked==false){
				document.forms[0].Amp[0].checked=false;
				document.forms[0].Amp[1].checked=false;
			}else if(document.forms[0].main[2].checked==false){
				document.forms[0].StartRange.value=="";
				document.forms[0].EndRange.value=="";
			}
		}					
	}else{
		document.forms[0].SearchMark.value="Accession/Marker Name";
		k=document.forms[0].Amp.length;
		for(p=0;p<document.forms[0].main.length;p++){
			document.forms[0].main[p].checked=false;
			if(p<k){
				document.forms[0].Amp[p].checked=false;
				document.forms[0].QuickSearch[p].checked=false;
			}
		}
		document.forms[0].MarkNm.value="Any part of Marker name";
		document.forms[0].StartRange.value=="";
		document.forms[0].EndRange.value=="";
		//alert(a.value);
		retrieveData(a);
	}						
}	
function retrieveData(ab){			
	val=ab.value;
	//alert(val);
	SelectedValue=val;
	var type="";
	document.getElementById("text1").innerHTML="Select "+val+" : ";
	document.getElementById("text2").innerHTML="Select Marker Name : ";
	
	document.getElementById("Firstlist").style.visibility="visible";
	document.getElementById("sec").style.visibility="hidden";
	var type="List1";
	//alert(window.ActiveXObject);
	if (window.ActiveXObject){ 
    	httpRequest = new ActiveXObject("Microsoft.XMLHTTP"); 
    }else if (window.XMLHttpRequest){ 
    	httpRequest = new XMLHttpRequest(); 
    } 
    name="";
    getDeta(type,val,name);				    
}
function getDeta(type,val,name){
	url='retrieveData.do?data='+val+'&type='+name;
	alert(url);
	httpRequest.open("GET", url, true);		        
  	httpRequest.onreadystatechange = function() {processRequest(val,type); } ; 
  	httpRequest.send(null);
}
function processRequest(value,type){
	if (httpRequest.readyState == 4) {
		if (httpRequest.status == 200) {
			msgDOM  = httpRequest.responseXML; 
	    	var data=msgDOM.getElementsByTagName("data")[0];
	    	details=data.getElementsByTagName("details");				    	
	    	document.forms[0].elements['type'].options.length=0;
	    	for(i=0;i<details.length;i++){				    		
	    		document.forms[0].elements['type'].options[i]=new Option(details[i].childNodes[0].nodeValue,details[i].childNodes[0].nodeValue);
	    	}				    	
	    }
	}
}
function select(ab){
	if(document.forms[0].elements['List1'].value!="- Select -"){
		tp="List2";
		va=SelectedValue;
		nm=document.forms[0].elements['List1'].value;
		getDeta(tp,va,nm);
		document.getElementById("sec").style.visibility="visible";					
	}
}
function validatePage(){
	var Selectedoption=document.forms[0].elements['SelectedOption'].value;
	if(Selectedoption==""){
		alert("Select one of the options to form a Query");
		return false;
	}else{
		if(Selectedoption=="QuickSearch"){
			if(document.forms[0].elements['SearchMark'].value==""||document.forms[0].elements['SearchMark'].value==null||document.forms[0].elements['SearchMark'].value=="Accession/Marker Name"){
				alert("Enter Accession/Marker Name");
				return false;
			}
								
		}else if(Selectedoption=="main"){
			Query="";
			if(document.forms[0].main[0].checked==true){
				if(document.forms[0].elements['MarkNm'].value==""||document.forms[0].elements['MarkNm'].value==null||document.forms[0].elements['MarkNm'].value=="Any part of Marker name"){
					alert("Enter Marker name");
					return false;
				}
			}else if(document.forms[0].main[1].checked==true){
				if(document.forms[0].Amp[0].checked==false && document.forms[0].Amp[1].checked==false){
					alert("Select Amplified/Unamplified");
					return false;
				}					
			}else if(document.forms[0].main[2].checked==true){
				if(document.forms[0].elements['StartRange'].value==""||document.forms[0].elements['StartRange'].value==null||document.forms[0].elements['EndRange'].value==""||document.forms[0].elements['EndRange'].value==null){
					alert("Enter range for Annealing Temprature");
					return false;
				}
			}
		
		}else{
			//alert(Selectedoption);
			var value=document.forms[0].elements['List1'].value;
			if(value==null||value==""||value=="- Select -"){
				alert("Select the "+SelectedValue+".");
				return false;
			}
			var Markervalue=document.forms[0].elements['List2'].value;
			if(Markervalue==null||Markervalue==""||Markervalue=="- Select -"){
				alert("Select the Marker Name.");
				return false;
			}
		}
		return saveMe();			
	}				
}
function test(a){
	if(a=="Amplified"){
		if(document.forms[0].main[1].checked==false){
			alert("Please check the 'Amplification type' checkbox");
			document.forms[0].main[1].focus();
			return false;
		}
	}else if(a=="QuickSearch"){
		Selectedoption=document.forms[0].elements['SelectedOption'].value;
		if(Selectedoption!="QuickSearch"){
			alert("Select Accession/Marker");
			document.forms[0].QuickSearch[0].focus();
			return false;
		}
	}else if(a=="Annealing"){
		if(document.forms[0].main[2].checked==false){
			alert("Please check the 'Annealing Temprature' checkbox");
			document.forms[0].main[2].focus();
			return false;
		}
	}else{
		if(document.forms[0].main[0].checked==false){
			alert("Please check the 'Marker Name' checkbox");
			document.forms[0].main[0].focus();
			return false;
		}
	}
}
function moveOver() {
	var boxLength = document.forms[0].FieldsList.length;
	var selectedLength=document.forms[0].choiceBox.length;
	if(selectedLength !=0){
		for(p=0;p<selectedLength;p++){
			if(document.forms[0].choiceBox.options[p].selected){
				var selectedText = document.forms[0].choiceBox.options[p].text;
				var selectedValue = document.forms[0].choiceBox.options[p].value;
				var i;
				var isNew = true;
				if (boxLength != 0) {
					for (i = 0; i < boxLength; i++) {
						thisitem = document.forms[0].FieldsList.options[i].text;
						if (thisitem == selectedText) {
							isNew = false;
							break;
						}
					}
				} 
				if (isNew) {
					newoption = new Option(selectedText, selectedValue, false, false);
					document.forms[0].FieldsList.options[boxLength] = newoption;
					boxLength++;
				}
			}						
		}
	}else{
		alert("Select Feilds from Textbox.");
	}
	document.forms[0].choiceBox.selectedIndex=-1;
}
function removeMe() {
	var boxLength = document.forms[0].FieldsList.length;
	arrSelected = new Array();
	var count = 0;
	for (i = 0; i < boxLength; i++) {
		if (document.forms[0].FieldsList.options[i].selected) {
			arrSelected[count] = document.forms[0].FieldsList.options[i].value;
		}
		count++;
	}
	var x;
	for (i = 0; i < boxLength; i++) {
		for (x = 0; x < arrSelected.length; x++) {
			if (document.forms[0].FieldsList.options[i].value == arrSelected[x]) {
				document.forms[0].FieldsList.options[i] = null;
		   }
		}
		boxLength = document.forms[0].FieldsList.length;
	}
}

function saveMe() {
	strValues= new Array();
	var boxLength = document.forms[0].FieldsList.length;
	var count = 0;
	if (boxLength != 0) {
		for (i = 0; i < boxLength; i++) {
			strValues[i]=document.forms[0].FieldsList.options[i].value;
			count++;
		}
	}
	//alert("strValues="+strValues);
	if (count == 0) {
		alert("You have not made any selections");
		return false;
	}else {
		document.forms[0].elements['SelectedFeilds'].value=strValues;
	}
}
</script>
