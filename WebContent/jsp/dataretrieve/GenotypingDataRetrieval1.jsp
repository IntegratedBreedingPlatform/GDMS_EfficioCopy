
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.*" %>
<html:html>
	<head>
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<script>
			function pageRefresh(){
				var op='<%=request.getQueryString()%>';
				//alert(op);
				if(op=="first"){
					var radList = document.getElementsByName('retrievalType');
					for (var i = 0; i < radList.length; i++) {
						if(radList[i].checked) radList[i].checked = false;
					}
				}
				//if((op=="files")||(op=="mupl")||(op=="gupl")||(op=="dset")){
				if(op=="files"){
					var radList = document.getElementsByName('selection');
					for (var i = 0; i < radList.length; i++) {
						if(radList[i].checked) radList[i].checked = false;
					}
				}
				
				if(op=="dset"){
					var radList = document.getElementsByName('FormatcheckGroup');
					for (var i = 0; i < radList.length; i++) {
						if(radList[i].checked) radList[i].checked = false;
					}
				}
				if(op=="lines"){
					//document.forms[0].linesO.value = "- Select -";
					document.forms[0].elements['linesO'].selectedIndex=0;		
				}
				document.forms[0].qtlData.checked=false;
				document.forms[0].trait.checked=false;
				var radList = document.getElementsByName('polyType');
				for (var i = 0; i < radList.length; i++) {
					if(radList[i].checked) radList[i].checked = false;
				}
				
			}
			function msg(){				
				<%				
				String strValue = "";				
				String strResult = (String)request.getSession().getAttribute("indErrMsg");
				String strResult1 = request.getQueryString();
				if(strResult1.equals("ErrMsg")){
					strValue = (String)	session.getAttribute("indErrMsg");
					strResult = "Error : "+strValue +".";
				}
				session.removeAttribute("indErrMsg");
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
	<body onload="msg();pageRefresh();">
		<html:form action="/dataretrieval.do" method="post" enctype="multipart/form-data">
			<div class="heading" align="center">Genotyping Data Retrieval</div>
			<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
			-->
			<br><br>
			<center>
				<table width="40%" align="center" border=0>
					<%--<tr style="font-size: medium;font-weight: bold;"><td colspan=3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Retrieve</td></tr>
					<tr><td>&nbsp;</td></tr>--%>
					<tr class="displayText">
						<td width="50%" align="center"><html:radio property="retrievalType" value="Output" onclick="retrieveOPData(this)">Genotyping Matrix</html:radio></td>
						<td width="50%" align="left"><html:radio property="retrievalType" value="polymorphic" onclick="retrieveOPData(this)">Polymorphic Markers</html:radio></td>
						<%--<td width="30%" align="left"><html:radio property="retrievalType" value="QTL/Map" onclick="retrieveOPData(this)">Map/QTL Data</html:radio></td>--%>
						
					</tr>
					
				</table>
				
				<%if(request.getQueryString().equals("sec")){%>
				<br>
				
					<table width="40%" align="center" border=0>
						<tr class="displayText">
							<td align="center"><html:radio property="polyType" value="fingerprinting" onclick="retGermplasm(this)">Fingerprinting Data</html:radio></td>
							<td><html:radio property="polyType" value="mapping" onclick="retGermplasm(this)">Mapping Population</html:radio></td>
						</tr>
					</table>
				<%} %>
				<input type=hidden name="hResult" value='<%=strResult %>'>
				
				<%
				if(request.getQueryString().equals("lines")){				
				%>
				<br>
				
				<table width="40%" align="center" border=0>
					<tr class="displayText">
						<td align="center"><html:radio property="polyType" value="fingerprinting" onclick="retGermplasm(this)">Fingerprinting Data</html:radio></td>
						<td><html:radio property="polyType" value="mapping" onclick="retGermplasm(this)">Mapping Population</html:radio></td>
					</tr>
				</table><br>
				<logic:notEmpty name="listValues">
				
				<table border=0 width="70%" align="center">
					<tr class="displayText">
						<td width="50%" align="right">Please select the Lines :</td><td>&nbsp;</td>					
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr class="displayText" align="center">
						<td align="right" width="40%"><html:select property="linesO" size="5" onchange="getCorrespondingLines(this.options[this.selectedIndex].value, this.name)">
							<html:option value="">- select -</html:option>
							<logic:iterate name="listValues" id="lines" type="java.lang.String">
								<html:option value="<%=lines%>"/>
							</logic:iterate>					
							</html:select> 
						</td>
						
						<td align="left">
							<span id="linesS" style="visibility: hidden;">
							<table><tr class="displayText">
								<td width="5%" align="center">and</td> 
								<td><html:select property="linesT" size="5">											
								</html:select>
								</td></tr></table>
							</span>
						</td>
						
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr><td>&nbsp;</td></tr>
					
					<tr class="displayText" align="center">
						<td colspan="3" align="center"><span id="submitButton" style="visibility: hidden;"><html:submit property="linesSub" value="Submit" onclick="return sub('lines')"/></span></td>
					</tr>
			
				</table>
				</logic:notEmpty>
				<logic:empty name="listValues">
					<table border="0" width="40%" align="center">					
						<tr class="errorMsgs">
							<td colspan="3" align="center">No data available to check for polymorphic markers. </td>
						</tr>	
						<tr><td>&nbsp;</td></tr>
							
					</table>
				
				</logic:empty>
				<html:hidden property="polyTypeH" value=""/>
				<%} 
				if((request.getQueryString().equals("files"))||(request.getQueryString().equals("mupl"))||(request.getQueryString().equals("gupl"))||(request.getQueryString().equals("dset"))||(request.getQueryString().equals("gNupl"))){
				%>
				<br><br>
				<table border=0 width="60%" align="center">
					<tr class="displayText">
						<td width="20%" align="center"><font color="black">Retrieve using :</font></td>					
						<td width="20%" align="center"><html:radio property="selection" value="gids" onclick="selOpt(this)"/>GIDs</td>
						<td width="25%" align="center"><html:radio property="selection" value="gNames" onclick="selOpt(this)"/>GermplasmNames</td>
						<td width="20%" align="center"><html:radio property="selection" value="markers" onclick="selOpt(this)"/>Markers</td>
						<td width="25%" align="center"><html:radio property="selection" value="dataset" onclick="selOpt(this)"/>Dataset</td> 
			 												
					</tr>
				</table>
				<%}
				if(request.getQueryString().equals("gupl")){
				%>
				<br>
					 <br>
					<table width=45% align="center" border=0>
						<tr class="displayText"><td>Upload the text file with desired gids</td><td width="5%" align="left">:</td><td align="left"><html:file property="txtNameM"/></td></tr>
						<tr><td colspan="3" >&nbsp;</td></tr>
						<tr><td align="center" colspan="3"><a href="<%=request.getContextPath()%>/jsp/dataretrieve/GIDs.txt" target="new">Sample File</a></td></tr>
						<tr><td colspan="3" >&nbsp;</td></tr>
						<tr><th colspan="3" align="center">(or)</th></tr>
						<tr><td colspan="3" >&nbsp;</td></tr>
						<tr class="displayText"><td>Enter the gids seperated by Comma(,) (or) Tab (or) New Line</td><td width="5%" align="left">:</td><td align="left"><html:textarea property="gidsText" rows="3"/></td></tr>
						<tr><td colspan="3" >&nbsp;</td></tr>
						
						
						<tr><td colspan="3" align="center"><html:submit property="markersButton" value="Submit" onclick="return sub('Get Markers')"/> </td></tr>
				 	</table>
				 	<br>
				 	<html:hidden property="opTypeGids"/>
				 <%}
				if(request.getQueryString().equals("mupl")){
				%>	
				<br><br>
					<table width=50% align="center" border=0>
						<tr class="displayText"><td>Upload the text file with desired markers</th><td width="5%" align="left">:</td><td align="left"><html:file property="txtNameL"/></td></tr>
						<tr><td>&nbsp;</td></tr>
						<tr><td align="center" colspan="3"><a href="<%=request.getContextPath()%>/jsp/dataretrieve/Markers.txt" target="new">Sample File</a></td></tr>
						<tr><td colspan="3" >&nbsp;</td></tr>
						<tr><th colspan="3" align="center">(or)</th></tr>
						<tr><td colspan="3" >&nbsp;</td></tr>
						<tr class="displayText"><td>Enter the Marker names seperated by Comma(,) (or) Tab (or) New Line</td><td width="5%" align="left">:</td><td align="left"><html:textarea property="markersText" rows="3"/></td></tr>
						<tr><td colspan="3" >&nbsp;</td></tr>
						<tr><td colspan="3" align="center"><html:submit property="linesButton" value="Submit" onclick="return sub('Get Lines')"/></td></tr>
				 	</table>
				 	<br>
				 	<html:hidden property="opTypeMarkers"/>
				<%}
				if(request.getQueryString().equals("gNupl")){
				%>	
					<br><br>
						<table width=50% align="center" border=0>
							<tr class="displayText"><td>Upload the text file with germplasm names</th><td width="5%" align="left">:</td><td align="left"><html:file property="txtNameGN"/></td></tr>
							<tr><td>&nbsp;</td></tr>
							<tr><td align="center" colspan="3"><a href="<%=request.getContextPath()%>/jsp/dataretrieve/GermplasmNames.txt" target="new">Sample File</a></td></tr>
							<tr><td colspan="3" >&nbsp;</td></tr>
							<tr><th colspan="3" align="center">(or)</th></tr>
							<tr><td colspan="3" >&nbsp;</td></tr>
							<tr class="displayText"><td>Enter the Germplasm names seperated by Comma(,) (or) Tab (or) New Line</td><td width="5%" align="left">:</td><td align="left"><html:textarea property="GNamesText" rows="3"/></td></tr>
							<tr><td colspan="3" >&nbsp;</td></tr>
							<tr><td colspan="3" align="center"><html:submit property="linesButton" value="Submit" onclick="return sub('Get')"/></td></tr>
					 	</table>
					 	<br>
					 	<html:hidden property="opTypeGN"/>
				<%}
				if(request.getQueryString().equals("dset")){
				%>
				<br><br>
				 	<center>	 	
						<table width=55% align="center" border=0>
							<tr class="displayText">
								<td width="5%">&nbsp;</td>
								<td align="left" nowrap="nowrap" width="25%">Select the Dataset</td><td width="5%" align="left">:</td><td align="left">
								<html:select property="dataset" onchange='retrieveSize(this.options[this.selectedIndex].value, this.name)'>
									<html:option value=""/>
									<%ArrayList dList=(ArrayList)session.getAttribute("dataSetList"); 
									for(int d=0;d<dList.size();d++){
										String[] data=dList.get(d).toString().split("!~!");								
									%>
										<%--<logic:iterate name="dataSetList" id="dataset" type="java.lang.String">--%>
										<html:option value="<%=data[1] %>" ><%=data[0]%></html:option>
										<%--</logic:iterate> --%>		
									<%} %>			
								</html:select>
							</td></tr>
					 	</table>
					 	<br>
					 	<table border=0 align=center width=60%><tr><td class="displayBoldText" align=left>Choose Data Export Format You Would Like to View</td></tr></table>
						   	 <table align="center" width=60% border=0>
						    	 <tr>
							    	 <td width=1%><html:radio  property="FormatcheckGroup" value="Genotyping X Marker Matrix" onclick="selOpt1(this)"/></td>
							    	 <td class="QrytextColor" align=left width="40%">Genotyping X Marker Matrix</td>
							    	 <td>
							    	 	<span id="dataType" style="visibility: hidden;">
							    	 		<table>
							    	 			<tr>
							    	 				<td><html:radio property="dataType" value="allelic">Allelic </html:radio></td>
								    	 			<td><html:radio property="dataType" value="ab">AB    Format </html:radio></td>
								    	 		</tr>
							    	 		</table>
							    	 </td>
						    	 </tr>
						    	 					    	 
							     <tr>
							    	 <td width=1%><html:radio property="FormatcheckGroup" value="Flapjack" onclick="selOpt1(this)"/></td>
							    	 <td class="QrytextColor" align="left">Flapjack</td>							    	 
							     </tr>   
							    
						    	<tr align="left">
							    	 <td colspan=3>
							    	
							    	 <span id="map" style="visibility: hidden;">
							    	 <%int mcount=Integer.parseInt(session.getAttribute("mapsCount").toString()); 
							    	 System.out.println("mcount in jsp=;"+mcount);
							    	 //if(mcount>0){
							    	 %>
									 	<table width=100% align="left" border=0>
											<tr>												
												<th width="40%">Please select the map</th>
												<td width="5%" align="left">:</td>
												<td align="left"><select name="maps" id="maps" >
														<option value=""></option>														
												</select></td>
											
											</tr>
											<tr><td>&nbsp;</td></tr>
											<tr>
												<th width=30%>Identify a column</th>
												<td colspan="2">
													<table width="100%">
														<tr class="displayText">
															<td><html:radio property="exportType" value="gid">Gid's</html:radio></td>
								    	 					<td><html:radio property="exportType" value="gname">Germplasm Name</html:radio></td>
								    	 				</tr>
								    	 			</table>
								    	 		</td>
								    	 	</tr>
										 </table>									 	
									<%//} else{ %>
									<%--<table width=100% align="left" border=0>
											<tr>
									<th width="40%" colspan="3" align="center"><font color="red">NO Maps!!!</font> <font color="black">Please upload Map data to create Export formats for Flapjack...</font></th>
									</tr>
									</table>
									<%} %>--%>
									</span>
							    	 </td>
						    	 </tr>
								</table><br>
							</tr>
						</table></td>
					</tr>
					</table>
					 	<br>	
					 	<html:hidden property="opType" value="dataset"/> 
					 	<html:hidden property="expType"/>	
					 	<html:hidden property="exportTypeH"/>	
					 	<html:button property="export" value="Next" onclick="subExport(this)"/>	 	
					</center>
				</span>		
				
				
				<%} %>
				
				<html:hidden property="op"/>
				<html:hidden property="retrieveOP"/>
			</center>
		</html:form>
	</body>
