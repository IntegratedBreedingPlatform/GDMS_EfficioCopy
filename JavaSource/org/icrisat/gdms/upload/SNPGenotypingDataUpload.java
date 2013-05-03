/**
 * 
 */
package org.icrisat.gdms.upload;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

public class SNPGenotypingDataUpload {
	String strupl="";
	
	//getUpload method is used to insert the SNP Genotyping data from SNPGenotyping Template to database.	
	static Map<Integer, ArrayList<String>> hashMap = new HashMap<Integer,  ArrayList<String>>();  
	
	
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
		String[] gids=null;
		String[] genotype=null;
		List<List<String>> genoData = new ArrayList<List<String>>(); 
		int intAC_ID = 0;
		int intRMarkerId=1;
		int mid=0;
		String charData="";
		String marker_type="SNP";
		String marker="";
		List<String> str=new ArrayList<String>();
		
		String ErrMsg="";
		int size=0;
		
		List genoDataMarkers = new ArrayList();
		String alertGN="no";
        String alertGID="no";
        String notMatchingData="";
        String notMatchingGIDS="";
        String notMatchingDataExists="";
		int maxMid=0;
		int gidCount=0;
		int gCount=0;int dataCount=0;
		   String gNames="";
		   ArrayList gidsAList=new ArrayList();
		try{
			DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			factory = new ManagerFactory(local, central);
			
			 GermplasmDataManager manager = factory.getGermplasmDataManager();
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
						Query rsDatasetNames=session.createQuery("from DatasetBean where dataset_name ='"+strDatasetName+"'");				
						
						List result1= rsDatasetNames.list();
						System.out.println(".............:"+result1.size());
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
				HashMap<Integer, String> GIDsMap = new HashMap<Integer, String>();
				
				ArrayList gidNamesList=new ArrayList();
				
				 //SortedMap GIDsMap = new TreeMap();
	            for(int d=1;d<gids.length;d++){	               
	            	gidsForQuery = gidsForQuery + gids[d]+",";
	            	if(!gidNamesList.contains(Integer.parseInt(gids[d])))
						gidNamesList.add(Integer.parseInt(gids[d])+","+genotype[d]);
	            	//GIDsMap.put(Integer.parseInt(gids[d]), genotype[d]);
	            	if(!gidsAList.contains(Integer.parseInt(gids[d])))
						gidsAList.add(gids[d]);
	            	
	            	NamesList.add(genotype[d]);
	            }
	            gidsForQuery=gidsForQuery.substring(0, gidsForQuery.length()-1);
	            
	            System.out.println("....:"+gidsAList);
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
	            
	            SortedMap map = new TreeMap();
	            List lstgermpName = new ArrayList();
	           
				List<Name> names = null;
				for(int n=0;n<gidsAList.size();n++){
					names = manager.getNamesByGID(Integer.parseInt(gidsAList.get(n).toString()), null, null);
					for (Name name : names) {
						//System.out.println("........:"+name.getGermplasmId()+"  ,"+name.getNval()+" :  "+name.getNid());
						lstgermpName.add(name.getGermplasmId());
						map.put(name.getGermplasmId(), name.getNval());
						addValues(name.getGermplasmId(), name.getNval().toLowerCase());	
					}
				}
	           
	           if(map.size()==0){
	        	   alertGID="yes";
	        	   size=0;
	           }
	           int gidToCompare=0;
	           String gNameToCompare="";
	           //String gNameFromMap="";
	           ArrayList gNameFromMap=new ArrayList();
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
		        		   size=map.size();
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
			//System.out.println("dataset id = ");
			session.save(ub);
			
			//*************  dataset_users*************
			usb.setDataset_id(dataset_id);
			usb.setUser_id(user_id);
			
			session.save(usb);
			
			
			SortedMap mapN = new TreeMap();
			System.out.println(",,,,,,,,,,,,,,,,,gNames="+gNames);
			ArrayList finalList =new ArrayList();
			ArrayList gidL=new ArrayList();
			/*ArrayList lstNids=uptMethod.getNids("gid, nid", "names", "nval", session, gNames);
			for(int w=0;w<lstNids.size();w++){
	        	Object[] strMareO= (Object[])lstNids.get(w);
	           // System.out.println("W=....."+w+"    "+strMareO[0]+"   "+strMareO[1]);
	            if(!gidL.contains(Integer.parseInt(strMareO[0].toString())))
	            	gidL.add(Integer.parseInt(strMareO[0].toString()));
	            mapN.put(Integer.parseInt(strMareO[0].toString()), strMareO[1]);
	          
	 		}*/
			
			/*
			 * getting nids with gid and nval for inserting into gdms_acc_metadataset table			
			*/
			Name names = null;
			for(int n=0;n<gidsAList.size();n++){
				names = manager.getNameByGIDAndNval(Integer.parseInt(gidsAList.get(n).toString()), NamesList.get(n).toString());
				if(!gidL.contains(names.getGermplasmId()))
	            	gidL.add(names.getGermplasmId());
	            mapN.put(names.getGermplasmId(), names.getNid());
				//for (Name name : names) {					
					// lstgermpName.add(name.getGermplasmId());
					// map.put(name.getGermplasmId(), name.getNval());	            
		       // }
			}
			
			
	        for(int a=0;a<gidsAList.size();a++){
	        	int gid1=Integer.parseInt(gidsAList.get(a).toString());
	        	if(gidL.contains(gid1)){
	        		finalList.add(gid1+"~!~"+mapN.get(gid1));	
	        	}
	        }
            System.out.println("******************  "+finalList);
			
			
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
         
            HashMap<String, Object> map = new HashMap<String, Object>();
            ArrayList lstMarIdNames=uptMethod.getMarkerIds("marker_id, marker_name", "gdms_marker", "marker_name", session, marForQuery);
          
            List lstMarkers = new ArrayList();
            for(int w=0;w<lstMarIdNames.size();w++){
                 Object[] strMareO= (Object[])lstMarIdNames.get(w);
                 lstMarkers.add(strMareO[1]);
                 String strMa123 = (String)strMareO[1];
                 map.put(strMa123, strMareO[0]);
                 
            }
            
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
							intRMarkerId=(Integer)(map.get(marker));
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
				//System.out.println(str);
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
		}catch(Exception e){
			e.printStackTrace();
			session.clear();
			//con.rollback();
			tx.rollback();
		}finally{			
		    // Actual contact insertion will happen at this step
		    //session.flush();
		    //session.close();
			//conn.close();
			session.clear();
			session.disconnect();
			factory.close();
		}
		
		return strupl;
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
