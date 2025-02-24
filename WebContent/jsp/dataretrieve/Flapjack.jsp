<%@ page language="java" %>
<%@ page import="java.io.File"%>
 
<html>
<head>
<title>GDMS- Running Batch file</title>

<link rel="stylesheet" href="<%=request.getContextPath() %>/jsp/common/GDMSStyleSheet.css" type="text/css">	
</head>
<%
	try{
		int exitval =1;		
		//System.out.println("Str value="+request.getParameter("str"));
		String batchType=request.getParameter("str");
		String realPath=request.getSession().getServletContext().getRealPath("//");
		String batchFileName="";
		String flapjakStatus="";
		
		//System.out.println("realPath in flapjack.jsp:"+realPath);
		if(batchType.equals("View in CMTV")){
			batchFileName=realPath+"jsp\\dataretrieve\\cmtvrun.bat";
			System.out.println("batch file path/...."+batchFileName);

			String[] cmd = {"cmd.exe", "/c", "start", "\""+"cmtv"+"\"", batchFileName};
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			
		}else if(batchType.equals("Visualize In Flapjack")){
			
			batchFileName=realPath+"\\jsp\\dataretrieve\\flapjackrun.bat";
			//System.out.println("batch file path/...."+batchFileName);
			//System.out.println("realPath=:"+realPath);
			File fexists=new File(realPath+"/Flapjack/Flapjack.flapjack");
			if(fexists.exists()) { fexists.delete(); 
			//System.out.println("proj exists and deleted");
			}
			//Process p = Runtime.getRuntime().exec("cmd.exe /c start " + batchFileName);
			String[] cmd = {"cmd.exe", "/c", "start", "\""+"flapjack"+"\"", batchFileName};
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			// Runtime runtime = Runtime.getRuntime(); 
			 // Process process = runtime.exec(cmd); 
			  //exitval = process.waitFor(); 
			  //process.exitValue();
		       // System.out.println("<<<<<<<<<<<<<<<<<<<<<<  "+ process.exitValue()); 

			//System.out.println(">...................... :"+process.getInputStream().toString());
			flapjakStatus="done";
			session.setAttribute("op", batchType);
		}else if(batchType.equals("Show Similarity Matrix")){
			//System.out.println("<<<<<<<<<<<<<<<<<<<<<<  ");
			batchFileName=realPath+"\\jsp\\dataretrieve\\flapjackMatrix.bat";
			//System.out.println("batch file path/...."+batchFileName);
			//System.out.println("Show Similarity Matrix   realPath=:"+realPath);
			File fexists=new File(realPath+"/Flapjack/Flapjack_matrix.txt");
			if(fexists.exists()) { fexists.delete(); 
			//System.out.println("proj exists and deleted");
			}
			//Process p = Runtime.getRuntime().exec("cmd.exe /c start " + batchFileName);
			String[] cmd = {"cmd.exe", "/c", "start", "\""+"flapjack"+"\"", batchFileName};
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			// Runtime runtime = Runtime.getRuntime(); 
			 // Process process = runtime.exec(cmd); 
			  //exitval = process.waitFor(); 
			  //process.exitValue();
		       // System.out.println("<<<<<<<<<<<<<<<<<<<<<<  "+ process.exitValue()); 

			//System.out.println(">...................... :"+process.getInputStream().toString());
			flapjakStatus="done";
			session.setAttribute("op", batchType);
		}
		/*else if(batchType.equals("Run Flapjack")){
			
			String filesPath=realPath+"/Flapjack";		
			Runtime.getRuntime().exec(filesPath+"/createproject.exe -map="+filesPath+"/Flapjack.map -genotypes="+filesPath+"/Flapjack.dat -project="+filesPath+"/Flapjack.flapjack");
			
		}*/
	
%>
<body>
<script>
function PreviousPage(){
	document.forms[0].action="../../genotypingpage.do?out";	
	//alert(document.forms[0].action);
	document.forms[0].submit();	
}
</script>
	
	<form method="post"> 
	<br>
	<center>
	<%

	if(batchType.equals("View in CMTV")){ %>
		<input type="button" name="Back" value=" Back " onclick="javascript:history.back()"/>
	<%}else if(batchType.equals("Visualize In Flapjack")){%>			
	 	<div class="displayText"><b><a href="FViewFiles.jsp">Download</a>&nbsp; the project file</b></div>	 	
	<%} else {%>		
	 	<div class="displayText"><b><a href="FViewFiles.jsp">Download</a>&nbsp; the Similarity Matrix file</b></div>	 	
	<%} %>
		<br><br><br>
	 	<input type="button" name="Back" value=" Back " onclick="PreviousPage()"/>
	</center>
	
	<br><br><br>
	</form>
</body>
<%}catch(Exception e){
	e.printStackTrace();
} %>
</html>

