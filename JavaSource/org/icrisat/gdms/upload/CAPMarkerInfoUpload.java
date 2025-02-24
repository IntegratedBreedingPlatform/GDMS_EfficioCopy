package org.icrisat.gdms.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;
import org.icrisat.gdms.common.MaxIdValue;

public class CAPMarkerInfoUpload {
	private Session session;
	private Transaction tx;
	HttpServletRequest request;
	
//setMarkerDetails method is used to insert the Marker details information from Marker Template to database.	
	public String setMarkerDetails(HttpServletRequest request, String snpfile){
		String strResult = "inserted";
		HttpSession hsession = request.getSession();
		//String crop=request.getSession().getAttribute("crop").toString();
		try{
			session = HibernateSessionFactory.currentSession();
			tx=session.beginTransaction();
			
			
			Workbook workbook=Workbook.getWorkbook(new File(snpfile));
			String[] strSheetNames=workbook.getSheetNames();
			
			///Sheet Names display
			String[] strArrSheetNames=null;
			String strSheetName = "";
			for (int i=0;i<strSheetNames.length;i++){
				//System.out.println("Sheet Names="+ strSheetNames[i]);
				if(strSheetNames[i].equalsIgnoreCase("CAPMarkers"))
					strSheetName = strSheetNames[i];
			}
			Sheet sheetMarkerDetails = workbook.getSheet(strSheetName);
			//Marker Template validations
			String sheetNameCheck = "";			
			if(strSheetName.equals("")){
				return strResult = "SheetNameNotFound";	
			}
			int ColCount = sheetMarkerDetails.getColumns();
			int intSRC=sheetMarkerDetails.getRows();
			String strTempColumnNames[]={"Marker Name","Primer ID","Alias (comma separated for multiple names)","Crop","Genotype","Ploidy","GID","Principal Investigator","Contact","Institute","Incharge Person","Assay Type","Forward Primer","Reverse Primer","Product Size","Expected Product Size","Restriction enzyme for assay","Position on Refrence Sequence","Motif","Annealing Temperature","Sequence","Reference","Remarks"};
			//String strTempColumnNames[] = {"Marker Name","Marker Type","Principal Investigator","Contact","Institute","Incharge Person","publication","Crop","Accession ID","Genotype","Chromosome","Map","Position","Cmap ID","Forward Primer","Reverse Primer","Product Size","Expected Product Size","Position on Refrence Sequence","Left Flanking Region","Right Flanking Region","Motif","Annealing Temperature","Sequence","Reference"};
			
			//Checking column names in Excelsheet
			for(int j=0;j<strTempColumnNames.length;j++){
				String strMFieldNames = (String)sheetMarkerDetails.getCell(j, 0).getContents().trim();
					if(strMFieldNames.equalsIgnoreCase(""))
						strMFieldNames = "Empty";
				if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
				hsession.setAttribute("colMsg", strMFieldNames);
				hsession.setAttribute("colMsg1", strTempColumnNames[j]);
				hsession.setAttribute("sheetName", strSheetName);
				return "ColumnNameNotFound";
				}
			}
			
//			check the Marker Name, Crop Name, Principal Investigator, Institue fields in sheet
			for(int i=1;i<intSRC;i++){
				String strMarkerName = (String)sheetMarkerDetails.getCell(0, i).getContents().trim();
				String strCropName = (String)sheetMarkerDetails.getCell(3, i).getContents().trim();	
				String strPrincipalInves=(String)sheetMarkerDetails.getCell(7, i).getContents().trim();	
				String strInstitute=(String)sheetMarkerDetails.getCell(9, i).getContents().trim();
				//String strForwaredPrimer=(String)sheetMarkerDetails.getCell(20, i).getContents().trim();
				//String strReversePrimer=(String)sheetMarkerDetails.getCell(21, i).getContents().trim();
				boolean bCrop = false;
				if(strMarkerName.equals("")||strMarkerName==null){
					ExcelSheetColumnName escn =  new ExcelSheetColumnName();
					String strColName = escn.getColumnName(sheetMarkerDetails.getCell(0, i).getColumn());							
					String ErrMsg = " Provide Marker name at cell position "+strColName+(sheetMarkerDetails.getCell(0, i).getRow()+1);
					hsession.setAttribute("indErrMsg", ErrMsg);							
					return strResult = "ErrMsg";
				}else{ //	bCrop = !strCropName.equals("");						
					if(strCropName.equals("")||strCropName==null){
						ExcelSheetColumnName escn =  new ExcelSheetColumnName();
						String strColName = escn.getColumnName(sheetMarkerDetails.getCell(3, i).getColumn());							
						String ErrMsg = "Provide the Species derived from for Marker "+strMarkerName+" at cell position "+strColName+(sheetMarkerDetails.getCell(3, i).getRow()+1);
						hsession.setAttribute("indErrMsg", ErrMsg);							
						return strResult = "ErrMsg";							
					}else if(strPrincipalInves.equals("")||strPrincipalInves==null){
						ExcelSheetColumnName escn =  new ExcelSheetColumnName();
						String strColName = escn.getColumnName(sheetMarkerDetails.getCell(7, i).getColumn());							
						String ErrMsg = "Provide the Principal Investigator for Marker "+strMarkerName+" at cell position "+strColName+(sheetMarkerDetails.getCell(7, i).getRow()+1);
						hsession.setAttribute("indErrMsg", ErrMsg);							
						return strResult = "ErrMsg";
					}else if(strInstitute.equals("")||strInstitute==null){
						ExcelSheetColumnName escn =  new ExcelSheetColumnName();
						String strColName = escn.getColumnName(sheetMarkerDetails.getCell(9, i).getColumn());							
						String ErrMsg = "Provide the Institute for Marker "+strMarkerName+" at cell position "+strColName+(sheetMarkerDetails.getCell(9, i).getRow()+1);
						hsession.setAttribute("indErrMsg", ErrMsg);							
						return strResult = "ErrMsg";
					}
				}
				
			}			
			
			//Get the Marker names from template and adding the Marker and Crop names to the List object.
			List<String> listTempMarkerNames= new ArrayList<String>();
			List<String> listCropNames = new ArrayList<String>();
			String strCropNames = "";
			String strC = "";
			int Position_on_reference_sequence;
			String restriction_enzyme_for_assay="";
			for(int i=1;i<intSRC;i++){
				String strMName = (String)sheetMarkerDetails.getCell(0,i).getContents().trim();
				String strCName = (String)sheetMarkerDetails.getCell(3,i).getContents().trim();
				if(!strMName.equals("") && !strCName.equals("")){
					String str=sheetMarkerDetails.getCell(0,i).getContents().trim()+"!`!"+sheetMarkerDetails.getCell(3,i).getContents().trim()+"!`!CAP";
					listTempMarkerNames.add(str.toLowerCase());
					strC = sheetMarkerDetails.getCell(3,i).getContents().trim();
					if(!listCropNames.contains(strC.toLowerCase())){
						strCropNames = strCropNames +"'"+ sheetMarkerDetails.getCell(3,i).getContents().trim() +"'"+",";	
						listCropNames.add(strC.toLowerCase());
					}
										
				}
			}
			
			//Retrieving the marker names from the database					
			Query rsMarkerNames=session.createQuery("from MarkerInfoBean where Lower(species) in("+strCropNames.substring(0, strCropNames.length()-1).toLowerCase()+")and marker_type='CAP'");
			
			List result= rsMarkerNames.list();				
			Iterator it=result.iterator();
			List<String> listDBMarkerNames = new ArrayList<String>();				
			///concatenate Marker name with crop name and adding to List object.
			while(it.hasNext()){
				MarkerInfoBean uMarkerInfo= (MarkerInfoBean)it.next();					
				//String strMC=uMarkerInfo.getMarker_name()+"!`!"+uMarkerInfo.getCrop()+"!`!"+uMarkerInfo.getMarker_type();					
				String strMC=uMarkerInfo.getMarker_name()+"!`!"+uMarkerInfo.getSpecies()+"!`!"+uMarkerInfo.getMarker_type();
				listDBMarkerNames.add(strMC.toLowerCase());					
			}		
			//System.out.println("listDBMarkerNames:"+listDBMarkerNames);
			///Database and Template Marker names comparision
			Object objCom=null;
			Iterator itCom;				
			itCom=listTempMarkerNames.iterator();
			List<String> listNewMarkers = new ArrayList<String>();
			String strDupMarkers = "";
			while(itCom.hasNext()){
				objCom=itCom.next();
				if(!listDBMarkerNames.contains(objCom)){						
					String str=(String)objCom;
					listNewMarkers.add(str.toLowerCase());
				}else{
					strDupMarkers = strDupMarkers + (String)objCom +",";
					strDupMarkers=strDupMarkers.replaceAll("!`!", ":");
				}
			}
			//Message will be displayed when the marker(s) already exists in the database.
			//System.out.println("listNewMarkers="+listNewMarkers);
			if(listNewMarkers.size()==0){
				String ErrMsg = "All the marker(s) already exists in the database";
				hsession.setAttribute("indErrMsg", ErrMsg);					
				return strResult = "ErrMsg";
			}	
			
			///New Markers insertion
			MarkerInfoBean markerInfo=null;
			MarkerUserInfoBean markerUserInfo=null;
			MarkerDetailsBean mDet=null;
			//ChromosomeBean chromosome=null;
			MarkerAliasBean markerAlias=null;
			
			String MarkerType="CAP";
//			Retrieve the maximum marker_id from the marker_details table.
			MaxIdValue uptMDsetId=new MaxIdValue();
			int MarkerID=0;double Annealing_temp=0;
			String productSize="";
			//int productSize=0;
			String expectedProductSize="";
			int maxMarkerId=uptMDsetId.getMaxIdValue("marker_id","gdms_marker",session);
			//maxMarkerId++;
			maxMarkerId--;				
			boolean	exists=false;			
				//System.out.println("maxMarkerId="+maxMarkerId+"    listNewMarkers="+listNewMarkers);
			for(int r=1;r<intSRC;r++){	
				exists=false;				
				String str=sheetMarkerDetails.getCell(0,r).getContents().trim()+"!`!"+sheetMarkerDetails.getCell(3,r).getContents().trim()+"!`!CAP";					
				if(listNewMarkers.contains(str.toLowerCase())){
					//MarkerID=maxMarkerId++;
					MarkerID=maxMarkerId--;
				}else{
					//retrieving MarkerID for the already existing marker from MarkerInfo table
					List listValues=session.createQuery("select markerId from MarkerInfoBean where Lower(marker_name) ='"+(String)sheetMarkerDetails.getCell(0,r).getContents().trim().toLowerCase()+"' and Lower(species)='"+(String)sheetMarkerDetails.getCell(3,r).getContents().trim().toLowerCase()+"' and marker_type='CAP'").list();
					Iterator itList=listValues.iterator();
					Object obj=null;			
					while(itList.hasNext())
					{
						obj=itList.next();
						MarkerID=Integer.parseInt(obj.toString());
						//checking if the data for marker exists in SSR Marker Table
						List MarkerInSSR=session.createQuery("from MarkerDetailsBean where marker_id ="+MarkerID).list();
						Iterator MarkerInSSRList=MarkerInSSR.iterator();
						if(MarkerInSSRList.hasNext()){
							exists=true;
						}
					}
				}
				if(exists==true){
					continue;
				}
				
				//System.out.println("MarkerID == "+MarkerID);
				
//				Adding data to MarkerInfo table
				markerInfo=new MarkerInfoBean();
				markerInfo.setMarkerId(MarkerID);
				//markerInfo.setMarker_type_id(MarkerTypeID);
				markerInfo.setMarker_type(MarkerType);
				markerInfo.setMarker_name(sheetMarkerDetails.getCell(0,r).getContents().trim());
				/*markerInfo.setCrop(sheetMarkerDetails.getCell(3,r).getContents().trim());
				markerInfo.setAccession_id(sheetMarkerDetails.getCell(6,r).getContents().trim());*/
				markerInfo.setSpecies(sheetMarkerDetails.getCell(3,r).getContents().trim());
				markerInfo.setDb_accession_id(sheetMarkerDetails.getCell(6,r).getContents().trim());
				markerInfo.setReference(sheetMarkerDetails.getCell(21,r).getContents().trim());
				markerInfo.setGenotype(sheetMarkerDetails.getCell(4,r).getContents().trim());
				markerInfo.setPloidy(sheetMarkerDetails.getCell(5,r).getContents().trim());
				markerInfo.setRemarks(sheetMarkerDetails.getCell(22,r).getContents().trim());
				markerInfo.setAssay_type(sheetMarkerDetails.getCell(11,r).getContents().trim());
				markerInfo.setForward_primer(sheetMarkerDetails.getCell(12,r).getContents().trim());
				markerInfo.setReverse_primer(sheetMarkerDetails.getCell(13,r).getContents().trim());
				//System.out.println("-----------  "+sheetMarkerDetails.getCell(14,r).getContents());
				if(sheetMarkerDetails.getCell(14,r).getContents().equals("")){
					productSize="";
					//productSize=0;
				}else{
					productSize=sheetMarkerDetails.getCell(14,r).getContents().trim();
					//productSize=Integer.parseInt(sheetMarkerDetails.getCell(14,r).getContents().trim().toString());
				}
				markerInfo.setProduct_size(productSize);
				markerInfo.setMotif(sheetMarkerDetails.getCell(18,r).getContents().trim());
				if(sheetMarkerDetails.getCell(19,r).getContents().equals("")){
						Annealing_temp=0;
					}else{
						Annealing_temp=Double.parseDouble(sheetMarkerDetails.getCell(19,r).getContents().trim());
					}
				markerInfo.setAnnealing_temp(Annealing_temp);
				
				
				//Adding data to marker_alias Table
				markerAlias=new MarkerAliasBean();
				markerAlias.setMarkerId(MarkerID);
				markerAlias.setAlias(sheetMarkerDetails.getCell(2,r).getContents().trim());						
				
				//Adding data to marker_user_info
				markerUserInfo=new MarkerUserInfoBean();						
				markerUserInfo.setMarker_id(MarkerID);
				markerUserInfo.setPrincipal_investigator(sheetMarkerDetails.getCell(7,r).getContents().trim());
				markerUserInfo.setContact(sheetMarkerDetails.getCell(8,r).getContents().trim());
				markerUserInfo.setInstitute(sheetMarkerDetails.getCell(9,r).getContents().trim());
				//markerUserInfo.setPublication(sheetMarkerDetails.getCell(6,r).getContents().trim());
				//markerUserInfo.setIncharge_person(sheetMarkerDetails.getCell(10,r).getContents().trim());
				
				
					mDet=new MarkerDetailsBean();						
					mDet.setMarker_id(MarkerID);
					
					if(sheetMarkerDetails.getCell(15,r).getContents().equals("")){
						expectedProductSize="0";
					}else{
						expectedProductSize=sheetMarkerDetails.getCell(15,r).getContents().trim();
					}
					mDet.setExpected_product_size(Integer.parseInt(expectedProductSize));
					mDet.setRestriction_enzyme_for_assay(sheetMarkerDetails.getCell(16,r).getContents().trim());
					if(sheetMarkerDetails.getCell(17,r).getContents().equals("")){
						Position_on_reference_sequence=0;
						
					}else{
						Position_on_reference_sequence=Integer.parseInt(sheetMarkerDetails.getCell(17,r).getContents().trim());
						
					}
					mDet.setPosition_on_reference_sequence(Position_on_reference_sequence);
					//SNPMarker.setLeft_flanking_region(sheetMarkerDetails.getCell(19,r).getContents().trim());
					//SNPMarker.setRight_flanking_region(sheetMarkerDetails.getCell(20,r).getContents().trim());
					
					
					mDet.setSequence(sheetMarkerDetails.getCell(20,r).getContents().trim());
					
					
					
					session.saveOrUpdate(markerInfo);	
					session.saveOrUpdate(markerUserInfo);
					session.saveOrUpdate(mDet);
					session.saveOrUpdate(markerAlias);
					
					//session.save(chromosome);
					//System.out.println("Inserted a record");
				//}
				if (r % 1 == 0){
					session.flush();
					session.clear();
				}					
			}	
			//System.out.println("Commited record");
			tx.commit();				
			//duplication markers list display message.							
			if(!strDupMarkers.equals("")){
				strDupMarkers = strDupMarkers.substring(0,strDupMarkers.length()-1);					
				String ErrMsg = "Marker(s) ["+strDupMarkers+"] already exists in the database.\nRemaining Marker(s) has been inserted successfully";
				hsession.setAttribute("indErrMsg", ErrMsg);					
				return strResult = "ErrMsg";
			}
			
		}catch(Exception e){
			System.out.println("Error "+ e.toString());
			//tx.rollback();
			e.printStackTrace();
		}finally{
		      // Actual contact insertion will happen at this step
		      //session.flush();
		      session.clear();
		      session.disconnect();
		      }
		
		return strResult;
	}
}
