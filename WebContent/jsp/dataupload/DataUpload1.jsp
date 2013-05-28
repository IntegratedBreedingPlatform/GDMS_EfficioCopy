<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<html:html>
<head>
<title>GDMS</title>
<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
<script>
function chkFile(){
	var selValue="";
	if (document.forms[0].fileuploads.value == ""){
		alert("Upload the Template file");
		document.forms[0].fileuploads.focus();
		return false;
	}
		var maptype="";
	for(i=0;i<document.forms[0].radios.length;i++)
		if(document.forms[0].radios[i].checked==true){
			//alert(document.forms[0].radios[i].value);
			if((document.forms[0].radios[i].value=="SSRGenotype")||(document.forms[0].radios[i].value=="DArt")||(document.forms[0].radios[i].value=="Mapping")||(document.forms[0].radios[i].value=="QTL")||(document.forms[0].radios[i].value=="SSRMarker")||(document.forms[0].radios[i].value=="SNPMarker")||(document.forms[0].radios[i].value=="CISPMarker")||(document.forms[0].radios[i].value=="CAPMarker")){
			 	 if(document.forms[0].fileuploads.value.indexOf(".xls")== -1){
				 	alert("Check the input file, it has to be in excel format");
				 	document.forms[0].fileuploads.value="";		 	
				 	document.forms[0].fileuploads.focus();
				 	return false;	
				 }
			}else if(document.forms[0].radios[i].value=="SNPGenotype"){
				if(document.forms[0].fileuploads.value.indexOf(".txt")== -1){
				 	alert("Check the input file, it has to be in tab delimited text format");
				 	document.forms[0].fileuploads.value="";		 	
				 	document.forms[0].fileuploads.focus();
				 	return false;		
				 }
			}
			if(document.forms[0].radios[i].value=="Mapping"){
				
				for(ii=0;ii<document.forms[0].uploadFormatType.length;ii++)
					if(document.forms[0].uploadFormatType[ii].checked==true){
						document.forms[0].mapType.value=document.forms[0].uploadFormatType[ii].value;					
					}	
				//alert(document.forms[0].mapType.value); 
			}

			selValue=document.forms[0].radios[i].value;
			break;
		 }
		 
		
		if(selValue==""){
			alert("Please select upload template type");
			return false;
		}
	}
