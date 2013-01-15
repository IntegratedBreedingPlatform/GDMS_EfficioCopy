<%@ page language="java" %>
<%@ page import="java.io.File"%>
 
<html>
<head><title>GDMS- Running Batch file</title></head>
<%
	try{
				
		System.out.println("Str value="+request.getParameter("str"));
		String batchType=request.getParameter("str");
		String realPath=getServletContext().getRealPath("//"); 
		String batchFileName="";
		
		if(batchType.equals("View in CMTV")){
			batchFileName=realPath+"jsp\\dataretrieve\\cmtvrun.bat";
			System.out.println("batch file path/...."+batchFileName);

			String[] cmd = {"cmd.exe", "/c", "start", "\""+"cmtv"+"\"", batchFileName};
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			
		}else if(batchType.equals("Run Flapjack")){
			batchFileName=realPath+"\\jsp\\dataretrieve\\flapjackrun.bat";
			System.out.println("batch file path/...."+batchFileName);

			String[] cmd = {"cmd.exe", "/c", "start", "\""+"flapjack"+"\"", batchFileName};
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			System.out.println("..............  in jsp");
		}
		/*else if(batchType.equals("Run Flapjack")){
			//String filesPath=session.getAttribute("FlapjackPath").toString();
			
			String sec=(String)request.getSession().getAttribute("msec");
			String user=(String)request.getSession().getAttribute("user");
			String filesPath=realPath+"\\Flapjack\\OutputFiles\\"+sec+user;
			System.out.println("RealPath="+realPath);
			System.out.println("filesPath="+filesPath);
		
			//Runtime.getRuntime().exec(realPath+"\\Flapjack\\createproject.exe -map="+filesPath+"\\Flapjack.map -genotypes="+filesPath+"\\Flapjack.dat -project="+filesPath+"\\Flapjack.flapjack");
			/*String[] cmd = {""+realPath+"/Flapjack/createproject.exe", "-map='"+filesPath+"/Flapjack.map'", "-genotypes='"+filesPath+"/Flapjack.dat'", "-project='"+filesPath+"/Flapjack.flapjack'"};
			for(int c=0;c<cmd.length;c++){
				System.out.println(cmd[c]);
			}
			
			Process process = Runtime.getRuntime().exec(cmd);
			*/
	//	} 
	
%>
<body>

	<form name='BackFrm' action="Flapjack.jsp"> 
	<input type="submit">
	</form>
</body>
<%}catch(Exception e){
	e.printStackTrace();
} %>
</html>

