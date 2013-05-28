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
		<%
			String path="";
			//System.out.println("...............:"+request.getSession().getAttribute("exop"));
			String option=request.getSession().getAttribute("exop").toString();
			if(option.equalsIgnoreCase("MT"))
				path="MarkerTraitFiles/MarkerTrait"+session.getAttribute("msec")+".xls";
			else
				path="InputFormats/KBio"+session.getAttribute("msec")+".xls";
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
							<td colspan="2"><input type="button" name="Back" value=" Back "  styleClass="button" onclick="javascript:history.back()"/></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html:html>
	