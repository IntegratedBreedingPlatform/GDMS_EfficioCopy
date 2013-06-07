<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.ArrayList,java.util.Iterator,jxl.format.*" %>
<%@ page import="java.io.*,jxl.*,jxl.write.*,java.util.Calendar,java.util.StringTokenizer, org.apache.commons.io.FileUtils"%>
<html:html>
	<head>

	<title>GDMS</title>
	<link rel="stylesheet" type="text/css" href="<html:rewrite forward='GDMSStyleSheet'/>">
	<style>
		body {
			padding: 0;
			font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
			font-size: 13px;
			color: #837669;
		}		
		
		.texttyp{
			height: 5%;
			margin-bottom: 3px;
			padding: 1% 1% 1% 18%;
			text-align:left;
			//color: #FFFFFF;
			color: black;
			font-weight: bold;
			font-family: Georgia, "Times New Roman", Times, serif;	
		}
		.tit_bot { 
			background: url(images/left_bg1.jpg) center top no-repeat;						
			padding: 0% 0% 1% 1%;
		}
		.tit { 
			background: url(images/left_b2.jpg) repeat-y center;						
			padding: 0px 0px 0px 0px;
		}
		.tit_top { 
			background: url(images/left_bg31.jpg) center top no-repeat;						
			padding: 2% 0% 0% 0%;
		}
		.cls1{
				OVERFLOW-X: auto; OVERFLOW-Y: auto;
				Width:95%;
				
				padding-right: 5px;
				padding-left:5px;
				padding-top:5px;
				//padding-bottom:50px;
				//border-color: #E4EAF8;
				border-left-style: inset;
				border-right-style: inset;
				border-top-style: inset;
				border-bottom-style: inset;
				background-color: #FBF9EA;
				height: 350px;
			}
			td{
			 font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
			font-size: 13px;
			font-weight:bold;
			font-stretch:narrower;
			color: black;
			}
			.td1{
			font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
			font-size: 13px;
			font-weight:bold;
			font-stretch:narrower;
			text-decoration:underline;
			border:thin;
			bordercolor:#B1E1F9; 
			color: white;
			width: 100px;
			}
		</style>
	</head>
	<body>
		<html:form action="/retrieveInfo.do">
			<div class="heading" align="center">Marker Data Retrieval</div>
			<!--<table width='35%' border=0 align=right>
				<tr><td align=left nowrap class="displayBoldText" width="20%">Species</td><td><b>:</b></td><td align=left nowrap class="displayBoldText"><%=session.getAttribute("crop") %></td></tr>		
			</table>
			-->
			<br><br>
			<center>
			<%ArrayList mdet=(ArrayList)session.getAttribute("al");%>
			<%
			if(mdet.size()>0){
			Calendar now = Calendar.getInstance();
			String foldername="MarkerFiles";
			String fname1=session.getServletContext().getRealPath("//")+"/"+foldername;
			if(new File(fname1).exists()){				
				FileUtils.cleanDirectory(new File(fname1)); 
			}
			
			if(!new File(fname1).exists())
		       	new File(fname1).mkdir();
			//System.out.println("fname1="+fname1);
			String pathWB=request.getSession().getAttribute("WBPath").toString();
			if(!new File(pathWB).exists())
		       	new File(pathWB).mkdir();
			String createfileWB=pathWB+"/Marker"+now.getTimeInMillis()+".xls";
			File fileWB=new File(createfileWB);
			fileWB.createNewFile();		
			
			String createfile=fname1+"/Marker"+now.getTimeInMillis()+".xls";
			File file=new File(createfile);
			file.createNewFile();	
			
			WritableWorkbook workbookWB = Workbook.createWorkbook(new File(createfileWB));
			WritableSheet sheetWB=workbookWB.createSheet("MarkerDetails",0);			
			
			WritableWorkbook workbook = Workbook.createWorkbook(new File(createfile));
			WritableSheet sheet=workbook.createSheet("MarkerDetails",0);
			
			WritableFont wf = new WritableFont(WritableFont.TIMES,WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD,false,UnderlineStyle.SINGLE);
		   	WritableCellFormat cf = new WritableCellFormat(wf);
		    cf.setWrap(true); 
			
		    
		    String filenm=foldername+"/Marker"+now.getTimeInMillis()+".xls";
			
			//System.out.println("filenm="+filenm);
			
			%>
			
			<a href='<%=filenm%>' target="_blank">Download Excel Format file</a>
			<br><br>
			<%int k=0;		
				//ArrayList mdet=(ArrayList)session.getAttribute("al");
				//System.out.println("mdet in jsp="+mdet);
				for(Iterator iter=mdet.iterator();iter.hasNext();){	
					ArrayList  al=(ArrayList)iter.next();
					int i=0,len=0;%>
					<br>
					<font style="font-family : verdana;	color: #2E566F;font-size   : 12px;font-weight : bold;font: italic;">
						Found a total of <%=al.size()-1 %> results.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 					</font>
					<div class="cls1" align="center" >
						<table border=1 cellpadding="3" cellspacing="0">
							<%
							for(Iterator it=al.iterator();it.hasNext();i++){
								String str=(String)it.next();
								String[] splitStr=str.split("!!~~");
					
								String color="";
								if(i==0){						
									len=splitStr.length;
								}					
							%>
							<tr>
					
							<%for(int p=0;p<splitStr.length;p++){ 
								if(i==0){%>
									<td bgcolor="#006633" class="td1" nowrap >
								<%}else{ %>
									<td nowrap>
								<%}
								if(i>0 &&(p==0)){
									String path="";
									String p1="";
									if(p==0){
										StringTokenizer stk =new StringTokenizer(splitStr[p]," ");
										int count= stk.countTokens();
										while(stk.hasMoreTokens()){								
											p1=p1+stk.nextToken()+"%20";		
										}
										String pathF=p1.substring(0,p1.length()-3);
										path="http://cmap.icrisat.ac.in/cgi-bin/cmap_public/feature_search?features="+pathF+"&search_field=feature_name&order_by=&data_source=CMAP_PUBLIC&submit=Submit";
										%>
										<a href=<%=path %> target="new">
											<%=splitStr[p] %>
										</a>
										<%
									}					
									//}else if(i>0 &&(p==9)){
								}else if(i>0 &&(splitStr[p].contains("!~!"))){
									String path="";
									String[] splitGenotype=splitStr[p].split("!~!");
									//System.out.println("************************** "+splitGenotype.length);
									if(Integer.parseInt(splitGenotype[0])==0){%>
										<%=splitGenotype[0] %>
									<%}else{
										path="RetrieveGenotypes.do?data="+splitGenotype[1];
									%>
										<a href='<%=path %>' target="new">								
											<%=splitGenotype[0] %>
										</a>
									<%}							
								}else{							
									if((splitStr[p].equals(""))||(splitStr[p].equals("0"))||(splitStr[p].equals("0.0"))||(splitStr[p].equals("null"))||(splitStr[p].equals(null))){
									%>&nbsp;
									<%
									}else{%>
										<%=splitStr[p] %>
									<%}
								}%>
							
							</td>
							
							<%
							if(splitStr[p].contains("!~!")){
								String[] splitGenotype=splitStr[p].split("!~!");
								Label ll = new Label(p,k,splitGenotype[0]);
								sheet.addCell(ll);
								Label llW = new Label(p,k,splitGenotype[0]);
								sheetWB.addCell(llW);
							}else{					
								Label ll = new Label(p,k,splitStr[p]);
								sheet.addCell(ll);
								Label llW = new Label(p,k,splitStr[p]);
								sheetWB.addCell(llW);
							}
							if((p==(splitStr.length-1))&& (splitStr.length<len)){
								for(p=splitStr.length;p<len;p++){
								%>
									<td nowrap>&nbsp;</td>
								<%
								}
							}
						} 
						k++;%>				
					</tr>
					<%} %>
					</table></div>
 					<%
					}
					workbook.write();
					workbook.close();
					workbookWB.write();
					workbookWB.close();
					
					}else{
				
				String searchType=session.getAttribute("searchType").toString();
				//System.out.println("search type=:"+searchType);
				String arg=session.getAttribute("quickSearchArg").toString();
				if(searchType.equalsIgnoreCase("QuickSearch")){
						
					%>
					<br><br>
					<div style="font-size: 14px; color: red;">No markers found starting with '<%=arg%>'</div><BR><BR>
				<%}else{ 
					String[] opType=null;
					String op="";
					//System.out.println("count=:"+session.getAttribute("count"));
					int count=Integer.parseInt(session.getAttribute("count").toString());
					if(count==3 || count==2){
						opType=session.getAttribute("quickSearchOp").toString().split("!!~~");
					}else {
						op=session.getAttribute("quickSearchOp").toString();
					}
					//System.out.println("op=:"+session.getAttribute("quickSearchOp"));
					%>
					<div style="font-size: 14px; color: red;">No markers found that are
					<%
					if(count==3){
						String marker = session.getAttribute("marker").toString();
						String next1=session.getAttribute("next1").toString();
						String next2=session.getAttribute("next2").toString();
						String[] temp=session.getAttribute("temp").toString().split("!!~~!!");
					%>
					 starting with '<%=marker%>' <%=next1.toLowerCase()%> '<%=arg%>' <%=next2.toLowerCase()%> with annealing temperature between '<%=temp[0]%>' and '<%=temp[1]%>'</div>
					
						
					<%}else if(count==2){
						String next1=session.getAttribute("next1").toString();
						String next2=session.getAttribute("next2").toString();
						String str="";
						for(int o=0;o<opType.length;o++){
							if(opType[o].equalsIgnoreCase("marker")){	
								String marker = session.getAttribute("marker").toString();
								str=str+" starting with '"+marker+"'";
							}
							if(opType[o].equalsIgnoreCase("ampAlso")){	
								if((!(next1.equalsIgnoreCase(" ")))||(!(next1.equalsIgnoreCase("null")))||(!(next1.equalsIgnoreCase(null))))
									str=str+" "+next1.toLowerCase();
								str=str+" '"+arg+"'";
							}
							if(opType[o].equalsIgnoreCase("annAlso")){
								String[] temp=session.getAttribute("temp").toString().split("!!~~!!");
								if((!(next2.equalsIgnoreCase(" ")))||(!(next2.equalsIgnoreCase("null")))||(!(next2.equalsIgnoreCase(null))))
									str=str+" "+next2.toLowerCase();
								str=str+" with annealing temperature between '"+temp[0]+"' and '"+temp[1]+"'";
							}
						}%>
						<%=str%>
					<%}else{
						String str="";
						String op1=op.substring(0,op.length()-4);
						if(op1.equalsIgnoreCase("marker")){	
								String marker = session.getAttribute("marker").toString();
								str=str+" starting with '"+marker+"'";
							}
							if(op1.equalsIgnoreCase("ampAlso")){								
								str=str+" '"+arg+"'";
							}
							if(op1.equalsIgnoreCase("annAlso")){
								//System.out.println(session.getAttribute("temp"));
								String[] temp=session.getAttribute("temp").toString().split("!!~~!!");
								
								str=str+" with annealing temperature between '"+temp[0]+"' and '"+temp[1]+"'";
							}%>
							<%=str %>
					<%} %>
				<%} %>
				<BR><BR>
			<%} %>
					<br>
				<html:button property="back" value="Back" onclick="javascript:history.back()"/>
				<%System.out.println(".............................."+request.getSession().getAttribute("markerType")); %>
				<%if(request.getSession().getAttribute("markerType").equals("snp")){ %>
					<html:button property="nButton" onclick="funcSubmitPage()" value="Create KBio Order Form"/>
					<html:hidden property="kbioMarkers"/>
					<html:hidden property="fromPage" value="markers"/>
				<%} %>
		</center>
		</html:form>
	</body>
</html:html>
<script>
function funcSubmitPage(val){
		//document.forms[0].kbioMarkers.value='<%=request.getSession().getAttribute("kbioMarkers")%>';
		document.forms[0].action="exportKBIOFile.do?markers";
		document.forms[0].submit();
	
}
</script>


