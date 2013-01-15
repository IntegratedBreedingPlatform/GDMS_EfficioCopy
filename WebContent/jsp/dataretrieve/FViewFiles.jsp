<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.io.ByteArrayOutputStream"%>



<%	
try
{
		System.out.println("here in the servlet");
				
		String realPath=getServletContext().getRealPath("\\"); 
		String fileName=realPath+"\\Flapjack\\Flapjack.flapjack";
		/*String filePath=session.getAttribute("FlapjackPath").toString();
		String sec=(String)request.getSession().getAttribute("msec");
		String user=(String)request.getSession().getAttribute("user");*/
		//String fileName=realPath+filePath+"/Flapjack.flapjack";
		System.out.println(fileName);
		File f=new File(fileName);


		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + "Flapjack.flapjack" + "\"");
		response.setHeader("Cache-Control", "no-cache");

		byte[] buf = new byte[response.getBufferSize()];
		response.setContentLength((int)f.length());
		System.out.println("file length : " + (int)f.length());
		System.out.println("here");
		int length;
		FileInputStream fis = null;
		BufferedInputStream fileInBuf = null;
		
		fileInBuf = new BufferedInputStream(new FileInputStream (f));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		while((length = fileInBuf.read(buf)) > 0) {
		baos.write(buf, 0, length);
		}
		
		response.getOutputStream().write(baos.toByteArray());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		}
		catch(Exception e)
		{
		System.out.println(e.getMessage());
		}
		finally{
			 
	}
%>