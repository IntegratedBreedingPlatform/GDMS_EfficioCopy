<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<html:html>
	<head>		
		<title>GDMS</title>	
		<LINK REL="stylesheet" HREF="jsp/common/GDMSStyleSheet.css" TYPE="text/css">
			
		<script>
		function pageRefresh(){
			document.forms[0].uname.focus();
		}
		
		</script>
	</head>
	<body onload="pageRefresh()">
	<center>
			<img src="jsp/Images/GDMS_1.gif" border=0 usemap="#Map2">
				<!--<map name="Map2">
					<area shape="rect" coords="13,90,60,109" href="../../" alt="Home" target="_top">
				 	<area shape="rect" coords="88,90,142,109" href="jsp/common/GDMSLayout.jsp?str=upload" alt="Upload" target="_top">
				  	<area shape="rect" coords="172,90,235,109" href="jsp/common/GDMSLayout.jsp?str=retrieve" alt="Retrieve" target="_top">
				  	<area shape="rect" coords="260,90,320,109" href="jsp/common/GDMSLayout.jsp?str=delete" alt="Delete" target="_top">
				</map>
		--><html:form method="post" action="/login.do">
			
			<p class="heading"><b>Welcome </b></p><br>
			<table border=0 width="68%" cellpadding="2" cellspacing="2" align="center">
				<tr>
					<td width="25%" valign="top">
						<table border=0 cellpadding="1" cellspacing="0" width="95%" style="height:4cm;">						
							<tr><td>
								<table cellspacing=5 border="0" align="center">
									<tr>
										<td nowrap valign=top class="displayText">Username:</td>
										<td width=5></td>
										<td align=left><html:text property="uname" value=""/></td>
									</tr>			
									<tr>
										<td nowrap class="displayText">Password:</td>
										<td width=5></td>
										<td align=left><html:password property="password" value=""/></td>				
									</tr>
									
									<tr>
										<td colspan="3">
											<table border=0 align="center">
												<tr>
													<td align="right"><html:submit value="Submit" property="login"/></td>
													<td>&nbsp;</td>
													<td><html:reset value="Clear" property="reset"/></td>											
												</tr>								
											</table>
										</td>								
									</tr>
								</table>
							</td>
							</tr>					
						</table>	
					</td>
					<td rowspan=6 valign="top" align="center" width="45%">
						
		  				<p align="justify"><font face="verdana" size=2>The <b>Genotyping Data Management System</b> aims to provide a comprehensive public repository for genotype, linkage map and QTL data from crop species relevant to the GCP's aim to boost crop productivity and resilience for smallholders in drought-prone environments.</font></p>
		 				<p align="justify"><font face="verdana" size=2>This system is developed in Java and the database is MySQL. The initial release records details of current genotype datasets generated for GCP mandate crops along with details of molecular markers and related metadata. The Retrieve tab on banner is a good starting point to browse or query the database contents. The datasets available for each crop species can be queried. Access to datasets requires user login. <br>
		 				<br>Data may be currently exported to the following formats: 2x2 matrix and Flapjack formats. Data submission is through templates; upload templates are available for genotype, QTL and map data (type of markers - SSR, SNP and DArT). </font></p> 
		 			</td>	
		 						
				</tr>	
							
			</table>
			
			<br>
			<html:hidden property="crop"/>
		</html:form>
	</body>
	<center><img src="jsp/Images/GDMS_Footer.gif" border="0" usemap="#Map3" valign="bottom">
<map name="Map3">
	<area shape="rect" coords="382,6,468,23" href="mailto:bioinformatics@cgiar.org">	
</map>
</center>
</html:html>
