package org.icrisat.gdms.retrieve;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;
import org.generationcp.middleware.util.HibernateUtil;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.manager.api.OntologyDataManager;
import org.icrisat.gdms.common.FileUploadToServer;
public class DataRetrieveAction extends Action{
	static Map<Integer, ArrayList<String>> hashMap = new HashMap<Integer,  ArrayList<String>>();  
	Connection con;
	Connection conn;
	private static WorkbenchDataManager wdm;
	private static HibernateUtil hibernateUtil;
	ArrayList<Integer> tid=new ArrayList();
	String str="";
	String strVal="";
	String str1="";
	String type="";
	ArrayList  markerList=new ArrayList();
	ArrayList genotypesList=new ArrayList();
	ArrayList mapList=new ArrayList();
	ArrayList mList=new ArrayList();
	String gids="";
	String upRes="";
	String chValues="";
	ResultSet rsDet=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		ActionErrors ae = new ActionErrors();	
		if(session!=null){
			session.removeAttribute("strdata");			
		}
		// TODO Auto-generated method stub
		
		//String crop=req.getSession().getAttribute("crop").toString();
		
		DynaActionForm df = (DynaActionForm) af;
		
		String retrieveOP=(String)df.get("retrieveOP");	
		//System.out.println(".................................:"+retrieveOP);
		float distance=0;
		int mCount=0;
	
		ManagerFactory factory = null;
		
