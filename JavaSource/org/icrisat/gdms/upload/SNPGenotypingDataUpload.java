/**
 * 
 */
package org.icrisat.gdms.upload;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

public class SNPGenotypingDataUpload {
	String strupl="";
	
	//getUpload method is used to insert the SNP Genotyping data from SNPGenotyping Template to database.	
	static Map<String, ArrayList<Integer>> hashMap = new HashMap<String,  ArrayList<Integer>>();  
	int gidCount=0;
	int gCount=0;int dataCount=0;
	int genotypeCount=0;
	String gNames="";
	String[] gids=null;
	String[] genotype=null;
	String[] markers=null;
	String alertGN="no";
    String alertGID="no";
    String notMatchingData="";
    String notMatchingGIDS="";
    String notMatchingDataDB="";
    String notMatchingGIDSDB="";
    String notMatchingDataExists="";
	int maxMid=0;
	String ErrMsg="";
	int size=0;
	ArrayList NamesList=new ArrayList();
	List genoDataMarkers = new ArrayList();
	ArrayList gidsAList=new ArrayList();
	List<List<String>> genoData = new ArrayList<List<String>>(); 
	int intAC_ID = 0;
	int intRMarkerId=1;
	int mid=0;
	String charData="";
	String marker_type="SNP";
	String marker="";
	String datatype="char";
	String dataset_type="SNP";
	String ins="";
	String strIns="";
	String strPI="";
	String strEmail="";
	String strDesc="";
	String strDatasetName="";
	String strGenus="";
	String strSpecies="";
	String strMissData="";
	String strDate="";
	String IncPerson="";
	String PurposeStudy="";
	List<String> str=new ArrayList<String>();
	ArrayList result1=new ArrayList();
	java.sql.Connection conn;
	java.sql.Connection con;
	
