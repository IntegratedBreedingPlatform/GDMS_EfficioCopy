package org.icrisat.gdms.retrieve;


import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.struts.action.DynaActionForm;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;

public class DataRetrieveDirectingAction extends Action{
	List listValues=null;
	List list1=null;
	Query query=null;
	Query query1=null;
	Query query2=null;
	String str="";
	String crop="";
	
	java.sql.Connection conn;
	java.sql.Connection con;
	
	
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);  
		//String crop=req.getSession().getAttribute("crop").toString();
		if(session!=null){
			session.removeAttribute("listValues");			
		}
		
		//System.out.println("**********************************"+req.getQueryString());
		String op=req.getQueryString();
		//System.out.println("**********************************"+op);
		/*hsession = HibernateSessionFactory.currentSession();
		tx=hsession.beginTransaction();
		*/
		DynaActionForm df = (DynaActionForm) af;
		//Connection con=null;
		
		//ResultSet rs1=null;
		//ResultSet rs1L=null;
		ResultSet rs=null;
		ResultSet rs2=null;
		ResultSet rs2L=null;
		ResultSet rsQTL=null;		
		ResultSet rsMap=null;
		//ResultSet rsDS=null;
		ArrayList qtlList=new ArrayList();
		ArrayList gids =new ArrayList();
		int count=0;
		ManagerFactory factory=null;
		int mapCount=0;
		Properties prop=new Properties();
		try{
			
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
			
			
			session.setAttribute("mapsCount", mapCount);
			
			//factory = new ManagerFactory(local, central);
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			GenotypicDataManager manager1 = factory.getGenotypicDataManager();
			
			String datasetId="";
			
			
			Statement st=con.createStatement();
			Statement stmt2=con.createStatement();
			
			Statement stCenM=conn.createStatement();
			Statement stLocM=conn.createStatement();
			Statement stmtM=con.createStatement();
			Statement stmtMap=con.createStatement();
			
			if(op.equalsIgnoreCase("second")){
				//crop=(String)df.get("crop");
				String option=req.getParameter("reportType");
				//System.out.println("option ="+option);
				//session.setAttribute("crop", crop);
				if (option.equalsIgnoreCase("genotyping"))
					str="directing";
				else
					str="markerPage";
			}else if(op.equalsIgnoreCase("polyType")){
				str="directingPoly";
			}else if(op.equalsIgnoreCase("poly")){
				ResultSet rsD=null;
				ResultSet rsC=null;
				/** retrieving germplasm names based on crop **/
				//System.out.println("...............:"+req.getParameter("op"));
				String polyType=req.getParameter("op");
				session.setAttribute("polyType", polyType);
				String germplasmName="";
				datasetId="";
				ArrayList germName=new ArrayList();
				ArrayList pGidsA=new ArrayList();
				String pGids="";
				String finalGids="";
				String nids="";
				rsC=null;
				//String crop=req.getSession().getAttribute("crop").toString();
				if(polyType.equalsIgnoreCase("fingerprinting")){
					//System.out.println("SELECT dataset_id FROM gdms_dataset WHERE dataset_type!='mapping' AND dataset_type !='QTL'");
					rsC=stCen.executeQuery("SELECT dataset_id FROM gdms_dataset WHERE dataset_type!='mapping' AND dataset_type !='QTL'");					
					rsD=stLoc.executeQuery("SELECT dataset_id FROM gdms_dataset WHERE dataset_type!='mapping' AND dataset_type !='QTL'");
					while(rsC.next()){
						datasetId=datasetId+rsC.getInt(1)+",";
					}
					while(rsD.next()){
						datasetId=datasetId+rsD.getInt(1)+",";
					}
				}else if(polyType.equalsIgnoreCase("mapping")){
					//System.out.println("SELECT dataset_id FROM gdms_dataset WHERE dataset_type ='mapping' AND dataset_type !='QTL'");
					ResultSet rsMC=stCenM.executeQuery("SELECT dataset_id FROM gdms_dataset WHERE dataset_type ='mapping' AND dataset_type !='QTL'");
					rsD=stLocM.executeQuery("SELECT dataset_id FROM gdms_dataset WHERE dataset_type ='mapping' AND dataset_type !='QTL'");
					rs2=stCen.executeQuery("select parent_a_nid, parent_b_nid from gdms_mapping_pop");
					rs2L=stLoc.executeQuery("select parent_a_nid, parent_b_nid from gdms_mapping_pop");
					while(rs2.next()){
						pGids=pGids+rs2.getInt(1)+","+rs2.getInt(2)+",";
					}
					while(rs2L.next()){
						pGids=pGids+rs2L.getInt(1)+","+rs2L.getInt(2)+",";
					}
					while(rsMC.next()){
						datasetId=datasetId+rsMC.getInt(1)+",";
					}
					while(rsD.next()){
						datasetId=datasetId+rsD.getInt(1)+",";
					}
				}
				
				/*while(rsC.next()){
					datasetId=datasetId+rsC.getInt(1)+",";
				}
				while(rsD.next()){
					datasetId=datasetId+rsD.getInt(1)+",";
				}*/
				String[] pGids1=pGids.split(",");
				for(int p=0;p<pGids1.length;p++){
					if(!(pGidsA.contains(pGids1[p])))
						pGidsA.add(pGids1[p]);
				}
				for(int a=0;a<pGidsA.size();a++){
					finalGids=finalGids+pGidsA.get(a)+",";
				}
				ArrayList nidsList=new ArrayList();
				if(datasetId !=""){					
					if(polyType.equalsIgnoreCase("fingerprinting")){
						//System.out.println("SELECT distinct nval FROM names WHERE nid IN (SELECT DISTINCT(nid) FROM acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+")) ORDER BY nval");
						//System.out.println("SELECT DISTINCT(nid) FROM gdms_acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+")");
						rs2=stCen.executeQuery("SELECT DISTINCT(nid) FROM gdms_acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+")");
						while(rs2.next()){
							nids =nids+rs2.getInt(1)+",";
							nidsList.add(rs2.getInt(1));
						}
						rs2L=stLoc.executeQuery("SELECT DISTINCT(nid) FROM gdms_acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+")");
						while(rs2L.next()){
							nids =nids+rs2L.getInt(1)+",";
							nidsList.add(rs2L.getInt(1));
						}
					}else if(polyType.equalsIgnoreCase("mapping")){
						//System.out.println("SELECT distinct nval FROM names WHERE nid IN (SELECT DISTINCT(nid) FROM acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+") and gid not in("+finalGids.substring(0, finalGids.length()-1)+")) ORDER BY nval");
						//rs1=stmtR.executeQuery("SELECT distinct nval FROM names WHERE nid IN (SELECT DISTINCT(nid) FROM acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+") and gid not in("+finalGids.substring(0, finalGids.length()-1)+")) ORDER BY nval");
						rs2=stCen.executeQuery("SELECT DISTINCT(nid) FROM gdms_acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+") and gid not in("+finalGids.substring(0, finalGids.length()-1)+")");
						while(rs2.next()){
							nids =nids+rs2.getInt(1)+",";
							nidsList.add(rs2.getInt(1));
						}
						rs2L=stLoc.executeQuery("SELECT DISTINCT(nid) FROM gdms_acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+") and gid not in("+finalGids.substring(0, finalGids.length()-1)+")");
						while(rs2L.next()){
							nids =nids+rs2L.getInt(1)+",";
							nidsList.add(rs2L.getInt(1));
						}
						
					}
					//System.out.println(".......... nidsList=:"+nidsList);
					//GermplasmDataManager manager = factory.getGermplasmDataManager();
					//List<Name> names = null;
					Name names = null;
					for(int n=0;n<nidsList.size();n++){
						//names = manager.getNamesByGID(Integer.parseInt(nidsList.get(n).toString()), null, null);
						/*names = manager.getGermplasmNameByID((Integer.parseInt(nidsList.get(n).toString())));
						for (Name name : names) {
							//list1.add(name.getGermplasmId()+"!!"+name.getNval());
							if(!(germName.contains(name.getNval())))
								germName.add(name.getNval());
				            
				        }*/
						
						names=manager.getGermplasmNameByID(Integer.parseInt(nidsList.get(n).toString()));
						if(!(germName.contains(names.getNval())))
							germName.add(names.getNval());
					}
					//System.out.println("germName=:"+germName);
					
					/*rs1=stmtR.executeQuery("SELECT distinct nval FROM names WHERE nid IN ("+nids.substring(0, nids.length()-1)+") order by nval");
					while(rs1.next()){
						germplasmName=germplasmName+rs1.getString(1)+",";
						if(!(germName.contains(rs1.getString(1))))
							germName.add(rs1.getString(1));
					}*/
					//System.out.println("germplasmName="+germName);
					session.setAttribute("listValues", germName);
					str="retLines";
				}else{
					session.setAttribute("listValues", germName);
					str="retNoLines";
				}
				
			}else if(op.equalsIgnoreCase("out")){
				str="genoOut";
			}else if(op.equalsIgnoreCase("gNamesDirecting")){
				str="gNdir";
				
			}else if(op.equalsIgnoreCase("gidsDirecting")){
				str="gdir";
				
			}else if(op.equalsIgnoreCase("markersDirecting")){
				str="mdir";
			}else if(op.equalsIgnoreCase("datasetRet")){
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				/** retrieving datasets based on crop **/
				
				ArrayList dataSetList=new ArrayList();
				
				String markers="";
				//crop=session.getAttribute("crop").toString();
				//System.out.println("select dataset_desc from dataset where dataset_type !='QTL'");
				//rsDS=stmtDS.executeQuery("select dataset_desc from dataset where dataset_type !='QTL'");
				int sta=1;
				int retRows=12;
				//GenotypicDataManager m=factory.getGenotypicDataManager();
				//m.getDatasetNames(sta, retRows, );
				ArrayList datasets=new ArrayList();
				ResultSet rsC=stCen.executeQuery("select dataset_name, dataset_id from gdms_dataset where dataset_type !='QTL' and dataset_type !='MTA' order by dataset_name");
				ResultSet rsL=stLoc.executeQuery("select dataset_name, dataset_id from gdms_dataset where dataset_type !='QTL' and dataset_type !='MTA' order by dataset_name");
				while(rsC.next()){
					datasets.add(rsC.getString(1));
					dataSetList.add(rsC.getString(1)+"!~!"+rsC.getInt(2));					
				}
				while(rsL.next()){
					if(!datasets.contains(rsL.getString(1))){
						datasets.add(rsL.getString(1));
						dataSetList.add(rsL.getString(1)+"!~!"+rsL.getInt(2));						
					}					
				}
				
				
				/*rsDS=stmtDS.executeQuery("select dataset_name from gdms_dataset where dataset_type !='QTL'");
				while(rsDS.next()){
					dataSetList.add(rsDS.getString(1));
				}*/
				int initialmapCount=0;
				rs=st.executeQuery("select map_name from gdms_map");
				while(rs.next()){
					initialmapCount++;
					//dataSetList.add(rs.getString(1));
				}
				
				//System.out.println("dataSetList="+dataSetList);
				session.setAttribute("mapsCount", initialmapCount);		
				session.setAttribute("dataSetList", dataSetList);
				str="retDataset";
			
			}else if(op.equalsIgnoreCase("mapsRet")){
				ArrayList mapList=new ArrayList();
				
				String markers="";
				String datasetDesc=req.getParameter("op");
				ResultSet rsM=stmtM.executeQuery("select marker_id from gdms_marker_metadataset where dataset_id=(select dataset_id from gdms_dataset where dataset_name='"+datasetDesc+"')");
				while(rsM.next()){
					markers=markers+rsM.getInt(1)+",";
				}
				markers=markers.substring(0, markers.length()-1);
				//System.out.println(markers);
				rsMap=stmtMap.executeQuery("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markers+") GROUP BY gdms_map.map_name");
				//rsMap=stmtMap.executeQuery("SELECT linkagemap.linkagemap_name, COUNT(marker_linkagemap.marker_id) FROM marker_linkagemap JOIN linkagemap ON linkagemap.linkagemap_id=marker_linkagemap.linkagemap_id GROUP BY linkagemap.linkagemap_name");
				while(rsMap.next()){
					mapList.add(rsMap.getString(1)+" ("+rsMap.getInt(2)+")");
				}
				//System.out.println("mapList="+mapList);								
				session.setAttribute("mapList", mapList);
				str="retMaps";
				
			}else{
				if(session!=null){
					session.removeAttribute("indErrMsg");		
				}				
				String dType="";		
				
				rsQTL=stmt2.executeQuery("select qtl_name from gdms_qtl");
				while(rsQTL.next()){
					qtlList.add(rsQTL.getString(1));					
				}
				//System.out.println("qtlList="+qtlList);
				
				/**for retrieving the size of dataset**/
				//rsS=stS."SELECT UPPER(char_values.dataset_id) AS id, COUNT(DISTINCT(char_values.marker_id)) AS Markercount, COUNT(DISTINCT(char_values.gid)) AS Gcount FROM char_values GROUP BY UPPER(char_values.dataset_id)");
				
				//System.out.println("QTL LIST ="+qtlList);
				
				//query=hsession.createQuery("select distinct species from DatasetBean ORDER BY species asc");
				//query1=hsession.createQuery("select distinct germplasm_name from RetrievalMarkers WHERE lower(species) ='"+crop.toLowerCase()+"'order BY germplasm_name");
				//listValues=query1.list();
				//itList=listValues.iterator();
				session.setAttribute("qtlList", qtlList);
				
				//System.out.println(".................... "+listValues);
				
				str="retLines";
				
			}
			/*if(rs!=null) rs.close(); if(rs2!=null) rs2.close(); if(rs2L!=null) rs2L.close(); if(rsQTL!=null) rsQTL.close(); if(rsMap!=null) rsMap.close();			
			if(st!=null) st.close(); if(stmt2!=null) stmt2.close(); if(stLoc!=null) stLoc.close(); if(stCenM!=null) stCenM.close(); if(stLocM!=null) stLocM.close(); if(stmtM!=null) stmtM.close(); if(stmtMap!=null) stmtMap.close();
			*/
			if(con!=null) con.close(); if(conn!=null) conn.close();
		}catch(Exception e){
			e.printStackTrace();
			
			factory.close();
		}finally{
			
			try{		      		
	      		if(con!=null) con.close();conn.close();
	      		factory.close();
	         }catch(Exception e){System.out.println(e);}
		}
		return am.findForward(str);
	}
}
