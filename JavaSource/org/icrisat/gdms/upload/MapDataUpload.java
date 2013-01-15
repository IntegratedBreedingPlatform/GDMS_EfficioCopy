package org.icrisat.gdms.upload;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.FileUploadToServer;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

import com.mysql.jdbc.Connection;

public class MapDataUpload {
	private Session session;
	static int map_count=0;
		private Transaction tx;
		Connection con = null;
		
		
		public String setMapDetails(HttpServletRequest request, String mapfile) throws SQLException{
			String result = "inserted";
			try{
				//upload the excel file
				//String crop=request.getSession().getAttribute("crop").toString();
				session = HibernateSessionFactory.currentSession();
				tx=session.beginTransaction();
				String CropName="";
				String MapUnit="";			
				String selMap="";
				int mpid=0;
				String option=request.getParameter("opMap");
				
				//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>  :"+option);
				// Workbook code   				
				Workbook workbook=Workbook.getWorkbook(new File(mapfile));
				Sheet sheetMarkerDetails = workbook.getSheet(0);
				// All the Sheets
				// Excel sheet validations
				ExcelSheetValidations fv = new ExcelSheetValidations();
				String strFv=fv.validation(workbook, request,"Map");
				//System.out.println("Valid="+strFv);
				if(!strFv.equals("valid"))
					return strFv;
				Cell cell=null;
				String str="";
				int ColCount = sheetMarkerDetails.getColumns();
				int intSRC=sheetMarkerDetails.getRows();
			    HttpSession hsession = request.getSession(true);
			    
				
				
//				/New Mapping insertion
			    //UA stands for Un Assigned
				String markerType="UA";
				String mapType="";
				String strMarkerName="";
				String LinkageGroup="" ;
				String Position=""; 
				String Query="";
				int count=0;
				int count1=0;
				int markerid_max=0;
		
				CropName=(String)sheetMarkerDetails.getCell(1, 2).getContents().trim();
				MapUnit=(String)sheetMarkerDetails.getCell(1, 3).getContents().trim();
				if (MapUnit.equalsIgnoreCase("cm")){
					mapType="genetic";
				}else
					mapType="sequence\\physical";
				/** Retrieving maximum marker id from database table 'marker'  **/
				MaxIdValue uptMDsetId=new MaxIdValue();
				int maxMarkerId=uptMDsetId.getMaxIdValue("marker_id","gdms_marker",session);
				
				
				if(option.equalsIgnoreCase("yes")){
					selMap=request.getParameter("List1");
					mpid=uptMDsetId.getUserId("dataset_id", "gdms_mapping_pop", "mapdata_desc", session,selMap);
				}
				/** Retrieving maximum linkagemap_id from database table 'linkagemap' **/
				int maxLinkageMapId=uptMDsetId.getMaxIdValue("map_id","gdms_map",session);
				int linkageMapID=maxLinkageMapId+1;
				
				/** writing to database table 'linkagemap' **/
				
				MapBean map = new MapBean();
				map.setMap_id(linkageMapID);
				map.setMap_name((String)sheetMarkerDetails.getCell(1, 0).getContents().trim());
				map.setMap_type(mapType);
				//linkagemap.setMcid(mcid);
				map.setMp_id(mpid);
				session.save(map);
				
				
				
				for(int i=6;i<intSRC;i++){
					/** writing to 'marker_linkagemap'  **/
					//map_count++;
					MarkerInfoBean markerInfo1 = new MarkerInfoBean();
					MapMarkersBean marker_linkage = new MapMarkersBean();
					
					strMarkerName = (String)sheetMarkerDetails.getCell(0, i).getContents().trim();
					LinkageGroup = (String)sheetMarkerDetails.getCell(1, i).getContents().trim();
					//int linkage_group= Integer.parseInt(LinkageGroup);
					Position = (String)sheetMarkerDetails.getCell(2, i).getContents().trim();
					//System.out.println("Position="+Position);
					double pos = Double.parseDouble(Position);
					/** insert into 'marker' table if marker doesn't exists **/
					List resultC= session.createQuery("select count(*) from MarkerInfoBean WHERE marker_name ='"+strMarkerName+"'").list();
					count= Integer.parseInt(resultC.get(0).toString()); 
							
						if(count==0){
							maxMarkerId=maxMarkerId+1;
							markerInfo1.setMarkerId(maxMarkerId);
							markerInfo1.setMarker_type(markerType);
							markerInfo1.setMarker_name(strMarkerName);
							//markerInfo1.setCrop(CropName);
							markerInfo1.setSpecies(CropName);
							session.saveOrUpdate(markerInfo1);
						}
						else{
							//System.out.println("........................marker EXITS");	
						}
							
						
						
						if(count==0){
							marker_linkage.setMarkerId(maxMarkerId);
						}else{
							List result1= session.createQuery("select markerId from MarkerInfoBean WHERE marker_name ='"+strMarkerName+"'").list();
							count1= Integer.parseInt(result1.get(0).toString()); 
							//System.out.println("count1 value is.....:"+count1);
							marker_linkage.setMarkerId(count1);
							
						}
						
						marker_linkage.setMap_id(linkageMapID);
						marker_linkage.setLinkage_group(LinkageGroup);
						marker_linkage.setStart_position(pos);
						marker_linkage.setEnd_position(pos);
						marker_linkage.setMap_unit(MapUnit);
						
						
						
						session.save(marker_linkage);
						if (i % 1 == 0){
							session.flush();
							session.clear();
						}
				}				
				tx.commit();	
				
			}catch(Exception e){
			session.clear();
			//con.rollback();
			//tx.rollback();
			
			e.printStackTrace();
		}finally{
		    
			session.clear();
			
		      }
		return result;
		}

}
