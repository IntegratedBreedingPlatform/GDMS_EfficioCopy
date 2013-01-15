<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GDMS</title>

</head>
<body>
<form method="post">
<center>
<%if(session.getAttribute("user")==null){
//System.out.println("In Header.jsp USER="+session.getAttribute("user"));
%> 
	<img src="../Images/GDMS_1.gif" border=0 usemap="#Map2">
		<map name="Map2">		
			<area shape="rect" coords="11,90,65,108" href="../../" alt="Home" target="_parent">
			<area shape="rect" coords="88,90,138,108" href="../../jsp/common/About.html" alt="About" target="new">
		 	<area shape="rect" coords="171,90,231,108" href="../../jsp/common/GDMSLayout.jsp?str=upload" alt="Upload" target="_parent">
		  	<area shape="rect" coords="253,90,320,108" href="../../jsp/common/GDMSLayout.jsp?str=retrieve" alt="Retrieve" target="_parent">
		  	<area shape="rect" coords="346,90,400,108" href="../../jsp/common/GDMSLayout.jsp?str=delete" alt="Retrieve" target="_parent">
		</map>
<%}else{%>
	<img src="../Images/GDMS_2.gif" border=0 usemap="#Map3">
		<map name="Map3">
		 	<area shape="rect" coords="11,90,65,108" href="../../" alt="Home" target="_parent">
			<area shape="rect" coords="88,90,138,108" href="../../jsp/common/About.html" alt="About" target="new">
		 	<area shape="rect" coords="171,90,231,108" href="../../jsp/common/GDMSLayout.jsp?str=upload" alt="Upload" target="_parent">
		  	<area shape="rect" coords="253,90,320,108" href="../../jsp/common/GDMSLayout.jsp?str=retrieve" alt="Retrieve" target="_parent">
		  	<area shape="rect" coords="346,90,400,108" href="../../jsp/common/GDMSLayout.jsp?str=delete" alt="Retrieve" target="_parent">
	  		<area shape="rect" coords="750,88,830,108" href="../../jsp/common/URLtoAction.jsp?str=logout" alt="Logout" target="_parent">	
	  	</map>

<%} %>

	</center>
	</form>
</body>
</html>