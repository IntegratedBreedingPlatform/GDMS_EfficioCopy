<%@ page language="java" %>
<%@ page import="java.io.File"%>
 
<html>
<head><title>GDMS- Running Batch file</title></head>
<%
	try{
		int exitval =1;		
		System.out.println("Str value="+request.getParameter("str"));
		String batchType=request.getParameter("str");
		String realPath=request.getSession().getServletContext().getRealPath("//");
		String batchFileName="";
		String flapjakStatus="";
		System.out.println("realPath in flapjack.jsp:"+realPath);
		if(batchType.equals("View in CMTV")){
			batchFileName=realPath+"jsp\\dataretrieve\\cmtvrun.bat";
			System.out.println("batch file path/...."+batchFileName);

			String[] cmd = {"cmd.exe", "/c", "start", "\""+"cmtv"+"\"", batchFileName};
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			
		}else if(batchType.equals("Run Flapjack")){
			batchFileName=realPath+"\\jsp\\dataretrieve\\flapjackrun.bat";
			System.out.println("batch file path/...."+batchFileName);
			
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
			
		}
		/*else if(batchType.equals("Run Flapjack")){
			
			String filesPath=realPath+"/Flapjack";		
			Runtime.getRuntime().exec(filesPath+"/createproject.exe -map="+filesPath+"/Flapjack.map -genotypes="+filesPath+"/Flapjack.dat -project="+filesPath+"/Flapjack.flapjack");
			
		}*/
	
%>
<body>
<script>
function PreviousPage(){
	document.forms[0].action="../../genotypingpage.do?second";	
	document.forms[0].submit();	
}
</script>
	<br>
	<center>
	<%

	if(batchType.equals("View in CMTV")){ %>
		<input type="button" name="Back" value=" Back " onclick="javascript:history.back()"/>
	<%}else { %>	 	
	 	<div class="displayText"><a href="FViewFiles.jsp">Download </a> the project file</div><br><br><br>
	 	<input type="button" name="Back" value=" Back " onclick="PreviousPage()"/>
	<%} %>
	</center>
	
	<br><br><br>
	<form name='BackFrm'> 
	</form>
</body>
<%}catch(Exception e){
	e.printStackTrace();
} %>
</html>

