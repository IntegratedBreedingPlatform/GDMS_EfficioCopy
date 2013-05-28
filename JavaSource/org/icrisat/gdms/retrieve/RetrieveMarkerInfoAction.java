package org.icrisat.gdms.retrieve;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Query;

public class RetrieveMarkerInfoAction extends Action{
	
	java.sql.Connection conn;
	java.sql.Connection con;
	
	ResultSet rsC=null;
	ResultSet rsL=null;
	Statement stCen=null;
	Statement stLoc=null;
	String str1="";
	Query query=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {		
		try{			
			Properties prop=new Properties();			
			//System.out.println("USER="+req.getSession().getAttribute("user"));		
			PrintWriter pr = res.getWriter();
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");
			String ORGvalue = req.getParameter("data");
			String[] value=ORGvalue.split("!!");
			String type = req.getParameter("type");
			//System.out.println("value == "+value[0]+"  "+value[1]+"  type=="+type);
			String sqlQuery = "";
					
			
			prop.load(new FileInputStream(req.getSession().getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
			String host=prop.getProperty("central.host");
			String port=prop.getProperty("central.port");
			String url = "jdbc:mysql://"+host+":"+port+"/";
			String dbName = prop.getProperty("central.dbname");
			String driver = "com.mysql.jdbc.Driver";
			String userName = prop.getProperty("central.username"); 
			String password = prop.getProperty("central.password");
			//System.out.println(host+"   "+port+"   "+url+"   "+dbName+"   "+driver+"   "+userName+"   "+password);
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);
			stCen=conn.createStatement();	
			
			
			String hostL=prop.getProperty("local.host");
			String portL=prop.getProperty("local.port");
			String urlL = "jdbc:mysql://"+hostL+":"+portL+"/";
			String dbNameL = prop.getProperty("local.dbname");
			//String driver = "com.mysql.jdbc.Driver";
			String userNameL = prop.getProperty("local.username"); 
			String passwordL = prop.getProperty("local.password");
			
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(urlL+dbNameL,userNameL,passwordL);
			stLoc=con.createStatement();
						
			ArrayList combinedList=new ArrayList();
			
			//if (value[0].equals("Marker_Type")) {
			if (ORGvalue.contains("Marker_Type")) {	
				//System.out.println("1st if");
				if (type.equals("")) {
					//System.out.println("if inside of 1st if");
					sqlQuery = "Select Distinct Marker_Type from gdms_marker where marker_type != 'UA' and marker_type !='DArT'";
				} else {
					//System.out.println("else if inside of 1st if");
					//System.out.println("Select Marker_Name from marker where Lower(Marker_Type)='"+ type.toLowerCase() + "'");
					//Query = "Select Marker_Name from marker_info inner Join marker_type on Marker_Type.marker_type_id=marker_info.marker_type_id where Marker_Type.Marker_Type='"	+ type + "'";
					sqlQuery = "Select Marker_Name from gdms_marker where Lower(Marker_Type)='"+ type.toLowerCase() + "'";
				}
			} else if(ORGvalue.contains("Crop")){
				//System.out.println("else of 1st if");
				//System.out.println("...............type="+type);
				if(value[1].equalsIgnoreCase("list3")){
					//System.out.println("if inside of 1st if");
					String[] ret=type.split("!~!");
					sqlQuery="select distinct marker_name from gdms_marker where crop='"+ ret[1]+"' and marker_type='"+ret[0]+"'";
				}else{
					//System.out.println("else if inside of 1st if");
					if (type.equals("")) {
						sqlQuery = "Select Distinct(" + value[0] + ") from gdms_marker";
					} else {
						sqlQuery = "Select distinct marker_type from gdms_marker where "
								+ value[0] + "='" + type + "' and marker_type !='UA'";
					}
				}
			} else {
				if (type.equals("")) {
					sqlQuery = "Select Distinct(" + value[0] + ") from gdms_marker where "+value[0]+" != 'null' or "+value[0]+" !=''";
				} else {
					sqlQuery = "Select Marker_Name from gdms_marker where "
							+ value[0] + "='" + type + "'";
				}
			}
			//System.out.println("query="+sqlQuery);
			//st = con.createStatement();
			//resultset object
			ArrayList cList=new ArrayList();
			rsC=stCen.executeQuery(sqlQuery);
			rsL=stLoc.executeQuery(sqlQuery);
			while(rsC.next()){
				cList.add(rsC.getString(1));
				combinedList.add(rsC.getString(1));
			}
			while(rsL.next()){
				cList.add(rsL.getString(1));
				if(!combinedList.contains(rsL.getString(1)))
					combinedList.add(rsL.getString(1));
			}
		
			//System.out.println("combinedList.size="+combinedList.size()+"   "+combinedList);
			//System.out.println(cList.size()+"  "+cList);
			//query=hsession.createQuery(sqlQuery);
			pr.println("<data>");
			pr.println("<details><![CDATA[- Select -]]></details>");
			for(int l=0;l<combinedList.size();l++){
				pr.println("<details><![CDATA[" + combinedList.get(l) + "]]></details>");
			
			}			
			pr.println("</data>");
			
			/*if(rsC!=null)rsC.close();
			if(rsL!=null)rsL.close();
			if(stCen!=null)stCen.close();
			if(stLoc!=null)stLoc.close();*/
			if(con!=null)con.close();
			if(conn!=null)conn.close();
			
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			//hsession.clear();		
			//tx.rollback();
		}finally{
			try{
				//hsession.clear();
				//closing open connection, Resultset, Statement objects.
				if(con!=null)con.close();
				if(conn!=null)conn.close();
				if(stCen!=null)stCen.close();
				if(stLoc!=null)stLoc.close();
				if(rsC!=null)rsC.close();
				if(rsL!=null)rsL.close();
			}catch(Exception e){
				System.out.println("Exception :"+e);
			}
		}
		str1="return";		
		return am.findForward(str1);
	}
}