</html:html>
<script language="javascript">
var httpRequest;
function retrieveOPData(a){
	//alert(a.value);
	if(a.value=="QTL/Map"){
		document.forms[0].elements["op"].value=a.value;	
		//document.getElementById('QTLSpan').style.visibility='visible';
		document.forms[0].elements["retrieveOP"].value="first";
		document.forms[0].action="dataretrieval.do";
		document.forms[0].submit();
	}else if(a.value=="polymorphic"){
		//document.getElementById('poly').style.visibility='visible';	
		document.forms[0].elements["op"].value=a.value;	
		document.forms[0].action="genotypingpage.do?polyType";		
		document.forms[0].submit();	
		
	}else if(a.value=="Output"){		
		document.forms[0].action="genotypingpage.do?out";		
		document.forms[0].submit();
	}		
}

function retGermplasm(opType){
	document.forms[0].elements["op"].value=opType.value;	
	document.forms[0].action="genotypingpage.do?poly";		
	document.forms[0].submit();
}


function sub(s){
	//alert(s);
	var selValue="";
	var obj1;
	//alert(s);		
	if(s=="qtl"){	
		//alert(document.forms[0].opType.value);
		//var retT=document.forms[0].retType.value;
		if(document.forms[0].maps.value=="QTLName" && document.forms[0].qtl.value==""){			
			alert("Please provide the QTL name");
			return false;
		}else if(document.forms[0].maps.value=="Trait" && document.forms[0].qtl.value==""){
			alert("Please provide the trait");
			return false;
		}else if(document.forms[0].maps.value=="maps" && document.forms[0].qtl.value==""){
			alert("Please provide the Map name");
			return false;
		}
		//alert(document.forms[0].maps.value);
		document.forms[0].elements["retType"].value=document.forms[0].maps.value;	
		
		
		document.forms[0].elements["retrieveOP"].value="GetInfo";	
	}else if(s=="lines"){		
		if(document.forms[0].linesO.value==""){
			alert("Please select the Line");
			return false;
		}
		if((document.forms[0].linesO.value!="")&&(document.forms[0].linesT.value=="")){
			alert("Please select the other Line");
			return false;
		}
		/*if(document.forms[0].linesO.value==document.forms[0].linesT.value){
			alert("The selected lines should be different");
			return false;
		}*/
		document.forms[0].elements["retrieveOP"].value="Submit";	
	}else if(s=="Get Markers"){
		//alert(document.forms[0].txtNameM.value);
		//alert(">>>>>>>>>>>>>>>"+document.forms[0].gidsText.value);
		if(document.forms[0].txtNameM.value=="" && document.forms[0].gidsText.value==""){
			alert("Either upload a file or enter the gids");		 	
		 	return false;	
		}
		if(document.forms[0].txtNameM.value!="" && document.forms[0].gidsText.value!=""){
			alert("Either upload a file or enter the gids");
			document.forms[0].txtNameM.value=""; 
			document.forms[0].gidsText.value=""; 		 	
		 	return false;	
		}
		if(document.forms[0].txtNameM.value!="" && document.forms[0].txtNameM.value.indexOf(".txt")== -1){
		 	alert("Check the file, it has to be in text format");	
		 	document.forms[0].txtNameM.value=""; 
		 	//document.forms[0].txtNameM.focus();
		 	return false;		
		}
		if(document.forms[0].txtNameM.value!=""){
			document.forms[0].elements["opTypeGids"].value="file";
		}else if(document.forms[0].gidsText.value!=""){
			document.forms[0].elements["opTypeGids"].value="textbox";
		}
		document.forms[0].elements["retrieveOP"].value=s;
	}else if(s=="Get Lines"){
		if(document.forms[0].txtNameL.value=="" && document.forms[0].markersText.value==""){
			alert("Either upload a file or enter the markers");		 	
		 	return false;
		}
		if(document.forms[0].txtNameL.value!="" && document.forms[0].markersText.value!=""){
			alert("Either upload a file or enter the markers");
			document.forms[0].txtNameL.value=""; 
			document.forms[0].markersText.value=""; 		 	
		 	return false;
		}
		if(document.forms[0].txtNameL.value!="" && document.forms[0].txtNameL.value.indexOf(".txt")== -1){
		 	alert("Check the file, it has to be in text format");
		 	return false;		
		 }

		if(document.forms[0].txtNameL.value!=""){
			document.forms[0].elements["opTypeMarkers"].value="file";
		}else if(document.forms[0].markersText.value!=""){
			document.forms[0].elements["opTypeMarkers"].value="textbox";
		}

		 
		document.forms[0].elements["retrieveOP"].value=s;
	}else if(s=="Get"){
		if(document.forms[0].txtNameGN.value=="" && document.forms[0].GNamesText.value==""){
			alert("Either upload a file or enter the germplasm names");		 	
		 	return false;
		}
		if(document.forms[0].txtNameGN.value!="" && document.forms[0].GNamesText.value!=""){
			alert("Either upload a file or enter the germplasm names");	
			document.forms[0].txtNameGN.value=""; 
			document.forms[0].GNamesText.value=""; 		 	
		 	return false;
		}
		if(document.forms[0].txtNameGN.value!="" && document.forms[0].txtNameGN.value.indexOf(".txt")== -1){
		 	alert("Check the file, it has to be in text format");
		 	return false;		
		 }

		if(document.forms[0].txtNameGN.value!=""){
			document.forms[0].elements["opTypeGN"].value="file";
		}else if(document.forms[0].GNamesText.value!=""){
			document.forms[0].elements["opTypeGN"].value="textbox";
		}

		 
		document.forms[0].elements["retrieveOP"].value=s;
	}
	
}

