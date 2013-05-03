<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GDMS</title>
<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">

</head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script>
function msg(){	
	<%
	String strValue = "";				
	String strResult = (String)request.getSession().getAttribute("indErrMsg");
	String strResult1 = request.getQueryString();
	
	if(strResult == null)
		strResult = "";
	
		
	if(strResult.equals("ErrMsg")){
		strValue = (String)	session.getAttribute("indErrMsg");
		strResult = "Error : "+strValue +".";
	}
	%>
	if(document.forms[0].elements['hResult'].value != ""){
		alert(document.forms[0].elements['hResult'].value);					
		document.forms[0].qtl.focus();
		document.forms[0].qtl.value="";
	}
	<%
	
	session.removeAttribute("indErrMsg");
	%>
}


</script>
</head>
<body onload="msg();">
<html:form action="/dataretrieval.do" method="post" enctype="multipart/form-data">
<br><div class="heading" align="center">Map/QTL Data Retrieval</div><br>
				<br>
				<center>
				<table border=0 width="45%">
					<tr class="displayText" align="center">
						<td>Search by &nbsp;&nbsp;&nbsp;
							<select name="maps" id="maps" onclick="onClickOption(this.name)">
								<option value="maps">Map Name</option>
								<option value="QTLName">QTL Name</option>
								<option value="Trait">Trait</option>								
							</select>
							&nbsp;&nbsp;&nbsp;<html:text property="qtl" value="" style="COLOR:#666;"/>
						</td>
					</tr>
						
					
					<tr><td>&nbsp;</td></tr>
					<tr><td>&nbsp;</td></tr>
					<tr class="displayText" align="center">
						<td align="center"><html:submit property="qtlsub" value="Submit" onclick="return sub()"/></td>
					</tr>
				</table>
				<input type=hidden name="hResult" value='<%=strResult %>'>
				<input type=hidden name="retType" value="">
				<html:hidden property="retrieveOP"/>
				</html:form>
</body>
</html:html>

<script>
function sub(){
	if(document.forms[0].maps.value=="QTLName" && document.forms[0].qtl.value==""){			
		alert("Please provide the QTL name");
		return false;
	}else if(document.forms[0].maps.value=="Trait" && document.forms[0].qtl.value==""){
		alert("Please provide the trait");
		return false;
	}else if(document.forms[0].maps.value=="maps" && document.forms[0].qtl.value==""){
		alert("Please provide the Map name");
		return false;
	}
	//alert(document.forms[0].maps.value);
	document.forms[0].elements["retType"].value=document.forms[0].maps.value;	
	
	
	document.forms[0].elements["retrieveOP"].value="GetInfo";	
	
}
</script>