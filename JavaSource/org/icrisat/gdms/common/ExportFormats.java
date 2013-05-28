package org.icrisat.gdms.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class ExportFormats {
	
	ArrayList alleleList=new ArrayList();
	ArrayList accList=new ArrayList(); 
	ArrayList markList=new ArrayList();		
	int countofacc=1;
	String temp="",temp1="";
	ArrayList excelAccList=new ArrayList();
	ArrayList excelAccList1=new ArrayList();
	ArrayList dataidList=new ArrayList();
	
	

//	 To write matrix 
	public void Matrix(ArrayList a,String filePath,HttpServletRequest req, ArrayList accList, ArrayList markList, Map gMap){
		
		int columns=2;
		int row=1;
		String MarkernameId="";
		String previousMarkerId="";			
		String MarkerIdNameList="";
		String markerId="";
		
		try{
			//System.out.println("****************  EXPORT FORMATS CLASS  *****************");
			WritableWorkbook workbook=Workbook.createWorkbook(new File(filePath+"/jsp/analysisfiles/matrix"+(String)req.getSession().getAttribute("msec")+".xls"));
			WritableSheet sheet=workbook.createSheet("DataSheet",0);
			
			int accIndex=1,markerIndex=2;
			int i;
			
			/*System.out.println("accList="+accList);
			System.out.println("markList="+markList);*/
			//System.out.println("gMap="+gMap);
			req.getSession().setAttribute("mCount", markList.size());
			req.getSession().setAttribute("genCount", accList.size());
			int gid=0;
			String gname="";
			
			int noOfAccs=accList.size();
			int noOfMarkers=markList.size();
			
			Label l=new Label(0,0," ");
			sheet.addCell(l);
			
//			 To write accessions
			for(i=0;i<noOfAccs;i++){
				Iterator iterator = gMap.keySet().iterator();
				gid=Integer.parseInt(accList.get(i).toString());
				while (iterator.hasNext()){
	        	   Object key = iterator.next();
	        	   if(key.equals(gid)){
	        		   gname=gMap.get(key).toString();
	        	   }
				 }	
				l=new Label(0,accIndex++,gid+"");
				sheet.addCell(l);
				accIndex--;
				l=new Label(1,accIndex++,gname);
				sheet.addCell(l);
			}
			
			//To write markers
			for(i=0;i<noOfMarkers;i++){					
				l=new Label(markerIndex++,0,(String)markList.get(i));
				sheet.addCell(l);				
			}			
			MarkerIdNameList=MarkerNameIdList(markList);
			row=0;
			String[] AllelesList=null;
			for(int a1=0;a1<a.size();a1++){
				AllelesList=a.get(a1).toString().split(",");
				//System.out.println("(AllelesList[1].toString()).equals(previousMarkerId)"+AllelesList[1].toString()+ "  ).equals(  "+previousMarkerId);
				if((AllelesList[1].toString()).equals(previousMarkerId)){
					row++;
				}else{
					int totalRows=sheet.getRows();
					for(int ss=0;ss<totalRows;ss++){
						if(sheet.getCell(0,ss).getContents().equals(AllelesList[0].toString())){
							row=ss;
							break;
						}
					}
				}
				markerId=AllelesList[1];
				int firstindex=MarkerIdNameList.indexOf(markerId);

				if(firstindex!=0){
					firstindex=MarkerIdNameList.indexOf("!&&!"+markerId)+4;
				}
				int nextindex=MarkerIdNameList.indexOf("!&&!", firstindex);
				MarkernameId=MarkerIdNameList.substring(firstindex,nextindex);
				int totalcols=sheet.getColumns();

				for(int ss=0;ss<totalcols;ss++){
					if(sheet.getCell(ss,0).getContents().equals(MarkernameId)){
						columns=ss;
						break;
					}
				}	
				String[] allele1=null;
				String allele2=AllelesList[2];
				String allele="";
				if(allele2.contains(":")){
					allele1=allele2.split(":");
					if(allele1[0].equalsIgnoreCase(allele1[1])){
						allele=allele1[0];
					}else{
						allele=allele1[0]+"/"+allele1[1];
					}
						
				}else if(allele2.contains(",")){					
					allele1=allele.split(",");					
					if(allele1[0].equalsIgnoreCase(allele1[1])){						
						allele=allele1[0];
					}else{
						allele=allele1[0]+"/"+allele1[1];
					}
				}else{
					allele=allele2;
				}
				
				l=new Label(columns,row,allele+"");
				sheet.addCell(l);
				columns++;
					
				previousMarkerId=AllelesList[1].toString();
			}
			
			workbook.write();			 
			workbook.close();		
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public String MarkerNameIdList(ArrayList markList){		
		String MarkerIdNameList="";
		for(int i=0;i<markList.size();i++){
			MarkerIdNameList=MarkerIdNameList+markList.get(i)+"!&&!";
		}
	
		return MarkerIdNameList;
	}
	
	
/**	Writing genotyping .dat file for FlapJack */
	public void MatrixDat(ArrayList a, String mapData, String filePath,HttpServletRequest req, ArrayList accList, ArrayList markList, ArrayList qtlData, String expOp, boolean qtlexists, HashMap dataMap, HashMap gList){
		HashMap<String,Object> markerAlleles= new HashMap<String,Object>();
		try{
			boolean condition=false;
			/*System.out.println("List="+a);
			System.out.println(" gListExp="+ accList);
			System.out.println(" mListExp="+ markList);*/
			req.getSession().setAttribute("mCount", markList.size());
			req.getSession().setAttribute("genCount", accList.size());				
			
			int noOfAccs=accList.size();
			int noOfMarkers=markList.size();			
			File fexists=new File(filePath+("//")+"/Flapjack.txt");
			if(fexists.exists()) { fexists.delete(); }
			int accIndex=1,markerIndex=1;
			int i;String chVal="";
			//(String)req.getSession().getAttribute("msec");
			FileWriter flapjackdatstream = new FileWriter(filePath+("//")+"/Flapjack.dat");
			BufferedWriter fjackdat = new BufferedWriter(flapjackdatstream);
			
			for(int m1 = 0; m1< markList.size(); m1++){
				fjackdat.write("\t"+markList.get(m1));
			}
			
			int al=0;
			String finalData="";	
			for (int j=0;j<accList.size();j++){ 
				String arrList6[]=new String[3];
				fjackdat.write("\n"+accList.get(j));
						
			    for (int k=0;k<markList.size();k++){
			    	//System.out.println("**************************  :"+dataMap.get(Integer.parseInt((gList.get(accList.get(j).toString())).toString())));
			    	if(!(expOp.equalsIgnoreCase("gid"))){
				    	markerAlleles=(HashMap)dataMap.get(Integer.parseInt((gList.get(accList.get(j).toString())).toString()));
				    	if(markerAlleles.containsKey(gList.get(accList.get(j).toString()).toString()+"!~!"+markList.get(k).toString())){
							String alleleValue=markerAlleles.get(gList.get(accList.get(j).toString()).toString()+"!~!"+markList.get(k).toString()).toString();
							//System.out.println("k=:"+k +"   "+alleleValue);
							if(alleleValue.contains("/")){
								String[] strAllele=alleleValue.split("/");
								//System.out.println("strAllele[0]="+strAllele[0]+"    strAllele[1]="+strAllele[1]);
								if(strAllele[0].equalsIgnoreCase(strAllele[1]))
									finalData=strAllele[0];
								else
									finalData=strAllele[0]+"/"+strAllele[1];
							}else{
								finalData=alleleValue;
							}
							fjackdat.write("\t"+finalData);
				    		
				    	}else{
				    		fjackdat.write("\t");	
				    	}	
					
					}else{
			    		markerAlleles=(HashMap)dataMap.get(Integer.parseInt(accList.get(j).toString()));
				    	if(markerAlleles.containsKey(accList.get(j).toString()+"!~!"+markList.get(k).toString())){
							//fjackdat.write("\t"+markerAlleles.get(gList.get(accList.get(j).toString()).toString()+"!~!"+markList.get(k).toString()));
							String alleleValue=markerAlleles.get(accList.get(j).toString()+"!~!"+markList.get(k).toString()).toString();
							//System.out.println("k=:"+k +"   "+alleleValue);
							if(alleleValue.contains("/")){
								String[] strAllele=alleleValue.split("/");
								//System.out.println("strAllele[0]="+strAllele[0]+"    strAllele[1]="+strAllele[1]);
								if(strAllele[0].equalsIgnoreCase(strAllele[1]))
									finalData=strAllele[0];
								else
									finalData=strAllele[0]+"/"+strAllele[1];
							}else{
								finalData=alleleValue;
							}
							fjackdat.write("\t"+finalData);
				    		
				    	}else{
				    		fjackdat.write("\t");	
				    	}	
			    	}
			    }		    	
			}
						
			fjackdat.close();			
			
			
						
			/**	writing tab delimited .map file for FlapJack  
			 * 	consisting of marker chromosome & position
			 * 
			 * **/
			
			FileWriter flapjackmapstream = new FileWriter(filePath+("//")+"/Flapjack.map");
			BufferedWriter fjackmap = new BufferedWriter(flapjackmapstream);
			String[] mData=mapData.split("~~!!~~");
			
			for(int m=0;m<mData.length;m++){		
				String[] strMData=mData[m].split("!~!");
				fjackmap.write(strMData[0]);
				fjackmap.write("\t");
				fjackmap.write(strMData[1]);
				fjackmap.write("\t");
				fjackmap.write(strMData[2]);
				fjackmap.write("\n");		
			}
			fjackmap.close();
			
			
			/**	writing tab delimited qtl file for FlapJack  
			 * 	consisting of marker chromosome & position
			 * 
			 * **/
			if(qtlexists){
				FileWriter flapjackQTLstream = new FileWriter(filePath+("//")+"/Flapjack.txt");
				BufferedWriter fjackQTL = new BufferedWriter(flapjackQTLstream);
				//String[] qtlData=qtlData.split("~~!!~~");
				//fjackQTL.write("QTL\tChromosome\tPosition\tMinimum\tMaximum\tTrait\tExperiment\tTrait Group\tLOD\tR2\tfavallele\tFlanking markers in original publication\teffect\tSE additive\tHV Parent\tHV Allele\tLV Parent\tLV Allele");
				fjackQTL.write("QTL\tChromosome\tPosition\tMinimum\tMaximum\tTrait\tExperiment\tTrait Group\tLOD\tR2\tFlanking markers in original publication\tSE additive\tHV Parent\tLV Parent");
				fjackQTL.write("\n");
				for(int q=0;q<qtlData.size();q++){					
					String[] strMData=qtlData.get(q).toString().split("!~!");
					fjackQTL.write(strMData[0]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[1]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[2]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[3]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[4]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[5]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[6]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[7]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[8]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[9]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[11]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[13]);
					fjackQTL.write("\t");					
					fjackQTL.write(strMData[14]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[15]);					
					fjackQTL.write("\n");
					
				}
				fjackQTL.close();
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Writing input file for CMTV
	 * 
	 */
	public void CMTVTxt(ArrayList aList, String filePath, HttpServletRequest req){
		try{
			FileWriter cmtvstream = new FileWriter(filePath+("//")+"/jsp/analysisfiles/"+(String)req.getSession().getAttribute("msec")+"CMTV.txt");
			BufferedWriter cmtvBW = new BufferedWriter(cmtvstream);
			
			for(int a=0;a<aList.size();a++){		
				String[] strData=aList.get(a).toString().split("!~!");
				cmtvBW.write(strData[0]+"\t"+strData[1]+"\t"+strData[2]+"\t"+strData[3]+"\t"+strData[4]+"\n");					
			}
			cmtvBW.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//Matrix for SNP data
	public void MatrixDataSNP(String filePath,HttpServletRequest req, ArrayList accList, ArrayList markList, Map gMap, HashMap dataMap){		
		try{
			
			req.getSession().setAttribute("mCount", markList.size());
			req.getSession().setAttribute("genCount", accList.size());				
			
			int noOfAccs=accList.size();
			int noOfMarkers=markList.size();			
			
			int accIndex=1,markerIndex=1;
			int i;String chVal="";
			HashMap<String,Object> markerAlleles= new HashMap<String,Object>();
			
			FileWriter SNPdatstream = new FileWriter(filePath+("//")+"/jsp/analysisfiles/matrix"+(String)req.getSession().getAttribute("msec")+".txt");
			BufferedWriter SNPMatrix = new BufferedWriter(SNPdatstream);
			SNPMatrix.write("\t");
			for(int m1 = 0; m1< markList.size(); m1++){
				SNPMatrix.write("\t"+markList.get(m1));
			}
			
			//int k=0;
			int gid=0;
			String gname="";		
			
			for (int j=0;j<accList.size();j++){ 
				Iterator iterator = gMap.keySet().iterator();
				String arrList6[];
				gid=Integer.parseInt(accList.get(j).toString());
				 while (iterator.hasNext()){
	        	   Object key = iterator.next();
	        	   if(key.equals(gid)){
	        		   gname=gMap.get(key).toString();
	        	   }
				 }
				SNPMatrix.write("\n"+accList.get(j)+"\t"+gname);		
			    for (int k=0;k<markList.size();k++){
			    	//System.out.println("**************************  :"+dataMap.get(Integer.parseInt(accList.get(j).toString())));
			    	markerAlleles=(HashMap)dataMap.get(Integer.parseInt(accList.get(j).toString()));
			    	List markerKey = new ArrayList();
					markerKey.addAll(markerAlleles.keySet());
					//for(int m=0; m<markerKey.size();m++){
						//markerAlleles.
						if(markerAlleles.containsKey(accList.get(j).toString()+"!~!"+markList.get(k).toString())){
				    		SNPMatrix.write("\t"+markerAlleles.get(accList.get(j).toString()+"!~!"+markList.get(k).toString()));
				    		
				    	}else{
				    		SNPMatrix.write("\t");	
				    	}	
						
					//}
					
			    }		    	
			}					
			SNPMatrix.close();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	 To write matrix for mapping data
	
	public void mapMatrix(ArrayList a,String filePath,HttpServletRequest req, ArrayList accList, ArrayList markList, String parentsList, Map gMap){
		int columns=2;
		int row=0;
		String MarkernameId="";
		String previousMarkerId="";			
		String MarkerIdNameList="";
		String markerId="";
		
		try{
			//System.out.println("****************  EXPORT FORMATS CLASS  *****************");
			WritableWorkbook workbook=Workbook.createWorkbook(new File(filePath+"/jsp/analysisfiles/matrix"+(String)req.getSession().getAttribute("msec")+".xls"));
			WritableSheet sheet=workbook.createSheet("DataSheet",0);
			
			int accIndex=1,markerIndex=2;
			int PmarkerIndex=2;
			int i;
			
			req.getSession().setAttribute("mCount", markList.size());
			req.getSession().setAttribute("genCount", accList.size());
			int gid=0;
			String gname="";
			//System.out.println(".........parents=:"+parentsList);
			//System.out.println(" data list=:"+a);
			//System.out.println("acc list=:"+accList);
			int noOfAccs=accList.size();
			int noOfMarkers=markList.size();
			String[] parents=parentsList.split("!~!");
			String[] parentsToWrite=null;
			Label l=null;
			//sheet.addCell(l);
			int pr=1;
			for(int p1=0;p1<parents.length;p1++){
				parentsToWrite=parents[p1].split(";;");
				l=new Label(0,pr,parentsToWrite[0]+"");
				sheet.addCell(l);
				//accIndex--;
				l=new Label(1,pr,parentsToWrite[1]);
				sheet.addCell(l);
				pr++;
				
			}
			
			/*Label l=new Label(0,0," ");
			sheet.addCell(l);*/
			/*l=new Label(0,3,gid+"");
			sheet.addCell(l);
			//accIndex--;
			l=new Label(1,3,p[0]);
			sheet.addCell(l);
			l=new Label(0,4,gid+"");
			sheet.addCell(l);
			//accIndex--;
			l=new Label(1,4,p[1]);
			sheet.addCell(l);*/
			
			for(i=0;i<noOfMarkers;i++){					
				l=new Label(markerIndex++,0,(String)markList.get(i));
				sheet.addCell(l);				
			}	
			
			
//			 To write accessions
			for(i=0;i<noOfAccs;i++){
				Iterator iterator = gMap.keySet().iterator();
				gid=Integer.parseInt(accList.get(i).toString());
				while (iterator.hasNext()){
	        	   Object key = iterator.next();
	        	   if(key.equals(gid)){
	        		   gname=gMap.get(key).toString();
	        	   }
				 }	
				l=new Label(0,accIndex++,gid+"");
				sheet.addCell(l);
				accIndex--;
				l=new Label(1,accIndex++,gname);
				sheet.addCell(l);
			}
			
			//To write markers
			/*for(i=0;i<noOfMarkers;i++){					
				l=new Label(PmarkerIndex++,1,"A");
				sheet.addCell(l);
				PmarkerIndex--;
				l=new Label(PmarkerIndex++,2,"B");
				sheet.addCell(l);
				
			}*/			
			MarkerIdNameList=MarkerNameIdList(markList);
			row=1;
			String[] AllelesList=null;
			for(int a1=0;a1<a.size();a1++){
				AllelesList=a.get(a1).toString().split(",");
				//System.out.println("(AllelesList[1].toString()).equals(previousMarkerId)"+AllelesList[1].toString()+ "  ).equals(  "+previousMarkerId);
				if((AllelesList[1].toString()).equals(previousMarkerId)){
					row++;
				}else{
					int totalRows=sheet.getRows();
					for(int ss=0;ss<totalRows;ss++){
						if(sheet.getCell(0,ss).getContents().equals(AllelesList[0].toString())){
							row=ss;
							break;
						}
					}
				}
				markerId=AllelesList[1];
				int firstindex=MarkerIdNameList.indexOf(markerId);

				if(firstindex!=0){
					firstindex=MarkerIdNameList.indexOf("!&&!"+markerId)+4;
				}
				int nextindex=MarkerIdNameList.indexOf("!&&!", firstindex);
				MarkernameId=MarkerIdNameList.substring(firstindex,nextindex);
				int totalcols=sheet.getColumns();

				for(int ss=0;ss<totalcols;ss++){
					if(sheet.getCell(ss,0).getContents().equals(MarkernameId)){
						columns=ss;
						break;
					}
				}	
				String[] allele1=null;
				String allele2=AllelesList[2];
				String allele="";
				if(allele2.contains(":")){
					allele1=allele2.split(":");
					if(allele1[0].equalsIgnoreCase(allele1[1])){
						allele=allele1[0];
					}else{
						allele=allele1[0]+"/"+allele1[1];
					}
						
				}else if(allele2.contains(",")){					
					allele1=allele.split(",");					
					if(allele1[0].equalsIgnoreCase(allele1[1])){						
						allele=allele1[0];
					}else{
						allele=allele1[0]+"/"+allele1[1];
					}
				}else{
					allele=allele2;
				}
				
				l=new Label(columns,row,allele+"");
				sheet.addCell(l);
				columns++;
					
				previousMarkerId=AllelesList[1].toString();
			}
			
			workbook.write();			 
			workbook.close();		
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//Matrix for SNP data
	public void MatrixDataSNPDataset(ArrayList a, String filePath,HttpServletRequest req, ArrayList accList, ArrayList markList, Map gMap){		
		try{
					/*System.out.println("...a;:"+a);
					System.out.println("   accList:"+accList);*/
			req.getSession().setAttribute("mCount", markList.size());
			req.getSession().setAttribute("genCount", accList.size());				
			
			int noOfAccs=accList.size();
			int noOfMarkers=markList.size();			
			
			int accIndex=1,markerIndex=1;
			int i;String chVal="";

			FileWriter SNPdatstream = new FileWriter(filePath+("//")+"/jsp/analysisfiles/matrix"+(String)req.getSession().getAttribute("msec")+".txt");
			BufferedWriter SNPMatrix = new BufferedWriter(SNPdatstream);
			SNPMatrix.write("\t");
			for(int m1 = 0; m1< markList.size(); m1++){
				SNPMatrix.write("\t"+markList.get(m1));
			}
			
			int al=0;
			int gid=0;
			String gname="";		
					
			for (int j=0;j<accList.size();j++){ 
				Iterator iterator = gMap.keySet().iterator();
				String arrList6[];
				gid=Integer.parseInt(accList.get(j).toString());
				 while (iterator.hasNext()){
		        	   Object key = iterator.next();
		        	   if(key.equals(gid)){
		        		   gname=gMap.get(key).toString();
		        	   }
				 }
				SNPMatrix.write("\n"+accList.get(j)+"\t"+gname);		
			    for (int k=0;k<markList.size();k++){
				   String strList5=a.get(al).toString();
				   // String[] arrList6=strList5.split(",");
				   StringTokenizer stz = new StringTokenizer(strList5.toString(), ",");
					 //System.out.println("stz.countTokens()="+stz.countTokens());
				   arrList6 = new String[stz.countTokens()];
				   int i1=0;
				  
				   while(stz.hasMoreTokens()){
					   arrList6[i1] = stz.nextToken();
					   i1++;
				   }
				   //arrList6=strList5.split(",");
				   //System.out.println(arrList6[0]+".equals(   "+accList.get(j)+"  )) &&   "+arrList6[1]+"   .equals(  "+markList.get(k));
				   if((Integer.parseInt(arrList6[0])==Integer.parseInt(accList.get(j).toString())) && arrList6[1].equals(markList.get(k))){	
					   //System.out.println("..........  if condition   ");
					   /*if(arrList6[2].contains(":")){
						   String[] ChVal1=arrList6[2].split(":");
						   if(ChVal1[0].equalsIgnoreCase(ChVal1[1])){
							   chVal=ChVal1[0];
						   }else{
							   chVal=ChVal1[0]+"/"+ChVal1[1];
						   }					
						}else{*/
							chVal=arrList6[2];
						//}
					   //System.out.println(chVal);
					   SNPMatrix.write("\t"+chVal);	
					  
					   
					}else{
						//System.out.println("..........  elwse condition   ");
						SNPMatrix.write("\t");
					}
				   al++;
			    }		    	
			}						
			SNPMatrix.close();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**	Writing genotyping .dat file for FlapJack */
	public void FlapjackDat(ArrayList a, String mapData, String filePath,HttpServletRequest req, ArrayList accList, ArrayList markList, ArrayList qtlData, String expOp, boolean qtlexists){
		
		try{
			boolean condition=false;
			/*System.out.println("List="+a);
			System.out.println(" gListExp="+ accList);
			System.out.println(" mListExp="+ markList);*/
			req.getSession().setAttribute("mCount", markList.size());
			req.getSession().setAttribute("genCount", accList.size());				
			
			int noOfAccs=accList.size();
			int noOfMarkers=markList.size();			
			File fexists=new File(filePath+("//")+"/Flapjack.txt");
			if(fexists.exists()) { fexists.delete(); }
			int accIndex=1,markerIndex=1;
			int i;String chVal="";
			//(String)req.getSession().getAttribute("msec");
			FileWriter flapjackdatstream = new FileWriter(filePath+("//")+"/Flapjack.dat");
			BufferedWriter fjackdat = new BufferedWriter(flapjackdatstream);
			
			for(int m1 = 0; m1< markList.size(); m1++){
				fjackdat.write("\t"+markList.get(m1));
			}
			
			int al=0;
				/*for(int s=0; s<a.size();s++){
					System.out.println(s+"   "+a.get(s));
				}*/
			for (int j=0;j<accList.size();j++){ 
				String arrList6[]=new String[3];
				fjackdat.write("\n"+accList.get(j));		
			    for (int k=0;k<markList.size();k++){
			    	if(al<a.size()){
			    		//System.out.println("al="+al+"    "+a.get(al));
			    		String strList5=a.get(al).toString();
			    		// String[] arrList6=strList5.split(",");
			    		 //System.out.println(k+":"+strList5);
			    		StringTokenizer stz = new StringTokenizer(strList5.toString(), ",");
			    		//arrList6 = new String[stz.countTokens()];
			    		int i1=0;				  
			    		while(stz.hasMoreTokens()){
			    			arrList6[i1] = stz.nextToken();
			    			i1++;
			    		}
			    		if(expOp.equalsIgnoreCase("gids"))
			    			condition=((Integer.parseInt(arrList6[0])==Integer.parseInt(accList.get(j).toString())) && arrList6[1].equals(markList.get(k)));
			    		else
			    			condition=((arrList6[0].equalsIgnoreCase(accList.get(j).toString().toLowerCase())) && arrList6[1].equals(markList.get(k)));
					  // System.out.println("arrList6[0]"+arrList6[0]+".equals(accList.get(j))"+accList.get(j)+"&& arrList6[1]"+arrList6[1]+".equals(markList.get(k))"+markList.get(k));
					   //if((arrList6[0].equals(accList.get(j))) && arrList6[1].equals(markList.get(k))){
					   //if((Integer.parseInt(arrList6[0])==Integer.parseInt(accList.get(j).toString())) && arrList6[1].equals(markList.get(k))){
			    		if(condition){
			    			if(arrList6[2].contains("/")){								
								String[] ChVal1=arrList6[2].split("/");
								if(ChVal1[0].equalsIgnoreCase(ChVal1[1])){
									chVal=ChVal1[0];
								}else{
									chVal=ChVal1[0]+"/"+ChVal1[1];
								}
			    			}else if(arrList6[2].contains(":")){								
								String[] ChVal1=arrList6[2].split(":");
								if(ChVal1[0].equalsIgnoreCase(ChVal1[1])){
									chVal=ChVal1[0];
								}else{
									chVal=ChVal1[0]+"/"+ChVal1[1];
								}
							}else{
								chVal=arrList6[2];
							}
							fjackdat.write("\t"+chVal);	
							
						   
						}else{
							fjackdat.write("\t");
						}
					   al++;
			    	}
			      }
		    	
		     }
						
			fjackdat.close();			
			
			
						
			/**	writing tab delimited .map file for FlapJack  
			 * 	consisting of marker chromosome & position
			 * 
			 * **/
			
			FileWriter flapjackmapstream = new FileWriter(filePath+("//")+"/Flapjack.map");
			BufferedWriter fjackmap = new BufferedWriter(flapjackmapstream);
			String[] mData=mapData.split("~~!!~~");
			
			for(int m=0;m<mData.length;m++){		
				String[] strMData=mData[m].split("!~!");
				fjackmap.write(strMData[0]);
				fjackmap.write("\t");
				fjackmap.write(strMData[1]);
				fjackmap.write("\t");
				fjackmap.write(strMData[2]);
				fjackmap.write("\n");		
			}
			fjackmap.close();
			
			
			/**	writing tab delimited qtl file for FlapJack  
			 * 	consisting of marker chromosome & position
			 * 
			 * **/
			if(qtlexists){
				FileWriter flapjackQTLstream = new FileWriter(filePath+("//")+"/Flapjack.txt");
				BufferedWriter fjackQTL = new BufferedWriter(flapjackQTLstream);
				//String[] qtlData=qtlData.split("~~!!~~");
				//fjackQTL.write("QTL\tChromosome\tPosition\tMinimum\tMaximum\tTrait\tExperiment\tTrait Group\tLOD\tR2\tfavallele\tFlanking markers in original publication\teffect\tSE additive\tHV Parent\tHV Allele\tLV Parent\tLV Allele");
				fjackQTL.write("QTL\tChromosome\tPosition\tMinimum\tMaximum\tTrait\tExperiment\tTrait Group\tLOD\tR2\tFlanking markers in original publication\tSE additive\tHV Parent\tLV Parent");
				fjackQTL.write("\n");
				
				for(int q=0;q<qtlData.size();q++){					
					String[] strMData=qtlData.get(q).toString().split("!~!");
					//System.out.println("strMData.size=:"+strMData.length);
					fjackQTL.write(strMData[0]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[1]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[2]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[3]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[4]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[5]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[6]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[7]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[8]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[9]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[11]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[13]);
					fjackQTL.write("\t");					
					fjackQTL.write(strMData[14]);
					fjackQTL.write("\t");
					fjackQTL.write(strMData[15]);					
					fjackQTL.write("\n");					
				}
				fjackQTL.close();
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