	Properties prop=new Properties();
	public String getUpload(HttpServletRequest request, String fname) throws SQLException{
		final Session session;		
		final Transaction tx;
		
		session = HibernateSessionFactory.currentSession();
		tx=session.beginTransaction();
		
		ManagerFactory factory =null;
		DatasetBean ub=new DatasetBean();
		//UsersBean u=new UsersBean();
		GenotypeUsersBean usb=new GenotypeUsersBean();	
		ConditionsBean ubConditions=new ConditionsBean();
		
		HttpSession httpsession = request.getSession();
		int g1=0;
		String line; 
		String[] datavalue = null;
		
		try{
			result1.clear();
			prop.load(new FileInputStream(httpsession.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
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
			
			ResultSet rsLoc=null;
			ResultSet rsCen=null; ResultSet rsML=null; ResultSet rsMC=null;
			DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			factory = new ManagerFactory(local, central);
			
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			GenotypicDataManager gdms=factory.getGenotypicDataManager();
			MaxIdValue uptMethod=new MaxIdValue();
			int maxDatasetId=uptMethod.getMaxIdValue("dataset_id","gdms_dataset",session);
			//int dataset_id=maxDatasetId+1;
			int dataset_id=maxDatasetId-1;
			//System.out.println("....................:"+dataset_id);
			
				BufferedReader bReader = new BufferedReader(new FileReader(fname)); 
				while ((line = bReader.readLine()) != null) {
					datavalue =line.split("\t");	
									
					int len=datavalue.length;		
					
					if(line.startsWith("Institute")){
						//System.out.println("INS="+len);
						if(len==2) strIns=datavalue[1];
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Institute";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Institute";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}
					/*if((len==2)&&(datavalue[0].startsWith("Institute"))){
						strIns=datavalue[1]; 
					}*/
					if(line.startsWith("PI")){
						//System.out.println("PI"+len+"   line="+line);
						if(len==2) strPI=datavalue[1];
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the PI ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line PI";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}		
					//if((len==2)&&(datavalue[0].contains("PI"))) strPI=datavalue[1];
					if(line.startsWith("Email")){
						//System.out.println("Email"+len);
						if(len==2) strEmail=datavalue[1];
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Email ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Email";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}			
					//if((len==2)&&(datavalue[0].contains("Email"))) strEmail=datavalue[1];
					if(line.startsWith("Incharge_Person")){
						//System.out.println("INC Per="+len);
						if(len==2){
							//System.out.println("******************IP"+len);
							IncPerson=datavalue[1];
						}else if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Incharge Person ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}else if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Incharge_Person";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}
					if(line.startsWith("Dataset_Name")){
						//System.out.println("INC Per="+len);
						if(len==2){
							//System.out.println("******************IP"+len);
							strDatasetName=datavalue[1];
							
							rsLoc=stLoc.executeQuery("select dataset_name from gdms_dataset where dataset_name='"+strDatasetName+"'");
							while(rsLoc.next()){
								result1.add(rsLoc.getString(1));						
							}
							//System.out.println("select traitid, trabbr from tmstraits where trabbr in ("+traits+")");
							rsCen=stCen.executeQuery("select dataset_name from gdms_dataset where dataset_name='"+strDatasetName+"'");
							while(rsCen.next()){
								result1.add(rsCen.getString(1));	
							}
							
							//Query rsDatasetNames=session.createQuery("from DatasetBean where dataset_name ='"+strDatasetName+"'");							
							//List result1= rsDatasetNames.list();
							//System.out.println(".............:"+result1.size());
							if(result1.size()>0){
								ErrMsg = "Dataset Name already exists.";
								request.getSession().setAttribute("indErrMsg", ErrMsg);							
								return "ErrMsg";
							}
							if(strDatasetName.length()>30){
								ErrMsg = "Dataset Name value exceeds max char size.";
								request.getSession().setAttribute("indErrMsg", ErrMsg);							
								return "ErrMsg";
							}
						}else if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Dataset Name ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}else if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Dataset Name";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}	
					//if((len==2)&&(datavalue[0].contains("Incharge_Person"))) IncPerson=datavalue[1];    			
					if(line.startsWith("Purpose_Of_Study")){
						//System.out.println("P OF Study"+len);
						if(len==2){ 
							//System.out.println("len"+len);
							PurposeStudy=datavalue[1];
							}
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Purpose_Of_Study";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Purpose_Of_Study";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}
					//if((len==2)&&(datavalue[0].contains("Purpose_Of_Study"))) PurposeStudy=datavalue[1];			
					if(line.startsWith("Dataset_Description")){
						if(len==2) strDesc=datavalue[1];		
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Description";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Description";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}
					//if((len==2)&&(datavalue[0].contains("Description"))) strDesc=datavalue[1];
					if(line.startsWith("Genus")){
						if(len==2) strGenus=datavalue[1];	
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Genus";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Genus";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}	
					//if((len==2)&&(datavalue[0].contains("Genus"))) strGenus=datavalue[1];
					if(line.startsWith("Species")){
						if(len==2) strSpecies=datavalue[1];
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Species";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Species";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}				
					//if((len==2)&&(datavalue[0].contains("Species"))) strSpecies=datavalue[1];
					if(line.startsWith("Missing_Data")){
						if(len==2) strMissData=datavalue[1];
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Missing_Data";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Missing_Data";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}			
					//if((len==2)&&(datavalue[0].contains("Missing_Data"))) strMissData=datavalue[1];
					if(line.startsWith("Creation_Date")){
						if(len==2){ 
							boolean dFormat=uptMethod.isValidDate(datavalue[1]);
							if(dFormat==false){
								ErrMsg = "Creation_Date should be in yyyy-mm-dd format";
								request.getSession().setAttribute("indErrMsg", ErrMsg);
								return "ErrMsg";
							}else{
								strDate=datavalue[1];
							}
							
						}
						if(len==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Creation_Date";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(len>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Creation_Date";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						//if(strDate)
					}
					
					if((len>2)&&(datavalue[0].startsWith("gid's"))){					
						len=datavalue.length;
						gidCount=len;
						for(int g=1;g<len;g++){						
							gids=datavalue;							
						}					
					}
						
					
					//System.out.println(gids);
					if((len>2)&&(datavalue[0].startsWith("Marker\\Genotype"))){	
						len=datavalue.length;
						gCount=len;
						for(int g=1;g<len;g++){
							genotype=datavalue;							
						}					
					}
					
					if((len>2)&&(!(datavalue[0].equals("Marker\\Genotype")))&&(!(datavalue[0].equals("gid's")))){
						dataCount=len;
						genoData.add(Arrays.asList(datavalue)); 			
						
						 genoDataMarkers.add(datavalue[0]);
					}			
				}
				//System.out.println("gidCount="+gidCount+"      gCount="+gCount);
				if(gidCount<gCount){
					ErrMsg = "The number of GIDs is less than the number of Germplasm names provided";
					request.getSession().setAttribute("indErrMsg", ErrMsg);
					return "ErrMsg";
				}else if(gCount<gidCount){
					ErrMsg = "The number of GIDs is more than the number of Germplasm names provided";
					request.getSession().setAttribute("indErrMsg", ErrMsg);
					return "ErrMsg";
				}
				
				ArrayList NamesList=new ArrayList();
				
				
				//int user_id=uptMethod.getUserId("userid", "users", "uname", session,strPI);
				String username=request.getSession().getAttribute("user").toString();
				int user_id=uptMethod.getUserId("userid", "users", "uname", session,username);
				
				
				//System.out.println("user_id="+user_id);
				
				if(gids.length!=genotype.length){
					strupl="notInserted";
				}if(gids.length!=genotype.length){
					strupl="notInserted";
				}else if(gids.length==genotype.length){
					String gidsForQuery = "";
					HashMap<String, Integer> GIDsMap = new HashMap<String, Integer>();
					
					ArrayList gidNamesList=new ArrayList();
					
					 //SortedMap GIDsMap = new TreeMap();
		            for(int d=1;d<gids.length;d++){	               
		            	gidsForQuery = gidsForQuery + gids[d]+",";
		            	if(!gidNamesList.contains(Integer.parseInt(gids[d])))
							gidNamesList.add(Integer.parseInt(gids[d])+","+genotype[d]);
		            	GIDsMap.put(genotype[d], Integer.parseInt(gids[d]));
		            	if(!gidsAList.contains(Integer.parseInt(gids[d])))
							gidsAList.add(gids[d]);
		            	
		            	NamesList.add(genotype[d]);
		            }
		            gidsForQuery=gidsForQuery.substring(0, gidsForQuery.length()-1);
		            
		           // System.out.println("....:"+gidsAList);
		            for(int d=1;d<genotype.length;d++){	               
		            	gNames = gNames +"'"+genotype[d]+"',";
		            	
		            }
		            gNames=gNames.substring(0, gNames.length()-1);
		            
		            //System.out.println("GIDsMap.."+GIDsMap);
		            //Map<Object, String> sortedMap = new TreeMap<Object, String>(GIDsMap);
		            //System.out.println("%%%%%%%%%%%%%sortedMap=:"+sortedMap);
		            //HashMap<Object, String> map = new HashMap<Object, String>();
		            //ArrayList lstGIDs=uptMethod.getGIds("gid, germplasm_name", "germplasm_temp", "gid", session, gidsForQuery);
		            //ArrayList lstGIDs=uptMethod.getGIds("gid, nval", "names", "gid", session, gidsForQuery);
		            
		            ArrayList gidsDBList = new ArrayList();
					ArrayList gNamesDBList = new ArrayList();
					hashMap.clear();
					for(int n=0;n<NamesList.size();n++){
						List<Germplasm> germplasmList = manager.getGermplasmByName(NamesList.get(n).toString(), 0, new Long(manager.countGermplasmByName(NamesList.get(n).toString(), Operation.EQUAL)).intValue(), Operation.EQUAL);
						for (Germplasm g : germplasmList) {
				        	if(!(gidsDBList.contains(g.getGid()))){
				        		gidsDBList.add(g.getGid());
				        		gNamesDBList.add(NamesList.get(n).toString());
				        		addValues(NamesList.get(n).toString(), g.getGid());					        		
				        	}				        	
				           //System.out.println("  " + g.getGid());
				        }
				        System.out.println(n+":"+NamesList.get(n).toString()+"   "+hashMap.get(NamesList.get(n).toString()));
					}
		           
		           if(gidsDBList.size()==0){
		        	   alertGID="yes";
		        	   size=0;
		           }
		           int gidToCompare=0;
		           String gNameToCompare="";
		           //String gNameFromMap="";
		           ArrayList gNameFromMap=new ArrayList();
		           if(gidsDBList.size()>0){			           
		        	   for(int n=0;n<NamesList.size();n++){
		        		   if(gNamesDBList.contains(NamesList.get(n))){
		        			   if(!(hashMap.get(NamesList.get(n).toString()).contains(GIDsMap.get(NamesList.get(n).toString())))){
		        				   notMatchingData=notMatchingData+NamesList.get(n)+"   "+GIDsMap.get(NamesList.get(n).toString())+"\n\t";
		        				   
		        				   notMatchingDataDB=notMatchingDataDB+NamesList.get(n)+"="+hashMap.get(NamesList.get(n))+"\t";
				        		   alertGN="yes";
		        			   }
		        		   }else{
		        			   //int gid=GIDsMap.get(NamesList.get(n).toString());
		        			   alertGID="yes";
			        		   size=hashMap.size();
			        		   notMatchingGIDS=notMatchingGIDS+NamesList.get(n).toString()+", ";
		        		   }
		        	   }		        	   
		           }
		           if((alertGN.equals("yes"))&&(alertGID.equals("no"))){
		        	   //String ErrMsg = "GID(s) ["+notMatchingGIDS.substring(0,notMatchingGIDS.length()-1)+"] of Germplasm(s) ["+notMatchingData.substring(0,notMatchingData.length()-1)+"] being assigned to ["+notMatchingDataExists.substring(0,notMatchingDataExists.length()-1)+"] \n Please verify the template ";
		        	   ErrMsg = "Please verify the name(s) provided \t "+notMatchingData+" which do not match the GID(s) present in the database"+notMatchingDataDB;
		        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
		        	   return "ErrMsg";	 
		           }
		           if((alertGID.equals("yes"))&&(alertGN.equals("no"))){	        	   
		        	   if(size==0){
		        		   ErrMsg = "The Germplasm(s) provided do not exist in the database. \n Please upload the relevant germplasm information through the Fieldbook ";
		        	   }else{
		        		   ErrMsg = "The following Germplasm(s) provided do not exist in the database. \n Please upload the relevant germplasm information through the Fieldbook \n \t"+notMatchingGIDS;
		        		   //ErrMsg = "Please verify the GID/Germplasm(s) provided as some of them do not exist in the database. \n Please upload germplasm information into GMS ";
		        	   }	        	   
		        	   //ErrMsg = "Please verify the following GID/Germplasm(s) doesnot exists. \n Upload germplasm Information into GMS \n\t"+notMatchingGIDS;
		        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
		        	   return "ErrMsg";
		           }
				
		           if((alertGID.equals("yes"))&&(alertGN.equals("yes"))){
		        	   ErrMsg = "The following Germplasm(s) provided do not exist in the database. \n Please upload the relevant germplasm information through the Fieldbook \n \t"+notMatchingGIDS+" \n Please verify the name(s) provided "+notMatchingData+" which do not match the GIDS(s) present in the database "+notMatchingDataDB;
		        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
		        	   return "ErrMsg";	 
		           }		           
		           
				}
				/*System.out.println("gids= "+gids.length);
				System.out.println("genotype = "+genotype.length);
				System.out.println("Data = "+genoData);
				System.out.println("Done");*/
				
				/** writing to 'dataset' table **/
				
				ub.setDataset_id(dataset_id);
				ub.setDataset_name(strDatasetName);
				ub.setDataset_desc(strDesc);
				ub.setDataset_type(dataset_type);
				ub.setGenus(strGenus);
				ub.setSpecies(strSpecies);
				ub.setUpload_template_date(strDate);					
				ub.setDatatype(datatype);
				ub.setMissing_data(strMissData);
				
				ub.setInstitute(strIns);
				ub.setPrincipal_investigator(strPI);
				
				
				//System.out.println("dataset id = ");
				session.save(ub);
				
				//*************  dataset_users*************
				usb.setDataset_id(dataset_id);
				usb.setUser_id(user_id);
				
				session.save(usb);
				
				
				SortedMap mapN = new TreeMap();
				//System.out.println(",,,,,,,,,,,,,,,,,gNames="+gNames);
				ArrayList finalList =new ArrayList();
				ArrayList gidL=new ArrayList();
				/*
				 * getting nids with gid and nval for inserting into gdms_acc_metadataset table			
				*/
				Name names = null;
				for(int n=0;n<gidsAList.size();n++){
					/*names = manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString(), GetGermplasmByNameModes.STANDARDIZED);
					if(names.getNid().equals("")){
						System.out.println("............ returned null");
					}*/
					names = manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString(), GetGermplasmByNameModes.STANDARDIZED);
					if(names==null){
						names=manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString(), GetGermplasmByNameModes.NORMAL);
					}				
					if(!gidL.contains(names.getGermplasmId()))
		            	gidL.add(names.getGermplasmId());
		            mapN.put(names.getGermplasmId(), names.getNid());
					//System.out.println(gidsAList.get(n).toString()+","+NamesList.get(n).toString()+"   nid=:"+names.getNid());//for (Name name : names) {					
				}
				
				
		        for(int a=0;a<gidsAList.size();a++){
		        	int gid1=Integer.parseInt(gidsAList.get(a).toString());
		        	if(gidL.contains(gid1)){
		        		finalList.add(gid1+"~!~"+mapN.get(gid1));	
		        	}
		        }
	           // System.out.println("******************  "+finalList);
				
				
				//*********** dataset_details;
				/*ubConditions.setDataset_id(dataset_id);
				ubConditions.setMissing_data(strMissData);
				
				session.saveOrUpdate(ubConditions);*/
				
				
				/*****  modified code for performance  *****/
				String marForQuery = "";
	            for(int d=0;d<genoDataMarkers.size();d++){
	               
	                marForQuery = marForQuery +"'"+ genoDataMarkers.get(d)+"',";
	            }
	            marForQuery=marForQuery.substring(0, marForQuery.length()-1);
	         
	            HashMap<String, Object> markersMap = new HashMap<String, Object>();
	            
	            
	            List lstMarkers = new ArrayList();
	            //ArrayList lstMarIdNames=uptMethod.getMarkerIds("marker_id, marker_name", "gdms_marker", "marker_name", session, marForQuery);
	            //System.out.println("select distinct marker_id, marker_name from gdms_marker where Lower(marker_name) in ("+marForQuery.toLowerCase()+")");
	            rsML=stLoc.executeQuery("select distinct marker_id, marker_name from gdms_marker where Lower(marker_name) in ("+marForQuery.toLowerCase()+")");
	            rsMC=stCen.executeQuery("select distinct marker_id, marker_name from gdms_marker where Lower(marker_name) in ("+marForQuery.toLowerCase()+")");
	            
	            while(rsMC.next()){
	            	//lstMarIdNames.add(rsMC.getString(2)+":"+rsMC.getString(1));
	            	lstMarkers.add(rsMC.getString(2));
	            	markersMap.put(rsMC.getString(2), rsMC.getInt(1));		
	            }
	            while(rsML.next()){
	            		
	            	if(!lstMarkers.contains(rsML.getString(2))){
	            		lstMarkers.add(rsML.getString(2));
	            		//lstMarIdNames.add(rsML.getString(2)+":"+rsML.getString(1));
	            	}
	            	markersMap.put(rsML.getString(2), rsML.getInt(1));	
	            }    
	            
	            /*for(int w=0;w<lstMarIdNames.size();w++){
	                 Object[] strMareO= (Object[])lstMarIdNames.get(w);
	                 lstMarkers.add(strMareO[1]);
	                 String strMa123 = (String)strMareO[1];
	                 map.put(strMa123, strMareO[0]);
	                 
	            }*/
	            
	          //System.out.println("..."+lstMarkers);
	           maxMid=uptMethod.getMaxIdValue("marker_id","gdms_marker",session);		
	           /*******************   END   *****************************/		
	           intAC_ID=uptMethod.getMaxIdValue("ac_id","gdms_char_values",session);		
	           //intAC_ID=intAC_ID+1;
	           intAC_ID=intAC_ID-1;
	           //ArrayList mids=new ArrayList();
	           ArrayList midsList = new ArrayList();
				SortedMap mids=new TreeMap();
				//int intRMarkerId=0;
	           for(int m=0;m<genoData.size();m++){
	        	   MarkerInfoBean mb=new MarkerInfoBean();				
					str=genoData.get(m);						
					//System.out.println(str);
					for(int s=0;s<1;s++){
						if(s==0){
							marker=str.get(0);							
							/*************   Modified for performance  *******************/						
							if(lstMarkers.contains(marker)){
								intRMarkerId=(Integer)(markersMap.get(marker));
								mids.put(marker, intRMarkerId);
								midsList.add(intRMarkerId);								
							}else{
								//maxMid=maxMid+1;
								maxMid=maxMid-1;
								intRMarkerId=maxMid;
								mids.put(marker, intRMarkerId);
								midsList.add(intRMarkerId);	
								
								mb.setMarkerId(intRMarkerId);
								mb.setMarker_type(dataset_type);
								mb.setMarker_name(marker.toString());
								mb.setSpecies(strSpecies);
								
								session.save(mb);
								if (m % 1 == 0){
									session.flush();
									session.clear();
								}
							}						
						}
						
					}
	        	   
	        	   
	           }
	           
	           String finalAlleleCall="";
	           int marker_id=0;
				for(int d=0;d<genoData.size();d++){					
					MarkerInfoBean mb=new MarkerInfoBean();				
					str=genoData.get(d);						
					//System.out.println(str.size()+"   "+str);
					for(int s=0;s<str.size();s++){
						if(s==0){
							marker=str.get(0);					
							marker_id=Integer.parseInt(mids.get(marker).toString());
						}	
						
						if(s!=0){
							CharArrayBean chb=new CharArrayBean();
							CharArrayCompositeKey cack = new CharArrayCompositeKey();
							//ReferenceBean ref=new ReferenceBean();					
							//ReferenceCompositeKey rbck = new ReferenceCompositeKey();							
							MarkerMetaDataBean mmb=new MarkerMetaDataBean();
							
							//**************** writing to char_values tables........
							cack.setDataset_id(dataset_id);
							cack.setAc_id(intAC_ID);
							chb.setComKey(cack);
	                        //System.out.println("........................"+str.get(s));
							//System.out.println("str="+str.get(s).length());
							
							if(str.get(s).length()>2){
								String charStr=str.get(s);
								if(charStr.contains(":")){
									String str1="";
									String str2="";
									//String charStr=str.get(s);
									str1=charStr.substring(0, charStr.length()-2);
									str2=charStr.substring(2, charStr.length());
									charData=str1+"/"+str2;
								}else if(charStr.contains("/")){
									charData=charStr;
								}else{
									 ErrMsg = "Heterozygote data representation should be either : or /";
									 request.getSession().setAttribute("indErrMsg", ErrMsg);
									 return "ErrMsg";	 
								}
								
							}else if(str.get(s).length()==2){
								String str1="";
								String str2="";
								String charStr=str.get(s);
								str1=charStr.substring(0, charStr.length()-1);
								str2=charStr.substring(1);
								charData=str1+"/"+str2;
								//System.out.println(".....:"+str.get(s).substring(1));
							}else if(str.get(s).length()==1){
								if(str.get(s).equalsIgnoreCase("A")){
									charData="A/A";	
								}else if(str.get(s).equalsIgnoreCase("C")){	
									charData="C/C";
								}else if(str.get(s).equalsIgnoreCase("G")){
									charData="G/G";
								}else if(str.get(s).equalsIgnoreCase("T")){
									charData="T/T";
								}else{
									charData=str.get(s);
								}							
							}
							//System.out.println(charData+"   "+gids[s]+"   "+intRMarkerId+"   "+genotype[s]);
							chb.setChar_value(charData);
							chb.setGid(Integer.parseInt(gids[s]));
							
							chb.setMarker_id(marker_id);
							//chb.setGermplasm_name(genotype[s]);
							session.save(chb);
							
							
							//intAC_ID++;
							intAC_ID--;
													
							if (d % 1 == 0){
								session.flush();
	                            session.clear();
							}							
						}				
					}				
				}
				for(int g=0;g<finalList.size();g++){					
					//System.out.println("gids doesnot Exists    :"+lstgermpName+"   "+gids[l]);
					String[] strList=finalList.get(g).toString().split("~!~");
					AccessionMetaDataBean amdb=new AccessionMetaDataBean();					
					//******************   GermplasmTemp   *********************//*	
					amdb.setDataset_id(dataset_id);
					amdb.setGid(Integer.parseInt(strList[0]));
					amdb.setNid(Integer.parseInt(strList[1]));
					
					session.save(amdb);
					if (g % 1 == 0){
	                    session.flush();
	                    session.clear();
					}
				
				}
				for(int m=0;m<midsList.size();m++){					
					//System.out.println("gids doesnot Exists    :"+lstgermpName+"   "+gids[l]);
					
					MarkerMetaDataBean mdb=new MarkerMetaDataBean();					
					//******************   GermplasmTemp   *********************//*	
					mdb.setDataset_id(dataset_id);
					mdb.setMarker_id(Integer.parseInt(midsList.get(m).toString()));
					
					session.save(mdb);
					if (m % 1 == 0){
	                    session.flush();
	                    session.clear();
					}
				
				}
				//System.out.println("mids List="+mids);
				tx.commit();
				
				
				strupl="inserted";
			/*rsCen.close(); rsML.close();rsMC.close();
			rsLoc.close();
			stCen.close();stLoc.close();*/
			con.close();conn.close();
		}catch(Exception e){
			e.printStackTrace();
			session.clear();
			//con.rollback();
			tx.rollback();
		}finally{			
		    // Actual contact insertion will happen at this step
		    //session.flush();
		    //session.close();
			con.close();
			conn.close();
			session.clear();
			session.disconnect();
			factory.close();
		}
		
		return strupl;
	}
	public String getKBioUpload(HttpServletRequest request, String fname, String kb_fname) throws SQLException{
		try { 
			final Session session;		
			final Transaction tx;
			result1.clear();
			session = HibernateSessionFactory.currentSession();
			tx=session.beginTransaction();
			
			ManagerFactory factory =null;
			DatasetBean ub=new DatasetBean();
			//UsersBean u=new UsersBean();
			GenotypeUsersBean usb=new GenotypeUsersBean();	
			ConditionsBean ubConditions=new ConditionsBean();
			
			HttpSession httpsession = request.getSession();
			String[] datavalue = null;
			String[] data = null;
		    
		    List genotypes = new ArrayList();
		    //create BufferedReader to read csv file
		    BufferedReader br = new BufferedReader(new FileReader(kb_fname));
		    BufferedReader brd = new BufferedReader(new FileReader(fname));
		    String line = "";
		    String linedata="";
		    
		    
		    prop.load(new FileInputStream(httpsession.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
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
		    ResultSet rsML=null; ResultSet rsMC=null;
		    ResultSet rsCen=null; ResultSet rsLoc=null;
		    DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
		    DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
		    factory = new ManagerFactory(local, central);
				
		    GermplasmDataManager manager = factory.getGermplasmDataManager();
		    GenotypicDataManager gdms=factory.getGenotypicDataManager();
	       
		    MaxIdValue uptMethod=new MaxIdValue();
		    int maxDatasetId=uptMethod.getMaxIdValue("dataset_id","gdms_dataset",session);
		    //int dataset_id=maxDatasetId+1;
		    int dataset_id=maxDatasetId-1;
		    while((linedata=brd.readLine())!=null){
		    	data =linedata.split("\t");	
		    	
		    	int length=data.length;
		    	if(linedata.startsWith("Institute")){
		    		//System.out.println("INS="+len);
		    		if(length==2) strIns=data[1];
		    		if(length==1){
		    			//System.out.println("Length = 1 and null");
		    			ErrMsg = "Please provide the Institute";
		    			request.getSession().setAttribute("indErrMsg", ErrMsg);
		    			return "ErrMsg";
					}
		    		if(length>2){ 	
		    			//System.out.println("Length greater than 2");
		    			ErrMsg = "There are extra tabs at line Institute";
		    			request.getSession().setAttribute("indErrMsg", ErrMsg);
		    			return "ErrMsg";
					}
				}
					/*if((len==2)&&(datavalue[0].startsWith("Institute"))){
						strIns=datavalue[1]; 
					}*/
					if(linedata.startsWith("PI")){
						//System.out.println("PI"+len+"   line="+line);
						if(length==2) strPI=data[1];
						if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the PI ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line PI";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}		
					//if((len==2)&&(datavalue[0].contains("PI"))) strPI=datavalue[1];
					if(linedata.startsWith("Email")){
						//System.out.println("Email"+len);
						if(length==2) strEmail=data[1];
						if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Email ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Email";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}			
					//if((len==2)&&(datavalue[0].contains("Email"))) strEmail=datavalue[1];
					if(linedata.startsWith("Incharge_Person")){
						//System.out.println("INC Per="+len);
						if(length==2){
							//System.out.println("******************IP"+len);
							IncPerson=data[1];
						}else if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Incharge Person ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}else if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Incharge_Person";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}
					if(linedata.startsWith("Dataset_Name")){
						//System.out.println("INC Per="+len);
						if(length==2){
							//System.out.println("******************IP"+len);
							strDatasetName=data[1];
							
							/*Query rsDatasetNames=session.createQuery("from DatasetBean where dataset_name ='"+strDatasetName+"'");						
							List result1= rsDatasetNames.list();*/
							
							rsLoc=stLoc.executeQuery("select dataset_name from gdms_dataset where dataset_name='"+strDatasetName+"'");
							while(rsLoc.next()){
								result1.add(rsLoc.getString(1));						
							}
							//System.out.println("select traitid, trabbr from tmstraits where trabbr in ("+traits+")");
							rsCen=stCen.executeQuery("select dataset_name from gdms_dataset where dataset_name='"+strDatasetName+"'");
							while(rsCen.next()){
								result1.add(rsCen.getString(1));	
							}
							
							
							//System.out.println(".............:"+result1.size());
							if(result1.size()>0){
								ErrMsg = "Dataset Name already exists.";
								request.getSession().setAttribute("indErrMsg", ErrMsg);							
								return "ErrMsg";
							}
							if(strDatasetName.length()>30){
								ErrMsg = "Dataset Name value exceeds max char size.";
								request.getSession().setAttribute("indErrMsg", ErrMsg);							
								return "ErrMsg";
							}
						}else if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Dataset Name ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}else if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Dataset Name";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}	
					//if((len==2)&&(datavalue[0].contains("Incharge_Person"))) IncPerson=datavalue[1];    			
					if(linedata.startsWith("Purpose_Of_Study")){
						//System.out.println("P OF Study"+len);
						if(length==2){ 
							//System.out.println("len"+len);
							PurposeStudy=data[1];
							}
						if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Purpose_Of_Study";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Purpose_Of_Study";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}
					//if((len==2)&&(datavalue[0].contains("Purpose_Of_Study"))) PurposeStudy=datavalue[1];			
					if(linedata.startsWith("Dataset_Description")){
						if(length==2) strDesc=data[1];		
						if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Description";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Description";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}
					//if((len==2)&&(datavalue[0].contains("Description"))) strDesc=datavalue[1];
					if(linedata.startsWith("Genus")){
						if(length==2) strGenus=data[1];	
						if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Genus";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Genus";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}	
					//if((len==2)&&(datavalue[0].contains("Genus"))) strGenus=datavalue[1];
					if(linedata.startsWith("Species")){
						if(length==2) strSpecies=data[1];
						if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Species";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Species";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}				
					//if((len==2)&&(datavalue[0].contains("Species"))) strSpecies=datavalue[1];
					if(linedata.startsWith("Missing_Data")){
						if(length==2) strMissData=data[1];
						if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Missing_Data";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Missing_Data";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}			
					//if((len==2)&&(datavalue[0].contains("Missing_Data"))) strMissData=datavalue[1];
					if(linedata.startsWith("Creation_Date")){
						if(length==2){ 
							boolean dFormat=uptMethod.isValidDate(data[1]);
							if(dFormat==false){
								ErrMsg = "Creation_Date should be in yyyy-mm-dd format";
								request.getSession().setAttribute("indErrMsg", ErrMsg);
								return "ErrMsg";
							}else{
								strDate=data[1];
							}
							
						}
						if(length==1){
							//System.out.println("Length = 1 and null");
							ErrMsg = "Please provide the Creation_Date";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						if(length>2){ 	
							//System.out.println("Length greater than 2");
							ErrMsg = "There are extra tabs at line Creation_Date";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
						//if(strDate)
					}
					if((length>2)&&(data[0].startsWith("gid's"))){					
						length=data.length;
						gidCount=length-1;
						for(int g=1;g<length;g++){						
							gids=data;							
						}					
					}
					
					if((length>2)&&(data[0].startsWith("Genotype"))){	
						length=data.length;
						genotypeCount=length-1;
						for(int g=1;g<length;g++){
							genotype=data;		
							//System.out.println("genotype=:"+genotype[g]);
						}					
					}
					
				}
				//System.out.println(genotypeCount+"<"+gidCount);
				System.out.println("genotype=:"+genotype);
				if(gidCount<genotypeCount){
					ErrMsg = "The number of GIDs is less than the number of Germplasm names provided";
					request.getSession().setAttribute("indErrMsg", ErrMsg);
					return "ErrMsg";
				}else if(genotypeCount<gidCount){
					ErrMsg = "The number of GIDs is more than the number of Germplasm names provided";
					request.getSession().setAttribute("indErrMsg", ErrMsg);
					return "ErrMsg";
				}
				String username=request.getSession().getAttribute("user").toString();
				int user_id=uptMethod.getUserId("userid", "users", "uname", session,username);
				
				while ((line = br.readLine()) != null) {
					datavalue =line.split(",");	
									
					int len=datavalue.length;	
					
					//System.out.println(len);
					if((len>2)&&((datavalue[0].startsWith("Sample name"))||(datavalue[0].startsWith("DNA \\ Assay")))){	
						len=datavalue.length;
						gCount=len;
						for(int g=1;g<len;g++){
							markers=datavalue;							
						}					
					}
					
					if((len>2)&&((!(datavalue[0].startsWith("Sample name")))||(!(datavalue[0].startsWith("DNA \\ Assay"))))){	
						len=datavalue.length;
						//genotype=datavalue;
						dataCount=len;
						genoData.add(Arrays.asList(datavalue)); 			
						
						 genotypes.add(datavalue[0]);
						/*for(int g=1;g<len;g++){
							markers=datavalue;							
						}*/
					}
					
				}
				int gCount=genotypes.size()-1;
				//System.out.println(genotypes.size()+"    "+genotypeCount+" genotypes:"+genotypes);
				if(gCount==genotypeCount){
					for(int g=1;g<genotype.length;g++){
						System.out.println(g+"   genotype[g]=:"+genotype[g]);
						if(!(genotypes.contains(genotype[g]))){
							ErrMsg = "Germplasm names given in the file do not match with the sample in kbio output file ";
							request.getSession().setAttribute("indErrMsg", ErrMsg);
				        	   return "ErrMsg";	
						}
					}
				}
				if(gCount!=genotypeCount){
					ErrMsg = "Germplasm names given in the file do not match with the sample in kbio output file";
					request.getSession().setAttribute("indErrMsg", ErrMsg);
		        	   return "ErrMsg";	
				}
				//System.out.println(genotypes.size()+"=="+genotypeCount);
				String gidsForQuery = "";
				HashMap<Integer, String> GIDsMap = new HashMap<Integer, String>();
				
				ArrayList gidNamesList=new ArrayList();
				
				 SortedMap GIDsMapK = new TreeMap();
	            for(int d=1;d<gids.length;d++){	               
	            	gidsForQuery = gidsForQuery + gids[d]+",";
	            	if(!gidNamesList.contains(Integer.parseInt(gids[d])))
						gidNamesList.add(Integer.parseInt(gids[d])+","+genotype[d]);
	            	GIDsMapK.put(genotype[d], Integer.parseInt(gids[d]));
	            	if(!gidsAList.contains(Integer.parseInt(gids[d])))
						gidsAList.add(gids[d]);
	            	
	            	NamesList.add(genotype[d]);
	            }
	            gidsForQuery=gidsForQuery.substring(0, gidsForQuery.length()-1);
	            
	            System.out.println("....:"+GIDsMapK);
	            for(int d=1;d<genotype.length;d++){	               
	            	gNames = gNames +"'"+genotype[d]+"',";
	            	
	            }
	            gNames=gNames.substring(0, gNames.length()-1);
	             
	            /*SortedMap germplasmsMap = new TreeMap();
	            List lstgermpName = new ArrayList();*/
	           
				ArrayList gidsDBList = new ArrayList();
				ArrayList gNamesDBList = new ArrayList();
				hashMap.clear();
				for(int n=0;n<NamesList.size();n++){
					List<Germplasm> germplasmList = manager.getGermplasmByName(NamesList.get(n).toString(), 0, new Long(manager.countGermplasmByName(NamesList.get(n).toString(), Operation.EQUAL)).intValue(), Operation.EQUAL);
					for (Germplasm g : germplasmList) {
			        	if(!(gidsDBList.contains(g.getGid()))){
			        		gidsDBList.add(g.getGid());
			        		gNamesDBList.add(NamesList.get(n).toString());
			        		addValues(NamesList.get(n).toString(), g.getGid());					        		
			        	}				        	
			           //System.out.println("  " + g.getGid());
			        }
			        System.out.println(n+":"+NamesList.get(n).toString()+"   "+hashMap.get(NamesList.get(n).toString()));
				}
				/*List<Name> names = null;
				for(int n=0;n<gidsAList.size();n++){
					names = manager.getNamesByGID(Integer.parseInt(gidsAList.get(n).toString()), null, null);
					for (Name name : names) {
						//System.out.println("........:"+name.getGermplasmId()+"  ,"+name.getNval()+" :  "+name.getNid());
						lstgermpName.add(name.getGermplasmId());
						germplasmsMap.put(name.getGermplasmId(), name.getNval());
						addValues(name.getGermplasmId(), name.getNval().toLowerCase());	
					}
				}*/
	           
	           if(gidsDBList.size()==0){
	        	   alertGID="yes";
	        	   size=0;
	           }
	           int gidToCompare=0;
	           String gNameToCompare="";
	           //String gNameFromMap="";
	           ArrayList gNameFromMap=new ArrayList();
	           if(gidsDBList.size()>0){
		          /*for(int gi=0;gi<gidNamesList.size();gi++){
		        	   String arrP[]=new String[3];
						 StringTokenizer stzP = new StringTokenizer(gidNamesList.get(gi).toString(), ",");
						 int iP=0;
						 while(stzP.hasMoreTokens()){
							 arrP[iP] = stzP.nextToken();
							 iP++;
						 }	
		        	   gidToCompare=Integer.parseInt(arrP[0].toString());
		        	   gNameToCompare=arrP[1].toString();
		        	  
		        	   //System.out.println("...."+gidToCompare+"   "+lstgermpName.contains(gidToCompare));
		        	   if(lstgermpName.contains(gidToCompare)){
		        		   //gNameFromMap=map.get(gidToCompare).toString();
		        		   gNameFromMap=hashMap.get(gidToCompare);
		        		   //System.out.println("...."+gNameToCompare+"   "+map.get(gidToCompare).equals(gNameToCompare)+"  from map: "+map.get(gidToCompare));
		        		   //if(!(gNameFromMap.toLowerCase().equals(gNameToCompare.toLowerCase()))){
		        		   if(!(gNameFromMap.contains(gNameToCompare.toLowerCase()))){
		        			   notMatchingData=notMatchingData+gidToCompare+"   "+hashMap.get(gidToCompare)+"\n\t";
			        		   alertGN="yes"; 
		        		   }			        			   
		        	   }else{
		        		   alertGID="yes";
		        		   size=germplasmsMap.size();
		        		   notMatchingGIDS=notMatchingGIDS+gidToCompare+", ";
		        	   }
		           }*/
		           for(int n=0;n<NamesList.size();n++){
	        		   if(gNamesDBList.contains(NamesList.get(n))){
	        			   if(!(hashMap.get(NamesList.get(n).toString()).contains(GIDsMapK.get(NamesList.get(n).toString())))){
	        				   notMatchingData=notMatchingData+NamesList.get(n)+"   "+GIDsMapK.get(NamesList.get(n).toString())+"\n\t";
	        				   notMatchingDataDB=notMatchingDataDB+hashMap.get(NamesList.get(n))+"\t";
			        		   alertGN="yes";
	        			   }
	        		   }else{
	        			   alertGID="yes";
		        		   size=hashMap.size();
		        		   notMatchingGIDS=notMatchingGIDS+NamesList.get(n).toString()+", ";
	        		   }
	        	   }
	           }
	           if((alertGN.equals("yes"))&&(alertGID.equals("no"))){
	        	   //String ErrMsg = "GID(s) ["+notMatchingGIDS.substring(0,notMatchingGIDS.length()-1)+"] of Germplasm(s) ["+notMatchingData.substring(0,notMatchingData.length()-1)+"] being assigned to ["+notMatchingDataExists.substring(0,notMatchingDataExists.length()-1)+"] \n Please verify the template ";
	        	   ErrMsg = "Please verify the name(s) provided with the following GID(s) \t "+notMatchingData+" which do not match the name(s) present in the database \t"+notMatchingDataDB;
	        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
	        	   return "ErrMsg";	 
	           }
	           if((alertGID.equals("yes"))&&(alertGN.equals("no"))){	        	   
	        	   if(size==0){
	        		   //ErrMsg = "The GIDs provided do not exist in the database. \n Please upload the relevant germplasm information to the GMS ";
	        		   ErrMsg = "The Germplasm(s) provided do not exist in the database. \n Please upload the relevant germplasm information through the Fieldbook ";
	        	   }else{
	        		   ErrMsg = "The following Germplasm(s) provided do not exist in the database. \n Please upload the relevant germplasm information through the Fieldbook \n \t"+notMatchingGIDS;
	        		   //ErrMsg = "Please verify the GID/Germplasm(s) provided as some of them do not exist in the database. \n Please upload germplasm information into GMS ";
	        	   }	        	   
	        	   //ErrMsg = "Please verify the following GID/Germplasm(s) doesnot exists. \n Upload germplasm Information into GMS \n\t"+notMatchingGIDS;
	        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
	        	   return "ErrMsg";
	           }
			
	           if((alertGID.equals("yes"))&&(alertGN.equals("yes"))){
	        	   ErrMsg = "The following Germplasm(s) provided do not exist in the database. \n Please upload the relevant germplasm information through the Fieldbook \n \t"+notMatchingGIDS+" \n Please verify the name(s) provided with the following GID(s) \t "+notMatchingData+" which do not match the name(s) present in the database \t"+notMatchingDataDB;
	        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
	        	   return "ErrMsg";	 
	           }
	       
				/** writing to 'dataset' table **/
				String remarks="";
				String method="";
				String score="";
				//Date uploadTemplateDate = new Date(System.currentTimeMillis());
				//System.out.println("uploadTemplateDate="+uploadTemplateDate);
				/*Dataset dataset = new Dataset(dataset_id, strDatasetName, strDesc, dataset_type, strGenus, strSpecies, uploadTemplateDate, remarks,
						datatype, strMissData, method, score);        
		        */
		       
				
				
				
				ub.setDataset_id(dataset_id);
				ub.setDataset_name(strDatasetName);
				ub.setDataset_desc(strDesc);
				ub.setDataset_type(dataset_type);
				ub.setGenus(strGenus);
				ub.setSpecies(strSpecies);
				ub.setUpload_template_date(strDate);					
				ub.setDatatype(datatype);
				ub.setMissing_data(strMissData);
				//System.out.println("dataset id = ");
				session.save(ub);
				
				//*************  dataset_users*************
				
				//DatasetUsers datasetUser = new DatasetUsers(dataset_id, user_id);
				
				usb.setDataset_id(dataset_id);
				usb.setUser_id(user_id);
				
				session.save(usb);
				
				
				SortedMap mapN = new TreeMap();
				//System.out.println(",,,,,,,,,,,,,,,,,gNames="+gNames);
				ArrayList finalList =new ArrayList();
				ArrayList gidL=new ArrayList();
				
				
				/*
				 * getting nids with gid and nval for inserting into gdms_acc_metadataset table			
				*/
				Name names1 = null;
				for(int n=0;n<gidsAList.size();n++){										
					//names1 = manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString());
			        //names1 = manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString(), GetGermplasmByNameModes.STANDARDIZED);
			        names1 = manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString(), GetGermplasmByNameModes.STANDARDIZED);
					if(names1==null){
						names1=manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString(), GetGermplasmByNameModes.NORMAL);
					}
					if(!gidL.contains(names1.getGermplasmId()))
		            	gidL.add(names1.getGermplasmId());
		            mapN.put(names1.getGermplasmId(), names1.getNid());
					
				}
				
				
		        for(int a=0;a<gidsAList.size();a++){
		        	int gid1=Integer.parseInt(gidsAList.get(a).toString());
		        	if(gidL.contains(gid1)){
		        		finalList.add(gid1+"~!~"+mapN.get(gid1));	
		        	}
		        }
	            //System.out.println("******************  "+finalList);
	            /*****  modified code for performance  *****/
				String marForQuery = "";
	            for(int d=1;d<markers.length;d++){
	               
	                marForQuery = marForQuery +"'"+ markers[d]+"',";
	            }
	            marForQuery=marForQuery.substring(0, marForQuery.length()-1);
	         
	            HashMap<String, Object> markersMap = new HashMap<String, Object>();
	            List lstMarkers = new ArrayList();
	            //ArrayList lstMarIdNames=uptMethod.getMarkerIds("marker_id, marker_name", "gdms_marker", "marker_name", session, marForQuery);
	            rsML=stLoc.executeQuery("select distinct marker_id, marker_name from gdms_marker where Lower(marker_name) in ("+marForQuery.toLowerCase()+")");
	            rsMC=stCen.executeQuery("select distinct marker_id, marker_name from gdms_marker where Lower(marker_name) in ("+marForQuery.toLowerCase()+")");
	            
	            while(rsMC.next()){
	            	//lstMarIdNames.add(rsMC.getString(2)+":"+rsMC.getString(1));
	            	lstMarkers.add(rsMC.getString(2));
	            	markersMap.put(rsMC.getString(2), rsMC.getInt(1));		
	            }
	            while(rsML.next()){
	            		
	            	if(!lstMarkers.contains(rsML.getString(2))){
	            		lstMarkers.add(rsML.getString(2));
	            		//lstMarIdNames.add(rsML.getString(2)+":"+rsML.getString(1));
	            	}
	            	markersMap.put(rsML.getString(2), rsML.getInt(1));	
	            }
	            
	            
	           //System.out.println("..."+markersMap);
	           maxMid=uptMethod.getMaxIdValue("marker_id","gdms_marker",session);		
	           /*******************   END   *****************************/		
	           intAC_ID=uptMethod.getMaxIdValue("ac_id","gdms_char_values",session);		
	           //intAC_ID=intAC_ID+1;
	           intAC_ID=intAC_ID-1;
	           ArrayList midsList = new ArrayList();
				SortedMap mids=new TreeMap();
	           for(int m=1;m<markers.length;m++){
	        	   MarkerInfoBean mb=new MarkerInfoBean();					
					marker=markers[m];							
					/*************   Modified for performance  *******************/						
					if(lstMarkers.contains(marker)){
						intRMarkerId=(Integer)(markersMap.get(marker));
						mids.put(marker, intRMarkerId);
						midsList.add(intRMarkerId);								
					}else{
						//maxMid=maxMid+1;
						maxMid=maxMid-1;
						intRMarkerId=maxMid;
						mids.put(marker, intRMarkerId);
						midsList.add(intRMarkerId);	
						
						mb.setMarkerId(intRMarkerId);
						mb.setMarker_type(dataset_type);
						mb.setMarker_name(marker.toString());
						mb.setSpecies(strSpecies);
						
						session.save(mb);
						if (m % 1 == 0){
							session.flush();
							session.clear();
						}
					}	        	   
	           }
				System.out.println("genoData:"+genoData);
	           //CharValues charValues = new CharValues();
	           String finalAlleleCall="";
	           int g_id=0;
	           String genotypeK="";
				for(int d=1;d<genoData.size();d++){					
					MarkerInfoBean mb=new MarkerInfoBean();				
					str=genoData.get(d);						
					//System.out.println(str.size()+"   "+str);
					for(int s=0;s<str.size();s++){
						if(s==0){
							genotypeK=str.get(0);	
							System.out.println("genotypeK=:"+genotypeK);
							g_id=Integer.parseInt(GIDsMapK.get(genotypeK).toString());
						}	
						
						if(s!=0){
							CharArrayBean chb=new CharArrayBean();
							CharArrayCompositeKey cack = new CharArrayCompositeKey();
							//ReferenceBean ref=new ReferenceBean();					
							//ReferenceCompositeKey rbck = new ReferenceCompositeKey();							
							//MarkerMetaDataBean mmb=new MarkerMetaDataBean();
							
							//**************** writing to char_values tables........
							cack.setDataset_id(dataset_id);
							cack.setAc_id(intAC_ID);
							chb.setComKey(cack);
	                        //System.out.println("........................"+str.get(s));
							//System.out.println("str="+str.get(s).length());
							
							if(str.get(s).length()>2){
								String charStr=str.get(s);
								if(charStr.contains(":")){
									String str1="";
									String str2="";
									//String charStr=str.get(s);
									str1=charStr.substring(0, charStr.length()-2);
									str2=charStr.substring(2, charStr.length());
									charData=str1+"/"+str2;
								}else if(charStr.contains("/")){
									charData=charStr;
								}else if((charStr.equalsIgnoreCase("DUPE"))||(charStr.equalsIgnoreCase("BAD"))){
									charData="?";
								}else{
								
									 ErrMsg = "Heterozygote data representation should be either : or /"+charStr;
									 request.getSession().setAttribute("indErrMsg", ErrMsg);
									 return "ErrMsg";	 
								}
								
							}else if(str.get(s).length()==2){
								String str1="";
								String str2="";
								String charStr=str.get(s);
								str1=charStr.substring(0, charStr.length()-1);
								str2=charStr.substring(1);
								charData=str1+"/"+str2;
								//System.out.println(".....:"+str.get(s).substring(1));
							}else if(str.get(s).length()==1){
								if(str.get(s).equalsIgnoreCase("A")){
									charData="A/A";	
								}else if(str.get(s).equalsIgnoreCase("C")){	
									charData="C/C";
								}else if(str.get(s).equalsIgnoreCase("G")){
									charData="G/G";
								}else if(str.get(s).equalsIgnoreCase("T")){
									charData="T/T";
								}else{
									charData=str.get(s);
								}							
							}
							//System.out.println(charData+"   "+genotypeK+"   "+g_id+"   "+markers[s]+"  "+mids.get(markers[s]).toString());
							chb.setChar_value(charData);
							chb.setGid(g_id);
							
							chb.setMarker_id(Integer.parseInt(mids.get(markers[s]).toString()));
							//chb.setGermplasm_name(genotype[s]);
							session.save(chb);
							
							intAC_ID--;
													
							if (d % 1 == 0){
								session.flush();
	                            session.clear();
							}	
							//CharValues charValues = new CharValues(acId, datasetId, markerId, gId, charValue);
						}				
					}				
				} 
	           
	           
	           
				AccMetadataSet accMetadataSet = new AccMetadataSet();
				for(int g=0;g<finalList.size();g++){
					String[] strList=finalList.get(g).toString().split("~!~");
					AccessionMetaDataBean amdb=new AccessionMetaDataBean();					
					//******************   GermplasmTemp   *********************//*	
					amdb.setDataset_id(dataset_id);
					amdb.setGid(Integer.parseInt(strList[0]));
					amdb.setNid(Integer.parseInt(strList[1]));
					
					session.save(amdb);
					if (g % 1 == 0){
	                    session.flush();
	                    session.clear();
					}
					
				}
	           MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet();
				for(int m=0;m<midsList.size();m++){					
					//System.out.println("gids doesnot Exists    :"+lstgermpName+"   "+gids[l]);
					
					MarkerMetaDataBean mdb=new MarkerMetaDataBean();					
					//******************   GermplasmTemp   *********************//*	
					mdb.setDataset_id(dataset_id);
					mdb.setMarker_id(Integer.parseInt(midsList.get(m).toString()));
					
					session.save(mdb);
					if (m % 1 == 0){
	                    session.flush();
	                    session.clear();
					}
					
				}
				  
			    
				tx.commit();
				strupl="inserted";
				/*if(rsCen!= null) rsCen.close(); if(rsML!= null) rsML.close();
				if(rsLoc!= null) rsLoc.close(); if(rsMC!= null)rsMC.close();
				if(stCen!= null) stCen.close(); if(stLoc!= null) stLoc.close();*/
				if(con!= null) con.close(); if(conn!= null) conn.close();
		}catch(Exception e){
			e.printStackTrace();
			//session.clear();
			//con.rollback();
			//tx.rollback();
		}finally{			
		    // Actual contact insertion will happen at this step
		    //session.flush();
		    //session.close();
			conn.close();con.close();
			//session.clear();
			//session.disconnect();
			//factory.close();
		}
		
		return strupl;
	}
	private static void addValues(String key, Integer value){
		ArrayList<Integer> tempList = null;
		if(hashMap.containsKey(key)){
			tempList=hashMap.get(key);
			if(tempList == null)
				tempList = new ArrayList<Integer>();
			tempList.add(value);
		}else{
			tempList = new ArrayList();
			tempList.add(value);
		}
		hashMap.put(key,tempList);
	}

}
