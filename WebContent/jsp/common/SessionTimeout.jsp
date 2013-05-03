<html>
<body bgcolor=white>
<center>
<br><br><br><br>
<br><br><br><b><font size=4 color=red>you have to re-login to access the LIMS System.</font></b><br><br>
<font size=4 color=blue>
<%System.out.println("request.getQueryString()="+request.getQueryString()); %>
	<%if(request.getParameter("from")=="jsp"){
	//if(request.getQueryString()==null){ %>
		<a href="../../common/URLtoAction.jsp?str=logout" target=_parent>click here to re-login</a>
	
	<%}else{%>
			<a href="../common/URLtoAction.jsp?str=logout" target=_parent>click here to re-login</a>
	<%}%>
	
</font>
<br><br>
</center>
</body>
</html>