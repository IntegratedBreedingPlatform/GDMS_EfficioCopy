<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<%@ page import="java.util.*" %>
<html:html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">	
		<script type="text/javascript" src="<%=request.getContextPath() %>/jsp/common/overlib.js"></script>
		
	</head>
	
	<body>
		<html:form action="/retrieveMap.do">
			<logic:notEmpty name="result">
			<br>
			<center>
				<table border=0 width="60%" cellpadding=0>		
					<tr><td align="center" class="displayText" colspan=2> <b>'<%=session.getAttribute("recCount")%>'</b> markers are polymorphic between <b><%=session.getAttribute("selLines") %></b> </td>
						<td align="left" class="displaysmallText" colspan=2> </td>
					</tr>
				</table>
				
	  			<br>
	  			<%
	  				int missingCount=0;
	  				
	  				int mcount=0;
		  			int totalItemcount=Integer.parseInt(session.getAttribute("recCount").toString());
		  			int missCount=Integer.parseInt(session.getAttribute("missingCount").toString());
	  				int genoCount=0;
	  				int colCount=0;
	  				int rowCount=0;
	  				genoCount=(totalItemcount/30);
	  				if(genoCount*30<totalItemcount){
	  					genoCount=genoCount+1;	
	  				} 
	  				
	  				missingCount=(missCount/30);
	  				if(missingCount*30<missCount){
	  					missingCount=missingCount+1;
	  				}
	  				
	  				colCount=1;
	  				String[] mD=null;
		  			String mapData="";		  			
		  			mcount=totalItemcount;		
	  				
	  			%>
	  			<table width="50%" border=0>
					<tr valign="top" style="font-size: small;">
						<td width="50%" >
							<table border="1" width="50%" align="center"><tr bgcolor="#006633">	
								
									<%for(int p=0;p<genoCount;p++){ %>					
										<td nowrap align="center" class="displaysmallwhiteboldText">Marker</td>
									<%} %>	
									</tr>	
									<%
										ArrayList markers=(ArrayList)session.getAttribute("result");										
										for (int j = 0;j<totalItemcount;j++){
											String path="retrieveMap.do?str="+markers.get(j);
											if(rowCount==genoCount){
												rowCount=0;%>
												</tr><tr class="displaysmallText">
											<%}	%>															
											<%--<td class="displaysmallText"><a href="javascript:anchor_test('<%=markers.get(j)%>')"><%=markers.get(j)%></a></td>
												<td class="displaysmallText"><a href='<%=path%>'><%=markers.get(j)%></a></td>--%>
												<td class="displaysmallText"><%=markers.get(j)%></td>
																					
											<%rowCount++;										
										}
								//}%>									
								</tr>		
							</table>
						</td>					
					</tr>
				</table>
				<br>
				<logic:notEmpty name="MissingData">
					<table border=0>
						<tr bgcolor="#006633" ><td nowrap align="center" class="displaysmallwhiteboldText">Markers with missing data(<%=session.getAttribute("missingCount")%>)</td></tr>
									
						<tr><td><table border=1 align="center" width="100%">
						
									<%
										ArrayList missingMarkers=(ArrayList)session.getAttribute("MissingData");										
										for (int j = 0;j<missCount;j++){
											if(rowCount==missingCount){
												rowCount=0;%>
												<tr class="displaysmallText">
											<%}	%>															
											<td class="displaysmallText">&nbsp;<%=missingMarkers.get(j)%>&nbsp;</td>											
											<%rowCount++;										
										}
								//}%>			
							
						</tr>
						</table>
						</td></tr>
					</table>
				</logic:notEmpty>
				<br><br>
				<logic:notEmpty name="maps">
					<html:hidden property="mapsH" value="yes"/>
					<html:select property="maps">
						<html:option value="">-- select Map --</html:option>
					  	<logic:iterate name="maps" id="maps" type="java.lang.String">
					  		<html:option value="<%=maps %>" />
					   	</logic:iterate>
					</html:select>
				</logic:notEmpty>
				<logic:empty name="maps">
					<html:hidden property="mapsH" value="noMaps"/>
				</logic:empty>
				<br><br>
			<center>
 				<html:button property="backButton" value="Back" onclick="javascript:history.back()"/>
 				<logic:notEmpty name="maps">
 				<html:button property="nextButton" onclick="funcSubmitPage()" value="View On Map"/>
 				</logic:notEmpty>
 			</center>
 			</center>
 			
 			</logic:notEmpty>
		</html:form>
	</body>
</html:html>
<script>
function funcSubmitPage(){
	if(document.forms[0].mapsH.value=="yes"){
		if(document.forms[0].maps.value==""){
			alert("Please select the Map");
		}else{
			document.forms[0].action="retrieveMap.do?polymap";
			document.forms[0].submit();
		}
	}else{
		document.forms[0].action="retrieveMap.do?polymap";
		document.forms[0].submit();
	}
}
</script>