<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<%@ page import="java.util.*;" %>
<html:html>
	<head>

		<title>GDMS</title>
		<script>
			<%
			int mtaCount=Integer.parseInt(request.getSession().getAttribute("mtaCount").toString());
			String mtaDetails="";
			HashMap<Object, String> mtaTraits = new HashMap<Object, String>();
			ArrayList mtaList=new ArrayList();
			if(mtaCount>0){				
				mtaList=(ArrayList)request.getSession().getAttribute("mtaMIDsList");
				System.out.println("...mtaList=:"+mtaList);
				mtaTraits=(HashMap)request.getSession().getAttribute("mtaTraits");
				System.out.println("...mtaTraits..... JSP=:"+mtaTraits);
				mtaDetails=request.getSession().getAttribute("mtaMarkers").toString();
			}
			%>
			function stopRKey(evt) { 
			  var evt = (evt) ? evt : ((event) ? event : null); 
			  var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null); 
			  if (evt.keyCode == 13)   {return false;} 
			} 

			document.onkeypress = stopRKey; 

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
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">		
	</head>
	<body onload="refreshPage(); msg(); mtaMarkers();">
		<html:form action="/exportToFile.do">
			<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
			-->
			
			<%			
				String showTraits=session.getAttribute("showTraits").toString();
				ArrayList mdata=new ArrayList();
				mdata=(ArrayList)request.getSession().getAttribute("map_data");
				String testing=request.getSession().getAttribute("finalData").toString();
				String fromPage=session.getAttribute("fromPage").toString();
				//mtaList=(ArrayList)request.getSession().getAttribute("mtaMIDsList");
				//System.out.println("...mtaList=:"+mtaList);
				//mtaTraits=(HashMap)request.getSession().getAttribute("mtaTraits");
			%>
			<html:hidden property="fromPage" value='<%=fromPage%>'/>
			<logic:notEmpty name="map_data">
				<html:hidden property="traitValues" value='<%=testing%>'/>
				<html:hidden property="mtaValues" value='<%=mtaDetails%>'/>
				<br>
				<br>
				<%
				String map_unit=session.getAttribute("map_unit").toString();
				int actualCount=Integer.parseInt(session.getAttribute("recCount").toString());
				//int retCount=mdata.length;
				int retCount=Integer.parseInt(request.getSession().getAttribute("retCount").toString());
				//System.out.println(retCount+"=="+actualCount);
				if(retCount<actualCount){	
				%>
				<div align="center" class="displayText"><b>Out of <%=actualCount%> polymorphic markers only <%=retCount%> are on Map</b></div>
				<%} else if(retCount==actualCount){%>
				<div align="center" class="displayText"><b><%=actualCount%> polymorphic markers on Map</b></div>
				<%} %>
				<html:hidden property="mapUnit" value='<%=map_unit%>'/>
				<br>
				<table width="60%" border=0 align="center">
					<tr class="displayText">
					<%
					System.out.println(".................:"+session.getAttribute("map_unit"));
					if(showTraits.equalsIgnoreCase("yes")){ %>
						<td align="right">Bin size:</td><td><html:text property="binSize" onkeyup="funCheckMarkers(this)" value=""/>&nbsp;<%=map_unit%></td>
						
						<%--<td align="right">Trait:</td><td><html:checkbox property="traitM" onclick="funCheckMarkers(binSize)"/></td>--%>
						<td align="right"><html:select property="traits" size="3" multiple="true" onchange="getSelectedOptions(this)">
								<html:option value="">-- select Trait(s) --</html:option>
				  					<logic:iterate name="traits" id="traits" type="java.lang.String">
				  					<html:option value="<%=traits %>" />
				   					</logic:iterate>
							</html:select>
						</td>
						
						<td><html:checkbox property="traitM" onclick="funCheckMarkers(binSize)"/></td><td>Check Trait(s)</td>
					<%}else{ %>
						<td align="right">Bin size:</td><td><html:text property="binSize" onkeyup="funCheckMarkers1(this)" value=""/>&nbsp;<%=map_unit%></td>
						
					<%} %>
				</table>
				<br>
				<table border=1 align="center" width="60%" cellpadding="2" cellspacing="2">
					<tr bgcolor="#006633">	
						<td width="5%">&nbsp;</td>														
						<td nowrap align="center" class="displaysmallwhiteboldText">Marker</td>
						<td nowrap align="center" class="displaysmallwhiteboldText">Map</td>
						<td nowrap align="center" class="displaysmallwhiteboldText">Chromosome</td>
						<td nowrap align="center" class="displaysmallwhiteboldText">Position</td>									
						<%if(showTraits.equalsIgnoreCase("yes")){ %>									
						<td nowrap align="center" class="displaysmallwhiteboldText">Trait</td>	
						<%} else{
							if(mtaList.size()>0){
						%>
						<td nowrap align="center" class="displaysmallwhiteboldText">Trait</td>	
							<%}} %>
					</tr>								
					<%					
					for(int m=0; m<mdata.size();m++){
						String[] strMdata=mdata.get(m).toString().split("!~!");
						//System.out.println("jsp=:"+strMdata[0]);
						//System.out.println(mtaTraits.get(strMdata[0])+"   "+strMdata[0]);
						
						if((mtaList.size()>0)&&(mtaList.contains(strMdata[0]))){%>			
							<tr>
								<td  class="displaysmallText" width="5%">&nbsp;<input type="checkbox" name='markers<%=m%>' value='<%=strMdata[0]%>' onclick="startChecking(this)"/><input type="hidden" name='hmarkers<%=m%>'></td>											
								<td nowrap class="displaysmallText"><b><%=strMdata[0]%></b></td>				
								<td nowrap class="displaysmallText"><b><%=strMdata[1]%></b></td>
								<td nowrap class="displaysmallText"><b><%=strMdata[3]%></b></td>	
								<td nowrap class="displaysmallText"><b><%=strMdata[2]%></b></td>	
								<%if(showTraits.equalsIgnoreCase("yes")){ 
									if(mtaList.size()>0){%>
										<td nowrap><font class="displaysmallText">&nbsp;<b><%=mtaTraits.get(strMdata[0])%>,</b></font>&nbsp;<font class="displaysmallText"><%=strMdata[4]%></font></td>
									<%}else{ %>
										<td nowrap class="displaysmallText" >&nbsp;<%=strMdata[4]%></td>	
									<%} 
								}else{
									//if(mtaList.size()>0){
									
									%>
										<td nowrap class="displaysmallText" >&nbsp;<b><%=mtaTraits.get(strMdata[0])%></b></td>	
									<%//}
								}%>
							</tr>
						<%}else{%>
							<tr>
								<td width="5%">&nbsp;<input type="checkbox" name='markers<%=m%>' value='<%=strMdata[0]%>' onclick="startChecking(this)"/><input type="hidden" name='hmarkers<%=m%>'></td>											
								<td nowrap class="displaysmallText" ><%=strMdata[0]%></td>				
								<td nowrap class="displaysmallText" ><%=strMdata[1]%></td>
								<td nowrap class="displaysmallText" ><%=strMdata[3]%></td>	
								<td nowrap class="displaysmallText" ><%=strMdata[2]%></td>	
								<%if(showTraits.equalsIgnoreCase("yes")){ %>
									<td nowrap class="displaysmallText" >&nbsp;<%=strMdata[4]%></td>	
								<%} %>
							</tr>
						<%}
					}%>
				</table>
				<center>
				
					<br>
					<br><table border=0 align=center width=77%><tr><td class="displayBoldText" align=left>Choose Data Export Format You Would Like</td></tr></table>
					<table align="center" width=77% border=0>
				    	 <tr>
					    	 <td width=1%><html:radio  property="FormatcheckGroup" value="excel" /></td>
					    	 <td class="QrytextColor" align=left>Excel sheet</td>
				    	 </tr>
				    	  <tr>
					    	 <td width=1%><html:radio property="FormatcheckGroup" value="kbio" /></td>
					    	 <td class="QrytextColor" align="left">KBio order form</td>
					     </tr> 
					</table> 
					<br><br>
					<input type="hidden" name="dataToExp"/>
					<input type="hidden" name="op" value='<%=session.getAttribute("polyType") %>'/>
					<html:button property="back" value="Back" onclick="javascript:history.back()"/>
					<html:button property="export" value="Export" onclick="submitPage()"/>
 				</center>
			</logic:notEmpty>
			<logic:empty name="map_data">
				<br><br><br><br><br><div class="errorMsgs" align="center">
				Map(s) NOT uploaded</div>
				<center>
				<br>
				<br>
				<input type="hidden" name="dataToExp"/>
				<input type="hidden" name="op" value='<%=session.getAttribute("polyType") %>'/>
				<html:button property="back" value="Back" onclick="javascript:history.back()"/>
				
	 			</center>
			</logic:empty>		
			<input type="hidden" name="option" value='<%=session.getAttribute("showTraits") %>'>
			<input type="hidden" name="hTraits">	
			<input type=hidden name="hResult" value='<%=strResult %>'>		
		</html:form>
	</body>
	</html:html>
	
