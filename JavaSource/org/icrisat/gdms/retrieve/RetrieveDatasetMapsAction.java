package org.icrisat.gdms.retrieve;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

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
	Connection con=null;
	

	
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
		System.out.println("***************** ");
		
		try{
			PrintWriter pr = res.getWriter();
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");	
			
			//String crop=req.getSession().getAttribute("crop").toString();
			//System.out.println("****************************"+marker);
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			int mapCount=0;
			
			String markers="";
			ResultSet rs=null;
			
			ResultSet rs1=null;
			ResultSet rs2=null;
			Statement st=con.createStatement();
			Statement stmt=con.createStatement();
			Statement stmtR=con.createStatement();
			ArrayList mapList= new ArrayList ();
			String datasetName=req.getParameter("ChkDataSets");
			//System.out.println(datasetName);
			//System.out.println("select marker_id from marker_metadataset where dataset_id=(select dataset_id from dataset where dataset_desc='"+datasetDesc+"')");
			//rs=stmt.executeQuery("select marker_id from marker_metadataset where dataset_id=(select dataset_id from dataset where dataset_desc='"+datasetDesc+"')");
			rs=stmt.executeQuery("select marker_id from gdms_marker_metadataset where dataset_id=(select distinct dataset_id from gdms_dataset where dataset_name='"+datasetName+"')");
			while(rs.next()){
				markers=markers+rs.getInt(1)+",";
			}
			markers=markers.substring(0, markers.length()-1);
			//System.out.println(markers);
			//System.out.println("SELECT map.map_name, COUNT(markers_onmap.marker_id) FROM markers_onmap JOIN map ON map.map_id=markers_onmap.map_id WHERE markers_onmap.marker_id IN("+markers+") GROUP BY map.map_name");
			rs1=stmtR.executeQuery("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markers+") GROUP BY gdms_map.map_name");
			while(rs1.next()){
				mapCount++;
			}
			//System.out.println("mapCount=:"+mapCount);
			if(mapCount==0){
				//System.out.println("Zero maps");
				rs2=st.executeQuery("select map_name from gdms_map");
				pr.println("<data>");
				pr.println("<details><![CDATA[- Select -]]></details>");
				while(rs2.next()){
					//mapList.add(rs1.getString(1)+" ("+rs1.getInt(2)+")");
					
					String str = rs2.getString(1)+"(0)";	
					//System.out.println(str);
					pr.println("<details><![CDATA[" + str + "]]></details>");
					
				}
			}else{
				//System.out.println("maps exists");
				rs1=stmtR.executeQuery("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markers+") GROUP BY gdms_map.map_name");
				//rsMap=stmtMap.executeQuery("SELECT linkagemap.linkagemap_name, COUNT(marker_linkagemap.marker_id) FROM marker_linkagemap JOIN linkagemap ON linkagemap.linkagemap_id=marker_linkagemap.linkagemap_id GROUP BY linkagemap.linkagemap_name");
				pr.println("<data>");
				pr.println("<details><![CDATA[- Select -]]></details>");
				while(rs1.next()){
					//mapList.add(rs1.getString(1)+" ("+rs1.getInt(2)+")");
					
					String str = rs1.getString(1)+" ("+rs1.getInt(2)+")";	
					//System.out.println(str);
					pr.println("<details><![CDATA[" + str + "]]></details>");
					
				}
			}
			pr.println("</data>");	 
			session.setAttribute("mapsCount", mapCount);
			return null;
			//return mapCount;
			
			//System.out.println("mapList="+mapList);								
			//session.setAttribute("mapList", mapList);
			//str="retMaps";
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		      		
		         }catch(Exception e){System.out.println(e);}
			}	
		return am.findForward("ret");
	}
	

}