function selOpt(opt){
	
	var check=opt.value;	
	//alert(check);		
	if(check=="gids"){			
		document.forms[0].action="genotypingpage.do?gidsDirecting";
		document.forms[0].submit();		
		
	}else if(check=="markers"){
		document.forms[0].action="genotypingpage.do?markersDirecting";
		document.forms[0].submit();
		
	}else if(check=="dataset"){
		document.forms[0].action="genotypingpage.do?datasetRet";
		document.forms[0].submit();
		
	}else if(check=="gNames"){
		document.forms[0].action="genotypingpage.do?gNamesDirecting";
		document.forms[0].submit();
	}	
}

function selOpt1(opt){
	var check=opt.value;
	//alert(check);
	var mType='<%=session.getAttribute("mType")%>';	
	//alert(mType);	
	var textboxname;	
	if(check=="Flapjack"){	
		//alert("flapjack   "+mType);
		if(mType=="allelic"){
			document.getElementById('dataType').style.visibility='visible';
		}else{
			document.getElementById('dataType').style.visibility='hidden';
		}


		
		//document.getElementById('dataType').style.visibility='hidden';
		textboxname="maps";
		var selectedValue=document.forms[0].dataset.value;				
		//alert(selectedValue);
		var url='retMaps.do?ChkDataSets='+selectedValue;		
		document.getElementById("map").style.visibility="visible";
		if (window.ActiveXObject){ 
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP"); 
		}else if (window.XMLHttpRequest){ 
			httpRequest = new XMLHttpRequest(); 
		} 
		httpRequest.open("GET", url , true); 
		httpRequest.onreadystatechange = function() { processRequest(textboxname); } ;
		httpRequest.send(null);			
	}else{
		//alert("*****  "+document.forms[0].dataset.value);	
		
		if(mType=="allelic"){
			document.getElementById('dataType').style.visibility='visible';
		}else{
			document.getElementById('dataType').style.visibility='hidden';
		}
		document.getElementById('map').style.visibility='hidden';
		
	}	
}


