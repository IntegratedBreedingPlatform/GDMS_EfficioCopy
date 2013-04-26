<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GDMS</title>
<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">

</head>
<body onload="pageRefresh();">

	<html:form action="/genotypingpage.do?second">
	
		<%if((session.getAttribute("user")==null)||(session.getAttribute("user")=="null")||(session.getAttribute("user")=="")){%>
			<br><br><br>
			<center><font color="blue" face="verdana" size="3px"><a href="../common/URLtoAction.jsp?str=logout" target="_parent">Please Login to upload/retrieve data</a></font></center>
		<%} %>
		<logic:notEmpty name="user">
	
			<br>
			<%
				//String str=request.getParameter("str"); 
				String path=request.getSession().getServletContext().getRealPath("//");	
				
			%>
				
				<Table border=0 width="60%" cellpadding=5 cellspacing=2 align="center">
					<tr style="font-size: medium;font-weight: bold;">
						<%--<td colspan=2>&nbsp;&nbsp;<img src="<%=path%>" height="15px" width="15px">&nbsp;&nbsp;&nbsp;
							Retrieve </td>--%>
						<td colspan=2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Retrieve </td>
					</tr>
					<tr><td colspan=2 align="center"><font color=blue >&nbsp;Note: select the option Genotyping Data or Marker Information for retrieving the corresponding data</font></td></tr>
					<tr><td colspan=2>&nbsp;</td></tr>
					<tr class="displayText">
						<td colspan=2>
							<table width=70% border=0 align="center">
								<tr class="displayText">
									<td align="center"><html:radio property="reportType" value="genotyping" onclick='nextPage(this)'>Genotyping Data</html:radio></td>
									<td align="center"><html:radio property="reportType" value="marker" onclick='nextPage(this)'>Marker Information</html:radio></td>
									<td align="center"><html:radio property="reportType" value="QTL/Map" onclick='nextPage(this)'>Map/QTL Data</html:radio></td>
								</tr>
							</table>
						</td>
					</tr>
				</Table>
				
				
			<br>
			</logic:notEmpty>

	</html:form>
</body>
</html:html>
<script>
function nextPage(opt){	
	document.forms[0].elements['reportType'].value="opt.value";
	var check=opt.value;
	//alert(check);
	if(check=="genotyping"){
		document.forms[0].action="../../genotypingpage1.do";	
		document.forms[0].submit();
	}else if(check=="marker"){
		document.forms[0].action="../../markerpage.do";
		//alert(document.forms[0].action);
		document.forms[0].submit();		
	}else{		
		//document.forms[0].elements["op"].value=a.value;	
		//document.getElementById('QTLSpan').style.visibility='visible';
		//document.forms[0].elements["retrieveOP"].value="first";
		document.forms[0].action="../../retrievalmapsqtls.do";
		//alert(document.forms[0].action);
		document.forms[0].submit();
	}
}
/*function nPage(opt){	
	document.forms[0].elements['reportType'].value="opt.value";
	var check=opt.value;	
	if(check=="genotyping"){
		document.forms[0].action="././genotypingpage.do?second";	
		document.forms[0].submit();
	}else{		
		document.forms[0].action="././markerpage.do";
		document.forms[0].submit();
	}
}*/
function pageRefresh(){	
	
		var radList = document.getElementsByName('reportType');
		for (var i = 0; i < radList.length; i++) {
			if(radList[i].checked) radList[i].checked = false;
		}
	
}


</script>
	