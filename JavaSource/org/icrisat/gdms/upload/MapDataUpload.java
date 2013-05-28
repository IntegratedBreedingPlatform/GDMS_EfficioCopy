package org.icrisat.gdms.upload;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

import com.mysql.jdbc.Connection;

public class MapDataUpload {
	private Session session;
	static int map_count=0;
	private Transaction tx;
	java.sql.Connection conn;
	java.sql.Connection con;	
		
	public String setMapDetails(HttpServletRequest request, String mapfile) throws SQLException{
		String result = "inserted";
		Properties prop=new Properties();
		HttpSession hsession = request.getSession(true);
		try{
				Pattern p = Pattern.compile( "([0-9]*)\\.[0]" );
				
				 prop.load(new FileInputStream(hsession.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
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
				 ResultSet rsCen=null;
				 ResultSet rsMPC=null;
				 ResultSet rsMPL=null;
				 ResultSet rsML=null;
				 ResultSet rsMC=null;
				//upload the excel file
				//String crop=request.getSession().getAttribute("crop").toString();
				session = HibernateSessionFactory.currentSession();
				tx=session.beginTransaction();
				String CropName="";
				String MapUnit="";			
				String selMap="";
				int mpid=0;
				ArrayList result1=new ArrayList();
			    //markerType =UA stands for Un Assigned
				String markerType="UA";
				String mapType="";
				String strMarkerName="";
				String LinkageGroup="" ;
				double Position=0; 
				//String Position="";
				String Query="";
				int count=0;
				int count1=0;
				int markerid_max=0;
				int intRMarkerId=1;
				String option=request.getParameter("opMap");
				boolean	exists=false;

				DecimalFormat df = new DecimalFormat("#.0");
				
				InputStream in = new FileInputStream(mapfile);
				Workbook workbook = WorkbookFactory.create(in);
				int noOfSheets=workbook.getNumberOfSheets();
				
				
				String[] strArrSheetNames=null;
				String strSheetName = "";
				for (int i=0;i<noOfSheets;i++){
					strSheetName = workbook.getSheetName(i);				
				}
				//Sheet sheetSource = workbook.getSheet(strSource);
				Sheet sheetMarkerDetails = workbook.getSheet(strSheetName);
				//System.out.println(".......  sheet name=:"+strSheetName);
				//Marker Template validations
				String sheetNameCheck = "";			
				if(!(strSheetName.equals("Map"))){
					hsession.setAttribute("sheetName", "Map");
					hsession.setAttribute("sheetNameErr", strSheetName);
					return result = "SheetNameNotFound";						
				}
				
				int intSRC = sheetMarkerDetails.getPhysicalNumberOfRows();
				//System.out.println("........  "+intSRC);
				String strColName ="";
				String strD="";
				String str="";
				/*int ColCount = sheetMarkerDetails.getColumns();
				int intSRC=sheetMarkerDetails.getRows();*/
				Row row = sheetMarkerDetails.getRow(0);
	            int noOfColumns = row.getLastCellNum();	
				
	            
				
	            Cell cell;
				String map_name="";String map_description="";
				String crop="";//String map_unit="";
	            CellReference cr = new CellReference("A1");
	            row = sheetMarkerDetails.getRow(cr.getRow());
	            cell = row.getCell(cr.getCol());
	            if(cell.getStringCellValue().trim().equalsIgnoreCase("Map Name")){
	            	cr = new CellReference("B1");
		            row = sheetMarkerDetails.getRow(cr.getRow());
		            cell = row.getCell(cr.getCol());
		            map_name=cell.getStringCellValue().trim();
		           // System.out.println("..................:"+map_name.length());
		            int mapNameLength=map_name.length();
		            
		            rsLoc=stLoc.executeQuery("select map_name from gdms_map where map_name='"+map_name+"'");
					while(rsLoc.next()){
						result1.add(rsLoc.getString(1));						
					}
					//System.out.println("select traitid, trabbr from tmstraits where trabbr in ("+traits+")");
					rsCen=stCen.executeQuery("select map_name from gdms_map where map_name='"+map_name+"'");
					while(rsCen.next()){
						result1.add(rsCen.getString(1));	
					}
		            
		            //System.out.println(".........................:"+result1);
		            //Query rsDatasetNames=session.createQuery("from MapBean where map_name ='"+map_name+"'");	
					//List result1= rsDatasetNames.list();
					//System.out.println(".............:"+result1.size());
					if(result1.size()>0){
						String ErrMsg = "Map with a given name already exists.";
						request.getSession().setAttribute("indErrMsg", ErrMsg);							
						return "ErrMsg";
					}
		            if(map_name.equals("")){
		            	String ErrMsg = "Error : Map Name value should not be empty.\n  The column/row position is B1.";
		            	hsession.setAttribute("indErrMsg", ErrMsg);							
						return result = "ErrMsg";
		            }
		            if(mapNameLength>30){
		            	String ErrMsg = "Error : Map Name value exceeds max char size.";
		            	hsession.setAttribute("indErrMsg", ErrMsg);							
						return result = "ErrMsg";
		            }
	            }else{
	            	String ErrMsg = "Error : Column Name should be Map Name at cell A1";
					hsession.setAttribute("indErrMsg", ErrMsg);							
					return result = "ErrMsg";	            	
	            }
	             
	            CellReference crMD = new CellReference("A2");
	            row = sheetMarkerDetails.getRow(crMD.getRow());
	            cell = row.getCell(crMD.getCol());
	            if(cell.getStringCellValue().trim().equalsIgnoreCase("Map Description")){
	            	crMD = new CellReference("B2");
		            row = sheetMarkerDetails.getRow(crMD.getRow());
		            cell = row.getCell(crMD.getCol());
		            map_description=cell.getStringCellValue().trim();
		            
		            if(map_description.equals("")){
		            	String ErrMsg = "Error : Map Description value should not be empty.\n  The column/row position is B2.";
		            	hsession.setAttribute("indErrMsg", ErrMsg);							
						return result = "ErrMsg";
		            }
	            }else{
	            	String ErrMsg = "Error : Column Name should be Map Description at cell A2";
					hsession.setAttribute("indErrMsg", ErrMsg);							
					return result = "ErrMsg";
	            	
	            }
	            
	            CellReference crSP = new CellReference("A3");
	            row = sheetMarkerDetails.getRow(crSP.getRow());
	            cell = row.getCell(crSP.getCol());
	            if(cell.getStringCellValue().trim().equalsIgnoreCase("Crop")){
	            	crSP = new CellReference("B3");
		            row = sheetMarkerDetails.getRow(crSP.getRow());
		            cell = row.getCell(crSP.getCol());
		            crop=cell.getStringCellValue().trim();
		            if(crop.equals("")){
		            	String ErrMsg = "Error : Crop value should not be empty.\n  The column/row position is B3.";
		            	hsession.setAttribute("indErrMsg", ErrMsg);							
						return result = "ErrMsg";
		            }
	            }else{
	            	String ErrMsg = "Error : Column Name should be Crop at cell A3";
					hsession.setAttribute("indErrMsg", ErrMsg);							
					return result = "ErrMsg";
	            	
	            }
	            
	            CellReference crMU = new CellReference("A4");
	            row = sheetMarkerDetails.getRow(crMU.getRow());
	            cell = row.getCell(crMU.getCol());
	            if(cell.getStringCellValue().trim().equalsIgnoreCase("Map Unit")){
	            	crMU = new CellReference("B4");
		            row = sheetMarkerDetails.getRow(crMU.getRow());
		            cell = row.getCell(crMU.getCol());
		            MapUnit=cell.getStringCellValue().trim();
		            if(MapUnit.equals("")){
		            	String ErrMsg = "Error : Map Unit value should not be empty.\n            The column/row position is B4.";
		            	hsession.setAttribute("indErrMsg", ErrMsg);							
						return result = "ErrMsg";
		            }
	            }else{
	            	String ErrMsg = "Error : Column Name should be Map Unit at cell A4";
					hsession.setAttribute("indErrMsg", ErrMsg);							
					return result = "ErrMsg";
	            	
	            }
	            //System.out.println("MapUnit=:"+MapUnit);
	            if (MapUnit.equalsIgnoreCase("cm")){
					mapType="genetic";
				}else
					mapType="sequence\\physical";
				/** Retrieving maximum marker id from database table 'marker'  **/
				MaxIdValue uptMDsetId=new MaxIdValue();
				int maxMarkerId=uptMDsetId.getMaxIdValue("marker_id","gdms_marker",session);
				ArrayList mpidL=new ArrayList();
				if(option.equalsIgnoreCase("yes")){
					selMap=request.getParameter("List1");
					//mpid=uptMDsetId.getUserId("dataset_id", "gdms_mapping_pop", "mapdata_desc", session,selMap);
					rsMPC=stCen.executeQuery("select dataset_id from gdms_dataset where dataset_name='"+selMap+"'");
					while(rsMPC.next()){
						//mpid=rsMPC.getInt(1);
						mpidL.add(rsMPC.getInt(1));
					}
					rsMPL=stLoc.executeQuery("select dataset_id from gdms_dataset where dataset_name='"+selMap+"'");
					while(rsMPL.next()){
						if(mpidL.size()==0){
							mpidL.add(rsMPL.getInt(1));
						}
					}
					mpid=Integer.parseInt(mpidL.get(0).toString());
				}
				/** Retrieving maximum linkagemap_id from database table 'linkagemap' **/
				int maxLinkageMapId=uptMDsetId.getMaxIdValue("map_id","gdms_map",session);
				//int linkageMapID=maxLinkageMapId+1;
				int linkageMapID=maxLinkageMapId-1;
				
				/** writing to database table 'linkagemap' **/
				
				MapBean map = new MapBean();
				map.setMap_id(linkageMapID);
				map.setMap_name(map_name);
				map.setMap_type(mapType);
				//linkagemap.setMcid(mcid);
				map.setMap_desc(map_description);
				map.setMap_unit(MapUnit);
				map.setMp_id(mpid);
			
				session.save(map);
				
	            
	            ArrayList mNames=new ArrayList();
	            String markers="";
	           // System.out.println("*********************************:"+map_name);
	            for (int rows = 6; rows <= intSRC; rows++) {
	            	int colnum = 0;
                    row = sheetMarkerDetails.getRow(rows);
                   	cell = row.getCell(colnum);
                   	//System.out.println("**************:"+cell.getStringCellValue().trim());
                   	if(!(mNames.contains(cell.getStringCellValue().trim()))){
                   		mNames.add(cell.getStringCellValue());   
                   		markers=markers+"'"+cell.getStringCellValue().trim()+"',";
                   		
                   	}
	            }
	            //System.out.println("******markers ********:"+markers);
	            HashMap<String, Object> markersMap = new HashMap<String, Object>();
	            ArrayList lstMarIdNames=new ArrayList();
	            List lstMarkers = new ArrayList();
	            
	            //System.out.println("select distinct marker_id, marker_name from gdms_marker where marker_name in ("+markers.substring(0, markers.length()-1)+")");
	            rsML=stLoc.executeQuery("select distinct marker_id, marker_name from gdms_marker where marker_name in ("+markers.substring(0, markers.length()-1)+")");
	            rsMC=stCen.executeQuery("select distinct marker_id, marker_name from gdms_marker where marker_name in ("+markers.substring(0, markers.length()-1)+")");
	            
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
	            //ArrayList lstMarIdNames=uptMDsetId.getMarkerIds("marker_id, marker_name", "gdms_marker", "marker_name", session, markers.substring(0, markers.length()-1));
	                  
	            /*for(int w=0;w<lstMarIdNames.size();w++){
	                 Object[] strMareO= (Object[])lstMarIdNames.get(w);
	                 lstMarkers.add(strMareO[1]);
	                 String strMa123 = (String)strMareO[1];
	                 markersMap.put(strMa123, strMareO[0]);	                 
	            }*/
	            
	            //System.out.println(markersMap.size()+"   "+markersMap);
	            /*System.out.println(lstMarIdNames.size()+"     >>>>>>>>>>>>>>>L:"+lstMarIdNames);
	            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<:"+intSRC);*/
				for (int rows = 6; rows <= intSRC; rows++) {
                    row = sheetMarkerDetails.getRow(rows);
                    int rcount=0;
                    noOfColumns = row.getLastCellNum();
                    strD="";
                    for (int colnum = 0; colnum < noOfColumns; colnum++) {
                    	cell = row.getCell(colnum);
                    	
                    	
                    	ExcelSheetColumnName escn =  new ExcelSheetColumnName();
                    	if(colnum==0){
                    		int cellType = cell.getCellType(); //Getting a null pointer exception when the cell is empty
	                        if (cellType == Cell.CELL_TYPE_NUMERIC) {
	                        	strMarkerName = cell.getNumericCellValue() + "";
	                        } else {
	                        	strMarkerName = cell.getStringCellValue().trim();
	                        }
                    		//strMarkerName = (String)cell.getStringCellValue();                    		
                        	if(strMarkerName.equals("")||strMarkerName==null){   
                        		strColName = escn.getColumnName(colnum);	
                        		rcount=rows+1;
        						String ErrMsg = " Provide Marker name at cell position "+strColName+rcount;
        						hsession.setAttribute("indErrMsg", ErrMsg);							
        						return result = "ErrMsg";
        					}else{
        						strD=strD+strMarkerName+"!`!";
        					}
                        	
                    	}else if(colnum==1){
                    		int cellType = cell.getCellType(); //Getting a null pointer exception when the cell is empty
	                        if (cellType == Cell.CELL_TYPE_NUMERIC) {
	                        	LinkageGroup = cell.getNumericCellValue() + "";
	                        } else {
	                        	LinkageGroup = cell.getStringCellValue().trim();
	                        }
                    		
                    		if(LinkageGroup.equals("")||LinkageGroup==null){  
                        		strColName = escn.getColumnName(colnum);
                        		rcount=rows+1;
        						String ErrMsg = " Provide the Linkage Group for Marker "+strMarkerName+" at cell position "+strColName+rcount;
        						hsession.setAttribute("indErrMsg", ErrMsg);							
        						return result = "ErrMsg";
        					}else{
        						strD=strD+LinkageGroup+"!`!";
        						//strC = strCropName;
        					}
                    	}else if(colnum==2){
                    		int cellType = cell.getCellType(); //Getting a null pointer exception when the cell is empty
                    		Position= cell.getNumericCellValue();
	                       	/*if((Position.equals(""))||(Position==null)){  
                        		strColName = escn.getColumnName(colnum);
                        		rcount=rows+1;
        						String ErrMsg = " Provide the Position for Marker "+strMarkerName+" at cell position "+strColName+rcount;
        						hsession.setAttribute("indErrMsg", ErrMsg);							
        						return result = "ErrMsg";
        					}*/
                    	}
                    }
				}
				boolean LGType=true;
				for(int r=6;r<= intSRC;r++){	
					MapMarkersBean marker_linkage = new MapMarkersBean();
					MarkerInfoBean mb=new MarkerInfoBean();
					row = sheetMarkerDetails.getRow(r);
					noOfColumns = row.getLastCellNum();
					for (int colnum = 0; colnum < noOfColumns; colnum++) {
                    	cell = row.getCell(colnum);
                    	
						if(colnum==0){
                    		strMarkerName = (String)cell.getStringCellValue().trim(); 
                    	}else if(colnum==1){
                    		int cellType = cell.getCellType(); 
	                        if (cellType == Cell.CELL_TYPE_NUMERIC) {
	                        	LinkageGroup = cell.getNumericCellValue()+"";
	                        	Matcher m = p.matcher(LinkageGroup);
	            				LGType=m.matches();
	            				//System.out.println("*************   :"+m.matches());
	            				//if(LGType==true){
	                        } else {
	                        	LinkageGroup = cell.getStringCellValue().trim();
	                        	Matcher m = p.matcher(LinkageGroup);
	            				LGType=m.matches();
	                        }
	                        
                    		//LinkageGroup = (String)cell.getStringCellValue(); 
                    	}else if(colnum==2){
                    		Position= cell.getNumericCellValue();
                    		/*double pos = cell.getNumericCellValue();
                    		if(MapUnit.equalsIgnoreCase("bp")){
                    			Position=new BigDecimal(pos)+"";
                    		}else{
                    			Position=df.format(pos);
                    		}*/                    		
                    	}				
					}
					if(lstMarkers.contains(strMarkerName)){
						intRMarkerId=(Integer)(markersMap.get(strMarkerName));
						/*mids.put(strMarkerName, intRMarkerId);
						midsList.add(intRMarkerId);*/
						
					}else{
						//maxMarkerId=maxMarkerId+1;
						maxMarkerId=maxMarkerId-1;
						intRMarkerId=maxMarkerId;
						/*mids.put(marker, intRMarkerId);
						midsList.add(intRMarkerId);	*/
						
						mb.setMarkerId(intRMarkerId);
						mb.setMarker_type(markerType);
						mb.setMarker_name(strMarkerName);
						mb.setSpecies(crop);
						
						session.save(mb);
						
					}	
					//System.out.println("MarkerID =="+strMarkerName+"   "+intRMarkerId+"   "+LinkageGroup+"   "+Position);
					marker_linkage.setMarkerId(intRMarkerId);				
					marker_linkage.setMap_id(linkageMapID);
					if(LGType==true)						
						marker_linkage.setLinkage_group(Double.valueOf((String)LinkageGroup).intValue()+"");
					else
						marker_linkage.setLinkage_group((String)LinkageGroup);
					marker_linkage.setStart_position(Position);
					marker_linkage.setEnd_position(Position);
								
					//System.out.println("........................:"+MapUnit);
					session.save(marker_linkage);	
					//System.out.println("MarkerID =="+strMarkerName+" intRMarkerId=:"+intRMarkerId+"    "+LinkageGroup+"   "+Position);
					//System.out.println("MarkerID =="+strMarkerName+"   "+LinkageGroup+"   "+df.format(Position)+"   "+new BigDecimal(Position)+"  "+(long)Position);
					if (r % 1 == 0){
						session.flush();
						session.clear();
					}
				}	
				
				tx.commit();
				/*if(rsLoc!=null) rsLoc.close(); if(rsCen!=null) rsCen.close(); if(rsMPC!=null) rsMPC.close(); if(rsMPL!=null) rsMPL.close(); if(rsML!= null) rsML.close(); if(rsMC!=null) rsMC.close();
				if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close();*/
				if(con!=null) con.close(); if(conn!=null) conn.close();
			}catch(Exception e){
			session.clear();
			//con.rollback();
			//tx.rollback();
			
			e.printStackTrace();
		}finally{
		    con.close(); conn.close();
			session.clear();
			session.disconnect();
		      }
		return result;
		}

}