function msg(){
	
	<% //System.out.println("test"+request.getQueryString());
	String strValue = "";
	String strValue1 = "";
	String strValue2 = "";
	String strConc = "";
	String strColPosi = "";
	String strResult = request.getQueryString();
	String res="no";
	if(strResult == null)
		strResult = "";


		
	//Session time out message
	if(strResult.equals("SeTimeOut")){
		strResult="You have to re-login to upload the template.";
		res="yes";
	}

	//Common messages for all templates

	/////Message will be displayed when the sheet name not found in the uploaded template.
	if(strResult.equals("SheetNameNotFound")){
		strResult = "Error : Required sheet name(s) not found. Please verify with the sample template.";
		res="yes";
	}
	/////Message will be displayed when the column name should not be ordered as sample template.
	if(strResult.equals("ColumnNameNotFound")){
		res="yes";
		strResult = "Error : Column Name should be ";
		strValue = (String) session.getAttribute("colMsg");
	    strValue1 = (String) session.getAttribute("colMsg1");
	    strValue2 = " in "+(String) session.getAttribute("sheetName") +" sheet";
	    
	    strColPosi=(String) session.getAttribute("colposition");
	    String strCP = "";
	    if(strColPosi!=null)
	    		strCP = ".\n            Please delete if not required.\n            The row position is "+strColPosi +".";
	    		
	    
	    strConc = strValue1 + " not " + strValue + strValue2 + strCP;
	}

	

	if(strResult.equals("ReqFields")){
		res="yes";
		strValue = (String) session.getAttribute("fieldName");
		strValue1 = (String) session.getAttribute("colposition");
		strValue2 = (String) session.getAttribute("sheetName") +" sheet";
		strResult = "Error : "+strValue +" value should not be empty in the "+strValue2 +".\n            The column/row position is "+strValue1 +".";
		
	}

	///all the individual messages will be displayed.
	if(strResult.equals("ErrMsg")){
		res="yes";
		strValue = (String)	session.getAttribute("indErrMsg");
		strResult = "Error : "+strValue +".";
	}
	//Fields accepts only numberic values.
	if(strResult.equals("NumericValue")){
		res="yes";
		strResult = "";
		strValue = (String) session.getAttribute("fieldName");
	    strValue1 = (String) session.getAttribute("sheetName");

	    
	    strConc = "The "+strValue + " column name accepts only numeric values (or) two special characters (?,-) in " + strValue1 + " sheet";
	}

	//Field length messages.
	if(strResult.equals("FieldLength")){
		res="yes";
		strValue = (String) session.getAttribute("fieldName");
		strValue1 = (String) session.getAttribute("sheetName");
	strResult = strValue +" value should not exceed 75 characters in "+strValue1 +" sheet.";
	}
	//Field name messages.
	if(strResult.equals("FieldName")){
		res="yes";
		strValue = (String) session.getAttribute("sheetName");
		strValue1 = (String) session.getAttribute("colposition");
		strResult = "Error : Column name should not be empty in " + strValue + " sheet.\n            Please delete if not required.\n            The cell position is "+strValue1+".";
		
	}
	///message will be displayed when user uploaded filed size is more than datatype size.
	if(strResult.equals("ColLengthError")){
		res="yes";
		strValue = (String) session.getAttribute("dlength");
		strValue1 = (String) session.getAttribute("sheetName");
		strValue2 = (String) session.getAttribute("colposition");
		strResult = "Column length should not exceed more than " + strValue + " in " + strValue1 + " sheet.\n The column position is "+strValue2 +".";

	}
	
	if(strResult.equals("infoRequired")){
		res="yes";
		//strValue1 = (String) session.getAttribute("colpositionNull");
		strValue2 = (String) session.getAttribute("sheetNameNull") +" sheet.";
		strResult = "Error : Please provide map info in " + strValue2;
	}

	

	//Message for empty rows deletion in SSR_Genotyping Template
	if(strResult.equals("DelEmptyRows")){
		res="yes";
		strValue1 = (String) session.getAttribute("colposition");
		strValue2 = (String) session.getAttribute("sheetName") +" sheet.";
		strResult = "Error : Please delete empty row in " + strValue2 +".\nThe row position is "+strValue1+".";
	}
	//Message for empty cols deletion in SSR_Genotyping Template
	if(strResult.equals("DelEmptyColumns")){
		res="yes";
		strValue1 = (String) session.getAttribute("colposition");
		strValue2 = (String) session.getAttribute("sheetName") +" sheet.";
		strResult = "Error : Please delete empty column in " + strValue2 +".\nThe column position is "+strValue1+".";
	}
	//insertion messages
	if (strResult.equals("inserted")){
		res="yes";
		strResult = "Data has been inserted into the database.";
		
	}
	if(strResult.equals("error")){
		res="yes";
		strResult = "Found Errors while uploading the Excel Sheet";
	}

	if(strConc.equals(" not "))
		strConc = "";

	//eeeeeeeeeeeeeeeeeee


	%>
	if((document.forms[0].elements['hResult'].value != "")&&(document.forms[0].elements['hResult'].value != "first")&&(document.forms[0].elements['hResult'].value != "markers")&&(document.forms[0].elements['hResult'].value != "geno")&&(document.forms[0].elements['hResult'].value != "mapsQtls")&&(document.forms[0].elements['hResult'].value != "qtl"))
	alert(document.forms[0].elements['hResult'].value)

	//go to login page when default session timeout is over.

	if(document.forms[0].elements['hResult'].value=="You have to re-login to upload the template."){
		//document.forms[0].action="Login.jsp";
		document.forms[0].submit();
	}
	<%
	String upType=session.getAttribute("uploadType").toString();
	
	//remove the content from session variables   	
	session.removeAttribute("colMsg");
	session.removeAttribute("colMsg1");
	session.removeAttribute("sheetName");
	session.removeAttribute("dlength");
	session.removeAttribute("colposition");
	session.removeAttribute("fieldName");
	session.removeAttribute("indErrMsg");
	%>
	}
	function refreshPage(){
		//alert('<%=request.getQueryString()%>');
		<%
			String str=request.getQueryString();
		
		%>
		if(str="first"){
			//alert(">>>>>>>>>>>>>>>>");
			var radList = document.getElementsByName('uploadType');
			for (var i = 0; i < radList.length; i++) {
				if(radList[i].checked) radList[i].checked = false;
			}
		}
		
		
	}
	function refresh(){
		//alert('<%=request.getQueryString()%>'+"   "+'<%=upType%>');
		var op='<%=request.getQueryString()%>';
		var radList = document.getElementsByName('uploadType');
		var radList1 = document.getElementsByName('radios');
		var radList2 = document.getElementsByName('opMap');
		for (var i = 0; i < radList.length; i++) {			
			if(radList[i].checked && radList[i].value!=op) radList[i].checked = false;
			//if(radList[i].checked && radList1[i].value!=op) radList[i].checked = false;
		}
		
		for (var i = 0; i < radList1.length; i++) {
			if(radList1[i].checked && radList1[i].value!=op) radList1[i].checked = false;
		}

		
		for (var i = 0; i < radList2.length; i++) {
			if(radList2[i].checked && radList2[i].value!=op) radList2[i].checked = false;
		}

	}
