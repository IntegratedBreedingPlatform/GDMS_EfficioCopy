package org.icrisat.gdms.upload;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Sheet;
import jxl.Workbook;

import org.icrisat.gdms.upload.ExcelSheetColumnName;

public class GenotypeMappingExcelFileValidations {

	String strValid = "valid";
	
	//Marker Template validations
		public String validation(Workbook workbook, HttpServletRequest request){
			 HttpSession hsession = request.getSession(true);
			 CheckNumericDatatype cnd = new CheckNumericDatatype();
			// String mspStd = (String)request.getAttribute("genotypemap");
			 ExcelSheetColumnName escn =  new ExcelSheetColumnName();
			String[] strSheetNames=workbook.getSheetNames();
//			/Sheet Names display
			
							List<String> lSN = new ArrayList<String>();
							List<String> lstSheetNames = new ArrayList<String>();
							
							lstSheetNames.add("ssr_source");
							//if(mspStd.equals("map"))
//							lstSheetNames.add("ssr_map study");
//							lstSheetNames.add("ssr_experiment");
//							lstSheetNames.add("ssr_conditions");
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
									
									String strTempColumnNames[] = {"Institute","Principle investigator","Dataset description","Genus","Species","Missing Data","Remark"};
									
									for(int j=0;j<strTempColumnNames.length;j++){
										String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
										
										System.out.println("strMFieldNames="+strMFieldNames);
										if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
											System.out.println("IF NOT Loop");
											hsession.setAttribute("colMsg", strMFieldNames);
											hsession.setAttribute("colMsg1", strTempColumnNames[j]);
											hsession.setAttribute("sheetName", strSName);
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
								
								
							//ssr_map study fields validation
//								if(strSName.equalsIgnoreCase("Map Study")){
//									Sheet sName = workbook.getSheet(strSName);
//									
//									String strTempColumnNames[] = {"Population ID","Parent A","Parent B","Population Size","Population Type","Purpose of the study","Scoring Scheme","Remark"};
//									
//									for(int j=0;j<strTempColumnNames.length;j++){
//										String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
//											if(strMFieldNames.equalsIgnoreCase(""))
//												strMFieldNames="Empty";
//										if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
//											hsession.setAttribute("colMsg", strMFieldNames);
//											hsession.setAttribute("colMsg1", strTempColumnNames[j]);
//											hsession.setAttribute("sheetName", strSName);
//											return "ColumnNameNotFound";
//										}
//										if(strMFieldNames==null || strMFieldNames==""){
//											String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
//											hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
//											hsession.setAttribute("sheetName", strSName);
//											return "DelEmptyRows";
//										}
//									}
//								}
//								
//								
//								
//							//ssr_experiment fields validation
//								if(strSName.equalsIgnoreCase("SSR_Experiment")){
//									Sheet sName = workbook.getSheet(strSName);
//									
//									String strTempColumnNames[] = {"Operational Taxonomic Unit","Purpose of the study","Missing Data"};
//									
//									for(int j=0;j<strTempColumnNames.length;j++){
//										String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
//										
//										if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
//											hsession.setAttribute("colMsg", strMFieldNames);
//											hsession.setAttribute("colMsg1", strTempColumnNames[j]);
//											hsession.setAttribute("sheetName", strSName);
//											return "ColumnNameNotFound";
//										}
//										if(strMFieldNames==null || strMFieldNames==""){
//											String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
//											hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
//											hsession.setAttribute("sheetName", strSName);
//											return "DelEmptyRows";
//										}
//									}
//								}
////								SSR_Conditions fields validation
//								if(strSName.equalsIgnoreCase("SSR_Conditions")){
//									Sheet sName = workbook.getSheet(strSName);
//									
//									String strTempColumnNames[] = {"Sampling strategy","Control genotypes","Size Standard","DNA extraction","DNA amplification and detection","Genotyping Software","Quality Measure","Reference"};
//									
//									for(int j=0;j<strTempColumnNames.length;j++){
//										String strMFieldNames = (String)sName.getCell(0, j).getContents().trim();
//										
//										if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
//											hsession.setAttribute("colMsg", strMFieldNames);
//											hsession.setAttribute("colMsg1", strSName);
//											hsession.setAttribute("sheetName", strSName);
//											return "ColumnNameNotFound";
//										}
//										if(strMFieldNames==null || strMFieldNames==""){
//											String strColName = escn.getColumnName(sName.getCell(0, j).getColumn());
//											hsession.setAttribute("colposition", strColName+(sName.getCell(0, j).getRow()+1));
//											hsession.setAttribute("sheetName", strSName);
//											return "DelEmptyRows";
//										}
//									}
//								}
								//SSR_DataList fields validation
//								if(strSName.equalsIgnoreCase("SSR_Data List")){
//									Sheet sName = workbook.getSheet(strSName);
//									int ColCount = sName.getColumns();
//																	
//									String strTempColumnNames[] = {"Alias","Cross","Line"};
//									
//									for(int j=0;j<strTempColumnNames.length;j++){
//										String strMFieldNames = (String)sName.getCell(j, 0).getContents().trim();
//										
//										if(!strTempColumnNames[j].toLowerCase().contains(strMFieldNames.toLowerCase())){
//											System.out.println("IF NOT Loop   in DATA LIST");
//											hsession.setAttribute("colMsg", strMFieldNames);
//											hsession.setAttribute("colMsg1", strTempColumnNames[j]);
//											hsession.setAttribute("sheetName", strSName);
//											return "ColumnNameNotFound";
//										}
//										if(strMFieldNames==null || strMFieldNames==""){
//											String strColName = escn.getColumnName(sName.getCell(j, 0).getColumn());
//											hsession.setAttribute("colposition", strColName);
//											hsession.setAttribute("sheetName", strSName);
//											return "DelEmptyColumns";
//										}
//									}
//									
//									
//								}
								
								
								
								
							}
							
							
							
	//check the required fields in SSR_Source;
					for(int i=0;i<strSheetNames.length;i++){
								
								if(strSheetNames[i].equalsIgnoreCase("SSR_Source")){
									Sheet sName = workbook.getSheet(i);
									int intNoOfRows = sName.getRows();
									for(int j=0;j<intNoOfRows;j++){
										String strFieldsName = sName.getCell(0, j).getContents().trim();
										
										//required fields institute, species, genus, creation date in ssr_source.
										if(strFieldsName.equalsIgnoreCase("institute") || strFieldsName.equalsIgnoreCase("Dataset description") || strFieldsName.equalsIgnoreCase("genus") || strFieldsName.equalsIgnoreCase("creation date")){
												String strFieldValue = sName.getCell(1, j).getContents().trim();
												if(strFieldValue == null || strFieldValue == ""){
													hsession.setAttribute("fieldName", strFieldsName);
													hsession.setAttribute("sheetName", strSheetNames[i]);
													String strColName = escn.getColumnName(sName.getCell(1, j).getColumn());
													hsession.setAttribute("colposition", strColName+(sName.getCell(1,j).getRow()+1));
													return "ReqFields";
												}
										}
									}
								}
								
//								//ssr_map study required fields
//								if(strSheetNames[i].equalsIgnoreCase("SSR_Map Study")){
//									Sheet sName = workbook.getSheet(i);
//									int intNoOfRows = sName.getRows();
//									for(int j=0;j<intNoOfRows;j++){
//										String strFieldsName = sName.getCell(0, j).getContents().trim();
//										
//										//required fields institute, species, genus, creation date in ssr_source.
//										if(strFieldsName.equalsIgnoreCase("Population ID")){
//												String strFieldValue = sName.getCell(1, j).getContents().trim();
//												if(strFieldValue == null || strFieldValue == ""){
//													
//													hsession.setAttribute("fieldName", strFieldsName);
//													hsession.setAttribute("sheetName", strSheetNames[i]);
//													String strColName = escn.getColumnName(sName.getCell(1, j).getColumn());
//													hsession.setAttribute("colposition", strColName+(sName.getCell(1,j).getRow()+1));
//													return "ReqFields";
//												}
//										}
//									}
//								}
//								
//								
////								purpose of the study from ssr_experiment.
//								if(strSheetNames[i].equalsIgnoreCase("SSR_Experiment")){
//									Sheet sName = workbook.getSheet(i);
//									int intNoOfRows = sName.getRows();
//									for(int j=0;j<intNoOfRows;j++){
//										String strFieldsName = sName.getCell(0, j).getContents().trim();
//										//purpose of the study from ssr_experiment.
//										if(strFieldsName.equalsIgnoreCase("Purpose of the study")){
//											String strFieldValue = sName.getCell(1, j).getContents().trim();
//											
//											if(strFieldValue == null || strFieldValue == ""){
//												//return "ivstudy";
//												hsession.setAttribute("fieldName", strFieldsName);
//												hsession.setAttribute("sheetName", strSheetNames[i]);
//												String strColName = escn.getColumnName(sName.getCell(1, j).getColumn());
//												hsession.setAttribute("colposition", strColName+(sName.getCell(1,j).getRow()+1));
//												return "ReqFields";
//											}
//										}
//											
//									}
//								}
								//check the mandatory fields in SSR_Data List;
								if(strSheetNames[i].equalsIgnoreCase("SSR_Data List")){
									Sheet sName = workbook.getSheet(i);
									int intNoOfRows = sName.getRows();
									int intNoOfCols = sName.getColumns();
									int colCount = 0;
									System.out.println("nofofrows="+intNoOfRows);
									//Row count in ssr_data list sheet
									int intRowCount=0;
									for(int t=0;t<intNoOfRows;t++){
										String strCross = sName.getCell(1, t).getContents().trim();
										String strLine = sName.getCell(2, t).getContents().trim();
										
										//skip the row which contains all the null values
										if(!strCross.equals("") && !strLine.equals("")){
											intRowCount++;
										}
										
									}
									
									
									for(int j=1;j<intRowCount;j++){
										
										for(int k=0;k<intNoOfCols;k++){
											String ColName = sName.getCell(k,0).getContents().trim();
											//System.out.println("ConName="+ColName);
											if(ColName == "" || ColName ==null)
												ColName = "test~``";
											String fValue = sName.getCell(k, j).getContents().trim();
											if(!ColName.equals("test~``") && !ColName.equalsIgnoreCase("Alias")){
												
												if(fValue.equals("")){
													hsession.setAttribute("fieldName", ColName);
													hsession.setAttribute("sheetName", sName.getName());
													String strColName = escn.getColumnName(sName.getCell(k, j).getColumn());
													//hsession.setAttribute("markername", strMarkerName);
													hsession.setAttribute("colposition", strColName+(sName.getCell(k, j).getRow()+1));
												
													//return "DataManFields";
													return "ReqFields";
												}
												
											}
											//System.out.println("fValue="+fValue);
											if(!fValue.equals("") && ColName.equals("test~``")){
												//hsession.setAttribute("fieldName", ColName);
												hsession.setAttribute("sheetName", sName.getName());
												String strColName = escn.getColumnName(sName.getCell(k, 0).getColumn());
												hsession.setAttribute("colposition", strColName+(sName.getCell(k, 0).getRow()+1));
												return "FieldName";
												//String ErrMsg = "Property field value of Variates should not be repeated in Factors and Variates sheet";
												//hsession.setAttribute("indErrMsg", ErrMsg);
												
												//return "ErrMsg";
											}
												
										
										}
										
									}
								} // end Data List validation
								
								
//								check the field length
									if(strSheetNames[i].equalsIgnoreCase("SSR_Source")){
										Sheet sName = workbook.getSheet(strSheetNames[i]);
										String strLableVal = (String)sName.getCell(1, 3).getContents().trim();
												String strLable = (String)sName.getCell(0, 3).getContents().trim();
												int len = strLableVal.length();
												
												if(len>76){
														hsession.setAttribute("fieldName", strLable);
														hsession.setAttribute("sheetName", strSheetNames[i]);
														return "FieldLength";
													}
													
													
													
								 }
								
							}
							
			
			return strValid;
		}


	
}
