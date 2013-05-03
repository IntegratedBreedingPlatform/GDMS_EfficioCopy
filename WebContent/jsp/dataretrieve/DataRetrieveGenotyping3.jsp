<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.*" %>
<html:html>
	<head>
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
	</head>
	<body onload="pageRefresh()">
		<html:form action="/export.do" method="post">
			<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
			--><br>
			<% 
				ArrayList dataList=null;
				int totalItemcount=0;
				if(request.getQueryString().equalsIgnoreCase("first")){ 
					String opType=session.getAttribute("type").toString();
					System.out.println("opType="+opType);
					if(opType.equals("markers")){				
						dataList=(ArrayList)session.getAttribute("markerList");
						totalItemcount=Integer.parseInt(session.getAttribute("MarkerCount").toString());
					}else{
						dataList=(ArrayList)session.getAttribute("AccListFinal"); 
						totalItemcount=Integer.parseInt(session.getAttribute("GenoCount").toString());
					}
					int genoCount=0;
					int colCount=0;
					String str="";
						
					String option=session.getAttribute("op").toString();
					if(option.equalsIgnoreCase("Get Markers"))
						str="Markers";
					else
						str="GermplasmName";
					
					genoCount=(totalItemcount/30);
					if(genoCount*30<totalItemcount){
						genoCount=genoCount+1;	
					} 
					colCount=1;
					
					%>
					<br>
				<table border=0 width="50%" cellpadding=0 align="center">
					<tr>
						<td align="left" class="displaysmallText" colspan=2><html:checkbox property="chk" onclick="checkAllAcc();" > Select All </html:checkbox></td>
						<td align="left" class="displayText" colspan=2>  Select from the list of <b><%=totalItemcount%></b>  </td>						
					</tr>
				</table>
				<table border=0 width="80%" align="center" cellpadding=10 cellspacing=0 >
				<tr><td align="center">
					
			  	<br>
					<Table border="0" cellpadding="0" cellspacing="0" align="left">
						
						<tr bgcolor="white">
						<%
							String data="";
							int rowCount=0;
							String[] args=null;
							for (int j = 0;j<totalItemcount;j++){
								data=dataList.get(j).toString();
								if(!(opType.equals("markers"))){	
									args=data.split(",");
								}
								if(rowCount==genoCount){
									rowCount=0;
									%>
									</tr><tr class='reportsBody'>
								<%}	
								if(opType.equals("markers")){%>	
									<td nowrap class="displaysmallText" ><input type="checkbox" name="McheckGroup" value="<%=dataList.get(j)%>"><%=dataList.get(j)%></td>
								<%}else{ %>	
									<td nowrap class="displaysmallText" ><input type="checkbox" name="McheckGroup" value="<%=args[1]%>"><%=args[0]%></td>
				 				<%} %>
								<%rowCount++;
							} %>
						</tr>
				  </table>
					</td>
				  </tr>
				  <tr>
				  
				  	<br>
				 	<table border=0 align=center width=77%><tr><td class="displayBoldText" align=left>Choose Data Export Format You Would Like to View</td></tr></table>
					   	 <table align="center" width=77% border=0>
					    	 <tr>
						    	 <td width=1%><html:radio  property="FormatcheckGroup" value="Genotyping X Marker Matrix" onclick="selOpt(this)"/></td>
						    	 <td class="QrytextColor" align=left>Genotyping X Marker Matrix</td>
					    	 </tr>
					    	  <tr>
						    	 <td width=1%><html:radio property="FormatcheckGroup" value="Flapjack" onclick="selOpt(this)"/></td>
						    	 <td class="QrytextColor" align="left">Flapjack</td>
						     </tr>  					    	 
						     
					    	 <tr align="left">
						    	 <td colspan=2>
						    	<%
						    	ArrayList maps=(ArrayList)request.getSession().getAttribute("mapList");
						    	
						    	
						    	%>
						    	 <span id="map" style="visibility: hidden;">
								 	<center>
										<table width=60% align="left" border=0>
										
											<tr>
												<th width=30%>Identify a column</th>
												<td width=5%>:</td>
												<td>
													<table width="100%">
														<tr class="displayText">
															<td><html:radio property="exportType" value="gid">Gid's</html:radio></td>
								    	 					<td><html:radio property="exportType" value="gname">Germplasm Name</html:radio></td>
								    	 				</tr>
								    	 			</table>
								    	 		</td>
								    	 	</tr>
								    	 	<tr><td>&nbsp;</td></tr>
											<tr>
											<%
											//System.out.println("%%%%%% "+maps);
											if(!(maps.isEmpty())){%>
												<th width="40%">Please select the map</th>
												<td width="5%" align="left">:</td>
												<td align="left"><select name="maps" id="maps" >
														<option value=""></option>
													<%for(int i=0;i<maps.size();i++){%>
														<option value="<%=maps.get(i)%>"><%=maps.get(i)%></option>				
													<%} %>
												</select></td>
												<%}else{ %>
													<th width="40%" colspan="3" align="center"><font color="red">NO Maps!!!</font> <font color="black">Please upload Map data to create Export formats for Flapjack...</font></th>
												<%} %>
											</tr>
									 	</table>
									 	
									</center>
								</span>
						    	 </td>
					    	 </tr> 	 
							</table><br>
						</tr>
					</table></td>
				</tr>
				</table>
				<html:hidden property="exportTypeH"/>	
				<html:hidden property="opType" value="nonDataset"/> 	
				<html:hidden property="str" value="<%=str%>"/>
				<center><html:button property="export" value="Submit" onclick="sub('<%=str%>')" />&nbsp;<input type="button" name="Back" value=" Back " onclick="javascript:history.back()"/></center>
				<input type="hidden" name="selectListN"> 	
		  		<input type="hidden" name="markersSel">
		  		
	  		<%} else if(request.getQueryString().equalsIgnoreCase("second")){  %>
	  			<br><br>
	  			<table border=0 cellpadding=0 cellspacing=0 width="75%" align="center">
						<tr>
							<td>
								<table border=0 width="75%" bordercolor="black" cellpadding=2 cellspacing=2 bgcolor="white" align=center>
									<tr>
									
								<%
								String path1="";
								String label1="";
								String label2="";
								String path="";
								String label3="";
								String path2="";
								
								String ExportFormats=session.getAttribute("exportFormat").toString();
								//System.out.println("  >>>>>>>>>>>>>>>  in jsp :"+ExportFormats+" (^!^)^^^^^^^^^^^  "+qtl);
								if(ExportFormats.equals("Flapjack")) {
									String qtl=session.getAttribute("qtlExistsSes").toString();
								
									label1="Flapjack data file";
									label2="Flapjack Map file";
									label3="Flapjack QTL file";
									path="Flapjack/"+"Flapjack.dat";	
									path1="Flapjack/"+"Flapjack.map";	
									path2="Flapjack/"+"Flapjack.txt";	
								
								%>
									<br><br>
									<tr>
											<td width="15%" align=right>
												<img src="jsp/Images/bullet2.gif"  border=0 >
											</td>
											<td align="left"> 												
												<a href=<%=path %> target="_blank" class="link2">												
													<b>&nbsp;&nbsp;<%=label1%></b>													
												</a>													
											</td>
										</tr>	
										<tr>
											<td width="15%" align=right>
												<img src="jsp/Images/bullet2.gif"  border=0 >
											</td>
											<td align="left"> 												
												<a href=<%=path1 %> target="_blank" class="link2">												
													<b>&nbsp;&nbsp;<%=label2%></b>													
												</a>													
											</td>
										</tr>
										<%if(qtl.equals("true")) {
										//System.out.println("QTL TRUE");
										%>
										<tr>
											<td width="15%" align=right>
												<img src="jsp/Images/bullet2.gif"  border=0 >
											</td>
											<td align="left"> 												
												<a href=<%=path2 %> target="_blank" class="link2">												
													<b>&nbsp;&nbsp;<%=label3%></b>													
												</a>													
											</td>
										</tr>
										<%} %>
										<tr><td colspan="2"> &nbsp;</td></tr>
										<tr>
											<td colspan="2" align="center"> 				
												<table width="100%" border=0>
													<tr><td align="right">
														<html:submit value="Visualize In Flapjack" property="flapjack" onclick="funcFlapjack(this.value)"/>
														</td>
														<td width="5%">&nbsp;</td>
														<td align="left"> 				
															<html:submit value="Show Similarity Matrix" property="flapjack" onclick="funcFlapjack(this.value)"/>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<html:hidden property="flapjackOp"/>
					
								<%	//System.out.println("Flapjack");
								}else if(ExportFormats.equals("Genotyping X Marker Matrix")){
									
									String type=session.getAttribute("datasetType").toString();
									//System.out.println("Type id jsp="+type);
								%>			
									<%if(session.getAttribute("op").toString().equalsIgnoreCase("dataset")){ %>
										<td colspan=2 align="center"  class="displayBoldText">
											<font color=red><%=session.getAttribute("genCount")%></font>
											 Germplasm ID(s)   
											 <font color=red> <%=session.getAttribute("mCount")%></font> 
											 Marker(s)										 
										</td>	
									<%}else{ %>
										<td colspan=2 align="center"  class="displayBoldText">
											<font color=red><%=session.getAttribute("genCount")%></font>
											 Germplasm ID(s)   
											 <font color=red> <%=session.getAttribute("mCount")%></font> 
											 Marker(s) selected 										 
										</td>
									<%} %>							
										<tr>
											<td  colspan="2" align="center">&nbsp;</td>
										</tr>
										<tr>
											<td colspan="2" align="center" class="displayBoldText" bgcolor="lightgrey">
											Data Export Formats
											</td>
										</tr>
										<%
										
										//for(int l=0;l<ExportFormats.length;l++){
											if(ExportFormats.equals("Genotyping X Marker Matrix")){
											
												//if((type.equalsIgnoreCase("SNP"))||(type.equalsIgnoreCase("SSR"))){
												if(type.equalsIgnoreCase("SNP")){
													path="./jsp/analysisfiles/matrix"+session.getAttribute("msec")+".txt";
												}else{
													path="./jsp/analysisfiles/matrix"+session.getAttribute("msec")+".xls";
												}
											}else if(ExportFormats.equals("Flapjack")){
												label1="Flapjack data file";
												label2="Flapjack Map file";
												label3="Flapjack QTL file";
												path="../.././tempfiles/Flapjack"+session.getAttribute("msec")+".dat";	
												path1="../.././tempfiles/Flapjack"+session.getAttribute("msec")+".map";	
												path2="../.././tempfiles/Flapjack"+session.getAttribute("msec")+".txt";	
											}
										%>
										<tr>
											<td colspan="2">&nbsp;</td>
										</tr>
										<tr>
											<td width="15%" align=right>
												<img src="jsp/Images/bullet2.gif"  border=0 >
											</td>
											<td align="left"> 												
												<a href=<%=path %> target="_blank" class="link2">												
													<b>&nbsp;&nbsp;<%=ExportFormats%></b>													
												</a>													
											</td>
										</tr>								
										
									<tr><td>&nbsp;</td></tr>
									<%--
									if(type.equalsIgnoreCase("mapping")){
										String mappingType=session.getAttribute("mappingType").toString();
										if(mappingType.equalsIgnoreCase("allelic")){
											%>						
											<tr align="center">
												<td colspan="2"><input type="button" value="Export to AB format" onclick="exportToABH()">
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" name="Back" value=" Back "  styleClass="button" onclick="funcBack()"/></td>
											</tr>
											
										<%}
									}else{
									
									--%>						
									<tr align="center">
										<td colspan="2"><input type="button" name="Back" value=" Back "  styleClass="button" onclick="funcBack()"/></td>
									</tr>
									<%//} %>
								</table>
							</td>
							
						</tr>
						<%}else{
							//int c=Integer.parseInt(session.getAttribute("count").toString());
							String[] files1=session.getAttribute("f1").toString().split(";;");%>
							<tr>
								<td colspan="2" align="center" class="displayBoldText" bgcolor="lightgrey">Data Export Formats</td>
							</tr>
							<%for(int f=0;f<files1.length;f++){
								String[] files=files1[f].split("!~!");
							%>							
								<%path="./jsp/analysisfiles/"+files[1]+"CMTV.txt";%>
								<tr><td colspan="2">&nbsp;</td></tr>
								<tr>
									<td width="15%" align=right>
										<img src="jsp/Images/bullet2.gif"  border=0 >
									</td>
									<td align="left"> 												
										<a href=<%=path %> target="_blank" class="link2">												
											Download <b>&nbsp;&nbsp;<%=ExportFormats%> </b>	&nbsp;input file of map <b><%=files[0]%></b>												
										</a>													
									</td>
								</tr>								
							<%}%>	
							<tr><td>&nbsp;</td></tr>	
							<tr><td>&nbsp;</td></tr>					
							<tr align="center">
								<td colspan=2>
								<%--<input type=submit value="View in CMTV" onclick="funcFlapjack(this.value)">&nbsp;&nbsp;--%>
								<input type="button" name="Back" value=" Back " onclick="javascript:history.back()"/></td>
							</tr>
						<%} %>
					</table>	  		
	  		<%}%>
	  		<html:hidden property="reportType" value="genotyping"/>
	  		</center>
		</html:form>	
	</body>
</html:html>
<script>
function exportToABH(){
	var datasetId='<%=session.getAttribute("dataset")%>';
	document.forms[0].action="exportabh.do?arg="+datasetId;
	document.forms[0].submit();	
}

function pageRefresh(){
	var op='<%=request.getQueryString()%>';
	//alert(op);
	if(op=="first"){
		var radList = document.getElementsByName('FormatcheckGroup');
		for (var i = 0; i < radList.length; i++) {
			if(radList[i].checked) radList[i].checked = false;
		}
	}
	
}
function checkAllAcc(){
	var len=document.forms[0].McheckGroup.length;
	var temp="";
	c=0;
	if(document.forms[0].chk.checked == true){
		document.forms[0].chk.value="checked";
		for(i=0;i<len;i++){
			document.forms[0].McheckGroup[i].checked=true;
			c++;
			//alert("Marker at "+k+"="+document.retDisp.McheckGroup[k].value);
			temp=temp+document.forms[0].McheckGroup[i].value+";;";
		}	 
	}else{
		for(i=0;i<len;i++){
	 		document.forms[0].McheckGroup[i].checked=false;
	 	}
	}	
	document.forms[0].markersSel.value=temp;
}
function selOpt(opt){
	var check=opt.value;
	if(check=="Flapjack"){				
		document.getElementById('map').style.visibility='visible';		
	}else{
		document.getElementById('map').style.visibility='hidden';		
	}	
}
function funcFlapjack(a){
	//alert(a);
	document.forms[0].flapjackOp.value=a;
	document.forms[0].action="jsp/dataretrieve/Progress.jsp";
		
}
function funcBack(){
	document.forms[0].action="genotypingpage.do?second";
	document.forms[0].submit();	
}
function funcBackCMTV(){
	document.forms[0].action="jsp/dataretrieve/DataRetrieve1.jsp";
	document.forms[0].submit();	
}
function sub(type){
	//alert(document.forms[0].str.value);
	var op=document.forms[0].str.value;
	var selType="";
	var mapName="";
	if(type=="map"){
		var len=document.forms[0].maps.length;
		alert(len);
		var temp="";
		c=0;	
		for(k=0;k<len;k++){
			alert(document.forms[0].maps[k].checked);
			if(document.forms[0].maps[k].checked==true){
				c++;
				temp=temp+"'"+document.forms[0].maps[k].value+"',";
			}
		}
		document.forms[0].selMaps.value=temp;
		
	}else{
		var len=document.forms[0].McheckGroup.length;
		var temp="";
		c=0;
		var temp1="";
		c1=0;	
		for(k=0;k<len;k++){
			if(document.forms[0].McheckGroup[k].checked==true){
				c++;
				temp=temp+document.forms[0].McheckGroup[k].value+";;";
			}
		}
		//alert(temp);
		if(temp==""){
			alert("Please Select " + op);
			return false;
		}
		var len1=document.forms[0].FormatcheckGroup.length;
		for(k=0;k<len1;k++){
			if(document.forms[0].FormatcheckGroup[k].checked==true){
				c1++;
				temp1=document.forms[0].FormatcheckGroup[k].value;			
			}
		}
		//alert(temp1);
		if(c1==0){
			alert("Please Select Export Format");
			return false;
		}
		
		
		document.forms[0].markersSel.value=temp;
		if(temp!="" && c1>0){
			if(temp1=="Flapjack"){
				//alert(document.forms[0].maps.value);
				var expTypeValue="";
				//if(document.forms[0].exportType)
				var lenE=document.forms[0].exportType.length;
				for(e=0;e<lenE;e++){
					if(document.forms[0].exportType[e].checked==true){
						c++;
						expTypeValue=document.forms[0].exportType[e].value;			
					}
				}
				//alert(expTypeValue+"   "+c)
				if(c==0){
					alert("Please mention whether to write gids or germplasm name to the data file for flapjack");
					return false;
				}else{
					document.forms[0].exportTypeH.value=expTypeValue;
				}
				
				
				if(document.forms[0].maps.value==""){
					alert("Please select the Map");
				}else{
					document.forms[0].action="export.do";
					document.forms[0].submit();
				}
			}else{
				document.forms[0].action="export.do";
				document.forms[0].submit();
			}
		}
		
	}	
}



</script>
