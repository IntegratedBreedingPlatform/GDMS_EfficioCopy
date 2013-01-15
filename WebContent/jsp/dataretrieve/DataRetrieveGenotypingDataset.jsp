<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.*" %>
<html:html>
	<head>
		<title>GDMS</title>
		<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
	</head>
	<body>
		<html:form action="/dataset.do" method="post">
			<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
			--><%
				ArrayList dataList=(ArrayList)session.getAttribute("glist");
				//String[] dataArr=session.getAttribute("glist").toString().split(";;");
				int totalItemcount=Integer.parseInt(session.getAttribute("GCount").toString());
				int genoCount=0;
				int colCount=0;
				String str="";
				/*String option=session.getAttribute("op").toString();
				if(option.equalsIgnoreCase("Get Markers"))
					str="Markers";
				else*/
				str="GermplasmName"; 
				
				genoCount=(totalItemcount/30);
				if(genoCount*30<totalItemcount){
					genoCount=genoCount+1;	
				} 
				colCount=1;
				
				%>
				<table border=0 width="50%" cellpadding=0 align="center">
					<tr>
						<td align="left" class="displaysmallText" colspan=2><html:checkbox property="chk" onclick="checkAllAcc();" > Select All </html:checkbox></td>
						<td align="left" class="displayText" colspan=2>  Select from the list of <b><%=session.getAttribute("GCount")%></b>  </td>						
					</tr>
				</table>
				<table border=0 width="80%" align="center" cellpadding=10 cellspacing=0 >
				<tr><td align="center">
					
			  	<br>
					<Table border="0" cellpadding="0" cellspacing="0" align="left">
						
						<tr bgcolor="white">
						<%
							int rowCount=0;
							for (int j = 0;j<totalItemcount;j++){
								String[] arr1=dataList.get(j).toString().split("!~!");
								if(rowCount==genoCount){
									rowCount=0;
									%>
									</tr><tr class='reportsBody'>
								<%}	%>		
								<td nowrap class="displaysmallText" ><input type="checkbox" name="GcheckGroup" value="<%=arr1[1]%>"><%=arr1[0]%></td>
				 
								<%rowCount++;
								} %>
						</tr>
				  </table>
				</td>
				</tr>
			</table>
			<center>
				<html:submit property="next" value="Next" onclick="sub(this)"/>
			</center>
			<html:hidden property="germplasmSel"/>
			<html:hidden property="retrieveOP"/>
		</html:form>
	</body>
</html:html>
<script>
function checkAllAcc(){
	var len=document.forms[0].GcheckGroup.length;
	var temp="";
	c=0;
	if(document.forms[0].chk.checked == true){
	 document.forms[0].chk.value="checked";
	 for(i=0;i<len;i++){
	 	document.forms[0].GcheckGroup[i].checked=true;
		c++;
		//alert("Marker at "+k+"="+document.retDisp.McheckGroup[k].value);
		temp=temp+"'"+document.forms[0].GcheckGroup[i].value+"',";
	 }
	 //alert(temp);
	}else{
	 for(i=0;i<len;i++){
	 	document.forms[0].GcheckGroup[i].checked=false;
	 }
	}
	//alert(len);
	document.forms[0].germplasmSel.value=temp;
}

function sub(c){	
	//alert(document.forms[0].elements["op"].value);	
	document.forms[0].elements["retrieveOP"].value=c.value;		
	//document.forms[0].submit();
}
</script>