</script>
</head>
<body onload="msg(); refresh();">
<html:form method="post" action="/dataupload.do"  enctype="multipart/form-data">
<logic:notEmpty name="user">
	<div class="heading" align="center">Data Uploading</div>
	<!-- <table width='35%' border=0 align=right>
		<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
	</table>
	
	--><center>
	<br>
	<html:errors/>
	<br>

	<div align="center" class="displayText">(Data can be uploaded using provided templates.<br> To upload, select button, browse & upload template containing data.)</div>
	<br>
	<div align="center" class="displayText">Please upload Marker Information before uploading Genotyping Data</div>
	<br><br>
	<%if(request.getQueryString().equals("first")){%>
	<table width="50%" border=0 align="center">
		<tr class="displayText">
			<td width="32%" align="center"><html:radio property="uploadType" value="markers" onclick="retOtherOptions(this)"/>&nbsp;Marker Information</td>
			<td width="35%" align="center"><html:radio property="uploadType" value="geno" onclick="retOtherOptions(this)"/>&nbsp;Genotyping Data</td>
			<td align="center"><html:radio property="uploadType" value="mapsQtls" onclick="retOtherOptions(this)"/>&nbsp;Maps/QTLs/MTAs</td>
		</tr>			
	</table>
	<%} %>
	
	<br>
	<%if((request.getQueryString().equals("markers"))||((res.equals("yes"))&&((upType.equalsIgnoreCase("SSRMarker"))||(upType.equalsIgnoreCase("CISRMarker"))||(upType.equalsIgnoreCase("SNPMarker"))||(upType.equalsIgnoreCase("CAPMarker"))))){
				%>
	<table width="50%" border=0>
		<tr class="displayText">
			<td width="32%" align="center"><html:radio property="uploadType" value="markers" onclick="retOtherOptions1(this)"/>&nbsp;Marker Information</td>
			<td width="35%" align="center"><html:radio property="uploadType" value="geno" onclick="retOtherOptions1(this)"/>&nbsp;Genotyping Data</td>
			<td align="center"><html:radio property="uploadType" value="mapsQtls" onclick="retOtherOptions1(this)"/>&nbsp;Maps/QTLs/MTAs</td>			
		</tr>			
	</table>
	<br>
	<table border=0 cellpadding=3 cellspacing=1 width="60%"  bgcolor="white">		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="SSRMarker" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">SSR Marker</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/SSR_Marker.xls" target="new">SSR Marker Sample Template</a>&nbsp;</td></tr>
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="SNPMarker" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">SNP Marker</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/SNP_Marker.xls" target="new">SNP Marker Sample Template</a>&nbsp;</td></tr>
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="CISRMarker" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">CISR Marker</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/CISRMarker.xls" target="new">CISR Marker Sample Template</a>&nbsp;</td></tr>
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="CAPMarker" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">CAP Marker</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/CAPMarker.xls" target="new">CAP Marker Sample Template</a>&nbsp;</td></tr>
	
	
		<tr><td colspan=3>&nbsp;</td></tr>
		<tr><td colspan=3 align="center"><html:file property="fileuploads"/></td></tr>
		<tr><td colspan=3>&nbsp;</td></tr>
		<tr><td>&nbsp;</td><td>&nbsp;</td><td><html:submit property="dupload" onclick="return chkFile()"/></td></tr>
		<tr><td colspan=3>&nbsp;</td></tr>
	<%} %>
	<%if((request.getQueryString().equals("geno"))||((res.equals("yes"))&&((upType.equalsIgnoreCase("SSRGenotype"))||(upType.equalsIgnoreCase("SNPGenotype"))||(upType.equalsIgnoreCase("DArT"))||(upType.equalsIgnoreCase("Mapping"))))){
				%>
	<table width="50%" border=0>
		<tr class="displayText">
			<td width="32%" align="center"><html:radio property="uploadType" value="markers" onclick="retOtherOptions1(this)"/>&nbsp;Marker Information</td>
			<td width="35%" align="center"><html:radio property="uploadType" value="geno" onclick="retOtherOptions1(this)"/>&nbsp;Genotyping Data</td>
			<td align="center"><html:radio property="uploadType" value="mapsQtls" onclick="retOtherOptions1(this)"/>&nbsp;Maps/QTLs/MTAs </td>			
		</tr>			
	</table>
	<br>
	<table border=0 cellpadding=3 cellspacing=1 width="60%"  bgcolor="white">		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="SSRGenotype" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">SSR Genotype</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/SSR_GenotypingTemplate.xls" target="new">SSR Genotype Sample Template</a></td></tr>		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="SNPGenotype" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">SNP Genotype</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/SNPGenotypingTemplate.txt" target="new">SNP Genotype Sample Template</a></td></tr>
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="DArT" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">DArT Genotype</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/DArTGenotypingTemplate.xls" target="new">DArT Genotype Sample Template</a></td></tr>
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="Mapping" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">Mapping </td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/MappingTemplate.xls" target="new">Mapping Sample Template</a></td></tr>	
		
		<tr><td colspan=3>
			<span id="option" style="visibility: hidden;">
				<table border=0 width="100%">
					<tr><td width="6%">&nbsp;</td>
						<td class="displayText" width="30%" align="right" nowrap="nowrap"><div id="text2"></div></td>
						<td width="15%"><html:radio property="opMap" value="yes" onclick="retrieveData(this)">Yes</html:radio>&nbsp;<html:radio property="opMap" value="no" onclick="retrieveData(this)">No</html:radio></td>
						<td>				
							<span style="visibility: hidden;" id="Firstlist">					
								<table border=0>
									<tr>
										<td class="displayText" nowrap="nowrap"><div id="text1"></div></td>
										<td>&nbsp;&nbsp;<select name="List1" style="COLOR: #666; FONT-SIZE: 11px">
												<option value="">- Select -</option>
											</select>
										</td>
									</tr>
									
								</table>
							</span>
						</td>
					</tr>
					<tr class="displayText">
						<td colspan="4">
							<span style="visibility: hidden;" id="firstOption">				
								<table width="60%" border=0 align="center">
									<tr class="displayText">					
										<td width="30%" nowrap="nowrap">Data Format: </td>
										<td align="left" width="25%" nowrap="nowrap"><html:radio property="uploadFormatType" value="allelic" onclick="funcDataType(this.value)"/>&nbsp;Allelic Data</td>
										<td nowrap="nowrap"><html:radio property="uploadFormatType" value="abh" onclick="funcDataType(this.value)"/>&nbsp;AB Data</td>
									</tr>
								</table>
							</span>
						</td>
					</tr>
					<tr class="displayText">
						<td colspan="4" nowrap="nowrap">
							<span style="visibility: hidden;" id="secOption">				
								<table width="50%" border=0 align="rigth">
									<tr class="displayText">
									<td align="right" width="55%"><html:radio property="uploadDataType" value="ssr" />&nbsp;SSR</td>
									<td align="center"><html:radio property="uploadDataType" value="snp"/>&nbsp;SNP</td>
									<td><html:radio property="uploadDataType" value="dart"/>&nbsp;DArT</td>
									</tr>
								</table>							
							</span>						
						</td>
					</tr>
				</table>
			</span>
		</td></tr>
		<tr><td colspan=3>
			<span id="option1" style="visibility: hidden;">
				<table border=0 width="100%">
					<tr><td width="6%">&nbsp;</td>
						<td class="displayText" width="15%" align="center" nowrap="nowrap">K-BioScience Format</td>
						<td width="15%">
							<html:radio property="opMap" value="yes" onclick="retrieveSNP(this)">Yes</html:radio>&nbsp;
							<html:radio property="opMap" value="no" onclick="retrieveSNP(this)">No</html:radio></td>
						<td>				
							<span style="visibility: hidden;" id="FirstlistSNP">					
								<table border=0>
									<tr>
										<td class="displayText" nowrap="nowrap">K-BioScience Genotyping Grid file </div></td>
										<td>&nbsp;&nbsp;<html:file property="KBfileuploads"/></td>
									</tr>
									
								</table>
							</span>
						</td>						
					</tr>
					
				</table>
			</span>
		</td></tr>
		<tr><td colspan=3>&nbsp;</td></tr>
		<tr>
			<td colspan="2" align="right">
				<span style="visibility: hidden;" id="FirstlistSNP1">					
					<table border=0>
						<tr>
							<td class="displayText" nowrap="nowrap">KBio genotyping additional information</td>
							
						</tr>
						
					</table>
				</span>
			</td>
			<td colspan=1 align="left"><html:file property="fileuploads"/></td>
		</tr>
		<tr><td colspan=3>&nbsp;</td></tr>
		<tr><td>&nbsp;</td><td>&nbsp;</td><td><html:submit property="dupload" onclick="return chkFile()"/></td></tr>
		<tr><td colspan=3>&nbsp;</td></tr>
	<%} %>
	
	<%if((request.getQueryString().equals("mapsQtls"))||((res.equals("yes"))&&((upType.equalsIgnoreCase("Map"))||(upType.equalsIgnoreCase("QTL"))||(upType.equalsIgnoreCase("MTA"))))){%>
	<table width="50%" border=0>
		<tr class="displayText">
			<td width="32%" align="center"><html:radio property="uploadType" value="markers" onclick="retOtherOptions1(this)"/>&nbsp;Marker Information</td>
			<td width="35%" align="center"><html:radio property="uploadType" value="geno" onclick="retOtherOptions1(this)"/>&nbsp;Genotyping Data</td>
			<td align="center"><html:radio property="uploadType" value="mapsQtls" onclick="retOtherOptions1(this)"/>&nbsp;Maps/QTLs/MTAs</td>			
		</tr>			
	</table>
	<br>
	<table border=0 cellpadding=3 cellspacing=1 width="60%"  bgcolor="white">		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="Map" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">Map</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/MapTemplate.xls" target="new">Map Sample Template</a></td></tr>		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="QTL" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">QTL</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/QTLTemplate.xls" target="new">QTL Sample Template</a></td></tr>		
		<tr><td width="10%" align=right class="displayText"><html:radio property="radios" value="MTA" onclick="funcShowOption(this.value)" /></td><td width="15%" class="displayText">MTA</td><td width="35%" class="displayText"><a href="<%=request.getContextPath()%>/jsp/dataupload/MTATemplate.xls" target="new">MTA Sample Template</a></td></tr>
		<tr><td colspan=3>
			<span id="option" style="visibility: hidden;">
				<table border=0 width="100%">
					<tr><td width="6%">&nbsp;</td>
						<td class="displayText" width="30%" align="right" nowrap="nowrap"><div id="text2"></div></td>
						<td width="15%"><html:radio property="opMap" value="yes" onclick="retrieveData(this)">Yes</html:radio>&nbsp;<html:radio property="opMap" value="no" onclick="retrieveData(this)">No</html:radio></td>
						<td>				
							<span style="visibility: hidden;" id="Firstlist">					
								<table border=0>
									<tr>
										<td class="displayText" nowrap="nowrap"><div id="text1"></div></td>
										<td>&nbsp;&nbsp;<select name="List1" style="COLOR: #666; FONT-SIZE: 11px">
												<option value="">- Select -</option>
											</select>
										</td>
									</tr>
									
								</table>
							</span>
						</td>
					</tr>
				</table>
			</span>
		</td>
		</tr>
		<tr><td colspan=3>&nbsp;</td></tr>
		<tr><td colspan=3 align="center"><html:file property="fileuploads"/></td></tr>
		<tr><td colspan=3>&nbsp;</td></tr>
		<tr><td>&nbsp;</td><td>&nbsp;</td><td><html:submit property="dupload" onclick="return chkFile()"/></td></tr>
		<tr><td colspan=3>&nbsp;</td></tr>
	<%} %>
	<input type=hidden name="UploadingOption" value=""/>
	<input type=hidden name="hResult" value='<%=strResult %><%=strConc %>'>
	<input type=hidden name="mapType" value="">
	</center>
