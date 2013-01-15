<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.ArrayList,java.util.Iterator,jxl.format.*" %>
<%@ page import="java.io.*,jxl.*,jxl.write.*,java.util.Calendar"%>
<html:html>
<head>

	<title>GDMS</title>
	<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
	<style >
		body {
			padding: 0;
			/*font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;*/
			font-family : verdana;
			font-size: 13px;
			/*color: #837669;*/
			color: black;
		}
		td{
			/*font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;*/
			font-family : verdana;
			font-size: 13px;
			font-weight:bold;
			font-stretch:narrower;
			color: #837669;
					
		}
		.td1{
			/*font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;*/
			font-family : verdana;
			font-size: 13px;
			font-weight:bold;
			font-stretch:narrower;
			text-decoration:underline;
			border:thin;
			bordercolor:#B1E1F9; 
			//color: #837669;
			color: white;
			width: 100px;
		}			
	</style>
</head>
<body>
<center>
<img src="jsp/Images/GDMS.gif" border=0 usemap="#Map2">
	<!--<map name="Map2">
		<area shape="rect" coords="13,90,60,109" href="../../" alt="Home" target="_top">
	 	<area shape="rect" coords="88,90,142,109" href="jsp/common/GDMSLayout.jsp?upl" alt="Upload" target="_top">
	  	<area shape="rect" coords="172,90,235,109" href="jsp/common/GDMSLayout.jsp?ret" alt="Retrieve" target="_top">
	</map>
	
-->
<br>
	<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
--><br>

<%
	 Calendar now = Calendar.getInstance();
	String foldername="MarkerFiles";
	String fname1=session.getServletContext().getRealPath("//")+"/"+foldername;
	if(!new File(fname1).exists())
       	new File(fname1).mkdir();
	System.out.println("fname1="+fname1);
	String createfile=fname1+"/Marker"+now.getTimeInMillis()+".xls";
	File file=new File(createfile);
	file.createNewFile();		
	
	WritableWorkbook workbook = Workbook.createWorkbook(new File(createfile));
	WritableSheet sheet=workbook.createSheet("MarkerDetails",0);
	WritableFont wf = new WritableFont(WritableFont.TIMES,WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD,false,UnderlineStyle.SINGLE);
   	WritableCellFormat cf = new WritableCellFormat(wf);
    cf.setWrap(true); 
	String filenm=foldername+"/Marker"+now.getTimeInMillis()+".xls";
	
	System.out.println("filenm="+filenm);
	
	System.out.println(request.getParameter("data"));
	int mid=Integer.parseInt(request.getParameter("data").toString());
	//String samples=det.getGenotypeList(request,mid);
	String samples=session.getAttribute("finalStr").toString();
	
	int k=0;	
	int k1=1;
	%>	
	<%String[] str1=samples.split("~!~"); %>
	<%String color="#D9EFFC";
	//ArrayList al=(ArrayList)request.getAttribute("result");%>
	<font style="font-family : verdana;	color: #2E566F;font-size   : 12px;font-weight : bold;font: italic;">
					List of germplasm for marker '<%=request.getAttribute("MName").toString() %>' 
					</font><br><br>
					<a href='<%=filenm%>' target="_blank">Download Excel Format file</a><br><br>
		<table border=1 cellpadding="3" cellspacing="0">
			<tr bgcolor="#006633" ><td class="td1" nowrap>GID</td><td class="td1" nowrap>Germplasm Names</td></tr>
			<%
			int i=0;
			for(int i1=0;i1<str1.length;i1++){
			//Iterator it=al.iterator();
				String[] strVal=str1[i1].split("!~!");
			//while(it.hasNext()){
				i++;
				//String genotype=(String)it.next();
				if(i%2==0)
						color="#D9EFFC";
					else
						color="#EAF7FF";
				
				Label ll = new Label(k,i1,strVal[0]);
				sheet.addCell(ll);
				Label l2 = new Label(k1,i1,strVal[1]);
				sheet.addCell(l2);
						%>
			<tr><td ><%=strVal[0]%><td><%=strVal[1]%></td></tr>
			<%}
			k++;
			k1++;%>
		</table>
		<%
		workbook.write();
		workbook.close();
		
		%>
		
		<br>
<br>
<html:button property="back" value="Close" onclick="javascript:window.close();"/>
		</center>



</body>
</html:html>