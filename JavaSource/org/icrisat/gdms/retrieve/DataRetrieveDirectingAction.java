package org.icrisat.gdms.retrieve;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
		System.out.println("**********************************"+op);
		/*hsession = HibernateSessionFactory.currentSession();
		tx=hsession.beginTransaction();
		*/
		DynaActionForm df = (DynaActionForm) af;
		Connection con=null;
		ResultSet rs=null;
		ResultSet rs1=null;
		ResultSet rs2=null;
		ResultSet rsQTL=null;
		ResultSet rsDS=null;
		ResultSet rsMap=null;
		ArrayList qtlList=new ArrayList();
		ArrayList gids =new ArrayList();
		int count=0;
		ManagerFactory factory=null;
		int mapCount=0;
		try{
			//crop=req.getSession().getAttribute("crop").toString();
			/*Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/icis","root","root");*/
			
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			session.setAttribute("mapsCount", mapCount);
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("localhost", "3306", "ivis", "root", "root");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("localhost", "3306", "ibdb_ivis", "root", "root");*/
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");*/
			
			//factory = new ManagerFactory(local, central);
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			GenotypicDataManager manager1 = factory.getGenotypicDataManager();
			
			String datasetId="";
			Statement stmt=con.createStatement();
			Statement stmt1=con.createStatement();
			Statement stmtR=con.createStatement();
			Statement st=con.createStatement();
			if(op.equalsIgnoreCase("first")){
				/** retieving crops from the database 
				 * this is executed when the genotyping data option is selected		
				 * under retrievals  
				 *  **/
				String option=req.getParameter("reportType");
				/*if (option.equalsIgnoreCase("genotyping"))
					query=hsession.createQuery("select distinct species from DatasetBean ORDER BY species asc");
				else
					query=hsession.createQuery("select distinct species from MarkerInfoBean ORDER BY species asc");
				listValues=query.list();
				//itList=listValues.iterator();
				session.setAttribute("listValues", listValues);
				System.out.println(".................... "+listValues);
				*/str="retSpecies";
			}else if(op.equalsIgnoreCase("second")){
				//crop=(String)df.get("crop");
				String option=req.getParameter("reportType");
				System.out.println("option ="+option);
				//session.setAttribute("crop", crop);
				if (option.equalsIgnoreCase("genotyping"))
					str="directing";
				else
					str="markerPage";
			}else if(op.equalsIgnoreCase("polyType")){
				str="directingPoly";
			}else if(op.equalsIgnoreCase("poly")){
				ResultSet rsD=null;
				/** retrieving germplasm names based on crop **/
				System.out.println("...............:"+req.getParameter("op"));
				String polyType=req.getParameter("op");
				session.setAttribute("polyType", polyType);
				String germplasmName="";
				datasetId="";
				ArrayList germName=new ArrayList();
				ArrayList pGidsA=new ArrayList();
				String pGids="";
				String finalGids="";
				String nids="";
				//String crop=req.getSession().getAttribute("crop").toString();
				if(polyType.equalsIgnoreCase("fingerprinting")){
					System.out.println("SELECT dataset_id FROM gdms_dataset WHERE dataset_type!='mapping' AND dataset_type !='QTL'");
					rsD=stmt.executeQuery("SELECT dataset_id FROM gdms_dataset WHERE dataset_type!='mapping' AND dataset_type !='QTL'");
				}else if(polyType.equalsIgnoreCase("mapping")){
					System.out.println("SELECT dataset_id FROM gdms_dataset WHERE dataset_type ='mapping' AND dataset_type !='QTL'");
					rsD=stmt.executeQuery("SELECT dataset_id FROM gdms_dataset WHERE dataset_type ='mapping' AND dataset_type !='QTL'");
					rs2=stmt1.executeQuery("select parent_a_gid, parent_b_gid from gdms_mapping_pop");
					while(rs2.next()){
						pGids=pGids+rs2.getInt(1)+","+rs2.getInt(2)+",";
					}
				}
				while(rsD.next()){
					datasetId=datasetId+rsD.getInt(1)+",";
				}
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
						//System.out.println("SELECT DISTINCT(nid) FROM acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+")");
						rs2=st.executeQuery("SELECT DISTINCT(nid) FROM gdms_acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+")");
						while(rs2.next()){
							nids =nids+rs2.getInt(1)+",";
							nidsList.add(rs2.getInt(1));
						}
						
						
					}else if(polyType.equalsIgnoreCase("mapping")){
						//System.out.println("SELECT distinct nval FROM names WHERE nid IN (SELECT DISTINCT(nid) FROM acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+") and gid not in("+finalGids.substring(0, finalGids.length()-1)+")) ORDER BY nval");
						//rs1=stmtR.executeQuery("SELECT distinct nval FROM names WHERE nid IN (SELECT DISTINCT(nid) FROM acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+") and gid not in("+finalGids.substring(0, finalGids.length()-1)+")) ORDER BY nval");
						rs2=st.executeQuery("SELECT DISTINCT(nid) FROM gdms_acc_metadataset where dataset_id in("+datasetId.substring(0,datasetId.length()-1)+") and gid not in("+finalGids.substring(0, finalGids.length()-1)+")");
						while(rs2.next()){
							nids =nids+rs2.getInt(1)+",";
							nidsList.add(rs2.getInt(1));
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
				Statement stmtDS=con.createStatement();
				Statement stmtM=con.createStatement();
				Statement stmtMap=con.createStatement();
				String markers="";
				//crop=session.getAttribute("crop").toString();
				//System.out.println("select dataset_desc from dataset where dataset_type !='QTL'");
				//rsDS=stmtDS.executeQuery("select dataset_desc from dataset where dataset_type !='QTL'");
				int sta=1;
				int retRows=12;
				//GenotypicDataManager m=factory.getGenotypicDataManager();
				//m.getDatasetNames(sta, retRows, );
				rsDS=stmtDS.executeQuery("select dataset_name from gdms_dataset where dataset_type !='QTL'");
				while(rsDS.next()){
					dataSetList.add(rsDS.getString(1));
				}
				//System.out.println("dataSetList="+dataSetList);
							
				session.setAttribute("dataSetList", dataSetList);
				str="retDataset";
			
			}else if(op.equalsIgnoreCase("mapsRet")){
				ArrayList mapList=new ArrayList();
				Statement stmtM=con.createStatement();
				Statement stmtMap=con.createStatement();
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
				
				Statement stmt2=con.createStatement();
				
				
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
				//System.out.println("*****************END*******************");
			}
		}catch(Exception e){
			e.printStackTrace();
			
			factory.close();
		}finally{
			
			try{		      		
	      		if(con!=null) con.close();
	      		factory.close();
	         }catch(Exception e){System.out.println(e);}
		}
		return am.findForward(str);
	}
}
