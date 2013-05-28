package org.icrisat.gdms.retrieve;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ExportToKBIOFileAction extends Action{

	java.sql.Connection conn;
	java.sql.Connection con;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		Properties prop=new Properties();
		try{
			 String op="kbio";
			/*ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	*/
			 
			 prop.load(new FileInputStream(req.getSession().getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
				String host=prop.getProperty("central.host");
				String port=prop.getProperty("central.port");
				String url = "jdbc:mysql://"+host+":"+port+"/";
				String dbName = prop.getProperty("central.dbname");
				String driver = "com.mysql.jdbc.Driver";
				String userName = prop.getProperty("central.username"); 
				String password = prop.getProperty("central.password");
				
				Class.forName(driver).newInstance();
				conn = DriverManager.getConnection(url+dbName,userName,password);
				Statement stCen=conn.createStatement();
				
				
				String hostL=prop.getProperty("local.host");
				String portL=prop.getProperty("local.port");
				String urlL = "jdbc:mysql://"+hostL+":"+portL+"/";
				String dbNameL = prop.getProperty("local.dbname");
				//String driver = "com.mysql.jdbc.Driver";
				String userNameL = prop.getProperty("local.username"); 
				String passwordL = prop.getProperty("local.password");
				
				Class.forName(driver).newInstance();
				con = DriverManager.getConnection(urlL+dbNameL,userNameL,passwordL);
				Statement stLoc=con.createStatement();
			 
			//Statement st=con.createStatement();
			ResultSet rsC=null;
			ResultSet rsL=null;
			String[] data=null;
			String markers="";
			Calendar now = Calendar.getInstance();
			String mSec=now.getTimeInMillis()+"";
			req.getSession().setAttribute("msec", mSec);
			ArrayList markersList = new ArrayList();
			//System.out.println(req.getParameter("dataToExp"));
			String option=req.getQueryString();
			//System.out.println("option=:"+option);
			if(option.equalsIgnoreCase("map")){			
				data=req.getParameter("dataToExp").split("~~!!~~");
				for(int d=0;d<data.length;d++){
					String[] strData=data[d].split("!~!");
					//markersList.add(strData[1]);
					markers=markers+"'"+strData[1]+"',";
				}
				
			}else if(option.equalsIgnoreCase("poly")){	
				ArrayList dataL=new ArrayList();
				dataL=(ArrayList)req.getSession().getAttribute("result");
				/*String dataL=req.getParameter("hPolyMarkers");
				System.out.println("........................:"+dataL);
				markers=dataL;*/
				//System.out.println("........................:"+dataL);
				for(int d=0;d<dataL.size();d++){
					markers=markers+"'"+dataL.get(d)+"',";
				}
				
			}
			//destFile="InputFormats/KBio"+session.getAttribute("msec")+".xls";
			 req.getSession().setAttribute("exop", op);
			if(option.equalsIgnoreCase("markers")){
				//data=req.getParameter("kbioMarkers").split("~~!!~~");
				String m=req.getSession().getAttribute("kbioMarkers").toString();
				//markersList=(ArrayList)req.getSession().getAttribute("kbioMarkers");
				markers=m;
			}
			markers=markers.substring(0, markers.length()-1);
			//System.out.println("markers=:"+markers);
			//System.out.println("select marker_name from gdms_marker where marker_name in("+markers+") and lower(marker_type)='snp'");
			rsC=stCen.executeQuery("select marker_name from gdms_marker where marker_name in("+markers+") and lower(marker_type)='snp'");
			while(rsC.next()){
				markersList.add(rsC.getString(1));
			}
			rsL=stLoc.executeQuery("select marker_name from gdms_marker where marker_name in("+markers+") and lower(marker_type)='snp'");
			while(rsL.next()){
				if(!markersList.contains(rsL.getString(1)))
					markersList.add(rsL.getString(1));
			}
			
			
			//System.out.println("markersList.size=:"+markersList.size());
			if(markersList.size()==0){
				String ErrMsg = "No SNP Marker(s) to create KBio Order form";
				//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				req.getSession().setAttribute("indErrMsg", ErrMsg);
				if(option.equalsIgnoreCase("map")){	
					//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&");
					return am.findForward("ErrMsgMap");
				}else{
					//System.out.println("_____________________________");
					return am.findForward("ErrMsgP");
				}
			}
			//System.out.println("markersList=:"+markersList);
			String foldername="InputFormats";
			String fname1=req.getSession().getServletContext().getRealPath("//")+"/"+foldername;
			if(!new File(fname1).exists())
		       	new File(fname1).mkdir();
			//System.out.println("fname1="+fname1);
			String destFile=fname1+"/KBio"+mSec+".xls";
			String srcFile=req.getSession().getServletContext().getRealPath("//")+"/"+"jsp"+"/"+"common"+"/"+"snp_template.xls";
			//String srcFile="d:\\snp_template.xls";
			//String destFile="d:\\sri\\kbio_snp_template.xls";
			InputStream oInStream = new FileInputStream(srcFile);
	        OutputStream oOutStream = new FileOutputStream(destFile);
	
	        // Transfer bytes from in to out
	        byte[] oBytes = new byte[1024];
	        int nLength;
	        BufferedInputStream oBuffInputStream = 
	                        new BufferedInputStream( oInStream );
	        while ((nLength = oBuffInputStream.read(oBytes)) > 0) 
	        {
	            oOutStream.write(oBytes, 0, nLength);
	        }
	        oInStream.close();
	        oOutStream.close();
	        
	        FileInputStream file = new FileInputStream(destFile);
	        
	        HSSFWorkbook workbook = new HSSFWorkbook(file);
	        HSSFSheet sheet = workbook.getSheetAt(1);
	       
	    
	        int rowNum=2;
	        Cell cell = null;
	       
	     
		     for(int m1=0;m1<markersList.size();m1++){
		    	//System.out.println(m1+":"+markersList.get(m1));
		    	 cell = sheet.getRow(rowNum).getCell((short)0);
		    	 if (cell == null)
		    		 cell = sheet.getRow(rowNum).createCell((short)0);
		    	 cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		    	 cell.setCellValue(markersList.get(m1).toString());		    	
		    	 rowNum++;
		     }
	      
	        file.close();
	         
	        FileOutputStream outFile =new FileOutputStream(destFile);
	        workbook.write(outFile);
	        outFile.close();
	        //System.out.println("op=:"+op);
	       
	       // System.out.println("done");
	        
	        if(rsC!=null) rsC.close();if(rsL!=null) rsL.close();if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close();
	        if(conn!=null)conn.close(); if(con!=null) con.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				//closing open connection, Resultset, Statement objects.
				if(con!=null)con.close(); conn.close();
				//if(stCen!=null)st.close();
				//if(rs!=null)rs.close();
			}catch(Exception e){
				System.out.println("Exception :"+e);
			}
		}

		
		
		return am.findForward("exp");
		
	}
	
	

}