		int alleleCount=0;
		int charCount=0;
		int mapCharCount=0;
		int cenAlleleCount=0;
		int cenCharCount=0;
		int cenMapCharCount=0;
		int locAlleleCount=0;
		int locCharCount=0;
		int locMapCharCount=0;
		try{
			
			Properties p=new Properties();
			
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
			
			ResultSet rs2=null;
			ResultSet rsM=null;
			ResultSet rsMaps=null;
			ResultSet rsC=null;
			ResultSet rsL=null;
			ResultSet rs=null;
			ResultSet rs1=null;
			ResultSet rsMp=null;			
			ResultSet rsN=null;
			ResultSet rsMtas=null;
			ResultSet rsDetL=null;
			ResultSet rsDet=null;
			ResultSet rsML=null;
			ResultSet rsMpL=null;
			ResultSet rsMapsL=null;
			ResultSet rs1C=null;
			ResultSet rsMtasC=null;
			ResultSet rsQC=null;
			
			Statement st=con.createStatement();
			
			Statement stmtN=con.createStatement();
			Statement stAC=conn.createStatement();
			Statement stAL=con.createStatement();
			Statement stCC=conn.createStatement();
			Statement stCL=con.createStatement();
			Statement stML=con.createStatement();
			Statement stMC=conn.createStatement();
			
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			OntologyDataManager om=factory.getNewOntologyDataManager();
			
			/*DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters("DatabaseConfig.properties", "workbench");
	        hibernateUtil = new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), 
	                                workbenchDb.getUsername(), workbenchDb.getPassword());
	        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
	        wdm = new WorkbenchDataManagerImpl(sessionProvider);
	       // final WorkbenchDataManager workbenchDataManager=null;	
	        //  WorkbenchSetting workbenchSetting = wdm.getWorkbenchSetting();
           
               // System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%   :"+workbenchSetting.getInstallationDirectory());
           System.out.println("......................:"+session.getServletContext().getRealPath("//"));
           String path="";
           //String bPath=session.getServletContext().getRealPath("//");
           String bPath="C:\\IBWorkflowSystem\\infrastructure\\tomcat\\webapps\\GDMS";
           //String strTest2  = strTest1.substring(0,strTest1.lastIndexOf("::"));
          // name.substring(0,name.indexOf(","));  
           String op=bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1);
          
           System.out.println(",,,,,,,,,,,,,  :"+bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1));
           HashMap<Object, String> IBWFProjects= new HashMap<Object, String>();
	        List<Project> projects = wdm.getProjects();
	        Long projectId = Long.valueOf(0);
	        System.out.println("testGetProjects(): ");
	        for (Project project : projects) {
	            System.out.println("  " + project.getLocalDbName());
	            projectId = project.getProjectId();
	            IBWFProjects.put(project.getLocalDbName(),project.getProjectId()+"_"+project.getProjectName());
	        }
	        System.out.println(".........:"+IBWFProjects.get(dbNameL));
	        path=op+"/IBWorkflowSystem/workspace/"+IBWFProjects.get(dbNameL)+"/gdms/output";
	        System.out.println("**************  :"+path);
	        session.setAttribute("OutputPath", path);*/
			//System.out.println("..........installation directory........  :"+workbenchSetting.getInstallationDirectory());
			if(retrieveOP.equalsIgnoreCase("first")){
				if(session!=null){
					session.removeAttribute("indErrMsg");	
				}
				
				str="qtlPage";
			}else if(retrieveOP.equalsIgnoreCase("Submit")){
				/** for retrieving polymorphic markers between 2 lines **/
				if(session!=null){
					session.removeAttribute("MissingData");		
					session.removeAttribute("map_data");	
					session.removeAttribute("recCount");	
					session.removeAttribute("missingCount");	
				}
				String gids="";
				String gids1="";
				chValues="";
				ArrayList finalList=new ArrayList();
				ArrayList missingList=new ArrayList();
				ArrayList SSRfinalList=new ArrayList();
				ArrayList SSRmissingList=new ArrayList();
				ArrayList SNPfinalList=new ArrayList();
				ArrayList SNPmissingList=new ArrayList();
				String[] str1=null;
				ArrayList geno1=new ArrayList();
				ArrayList geno2=new ArrayList();
				ArrayList mark1=new ArrayList();
				ArrayList mark2=new ArrayList();
				ArrayList ch1=new ArrayList();
				ArrayList ch2=new ArrayList();				
				
				
				String markers="";
				int markerCount=0;
				
				String recCount="";
				String mcount="";
				
				List<String> linesList=new ArrayList<String>();
				String line1=(String)df.get("linesO");
				String line2=(String)df.get("linesT");
				String selLines="'"+line1+"' & '"+line2+"'";
				strVal="'"+line1+"','"+line2+"'";	
				linesList.add(line1);
				linesList.add(line2);
				session.setAttribute("selLines", selLines);
				
				
				/**
				 * Query for retrieving the gid of the respective germplasm_name using middleware
				 */			
				List<GidNidElement> results = manager.getGidAndNidByGermplasmNames(linesList);
				//System.out.println("RESULTS (getGidAndNidByGermplasmNames): " + results);
				for(int r=0;r<results.size();r++){
					gids1=gids1+results.get(r).getGermplasmId()+",";					
				}
				
				String nid="";
				ArrayList<Integer> nidList=new ArrayList<Integer>();
				//System.out.println("select distinct nid from gdms_acc_metadataset where gid in ("+gids1.substring(0, gids1.length()-1)+")");
				rsN=stCen.executeQuery("select distinct nid from gdms_acc_metadataset where gid in ("+gids1.substring(0, gids1.length()-1)+")");
				while(rsN.next()){
					nid=nid+rsN.getString(1)+",";
					nidList.add(rsN.getInt(1));
				}
				ResultSet rsNL=stLoc.executeQuery("select distinct nid from gdms_acc_metadataset where gid in ("+gids1.substring(0, gids1.length()-1)+")");
				while(rsNL.next()){
					nid=nid+rsNL.getString(1)+",";
					nidList.add(rsNL.getInt(1));
				}
				/** 
				 * implementing middleware jar file 
				 */
				
				
				Name names = null;
				//System.out.println(",,,,,,,,,,,,,,,,,,,,  :"+nidList);
				for(int n=0;n<nidList.size();n++){
					names=manager.getGermplasmNameByID(Integer.parseInt(nidList.get(n).toString()));
					gids=gids+names.getGermplasmId()+",";
				}
				
				gids = gids.substring(0,gids.length()-1);
				String[] gidsO=gids.split(",");
				int gid1=Integer.parseInt(gidsO[0]);
				int gid2=Integer.parseInt(gidsO[1]);
				
				String polyType=session.getAttribute("polyType").toString();
				//System.out.println("....:"+polyType);
				
				if(polyType.equalsIgnoreCase("fingerprinting")){				
					/** checking whether the gid exists in 'allele_values' table **/					
					ResultSet rsa=stCen.executeQuery("select count(*) from gdms_allele_values where gid in ("+gids+")");
					while (rsa.next()){
						cenAlleleCount=rsa.getInt(1);						
					}
					ResultSet rsaL=stLoc.executeQuery("select count(*) from gdms_allele_values where gid in ("+gids+")");
					while (rsaL.next()){
						locAlleleCount=rsaL.getInt(1);					
					}
					if(locAlleleCount>0)
						alleleCount=locAlleleCount;
					else if(cenAlleleCount>0)
						alleleCount=cenAlleleCount;
					else if(locAlleleCount>0 && cenAlleleCount > 0)
						alleleCount=cenAlleleCount;
					else
						alleleCount=0;
					//System.out.println("alleleCount="+alleleCount);
					/** checking whether the gid exists in 'char_values' table **/
					ResultSet rsc=stCen.executeQuery("select count(*) from gdms_char_values where gid in("+gids+")");
					while(rsc.next()){
						cenCharCount=rsc.getInt(1);							
					}
					ResultSet rscL=stLoc.executeQuery("select count(*) from gdms_char_values where gid in("+gids+")");
					while(rscL.next()){
						locCharCount=rscL.getInt(1);	
					}
					if(locCharCount>0)
						charCount=locCharCount;
					else if(cenCharCount>0)
						charCount=cenCharCount;
					else if(locCharCount>0 && cenCharCount > 0)
						charCount=cenCharCount;
					else
						charCount=0;
					//System.out.println("charCount="+charCount);
				}
				ArrayList mList1=new ArrayList();
				ArrayList mList2=new ArrayList();
				ArrayList mFinalList=new ArrayList();
				ArrayList SNPList=new ArrayList();
				ArrayList SSRList=new ArrayList();
				ArrayList chVal=new ArrayList();
				String datatype="";
				String[] dType=null;
				if(polyType.equalsIgnoreCase("fingerprinting")){
				
					/** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
					if(alleleCount>0){
						//ResultSet rsDet=stmtA.executeQuery("SELECT allele_values.dataset_id,allele_values.gid,germplasm_temp.germplasm_name,marker.species,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,germplasm_temp,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=germplasm_temp.gid AND marker.marker_id = allele_values.marker_id ORDER BY germplasm_name, marker_name");
						//System.out.println("SELECT allele_values.dataset_id,allele_values.gid,names.nval,marker.crop,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,names,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
						//System.out.println("SELECT gdms_allele_values.dataset_id,gdms_allele_values.gid,gdms_marker.marker_name,gdms_allele_values.allele_bin_value as data FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER BY gid, marker_name");
						rsDet=stCen.executeQuery("SELECT gdms_allele_values.dataset_id,gdms_allele_values.gid,gdms_marker.marker_name,gdms_allele_values.allele_bin_value as data FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER BY gid, marker_name");
						
						while(rsDet.next()){
							//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
							SSRList.add(rsDet.getInt(2)+"!~!"+rsDet.getString(3)+"!~!"+rsDet.getString(4));
							addValues(rsDet.getInt(2), rsDet.getString(3));	
						}
						rsDetL=stLoc.executeQuery("SELECT gdms_allele_values.dataset_id,gdms_allele_values.gid,gdms_marker.marker_name,gdms_allele_values.allele_bin_value as data FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER BY gid, marker_name");
						while(rsDetL.next()){
							//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
							SSRList.add(rsDetL.getInt(2)+"!~!"+rsDetL.getString(3)+"!~!"+rsDetL.getString(4));
							addValues(rsDetL.getInt(2), rsDetL.getString(3));	
						}
						
						
						datatype=datatype+"SSR"+"~~!!~~";
					}
					/** if gids exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
					if(charCount>0){
						//ResultSet rsDet=stmtC.executeQuery("SELECT distinct char_values.dataset_id,char_values.gid,germplasm_temp.germplasm_name,marker.species,char_values.marker_id,marker.marker_name,char_values.char_value as data FROM char_values,germplasm_temp,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=germplasm_temp.gid AND marker.marker_id = char_values.marker_id ORDER BY germplasm_name, marker_name");
						//System.out.println("SELECT distinct char_values.dataset_id,char_values.gid,names.nval,marker.crop,char_values.marker_id,marker.marker_name,char_values.char_value FROM char_values,names,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=names.gid AND marker.marker_id = char_values.marker_id ORDER BY nval, marker_name");
						//System.out.println("SELECT distinct gdms_char_values.dataset_id,gdms_char_values.gid,gdms_marker.marker_name,gdms_char_values.char_value FROM gdms_char_values,gdms_marker WHERE gdms_char_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_char_values.marker_id ORDER BY gid, marker_name");
						rsDet=stCen.executeQuery("SELECT distinct gdms_char_values.dataset_id,gdms_char_values.gid,gdms_marker.marker_name,gdms_char_values.char_value FROM gdms_char_values,gdms_marker WHERE gdms_char_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_char_values.marker_id ORDER BY gid, marker_name");
						while(rsDet.next()){
							//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
							SNPList.add(rsDet.getInt(2)+"!~!"+rsDet.getString(3)+"!~!"+rsDet.getString(4));
							addValues(rsDet.getInt(2), rsDet.getString(3));	
						}
						rsDetL=stLoc.executeQuery("SELECT distinct gdms_char_values.dataset_id,gdms_char_values.gid,gdms_marker.marker_name,gdms_char_values.char_value FROM gdms_char_values,gdms_marker WHERE gdms_char_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_char_values.marker_id ORDER BY gid, marker_name");
						while(rsDetL.next()){
							//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
							SNPList.add(rsDetL.getInt(2)+"!~!"+rsDetL.getString(3)+"!~!"+rsDetL.getString(4));
							addValues(rsDetL.getInt(2), rsDetL.getString(3));	
						}
						
						datatype=datatype+"SNP"+"~~!!~~";
					}
					dType=datatype.split("~~!!~~");
					//System.out.println("dType=:"+dType.length);
					session.setAttribute("dataTypes", dType.length);
				}else if(polyType.equalsIgnoreCase("mapping")){
					/** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
					
					//ResultSet rsDet=stmtM.executeQuery("SELECT mapping_pop_values.dataset_id,mapping_pop_values.gid,germplasm_temp.germplasm_name,marker.species,mapping_pop_values.marker_id,marker.marker_name,mapping_pop_values.map_char_value as data FROM mapping_pop_values,germplasm_temp,marker WHERE mapping_pop_values.gid in ("+gids+") AND mapping_pop_values.gid=germplasm_temp.gid AND marker.marker_id = mapping_pop_values.marker_id ORDER BY germplasm_name, marker_name");
					//System.out.println("SELECT allele_values.dataset_id,allele_values.gid,names.nval,marker.crop,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,names,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
					//System.out.println("SELECT gdms_mapping_pop_values.dataset_id,gdms_mapping_pop_values.gid,gdms_marker.marker_name,gdms_mapping_pop_values.map_char_value as data FROM gdms_mapping_pop_values,gdms_marker WHERE gdms_mapping_pop_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_mapping_pop_values.marker_id ORDER BY gid, marker_name");
					rsDet=stCen.executeQuery("SELECT gdms_mapping_pop_values.dataset_id,gdms_mapping_pop_values.gid,gdms_marker.marker_name,gdms_mapping_pop_values.map_char_value as data FROM gdms_mapping_pop_values,gdms_marker WHERE gdms_mapping_pop_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_mapping_pop_values.marker_id ORDER BY gid, marker_name");
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						chVal.add(rsDet.getInt(2)+"!~!"+rsDet.getString(3)+"!~!"+rsDet.getString(4));
						addValues(rsDet.getInt(2), rsDet.getString(3));	
					}
					rsDetL=stLoc.executeQuery("SELECT gdms_mapping_pop_values.dataset_id,gdms_mapping_pop_values.gid,gdms_marker.marker_name,gdms_mapping_pop_values.map_char_value as data FROM gdms_mapping_pop_values,gdms_marker WHERE gdms_mapping_pop_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_mapping_pop_values.marker_id ORDER BY gid, marker_name");
					while(rsDetL.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						chVal.add(rsDetL.getInt(2)+"!~!"+rsDetL.getString(3)+"!~!"+rsDetL.getString(4));
						addValues(rsDetL.getInt(2), rsDetL.getString(3));	
					}
					int dTypeM=1;
					session.setAttribute("dataTypes", dTypeM);
				}
				
				
				
				/*System.out.println(".........SNPList="+SNPList);
				System.out.println(".........SSRList="+SSRList);*/
				// String[] chVal=chValues.split("~!!~");
				mList1=hashMap.get(gid1);
				mList2=hashMap.get(gid2);
				/*System.out.println("mList1=:"+mList1);
				System.out.println("mList2=:"+mList2);*/
				//System.out.println("copmmon Markers="+mFinalList);
				 int s=0;
				 geno1.clear();geno2.clear();mark1.clear(); mark2.clear();ch1.clear();ch2.clear();
				 session.setAttribute("data", polyType);
				 if(polyType.equalsIgnoreCase("mapping")){
					 if((hashMap.get(gid1).size())>(hashMap.get(gid2).size())){					
							for(int ml=0; ml<mList1.size();ml++){
								if(mList2.contains(mList1.get(ml))){
									mFinalList.add(mList1.get(ml));
								}
							}					
						}else if((hashMap.get(gid1).size())<(hashMap.get(gid2).size())){
							for(int ml=0; ml<mList2.size();ml++){
								if(mList1.contains(mList2.get(ml))){
									mFinalList.add(mList2.get(ml));
								}
							}
						}else if((hashMap.get(gid1).size())==(hashMap.get(gid2).size())){
							for(int ml=0; ml<mList2.size();ml++){
								if(mList1.contains(mList2.get(ml))){
									mFinalList.add(mList2.get(ml));
								}
							}
						}
					 geno1.clear();geno2.clear();mark1.clear(); mark2.clear();ch1.clear();ch2.clear();
					 for(int c=0;c<chVal.size();c++){	
						 ///System.out.println(".............:"+chVal.get(c));
						 String arr[]=new String[3];
							StringTokenizer stz = new StringTokenizer(chVal.get(c).toString(), "!~!");
				    		//arrList6 = new String[stz.countTokens()];
				    		int i1=0;				  
				    		while(stz.hasMoreTokens()){				    			
				    			arr[i1] = stz.nextToken();
				    			i1++;
				    		}
						//str1=chVal.get(c).toString().split("!~!");
						if((Integer.parseInt(arr[0])==(gid1))&&(mFinalList.contains(arr[1]))){
							geno1.add(arr[0]);
							mark1.add(arr[1]);
							ch1.add(arr[2]);					
						}else if((Integer.parseInt(arr[0])==(gid2))&&(mFinalList.contains(arr[1]))){
							geno2.add(arr[0]);
							mark2.add(arr[1]);
							ch2.add(arr[2]);
						}			
					}
					 finalList=new ArrayList();
						missingList=new ArrayList();
						
						//System.out.println(mark1);
						for(int k=0;k<geno1.size();k++){
							if((!(ch2.get(k).equals("N")||ch2.get(k).equals("?")||ch2.get(k).equals("-")))&&(!(ch1.get(k).equals("N")||ch1.get(k).equals("?")||ch1.get(k).equals("-")))&&(!(ch1.get(k).equals(ch2.get(k))))){
								if(!finalList.contains(mark1.get(k))){
									finalList.add(mark1.get(k));
									markers=markers+"'"+mark1.get(k)+"',";	
									markerCount++;
								}
							}
							if((ch1.get(k).equals("?"))||(ch2.get(k).equals("?"))||(ch1.get(k).equals("N"))||(ch2.get(k).equals("N"))||(ch1.get(k).equals("-"))||(ch2.get(k).equals("-"))){
								if(!missingList.contains(mark1.get(k))){
									missingList.add(mark1.get(k));
								}
							}
						}
						recCount=finalList.size()+"";
						mcount=missingList.size()+"";
						req.getSession().setAttribute("MissingData", missingList);
						//req.getSession().setAttribute("map_data", map_data);
						req.getSession().setAttribute("recCount",recCount);
						req.getSession().setAttribute("missingCount",mcount);
						req.getSession().setAttribute("result", finalList);
				 }else if(polyType.equalsIgnoreCase("fingerprinting")){
					 missingList=new ArrayList();
					 SSRfinalList=new ArrayList();
						SSRmissingList=new ArrayList();
						String ssrRecCount="";
						//System.out.println("SSRList size=:"+SSRList.size()+"  "+SNPList.size());
					 if(SSRList.size()>0){
						 if((hashMap.get(gid1).size())>(hashMap.get(gid2).size())){					
								for(int ml=0; ml<mList1.size();ml++){
									if(mList2.contains(mList1.get(ml))){
										mFinalList.add(mList1.get(ml));
									}
								}					
							}else if((hashMap.get(gid1).size())<(hashMap.get(gid2).size())){
								for(int ml=0; ml<mList2.size();ml++){
									if(mList1.contains(mList2.get(ml))){
										mFinalList.add(mList2.get(ml));
									}
								}
							}else if((hashMap.get(gid1).size())==(hashMap.get(gid2).size())){
								for(int ml=0; ml<mList2.size();ml++){
									if(mList1.contains(mList2.get(ml))){
										mFinalList.add(mList2.get(ml));
									}
								}
							}
						 session.setAttribute("dataType", "SSR");
						 geno1.clear();geno2.clear();mark1.clear(); mark2.clear();ch1.clear();ch2.clear();
						 for(int c=0;c<SSRList.size();c++){	
							 ///System.out.println(".............:"+chVal.get(c));
							 String arr[]=new String[3];
								StringTokenizer stz = new StringTokenizer(SSRList.get(c).toString(), "!~!");
					    		//arrList6 = new String[stz.countTokens()];
					    		int i1=0;				  
					    		while(stz.hasMoreTokens()){				    			
					    			arr[i1] = stz.nextToken();
					    			i1++;
					    		}
							//str1=chVal.get(c).toString().split("!~!");
							if((Integer.parseInt(arr[0])==(gid1))&&(mFinalList.contains(arr[1]))){
								geno1.add(arr[0]);
								mark1.add(arr[1]);
								ch1.add(arr[2]);					
							}else if((Integer.parseInt(arr[0])==(gid2))&&(mFinalList.contains(arr[1]))){
								geno2.add(arr[0]);
								mark2.add(arr[1]);
								ch2.add(arr[2]);
							}			
						}
						
						//System.out.println(mark1);
						for(int k=0;k<geno1.size();k++){
							if((!(ch2.get(k).equals("0/0")||ch2.get(k).equals("0:0")||ch2.get(k).equals("N")||ch2.get(k).equals("?")||ch2.get(k).equals("-")))&&(!(ch1.get(k).equals("0:0")||ch1.get(k).equals("0/0")||ch1.get(k).equals("N")||ch1.get(k).equals("?")||ch1.get(k).equals("-")))&&(!(ch1.get(k).equals(ch2.get(k))))){
								if(!SSRfinalList.contains(mark1.get(k))){
									SSRfinalList.add(mark1.get(k));
									markers=markers+"'"+mark1.get(k)+"',";	
									markerCount++;
								}
							}
							if((ch1.get(k).equals("0/0"))||(ch2.get(k).equals("0/0"))||(ch1.get(k).equals("0:0"))||(ch2.get(k).equals("0:0"))||(ch1.get(k).equals("?"))||(ch2.get(k).equals("?"))||(ch1.get(k).equals("N"))||(ch2.get(k).equals("N"))||(ch1.get(k).equals("-"))||(ch2.get(k).equals("-"))){
								if(!missingList.contains(mark1.get(k))){
									missingList.add(mark1.get(k));
								}
							}
						}
						//System.out.println("SSRfinalList="+SSRfinalList);
						//String ssrMissingcount=missingList.size()+"";
						if(dType.length==2){
							ssrRecCount=SSRfinalList.size()+"";
							req.getSession().setAttribute("ssrMissingData", missingList);
							req.getSession().setAttribute("ssrResult", SSRfinalList);
							req.getSession().setAttribute("ssrRecCount", ssrRecCount);
						}else{
							req.getSession().setAttribute("MissingData", missingList);
							req.getSession().setAttribute("result", SSRfinalList);
							req.getSession().setAttribute("recCount", SSRfinalList.size());
						}
						//req.getSession().setAttribute("ssrMissingcount", ssrMissingcount);
					 }
					 if(SNPList.size()>0){
						 if((hashMap.get(gid1).size())>(hashMap.get(gid2).size())){
							 //System.out.println("gid1 > gid2");
								for(int ml=0; ml<mList1.size();ml++){
									if(mList2.contains(mList1.get(ml))){
										mFinalList.add(mList1.get(ml));
									}
								}					
							}else if((hashMap.get(gid1).size())<(hashMap.get(gid2).size())){
								//System.out.println("gid1 < gid2");
								for(int ml=0; ml<mList2.size();ml++){
									if(mList1.contains(mList2.get(ml))){
										mFinalList.add(mList2.get(ml));
									}
								}
							}else if((hashMap.get(gid1).size())==(hashMap.get(gid2).size())){
								//System.out.println("gid1 = gid2");
								for(int ml=0; ml<mList2.size();ml++){
									if(mList1.contains(mList2.get(ml))){
										mFinalList.add(mList2.get(ml));
									}
								}
							}
						 /*System.out.println(">>>>>>>>>>>>>>SNPList > 0 "+SNPList);
						 System.out.println("mFinalList=:"+mFinalList);*/
						 session.setAttribute("dataType", "SNP");
						 geno1.clear();geno2.clear();mark1.clear(); mark2.clear();ch1.clear();ch2.clear();
						 for(int c=0;c<SNPList.size();c++){	
							 ///System.out.println(".............:"+chVal.get(c));
							 String arr[]=new String[3];
								StringTokenizer stz = new StringTokenizer(SNPList.get(c).toString(), "!~!");
					    		//arrList6 = new String[stz.countTokens()];
					    		int i1=0;				  
					    		while(stz.hasMoreTokens()){				    			
					    			arr[i1] = stz.nextToken();
					    			i1++;
					    		}
					    		//System.out.println(arr[0]+"=="+gid1+" && "+mFinalList.contains(arr[1])+"  "+arr[1]);
					    		
							if((Integer.parseInt(arr[0])==(gid1))&&(mFinalList.contains(arr[1]))){
								geno1.add(arr[0]);
								mark1.add(arr[1]);
								ch1.add(arr[2]);					
							}else if((Integer.parseInt(arr[0])==(gid2))&&(mFinalList.contains(arr[1]))){
								geno2.add(arr[0]);
								mark2.add(arr[1]);
								ch2.add(arr[2]);
							}			
						}
						/* System.out.println(geno1);
						 System.out.println(geno2);
						 System.out.println(mark1);
						 System.out.println(mark2);
						 System.out.println(ch1);
						 System.out.println(ch2);*/
						SNPfinalList=new ArrayList();
						SNPmissingList=new ArrayList();
							
							//System.out.println(mark1);
							for(int k=0;k<geno1.size();k++){
								if((!(ch2.get(k).equals("N")||ch2.get(k).equals("?")||ch2.get(k).equals("-")))&&(!(ch1.get(k).equals("N")||ch1.get(k).equals("?")||ch1.get(k).equals("-")))&&(!(ch1.get(k).equals(ch2.get(k))))){
									if(!SNPfinalList.contains(mark1.get(k))){
										SNPfinalList.add(mark1.get(k));
										markers=markers+"'"+mark1.get(k)+"',";	
										markerCount++;
									}
								}
								if((ch1.get(k).equals("0/0"))||(ch2.get(k).equals("0/0"))||(ch1.get(k).equals("0:0"))||(ch2.get(k).equals("0:0"))||(ch1.get(k).equals("?"))||(ch2.get(k).equals("?"))||(ch1.get(k).equals("N"))||(ch2.get(k).equals("N"))||(ch1.get(k).equals("-"))||(ch2.get(k).equals("-"))){
									if(!missingList.contains(mark1.get(k))){
										missingList.add(mark1.get(k));
									}
								}
							}
							String snpRecCount="";
						//System.out.println("SNPfinalList="+SNPfinalList); 
						//String snpMissingcount=SNPmissingList.size()+"";
							if(dType.length==2){
								snpRecCount=SNPfinalList.size()+"";
								//req.getSession().setAttribute("snpMissingData", SNPmissingList);
								req.getSession().setAttribute("snpResult", SNPfinalList);
								req.getSession().setAttribute("snpRecCount", snpRecCount);
								//req.getSession().setAttribute("snpMissingcount", snpMissingcount);
							}else{
								req.getSession().setAttribute("result", SNPfinalList);
								req.getSession().setAttribute("recCount", SNPfinalList.size());
							}
					 }
					 //System.out.println("missing List=:"+missingList);
					 req.getSession().setAttribute("MissingData", missingList);
					 req.getSession().setAttribute("missingCount", missingList.size());
				 }
				 /*System.out.println(geno1);
				 System.out.println(geno2);
				 System.out.println(mark1);
				 System.out.println(mark2);
				 System.out.println(ch1);
				 System.out.println(ch2);*/
				/*finalList=new ArrayList();
				missingList=new ArrayList();
				String geno="";
				String markers="";
				int markerCount=0;
				//System.out.println(mark1);
				for(int k=0;k<geno1.size();k++){
					if((!(ch2.get(k).equals("0/0")||ch2.get(k).equals("0:0")||ch2.get(k).equals("N")||ch2.get(k).equals("?")||ch2.get(k).equals("-")))&&(!(ch1.get(k).equals("0:0")||ch1.get(k).equals("0/0")||ch1.get(k).equals("N")||ch1.get(k).equals("?")||ch1.get(k).equals("-")))&&(!(ch1.get(k).equals(ch2.get(k))))){
						if(!finalList.contains(mark1.get(k))){
							finalList.add(mark1.get(k));
							markers=markers+"'"+mark1.get(k)+"',";	
							markerCount++;
						}
					}
					if((ch1.get(k).equals("0/0"))||(ch2.get(k).equals("0/0"))||(ch1.get(k).equals("0:0"))||(ch2.get(k).equals("0:0"))||(ch1.get(k).equals("?"))||(ch2.get(k).equals("?"))||(ch1.get(k).equals("N"))||(ch2.get(k).equals("N"))||(ch1.get(k).equals("-"))||(ch2.get(k).equals("-"))){
						if(!missingList.contains(mark1.get(k))){
							missingList.add(mark1.get(k));
						}
					}
				}*/
				//String recCount=finalList.size()+"";
				//String mcount=missingList.size()+"";
				ArrayList mapList1=new ArrayList();
				String markerIDs="";
				if(markerCount>0){
					//System.out.println("select marker_id from gdms_marker where marker_name in("+markers.substring(0, markers.length()-1)+")");
					ResultSet RM=stCen.executeQuery("select marker_id from gdms_marker where marker_name in("+markers.substring(0, markers.length()-1)+")");
					while (RM.next()){
						markerIDs=markerIDs+RM.getInt(1)+",";
					}
					ResultSet RML=stLoc.executeQuery("select marker_id from gdms_marker where marker_name in("+markers.substring(0, markers.length()-1)+")");
					while (RML.next()){
						markerIDs=markerIDs+RML.getInt(1)+",";
					}
					String markerIDsN=markerIDs.substring(0, markerIDs.length()-1);
					//markers=markers.substring(0, markers.length()-1);
					//System.out.println(markers);
					//System.out.println("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markerIDsN+") GROUP BY gdms_map.map_name");
					ResultSet rsMap=stCen.executeQuery("SELECT gdms_map.map_id, gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markerIDsN+") GROUP BY gdms_map.map_name");
					while(rsMap.next()){
						mapList1.add(rsMap.getString(2)+" ("+rsMap.getInt(3)+")"+"!~!"+rsMap.getInt(1));
					}
					ResultSet rsMapL=stLoc.executeQuery("SELECT gdms_map.map_id, gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markerIDsN+") GROUP BY gdms_map.map_name");
					while(rsMapL.next()){
						mapList1.add(rsMapL.getString(2)+" ("+rsMapL.getInt(3)+")"+"!~!"+rsMapL.getInt(1));
					}
					//System.out.println("mapList="+mapList1);								
					session.setAttribute("maps", mapList1);
				}
				//System.out.println("missing list=:"+missingList);
				//System.out.println("final list=:"+finalList);
				//req.getSession().setAttribute("map", map);
				//req.getSession().setAttribute("lines", geno);
				
				str="poly";
				String sel="yes";
				req.getSession().setAttribute("sel", sel);
				/*if(map==true){
					req.getSession().setAttribute("result",map_data);
				}else if(map==false){*/
					
				//}
			
			}else if(retrieveOP.equalsIgnoreCase("Get Lines")){
				/** retrieving gid, germplasm name for the markers that are uploaded through the text file  **/
				type="GermplasmName";
				req.getSession().setAttribute("op", retrieveOP);
				ArrayList germNames=new ArrayList();
				ArrayList markerList=new ArrayList();
				
				List<String> mList = new ArrayList<String>();
				String markers="";
				String m1="";
				String m2="";
				String fileName="";String saveFtoServer="";String saveF="";
				String[] splitStr=null;
				String op1=req.getParameter("opTypeMarkers");
				//System.out.println("...................."+op1);
				if(op1.equalsIgnoreCase("file")){
					FileUploadToServer fus = null;
				
					InputStream stream=null;
					FormFile file=(FormFile)df.get("txtNameL");
					//String fname1=file.getFileName();
					
					stream=file.getInputStream();
					saveFtoServer="UploadFiles";
					saveF=req.getSession().getServletContext().getRealPath("//")+"/"+saveFtoServer;
					if(!new File(saveF).exists())
						new File(saveF).mkdir();	        
					fileName=saveF+"/"+file.getFileName();
					fus=new FileUploadToServer();
					//System.out.println("................   :"+fileName);
					fus.createFile(stream,fileName);
					BufferedReader bf=new BufferedReader(new FileReader(fileName));		
					while ((str1 = bf.readLine()) != null){				
						markers= markers+str1+",";	
						markerList.add(str1);
					}
					//System.out.println("markers="+markers);
					splitStr=markers.split(",");
					session.setAttribute("mCount", splitStr.length-1);
					if(splitStr[0].equalsIgnoreCase("marker name")){
						for(int m=1;m<splitStr.length;m++){
							m1=m1+"'"+splitStr[m]+"',";
							if(!(mList.contains(splitStr[m])))
								mList.add(splitStr[m]);
							m2=m2+splitStr[m]+",";
						}			
					}
					 m1=m1.substring(0,m1.length()-1);
				}else if(op1.equalsIgnoreCase("textbox")){
					markers=req.getParameter("markersText");
					//System.out.println("#####################  "+markers.length());
					if(markers.contains("\n")){
						//System.out.println("new line");
						splitStr=markers.split("\n");
						for(int m=0;m<splitStr.length;m++){
							m1=m1+"'"+splitStr[m].trim()+"',";
							if(!(mList.contains(splitStr[m].trim())))
								mList.add(splitStr[m].trim());
							m2=m2+splitStr[m].trim()+",";
						}
						// m1=m1.substring(0,m1.length()-1);
						session.setAttribute("mCount", splitStr.length);
					}else if(markers.contains("\t")){
						//System.out.println("tab seperated");
						splitStr=markers.split("\t");
						for(int m=0;m<splitStr.length;m++){
							m1=m1+"'"+splitStr[m].trim()+"',";
							if(!(mList.contains(splitStr[m].trim())))
								mList.add(splitStr[m].trim());
							m2=m2+splitStr[m].trim()+",";
						}
						// m1=m1.substring(0,m1.length()-1);
						session.setAttribute("mCount", splitStr.length);
					}else if(markers.contains(",")){
						//System.out.println(", seperated");
						splitStr=markers.split(",");
						for(int m=0;m<splitStr.length;m++){
							m1=m1+"'"+splitStr[m].trim()+"',";
							if(!(mList.contains(splitStr[m].trim())))
								mList.add(splitStr[m].trim());
							m2=m2+splitStr[m].trim()+",";
						}
						 //m1=m1.substring(0,m1.length()-1);
						session.setAttribute("mCount", splitStr.length);
					}else{
						int mcount=1;
						//System.out.println("ELSE , TAB AND NEW LINE Seperated");						
						/*String ErrMsg = " Marker names should be provided with seperator as indicated";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");*/
						m1="'"+markers.trim()+"',";
						if(!(mList.contains(markers.trim())))
							mList.add(markers.trim());
						m2=markers.trim()+",";
						session.setAttribute("mCount", mcount);
					}
					
					
									
					
				}
				//System.out.println("..............markers="+m1);
				session.setAttribute("mnames1", m1);
				session.setAttribute("mnames", m2);
				String markerId="";
				
				
				
				
				rs2=null;
				
				int count=0;
				int start=0;
				int end=100;
					rs=stCen.executeQuery("select marker_id from gdms_marker where marker_name in("+ m1.substring(0,m1.length()-1)+") order by marker_id");
					rs2=stLoc.executeQuery("select marker_id from gdms_marker where marker_name in("+ m1.substring(0,m1.length()-1)+") order by marker_id");
					while(rs.next()){
						count=count+1;
						markerId=markerId+rs.getInt(1)+",";
					}
					
					while(rs2.next()){
						count=count+1;
						markerId=markerId+rs2.getInt(1)+",";
					}
					
					if(count<1){
						String ErrMsg = "Marker(s) does not exist";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");
					}
					//System.out.println(markerId);
					
					/** checking whether the marker id exists in 'allele_values' table for both local and central **/
					
					//System.out.println("select count(*) from allele_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					
					ResultSet rsa=stAC.executeQuery("select count(*) from gdms_allele_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while (rsa.next()){
						cenAlleleCount=rsa.getInt(1);
					}
					ResultSet rsaL=stAL.executeQuery("select count(*) from gdms_allele_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while (rsaL.next()){
						locAlleleCount=rsaL.getInt(1);
					}
					if(locAlleleCount>0)
						alleleCount=locAlleleCount;
					else if(cenAlleleCount>0)
						alleleCount=cenAlleleCount;
					else if(locAlleleCount>0 && cenAlleleCount > 0)
						alleleCount=cenAlleleCount;
					else
						alleleCount=0;
					
					
					//System.out.println("select count(*) from gdms_char_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					/** checking whether the marker id exists in 'char_values' table **/
					ResultSet rsc=stCC.executeQuery("select count(*) from gdms_char_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while(rsc.next()){
						cenCharCount=rsc.getInt(1);
					}
					ResultSet rscL=stCL.executeQuery("select count(*) from gdms_char_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while(rscL.next()){
						locCharCount=rscL.getInt(1);
					}
					
					if(locCharCount>0)
						charCount=locCharCount;
					else if(cenCharCount>0)
						charCount=cenCharCount;
					else if(locCharCount>0 && cenCharCount > 0)
						charCount=cenCharCount;
					else
						charCount=0;
					//System.out.println("select count(*) from gdms_mapping_pop_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					/** checking whether the marker id exists in 'mapping_pop_values' table **/
					rsM=stMC.executeQuery("select count(*) from gdms_mapping_pop_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while(rsM.next()){
						cenMapCharCount=rsM.getInt(1);
					}
					//System.out.println("...."+mapCharCount);
					rsML=stML.executeQuery("select count(*) from gdms_mapping_pop_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while(rsML.next()){
						locMapCharCount=rsML.getInt(1);
					}
					
					if(locMapCharCount>0)
						mapCharCount=locMapCharCount;
					else if(cenMapCharCount>0)
						mapCharCount=cenMapCharCount;
					else if(locMapCharCount>0 && cenMapCharCount > 0)
						mapCharCount=cenMapCharCount;
					else
						mapCharCount=0;
					
					//System.out.println("charCount=:"+charCount+"   alleleCount= "+alleleCount+"    mapCharCount="+mapCharCount);
					
					String gids="";
					
					/** if marker_id exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
					if(charCount>0){
						rsDet=stCen.executeQuery("SELECT distinct gid FROM gdms_char_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDet.next()){							
							gids=gids+rsDet.getInt(1)+",";
						}
						
						rsDetL=stLoc.executeQuery("SELECT distinct gid FROM gdms_char_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDetL.next()){							
							gids=gids+rsDetL.getInt(1)+",";
						}
						
					
					}
					
					/** if marker id exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
					if(alleleCount>0){
						rsDet=stCen.executeQuery("SELECT distinct gid FROM gdms_allele_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDet.next()){							
							gids=gids+rsDet.getInt(1)+",";
						}
						rsDetL=stLoc.executeQuery("SELECT distinct gid FROM gdms_allele_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDetL.next()){							
							gids=gids+rsDetL.getInt(1)+",";
						}
						
					}
					
					
					/** if marker_id exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
					if(mapCharCount>0){
						rsDet=stCen.executeQuery("SELECT distinct gid FROM gdms_mapping_pop_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDet.next()){							
							gids=gids+rsDet.getInt(1)+",";
						}
						rsDetL=stLoc.executeQuery("SELECT distinct gid FROM gdms_mapping_pop_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDetL.next()){							
							gids=gids+rsDetL.getInt(1)+",";
						}
					
					}
					
					if(charCount==0 && alleleCount==0 && mapCharCount==0){
						String ErrMsg = "No Genotyping data for the provided marker(s)";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");
					}
					
					String nid="";
					ArrayList nList=new ArrayList();
					//System.out.println("select nid from gdms_acc_metadataset where gid in ("+gids.substring(0, gids.length()-1)+")");
					rs=stCen.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gids.substring(0, gids.length()-1)+")");
					ResultSet rsNL=stLoc.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gids.substring(0, gids.length()-1)+")");
					while(rs.next()){
						nid=nid+rs.getString(1)+",";
						nList.add(rs.getInt(1));
					}
					while(rsNL.next()){
						nid=nid+rsNL.getString(1)+",";
						nList.add(rsNL.getInt(1));
					}
					
					/** 
					 * implementing middleware jar file 
					 */
					
					//GermplasmDataManager manager = factory.getGermplasmDataManager();
					Name names = null;
				
					for(int n=0;n<nList.size();n++){
						names=manager.getGermplasmNameByID(Integer.parseInt(nList.get(n).toString()));
						if(!germNames.contains(names.getNval()+","+names.getGermplasmId()))
							germNames.add(names.getNval()+","+names.getGermplasmId());
					}
					
					/*System.out.println("select distinct gid,nval from names where nid in("+nid.substring(0,nid.length()-1)+") order by nid");
					rs1=stmtG.executeQuery("select distinct gid,nval from names where nid in("+nid.substring(0,nid.length()-1)+") order by nid");
					while(rs1.next()){
						if(!germNames.contains(rs1.getString(2)+","+rs1.getInt(1)))
							germNames.add(rs1.getString(2)+","+rs1.getInt(1));
						
					}*/
					
					
					/*query2=hsession.createQuery("select distinct map_name from MapBean");
					mapList=query2.list();*/
					mapList.clear();
					rsMp=stCen.executeQuery("select distinct map_name from gdms_map");
					rsMpL=stLoc.executeQuery("select distinct map_name from gdms_map");
					while(rsMp.next()){
						mapList.add(rsMp.getString(1));
					}
					while(rsMpL.next()){
						mapList.add(rsMpL.getString(1));
					}
					session.setAttribute("AccListFinal", germNames);
					session.setAttribute("mapList", mapList);
					session.setAttribute("markerList", markerList);
					session.setAttribute("GenoCount", germNames.size());
					session.setAttribute("type", type);
				
				str="retGermplasms";
			}else if(retrieveOP.equalsIgnoreCase("Get Markers")){	
				/** retrieving markers for the gids that are uploaded through the text file  **/
				markerList.clear();
				genotypesList.clear();
								
				type="markers";
				gids="";
				String[] strGids=null;
				FileUploadToServer fus = null;
				String fileName="";String saveFtoServer="";String saveF="";
				InputStream stream=null;
				//System.out.println("*******************************************:"+req.getParameter("opTypeGids"));
				String op1=req.getParameter("opTypeGids");
				String gidsN="";
				String gid="";
				//Statement stmttest=con.createStatement();
				int gidsCount=0;
				if(op1.equalsIgnoreCase("file")){
					FormFile file=(FormFile)df.get("txtNameM");
				
					stream=file.getInputStream();
					saveFtoServer="UploadFiles";
					saveF=req.getSession().getServletContext().getRealPath("//")+"/"+saveFtoServer;
					if(!new File(saveF).exists())
						new File(saveF).mkdir();	        
					fileName=saveF+"/"+file.getFileName();
					fus=new FileUploadToServer();
					fus.createFile(stream,fileName);
				
				
					BufferedReader bf=new BufferedReader(new FileReader(fileName));		
					while ((str1 = bf.readLine()) != null){				
						gids= gids+str1+",";					
					}
					strGids=gids.split(",");
					if(strGids[0].equalsIgnoreCase("gids")){				
						for(int i=1;i<strGids.length;i++){
							gidsN=gidsN+strGids[i]+",";
						}	
						gidsCount=strGids.length-1;
						req.getSession().setAttribute("genCount", gidsCount);
					}
				}else if(op1.equalsIgnoreCase("textbox")){
					gids=req.getParameter("gidsText");
					if(gids.contains("\n")){
						//System.out.println("new line");
						strGids=gids.split("\n");
						for(int i=0;i<strGids.length;i++){
							gidsN=gidsN+strGids[i].trim()+",";
						}				
						req.getSession().setAttribute("genCount", strGids.length);
						//gid=gidsN.substring(0,gidsN.length()-1);
					}else if(gids.contains("\t")){
						//System.out.println("tab seperated");
						strGids=gids.split("\t");
						for(int i=0;i<strGids.length;i++){
							gidsN=gidsN+strGids[i].trim()+",";
						}				
						req.getSession().setAttribute("genCount", strGids.length);
						//gid=gidsN.substring(0,gidsN.length()-1);
					}else if(gids.contains(",")){
						strGids=gids.split(",");
						for(int i=0;i<strGids.length;i++){
							gidsN=gidsN+strGids[i].trim()+",";
						}				
						req.getSession().setAttribute("genCount", strGids.length);
						
					}else{
						int gcount=1;
						gidsN=gids+",";
						/*String ErrMsg = " Gids should be provided with seperator as indicated";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");*/
						req.getSession().setAttribute("genCount", gcount);
						gid=gidsN;
					}
					
				}
				gid=gidsN.substring(0,gidsN.length()-1);
				
				//System.out.println("request.getParameter radios ="+request.getParameter("str") );
				//System.out.println("........................................   gids in class="+gids);
				//String op=req.getParameter("str");
				req.getSession().setAttribute("op", retrieveOP);
				
				req.getSession().setAttribute("gidsN", gidsN);
				
				Statement stN=con.createStatement();
				
				ArrayList gidsList=new ArrayList();
				String nid="";
				int count=0;
				ArrayList nList=new ArrayList();
				rs=stCen.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gid+")");
				while(rs.next()){
					nid=nid+rs.getString(1)+",";
					nList.add(rs.getInt(1));
					count=count+1;
				}
				ResultSet rs1L=stLoc.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gid+")");
				while(rs1L.next()){
					nid=nid+rs1L.getString(1)+",";
					nList.add(rs1L.getInt(1));
					count=count+1;
				}
				if(count<1){
					String ErrMsg = "Gid(s) does not exist";
					req.getSession().setAttribute("indErrMsg", ErrMsg);
					return am.findForward("ErrMsgGID");
				}
				/*rs1=stN.executeQuery("select distinct gid,nval from names where nid in ("+nid.substring(0,nid.length()-1)+") order by nid");
				while(rs1.next()){
					if(!gidsList.contains(rs1.getInt(1)))
						gidsList.add(rs1.getInt(1));
					if(!genotypesList.contains(rs1.getString(2)))
						genotypesList.add(rs1.getString(2));	
					
					//mapN.put(rs1.getInt(1), rs1.getString(2));
					
				}*/
				
				/** 
				 * implementing middleware jar file 
				 */
				
				//GermplasmDataManager manager = factory.getGermplasmDataManager();
				Name names = null;
			
				for(int n=0;n<nList.size();n++){
					names=manager.getGermplasmNameByID(Integer.parseInt(nList.get(n).toString()));
					if(!gidsList.contains(names.getGermplasmId()))
						gidsList.add(names.getGermplasmId());
					if(!genotypesList.contains(names.getNval()))
						genotypesList.add(names.getNval());	
				}
				
				
				
				
				//System.out.println(mapN);
				//System.out.println("..........."+gidsList);
				
				
				
				//System.out.println("select count(*) from gdms_allele_values where gid in ("+gid+")");
				/** checking whether the gid exists in 'allele_values' table **/
				ResultSet rsa=stCen.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
				while (rsa.next()){
					cenAlleleCount=rsa.getInt(1);
				}
				ResultSet rsaL=stLoc.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
				while (rsaL.next()){
					locAlleleCount=rsaL.getInt(1);
				}
				if(locAlleleCount>0)
					alleleCount=locAlleleCount;
				else if(cenAlleleCount>0)
					alleleCount=cenAlleleCount;
				else if(locAlleleCount>0 && cenAlleleCount > 0)
					alleleCount=cenAlleleCount;
				else
					alleleCount=0;
				
				/** checking whether the gid exists in 'char_values' table **/
				//System.out.println("select count(*) from gdms_char_values where gid in("+gid+")");
				ResultSet rsc=stCen.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
				while(rsc.next()){
					//charCount=rsc.getInt(1);
					cenCharCount=rsc.getInt(1);
				}
				ResultSet rscL=stLoc.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
				while(rscL.next()){
					locCharCount=rscL.getInt(1);
				}
				if(locCharCount>0)
					charCount=locCharCount;
				else if(cenCharCount>0)
					charCount=cenCharCount;
				else if(locCharCount>0 && cenCharCount > 0)
					charCount=cenCharCount;
				else
					charCount=0;
				/** checking whether the gid exists in 'char_values' table **/
				//System.out.println("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				rsM=stCen.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				while(rsM.next()){
					cenMapCharCount=rsM.getInt(1);
				}
				rsML=stLoc.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				while(rsML.next()){
					locMapCharCount=rsML.getInt(1);
				}
				if(locMapCharCount>0)
					mapCharCount=locMapCharCount;
				else if(cenMapCharCount>0)
					mapCharCount=cenMapCharCount;
				else if(locMapCharCount>0 && cenMapCharCount > 0)
					mapCharCount=cenMapCharCount;
				else
					mapCharCount=0;
				/** if gids exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
				if(charCount>0){
					rsDet=stCen.executeQuery("SELECT distinct gdms_char_values.gid, gdms_marker.marker_name FROM gdms_char_values join gdms_marker on gdms_marker.marker_id = gdms_char_values.marker_id WHERE gdms_char_values.gid in ("+gid+") ORDER BY gid, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
					rsDetL=stLoc.executeQuery("SELECT distinct gdms_char_values.gid, gdms_marker.marker_name FROM gdms_char_values join gdms_marker on gdms_marker.marker_id = gdms_char_values.marker_id WHERE gdms_char_values.gid in ("+gid+") ORDER BY gid, marker_name");
					
					while(rsDetL.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDetL.getString(2)))
							markerList.add(rsDetL.getString(2));
					}
					
					
					session.setAttribute("datasetType", "SNP");
				}
				
				/** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
				if(alleleCount>0){
					
					rsDet=stCen.executeQuery("SELECT gdms_allele_values.gid, gdms_marker.marker_name FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.gid in ("+gid+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER BY gid, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
					rsDetL=stLoc.executeQuery("SELECT gdms_allele_values.gid, gdms_marker.marker_name FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.gid in ("+gid+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER BY gid, marker_name");
					
					while(rsDetL.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDetL.getString(2)))
							markerList.add(rsDetL.getString(2));
					}
					session.setAttribute("datasetType", "SSR");
				}
				
				/** if gids exists in 'map_char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
				if(mapCharCount>0){
					//rsDet=stmtM.executeQuery("SELECT distinct germplasm_temp.germplasm_name,marker.marker_name FROM germplasm_temp join mapping_pop_values on mapping_pop_values.gid=germplasm_temp.gid join marker on marker.marker_id = mapping_pop_values.marker_id WHERE mapping_pop_values.gid in ("+gid+") ORDER BY germplasm_name, marker_name");
								
					rsDet=stCen.executeQuery("SELECT distinct gdms_mapping_pop_values.gid, gdms_marker.marker_name FROM gdms_mapping_pop_values join gdms_marker on gdms_marker.marker_id = gdms_mapping_pop_values.marker_id WHERE gdms_mapping_pop_values.gid in ("+gid+") ORDER BY gid, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}	
					
					rsDetL=stLoc.executeQuery("SELECT distinct gdms_mapping_pop_values.gid, gdms_marker.marker_name FROM gdms_mapping_pop_values join gdms_marker on gdms_marker.marker_id = gdms_mapping_pop_values.marker_id WHERE gdms_mapping_pop_values.gid in ("+gid+") ORDER BY gid, marker_name");
					
					while(rsDetL.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDetL.getString(2)))
							markerList.add(rsDetL.getString(2));
					}
					
					session.setAttribute("datasetType", "mapping");
				}
				
				//System.out.println(markerList);
				
				/*query2=hsession.createQuery("select distinct map_name from MapBean");
				
				
				mapList=query2.list();*/
				mapList.clear();
				//System.out.println("select distinct map_name from gdms_map");
				rsMp=stCen.executeQuery("select distinct map_name from gdms_map");
				while(rsMp.next()){
					mapList.add(rsMp.getString(1));
				}
				rsMpL=stLoc.executeQuery("select distinct map_name from gdms_map");
				while(rsMpL.next()){
					if(!mapList.contains(rsMpL.getString(1)))
					mapList.add(rsMpL.getString(1));
				}
				req.getSession().setAttribute("AccListFinal", genotypesList);
				req.getSession().setAttribute("mapList", mapList);
				req.getSession().setAttribute("markerList", markerList);
				
