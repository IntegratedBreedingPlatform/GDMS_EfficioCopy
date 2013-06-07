package org.icrisat.gdms.retrieve;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
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
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.manager.api.OntologyDataManager;
import org.icrisat.gdms.common.ExportFormats;

public class RetrieveExportABHAction extends Action{
	java.sql.Connection conn;
	java.sql.Connection con;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
	//	System.out.println(">>>>>>>>>>>>   RetrieveExportABHAction.java   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		HttpSession session = req.getSession(true);
		Connection con=null;
		ManagerFactory factory =null;
		Properties prop=new Properties();
		try{
			if(session!=null){
				session.removeAttribute("qtlExistsSes");
			}
			int qtlCount=0;
			boolean qtlExists=false;
			String qtl_id="";
			ArrayList qtlData=new ArrayList();
			
			
			ResultSet rs=null;			
			ResultSet rs1=null;
			ResultSet rs2=null;
			ResultSet rsG=null;	
			ResultSet rsM=null;			
			ResultSet rsD=null;
			ResultSet rsN=null;
			ResultSet rsP=null;
			ResultSet rsQ=null;
			DynaActionForm df = (DynaActionForm) af;
			
			ExportFormats ef=new ExportFormats();
			String mtype="";
			String parents="";
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
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			*/
			//factory = new ManagerFactory(local, central);
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			OntologyDataManager om=factory.getNewOntologyDataManager();
			
			Statement st=null;
			Statement stmt=null;
			Statement stmt1=null;
			Statement stmt2=null;
			Statement stmtM=null;		
			Statement stmtG=null;
			Statement stmtD=null;
			Statement stmtN=null;
			Statement stP=null;
			Statement stQ=null;
			ResultSet rs3=null;			
			ResultSet rs4=null;
			ResultSet rsMap=null;
			ResultSet rsQL=null;
			//Statement st3=con.createStatement();
			ResultSet rsNL=null;
			ResultSet rsC=null;
			ResultSet rsMapL=null;
			ResultSet rsL=null;
			String[] str1=null;
			String[] strPop=null;
			ArrayList list=new ArrayList();
			ArrayList gNamesList=new ArrayList();
			ArrayList gParentsList=new ArrayList();
			int parentA=0;
			int parentB=0;
			ArrayList parentsData=new ArrayList();
			ArrayList populationData=new ArrayList();
			String gids="";			
			ArrayList parentAList=new ArrayList();
			ArrayList parentBList=new ArrayList();
			ArrayList mListExp=new ArrayList();
			SortedMap mapA = new TreeMap();
			SortedMap mapB = new TreeMap();
			SortedMap mapGNames = new TreeMap();
			String markerIds="";			
			ArrayList<Integer> tid=new ArrayList();
			String mapData="";
			
			String exportOpType="";
			
			String datasetName="";
			Calendar now = Calendar.getInstance();
			String mSec=now.getTimeInMillis()+"";
			//String fname=filePath+"/jsp/analysisfiles/matrix"+mSec+".xls";
			
			if(session!=null){
				session.removeAttribute("msec");			
			}
			req.getSession().setAttribute("msec", mSec);
			
			 SortedMap mMap = new TreeMap();
			 String filePath="";
				filePath=req.getSession().getServletContext().getRealPath("//");
				if(!new File(filePath+"/jsp/analysisfiles").exists())
			   		new File(filePath+"/jsp/analysisfiles").mkdir();
				
				
			//String op=session.getAttribute("op").toString();
			//String op=req.getParameter("opType");
			//System.out.println("@@@@@@@@@@@@@@@@@   op="+op);
			String op=df.get("opType").toString();
			String map="";
			//System.out.println("*************  "+df.get("expType").toString());
			String expType=df.get("expType").toString();
			
			req.getSession().setAttribute("exportFormat", expType);
			req.getSession().setAttribute("op",op);
			//System.out.println("@@@@@@@@@@@@@@@@@   op="+op);
			if(expType.equalsIgnoreCase("flapjack"))
				map=df.get("maps").toString();
			
			if(op.equalsIgnoreCase("dataset")){
				
				if(expType.equalsIgnoreCase("flapjack")){
					exportOpType=df.get("exportTypeH").toString();
					//System.out.println(",,,,,,,,,,,,,,,:"+df.get("exportTypeH"));
				}
				
				int datasetId=0;
				String dType="mapping";
				String mType="";
				//datasetName=df.get("dataset").toString();
				datasetId=Integer.parseInt(df.get("dataset").toString());
				if(datasetId>0){
					st=conn.createStatement();
					stmt=conn.createStatement();
					stmt1=conn.createStatement();
					stmt2=conn.createStatement();
					stmtM=conn.createStatement();		
					stmtG=conn.createStatement();
					stmtD=conn.createStatement();
					//stmtN=conn.createStatement();
					stP=conn.createStatement();
					stQ=conn.createStatement();
					//st3=conn.createStatement();
				}else{
					st=con.createStatement();
					stmt=con.createStatement();
					stmt1=con.createStatement();
					stmt2=con.createStatement();
					stmtM=con.createStatement();		
					stmtG=con.createStatement();
					stmtD=con.createStatement();
					//stmtN=con.createStatement();
					stP=con.createStatement();
					stQ=con.createStatement();
					//st3=con.createStatement();
				}
				ArrayList markersList=new ArrayList();
				//String datasetName=req.getParameter("dataset");
				/*rs3=st3.executeQuery("select dataset_type from gdms_dataset where dataset_name='"+datasetName+"'");
				while(rs3.next()){
					dataset=rs3.getInt(1);
					dType=rs3.getString(2);
				}*/
				//System.out.println(dataset);
				//if(dType.equalsIgnoreCase("mapping")){
					rs4=stmt2.executeQuery("select mapping_type from gdms_mapping_pop where dataset_id="+datasetId);
					while(rs4.next()){
						mType=rs4.getString(1);
					}
				//}
				rsM=stmtM.executeQuery("select marker_id from gdms_marker_metadataset where dataset_id="+datasetId+" order by marker_id");
				while(rsM.next()){
					markerIds=markerIds+rsM.getInt(1)+",";
					//mMap.put(rsM.getInt(1), rsM.getString(2));
					if(!markersList.contains(rsM.getInt(1)))
						markersList.add(rsM.getInt(1));
				}
				rsN=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in("+markerIds.substring(0,markerIds.length()-1)+")");
				while(rsN.next()){
					if(!mListExp.contains(rsN.getString(2)))
						mListExp.add(rsN.getString(2));
					mMap.put(rsN.getInt(1), rsN.getString(2));
				}
				rsNL=stLoc.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in("+markerIds.substring(0,markerIds.length()-1)+")");
				while(rsNL.next()){
					if(!mListExp.contains(rsNL.getString(2)))
						mListExp.add(rsNL.getString(2));
					mMap.put(rsNL.getInt(1), rsNL.getString(2));
				}
				Map<Object, String> sortedMarkerMap = new TreeMap<Object, String>(mMap);
				rs=st.executeQuery("select distinct marker_type from gdms_marker where marker_id in("+markerIds.substring(0,markerIds.length()-1)+")");
				while(rs.next()){
					mtype=rs.getString(1);
				}
				//System.out.println("type="+mtype);
				
				rs1=stmt.executeQuery("select parent_a_nid, parent_b_nid from gdms_mapping_pop where dataset_id="+datasetId);
				while(rs1.next()){
					parents=rs1.getInt(1)+","+rs1.getInt(2);
					parentA=rs1.getInt(1);
					parentB=rs1.getInt(2);				
				}
				
				SortedMap parentsMap = new TreeMap();
				ArrayList nids=new ArrayList();
				if(mType.equalsIgnoreCase("allelic")){
					
					rsP=stP.executeQuery("select nid from gdms_acc_metadataset where gid in("+parents+")");
					while(rsP.next()){
						nids.add(rsP.getInt(1));
					}
					Name names = null;
					for(int n=0;n<nids.size();n++){
						names=manager.getGermplasmNameByID(Integer.parseInt(nids.get(n).toString()));
						if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
							if(!gNamesList.contains(names.getNval()))
								gNamesList.add(names.getNval());
						}else{
							if(!gNamesList.contains(names.getGermplasmId()))
								gNamesList.add(names.getGermplasmId());
							
						}				
						
						
						if(!(gParentsList.contains(names.getGermplasmId())))
							gParentsList.add(names.getGermplasmId());
						parentsMap.put(names.getGermplasmId(), names.getNval());
						mapGNames.put(names.getGermplasmId(), names.getNval());
						
					}
					for(int m=0;m<mListExp.size();m++){
						if(gParentsList.contains(parentA)){
							//list.add(parentsMap.get(parentA)+","+mListExp.get(m)+","+"A");
							if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
								list.add(parentsMap.get(parentA)+","+mListExp.get(m)+","+"A");
							}else{
								list.add(parentA+","+mListExp.get(m)+","+"A");
							}
						}
						
					}
					for(int m=0;m<mListExp.size();m++){
						if(gParentsList.contains(parentB)){
							//list.add(parentsMap.get(parentB)+","+mListExp.get(m)+","+"B");
							if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
								list.add(parentsMap.get(parentB)+","+mListExp.get(m)+","+"B");
							}else{
								list.add(parentB+","+mListExp.get(m)+","+"B");
							}
							
						}
					}
					session.setAttribute("datasetType",dType);
					//System.out.println("*******************  "+list);
					if(mtype.equalsIgnoreCase("SNP")){
						rs2=stmt1.executeQuery("select gid, marker_id, char_value from gdms_char_values where gid in("+parents+") and marker_id in("+markerIds.substring(0,markerIds.length()-1)+") order by gid, marker_id");
					}else if(mtype.equalsIgnoreCase("SSR") || mtype.equalsIgnoreCase("DArT")){
						rs2=stmt1.executeQuery("select gid, marker_id, allele_bin_value from gdms_allele_values where gid in("+parents+") and marker_id in("+markerIds.substring(0,markerIds.length()-1)+") order by gid, marker_id");
					}
					while(rs2.next()){
						parentsData.add(rs2.getInt(1)+"!~!"+rs2.getInt(2)+"!~!"+rs2.getString(3));
					}
					//System.out.println("parentsData="+parentsData);
					 for(int c=0;c<parentsData.size();c++){
						 String arrP[]=new String[3];
						 StringTokenizer stzP = new StringTokenizer(parentsData.get(c).toString(), "!~!");
						 int iP=0;
						 while(stzP.hasMoreTokens()){
							 arrP[iP] = stzP.nextToken();
							 iP++;
						 }			 
						 if(Integer.parseInt(arrP[0])==(parentA)){
							 mapA.put(Integer.parseInt(arrP[1]), arrP[2]);
							 //parentAList.add(arrP[1]+"!~!"+arrP[2]);
						 }else{
							 mapB.put(Integer.parseInt(arrP[1]), arrP[2]);					
						 }			
					}
				}
				rsD=stmtD.executeQuery("select gid, marker_id, map_char_value from gdms_mapping_pop_values where dataset_id="+datasetId+" order by gid, marker_id");
				while(rsD.next()){
					gids=gids+rsD.getInt(1)+",";
					populationData.add(rsD.getInt(1)+"!~!"+rsD.getInt(2)+"!~!"+rsD.getString(3));
				}
				
				
				nids.clear();
				rsG=stmtG.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gids.substring(0,gids.length()-1)+") order by gid");
				while(rsG.next()){
					nids.add(rsG.getInt(1));
				}
				Name names = null;
				for(int n=0;n<nids.size();n++){
					names=manager.getGermplasmNameByID(Integer.parseInt(nids.get(n).toString()));
					if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
						if(!(gNamesList.contains(names.getNval())))
							gNamesList.add(names.getNval());
					}else{
						if(!(gNamesList.contains(names.getGermplasmId())))
							gNamesList.add(names.getGermplasmId());
					}
					
					mapGNames.put(names.getGermplasmId(), names.getNval());
					
				}
				
				String data="";
				Map<Object, String> sortedGMap = new TreeMap<Object, String>(mapGNames);
				
				//System.out.println("............."+populationData);
				if(mType.equalsIgnoreCase("allelic")){
					for(int p=0; p<populationData.size();p++){
						String arr[]=new String[3];
						StringTokenizer stz = new StringTokenizer(populationData.get(p).toString(), "!~!");
						int i=0;
						while(stz.hasMoreTokens()){
							arr[i] = stz.nextToken();
						   i++;
						}
						//System.out.println("<<<<<<   :"+mapA.get(Integer.parseInt(arr[1]))+"equals("+arr[2]);
						if((mapA.get(Integer.parseInt(arr[1])).equals(arr[2]))){
							//list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"A");	
							if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
								list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"A");
							}else{
								list.add(arr[0]+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"A");
							}
								
						}else if((mapB.get(Integer.parseInt(arr[1])).equals(arr[2]))){
							//list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"B");
							if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
								list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"B");
							}else{
								list.add(arr[0]+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"B");
							}
							
						}else if((arr[2].equals("-"))||(arr[2].equals("?"))){
							//list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
							
							if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
								list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
							}else{
								list.add(arr[0]+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
							}					
							
						}else{
							//list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"A/B");							
							if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
								list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"A/B");
							}else{
								list.add(arr[0]+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+"A/B");
							}
							
						}		
						
					}
				}else{
					for(int p=0; p<populationData.size();p++){
						String arr[]=new String[3];
						StringTokenizer stz = new StringTokenizer(populationData.get(p).toString(), "!~!");
						int i=0;
						while(stz.hasMoreTokens()){
							arr[i] = stz.nextToken();
						   i++;
						}
						
						if((expType.equalsIgnoreCase("flapjack"))&&(exportOpType.equalsIgnoreCase("gname"))){
							list.add(sortedGMap.get(Integer.parseInt(arr[0]))+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
						}else{
							list.add(arr[0]+","+sortedMarkerMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
						}
							
						//}		
						
					}
				}
				ArrayList tidsList=new ArrayList();
				SortedMap traitsMap = new TreeMap();
				ArrayList markersInMap=new ArrayList();
				//System.out.println("list="+list);
				//System.out.println("gNamesList="+gNamesList);
				//System.out.println("mListExp="+mListExp);
				if(expType.equalsIgnoreCase("flapjack")){
					 String mapName  = map.substring(0,map.lastIndexOf("("));
					    //System.out.println("select marker_name, linkage_group, start_position from mapping_data where map_name in ('"+mapName+"') order by linkage_group, start_position,marker_name");
						rs=stCen.executeQuery("select marker_name, linkage_group, start_position, marker_id from gdms_mapping_data where map_name in ('"+mapName+"') order by linkage_group, start_position,marker_name");
						while(rs.next()){
							//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
							mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"~~!!~~";
							if(!markersInMap.contains(rs.getInt(4)))
								markersInMap.add(rs.getInt(4));
						}
						rsL=stLoc.executeQuery("select marker_name, linkage_group, start_position, marker_id from gdms_mapping_data where map_name in ('"+mapName+"') order by linkage_group, start_position,marker_name");
						while(rs.next()){
							//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
							mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"~~!!~~";
							if(!markersInMap.contains(rs.getInt(4)))
								markersInMap.add(rs.getInt(4));
						}
						for(int m=0;m<markersList.size();m++){
							if(!(markersInMap.contains(markersList.get(m)))){
								mapData=mapData+mMap.get(markersList.get(m))+"!~!"+"unmapped"+"!~!"+"0"+"~~!!~~";
							}
						}
						//System.out.println("mapData=:"+mapData);
						
						rsMap=stCen.executeQuery("select qtl_id, tid from gdms_qtl_details where map_id =(select map_id from gdms_map where map_name ='"+mapName+"')");
						//rsQ=stQ.executeQuery("");
						while(rsMap.next()){
							qtlCount++;
							qtl_id=qtl_id+rsMap.getInt(1)+",";
							if(!tid.contains(rsMap.getInt(2)))
								tid.add(rsMap.getInt(2));
						}
						rsMapL=stLoc.executeQuery("select qtl_id,tid from gdms_qtl_details where map_id =(select map_id from gdms_map where map_name ='"+mapName+"')");
						while(rsMapL.next()){
							qtlCount++;
							qtl_id=qtl_id+rsMapL.getInt(1)+",";
							if(!tid.contains(rsMapL.getInt(2)))
								tid.add(rsMapL.getInt(2));
						}
						
						if(qtlCount>0){
							qtlExists=true;
							/*rsC=stCen.executeQuery("select distinct trabbr, tid from tmstraits");
							while(rsC.next()){							
								tidsList.add(rsC.getInt(2));
									traitsMap.put(rsC.getInt(2), rsC.getString(1));
								
							}
							rsN=stmtN.executeQuery("select distinct trabbr, tid from tmstraits");
							while(rsN.next()){							
								if(!tidsList.contains(rsN.getInt(2)))
									traitsMap.put(rsN.getInt(2), rsN.getString(1));
							}*/
							for(int t=0; t<tid.size();t++){
								Term term =om.getTermById(tid.get(t));
								//System.out.println(".................def:"+term.getDefinition()+"   id:"+term.getId()+"  name=:"+term.getName()+"  vocID:"+term.getVocabularyId()+"  nsyn:"+term.getNameSynonyms());
								tidsList.add(term.getId());
								traitsMap.put(term.getId(), term.getName());
							}
							
							
							
							rsQ=stCen.executeQuery("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl_details.linkage_group, gdms_qtl_details.qtl_id");
							while(rsQ.next()){
								String Fmarkers=rsQ.getString(12)+"/"+rsQ.getString(13);
								qtlData.add(rsQ.getString(22)+"!~!"+rsQ.getString(10)+"!~!"+rsQ.getFloat(14)+"!~!"+rsQ.getFloat(3)+"!~!"+rsQ.getFloat(4)+"!~!"+traitsMap.get(rsQ.getInt(5)).toString()+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getFloat(8)+"!~!"+rsQ.getFloat(9)+"!~!"+rsQ.getString(6)+"!~!"+Fmarkers+"!~!"+rsQ.getString(7)+"!~!"+rsQ.getString(16)+"!~!"+rsQ.getString(17)+"!~!"+rsQ.getString(18)+"!~!"+rsQ.getString(19)+"!~!"+rsQ.getString(20));
							}
							
							rsQL=stLoc.executeQuery("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl_details.linkage_group, gdms_qtl_details.qtl_id");
							while(rsQL.next()){
								String Fmarkers=rsQL.getString(12)+"/"+rsQL.getString(13);
								qtlData.add(rsQL.getString(22)+"!~!"+rsQL.getString(10)+"!~!"+rsQL.getFloat(14)+"!~!"+rsQL.getFloat(3)+"!~!"+rsQL.getFloat(4)+"!~!"+traitsMap.get(rsQL.getInt(5)).toString()+"!~!"+rsQL.getString(6)+"!~!"+rsQL.getString(6)+"!~!"+rsQL.getFloat(8)+"!~!"+rsQL.getFloat(9)+"!~!"+rsQL.getString(6)+"!~!"+Fmarkers+"!~!"+rsQL.getString(7)+"!~!"+rsQL.getString(16)+"!~!"+rsQL.getString(17)+"!~!"+rsQL.getString(18)+"!~!"+rsQL.getString(19)+"!~!"+rsQL.getString(20));
							}
						}else
							qtlExists=false;
						session.setAttribute("qtlExistsSes", qtlExists);
				}
				
				
				/*System.out.println("list=:"+list);
				System.out.println("mListExp="+mListExp);
				System.out.println("mapData:"+mapData);*/
				if(expType.equalsIgnoreCase("flapjack")){
					String FlapjackPath=filePath+"/Flapjack";					
					ef.FlapjackDat(list, mapData, FlapjackPath, req, gNamesList, mListExp, qtlData, exportOpType, qtlExists);
					
				}else if(expType.contains("Genotyping X Marker Matrix")){					
					ef.Matrix(list, filePath, req, gNamesList, mListExp, sortedGMap);
				}
			}
			/*if(rs4!=null) rs4.close(); if(rsM!=null) rsM.close(); if(rsN!=null) rsN.close();if(rsNL!=null) rsNL.close(); if(rsQ!=null) rsQ.close(); if(rsMap!=null) rsMap.close();if(rs!=null) rs.close();if(rsG!=null) rsG.close(); if(rsD!=null) rsD.close();if(rs2!=null) rs2.close(); if(rs1!=null) rs1.close(); if(rs!=null) rs.close();
			if(rs4!=null) stQ.close();if(st!=null) st.close(); if(stmt!=null) stmt.close(); if(stmt1!=null) stmt1.close(); if(stmt2!=null) stmt2.close(); if(stmtM!=null) stmtM.close(); if(stmtG!=null) stmtG.close(); if(stmtD!=null) stmtD.close(); if(stP!=null) stP.close();
			*/
			if(con!=null) con.close(); if(conn!=null) conn.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close(); conn.close();
		      		factory.close();
		         }catch(Exception e){System.out.println(e);}
			}
		
		
		
		return am.findForward("exp");
	}
	

}
