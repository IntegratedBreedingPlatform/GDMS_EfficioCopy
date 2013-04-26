<html>
<head>
<title>GDMS</title>
</head>	
<%
String uploadType="no";
//System.out.println("GDMSLayout="+request.getParameter("str"));
String op=request.getParameter("str"); 
//System.out.println("GDMSLayout="+request.getParameter("str"));
request.getSession().setAttribute("uploadType", uploadType);
%>
<% if(op.equals("login")){%>
	<frameset rows="125,*,40" cols="*" BORDER=0 FRAMEBORDER=0 FRAMESPACING=0>	
		<frame src="<%=request.getContextPath() %>/jsp/common/Header.jsp" name="topFrame" scrolling="no" FRAMEBORDER=0 noresize>
 	 	<frame src="<%=request.getContextPath() %>/jsp/common/HomePage.jsp?str=login" name="mainFrame" FRAMEBORDER=0 scrolling="auto" name='res'>
 		<frame src="<%=request.getContextPath() %>/jsp/common/Footer.html" name="bottomFrame" scrolling="NO" noresize border=0 FRAMEBORDER=0>
	</frameset>
<%} if(op.equals("upload")){%>
	<frameset rows="125,*,40" cols="*" BORDER=0 FRAMEBORDER=0 FRAMESPACING=0>	
		<frame src="<%=request.getContextPath() %>/jsp/common/Header.jsp" name="topFrame" scrolling="no" FRAMEBORDER=0 noresize>
 	 	<frame src="<%=request.getContextPath() %>/jsp/dataupload/DataUpload1.jsp?first" name="mainFrame" FRAMEBORDER=0 scrolling="auto" name='res'>
 		<frame src="<%=request.getContextPath() %>/jsp/common/Footer.html" name="bottomFrame" scrolling="NO" noresize border=0 FRAMEBORDER=0>
	</frameset>
<%}if(op.equals("retrieve")){%>
	<frameset rows="125,*,40" cols="*" BORDER=0 FRAMEBORDER=0 FRAMESPACING=0>	
		<frame src="<%=request.getContextPath() %>/jsp/common/Header.jsp" name="topFrame" scrolling="no" FRAMEBORDER=0 noresize>
 	 	<frame src="<%=request.getContextPath() %>/jsp/dataretrieve/DataRetrieve1.jsp" name="mainFrame" FRAMEBORDER=0 scrolling="auto" name='res'>
 		<frame src="<%=request.getContextPath() %>/jsp/common/Footer.html" name="bottomFrame" scrolling="NO" noresize border=0 FRAMEBORDER=0>
	</frameset>
<%}if(op.equals("delete")){%>
<frameset rows="125,*,40" cols="*" BORDER=0 FRAMEBORDER=0 FRAMESPACING=0>	
<frame src="<%=request.getContextPath() %>/jsp/common/Header.jsp" name="topFrame" scrolling="no" FRAMEBORDER=0 noresize>
	<frame src="<%=request.getContextPath() %>/jsp/common/URLtoAction.jsp?str=delete" name="mainFrame" FRAMEBORDER=0 scrolling="auto" name='res'>
	<frame src="<%=request.getContextPath() %>/jsp/common/Footer.html" name="bottomFrame" scrolling="NO" noresize border=0 FRAMEBORDER=0>
</frameset>
<%}if(op.equals("logout")){ 
	session.removeAttribute("user");%>	
	<frameset rows="125,*,40" cols="*" BORDER=0 FRAMEBORDER=0 FRAMESPACING=0>	
		<frame src="<%=request.getContextPath() %>/jsp/common/Header.jsp" name="topFrame" scrolling="no" FRAMEBORDER=0 noresize>
 	 	<frame src="<%=request.getContextPath() %>/jsp/common/HomePage.jsp?str=logout" name="mainFrame" FRAMEBORDER=0 scrolling="auto" name='res'>
 		<frame src="<%=request.getContextPath() %>/jsp/common/Footer.html" name="bottomFrame" scrolling="NO" noresize border=0 FRAMEBORDER=0>
	</frameset>
<%} %>
</html>