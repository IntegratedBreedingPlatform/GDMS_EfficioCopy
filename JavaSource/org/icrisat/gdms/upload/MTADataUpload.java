package org.icrisat.gdms.upload;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Sheet;
import jxl.Workbook;

import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.manager.api.OntologyDataManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

public class MTADataUpload {
	private Session session;
	private Transaction tx;
	java.sql.Connection conn;
	java.sql.Connection con;
	public String setMTADetails(HttpServletRequest request, String mtafile) throws SQLException{
		String result = "inserted";
		
		String dataset_type="MTA";
		String datatype="char";
		ManagerFactory factory = null;
		Properties p=new Properties();
		HttpSession ses = request.getSession(true);
		ArrayList traitsComList=new ArrayList();
		try{	
			 p.load(new FileInputStream(ses.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
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
				Statement st=con.createStatement();
				ResultSet rs=null;
				ResultSet rsLoc=null;
				ResultSet rsCen=null;
				ResultSet rsMPL=null;
				ResultSet rsMPC=null;
				ResultSet rsMC=null;
				ResultSet rsML=null;
				ResultSet rsLE=null;
			session = HibernateSessionFactory.currentSession();
			tx=session.beginTransaction();
			String ErrMsg="";
			MaxIdValue uptMId=new MaxIdValue();
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(request);
			OntologyDataManager om=factory.getNewOntologyDataManager();
			DatasetBean ub=new DatasetBean();
			GenotypeUsersBean usb=new GenotypeUsersBean();	
			//UsersBean u=new UsersBean();
			String strSource="",strDatalist="";
			HttpSession httpsession = request.getSession();
			Workbook workbook=Workbook.getWorkbook(new File(mtafile));
			String[] strSheetNames=workbook.getSheetNames();
			
			ExcelSheetValidations fv = new ExcelSheetValidations();
			String strFv=fv.validation(workbook, request,"MTA");
			System.out.println("Valid="+strFv);
			if(!strFv.equals("valid"))
				return strFv;
			
			
			
			
			for (int i=0;i<strSheetNames.length;i++){				
				if(strSheetNames[i].equalsIgnoreCase("MTA_Source"))
					strSource = strSheetNames[i];
				if(strSheetNames[i].equalsIgnoreCase("MTA_Data"))
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
				 if(sheetData.getCell(4,i).getContents().trim()!=""){
					 if(!traitList.contains(sheetData.getCell(4, i).getContents().trim().toString()))
						 traitList.add(sheetData.getCell(4, i).getContents().trim().toString());
				 }
			 }
			 for(int t=0;t<traitList.size();t++){
				 traits=traits+"'"+traitList.get(t)+"',";
			 }
			 traits=traits.substring(0, traits.length()-1);
			 SortedMap map = new TreeMap();
	         List retTraits = new ArrayList();
	         for(int t=0;t<traitList.size();t++){
	        	 Set<StandardVariable> standardVariables = om.findStandardVariablesByNameOrSynonym(traitList.get(t).toString());
				assertTrue(standardVariables.size() == 1);
				for (StandardVariable stdVar : standardVariables) {
					System.out.println(stdVar.getId()+"   "+stdVar.getNameSynonyms()+"   "+stdVar.getName());
					traitsComList.add(stdVar.getId());
					retTraits.add(stdVar.getName());
					map.put(stdVar.getName(), stdVar.getId());
					/*tids=tids+stdVar.getId()+",";
					tidsCount++;*/
				}
	         }
			/*rsLoc=stLoc.executeQuery("select traitid, trabbr from tmstraits where trabbr in ("+traits+")");
			while(rsLoc.next()){
				traitsComList.add(rsLoc.getString(1));
				//+"!~!"+rsN.getString(2)+"!~!"+rsN.getString(3));
				retTraits.add(rsLoc.getString(2));
				map.put(rsLoc.getString(2), rsLoc.getString(1));
			}
	       //  System.out.println("select traitid, trabbr from tmstraits where trabbr in ("+traits+")");
			rsCen=stCen.executeQuery("select traitid, trabbr from tmstraits where trabbr in ("+traits+")");
			while(rsCen.next()){
				traitsComList.add(rsCen.getString(1));
				//+"!~!"+rsN.getString(2)+"!~!"+rsN.getString(3));
				retTraits.add(rsCen.getString(2));
				map.put(rsCen.getString(2), rsCen.getString(1));
			}*/
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
            List result1= new ArrayList();
			String dname=sheetSource.getCell(1,2).getContents().trim();
            //System.out.println("datasetName=:"+dname);
            //Query rsDatasetNames=session.createQuery("from DatasetBean where dataset_name ='"+dname+"'");
            rsLoc=stLoc.executeQuery("select dataset_name from gdms_dataset where dataset_name='"+dname+"'");
			while(rsLoc.next()){
				result1.add(rsLoc.getString(1));						
			}
			
			rsCen=stCen.executeQuery("select dataset_name from gdms_dataset where dataset_name='"+dname+"'");
			while(rsCen.next()){
				result1.add(rsCen.getString(1));	
			}		
			
			//System.out.println(".............:"+result1.size());
			if(result1.size()>0){
				ErrMsg = "Dataset Name already exists.";
				request.getSession().setAttribute("indErrMsg", ErrMsg);							
				return "ErrMsg";
			}            
            if(dname.length()>30){
            	ErrMsg = "Dataset Name value exceeds max char size.";
            	request.getSession().setAttribute("indErrMsg", ErrMsg);							
				return "ErrMsg";
            }            
            //System.out.println("score="+sheetSource.getCell(1,6).getContents().trim());			
			
			int mapId=0;
			//int marker_id=0;
			List marker_id=new ArrayList();
			String markerIds="";
			String linkMapId="";
			List mapIdL=new ArrayList();
			int intmtaId=uptMId.getMaxIdValue("mta_id","gdms_mta",session);
			//System.out.println("user_id="+user_id);
			//int qtlId=intqtlId+1;
			int mtaId=intmtaId-1;
			SortedMap linkageMaps = new TreeMap();
			
			//System.out.println("rowCount=:"+rowCount);
			for (int i=1;i<rowCount;i++){
				if(sheetData.getCell(1,i).getContents().trim()!=""){
					//System.out.println("Map=:"+sheetData.getCell(2,i).getContents().trim());
					//mapId=uptMId.getMapId("map_id", "gdms_map", "map_name", session,sheetData.getCell(2,i).getContents().trim());
					//System.out.println("select map_id, map_name from gdms_map where map_name='"+sheetData.getCell(2,i).getContents().trim()+"'");
					rsMPL=stLoc.executeQuery("select map_id, map_name from gdms_map where map_name='"+sheetData.getCell(2,i).getContents().trim()+"'");
					rsMPC=stCen.executeQuery("select map_id, map_name from gdms_map where map_name='"+sheetData.getCell(2,i).getContents().trim()+"'");
					while(rsMPC.next()){
						mapIdL.add(rsMPC.getInt(1));
						linkageMaps.put(rsMPC.getString(2),rsMPC.getInt(1));
					}
					while(rsMPL.next()){
						if(!mapIdL.contains(rsMPL.getInt(1)))
							mapIdL.add(rsMPL.getInt(1));
						linkageMaps.put(rsMPL.getString(2),rsMPL.getInt(1));
					}
					
					rsML=stLoc.executeQuery("select marker_id from gdms_marker where marker_name='"+sheetData.getCell(0,i).getContents().trim()+"'");
					rsMC=stCen.executeQuery("select marker_id from gdms_marker where marker_name='"+sheetData.getCell(0,i).getContents().trim()+"'");
					//marker_id=uptMId.getMapId("marker_id", "gdms_marker", "marker_name", session, sheetData.getCell(0,i).getContents().trim());
					//System.out.println("mapId="+mapId);
					while(rsMC.next()){
						//mapIdL.add(rsMC.getInt(1));
						marker_id.add(rsMC.getInt(1));
					}
					while(rsML.next()){
						if(marker_id.contains(rsML.getInt(1)))
							marker_id.add(rsML.getInt(1));
					}					
				}				
			}
			//System.out.println("mapIdL ="+mapIdL);
			//System.out.println("linkageMaps:"+linkageMaps);
			for (int i=1;i<rowCount;i++){
				if(sheetData.getCell(1,i).getContents().trim()!=""){
					//System.out.println(mapIdL.contains(linkageMaps.get(sheetData.getCell(2,i).getContents().trim()))+"   "+linkageMaps.get(sheetData.getCell(2,i).getContents().trim())+"  "+sheetData.getCell(2,i).getContents().trim());
					if(!(mapIdL.contains(linkageMaps.get(sheetData.getCell(2,i).getContents().trim())))){
						ErrMsg = "Map does not exists.\nPlease Upload the corresponding Map\n"+sheetData.getCell(2,i).getContents().trim();
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
						
					}
				}
			}
			
			if(marker_id.size()!=0){
				for(int m=0; m<marker_id.size(); m++){
					markerIds=markerIds+marker_id.get(m)+",";
				}
			}else{
				ErrMsg = "Marker does not exists.\nPlease Upload the marker info";
				request.getSession().setAttribute("indErrMsg", ErrMsg);
				return "ErrMsg";
			}
			/** inserting to 'dataset' table  **/
			ub.setDataset_id(dataset_id);
			ub.setDataset_name(dname);
			ub.setDataset_desc((String)sheetSource.getCell(1,3).getContents().trim());
			ub.setDataset_type(dataset_type);
			ub.setGenus(sheetSource.getCell(1,4).getContents().trim());
			ub.setSpecies(sheetSource.getCell(1,7).getContents().trim());
			//ub.setTemplate_date(curDate);
			ub.setUpload_template_date(curDate);
			ub.setDatatype(datatype);
			ub.setMethod(sheetSource.getCell(1,5).getContents().trim());
			ub.setScore(sheetSource.getCell(1,6).getContents().trim());
			ub.setInstitute(sheetSource.getCell(1,0).getContents().trim());
			ub.setPrincipal_investigator(sheetSource.getCell(1,1).getContents().trim());

			session.save(ub);
			String username=request.getSession().getAttribute("user").toString();
			int user_id=uptMId.getUserId("userid", "users", "uname", session,username);
						
			//*************  dataset_users*************
			usb.setDataset_id(dataset_id);
			usb.setUser_id(user_id);
			boolean markerExist=false;
			session.save(usb);
			int l=0;
			int cl=0;
			String[] linkageMapID=linkMapId.split(",");
			String[] strMarkerIds=markerIds.split(",");
			for(int c=1;c<rowCount;c++){
				if(sheetData.getCell(2, c).getContents().trim()!=""){
				//System.out.println("select * from gdms_markers_onmap where map_id="+linkageMaps.get(sheetData.getCell(2, c).getContents().trim().toString())+" and linkage_group='"+sheetData.getCell(1, c).getContents().trim().toString()+"' and marker_id="+strMarkerIds[cl]+" and start_position like "+sheetData.getCell(3, c).getContents().trim().toString()+"");
					rs=stCen.executeQuery("select * from gdms_markers_onmap where map_id="+linkageMaps.get(sheetData.getCell(2, c).getContents().trim().toString())+" and linkage_group='"+sheetData.getCell(1, c).getContents().trim().toString()+"' and marker_id="+strMarkerIds[cl]+" and start_position like "+sheetData.getCell(3, c).getContents().trim().toString());
					while(rs.next()){
						markerExist=true;
					}
					rsLE=stLoc.executeQuery("select * from gdms_markers_onmap where map_id="+linkageMaps.get(sheetData.getCell(2, c).getContents().trim().toString())+" and linkage_group='"+sheetData.getCell(1, c).getContents().trim().toString()+"' and marker_id="+strMarkerIds[cl]+" and start_position like "+sheetData.getCell(3, c).getContents().trim().toString());
					while(rsLE.next()){
						markerExist=true;
					}
					if(!markerExist){
						ErrMsg = "Marker does not exists on the map.";
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					cl++;
				}
			}
			for(int i=1;i<rowCount;i++){				
				/** reading from datasheet of template & writing to 'qtl' table  **/
				if(sheetData.getCell(1,i).getContents().trim()!=""){				
					/** reading from datasheet of template & writing to 'qtl_linkagemap' **/
					MTABean mtab=new MTABean();									
					mtab.setMta_id(mtaId);
					mtab.setMarker_id(Integer.parseInt(strMarkerIds[l]));
					mtab.setDataset_id(dataset_id);
					mtab.setLinkage_group(sheetData.getCell(1, i).getContents().trim().toString());
					mtab.setMap_id(Integer.parseInt(linkageMaps.get(sheetData.getCell(2, i).getContents().trim().toString()).toString()));
					mtab.setPosition(Float.parseFloat(sheetData.getCell(3, i).getContents().trim().toString()));
					mtab.setTid(Integer.parseInt(map.get(sheetData.getCell(4, i).getContents().trim().toString()).toString()));
					mtab.setEffect(Integer.parseInt(sheetData.getCell(5, i).getContents().trim().toString()));
					mtab.setHv_allele(sheetData.getCell(6, i).getContents().trim().toString());
					mtab.setExperiment(sheetData.getCell(7, i).getContents().trim().toString());
					mtab.setScore_value(Float.parseFloat(sheetData.getCell(8, i).getContents().trim().toString()));
					mtab.setR_square(Float.parseFloat(sheetData.getCell(9, i).getContents().trim().toString()));
					l++;							
					session.save(mtab);
					mtaId--;
					if (i % 1 == 0){
						session.flush();
						session.clear();
					}
				}
			}
			
			tx.commit();
			/*if(rs!=null) rs.close(); if(rsLoc!=null) rsLoc.close(); if(rsCen!=null) rsCen.close(); if(rsMPL != null) rsMPL.close(); 
			if(rsMPC!=null) rsMPC.close(); if(rsMC!=null) rsMC.close(); if(rsML!=null) rsML.close(); if(rsLE!=null) rsLE.close();
			if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close();*/
			if(con!=null) con.close(); if(conn!=null) conn.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{		    
			session.clear();	
			session.disconnect();
			con.close();
			conn.close();
		}
		return result;
	}
}