<script>
var mapUnit=document.forms[0].mapUnit.value;
	function refreshPage(){
		var str='<%=request.getQueryString()%>';
		//alert(str);
		if(str=="first"){
			document.forms[0].elements['traitM'].checked=false;
			document.forms[0].elements['traits'].selectedIndex=0;
		}
	}

	/*function earlierPage(){
		//alert(document.forms[0].elements['fromPage'].value);
		if(document.forms[0].elements['fromPage'].value=='map')
			document.forms[0].action="retrievalmapsqtls.do";
		else				
			document.forms[0].action="genotypingpage.do?poly";		
		document.forms[0].submit();
	}*/
	
	function funCheckMarkers(val){
		if(document.forms[0].elements['traitM'].checked==true){	
			//alert("....:"+document.forms[0].hTraits.value);
			if(document.forms[0].hTraits.value==" "){
				alert("Please select the traits");
				return false;
				//document.forms[0].traits.focus();
				
			}
		}
		var chData=document.forms[0].traitValues.value.split("~~!!~~");	
		var binSize=0;
		binSize=parseFloat(val.value);	
		var splitValues;
		var arg=0;var pos=0;	
		arg=binSize;
		var str1=chData[0].split("!~!");
		var markerT=str1[0];
		var trait=str1[4];
		var LG=str1[3];
		var Map=str1[1];
		var SPos=str1[2];		
		for(a=0;a<chData.length;a++){
			splitValues=chData[a].split("!~!");		
			//alert(Map+"=="+splitValues[1]);
			if((Map==splitValues[1])&&(LG==splitValues[3])){				
				//alert(parseFloat(splitValues[2])+">="+parseFloat(arg));
				if(parseFloat(splitValues[2])>=parseFloat(arg)){
					document.forms[0].elements['markers'+a].checked=true;
					document.forms[0].elements['hmarkers'+a].value=document.forms[0].elements['markers'+a].name+"!~!"+document.forms[0].elements['markers'+a].value+"!~!"+binSize+mapUnit+"!~!"+splitValues[1]+"!~!"+splitValues[3]+"!~!"+splitValues[2]+"!~!"+splitValues[4];
					arg=parseFloat(splitValues[2])+binSize;
				}else{
					document.forms[0].elements['markers'+a].checked=false;
				}
				if(document.forms[0].elements['traitM'].checked==true){					
					var traits=	document.forms[0].hTraits.value.split(",");	
					if(splitValues[4]!=" "){
						for(var t=0;t<traits.length;t++){
							if((splitValues[4]==traits[t])||(splitValues[4].indexOf(traits[t]) != -1)){	
								document.forms[0].elements['markers'+a].checked=true;
								document.forms[0].elements['hmarkers'+a].value=document.forms[0].elements['markers'+a].name+"!~!"+document.forms[0].elements['markers'+a].value+"!~!"+" "+"!~!"+splitValues[1]+"!~!"+splitValues[3]+"!~!"+splitValues[2]+"!~!"+splitValues[4];
								//alert("not null  "+document.forms[0].elements['markers'+a].value+"   "+splitValues[4]+"    "+document.forms[0].elements['markers'+a].checked);
							}
						}
					}
				}else{
					document.forms[0].elements['traits'].selectedIndex=0;
				}			
			}else{
				//alert("Else  con....."+splitValues[1]+"Map=:"+Map+"    "+splitValues[3]+"   LG=:"+LG+"   arg=: "+arg)
				arg=binSize;
				LG=splitValues[3];
				Map=splitValues[1];	
				a--;
			}			
			//alert(document.forms[0].elements['hMarkers'+a].value);	
		}
		
	}
	
	function getSelectedOptions(oList){
		oList = document.forms[0].elements["traits"];
	  	var sdValues = [];
	   	for(var i = 1; i < oList.options.length; i++){
		   	if(oList.options[i].selected == true){
			   	sdValues.push(oList.options[i].value);
			}
		}
		//alert(sdValues);		
		document.forms[0].hTraits.value=sdValues;	
		if(document.forms[0].elements['traitM'].checked==true){
			funCheckMarkers(document.forms[0].elements['binSize']);
		}
	}
	function funCheckMarkers1(val){
		
		var chData=document.forms[0].traitValues.value.split("~~!!~~");	
		var binSize=0;
		binSize=parseFloat(val.value);	
		var splitValues;
		var arg=0;var pos=0;	
		arg=binSize;
		//alert("....:"+chData[0]);
		var str1=chData[0].split("!~!");
		var markerT=str1[0];
		
		var LG=str1[3];
		var Map=str1[1];
		var SPos=str1[2];	
		//alert(".............:"+chData.length);	
		for(a=0;a<chData.length;a++){
			splitValues=chData[a].split("!~!");	
			//alert(splitValues);	
			//alert(Map+"=="+splitValues[1]+"          "+LG+"=="+splitValues[3]);
			if((Map==splitValues[1])&&(LG==splitValues[3])){				
				//alert(parseFloat(splitValues[2])+">="+parseFloat(arg));
				if(parseFloat(splitValues[2])>=parseFloat(arg)){
					document.forms[0].elements['markers'+a].checked=true;
					document.forms[0].elements['hmarkers'+a].value=document.forms[0].elements['markers'+a].name+"!~!"+document.forms[0].elements['markers'+a].value+"!~!"+binSize+mapUnit+"!~!"+splitValues[1]+"!~!"+splitValues[3]+"!~!"+splitValues[2];
					arg=parseFloat(splitValues[2])+binSize;
				}else{
					document.forms[0].elements['markers'+a].checked=false;
				}
							
			}else{
				//alert("Else  con....."+splitValues[1]+"Map=:"+Map+"    "+splitValues[3]+"   LG=:"+LG+"   arg=: "+arg)
				arg=binSize;
				LG=splitValues[3];
				Map=splitValues[1];	
				a--;
			}			
			//alert(document.forms[0].elements['hMarkers'+a].value);	
		}
		
	}
	function submitPage(){	
		//alert(mapUnit);	
		var count=0;
		var strData="";
		var c1=0;
		var temp1="";
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

		
		for(var i=0; i<document.forms[0].elements.length; i++){
			obj = document.forms[0].elements[i];
			if(obj.type == "checkbox" && obj.checked && obj.name != "traitM"){
				var  nameM=obj.name;
				
					strData=strData+document.forms[0].elements['h'+nameM].value+"~~!!~~";
				
				count=count+1;
			}			    		          
		}
		//alert(strData);
		if(count<1){
			alert("None of them are selected");
			return false;
		}else{
			document.forms[0].elements['dataToExp'].value=strData;
			//alert(document.forms[0].elements['dataToExp'].value);	
			if(temp1=="kbio"){
				//alert("...........................");
				document.forms[0].action="exportKBIOFile.do?map";
			}else{
				//alert("***************************");
				document.forms[0].action="exportToFile.do";
			}
			document.forms[0].submit();
		}
		
	}
	function mtaMarkers(){		
		var chData=document.forms[0].mtaValues.value.split("!~!");		
		//alert(chData);
		for(var i=0; i<document.forms[0].elements.length; i++){
			obj = document.forms[0].elements[i];
			//mylist.indexOf(1) == 0
			if(obj.type == "checkbox" && chData.indexOf(obj.value)==0){
				obj.checked=true;
			}			    		          
		}		
	}
	
</script>


