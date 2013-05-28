package org.icrisat.gdms.upload;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RetrieveDataAction extends Action{
	
	Connection con=null;
	Connection conn=null;
	ResultSet rsC=null;
	ResultSet rsL=null;
	//Statement st=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		PrintWriter pr = res.getWriter();
		res.setContentType("text/xml");
		res.setHeader("Cache-Control", "no-cache");
		String data = req.getParameter("data");
		
		String type = req.getParameter("type");
		//System.out.println("value == "+data+"    type=="+type);
		String sqlQuery = "";
		//String crop=req.getSession().getAttribute("crop").toString();
		try{
		Properties p=new Properties();
		
		p.load(new FileInputStream(req.getSession().getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
		String host=p.getProperty("central.host");
		String port=p.getProperty("central.port");
		String url = "jdbc:mysql://"+host+":"+port+"/";
		String dbName = p.getProperty("central.dbname");
		String driver = "com.mysql.jdbc.Driver";
		String userName = p.getProperty("central.username"); 
		String password = p.getProperty("central.password");
		
		Class.forName(driver).newInstance();
		 conn = DriverManager.getConnection(url+dbName,userName,password);
		 Statement stCen=conn.createStatement();
			
			
		 String hostL=p.getProperty("local.host");
		 String portL=p.getProperty("local.port");
		 String urlL = "jdbc:mysql://"+hostL+":"+portL+"/";
		 String dbNameL = p.getProperty("local.dbname");
		 //String driver = "com.mysql.jdbc.Driver";
		 String userNameL = p.getProperty("local.username"); 
		 String passwordL = p.getProperty("local.password");
		 
		 Class.forName(driver).newInstance();
		 con = DriverManager.getConnection(urlL+dbNameL,userNameL,passwordL);
		 Statement stLoc=con.createStatement();
		ArrayList retList=new ArrayList();
		
		
		//st = con.createStatement();
		if(type.equalsIgnoreCase("mapping")){
			rsC = stCen.executeQuery("select map_name from gdms_map");
			rsL = stLoc.executeQuery("select map_name from gdms_map");
			while(rsC.next()){
				retList.add(rsC.getString(1));
			}
			while(rsL.next()){
				if(!retList.contains(rsL.getString(1)))
					retList.add(rsL.getString(1));
			}
			//query=hsession.createQuery(sqlQuery);
			pr.println("<data>");
			pr.println("<details><![CDATA[- Select -]]></details>");
			for(int r=0; r<retList.size();r++){
				String str = retList.get(r).toString();	
				//System.out.println(str);
				pr.println("<details><![CDATA[" + str + "]]></details>");
				
			}
			
			pr.println("</data>");	 
		
			return null;
		}else if(type.equalsIgnoreCase("map")){
			rsC = stCen.executeQuery("select dataset_name from gdms_dataset where dataset_type='mapping'");
			rsL = stLoc.executeQuery("select dataset_name from gdms_dataset where dataset_type='mapping'");
			//query=hsession.createQuery(sqlQuery);
			
			while(rsC.next()){
				retList.add(rsC.getString(1));
			}
			while(rsL.next()){
				if(!retList.contains(rsL.getString(1)))
					retList.add(rsL.getString(1));
			}
			pr.println("<data>");
			pr.println("<details><![CDATA[- Select -]]></details>");
			for(int r=0; r<retList.size();r++){
				String str = retList.get(r).toString();	
				//System.out.println(str);
				pr.println("<details><![CDATA[" + str + "]]></details>");
				
			}
			
			pr.println("</data>");	 
			/*if(rsC!=null) rsC.close(); if(rsL!=null) rsL.close(); if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close();*/
			if(con!=null) con.close(); if(conn!=null) conn.close();
			
			return null;
		}
		}catch(Exception e){
			e.printStackTrace();
			//hsession.clear();		
			//tx.rollback();
		}finally{
			try{
				//hsession.clear();
				//closing open connection, Resultset, Statement objects.
				if(con!=null)con.close(); conn.close();
				//if(stCen!=null)st.close();
				//if(rs!=null)rs.close();
			}catch(Exception e){
				System.out.println("Exception :"+e);
			}
		}
		
		return am.findForward("test");
	}
	

}
