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
		<script>
			function msg(){	
				<%
				String strValue = "";				
				String strResult = (String)request.getSession().getAttribute("indErrMsg");
				String strResult1 = request.getQueryString();
				
				if(strResult == null)
					strResult = "";
				
					
				if(strResult.equals("ErrMsg")){
					strValue = (String)	session.getAttribute("indErrMsg");
					strResult = "Error : "+strValue +".";
				}
				%>
				if(document.forms[0].elements['hResult'].value != ""){
					alert(document.forms[0].elements['hResult'].value);					
					document.forms[0].qtl.focus();
					document.forms[0].qtl.value="";
				}
				<%
				
				session.removeAttribute("indErrMsg");
				%>
			}
		</script>
	</head>
	
	<body onload="msg();">
		<html:form action="/retrieveMap.do">
			
			<input type=hidden name="hResult" value='<%=strResult %>'>
			<%
			String data=session.getAttribute("data").toString();
			int dataType=Integer.parseInt(session.getAttribute("dataTypes").toString());
			if(dataType==1){			
			%>		
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
	  				
	  				int mrowCount=0;
	  				int mcolCount=0;
	  				genoCount=(totalItemcount/30);
	  				if(genoCount*30<totalItemcount){
	  					genoCount=genoCount+1;	
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
									missingCount=(missCount/30);
					  				if(missingCount*30<missCount){
					  					missingCount=missingCount+1;
					  				}
					  				
					  				mcolCount=1;
									//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
										ArrayList missingMarkers=(ArrayList)session.getAttribute("MissingData");
										mcount=totalItemcount;	
										for (int j = 0;j<missCount;j++){
											if(mrowCount==missingCount){
												mrowCount=0;%>
												<tr class="displaysmallText">
											<%}	%>															
											<td class="displaysmallText">&nbsp;<%=missingMarkers.get(j)%>&nbsp;</td>											
											<%mrowCount++;										
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
					  	<%ArrayList mList=(ArrayList)session.getAttribute("maps"); 
							for(int d=0;d<mList.size();d++){
								String[] dataM=mList.get(d).toString().split("!~!");								
							%>
					  		<html:option value="<%=dataM[1] %>" ><%=dataM[0]%></html:option>
					   	<%} %>		
					</html:select>
				</logic:notEmpty>
				<logic:empty name="maps">
					<html:hidden property="mapsH" value="noMaps"/>
				</logic:empty>
				<br><br>
			<center>
 				<html:button property="backButton" value="Back" onclick="javascript:history.back()"/>
 				<logic:notEmpty name="maps">
 				<html:button property="nextButton" onclick="funcSubmitPage(this.value)" value="View On Map"/>
 				</logic:notEmpty>
 				<html:button property="nButton" onclick="funcSubmitPage(this.value)" value="Create KBio Order Form"/>
 			</center>
 			</center>
 			
 			</logic:notEmpty>
 			<logic:empty name="result">
 			<br>
			<center>
 				<table border=0 width="60%" cellpadding=0>		
					<tr><td align="center" class="displayText" colspan=2> <b>'<%=session.getAttribute("recCount")%>'</b> markers are polymorphic between <b><%=session.getAttribute("selLines") %></b> </td>
						<td align="left" class="displaysmallText" colspan=2> </td>
					</tr>
				</table>
 				<logic:notEmpty name="MissingData">
 				<%

  				int mrowCount=0;
  				int mcolCount=0;
 				int missingCount=0;
 				int missCount=Integer.parseInt(session.getAttribute("missingCount").toString());
 				int genoCount=0;
  				int mcount=0;
  				missingCount=(missCount/30);
  				//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
 				%>
 				<br><br>
 				<table border=0>
						<tr bgcolor="#006633" ><td nowrap align="center" class="displaysmallwhiteboldText">Markers with missing data(<%=session.getAttribute("missingCount")%>)</td></tr>
									
						<tr><td><table border=1 align="center" width="100%">
						
									<%
									missingCount=(missCount/30);
					  				if(missingCount*30<missCount){
					  					missingCount=missingCount+1;
					  				}
					  				
					  				mcolCount=1;
									//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
										ArrayList missingMarkers=(ArrayList)session.getAttribute("MissingData");
										mcount=missCount;	
										for (int j = 0;j<missCount;j++){
											if(mrowCount==missingCount){
												mrowCount=0;%>
												<tr class="displaysmallText">
											<%}	%>															
											<td class="displaysmallText">&nbsp;<%=missingMarkers.get(j)%>&nbsp;</td>											
											<%mrowCount++;										
										}
								//}%>			
							
						</tr>
						</table>
						</td></tr>
					</table>
					
 				</logic:notEmpty>
 				<center>
					<br><br>
 				<html:button property="backButton" value="Back" onclick="javascript:history.back()"/>
 				
 				</center>
 			</logic:empty>
 			<%}else{ %>
 			<logic:notEmpty name="ssrResult">
			<br>
			<center>
				<table border=0 width="60%" cellpadding=0>		
					<tr><td align="center" class="displayText" colspan=2> <b>'<%=session.getAttribute("ssrRecCount")%>'</b>  SSR/DArT markers are polymorphic between <b><%=session.getAttribute("selLines") %></b> </td>
						<td align="left" class="displaysmallText" colspan=2> </td>
					</tr>
				</table>
				
	  			<br>
	  			<%
	  				int missingCount=0;
	  				
	  				int mcount=0;
		  			int totalItemcount=Integer.parseInt(session.getAttribute("ssrRecCount").toString());
		  			int missCount=Integer.parseInt(session.getAttribute("missingCount").toString());
	  				int genoCount=0;
	  				int colCount=0;
	  				int rowCount=0;
	  				
	  				int mrowCount=0;
	  				int mcolCount=0;
	  				genoCount=(totalItemcount/30);
	  				if(genoCount*30<totalItemcount){
	  					genoCount=genoCount+1;	
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
										ArrayList markers=(ArrayList)session.getAttribute("ssrResult");										
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
 				<%

  				mrowCount=0;
  				 mcolCount=0;
 				missingCount=0;
 				 missCount=Integer.parseInt(session.getAttribute("missingCount").toString());
 				genoCount=0;
  				mcount=0;
  				missingCount=(missCount/30);
  				//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
 				%>
 				<br><br>
 				<table border=0>
						<tr bgcolor="#006633" ><td nowrap align="center" class="displaysmallwhiteboldText">Markers with missing data(<%=session.getAttribute("missingCount")%>)</td></tr>
									
						<tr><td><table border=1 align="center" width="100%">
						
									<%
									missingCount=(missCount/30);
					  				if(missingCount*30<missCount){
					  					missingCount=missingCount+1;
					  				}
					  				
					  				mcolCount=1;
									//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
										ArrayList missingMarkers=(ArrayList)session.getAttribute("MissingData");
										mcount=missCount;	
										for (int j = 0;j<missCount;j++){
											if(mrowCount==missingCount){
												mrowCount=0;%>
												<tr class="displaysmallText">
											<%}	%>															
											<td class="displaysmallText">&nbsp;<%=missingMarkers.get(j)%>&nbsp;</td>											
											<%mrowCount++;										
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
				<br>
				
				<br>
			<center>
 				<html:button property="backButton" value="Back" onclick="javascript:history.back()"/>
 				<logic:notEmpty name="maps">
 				<html:button property="nextButton" onclick="funcSubmitPage(this.value)" value="View On Map"/>
 				</logic:notEmpty>
 				<html:button property="nButton" onclick="funcSubmitPage(this.value)" value="Create KBio Order Form"/>
 			</center>
 			</center>
 			
 			</logic:notEmpty>
 			<logic:empty name="ssrResult">
 			<br>
			<center>
 				<table border=0 width="60%" cellpadding=0>		
					<tr><td align="center" class="displayText" colspan=2> <b>'<%=session.getAttribute("ssrRecCount")%>'</b> SSR/DArT markers are polymorphic between <b><%=session.getAttribute("selLines") %></b> </td>
						<td align="left" class="displaysmallText" colspan=2> </td>
					</tr>
				</table>
 				<logic:notEmpty name="MissingData">
 				<%

  				int mrowCount=0;
  				int mcolCount=0;
 				int missingCount=0;
 				int missCount=Integer.parseInt(session.getAttribute("missingCount").toString());
 				int genoCount=0;
  				int mcount=0;
  				missingCount=(missCount/30);
  				//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
 				%>
 				<br><br>
 				<table border=0>
						<tr bgcolor="#006633" ><td nowrap align="center" class="displaysmallwhiteboldText">Markers with missing data(<%=session.getAttribute("missingCount")%>)</td></tr>
									
						<tr><td><table border=1 align="center" width="100%">
						
									<%
									missingCount=(missCount/30);
					  				if(missingCount*30<missCount){
					  					missingCount=missingCount+1;
					  				}
					  				
					  				mcolCount=1;
									//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
										ArrayList missingMarkers=(ArrayList)session.getAttribute("MissingData");
										mcount=missCount;	
										for (int j = 0;j<missCount;j++){
											if(mrowCount==missingCount){
												mrowCount=0;%>
												<tr class="displaysmallText">
											<%}	%>															
											<td class="displaysmallText">&nbsp;<%=missingMarkers.get(j)%>&nbsp;</td>											
											<%mrowCount++;										
										}
								//}%>			
							
						</tr>
						</table>
						</td></tr>
					</table>
					
 				</logic:notEmpty>
 				<center>
					<br><br>
 				<html:button property="backButton" value="Back" onclick="javascript:history.back()"/>
 				
 				</center>
 			</logic:empty>
 			
 			<logic:notEmpty name="snpResult">
			<br>
			<center>
				<table border=0 width="60%" cellpadding=0>		
					<tr><td align="center" class="displayText" colspan=2> <b>'<%=session.getAttribute("snpRecCount")%>'</b> SNP markers are polymorphic between <b><%=session.getAttribute("selLines") %></b> </td>
						<td align="left" class="displaysmallText" colspan=2> </td>
					</tr>
				</table>
				
	  			<br>
	  			<%
	  				int missingCount=0;
	  				
	  				int mcount=0;
		  			int totalItemcount=Integer.parseInt(session.getAttribute("snpRecCount").toString());
		  			int missCount=Integer.parseInt(session.getAttribute("missingCount").toString());
	  				int genoCount=0;
	  				int colCount=0;
	  				int rowCount=0;
	  				
	  				int mrowCount=0;
	  				int mcolCount=0;
	  				genoCount=(totalItemcount/30);
	  				if(genoCount*30<totalItemcount){
	  					genoCount=genoCount+1;	
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
										ArrayList markers=(ArrayList)session.getAttribute("snpResult");										
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
 				<html:button property="nextButton" onclick="funcSubmitPage(this.value)" value="View On Map"/>
 				</logic:notEmpty>
 				<html:button property="nButton" onclick="funcSubmitPage(this.value)" value="Create KBio Order Form"/>
 			</center>
 			</center>
 			
 			</logic:notEmpty>
 			<logic:empty name="ssrResult">
 			<br>
			<center>
 				<table border=0 width="60%" cellpadding=0>		
					<tr><td align="center" class="displayText" colspan=2> <b>'<%=session.getAttribute("snpRecCount")%>'</b> markers are polymorphic between <b><%=session.getAttribute("selLines") %></b> </td>
						<td align="left" class="displaysmallText" colspan=2> </td>
					</tr>
				</table>
 				<logic:notEmpty name="MissingData">
 				<%

  				int mrowCount=0;
  				int mcolCount=0;
 				int missingCount=0;
 				int missCount=Integer.parseInt(session.getAttribute("missingCount").toString());
 				int genoCount=0;
  				int mcount=0;
  				missingCount=(missCount/30);
  				//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
 				%>
 				<br><br>
 				<table border=0>
						<tr bgcolor="#006633" ><td nowrap align="center" class="displaysmallwhiteboldText">Markers with missing data(<%=session.getAttribute("missingCount")%>)</td></tr>
									
						<tr><td><table border=1 align="center" width="100%">
						
									<%
									missingCount=(missCount/30);
					  				if(missingCount*30<missCount){
					  					missingCount=missingCount+1;
					  				}
					  				
					  				mcolCount=1;
									//System.out.println("missingCount="+missingCount+"   missCount=:"+missCount);
										ArrayList missingMarkers=(ArrayList)session.getAttribute("MissingData");
										mcount=missCount;	
										for (int j = 0;j<missCount;j++){
											if(mrowCount==missingCount){
												mrowCount=0;%>
												<tr class="displaysmallText">
											<%}	%>															
											<td class="displaysmallText">&nbsp;<%=missingMarkers.get(j)%>&nbsp;</td>											
											<%mrowCount++;										
										}
								//}%>			
							
						</tr>
						</table>
						</td></tr>
					</table>
					
 				</logic:notEmpty>
 				<center>
					<br><br>
 					<html:button property="backButton" value="Back" onclick="javascript:history.back()"/> 				
 				</center>
 			</logic:empty>	
 			
 			<%} %>
 			<html:hidden property="fromPage" />
		</html:form>
	</body>
</html:html>
<script>
function funcSubmitPage(val){
	if(val=="View On Map"){
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
	}else if(val=="Create KBio Order Form"){
		//alert('<%=session.getAttribute("result")%>')	
		document.forms[0].elements['fromPage'].value='poly';
		document.forms[0].action="exportKBIOFile.do?poly";
		document.forms[0].submit();
	}
}
</script>