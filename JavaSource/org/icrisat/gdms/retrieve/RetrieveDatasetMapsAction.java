package org.icrisat.gdms.retrieve;

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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RetrieveDatasetMapsAction extends Action{
	
	java.sql.Connection conn;
	java.sql.Connection con;
	
	//ArrayList map_data=new ArrayList();
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		ActionErrors ae = new ActionErrors();	
		if(session!=null){
			//session.removeAttribute("markers");
			//session.removeAttribute("map_data");
		}
		//System.out.println("***************** ");
		Properties prop=new Properties();
		try{
			PrintWriter pr = res.getWriter();
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");	
			
			prop.load(new FileInputStream(session.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
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
			Statement stCen=conn.createStatement();
			
			Statement stLC=null;
			
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
			
			
			
			//String crop=req.getSession().getAttribute("crop").toString();
			//System.out.println("****************************"+marker);
			/*ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();*/	
			int mapCount=0;
			
			String markers="";
			ResultSet rsL=null;
			ResultSet rs=null;
			ResultSet rsM=null;
			
			ResultSet rs2=null;
			ResultSet rsMC=null;
			ResultSet rsML=null;
			ResultSet rsC=null;
			
			Statement st=null;
			Statement stmt=null;
			Statement stmtR=null;
			
			
			ArrayList mList= new ArrayList ();
			ArrayList mListDup= new ArrayList ();
			ArrayList mapList= new ArrayList ();
			//String datasetName=req.getParameter("ChkDataSets");
			
			st=con.createStatement();	
			stmtR=con.createStatement();
			
			
			int datasetId=Integer.parseInt(req.getParameter("ChkDataSets"));
			
			if(datasetId>0){
				stmt=conn.createStatement();			
			}else{					
				stmt=con.createStatement();				
			}
			rs=stmt.executeQuery("select marker_id from gdms_marker_metadataset where dataset_id="+datasetId);
			while(rs.next()){
				markers=markers+rs.getInt(1)+",";
			}
			markers=markers.substring(0, markers.length()-1);
			
			//System.out.println(markers);
			//System.out.println("SELECT map.map_name, COUNT(markers_onmap.marker_id) FROM markers_onmap JOIN map ON map.map_id=markers_onmap.map_id WHERE markers_onmap.marker_id IN("+markers+") GROUP BY map.map_name");
			
			rsC=stCen.executeQuery("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markers+") GROUP BY gdms_map.map_name");
			rsL=stLoc.executeQuery("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markers+") GROUP BY gdms_map.map_name");
			while(rsC.next()){
				mapCount++;				
			}
			while(rsL.next()){
				mapCount++;				
			}
			//System.out.println("mapCount=:"+mapCount);
			if(mapCount==0){
				//System.out.println("Zero maps");
				rs2=stCen.executeQuery("select map_name from gdms_map");
				rsM=stLoc.executeQuery("select map_name from gdms_map");
				while(rs2.next()){
					mList.add(rs2.getString(1));
				}
				while(rsM.next()){
					if(!mList.contains(rsM.getString(1)))
						mList.add(rsM.getString(1));
				}
				//System.out.println(mList);
				pr.println("<data>");
				pr.println("<details><![CDATA[- Select -]]></details>");
				for(int m=0; m< mList.size();m++){									
					String str = mList.get(m)+"(0)";
					pr.println("<details><![CDATA[" + str + "]]></details>");					
				}
				pr.println("</data>");	
			}else{
				rsMC=stCen.executeQuery("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markers+") GROUP BY gdms_map.map_name");
				rsML=stLoc.executeQuery("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markers+") GROUP BY gdms_map.map_name");
					
				while(rsMC.next()){
					mListDup.add(rsMC.getString(1));
					mapList.add(rsMC.getString(1)+" ("+rsMC.getInt(2)+")");			
				}
				while(rsML.next()){
					if(!mListDup.contains(rsML.getString(1))){
						mListDup.add(rsML.getString(1));	
						mapList.add(rsML.getString(1)+" ("+rsML.getInt(2)+")");
					}
				}		
				//System.out.println("mapList-=:"+mapList);
				pr.println("<data>");
				pr.println("<details><![CDATA[- Select -]]></details>");
				for(int m=0;m<mapList.size();m++){
					//System.out.println("mapList.get(m)=:"+mapList.get(m));
					String str = mapList.get(m).toString();	
					//System.out.println(str);
					pr.println("<details><![CDATA[" + str + "]]></details>");
					//System.out.println(".................:"+str);
				}
				pr.println("</data>");	
			}
			 
			session.setAttribute("mapsCount", mapCount);
			//if(con!=null) con.close(); if(conn!=null) conn.close();
			return null;
			/*if(rsL!=null) rsL.close(); if(rs!=null) rs.close(); if(rsM!=null) rsM.close();  if(rs2!=null) rs2.close(); if(rsMC!=null) rsMC.close();if(rsML!=null) rsML.close(); if(rsC!=null) rsC.close();
			if(st!=null) st.close(); if(stmt!=null) stmt.close();  if(stmtR!=null) stmtR.close();
			if(con!=null) con.close(); if(conn!=null) conn.close();*/
			
			
					
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();	conn.close();	      		
		         }catch(Exception e){System.out.println(e);}
			}	
		return am.findForward("ret");
	}
	

}
