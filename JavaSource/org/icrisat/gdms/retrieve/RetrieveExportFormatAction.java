/**
 * 
 */
package org.icrisat.gdms.retrieve;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
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
import org.apache.struts.action.DynaActionForm;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;
import org.hibernate.Query;
import org.icrisat.gdms.common.ExportFormats;
import org.icrisat.gdms.common.MaxIdValue;


public class RetrieveExportFormatAction extends Action{
	Connection con=null;
	String str="";
	String map="";
	String mapData="";
	ArrayList list=new ArrayList();
	ArrayList mlist=new ArrayList();
	//private Session hsession;	
	//private Transaction tx;
	boolean qtlExists=false;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		//String crop=req.getSession().getAttribute("crop").toString();
		ManagerFactory factory = null;
		
		HttpSession session = req.getSession(true);
		if(session!=null){
			session.removeAttribute("qtlExistsSes");
		}
		MaxIdValue r=new MaxIdValue();
		ArrayList gListExp=new ArrayList();
		ArrayList mListExp=new ArrayList();
		DecimalFormat decfor = new DecimalFormat("#.0");
		int qtlCount=0;
		String chValues="";
		Query charQuery=null;
		String markers="";
		String gids="";
		String gidslist="";
		String datasetName="";
		DynaActionForm df = (DynaActionForm) af;
		//System.out.println("********************  "+df.get("opType"));
		String op=df.get("opType").toString();
		req.getSession().setAttribute("op", op);
		//System.out.println(df.get("FormatcheckGroup"));
		String format=df.get("FormatcheckGroup").toString();
		ExportFormats ef=new ExportFormats();
		String markerslist="";
		String accessionslist="";
		String filePath="";
		filePath=req.getSession().getServletContext().getRealPath("//");
		if(!new File(filePath+"/jsp/analysisfiles").exists())
	   		new File(filePath+"/jsp/analysisfiles").mkdir();
		
		//if((format.equalsIgnoreCase("flapjack"))||(format.equalsIgnoreCase("cmtv")))
		if(format.equalsIgnoreCase("flapjack"))
			map=df.get("maps").toString();
		
		req.getSession().setAttribute("exportFormat", format);
		Calendar now = Calendar.getInstance();
		String mSec=now.getTimeInMillis()+"";
		//String fname=filePath+"/jsp/analysisfiles/matrix"+mSec+".xls";
		
