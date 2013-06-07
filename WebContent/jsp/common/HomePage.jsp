<html>
<head>
	<title>GDMS</title>
	<LINK REL="stylesheet" HREF="jsp/common/GDMSStyleSheet.css" TYPE="text/css">
</head>
<br><br><br><br>
<center>
	<br><br><br><br>
	<%String op=request.getParameter("str");
	if(op.equalsIgnoreCase("login")){
	
	%>
		<font color="blue" face="Calibri" size="4px">Login Successful!</font><br><br>
		<font color="blue" face="Calibri" size="4px">Now you can <a href="../../jsp/common/GDMSLayout.jsp?str=upload" target="_parent">Upload</a> , <a href="../../jsp/common/GDMSLayout.jsp?str=retrieve" target="_parent">Retrieve</a> or <a href="../../jsp/common/GDMSLayout.jsp?str=delete" target="_parent">View/Delete</a> Data</font>
		
		<!--<font color="blue" face="verdana" size="4px">Login Successful!</font><br><br>
		<font color="blue" face="verdana" size="4px">Now you can <a href="../../jsp/common/GDMSLayout.jsp?str=upload" target="_parent">Upload</a> or <a href="../../jsp/common/GDMSLayout.jsp?str=retrieve" target="_parent">Retrieve</a> Data</font>-->
		
	<%}else if(op.equalsIgnoreCase("logout")){ %>
		<font color="blue" face="verdana" size="3px">Logged out Successfully!</font>
	<%} %>
</center>		
</html>