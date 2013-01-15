<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<html:html>
<head>
	<title>GDMS</title>
	<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
	
<script>
function msg(){
	<% System.out.println("test:"+request.getQueryString());
	String strResult = request.getQueryString();
	if(strResult == null)
		strResult = "";

	if(strResult.equals("incorrect"))
		strResult = "Password incorrect";
	System.out.println("***********************"+strResult);
	%>
	if(document.forms[0].elements['hResult'].value != "")
	alert(document.forms[0].elements['hResult'].value);	
	}

</script>
</head>
	<%String op=request.getParameter("op");%>

	
	<body onload="msg()">
	<center>
	<img src="jsp/Images/GDMS_1.gif" border=0 usemap="#Map2">
	<html:form method="post" action="/login.do">
		<!-- <div class="heading" align="center">Login</div>-->
		<br><br><br><br><br>
		
			<table cellspacing=5 border="0">
				<tr>
					<td nowrap valign=top class="displayText">Username:</td>
					<td width=5></td>
					<td align=left>
						<html:text property="uname" value="" />		
					</td>
				</tr>			
				<tr>
					<td nowrap class="displayText">Password:</td>
					<td width=5></td>
					<td align=left><html:password property="password" value=""/></td>				
				</tr>				
			</table>			
			<html:hidden property="menuOp" value='<%=request.getParameter("op")%>'/>
			<br><br><br>
			<html:submit value="Submit" />
			<html:reset value="Clear" />&nbsp;&nbsp;
		<html:hidden property="crop" value='<%=request.getParameter("crop") %>'/>
			</center> 
	</html:form>
</body>
		<input type=hidden name="hResult" value='<%=strResult %>'>
		<br><br><br><br><br><br><br>
		<center><img src="jsp/Images/GDMS_Footer.gif" border="0" usemap="#Map3" valign="bottom">
<map name="Map3">
	<area shape="rect" coords="382,6,468,23" href="mailto:bioinformatics@cgiar.org">	
</map>
</center>
</html:html>

