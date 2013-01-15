package org.icrisat.gdms.upload;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

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


public class SSRGenotypingDataUpload {
	String str="";
	private Session session;
	
	private Transaction tx;	
	
	public String getUpload(HttpServletRequest request, String fname) throws SQLException{
		
		ManagerFactory factory =null;
		
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
			
			
			String alertGN="no";
	        String alertGID="no";
	        String notMatchingData="";
	        String notMatchingGIDS="";
	        String notMatchingDataExists="";
	        int markerID=0;
	        String ErrMsg = "";
			//int intDataOrderIndex = 1;
			String dataset_type="SSR";
			String datatype="int";
			DatasetBean ub=new DatasetBean();
			GenotypeUsersBean usb=new GenotypeUsersBean();	
			ConditionsBean ubConditions=new ConditionsBean();
			CheckNumericDatatype cnd = new CheckNumericDatatype();
			UsersBean u=new UsersBean();
			
			//MetaDatasetBean ub1=new MetaDatasetBean();
			Workbook workbook=Workbook.getWorkbook(new File(fname));
			String[] strSheetNames=workbook.getSheetNames();
			
			///All the Sheets
			///Excel sheet validations
			ExcelSheetValidations fv = new ExcelSheetValidations();
			String strFv=fv.validation(workbook, request,"SSRG");
			//System.out.println("Valid="+strFv);
			if(!strFv.equals("valid"))
				return strFv;
			
			
			//avoids the case sensitive of sheet names
			String strSource="",strDatalist="";
			
			for (int i=0;i<strSheetNames.length;i++){				
				if(strSheetNames[i].equalsIgnoreCase("SSR_Source"))
					strSource = strSheetNames[i];
				if(strSheetNames[i].equalsIgnoreCase("SSR_Data List"))
					strDatalist = strSheetNames[i];					
			}
				
			int size=0;
			///timestamp code			
			String mon="";
			Calendar cal = new GregorianCalendar();
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			if(month>=10) 
				mon=String.valueOf(month+1);
			else 
				mon="0"+(month+1);
			  
			 String curDate=year+ "-" + mon + "-" +day;
			
			Sheet sheetSource = workbook.getSheet(strSource);
			
			Sheet sheetDataList=workbook.getSheet(strDatalist);
			int intDataListRowCount=sheetDataList.getRows();
			
			MaxIdValue uptMId=new MaxIdValue();		
			//getting the germplasm names and count of germplasm from Data List of Template
			List<String> listGIDs = new ArrayList<String>();
			List<String> listGNames = new ArrayList<String>();
			String strMarCheck = (String) sheetDataList.getCell(2,1).getContents().trim();
			//System.out.println("4:"+sheetSource.getCell(1,4).getContents()+"     5:"+sheetSource.getCell(1,5).getContents());
			String species=sheetSource.getCell(1,5).getContents().trim();
			String curAcc ="";String preAcc = "";String strPreAmount="";
			for(int i=1;i<intDataListRowCount;i++){
				String strAmount=(String)sheetDataList.getCell(10,i).getContents().trim();
				float fltAmount = Float.parseFloat(strAmount);
				//System.out.println("floatval="+fltAmount+" == " +strMarCheck+" == "+(String)sheetDataList.getCell(1,i).getContents().trim());
				if(strMarCheck == (String)sheetDataList.getCell(2,i).getContents().trim()){
					if((fltAmount == 0.0) || (fltAmount == 1.0)){
						//System.out.println("IF............");
						
						//if((listGNames.contains(sheetDataList.getCell(0,1).getContents().trim())) && (fltAmount == 1.0))
						listGNames.add(sheetDataList.getCell(1,i).getContents().trim());
						listGIDs.add(sheetDataList.getCell(0,i).getContents().trim());
							//System.out.println("Acc:01="+(String)sheetDataList.getCell(0,i).getContents().trim());
					}else{
						//System.out.println("ELSE..............");
						//System.out.println("BOolean" +listGNames.contains(sheetDataList.getCell(0,i).getContents().trim()));
																			
						//if(!(listGNames.contains(sheetDataList.getCell(0,i).getContents().trim()))){
						listGNames.add(sheetDataList.getCell(1,i).getContents().trim());
						listGIDs.add(sheetDataList.getCell(0,i).getContents().trim());
							//System.out.println("contains="+(String)sheetDataList.getCell(0,i).getContents().trim());
						//}else{
						if(i>1){
							curAcc = (String)sheetDataList.getCell(0,i).getContents().trim();
							preAcc = (String)sheetDataList.getCell(0,i-1).getContents().trim();
							//String strPreAmount=(String)sheetDataList.getCell(9,i-1).getContents().trim();
							strPreAmount=(String)sheetDataList.getCell(10,i-1).getContents().trim().toString();
							//System.out.println("&&&&&&&&&&&&&&&&&&&&   strPreAmount="+strPreAmount);
						}else if (i==1){
							curAcc = (String)sheetDataList.getCell(0,i).getContents().trim();
							preAcc = (String)sheetDataList.getCell(0,i+1).getContents().trim();
							//String strPreAmount=(String)sheetDataList.getCell(9,i-1).getContents().trim();
							strPreAmount=(String)sheetDataList.getCell(10,i+1).getContents().trim().toString();
							
						}
							double fltPreAmount = Float.parseFloat(strPreAmount);
														
							int fltA=0;
								for(int r=1;r<25;r++){
									double f = fltAmount*r;
									
									MaxIdValue rt = new MaxIdValue();
									double fltRB=rt.roundThree(f);
									if((fltRB>=0.900 && fltRB<=0.999))
										fltRB=Math.round(f);
									
									if(fltRB==1.000){
										//System.out.println("fltRB==1.000="+fltRB+"Rvalue="+r);
										fltA=r;
										r=25;
									}
								}							
							if(fltA!=0){
								i=i+fltA-1;								
							}					
					}		
				}else{					
					//strMarCheck=(String) sheetDataList.getCell(1,i).getContents().trim();
					strMarCheck=(String) sheetDataList.getCell(2,i).getContents().trim();
					i=i-1;
				}
			 }
			String gidsString="";
			ArrayList gidsList = new ArrayList();
			ArrayList gnamesList = new ArrayList();
			for(int g1=0;g1<listGIDs.size();g1++){
				if(!gidsList.contains(listGIDs.get(g1)))
					gidsList.add(listGIDs.get(g1));
			}
			//System.out.println("listGIDs="+listGIDs);
			//System.out.println("gidsList="+gidsList);
			for(int g2=0;g2<listGNames.size();g2++){
				if(!gnamesList.contains(listGNames.get(g2)))
					gnamesList.add(listGNames.get(g2));
			}
			//System.out.println(gnamesList.size()+"    "+gidsList.size());
			int gCount=gnamesList.size();
			int gidCount=gidsList.size();
			if(gidCount<gCount){
				ErrMsg = "The number of GIDs is less than the number of Germplasm names provided";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
			}else if(gCount<gidCount){
				ErrMsg = "The number of GIDs is more than the number of Germplasm names provided";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
			}
			
			
				//String gidsForQuery = "";
				ArrayList gidsForQuery=new ArrayList();
				String gNames="";
				HashMap<Integer, String> GIDsMap = new HashMap<Integer, String>();
				ArrayList gidNamesList=new ArrayList();
				 //SortedMap GIDsMap = new TreeMap();
	            for(int d=1;d<gidsList.size();d++){	               
	            	//gidsForQuery = gidsForQuery + gidsList.get(d)+",";
	            	
	            	gNames=gNames+"'"+gnamesList.get(d).toString()+"',";
	            	if(!gidNamesList.contains(Integer.parseInt(gidsList.get(d).toString())))
						gidNamesList.add(Integer.parseInt(gidsList.get(d).toString())+","+gnamesList.get(d).toString());
	            	
	            	
	            	GIDsMap.put((Integer.parseInt(gidsList.get(d).toString())), gnamesList.get(d).toString());
	            }
	            //gidsForQuery=gidsForQuery.substring(0, gidsForQuery.length()-1);
	           // System.out.println("GIDsMap.."+GIDsMap);
	            Map<Object, String> sortedMap = new TreeMap<Object, String>(GIDsMap);
	            //System.out.println("%%%%%%%%%%%%%sortedMap=:"+sortedMap);
	            //HashMap<Object, String> map = new HashMap<Object, String>();
	            //ArrayList lstGIDs=uptMethod.getGIds("gid, germplasm_name", "germplasm_temp", "gid", session, gidsForQuery);
	            //ArrayList lstGIDs=uptMId.getGIds("gid, nval", "names", "gid", session, gidsForQuery);
	            SortedMap gidsmap = new TreeMap();
	            List lstgermpName = new ArrayList();
	            
				List<Name> names = null;
				for(int n=0;n<gidsList.size();n++){
					names = manager.getNamesByGID(Integer.parseInt(gidsList.get(n).toString()), null, null);
					for (Name name : names) {					
						 lstgermpName.add(name.getGermplasmId());
						 gidsmap.put(name.getGermplasmId(), name.getNval());	            
			        }
				}
	            
	            
	           /* SortedMap gidsmap = new TreeMap();
	            List lstgermpName = new ArrayList();
	            for(int w=0;w<lstGIDs.size();w++){
	                 Object[] strMareO= (Object[])lstGIDs.get(w);
	                 lstgermpName.add(strMareO[0]);
	                 String strMa123 = (String)strMareO[1];
	                 gidsmap.put(strMareO[0], strMa123);
	                 
	            }*/
	            /*Iterator iterator = gidsmap.keySet().iterator();
		        Iterator iterator1 = sortedMap.keySet().iterator();*/
	           //System.out.println("map=:"+map.size());
	           if(gidsmap.size()==0){
	        	   alertGID="yes";
	        	   size=0;
	           }
	            
	           String markersForQuery="";
	           ArrayList markerList = new ArrayList();
				for(int m1=1;m1<intDataListRowCount;m1++){
					//markersForQuery=markersForQuery+"'"+sheetDataList.getCell(2,m1).getContents().trim()+"',";
					if(!markerList.contains(sheetDataList.getCell(2,m1).getContents().trim()))
						markerList.add(sheetDataList.getCell(2,m1).getContents().trim());
				}
	          // System.out.println(markerList.size()+"  markers="+markerList);
	           for(int ml=0;ml<markerList.size();ml++){
	        	   markersForQuery=markersForQuery+"'"+markerList.get(ml)+"',";
	           }
	           markersForQuery=markersForQuery.substring(0, markersForQuery.length()-1);
	           int gidToCompare=0;
	           String gNameToCompare="";
	           String gNameFromMap="";
	           //System.out.println("gidNamesList="+gidNamesList);
	           if(gidsmap.size()>0){
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
		        		   gNameFromMap=gidsmap.get(gidToCompare).toString();
		        		   //System.out.println("...."+gNameToCompare+"   "+map.get(gidToCompare).equals(gNameToCompare)+"  from map: "+map.get(gidToCompare));
		        		   if(!(gNameFromMap.toLowerCase().equals(gNameToCompare.toLowerCase()))){
		        			   notMatchingData=notMatchingData+gidToCompare+"   "+gidsmap.get(gidToCompare)+"\n\t";
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
	          		
	           SortedMap map = new TreeMap();
	           SortedMap finalMarkersMap = new TreeMap();
	           HashMap<String, Object> markerMap = new HashMap<String, Object>();
	            ArrayList lstMarIdNames=uptMId.getMarkerIds("marker_id, marker_name", "gdms_marker", "marker_name", session, markersForQuery);
	          
	            List lstMarkers = new ArrayList();
	            for(int w=0;w<lstMarIdNames.size();w++){
	                 Object[] strMareO= (Object[])lstMarIdNames.get(w);
	                 lstMarkers.add(strMareO[1]);
	                 String strMa123 = (String)strMareO[1];
	                 map.put(strMa123, strMareO[0]);
	                 
	            }
	            int markerId=0;
	            String marker="";
	            int maxMarkerId=uptMId.getMaxIdValue("marker_id","gdms_marker",session);
	         // System.out.println("marker in map=:"+map); 
	           
////		Retrieve the maximum column id from the database
			
			int intDatasetId=uptMId.getMaxIdValue("dataset_id","gdms_dataset",session);
			int dataset_id=intDatasetId+1;
			//** writing to 'dataset' table **//*
			ub.setDataset_id(dataset_id);
			ub.setDataset_name((String)sheetSource.getCell(1,2).getContents().trim());
			ub.setDataset_desc((String)sheetSource.getCell(1,3).getContents().trim());
			ub.setDataset_type(dataset_type);
			ub.setGenus(sheetSource.getCell(1,4).getContents().trim());
			ub.setSpecies(species);
			ub.setUpload_template_date(curDate);					
			ub.setDatatype(datatype);
			ub.setMissing_data(sheetSource.getCell(1,6).getContents().trim());
			//System.out.println("dataset id = ");
			session.save(ub);
			
			/*for(int n=0;n<nidsmap.size();n++){
				DatasetGidsBean gb= new DatasetGidsBean();
				gb.setGid(Integer.parseInt(gidsList.get(n).toString()));
				
				//gb.setGermplasm_name(gnamesList.get(g).toString());
				//gb.setDataset_id(dataset_id);
				session.save(gb);
			}*/
			/*ArrayList lstNids=uptMId.getNids("gid, nid", "names", "nval", session, gNames.substring(0,gNames.length()-1));
	        for(int w=0;w<lstNids.size();w++){
	        	Object[] strMareO= (Object[])lstNids.get(w);
	            //System.out.println("....."+strMareO[0]+"   "+strMareO[1]);
	            DatasetGidsBean gb= new DatasetGidsBean();
	 			gb.setGid(Integer.parseInt(strMareO[0].toString()));
	 			gb.setNid(Integer.parseInt(strMareO[1].toString()));
	 				
	 			session.save(gb);
	 		}*/
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
			
			Name name = null;
			for(int n=0;n<gidsList.size();n++){
				name = manager.getNameByGIDAndNval(Integer.parseInt(gidsList.get(n).toString()), gnamesList.get(n).toString());
				if(!gidL.contains(name.getGermplasmId()))
	            	gidL.add(name.getGermplasmId());
	            mapN.put(name.getGermplasmId(), name.getNid());
				
			}
			
	       
	        for(int a=0;a<gidsList.size();a++){
	        	int gid1=Integer.parseInt(gidsList.get(a).toString());
	        	if(gidL.contains(gid1)){
	        		finalList.add(gid1+"~!~"+mapN.get(gid1));	
	        	}
	        }
            System.out.println("******************  "+finalList);
			
			int intDataOrderIndex =uptMId.getMaxIdValue("an_id","gdms_allele_values",session);
			int user_id=uptMId.getUserId("userid", "users", "uname", session,sheetSource.getCell(1,1).getContents().trim());
			//System.out.println("user_id="+user_id);
			/*if(user_id==0){
				int maxUid=uptMId.getMaxIdValue("userid","users",session);
				ErrMsg = "PI doesnot exists in the database";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
				u.setUserid(maxUid+1);
				u.setUname(sheetSource.getCell(1,1).getContents().trim());
				u.setUpswd("gdms");
				session.save(u);
			}*/
			//*************  writing to dataset_users table*************
			usb.setDataset_id(dataset_id);
			usb.setUser_id(user_id);
			
			session.save(usb);
			
			//*********** writing to dataset_details table **********
			/*ubConditions.setDataset_id(dataset_id);
			ubConditions.setMissing_data(sheetSource.getCell(1,5).getContents().trim());
			
			session.saveOrUpdate(ubConditions);*/	
			
			//getting the marker names and count from Data List of Template
			
			
			String markers="";
			int mid=0;
			//String marker="";
			int count=0;
			//int maxMarkerId=uptMId.getMaxIdValue("marker_id","marker",session);
			ArrayList midsList = new ArrayList();
			SortedMap mids=new TreeMap();
			int intRMarkerId=0;
			for(int m=0;m<markerList.size();m++){				
				MarkerInfoBean mib=new MarkerInfoBean();
				if(lstMarkers.contains(markerList.get(m))){
					intRMarkerId=(Integer)(map.get(markerList.get(m)));	
					mids.put(markerList.get(m).toString(), intRMarkerId);
					midsList.add(intRMarkerId);					
				}else{
					maxMarkerId=maxMarkerId+1;
					intRMarkerId=maxMarkerId;
					mids.put(markerList.get(m).toString(), intRMarkerId);
					midsList.add(intRMarkerId);	
					
					mib.setMarkerId(intRMarkerId);
					mib.setMarker_type(dataset_type);
					mib.setMarker_name(markerList.get(m).toString());
					mib.setSpecies(sheetSource.getCell(1,5).getContents());
					
					session.save(mib);
					if (m % 1 == 0){
						session.flush();
						session.clear();
					}
				}
				
			}
			
			//System.out.println("mids="+mids);
			
			if (sheetDataList==null){
				System.out.println("Empty Sheet");		
			}else{
				System.out.println("NOT Empty Sheet");		
				int intNR=sheetDataList.getRows();			
				int intColRowEmpty=0;			
				for(int i=0;i<intNR;i++){
					Cell c=sheetDataList.getCell(0,i);
					String s=c.getContents();
					if(!s.equals("")){
						intColRowEmpty=intColRowEmpty + 1;						
					}
				}

				int m=0;
				
				
				//End marker metadataset insertion //
				intDataOrderIndex=intDataOrderIndex+1;
				for(int i=1;i<intColRowEmpty;i++){					
					MarkerInfoBean mib=new MarkerInfoBean();
					marker=sheetDataList.getCell(2,i).getContents().trim();					
					IntArrayBean intb=new IntArrayBean();
					IntArrayCompositeKey intcomk=new IntArrayCompositeKey();
					
					intcomk.setDataset_id(dataset_id);
					intcomk.setAn_id(intDataOrderIndex);
					intb.setComKey(intcomk);					
					//intb.setMarker_id(markerID);
					intb.setMarker_id(Integer.parseInt(mids.get(marker).toString()));
					//System.out.println("88888888888888888888888888 "+m1[m]);
					intb.setGid(Integer.parseInt(listGIDs.get(m)));
					
					String strV = (String)sheetDataList.getCell(5,i).getContents().trim();
					String strRV = (String)sheetDataList.getCell(6,i).getContents().trim();
					String strAmountVal = (String)sheetDataList.getCell(10,i).getContents().trim();
					int intAlleleBinValues = 0;
					float intAlleleRawValues = 0;
					if(cnd.isInteger(strV)){
						intAlleleBinValues = Integer.parseInt(sheetDataList.getCell(5,i).getContents().trim());
					}else{
						String str=(String)sheetDataList.getCell(5,i).getContents().trim();
						/*if(str.equalsIgnoreCase("?")){
							intAlleleBinValues=999999999;
						}else{
							intAlleleBinValues=88888888;
						}*/
					}
					
					if(cnd.isFloat(strRV)){
						intAlleleRawValues = Float.parseFloat(sheetDataList.getCell(6,i).getContents().trim());
					}else{
						String str=(String)sheetDataList.getCell(6,i).getContents().trim();
						/*if(str.equalsIgnoreCase("?")){
							intAlleleRawValues=999999999;
						}else{
							intAlleleRawValues=88888888;
						}*/
					}
					//check the amount value and insert the data into database 
					//without using amount value
					
						if((strAmountVal.equals("1"))||(strAmountVal.equals("0"))){
						//if(strAmountVal.equals("1")){
							//System.out.println("strAmountval="+strAmountVal);
							String strValue = intAlleleBinValues+":"+intAlleleBinValues;
							String strRValue = intAlleleRawValues+":"+intAlleleRawValues;
							
							intb.setAllele_bin_value(strValue);
							intb.setAllele_raw_value(strRValue);						
						}else{
							
							String strValue1="";
							String strRValue1="";
							////////////////////tr	
							//amout value 
							String strA = (String)sheetDataList.getCell(10,i).getContents().trim();
							int intAmoutVal = 0;
							for(int l=1;l<17;l++){
								Float val;
								val = Float.parseFloat(strA) * l;
								if(val >= 0.9){
									intAmoutVal = l;
									break;
								}
							}
							intAmoutVal = intAmoutVal +i;
							//System.out.println("....intAmoutVal:"+intAmoutVal);
							for(int n=i;n<intAmoutVal;n++){
								String strV1 = (String)sheetDataList.getCell(5,n).getContents().trim();
								String strRV1 = (String)sheetDataList.getCell(6,n).getContents().trim();
								int intAlleleBinValues1 = 0;
								float intAlleleRawValues1 = 0;
								if(cnd.isInteger(strV1)){
									intAlleleBinValues1 = Integer.parseInt(sheetDataList.getCell(5,n).getContents().trim());
								}else{
									String str=(String)sheetDataList.getCell(5,n).getContents().trim();
									/*if(str.equalsIgnoreCase("?")){
										intAlleleBinValues1=999999999;
									}else{
										intAlleleBinValues1=88888888;
									}*/
								}
								
								if(cnd.isFloat(strRV1)){
									intAlleleRawValues1 = Float.parseFloat(sheetDataList.getCell(6,n).getContents().trim());
								}else{
									String str=(String)sheetDataList.getCell(6,n).getContents().trim();
									/*if(str.equalsIgnoreCase("?")){
										intAlleleRawValues1=999999999;
									}else{
										intAlleleRawValues1=88888888;
									}*/
								}			
								
								 strValue1 = strValue1+intAlleleBinValues1+":";
								 strRValue1 = strRValue1+intAlleleRawValues1+":";
								 i++;
								 marker=sheetDataList.getCell(2,n).getContents().trim();
							}
							i--;
							
							//System.out.println(".............:"+strValue1+"   "+(strValue1.length()-1)+"   "+marker);
							
							
							strValue1=strValue1.substring(0, strValue1.length()-1);
							strRValue1=strRValue1.substring(0, strRValue1.length()-1);
							
							//////////////////////////////////////////////////
							
							//System.out.println("strRValue1="+strRValue1);
							//System.out.println("strValue1="+strValue1);
							
							intb.setAllele_bin_value(strValue1);
							intb.setAllele_raw_value(strRValue1);
							
						}
						intDataOrderIndex++;
						m++;
						session.save(intb);								
						if (i % 1 == 0){
							session.flush();
							session.clear();
						}
				}
				
			
				//System.out.println("gidsList="+gidsList);
				for(int g=0;g<finalList.size();g++){					
					String[] strList=finalList.get(g).toString().split("~!~");
	            	AccessionMetaDataBean amdb=new AccessionMetaDataBean();					
					//******************   GermplasmTemp   *********************//*	
					amdb.setDataset_id(dataset_id);
					amdb.setGid(Integer.parseInt(strList[0].toString()));
					amdb.setNid(Integer.parseInt(strList[1].toString()));
					
					session.save(amdb);
					
					if (g % 1 == 0){
						session.flush();
						session.clear();
					}
				
				}
				//System.out.println(mids.size()+"   "+mids);
				for(int m1=0;m1<midsList.size();m1++){					
					//System.out.println("gids doesnot Exists    :"+lstgermpName+"   "+gids[l]);
					MarkerMetaDataBean mdb=new MarkerMetaDataBean();					
					//******************   GermplasmTemp   *********************//*	
					mdb.setDataset_id(dataset_id);
					mdb.setMarker_id(Integer.parseInt(midsList.get(m1).toString()));
					
					session.save(mdb);
					if (m1 % 1 == 0){
	                    session.flush();
	                    session.clear();
					}
				
				}	
				tx.commit();	
			
		}
		str="inserted";
			
		
		}catch(Exception e){
			session.clear();			
			tx.rollback();			
			e.printStackTrace();
		}finally{
			// Actual contact insertion will happen at this step
		    //session.flush();
		    //session.close();
			//session.clear();
			factory.close();
		}
		return str;
	}

}
