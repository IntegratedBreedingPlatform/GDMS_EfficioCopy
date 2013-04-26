package org.icrisat.gdms.upload;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Sheet;
import jxl.Workbook;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

import com.mysql.jdbc.Connection;

public class QTLDataUpload {
	
	private Session session;
	private Transaction tx;
	Connection con = null;
	HttpServletRequest request;
	/*String crop=request.getSession().getAttribute("crop").toString();
	public QTLDataUpload(){
		session = HibernateSessionFactory.currentSession(crop);
		tx=session.beginTransaction();	
	}*/
		
	public String setQTLData(HttpServletRequest request, String qtlfile) throws SQLException{
		String result = "inserted";
		String dataset_type="QTL";
		String datatype="int";
		
		ManagerFactory factory =null;
		
		try{
			//String crop=request.getSession().getAttribute("crop").toString();
			session = HibernateSessionFactory.currentSession();
			tx=session.beginTransaction();
			
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			
			factory = new ManagerFactory(local, central);
			
			TraitDataManager manager=factory.getTraitDataManager();
			
			*/
			
			String ErrMsg="";
			MaxIdValue uptMId=new MaxIdValue();
			
			DatasetBean ub=new DatasetBean();
			GenotypeUsersBean usb=new GenotypeUsersBean();	
			UsersBean u=new UsersBean();
			String strSource="",strDatalist="";
			HttpSession httpsession = request.getSession();
			Workbook workbook=Workbook.getWorkbook(new File(qtlfile));
			String[] strSheetNames=workbook.getSheetNames();
			
			ExcelSheetValidations fv = new ExcelSheetValidations();
			String strFv=fv.validation(workbook, request,"QTL");
			System.out.println("Valid="+strFv);
			if(!strFv.equals("valid"))
				return strFv;
			
			for (int i=0;i<strSheetNames.length;i++){				
				if(strSheetNames[i].equalsIgnoreCase("QTL_Source"))
					strSource = strSheetNames[i];
				if(strSheetNames[i].equalsIgnoreCase("QTL_Data"))
					strDatalist = strSheetNames[i];					
			}
			Sheet sheetSource = workbook.getSheet(strSource);
			Sheet sheetData = workbook.getSheet(strDatalist);		
			
			int rowCount=sheetData.getRows();
			int colCount=sheetData.getColumns();	
			int intDatasetId=uptMId.getMaxIdValue("dataset_id","gdms_dataset",session);
			//int dataset_id=intDatasetId+1;
			int dataset_id=intDatasetId-1;
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
			  
			 String curDate=year+"-"+mon+"-"+day;
			 
			 // validating traits
			 ArrayList traitList=new ArrayList();
			 
			 String alertT="no";
		     String alertGN="no";
		     //String notMatchingData="";
		     String notExisTraits="";
		     //String notMatchingDataExists="";
			 
			 int size=0;
			 String traits="";
			 for(int i=1;i<rowCount;i++){
				 if(!traitList.contains(sheetData.getCell(6, i).getContents().trim().toString()))
					 traitList.add(sheetData.getCell(6, i).getContents().trim().toString());
				 
			 }
			 for(int t=0;t<traitList.size();t++){
				 traits=traits+"'"+traitList.get(t)+"',";
			 }
			 traits=traits.substring(0, traits.length()-1);
			 ArrayList lstTraits=uptMId.getMarkerIds("traitid, trabbr", "tmstraits", "trabbr", session, traits);
			 SortedMap map = new TreeMap();
            List retTraits = new ArrayList();
            for(int w=0;w<lstTraits.size();w++){
                 Object[] strMareO= (Object[])lstTraits.get(w);
                 retTraits.add(strMareO[1]);
                 String strMa123 = (String)strMareO[1];
                 map.put(strMa123, strMareO[0]);	                 
            }
            if(map.size()==0){
            	alertT="yes";
	        	size=0;
	        }
            if(map.size()>0){
            	for(int t=0;t<traitList.size();t++){
		        	   String arrP[]=new String[3];
		        	   StringTokenizer stzP = new StringTokenizer(traitList.get(t).toString(), ",");
		        	   int iP=0;
		        	   while(stzP.hasMoreTokens()){
		        		   arrP[iP] = stzP.nextToken();
		        		   iP++;
		        	   }	
		        	  
		        	   //gNameToCompare=arrP[1].toString();
		        	   //System.out.println("...."+gidToCompare+"   "+lstgermpName.contains(gidToCompare));
		        	   if(retTraits.contains(traitList.get(t).toString())){
		        		   alertT="no"; 		        		  		        			   
		        	   }else{
		        		   alertT="yes";
		        		   size=map.size();
		        		   notExisTraits=notExisTraits+traitList.get(t).toString()+", ";
		        	   }
            	}
            }
            
            if(alertT.equalsIgnoreCase("yes")){
	            if(size==0){
	            	ErrMsg = "The Traits provided do not exist in the database. \n Please upload the relevant information to the TraitManagementSystem ";
	     	   	}else{
	     	   		ErrMsg = "The following Traits provided do not exist in the database. \n Please upload the relevant information to the TraitManagementSystem. ";
	     	   	}
	            request.getSession().setAttribute("indErrMsg", ErrMsg);
	        	return "ErrMsg";
            }         
            String dname=sheetSource.getCell(1,2).getContents().trim();
            if(dname.length()>30){
            	ErrMsg = "Error : Dataset Name value exceeds max char size.";
            	request.getSession().setAttribute("indErrMsg", ErrMsg);							
				return "ErrMsg";
            }
            
			/** inserting to 'dataset' table  **/
			ub.setDataset_id(dataset_id);
			ub.setDataset_name(dname);
			ub.setDataset_desc((String)sheetSource.getCell(1,3).getContents().trim());
			ub.setDataset_type(dataset_type);
			ub.setGenus(sheetSource.getCell(1,4).getContents().trim());
			ub.setSpecies(sheetSource.getCell(1,5).getContents().trim());
			//ub.setTemplate_date(curDate);
			ub.setUpload_template_date(curDate);
			ub.setDatatype(datatype);
			//System.out.println("dataset id = ");
			session.save(ub);
			String username=request.getSession().getAttribute("user").toString();
			int user_id=uptMId.getUserId("userid", "users", "uname", session,username);
			//System.out.println("user_id="+user_id);
						
			
			//*************  dataset_users*************
			usb.setDataset_id(dataset_id);
			usb.setUser_id(user_id);
			
			session.save(usb);
			
			
			int mapId=0;
			String linkMapId="";
			
			int intqtlId=uptMId.getMaxIdValue("qtl_id","gdms_qtl",session);
			//System.out.println("user_id="+user_id);
			//int qtlId=intqtlId+1;
			int qtlId=intqtlId-1;
			
			System.out.println("rowCount=:"+rowCount);
			for (int i=1;i<rowCount;i++){
				if(sheetData.getCell(1,i).getContents().trim()!=""){
					mapId=uptMId.getMapId("map_id", "gdms_map", "map_name", session,sheetData.getCell(2,i).getContents().trim());
					System.out.println("mapId="+mapId);
					if(mapId!=0){
						linkMapId=linkMapId+mapId+",";	
					}else{
						ErrMsg = "Map does not exists.\nPlease Upload the corresponding Map";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
				}
			}

			//System.out.println("mapID="+linkMapId);
			int l=0;
			String[] linkageMapID=linkMapId.split(",");
			for(int i=1;i<rowCount;i++){	
				/** reading from datasheet of template & writing to 'qtl' table  **/
				if(sheetData.getCell(1,i).getContents().trim()!=""){
				QTLBean qb=new QTLBean();				
				qb.setQtl_id(qtlId);
				qb.setDataset_id(dataset_id);
				qb.setQtl_name(sheetData.getCell(0, i).getContents().trim().toString());
				session.save(qb);	
				
				/** reading from datasheet of template & writing to 'qtl_linkagemap' **/
				QTLDetailsBean qtlb=new QTLDetailsBean();				
				qtlb.setMap_id(Integer.parseInt(linkageMapID[l]));
				qtlb.setQtl_id(qtlId);
				qtlb.setLinkage_group(sheetData.getCell(1, i).getContents().trim().toString());
				qtlb.setPosition(Float.parseFloat(sheetData.getCell(3, i).getContents().trim().toString()));
				qtlb.setMin_position(Float.parseFloat(sheetData.getCell(4, i).getContents().trim().toString()));
				qtlb.setMax_position(Float.parseFloat(sheetData.getCell(5, i).getContents().trim().toString()));
				qtlb.setTrait(sheetData.getCell(6, i).getContents().trim().toString());
				qtlb.setExperiment(sheetData.getCell(7, i).getContents().trim().toString());
				qtlb.setLeft_flanking_marker(sheetData.getCell(9, i).getContents().trim().toString());
				qtlb.setRight_flanking_marker(sheetData.getCell(10, i).getContents().trim().toString());
				qtlb.setEffect(Float.parseFloat(sheetData.getCell(11, i).getContents().trim().toString()));
				//qtlb.setLod(Float.parseFloat(sheetData.getCell(12, i).getContents().trim().toString()));
				qtlb.setScore_value(Float.parseFloat(sheetData.getCell(12, i).getContents().trim().toString()));
				qtlb.setR_square(Float.parseFloat(sheetData.getCell(13, i).getContents().trim().toString()));
				qtlb.setInteractions(sheetData.getCell(14, i).getContents().trim().toString());
					
			
				l++;
						
				session.save(qtlb);
				//qtlId++;
				qtlId--;
				if (i % 1 == 0){
					session.flush();
					session.clear();
				}
				}
			}
			
			tx.commit();
		}catch(Exception e){
			tx.rollback();
			session.clear();
			e.printStackTrace();
		}finally{		    
			session.clear();							
		}
		return result;	
	}

}
