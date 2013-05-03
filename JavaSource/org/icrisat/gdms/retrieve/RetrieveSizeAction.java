package org.icrisat.gdms.retrieve;

import java.io.PrintWriter;
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
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;

public class RetrieveSizeAction extends Action{
	Connection con=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		try{
			//String crop=req.getSession().getAttribute("crop").toString();
			ResultSet rs=null;
			ResultSet rs3=null;
			ResultSet rs1=null;
			//ResultSet rs=null;
			//int datasetId=0;
			String datasetId="";
			String datasetType="";
			ResultSet rs2=null;
			
			ResultSet rsG=null;
			ResultSet rsP=null;
			ManagerFactory factory = null;
			
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			
			
			Properties p=new Properties();
			String server="";
			String cc="";
			//p.load(new FileInputStream(session.getServletContext().getRealPath("//")+"\\DatabaseConfig.properties"));
			System.out.println("......:"+p.getProperty("local.host"));		
			
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			
			factory = new ManagerFactory(local, central);*/
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			
			Statement stmt=con.createStatement();
			Statement stmt1=con.createStatement();
			Statement stmt2=con.createStatement();
			Statement stmt3=con.createStatement();
			Statement st=con.createStatement();
			Statement stP=con.createStatement();
			String[] qryStr=req.getParameter("ChkDataSets").toString().split("!~!");
			String genotype=qryStr[0];
			List<String> genotypeList=new ArrayList<String>();
			int nid=0;
			String gid="";
			String nids="";
			int gidO=0;
			ArrayList pGidsA=new ArrayList();
			String pGids="";
			String finalGids="";
			//System.out.println(",,,,,,,,,,,,,,,,,:"+qryStr[1]);
			datasetType=qryStr[1];
			genotypeList.add(qryStr[0]);
			//System.out.println("select gid from germplasm_temp where germplasm_name='"+genotype+"'");
			
			ArrayList nidsList=new ArrayList();
			List<GidNidElement> results = manager.getGidAndNidByGermplasmNames(genotypeList);
			//System.out.println("RESULTS (getGidAndNidByGermplasmNames): " + results);
			for(int r=0;r<results.size();r++){
				gid=gid+results.get(r).getGermplasmId()+",";				
			}
			
			/*rs3=st.executeQuery("select gid from names where nval='"+genotype+"'");
			while(rs3.next()){
				gid=gid+rs3.getInt(1)+",";
			}*/
			//String nid="";
			System.out.println("select gid,nid,dataset_id from gdms_acc_metadataset where gid in ("+gid.substring(0,gid.length()-1)+")");
			rs=stmt.executeQuery("select gid,nid,dataset_id from gdms_acc_metadataset where gid in ("+gid.substring(0,gid.length()-1)+")");
			while(rs.next()){
				gidO=rs.getInt(1);
				nid=rs.getInt(2);
				datasetId=datasetId+rs.getInt(3)+",";
				//count++;
			}
			
			
			if(datasetType.equalsIgnoreCase("mapping")){
				rsP=stP.executeQuery("select parent_a_gid, parent_b_gid from gdms_mapping_pop");
				while(rsP.next()){
					pGids=pGids+rsP.getInt(1)+","+rsP.getInt(2)+",";
				}
				/*String[] pGids1=pGids.split(",");
				for(int p=0;p<pGids1.length;p++){
					if(!(pGidsA.contains(pGids1[p])))
						pGidsA.add(pGids1[p]);
				}
				for(int a=0;a<pGidsA.size();a++){
					finalGids=finalGids+pGidsA.get(a)+",";
				}*/
			}
			PrintWriter pr = res.getWriter();
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");	
			String size="";
			String markerId="";
			
			/*if(datasetType.equalsIgnoreCase("SNP")){
				rs1=st.executeQuery("SELECT marker_id FROM char_values WHERE gid="+gid+" and datasetId="+datasetId);
			}else if((datasetType.equalsIgnoreCase("SSR"))||(datasetType.equalsIgnoreCase("DArT"))){
				rs1=st.executeQuery("SELECT marker_id FROM allele_values WHERE gid="+gid+" and datasetId="+datasetId);
			}else if(datasetType.equalsIgnoreCase("mapping")){
				rs1=st.executeQuery("SELECT marker_id FROM map_char_values WHERE gid="+gid+" and datasetId="+datasetId);
			}*/
			System.out.println("SELECT DISTINCT marker_id FROM gdms_marker_metadataset JOIN gdms_acc_metadataset ON gdms_marker_metadataset.dataset_id=gdms_acc_metadataset.dataset_id WHERE gdms_marker_metadataset.dataset_id in("+datasetId.substring(0, datasetId.length()-1)+") and gdms_acc_metadataset.gid="+gidO+" order by gdms_marker_metadataset.marker_id");
			rs1=st.executeQuery("SELECT DISTINCT marker_id FROM gdms_marker_metadataset JOIN gdms_acc_metadataset ON gdms_marker_metadataset.dataset_id=gdms_acc_metadataset.dataset_id WHERE gdms_marker_metadataset.dataset_id in("+datasetId.substring(0, datasetId.length()-1)+") and gdms_acc_metadataset.gid="+gidO+" order by gdms_marker_metadataset.marker_id");
			while(rs1.next()){
				markerId=markerId+rs1.getInt(1)+",";
			}

			/*if(datasetType.equalsIgnoreCase("SNP")){
				rsG=stmt2.executeQuery("select distinct gid from char_values where marker_id in("+markerId.substring(0,markerId.length()-1)+") order by gid desc");
			}else if((datasetType.equalsIgnoreCase("SSR"))||(datasetType.equalsIgnoreCase("DArT"))){
				rsG=stmt2.executeQuery("select distinct gid from allele_values where marker_id in("+markerId.substring(0,markerId.length()-1)+") order by gid desc");
			}else if(datasetType.equalsIgnoreCase("mapping")){
				rsG=stmt2.executeQuery("select gid from map_char_values where marker_id in("+markerId.substring(0,markerId.length()-1)+") order by gid desc");
			}*/
			if(datasetType.equalsIgnoreCase("mapping")){
				//System.out.println("SELECT DISTINCT nid FROM gdms_acc_metadataset JOIN gdms_marker_metadataset ON gdms_acc_metadataset.dataset_id=gdms_marker_metadataset.dataset_id WHERE gdms_acc_metadataset.dataset_id in("+datasetId.substring(0, datasetId.length()-1)+") and gdms_marker_metadataset.marker_id in("+markerId.substring(0,markerId.length()-1)+") and gdms_acc_metadataset.gid not in("+pGids.substring(0, pGids.length()-1)+") order by nid desc");
				rsG=stmt2.executeQuery("SELECT DISTINCT nid FROM gdms_acc_metadataset JOIN gdms_marker_metadataset ON gdms_acc_metadataset.dataset_id=gdms_marker_metadataset.dataset_id WHERE gdms_acc_metadataset.dataset_id in("+datasetId.substring(0, datasetId.length()-1)+") and gdms_marker_metadataset.marker_id in("+markerId.substring(0,markerId.length()-1)+") and gdms_acc_metadataset.gid not in("+pGids.substring(0, pGids.length()-1)+") order by nid desc");
			}else{
				//System.out.println("SELECT DISTINCT nid FROM gdms_acc_metadataset JOIN gdms_marker_metadataset ON gdms_acc_metadataset.dataset_id=gdms_marker_metadataset.dataset_id WHERE gdms_acc_metadataset.dataset_id in("+datasetId.substring(0, datasetId.length()-1)+") and gdms_marker_metadataset.marker_id in("+markerId.substring(0,markerId.length()-1)+") order by nid desc");
				rsG=stmt2.executeQuery("SELECT DISTINCT nid FROM gdms_acc_metadataset JOIN gdms_marker_metadataset ON gdms_acc_metadataset.dataset_id=gdms_marker_metadataset.dataset_id WHERE gdms_acc_metadataset.dataset_id in("+datasetId.substring(0, datasetId.length()-1)+") and gdms_marker_metadataset.marker_id in("+markerId.substring(0,markerId.length()-1)+") order by nid desc");
			}
			
			while(rsG.next()){
				nids=nids+rsG.getInt(1)+",";
				if(!(rsG.getInt(1)==nid))
					nidsList.add(rsG.getInt(1));
			}
			//System.out.println(".........:"+nidsList);
			//System.out.println("select distinct germplasm_name from germplasm_temp where gid in("+gids.substring(0,gids.length()-1)+") AND gid != "+gid+" order by gid desc");
			/*System.out.println("select distinct nval from names where nid in("+nids.substring(0,nids.length()-1)+") AND gid != "+gidO+" order by gid desc");
			rs2=stmt3.executeQuery("select distinct nval from names where nid in("+nids.substring(0,nids.length()-1)+") AND gid != "+gidO+" order by gid desc");*/
			pr.println("<data>");
			Name names = null;
			for(int n=0;n<nidsList.size();n++){				
				names=manager.getGermplasmNameByID(Integer.parseInt(nidsList.get(n).toString()));
				String str = names.getNval();	
				//System.out.println(str);
				pr.println("<details><![CDATA[" + str + "]]></details>");
				
			}
			
			//pr.println("<details><![CDATA[- Select -]]></details>");
			/*while(rs2.next()){
				String str = rs2.getString(1);	
				//System.out.println(str);
				pr.println("<details><![CDATA[" + str + "]]></details>");
				
			}*/
			
			pr.println("</data>");	 
			
			return null;		
		}catch(Exception e){
			e.printStackTrace();
		
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		      		
		         }catch(Exception e){System.out.println(e);}
			}
		//System.out.println("done");
		return am.findForward("ret");
	}
	

}
