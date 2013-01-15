<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GDMS</title>
<LINK REL="stylesheet" HREF="<html:rewrite forward='GDMSStyleSheet'/>" TYPE="text/css">

<script>

function refresh(){
	var op=document.forms[0].option.value;
	//alert(op);
	var valueF="Run Flapjack";
	//if((op=="allelic")||(op=="flapjack")){
	if(op=="allelic"){
		document.forms[0].action="export.do";
	}else if(op=="abhformat"){
		document.forms[0].action="exportabh.do";
	}else{
		document.forms[0].action="Flapjack.jsp?str="+valueF;
	}

document.forms[0].submit();
}
</script>
</head>
<body onload="refresh()">

<form action="/export" method="post">
 <center><br><font class="pageTitle"></font></center>
 <br>
 <!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
--><br><br><br><br><br><br>
<center>
<font class="sessionDataLabels"><b>retrieving data..</b></font><br><br>
<table border=0 cellspacing=0 width="25%">
	<%--<tr><td><img src="<%=request.getContextPath() %>/img/progressbar1.gif"/></td></tr>--%>
	<tr><td align="center"><img src="<%=request.getContextPath() %>/jsp/Images/progressbar2.gif"/></td></tr>
</table>
<input type="hidden" name=option value='<%= request.getQueryString()%>'/>
</center>
</form>
</body>
</html>