		if(session!=null){
			session.removeAttribute("msec");			
		}
		try{
			ResultSet rs=null;			
			ResultSet rs1=null;
			ResultSet rsG=null;	
			ResultSet rsM=null;
			ResultSet rs2=null;
			ResultSet rsD=null;
			ResultSet rsN=null;
			ResultSet rsMT=null;
			ResultSet rsPD=null;
			ResultSet rsP=null;
			ResultSet rs3=null;
			ResultSet rsQ=null;
			
			ResultSet rsP1=null;
			ResultSet rsP2=null;
			
			
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();
			
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			*/
		//	factory = new ManagerFactory(local, central);
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			
			String parentsListToWrite="";
			
			ArrayList gidsList=new ArrayList();
			ArrayList midsList=new ArrayList();
			ArrayList strL=new ArrayList();
			//String strL="";
			String qtl_id="";
			ArrayList qtlData=new ArrayList();
			String datasetType="";
			String parentsNames="";
			String parents="";
			String parentB="";
			String parentA="";
			Statement stmt=con.createStatement();
			Statement stmt1=con.createStatement();
			Statement stmt2=con.createStatement();
			Statement stmtM=con.createStatement();
			Statement st=con.createStatement();
			Statement stmtG=con.createStatement();
			Statement stmtN=con.createStatement();
			Statement stmtMT=con.createStatement();
			Statement stmtPD=con.createStatement();
			Statement stmtP=con.createStatement();
			Statement stP=con.createStatement();
			Statement stQ=con.createStatement();
			
			Statement stP1=con.createStatement();
			Statement stP2=con.createStatement();
			
			
			req.getSession().setAttribute("msec", mSec);
			String mapping_type="";
			String mType="";
			ArrayList parentsList = new ArrayList();
			ArrayList gidList=new ArrayList();
			//System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^op="+op);
			
			String exportOpType="";
			
			if(format.equalsIgnoreCase("flapjack")){
				exportOpType=df.get("exportTypeH").toString();
				//System.out.println(",,,,,,,,,,,,,,,:"+df.get("exportTypeH"));
			}
			
			
			if(op.equalsIgnoreCase("dataset")){
				
				/** retrieving data of the whole dataset for export formats **/				
				datasetName=df.get("dataset").toString();
				  HashMap<Object, String> gMap = new HashMap<Object, String>();
				  HashMap<Object, String> mMap = new HashMap<Object, String>();
				int datasetId=0;
				ArrayList parentsData=new ArrayList();
				ArrayList parentAData=new ArrayList();
				ArrayList parentBData=new ArrayList();
				//String datasetType="";
			
				//Statement st=con.createStatement();
				//list.clear();
				//System.out.println("select dataset_id,dataset_type from dataset where dataset_desc='"+datasetDesc+"'");
				//rs=stmt.executeQuery("select dataset_id,dataset_type from dataset where dataset_desc='"+datasetDesc+"'");
				rs=stmt.executeQuery("select dataset_id,dataset_type from gdms_dataset where dataset_name='"+datasetName+"'");
				while(rs.next()){
					datasetId=rs.getInt(1);
					datasetType=rs.getString(2);					
				}
				String gid="";
				String mid="";
				int parentAint=0;
				int parentBint=0;
				String pgids="";
				
				
				
				long startTime = System.currentTimeMillis();
				//System.out.println("............"+datasetId+"   "+datasetType);
				//rs2=stmt2.executeQuery("SELECT gid from acc_metadataset where dataset_id="+datasetId);
				
				//System.out.println("<<<<<<<<<<<<<<<<<<<< gid="+gid);
				//System.out.println("select marker_id from marker_metadataset where dataset_id="+datasetId+" order by marker_id");
				rs=stmt.executeQuery("select marker_id from gdms_marker_metadataset where dataset_id="+datasetId+" order by marker_id");
				while(rs.next()){
					mid=mid+rs.getInt(1)+",";
				}
				
				rsMT=stmtMT.executeQuery("select distinct marker_type from gdms_marker where marker_id in("+mid.substring(0,mid.length()-1)+")");
				while (rsMT.next()){
					mType=rsMT.getString(1);
				}
				if(datasetType.equalsIgnoreCase("mapping")){
					//System.out.println("IF Mapping ...................");
					rsP=stmt2.executeQuery("select parent_a_gid, parent_b_gid from gdms_mapping_pop where dataset_id="+datasetId);
					rs1=stmt1.executeQuery("select mapping_type from gdms_mapping_pop where dataset_id="+datasetId);
					while(rs1.next()){
						mapping_type=rs1.getString(1);
					}
					
					session.setAttribute("mappingType", mapping_type);
					//System.out.println("<<<<<<<<<<<<<<<<<<<< mapping_type="+mapping_type);
					while(rsP.next()){
						parentsNames=rsP.getInt(1)+","+rsP.getInt(2);	
						parentsList.add(rsP.getInt(1));
						parentsList.add(rsP.getInt(2));
						parentAint=rsP.getInt(1);
						parentBint=rsP.getInt(2);
					}
					//if(format.equalsIgnoreCase("flapjack")){
						//if((mapping_type.equalsIgnoreCase("allelic"))||(format.equalsIgnoreCase("flapjack"))){
						if(mapping_type.equalsIgnoreCase("allelic")){
							/*if(!exportOpType.equals("gname")){
							
							if(!gListExp.contains(parentAint))
								gListExp.add(parentAint);
							if(!gListExp.contains(parentBint))
								gListExp.add(parentBint);
							}*/
							rsP1=stP1.executeQuery("select nid from gdms_acc_metadataset where gid="+parentAint+" and dataset_id="+datasetId);
							rsP2=stP2.executeQuery("select nid from gdms_acc_metadataset where gid="+parentBint+" and dataset_id="+datasetId);
							while(rsP1.next()){
								if(!gidList.contains(rsP1.getInt(1)))
									gidList.add(rsP1.getInt(1));
							}
							while(rsP2.next()){
								if(!gidList.contains(rsP2.getInt(1)))
									gidList.add(rsP2.getInt(1));
							}
							
							
						}
						//System.out.println("SELECT nid from gdms_acc_metadataset where dataset_id="+datasetId+" and gid not in("+parentsNames+") order by nid");
						rs2=stmt2.executeQuery("SELECT nid from gdms_acc_metadataset where dataset_id="+datasetId+" and gid not in("+parentsNames+") order by nid");
						int count=0;
						while(rs2.next()){
							if(!gidList.contains(rs2.getInt(1)))
								gidList.add(rs2.getInt(1));
							gid=gid+rs2.getInt(1)+",";
							count=count+1;
						}
					/*}else{
						/*if(!gListExp.contains(parentAint))
							gListExp.add(parentAint);
						if(!gListExp.contains(parentBint))
							gListExp.add(parentBint);*/
						/*rs2=stmt2.executeQuery("SELECT nid from gdms_acc_metadataset where dataset_id="+datasetId+" and gid not in("+parentsNames+")");
						int count=0;
						while(rs2.next()){
							gid=gid+rs2.getInt(1)+",";
							count=count+1;
						}
					}*/
					//System.out.println("gid in between ==:"+gidList);
					ArrayList pgidsList=new ArrayList();
					
					ArrayList nids=new ArrayList();
					if(mapping_type.equalsIgnoreCase("allelic")){
						rsN=stmtN.executeQuery("select nid from gdms_acc_metadataset where gid in("+parentsNames+")");
						while(rsN.next()){
							nids.add(rsN.getInt(1));
						}
						Name names = null;
						for(int n=0;n<nids.size();n++){
							names=manager.getGermplasmNameByID(Integer.parseInt(nids.get(n).toString()));
							pgids=pgids+names.getGermplasmId()+",";
							if(!(pgidsList.contains(names.getGermplasmId())))
								pgidsList.add(names.getGermplasmId());
							gMap.put(names.getGermplasmId(), names.getNval());
							//gids=gids+names.getGermplasmId()+",";
						}
						if(pgidsList.contains(parentAint)){
							parents=parents+gMap.get(parentAint)+"!~!";
							parentsListToWrite=parentsListToWrite+parentAint+";;"+gMap.get(parentAint)+"!~!";
						}
						if(pgidsList.contains(parentBint)){
							parents=parents+gMap.get(parentBint);
							parentsListToWrite=parentsListToWrite+parentBint+";;"+gMap.get(parentBint)+"!~!";
						}
						
						/*System.out.println("select gid,nval from names where nid in (select nid from acc_metadataset where gid in("+parentsNames+"))");
						rsN=stmtN.executeQuery("select gid,nval from names where nid in (select nid from acc_metadataset where gid in("+parentsNames+"))");*/
					}else{
						//List<Name> names = null;
						Name names = null;
						for(int p=0;p<parentsList.size();p++){
							//names = manager.getNamesByGID(Integer.parseInt(parentsList.get(p).toString()), null, null);
							names=manager.getGermplasmNameByID(Integer.parseInt(parentsList.get(p).toString()));
							//for (Name name : names) {
								pgids=pgids+names.getGermplasmId()+",";
								if(!(pgidsList.contains(names.getGermplasmId())))
									pgidsList.add(names.getGermplasmId());
								///gMap.put(names.getGermplasmId(), names.getNval());
								parentsListToWrite=parentsListToWrite+names.getGermplasmId()+";;"+names.getNval()+"!~!";
					        //}
						}			
						
						/*System.out.println("select gid,nval from names where gid in ("+parentsNames+")");
						rsN=stmtN.executeQuery("select gid,nval from names where gid in ("+parentsNames+")");*/
					}
					
					
					//if(mapping_type.equalsIgnoreCase("allelic")){
						/*for(int p=0; p<pgidsList.size();p++){
							
						}*/
					
						
					//}
						//System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,  parents:"+parents);
					/*System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,  parents:"+parents);
					System.out.println("pgids=:"+pgids);
					System.out.println("mids=:"+mid);*/
					/*String parentAData="";
					String parentBData="";*/
					if(mapping_type.equalsIgnoreCase("allelic")){							
						//System.out.println("....."+"select gid,marker_id, char_value from gdms_char_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid.substring(0,mid.length()-1)+") order by gid, marker_id");
						if((mType.equalsIgnoreCase("SSR"))||(mType.equalsIgnoreCase("DArT"))){
							rsPD=stmtPD.executeQuery("select gid,marker_id, allele_bin_value from gdms_allele_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid.substring(0,mid.length()-1)+") order by gid, marker_id");
						}else if(mType.equalsIgnoreCase("SNP")){
							//System.out.println("select gid,marker_id, char_value from gdms_char_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid.substring(0,mid.length()-1)+") order by gid, marker_id");
							rsPD=stmtPD.executeQuery("select gid,marker_id, char_value from gdms_char_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid.substring(0,mid.length()-1)+") order by gid, marker_id");
						}
						while(rsPD.next()){
							parentsData.add(rsPD.getInt(1)+","+rsPD.getInt(2)+","+rsPD.getString(3));
						}
						
						for(int c=0;c<parentsData.size();c++){
							 String arrP[]=new String[3];
							 StringTokenizer stzP = new StringTokenizer(parentsData.get(c).toString(), ",");
							 int iP=0;
							 while(stzP.hasMoreTokens()){
								 arrP[iP] = stzP.nextToken();
								 iP++;
							 }	
							
							 if(Integer.parseInt(arrP[0])==parentAint){								
								 parentAData.add(parentAint+","+arrP[1]+","+arrP[2]);
							 }else if(Integer.parseInt(arrP[0])==parentBint){									
								 parentBData.add(parentBint+","+arrP[1]+","+arrP[2]);
							 }	
							 
						}
						//System.out.println("parentAData="+parentAData);
						//System.out.println("parentBData="+parentBData);
						if(format.contains("Flapjack")){
							for(int pa=0;pa<parentAData.size();pa++){
								String arrA[]=new String[3];
								StringTokenizer stz = new StringTokenizer(parentAData.get(pa).toString(), ",");
					    		//arrList6 = new String[stz.countTokens()];
					    		int i1=0;				  
					    		while(stz.hasMoreTokens()){				    			
					    			arrA[i1] = stz.nextToken();
					    			i1++;
					    		}
					    		strL.add(arrA[0]+","+arrA[1]+","+arrA[2]);
							}
							for(int pa=0;pa<parentBData.size();pa++){
								String arrB[]=new String[3];
								StringTokenizer stz = new StringTokenizer(parentBData.get(pa).toString(), ",");
					    		//arrList6 = new String[stz.countTokens()];
					    		int i1=0;				  
					    		while(stz.hasMoreTokens()){				    			
					    			arrB[i1] = stz.nextToken();
					    			i1++;
					    		}
					    		strL.add(arrB[0]+","+arrB[1]+","+arrB[2]);
							}
						//strL=parentAData+parentBData;
						}
					}
				}else{
					//System.out.println("  If not mapping");
					rs2=stmt2.executeQuery("SELECT nid from gdms_acc_metadataset where dataset_id="+datasetId);
					int count=0;
					while(rs2.next()){
						if(!gidList.contains(rs2.getInt(1)))
							gidList.add(rs2.getInt(1));
						gid=gid+rs2.getInt(1)+",";
						count=count+1;
					}
				}
				
				//System.out.println("strL="+strL);
				//System.out.println("mid="+mid);
				//System.out.println("..................... nid="+gidList);
				
				//rsG=stmtG.executeQuery("select distinct gid,nval from names where nid in("+ gid.substring(0,gid.length()-1) +") order by gid DESC");
				
				/** 
				 * implementing middleware jar file 
				 */
				
				Name names = null;
			
				for(int n=0;n<gidList.size();n++){
					names=manager.getGermplasmNameByID(Integer.parseInt(gidList.get(n).toString()));
					//gids=gids+names.getGermplasmId()+",";
					
					//System.out.println("&&&&&&&&&&&&&&&&  :"+names.getGermplasmId()+"  "+names.getNval());
					if(!(gMap.containsKey(names.getGermplasmId())))
						gMap.put(names.getGermplasmId(), names.getNval());
					 //gidsList.add(rsG.getString(1));
					
					/*if((format.contains("Flapjack"))&&(exportOpType.equals("gname"))){
						if(!(gListExp.contains(names.getNval())))
							 gListExp.add(names.getNval());
					}else{
						if(!(gListExp.contains(names.getGermplasmId())))
							 gListExp.add(names.getGermplasmId());
					}
					*/					
				}
				//System.out.println("gMap=:"+gMap);
								
				//System.out.println("select marker_id, marker_name from marker where marker_id in("+ mid.substring(0,mid.length()-1) +") order by marker_id ASC");
				rsM=stmtM.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in("+ mid.substring(0,mid.length()-1) +") order by marker_id ASC");
				
				if(datasetType.equalsIgnoreCase("SNP")){
					//System.out.println("select gid, marker_id, char_value from gdms_char_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					rsD=st.executeQuery("select gid, marker_id, char_value from gdms_char_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					
				}else if((datasetType.equalsIgnoreCase("SSR"))||(datasetType.equalsIgnoreCase("DArT"))){
					//System.out.println("select gid, marker_id, allele_bin_value from gdms_allele_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					rsD=st.executeQuery("select gid, marker_id, allele_bin_value from gdms_allele_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					
				}else if(datasetType.equalsIgnoreCase("mapping")){
					//System.out.println("select gid, marker_id, map_char_value from gdms_mapping_pop_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					rsD=st.executeQuery("select gid, marker_id, map_char_value from gdms_mapping_pop_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					
					session.setAttribute("dataset", datasetId);
				}
				
				String str="";
				if((format.contains("Genotyping X Marker Matrix"))&&(mapping_type.equalsIgnoreCase("allelic"))){
					/*if(!(gListExp.contains(parentAint+"")))
						 gListExp.add(parentAint+"");
					if(!(gListExp.contains(parentBint+"")))
						 gListExp.add(parentBint+"");*/
					for(int pa=0;pa<parentAData.size();pa++){
						String arrA[]=new String[3];
						StringTokenizer stz = new StringTokenizer(parentAData.get(pa).toString(), ",");
			    		//arrList6 = new String[stz.countTokens()];
			    		int i1=0;				  
			    		while(stz.hasMoreTokens()){				    			
			    			arrA[i1] = stz.nextToken();
			    			i1++;
			    		}
			    		strL.add(arrA[0]+","+arrA[1]+","+arrA[2]);
					}
					for(int pa=0;pa<parentBData.size();pa++){
						String arrB[]=new String[3];
						StringTokenizer stz = new StringTokenizer(parentBData.get(pa).toString(), ",");
			    		//arrList6 = new String[stz.countTokens()];
			    		int i1=0;				  
			    		while(stz.hasMoreTokens()){				    			
			    			arrB[i1] = stz.nextToken();
			    			i1++;
			    		}
			    		strL.add(arrB[0]+","+arrB[1]+","+arrB[2]);
					}
				}

				ArrayList strL2=new ArrayList();
				while(rsD.next()){
					//strL=strL+rsD.getInt(1)+","+rsD.getInt(2)+","+rsD.getString(3)+"!~!";
					strL.add(rsD.getInt(1)+","+rsD.getInt(2)+","+rsD.getString(3));
				}
				//System.out.println("^^^^^^^^^final strL  :"+strL);
				/*while(rsG.next()){
					
					if(!(gMap.containsKey(rsG.getInt(1))))
						gMap.put(rsG.getInt(1), rsG.getString(2));
					 //gidsList.add(rsG.getString(1));
					 if(!(gListExp.contains(rsG.getString(1))))
							 gListExp.add(rsG.getString(1));
					if(!(gListExp.contains(rsG.getInt(1))))
						 gListExp.add(rsG.getInt(1));
				}*/
				//System.out.println(gMap);
				while(rsM.next()){
					mMap.put(rsM.getInt(1), rsM.getString(2));
					midsList.add(rsM.getString(1));
					if(!(mListExp.contains(rsM.getString(2))))
					mListExp.add(rsM.getString(2));
				}
				Map<Object, String> sortedMap = new TreeMap<Object, String>(gMap);
				Map<Object, String> sortedMarkerMap = new TreeMap<Object, String>(mMap);
	           	Set keys = sortedMap.keySet();


				for (Iterator i = keys.iterator(); i.hasNext(); ){
					int key = Integer.parseInt(i.next().toString());
					String value=(String) sortedMap.get(key);
					//System.out.println(".....:"+key+"   "+value);
					if((format.contains("Flapjack"))&&(exportOpType.equals("gname"))){
						if(!(gListExp.contains(value)))
							gListExp.add(value);
					}else{
						if(!(gListExp.contains(key)))
							 gListExp.add(key);
					}
				}
				//System.out.println("gListExp-=:"+gListExp);
				//System.out.println(strL.size());
				list.clear();
				
				for(int l=0; l<strL.size();l++){
					String arr[]=new String[3];
					//String[] data=strL.get(l).toString().split(",");
					 StringTokenizer stz = new StringTokenizer(strL.get(l).toString(), ",");
					// arr = new String[stz.countTokens()];
					  int i=0;
					//if((midsList.contains(data[1]))&&(gidsList.contains(data[0]))){
					while(stz.hasMoreTokens()){
						arr[i] = stz.nextToken();
					   i++;
					}
					//System.out.println(arr[0]+"   "+arr[1]+"    "+arr[2]);
					//if((midsList.contains(arr[1]))&&(gidsList.contains(arr[0]))){
						//list.add(sortedMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
					if((format.contains("Flapjack"))&&(exportOpType.equals("gname"))){
						list.add(sortedMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
					}else{
						list.add(arr[0]+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
					}
					//}					
				}
				//System.out.println("list="+list);
				
				mapData="";
				
				//qtl_id
				/** retrieving map data for flapjack .map file **/
				if(format.contains("Flapjack")){
					//System.out.println("map="+map.split(" ("));
				    String mapName  = map.substring(0,map.lastIndexOf("("));
				    //System.out.println("select marker_name, linkage_group, start_position from mapping_data where map_name in ('"+mapName+"') order by linkage_group, start_position,marker_name");
					rs=stmt.executeQuery("select marker_name, linkage_group, start_position from gdms_mapping_data where map_name in ('"+mapName+"') order by linkage_group, start_position,marker_name");
					while(rs.next()){
						//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
						mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"~~!!~~";
					}
					ResultSet rsMap=st.executeQuery("select qtl_id from gdms_qtl_details where map_id =(select map_id from gdms_map where map_name ='"+mapName+"')");
					//rsQ=stQ.executeQuery("");
					while(rsMap.next()){
						//System.out.println("..............:"+rsMap.getInt(1));
						qtlCount++;
						qtl_id=qtl_id+rsMap.getInt(1)+",";
					}
					if(qtlCount>0){
						qtlExists=true;
						//System.out.println("................;"+qtl_id);
						//System.out.println("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl_details.linkage_group, gdms_qtl_details.qtl_id");
						rsQ=stQ.executeQuery("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl_details.linkage_group, gdms_qtl_details.qtl_id");
						while(rsQ.next()){
							/*String Fmarkers=rsQ.getString(13)+"/"+rsQ.getString(14);
							//float pos=(rsQ.getFloat(3)+rsQ.getFloat(4))/2;
							//System.out.println(",,,,,,,,,,,,,,,,,,,,,,  :"+rsQ.getString(5));
							qtlData.add(rsQ.getString(16)+"!~!"+rsQ.getString(11)+"!~!"+rsQ.getFloat(3)+"!~!"+rsQ.getFloat(4)+"!~!"+rsQ.getFloat(5)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getString(7)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getFloat(9)+"!~!"+rsQ.getFloat(10)+"!~!"+rsQ.getString(6)+"!~!"+Fmarkers+"!~!"+rsQ.getString(8));*/
							String Fmarkers=rsQ.getString(12)+"/"+rsQ.getString(13);
							qtlData.add(rsQ.getString(22)+"!~!"+rsQ.getString(10)+"!~!"+rsQ.getFloat(14)+"!~!"+rsQ.getFloat(3)+"!~!"+rsQ.getFloat(4)+"!~!"+rsQ.getString(5)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getFloat(8)+"!~!"+rsQ.getFloat(9)+"!~!"+rsQ.getString(6)+"!~!"+Fmarkers+"!~!"+rsQ.getString(7));
						
						}
					}else
						qtlExists=false;
					session.setAttribute("qtlExistsSes", qtlExists);
				}
				
				//System.out.println("*************qtlData="+qtlData);
				
				
				//System.out.println("........list="+list);
				//System.out.println(gListExp);
				//System.out.println("List="+list);
				//System.out.println(" gListExp="+ gListExp);
				//System.out.println(" mListExp="+ mListExp);
				session.setAttribute("datasetType", datasetType);
				if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("SNP"))){
					ef.MatrixDataSNP(list, filePath, req, gListExp, mListExp, sortedMap);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("mapping"))){
					ef.mapMatrix(list, filePath, req, gListExp, mListExp, parentsListToWrite, sortedMap);			
				
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("SSR"))){
					ef.MatrixDataSNP(list, filePath, req, gListExp, mListExp, sortedMap);
					//ef.Matrix(list, filePath, req, gListExp, mListExp);
					//ef.Matrix(list, filePath, req);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("DArT"))){
					ef.Matrix(list, filePath, req, gListExp, mListExp, sortedMap);
				}
				if(format.contains("Flapjack")){
					if(session!=null){
						session.removeAttribute("FlapjackPath");	
						session.removeAttribute("Fsession");		
					}
					
					String fSession=req.getSession().getAttribute("msec").toString();
					String FlapjackPath=filePath+"/Flapjack";
					
					ef.MatrixDat(list, mapData, FlapjackPath, req, gListExp, mListExp, qtlData, exportOpType, qtlExists);
					//ef.MatrixDat(list, mapData, filePath, req);
					//ef.Matrix(list, filePath, request);
					session.setAttribute("FlapjackPath", FlapjackPath);
					session.setAttribute("Fsession",fSession);
					
					
				}
				long endTime = System.currentTimeMillis();
				 //System.out.println("endTime="+endTime);
			}else{
				String gid="";
				String mid="";
				String mlist1="";
				SortedMap mapN = new TreeMap();
				SortedMap mapgids = new TreeMap();
				int gCount=0;
				int mCount=0;
				ResultSet rsDet=null;
				int count=0;
				list.clear();
				String f1="";
				String type=req.getSession().getAttribute("type").toString();
				//System.out.println("type="+req.getSession().getAttribute("type"));
				if(type.equals("map")){
					String[] mapStr=null;
					//session.setAttribute("mapsSess", df.get("selMaps"));
					//System.out.println("***********************************  "+df.get("selMaps"));
					mapStr=df.get("selMaps").toString().split(",");
					if(session!=null){
						session.removeAttribute("msec");			
					}
					//float distance=0;
					//System.out.println(",,,,,,,,,,,,,,,,,,,,,,,  :"+mapStr.length);
					//Calendar now = Calendar.getInstance();
					filePath=req.getSession().getServletContext().getRealPath("//");
					if(!new File(filePath+"/jsp/analysisfiles").exists())
				   		new File(filePath+"/jsp/analysisfiles").mkdir();
					session.setAttribute("count", (mapStr.length));
					for(int c=0;c<mapStr.length;c++){
						if(session!=null){
							session.removeAttribute("msec");			
						}
						mSec=now.getTimeInMillis()+"";
						//System.out.println("msec="+mapStr[c]);			
						f1=f1+mapStr[c]+"!~!"+mSec+c+";;";
						ArrayList dist=new ArrayList();
						ArrayList CD=new ArrayList();
						req.getSession().setAttribute("exportFormat","CMTV");
						req.getSession().setAttribute("msec", mSec+c);
						//String filePath="";
						//ExportFormats ef=new ExportFormats();
						//MaxIdValue r=new MaxIdValue();
						String mapUnit="";
						rs1=stmt1.executeQuery("select map_unit from gdms_mapping_data WHERE map_name="+mapStr[c]);
						while (rs1.next()){
							mapUnit=rs1.getString(1);
						}
						//System.out.println("mapUnit=:"+mapUnit);
						System.out.println("SELECT marker_name, linkage_group, start_position,map_name FROM gdms_mapping_data WHERE map_name=("+mapStr[c]+") ORDER BY linkage_group, start_position asc, marker_id,map_name");
						rs=stmt.executeQuery("SELECT marker_name, linkage_group, start_position,map_name FROM gdms_mapping_data WHERE map_name =("+mapStr[c]+") ORDER BY linkage_group, start_position asc, marker_id, map_name");
						while(rs.next()){
							//mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getDouble(3)+"!~!"+rs.getString(4)+"~~!!~~";
							//CD.add(rs.getString(2)+"!~!"+rs.getDouble(3)+"!~!"+rs.getString(1)+"!~!"+rs.getString(4));
							if(mapUnit.equalsIgnoreCase("bp")){
								CD.add(rs.getString(2)+"!~!"+new BigDecimal(rs.getString(3))+"!~!"+rs.getString(1)+"!~!"+rs.getString(4));
							}else{
								CD.add(rs.getString(2)+"!~!"+decfor.format(rs.getDouble(3))+"!~!"+rs.getString(1)+"!~!"+rs.getString(4));
							}
							count=count+1;
							//CD.add(rs.getFloat(3));
						}
						/*if(count==0){
							ae.add("myerrors", new ActionError("trait.doesnot.exists"));
							saveErrors(req, ae);				
							//msg="chkPlateid";
							return (new ActionForward(am.getInput()));
							String ErrMsg = "Map Name not found";
							req.getSession().setAttribute("indErrMsg", ErrMsg);
							return am.findForward("ErrMsg");
						}*/
						//System.out.println("CDistance="+CD);
						String[] strArr=CD.get(0).toString().split("!~!");
						double dis=Double.parseDouble(strArr[1]);
						String chr=strArr[0];
						count=0;
						int dis1=0;
						for(int a=0;a<CD.size();a++){
							String[] str1=CD.get(a).toString().split("!~!");						
							if(str1[0].equals(chr)){							
								double distance=Double.parseDouble(str1[1])-dis;
								//System.out.println("....:"+distance);
								distance=r.roundThree(distance);
								if(mapUnit.equalsIgnoreCase("bp")){
									dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+new BigDecimal(distance)+"!~!"+str1[1]);
								}else{
									if(distance==0.0){
										dis1=0;
										//distance=dis1;
										//System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<:"+distance+ "   "+dis1);	
										dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+dis1+"!~!"+dis1);
									}else{
										dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+distance+"!~!"+str1[1]);
									}
								}
								count=count+1;
								//dis=distance;
								dis=Double.parseDouble(str1[1]);
							}else{	
								count=0;
								//float distance=Float.parseFloat(str1[1])-dis;
								dis=Double.parseDouble(str1[1]);
								chr=str1[0];
								if(mapUnit.equalsIgnoreCase("bp")){
									dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+new BigDecimal(dis)+"!~!"+str1[1]);
								}else{
									if(dis==0.0){
										dis1=0;
										//dis=dis1;
										//System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<:"+dis);	
										dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+dis1+"!~!"+dis1);
									}else{
										dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+dis+"!~!"+str1[1]);
									}
									
								}
								count=count+1;	
							}
						}
						//System.out.println("..............."+dist);
						ef.CMTVTxt(dist, filePath, req);
						
					}
					//System.out.println("f1="+f1);
					session.setAttribute("f1", f1);
				}else{
					//System.out.println("type="+type);
					ArrayList gList1=new ArrayList();
					if(type.equals("GermplasmName")){
						gidslist=df.get("markersSel").toString();
						markerslist=req.getSession().getAttribute("mnames").toString();
						mlist1=req.getSession().getAttribute("mnames1").toString();
						String[] gList=gidslist.split(";;");
						for(int m=0;m<gList.length;m++){
							gids=gids+gList[m]+",";
							gList1.add(gList[m]);
						}
						//System.out.println("Marekrs="+markerslist+"    Gids="+gids);
						gCount=gList.length;
						mCount=Integer.parseInt(req.getSession().getAttribute("mCount").toString());
						req.getSession().setAttribute("genCount", gList.length);
						gid=gids.substring(0,gids.length()-1);
						mid=markerslist.substring(0,markerslist.length()-1);
					}else if(type.equals("markers")){				
						markers=df.get("markersSel").toString();
						//System.out.println("*********************************************"+markers);
						gids=req.getSession().getAttribute("gidsN").toString();
						gid=gids.substring(0,gids.length()-1);
						String[] g1=gids.split(",");
						for(int g=0;g<g1.length;g++){
							gList1.add(g1[g]);
						}
						//System.out.println("gid="+gids);
						String[] mList=markers.split(";;");
						for(int m=0;m<mList.length;m++){
							//System.out.println(m+"="+mList[m]);
							mlist1=mlist1+"'"+mList[m]+"',";
							markerslist=markerslist+mList[m]+",";
						}
						//System.out.println("mcount="+mList.length+"&&&&&&&&&&&&&&&&&&&&&&  "+markerslist);
						mCount=mList.length;
						gCount=Integer.parseInt(req.getSession().getAttribute("genCount").toString());
						req.getSession().setAttribute("mCount", mList.length);
						
						mid=markerslist.substring(0,markerslist.length()-1);
					}
					int alleleCount=0;
					int charCount=0;
					int mapCharCount=0;
					
					ArrayList nidList=new ArrayList();
					//System.out.println(".... gid="+gid);
					//System.out.println("gList1="+gList1);
					try{
						String data="";
						String nid="";
						rs=stmt.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gid+") order by gid");
						while(rs.next()){
							if(!nidList.contains(rs.getInt(1)))
								nidList.add(rs.getInt(1));
							nid=nid+rs.getString(1)+",";
						}
						
						//System.out.println("select count(*) from gdms_allele_values where gid in ("+gid+")");
						ResultSet rsa=stmt1.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
						while (rsa.next()){
							alleleCount=rsa.getInt(1);
						}
						
						ResultSet rsc=stmt2.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
						while(rsc.next()){
							charCount=rsc.getInt(1);
						}
					
						ResultSet rsMap=stmtM.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
						while(rsMap.next()){
							mapCharCount=rsMap.getInt(1);
						}
						//System.out.println(alleleCount+"    "+charCount+"    "+mapCharCount);
						if(charCount>0){							
							rsDet=st.executeQuery("SELECT DISTINCT gdms_char_values.gid,gdms_char_values.char_value as data,gdms_marker.marker_name"+
									" FROM gdms_char_values,gdms_marker WHERE gdms_char_values.marker_id=gdms_marker.marker_id"+
									" AND gdms_char_values.gid IN ("+gid+") AND gdms_char_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_char_values.gid, gdms_marker.marker_name");
							
							datasetType="SNP";
						}
						if(alleleCount>0){							
							rsDet=st.executeQuery("SELECT distinct gdms_allele_values.gid,gdms_allele_values.allele_bin_value,gdms_marker.marker_name"+
									" FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.marker_id=gdms_marker.marker_id"+
									" AND gdms_allele_values.gid IN ("+gid+") AND gdms_allele_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_allele_values.gid, gdms_marker.marker_name");
							
							datasetType="mapping";
						}
						if(mapCharCount>0){		
							rsDet=st.executeQuery("SELECT DISTINCT gdms_mapping_pop_values.gid,gdms_mapping_pop_values.map_char_value as data,gdms_marker.marker_name"+
									" FROM gdms_mapping_pop_values,gdms_marker WHERE gdms_mapping_pop_values.marker_id=gdms_marker.marker_id "+
									" AND gdms_mapping_pop_values.gid IN ("+gid+") AND gdms_mapping_pop_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_mapping_pop_values.gid, gdms_marker.marker_name");
							rsMT=stmtMT.executeQuery("SELECT DISTINCT gdms_mapping_pop_values.dataset_id,gdms_mapping_pop.mapping_type,gdms_mapping_pop.parent_a_gid,gdms_mapping_pop.parent_b_gid,gdms_marker.marker_type FROM gdms_mapping_pop_values,gdms_mapping_pop,gdms_marker WHERE gdms_mapping_pop_values.dataset_id=gdms_mapping_pop.dataset_id AND gdms_mapping_pop_values.marker_id=gdms_marker.marker_id  AND gdms_mapping_pop_values.gid IN ("+gid+") AND gdms_mapping_pop_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_mapping_pop_values.gid DESC, gdms_marker.marker_name");
							while(rsMT.next()){
								mapping_type=rsMT.getString(2);
								parents=rsMT.getInt(3)+","+rsMT.getInt(4);
								mType=rsMT.getString(5);
							}
							String parentsExists="";
							String nids="";
							int parentAint=0;
							int parentBint=0;
							String parentAData="";
							String parentBData="";
							String data1="";
							String[] p1=parents.split(",");
							//System.out.println("#############################################  :"+mapping_type);
							//for(int p=0;p<p1.length;p++){
								parentAint=Integer.parseInt(p1[0].toString());
								parentBint=Integer.parseInt(p1[1].toString());
								
								if((gList1.contains(p1[0]))&&(gList1.contains(p1[1])))
									parentsExists="yes";
								else
									parentsExists="no";
							//}
							if(parentsExists.equalsIgnoreCase("yes")){
								ArrayList nidsList=new ArrayList();
								if(!(gListExp.contains(parentAint)))
									gListExp.add(parentAint);
								if(!(gListExp.contains(parentBint)))
									gListExp.add(parentBint);
								rsP=stmtP.executeQuery("select nid from gdms_acc_metadataset where gid in("+parents+")");
								while(rsP.next()){
									nids=nids+rsP.getInt(1)+",";
									nidsList.add(rsP.getInt(1));
								}
								//System.out.println("nids="+nids);
								/*rs3=stP.executeQuery("select gid, nval from names where nid in ("+nids.substring(0, nids.length()-1)+")");
								while(rs3.next()){
									if(!gidsList.contains(rs3.getInt(1)))
										gidsList.add(rs3.getInt(1));
									if(!(gListExp.contains(rs3.getString(2))))
										gListExp.add(rs3.getString(2));
									if(!(gListExp.contains(rs3.getString(1))))
										gListExp.add(rs3.getString(1));
								}*/
								
								
								/** 
								 * implementing middleware jar file 
								 */
								
								Name names = null;
							
								for(int n=0;n<nidsList.size();n++){
									names=manager.getGermplasmNameByID(Integer.parseInt(nidsList.get(n).toString()));
									gids=gids+names.getGermplasmId()+",";
									if(!gidsList.contains(names.getGermplasmId()))
										gidsList.add(names.getGermplasmId());
								}
								
								
							}
							if(mapping_type.equalsIgnoreCase("allelic")){
								if(mType.equalsIgnoreCase("snp")){
									rsPD=stmtPD.executeQuery("SELECT DISTINCT gdms_char_values.gid,gdms_char_values.char_value AS DATA,gdms_marker.marker_name FROM gdms_char_values,gdms_marker WHERE gdms_char_values.marker_id=gdms_marker.marker_id  AND gdms_char_values.gid IN ("+parents+") AND gdms_char_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_char_values.gid, gdms_marker.marker_name");
									
								}else if((mType.equalsIgnoreCase("ssr"))||(mType.equalsIgnoreCase("DArT"))){
									rsPD=stmtPD.executeQuery("SELECT DISTINCT gdms_allele_values.gid,gdms_allele_values.allele_bin_value AS DATA,gdms_marker.marker_name FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.marker_id=gdms_marker.marker_id  AND gdms_allele_values.gid IN ("+parents+") AND gdms_allele_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_allele_values.gid, gdms_marker.marker_name");
									
								}
								while(rsPD.next()){
									data1=data1+rsPD.getInt(1)+"~!~"+rsPD.getString(2)+"~!~"+rsPD.getString(3)+"!~!";
								}
								String[] parentsData=data1.split("!~!");
								for(int c=0;c<parentsData.length;c++){
									 String arrP[]=new String[3];
									 StringTokenizer stzP = new StringTokenizer(parentsData[c].toString(), "~!~");
									 int iP=0;
									 while(stzP.hasMoreTokens()){
										 arrP[iP] = stzP.nextToken();
										 iP++;
									 }	
									
									 if(Integer.parseInt(arrP[0])==parentAint){								
										 parentAData=parentAData+parentAint+"~!~"+arrP[1]+"~!~"+arrP[2]+"!~!";
									 }else if(Integer.parseInt(arrP[0])==parentBint){									
										 parentBData=parentBData+parentBint+"~!~"+arrP[1]+"~!~"+arrP[2]+"!~!";
									 }	
									 
								}
								
								data=parentAData+parentBData;
								
							}
							session.setAttribute("mappingType", mapping_type);
							datasetType="mapping";
						}
						//System.out.println("data:"+data);
						while(rsDet.next()){
							
							if(!(mListExp.contains(rsDet.getString(3))))
								mListExp.add(rsDet.getString(3));
							
							data=data+rsDet.getInt(1)+"~!~"+rsDet.getString(2)+"~!~"+rsDet.getString(3)+"!~!";
							//list.add(rsDet.getString(2)+","+rsDet.getString(5)+","+rsDet.getString(4));
						}
						
						
						/** 
						 * implementing middleware jar file 
						 */
						
						Name names = null;
					
						for(int n=0;n<nidList.size();n++){
							names=manager.getGermplasmNameByID(Integer.parseInt(nidList.get(n).toString()));
							gids=gids+names.getGermplasmId()+",";
							if(!gidsList.contains(names.getGermplasmId()))
								gidsList.add(names.getGermplasmId());
							
							
							/*if(!(gListExp.contains(rs1.getString(2))))
								gListExp.add(rs1.getString(2));*/
							
							if((format.contains("Flapjack"))&&(exportOpType.equals("gname"))){
								if(!(gListExp.contains(names.getNval())))
									 gListExp.add(names.getNval());
							}else{
								if(!(gListExp.contains(names.getGermplasmId())))
									gListExp.add(names.getGermplasmId());
							}						
							
							mapN.put(names.getGermplasmId(), names.getNval());						
							
						}
						
						
						/*rs1=stmtG.executeQuery("select distinct gid,nval from names where nid in("+nid.substring(0,nid.length()-1)+") order by nid desc");
						while(rs1.next()){
							//mapgids.put(rs1.getInt(1), arg1)
							if(!gidsList.contains(rs1.getInt(1)))
								gidsList.add(rs1.getInt(1));
							if(!(gListExp.contains(rs1.getString(2))))
								gListExp.add(rs1.getString(2));
							if(!(gListExp.contains(rs1.getInt(1))))
								gListExp.add(rs1.getInt(1));
							mapN.put(rs1.getInt(1), rs1.getString(2));
							
						}*/
						/*System.out.println(mapN);
						System.out.println("..........."+gidsList);
						System.out.println("*************:"+gListExp);*/
						//System.out.println(",,,,"+data);
						//System.out.println(",,,,"+gidsList);
						String[] dataArr=data.split("!~!");
						for(int d=0;d<dataArr.length;d++){
							String[] arrData=dataArr[d].split("~!~");
							if(gidsList.contains(Integer.parseInt(arrData[0]))){
								//list.add(mapN.get(Integer.parseInt(arrData[0]))+","+arrData[2]+","+arrData[1]);
								
								if((format.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
									list.add(mapN.get(Integer.parseInt(arrData[0]))+","+arrData[2]+","+arrData[1]);	
								}else{
									list.add(arrData[0]+","+arrData[2]+","+arrData[1]);	
								}
								
								
							}										
						}
						
						
					session.setAttribute("datasetType", datasetType);
					
					
					}catch(Exception e){
						e.printStackTrace();
					}
				
					//System.out.println(".....................List="+gListExp);
					if(format.contains("Flapjack")){						
						//System.out.println("select marker_name, linkage_group, start_position from mapping_data where marker_name in ("+ mlist1.substring(0,mlist1.length()-1) +") and linkagemap_name in ('"+map+"') order by marker_name");
						//rs=stmt.executeQuery("select marker_name, linkage_group, start_position from mapping_data where marker_name in ("+ mlist1.substring(0,mlist1.length()-1) +") and map_name in ('"+map+"') order by marker_name");
						System.out.println("select marker_name, linkage_group, start_position from gdms_mapping_data where map_name in ('"+map+"') order by marker_name");
						//rs=stmt.executeQuery("select marker_name, linkage_group, start_position from mapping_data where map_name in ('"+map+"') order by marker_name");
						rs=stmt.executeQuery("SELECT marker_name, linkage_group, start_position FROM gdms_mapping_data WHERE map_name ='"+map+"' ORDER BY linkage_group, start_position , marker_name");
						while(rs.next()){
							//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
							mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"~~!!~~";
							
						}
						
						ResultSet rsMap=st.executeQuery("select qtl_id from gdms_qtl_details where map_id =(select map_id from gdms_map where map_name ='"+map+"')");
						//rsQ=stQ.executeQuery("");
						while(rsMap.next()){
							//System.out.println("..............:"+rsMap.getInt(1));
							qtlCount++;
							qtl_id=qtl_id+rsMap.getInt(1)+",";
						}
						if(qtlCount>0){
							qtlExists=true;
							//System.out.println("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id");
							rsQ=stQ.executeQuery("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id");
							while(rsQ.next()){
								/*String Fmarkers=rsQ.getString(13)+"/"+rsQ.getString(14);
								//System.out.println(",,,,,,,,,,,,,,,,,,,,,,  :"+rsQ.getString(5));
								//qtlData.add(rsQ.getString(15)+"!~!"+rsQ.getString(10)+"!~!"+rsQ.getString(15)+"!~!"+rsQ.getFloat(3)+"!~!"+rsQ.getFloat(4)+"!~!"+rsQ.getString(5)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getString(5)+"!~!"+rsQ.getFloat(8)+"!~!"+rsQ.getFloat(9)+"!~!"+rsQ.getString(5)+"!~!"+rsQ.getString(12)+"/"+rsQ.getString(13)+"!~!"+rsQ.getString(7));
								qtlData.add(rsQ.getString(16)+"!~!"+rsQ.getString(11)+"!~!"+rsQ.getFloat(3)+"!~!"+rsQ.getFloat(4)+"!~!"+rsQ.getFloat(5)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getString(7)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getFloat(9)+"!~!"+rsQ.getFloat(10)+"!~!"+rsQ.getString(6)+"!~!"+Fmarkers+"!~!"+rsQ.getString(8));*/
								String Fmarkers=rsQ.getString(12)+"/"+rsQ.getString(13);
								qtlData.add(rsQ.getString(22)+"!~!"+rsQ.getString(10)+"!~!"+rsQ.getFloat(14)+"!~!"+rsQ.getFloat(3)+"!~!"+rsQ.getFloat(4)+"!~!"+rsQ.getString(5)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getFloat(8)+"!~!"+rsQ.getFloat(9)+"!~!"+rsQ.getString(6)+"!~!"+Fmarkers+"!~!"+rsQ.getString(7));
							
							}
						}else
							qtlExists=false;
						session.setAttribute("qtlExistsSes", qtlExists);
						
					}			
				}
				/*System.out.println("List="+list);
				System.out.println(" gListExp="+ gListExp);
				System.out.println(" mListExp="+ mListExp);*/
				//To write matrix  if datatype is character 
				if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("SNP"))){
					ef.MatrixDataSNP(list, filePath, req, gListExp, mListExp, mapN);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("mapping"))){
					ef.Matrix(list, filePath, req, gListExp, mListExp, mapN);		
				
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("SSR"))){
					ef.MatrixDataSNP(list, filePath, req, gListExp, mListExp, mapN);
					//ef.Matrix(list, filePath, req, gListExp, mListExp);
					//ef.Matrix(list, filePath, req);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("DArT"))){
					ef.Matrix(list, filePath, req, gListExp, mListExp, mapN);
				}
				/*if(format.contains("Genotyping X Marker Matrix")){
					ef.Matrix(list, filePath, req, gListExp, mListExp);
					//ef.Matrix(list, filePath, req);
				}*/
				
				if(format.contains("Flapjack")){
					//String FlapjackPath=filePath+"/Flapjack/OutputFiles/"+req.getSession().getAttribute("msec")+req.getSession().getAttribute("user");
					String FlapjackPath=filePath+"/Flapjack";
					//System.out.println((!new File(filePath+"/Flapjack/OutputFiles/"+req.getSession().getAttribute("msec")+req.getSession().getAttribute("user")).exists()));
					/*if(!new File(filePath+"/Flapjack/OutputFiles").exists())
						new File(filePath+"/Flapjack/OutputFiles").mkdir();
					if(!new File(filePath+"/Flapjack/OutputFiles/"+req.getSession().getAttribute("msec")+req.getSession().getAttribute("user")).exists())
				   		new File(filePath+"/Flapjack/OutputFiles/"+req.getSession().getAttribute("msec")+req.getSession().getAttribute("user")).mkdir();*/
					ef.MatrixDat(list, mapData, FlapjackPath, req, gListExp, mListExp, qtlData, exportOpType, qtlExists);
					
					session.setAttribute("FlapjackPath", FlapjackPath);
					//ef.MatrixDat(list, mapData, filePath, req, gListExp, mListExp);
				
				}	
			}
			
	}catch(Exception e){
		e.printStackTrace();
	}finally{
	      try{		      		
	      		if(con!=null) con.close();
	      		factory.close(); 
	         }catch(Exception e){System.out.println(e);}
		}
		return am.findForward("exp");
	}
	

}
