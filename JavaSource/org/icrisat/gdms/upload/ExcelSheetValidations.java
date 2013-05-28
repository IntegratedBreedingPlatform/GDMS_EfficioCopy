package org.icrisat.gdms.upload;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Sheet;
import jxl.Workbook;

import org.icrisat.gdms.common.MaxIdValue;



public class ExcelSheetValidations {
	String strValid = "valid";
	public String validation(Workbook workbook, HttpServletRequest request,String type){
		System.out.println("....................................TYPE="+type);
		 HttpSession hsession = request.getSession(true);
		 CheckNumericDatatype cnd = new CheckNumericDatatype();
		 //String mspStd = (String)request.getAttribute("genotypemap");
		 ExcelSheetColumnName escn =  new ExcelSheetColumnName();
		 String[] strSheetNames=workbook.getSheetNames();
		 ///Sheet Names display
		
		 List<String> lSN = new ArrayList<String>();
		 List<String> lstSheetNames = new ArrayList<String>();
		 if(type.equalsIgnoreCase("ssrG")){
			 lstSheetNames.add("ssr_source");
			 lstSheetNames.add("ssr_data list");
							
			 for (int i=0;i<strSheetNames.length;i++){
				 String strSN = strSheetNames[i];
				 if(lstSheetNames.contains(strSN.toLowerCase())){
					 if(!lSN.contains(strSN))
						 lSN.add(strSN);
				 }	
			 }
			 
			 if(lstSheetNames.size()!=lSN.size())
				 return "SheetNameNotFound";
							
			 //check the template fields
			 for(int i=0;i<strSheetNames.length;i++){
				 String strSName = strSheetNames[i].toString();
				 if(strSName.equalsIgnoreCase("SSR_Source")){
					 Sheet sName = workbook.getSheet(strSName);
					 //String strTempColumnNames[] = {"Institute","Principle investigator","Email contact","Dataset description","Genus","Species","Version","Creation Date","Remark"};
					 String strTempColumnNames[] = {"Institute","Principle investigator","Dataset Name","Dataset description","Genus","Species","Missing Data","Remark"};
									
					 for(int j=0;j<strTempColumnNames.length;j++){
						 String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
										
						 if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 //System.out.println("TTTTTTTT="+strMFieldNames);
							 //System.out.println("TTTTTTTT="+strTempColumnNames[j]);
							 hsession.setAttribute("colMsg1", strTempColumnNames[j]);
							 hsession.setAttribute("sheetName", strSName);
							 //System.out.println("SSR Source");
							 return "ColumnNameNotFound";
						 }
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
							 hsession.setAttribute("sheetName", strSName);
							 return "DelEmptyRows";
						 }
					 }															
				 }
								
				 //SSR_DataList fields validation
				 if(strSName.equalsIgnoreCase("SSR_Data List")){
					 Sheet sName = workbook.getSheet(strSName);
					 //int ColCount = sName.getColumns();					
					 String strTempColumnNames[] = {"GID","Accession","Marker","Gel/Run","Dye","Called Allele","Raw Data","Quality","Height","Volume","Amount"};
					 for(int j=0;j<strTempColumnNames.length;j++){
						 String strMFieldNames = (String)sName.getCell(j, 0).getContents().trim();
						 if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 hsession.setAttribute("colMsg1", strTempColumnNames[j]);
							 hsession.setAttribute("sheetName", strSName);
							 return "ColumnNameNotFound";
						 }
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(j, 0).getColumn());
							 hsession.setAttribute("colposition", strColName);
							 hsession.setAttribute("sheetName", strSName);
							 return "DelEmptyColumns";
						 }
					 }
				 }
			 }
			 
			 //check the required fields in SSR_Source;
			 for(int i=0;i<strSheetNames.length;i++){
				 if(strSheetNames[i].equalsIgnoreCase("SSR_Source")){
					 Sheet sName = workbook.getSheet(i);
					 int intNoOfRows = sName.getRows();
					 for(int j=0;j<intNoOfRows;j++){
						 String strFieldsName = sName.getCell(0, j).getContents().trim();
						 //required fields institute, species, genus, creation date in ssr_source.
						 //if(strFieldsName.equalsIgnoreCase("institute") || strFieldsName.equalsIgnoreCase("Dataset description") || strFieldsName.equalsIgnoreCase("genus") || strFieldsName.equalsIgnoreCase("creation date")){
						 if(strFieldsName.equalsIgnoreCase("institute")|| strFieldsName.equalsIgnoreCase("Dataset Name") || strFieldsName.equalsIgnoreCase("Dataset description") || strFieldsName.equalsIgnoreCase("genus") || strFieldsName.equalsIgnoreCase("missing data")){
							 String strFieldValue = sName.getCell(1, j).getContents().trim();
							 if(strFieldValue == null || strFieldValue == ""){
								 //hsession.setAttribute("dlength", strFieldsName);
								 //hsession.setAttribute("colposition", strColName+(sName.getCell(k,j).getRow()+1));
								 //return "ivSpecies";
								 hsession.setAttribute("fieldName", strFieldsName);
								 hsession.setAttribute("sheetName", strSheetNames[i]);
								 String strColName = escn.getColumnName(sName.getCell(1, j).getColumn());
								 hsession.setAttribute("colposition", strColName+(sName.getCell(1,j).getRow()+1));
								 return "ReqFields";
							 }
						 }
					 }
				 }
				 
				 //Accession, Marker and Amount fields from ssr_data list.
				 if(strSheetNames[i].equalsIgnoreCase("SSR_Data List")){
					 Sheet sName = workbook.getSheet(i);
					 int intNoOfRows = sName.getRows();
					 int intNoOfCols = sName.getColumns();
					 String strFieldsName = "";
					 String strFieldsName1 = "";
					 int iAcc=0,iMar=0,iGr=0,iDye=0,iAlle=0,iSize=0,iQua=0,iHei=0,iVol=0,iAmo=0, iGID=0;
					 for(int c=0;c<intNoOfCols;c++){
						 String strFieldsN = sName.getCell(c, 0).getContents().trim();
						 if(strFieldsN.equalsIgnoreCase("GID"))
							 iGID = c;
						 if(strFieldsN.equalsIgnoreCase("Accession"))
							 iAcc = c;
						 if(strFieldsN.equalsIgnoreCase("Marker"))
							 iMar = c;
						 if(strFieldsN.equalsIgnoreCase("Gel/Run"))
							 iGr = c;
						 if(strFieldsN.equalsIgnoreCase("Dye"))
							 iDye = c;
						 if(strFieldsN.equalsIgnoreCase("Called Allele"))
							 iAlle = c;
						 if(strFieldsN.equalsIgnoreCase("Raw Data"))
							 iSize = c;
						 if(strFieldsN.equalsIgnoreCase("Quality"))
							 iQua = c;
						 if(strFieldsN.equalsIgnoreCase("Height"))
							 iHei = c;
						 if(strFieldsN.equalsIgnoreCase("Volume"))
							 iVol = c;
						 if(strFieldsN.equalsIgnoreCase("Amount"))
							 iAmo = c;
						 
						 strFieldsName1 = sName.getCell(1, 0).getContents().trim();
						 strFieldsName = sName.getCell(0, 0).getContents().trim();
					 }
					 
					 ///Accession sets validation
					 //getting the germplasm names and count of germplasm from Data List of Template
					 int intColcount=0;
					 for(int t=0;t<intNoOfRows;t++){
						 String strAFieldValue = sName.getCell(0, t).getContents().trim();
						 String strMFieldValue = sName.getCell(1, t).getContents().trim();
						 String strAmount = sName.getCell(iAmo, t).getContents().trim();
						 //skip the row which contains all the null values
						 if(!strAFieldValue.equals("") && !strMFieldValue.equals("") && !strAmount.equals("")){
							 intColcount++;
						 }
					 }
					 //System.out.println("intcount="+intColcount);
					 //System.out.println("intNofofrows="+intNoOfRows);
					 for(int j=0;j<intNoOfRows;j++){
						 //String strFieldsName1 = sName.getCell(0, j).getContents().trim();
						 String strAFieldValue = sName.getCell(0, j).getContents().trim();
						 String strMFieldValue = sName.getCell(1, j).getContents().trim();
						 String strAmount = sName.getCell(iAmo, 0).getContents().trim();
						 //skip the row which contains all the null values
						 //if(!strAFieldValue.equals("") && !strMFieldValue.equals("") && !strAmount.equals("")){
						 if(j<intColcount){
							 //GIDs
							 if(strFieldsName.equalsIgnoreCase("GID")){
								 //System.out.println("MMMMMMMMMMAccession="+sName.getCell(0, j).getContents().trim()+"=="+sName.getCell(1, j).getContents().trim());
								 if(strAFieldValue.equals("") && !strMFieldValue.equals("")){
									 hsession.setAttribute("fieldName", strFieldsName);
									 hsession.setAttribute("sheetName", strSheetNames[i]);
									 String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
									 hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
									 
									 return "ReqFields";
									 //return "AccCheck";
								 }
							 }
							 
							 //Accessions
							 if(strFieldsName.equalsIgnoreCase("Accession")){
								 //System.out.println("MMMMMMMMMMAccession="+sName.getCell(0, j).getContents().trim()+"=="+sName.getCell(1, j).getContents().trim());
								 if(strAFieldValue.equals("") && !strMFieldValue.equals("")){
									 hsession.setAttribute("fieldName", strFieldsName);
									 hsession.setAttribute("sheetName", strSheetNames[i]);
									 String strColName = escn.getColumnName(sName.getCell(1, j).getColumn());
									 hsession.setAttribute("colposition", strColName+(sName.getCell(1, j).getRow()+1));
									 
									 return "ReqFields";
									 //return "AccCheck";
								 }
							 }
							 //Markers
							 if(strFieldsName1.equalsIgnoreCase("Marker")){
								 //String strAFieldValue = sName.getCell(0, j).getContents().trim();
								 //String strMFieldValue = sName.getCell(1, j).getContents().trim();
								 //System.out.println("MMMMMMMMMMarker="+sName.getCell(0, j).getContents().trim()+"=="+sName.getCell(1, j).getContents().trim());
								 if(!strAFieldValue.equals("") && strMFieldValue.equals("")){
									 hsession.setAttribute("fieldName", strFieldsName);
									 hsession.setAttribute("sheetName", strSheetNames[i]);
									 String strColName = escn.getColumnName(sName.getCell(2, j).getColumn());
									 hsession.setAttribute("colposition", strColName+(sName.getCell(2, j).getRow()+1));
									 return "ReqFields";										
									 //return "MccCheck";
								 }
							 }
							 //Both the Accession and Marker is either null or not
							 if(strAFieldValue.equals("") && strMFieldValue.equals("")){
								 //String strColName = escn.getColumnName(sName.getCell(1, j).getColumn());
								 String strRowNumber = String.valueOf(sName.getCell(1, j).getRow()+1);
								 //hsession.setAttribute("colposition", strRowNumber);
								 //return "AccMccCheck";
								 String ErrMsg = "Accession and Marker values should not be null in SSR_Data List sheet.\n            Please delete it if not required.\n            The row position is "+strRowNumber;
								 hsession.setAttribute("indErrMsg", ErrMsg);
								 return "ErrMsg";
							 }
							 // Amount field should contain the value when the fields (Allele,Size and Volume) are not null;
						
							 if(strAmount.equalsIgnoreCase("Amount")){
								 if(j!=0){
									 boolean bIntASize = cnd.isFloat(sName.getCell(10,j).getContents().trim());
							
									 if(bIntASize){
										 //upDLB.setAllele_size(Float.parseFloat(sName.getCell(9,j).getContents().trim()));
									
										 String stra = (String)sName.getCell(10, j).getContents().trim();
										 Double dbA = Double.valueOf(stra);
										 //System.out.println("dba="+dbA);
										 if(dbA>=0.0 && dbA<=1.00){
										
										
										 }else{
											 String strRowNumber = String.valueOf(sName.getCell(10, j).getRow()+1);
											 //hsession.setAttribute("colposition", strRowNumber);
											 //return "AmountValCheck";
											 String ErrMsg = "The value under Amount in the SSR_Data List sheet should be between 0 and 1.\n The row position is "+strRowNumber;
											 hsession.setAttribute("indErrMsg", ErrMsg);
											 return "ErrMsg";
										 }
									 }else{
										 //System.out.println("not a numeric value");
										 String strRowNumber = String.valueOf(sName.getCell(10, j).getRow()+1);
										 //hsession.setAttribute("colposition", strRowNumber);
										 //return "AmountValCheck";
										 String ErrMsg = "The value under Amount in the SSR_Data List sheet should be between 0 and 1.\n The row position is "+strRowNumber;
										 hsession.setAttribute("indErrMsg", ErrMsg);
										 return "ErrMsg";
									 }
								 }
							 }
							 //}	
						 }else{
							 //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAaa========"+j+ "  intColcount="+intColcount);
							 //End Error message should not display
						 }
					 }
					 //get the Markers from the Data List sheet
					 List<String> lstMarNames = new ArrayList<String>();
									
					 for(int a=1;a<intNoOfRows;a++){
						 String strM = (String)sName.getCell(2, a).getContents().trim();
						 String strAcc = (String)sName.getCell(0, a).getContents().trim();
						 String strAmo = (String)sName.getCell(10, a).getContents().trim();
						 if(!lstMarNames.contains(strM) && !strM.equals("") && !strAcc.equals("") && !strAmo.equals("")){
							 lstMarNames.add(strM);
							 //System.out.println("getthe markers from the DL="+strM);
						 }
					 }
					 int firstAccCount = 0;
					 int rowcount = 1;
					 //System.out.println("lstMarNames.size()="+lstMarNames.size());
					 List<String> lstFirstAccSet = new ArrayList<String>();
					 for(int m=0;m<lstMarNames.size();m++){
						 List<String> listGNames = new ArrayList<String>();
						 String strMarCheck = (String) lstMarNames.get(m);
						 //System.out.println("Markername="+strMarCheck);
						 for(int ac=rowcount;ac<intNoOfRows;ac++){
							 String strAmount=(String)sName.getCell(10,ac).getContents().trim();
							 //String strAcc=(String)sName.getCell(0,ac).getContents().trim();
							 //String strMar=(String)sName.getCell(2,ac).getContents().trim();
						
							 //skip the row which contains all the null values
							 //if(!strAcc.equals("") && !strMar.equals("") && !strAmount.equals("")){
							 if(ac<intColcount){
								 float fltAmount = Float.parseFloat(strAmount);
								 //System.out.println("fltAmount="+fltAmount);
								 
								 if(strMarCheck == (String)sName.getCell(2,ac).getContents().trim()){
									 if((fltAmount == 0.0) || (fltAmount == 1.0)){
										 listGNames.add(sName.getCell(0,ac).getContents().trim());
										// System.out.println("gn="+sName.getCell(0,ac).getContents().trim()+" ac="+ac);
									 }else{
										 listGNames.add(sName.getCell(0,ac).getContents().trim());
										// System.out.println("gnelse="+sName.getCell(0,ac).getContents().trim()+" ac= "+ac);
										 int fltA=0;
										 for(int r=1;r<25;r++){
											 double f = fltAmount*r;
											 MaxIdValue rt = new MaxIdValue();
											 double fltRB=rt.roundThree(f);
											 if((fltRB>=0.900 && fltRB<=0.999))
												 fltRB=Math.round(f);
											 if(fltRB==1.000){
												 fltA=r;
												 r=25;
											 }
										 }
										 if(fltA!=0){
											 ac=ac+fltA-1;
											 rowcount=rowcount+fltA-1;
										 }
									 }
								 }else{
									 //ac=intNoOfRows;
									 break;
								 }
								 rowcount++;
								 //System.out.println("rowcount"+rowcount);
							 }
							 //}
						 }
						 //System.out.println("listGNames"+listGNames);
						// System.out.println("lstFirstAccSet"+lstFirstAccSet);
						/* if(m==0){
							 firstAccCount=listGNames.size();
							 lstFirstAccSet.addAll(listGNames);
						 }else{
							 for(int k=0;k<firstAccCount;k++){
								// System.out.println("BBBBBBBBBLLLEEEEEEEEEE  ="+k+"    "+lstFirstAccSet.get(k)+".equalsIgnoreCase(  "+listGNames.get(k));
								 if(!lstFirstAccSet.get(k).equalsIgnoreCase(listGNames.get(k))){
									 String ErrMsg = "In the SSR_Data List sheet, order of the Accession per Marker does not match";
									 hsession.setAttribute("indErrMsg", ErrMsg);
									 return "ErrMsg";
									 //return "AccSetNotPro";	
								 }
							 }
						 }
						 //System.out.println("firstAccCount!=listGNames.size()"+firstAccCount+"!="+listGNames.size());
						 if(firstAccCount!=listGNames.size()){
							 String ErrMsg = "In the SSR_Data List sheet, count of Accession per Marker does not tally";
							 hsession.setAttribute("indErrMsg", ErrMsg);
							 return "ErrMsg";
							 //return "AccSetNotProperly";
						 }*/
					 }				 
					 //end of the Accession set validation
				 } // end Data List validation
								
				 //check the field length
				 /*if(strSheetNames[i].equalsIgnoreCase("SSR_Source")){
					Sheet sName = workbook.getSheet(strSheetNames[i]);
					String strLableVal = (String)sName.getCell(1, 3).getContents().trim();
					String strLable = (String)sName.getCell(0, 3).getContents().trim();
					int len = strLableVal.length();
					
					if(len>76){
						hsession.setAttribute("fieldName", strLable);
						hsession.setAttribute("sheetName", strSheetNames[i]);
						return "FieldLength";
					}
								
								
								
			 	}*/
			 }		
		 }else if(type.equalsIgnoreCase("QTL")){
			 lstSheetNames.add("qtl_source");
			 lstSheetNames.add("qtl_data");
				
			 //System.out.println("lstSheetNames=:"+lstSheetNames);
			 
			 for (int i=0;i<strSheetNames.length;i++){
				 String strSN = strSheetNames[i];
				 //System.out.println("strSN=:"+strSN);
				 if(lstSheetNames.contains(strSN.toLowerCase())){
					 if(!lSN.contains(strSN))
						 lSN.add(strSN);
				 }	
			 }
			 
			 if(lstSheetNames.size()!=lSN.size())
				 return "SheetNameNotFound";
							
			 //check the template fields
			 for(int i=0;i<strSheetNames.length;i++){
				 String strSName = strSheetNames[i].toString();
				 if(strSName.equalsIgnoreCase("QTL_Source")){
					 Sheet sName = workbook.getSheet(strSName);
					 //String strTempColumnNames[] = {"Institute","Principle investigator","Email contact","Dataset description","Genus","Species","Version","Creation Date","Remark"};
					 //String strTempColumnNames[] = {"Institute","Principle investigator","Dataset Name","Dataset description","Genus","Species","Remark"};
					 String strTempColumnNames[] = {"Institute","Principle investigator","Dataset Name","Dataset description","Genus","Method","Score","Species","Remark"};
									
					 for(int j=0;j<strTempColumnNames.length;j++){
						 String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
										
						 if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 //System.out.println("TTTTTTTT="+strMFieldNames);
							 //System.out.println("TTTTTTTT="+strTempColumnNames[j]);
							 hsession.setAttribute("colMsg1", strTempColumnNames[j]);
							 hsession.setAttribute("sheetName", strSName);
							 //System.out.println("SSR Source");
							 return "ColumnNameNotFound";
						 }
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
							 hsession.setAttribute("sheetName", strSName);
							 return "DelEmptyRows";
						 }
					 }															
				 }
								
				 //SSR_DataList fields validation
				 if(strSName.equalsIgnoreCase("QTL_Data")){
					 Sheet sName = workbook.getSheet(strSName);
					 //int ColCount = sName.getColumns();					
					 //String strTempColumnNames[] = {"Name","Chromosome","Marker","Gel/Run","Dye","Called Allele","Raw Data","Quality","Height","Volume","Amount"};
					 //String strTempColumnNames[] = {"Name","Chromosome","Map-Name","Position","Pos-Min","Pos-Max","Trait","Experiment","CLEN","LFM","RFM","Effect","LOD","R2","Interactions"};
					 String strTempColumnNames[] = {"Name","Chromosome","Map-Name","Position","Pos-Min","Pos-Max","Trait","Experiment","CLEN","LFM","RFM","Effect","SE additive","High value parent","High value allele","Low value parent","Low value allele","Score (e.g., LOD/-log10 (p))","R2","Interactions"};

					 for(int j=0;j<strTempColumnNames.length;j++){
						 String strMFieldNames = (String)sName.getCell(j, 0).getContents().trim();
						 if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 hsession.setAttribute("colMsg1", strTempColumnNames[j]);
							 hsession.setAttribute("sheetName", strSName);
							 return "ColumnNameNotFound";
						 }
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(j, 0).getColumn());
							 hsession.setAttribute("colposition", strColName);
							 hsession.setAttribute("sheetName", strSName);
							 return "DelEmptyColumns";
						 }
					 }
				 }
			 }
			 
		}else if(type.equalsIgnoreCase("Map")){
			lstSheetNames.add("map");
			for (int i=0;i<strSheetNames.length;i++){
				 String strSN = strSheetNames[i];
				 if(lstSheetNames.contains(strSN.toLowerCase())){
					 if(!lSN.contains(strSN))
						 lSN.add(strSN);
				 }	
			 }
			 
			 if(lstSheetNames.size()!=lSN.size())
				 return "SheetNameNotFound";
							
			 //check the template fields
			 for(int i=0;i<strSheetNames.length;i++){
				 String strSName = strSheetNames[i].toString();
				 if(strSName.equalsIgnoreCase("Map")){
					 Sheet sName = workbook.getSheet(strSName);
					 //String strTempColumnNames[] = {"Institute","Principle investigator","Dataset description","Genus","Species","Missing Data","Remark"};
					 String strTempColumnNames[] = {"Map Name","Map Description","Crop","Map Unit"};

									
					 for(int j=0;j<strTempColumnNames.length;j++){
						 String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
							//System.out.println("strMFieldNames="+strMFieldNames);
							//System.out.println("strTempColumnNames;"+strTempColumnNames[j]);
						 if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 //System.out.println("TTTTTTTT="+strMFieldNames);
							 //System.out.println("TTTTTTTT="+strTempColumnNames[j]);
							 hsession.setAttribute("colMsg1", strTempColumnNames[j]);
							 hsession.setAttribute("sheetName", strSName);
							 //System.out.println("SSR Source");
							 return "ColumnNameNotFound";
						 }
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
							 hsession.setAttribute("sheetName", strSName);
							 return "DelEmptyRows";
						 }
					 }	
					 
					 String strTempColumnNames1[] = {"Marker Name","Linkage Group","Position"};
					 Sheet sNameMap = workbook.getSheet(i);
					 int intNoOfRows = sNameMap.getRows();
					 int intNoOfCols = sNameMap.getColumns();
					 for(int j=0;j<strTempColumnNames1.length;j++){
						 
						 String strMFieldNames = (String)sName.getCell(j, 5).getContents().trim();
						// System.out.println("strMFieldNames="+strMFieldNames);
						 if(!strTempColumnNames1[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 hsession.setAttribute("colMsg1", strTempColumnNames1[j]);
							 hsession.setAttribute("sheetName", strSName);
							 return "ColumnNameNotFound";
						 }
						
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(j, 5).getColumn());
							// hsession.setAttribute("colpositionNull", strColName);
							 hsession.setAttribute("sheetNameNull", strSName);
							 //return "DelEmptyColumns";
							 return "infoRequired";
						 }
					 }
					 String strFieldsName = "";
					 String strFieldsName1 = "";
					 int iMar=0,iLG=0,iPos=0;
					 for(int c=0;c<intNoOfCols;c++){
						 String strFieldsN = sName.getCell(c, 0).getContents().trim();
						 
						 if(strFieldsN.equalsIgnoreCase("Marker Name"))
							 iMar = c;
						 if(strFieldsN.equalsIgnoreCase("Linkage Group"))
							 iLG = c;
						 if(strFieldsN.equalsIgnoreCase("Position"))
							 iPos = c;
											 
						 strFieldsName1 = sName.getCell(1, 0).getContents().trim();
						 strFieldsName = sName.getCell(0, 0).getContents().trim();
					 }
					 
					 
					 for(int j=7;j<intNoOfRows;j++){
						 String strMFieldValue = sName.getCell(0, j).getContents().trim();
						 String strLGFieldValue = sName.getCell(1, j).getContents().trim();
						 String strPosition = sName.getCell(2, j).getContents().trim();
						// System.out.println(strMFieldValue+"   "+strLGFieldValue+"   "+strPosition);
					 	 if(strMFieldValue.equals("") && !strLGFieldValue.equals("")){
							 hsession.setAttribute("fieldName", strFieldsName);
							 hsession.setAttribute("sheetName", strSheetNames[i]);
							 String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));							 
							 return "ReqFields";							 
						 }
					
						 if(strMFieldValue.equals("") && !strLGFieldValue.equals("")){
							 hsession.setAttribute("fieldName", "Marker Name");
							 hsession.setAttribute("sheetName", strSheetNames[i]);
							 String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
							 
							 return "ReqFields";
							
						 }
						
						 if(!strMFieldValue.equals("") && strLGFieldValue.equals("")){
							 hsession.setAttribute("fieldName", "Linkage Group");
							 hsession.setAttribute("sheetName", strSheetNames[i]);
							 String strColName = escn.getColumnName(sName.getCell(1, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(1, j).getRow()+1));
							 return "ReqFields";										
							 
						 }
						 
						 if(!strMFieldValue.equals("") && strPosition.equals("")){
							 hsession.setAttribute("fieldName", "Position");
							 hsession.setAttribute("sheetName", strSheetNames[i]);
							 String strColName = escn.getColumnName(sName.getCell(2, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(2, j).getRow()+1));							 
							 return "ReqFields";
							 
						 }
					
						 if(strMFieldValue.equals("") && strLGFieldValue.equals("") && strPosition.equals("")){
							 String strRowNumber = String.valueOf(sName.getCell(1, j).getRow()+1);								 
							 String ErrMsg = "There is an empty row at position "+strRowNumber+".\nPlease delete it.";
							 hsession.setAttribute("indErrMsg", ErrMsg);
							 return "ErrMsg";
						 }							 
						
					 }
					 
				 }
					 
			}
		}else if(type.equalsIgnoreCase("DArt")){	
			lstSheetNames.add("dart_source");
			lstSheetNames.add("dart_data");
			lstSheetNames.add("dart_gids");
			for (int i=0;i<strSheetNames.length;i++){
				 String strSN = strSheetNames[i];
				 System.out.println("strSN=:"+strSN);
				 if(lstSheetNames.contains(strSN.toLowerCase())){
					 if(!lSN.contains(strSN))
						 lSN.add(strSN);
				 }	
			 }
			 
			 if(lstSheetNames.size()!=lSN.size())
				 return "SheetNameNotFound";
							
			 //check the template fields
			 for(int i=0;i<strSheetNames.length;i++){
				 String strSName = strSheetNames[i].toString();
				 if(strSName.equalsIgnoreCase("DArT_Source")){
					 Sheet sName = workbook.getSheet(strSName);
					
					 //String strTempColumnNames[] = {"Institute","Principle investigator","Email contact","Dataset description","Genus","Species","Version","Creation Date","Remark"};
					 String strTempColumnNames[] = {"Institute","Principle investigator","Dataset Name","Dataset description","Genus","Species","Remark"};
									
					 for(int j=0;j<strTempColumnNames.length;j++){
						 String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
										
						 if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 //System.out.println("TTTTTTTT="+strMFieldNames);
							 //System.out.println("TTTTTTTT="+strTempColumnNames[j]);
							 hsession.setAttribute("colMsg1", strTempColumnNames[j]);
							 hsession.setAttribute("sheetName", strSName);
							 //System.out.println("SSR Source");
							 return "ColumnNameNotFound";
						 }
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
							 hsession.setAttribute("sheetName", strSName);
							 return "DelEmptyRows";
						 }
					 }															
				 }
				 
				 if(strSName.equalsIgnoreCase("DArT_Data")){
					 Sheet sName = workbook.getSheet(strSName);
					 int intNoOfRows = sName.getRows();
					 int intNoOfCols = sName.getColumns();
					 String strTempColumnNames[] = {"CloneID","MarkerName","Q","Reproducibility","Call Rate","PIC","Discordance"};					 
					 for(int j=0;j<strTempColumnNames.length;j++){
						 String strMFieldNames = (String)sName.getCell(j, 0).getContents().trim();
						 if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 hsession.setAttribute("colMsg1", strTempColumnNames[j]);
							 hsession.setAttribute("sheetName", strSName);
							 return "ColumnNameNotFound";
						 }
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(j, 0).getColumn());
							 hsession.setAttribute("colposition", strColName);
							 hsession.setAttribute("sheetName", strSName);
							 return "DelEmptyColumns";
						 }
					 }
					 for(int c=0;c<6;c++){
						 for(int r=1;r<intNoOfRows;r++){
							 String value=(String)sName.getCell(c, r).getContents().trim();
							 if(value==null || value==""){
								 String strRowNumber = String.valueOf(sName.getCell(c, r).getRow()+1);	
								 String strColumnName = escn.getColumnName(sName.getCell(c, r).getColumn());	
								 String ErrMsg = "This cell is empty at position "+strColumnName+strRowNumber+".";
								 hsession.setAttribute("indErrMsg", ErrMsg);
								 return "ErrMsg";
							 }
						 }
					 }
					 for(int c=7;c<intNoOfCols;c++){
						 for(int r=0;r<intNoOfRows;r++){
							 String value=(String)sName.getCell(c, r).getContents().trim();
							 if(value==null || value==""){
								 String strRowNumber = String.valueOf(sName.getCell(c, r).getRow()+1);	
								 String strColumnName = escn.getColumnName(sName.getCell(c, r).getColumn());	
								 String ErrMsg = "This cell is empty at position "+strColumnName+strRowNumber+".";
								 hsession.setAttribute("indErrMsg", ErrMsg);
								 return "ErrMsg";
							 }
						 }
					 }				 
				 }
				 
				 
			 }
		}else if(type.equalsIgnoreCase("mapping")){	
			lstSheetNames.add("mapping_source");
			lstSheetNames.add("mapping_datalist");
			
			for (int i=0;i<strSheetNames.length;i++){
				 String strSN = strSheetNames[i];
				 System.out.println("strSN=:"+strSN);
				 if(lstSheetNames.contains(strSN.toLowerCase())){
					 if(!lSN.contains(strSN))
						 lSN.add(strSN);
				 }	
			 }
			 
			 if(lstSheetNames.size()!=lSN.size())
				 return "SheetNameNotFound";
							
			 //check the template fields
			 for(int i=0;i<strSheetNames.length;i++){
				 String strSName = strSheetNames[i].toString();
				 if(strSName.equalsIgnoreCase("Mapping_Source")){
					 Sheet sName = workbook.getSheet(strSName);
					 String strTempColumnNames[] = {"Institute","Principle investigator","Email contact","Dataset Name","Dataset description","Genus","Species","Population ID","Parent A GID","Parent A","Parent B GID","Parent B","Population Size","Population Type","Purpose of the study","Scoring Scheme","Missing Data","Creation Date","Remark"};

					 for(int j=0;j<strTempColumnNames.length;j++){
						 String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
										
						 if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
							 hsession.setAttribute("colMsg", strMFieldNames);
							 //System.out.println("TTTTTTTT="+strMFieldNames);
							 //System.out.println("TTTTTTTT="+strTempColumnNames[j]);
							 hsession.setAttribute("colMsg1", strTempColumnNames[j]);
							 hsession.setAttribute("sheetName", strSName);
							 //System.out.println("SSR Source");
							 return "ColumnNameNotFound";
						 }
						 if(strMFieldNames==null || strMFieldNames==""){
							 String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
							 hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
							 hsession.setAttribute("sheetName", strSName);
							 return "DelEmptyRows";
						 }
					 }															
				 }		 
				 
			 }
			//check the required fields in Mapping_Source;
			 for(int i=0;i<strSheetNames.length;i++){
				 if(strSheetNames[i].equalsIgnoreCase("Mapping_Source")){
					 Sheet sName = workbook.getSheet(i);
					 int intNoOfRows = sName.getRows();
					 for(int j=0;j<intNoOfRows;j++){
						 String strFieldsName = sName.getCell(0, j).getContents().trim();
						 //required fields institute, species, genus, creation date in ssr_source.
						 //if(strFieldsName.equalsIgnoreCase("institute") || strFieldsName.equalsIgnoreCase("Dataset description") || strFieldsName.equalsIgnoreCase("genus") || strFieldsName.equalsIgnoreCase("creation date")){
						 if(strFieldsName.equalsIgnoreCase("institute")|| strFieldsName.equalsIgnoreCase("Principle investigator")|| strFieldsName.equalsIgnoreCase("Dataset Name") || strFieldsName.equalsIgnoreCase("Dataset description") || strFieldsName.equalsIgnoreCase("genus")|| strFieldsName.equalsIgnoreCase("species")|| strFieldsName.equalsIgnoreCase("Population ID")|| strFieldsName.equalsIgnoreCase("Parent A GID")|| strFieldsName.equalsIgnoreCase("Parent A")|| strFieldsName.equalsIgnoreCase("Parent B GID")|| strFieldsName.equalsIgnoreCase("Parent B")|| strFieldsName.equalsIgnoreCase("Purpose of the study")|| strFieldsName.equalsIgnoreCase("Creation Date") || strFieldsName.equalsIgnoreCase("missing data")){
							 String strFieldValue = sName.getCell(1, j).getContents().trim();
							 if(strFieldValue == null || strFieldValue == ""){
								 //hsession.setAttribute("dlength", strFieldsName);
								 //hsession.setAttribute("colposition", strColName+(sName.getCell(k,j).getRow()+1));
								 //return "ivSpecies";
								 hsession.setAttribute("fieldName", strFieldsName);
								 hsession.setAttribute("sheetName", strSheetNames[i]);
								 String strColName = escn.getColumnName(sName.getCell(1, j).getColumn());
								 hsession.setAttribute("colposition", strColName+(sName.getCell(1,j).getRow()+1));
								 return "ReqFields";
							 }
						 }
					 }
				 }
			 }
		}
		return strValid;
	}

}
