package org.icrisat.gdms.delete;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.gdms.DatasetElement;

public class DataDeletionAction extends Action{

	Connection con=null;
	String str="";
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		DynaActionForm df = (DynaActionForm) af;	
		String delData=df.get("getOp").toString();
		//System.out.println("delData="+delData);
		//String crop=req.getSession().getAttribute("crop").toString();
		ManagerFactory factory = null;
		try{
			
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			
			DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			
			factory = new ManagerFactory(local, central);
			GenotypicDataManager gdms=factory.getGenotypicDataManager();
			
			
			ResultSet rs=null;
			ResultSet rs2=null;
			ResultSet rs1=null;
			Statement stmt=con.createStatement();
			Statement stmtR=con.createStatement();
			Statement st=con.createStatement();
			Statement stR=con.createStatement();
			Statement stD=con.createStatement();
			Statement stDa=con.createStatement();
			Statement stP=con.createStatement();
			
			Statement stPD=con.createStatement();
			Statement stmtP=con.createStatement();
			
			ResultSet rs3=null;
			Statement stmtPD=con.createStatement();
			
			
			int datasetID=0;
			String datasetType="";
			String qtls="";
			String marker_type="";
			String mapping_type="";
			String[] strArr1=delData.split(";;");
			for(int i=0;i<strArr1.length;i++){
			
				if(!(strArr1[0]).equals(" ")){
					//System.out.println("            Genotyping data");
					String[] strArr2=strArr1[0].split("!~!");
					for(int d=0;d<strArr2.length;d++){
						
						
						/*List<DatasetElement> results = gdms.getDatasetDetailsByDatasetName(strArr2[d], Database.LOCAL);
				        System.out.println("RESULTS (testGetDatasetDetailsByDatasetName): " + results);*/
						rs=stmt.executeQuery("select dataset_id, dataset_type from gdms_dataset where dataset_name='"+strArr2[d]+"'");
						while(rs.next()){
							datasetID=rs.getInt(1);
							datasetType=rs.getString(2);
						}
						if(datasetType.equalsIgnoreCase("SNP")){
							int del=stmtR.executeUpdate("delete from gdms_char_values where dataset_id='"+datasetID+"'");						
						}else if(datasetType.equalsIgnoreCase("SSR")){
							int del=stmtR.executeUpdate("delete from gdms_allele_values where dataset_id='"+datasetID+"'");							
						}else if(datasetType.equalsIgnoreCase("DArT")){
							int del=stmtR.executeUpdate("delete from gdms_allele_values where dataset_id='"+datasetID+"'");
							int delDA=stmtR.executeUpdate("delete from gdms_dart_values where dataset_id='"+datasetID+"'");						
						}else if(datasetType.equalsIgnoreCase("mapping")){
							String exists="no";
							rs1=stP.executeQuery("select distinct marker_type from gdms_marker where marker_id in(select marker_id from gdms_marker_metadataset where dataset_id="+datasetID+")");
							while(rs1.next()){
								//System.out.println(rs1.getString(1));
								marker_type=rs1.getString(1);
							}
							rs3=stmtPD.executeQuery("select mapping_type from gdms_mapping_pop where dataset_id="+datasetID);
							while(rs3.next()){
								mapping_type=rs3.getString(1);
							}
							if(mapping_type.equalsIgnoreCase("allelic")){
								if(marker_type.equalsIgnoreCase("snp")){
									rs2=stmtP.executeQuery("select * from gdms_char_values where dataset_id="+datasetID);
									if(rs2.next()){
										exists="yes";
									}
									if(exists.equalsIgnoreCase("yes")){
										int delPD=stPD.executeUpdate("delete from gdms_char_values where dataset_id="+datasetID);
									}
								}else if((marker_type.equalsIgnoreCase("ssr"))||(marker_type.equalsIgnoreCase("DArT"))){
									//int delPD=stPD.executeUpdate("");
									rs2=stmtP.executeQuery("select * from gdms_allele_values where dataset_id="+datasetID);
									if(rs2.next()){
										exists="yes";
									}
									if(exists.equalsIgnoreCase("yes")){
										int delPD=stPD.executeUpdate("delete from gdms_allele_values where dataset_id="+datasetID);
									}
								}
							}
							int del=stmtR.executeUpdate("delete from gdms_mapping_pop_values where dataset_id='"+datasetID+"'");
							int delDA=stmtR.executeUpdate("delete from gdms_mapping_pop where dataset_id='"+datasetID+"'");		
						}
						int del1=st.executeUpdate("delete from gdms_dataset_users where dataset_id='"+datasetID+"'");
						//int del2=stR.executeUpdate("delete from dataset_details where dataset_id='"+datasetID+"'");
						int del2=stR.executeUpdate("delete from gdms_acc_metadataset where dataset_id='"+datasetID+"'");
						int del4=stDa.executeUpdate("delete from gdms_marker_metadataset where dataset_id='"+datasetID+"'");
						int del3=stD.executeUpdate("delete from gdms_dataset where dataset_id='"+datasetID+"'");	
					}
				}
				if(!(strArr1[1]).equals(" ")){
					//System.out.println(".............    QTLs data");
					String[] strArr2=strArr1[1].split("!~!");
					for(int d=0;d<strArr2.length;d++){
						rs=stmt.executeQuery("select dataset_id from gdms_dataset where dataset_name='"+strArr2[d]+"'");
						while(rs.next()){
							datasetID=rs.getInt(1);							
						}
						rs1=st.executeQuery("select qtl_id from gdms_qtl where dataset_id='"+datasetID+"'");
						while(rs1.next()){
							qtls=qtls+rs1.getInt(1)+";;";						
						}
						String[] qtl=qtls.split(";;");
						for(int q=0;q<qtl.length;q++){
							int del=stmtR.executeUpdate("delete from gdms_qtl_details where qtl_id='"+qtl[q]+"'");
							int delDA=stmtR.executeUpdate("delete from gdms_qtl where qtl_id='"+qtl[q]+"'");
						}
						int del1=st.executeUpdate("delete from gdms_dataset_users where dataset_id='"+datasetID+"'");
						//int del2=stR.executeUpdate("delete from dataset_details where dataset_id='"+datasetID+"'");
						int del3=stD.executeUpdate("delete from gdms_dataset where dataset_id='"+datasetID+"'");	
					}
				}
				if(!(strArr1[2]).equals(" ")){
					//System.out.println(".............   Maps data");
					String[] strArr2=strArr1[2].split("!~!");
					for(int d=0;d<strArr2.length;d++){
						rs=stmt.executeQuery("select map_id from gdms_map where map_name='"+strArr2[d]+"'");
						while(rs.next()){
							datasetID=rs.getInt(1);						
						}
						int del=stmtR.executeUpdate("delete from gdms_markers_onmap where map_id='"+datasetID+"'");
						int delDA=stmtR.executeUpdate("delete from gdms_map where map_id='"+datasetID+"'");	
					}
				}
			}
			str="delete";
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		         }catch(Exception e){System.out.println(e);}
			}
		
		
		return am.findForward(str);
	}
	

}
