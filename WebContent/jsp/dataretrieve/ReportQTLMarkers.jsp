<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GDMS</title>
<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
</head>
<body>
<center>
<img src="jsp/Images/GDMS_Banner.gif" border=0 usemap="#Map2">
	<map name="Map2">
		<area shape="rect" coords="13,90,60,109" href="../../" alt="Home" target="_parent">
	 	<area shape="rect" coords="88,90,142,109" href="jsp/common/GDMSLayout.jsp?upl" alt="Upload" target="_parent">
	  	<area shape="rect" coords="172,90,235,109" href="jsp/common/GDMSLayout.jsp?ret" alt="Retrieve" target="_parent">
	</map>
	
<br>
<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
--><br>
<br>

<%
String marker=session.getAttribute("markers").toString();
//if(marker.)
String[] args=marker.split("!~!");
System.out.println("Length="+args.length);
if(args.length>1){%>
	<table border=1  style="font-size:11" cellpadding=4 cellspacing=1 bordercolor="#006633" align="center">
		<tr bgcolor="#006633" class="displayHeadingBoldText"><td nowrap>Marker Names</td></tr>
		<%for(int m=0;m<args.length;m++){%>
			<tr><td><%=args[m]%></td></tr>
		<%} %>
	</table>
<%}else{%>
	<div align="center" class="displayText"><font color="red">No Data!! </font>&nbsp; for '<%=session.getAttribute("qtlName") %>'</div>
	
<%} %>
<br>
<br>
<html:button property="back" value="Close" onclick="javascript:window.close();"/>
</center>
</body>
</html:html>
