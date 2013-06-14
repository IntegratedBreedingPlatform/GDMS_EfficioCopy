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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.mysql.jdbc.Connection;

public class QTLDataUpload {
	
	private Session session;
	private Transaction tx;
	java.sql.Connection conn;
	java.sql.Connection con;
	HttpServletRequest request;
	/*String crop=request.getSession().getAttribute("crop").toString();
	public QTLDataUpload(){
		session = HibernateSessionFactory.currentSession(crop);
		tx=session.beginTransaction();	
	}*/
	static Map<Integer, ArrayList<String>> hashMap = new HashMap<Integer,  ArrayList<String>>(); 	
	public String setQTLData(HttpServletRequest request, String qtlfile) throws SQLException{
		String result = "inserted";
		String dataset_type="QTL";
		String datatype="int";
		
		ManagerFactory factory =null;
		Properties p=new Properties();
		HttpSession ses = request.getSession(true);
		ArrayList traitsComList=new ArrayList();
		//SortedMap mapT = new TreeMap();
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
				
			 ResultSet rsCen=null;
			 ResultSet rsLoc=null;
			 ResultSet rsQL=null;
			 ResultSet rsQC=null;
			 ResultSet rsDL=null;
			 ResultSet rsDC=null;
			 ResultSet rsML=null;
			 ResultSet rsMC=null;
			 	
			 //String crop=request.getSession().getAttribute("crop").toString();
			 session = HibernateSessionFactory.currentSession();
			 tx=session.beginTransaction();
			
			 /*DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");			
			factory = new ManagerFactory(local, central);
			TraitDataManager manager=factory.getTraitDataManager();			
			*/
			 factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(request);
				OntologyDataManager om=factory.getNewOntologyDataManager();
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
			 ArrayList QTLsList=new ArrayList();
			 String alertT="no";
		     String alertGN="no";
		     //String notMatchingData="";
		     String notExisTraits="";
		     //String notMatchingDataExists="";
			 List result2=new ArrayList();
			 int size=0;
			 String traits="";
			 String qtls="";
			 for(int i=1;i<rowCount;i++){
				 if(sheetData.getCell(6,i).getContents().trim()!=""){
					 if(!traitList.contains(sheetData.getCell(6, i).getContents().trim().toString()))
						 traitList.add(sheetData.getCell(6, i).getContents().trim().toString());
				 }
				 if(sheetData.getCell(0, i).getContents().trim().toString()!=""){
					 if(!QTLsList.contains(sheetData.getCell(0, i).getContents().trim().toString()))
						 QTLsList.add(sheetData.getCell(0, i).getContents().trim().toString());
				 }
			 }
			 for(int t=0;t<traitList.size();t++){
				 traits=traits+"'"+traitList.get(t)+"',";
			 }
			 
			 for(int q=0;q<QTLsList.size();q++){
				 qtls=qtls+"'"+QTLsList.get(q)+"',";
			 }
			 
			 //System.out.println("QTLsList=:"+QTLsList);
			 traits=traits.substring(0, traits.length()-1);
			 qtls=qtls.substring(0,qtls.length()-1);
			 SortedMap map = new TreeMap();
	         List retTraits = new ArrayList();
			
	         for(int t=0;t<traitList.size();t++){
	        	 Set<StandardVariable> standardVariables = om.findStandardVariablesByNameOrSynonym(traitList.get(t).toString());
				//assertTrue(standardVariables.size() == 1);
				for (StandardVariable stdVar : standardVariables) {
					//System.out.println(stdVar.getId()+"   "+stdVar.getNameSynonyms()+"   "+stdVar.getName());
					traitsComList.add(stdVar.getId());
					retTraits.add(stdVar.getName());
					map.put(stdVar.getName(), stdVar.getId());
					/*tids=tids+stdVar.getId()+",";
					tidsCount++;*/
				}
	         }
	         
	        /* 
			//System.out.println("select traitid, trabbr from tmstraits where trabbr in ("+traits+")");
			rsCen=stCen.executeQuery("select tid, trabbr from tmstraits where trabbr in ("+traits+")");
			while(rsCen.next()){
				traitsComList.add(rsCen.getString(1));
				//+"!~!"+rsN.getString(2)+"!~!"+rsN.getString(3));
				retTraits.add(rsCen.getString(2));
				map.put(rsCen.getString(2), rsCen.getString(1));
			}
			rsLoc=stLoc.executeQuery("select tid, trabbr from tmstraits where trabbr in ("+traits+")");
			while(rsLoc.next()){
				traitsComList.add(rsLoc.getString(1));
				//+"!~!"+rsN.getString(2)+"!~!"+rsN.getString(3));
				retTraits.add(rsLoc.getString(2));
				map.put(rsLoc.getString(2), rsLoc.getString(1));
			}*/
	         if(map.size()==0){
	            	alertT="yes";
		        	size=0;
		        }
	            if(map.size()>0){
	            	for(int t=0;t<traitList.size();t++){
		        	   
	            		 if(!retTraits.contains(traitList.get(t).toString())){
			        		   //System.out.println("does not contain:"+traitList.get(t));
			        		   alertT="yes";
			        		   size=map.size();
			        		   notExisTraits=notExisTraits+traitList.get(t).toString()+", ";
			        	   }
	            	}
	            }
	            
	            if(alertT.equalsIgnoreCase("yes")){
		            if(size==0){
		            	ErrMsg = "The Trait(s) provided do not exist in the database. \n Please upload the relevant information";
		     	   	}else{
		     	   		ErrMsg = "The following Trait(s) provided do not exist in the database. \n Please upload the relevant information. \n"+notExisTraits.substring(0,notExisTraits.length()-1);
		     	   	}
		            request.getSession().setAttribute("indErrMsg", ErrMsg);
		        	return "ErrMsg";
	            }    
			
			rsQL=stLoc.executeQuery("select * from gdms_qtl where qtl_name in ("+qtls+")");
			rsQC=stCen.executeQuery("select * from gdms_qtl where qtl_name in ("+qtls+")");
			while(rsQC.next()){
				result2.add(rsQC.getString(2));
			}
			while(rsQL.next()){
				result2.add(rsQL.getString(2));
			}
			if(result2.size()>0){
				ErrMsg = "Following QTL(s) already exists. Please check. \n"+result2;
				request.getSession().setAttribute("indErrMsg", ErrMsg);							
				return "ErrMsg";
			}
                
            String dname=sheetSource.getCell(1,2).getContents().trim();
            
            		
            List result1= new ArrayList();
            rsDL=stLoc.executeQuery("select dataset_name from gdms_dataset where dataset_name='"+dname+"'");
			while(rsDL.next()){
				result1.add(rsDL.getString(1));						
			}
			//System.out.println("select traitid, trabbr from tmstraits where trabbr in ("+traits+")");
			rsDC=stCen.executeQuery("select dataset_name from gdms_dataset where dataset_name='"+dname+"'");
			while(rsDC.next()){
				result1.add(rsDC.getString(1));	
			}           
			
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
            for(int i=1;i<rowCount;i++){
				String QName = (String)sheetData.getCell(0, i).getContents().trim();
				String chromosome = (String)sheetData.getCell(1, i).getContents().trim();	
				String mapName=(String)sheetData.getCell(2, i).getContents().trim();	
				String position=(String)sheetData.getCell(3, i).getContents().trim();
				String minPos=(String)sheetData.getCell(4, i).getContents().trim();
				String maxPos=(String)sheetData.getCell(5, i).getContents().trim();
				String trait = (String)sheetData.getCell(6, i).getContents().trim();
				String LFM = (String)sheetData.getCell(9, i).getContents().trim();	
				String RFM=(String)sheetData.getCell(10, i).getContents().trim();	
				String effect=(String)sheetData.getCell(11, i).getContents().trim();
				String lod=(String)sheetData.getCell(17, i).getContents().trim();
				String r2=(String)sheetData.getCell(18, i).getContents().trim();
				//boolean bCrop = false;
				if(QName.equals("")||QName==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(0, i).getColumn());							
					ErrMsg = " Provide QTL Name at cell position "+strColName+(sheetData.getCell(0, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(chromosome.equals("")||chromosome==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(1, i).getColumn());							
					ErrMsg = "Provide the chromosome for qtl "+QName+" at cell position "+strColName+(sheetData.getCell(1, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";							
				}else if(mapName.equals("")||mapName==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(2, i).getColumn());							
					ErrMsg = "Provide the Map Name for qtl "+QName+" at cell position "+strColName+(sheetData.getCell(2, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(position.equals("")||position==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(3, i).getColumn());							
					ErrMsg = "Provide the Position for "+QName+" at cell position "+strColName+(sheetData.getCell(3, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(minPos.equals("")||minPos==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(4, i).getColumn());							
					ErrMsg = "Provide the Pos_Min for "+QName+" at cell position "+strColName+(sheetData.getCell(4, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(maxPos.equals("")||maxPos==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(5, i).getColumn());							
					ErrMsg = "Provide the Pos_Max for "+QName+" at cell position "+strColName+(sheetData.getCell(5, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";							
				}else if(trait.equals("")||trait==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(6, i).getColumn());							
					ErrMsg = "Provide the trait for "+QName+" at cell position "+strColName+(sheetData.getCell(6, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(LFM.equals("")||LFM==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(9, i).getColumn());							
					ErrMsg = "Provide the LFM "+QName+" at cell position "+strColName+(sheetData.getCell(9, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(RFM.equals("")||RFM==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(10, i).getColumn());							
					ErrMsg = "Provide the RFMarker for QTL "+QName+" at cell position "+strColName+(sheetData.getCell(10, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(effect.equals("")||effect==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(11, i).getColumn());							
					ErrMsg = "Provide the Effect for "+QName+" at cell position "+strColName+(sheetData.getCell(11, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(lod.equals("")||lod==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(17, i).getColumn());							
					ErrMsg = "Provide the Lod value for "+QName+" at cell position "+strColName+(sheetData.getCell(17, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}else if(r2.equals("")||r2==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetData.getCell(18, i).getColumn());							
					ErrMsg = "Provide the R2 value for "+QName+" at cell position "+strColName+(sheetData.getCell(18, i).getRow()+1);
					request.getSession().setAttribute("indErrMsg", ErrMsg);							
					return "ErrMsg";
				}
				
				
			}
			/** inserting to 'dataset' table  **/
			ub.setDataset_id(dataset_id);
			ub.setDataset_name(dname);
			ub.setDataset_desc((String)sheetSource.getCell(1,3).getContents().trim());
			ub.setDataset_type(dataset_type);
			ub.setGenus(sheetSource.getCell(1,4).getContents().trim());
			ub.setSpecies(sheetSource.getCell(1,7).getContents().trim());
			ub.setUpload_template_date(curDate);
			ub.setDatatype(datatype);
			ub.setMethod(sheetSource.getCell(1,5).getContents().trim());
			ub.setScore(sheetSource.getCell(1,6).getContents().trim());
			ub.setInstitute(sheetSource.getCell(1,0).getContents().trim());
			ub.setPrincipal_investigator(sheetSource.getCell(1,1).getContents().trim());
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
			List mapIdL=new ArrayList();
			List mapIdLG=new ArrayList();
			//System.out.println("rowCount=:"+rowCount);
			hashMap.clear();
			for (int i=1;i<rowCount;i++){
				if(sheetData.getCell(1,i).getContents().trim()!=""){
					//System.out.println("select map_id from gdms_map where map_name='"+sheetData.getCell(2,i).getContents().trim()+"'");
					rsML=stLoc.executeQuery("select gdms_map.map_id, gdms_markers_onmap.linkage_group from gdms_map join gdms_markers_onmap on gdms_map.map_id=gdms_markers_onmap.map_id where gdms_map.map_name='"+sheetData.getCell(2,i).getContents().trim()+"'");
					rsMC=stCen.executeQuery("select gdms_map.map_id, gdms_markers_onmap.linkage_group from gdms_map join gdms_markers_onmap on gdms_map.map_id=gdms_markers_onmap.map_id where gdms_map.map_name='"+sheetData.getCell(2,i).getContents().trim()+"'");
					while(rsMC.next()){
						if(!(mapIdL.contains(rsMC.getInt(1)))){
							mapIdL.add(rsMC.getInt(1));							
						}
						if(!(mapIdLG.contains(rsMC.getString(2)))){
							mapIdLG.add(rsMC.getString(2));	
							addValues(rsMC.getInt(1), rsMC.getString(2));
						}
					}
					while(rsML.next()){
						if(!(mapIdL.contains(rsML.getInt(1)))){
							mapIdL.add(rsML.getInt(1));							
						}
						if(!(mapIdLG.contains(rsML.getString(2)))){
							mapIdLG.add(rsML.getString(2));	
							addValues(rsML.getInt(1), rsML.getString(2));
						}
					}
					
					if(mapIdL.size()!=0){
						linkMapId=linkMapId+mapIdL.get(0)+",";
						if(!mapIdLG.contains(sheetData.getCell(1,i).getContents().trim().toString())){
							ExcelSheetColumnName escn =  new ExcelSheetColumnName();
							String strColName = escn.getColumnName(sheetData.getCell(1, i).getColumn());
							ErrMsg = "Please Check. \n Linkage Group `"+sheetData.getCell(1,i).getContents().trim()+"` at cell position "+strColName+(sheetData.getCell(1, i).getRow()+1+" in template uploaded does not match with the Linkage Group in Map "+hashMap.get(mapIdL.get(0)));
							request.getSession().setAttribute("indErrMsg", ErrMsg);
							return "ErrMsg";
						}
					}else{
						ExcelSheetColumnName escn =  new ExcelSheetColumnName();
						String strColName = escn.getColumnName(sheetData.getCell(2, i).getColumn());
						ErrMsg = "Map does not exists.\nPlease Upload the corresponding Map. \n "+sheetData.getCell(2,i).getContents().trim()+" at cell position "+strColName+(sheetData.getCell(2, i).getRow()+1);
						request.getSession().setAttribute("indErrMsg", ErrMsg);
						return "ErrMsg";
					}
					/*if(mapId!=0){
						linkMapId=linkMapId+mapId+",";	
					}*/
				}
			}
			/*for(int i=1;i<rowCount;i++){
				sheetData.getCell(0, i).getContents().trim().toString()
			}*/
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
				qtlb.setTid(Integer.parseInt(map.get(sheetData.getCell(6, i).getContents().trim().toString()).toString()));
				qtlb.setExperiment(sheetData.getCell(7, i).getContents().trim().toString());
				qtlb.setLeft_flanking_marker(sheetData.getCell(9, i).getContents().trim().toString());
				qtlb.setRight_flanking_marker(sheetData.getCell(10, i).getContents().trim().toString());
				qtlb.setEffect(Float.parseFloat(sheetData.getCell(11, i).getContents().trim().toString()));
				//qtlb.setLod(Float.parseFloat(sheetData.getCell(12, i).getContents().trim().toString()));
				
				qtlb.setSe_additive(sheetData.getCell(12, i).getContents().trim().toString());
				qtlb.setHv_parent(sheetData.getCell(13, i).getContents().trim().toString());
				qtlb.setHv_allele(sheetData.getCell(14, i).getContents().trim().toString());
				qtlb.setLv_parent(sheetData.getCell(15, i).getContents().trim().toString());
				qtlb.setLv_allele(sheetData.getCell(16, i).getContents().trim().toString());			
				
				
				qtlb.setScore_value(Float.parseFloat(sheetData.getCell(17, i).getContents().trim().toString()));
				qtlb.setR_square(Float.parseFloat(sheetData.getCell(18, i).getContents().trim().toString()));
				qtlb.setInteractions(sheetData.getCell(19, i).getContents().trim().toString());
					
			
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
			
			/*if(rsCen!=null) rsCen.close(); if(rsLoc!=null) rsLoc.close(); if(rsQL!=null) rsQL.close(); if(rsQC!=null) rsQC.close(); if(rsDL!=null) rsDL.close(); 
			if(rsDC!=null) rsDC.close(); if(rsML!=null) rsML.close(); if(rsMC!=null) rsMC.close();
			if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close();*/
			if(con!=null) con.close(); if(conn !=null) conn.close();
			
		}catch(Exception e){
			tx.rollback();
			session.clear();
			e.printStackTrace();
		}finally{		    
			session.clear();	
			session.disconnect();
			con.close();
			conn.close();
		}
		return result;	
	}

	private static void addValues(Integer key, String value){
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