				req.getSession().setAttribute("MarkerCount", markerList.size());
				session.setAttribute("type", type);
				str="retMarkers";
				
			}else if(retrieveOP.equalsIgnoreCase("Get")){	
				/** retrieving markers for the gNames that are uploaded through the text file  **/
				markerList.clear();
				genotypesList.clear();
				type="markers";
				String gNames="";
				String[] strGNames=null;
				FileUploadToServer fus = null;
				String fileName="";String saveFtoServer="";String saveF="";
				String argGNames="";String gidsN="";
			//	System.out.println("*******************************************:"+req.getParameter("opTypeGN"));
				String op1=req.getParameter("opTypeGN");
				List<String> linesList=new ArrayList<String>();
				//Statement stmttest=con.createStatement();
				if(op1.equalsIgnoreCase("file")){
					InputStream stream=null;
					FormFile file=(FormFile)df.get("txtNameGN");
				
					stream=file.getInputStream();
					saveFtoServer="UploadFiles";
					saveF=req.getSession().getServletContext().getRealPath("//")+"/"+saveFtoServer;
					if(!new File(saveF).exists())
						new File(saveF).mkdir();	        
					fileName=saveF+"/"+file.getFileName();
					fus=new FileUploadToServer();
					fus.createFile(stream,fileName);
				
				
					BufferedReader bf=new BufferedReader(new FileReader(fileName));		
					while ((str1 = bf.readLine()) != null){				
						gNames= gNames+str1+",";					
					}
					
					strGNames=gNames.split(",");
					if(strGNames[0].equalsIgnoreCase("germplasm names")){				
						for(int i=1;i<strGNames.length;i++){
							argGNames=argGNames+"'"+strGNames[i]+"',";
							linesList.add(strGNames[i]);
						}				
						//req.getSession().setAttribute("genCount", gidsCount);
					}
				}else if(op1.equalsIgnoreCase("textbox")){
					gNames=req.getParameter("GNamesText");
					if(gNames.contains("\n")){
						//System.out.println("new line");
						strGNames=gNames.split("\n");
						for(int i=0;i<strGNames.length;i++){
							argGNames=argGNames+"'"+strGNames[i].trim()+"',";
							linesList.add(strGNames[i].trim());
						}
					}else if(gNames.contains("\t")){
						//System.out.println("tab seperated");
						strGNames=gNames.split("\t");
						for(int i=0;i<strGNames.length;i++){
							argGNames=argGNames+"'"+strGNames[i].trim()+"',";
							linesList.add(strGNames[i].trim());
						}
					}else if(gNames.contains(",")){
						strGNames=gNames.split(",");
						for(int i=0;i<strGNames.length;i++){
							argGNames=argGNames+"'"+strGNames[i].trim()+"',";
							linesList.add(strGNames[i].trim());
						}
					}else{
						/*String ErrMsg = "Germplasm names should be provided with seperator as indicated";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");*/
						argGNames="'"+gNames.trim()+"',";
						linesList.add(gNames.trim());
					}
					
				}
				int count=0;
				
				ArrayList gidL=new ArrayList();
				
				String nids="";
				int countG=0;
				
				
				List<GidNidElement> results = manager.getGidAndNidByGermplasmNames(linesList);
				//System.out.println("RESULTS (getGidAndNidByGermplasmNames): " + results);
				for(int r=0;r<results.size();r++){
					gidsN=gidsN+results.get(r).getGermplasmId()+",";
					countG=countG+1;
				}
				
				
				/*System.out.println("select distinct gid,nid from names where nval in("+argGNames.substring(0,argGNames.length()-1) +") order by gid desc");
				ResultSet rsGids=stmtM.executeQuery("select distinct gid from names where nval in("+argGNames.substring(0,argGNames.length()-1) +") order by gid desc");
				while(rsGids.next()){
					gidsN=gidsN+rsGids.getInt(1)+",";
					countG=countG+1;
					
				}*/
				if(countG<1){
					String ErrMsg = "Germplasm name(s) does not exist";
					req.getSession().setAttribute("indErrMsg", ErrMsg);
					return am.findForward("ErrMsgG");
				}
				String nid="";
				//System.out.println("select distinct gid,nid from gdms_acc_metadataset where gid in ("+gidsN.substring(0,gidsN.length()-1)+")");
				rs=stCen.executeQuery("select distinct gid,nid from gdms_acc_metadataset where gid in ("+gidsN.substring(0,gidsN.length()-1)+")");
				while(rs.next()){
					nid=nid+rs.getString(1)+",";
					count++;
				}
				ResultSet rs1L=stLoc.executeQuery("select distinct gid,nid from gdms_acc_metadataset where gid in ("+gidsN.substring(0,gidsN.length()-1)+")");
				while(rs1L.next()){
					nid=nid+rs1L.getString(1)+",";
					count++;
				}
				if(count<1){
					//String ErrMsg = "Germplasm name(s) does not exist";
					String ErrMsg = "No Genotyping data for the provided germplasm(s)";
					req.getSession().setAttribute("indErrMsg", ErrMsg);
					return am.findForward("ErrMsgG");
				}
				
				req.getSession().setAttribute("op", retrieveOP);
				
				req.getSession().setAttribute("genCount", count);
				req.getSession().setAttribute("gidsN", nid);
				//System.out.println("%%%%%%%%%%%%%%%%   length="+nid);
				String gid=nid.substring(0,nid.length()-1);
				/*int alleleCount=0;
				int charCount=0;
				int mapCharCount=0;
				
				*/
				//System.out.println("select count(*) from gdms_allele_values where gid in ("+gid+")");
				//** checking whether the gid exists in 'allele_values' table **//*
				ResultSet rsa=stCen.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
				while (rsa.next()){
					cenAlleleCount=rsa.getInt(1);
				}
				ResultSet rsaL=stLoc.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
				while (rsaL.next()){
					locAlleleCount=rsaL.getInt(1);
				}
				if(locAlleleCount>0)
					alleleCount=locAlleleCount;
				else if(cenAlleleCount>0)
					alleleCount=cenAlleleCount;
				else if(locAlleleCount>0 && cenAlleleCount > 0)
					alleleCount=cenAlleleCount;
				else
					alleleCount=0;
				//** checking whether the gid exists in 'char_values' table **//*
				//System.out.println("select count(*) from gdms_char_values where gid in("+gid+")");
				ResultSet rsc=stCen.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
				while(rsc.next()){
					cenCharCount=rsc.getInt(1);
				}
				ResultSet rscL=stLoc.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
				while(rscL.next()){
					locCharCount=rscL.getInt(1);
					
				}
				if(locCharCount>0)
					charCount=locCharCount;
				else if(cenCharCount>0)
					charCount=cenCharCount;
				else if(locCharCount>0 && cenCharCount > 0)
					charCount=cenCharCount;
				else
					charCount=0;
				
				//** checking whether the gid exists in 'char_values' table **//*
				//System.out.println("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				rsM=stCen.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				while(rsM.next()){
					cenMapCharCount=rsM.getInt(1);
				}
				rsML=stLoc.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				while(rsML.next()){
					locMapCharCount=rsML.getInt(1);
				}
				if(locMapCharCount>0)
					mapCharCount=locMapCharCount;
				else if(cenMapCharCount>0)
					mapCharCount=cenMapCharCount;
				else if(locMapCharCount>0 && cenMapCharCount > 0)
					mapCharCount=cenMapCharCount;
				else
					mapCharCount=0;
				//** if gids exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **//*
				if(charCount>0){
					//rsDet=stmtC.executeQuery("SELECT distinct germplasm_temp.germplasm_name,marker.marker_name FROM germplasm_temp join char_values on char_values.gid=germplasm_temp.gid join marker on marker.marker_id = char_values.marker_id WHERE char_values.gid in ("+gid+") ORDER BY germplasm_name, marker_name");
					
					rsDet=stCen.executeQuery("SELECT distinct gdms_char_values.gid, gdms_marker.marker_name FROM gdms_char_values join gdms_marker on gdms_marker.marker_id = gdms_char_values.marker_id WHERE gdms_char_values.gid in ("+gid+") ORDER BY marker_name");
					
					while(rsDet.next()){							
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
					rsDetL=stLoc.executeQuery("SELECT distinct gdms_char_values.gid, gdms_marker.marker_name FROM gdms_char_values join gdms_marker on gdms_marker.marker_id = gdms_char_values.marker_id WHERE gdms_char_values.gid in ("+gid+") ORDER BY marker_name");
					
					while(rsDetL.next()){							
						if(!markerList.contains(rsDetL.getString(2)))
							markerList.add(rsDetL.getString(2));
					}
					session.setAttribute("datasetType", "SNP");
				}
				
				//** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **//*				
				if(alleleCount>0){
					rsDet=stCen.executeQuery("SELECT distinct gdms_allele_values.gid, gdms_marker.marker_name FROM gdms_allele_values, gdms_marker WHERE gdms_allele_values.gid in ("+gid+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER by marker_name");
					while(rsDet.next()){
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
					rsDetL=stLoc.executeQuery("SELECT distinct gdms_allele_values.gid, gdms_marker.marker_name FROM gdms_allele_values, gdms_marker WHERE gdms_allele_values.gid in ("+gid+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER by marker_name");
					while(rsDetL.next()){
						if(!markerList.contains(rsDetL.getString(2)))
							markerList.add(rsDetL.getString(2));
					}
					session.setAttribute("datasetType", "SSR");
				}
				
				//** if gids exists in 'map_char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **//*
				if(mapCharCount>0){
					rsDet=stCen.executeQuery("SELECT distinct gdms_mapping_pop_values.gid, gdms_marker.marker_name FROM gdms_mapping_pop_values join gdms_marker on gdms_marker.marker_id = gdms_mapping_pop_values.marker_id WHERE gdms_mapping_pop_values.gid in ("+gid+") ORDER BY marker_name");
					while(rsDet.next()){						
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}	
					rsDetL=stLoc.executeQuery("SELECT distinct gdms_mapping_pop_values.gid, gdms_marker.marker_name FROM gdms_mapping_pop_values join gdms_marker on gdms_marker.marker_id = gdms_mapping_pop_values.marker_id WHERE gdms_mapping_pop_values.gid in ("+gid+") ORDER BY marker_name");
					while(rsDetL.next()){						
						if(!markerList.contains(rsDetL.getString(2)))
							markerList.add(rsDetL.getString(2));
					}
					session.setAttribute("datasetType", "mapping");
				}
				
				
				mapList.clear();
				rsMp=stCen.executeQuery("select distinct map_name from gdms_map");
				while(rsMp.next()){
					mapList.add(rsMp.getString(1));
				}
				rsMpL=stLoc.executeQuery("select distinct map_name from gdms_map");
				while(rsMpL.next()){
					mapList.add(rsMpL.getString(1));
				}
		        //System.out.println("..............:"+mapList);
				req.getSession().setAttribute("AccListFinal", genotypesList);
				req.getSession().setAttribute("mapList", mapList);
				req.getSession().setAttribute("markerList", markerList);
				
				req.getSession().setAttribute("MarkerCount", markerList.size());
				session.setAttribute("type", type);
				str="retMarkers";
				
			}else if(retrieveOP.equalsIgnoreCase("GetInfo")){
				String opType=req.getParameter("retType");
				//System.out.println("888888888888888888888....  opType="+opType);
				//if((opType.equals("qtlData"))||(opType.equals("trait"))){
				//System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ TTTTTTTTTTTTTTTTTTTTTTTTTTT");
				/** Retrieving qtl information **/
				if(session!=null){
					session.removeAttribute("strdata");		
					session.removeAttribute("indErrMsg");	
				}
				
				ArrayList traitsComList=new ArrayList();
				SortedMap map = new TreeMap();
				SortedMap traitsMap = new TreeMap();
				SortedMap traitMap = new TreeMap();
				SortedMap mtaMap = new TreeMap();
				ArrayList mtaMarkers=new ArrayList();
				
				SortedMap mtaMaps = new TreeMap();
				ArrayList mtas=new ArrayList();
				
				String mtaList="";
				/*Class.forName(driver).newInstance();
				conn = DriverManager.getConnection(url+dbName,userName,password);
				Statement stCen=conn.createStatement();*/
				
				boolean mtaExists=false;
				
				String qtl=(String)df.get("qtl");
				String qtlData="";
				int linkid=0;			float min=0;			float max=0;
				String lg="";			String markers="";			//int qtlId=0;
				String qtlIds="";int count=0;String qtl_id="";	String strData="";
				String finalQTLList="";
				ArrayList qtlDataList=new ArrayList();
				String qtlId="";
				ArrayList tidsList=new ArrayList();
				
				SortedMap mMaps = new TreeMap();
				ArrayList mMapsList=new ArrayList();
				
				//ResultSet rsMtas=null;
				//Statement st=con.createStatement();
				session.setAttribute("qtlType", opType);
				//System.out.println("******************************************  "+opType+"  $$$$$$$$   "+qtl);
				//if((opType.equals("QTLName"))||(opType.equals("Trait"))){
					
					//System.out.println("******************:"+traitsComList);
					//Statement stC=conn.createStatement();
					//System.out.println("select distinct trabbr, trname, ontology from tmstraits");
				ResultSet rsQL=null;
				String queryQ="";
				List qtlList=new ArrayList();
				if(opType.equals("QTLName")){
					
					/*rsN=stmtN.executeQuery("select distinct trabbr, trname, ontology, tid from tmstraits");
					while(rsN.next()){
						if(!(traitsComList.contains(rsN.getString(1)))){
							traitsComList.add(rsN.getString(1));
							map.put(rsN.getString(1), rsN.getString(2)+"!~!"+rsN.getString(3));							
						}
						if(!tidsList.contains(rsN.getInt(4)))
							traitsMap.put(rsN.getInt(4), rsN.getString(1));
					}*/
					
					/** if retrieval is through QTL Name **/
					qtlDataList.clear();
					//System.out.println("select qtl_id from qtl where qtl_name like '"+qtl+"%'");
					if(qtl.equalsIgnoreCase("*")){
						//System.out.println("***************************");
						queryQ="select distinct qtl_id, tid from gdms_qtl_details order by qtl_id";
						rs1=stCen.executeQuery(queryQ);
						rsQL=stLoc.executeQuery(queryQ);
					}else{
						//System.out.println("else if");
						System.out.println("select gdms_qtl_details.qtl_id, gdms_qtl_details.tid from gdms_qtl_details join gdms_qtl on gdms_qtl.qtl_id=gdms_qtl_details.qtl_id where gdms_qtl.qtl_name like '"+qtl+"%' order by gdms_qtl_details.qtl_id");
						rs1=stCen.executeQuery("select gdms_qtl_details.qtl_id, gdms_qtl_details.tid from gdms_qtl_details join gdms_qtl on gdms_qtl.qtl_id=gdms_qtl_details.qtl_id where gdms_qtl.qtl_name like '"+qtl+"%' order by gdms_qtl_details.qtl_id");
						rsQL=stLoc.executeQuery("select gdms_qtl_details.qtl_id, gdms_qtl_details.tid from gdms_qtl_details join gdms_qtl on gdms_qtl.qtl_id=gdms_qtl_details.qtl_id where gdms_qtl.qtl_name like '"+qtl+"%' order by gdms_qtl_details.qtl_id");
					}
					while(rs1.next()){
						qtlList.add(rs1.getInt(1));	
						if(!tid.contains(rs1.getInt(2)))
							tid.add(rs1.getInt(2));
					}
					while(rsQL.next()){
						qtlList.add(rsQL.getInt(1));	
						if(!tid.contains(rsQL.getInt(2)))
							tid.add(rsQL.getInt(2));
					}
					System.out.println(qtlList);
					String mapIds="";
					if(qtlList.size()==0){						
						String ErrMsg = "QTL Name not found";						
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}else{
						for(int t=0; t<tid.size();t++){
							Term term =om.getTermById(tid.get(t));
							if(!(traitsComList.contains(term.getName()))){
								traitsComList.add(term.getName());
								map.put(term.getName(), term.getName()+"!~!"+term.getName());
								
							}
							tidsList.add(term.getId());
								traitsMap.put(term.getId(), term.getName());
						}
						
						
						for(int q=0;q<qtlList.size();q++){
							qtlId=qtlId+qtlList.get(q)+",";
						}
						rsC=stCen.executeQuery("select map_id from gdms_qtl_details where qtl_id in("+qtlId.substring(0,qtlId.length()-1)+")");
						while(rsC.next()){
							mapIds=mapIds+rsC.getInt(1)+",";
						}
						rsL=stLoc.executeQuery("select map_id from gdms_qtl_details where qtl_id in("+qtlId.substring(0,qtlId.length()-1)+")");
						while(rsL.next()){
							mapIds=mapIds+rsL.getInt(1)+",";
							
						}
						System.out.println("select gdms_map.map_id, gdms_map.map_name from gdms_map join gdms_qtl_details on gdms_map.map_id=gdms_qtl_details.map_id where gdms_qtl_details.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+")");
						rs1=stCen.executeQuery("select gdms_map.map_id, gdms_map.map_name from gdms_map where map_id in ("+mapIds.substring(0,mapIds.length()-1)+")");
						rs2=stLoc.executeQuery("select gdms_map.map_id, gdms_map.map_name from gdms_map where map_id in ("+mapIds.substring(0,mapIds.length()-1)+")");
						while(rs1.next()){
							mMapsList.add(rs1.getInt(1));
							mMaps.put(rs1.getInt(1), rs1.getString(2));
						}
						while(rs2.next()){
							if(!mMapsList.contains(rs2.getInt(1))){
								mMapsList.add(rs2.getInt(1));
								mMaps.put(rs2.getInt(1), rs2.getString(2));
							}
						}			
						
						
						
						if(qtl.equalsIgnoreCase("*")){
							//rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, tmstraits.trname, tmstraits.ontology FROM gdms_qtl_details, gdms_qtl,gdms_map, tmstraits WHERE gdms_qtl.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id and gdms_qtl_details.trait=tmstraits.trabbr order by gdms_qtl.qtl_id");
							//System.out.println("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions FROM gdms_qtl_details, gdms_qtl,gdms_map WHERE gdms_qtl.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
							System.out.println("SELECT gdms_qtl.qtl_name,gdms_qtl_details.map_id,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.tid, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, gdms_qtl_details.clen, gdms_qtl_details.se_additive, gdms_qtl_details.hv_parent, gdms_qtl_details.hv_allele, gdms_qtl_details.lv_parent, gdms_qtl_details.lv_allele FROM gdms_qtl_details, gdms_qtl WHERE gdms_qtl.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl.qtl_id");
							rs=stCen.executeQuery("SELECT gdms_qtl.qtl_name,gdms_qtl_details.map_id,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.tid, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, gdms_qtl_details.clen, gdms_qtl_details.se_additive, gdms_qtl_details.hv_parent, gdms_qtl_details.hv_allele, gdms_qtl_details.lv_parent, gdms_qtl_details.lv_allele FROM gdms_qtl_details, gdms_qtl WHERE gdms_qtl.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl.qtl_id");
							rsQL=stLoc.executeQuery("SELECT gdms_qtl.qtl_name,gdms_qtl_details.map_id,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.tid, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, gdms_qtl_details.clen, gdms_qtl_details.se_additive, gdms_qtl_details.hv_parent, gdms_qtl_details.hv_allele, gdms_qtl_details.lv_parent, gdms_qtl_details.lv_allele FROM gdms_qtl_details, gdms_qtl WHERE gdms_qtl.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl.qtl_id");
						}else{						
							//rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, tmstraits.trname, tmstraits.ontology FROM gdms_qtl_details, gdms_qtl, gdms_map, tmstraits WHERE gdms_qtl.qtl_name like '"+qtl.toLowerCase()+"%' AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id and gdms_qtl_details.trait=tmstraits.trabbr order by gdms_qtl.qtl_id");
							rs=stCen.executeQuery("SELECT gdms_qtl.qtl_name,gdms_qtl_details.map_id,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.tid, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, gdms_qtl_details.clen, gdms_qtl_details.se_additive, gdms_qtl_details.hv_parent, gdms_qtl_details.hv_allele, gdms_qtl_details.lv_parent, gdms_qtl_details.lv_allele FROM gdms_qtl_details, gdms_qtl WHERE gdms_qtl.qtl_name like '"+qtl.toLowerCase()+"%' AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl.qtl_id");
							rsQL=stLoc.executeQuery("SELECT gdms_qtl.qtl_name,gdms_qtl_details.map_id,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.tid, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, gdms_qtl_details.clen, gdms_qtl_details.se_additive, gdms_qtl_details.hv_parent, gdms_qtl_details.hv_allele, gdms_qtl_details.lv_parent, gdms_qtl_details.lv_allele FROM gdms_qtl_details, gdms_qtl WHERE gdms_qtl.qtl_name like '"+qtl.toLowerCase()+"%' AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id order by gdms_qtl.qtl_id");
						}
						while(rs.next()){
							qtlDataList.add(rs.getString(1)+"!~!"+mMaps.get(rs.getInt(2)).toString()+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+traitsMap.get(Integer.parseInt(rs.getString(6)))+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+"!~!"+rs.getString(14)+"!~!"+rs.getString(15)+"!~!"+rs.getString(16)+"!~!"+rs.getString(17)+"!~!"+rs.getString(18)+"!~!"+rs.getString(19));
						}
						while(rsQL.next()){
							qtlDataList.add(rsQL.getString(1)+"!~!"+mMaps.get(rsQL.getInt(2))+"!~!"+rsQL.getString(3)+"!~!"+rsQL.getFloat(4)+"!~!"+rsQL.getFloat(5)+"!~!"+traitsMap.get(Integer.parseInt(rsQL.getString(6)))+"!~!"+rsQL.getString(7)+"!~!"+rsQL.getString(8)+"!~!"+rsQL.getString(9)+"!~!"+rsQL.getFloat(10)+"!~!"+rsQL.getFloat(11)+"!~!"+rsQL.getString(13)+"!~!"+rsQL.getFloat(12)+"!~!"+rsQL.getString(14)+"!~!"+rsQL.getString(15)+"!~!"+rsQL.getString(16)+"!~!"+rsQL.getString(17)+"!~!"+rsQL.getString(18)+"!~!"+rsQL.getString(19));
						}
						//System.out.println(qtlDataList.size()+"..................:"+qtlDataList);
						//ArrayList finalQTLList=new ArrayList();
						//System.out.println("map=:"+map);
						
						for(int a=0; a<qtlDataList.size();a++){
							String[] argTr=qtlDataList.get(a).toString().split("!~!");
							//System.out.println("argTr[5]=:"+argTr[5]);
							if(map.containsKey(argTr[5])){
								finalQTLList=finalQTLList+argTr[0]+"!~!"+argTr[1]+"!~!"+argTr[2]+"!~!"+argTr[3]+"!~!"+argTr[4]+"!~!"+argTr[5]+"!~!"+argTr[6]+"!~!"+argTr[7]+"!~!"+argTr[8]+"!~!"+argTr[9]+"!~!"+argTr[10]+"!~!"+argTr[11]+"!~!"+argTr[12]+"!~!"+map.get(argTr[5])+"!~!"+argTr[13]+"!~!"+argTr[14]+"!~!"+argTr[15]+"!~!"+argTr[16]+"!~!"+argTr[17]+";;;";
							}else{
								finalQTLList=finalQTLList+argTr[0]+"!~!"+argTr[1]+"!~!"+argTr[2]+"!~!"+argTr[3]+"!~!"+argTr[4]+"!~!"+argTr[5]+"!~!"+argTr[6]+"!~!"+argTr[7]+"!~!"+argTr[8]+"!~!"+argTr[9]+"!~!"+argTr[10]+"!~!"+argTr[11]+"!~!"+argTr[12]+"!~! "+"!~! "+argTr[13]+"!~!"+argTr[14]+"!~!"+argTr[15]+"!~!"+argTr[16]+"!~!"+argTr[17]+";;;";
							}
							
						}
						
						//System.out.println(finalQTLList);
						req.getSession().setAttribute("strdata",finalQTLList);
					}
					req.getSession().setAttribute("qtl", qtl);
					req.getSession().setAttribute("mta", mtaExists);
					req.getSession().setAttribute("mtaList", mtaList);
					str="retQTL";
				}else if(opType.equals("Trait")){
					/** if the option is through trait name **/
					//rsC=stCen.executeQuery("select distinct trabbr, trname, ontology, tid from tmstraits");
					rsC=stCen.executeQuery("select distinct cvterm_id, name from cvterm");
					while(rsC.next()){
						if(!(traitsComList.contains(rsC.getString(1)))){
							traitsComList.add(rsC.getString(1));
							map.put(rsC.getString(1), rsC.getString(2)+"!~!"+rsC.getInt(1));
							
						}
						tidsList.add(rsC.getString(2));
							traitsMap.put(rsC.getInt(1), rsC.getString(2));
							traitMap.put(rsC.getString(2), rsC.getInt(1));
						//traitsComList.add(rsC.getString(1)+"!~!"+rsC.getString(2)+"!~!"+rsC.getString(3));
					}
					rsN=stmtN.executeQuery("select distinct cvterm_id, name from cvterm");
					while(rsN.next()){
						if(!(traitsComList.contains(rsN.getString(1)))){
							traitsComList.add(rsN.getString(1));
							map.put(rsN.getString(1), rsN.getString(2)+"!~!"+rsN.getInt(1));							
						}
						if(!tidsList.contains(rsN.getString(2)))
							traitsMap.put(rsN.getInt(1), rsN.getString(2));
						traitMap.put(rsN.getString(2), rsN.getInt(1));
					}
					String mtaMarkerIds="";
					String mtaMapIds="";
					int mtaCount=0;
					qtlDataList.clear();
					/* getting mta's from both central and local */
					rs=stCen.executeQuery("select marker_id, map_id from gdms_mta");
					while(rs.next()){
						mtaMarkerIds=mtaMarkerIds+rs.getInt(1)+",";
						mtaMapIds=mtaMapIds+rs.getInt(2)+",";
						mtaCount++;
					}
					rsL=stLoc.executeQuery("select marker_id, map_id from gdms_mta");
					while(rsL.next()){
						mtaMarkerIds=mtaMarkerIds+rsL.getInt(1)+",";
						mtaMapIds=mtaMapIds+rsL.getInt(2)+",";
						mtaCount++;
					}
					System.out.println("mtaMapIds=:"+mtaMapIds);
					if(mtaCount>0){
						mtaMarkerIds=mtaMarkerIds.substring(0, mtaMarkerIds.length()-1);
						//System.out.println("mtaMarkerIds..........:"+mtaMarkerIds);
						mtaMapIds=mtaMapIds.substring(0, mtaMapIds.length()-1);
						rsM=stCen.executeQuery("SELECT marker_id, marker_name FROM gdms_marker where marker_id in("+mtaMarkerIds+")");
						while(rsM.next()){
							if(!(mtaMarkers.contains(rsM.getInt(1)))){
								mtaMarkers.add(rsM.getInt(1));
								mtaMap.put(rsM.getInt(1), rsM.getString(2));
							}
						}
						rsML=stLoc.executeQuery("SELECT marker_id, marker_name FROM gdms_marker where marker_id in("+mtaMarkerIds+")");
						while(rsML.next()){
							if(!(mtaMarkers.contains(rsML.getInt(1)))){
								mtaMarkers.add(rsML.getInt(1));
								mtaMap.put(rsML.getInt(1), rsML.getString(2));
							}
						}
						/*getting maps of mta's from both central and local */
					
						rsMaps=stCen.executeQuery("SELECT map_id, map_name FROM gdms_map");
						while(rsMaps.next()){
							if(!(mtas.contains(rsMaps.getInt(1)))){
								mtas.add(rsMaps.getInt(1));
								mtaMaps.put(rsMaps.getInt(1), rsMaps.getString(2));
							}
						}
						rsMapsL=stLoc.executeQuery("SELECT map_id, map_name FROM gdms_map");
						while(rsMapsL.next()){
							if(!(mtas.contains(rsMapsL.getInt(1)))){
								mtas.add(rsMapsL.getInt(1));
								mtaMaps.put(rsMapsL.getInt(1), rsMapsL.getString(2));
							}
						}
					}else{
						rsMaps=stCen.executeQuery("SELECT map_id, map_name FROM gdms_map");
						while(rsMaps.next()){
							if(!(mtas.contains(rsMaps.getInt(1)))){
								mtas.add(rsMaps.getInt(1));
								mtaMaps.put(rsMaps.getInt(1), rsMaps.getString(2));
							}
						}
						rsMapsL=stLoc.executeQuery("SELECT map_id, map_name FROM gdms_map");
						while(rsMapsL.next()){
							if(!(mtas.contains(rsMapsL.getInt(1)))){
								mtas.add(rsMapsL.getInt(1));
								mtaMaps.put(rsMapsL.getInt(1), rsMapsL.getString(2));
							}
						}
					}
					if(qtl.equalsIgnoreCase("*")){
						//System.out.println("***************************");
						rs1C=stCen.executeQuery("select distinct tid, qtl_id from gdms_qtl_details order by qtl_id");
						while(rs1C.next()){
							//qtlId=rs1.getInt(1);
							qtl_id=qtl_id+rs1C.getInt(2)+",";	
							count=count+1;
							if(!tid.contains(rs1C.getInt(1)))
								tid.add(rs1C.getInt(1));
						}
						rs1=stLoc.executeQuery("select distinct tid, qtl_id from gdms_qtl_details order by qtl_id");
						while(rs1.next()){
							//qtlId=rs1.getInt(1);
							qtl_id=qtl_id+rs1.getInt(2)+",";	
							count=count+1;
							if(!tid.contains(rs1.getInt(1)))
								tid.add(rs1.getInt(1));
						}
						//System.out.println(count+"   "+qtl_id);
						rsMtasC=stCen.executeQuery("select * from gdms_mta");
						while(rsMtasC.next()){
							mtaList=mtaList+traitsMap.get(Integer.parseInt(rsMtasC.getString("tid")))+"!~!"+mtaMap.get(rsMtasC.getInt("marker_id"))+"!~!"+mtaMaps.get(rsMtasC.getInt("map_id"))+"!~!"+rsMtasC.getString("linkage_group")+"!~!"+rsMtasC.getFloat("position")+"!~!"+rsMtasC.getString("hv_allele")+"!~!"+rsMtasC.getInt("effect")+"!~!"+rsMtasC.getString("experiment")+"!~!"+rsMtasC.getFloat("score_value")+"!~!"+rsMtasC.getFloat("r_square")+"~~!!~~";
							mtaExists=true;
						}
						
						rsMtas=st.executeQuery("select * from gdms_mta");
						while(rsMtas.next()){
							mtaList=mtaList+traitsMap.get(Integer.parseInt(rsMtas.getString("tid")))+"!~!"+mtaMap.get(rsMtas.getInt("marker_id"))+"!~!"+mtaMaps.get(rsMtas.getInt("map_id"))+"!~!"+rsMtas.getString("linkage_group")+"!~!"+rsMtas.getFloat("position")+"!~!"+rsMtas.getString("hv_allele")+"!~!"+rsMtas.getInt("effect")+"!~!"+rsMtas.getString("experiment")+"!~!"+rsMtas.getFloat("score_value")+"!~!"+rsMtas.getFloat("r_square")+"~~!!~~";
							mtaExists=true;
						}
						//System.out.println("traitsMap:"+traitsMap);
					}else{
						String tids="";
						int tidsCount=0;
						
						String trAbbr=qtl;
						//System.out.println("testGetCvTermById(): " + term);
						//System.out.println("Test FindStandardVariablesByNameOrSynonym");
						Set<StandardVariable> standardVariables = om.findStandardVariablesByNameOrSynonym(trAbbr);
						assertTrue(standardVariables.size() == 1);
						for (StandardVariable stdVar : standardVariables) {
							System.out.println(stdVar.getId()+"   "+stdVar.getNameSynonyms()+"   "+stdVar.getName());
							tids=tids+stdVar.getId()+",";
							tidsCount++;
						}
						
						//rs1=st.executeQuery("select qtl_id from qtl_linkagemap where trait='"+qtl+"' order by qtl_id");
						/*rsM=stCen.executeQuery("select tid from tmstraits where trabbr like '"+qtl+"%' order by tid");
						while(rsM.next()){
							tids=tids+rsM.getInt(1)+",";
							tidsCount++;
						}
						rsML=stLoc.executeQuery("select tid from tmstraits where trabbr like '"+qtl+"%' order by tid");
						while(rsML.next()){
							tids=tids+rsML.getInt(1)+",";
							tidsCount++;
						}*/
						if(tidsCount>0){
						rs1C=stCen.executeQuery("select qtl_id from gdms_qtl_details where tid in("+tids.substring(0, tids.length()-1)+") order by tid");						
						while(rs1C.next()){
							//qtlId=rs1.getInt(1);
							qtl_id=qtl_id+rs1C.getInt(1)+",";	
							count=count+1;
						}
						
						
						//rs1=st.executeQuery("select qtl_id from gdms_qtl_details where trait like '"+qtl+"%' order by qtl_id");
						rs1=st.executeQuery("select qtl_id from gdms_qtl_details where tid in("+tids.substring(0, tids.length()-1)+") order by tid");
					
						while(rs1.next()){
							//qtlId=rs1.getInt(1);
							qtl_id=qtl_id+rs1.getInt(1)+",";	
							count=count+1;
						}
						//qtl=qtl+"%";
						//System.out.println("traitsMap:"+traitsMap);
						rsMtasC=stCen.executeQuery("select * from gdms_mta where tid ="+traitMap.get(qtl));
						while(rsMtasC.next()){
							mtaList=mtaList+traitsMap.get(Integer.parseInt(rsMtasC.getString("tid")))+"!~!"+mtaMap.get(rsMtasC.getInt("marker_id"))+"!~!"+mtaMaps.get(rsMtasC.getInt("map_id"))+"!~!"+rsMtasC.getString("linkage_group")+"!~!"+rsMtasC.getFloat("position")+"!~!"+rsMtasC.getString("hv_allele")+"!~!"+rsMtasC.getInt("effect")+"!~!"+rsMtasC.getString("experiment")+"!~!"+rsMtasC.getFloat("score_value")+"!~!"+rsMtasC.getFloat("r_square")+"~~!!~~";
							mtaExists=true;
						}
						
						
						rsMtas=stLoc.executeQuery("select * from gdms_mta where tid ="+traitMap.get(qtl));
						while(rsMtas.next()){
							mtaList=mtaList+traitsMap.get(Integer.parseInt(rsMtas.getString("tid")))+"!~!"+mtaMap.get(rsMtas.getInt("marker_id"))+"!~!"+mtaMaps.get(rsMtas.getInt("map_id"))+"!~!"+rsMtas.getString("linkage_group")+"!~!"+rsMtas.getFloat("position")+"!~!"+rsMtas.getString("hv_allele")+"!~!"+rsMtas.getInt("effect")+"!~!"+rsMtas.getString("experiment")+"!~!"+rsMtas.getFloat("score_value")+"!~!"+rsMtas.getFloat("r_square")+"~~!!~~";
							mtaExists=true;
						}
						
						}else{
							String ErrMsg = "Trait Name not found";
							req.getSession().setAttribute("indErrMsg", ErrMsg);
							return am.findForward("ErrMsg");
						}
						
					}
					
					
					if(count==0){						
						String ErrMsg = "Trait Name not found";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}
					
					req.getSession().setAttribute("qtl", qtl);
					//System.out.println("traitsMap=:"+traitsMap);
					
					rsQC=stCen.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_id,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.tid, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square, gdms_qtl_details.interactions, gdms_qtl_details.clen, gdms_qtl_details.se_additive, gdms_qtl_details.hv_parent, gdms_qtl_details.hv_allele, gdms_qtl_details.lv_parent, gdms_qtl_details.lv_allele FROM gdms_qtl_details, gdms_qtl, gdms_map WHERE gdms_qtl_details.qtl_id IN("+qtl_id.substring(0,qtl_id.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
					while(rsQC.next()){
						//System.out.println(",,,,,,,CEN,,,,,,,,,:"+rsQC.getInt(6));
						//strData=strData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+";;;";
						qtlDataList.add(rsQC.getString(1)+"!~!"+mtaMaps.get(rsQC.getInt(2))+"!~!"+rsQC.getString(3)+"!~!"+rsQC.getFloat(4)+"!~!"+rsQC.getFloat(5)+"!~!"+traitsMap.get(rsQC.getInt(6))+"!~!"+rsQC.getString(7)+"!~!"+rsQC.getString(8)+"!~!"+rsQC.getString(9)+"!~!"+rsQC.getFloat(10)+"!~!"+rsQC.getFloat(11)+"!~!"+rsQC.getString(13)+"!~!"+rsQC.getFloat(12)+"!~!"+rsQC.getString(14)+"!~!"+rsQC.getString(15)+"!~!"+rsQC.getString(16)+"!~!"+rsQC.getString(17)+"!~!"+rsQC.getString(18)+"!~!"+rsQC.getString(19));
					}		
					
					rs=stLoc.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_id,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.tid, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square, gdms_qtl_details.interactions, gdms_qtl_details.clen, gdms_qtl_details.se_additive, gdms_qtl_details.hv_parent, gdms_qtl_details.hv_allele, gdms_qtl_details.lv_parent, gdms_qtl_details.lv_allele FROM gdms_qtl_details, gdms_qtl, gdms_map WHERE gdms_qtl_details.qtl_id IN("+qtl_id.substring(0,qtl_id.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
					while(rs.next()){
						//System.out.println(",,,,,,,,,Loc ,,,,,,,,:"+rs.getInt(6));
						//strData=strData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+";;;";
						qtlDataList.add(rs.getString(1)+"!~!"+mtaMaps.get(rs.getInt(2))+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+traitsMap.get(rs.getInt(6))+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+"!~!"+rs.getString(14)+"!~!"+rs.getString(15)+"!~!"+rs.getString(16)+"!~!"+rs.getString(17)+"!~!"+rs.getString(18)+"!~!"+rs.getString(19));
					}					
					
					
					for(int a=0; a<qtlDataList.size();a++){
						String[] argTr=qtlDataList.get(a).toString().split("!~!");
						if(map.containsKey(argTr[5])){
							finalQTLList=finalQTLList+argTr[0]+"!~!"+argTr[1]+"!~!"+argTr[2]+"!~!"+argTr[3]+"!~!"+argTr[4]+"!~!"+argTr[5]+"!~!"+argTr[6]+"!~!"+argTr[7]+"!~!"+argTr[8]+"!~!"+argTr[9]+"!~!"+argTr[10]+"!~!"+argTr[11]+"!~!"+argTr[12]+"!~!"+map.get(argTr[5])+"!~!"+argTr[13]+"!~!"+argTr[14]+"!~!"+argTr[15]+"!~!"+argTr[16]+"!~!"+argTr[17]+"!~!"+argTr[18]+";;;";
						}else{
							finalQTLList=finalQTLList+argTr[0]+"!~!"+argTr[1]+"!~!"+argTr[2]+"!~!"+argTr[3]+"!~!"+argTr[4]+"!~!"+argTr[5]+"!~!"+argTr[6]+"!~!"+argTr[7]+"!~!"+argTr[8]+"!~!"+argTr[9]+"!~!"+argTr[10]+"!~!"+argTr[11]+"!~!"+argTr[12]+"!~! "+"!~! "+argTr[13]+"!~!"+argTr[14]+"!~!"+argTr[15]+"!~!"+argTr[16]+"!~!"+argTr[17]+"!~!"+argTr[18]+";;;";
						}						
					}	
						
					
					//System.out.println("mtaList=:"+mtaList);
					//System.out.println("finalQTLList=:"+finalQTLList);
					req.getSession().setAttribute("strdata",finalQTLList);
					req.getSession().setAttribute("mta", mtaExists);
					req.getSession().setAttribute("mtaList", mtaList);
					//System.out.println("strData="+strData);	
					str="retTrait";					
				}else if(opType.equals("maps")){					
					//String map=df.get("maps").toString();
					//rs=stmt.executeQuery("select map_type from gdms_map where );
					//if(format.contains("CMTV")){
					ArrayList strDataM=new ArrayList();
					//rs1=stmtR.executeQuery("SELECT  MAX(`mapping_data`.`start_position`) AS `max` , `mapping_data`.`linkage_group` AS Linkage_group, `mapping_data`.`linkagemap_name` AS map FROM `mapping_data` WHERE mapping_data.linkagemap_name LIKE ('"+qtl+"%') GROUP BY UCASE(`mapping_data`.`linkage_group`)");
					int countF=0;
					if(qtl.equalsIgnoreCase("*")){
						/*System.out.println("SELECT  COUNT(DISTINCT `gdms_mapping_data`.`marker_id`) AS `marker_count` ,MAX(`gdms_mapping_data`.`start_position`) AS `max` , `gdms_mapping_data`.`linkage_group` AS Linkage_group, `gdms_mapping_data`.`map_name` AS map , gdms_mapping_data.map_unit AS map_unit FROM `gdms_mapping_data`, `gdms_map` WHERE gdms_mapping_data.map_id=gdms_map.map_id GROUP BY UCASE(`gdms_mapping_data`.`linkage_group`),UCASE(gdms_mapping_data.map_name) ORDER BY `gdms_mapping_data`.`map_name`, `gdms_mapping_data`.`linkage_group`");
						rs1=stmtR.executeQuery("SELECT  COUNT(DISTINCT `gdms_mapping_data`.`marker_id`) AS `marker_count` ,MAX(`gdms_mapping_data`.`start_position`) AS `max` , `gdms_mapping_data`.`linkage_group` AS Linkage_group, `gdms_mapping_data`.`map_name` AS map , gdms_mapping_data.map_unit AS map_unit FROM `gdms_mapping_data`, `gdms_map` WHERE gdms_mapping_data.map_id=gdms_map.map_id GROUP BY UCASE(`gdms_mapping_data`.`linkage_group`),UCASE(gdms_mapping_data.map_name) ORDER BY `gdms_mapping_data`.`map_name`, `gdms_mapping_data`.`linkage_group`");
						*/
						rsC=stCen.executeQuery("SELECT  COUNT(DISTINCT gdms_markers_onmap.marker_id) AS marker_count ,MAX(gdms_markers_onmap.start_position) AS maxPos , gdms_markers_onmap.linkage_group AS Linkage_group, gdms_map.map_name AS map , gdms_map.map_unit AS map_unit, gdms_map.map_id as map_id FROM gdms_markers_onmap, gdms_map WHERE gdms_map.map_id=gdms_markers_onmap.map_id GROUP BY UCASE(gdms_markers_onmap.linkage_group),UCASE(gdms_map.map_name) ORDER BY gdms_map.map_name, gdms_markers_onmap.linkage_group");
						while(rsC.next()){
							strDataM.add(rsC.getInt(1)+"!~!"+rsC.getFloat(2)+"!~!"+rsC.getString(3)+"!~!"+rsC.getString(4)+"!~!"+rsC.getString(5)+"!~!"+rsC.getInt(6));
							countF=countF+1;
							
						}
						rsL=stLoc.executeQuery("SELECT  COUNT(DISTINCT gdms_markers_onmap.marker_id) AS marker_count ,MAX(gdms_markers_onmap.start_position) AS maxPos , gdms_markers_onmap.linkage_group AS Linkage_group, gdms_map.map_name AS map , gdms_map.map_unit AS map_unit, gdms_map.map_id as map_id FROM gdms_markers_onmap, gdms_map WHERE gdms_map.map_id=gdms_markers_onmap.map_id GROUP BY UCASE(gdms_markers_onmap.linkage_group),UCASE(gdms_map.map_name) ORDER BY gdms_map.map_name, gdms_markers_onmap.linkage_group");
						while(rsL.next()){
							strDataM.add(rsL.getInt(1)+"!~!"+rsL.getFloat(2)+"!~!"+rsL.getString(3)+"!~!"+rsL.getString(4)+"!~!"+rsL.getString(5)+"!~!"+rsL.getInt(6));
							countF=countF+1;							
						}
						
						
					}else{
						//rs1=stmtR.executeQuery("SELECT  COUNT(DISTINCT `gdms_mapping_data`.`marker_id`) AS `marker_count` ,MAX(`gdms_mapping_data`.`start_position`) AS `max` , `gdms_mapping_data`.`linkage_group` AS Linkage_group, `gdms_mapping_data`.`map_name` AS map , gdms_mapping_data.map_unit AS map_unit FROM `gdms_mapping_data`, `gdms_map` WHERE gdms_mapping_data.map_id=gdms_map.map_id and lower(gdms_mapping_data.map_name) LIKE ('"+qtl.toLowerCase()+"%') GROUP BY UCASE(`gdms_mapping_data`.`linkage_group`),UCASE(gdms_mapping_data.map_name) ORDER BY `gdms_mapping_data`.`map_name`, `gdms_mapping_data`.`linkage_group`");
						rsC=stCen.executeQuery("SELECT  COUNT(DISTINCT gdms_markers_onmap.marker_id) AS marker_count ,MAX(gdms_markers_onmap.start_position) AS maxPos , gdms_markers_onmap.linkage_group AS Linkage_group, gdms_map.map_name AS map , gdms_map.map_unit AS map_unit, gdms_map.map_id as map_id FROM gdms_markers_onmap, gdms_map WHERE gdms_markers_onmap.map_id=gdms_map.map_id AND LOWER(gdms_map.map_name) LIKE ('"+qtl.toLowerCase()+"%') GROUP BY UCASE(gdms_markers_onmap.linkage_group),UCASE(gdms_map.map_name) ORDER BY gdms_map.map_name, gdms_markers_onmap.linkage_group");
						while(rsC.next()){
							strDataM.add(rsC.getInt(1)+"!~!"+rsC.getFloat(2)+"!~!"+rsC.getString(3)+"!~!"+rsC.getString(4)+"!~!"+rsC.getString(5)+"!~!"+rsC.getInt(6));
							countF=countF+1;
							
						}
						rsL=stLoc.executeQuery("SELECT  COUNT(DISTINCT gdms_markers_onmap.marker_id) AS marker_count ,MAX(gdms_markers_onmap.start_position) AS maxPos , gdms_markers_onmap.linkage_group AS Linkage_group, gdms_map.map_name AS map , gdms_map.map_unit AS map_unit, gdms_map.map_id as map_id FROM gdms_markers_onmap, gdms_map WHERE gdms_markers_onmap.map_id=gdms_map.map_id AND LOWER(gdms_map.map_name) LIKE ('"+qtl.toLowerCase()+"%') GROUP BY UCASE(gdms_markers_onmap.linkage_group),UCASE(gdms_map.map_name) ORDER BY gdms_map.map_name, gdms_markers_onmap.linkage_group");
						while(rsL.next()){
							strDataM.add(rsL.getInt(1)+"!~!"+rsL.getFloat(2)+"!~!"+rsL.getString(3)+"!~!"+rsL.getString(4)+"!~!"+rsL.getString(5)+"!~!"+rsL.getInt(6));
							countF=countF+1;
							
						}
					}
					/*while(rs1.next()){
						strDataM.add(rs1.getInt(1)+"!~!"+rs1.getFloat(2)+"!~!"+rs1.getString(3)+"!~!"+rs1.getString(4)+"!~!"+rs1.getString(5));
						countF=countF+1;
					}*/
					//System.out.println("strDataM="+strDataM);
					if(countF==0){						
						String ErrMsg = "Map Name not found";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}
					String[] strArr=strDataM.get(0).toString().split("!~!");
					
					String chr=strArr[3];
					mCount=Integer.parseInt(strArr[0]);
					distance=Float.parseFloat(strArr[1]);
					count=0;
					int mc=0;
					float d=0;
					String distSTR="";
					String mType="";
					int mpId=0;
					//System.out.println("strDataM.size()=:"+strDataM.size());
					for(int a=0;a<strDataM.size();a++){	
						String mapType="";
						int mapId=0;
						String[] str1=strDataM.get(a).toString().split("!~!");		
						//System.out.println(" a="+a+" ,,,,markerCount="+str1[0]+"    ;startPosition="+str1[1]+"  ;LinkageGroup="+str1[2]+"  ;MapName="+str1[3]);
						if(str1[3].equals(chr)){
							mc=mc+Integer.parseInt(str1[0]);
							d=d+Float.parseFloat(str1[1]);	
							mType=str1[4];
							mpId=Integer.parseInt(str1[5]);
							//System.out.println("..mc="+mc+"   d:"+d);
							if(a==(strDataM.size()-1)){
								//System.out.println("IF in IF "+mapType);
								mCount=mc;
								distance=d;
								mapType=mType;
								mapId=mpId;
								//System.out.println("IF in IF "+mapType+ ".... "+chr);
								distSTR=distSTR+mCount+"!~!"+chr+"!~!"+distance+"!~!"+mapType+"!~!"+mapId+";;";								
							}
						}else if(!(str1[3].equals(chr))){							
							mCount=mc;
							distance=d;
							mapType=mType;
							mapId=mpId;
							//System.out.println("else IF in IF "+mapType+ ".... "+chr);
							distSTR=distSTR+mCount+"!~!"+chr+"!~!"+distance+"!~!"+mapType+"!~!"+mapId+";;";
							mc=0;
							d=0;
							mType="";
							mpId=0;
							chr=str1[3];
							a=a-1;
						}						
					}
					session.setAttribute("mapsSTR", distSTR);
					//System.out.println("........&&&&&&&&&&&&........"+distSTR);			
					session.setAttribute("type", "map");
					
					str="map";
						
					}
				
			}
			
			//System.out.println(str);
			/*if(rsC!=null) rsC.close(); if(rsL!=null) rsL.close();if(rsMtas!=null) rsMtas.close();
			if(rsC!=null) stCen.close();if(stLoc!=null) stLoc.close();
			
			if(rs2!=null) rs2.close(); if(rsM!=null) rsM.close(); if(rsMaps!=null) rsMaps.close(); if(rs!=null) rs.close(); if(rs1!=null) rs1.close(); if(rsMp!=null) rsMp.close(); if(rsN!=null) rsN.close(); if(rsQC!=null) rsQC.close();
			if(rsDetL!=null) rsDetL.close(); if(rsDet!=null) rsDet.close(); if(rsML!=null) rsML.close(); if(rsMpL!=null) rsMpL.close(); if(rsMapsL!=null) rsMapsL.close(); if(rs1C!=null) rs1C.close(); if(rsMtasC!=null) rsMtasC.close(); 
			
			if(rsC!=null) st.close(); if(stmtN!=null) stmtN.close(); if(stA!=null) stA.close(); if(stC!=null) stC.close();  if(stM!=null) stM.close();  if(stmtC!=null) stmtC.close(); if(stmtA!=null) stmtA.close(); 
			if(rsC!=null) stmtM.close(); if(stmtMp!=null) stmtMp.close(); if(stmt!=null)  stmt.close(); if(stmtR!=null) stmtR.close(); if(stmttest!=null) stmttest.close();
			if(stAC!=null) stAC.close(); if(stAL!=null) stAL.close(); if(stCC!=null) stCC.close(); if(stCL!=null) stCL.close(); if(stML!=null) stML.close(); if(stMC!=null) stMC.close(); if(stmtG!=null) stmtG.close();
			*/
			if(conn!=null) conn.close(); if(con!=null) con.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		      		if(conn!=null) conn.close();
		      	
		      		factory.close(); 
		      		
		      		/*long time = System.currentTimeMillis();
		      	  	System.gc();
		      	  	System.out.println("It took " + (System.currentTimeMillis()-time) + " ms");*/
		         }catch(Exception e){System.out.println(e);}
			}
		return am.findForward(str);
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