function subExport(){
	//alert(document.forms[0].elements['dataset'].value);
	if(document.forms[0].elements['dataset'].value==""){
		alert("Please select the dataset");		
		return false;
		document.forms[0].elements['dataset'].focus();
	}
	var c1=0;
	var c=0;
	var temp1="";
	var c2=0;
	var tempType="";
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
	if(temp1=="Flapjack"){
		//alert("....:"+document.forms[0].maps.value);
		if(document.forms[0].maps.value==""){
			alert("Please select the Map");
			return false;
		}
		if(document.forms[0].maps.value=="- Select -"){
			alert("Map Required");
			return false;
		}
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
	}
	document.forms[0].expType.value=temp1;
	var len2=document.forms[0].dataType.length;
	var mType='<%=session.getAttribute("mType")%>';	
	
			
		if(mType=="allelic"){
			for(k=0;k<len1;k++){
				if(document.forms[0].dataType[k].checked==true){
					c2++;
					tempType=document.forms[0].dataType[k].value;			
				}
			}
			if(c2==0){
				alert("Please Select Data Format");
				return false;
			}
		}else
			tempType="allelic";

		//alert(tempType);
		if(tempType=="allelic"){
			document.forms[0].action="exportStatus.do?allelic";
		}else{
			document.forms[0].action="exportStatus.do?abhformat";
		}
		//alert(document.forms[0].action);

	
	var msg;
	var size='<%=session.getAttribute("size")%>';
	//alert(size);
	msg= "   Downloading the whole dataset would take time. \n          Do you want to continue? ";
	var agree=confirm(msg);
	//alert("");
	
	if (agree){	
		
		document.forms[0].submit();	
	}else{
		return false;
	}
}
function onClickOption(a){	
	//alert(a);
	document.forms[0].elements["retType"].value=a;	
}


