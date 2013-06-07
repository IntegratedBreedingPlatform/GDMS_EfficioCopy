/**
 * 
 */
package org.icrisat.gdms.retrieve;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;
import org.generationcp.middleware.util.HibernateUtil;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.manager.api.OntologyDataManager;
import org.hibernate.Query;
import org.icrisat.gdms.common.ExportFormats;
import org.icrisat.gdms.common.MaxIdValue;


public class RetrieveExportFormatAction extends Action{
	
	java.sql.Connection conn;
	java.sql.Connection con;
	private static WorkbenchDataManager wdm;
	private static HibernateUtil hibernateUtil;
	
	static Map<Integer, ArrayList<String>> hashMap = new HashMap<Integer,  ArrayList<String>>();  
	
	
	HashMap<Integer, HashMap<String, Object>> mapEx = new HashMap<Integer, HashMap<String,Object>>();	
	HashMap<String,Object> markerAlleles= new HashMap<String,Object>();
	
	HashMap<String,Object> markerPAAlleles= new HashMap<String,Object>();
	HashMap<String,Object> markerPBAlleles= new HashMap<String,Object>();
	
	//Connection con=null;
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
		Properties prop=new Properties();		
		HttpSession session = req.getSession(true);
		if(session!=null){
			session.removeAttribute("qtlExistsSes");
		}
		
