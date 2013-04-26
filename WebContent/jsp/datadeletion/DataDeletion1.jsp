<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title>GDMS</title>
	<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
</head>
<body onload="refreshOnLoad()">
    <html:form action="/confirmdeletion.do">
    	<logic:notEmpty name="user">
    		<div class="heading" align="center">Data Deletion</div>
    		<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
				--><center>
				<br>
				<html:errors/>
				<br>
				<% if(session.getAttribute("data")=="yes") {%>
				<div align="center" class="displayText">Can delete Genotyping Data, QTL information & Maps </div><br>
				<div align="center" class="displayText">Showing the available data as datasets </div>
    			<br><br>    				
				<table border=1 width="70%" bordercolor="#006633" cellpadding="0" cellspacing="2">
					<tr bgcolor="#006633" class="displayHeadingBoldText"><td width="35%">Genotyping Data</td> <td width="35%">Maps </td><td width="30%">QTL Info</td></tr>
					<tr class="displayText">
						<td valign="top">
							<table width="100%">
								<tr class="displayText">
									<td>
										<logic:notEmpty name="glist">								
											<logic:iterate name="glist" id="dataset" type="java.lang.String">
												<html:checkbox property="delOpG" value="<%=dataset %>"><%=dataset%></html:checkbox>&nbsp;
									</td>
								</tr>
								<tr>
											</logic:iterate>					
								</tr>
							</table>
										</logic:notEmpty>
										<logic:empty name="glist">
											<font color="red">No Data</font>
									</td>
								</tr>
							</table>
										</logic:empty>
						</td>
						<td valign="top">
							<table width="100%">
								<tr class="displayText">
									<td>	
										<logic:notEmpty name="mlist">
											<logic:iterate name="mlist" id="maplist" type="java.lang.String">
												<html:checkbox property="delOpM" onclick="onClickOption1(this)" value="<%=maplist %>"><%=maplist%></html:checkbox>
									</td>
								</tr>
								<tr>
											</logic:iterate>					
								</tr>
							</table>
										</logic:notEmpty>
										<logic:empty name="mlist">
											<font color="red">No Data</font>
									</td>
								</tr>
							</table>
										</logic:empty>
						</td>
						<td valign="top">
							<table width="100%">
								<tr class="displayText">
									<td>
										<logic:notEmpty name="qlist">							
											<logic:iterate name="qlist" id="qtlList" type="java.lang.String">
												<html:checkbox property="delOpQ" onclick="onClickOption1(this)" value="<%=qtlList %>"><%=qtlList%></html:checkbox>
									</td>
								</tr>
								<tr>
											</logic:iterate>					
								</tr>
							</table>
										</logic:notEmpty>
										<logic:empty name="qlist">
											<font color="red">No Data</font>
									</td>
								</tr>
							</table>
										</logic:empty>
						</td>
					</tr>
				</table>
				<br><br>
				<center><html:submit property="delete" value=" Delete " onclick="return sub()"/></center>
				<html:hidden property="getOp"/>
				<%}else{ %>
				<br><br>
					<div class="errorMsgs">No data submitted..</div>
				<%} %>
			</logic:notEmpty>
			<logic:empty name="user">
				<br><br><br>
				<center><font color="blue" face="verdana" size="3px"><a href="../common/URLtoAction.jsp?str=logout" target="_parent">Please Login to upload/retrieve data</a></font></center>
			</logic:empty>
		
    </html:form>
</body>
</html:html>
<script>
function refreshOnLoad(){
	document.forms[0].elements["delOpQ"].checked=false;
	document.forms[0].elements["delOpM"].checked=false;
	document.forms[0].elements["delOpG"].checked=false;
}
/*function onClickOption(a){
	//alert(a.checked);
	//alert(document.forms[0].elements["delOp"].checked);
	if(a.checked=="true"){
		a.checked=false;
	}else if(a.checked=="false"){
		document.forms[0].elements["getOp"].value=a.value;	
		document.forms[0].action="../../deletedataretrieval.do?first";
		document.forms[0].submit();
	}	
}
function onClickOption1(a){
	//alert(a.checked);
	if(a.checked=="true"){
		a.checked=false;
	}else if(a.checked=="false"){
		//alert(document.forms[0].elements["delOp"].checked);
		document.forms[0].elements["getOp"].value=a.value;	
		document.forms[0].action="deletedataretrieval.do?first";
		document.forms[0].submit();	
	}
}*/


function sub(){
	
	var ds="";
	var map="";
	var qtl="";
	var finalSets="";
	var msg;
	msg= "Are you sure you want to delete the data ? ";
	var agree=confirm(msg);
	if (agree){
		 for (var i=0; i<document.forms[0].elements.length; i++){
		    obj = document.forms[0].elements[i];
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpG"){
				ds=ds+obj.value+"!~!";
			} 
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpM"){
				map=map+obj.value+"!~!";
			} 
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpQ"){
				qtl=qtl+obj.value+"!~!";
			} 			
		 } 
	
		 finalSets=ds+" ;;"+qtl+" ;;"+map+" ;;";
		 //alert(finalSets);
		 document.forms[0].elements["getOp"].value=finalSets;
	}else{
		
		 for (var i=0; i<document.forms[0].elements.length; i++){
			    obj = document.forms[0].elements[i];
			    if (obj.type == "checkbox" && obj.checked){
			    	obj.checked=false;
			    }
		 }
		 return false ;
	}
}
</script>
