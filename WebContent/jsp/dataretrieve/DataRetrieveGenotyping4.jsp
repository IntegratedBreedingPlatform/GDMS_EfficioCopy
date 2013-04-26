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
<html:form action="/retrieveQTLs.do" method="post">
	<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
	--><%	
		request.getSession().removeAttribute("indErrMsg");
	
		
	 String[] strArg=session.getAttribute("strdata").toString().split(";;;");%>
	 <br>
	 <br>
	 <table border=1  style="font-size:11" cellpadding=4 cellspacing=1 width="80%" bordercolor="#006633" align="center">
		<tr bgcolor="#006633" class="displayHeadingBoldText"><td nowrap="nowrap">QTL Name</td><td nowrap="nowrap">Map Name</td><td nowrap="nowrap">Chromosome</td><td nowrap="nowrap">Min Position</td><td nowrap="nowrap">Max Position</td><td nowrap="nowrap">Trait</td><td nowrap="nowrap">Experiment</td><td nowrap="nowrap">LFM</td><td nowrap="nowrap">RFM</td><td nowrap="nowrap">Effect</td><td nowrap="nowrap">LOD</td><td nowrap="nowrap">R Square</td><td nowrap="nowrap">Interactions</td><td nowrap="nowrap">Visualize</td></tr>
	 	<%
	 	String cpath="";
	 	String OntPath="";
	 	
	 	for(int t=0;t<strArg.length;t++){
	 	 	String[] arg=strArg[t].split("!~!");	 	 	
	 	 	String args=arg[0]+"!~!"+arg[1]+"!~!"+arg[2]+"!~!"+arg[3]+"!~!"+arg[4];
	 	 	
	 	 	String path="retrieveQTLs.do?str="+args;
	 	 	cpath="http://cmap.icrisat.ac.in/cgi-bin/cmap_public/feature_search?features="+arg[0]+"&search_field=feature_name&order_by=&data_source=CMAP_PUBLIC&submit=Submit";
	 	 	OntPath="http://www.cropontology.org/terms/"+arg[14]+"/"+arg[13];
	  	%>
		 	<tr class="displayText">		 	
		 		<td nowrap="nowrap">&nbsp;<a href='<%=path%>' target="new"><%=arg[0]%></a></td>
		 		<td nowrap="nowrap">&nbsp;<%=arg[1] %></td>
		 		<td nowrap="nowrap">&nbsp;<%=arg[2] %></td><td nowrap="nowrap">&nbsp;<%=arg[3] %></td>
		 		<td nowrap="nowrap">&nbsp;<%=arg[4] %></td>
		 		
		 		<%if(!(arg[14].toString().equals(" "))){ %>
		 		<td nowrap="nowrap">&nbsp;<a href='<%=OntPath%>' target="new"><%=arg[5] %></a></td>
		 		<%}else{ %>
		 		<td nowrap="nowrap">&nbsp;<%=arg[5] %></td>
		 		<%} %>
		 		
		 		
		 		<td nowrap="nowrap">&nbsp;<%=arg[6] %></td>
		 		<td nowrap="nowrap">&nbsp;<%=arg[7] %></td><td nowrap="nowrap">&nbsp;<%=arg[8] %></td>
		 		<td nowrap="nowrap">&nbsp;<%=arg[9] %></td><td nowrap="nowrap">&nbsp;<%=arg[10] %></td>
		 		<td nowrap="nowrap">&nbsp;<%=arg[12] %></td><td nowrap="nowrap">&nbsp;<%=arg[11] %></td>
		 		<td nowrap="nowrap">&nbsp;<a href='<%=cpath%>' target="new">CMap</a></td>	 		
		 	</tr>	 	
	 	<%} %>
	 </table>	
 	<br><br>
 	<center>
 	<html:button property="backButton" value="Back" onclick="retBack()"/>
 	</center>
 	</html:form>
</body>	
</html:html>
<script>
function retBack(){	
	document.forms[0].action="dataretrieval.do?retrieveOP=first";		
	document.forms[0].submit();	
}
</script>
