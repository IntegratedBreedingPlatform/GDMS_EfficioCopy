package org.icrisat.gdms.retrieve;

import java.io.FileInputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

public class RetrieveMapsAction extends Action{
	
	java.sql.Connection conn;
	java.sql.Connection con;	
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		ActionErrors ae = new ActionErrors();	
		if(session!=null){
			//session.removeAttribute("markers");
			session.removeAttribute("map_data");
		}
		boolean LGType=false;
		DynaActionForm df = (DynaActionForm) af;
		Properties p=new Properties();
		try{
			//String crop=req.getSession().getAttribute("crop").toString();
			//System.out.println("****************************"+marker);
			p.load(new FileInputStream(session.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
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
				
			String mtaDetails="";
			int mtaCount=0;
			ArrayList mtaMarkersL=new ArrayList();
			String tids="";
			String mapIds="";
			SortedMap mtaMaps = new TreeMap();
			ArrayList mtas=new ArrayList();
			SortedMap mtaMap = new TreeMap();
			SortedMap mtaTraitsMap = new TreeMap();
			
			ArrayList mtaTraitsL=new ArrayList();
			String mtaList="";
				
			/*ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	*/
			String traitsExists="no";
			
			String fromPage=req.getQueryString();
			
			//System.out.println("From page=:"+fromPage);
			ResultSet rsTC=null;ResultSet rs1L=null;
			ResultSet rsTL=null;ResultSet rs1C=null;
			ResultSet rs=null; ResultSet rs2C=null;
			ResultSet rs1=null; ResultSet rs3C=null;
			ResultSet rs3L=null; ResultSet rsL=null;
			ResultSet rsM=null;ResultSet mRsL=null;ResultSet mRs=null;
			ResultSet rs2L=null;ResultSet rsC=null;
			ResultSet rsMTAL=null; ResultSet rsMTA=null;
			ResultSet rsMapsL=null;
			Statement stmt=con.createStatement();
			Statement stmtR=con.createStatement();
			
			ResultSet rsT=null;
			Statement st=con.createStatement();
			
			ArrayList traits=new ArrayList();
			
			int mapId=0;
			
			ArrayList map_data= new ArrayList();
			ArrayList retMarkers=new ArrayList();
			ArrayList markers=new ArrayList();
			String map="";
			String mapName ="";
			String[] maps=null;
			String marker="";String markersforQuery="";
			map=df.get("maps").toString();
			session.setAttribute("maps", map);
			//System.out.println("..............  :"+fromPage);
			if(fromPage.equalsIgnoreCase("polymap")){
				//System.out.println(".....**************....."+session.getAttribute("result"));				
				markers=(ArrayList)session.getAttribute("result");	
				//mapName  = map.substring(0,map.lastIndexOf("("));
				req.getSession().setAttribute("resultM", markers);
				for(int m=0;m<markers.size();m++){
					//System.out.println(".....**************....."+markers.get(m));
					marker=marker+"'"+markers.get(m)+"',";
				}
				//System.out.println("markers="+markers);
				mapId=Integer.parseInt(map);
				markersforQuery=marker.substring(0, marker.length()-1);
			}else{
				maps=map.split("!~!");
				mapId=Integer.parseInt(maps[1]);
				mapName=maps[0];
			}
			
			ArrayList traitsList=new ArrayList();
			String traitsData="";
			HashMap traitsmap = new HashMap();
			//System.out.println("*******  :"+markersforQuery);
			//System.out.println("mapName=:"+mapName);
			
			HashMap<Integer, Object> markersMap = new HashMap<Integer, Object>();
            List lstMarkers = new ArrayList();			
			
			String marker_id="";
			String mtaMarkerId="";
			//ResultSet rsMTA=null;
			String fData="";
			ArrayList mtaMIDsList=new ArrayList();
			ArrayList mapData=new ArrayList();
			ArrayList finalMapData=new ArrayList();
			String map_unit="";
			if(fromPage.equalsIgnoreCase("polymap")){
				if(mapId>0){
					rsC=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_name in ("+markersforQuery+")");
					while(rsC.next()){
						marker_id=marker_id+rsC.getInt(1)+",";
						lstMarkers.add(rsC.getInt(1));
		            	markersMap.put(rsC.getInt(1), rsC.getString(2));						
					}
					rsL=stLoc.executeQuery("select marker_id, marker_name from gdms_marker where marker_name in ("+markersforQuery+")");
					while(rsL.next()){
						marker_id=marker_id+rsL.getInt(1)+",";
						lstMarkers.add(rsL.getInt(1));
		            	markersMap.put(rsL.getInt(1), rsL.getString(2));						
					}
					marker_id=marker_id.substring(0, marker_id.length()-1);
					rs1C=stCen.executeQuery("SELECT distinct gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_map.map_unit FROM gdms_map join gdms_markers_onmap on gdms_map.map_id=gdms_markers_onmap.map_id where gdms_markers_onmap.marker_id in ("+marker_id+") and gdms_map.map_id="+mapId+" order BY gdms_map.map_name, gdms_markers_onmap.linkage_group, gdms_markers_onmap.start_position asc");
					while(rs1C.next()){	
						map_unit=rs1C.getString(5);
							mapData.add(markersMap.get(rs1C.getInt(1))+"!~!"+rs1C.getString(2)+"!~!"+rs1C.getString(3)+"!~!"+rs1C.getString(4));
							fData=fData+markersMap.get(rs1C.getInt(1))+"!~!"+rs1C.getString(2)+"!~!"+rs1C.getString(3)+"!~!"+rs1C.getString(4)+"~~!!~~";
						
						if(!(retMarkers.contains(rs1C.getInt(1))))
							retMarkers.add(rs1C.getInt(1));
					}
					rs2C=stCen.executeQuery("SELECT DISTINCT gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_qtl_details.tid FROM gdms_markers_onmap, gdms_map, gdms_qtl_details WHERE gdms_markers_onmap.marker_id IN("+marker_id+") and gdms_map.map_id="+mapId+" AND gdms_map.map_id=gdms_markers_onmap.map_id AND gdms_map.map_id=gdms_qtl_details.map_id AND gdms_markers_onmap.linkage_group=gdms_qtl_details.linkage_group AND gdms_markers_onmap.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY gdms_map.map_name, gdms_markers_onmap.linkage_group, gdms_markers_onmap.start_position, gdms_markers_onmap.marker_id ASC, gdms_qtl_details.tid");
				 	while(rs2C.next()){
						traitsList.add(markersMap.get(rs2C.getInt(1))+"!~!"+rs2C.getString(2)+"!~!"+rs2C.getString(3)+"!~!"+rs2C.getString(4)+"!~!"+rs2C.getString(5));
					}
				}else{
					rsC=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_name in ("+markersforQuery+")");
					while(rsC.next()){
						marker_id=marker_id+rsC.getInt(1)+",";
						lstMarkers.add(rsC.getInt(1));
		            	markersMap.put(rsC.getInt(1), rsC.getString(2));						
					}
					rsL=stLoc.executeQuery("select marker_id, marker_name from gdms_marker where marker_name in ("+markersforQuery+")");
					while(rsL.next()){
						marker_id=marker_id+rsL.getInt(1)+",";
						lstMarkers.add(rsL.getInt(1));
		            	markersMap.put(rsL.getInt(1), rsL.getString(2));						
					}
					//System.out.println("#############:"+marker_id);
					marker_id=marker_id.substring(0, marker_id.length()-1);
					
					rs1L=stLoc.executeQuery("SELECT distinct gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_map.map_unit FROM gdms_map join gdms_markers_onmap on gdms_map.map_id=gdms_markers_onmap.map_id where gdms_markers_onmap.marker_id in ("+marker_id+") and gdms_map.map_id="+mapId+" order BY gdms_map.map_name, gdms_markers_onmap.linkage_group, gdms_markers_onmap.start_position asc");
					while(rs1L.next()){	
						map_unit=rs1L.getString(5);
							mapData.add(markersMap.get(rs1L.getInt(1))+"!~!"+rs1L.getString(2)+"!~!"+rs1L.getString(3)+"!~!"+rs1L.getString(4));
							fData=fData+markersMap.get(rs1L.getInt(1))+"!~!"+rs1L.getString(2)+"!~!"+rs1L.getString(3)+"!~!"+rs1L.getString(4)+"~~!!~~";
						
						if(!(retMarkers.contains(rs1L.getInt(1))))
							retMarkers.add(rs1L.getInt(1));
					}
					rs2L=stLoc.executeQuery("SELECT DISTINCT gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_qtl_details.tid FROM gdms_markers_onmap, gdms_map, gdms_qtl_details WHERE gdms_markers_onmap.marker_id IN("+marker_id+") and gdms_map.map_id="+mapId+" AND gdms_map.map_id=gdms_markers_onmap.map_id AND gdms_map.map_id=gdms_qtl_details.map_id AND gdms_markers_onmap.linkage_group=gdms_qtl_details.linkage_group AND gdms_markers_onmap.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY gdms_map.map_name, gdms_markers_onmap.linkage_group, gdms_markers_onmap.start_position, gdms_markers_onmap.marker_id ASC, gdms_qtl_details.tid");
				 	while(rs2L.next()){
						traitsList.add(markersMap.get(rs2L.getInt(1))+"!~!"+rs2L.getString(2)+"!~!"+rs2L.getString(3)+"!~!"+rs2L.getString(4)+"!~!"+rs2L.getString(5));
					}				
				}		
				
			}else{
				if(mapId>0){
					//central
					//System.out.println("central");
					rs3C=stCen.executeQuery("select marker_id from gdms_markers_onmap where map_id="+mapId);
					while(rs3C.next()){
						marker_id=marker_id+rs3C.getInt(1)+",";
					}
					marker_id=marker_id.substring(0, marker_id.length()-1);
					
					rsC=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in ("+marker_id+")");
					while(rsC.next()){
						lstMarkers.add(rsC.getInt(1));
		            	markersMap.put(rsC.getInt(1), rsC.getString(2));						
					}
					
					rs1C=stCen.executeQuery("SELECT distinct gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_map.map_unit FROM gdms_map join gdms_markers_onmap on gdms_map.map_id=gdms_markers_onmap.map_id where gdms_map.map_name='"+mapName+"' order BY gdms_map.map_name, gdms_markers_onmap.linkage_group,gdms_markers_onmap.start_position asc");
					while(rs1C.next()){	
						map_unit=rs1C.getString(5);
							mapData.add(markersMap.get(rs1C.getInt(1))+"!~!"+rs1C.getString(2)+"!~!"+rs1C.getString(3)+"!~!"+rs1C.getString(4));
							fData=fData+markersMap.get(rs1C.getInt(1))+"!~!"+rs1C.getString(2)+"!~!"+rs1C.getString(3)+"!~!"+rs1C.getString(4)+"~~!!~~";
						
						if(!(retMarkers.contains(rs1C.getInt(1))))
							retMarkers.add(rs1C.getInt(1));
					}
					//ResultSet rs2C=stCen.executeQuery("SELECT DISTINCT gdms_mapping_data.marker_name, gdms_mapping_data.map_name, gdms_mapping_data.start_position, gdms_mapping_data.linkage_group, gdms_qtl_details.trait FROM gdms_mapping_data, gdms_qtl_details WHERE gdms_mapping_data.map_name='"+mapName+"' AND gdms_mapping_data.map_id=gdms_qtl_details.map_id AND gdms_mapping_data.linkage_group=gdms_qtl_details.linkage_group AND gdms_mapping_data.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position, marker_name ASC, trait");
					//System.out.println("SELECT DISTINCT gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_qtl_details.trait FROM gdms_markers_onmap, gdms_map, gdms_qtl_details WHERE gdms_map.map_name='"+mapName+"' AND gdms_markers_onmap.map_id=gdms_qtl_details.map_id AND gdms_markers_onmap.linkage_group=gdms_qtl_details.linkage_group AND gdms_markers_onmap.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position, marker_id ASC, trait");
					//System.out.println("SELECT DISTINCT gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_qtl_details.tid FROM gdms_markers_onmap, gdms_map, gdms_qtl_details WHERE gdms_map.map_name='"+mapName+"' AND gdms_markers_onmap.map_id=gdms_qtl_details.map_id AND gdms_markers_onmap.linkage_group=gdms_qtl_details.linkage_group AND gdms_markers_onmap.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position, marker_id ASC, tid");
					rs2C=stCen.executeQuery("SELECT DISTINCT gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_qtl_details.tid FROM gdms_markers_onmap, gdms_map, gdms_qtl_details WHERE gdms_map.map_name='"+mapName+"' AND gdms_markers_onmap.map_id=gdms_qtl_details.map_id AND gdms_markers_onmap.linkage_group=gdms_qtl_details.linkage_group AND gdms_markers_onmap.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position, marker_id ASC, tid");
					while(rs2C.next()){
						traitsList.add(markersMap.get(rs2C.getInt(1))+"!~!"+rs2C.getString(2)+"!~!"+rs2C.getString(3)+"!~!"+rs2C.getString(4)+"!~!"+rs2C.getString(5));
					}
					
					
				}else{
					//local
					//System.out.println("Local");
					rs3L=stLoc.executeQuery("select marker_id from gdms_markers_onmap where map_id="+mapId);
					while(rs3L.next()){
						marker_id=marker_id+rs3L.getInt(1)+",";
					}
					marker_id=marker_id.substring(0, marker_id.length()-1);
					
					rsC=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in ("+marker_id+")");
					while(rsC.next()){
						lstMarkers.add(rsC.getInt(1));
		            	markersMap.put(rsC.getInt(1), rsC.getString(2));						
					}
					
					rsL=stLoc.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in ("+marker_id+")");
					while(rsL.next()){
						if(!lstMarkers.contains(rsL.getInt(1))){
							lstMarkers.add(rsL.getInt(1));		            		
		            	}
						markersMap.put(rsL.getInt(1), rsL.getString(2));   		            			            							
					}
					//System.out.println("markersMap=:"+markersMap);
					rs1L=stLoc.executeQuery("SELECT distinct gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_map.map_unit FROM gdms_map join gdms_markers_onmap on gdms_map.map_id=gdms_markers_onmap.map_id where gdms_map.map_name='"+mapName+"' order BY gdms_map.map_name, gdms_markers_onmap.linkage_group,gdms_markers_onmap.start_position asc");
					while(rs1L.next()){	
						map_unit=rs1L.getString(5);
							mapData.add(markersMap.get(rs1L.getInt(1))+"!~!"+rs1L.getString(2)+"!~!"+rs1L.getString(3)+"!~!"+rs1L.getString(4));
							fData=fData+markersMap.get(rs1L.getInt(1))+"!~!"+rs1L.getString(2)+"!~!"+rs1L.getString(3)+"!~!"+rs1L.getString(4)+"~~!!~~";
						
						if(!(retMarkers.contains(markersMap.get(rs1L.getInt(1)))))
							retMarkers.add(markersMap.get(rs1L.getInt(1)));
					}
					//System.out.println("SELECT DISTINCT gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_qtl_details.tid FROM gdms_markers_onmap, gdms_map, gdms_qtl_details WHERE gdms_map.map_name='"+mapName+"' AND gdms_markers_onmap.map_id=gdms_qtl_details.map_id AND gdms_markers_onmap.linkage_group=gdms_qtl_details.linkage_group AND gdms_markers_onmap.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position, marker_id ASC, tid");
					rs2L=stLoc.executeQuery("SELECT DISTINCT gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_qtl_details.tid FROM gdms_markers_onmap, gdms_map, gdms_qtl_details WHERE gdms_map.map_name='"+mapName+"' AND gdms_markers_onmap.map_id=gdms_qtl_details.map_id AND gdms_markers_onmap.linkage_group=gdms_qtl_details.linkage_group AND gdms_markers_onmap.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position, marker_id ASC, tid");
					while(rs2L.next()){
						tids=tids+rs2L.getInt(5)+",";	
						traitsList.add(markersMap.get(rs2L.getInt(1))+"!~!"+rs2L.getString(2)+"!~!"+rs2L.getString(3)+"!~!"+rs2L.getString(4)+"!~!"+rs2L.getString(5));
					}					
				}				
			}
			//System.out.println("*********************   :"+traitsList);
			
			//System.out.println("select marker_id from gdms_mta where marker_id in("+marker_id+")");
			//rsMTA=stCen.executeQuery("select marker_id, trait, map_id from gdms_mta where marker_id in("+marker_id+")");
			rsMTA=stCen.executeQuery("select marker_id, tid, map_id from gdms_mta where marker_id in("+marker_id+")");
			while(rsMTA.next()){
				mtaMarkerId=mtaMarkerId+rsMTA.getInt("marker_id")+",";	
				tids=tids+rsMTA.getInt(2)+",";	
				mapIds=mapIds+rsMTA.getInt(3)+",";	
				mtaCount++;
			}
			//System.out.println("select marker_id from gdms_mta where marker_id in("+marker_id+")");
			//rsMTAL=stLoc.executeQuery("select marker_id, trait, map_id from gdms_mta where marker_id in("+marker_id+")");
			rsMTAL=stLoc.executeQuery("select marker_id, tid, map_id from gdms_mta where marker_id in("+marker_id+")");
			while(rsMTAL.next()){
				mtaMarkerId=mtaMarkerId+rsMTAL.getInt("marker_id")+",";	
				tids=tids+rsMTAL.getInt(2)+",";	
				mapIds=mapIds+rsMTAL.getInt(3)+",";	
				mtaCount++;
			}
			if(mtaCount>0){
			//mtaMarkerId
				//System.out.println("SELECT marker_id, marker_name FROM gdms_marker where marker_id in("+mtaMarkerId.substring(0, mtaMarkerId.length()-1)+")");
				rsM=stCen.executeQuery("SELECT marker_id, marker_name FROM gdms_marker where marker_id in("+mtaMarkerId.substring(0, mtaMarkerId.length()-1)+")");
				while(rsM.next()){
					if(!(mtaMarkersL.contains(rsM.getInt(1)))){
						mtaMarkersL.add(rsM.getInt(1));
						mtaMap.put(rsM.getInt(1), rsM.getString(2));
					}
				}
				rs=stLoc.executeQuery("SELECT marker_id, marker_name FROM gdms_marker where marker_id in("+mtaMarkerId.substring(0, mtaMarkerId.length()-1)+")");
				while(rs.next()){
					if(!(mtaMarkersL.contains(rs.getInt(1)))){
						mtaMarkersL.add(rs.getInt(1));
						mtaMap.put(rs.getInt(1), rs.getString(2));
					}
				}
				
				/*getting maps of mta's from both central and local */
				rs1=stCen.executeQuery("SELECT map_id, map_name FROM gdms_map where map_id in ("+mapIds.substring(0, mapIds.length()-1)+")");
				while(rs1.next()){
					if(!(mtas.contains(rs1.getInt(1)))){
						mtas.add(rs1.getInt(1));
						mtaMaps.put(rs1.getInt(1), rs1.getString(2));
					}
				}
				rsMapsL=stLoc.executeQuery("SELECT map_id, map_name FROM gdms_map where map_id in ("+mapIds.substring(0, mapIds.length()-1)+")");
				while(rsMapsL.next()){
					if(!(mtas.contains(rsMapsL.getInt(1)))){
						mtas.add(rsMapsL.getInt(1));
						mtaMaps.put(rsMapsL.getInt(1), rsMapsL.getString(2));
					}
				}
				
				
				//rsTC=stCen.executeQuery("SELECT tid, trabbr FROM tmstraits");
				rsTC=stCen.executeQuery("select distinct cvterm_id, name from cvterm");
				while(rsTC.next()){
					if(!(mtaTraitsL.contains(rsTC.getInt(1)))){
						mtaTraitsL.add(rsTC.getInt(1));
						mtaTraitsMap.put(rsTC.getInt(1), rsTC.getString(2));
						traits.add(rsTC.getString(2));
					}
				}
				//rsTL=stLoc.executeQuery("SELECT tid, trabbr FROM tmstraits");
				rsTL=stLoc.executeQuery("select distinct cvterm_id, name from cvterm");
				while(rsTL.next()){
					if(!(mtaTraitsL.contains(rsTL.getInt(1)))){
						mtaTraitsL.add(rsTL.getInt(1));
						mtaTraitsMap.put(rsTL.getInt(1), rsTL.getString(2));
						traits.add(rsTL.getString(2));
					}
				}
			
			}else{
				//System.out.println("SELECT tid, trabbr FROM tmstraits");
				//rsTC=stCen.executeQuery("SELECT tid, trabbr FROM tmstraits where tid in ("+tids.substring(0, tids.length()-1)+")");
				if(traitsList.size()>0){
					//rsTC=stCen.executeQuery("SELECT tid, trabbr FROM tmstraits");
					rsTC=stCen.executeQuery("select distinct cvterm_id, name from cvterm");
					while(rsTC.next()){
						if(!(mtaTraitsL.contains(rsTC.getInt(1)))){
							mtaTraitsL.add(rsTC.getInt(1));
							mtaTraitsMap.put(rsTC.getInt(1), rsTC.getString(2));
							traits.add(rsTC.getString(2));
						}
					}
					//rsTL=stLoc.executeQuery("SELECT tid, trabbr FROM tmstraits");
					rsTL=stLoc.executeQuery("select distinct cvterm_id, name from cvterm");
					while(rsTL.next()){
						if(!(mtaTraitsL.contains(rsTL.getInt(1)))){
							mtaTraitsL.add(rsTL.getInt(1));
							mtaTraitsMap.put(rsTL.getInt(1), rsTL.getString(2));
							traits.add(rsTL.getString(2));
						}
					}
				}
			}
			
			//System.out.println("@@@@@@@@@@@@@@@@@ mtaTraitsMap:"+mtaTraitsMap);
			
			
			req.getSession().setAttribute("mtaCount", mtaCount);
			HashMap<Object, String> mtaTraits = new HashMap<Object, String>();
			String mtaMarkers="";
			if(mtaCount>0){
				//System.out.println("SELECT gdms_marker.marker_name, gdms_mta.trait, gdms_mta.linkage_group, gdms_map.map_name FROM gdms_marker JOIN gdms_mta ON gdms_marker.marker_id=gdms_mta.marker_id JOIN gdms_map ON gdms_mta.map_id=gdms_map.map_id WHERE gdms_marker.marker_id in("+mtaMarkerId.substring(0, mtaMarkerId.length()-1)+")");
				//mRs=stCen.executeQuery("SELECT marker_id, trait, linkage_group, map_id FROM gdms_mta WHERE marker_id in("+mtaMarkerId.substring(0, mtaMarkerId.length()-1)+")");
				mRs=stCen.executeQuery("SELECT marker_id, tid, linkage_group, map_id FROM gdms_mta WHERE marker_id in("+mtaMarkerId.substring(0, mtaMarkerId.length()-1)+")");
				while(mRs.next()){
					if(!mtaMIDsList.contains(mtaMap.get(Integer.parseInt(mRs.getString(1).toString())))){
						mtaMIDsList.add(mtaMap.get(Integer.parseInt(mRs.getString(1).toString())));
						//mtaTraits.put(mtaMap.get(Integer.parseInt(mRs.getString(1).toString())),mRs.getString(2).toString());
						mtaTraits.put(mtaMap.get(Integer.parseInt(mRs.getString(1).toString())),mtaTraitsMap.get(Integer.parseInt(mRs.getString(2).toString())).toString());
					mtaMarkers=mtaMarkers+mtaMap.get(Integer.parseInt(mRs.getString(1).toString()))+"!~!";
					}
					mtaDetails=mtaDetails+mtaMap.get(Integer.parseInt(mRs.getString(1).toString()))+"!~!"+mtaMaps.get(Integer.parseInt(mRs.getString(4).toString()))+"!~!"+mRs.getString(3)+"!~!"+mtaTraitsMap.get(Integer.parseInt(mRs.getString(2).toString())).toString()+"~~!!~~";
				}
				//mRsL=stLoc.executeQuery("SELECT marker_id, trait, linkage_group, map_id FROM gdms_mta WHERE marker_id in("+mtaMarkerId.substring(0, mtaMarkerId.length()-1)+")");
				mRsL=stLoc.executeQuery("SELECT marker_id, tid, linkage_group, map_id FROM gdms_mta WHERE marker_id in("+mtaMarkerId.substring(0, mtaMarkerId.length()-1)+")");
				while(mRsL.next()){
					if(!mtaMIDsList.contains(mtaMap.get(Integer.parseInt(mRsL.getString(1).toString())))){
						mtaMIDsList.add(mtaMap.get(Integer.parseInt(mRsL.getString(1).toString())));
						//mtaTraits.put(mRs.getString(1), mRs.getString(2));
						//mtaTraits.put(mtaMap.get(Integer.parseInt(mRsL.getString(1).toString())), mtaTraitsMap.get(Integer.parseInt(mRsL.getString(2).toString())).toString());
						mtaTraits.put(mtaMap.get(Integer.parseInt(mRsL.getString(1).toString())), mtaTraitsMap.get(Integer.parseInt(mRsL.getString(2).toString())).toString());
						mtaMarkers=mtaMarkers+mtaMap.get(Integer.parseInt(mRsL.getString(1).toString()))+"!~!";
						}
						//mtaDetails=mtaDetails+mtaMap.get(Integer.parseInt(mRsL.getString(1).toString()))+"!~!"+mtaMaps.get(Integer.parseInt(mRsL.getString(4).toString()))+"!~!"+mRsL.getString(3)+"!~!"+mtaTraitsMap.get(Integer.parseInt(mRsL.getString(2).toString()))+"~~!!~~";
					mtaDetails=mtaDetails+mtaMap.get(Integer.parseInt(mRsL.getString(1).toString()))+"!~!"+mtaMaps.get(Integer.parseInt(mRsL.getString(4).toString()))+"!~!"+mRsL.getString(3)+"!~!"+mtaTraitsMap.get(Integer.parseInt(mRsL.getString(2).toString())).toString()+"~~!!~~";
				}
				//System.out.println(mtaMIDsList.size()+"    mtaMIDsList=:"+mtaMIDsList);
				req.getSession().setAttribute("mtaMIDsList", mtaMIDsList);
				req.getSession().setAttribute("mtaTraits",mtaTraits);
				req.getSession().setAttribute("mtaDetails",mtaDetails);
				req.getSession().setAttribute("mtaMarkers", mtaMarkers);
			}
			
			//System.out.println(traitsList.size()+".............."+traitsList);
			//System.out.println(".............."+mtaTraitsMap);
			ArrayList fList=new ArrayList();
			
			if(traitsList.size()>0){
				String[] str1=traitsList.get(0).toString().split("!~!");
				String markerT=str1[0];
				String trait=mtaTraitsMap.get(Integer.parseInt(str1[4].toString())).toString();
				String LG=str1[3];
				/*String a = "12.0"; //Valid
				String b = "12.5"; //Invalid */
				
				String Map=str1[1];
				String SPos=str1[2];
				String ins="no";
				for(int t=0;t<traitsList.size();t++){
					String[] str2=traitsList.get(t).toString().split("!~!");
					if(marker.equalsIgnoreCase(str2[0])){
						if(Map.equalsIgnoreCase(str2[1])){
							if(LG.equalsIgnoreCase(str2[3])){
								if(!(trait.equalsIgnoreCase(mtaTraitsMap.get(Integer.parseInt(str2[4].toString())).toString()))){
									trait=trait+","+mtaTraitsMap.get(Integer.parseInt(str2[4].toString())).toString();
									ins="yes";
								}else{
									trait=mtaTraitsMap.get(Integer.parseInt(str2[4].toString())).toString();
									ins="no";
								}							
							}
						}					
					}else{
						ins="no";					
						trait=mtaTraitsMap.get(Integer.parseInt(str2[4].toString())).toString();
						fList.add(str2[0]+"!~!"+str2[1]+"!~!"+str2[2]+"!~!"+str2[3]+"!~!"+trait);
					}
					if(ins.equalsIgnoreCase("yes")){
						fList.remove(fList.size()-1);
						fList.add(str2[0]+"!~!"+str2[1]+"!~!"+str2[2]+"!~!"+str2[3]+"!~!"+trait);
					}
						
					marker=str2[0];
					LG=str2[3];
					Map=str2[1];
					SPos=str2[2];
				}
				for(int f=0;f<fList.size();f++){
					 String arrP[]=new String[5];
					 StringTokenizer stzP = new StringTokenizer(fList.get(f).toString(), "!~!");
					 int iP=0;
					 while(stzP.hasMoreTokens()){
						 arrP[iP] = stzP.nextToken();
						 iP++;
					 }	
					 traitsmap.put(arrP[0]+"!!~~!!"+arrP[1], arrP[2]+"!~!"+arrP[3]+"!~!"+arrP[4]);
				}
				
				String finalData="";
				for(int l=0;l<mapData.size();l++){
					String arrP[]=new String[5];
					 StringTokenizer stzP = new StringTokenizer(mapData.get(l).toString(), "!~!");
					 int iP=0;
					 while(stzP.hasMoreTokens()){
						 arrP[iP] = stzP.nextToken();
						 iP++;
					 }	
					if(traitsmap.containsKey(arrP[0]+"!!~~!!"+arrP[1])){
						finalMapData.add(arrP[0]+"!~!"+arrP[1]+"!~!"+traitsmap.get(arrP[0]+"!!~~!!"+arrP[1]));
						finalData=finalData+arrP[0]+"!~!"+arrP[1]+"!~!"+traitsmap.get(arrP[0]+"!!~~!!"+arrP[1])+"~~!!~~";
					}else{
						finalMapData.add(arrP[0]+"!~!"+arrP[1]+"!~!"+arrP[2]+"!~!"+arrP[3]+"!~!"+" ");
						finalData=finalData+arrP[0]+"!~!"+arrP[1]+"!~!"+arrP[2]+"!~!"+arrP[3]+"!~!"+" "+"~~!!~~";
					}				
				}
				
				//System.out.println("finalMapData="+finalMapData);
				//rsT=st.executeQuery("select distinct trait from gdms_qtl_details order by trait");
				if(fromPage.equalsIgnoreCase("polymap")){
					traits.clear();
					//System.out.println("SELECT DISTINCT gdms_qtl_details.trait FROM gdms_mapping_data, gdms_qtl_details WHERE gdms_mapping_data.marker_id IN ("+marker_id+") AND gdms_mapping_data.map_id='"+mapId+"' AND gdms_mapping_data.map_id=gdms_qtl_details.map_id ORDER BY trait ASC");;
					String querryT="SELECT DISTINCT gdms_qtl_details.tid FROM gdms_mapping_data, gdms_qtl_details WHERE gdms_mapping_data.marker_id IN ("+marker_id+") AND gdms_mapping_data.map_id='"+mapId+"' AND gdms_mapping_data.map_id=gdms_qtl_details.map_id ORDER BY tid ASC";
					if(mapId>0){
						rsTC=stCen.executeQuery(querryT);
						while(rsTC.next()){							
							traits.add(mtaTraitsMap.get(rsTC.getInt(1)));
						}
					}else{
						rsTL=stLoc.executeQuery(querryT);
						while(rsTL.next()){
							if(!traits.contains(rsTL.getString(1)))
								traits.add(mtaTraitsMap.get(rsTL.getString(1)));
						}
					}
					
				}else{
					traits.clear();
					//System.out.println("SELECT DISTINCT gdms_qtl_details.trait FROM gdms_mapping_data, gdms_qtl_details WHERE gdms_mapping_data.marker_id IN ("+marker_id+") AND gdms_mapping_data.map_id='"+mapId+"' AND gdms_mapping_data.map_id=gdms_qtl_details.map_id ORDER BY trait ASC");;
					String querryT="SELECT DISTINCT gdms_qtl_details.tid FROM gdms_mapping_data, gdms_qtl_details WHERE gdms_mapping_data.map_id='"+mapId+"' AND gdms_mapping_data.map_id=gdms_qtl_details.map_id ORDER BY tid ASC";
					if(mapId>0){
						rsTC=stCen.executeQuery(querryT);
						while(rsTC.next()){							
							traits.add(mtaTraitsMap.get(rsTC.getInt(1)));
						}
					}else{
						rsTL=stLoc.executeQuery(querryT);
						while(rsTL.next()){
							if(!traits.contains(rsTL.getString(1)))
								traits.add(mtaTraitsMap.get(rsTL.getString(1)));
						}
					}
					
					
				}
				//System.out.println(traits.size()+"  traits=:"+traits);
				if(traits.size() > 0)
					traitsExists="yes";
				session.setAttribute("traits", traits);
				req.getSession().setAttribute("finalData",finalData);
				req.getSession().setAttribute("map_data", finalMapData);
				req.getSession().setAttribute("showTraits", traitsExists);
				
			}else{
				traitsExists="no";
				
				req.getSession().setAttribute("finalData",fData);
				req.getSession().setAttribute("map_data", mapData);
				req.getSession().setAttribute("showTraits", traitsExists);
			}
			///System.out.println("finalMapData="+mapData);
			//req.getSession().setAttribute("map_data", mapData);
			//System.out.println(traitsExists);
			session.setAttribute("map_unit", map_unit);
			//return null;
			session.setAttribute("recCount", markers.size());
			session.setAttribute("missingCount", req.getParameter("missCount"));
			session.setAttribute("result", req.getParameter("nData"));
			session.setAttribute("retCount", retMarkers.size());
			session.setAttribute("fromPage", fromPage);
			
			/*if(rsC!=null) rsC.close(); if(rs1C!=null) rs1C.close(); if(rs2C!=null) rs2C.close();
			if(rsL!=null) rsL.close(); if(rs1L!=null) rs1L.close(); if(rs2L!=null) rs2L.close();
			if(rs3L!=null) rs3L.close();if(rs3C!=null) rs3C.close();			
			if(rsTL!=null) rsTL.close();if(rsTC!=null) rsTC.close();if(mRs!=null) mRs.close();if(mRsL!=null) mRsL.close();
			if(rsMTAL!=null) rsMTAL.close();if(rsMTA!=null) rsMTA.close(); 
			if(stLoc!=null) stLoc.close();if(stCen!=null) stCen.close();*/
			if(con!=null) con.close();if(conn!=null) conn.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      	
		    	  
		      		if(con!=null) con.close();conn.close();
		      		
		         }catch(Exception e){System.out.println(e);}
			}	
		return am.findForward("ret");
	}
	

}