function errorCountFunc(){	
	var countn=0;
	var obj;
	for(var i=0;i<document.forms[0].elements.length; i++){
		obj=document.forms[0].elements[i];		
		if (obj.type=="checkbox" && obj.checked) 
			countn++;	   
	
		
		if(countn>1){
			alert("Not Allowed");
			obj.checked=false;
			return false;
		}
	}
	
}

function retrieveSize(selVal,name){
	//alert(selVal+"    "+name);
	document.forms[0].action="getDatasetType.do?str="+selVal;
	document.forms[0].submit();	
}


function getCorrespondingLines(selectedValue,textboxname){
	//alert(selectedValue+"   "+textboxname);
	textboxname="linesT";
	//alert('<%=session.getAttribute("polyType")%>');
	var pType='<%=session.getAttribute("polyType")%>';
	if(selectedValue !=""){
		var url='exportCheck.do?ChkDataSets='+selectedValue+"!~!"+pType;
		document.getElementById("linesS").style.visibility="visible";
		document.getElementById("submitButton").style.visibility="visible";
		if (window.ActiveXObject){ 
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP"); 
		}else if (window.XMLHttpRequest){ 
			httpRequest = new XMLHttpRequest(); 		
		} 
		httpRequest.open("GET", url , true); 
		httpRequest.onreadystatechange = function() { processRequest(textboxname); } ;
		httpRequest.send();
		
	}
}
/** 
	* This is the call back method 
	* If the call is completed when the readyState is 4 
	* and if the HTTP is successfull when the status is 200 
	* update the profileSection DIV 
*/
function processRequest(textboxname){
	//alert(textboxname);	
	
	if (httpRequest.readyState == 4) {
		if (httpRequest.status == 200) {
			
			msgDOM  = httpRequest.responseXML; 
	    	var data=msgDOM.getElementsByTagName("data")[0];
	    	details=data.getElementsByTagName("details");	
	    	
	    	document.forms[0].elements[textboxname].options.length=0;
	    	
	    	for(i=0;i<details.length;i++){	
		    	document.forms[0].elements[textboxname].options[i]=new Option(details[i].childNodes[0].nodeValue,details[i].childNodes[0].nodeValue);
	    	}				    	
	    }
	}  
}



</script>