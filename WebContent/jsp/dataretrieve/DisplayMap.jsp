<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<%@ page import="java.util.ArrayList;" %>
<html:html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GDMS</title>
		<script>
			function stopRKey(evt) { 
			  var evt = (evt) ? evt : ((event) ? event : null); 
			  var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null); 
			  if (evt.keyCode == 13)   {return false;} 
			} 

			document.onkeypress = stopRKey; 
		</script>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">		
	</head>
	<body onload="refreshPage()">
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
			%>
			<logic:notEmpty name="map_data">
				<html:hidden property="traitValues" value='<%=testing%>'/>
				<br>
				<br>
				<%
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
				<br>
				<table width="60%" border=0 align="center">
					<tr class="displayText">
					<%if(showTraits.equalsIgnoreCase("yes")){ %>
						<td align="right">Bin size:</td><td><html:text property="binSize" onkeyup="funCheckMarkers(this)" value=""/>cM</td>
						
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
						<td align="right">Bin size:</td><td><html:text property="binSize" onkeyup="funCheckMarkers1(this)" value=""/>cM</td>
						
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
						<%} %>
					</tr>								
					<%					
					for(int m=0; m<mdata.size();m++){
						String[] strMdata=mdata.get(m).toString().split("!~!");
					%>
						
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
					<%} %>
				</table>
				<center>
				<br>
				<br>
				<input type="hidden" name="dataToExp"/>
				<input type="hidden" name="op" value='<%=session.getAttribute("polyType") %>'/>
				<html:button property="back" value="Back" onclick="earlierPage()"/>
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
				<html:button property="back" value="Back" onclick="earlierPage()"/>
				
	 			</center>
			</logic:empty>		
			<input type="hidden" name="option" value='<%=session.getAttribute("showTraits") %>'>
			<input type="hidden" name="hTraits">			
		</html:form>
	</body>
	</html:html>
	
<script>
	function refreshPage(){
		var str='<%=request.getQueryString()%>';
		//alert(str);
		if(str=="first"){
			document.forms[0].elements['traitM'].checked=false;
			document.forms[0].elements['traits'].selectedIndex=0;
		}
	}

	function earlierPage(){
		document.forms[0].action="genotypingpage.do?poly";		
		document.forms[0].submit();
	}

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
					document.forms[0].elements['hmarkers'+a].value=document.forms[0].elements['markers'+a].name+"!~!"+document.forms[0].elements['markers'+a].value+"!~!"+binSize+"cM"+"!~!"+splitValues[1]+"!~!"+splitValues[3]+"!~!"+splitValues[2]+"!~!"+splitValues[4];
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
					document.forms[0].elements['hmarkers'+a].value=document.forms[0].elements['markers'+a].name+"!~!"+document.forms[0].elements['markers'+a].value+"!~!"+binSize+"cM"+"!~!"+splitValues[1]+"!~!"+splitValues[3]+"!~!"+splitValues[2];
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
		var count=0;
		var strData="";
		for(var i=0; i<document.forms[0].elements.length; i++){
			obj = document.forms[0].elements[i];
			if(obj.type == "checkbox" && obj.checked && obj.name != "traitM"){
				var  nameM=obj.name;
				strData=strData+document.forms[0].elements['h'+nameM].value+"~~!!~~";
				count=count+1;
			}			    		          
		}
		if(count<1){
			alert("None of them are selected");
			return false;
		}else{
			document.forms[0].elements['dataToExp'].value=strData;
			document.forms[0].submit();
		}	
	}
	
</script>