		/*List alleleList=new ArrayList();
		List charList=new ArrayList(); 
		List mapCharList=new ArrayList(); */
		int alleleCount=0;
		int charCount=0;
		int mapCharCount=0;
		int cenAlleleCount=0;
		int cenCharCount=0;
		int cenMapCharCount=0;
		int locAlleleCount=0;
		int locCharCount=0;
		int locMapCharCount=0;
		int ParentAGID=0;
		int ParentBGID=0;
		MaxIdValue r=new MaxIdValue();
		ArrayList gListExp=new ArrayList();
		HashMap gListExp1=new HashMap();
		ArrayList mListExp=new ArrayList();
		DecimalFormat decfor = new DecimalFormat("#.00");
		int qtlCount=0;
		String chValues="";
		Query charQuery=null;
		String markers="";
		String gids="";
		String gidslist="";
		//String datasetName="";
		int datasetId=0;
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
		ArrayList markersInMap=new ArrayList();
		ArrayList markerIDsList=new ArrayList();
		ArrayList markerIdList=new ArrayList();
		String markerIDs="";
		//SortedMap markersMap=
		SortedMap marNamesMap = new TreeMap();
		filePath=req.getSession().getServletContext().getRealPath("//");
		/*if(!new File(filePath+"/jsp/analysisfiles").exists())
	   		new File(filePath+"/jsp/analysisfiles").mkdir();
		*/
        String path="";
        String bPath=session.getServletContext().getRealPath("//");
        //String bPath="C:\\IBWorkflowSystem\\infrastructure\\tomcat\\webapps\\GDMS";
        String opPath=bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1);
       
        //System.out.println(",,,,,,,,,,,,,  :"+bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1));
        String pathWB="";
		String filePathWB="";
		//if((format.equalsIgnoreCase("flapjack"))||(format.equalsIgnoreCase("cmtv")))
		if(format.equalsIgnoreCase("flapjack"))
			map=df.get("maps").toString();
		
		req.getSession().setAttribute("exportFormat", format);
		Calendar now = Calendar.getInstance();
		String mSec=now.getTimeInMillis()+"";
		//String fname=filePath+"/jsp/analysisfiles/matrix"+mSec+".xls";
		ArrayList tidsList=new ArrayList();
		SortedMap traitsMap = new TreeMap();
		if(session!=null){
			session.removeAttribute("msec");			
		}
		try{
			ResultSet rs1C=null;
			ResultSet rsC=null;
			ResultSet rs=null;			
			ResultSet rs1=null;
			ResultSet rsG=null;	
			ResultSet rsM=null;
			ResultSet rs2=null;
			ResultSet rsD=null;
			ResultSet rsN=null;
			ResultSet rsMT=null;
			ResultSet rsPD=null;
			ResultSet rsP=null;ResultSet rsPDL=null;
			ResultSet rs3=null;ResultSet rsMTL=null;
			ResultSet rsQ=null; ResultSet rsDetL=null;
			ResultSet rsL=null; ResultSet rscL=null;
			ResultSet rsP1=null; ResultSet rsc=null;
			ResultSet rsP2=null; ResultSet rsaL=null;
			ResultSet rsML=null; ResultSet rsa=null;
			ResultSet rsMC=null; ResultSet rs2L=null;
			ResultSet rsMap=null; ResultSet rs1L=null;
			ResultSet rsMapL=null; ResultSet rs2C=null;
			ResultSet rsM1=null;ResultSet rsP1C=null;
			ResultSet rsP1L=null;
			ResultSet rsP2C=null;ResultSet rsP2L=null;
			
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
			
			ArrayList lstMarkers = new ArrayList();			
			DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters("DatabaseConfig.properties", "workbench");
	        hibernateUtil = new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), 
	                                workbenchDb.getUsername(), workbenchDb.getPassword());
	        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
	        wdm = new WorkbenchDataManagerImpl(sessionProvider);	
	        
	        HashMap<Object, String> IBWFProjects= new HashMap<Object, String>();
	        List<Project> projects = wdm.getProjects();
	        Long projectId = Long.valueOf(0);
	        //System.out.println("testGetProjects(): ");
	        for (Project project : projects) {
	            //System.out.println("  " + project.getLocalDbName());
	            projectId = project.getProjectId();
	            IBWFProjects.put(project.getLocalDbName(),project.getProjectId()+"-"+project.getProjectName());
	        }
	        //System.out.println(".........:"+IBWFProjects.get(dbNameL));
	        pathWB=opPath+"/IBWorkflowSystem/workspace/"+IBWFProjects.get(dbNameL)+"/gdms/output";
	        //pathWB="C:/IBWorkflowSystem/workspace/1-TL1_Groundnut/gdms/output";
	        if(!new File(pathWB+"/analysisfiles").exists())
		   		new File(pathWB+"/analysisfiles").mkdir();
	        
		
			filePath=req.getSession().getServletContext().getRealPath("//");
			if(new File(filePath+"/analysisfiles").exists()){
				//new File(filePath+"/analysisfiles").delete();
				FileUtils.cleanDirectory(new File(filePath+"/analysisfiles")); 
			}
		        
			if(!new File(filePath+"/analysisfiles").exists())
		   		new File(filePath+"/analysisfiles").mkdir();
			
			
			//filePath=path;
			//factory = new ManagerFactory(local, central);
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			OntologyDataManager om=factory.getNewOntologyDataManager();
			
			String parentsListToWrite="";
			
			ArrayList gidsList=new ArrayList();
			ArrayList midsList=new ArrayList();
			ArrayList strL=new ArrayList();
			//String strL="";
			String qtl_id="";
			ArrayList<Integer> tid=new ArrayList();
			ArrayList qtlData=new ArrayList();
			String datasetType="";
			String parentsNames="";
			String parents="";
			String parentB="";
			String parentA="";
			Statement stLC=null;
			Statement stmt=null;
			Statement stmt1=null;
			Statement stmt2=null;
			Statement stmtM=null;
			Statement st=null;
			Statement stmtG=null;
			Statement stmtN=null;
			Statement stmtMT=null;
			Statement stmtPD=null;
			Statement stmtP=null;
			Statement stP=null;
			Statement stQ=null;			
			Statement stP1=null;
			Statement stP2=null;
			
			
			req.getSession().setAttribute("msec", mSec);
			String mapping_type="";
			String mType="";
			ArrayList parentsList = new ArrayList();
			ArrayList gidList=new ArrayList();
			//System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^op="+op);
			
			String exportOpType="";
			
			if(format.equalsIgnoreCase("flapjack")){
				exportOpType=df.get("exportTypeH").toString();				
			}		
			
			if(op.equalsIgnoreCase("dataset")){	
				
				/** retrieving data of the whole dataset for export formats **/				
				datasetId=Integer.parseInt(df.get("dataset").toString());
				HashMap<Object, String> gMap = new HashMap<Object, String>();
				HashMap<Object, String> mMap = new HashMap<Object, String>();
				
				ArrayList parentsData=new ArrayList();
				ArrayList parentAData=new ArrayList();
				ArrayList parentBData=new ArrayList();
				//System.out.println("datasetId=:"+datasetId);
				if(datasetId > 0){
					stLC=conn.createStatement();	
					stmt=conn.createStatement();
					stmt1=conn.createStatement();
					stmt2=conn.createStatement();
					stmtM=conn.createStatement();
					st=conn.createStatement();
					stmtG=conn.createStatement();
					stmtN=conn.createStatement();
					stmtMT=conn.createStatement();
					stmtPD=conn.createStatement();
					stmtP=conn.createStatement();
					stP=conn.createStatement();
					stQ=conn.createStatement();					
					stP1=conn.createStatement();
					stP2=conn.createStatement();
				}else{				
					stLC=con.createStatement();		
					stmt=con.createStatement();
					stmt1=con.createStatement();
					stmt2=con.createStatement();
					stmtM=con.createStatement();
					st=con.createStatement();
					stmtG=con.createStatement();
					stmtN=con.createStatement();
					stmtMT=con.createStatement();
					stmtPD=con.createStatement();
					stmtP=con.createStatement();
					stP=con.createStatement();
					stQ=con.createStatement();					
					stP1=con.createStatement();
					stP2=con.createStatement();
				}
				
				
				
				rs=stmt.executeQuery("select dataset_id, dataset_type from gdms_dataset where dataset_id="+datasetId);
				while(rs.next()){
					//datasetId=rs.getInt(1);
					datasetType=rs.getString(2);					
				}
				/*rsL=stLoc.executeQuery("select dataset_id, dataset_type from gdms_dataset where dataset_id="+datasetId);
				while(rsL.next()){
					//datasetId=rs.getInt(1);
					datasetType=rsL.getString(2);					
				}*/
				System.out.println(",,,,,,,,,:"+datasetType);
				String gid="";
				String mid="";
				int parentAint=0;
				int parentBint=0;
				String pgids="";
				ArrayList markersList=new ArrayList();		
				
				long startTime = System.currentTimeMillis();
				rsC=stCen.executeQuery("select marker_id from gdms_marker_metadataset where dataset_id="+datasetId+" order by marker_id");
				while(rsC.next()){
					mid=mid+rsC.getInt(1)+",";
					if(!markersList.contains(rsC.getInt(1)))
						markersList.add(rsC.getInt(1));
					
				}
				rsL=stLoc.executeQuery("select marker_id from gdms_marker_metadataset where dataset_id="+datasetId+" order by marker_id");
				while(rsL.next()){
					mid=mid+rsL.getInt(1)+",";
					if(!markersList.contains(rsL.getInt(1)))
						markersList.add(rsL.getInt(1));
					
				}
				
				mid=mid.substring(0, mid.length()-1);
				//lstMarkers.clear();
				HashMap<Integer, Object> markersMap = new HashMap<Integer, Object>();
	            //System.out.println("select distinct marker_id, marker_name from gdms_marker where marker_id in ("+mid+") order by marker_id asc");
	            rsML=stLoc.executeQuery("select distinct marker_id, marker_name from gdms_marker where marker_id in ("+mid+") order by marker_id asc");
	            rsMC=stCen.executeQuery("select distinct marker_id, marker_name from gdms_marker where marker_id in ("+mid+") order by marker_id asc");
	            
	            while(rsMC.next()){	            	
	            	markersMap.put(rsMC.getInt(1), rsMC.getString(2));		
	            }
	            while(rsML.next()){            	
	            	markersMap.put(rsML.getInt(1), rsML.getString(2));	
	            }
				
				for(int mp=0;mp<markersList.size();mp++){					
					lstMarkers.add(markersMap.get(Integer.parseInt(markersList.get(mp).toString())));					
				}
				
				//System.out.println("select distinct marker_type from gdms_marker where marker_id in("+mid+")");
				rsMT=stCen.executeQuery("select distinct marker_type from gdms_marker where marker_id in("+mid+")");
				while (rsMT.next()){
					mType=rsMT.getString(1);
				}
				rsMT=stmtMT.executeQuery("select distinct marker_type from gdms_marker where marker_id in("+mid+")");
				while (rsMT.next()){
					mType=rsMT.getString(1);
				}
				if(datasetType.equalsIgnoreCase("mapping")){
					//System.out.println("IF Mapping ...................");
					rsP=stCen.executeQuery("select parent_a_nid, parent_b_nid from gdms_mapping_pop where dataset_id="+datasetId);
					rsPDL=stLoc.executeQuery("select parent_a_nid, parent_b_nid from gdms_mapping_pop where dataset_id="+datasetId);
					
					
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
					while(rsPDL.next()){
						parentsNames=rsPDL.getInt(1)+","+rsPDL.getInt(2);	
						parentsList.add(rsPDL.getInt(1));
						parentsList.add(rsPDL.getInt(2));
						parentAint=rsPDL.getInt(1);
						parentBint=rsPDL.getInt(2);
					}
					ParentAGID=Integer.parseInt(manager.getGermplasmNameByID(parentAint).getGermplasmId().toString());
					ParentBGID=Integer.parseInt(manager.getGermplasmNameByID(parentBint).getGermplasmId().toString());
					System.out.println("parentA="+parentAint+"   parentB=:"+parentBint);
					//if(format.equalsIgnoreCase("flapjack")){
						//if((mapping_type.equalsIgnoreCase("allelic"))||(format.equalsIgnoreCase("flapjack"))){
						if(mapping_type.equalsIgnoreCase("allelic")){
							/*if(!exportOpType.equals("gname")){
							
							if(!gListExp.contains(parentAint))
								gListExp.add(parentAint);
							if(!gListExp.contains(parentBint))
								gListExp.add(parentBint);
							}*/
							rsP1C=stCen.executeQuery("select nid from gdms_acc_metadataset where gid="+ParentAGID+" and dataset_id="+datasetId);
							rsP1L=stLoc.executeQuery("select nid from gdms_acc_metadataset where gid="+ParentAGID+" and dataset_id="+datasetId);
							
							
							rsP2C=stP2.executeQuery("select nid from gdms_acc_metadataset where gid="+ParentBGID+" and dataset_id="+datasetId);
							rsP2L=stP2.executeQuery("select nid from gdms_acc_metadataset where gid="+ParentBGID+" and dataset_id="+datasetId);
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
							
						}
						if(pgidsList.contains(parentAint)){
							parents=parents+gMap.get(parentAint)+"!~!";
							parentsListToWrite=parentsListToWrite+ParentAGID+";;"+gMap.get(parentAint)+"!~!";
						}
						if(pgidsList.contains(parentBint)){
							parents=parents+gMap.get(parentBint);
							parentsListToWrite=parentsListToWrite+ParentBGID+";;"+gMap.get(parentBint)+"!~!";
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
								gMap.put(names.getGermplasmId(), names.getNval());
								parentsListToWrite=parentsListToWrite+names.getGermplasmId()+";;"+names.getNval()+"!~!";
					        //}
						}			
						
						
					}
					System.out.println("parents List=:"+gMap);
					System.out.println("parentsListToWrite=:"+parentsListToWrite);
						System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,  mType:"+mType);
					/*System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,  parents:"+parents);
					System.out.println("pgids=:"+pgids);
					System.out.println("mids=:"+mid);*/
					/*String parentAData="";
					String parentBData="";*/
					if(mapping_type.equalsIgnoreCase("allelic")){							
						//System.out.println("....."+"select gid,marker_id, char_value from gdms_char_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid.substring(0,mid.length()-1)+") order by gid, marker_id");
						if((mType.equalsIgnoreCase("SSR"))||(mType.equalsIgnoreCase("DArT"))){
							rsPD=stmtPD.executeQuery("select gid,marker_id, allele_bin_value from gdms_allele_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid+") order by gid, marker_id");
						}else if(mType.equalsIgnoreCase("SNP")){
							//System.out.println("select gid,marker_id, char_value from gdms_char_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid.substring(0,mid.length()-1)+") order by gid, marker_id");
							//System.out.println("select gid,marker_id, char_value from gdms_char_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid.substring(0,mid.length()-1)+") order by gid, marker_id");
							rsPD=stmtPD.executeQuery("select gid,marker_id, char_value from gdms_char_values where gid in("+pgids.substring(0,pgids.length()-1)+") and marker_id in("+mid+") order by gid, marker_id");
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
					}else if(mapping_type.equalsIgnoreCase("abh")){	
						
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
				//System.out.println("format="+format);
				if(((format.contains("Flapjack"))&&(mapping_type.equalsIgnoreCase("abh")))||((format.contains("Genotyping X Marker Matrix"))&&(mapping_type.equalsIgnoreCase("abh")))){
					//System.out.println("only flapjack and abh");
					for(int m=0; m<markersList.size(); m++){
						strL.add(ParentAGID+","+markersList.get(m)+","+"A");
					}
					for(int m=0; m<markersList.size(); m++){
						strL.add(ParentBGID+","+markersList.get(m)+","+"B");
					}
				}
				System.out.println("strL=:"+strL);
				//System.out.println("select marker_id, marker_name from marker where marker_id in("+ mid.substring(0,mid.length()-1) +") order by marker_id ASC");
				//rsM=stmtM.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in("+ mid.substring(0,mid.length()-1) +") order by marker_id ASC");
				//System.out.println("datasetType="+datasetType);
				if(datasetType.equalsIgnoreCase("SNP")){
					//System.out.println("..................SNP.................");
					//System.out.println("select gid, marker_id, char_value from gdms_char_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					rsD=st.executeQuery("select gid, marker_id, char_value from gdms_char_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					
				}else if((datasetType.equalsIgnoreCase("SSR"))||(datasetType.equalsIgnoreCase("DArT"))){
					//System.out.println("**************************  DArT / SSR *****************************");
					//System.out.println("select gid, marker_id, allele_bin_value from gdms_allele_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					rsD=st.executeQuery("select gid, marker_id, allele_bin_value from gdms_allele_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					
				}else if(datasetType.equalsIgnoreCase("mapping")){
					//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%  Mapping %%%%%%%%%%%%%%%%%%%%%%");
					System.out.println("select gid, marker_id, map_char_value from gdms_mapping_pop_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					rsD=st.executeQuery("select gid, marker_id, map_char_value from gdms_mapping_pop_values where dataset_id="+datasetId+" ORDER BY gid, marker_id ASC");
					
					session.setAttribute("dataset", datasetId);
				}
				/*while(rsM.next()){
					mMap.put(rsM.getInt(1), rsM.getString(2));
					midsList.add(rsM.getString(1));
					if(!(mListExp.contains(rsM.getString(2))))
					mListExp.add(rsM.getString(2));
				}*/
				
				//System.out.println(".......1212121......:"+strL);
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
				
				Map<Object, String> sortedMap = new TreeMap<Object, String>(gMap);
				Map<Object, String> sortedMarkerMap = new TreeMap<Object, String>(mMap);
	           	Set keys = sortedMap.keySet();
	           //	int mcount=mListExp.size();
	           	int mcount=lstMarkers.size();

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
				ArrayList TestMarkersList=new ArrayList();
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
						list.add(sortedMap.get(Integer.parseInt(arr[0]))+","+markersMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
					}else{
						list.add(arr[0]+","+markersMap.get(Integer.parseInt(arr[1]))+","+arr[2]);
						if(!TestMarkersList.contains(markersMap.get(Integer.parseInt(arr[1]))))
								TestMarkersList.add(markersMap.get(Integer.parseInt(arr[1])));
					}
					//}					
				}
				//System.out.println("list="+list);
				
				mapData="";
				String mapIds="";
				String mapName  = "";
				//qtl_id
				/** retrieving map data for flapjack .map file **/
				if(format.contains("Flapjack")){
					System.out.println("............................   :"+req.getParameter("mapsCount"));
					int mapCount=Integer.parseInt(df.get("mapsCount").toString());
					if(mapCount==0){
						for(int m=0;m<markersList.size();m++){
							if(!markerIDsList.contains(markersList.get(m))){
					    		markerIDsList.add(markersList.get(m));
					    		markerIDs=markerIDs+markersList.get(m)+",";
							}
						}
						
					}else{						
						mapName  = map.substring(0,map.lastIndexOf("("));
						
					    rsMC=stCen.executeQuery("select gdms_markers_onmap.marker_id from gdms_markers_onmap, gdms_map where gdms_map.map_name='"+mapName+"'");
					    while(rsMC.next()){
					    	markerIDsList.add(rsMC.getInt(1));
					    	markerIDs=markerIDs+rsMC.getInt(1)+",";
					    }
					    rsML=stLoc.executeQuery("select gdms_markers_onmap.marker_id from gdms_markers_onmap, gdms_map where gdms_map.map_name='"+mapName+"'");
					    while(rsML.next()){
					    	if(!markerIDsList.contains(rsML.getInt(1))){
					    		markerIDsList.add(rsML.getInt(1));
					    		markerIDs=markerIDs+rsML.getInt(1)+",";
					    	}				    	
					    }
						
					}
				   // markerIDsList
				    rsM=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in("+markerIDs.substring(0, markerIDs.length()-1)+")");
				    while(rsM.next()){
				    	markerIdList.add(rsM.getString(2));
				    	marNamesMap.put(rsM.getInt(1), rsM.getString(2));
				    }
				    rsM1=stLoc.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in("+markerIDs.substring(0, markerIDs.length()-1)+")");
				    while(rsM1.next()){
				    	if(!markerIdList.contains(rsM1.getString(2))){
				    		markerIdList.add(rsM1.getString(2));
				    		marNamesMap.put(rsM1.getInt(1), rsM1.getString(2));
				    	}
				    	
				    }
				    if(mapCount!=0){
					    System.out.println("select marker_id, linkage_group, start_position from gdms_markers_onmap, gdms_map where gdms_map.map_name ='"+mapName+"' AND gdms_map.map_id=gdms_markers_onmap.map_id order by linkage_group, start_position,marker_id");
						rs=stCen.executeQuery("select marker_id, linkage_group, start_position from gdms_markers_onmap, gdms_map where gdms_map.map_name='"+mapName+"' AND gdms_map.map_id=gdms_markers_onmap.map_id order by linkage_group, start_position,marker_id");
						while(rs.next()){
							//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
							mapData=mapData+marNamesMap.get(rs.getInt(1)).toString()+"!~!"+rs.getString(2)+"!~!"+rs.getDouble(3)+"~~!!~~";
							if(!markersInMap.contains(rs.getInt(1)))
								markersInMap.add(rs.getInt(1));
						}
						rsL=stLoc.executeQuery("select marker_id, linkage_group, start_position from gdms_markers_onmap, gdms_map where gdms_map.map_name='"+mapName+"' AND gdms_map.map_id=gdms_markers_onmap.map_id order by linkage_group, start_position,marker_id");
						while(rsL.next()){
							//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
							mapData=mapData+marNamesMap.get(rsL.getInt(1)).toString()+"!~!"+rsL.getString(2)+"!~!"+rsL.getDouble(3)+"~~!!~~";
							if(!markersInMap.contains(rsL.getInt(1)))
								markersInMap.add(rsL.getInt(1));
						}
						
						
						
						System.out.println(markersList);
						System.out.println("......:"+markersInMap);
						for(int m=0;m<markersList.size();m++){
							if(!(markersInMap.contains(markersList.get(m)))){
								mapData=mapData+markersMap.get(markersList.get(m))+"!~!"+"unmapped"+"!~!"+"0"+"~~!!~~";
							}
						}
						//System.out.println("mapData=:"+mapData);
						rsM=stCen.executeQuery("select map_id from gdms_map where map_name ='"+mapName+"'");
						rsL=stLoc.executeQuery("select map_id from gdms_map where map_name ='"+mapName+"'");
						while(rsM.next()){
							mapIds=mapIds+rsM.getInt(1)+",";
						}
						while(rsL.next()){
							mapIds=mapIds+rsL.getInt(1)+",";
						}
						//rsMap=stCen.executeQuery("select qtl_id from gdms_qtl_details where map_id =(select map_id from gdms_map where map_name ='"+mapName+"')");
						System.out.println("select qtl_id, tid from gdms_qtl_details where map_id in("+mapIds.substring(0, mapIds.length()-1)+")");
						rsMap=stCen.executeQuery("select qtl_id, tid from gdms_qtl_details where map_id in("+mapIds.substring(0, mapIds.length()-1)+")");
						while(rsMap.next()){
							//System.out.println("..............:"+rsMap.getInt(1));
							qtlCount++;
							qtl_id=qtl_id+rsMap.getInt(1)+",";
							//tid=tid+rsMap.getInt(2)+",";
							if(!tid.contains(rsMap.getInt(2)))
								tid.add(rsMap.getInt(2));
						}
						rsMapL=stLoc.executeQuery("select qtl_id, tid from gdms_qtl_details where map_id in("+mapIds.substring(0, mapIds.length()-1)+")");
						//rsQ=stQ.executeQuery("");
						while(rsMapL.next()){
							//System.out.println("..............:"+rsMap.getInt(1));
							qtlCount++;
							qtl_id=qtl_id+rsMapL.getInt(1)+",";
							//tid=tid+rsMapL.getInt(2)+",";
							if(!tid.contains(rsMapL.getInt(2)))
								tid.add(rsMapL.getInt(2));
						}
						System.out.println("qtlCount:"+qtlCount);
						if(qtlCount>0){
							System.out.println("...............:Count >0");
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
							
							
							
							qtlExists=true;
							//System.out.println("................;"+qtl_id);
							//System.out.println("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl_details.linkage_group, gdms_qtl_details.qtl_id");
							rsQ=stCen.executeQuery("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl_details.linkage_group, gdms_qtl_details.qtl_id");
							while(rsQ.next()){
								String Fmarkers=rsQ.getString(12)+"/"+rsQ.getString(13);
								qtlData.add(rsQ.getString(22)+"!~!"+rsQ.getString(10)+"!~!"+rsQ.getFloat(14)+"!~!"+rsQ.getFloat(3)+"!~!"+rsQ.getFloat(4)+"!~!"+traitsMap.get(rsQ.getInt(5)).toString()+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getFloat(8)+"!~!"+rsQ.getFloat(9)+"!~!"+rsQ.getString(6)+"!~!"+Fmarkers+"!~!"+rsQ.getString(7)+"!~!"+rsQ.getString(16)+"!~!"+rsQ.getString(17)+"!~!"+rsQ.getString(18)+"!~!"+rsQ.getString(19)+"!~!"+rsQ.getString(20));
							
							}
							rsDetL=stLoc.executeQuery("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl_details.linkage_group, gdms_qtl_details.qtl_id");
							while(rsDetL.next()){
								String Fmarkers=rsDetL.getString(12)+"/"+rsDetL.getString(13);
								qtlData.add(rsDetL.getString(22)+"!~!"+rsDetL.getString(10)+"!~!"+rsDetL.getFloat(14)+"!~!"+rsDetL.getFloat(3)+"!~!"+rsDetL.getFloat(4)+"!~!"+traitsMap.get(rsDetL.getInt(5)).toString()+"!~!"+rsDetL.getString(6)+"!~!"+rsDetL.getString(6)+"!~!"+rsDetL.getFloat(8)+"!~!"+rsDetL.getFloat(9)+"!~!"+rsDetL.getString(6)+"!~!"+Fmarkers+"!~!"+rsDetL.getString(7)+"!~!"+rsDetL.getString(16)+"!~!"+rsDetL.getString(17)+"!~!"+rsDetL.getString(18)+"!~!"+rsDetL.getString(19)+"!~!"+rsDetL.getString(20));
							
							}
							System.out.println("qtlData="+qtlData);
						}else{
							qtlExists=false;
						}
						session.setAttribute("qtlExistsSes", qtlExists);
				    }else{
				    	System.out.println(markersList);
						System.out.println("......:"+markersInMap);
						for(int m=0;m<markersList.size();m++){
							//if(!(markersInMap.contains(markersList.get(m)))){
								mapData=mapData+markersMap.get(markersList.get(m))+"!~!"+"unmapped"+"!~!"+"0"+"~~!!~~";
							//}
						}
						qtlExists=false;
						session.setAttribute("qtlExistsSes", qtlExists);
				    }
				    
					
				}
				System.out.println("qtlExists=:"+qtlExists);
				System.out.println("TestMarkersList="+TestMarkersList);
				System.out.println("lstMarkers=:"+lstMarkers);
				if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("SNP"))){
					ef.MatrixDataSNPDataset(list, pathWB, req, gListExp, lstMarkers, sortedMap);
					ef.MatrixDataSNPDataset(list, filePath, req, gListExp, lstMarkers, sortedMap);
					session.setAttribute("datasetType", datasetType);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("mapping"))){
					ef.mapMatrix(list, pathWB, req, gListExp, lstMarkers, parentsListToWrite, sortedMap);	
					ef.mapMatrix(list, filePath, req, gListExp, lstMarkers, parentsListToWrite, sortedMap);	
					session.setAttribute("datasetType", datasetType);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("SSR"))){
					if(mcount>252){
						session.setAttribute("datasetType", "SNP");
						ef.MatrixDataSNPDataset(list, pathWB, req, gListExp, lstMarkers, sortedMap);
						ef.MatrixDataSNPDataset(list, filePath, req, gListExp, lstMarkers, sortedMap);
					}else{
						session.setAttribute("datasetType", "SSR");
						ef.Matrix(list, pathWB, req, gListExp, lstMarkers, sortedMap);
						ef.Matrix(list, filePath, req, gListExp, lstMarkers, sortedMap);
					}
						
					//ef.Matrix(list, filePath, req, gListExp, mListExp);
					//ef.Matrix(list, filePath, req);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("DArT"))){
					session.setAttribute("datasetType", datasetType);
					ef.Matrix(list, pathWB, req, gListExp, lstMarkers, sortedMap);
					ef.Matrix(list, filePath, req, gListExp, lstMarkers, sortedMap);
				}
				if(format.contains("Flapjack")){
					if(session!=null){
						session.removeAttribute("FlapjackPath");	
						session.removeAttribute("Fsession");		
					}
					session.setAttribute("datasetType", datasetType);
					String fSession=req.getSession().getAttribute("msec").toString();
					String FlapjackPath=filePath+"/Flapjack";
					
					/*ef.MatrixDat(list, mapData, FlapjackPath, req, gListExp, mListExp, qtlData, exportOpType, qtlExists, mapEx, gListExp1);
					
					ef.MatrixDat(list, mapData, FlapjackPath, req, gListExp, mListExp, qtlData, exportOpType, qtlExists, mapEx, gListExp1);*/
					
					ef.FlapjackDat(list, mapData, FlapjackPath, req, gListExp, lstMarkers, qtlData, exportOpType, qtlExists);
					
					//ef.MatrixDat(list, mapData, filePath, req);
					//ef.Matrix(list, filePath, request);
					session.setAttribute("FlapjackPath", FlapjackPath);
					session.setAttribute("Fsession",fSession);
					
					
				}
				long endTime = System.currentTimeMillis();
				 //System.out.println("*********************   dataset type="+session.getAttribute("datasetType"));
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
				int mapId=0;
				String type=req.getSession().getAttribute("type").toString();
				//System.out.println("type="+req.getSession().getAttribute("type"));
				if(type.equals("map")){
					String[] maps=null;
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
					if(!new File(filePath+"/analysisfiles").exists())
				   		new File(filePath+"/analysisfiles").mkdir();
					session.setAttribute("count", (mapStr.length));
					for(int c=0;c<mapStr.length;c++){
						maps=mapStr[c].split("!~!");
						mapId=Integer.parseInt(maps[1].substring(0, maps[1].length()-1));
						//System.out.println("^^^^^^^^^^^^^^^^^^^^    :"+maps[0].substring(1));
						if(session!=null){
							session.removeAttribute("msec");			
						}
						mSec=now.getTimeInMillis()+"";
						//System.out.println("msec="+mapStr[c]);			
						f1=f1+maps[0].substring(1)+"!~!"+mSec+c+";;";
						ArrayList dist=new ArrayList();
						ArrayList CD=new ArrayList();
						req.getSession().setAttribute("exportFormat","CMTV");
						req.getSession().setAttribute("msec", mSec+c);
						//String filePath="";
						//ExportFormats ef=new ExportFormats();
						//MaxIdValue r=new MaxIdValue();
						String mapUnit="";
						String marker_id="";
						HashMap<Integer, Object> markersMap = new HashMap<Integer, Object>();
			            //List lstMarkers = new ArrayList();
						if(mapId>0){
							rsC=stCen.executeQuery("select map_unit from gdms_map where map_id="+mapId);
							while (rsC.next()){
								mapUnit=rsC.getString(1);
							}
							rs1C=stCen.executeQuery("select marker_id from gdms_markers_onmap where map_id="+mapId);
							while(rs1C.next()){
								marker_id=marker_id+rs1C.getInt(1)+",";
							}
							marker_id=marker_id.substring(0, marker_id.length()-1);
							
							rs2C=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in ("+marker_id+")");
							while(rs2C.next()){
								lstMarkers.add(rs2C.getInt(1));
				            	markersMap.put(rs2C.getInt(1), rs2C.getString(2));						
							}
							
							
							//System.out.println("select gdms_markers_onmap.marker_id, gdms_markers_onmap.linkage_group, gdms_markers_onmap.start_position, gdms_map.map_name from gdms_markers_onmap, gdms_map where gdms_map.map_id="+mapId+" and gdms_map.map_id=gdms_markers_onmap.map_id ORDER BY linkage_group, start_position asc, marker_id, map_name");
							rsMC=stCen.executeQuery("select gdms_markers_onmap.marker_id, gdms_markers_onmap.linkage_group, gdms_markers_onmap.start_position, gdms_map.map_name from gdms_markers_onmap, gdms_map where gdms_map.map_id="+mapId+" and gdms_map.map_id=gdms_markers_onmap.map_id ORDER BY linkage_group, start_position asc, marker_id, map_name");
							while(rsMC.next()){
								if(mapUnit.equalsIgnoreCase("bp")){
									CD.add(rsMC.getString(2)+"!~!"+new BigDecimal(rsMC.getString(3))+"!~!"+markersMap.get(rsMC.getInt(1))+"!~!"+rsMC.getString(4));
								}else{
									CD.add(rsMC.getString(2)+"!~!"+decfor.format(rsMC.getDouble(3))+"!~!"+markersMap.get(rsMC.getInt(1))+"!~!"+rsMC.getString(4));
								}
								count=count+1;
							}
							
						}else{
							rsL=stLoc.executeQuery("select map_unit from gdms_map where map_id="+mapId);
							while (rsL.next()){
								mapUnit=rsL.getString(1);
							}
							
							rs1L=stLoc.executeQuery("select marker_id from gdms_markers_onmap where map_id="+mapId);
							while(rs1L.next()){
								marker_id=marker_id+rs1L.getInt(1)+",";
							}
							marker_id=marker_id.substring(0, marker_id.length()-1);
							
							rs2C=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in ("+marker_id+")");
							while(rs2C.next()){
								lstMarkers.add(rs2C.getInt(1));
				            	markersMap.put(rs2C.getInt(1), rs2C.getString(2));						
							}
							rs2L=stLoc.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in ("+marker_id+")");
							while(rs2L.next()){
								lstMarkers.add(rs2L.getInt(1));
				            	markersMap.put(rs2L.getInt(1), rs2L.getString(2));						
							}
							
							rsML=stLoc.executeQuery("select gdms_markers_onmap.marker_id, gdms_markers_onmap.linkage_group, gdms_markers_onmap.start_position, gdms_map.map_name from gdms_markers_onmap, gdms_map where gdms_map.map_id="+mapId+" and gdms_map.map_id=gdms_markers_onmap.map_id ORDER BY linkage_group, start_position asc, marker_id, map_name");
							while(rsML.next()){
								//mapData=mapData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getDouble(3)+"!~!"+rs.getString(4)+"~~!!~~";
								//CD.add(rs.getString(2)+"!~!"+rs.getDouble(3)+"!~!"+rs.getString(1)+"!~!"+rs.getString(4));
								if(mapUnit.equalsIgnoreCase("bp")){
									CD.add(rsML.getString(2)+"!~!"+new BigDecimal(rsML.getString(3))+"!~!"+markersMap.get(rsML.getInt(1))+"!~!"+rsML.getString(4));
								}else{
									CD.add(rsML.getString(2)+"!~!"+decfor.format(rsML.getDouble(3))+"!~!"+markersMap.get(rsML.getInt(1))+"!~!"+rsML.getString(4));
								}
								count=count+1;
								//CD.add(rs.getFloat(3));
							}
							
							
							
						}
						
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
										//dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+dis1+"!~!"+dis1);
										dist.add(str1[0]+"!~!"+str1[2]+"!~!"+count+"!~!"+dis1+"!~!"+str1[1]);
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
						ef.CMTVTxt(dist, pathWB, req);
						
					}
					//System.out.println("f1="+f1);
					session.setAttribute("f1", f1);
				}else{
					//System.out.println("type="+type);
					ArrayList gList1=new ArrayList();
					ArrayList gidsList2=new ArrayList();
					if(type.equals("GermplasmName")){
						gidslist=df.get("markersSel").toString();
						markerslist=req.getSession().getAttribute("mnames").toString();
						mlist1=req.getSession().getAttribute("mnames1").toString();
						String[] gList=gidslist.split(";;");
						for(int m=0;m<gList.length;m++){
							gids=gids+gList[m]+",";
							gList1.add(gList[m]);
							if(!(gidsList2.contains(Integer.parseInt(gList[m]))))
								gidsList2.add(Integer.parseInt(gList[m]));
						}
						//System.out.println("Marekrs="+markerslist+"  /n Gids="+gids);
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
							if(!(gList1.contains(g1[g])))
								gList1.add(g1[g]);
							
							if(!(gidsList2.contains(Integer.parseInt(g1[g]))))
								gidsList2.add(Integer.parseInt(g1[g]));
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
						//System.out.println("Marekrs="+markerslist+"  /n Gids="+gids);
						mid=markerslist.substring(0,markerslist.length()-1);
					}
					alleleCount=0;
					charCount=0;
					mapCharCount=0;
					ArrayList markersList=new ArrayList();
					
					ArrayList nidList=new ArrayList();
					//System.out.println(".... gid="+gid);
					//System.out.println("gList1="+gList1);
					try{
						String data="";
						String nid="";
						rs=stCen.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gid+") order by gid");
						while(rs.next()){
							if(!nidList.contains(rs.getInt(1)))
								nidList.add(rs.getInt(1));
							nid=nid+rs.getString(1)+",";
						}
						rsL=stLoc.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gid+") order by gid");
						while(rsL.next()){
							if(!nidList.contains(rsL.getInt(1)))
								nidList.add(rsL.getInt(1));
							nid=nid+rsL.getString(1)+",";
						}
						
						
						
						//System.out.println("select count(*) from gdms_allele_values where gid in ("+gid+")");
						rsa=stCen.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
						while (rsa.next()){
							//alleleList.add(rsa.getInt(1));
							cenAlleleCount=rsa.getInt(1);
						}
						rsaL=stLoc.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
						while (rsaL.next()){
							/*if(!alleleList.contains(rsaL.getInt(1)))
								alleleList.add(rsaL.getInt(1));*/
							locAlleleCount=rsaL.getInt(1);
						}
						//alleleCount=Integer.parseInt(alleleList.get(0).toString());
						if(locAlleleCount>0)
							alleleCount=locAlleleCount;
						else if(cenAlleleCount>0)
							alleleCount=cenAlleleCount;
						else if(locAlleleCount>0 && cenAlleleCount > 0)
							alleleCount=cenAlleleCount;
						else
							alleleCount=0;						
						rsc=stCen.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
						while(rsc.next()){
							//charList.add(rsc.getInt(1));
							cenCharCount=rsc.getInt(1);
						}
					
						rscL=stLoc.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
						while(rscL.next()){
							/*if(!charList.contains(rscL.getInt(1)))
							charList.add(rscL.getInt(1));*/
							locCharCount=rscL.getInt(1);							
						}
						//charCount=Integer.parseInt(charList.get(0).toString());
						if(locCharCount>0)
							charCount=locCharCount;
						else if(cenCharCount>0)
							charCount=cenCharCount;
						else if(locCharCount>0 && cenCharCount > 0)
							charCount=cenCharCount;
						else
							charCount=0;						
						
						rsMap=stCen.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
						while(rsMap.next()){
							//mapCharList.add(rsMap.getInt(1));
							cenMapCharCount=rsMap.getInt(1);
						}
						
						rsML=stLoc.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
						while(rsML.next()){
							/*if(!mapCharList.contains(rsML.getInt(1)))
							mapCharList.add(rsML.getInt(1));*/
							locMapCharCount=rsML.getInt(1);
						}
						//mapCharCount=Integer.parseInt(mapCharList.get(0).toString());
						if(locMapCharCount>0)
							mapCharCount=locMapCharCount;
						else if(cenMapCharCount>0)
							mapCharCount=cenMapCharCount;
						else if(locMapCharCount>0 && cenMapCharCount > 0)
							mapCharCount=cenMapCharCount;
						else
							mapCharCount=0;
						
					
						//System.out.println(alleleCount+"    "+charCount+"    "+mapCharCount);
						ArrayList gidsList1=new ArrayList();
						HashMap marker = new HashMap();
						mapEx.clear();
						if(alleleCount>0){	
							markerAlleles.clear();
							marker = new HashMap();
							HashMap alleleMap=new HashMap();
							datasetType="mapping";
							ArrayList glist = new ArrayList();
							//System.out.println("SELECT distinct gdms_allele_values.gid,gdms_allele_values.allele_bin_value,gdms_marker.marker_name FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.marker_id=gdms_marker.marker_id AND gdms_allele_values.gid IN ("+gid+") AND gdms_allele_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_allele_values.gid, gdms_marker.marker_name");
							rsDet=stCen.executeQuery("SELECT distinct gdms_allele_values.gid,gdms_allele_values.allele_bin_value,gdms_marker.marker_name"+
									" FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.marker_id=gdms_marker.marker_id"+
									" AND gdms_allele_values.gid IN ("+gid+") AND gdms_allele_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_allele_values.gid, gdms_marker.marker_name");
							
							while(rsDet.next()){								
								if(!(mListExp.contains(rsDet.getString(3))))
									mListExp.add(rsDet.getString(3));
								
								data=data+rsDet.getInt(1)+"~!~"+rsDet.getString(2)+"~!~"+rsDet.getString(3)+"!~!";
								markerAlleles.put(rsDet.getInt(1)+"!~!"+rsDet.getString(3), rsDet.getString(2));
								if(!(glist.contains(rsDet.getInt(1))))
								glist.add(rsDet.getInt(1));					
							}
							rsDetL=stLoc.executeQuery("SELECT distinct gdms_allele_values.gid,gdms_allele_values.allele_bin_value,gdms_marker.marker_name"+
									" FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.marker_id=gdms_marker.marker_id"+
									" AND gdms_allele_values.gid IN ("+gid+") AND gdms_allele_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_allele_values.gid, gdms_marker.marker_name");
							
							
							while(rsDetL.next()){								
								if(!(mListExp.contains(rsDetL.getString(3))))
									mListExp.add(rsDetL.getString(3));
								
								data=data+rsDetL.getInt(1)+"~!~"+rsDetL.getString(2)+"~!~"+rsDetL.getString(3)+"!~!";
								markerAlleles.put(rsDetL.getInt(1)+"!~!"+rsDetL.getString(3), rsDetL.getString(2));
								if(!(glist.contains(rsDetL.getInt(1))))
								glist.add(rsDetL.getInt(1));					
							}
							
							
							List markerKey = new ArrayList();
							markerKey.addAll(markerAlleles.keySet());
							for(int g=0; g<glist.size(); g++){
								for(int i=0; i<markerKey.size();i++){
									 if(!(mapEx.get(Integer.parseInt(glist.get(g).toString()))==null)){
										 marker = (HashMap)mapEx.get(Integer.parseInt(glist.get(g).toString()));
									 }else{
									marker = new HashMap();
									 }
									 if(Integer.parseInt(glist.get(g).toString())==Integer.parseInt(markerKey.get(i).toString().substring(0, markerKey.get(i).toString().indexOf("!~!")))){
										 marker.put(markerKey.get(i), markerAlleles.get(markerKey.get(i)));
										 mapEx.put(Integer.parseInt(glist.get(g).toString()),(HashMap)marker);
									 }
									
								}	
							}
						}
						if(charCount>0){
							markerAlleles.clear();
							marker = new HashMap();
							HashMap CharMap=new HashMap();
							datasetType="SNP";
							ArrayList glist = new ArrayList();
							//System.out.println("SELECT DISTINCT gdms_char_values.gid,gdms_char_values.char_value as data,gdms_marker.marker_name FROM gdms_char_values,gdms_marker WHERE gdms_char_values.marker_id=gdms_marker.marker_id AND gdms_char_values.gid IN ("+gid+") AND gdms_char_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_char_values.gid, gdms_marker.marker_name");
							rsDet=stCen.executeQuery("SELECT DISTINCT gdms_char_values.gid,gdms_char_values.char_value as data,gdms_marker.marker_name"+
									" FROM gdms_char_values,gdms_marker WHERE gdms_char_values.marker_id=gdms_marker.marker_id"+
									" AND gdms_char_values.gid IN ("+gid+") AND gdms_char_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_char_values.gid, gdms_marker.marker_name");
							while(rsDet.next()){								
								if(!(mListExp.contains(rsDet.getString(3))))
									mListExp.add(rsDet.getString(3));
								
								data=data+rsDet.getInt(1)+"~!~"+rsDet.getString(2)+"~!~"+rsDet.getString(3)+"!~!";
								markerAlleles.put(rsDet.getInt(1)+"!~!"+rsDet.getString(3), rsDet.getString(2));
		
								if(!(glist.contains(rsDet.getInt(1))))
								glist.add(rsDet.getInt(1))	;					
							}
							
							rsDetL=stLoc.executeQuery("SELECT DISTINCT gdms_char_values.gid,gdms_char_values.char_value as data,gdms_marker.marker_name"+
									" FROM gdms_char_values,gdms_marker WHERE gdms_char_values.marker_id=gdms_marker.marker_id"+
									" AND gdms_char_values.gid IN ("+gid+") AND gdms_char_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_char_values.gid, gdms_marker.marker_name");
							while(rsDetL.next()){								
								if(!(mListExp.contains(rsDetL.getString(3))))
									mListExp.add(rsDetL.getString(3));
								
								data=data+rsDetL.getInt(1)+"~!~"+rsDetL.getString(2)+"~!~"+rsDetL.getString(3)+"!~!";
								markerAlleles.put(rsDetL.getInt(1)+"!~!"+rsDetL.getString(3), rsDetL.getString(2));
		
								if(!(glist.contains(rsDetL.getInt(1))))
								glist.add(rsDetL.getInt(1))	;					
							}
							
							
							List markerKey = new ArrayList();
							markerKey.addAll(markerAlleles.keySet());
							for(int g=0; g<glist.size(); g++){
								for(int i=0; i<markerKey.size();i++){
									if(!(mapEx.get(Integer.parseInt(glist.get(g).toString()))==null)){
										 marker = (HashMap)mapEx.get(Integer.parseInt(glist.get(g).toString()));
									 }else{
										 marker = new HashMap();
									 }
									 if(Integer.parseInt(glist.get(g).toString())==Integer.parseInt(markerKey.get(i).toString().substring(0, markerKey.get(i).toString().indexOf("!~!")))){
										 try {
											marker.put(markerKey.get(i), markerAlleles.get(markerKey.get(i)));
										} catch (Exception e) {
											marker.put(markerKey.get(i), "");
										}
										 mapEx.put(Integer.parseInt(glist.get(g).toString()),(HashMap)marker);
									 }									
								}	
							}
						}
						if(mapCharCount>0){	
							markerAlleles.clear();
							marker = new HashMap();
							HashMap MappingMap=new HashMap();
							ArrayList glist = new ArrayList();
							rsDet=stCen.executeQuery("SELECT DISTINCT gdms_mapping_pop_values.gid,gdms_mapping_pop_values.map_char_value as data,gdms_marker.marker_name"+
									" FROM gdms_mapping_pop_values,gdms_marker WHERE gdms_mapping_pop_values.marker_id=gdms_marker.marker_id "+
									" AND gdms_mapping_pop_values.gid IN ("+gid+") AND gdms_mapping_pop_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_mapping_pop_values.gid, gdms_marker.marker_name");
							while(rsDet.next()){								
								if(!(mListExp.contains(rsDet.getString(3))))
									mListExp.add(rsDet.getString(3));								
								data=data+rsDet.getInt(1)+"~!~"+rsDet.getString(2)+"~!~"+rsDet.getString(3)+"!~!";
								//list.add(rsDet.getString(2)+","+rsDet.getString(5)+","+rsDet.getString(4));
								markerAlleles.put(rsDet.getInt(1)+"!~!"+rsDet.getString(3), rsDet.getString(2));								
								if(!(glist.contains(rsDet.getInt(1))))
								glist.add(rsDet.getInt(1));								
							}
							rsDetL=stLoc.executeQuery("SELECT DISTINCT gdms_mapping_pop_values.gid,gdms_mapping_pop_values.map_char_value as data,gdms_marker.marker_name"+
									" FROM gdms_mapping_pop_values,gdms_marker WHERE gdms_mapping_pop_values.marker_id=gdms_marker.marker_id "+
									" AND gdms_mapping_pop_values.gid IN ("+gid+") AND gdms_mapping_pop_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_mapping_pop_values.gid, gdms_marker.marker_name");
							while(rsDetL.next()){								
								if(!(mListExp.contains(rsDetL.getString(3))))
									mListExp.add(rsDetL.getString(3));								
								data=data+rsDetL.getInt(1)+"~!~"+rsDetL.getString(2)+"~!~"+rsDetL.getString(3)+"!~!";
								//list.add(rsDet.getString(2)+","+rsDet.getString(5)+","+rsDet.getString(4));
								markerAlleles.put(rsDetL.getInt(1)+"!~!"+rsDetL.getString(3), rsDetL.getString(2));								
								if(!(glist.contains(rsDetL.getInt(1))))
								glist.add(rsDetL.getInt(1));								
							}
							List markerKey = new ArrayList();
							markerKey.addAll(markerAlleles.keySet());
							for(int g=0; g<glist.size(); g++){
								for(int i=0; i<markerKey.size();i++){
									if(!(mapEx.get(Integer.parseInt(glist.get(g).toString()))==null)){
										 marker = (HashMap)mapEx.get(Integer.parseInt(glist.get(g).toString()));
									 }else{
										 marker = new HashMap();
									 }
									 if(Integer.parseInt(glist.get(g).toString())==Integer.parseInt(markerKey.get(i).toString().substring(0, markerKey.get(i).toString().indexOf("!~!")))){
										 try {
											marker.put(markerKey.get(i), markerAlleles.get(markerKey.get(i)));
										} catch (Exception e) {
											marker.put(markerKey.get(i), "");
										}
										 mapEx.put(Integer.parseInt(glist.get(g).toString()),(HashMap)marker);
									 }									
								}	
							}
							rsMT=stCen.executeQuery("SELECT DISTINCT gdms_mapping_pop_values.dataset_id,gdms_mapping_pop.mapping_type,gdms_mapping_pop.parent_a_nid,gdms_mapping_pop.parent_b_nid,gdms_marker.marker_type FROM gdms_mapping_pop_values,gdms_mapping_pop,gdms_marker WHERE gdms_mapping_pop_values.dataset_id=gdms_mapping_pop.dataset_id AND gdms_mapping_pop_values.marker_id=gdms_marker.marker_id  AND gdms_mapping_pop_values.gid IN ("+gid+") AND gdms_mapping_pop_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_mapping_pop_values.gid DESC, gdms_marker.marker_name");
							while(rsMT.next()){
								mapping_type=rsMT.getString(2);
								parents=rsMT.getInt(3)+","+rsMT.getInt(4);
								mType=rsMT.getString(5);
							}
							rsMTL=stLoc.executeQuery("SELECT DISTINCT gdms_mapping_pop_values.dataset_id,gdms_mapping_pop.mapping_type,gdms_mapping_pop.parent_a_nid,gdms_mapping_pop.parent_b_nid,gdms_marker.marker_type FROM gdms_mapping_pop_values,gdms_mapping_pop,gdms_marker WHERE gdms_mapping_pop_values.dataset_id=gdms_mapping_pop.dataset_id AND gdms_mapping_pop_values.marker_id=gdms_marker.marker_id  AND gdms_mapping_pop_values.gid IN ("+gid+") AND gdms_mapping_pop_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_mapping_pop_values.gid DESC, gdms_marker.marker_name");
							while(rsMTL.next()){
								mapping_type=rsMTL.getString(2);
								parents=rsMTL.getInt(3)+","+rsMTL.getInt(4);
								mType=rsMTL.getString(5);
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
								ParentAGID=Integer.parseInt(manager.getGermplasmNameByID(parentAint).getGermplasmId().toString());
								ParentBGID=Integer.parseInt(manager.getGermplasmNameByID(parentBint).getGermplasmId().toString());
							if(parentsExists.equalsIgnoreCase("yes")){
								ArrayList nidsList=new ArrayList();
								if(!(gListExp.contains(ParentAGID)))
									gListExp.add(ParentAGID);
								if(!(gListExp.contains(ParentBGID)))
									gListExp.add(ParentBGID);
								/*rsP=stCen.executeQuery("select nid from gdms_acc_metadataset where gid in("+parents+")");
								ResultSet rsPL=stLoc.executeQuery("select nid from gdms_acc_metadataset where gid in("+parents+")");
								while(rsP.next()){
									nids=nids+rsP.getInt(1)+",";
									nidsList.add(rsP.getInt(1));
								}
								while(rsPL.next()){
									nids=nids+rsPL.getInt(1)+",";
									nidsList.add(rsPL.getInt(1));
								}*/
								//nids=parentAint+","+parentBint;
								nidsList.add(parentAint);
								nidsList.add(parentBint);
								
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
									rsPD=stCen.executeQuery("SELECT DISTINCT gdms_char_values.gid,gdms_char_values.char_value AS DATA,gdms_marker.marker_name FROM gdms_char_values,gdms_marker WHERE gdms_char_values.marker_id=gdms_marker.marker_id  AND gdms_char_values.gid IN ("+parents+") AND gdms_char_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_char_values.gid, gdms_marker.marker_name");
									rsPDL=stLoc.executeQuery("SELECT DISTINCT gdms_char_values.gid,gdms_char_values.char_value AS DATA,gdms_marker.marker_name FROM gdms_char_values,gdms_marker WHERE gdms_char_values.marker_id=gdms_marker.marker_id  AND gdms_char_values.gid IN ("+parents+") AND gdms_char_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_char_values.gid, gdms_marker.marker_name");
								}else if((mType.equalsIgnoreCase("ssr"))||(mType.equalsIgnoreCase("DArT"))){
									rsPD=stCen.executeQuery("SELECT DISTINCT gdms_allele_values.gid,gdms_allele_values.allele_bin_value AS DATA,gdms_marker.marker_name FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.marker_id=gdms_marker.marker_id  AND gdms_allele_values.gid IN ("+parents+") AND gdms_allele_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_allele_values.gid, gdms_marker.marker_name");
									rsPDL=stLoc.executeQuery("SELECT DISTINCT gdms_allele_values.gid,gdms_allele_values.allele_bin_value AS DATA,gdms_marker.marker_name FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.marker_id=gdms_marker.marker_id  AND gdms_allele_values.gid IN ("+parents+") AND gdms_allele_values.marker_id IN (SELECT marker_id FROM gdms_marker WHERE marker_name IN ("+mlist1.substring(0,mlist1.length()-1)+")) ORDER BY gdms_allele_values.gid, gdms_marker.marker_name");
								}
								while(rsPD.next()){
									data1=data1+rsPD.getInt(1)+"~!~"+rsPD.getString(2)+"~!~"+rsPD.getString(3)+"!~!";									
								}
								while(rsPDL.next()){
									data1=data1+rsPDL.getInt(1)+"~!~"+rsPDL.getString(2)+"~!~"+rsPDL.getString(3)+"!~!";									
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
										 parentAData=parentAData+ParentAGID+"~!~"+arrP[1]+"~!~"+arrP[2]+"!~!";
										 	markerPAAlleles.put(ParentAGID+"!~!"+arrP[2], arrP[1]);
											
											if(!(glist.contains(ParentAGID)))
											glist.add(ParentAGID);
											
											markerKey = new ArrayList();
											markerKey.addAll(markerPAAlleles.keySet());
											for(int g=0; g<glist.size(); g++){
												for(int i=0; i<markerKey.size();i++){
													if(!(mapEx.get(Integer.parseInt(glist.get(g).toString()))==null)){
														 marker = (HashMap)mapEx.get(Integer.parseInt(glist.get(g).toString()));
													 }else{
														 marker = new HashMap();
													 }
													 if(Integer.parseInt(glist.get(g).toString())==Integer.parseInt(markerKey.get(i).toString().substring(0, markerKey.get(i).toString().indexOf("!~!")))){
														 try {
															marker.put(markerKey.get(i), markerPAAlleles.get(markerKey.get(i)));
														} catch (Exception e) {
															marker.put(markerKey.get(i), "");
														}
														 mapEx.put(Integer.parseInt(glist.get(g).toString()),(HashMap)marker);
													 }									
												}	
											}
									 }else if(Integer.parseInt(arrP[0])==ParentBGID){									
										 parentBData=parentBData+ParentBGID+"~!~"+arrP[1]+"~!~"+arrP[2]+"!~!";
										 markerPBAlleles.put(ParentBGID+"!~!"+arrP[2], arrP[1]);
											
											if(!(glist.contains(ParentAGID)))
											glist.add(ParentAGID);
											
											 markerKey = new ArrayList();
											markerKey.addAll(markerPBAlleles.keySet());
											for(int g=0; g<glist.size(); g++){
												for(int i=0; i<markerKey.size();i++){
													if(!(mapEx.get(Integer.parseInt(glist.get(g).toString()))==null)){
														 marker = (HashMap)mapEx.get(Integer.parseInt(glist.get(g).toString()));
													 }else{
														 marker = new HashMap();
													 }
													 if(Integer.parseInt(glist.get(g).toString())==Integer.parseInt(markerKey.get(i).toString().substring(0, markerKey.get(i).toString().indexOf("!~!")))){
														 try {
															marker.put(markerKey.get(i), markerPBAlleles.get(markerKey.get(i)));
														} catch (Exception e) {
															marker.put(markerKey.get(i), "");
														}
														 mapEx.put(Integer.parseInt(glist.get(g).toString()),(HashMap)marker);
													 }									
												}	
											}
									 }	
									 
								}
								
								data=parentAData+parentBData;
								
							}
							session.setAttribute("mappingType", mapping_type);
							datasetType="mapping";
						}
					///	System.out.println(">>>>>>>>>>>>>>>>>>   :"+mapEx);
						
						
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
								gListExp1.put(names.getNval(), names.getGermplasmId());
							}else{
								if(!(gListExp.contains(names.getGermplasmId())))
									gListExp.add(names.getGermplasmId());
								//gListExp1.put(names.getGermplasmId(), names.getNval());
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
						//System.out.println(",,,,"+exportOpType);
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
					//ArrayList markersInMap=new ArrayList();
					//System.out.println(".....................List="+gListExp);
					if(format.contains("Flapjack")){						
						 rsMC=stCen.executeQuery("select gdms_markers_onmap.marker_id from gdms_markers_onmap, gdms_map where gdms_map.map_name='"+map+"'");
						    while(rsMC.next()){
						    	markerIDsList.add(rsMC.getInt(1));
						    	markerIDs=markerIDs+rsMC.getInt(1)+",";
						    }
						    rsML=stLoc.executeQuery("select gdms_markers_onmap.marker_id from gdms_markers_onmap, gdms_map where gdms_map.map_name='"+map+"'");
						    while(rsML.next()){
						    	if(!markerIDsList.contains(rsML.getInt(1))){
						    		markerIDsList.add(rsML.getInt(1));
						    		markerIDs=markerIDs+rsML.getInt(1)+",";
						    	}				    	
						    }
						   // markerIDsList
						    rsM=stCen.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in("+markerIDs.substring(0, markerIDs.length()-1)+")");
						    while(rsM.next()){
						    	markerIdList.add(rsM.getString(2));
						    	marNamesMap.put(rsM.getInt(1), rsM.getString(2));
						    }
						    rsM1=stLoc.executeQuery("select marker_id, marker_name from gdms_marker where marker_id in("+markerIDs.substring(0, markerIDs.length()-1)+")");
						    while(rsM1.next()){
						    	if(!markerIdList.contains(rsM1.getString(2))){
						    		markerIdList.add(rsM1.getString(2));
						    		marNamesMap.put(rsM1.getInt(1), rsM1.getString(2));
						    	}
						    	
						    }
						
						
						rs=stCen.executeQuery("SELECT marker_id, linkage_group, start_position from gdms_markers_onmap, gdms_map where gdms_map.map_name  ='"+map+"' ORDER BY linkage_group, start_position , marker_id");
						rsL=stLoc.executeQuery("SELECT marker_id, linkage_group, start_position FROM gdms_markers_onmap, gdms_map where gdms_map.map_name ='"+map+"' ORDER BY linkage_group, start_position , marker_id");
						while(rs.next()){
							//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
							mapData=mapData+marNamesMap.get(rs.getInt(1)).toString()+"!~!"+rs.getString(2)+"!~!"+rs.getFloat(3)+"~~!!~~";
							if(!markersInMap.contains(marNamesMap.get(rs.getInt(1))))
								markersInMap.add(marNamesMap.get(rs.getInt(1)));
						}
						while(rsL.next()){
							//System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+rs.getFloat(3));
							mapData=mapData+marNamesMap.get(rsL.getInt(1)).toString()+"!~!"+rsL.getString(2)+"!~!"+rsL.getFloat(3)+"~~!!~~";
							if(!markersInMap.contains(marNamesMap.get(rsL.getInt(1))))
								markersInMap.add(marNamesMap.get(rsL.getInt(1)));
						}
						for(int m=0; m<mListExp.size();m++){
							if(!markersInMap.contains(mListExp.get(m))){
								mapData=mapData+mListExp.get(m)+"!~!"+"unmapped"+"!~!"+"0"+"~~!!~~";
							}
						}
						rsMap=stCen.executeQuery("select qtl_id, tid from gdms_qtl_details where map_id =(select map_id from gdms_map where map_name ='"+map+"')");
						rsMapL=stLoc.executeQuery("select qtl_id, tid from gdms_qtl_details where map_id =(select map_id from gdms_map where map_name ='"+map+"')");
						//rsQ=stQ.executeQuery("");
						while(rsMap.next()){
							//System.out.println("..............:"+rsMap.getInt(1));
							qtlCount++;
							qtl_id=qtl_id+rsMap.getInt(1)+",";
							if(!tid.contains(rsMap.getInt(2)))
								tid.add(rsMap.getInt(2));
						}
						while(rsMapL.next()){
							//System.out.println("..............:"+rsMap.getInt(1));
							qtlCount++;
							qtl_id=qtl_id+rsMapL.getInt(1)+",";
							if(!tid.contains(rsMapL.getInt(2)))
								tid.add(rsMapL.getInt(2));
						}
						if(qtlCount>0){
							qtlExists=true;
							/*rsC=stCen.executeQuery("select distinct trabbr, tid, traitgroup from tmstraits");
							while(rsC.next()){							
								tidsList.add(rsC.getInt(2));
									traitsMap.put(rsC.getInt(2), rsC.getString(1));
								
							}
							rsN=stLoc.executeQuery("select distinct trabbr, tid, traitgroup from tmstraits");
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
							
							
							//System.out.println("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id");
							rsQ=stCen.executeQuery("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id");
							while(rsQ.next()){
								String Fmarkers=rsQ.getString(12)+"/"+rsQ.getString(13);
								qtlData.add(rsQ.getString(22)+"!~!"+rsQ.getString(10)+"!~!"+rsQ.getFloat(14)+"!~!"+rsQ.getFloat(3)+"!~!"+rsQ.getFloat(4)+"!~!"+traitsMap.get(rsQ.getInt(5)).toString()+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getString(6)+"!~!"+rsQ.getFloat(8)+"!~!"+rsQ.getFloat(9)+"!~!"+rsQ.getString(6)+"!~!"+Fmarkers+"!~!"+rsQ.getString(7)+"!~!"+rsQ.getString(16)+"!~!"+rsQ.getString(17)+"!~!"+rsQ.getString(18)+"!~!"+rsQ.getString(19)+"!~!"+rsQ.getString(20));
							}
							ResultSet rsQL=stLoc.executeQuery("select * from gdms_qtl_details, gdms_qtl where gdms_qtl_details.qtl_id in ("+qtl_id.substring(0, qtl_id.length()-1)+") and gdms_qtl.qtl_id=gdms_qtl_details.qtl_id");
							while(rsQL.next()){
								String Fmarkers=rsQL.getString(12)+"/"+rsQL.getString(13);
								qtlData.add(rsQL.getString(22)+"!~!"+rsQL.getString(10)+"!~!"+rsQL.getFloat(14)+"!~!"+rsQL.getFloat(3)+"!~!"+rsQL.getFloat(4)+"!~!"+traitsMap.get(rsQL.getInt(5)).toString()+"!~!"+rsQL.getString(6)+"!~!"+rsQL.getString(6)+"!~!"+rsQL.getFloat(8)+"!~!"+rsQL.getFloat(9)+"!~!"+rsQL.getString(6)+"!~!"+Fmarkers+"!~!"+rsQL.getString(7)+"!~!"+rsQL.getString(16)+"!~!"+rsQL.getString(17)+"!~!"+rsQL.getString(18)+"!~!"+rsQL.getString(19)+"!~!"+rsQL.getString(20));
							
							}
							//System.out.println("qtlData="+qtlData);
						}else
							qtlExists=false;
						session.setAttribute("qtlExistsSes", qtlExists);
						
					}			
				}
				/*System.out.println("List="+list);*/
				/*System.out.println(" gListExp="+ gListExp);
				System.out.println(" mListExp="+ mListExp);*/
				//To write matrix  if datatype is character 
				if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("SNP"))){
					//System.out.println(".........snp .............");
					ef.MatrixDataSNP(pathWB, req, gListExp, mListExp, mapN, mapEx);
					ef.MatrixDataSNP(filePath, req, gListExp, mListExp, mapN, mapEx);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("mapping"))){
					//System.out.println(".........MAPPING .............");
					ef.Matrix(list, pathWB, req, gListExp, mListExp, mapN);		
					ef.Matrix(list, filePath, req, gListExp, mListExp, mapN);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("SSR"))){
					ef.MatrixDataSNP(pathWB, req, gListExp, mListExp, mapN, mapEx);
					ef.MatrixDataSNP(filePath, req, gListExp, mListExp, mapN, mapEx);
					//ef.Matrix(list, filePath, req, gListExp, mListExp);
					//ef.Matrix(list, filePath, req);
				}else if((format.contains("Genotyping X Marker Matrix"))&&(datasetType.equalsIgnoreCase("DArT"))){
					//System.out.println(".........daRt .............");
					ef.Matrix(list, pathWB, req, gListExp, mListExp, mapN);
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
					ef.MatrixDat(list, mapData, FlapjackPath, req, gListExp, mListExp, qtlData, exportOpType, qtlExists, mapEx, gListExp1);
					
					session.setAttribute("FlapjackPath", FlapjackPath);
					//ef.MatrixDat(list, mapData, filePath, req, gListExp, mListExp);
				
				}	
			}
			/*if(rsPDL!=null) rsPDL.close();if(rsMTL!=null) rsMTL.close(); if(rsDetL!=null) rsDetL.close(); if(rscL!=null) rscL.close(); if(rsc!=null) rsc.close();if(rsaL!=null) rsaL.close();if(rsa!=null) rsa.close(); if(rs2L!=null) rs2L.close();
			if(rs1L!=null) rs1L.close(); if(rs2C!=null) rs2C.close();		
			if(rs!=null) rs.close(); if(rs1!=null) rs1.close(); if(rsG!=null) rsG.close(); if(rsM!=null) rsM.close(); if(rs2!=null) rs2.close(); if(rsD!=null) rsD.close(); if(rsN!=null) rsN.close();  if(rsL!=null) rsL.close();
			if(rsMT!=null) rsMT.close(); if(rsPD!=null) rsPD.close(); if(rsP!=null) rsP.close(); if(rs3!=null) rs3.close(); if(rsQ!=null) rsQ.close(); if(rsP1!=null) rsP1.close(); if(rsP2!=null) rsP2.close();
			if(rsMap!=null) rsMap.close(); if(rsMapL!=null) rsMapL.close();
			if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close(); if(stLC!=null) stLC.close(); if(stmt!=null) stmt.close(); if(stmt1!=null) stmt1.close(); if(stmt2!=null) stmt2.close(); if(stmtM!=null) stmtM.close();
			if(st!=null) st.close(); if(stmtG!=null) stmtG.close(); 	if(stmtN!=null) stmtN.close(); 	if(stmtMT!=null) stmtMT.close(); if(stmtPD!=null) stmtPD.close(); if(stmtP!=null) stmtP.close();
			if(stP!=null) stP.close(); if(stQ!=null) stQ.close(); if(stP1!=null) stP1.close(); if(stP2!=null) stP2.close();
			
			*/
			con.close(); conn.close();
	}catch(Exception e){
		e.printStackTrace();
	}finally{
	      try{		      		
	      		if(con!=null) con.close();conn.close();
	      		factory.close(); 
	         }catch(Exception e){System.out.println(e);}
		}
		return am.findForward("exp");
	}
	private static void addValues(int key, String value){
		ArrayList<String> tempList = null;
		if(hashMap.containsKey(key)){
			tempList=hashMap.get(key);
			if(tempList == null)
				tempList = new ArrayList<String>();
			tempList.add(value);
		}else{
			tempList = new ArrayList();
			tempList.add(value);
		}
		hashMap.put(key,tempList);
	}

}