</logic:notEmpty>
<logic:empty name="user">
	<br><br><br>
	<!-- <center><font color="blue" face="verdana" size="3px"><a href="../common/Home.jsp" target="_parent">Please Login to upload/retrieve data</a></font></center>-->
	<center><font color="blue" face="verdana" size="3px"><a href="../common/URLtoAction.jsp?str=logout" target="_parent">Please Login to upload/retrieve data</a></font></center>
</logic:empty>
</html:form>
</body>
</html:html>
<script>

function retOtherOptions1(ss){
	//alert("2nd function: "+ss.value);
	if(ss.value=="markers"){
		document.forms[0].action="uploadingpage.do?markers";		
		document.forms[0].submit();
	}else if(ss.value=="geno"){
		document.forms[0].action="uploadingpage.do?geno";		
		document.forms[0].submit();
	} else if(ss.value=="mapsQtls"){
		document.forms[0].action="uploadingpage.do?mapsQtls";		
		document.forms[0].submit();
	}
	
}


function retOtherOptions(arg){

	//alert("first function="+arg.value);
	if(arg.value=="markers"){
		document.forms[0].action="../../uploadingpage.do?markers";		
		document.forms[0].submit();
	}else if(arg.value=="geno"){
		document.forms[0].action="../../uploadingpage.do?geno";		
		document.forms[0].submit();
	} else if(arg.value=="mapsQtls"){
		document.forms[0].action="../../uploadingpage.do?mapsQtls";		
		document.forms[0].submit();
	}
	
}
function funcDataType(val){
	if(val=="allelic")
		document.getElementById('secOption').style.visibility='visible';
	else
		document.getElementById('secOption').style.visibility='hidden';
}

