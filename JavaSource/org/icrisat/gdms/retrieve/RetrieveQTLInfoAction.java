/**
 * Retrieves markers under a particular QTL
 */
package org.icrisat.gdms.retrieve;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RetrieveQTLInfoAction extends Action{
	java.sql.Connection conn;
	java.sql.Connection con;
	String qtlName="";
	String map="";String chromosome="";
	float min=0;float max=0;
	int linkageMapId=0;
	String markers="";
	String qtl="";
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		ActionErrors ae = new ActionErrors();	
		if(session!=null){
			session.removeAttribute("markers");
			session.removeAttribute("qtlName");
		}
		
		qtl=req.getParameter("str");
		String[] data=qtl.split("!~!");
		qtlName=data[0];
		map=data[1];
		chromosome=data[2];
		min=Float.parseFloat(data[3]);
		max=Float.parseFloat(data[4]);
		Properties prop=new Properties();
		try{
			//String crop=req.getSession().getAttribute("crop").toString();
			
			/*ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			*/
			prop.load(new FileInputStream(session.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
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
			ResultSet rs1C=null;
			ResultSet rs1L=null;
			
			/** Retrieve qtl id of a particular qtl **/
			
			//System.out.println("select map_id from qtl_details where qtl_id=(select qtl_id from qtl where qtl_name='"+qtlName+"')");
			rsC=stCen.executeQuery("select map_id from gdms_qtl_details where qtl_id=(select qtl_id from gdms_qtl where qtl_name='"+qtlName+"')");
			while(rsC.next()){
				linkageMapId=rsC.getInt(1);
			}
			rsL=stLoc.executeQuery("select map_id from gdms_qtl_details where qtl_id=(select qtl_id from gdms_qtl where qtl_name='"+qtlName+"')");
			while(rsL.next()){
				linkageMapId=rsL.getInt(1);
			}
			markers="";
			
			/** Retrieves markers under a QTL  **/
			
			//System.out.println("select marker_name from marker where marker_id in(select marker_id from markers_onmap where map_id="+linkageMapId+" and linkage_group='"+chromosome+"' and start_position between "+min+" AND "+max+")");
			rs1C=stCen.executeQuery("select marker_name from gdms_marker where marker_id in(select marker_id from gdms_markers_onmap where map_id="+linkageMapId+" and linkage_group='"+chromosome+"' and start_position between "+min+" AND "+max+")");
			while(rs1C.next()){
				//System.out.println(rs1.getString(1));
				markers=markers+rs1C.getString(1)+"!~!";				
			}
			rs1L=stLoc.executeQuery("select marker_name from gdms_marker where marker_id in(select marker_id from gdms_markers_onmap where map_id="+linkageMapId+" and linkage_group='"+chromosome+"' and start_position between "+min+" AND "+max+")");
			while(rs1L.next()){
				markers=markers+rs1L.getString(1)+"!~!";				
			}
			
			//System.out.println("markers="+markers);
			req.getSession().setAttribute("markers", markers);
			session.setAttribute("qtlName", qtlName);
			/*if(rsC!=null) rsC.close(); if(rsL!=null) rsL.close(); if(rs1L!=null) rs1L.close(); if(rs1C!=null) rs1C.close();
			if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close();*/
			if(conn!=null) conn.close(); if(con!=null) con.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();conn.close();
		      		
		         }catch(Exception e){System.out.println(e);}
			}	
		return am.findForward("det");
	}
	

}
