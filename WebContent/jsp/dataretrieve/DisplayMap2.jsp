<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="java.util.*" %>

<html:html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">		
	</head>
	<body>
	<html:form action="/retrieveMap.do" method="post">
		<%
			String path="";
			//System.out.println("...............:"+request.getSession().getAttribute("exop"));
			String option=request.getSession().getAttribute("exop").toString();
			if(option.equalsIgnoreCase("MT"))
				path="MarkerTraitFiles/MarkerTrait"+session.getAttribute("msec")+".xls";
			else
				path="K-bioOrderForms/KBio"+session.getAttribute("msec")+".xls";
		%>
		<table border=0 cellpadding=0 cellspacing=0 width="75%" align="center">
			<tr><td colspan="2">&nbsp;</td></tr>
			<tr><td colspan="2">&nbsp;</td></tr>
			<tr><td colspan="2">&nbsp;</td></tr>
			<tr>
				<td>
					<table border=0 width="75%" bordercolor="black" cellpadding=2 cellspacing=2 bgcolor="white" align=center>
						<tr><td colspan="2">&nbsp;</td></tr>
						<tr>
							<td width="15%" align=right>
								<img src="jsp/Images/bullet2.gif"  border=0 >
							</td>
							<td align="left"> 												
								<a href=<%=path %> target="_blank" class="link2">Download</a>													
							</td>
						</tr>	
						<tr><td colspan="2">&nbsp;</td></tr>
						<tr><td colspan="2">&nbsp;</td></tr>
						<tr align="center">
						<%if((request.getParameter("fromPage").equalsIgnoreCase("markers"))||(request.getParameter("fromPage").equalsIgnoreCase("poly"))){%>
							<td colspan="2"><input type="button" name="Back" value=" Back "  styleClass="button" onclick="javascript:history.back()"/></td>						
						<%}else{ %>
							<td colspan="2"><input type="button" name="Back" value=" Back "  styleClass="button" onclick="earlierPage()"/></td>
							
						<%
						ArrayList dataL=new ArrayList();
						dataL=(ArrayList)request.getSession().getAttribute("resultM");
						request.getSession().setAttribute("result",dataL);
						} %>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		
		<html:hidden property="fromPage" value='<%=request.getParameter("fromPage")%>'/>
		<%if(option.equalsIgnoreCase("MT")){%>
			<html:hidden property="maps" value='<%=session.getAttribute("maps").toString()%>'/>
		<% }%>
		</html:form>
	</body>
</html:html>
<script>
function earlierPage(){
	//alert(document.forms[0].elements['fromPage'].value);
	var map=document.forms[0].elements['fromPage'].value;	
	document.forms[0].action="retrieveMap.do?"+map;	
	document.forms[0].submit();
}

</script>	