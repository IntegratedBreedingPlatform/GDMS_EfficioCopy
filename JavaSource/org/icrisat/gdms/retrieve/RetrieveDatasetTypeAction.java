package org.icrisat.gdms.retrieve;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RetrieveDatasetTypeAction extends Action{

	java.sql.Connection conn;
	java.sql.Connection con;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		//System.out.println("............."+req.getParameter("str"));
		
		int datasetId=Integer.parseInt(req.getParameter("str"));
		int dataset_id=0;
		String dType="";
		String mappingType="no";
		Properties prop=new Properties();
		try{
			HttpSession session = req.getSession(true);
			/*ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();*/	
			
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
			
			
			ResultSet rsC=null;
			ResultSet rsL=null;
			ResultSet rs1=null;
			
			if(datasetId>0){
				rsC=stCen.executeQuery("select dataset_id, dataset_type from gdms_dataset where dataset_id='"+datasetId+"'");
				while(rsC.next()){
					dataset_id=rsC.getInt(1);
					dType=rsC.getString(2);
				}
				if(dType.equalsIgnoreCase("mapping")){
					rs1=stCen.executeQuery("select mapping_type from gdms_mapping_pop where dataset_id="+dataset_id);
					while(rs1.next()){
						mappingType=rs1.getString(1);
					}				
				}
			}else{
				
				rsL=stLoc.executeQuery("select dataset_id, dataset_type from gdms_dataset where dataset_id='"+datasetId+"'");
				while(rsL.next()){
					dataset_id=rsL.getInt(1);
					dType=rsL.getString(2);
				}
				if(dType.equalsIgnoreCase("mapping")){
					rs1=stLoc.executeQuery("select mapping_type from gdms_mapping_pop where dataset_id="+dataset_id);
					while(rs1.next()){
						mappingType=rs1.getString(1);
					}				
				}
			}
			
			
			session.setAttribute("mType", mappingType);
			
			/*if(rsL!=null) rsL.close(); if(rsC!=null) rsC.close(); 
			if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close();*/
			if(conn!=null) conn.close(); if(con!=null) con.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close(); conn.close();
		      		
		         }catch(Exception e){System.out.println(e);}
			}	
		
		return am.findForward("ret");
	}
	

}