function funcShowOption(a){
	//alert(a);
	document.forms[0].elements['UploadingOption'].value=a;
	if(a=="Mapping"){
		document.getElementById("text2").innerHTML="Corresponding Map exists ";
		document.getElementById('firstOption').style.visibility='visible';
		document.getElementById('option').style.visibility='visible';
		document.getElementById('option1').style.visibility='hidden';
		document.getElementById('FirstlistSNP').style.visibility='hidden';
		document.getElementById('FirstlistSNP1').style.visibility='hidden';
	}else if(a=="Map"){
		document.getElementById("text2").innerHTML="Corresponding Mapping data exists ";
		document.getElementById('option').style.visibility='visible';
		document.getElementById('secOption').style.visibility='hidden';
		document.getElementById('firstOption').style.visibility='hidden';
		document.getElementById('option1').style.visibility='hidden';
		document.getElementById('FirstlistSNP').style.visibility='hidden';
		document.getElementById('FirstlistSNP1').style.visibility='hidden';
	}else if(a=="SNPGenotype"){
		document.getElementById("text2").innerHTML="K-BioScience Output";
		//document.getElementById('firstOption').style.visibility='visible';
		document.getElementById('option').style.visibility='hidden';
		//document.getElementById("text2").innerHTML="Corresponding Map exists ";
		//document.getElementById('firstOption').style.visibility='hidden';
		document.getElementById('option1').style.visibility='visible';
	}else{
		document.getElementById('option').style.visibility='hidden';
		document.getElementById('secOption').style.visibility='hidden';
		document.getElementById('firstOption').style.visibility='hidden';
		document.getElementById('Firstlist').style.visibility='hidden';
		document.getElementById('option1').style.visibility='hidden';
		document.getElementById('FirstlistSNP').style.visibility='hidden';
		document.getElementById('FirstlistSNP1').style.visibility='hidden';
	}
	
}

