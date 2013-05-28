<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
	<head>
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
		<script>
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
			}
			<%
			
			session.removeAttribute("indErrMsg");
			%>
		}
		
		</script>
	</head>
	<body onload="msg();refreshOnLoad()">
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
				<% if(session.getAttribute("dataC")=="yes") {%>
				<br>
				<div align="center" class="displayText">Can delete Genotyping Data, QTL information Maps & MTAs from <b>Local</b></div><br>
				<div align="center" class="displayText">Showing the available data as datasets </div>
    			<br><br>    								
				<table border=1 width="90%" bordercolor="#006633" cellpadding="0" cellspacing="2">
					<tr bgcolor="#006633" class="displayHeadingBoldText" align="center"><td colspan="4">Data from Central Database</td></tr>
					<tr bgcolor="#006633" class="displayHeadingBoldText"><td width="25%" align="center">Genotyping Data</td> <td width="25%" align="center">Maps </td><td width="25%" align="center">QTL Info</td><td width="25%" align="center">MTAs</td></tr>
					<tr class="displayText">
						<td valign="top" width="25%">
							<table width="100%">
								<tr class="displayText">
									<td>
										<logic:notEmpty name="glistC">
											<table border=0 width="100%">										
												<logic:iterate name="glistC" id="dataset" type="java.lang.String">
													<tr class="displayText"><td>
														&nbsp;<%=dataset%>
													</td></tr>						
												</logic:iterate>
											</table>	
									</td>											
								</tr>
							</table>
										</logic:notEmpty>
										<logic:empty name="glistC">
											<font color="red">No Data</font>
									</td>
								</tr>
							</table>
										</logic:empty>
						</td>
						<td valign="top" width="25%">
							<table width="100%">
								<tr class="displayText">
									<td>	
										<logic:notEmpty name="mlistC">
											<table border=0 width="100%">
												<logic:iterate name="mlistC" id="maplist" type="java.lang.String">
													<tr class="displayText"><td>
														<%=maplist%>
													</td></tr>
												</logic:iterate>
											</table>
									</td>					
								</tr>
							</table>
										</logic:notEmpty>
										<logic:empty name="mlistC">
											<font color="red">No Data</font>
									</td>
								</tr>
							</table>
										</logic:empty>
						</td>
						<td valign="top" width="25%">
							<table width="100%">
								<tr class="displayText">
									<td>
										<logic:notEmpty name="qlistC">
											<table border=0 width="100%">						
												<logic:iterate name="qlistC" id="qtlList" type="java.lang.String">
													<tr class="displayText"><td>
														<%=qtlList%>
													</td></tr>
												</logic:iterate>	
											</table>
										</td>				
								</tr>
							</table>
										</logic:notEmpty>
										<logic:empty name="qlistC">
											<font color="red">No Data</font>
									</td>
								</tr>
							</table>
										</logic:empty>
						</td>
						<td valign="top" width="25%">
							<table width="100%">
								<tr class="displayText">
									<td>
										<logic:notEmpty name="mtalistC">
											<table border=0 width="100%">						
												<logic:iterate name="mtalistC" id="mtaList" type="java.lang.String">
													<tr class="displayText"><td>
														<%=mtaList%>
													</td></tr>
												</logic:iterate>	
											</table>
										</td>				
								</tr>
							</table>
										</logic:notEmpty>
										<logic:empty name="mtalistC">
											<font color="red">No Data</font>
									</td>
								</tr>
							</table>
										</logic:empty>
						</td>
					</tr>
				</table>
				
				<%}else{ %>
				<br><br>
					<div class="errorMsgs">No data in Central database</div>
				<%} %>
				<br><br>
				<% if(session.getAttribute("data")=="yes") {%>
				
				<table border=1 width="90%" bordercolor="#006633" cellpadding="0" cellspacing="2">
				<tr bgcolor="#006633" class="displayHeadingBoldText" align="center"><td colspan="4">Data from Local Database</td></tr>
					<tr bgcolor="#006633" class="displayHeadingBoldText"><td width="25%" align="center">Genotyping Data</td> <td width="25%" align="center">Maps </td><td width="25%" align="center">QTL Info</td><td width="25%" align="center">MTAs</td></tr>
					<tr class="displayText">
						<td valign="top" width="25%">
							<table width="100%">
								<tr class="displayText">
									<td>
										<logic:notEmpty name="glist">
											<table border=0 width="100%">										
												<logic:iterate name="glist" id="dataset" type="java.lang.String">
													<tr class="displayText"><td>
														<html:checkbox property="delOpG" value="<%=dataset%>">&nbsp;<%=dataset%></html:checkbox>&nbsp;
													</td></tr>						
												</logic:iterate>
											</table>	
									</td>											
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
						<td valign="top" width="25%">
							<table width="100%">
								<tr class="displayText">
									<td>	
										<logic:notEmpty name="mlist">
											<table border=0 width="100%">
												<logic:iterate name="mlist" id="maplist" type="java.lang.String">
													<tr class="displayText"><td>
														<html:checkbox property="delOpM" onclick="onClickOption1(this)" value="<%=maplist %>"><%=maplist%></html:checkbox>
													</td></tr>
												</logic:iterate>
											</table>
									</td>					
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
						<td valign="top" width="25%">
							<table width="100%">
								<tr class="displayText">
									<td>
										<logic:notEmpty name="qlist">
											<table border=0 width="100%">						
												<logic:iterate name="qlist" id="qtlList" type="java.lang.String">
													<tr class="displayText"><td>
														<html:checkbox property="delOpQ" onclick="onClickOption1(this)" value="<%=qtlList %>"><%=qtlList%></html:checkbox>
													</td></tr>
												</logic:iterate>	
											</table>
										</td>				
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
						<td valign="top" width="25%">
							<table width="100%">
								<tr class="displayText">
									<td>
										<logic:notEmpty name="mtalist">
											<table border=0 width="100%">						
												<logic:iterate name="mtalist" id="mtaList" type="java.lang.String">
													<tr class="displayText"><td>
														<html:checkbox property="delOpMTA" onclick="onClickOption1(this)" value="<%=mtaList %>"><%=mtaList%></html:checkbox>
													</td></tr>
												</logic:iterate>	
											</table>
										</td>				
								</tr>
							</table>
										</logic:notEmpty>
										<logic:empty name="mtalist">
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
				<input type=hidden name="hResult" value='<%=strResult %>'>
				<%}else{ %>
				<br><br>
					<div class="errorMsgs">No data in Local database</div>
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
	 for (var i=0; i<document.forms[0].elements.length; i++){
		    obj = document.forms[0].elements[i];
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpG"){
				obj.checked=false;
			}
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpM"){
				obj.checked=false;
			}
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpQ"){
				obj.checked=false;
			}
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpMTA"){
				obj.checked=false;
			} 
	 }
	/*document.forms[0].elements["delOpQ"].checked=false;
	document.forms[0].elements["delOpM"].checked=false;
	document.forms[0].elements["delOpG"].checked=false;
	document.forms[0].elements["delOpMTA"].checked=false;*/
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
	var checkedCount=0;
	var ds="";
	var map="";
	var qtl="";
	var mta="";
	var finalSets="";
	var msg;
	msg= "Are you sure you want to delete the data ? ";
	var agree=confirm(msg);
	if (agree){
		 for (var i=0; i<document.forms[0].elements.length; i++){
		    obj = document.forms[0].elements[i];
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpG"){
				ds=ds+obj.value+"!~!";
				checkedCount++;
			} 
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpM"){
				map=map+obj.value+"!~!";
				checkedCount++;
			} 
		    if (obj.type == "checkbox" && obj.checked && obj.name=="delOpQ"){
				qtl=qtl+obj.value+"!~!";
				checkedCount++;
			} 
			if (obj.type == "checkbox" && obj.checked && obj.name=="delOpMTA"){
				mta=mta+obj.value+"!~!";
				checkedCount++;
			}			
		 } 
		if(checkedCount==0){
			alert("Please select the dataset to be deleted");
			return false;
		}else{
			finalSets=ds+" ;;"+qtl+" ;;"+map+" ;;"+mta+" ;;";
			 //alert(finalSets);
			 document.forms[0].elements["getOp"].value=finalSets;
		}		 
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
