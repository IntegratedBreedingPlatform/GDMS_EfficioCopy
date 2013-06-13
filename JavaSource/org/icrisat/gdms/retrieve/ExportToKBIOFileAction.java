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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.HibernateUtil;

public class ExportToKBIOFileAction extends Action{

	java.sql.Connection conn;
	java.sql.Connection con;
	private static WorkbenchDataManager wdm;
	private static HibernateUtil hibernateUtil;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		Properties prop=new Properties();
		try{
			String foldername="InputFormats";
			 String op="kbio";
			 String pathWB="";
			 String filePathWB="";
			 //String bPath="C:\\IBWorkflowSystem\\infrastructure\\tomcat\\webapps\\GDMS";
			 String bPath=req.getSession().getServletContext().getRealPath("//");
			 String opPath=bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1);
			       
			    //System.out.println(",,,,,,,,,,,,,  :"+bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1));
			   
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
				
				
				DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters("DatabaseConfig.properties", "workbench");
		        hibernateUtil = new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), 
		                                workbenchDb.getUsername(), workbenchDb.getPassword());
		        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
		        wdm = new WorkbenchDataManagerImpl(sessionProvider);	
		        
		        HashMap<Object, String> IBWFProjects= new HashMap<Object, String>();
		        List<Project> projects = wdm.getProjects();
		        Long projectId = Long.valueOf(0);
		        //System.out.println("testGetProjects(): ");
		        for (Project project : projects) {
		            //System.out.println("  " + project.getLocalDbName());
		            projectId = project.getProjectId();
		            IBWFProjects.put(project.getLocalDbName(),project.getProjectId()+"-"+project.getProjectName());
		        }
		        //System.out.println(".........:"+IBWFProjects.get(dbNameL));
		        
		        pathWB=opPath+"/IBWorkflowSystem/workspace/"+IBWFProjects.get(dbNameL)+"/gdms/output";
		        //pathWB="C:/IBWorkflowSystem/workspace/1-TL1_Groundnut/gdms/output";
		        if(!new File(pathWB+"/K-bioOrderForms").exists())
			   		new File(pathWB+"/K-bioOrderForms").mkdir();
		        
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
				req.getSession().setAttribute("resultM", dataL);
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
			
			String fname1=req.getSession().getServletContext().getRealPath("//")+"/K-bioOrderForms";
			if(!new File(fname1).exists())
		       	new File(fname1).mkdir();
			//System.out.println("fname1="+fname1);
			String destFile=fname1+"/KBio"+mSec+".xls";
			String destFileWF=pathWB+"/K-bioOrderForms/KBio"+mSec+".xls";
			String srcFile=req.getSession().getServletContext().getRealPath("//")+"/"+"jsp"+"/"+"common"+"/"+"snp_template.xls";
			//String srcFile="d:\\snp_template.xls";
			//String destFile="d:\\sri\\kbio_snp_template.xls";
			InputStream oInStream = new FileInputStream(srcFile);
	        OutputStream oOutStream = new FileOutputStream(destFile);
	
	        OutputStream oOutStreamWF = new FileOutputStream(destFileWF);
	        
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
	        Row row = null;
	        int rowNum=2;
	        Cell cell = null;
	       
            for(int m1=0;m1<markersList.size();m1++){
		    	 int colnum = 0;
		    	 row = sheet.getRow(rowNum);	        
		    	 if(row == null){row = sheet.createRow(rowNum);}
                 cell = row.getCell(0);
                 if (cell == null)
		    		 cell = row.createCell(0);
                 cell.setCellValue(cell.getStringCellValue()+markersList.get(m1).toString());
    	
		    	 rowNum++;
		     }
	      
	        file.close();
	         
	        FileOutputStream outFile =new FileOutputStream(destFile);
	        workbook.write(outFile);
	        outFile.close();
	        
	             
	        FileInputStream fileWF = new FileInputStream(destFile);
	        
	        HSSFWorkbook workbookWF = new HSSFWorkbook(fileWF);
	        HSSFSheet sheetWF = workbook.getSheetAt(1);
	        Row rowWF = null;
	        int rowNumWF=2;
	        Cell cellWF = null;
	       
            for(int m1=0;m1<markersList.size();m1++){
		    	 int colnum = 0;
		    	 rowWF = sheet.getRow(rowNumWF);	        
		    	 if(rowWF == null){rowWF = sheetWF.createRow(rowNumWF);}
                 cellWF = rowWF.getCell(0);
                 if (cellWF == null)
		    		 cellWF = rowWF.createCell(0);
                 cellWF.setCellValue(cellWF.getStringCellValue()+markersList.get(m1).toString());
    	
		    	 rowNumWF++;
		     }
	      
	        fileWF.close();
	         
	        FileOutputStream outFileWF =new FileOutputStream(destFileWF);
	        workbookWF.write(outFileWF);
	        outFileWF.close();
	       
	        
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
