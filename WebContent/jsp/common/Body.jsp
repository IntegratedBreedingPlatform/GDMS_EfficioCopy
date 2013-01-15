<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>GDMS</title>
	<LINK REL="stylesheet" HREF="../common/GDMSStyleSheet.css" TYPE="text/css">
	<style>
body
 {
 background-image:url('jsp/Images/1.jpg');

 }
 </style>
</head>
	<body>
	<center>
		<p class="heading"><b>Welcome </b></p><br>
			<table border=0 width="90%" cellpadding="2" cellspacing="2" align="center">
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
						
		  				<p align="justify"><font face="verdana" size=2>The <b>Genotyping Data Management System</b> aims to provide a comprehensive public repository for genotype, linkage map and QTL data from crop species relevant in the semi-arid tropics.</font></p>
		 				<p align="justify"><font face="verdana" size=2>This system is developed in java and the database is MySQL. The initial release records details of current genotype datasets generated for GCP mandate crops along with details of molecular markers and related metadata. The Retrieve tab on banner is a good starting point to browse or query the database contents. The datasets available for each crop species can be queried. Access to datasets requires user login. <br>
		 				<br>Data may be currently exported to the following formats: 2x2 matrix and flapjack software formats. Data submission is through templates; upload templates are available for genotype, QTL and map data (type of markers - SSR, SNP and DArT). The templates are in the form of excel sheets with built-in validation functions.</font></p> 
		 			</td>	
		 			<td rowspan=6 width="4%" ><center>
						<img src="jsp/Images/divider.gif"></center>
					</td>
					<td rowspan=6 valign="top" align="center" width="25%">
						<p align="left" class="displayText">Crop specific GDMS is Currently available for the following species.</p>
						<table border=1 align="left" cellpadding=5 cellspacing=0 >
	  						<Tr>
	  							<td>
	  								<div align="center">
	  									<a href="javascript:check('chickpea')">
	  										<img src="jsp/Images/Chickpeaicon.gif" alt="Chickpea" border=0>
	  									</a>
	  								</div>
	  							</td>
	  							<td class="displayText">
	  								<div align="left"><strong>
	  									<a href="javascript:check('chickpea')" class="link2">Chickpea</a>
	  								</strong></div>
	  							</td>
	  						</Tr>
	  						<Tr>
	  							<td>
	  								<div align="center">
	  									<a href="javascript:check('groundnut')">
	  										<img src="jsp/Images/Groundnuticon.gif" alt="Groundnut" border=0>
	  									</a>
	  								</div>
	  							</td>
	  							<td class="displayText">
	  								<div align="left"><strong>
	  									<a href="javascript:check('groundnut')" class="link2">Groundnut</a>
	  								</strong></div>
	  							</td>
	  						</Tr>

	  						<Tr>
	  							<td>
	  								<div align="center">
	  									<a href="javascript:check('cowpea')">
	  										<img src="jsp/Images/Cowpeaicon.gif" alt="Cowpea" border=0>
	  									</a>
	  								</div>
	  							</td>
	  							<td class="displayText">
	  								<div align="left"><strong>
	  									<a href="javascript:check('cowpea')" class="link2">Cowpea</a>
	  								</strong></div>
	  							</td>
	  						</Tr>
	  						<Tr>
	  							<td>
	  								<div align="center">
	  									<a href="javascript:check('commonbean')">
	  										<img src="jsp/Images/Commonbeanicon.gif" alt="CommonBean" border=0>
	  									</a>
	  								</div>
	  							</td>
	  							<td class="displayText">
	  								<div align="left"><strong>
	  									<a href="javascript:check('commonbean')" class="link2">CommonBean</a>
	  								</strong></div>
	  							</td>
	  						</tr>
	  						</table>
	  						</td>				
				</tr>					
			</table>				
			<br><br>
			
</center>
		
</body>
</html>