/**
 * 
 */
package org.icrisat.gdms.upload;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;


/**
 * @author psrikalyani
 *
 */
public class MappingDataUpload {
	private Session session;
		private Transaction tx;
		Connection con = null;
		HttpServletRequest request;
		/*String crop=request.getSession().getAttribute("crop").toString();
		public MappingDataUpload(){
			//session=HibernateSessionFactory.currentSession();
			session = HibernateSessionFactory.currentSession(crop);
			tx=session.beginTransaction();		
		}*/
		String marker="";	String data="";
		public String setMappingDetails(HttpServletRequest request, String mapfile) throws SQLException{
			String result = "inserted";
			
			DatasetBean db=new DatasetBean();
			GenotypeUsersBean usb=new GenotypeUsersBean();	
			ConditionsBean ubConditions=new ConditionsBean();
			MappingPopulationBean mb=new MappingPopulationBean();
			ManagerFactory factory = null;
			try{
				//String crop=request.getSession().getAttribute("crop").toString();
				session = HibernateSessionFactory.currentSession();
				tx=session.beginTransaction();
				
				/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("localhost", "3306", "ivis", "root", "root");
				DatabaseConnectionParameters central = new DatabaseConnectionParameters("localhost", "3306", "ibdb_ivis", "root", "root");*/
				DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
				DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
				
				factory = new ManagerFactory(local, central);
				GermplasmDataManager manager = factory.getGermplasmDataManager();
				
				HttpSession hsession = request.getSession(true);
				
				//con=dataSource.getConnection();
				con=session.connection();
				
				
				Statement st = con.createStatement();
				ResultSet rs=null;
				
				Statement st1 = con.createStatement();
				ResultSet rs1=null;
				
				String dataset_type="mapping";
				String marker_type="SSR";
				int g=0;
				int intDataOrderIndex = 1;
				int intRMarkerId = 1;
				int intRAccessionId = 1;
				//String str=
				int maxMid=0;
				int mid=0;
				String ErrMsg ="";
				String mType="";
				List genoDataMarkers = new ArrayList();
				String alertGN="no";
		        String alertGID="no";
		        String notMatchingData="";
		        String notMatchingGIDS="";
		        String notMatchingDataExists="";
		        ArrayList markersList=new ArrayList();
				//Retrieve the maximum dataset_id from the database
				MaxIdValue uptMId=new MaxIdValue();
//		      /Workbook code        
				int map_id=0;
				int size=0;
				Workbook workbook=Workbook.getWorkbook(new File(mapfile));
				String[] strSheetNames=workbook.getSheetNames();
				String selMap="";
				//System.out.println("*******************************   :"+request.getParameter("op"));
				String option=request.getParameter("opMap");
				if(option.equalsIgnoreCase("yes")){
					selMap=request.getParameter("List1");
					map_id=uptMId.getUserId("map_id", "gdms_map", "map_name", session,selMap);
				}
				//System.out.println(">>>>>>>>>>>>>>"+selMap+":"+map_id);
				//avoids the case sensitive of sheet names
				String strSource="",strDatalist="";
				int intAC_ID = 0;
				for (int i=0;i<strSheetNames.length;i++){					
					if(strSheetNames[i].equalsIgnoreCase("Mapping_Source"))
						strSource = strSheetNames[i];
					
					if(strSheetNames[i].equalsIgnoreCase("Mapping_DataList"))
						strDatalist = strSheetNames[i];						
				}
				String mapType=request.getParameter("mapType");
				System.out.println("mapType"+mapType);
					
				Sheet sheetDataList=workbook.getSheet(strDatalist);
				Sheet sheetSource = workbook.getSheet(strSource);
				int rowCount=sheetDataList.getRows();
				int colCount=sheetDataList.getColumns();	
				String gids1="";
				int gidsCount=0;
				
				if (sheetDataList==null){
						System.out.println("Empty Sheet");		
				}else{
					int intNR=sheetDataList.getRows();
					int intColRowEmpty=0;
					//int rows=sheetDataList.getRows();
					for(int i=0;i<intNR;i++){
						Cell c=sheetDataList.getCell(0,i);
						String s=c.getContents();
						if(!s.equals("")){
							intColRowEmpty=intColRowEmpty + 1;
							
						}
					}
					
					
					
					for (int a=3;a<colCount;a++){				
						markersList.add(sheetDataList.getCell(a,0).getContents().trim());		
						marker = marker +"'"+ sheetDataList.getCell(a,0).getContents().trim().toString()+"',";
					}
					String exists="";
					ArrayList pGidsList=new ArrayList();
					ArrayList pGNamesList=new ArrayList();
					if(mapType.equalsIgnoreCase("allelic")){
						marker_type=request.getParameter("uploadDataType");
						int dataset=0;
						String parentA=sheetSource.getCell(1,9).getContents().trim();
						int parentA_GID=Integer.parseInt(sheetSource.getCell(1,8).getContents().trim().toString());
						String parentB=sheetSource.getCell(1,11).getContents().trim();
						int parentB_GID=Integer.parseInt(sheetSource.getCell(1,10).getContents().trim().toString());
						String parentGids=parentA_GID+","+parentB_GID;
						if(!(pGidsList.contains(parentA_GID))){
							pGidsList.add(parentA_GID);
							pGNamesList.add(parentA_GID+","+parentA);	
						}
						if(!(pGidsList.contains(parentB_GID))){
							pGidsList.add(parentB_GID);
							pGNamesList.add(parentB_GID+","+parentB);
						}
						System.out.println("**************:"+pGidsList);
						SortedMap mapP = new TreeMap();
			            List lstgermpNameP = new ArrayList();
			            manager = factory.getGermplasmDataManager();
						List<Name> names = null;
						for(int n=0;n<pGidsList.size();n++){
							names = manager.getNamesByGID(Integer.parseInt(pGidsList.get(n).toString()), null, null);
							for (Name name : names) {					
								 lstgermpNameP.add(name.getGermplasmId());
								 mapP.put(name.getGermplasmId(), name.getNval());	            
					        }
						}
						System.out.println("....mapP="+mapP);
						if(mapP.size()==0){
				        	   alertGID="yes";
				        	   size=0;
				           }
						int gidToCompare=0;
				           String gNameToCompare="";
				           String gNameFromMap="";
				           if(mapP.size()>0){
					           for(int gi=0;gi<pGNamesList.size();gi++){
					        	   String arrP[]=new String[3];
									 StringTokenizer stzP = new StringTokenizer(pGNamesList.get(gi).toString(), ",");
									 int iP=0;
									 while(stzP.hasMoreTokens()){
										 arrP[iP] = stzP.nextToken();
										 iP++;
									 }	
					        	   gidToCompare=Integer.parseInt(arrP[0].toString());
					        	   gNameToCompare=arrP[1].toString();
					        	   //System.out.println("...."+gidToCompare+"   "+lstgermpName.contains(gidToCompare));
					        	   if(lstgermpNameP.contains(gidToCompare)){
					        		   gNameFromMap=mapP.get(gidToCompare).toString();
					        		   //System.out.println("...."+gNameToCompare+"   "+map.get(gidToCompare).equals(gNameToCompare)+"  from map: "+map.get(gidToCompare));
					        		   if(!(gNameFromMap.toLowerCase().equals(gNameToCompare.toLowerCase()))){
					        			   notMatchingData=notMatchingData+gidToCompare+"   "+mapP.get(gidToCompare)+"\n\t";
						        		   alertGN="yes"; 
					        		   }			        			   
					        	   }else{
					        		   alertGID="yes";
					        		   size=mapP.size();
					        		   notMatchingGIDS=notMatchingGIDS+gidToCompare+", ";
					        	   }
					           }
				           }
				           if((alertGN.equals("yes"))&&(alertGID.equals("no"))){
				        	   //String ErrMsg = "GID(s) ["+notMatchingGIDS.substring(0,notMatchingGIDS.length()-1)+"] of Germplasm(s) ["+notMatchingData.substring(0,notMatchingData.length()-1)+"] being assigned to ["+notMatchingDataExists.substring(0,notMatchingDataExists.length()-1)+"] \n Please verify the template ";
				        	   ErrMsg = "Please verify the Parent(s) name(s) provided with the following GID(s) which do not match the name(s) present in the database: \n\t "+notMatchingData;
				        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
				        	   return "ErrMsg";	 
				           }
				           if((alertGID.equals("yes"))&&(alertGN.equals("no"))){	        	   
				        	   if(size==0){
				        		   ErrMsg = "The GID(s) provided for parent(s) do not exist in the database. \n Please upload the relevant germplasm information to the GMS ";
				        	   }else{
				        		   ErrMsg = "The following GID(s) provided as parent(s) do not exist in the database. \n Please upload the relevant germplasm information to the GMS \n \t"+notMatchingGIDS;
				        		   //ErrMsg = "Please verify the GID/Germplasm(s) provided as some of them do not exist in the database. \n Please upload germplasm information into GMS ";
				        	   }	        	   
				        	   //ErrMsg = "Please verify the following GID/Germplasm(s) doesnot exists. \n Upload germplasm Information into GMS \n\t"+notMatchingGIDS;
				        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
				        	   return "ErrMsg";
				           }
						
				           if((alertGID.equals("yes"))&&(alertGN.equals("yes"))){
				        	   ErrMsg = "The following GID(s) provided for parent(s) do not exist in the database. \n Please upload the relevant germplasm information to the GMS \n \t"+notMatchingGIDS+" \n Please verify the parent(s) name(s) provided with the following GID(s) which do not match the name(s) present in the database: \n\t "+notMatchingData;
				        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
				        	   return "ErrMsg";	 
				           }
						
						
						
						
						System.out.println("select dataset_id from gdms_acc_metadataset where gid in("+parentGids.substring(0,parentGids.length()-1)+")");
						rs=st.executeQuery("select dataset_id from gdms_acc_metadataset where gid in("+parentGids.substring(0,parentGids.length()-1)+")");
						if(rs.next()){
							dataset=rs.getInt(1);							
						}else
							exists="no";
						
						System.out.println("1 exists="+exists);
						rs1=st1.executeQuery("select * from gdms_marker_metadataset where dataset_id="+dataset+" and marker_id in(select marker_id from gdms_marker where marker_name in("+marker.substring(0,marker.length()-1)+") order by marker_id)");	
						if(rs1.next())
							exists="yes";
						else
							exists="no";
						System.out.println("2 exists="+exists);
						if(exists.equalsIgnoreCase("no")){
							if((!(sheetDataList.getCell(2,1).getContents().trim().toString().equals(parentA)))&&(!(sheetDataList.getCell(2,2).getContents().trim().toString().equals(parentB)))){
								 String strRowNumber1 = String.valueOf(sheetDataList.getCell(2, 1).getRow()+1);	
								 String strRowNumber2 = String.valueOf(sheetDataList.getCell(2, 2).getRow()+1);	
								 ErrMsg = "Please provide Parents Information first followed by population in Mapping_DataList sheet.\n  The row position is "+strRowNumber1+" & "+strRowNumber2;
								 hsession.setAttribute("indErrMsg", ErrMsg);
								 return "ErrMsg";
							}
							
						}
					}
					
					String parentGids=sheetSource.getCell(1,8).getContents().trim()+","+sheetSource.getCell(1,10).getContents().trim();
					String gidsForQuery = "";
					HashMap<Integer, String> GIDsMap = new HashMap<Integer, String>();
					String gNames="";
					ArrayList gidsAList=new ArrayList();
					ArrayList gidNamesList=new ArrayList();
					ArrayList NamesList=new ArrayList();
					for(int r=1;r<rowCount;r++){	
						gidsForQuery = gidsForQuery + sheetDataList.getCell(1,r).getContents().trim()+",";
						gNames=gNames+"'"+sheetDataList.getCell(2,r).getContents().trim()+"',";
						gids1=gids1+sheetDataList.getCell(1,r).getContents().trim()+"!~!"+sheetDataList.getCell(2,r).getContents().trim()+",";
						//GIDsMap.put(Integer.parseInt(sheetDataList.getCell(1,r).getContents().trim()), sheetDataList.getCell(2,r).getContents().trim());
						
						if(!gidNamesList.contains(Integer.parseInt(sheetDataList.getCell(1,r).getContents().trim())))
							gidNamesList.add(Integer.parseInt(sheetDataList.getCell(1,r).getContents().trim())+","+sheetDataList.getCell(2,r).getContents().trim());
						
						if(!gidsAList.contains(Integer.parseInt(sheetDataList.getCell(1,r).getContents().trim())))
							gidsAList.add(Integer.parseInt(sheetDataList.getCell(1,r).getContents().trim()));
						
						if(!NamesList.contains(sheetDataList.getCell(2,r).getContents().trim()))
							NamesList.add(sheetDataList.getCell(2,r).getContents().trim());
						
						
						gidsCount=gidsCount+1;
					}
					String gidsO="";
					int gnCount=0;
					
					for(int gn=1;gn<rowCount;gn++){					
						gids1=gids1+sheetDataList.getCell(2,gn).getContents().trim()+",";
						gnCount=gnCount+1;
					}
					int s=0;
					//String fGids="";
					ArrayList fGids=new ArrayList();
					String gidsRet="";
					
					//HashMap<Integer, String> GIDsMap = new HashMap<Integer, String>();
					/** arranging gid's with respect to germplasm name in order to insert into allele_values table */
					if(gidsCount==gnCount){			
						
			            gidsForQuery=gidsForQuery.substring(0, gidsForQuery.length()-1);
			            //System.out.println("GIDsMap.."+GIDsMap);
			            Map<Object, String> sortedMap = new TreeMap<Object, String>(GIDsMap);
			            //System.out.println("%%%%%"+sortedMap.size()+"%%%%%%%%sortedMap=:"+sortedMap);
			            //System.out.println("%%%%%%%%%%%%%gidsForQuery=:"+gidsForQuery);
			            //HashMap<Object, String> map = new HashMap<Object, String>();
			            //ArrayList lstGIDs=uptMethod.getGIds("gid, germplasm_name", "germplasm_temp", "gid", session, gidsForQuery);
			            
			            //ArrayList lstGIDs=uptMId.getGIds("gid, nval", "names", "gid", session, gidsForQuery);
			            
			            SortedMap map = new TreeMap();
			            List lstgermpName = new ArrayList();
			            manager = factory.getGermplasmDataManager();
						List<Name> names = null;
						for(int n=0;n<gidsAList.size();n++){
							names = manager.getNamesByGID(Integer.parseInt(gidsAList.get(n).toString()), null, null);
							for (Name name : names) {					
								 lstgermpName.add(name.getGermplasmId());
								 map.put(name.getGermplasmId(), name.getNval());	            
					        }
						}
			            
						System.out.println("......"+map.size()+".........  map="+map);
			            
			            System.out.println("gidsAList="+gidsAList);
			            /*SortedMap map = new TreeMap();
			            List lstgermpName = new ArrayList();
			            for(int w=0;w<lstGIDs.size();w++){
			                 Object[] strMareO= (Object[])lstGIDs.get(w);
			                 lstgermpName.add(Integer.parseInt(strMareO[0].toString()));
			                 String strMa123 = (String)strMareO[1];
			                 map.put(Integer.parseInt(strMareO[0].toString()), strMa123);
			                 
			            }*/
			            /*
			            System.out.println("lstgermpName="+lstgermpName);*/			           
			           if(map.size()==0){
			        	   alertGID="yes";
			        	   size=0;
			           }
			          
			           int gidToCompare=0;
			           String gNameToCompare="";
			           String gNameFromMap="";
			           //System.out.println("gidNamesList="+gidNamesList);
			           if(map.size()>0){
				           for(int gi=0;gi<gidNamesList.size();gi++){
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
				        		   gNameFromMap=map.get(gidToCompare).toString();
				        		   //System.out.println("...."+gNameToCompare+"   "+map.get(gidToCompare).equals(gNameToCompare)+"  from map: "+map.get(gidToCompare));
				        		   if(!(gNameFromMap.toLowerCase().equals(gNameToCompare.toLowerCase()))){
				        			   
				        			   notMatchingData=notMatchingData+gidToCompare+"   "+map.get(gidToCompare)+"\n\t";
					        		   alertGN="yes"; 
				        		   }			        			   
				        	   }else{
				        		   alertGID="yes";
				        		   size=sortedMap.size();
				        		   notMatchingGIDS=notMatchingGIDS+gidToCompare+", ";
				        	   }
				           }
			           }
			           if((alertGN.equals("yes"))&&(alertGID.equals("no"))){
			        	   //String ErrMsg = "GID(s) ["+notMatchingGIDS.substring(0,notMatchingGIDS.length()-1)+"] of Germplasm(s) ["+notMatchingData.substring(0,notMatchingData.length()-1)+"] being assigned to ["+notMatchingDataExists.substring(0,notMatchingDataExists.length()-1)+"] \n Please verify the template ";
			        	   ErrMsg = "Please verify the name(s) provided with the following GID(s) which do not match the name(s) present in the database: \n\t "+notMatchingData;
			        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
			        	   return "ErrMsg";	 
			           }
			           if((alertGID.equals("yes"))&&(alertGN.equals("no"))){	        	   
			        	   if(size==0){
			        		   ErrMsg = "The GIDs provided do not exist in the database. \n Please upload the relevant germplasm information to the GMS ";
			        	   }else{
			        		   ErrMsg = "The following GID(s) provided do not exist in the database. \n Please upload the relevant germplasm information to the GMS \n \t"+notMatchingGIDS;
			        		   //ErrMsg = "Please verify the GID/Germplasm(s) provided as some of them do not exist in the database. \n Please upload germplasm information into GMS ";
			        	   }	        	   
			        	   //ErrMsg = "Please verify the following GID/Germplasm(s) doesnot exists. \n Upload germplasm Information into GMS \n\t"+notMatchingGIDS;
			        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
			        	   return "ErrMsg";
			           }
					
			           if((alertGID.equals("yes"))&&(alertGN.equals("yes"))){
			        	   ErrMsg = "The following GID(s) provided do not exist in the database. \n Please upload the relevant germplasm information to the GMS \n \t"+notMatchingGIDS+" \n Please verify the name(s) provided with the following GID(s) which do not match the name(s) present in the database: \n\t "+notMatchingData;
			        	   request.getSession().setAttribute("indErrMsg", ErrMsg);
			        	   return "ErrMsg";	 
			           }
					
					}
					
					
					int intDatasetId=uptMId.getMaxIdValue("dataset_id","gdms_dataset",session);
					int user_id=uptMId.getUserId("userid", "users", "uname", session,sheetSource.getCell(1,1).getContents().trim());
					int mp_id=uptMId.getMaxIdValue("mp_id","gdms_mapping_pop_values",session);
					int dataset_id=intDatasetId+1;
					/*int parent_a_gid=uptMId.getUserId("gid", "names", "nval", session,sheetSource.getCell(1,8).getContents().trim());
					int parent_b_gid=uptMId.getUserId("gid", "names", "nval", session,sheetSource.getCell(1,9).getContents().trim());*/
					/** writing to 'dataset' table **/
					
					db.setDataset_id(dataset_id);
					db.setDataset_name(sheetSource.getCell(1,3).getContents());
					db.setDataset_desc(sheetSource.getCell(1,4).getContents());
					db.setDataset_type(dataset_type);
					db.setGenus(sheetSource.getCell(1,5).getContents());
					db.setSpecies(sheetSource.getCell(1,5).getContents());
					db.setUpload_template_date(sheetSource.getCell(1,17).getContents());					
					db.setDatatype("map");
					db.setRemarks(sheetSource.getCell(1,17).getContents());
					db.setMissing_data(sheetSource.getCell(1,16).getContents().trim());
					//System.out.println("dataset id = ");
					session.save(db);
					
					//System.out.println("dataset_id;:"+dataset_id+"   mcid="+dataset_id+"  parentA;"+sheetSource.getCell(1,7).getContents()+"   Parent B:"+sheetSource.getCell(1,8).getContents()+"  Pop size="+sheetSource.getCell(1,9).getContents()+"    Pop type="+sheetSource.getCell(1,10).getContents()+"  DEsc="+sheetSource.getCell(1,11).getContents()+"   Scoring scheme="+sheetSource.getCell(1,12).getContents()+"   map id="+map_id);
					/** Writing to 'map_data' table **/
					mb.setDataset_id(dataset_id);
					//mb.setMp_id(mp_id);
					mb.setMapping_type(mapType);
					mb.setParent_a_gid(Integer.parseInt(sheetSource.getCell(1,8).getContents().trim().toString()));
					mb.setParent_b_gid(Integer.parseInt(sheetSource.getCell(1,10).getContents().trim().toString()));
					mb.setPopulation_size(Integer.parseInt(sheetSource.getCell(1,12).getContents().toString()));
					mb.setPopulation_type(sheetSource.getCell(1,13).getContents());
					mb.setMapdata_desc(sheetSource.getCell(1,14).getContents());	
					mb.setScoring_scheme(sheetSource.getCell(1,15).getContents());
					mb.setMap_id(map_id);
					//System.out.println("dataset_id;:"+dataset_id+"   mcid="+dataset_id+"  parentA;"+sheetSource.getCell(1,7).getContents()+"   Parent B:"+sheetSource.getCell(1,8).getContents()+"  Pop size="+sheetSource.getCell(1,9).getContents()+"    Pop type="+sheetSource.getCell(1,10).getContents()+"  DEsc="+sheetSource.getCell(1,11).getContents()+"   Scoring scheme="+sheetSource.getCell(1,12).getContents()+"   map id="+map_id);
					
					session.save(mb);
					
					
					SortedMap mapN = new TreeMap();
					System.out.println(",,,,,,,,,,,,,,,,,gNames="+gNames);
					ArrayList finalList =new ArrayList();
					ArrayList gidL=new ArrayList();
					/*ArrayList lstNids=uptMId.getNids("gid, nid", "names", "nval", session, gNames.substring(0,gNames.length()-1));
					for(int w=0;w<lstNids.size();w++){
			        	Object[] strMareO= (Object[])lstNids.get(w);
			           // System.out.println("W=....."+w+"    "+strMareO[0]+"   "+strMareO[1]);
			            if(!gidL.contains(Integer.parseInt(strMareO[0].toString())))
			            	gidL.add(Integer.parseInt(strMareO[0].toString()));
			            mapN.put(Integer.parseInt(strMareO[0].toString()), strMareO[1]);
			          
			 		}*/
					/**
					 * getting nids with gid and nval for inserting into gdms_acc_metadataset table			
					*/
			       
					Name names = null;
					for(int n=0;n<gidsAList.size();n++){
						names = manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString());
						if(!gidL.contains(names.getGermplasmId()))
			            	gidL.add(names.getGermplasmId());
			            mapN.put(names.getGermplasmId(), names.getNid());
						
					}
					
					
			        for(int a=0;a<gidsAList.size();a++){
			        	int gid1=Integer.parseInt(gidsAList.get(a).toString());
			        	if(gidL.contains(gid1)){
			        		finalList.add(gid1+"~!~"+mapN.get(gid1));	
			        	}
			        }
		            
			        System.out.println("finalList="+finalList);
			        
			        
					//*************  dataset_users*************
					usb.setDataset_id(dataset_id);
					usb.setUser_id(user_id);
					
					session.save(usb);					
								
					
					//String markersList="";
					/** retrieving maximum marker id from 'marker' table of database **/
					int maxMarkerId=uptMId.getMaxIdValue("marker_id","gdms_marker",session);
					
					HashMap<String, Object> markersMap = new HashMap<String, Object>();			
					
					ArrayList lstMarIdNames=uptMId.getMarkerIds("marker_id, marker_name", "gdms_marker", "marker_name", session, marker.substring(0, marker.length()-1));
			        //System.out.println(".............."+lstMarIdNames.size());
		            List lstMarkers = new ArrayList();
		            List lstMids=new ArrayList();
		            for(int w=0;w<lstMarIdNames.size();w++){
		                 Object[] strMareO= (Object[])lstMarIdNames.get(w);
		                 lstMids.add(Integer.parseInt(strMareO[0].toString()));
		                 lstMarkers.add(strMareO[1]);
		                 String strMa123 = (String)strMareO[1];
		                 markersMap.put(strMa123, strMareO[0]);
		                 
		            }
		            ArrayList gids=new ArrayList();
		            String gidsList="";
		            int rows=0;
		            int cols=0;
		            if(mapType.equalsIgnoreCase("allelic")){
		            	rows=3;
		            }else{
		            	rows=1;
		            }
		            for (int l=rows;l<rowCount;l++){				
						gids.add(sheetDataList.getCell(1,l).getContents().trim());		
						gidsList = gidsList +"'"+ sheetDataList.getCell(1,l).getContents().trim().toString()+"',";
					}
		            
		            
		            
		            
		            
		            
		            for(int i=0;i<finalList.size();i++){	
		            	String[] strList=finalList.get(i).toString().split("~!~");
		            	AccessionMetaDataBean amdb=new AccessionMetaDataBean();					
						//******************   GermplasmTemp   *********************//*	
						amdb.setDataset_id(dataset_id);
						amdb.setGid(Integer.parseInt(strList[0].toString()));
						amdb.setNid(Integer.parseInt(strList[1].toString()));
						
						session.save(amdb);
						
						if (i % 1 == 0){
							session.flush();
							session.clear();
						}
		            
		            }
		            /*System.out.println("lstMarkers="+lstMarkers);
		            System.out.println("map:"+map);*/
		            String charData="";
		            maxMid=uptMId.getMaxIdValue("marker_id","gdms_marker",session);		
					String[] markers=marker.split(",");
					
					ArrayList mids=new ArrayList();
					//if(lstMarIdNames.size()==0){
						for(int f=0; f<markersList.size();f++){
							MarkerInfoBean mib=new MarkerInfoBean();
							if(lstMarkers.contains(markersList.get(f))){
								intRMarkerId=(Integer)(markersMap.get(markersList.get(f)));							
								mids.add(intRMarkerId);
							}else{
								maxMid=maxMid+1;
								intRMarkerId=maxMid;
								mids.add(intRMarkerId);	
								mib.setMarkerId(intRMarkerId);
								mib.setMarker_type(marker_type);
								mib.setMarker_name(markersList.get(f).toString());
								//mib.setCrop(sheetSource.getCell(1,5).getContents());
								mib.setSpecies(sheetSource.getCell(1,5).getContents());
								
								session.save(mib);
								if (f % 1 == 0){
									session.flush();
									session.clear();
								}
							}
							
							
						}
					//}
						System.out.println("MIDS="+mids);
					/*System.out.println("MIDS="+mids);
					System.out.println("markers="+lstMarkers.size());
					System.out.println("gids count="+gids.size());*/
					if(mapType.equalsIgnoreCase("allelic")){
						if(marker_type.equalsIgnoreCase("snp")){
							intAC_ID=uptMId.getMaxIdValue("ac_id","gdms_char_values",session);	
							String[] pGids=parentGids.split(",");
							int mcount=0;
							int gcount=0;
							intAC_ID=intAC_ID+1;
							for(int r=1;r<3;r++){
								mcount=0;
								for(int c=3;c<colCount;c++){
									CharArrayBean chb=new CharArrayBean();
									CharArrayCompositeKey cack = new CharArrayCompositeKey();							
									cack.setDataset_id(dataset_id);
									cack.setAc_id(intAC_ID);
									chb.setComKey(cack);
			                       
									charData=sheetDataList.getCell(c,r).getContents().trim();
									
									chb.setChar_value(charData);
									chb.setGid(Integer.parseInt(pGids[gcount]));
									
									chb.setMarker_id(Integer.parseInt(mids.get(mcount).toString()));
									//chb.setGermplasm_name(genotype[s]);
									session.save(chb);
									mcount++;
									
									intAC_ID++;
															
									if (r % 1 == 0){
										session.flush();
			                            session.clear();
									}
								}
								gcount++;
							}
						}else if((marker_type.equalsIgnoreCase("ssr"))||(marker_type.equalsIgnoreCase("DArT"))){
							intAC_ID=uptMId.getMaxIdValue("an_id","gdms_allele_values",session);	
							String[] pGids=parentGids.split(",");
							int mcount=0;
							int gcount=0;
							intAC_ID=intAC_ID+1;
							for(int r=1;r<3;r++){
								mcount=0;
								for(int c=3;c<colCount;c++){
									IntArrayBean chb=new IntArrayBean();
									IntArrayCompositeKey cack = new IntArrayCompositeKey();							
									cack.setDataset_id(dataset_id);
									cack.setAn_id(intAC_ID);
									chb.setComKey(cack);
			                       
									charData=sheetDataList.getCell(c,r).getContents().trim();
									
									chb.setAllele_bin_value(charData);
									chb.setGid(Integer.parseInt(pGids[gcount]));
									
									chb.setMarker_id(Integer.parseInt(mids.get(mcount).toString()));
									//chb.setGermplasm_name(genotype[s]);
									session.save(chb);
									mcount++;
									
									intAC_ID++;
															
									if (r % 1 == 0){
										session.flush();
			                            session.clear();
									}
								}
								gcount++;
							}
						}
										
					}	
					int gi=0;
					mp_id=mp_id+1;
					for(int i=rows;i<rowCount;i++){	
						//String[] insGids=fGids.split(",");
						int m=0;
						for(int j=3;j<colCount;j++){
							MappingPopCharValuesBean mcb=new MappingPopCharValuesBean();		
							MapCharArrayCompositeKey Mcack = new MapCharArrayCompositeKey();
							Mcack.setDataset_id(dataset_id);
							Mcack.setMp_id(mp_id);
							//mcb.setMcomKey(Mcack);
							mcb.setMapComKey(Mcack);
							/*mcb.setMp_id(mp_id);
							mcb.setDataset_id(dataset_id);*/
							
							//mcb.setDataset_id(dataset_id);							
							/*if(sheetDataList.getCell(j,i).getContents().equalsIgnoreCase("A")){
								charData="A:A";	
							}else if(sheetDataList.getCell(j,i).getContents().equalsIgnoreCase("C")){	
								charData="C:C";
							}else if(sheetDataList.getCell(j,i).getContents().equalsIgnoreCase("G")){
								charData="G:G";
							}else if(sheetDataList.getCell(j,i).getContents().equalsIgnoreCase("T")){
								charData="T:T";
							}else if(sheetDataList.getCell(j,i).getContents().equalsIgnoreCase("B")){
								charData="B:B";
							}else{*/
								charData=sheetDataList.getCell(j,i).getContents();
							//}
							//System.out.println(i+"   "+j+"   "+charData+"     "+intRMarkerId+"   "+gids.get(i));
							mcb.setMap_char_value(charData);
							mcb.setGid(Integer.parseInt(gids.get(gi).toString()));
							
							mcb.setMarker_id(Integer.parseInt(mids.get(m).toString()));
							
							session.save(mcb);
							
							
							intDataOrderIndex++;
													
							g++;
							mp_id++;
							m++;
							if (g % 1 == 0){
								session.flush();
								session.clear();
							}					
						}
						m=0;
						gi++;
						g=0;
						//m=0;
					}
					for(int m1=0;m1<mids.size();m1++){					
						//System.out.println("gids doesnot Exists    :"+lstgermpName+"   "+gids[l]);
						MarkerMetaDataBean mdb=new MarkerMetaDataBean();					
						//******************   GermplasmTemp   *********************//*	
						mdb.setDataset_id(dataset_id);
						mdb.setMarker_id(Integer.parseInt(mids.get(m1).toString()));
						
						session.save(mdb);
						if (m1 % 1 == 0){
		                    session.flush();
		                    session.clear();
						}
					
					}					
					tx.commit();
				}				
			}catch(Exception e){
				session.clear();
				//con.rollback();
				//tx.rollback();				
				e.printStackTrace();
			}finally{	
				factory.close();
				session.clear();				
			  }
			return result;
		}

}