function retrieveSNP(a){
	var type=document.forms[0].elements['UploadingOption'].value;
	var val="";
	var name="List1";
	val=a.value;
	var msg="";
	//alert(type)
	if(val=="yes"){
		document.getElementById("FirstlistSNP").style.visibility="visible";
		document.getElementById("FirstlistSNP1").style.visibility="visible";
	}else{
		document.getElementById("FirstlistSNP").style.visibility="hidden";
		document.getElementById("FirstlistSNP1").style.visibility="hidden";
	}
}

function retrieveData(ab){	
	//alert(ab.name+"  "+document.forms[0].elements['UploadingOption'].value);
	var type=document.forms[0].elements['UploadingOption'].value;
	var val="";
	var name="List1";
	val=ab.value;
	var msg="";
	//alert(type)
	if(type=="Mapping")
		msg="Map";
	else if(type=="Map")
		msg="";
	//alert("val="+val);
	if(val=="yes"){
		document.getElementById("text1").innerHTML="Select "+msg+" : ";
					
		document.getElementById("Firstlist").style.visibility="visible";
		if (window.ActiveXObject){ 
	    	httpRequest = new ActiveXObject("Microsoft.XMLHTTP"); 
	    }else if (window.XMLHttpRequest){ 
	    	httpRequest = new XMLHttpRequest(); 
	    } 
	   
	    getDeta(type,val,name);	
	}else{
		document.getElementById("Firstlist").style.visibility="hidden";
	}		    
}
function getDeta(type,val,name){
	//alert(document.getElementById("radios").value);
	url='retrieve.do?data='+val+'&type='+type;
	//alert(url);
	httpRequest.open("GET", url, true);		        
  	httpRequest.onreadystatechange = function() {processRequest(val,name); } ; 
  	httpRequest.send(null);
}
function processRequest(value,name){
	if (httpRequest.readyState == 4) {
		if (httpRequest.status == 200) {
			msgDOM  = httpRequest.responseXML; 
	    	var data=msgDOM.getElementsByTagName("data")[0];
	    	details=data.getElementsByTagName("details");				    	
	    	document.forms[0].elements[name].options.length=0;
	    	for(i=0;i<details.length;i++){				    		
	    		document.forms[0].elements[name].options[i]=new Option(details[i].childNodes[0].nodeValue,details[i].childNodes[0].nodeValue);
	    	}				    	
	    }
	}
}

</script>