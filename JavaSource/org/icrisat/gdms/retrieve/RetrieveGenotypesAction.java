/**
 * 
 */
package org.icrisat.gdms.retrieve;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

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
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.upload.CharArrayBean;
import org.icrisat.gdms.upload.DatasetBean;
import org.icrisat.gdms.upload.IntArrayBean;

/**
 * @author psrikalyani
 *
 */
public class RetrieveGenotypesAction extends Action{
	java.sql.Connection conn;
	java.sql.Connection con;
	
	public ActionForward execute(ActionMapping am, ActionForm af, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		// TODO Auto-generated method stub
		HttpSession session = req.getSession(true);
		//String crop=req.getSession().getAttribute("crop").toString();
		
		ArrayList nids=new ArrayList();
		String marker_name="";
		
		String mapping_type="";
		String dataset_id="";
		int mid=0;
		String finalStr="";
		String mid1=req.getParameter("data");
		//System.out.println(".................... :"+mid1);
		mid=Integer.parseInt(mid1);
		ManagerFactory factory = null;
		Properties prop=new Properties();
		try{
			/*Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/icis","root","root");*/
			
			/*ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();*/
			
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
			
			
			
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			
			ArrayList gidsT=new ArrayList();
			ResultSet rsC=null;
			ResultSet rsL=null;
			ResultSet rsM=null;
			ResultSet rs1=null;
			ResultSet rs2=null;			
			ResultSet rsD=null;
			ResultSet rsNC=null;
			ResultSet rsNL=null;
			ResultSet rsML=null;
			
			
			Statement stmtR=con.createStatement();			
			Statement st=con.createStatement();
			Statement st2=con.createStatement();
			
			
			ArrayList gids =new ArrayList();
			SortedMap map = new TreeMap();
			rsC=stCen.executeQuery("select dataset_id from gdms_marker_metadataset where marker_id="+ mid+"");
			while(rsC.next()){
				//marker_name=rs.getString(1);
				dataset_id=dataset_id+rsC.getInt(1)+",";
			}
			rsL=stLoc.executeQuery("select dataset_id from gdms_marker_metadataset where marker_id="+ mid+"");
			while(rsL.next()){
				//marker_name=rs.getString(1);
				dataset_id=dataset_id+rsL.getInt(1)+",";
			}
			
			
			
			String[] datasetId=dataset_id.split(",");
			for(int d=0;d<datasetId.length;d++){
				//System.out.println("dataset_id="+dataset_id);
				rsNC=stCen.executeQuery("select nid from gdms_acc_metadataset where dataset_id ="+datasetId[d]);
				while(rsNC.next()){
					if(!nids.contains(rsNC.getInt(1)))
						nids.add(rsNC.getInt(1));
				}
				rsNL=stLoc.executeQuery("select nid from gdms_acc_metadataset where dataset_id ="+datasetId[d]);
				while(rsNL.next()){
					if(!nids.contains(rsNL.getInt(1)))
						nids.add(rsNL.getInt(1));
				}
			
				/** 
				 * implementing middleware jar file  
				 * retrieving gids, nval from names through nid
				 */
				
				GermplasmDataManager manager = factory.getGermplasmDataManager();
				Name names = null;
		
				for(int n=0;n<nids.size();n++){
					names=manager.getGermplasmNameByID(Integer.parseInt(nids.get(n).toString()));
					if(!gids.contains(names.getGermplasmId()))
						gids.add(names.getGermplasmId());
					map.put(names.getGermplasmId(), names.getNval());
					//gids=gids+names.getGermplasmId()+",";
				}
			
				rsM=stCen.executeQuery("select marker_name,marker_type from gdms_marker where marker_id="+ mid+"");
				while(rsM.next()){			
					marker_name=rsM.getString(1);
					//marker_type=rsM.getString(2);
				}
				rsML=stLoc.executeQuery("select marker_name,marker_type from gdms_marker where marker_id="+ mid+"");
				while(rsML.next()){			
					marker_name=rsML.getString(1);
					//marker_type=rsM.getString(2);
				}
				String dataset_type="";
				if(Integer.parseInt(datasetId[d])>0){
					stmtR=conn.createStatement();
					st=conn.createStatement();
					st2=conn.createStatement();
				}else{
					stmtR=con.createStatement();
					st=con.createStatement();
					st2=con.createStatement();
				}
				rs1=stmtR.executeQuery("select dataset_type from gdms_dataset where dataset_id="+datasetId[d]+"");
				while(rs1.next()){
					dataset_type=rs1.getString(1);
				}
				//System.out.println("dataset_type="+dataset_type+"   dataset_id="+dataset_id);
				if(dataset_type.equalsIgnoreCase("snp")){
					//rsD=st.executeQuery("SELECT distinct germplasm_temp.germplasm_name, char_values.gid FROM char_values JOIN germplasm_temp ON char_values.gid=germplasm_temp.gid WHERE char_values.marker_id="+ mid+"");
					rsD=st.executeQuery("SELECT distinct gdms_char_values.gid FROM gdms_char_values WHERE gdms_char_values.marker_id="+ mid+"");
					
				}else if((dataset_type.equalsIgnoreCase("ssr"))||(dataset_type.equalsIgnoreCase("DArT"))){
					//rsD=st.executeQuery("SELECT distinct germplasm_temp.germplasm_name, allele_values.gid FROM allele_values JOIN germplasm_temp ON allele_values.gid=germplasm_temp.gid WHERE allele_values.marker_id="+ mid+"");
					rsD=st.executeQuery("SELECT distinct gdms_allele_values.gid FROM gdms_allele_values WHERE gdms_allele_values.marker_id="+ mid+"");
					
				}else if(dataset_type.equalsIgnoreCase("mapping")){
					int parentAgid=0;
					int parentBgid=0;
					String parents="";
					//rsD=st.executeQuery("SELECT distinct germplasm_temp.germplasm_name, map_char_values.gid FROM map_char_values JOIN germplasm_temp ON map_char_values.gid=germplasm_temp.gid WHERE map_char_values.marker_id="+ mid+"");
					rsD=st.executeQuery("SELECT distinct gdms_mapping_pop_values.gid FROM gdms_mapping_pop_values WHERE gdms_mapping_pop_values.marker_id="+ mid+"");
					rs2=st2.executeQuery("select mapping_type, parent_a_nid, parent_b_nid from gdms_mapping_pop where dataset_id="+datasetId[d]);
					while(rs2.next()){
						mapping_type=rs2.getString(1);
						parentAgid=rs2.getInt(2);
						parentBgid=rs2.getInt(3);
						if(!gidsT.contains(parentAgid))
							gidsT.add(parentAgid);
						if(!gidsT.contains(parentBgid))
							gidsT.add(parentBgid);
						//parents=parentAgid+","+parentBgid;
					}				
				}	
				while(rsD.next()){
					//finalStr=finalStr+rsD.getInt(2)+"!~!"+rsD.getString(1)+"~!~";
					if(!gidsT.contains(rsD.getInt(1)))
						gidsT.add(rsD.getInt(1));
				}																							
			}
			//System.out.println("gidsT="+gidsT);
			for(int g=0;g<gidsT.size();g++){
				if(gids.contains(gidsT.get(g))){
					finalStr=finalStr+gidsT.get(g)+"!~!"+map.get(gidsT.get(g))+"~!~";
				}
			}
				
			//System.out.println(finalStr);
			session.setAttribute("finalStr", finalStr);
			req.setAttribute("MName", marker_name);
			/*if(rsC!=null) rsC.close(); if(rsL!=null) rsL.close();  if(rsM!=null) rsM.close(); if(rs1!=null) rs1.close(); if(rs2!=null) rs2.close();  if(rsD!=null) rsD.close(); if(rsNC!=null) rsNC.close(); if(rsNL!=null)  rsNL.close(); if(rsML!=null) rsML.close();
			if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close(); if(st!=null) st.close(); if(st2!=null) st2.close(); if(stmtR!=null) stmtR.close();
			*/
			if(con!=null) con.close(); if(conn!=null) conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close(); conn.close();
		      		factory.close();
		      		/*long time = System.currentTimeMillis();
		      	  System.gc();
		      	  System.out.println("It took " + (System.currentTimeMillis()-time) + " ms");*/
		         }catch(Exception e){System.out.println(e);}
			}
		
		return am.findForward("retGenos");
	}
	

}
