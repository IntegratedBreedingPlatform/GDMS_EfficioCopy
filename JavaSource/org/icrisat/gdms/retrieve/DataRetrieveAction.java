package org.icrisat.gdms.retrieve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;
import org.hibernate.Query;
import org.icrisat.gdms.common.FileUploadToServer;

public class DataRetrieveAction extends Action{
	
	Connection con;
	Connection conn;
	
	String str="";
	String strVal="";
	String str1="";
	String type="";
	ArrayList  markerList=new ArrayList();
	ArrayList genotypesList=new ArrayList();
	ArrayList mapList=new ArrayList();
	ArrayList mList=new ArrayList();
	String gids="";
	String upRes="";
	String chValues="";
	ResultSet rsDet=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		ActionErrors ae = new ActionErrors();	
		if(session!=null){
			session.removeAttribute("strdata");			
		}
		// TODO Auto-generated method stub
		
		//String crop=req.getSession().getAttribute("crop").toString();
		
		DynaActionForm df = (DynaActionForm) af;
		
		String retrieveOP=(String)df.get("retrieveOP");	
		System.out.println(".................................:"+retrieveOP);
		float distance=0;
		int mCount=0;
		String mapData="";
		ArrayList CD=new ArrayList();
		ArrayList dist=new ArrayList();
		List listValues=null;
		Query query=null;
		String exp="";
		//Query query=null;	
		Query query1=null;
		Query query2=null;
		ManagerFactory factory = null;
		try{
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			ResultSet rs=null;
			ResultSet rs1=null;
			ResultSet rsMp=null;
			Statement stmtMp=con.createStatement();
			Statement stmt=con.createStatement();
			Statement stmtR=con.createStatement();
			ResultSet rsN=null;
			Statement stmtN=con.createStatement();
			Statement stA=con.createStatement();
			Statement stC=con.createStatement();
			Statement stM=con.createStatement();
			Statement stmtC=con.createStatement();
			Statement stmtA=con.createStatement();
			Statement stmtM=con.createStatement();
			Properties p=new Properties();
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("localhost", "3306", "ivis", "root", "root");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("localhost", "3306", "ibdb_ivis", "root", "root");*/
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			*/
//			factory = new ManagerFactory(local, central);
			factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GermplasmDataManager manager = factory.getGermplasmDataManager();
			
			GenotypicDataManager gdms=factory.getGenotypicDataManager();
			//System.out.println("maps     ====  :"+gdms.getAllMaps(0, 5, Database.LOCAL));
			
		
			if(retrieveOP.equalsIgnoreCase("first")){
				if(session!=null){
					session.removeAttribute("indErrMsg");	
				}
				/*rs=stmt.executeQuery("select distinct map_name from gdms_map");
				while(rs.next()){
					mapList.add(rs.getString(1));
				}
				//query2=hsession.createQuery("select distinct map_name from MapBean");
				//mapList=query2.list();
				session.setAttribute("mapList", mapList);*/
				str="qtlPage";
			}else if(retrieveOP.equalsIgnoreCase("Submit")){
				/** for retrieving polymorphic markers between 2 lines **/
				if(session!=null){
					session.removeAttribute("MissingData");		
					session.removeAttribute("map_data");	
					session.removeAttribute("recCount");	
					session.removeAttribute("missingCount");	
				}
				String gids="";
				String gids1="";
				chValues="";
				List<String> linesList=new ArrayList<String>();
				String line1=(String)df.get("linesO");
				String line2=(String)df.get("linesT");
				String selLines="'"+line1+"' & '"+line2+"'";
				strVal="'"+line1+"','"+line2+"'";	
				linesList.add(line1);
				linesList.add(line2);
				session.setAttribute("selLines", selLines);
				ArrayList finalList=new ArrayList();
				String[] str1=null;
				ArrayList geno1=new ArrayList();
				ArrayList geno2=new ArrayList();
				ArrayList mark1=new ArrayList();
				ArrayList mark2=new ArrayList();
				ArrayList ch1=new ArrayList();
				ArrayList ch2=new ArrayList();
				ArrayList missingList=new ArrayList();
				int alleleCount=0;
				int charCount=0;
				int mapCharCount=0;
				Statement stmtMap=con.createStatement();
				/**
				 * Query for retrieving the gid of the respective germplasm_name using middleware
				 */			
				List<GidNidElement> results = manager.getGidAndNidByGermplasmNames(linesList);
				//System.out.println("RESULTS (getGidAndNidByGermplasmNames): " + results);
				for(int r=0;r<results.size();r++){
					gids1=gids1+results.get(r).getGermplasmId()+",";					
				}
				/*rs = stmt.executeQuery("select distinct gid from names where nval in ("+strVal+")");				
				while(rs.next()){				
					gids1 = gids1+rs.getString(1)+",";
				}*/
				//System.out.println("...........   :"+gids1);
				String nid="";
				ArrayList nidList=new ArrayList();
				
				rsN=stmtN.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gids1.substring(0, gids1.length()-1)+")");
				while(rsN.next()){
					nid=nid+rsN.getString(1)+",";
					nidList.add(rsN.getInt(1));
				}
				
				/** 
				 * implementing middleware jar file 
				 */
				
				
				Name names = null;
			
				for(int n=0;n<nidList.size();n++){
					names=manager.getGermplasmNameByID(Integer.parseInt(nidList.get(n).toString()));
					gids=gids+names.getGermplasmId()+",";
				}
				
				
				//System.out.println(",,,,,,,,,,,,,,,,,,,,  gids=:"+gids);
				/*
				rs=stmt.executeQuery("select gid, nval from names where nid in("+nid.substring(0, nid.length()-1)+")");
				while(rs.next()){
					gids=gids+rs.getString(1)+",";
				}*/
				
				
				gids = gids.substring(0,gids.length()-1);
				String[] gidsO=gids.split(",");
				int gid1=Integer.parseInt(gidsO[0]);
				int gid2=Integer.parseInt(gidsO[1]);
				//System.out.println("gids="+gids);
				
				
				
				
				String polyType=session.getAttribute("polyType").toString();
			//	System.out.println("....:"+polyType);
				
				if(polyType.equalsIgnoreCase("fingerprinting")){
				
					/** checking whether the gid exists in 'allele_values' table **/
					
					ResultSet rsa=stA.executeQuery("select count(*) from gdms_allele_values where gid in ("+gids+")");
					while (rsa.next()){
						alleleCount=rsa.getInt(1);
					}
					//System.out.println("alleleCount="+alleleCount);
					/** checking whether the gid exists in 'char_values' table **/
					ResultSet rsc=stC.executeQuery("select count(*) from gdms_char_values where gid in("+gids+")");
					while(rsc.next()){
						charCount=rsc.getInt(1);
					}
					//System.out.println("charCount="+charCount);
				}
				
				
				ArrayList chVal=new ArrayList();
				if(polyType.equalsIgnoreCase("fingerprinting")){
				
					/** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
					if(alleleCount>0){
						//ResultSet rsDet=stmtA.executeQuery("SELECT allele_values.dataset_id,allele_values.gid,germplasm_temp.germplasm_name,marker.species,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,germplasm_temp,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=germplasm_temp.gid AND marker.marker_id = allele_values.marker_id ORDER BY germplasm_name, marker_name");
						//System.out.println("SELECT allele_values.dataset_id,allele_values.gid,names.nval,marker.crop,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,names,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
						ResultSet rsDet=stmtA.executeQuery("SELECT gdms_allele_values.dataset_id,gdms_allele_values.gid,gdms_marker.marker_name,gdms_allele_values.allele_bin_value as data FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER BY gid, marker_name");
						
						while(rsDet.next()){
							//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
							chVal.add(rsDet.getInt(2)+"!~!"+rsDet.getString(3)+"!~!"+rsDet.getString(4));
						}
					}
					/** if gids exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
					if(charCount>0){
						//ResultSet rsDet=stmtC.executeQuery("SELECT distinct char_values.dataset_id,char_values.gid,germplasm_temp.germplasm_name,marker.species,char_values.marker_id,marker.marker_name,char_values.char_value as data FROM char_values,germplasm_temp,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=germplasm_temp.gid AND marker.marker_id = char_values.marker_id ORDER BY germplasm_name, marker_name");
						//System.out.println("SELECT distinct char_values.dataset_id,char_values.gid,names.nval,marker.crop,char_values.marker_id,marker.marker_name,char_values.char_value FROM char_values,names,marker WHERE char_values.gid in ("+gids+") AND char_values.gid=names.gid AND marker.marker_id = char_values.marker_id ORDER BY nval, marker_name");
						ResultSet rsDet=stmtC.executeQuery("SELECT distinct gdms_char_values.dataset_id,gdms_char_values.gid,gdms_marker.marker_name,gdms_char_values.char_value FROM gdms_char_values,gdms_marker WHERE gdms_char_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_char_values.marker_id ORDER BY gid, marker_name");
						
						
						while(rsDet.next()){
							//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
							chVal.add(rsDet.getInt(2)+"!~!"+rsDet.getString(3)+"!~!"+rsDet.getString(4));
						}
					
					}
				}else if(polyType.equalsIgnoreCase("mapping")){
					/** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
					
					//ResultSet rsDet=stmtM.executeQuery("SELECT mapping_pop_values.dataset_id,mapping_pop_values.gid,germplasm_temp.germplasm_name,marker.species,mapping_pop_values.marker_id,marker.marker_name,mapping_pop_values.map_char_value as data FROM mapping_pop_values,germplasm_temp,marker WHERE mapping_pop_values.gid in ("+gids+") AND mapping_pop_values.gid=germplasm_temp.gid AND marker.marker_id = mapping_pop_values.marker_id ORDER BY germplasm_name, marker_name");
					//System.out.println("SELECT allele_values.dataset_id,allele_values.gid,names.nval,marker.crop,allele_values.marker_id,marker.marker_name,allele_values.allele_bin_value as data FROM allele_values,names,marker WHERE allele_values.gid in ("+gids+") AND allele_values.gid=names.gid AND marker.marker_id = allele_values.marker_id ORDER BY nval, marker_name");
					System.out.println("SELECT gdms_mapping_pop_values.dataset_id,gdms_mapping_pop_values.gid,gdms_marker.marker_name,gdms_mapping_pop_values.map_char_value as data FROM gdms_mapping_pop_values,gdms_marker WHERE gdms_mapping_pop_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_mapping_pop_values.marker_id ORDER BY gid, marker_name");
					ResultSet rsDet=stmtM.executeQuery("SELECT gdms_mapping_pop_values.dataset_id,gdms_mapping_pop_values.gid,gdms_marker.marker_name,gdms_mapping_pop_values.map_char_value as data FROM gdms_mapping_pop_values,gdms_marker WHERE gdms_mapping_pop_values.gid in ("+gids+") AND gdms_marker.marker_id = gdms_mapping_pop_values.marker_id ORDER BY gid, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						chVal.add(rsDet.getInt(2)+"!~!"+rsDet.getString(3)+"!~!"+rsDet.getString(4));
					}
				}
				
				
				
				//System.out.println(".........chValues="+chVal);
				// String[] chVal=chValues.split("~!!~");
				
				 int s=0;
				 geno1.clear();geno2.clear();mark1.clear(); mark2.clear();ch1.clear();ch2.clear();
				 for(int c=0;c<chVal.size();c++){	
					// System.out.println(".............:"+chVal.get(c));
					 String arr[]=new String[3];
						StringTokenizer stz = new StringTokenizer(chVal.get(c).toString(), "!~!");
			    		//arrList6 = new String[stz.countTokens()];
			    		int i1=0;				  
			    		while(stz.hasMoreTokens()){				    			
			    			arr[i1] = stz.nextToken();
			    			i1++;
			    		}
					//str1=chVal.get(c).toString().split("!~!");
					if(Integer.parseInt(arr[0])==(gid1)){
						geno1.add(arr[0]);
						mark1.add(arr[1]);
						ch1.add(arr[2]);					
					}else{
						geno2.add(arr[0]);
						mark2.add(arr[1]);
						ch2.add(arr[2]);
					}			
				}
				/*System.out.println(mark1);
				System.out.println(mark2);
				System.out.println(ch1);
				System.out.println(ch2);*/
				finalList=new ArrayList();
				missingList=new ArrayList();
				String geno="";
				String markers="";
				//System.out.println(mark1);
				for(int k=0;k<geno1.size();k++){
					if((!(ch2.get(k).equals("0:0")||ch2.get(k).equals("N")||ch2.get(k).equals("?")||ch2.get(k).equals("-")))&&(!(ch1.get(k).equals("0:0")||ch1.get(k).equals("N")||ch1.get(k).equals("?")||ch1.get(k).equals("-")))&&(!(ch1.get(k).equals(ch2.get(k))))){
						if(!finalList.contains(mark1.get(k))){
							finalList.add(mark1.get(k));
							markers=markers+"'"+mark1.get(k)+"',";	
						}
					}
					if((ch1.get(k).equals("0:0"))||(ch2.get(k).equals("0:0"))||(ch1.get(k).equals("?"))||(ch2.get(k).equals("?"))||(ch1.get(k).equals("N"))||(ch2.get(k).equals("N"))||(ch1.get(k).equals("-"))||(ch2.get(k).equals("-"))){
						if(!missingList.contains(mark1.get(k))){
							missingList.add(mark1.get(k));
						}
					}
				}
				String recCount=finalList.size()+"";
				String mcount=missingList.size()+"";
				ArrayList mapList1=new ArrayList();
				String markerIDs="";
				System.out.println("select marker_id from gdms_marker where marker_name in("+markers.substring(0, markers.length()-1)+")");
				ResultSet RM=stmt.executeQuery("select marker_id from gdms_marker where marker_name in("+markers.substring(0, markers.length()-1)+")");
				while (RM.next()){
					markerIDs=markerIDs+RM.getInt(1)+",";
				}
				
				String markerIDsN=markerIDs.substring(0, markerIDs.length()-1);
				//markers=markers.substring(0, markers.length()-1);
				//System.out.println(markers);
				System.out.println("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markerIDsN+") GROUP BY gdms_map.map_name");
				ResultSet rsMap=stmtMap.executeQuery("SELECT gdms_map.map_name, COUNT(gdms_markers_onmap.marker_id) FROM gdms_markers_onmap JOIN gdms_map ON gdms_map.map_id=gdms_markers_onmap.map_id WHERE gdms_markers_onmap.marker_id IN("+markerIDsN+") GROUP BY gdms_map.map_name");
				while(rsMap.next()){
					mapList1.add(rsMap.getString(1)+" ("+rsMap.getInt(2)+")");
				}
				//System.out.println("mapList="+mapList1);								
				session.setAttribute("maps", mapList1);
				//System.out.println("missing list=:"+missingList);
				//System.out.println("final list=:"+finalList);
				//req.getSession().setAttribute("map", map);
				//req.getSession().setAttribute("lines", geno);
				req.getSession().setAttribute("MissingData", missingList);
				//req.getSession().setAttribute("map_data", map_data);
				req.getSession().setAttribute("recCount",recCount);
				req.getSession().setAttribute("missingCount",mcount);
				str="poly";
				String sel="yes";
				req.getSession().setAttribute("sel", sel);
				/*if(map==true){
					req.getSession().setAttribute("result",map_data);
				}else if(map==false){*/
					req.getSession().setAttribute("result", finalList);
				//}
			
			}else if(retrieveOP.equalsIgnoreCase("Get Lines")){
				/** retrieving gid, germplasm name for the markers that are uploaded through the text file  **/
				type="GermplasmName";
				req.getSession().setAttribute("op", retrieveOP);
				ArrayList germNames=new ArrayList();
				ArrayList markerList=new ArrayList();
				
				List<String> mList = new ArrayList<String>();
				String markers="";
				String m1="";
				String m2="";
				String fileName="";String saveFtoServer="";String saveF="";
				String[] splitStr=null;
				String op1=req.getParameter("opTypeMarkers");
				//System.out.println("...................."+op1);
				if(op1.equalsIgnoreCase("file")){
					FileUploadToServer fus = null;
				
					InputStream stream=null;
					FormFile file=(FormFile)df.get("txtNameL");
					//String fname1=file.getFileName();
					
					stream=file.getInputStream();
					saveFtoServer="UploadFiles";
					saveF=req.getSession().getServletContext().getRealPath("//")+"/"+saveFtoServer;
					if(!new File(saveF).exists())
						new File(saveF).mkdir();	        
					fileName=saveF+"/"+file.getFileName();
					fus=new FileUploadToServer();
					//System.out.println("................   :"+fileName);
					fus.createFile(stream,fileName);
					BufferedReader bf=new BufferedReader(new FileReader(fileName));		
					while ((str1 = bf.readLine()) != null){				
						markers= markers+str1+",";	
						markerList.add(str1);
					}
					//System.out.println("markers="+markers);
					splitStr=markers.split(",");
					session.setAttribute("mCount", splitStr.length-1);
					if(splitStr[0].equalsIgnoreCase("marker name")){
						for(int m=1;m<splitStr.length;m++){
							m1=m1+"'"+splitStr[m]+"',";
							if(!(mList.contains(splitStr[m])))
								mList.add(splitStr[m]);
							m2=m2+splitStr[m]+",";
						}			
					}
				}else if(op1.equalsIgnoreCase("textbox")){
					markers=req.getParameter("markersText");
					//System.out.println("#####################  "+markers);
					if(markers.contains("\n")){
						//System.out.println("new line");
						splitStr=markers.split("\n");
					}else if(markers.contains("\t")){
						//System.out.println("tab seperated");
						splitStr=markers.split("\t");
					}else if(markers.contains(",")){
						splitStr=markers.split(",");
					}else{
						String ErrMsg = " Marker names should be provided with seperator as indicated";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");
					}
					session.setAttribute("mCount", splitStr.length);
					
						for(int m=0;m<splitStr.length;m++){
							m1=m1+"'"+splitStr[m].trim()+"',";
							if(!(mList.contains(splitStr[m].trim())))
								mList.add(splitStr[m].trim());
							m2=m2+splitStr[m].trim()+",";
						}			
					
				}
				//System.out.println("..............markers="+m1);
				session.setAttribute("mnames1", m1);
				session.setAttribute("mnames", m2);
				String markerId="";
				
				
				int alleleCount=0;
				int charCount=0;
				int mapCharCount=0;
				/*Statement stA=con.createStatement();
				Statement stC=con.createStatement();
				Statement stmtC=con.createStatement();
				Statement stmtA=con.createStatement();
				Statement stM=con.createStatement();
				Statement stmtM=con.createStatement();*/
				Statement stmtG=con.createStatement();
					Statement stmttest=con.createStatement();
					ResultSet rs2=null;
					Statement st=con.createStatement();
					int count=0;
					int start=0;
					int end=100;
					//gdms.getMarkerIdsByMarkerNames(mList, 0, end, Database.LOCAL);
					
					
					
					
					System.out.println("select marker_id from gdms_marker where marker_name in("+ m1.substring(0,m1.length()-1) +") order by marker_id");
					rs=stmt.executeQuery("select marker_id from gdms_marker where marker_name in("+ m1.substring(0,m1.length()-1) +") order by marker_id");
					while(rs.next()){
						count=count+1;
						markerId=markerId+rs.getInt(1)+",";
					}
					if(count<1){
						String ErrMsg = "Marker(s) does not exist";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");
					}
					//System.out.println(markerId);
					
					/** checking whether the marker id exists in 'allele_values' table **/
					//System.out.println("select count(*) from allele_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					ResultSet rsa=stA.executeQuery("select count(*) from gdms_allele_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while (rsa.next()){
						alleleCount=rsa.getInt(1);
					}
					
					/** checking whether the marker id exists in 'char_values' table **/
					ResultSet rsc=stC.executeQuery("select count(*) from gdms_char_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while(rsc.next()){
						charCount=rsc.getInt(1);
					}
					
					/** checking whether the marker id exists in 'mapping_pop_values' table **/
					ResultSet rsM=stM.executeQuery("select count(*) from gdms_mapping_pop_values where marker_id in ("+ markerId.substring(0,markerId.length()-1) +")");
					while(rsM.next()){
						mapCharCount=rsM.getInt(1);
					}
					String gids="";
					
					/** if marker_id exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
					if(charCount>0){
						rsDet=stmtC.executeQuery("SELECT distinct gid FROM gdms_char_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDet.next()){							
							gids=gids+rsDet.getInt(1)+",";
						}
					
					}
					
					/** if marker id exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
					if(alleleCount>0){
						rsDet=stmtA.executeQuery("SELECT distinct gid FROM gdms_allele_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDet.next()){							
							gids=gids+rsDet.getInt(1)+",";
						}
					}
					
					
					/** if marker_id exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
					if(mapCharCount>0){
						rsDet=stmtM.executeQuery("SELECT distinct gid FROM gdms_mapping_pop_values WHERE marker_id in ("+ markerId.substring(0,markerId.length()-1) +") ORDER BY gid");
						while(rsDet.next()){							
							gids=gids+rsDet.getInt(1)+",";
						}
					
					}
					
					if(charCount==0 && alleleCount==0 && mapCharCount==0){
						String ErrMsg = "No Genotyping data for the provided marker(s)";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");
					}
					
					String nid="";
					ArrayList nList=new ArrayList();
					System.out.println("select nid from gdms_acc_metadataset where gid in ("+gids.substring(0, gids.length()-1)+")");
					rs=stmt.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gids.substring(0, gids.length()-1)+")");
					while(rs.next()){
						nid=nid+rs.getString(1)+",";
						nList.add(rs.getInt(1));
					}
					
					/** 
					 * implementing middleware jar file 
					 */
					
					//GermplasmDataManager manager = factory.getGermplasmDataManager();
					Name names = null;
				
					for(int n=0;n<nList.size();n++){
						names=manager.getGermplasmNameByID(Integer.parseInt(nList.get(n).toString()));
						if(!germNames.contains(names.getNval()+","+names.getGermplasmId()))
							germNames.add(names.getNval()+","+names.getGermplasmId());
					}
					
					/*System.out.println("select distinct gid,nval from names where nid in("+nid.substring(0,nid.length()-1)+") order by nid");
					rs1=stmtG.executeQuery("select distinct gid,nval from names where nid in("+nid.substring(0,nid.length()-1)+") order by nid");
					while(rs1.next()){
						if(!germNames.contains(rs1.getString(2)+","+rs1.getInt(1)))
							germNames.add(rs1.getString(2)+","+rs1.getInt(1));
						
					}*/
					
					
					/*query2=hsession.createQuery("select distinct map_name from MapBean");
					mapList=query2.list();*/
					mapList.clear();
					rsMp=stmtMp.executeQuery("select distinct map_name from gdms_map");
					while(rsMp.next()){
						mapList.add(rsMp.getString(1));
					}
					session.setAttribute("AccListFinal", germNames);
					session.setAttribute("mapList", mapList);
					session.setAttribute("markerList", markerList);
					session.setAttribute("GenoCount", germNames.size());
					session.setAttribute("type", type);
				
				str="retGermplasms";
			}else if(retrieveOP.equalsIgnoreCase("Get Markers")){	
				/** retrieving markers for the gids that are uploaded through the text file  **/
				markerList.clear();
				genotypesList.clear();
				type="markers";
				gids="";
				String[] strGids=null;
				FileUploadToServer fus = null;
				String fileName="";String saveFtoServer="";String saveF="";
				InputStream stream=null;
				System.out.println("*******************************************:"+req.getParameter("opTypeGids"));
				String op1=req.getParameter("opTypeGids");
				
				Statement stmttest=con.createStatement();
				if(op1.equalsIgnoreCase("file")){
					FormFile file=(FormFile)df.get("txtNameM");
				
					stream=file.getInputStream();
					saveFtoServer="UploadFiles";
					saveF=req.getSession().getServletContext().getRealPath("//")+"/"+saveFtoServer;
					if(!new File(saveF).exists())
						new File(saveF).mkdir();	        
					fileName=saveF+"/"+file.getFileName();
					fus=new FileUploadToServer();
					fus.createFile(stream,fileName);
				
				
					BufferedReader bf=new BufferedReader(new FileReader(fileName));		
					while ((str1 = bf.readLine()) != null){				
						gids= gids+str1+",";					
					}
				}else if(op1.equalsIgnoreCase("textbox")){
					gids=req.getParameter("gidsText");
					
				}
				
				String gidsN="";
				//System.out.println("request.getParameter radios ="+request.getParameter("str") );
				//System.out.println("........................................   gids in class="+gids);
				//String op=req.getParameter("str");
				req.getSession().setAttribute("op", retrieveOP);
				if(gids.contains("\n")){
					//System.out.println("new line");
					strGids=gids.split("\n");
				}else if(gids.contains("\t")){
					//System.out.println("tab seperated");
					strGids=gids.split("\t");
				}else if(gids.contains(",")){
					strGids=gids.split(",");
				}else{
					String ErrMsg = " Gids should be provided with seperator as indicated";
					req.getSession().setAttribute("indErrMsg", ErrMsg);
					return am.findForward("ErrMsgM");
				}
				//String[] strGids=gids.split(",");
				int gidsCount=strGids.length-1;
				if(op1.equalsIgnoreCase("file")){
					if(strGids[0].equalsIgnoreCase("gids")){				
						for(int i=1;i<strGids.length;i++){
							gidsN=gidsN+strGids[i]+",";
						}				
						req.getSession().setAttribute("genCount", gidsCount);
					}
				}else{
					for(int i=0;i<strGids.length;i++){
						gidsN=gidsN+strGids[i].trim()+",";
					}				
					req.getSession().setAttribute("genCount", strGids.length);
				}
				
				
				req.getSession().setAttribute("gidsN", gidsN);
				//System.out.println("%%%%%%%%%%%%%%%%   length="+gidsN);
				String gid=gidsN.substring(0,gidsN.length()-1);
				int alleleCount=0;
				int charCount=0;
				int mapCharCount=0;
				/*Statement stA=con.createStatement();
				Statement stC=con.createStatement();
				Statement stmtC=con.createStatement();
				Statement stmtA=con.createStatement();
				*/
				Statement stN=con.createStatement();
				/*Statement stM=con.createStatement();
				Statement stmtM=con.createStatement();*/
				//SortedMap mapN = new TreeMap();
				ArrayList gidsList=new ArrayList();
				String nid="";
				int count=0;
				ArrayList nList=new ArrayList();
				rs=stmt.executeQuery("select nid from gdms_acc_metadataset where gid in ("+gid+")");
				while(rs.next()){
					nid=nid+rs.getString(1)+",";
					nList.add(rs.getInt(1));
					count=count+1;
				}
				if(count<1){
					String ErrMsg = "Gid(s) does not exist";
					req.getSession().setAttribute("indErrMsg", ErrMsg);
					return am.findForward("ErrMsgGID");
				}
				/*rs1=stN.executeQuery("select distinct gid,nval from names where nid in ("+nid.substring(0,nid.length()-1)+") order by nid");
				while(rs1.next()){
					if(!gidsList.contains(rs1.getInt(1)))
						gidsList.add(rs1.getInt(1));
					if(!genotypesList.contains(rs1.getString(2)))
						genotypesList.add(rs1.getString(2));	
					
					//mapN.put(rs1.getInt(1), rs1.getString(2));
					
				}*/
				
				/** 
				 * implementing middleware jar file 
				 */
				
				//GermplasmDataManager manager = factory.getGermplasmDataManager();
				Name names = null;
			
				for(int n=0;n<nList.size();n++){
					names=manager.getGermplasmNameByID(Integer.parseInt(nList.get(n).toString()));
					if(!gidsList.contains(names.getGermplasmId()))
						gidsList.add(names.getGermplasmId());
					if(!genotypesList.contains(names.getNval()))
						genotypesList.add(names.getNval());	
				}
				
				
				
				
				//System.out.println(mapN);
				//System.out.println("..........."+gidsList);
				
				
				
				System.out.println("select count(*) from gdms_allele_values where gid in ("+gid+")");
				/** checking whether the gid exists in 'allele_values' table **/
				ResultSet rsa=stA.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
				while (rsa.next()){
					alleleCount=rsa.getInt(1);
				}
				
				/** checking whether the gid exists in 'char_values' table **/
				System.out.println("select count(*) from gdms_char_values where gid in("+gid+")");
				ResultSet rsc=stC.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
				while(rsc.next()){
					charCount=rsc.getInt(1);
				}
				
				/** checking whether the gid exists in 'char_values' table **/
				System.out.println("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				ResultSet rsM=stM.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				while(rsM.next()){
					mapCharCount=rsM.getInt(1);
				}
				
				/** if gids exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
				if(charCount>0){
					rsDet=stmtC.executeQuery("SELECT distinct gdms_char_values.gid, gdms_marker.marker_name FROM gdms_char_values join gdms_marker on gdms_marker.marker_id = gdms_char_values.marker_id WHERE gdms_char_values.gid in ("+gid+") ORDER BY gid, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
					session.setAttribute("datasetType", "SNP");
				}
				
				/** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **/				
				if(alleleCount>0){
					
					rsDet=stmtA.executeQuery("SELECT gdms_allele_values.gid, gdms_marker.marker_name FROM gdms_allele_values,gdms_marker WHERE gdms_allele_values.gid in ("+gid+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER BY gid, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
					session.setAttribute("datasetType", "SSR");
				}
				
				/** if gids exists in 'map_char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **/
				if(mapCharCount>0){
					//rsDet=stmtM.executeQuery("SELECT distinct germplasm_temp.germplasm_name,marker.marker_name FROM germplasm_temp join mapping_pop_values on mapping_pop_values.gid=germplasm_temp.gid join marker on marker.marker_id = mapping_pop_values.marker_id WHERE mapping_pop_values.gid in ("+gid+") ORDER BY germplasm_name, marker_name");
								
					rsDet=stmtC.executeQuery("SELECT distinct gdms_mapping_pop_values.gid, gdms_marker.marker_name FROM gdms_mapping_pop_values join gdms_marker on gdms_marker.marker_id = gdms_mapping_pop_values.marker_id WHERE gdms_mapping_pop_values.gid in ("+gid+") ORDER BY gid, marker_name");
					
					while(rsDet.next()){
						//chValues=chValues+rsDet.getString(3)+"!~!"+rsDet.getString(6)+"!~!"+rsDet.getString(7)+"~!!~";
						/*if(!genotypesList.contains(rsDet.getString(1)))
							genotypesList.add(rsDet.getString(1));	*/
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}	
					session.setAttribute("datasetType", "mapping");
				}
				
				////System.out.println(markerList);
				
				/*query2=hsession.createQuery("select distinct map_name from MapBean");
				
				
				mapList=query2.list();*/
				mapList.clear();
				System.out.println("select distinct map_name from gdms_map");
				rsMp=stmtMp.executeQuery("select distinct map_name from gdms_map");
				while(rsMp.next()){
					mapList.add(rsMp.getString(1));
				}
				req.getSession().setAttribute("AccListFinal", genotypesList);
				req.getSession().setAttribute("mapList", mapList);
				req.getSession().setAttribute("markerList", markerList);
				
				req.getSession().setAttribute("MarkerCount", markerList.size());
				session.setAttribute("type", type);
				str="retMarkers";
				
			}else if(retrieveOP.equalsIgnoreCase("Get")){	
				/** retrieving markers for the gNames that are uploaded through the text file  **/
				markerList.clear();
				genotypesList.clear();
				type="markers";
				String gNames="";
				String[] strGNames=null;
				FileUploadToServer fus = null;
				String fileName="";String saveFtoServer="";String saveF="";
				String argGNames="";String gidsN="";
			//	System.out.println("*******************************************:"+req.getParameter("opTypeGN"));
				String op1=req.getParameter("opTypeGN");
				List<String> linesList=new ArrayList<String>();
				Statement stmttest=con.createStatement();
				if(op1.equalsIgnoreCase("file")){
					InputStream stream=null;
					FormFile file=(FormFile)df.get("txtNameGN");
				
					stream=file.getInputStream();
					saveFtoServer="UploadFiles";
					saveF=req.getSession().getServletContext().getRealPath("//")+"/"+saveFtoServer;
					if(!new File(saveF).exists())
						new File(saveF).mkdir();	        
					fileName=saveF+"/"+file.getFileName();
					fus=new FileUploadToServer();
					fus.createFile(stream,fileName);
				
				
					BufferedReader bf=new BufferedReader(new FileReader(fileName));		
					while ((str1 = bf.readLine()) != null){				
						gNames= gNames+str1+",";					
					}
					
					strGNames=gNames.split(",");
					if(strGNames[0].equalsIgnoreCase("germplasm names")){				
						for(int i=1;i<strGNames.length;i++){
							argGNames=argGNames+"'"+strGNames[i]+"',";
							linesList.add(strGNames[i]);
						}				
						//req.getSession().setAttribute("genCount", gidsCount);
					}
				}else if(op1.equalsIgnoreCase("textbox")){
					gNames=req.getParameter("GNamesText");
					if(gNames.contains("\n")){
						//System.out.println("new line");
						strGNames=gNames.split("\n");
					}else if(gNames.contains("\t")){
						//System.out.println("tab seperated");
						strGNames=gNames.split("\t");
					}else if(gNames.contains(",")){
						strGNames=gNames.split(",");
					}else{
						String ErrMsg = "Germplasm names should be provided with seperator as indicated";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsgM");
					}
					
					
					//String[] strGNames=gNames.split(",");
					for(int i=0;i<strGNames.length;i++){
						argGNames=argGNames+"'"+strGNames[i].trim()+"',";
						linesList.add(strGNames[i].trim());
					}				
						
				}
				int count=0;
				//System.out.println("gNames  .......  ="+linesList);
				/*Statement stA=con.createStatement();
				Statement stC=con.createStatement();
				Statement stmtC=con.createStatement();
				Statement stmtA=con.createStatement();*/
				//SortedMap mapN = new TreeMap();
				ArrayList gidL=new ArrayList();
				/*Statement stM=con.createStatement();
				Statement stmtM=con.createStatement();*/
				String nids="";
				int countG=0;
				//System.out.println("select distinct gid from germplasm_temp where germplasm_name in("+argGNames.substring(0,argGNames.length()-1) +") order by gid desc");
				//ResultSet rsGids=stmtM.executeQuery("select distinct gid from germplasm_temp where germplasm_name in("+argGNames.substring(0,argGNames.length()-1) +") order by gid desc");
				
				
				List<GidNidElement> results = manager.getGidAndNidByGermplasmNames(linesList);
				//System.out.println("RESULTS (getGidAndNidByGermplasmNames): " + results);
				for(int r=0;r<results.size();r++){
					gidsN=gidsN+results.get(r).getGermplasmId()+",";
					countG=countG+1;
				}
				
				
				/*System.out.println("select distinct gid,nid from names where nval in("+argGNames.substring(0,argGNames.length()-1) +") order by gid desc");
				ResultSet rsGids=stmtM.executeQuery("select distinct gid from names where nval in("+argGNames.substring(0,argGNames.length()-1) +") order by gid desc");
				while(rsGids.next()){
					gidsN=gidsN+rsGids.getInt(1)+",";
					countG=countG+1;
					
				}*/
				if(countG<1){
					String ErrMsg = "Germplasm name(s) does not exist";
					req.getSession().setAttribute("indErrMsg", ErrMsg);
					return am.findForward("ErrMsgM");
				}
				String nid="";
				System.out.println("select distinct gid,nid from gdms_acc_metadataset where gid in ("+gidsN.substring(0,gidsN.length()-1)+")");
				rs=stmt.executeQuery("select distinct gid,nid from gdms_acc_metadataset where gid in ("+gidsN.substring(0,gidsN.length()-1)+")");
				while(rs.next()){
					nid=nid+rs.getString(1)+",";
					count++;
				}
				if(count<1){
					String ErrMsg = "Germplasm name(s) does not exist";
					req.getSession().setAttribute("indErrMsg", ErrMsg);
					return am.findForward("ErrMsgG");
				}
				
				req.getSession().setAttribute("op", retrieveOP);
				
				req.getSession().setAttribute("genCount", count);
				req.getSession().setAttribute("gidsN", nid);
				//System.out.println("%%%%%%%%%%%%%%%%   length="+nid);
				String gid=nid.substring(0,nid.length()-1);
				int alleleCount=0;
				int charCount=0;
				int mapCharCount=0;
				
				
				System.out.println("select count(*) from gdms_allele_values where gid in ("+gid+")");
				//** checking whether the gid exists in 'allele_values' table **//*
				ResultSet rsa=stA.executeQuery("select count(*) from gdms_allele_values where gid in ("+gid+")");
				while (rsa.next()){
					alleleCount=rsa.getInt(1);
				}
				
				//** checking whether the gid exists in 'char_values' table **//*
				System.out.println("select count(*) from gdms_char_values where gid in("+gid+")");
				ResultSet rsc=stC.executeQuery("select count(*) from gdms_char_values where gid in("+gid+")");
				while(rsc.next()){
					charCount=rsc.getInt(1);
				}
				
				//** checking whether the gid exists in 'char_values' table **//*
				System.out.println("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				ResultSet rsM=stM.executeQuery("select count(*) from gdms_mapping_pop_values where gid in("+gid+")");
				while(rsM.next()){
					mapCharCount=rsM.getInt(1);
				}
				
				//** if gids exists in 'char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **//*
				if(charCount>0){
					//rsDet=stmtC.executeQuery("SELECT distinct germplasm_temp.germplasm_name,marker.marker_name FROM germplasm_temp join char_values on char_values.gid=germplasm_temp.gid join marker on marker.marker_id = char_values.marker_id WHERE char_values.gid in ("+gid+") ORDER BY germplasm_name, marker_name");
					
					rsDet=stmtC.executeQuery("SELECT distinct gdms_char_values.gid, gdms_marker.marker_name FROM gdms_char_values join gdms_marker on gdms_marker.marker_id = gdms_char_values.marker_id WHERE gdms_char_values.gid in ("+gid+") ORDER BY marker_name");
					
					while(rsDet.next()){							
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
					session.setAttribute("datasetType", "SNP");
				}
				
				//** if gids exists in 'allele_values' table retrieving corresponding data and concatinating germplasm name, marker_name & allele_bin_value **//*				
				if(alleleCount>0){
					rsDet=stmtA.executeQuery("SELECT distinct gdms_allele_values.gid, gdms_marker.marker_name FROM gdms_allele_values, gdms_marker WHERE gdms_allele_values.gid in ("+gid+") AND gdms_marker.marker_id = gdms_allele_values.marker_id ORDER by marker_name");
					while(rsDet.next()){
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}
					session.setAttribute("datasetType", "SSR");
				}
				
				//** if gids exists in 'map_char_values' table retrieving corresponding data and concatinating germplasm name, marker_name & char_value **//*
				if(mapCharCount>0){
					rsDet=stmtC.executeQuery("SELECT distinct gdms_mapping_pop_values.gid, gdms_marker.marker_name FROM gdms_mapping_pop_values join gdms_marker on gdms_marker.marker_id = gdms_mapping_pop_values.marker_id WHERE gdms_mapping_pop_values.gid in ("+gid+") ORDER BY marker_name");
					while(rsDet.next()){						
						if(!markerList.contains(rsDet.getString(2)))
							markerList.add(rsDet.getString(2));
					}	
					session.setAttribute("datasetType", "mapping");
				}
				
				//System.out.println(markerList);
				
				//query2=hsession.createQuery("select distinct map_name from MapBean");
				/*GenotypicDataManager gm=factory.getGenotypicDataManager();				
				
				List<Map> maps = gm.getAllMaps(0, 10, Database.LOCAL);
		        
		        if(maps == null || maps.isEmpty()) {
		            System.out.println("No records found.");
		        } else {
		            for(Map map : maps) {
		                System.out.println(" " + map);
		                mList.add(map.getMapName());
		            }
		        }*/
				
				//mapList=query2.list();
				mapList.clear();
				rsMp=stmtMp.executeQuery("select distinct map_name from gdms_map");
				while(rsMp.next()){
					mapList.add(rsMp.getString(1));
				}
		        //System.out.println("..............:"+mapList);
				req.getSession().setAttribute("AccListFinal", genotypesList);
				req.getSession().setAttribute("mapList", mapList);
				req.getSession().setAttribute("markerList", markerList);
				
				req.getSession().setAttribute("MarkerCount", markerList.size());
				session.setAttribute("type", type);
				str="retMarkers";
				
			}else if(retrieveOP.equalsIgnoreCase("GetInfo")){
				String opType=req.getParameter("retType");
				//System.out.println("888888888888888888888....  opType="+opType);
				//if((opType.equals("qtlData"))||(opType.equals("trait"))){
				//System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ TTTTTTTTTTTTTTTTTTTTTTTTTTT");
				/** Retrieving qtl information **/
				if(session!=null){
					session.removeAttribute("strdata");		
					session.removeAttribute("indErrMsg");	
				}
				p.load(new FileInputStream(session.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
				String host=p.getProperty("central.host");
				String port=p.getProperty("central.port");
				String url = "jdbc:mysql://"+host+":"+port+"/";
				String dbName = p.getProperty("central.dbname");
				String driver = "com.mysql.jdbc.Driver";
				String userName = p.getProperty("central.username"); 
				String password = p.getProperty("central.password");
				ArrayList traitsComList=new ArrayList();
				SortedMap map = new TreeMap();
				Class.forName(driver).newInstance();
				conn = DriverManager.getConnection(url+dbName,userName,password);
				Statement stCen=conn.createStatement();
				
				/*ArrayList traitsList=new ArrayList(); 
				TraitDataManager tm=factory.getTraitDataManager();
				int tcount=Integer.parseInt(tm.countAllTraits()+"");
				List<Trait> traits =tm.getAllTraits(0, tcount, Database.CENTRAL);
				for (Trait trait : traits) {
		           // System.out.println("  " + trait);
		            traitsList.add(trait.getAbbreviation()+"%~%"+trait.getName());
		        }
				System.out.println("  " + traitsList);*/
				//String qtlType=(String)df.get("qtlData");
				//String qtlType=req.getParameter("retType");
				String qtl=(String)df.get("qtl");
				String qtlData="";
				int linkid=0;			float min=0;			float max=0;
				String lg="";			String markers="";			//int qtlId=0;
				String qtlIds="";int count=0;String qtl_id="";	String strData="";
				String finalQTLList="";
				ArrayList qtlDataList=new ArrayList();
				String qtlId="";
				ResultSet rs2=null;
				Statement st=con.createStatement();
				session.setAttribute("qtlType", opType);
				System.out.println("******************************************  "+opType+"  $$$$$$$$   "+qtl);
				rsN=stmtN.executeQuery("select distinct trabbr, trname, ontology from tmstraits");
				while(rsN.next()){
					traitsComList.add(rsN.getString(1));
					//+"!~!"+rsN.getString(2)+"!~!"+rsN.getString(3));
					map.put(rsN.getString(1), rsN.getString(2)+"!~!"+rsN.getString(3));
				}
				//System.out.println("******************:"+traitsComList);
				//Statement stC=conn.createStatement();
				ResultSet rsC=stCen.executeQuery("select distinct trabbr, trname, ontology from tmstraits");
				while(rsC.next()){
					if(!(traitsComList.contains(rsC.getString(1)))){
						traitsComList.add(rsC.getString(1));
						map.put(rsC.getString(1), rsC.getString(2)+"!~!"+rsC.getString(3));
					}
						//traitsComList.add(rsC.getString(1)+"!~!"+rsC.getString(2)+"!~!"+rsC.getString(3));
				}
				if(opType.equals("QTLName")){
					/** if retrieval is through QTL Name **/
					qtlDataList.clear();
					//System.out.println("select qtl_id from qtl where qtl_name like '"+qtl+"%'");
					if(qtl.equalsIgnoreCase("*")){
						//System.out.println("***************************");
						rs1=st.executeQuery("select qtl_id from gdms_qtl order by qtl_id");
					}else{
						//System.out.println("else if");
						//System.out.println("select qtl_id from gdms_qtl where qtl_name like '"+qtl+"%' order by qtl_id");
						rs1=st.executeQuery("select qtl_id from gdms_qtl where qtl_name like '"+qtl+"%' order by qtl_id");
					}
					while(rs1.next()){
						qtlId=qtlId+rs1.getInt(1)+",";
						//qtl_id=qtl_id+qtlId+",";	
						count=count+1;
					}
					if(count==0){
						/*ae.add("myerrors", new ActionError("qtl.doesnot.exists"));
						saveErrors(req, ae);				
						//msg="chkPlateid";
						return (new ActionForward(am.getInput()));*/
						String ErrMsg = "QTL Name not found";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}else{
						
						if(qtl.equalsIgnoreCase("*")){
							//rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, tmstraits.trname, tmstraits.ontology FROM gdms_qtl_details, gdms_qtl,gdms_map, tmstraits WHERE gdms_qtl.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id and gdms_qtl_details.trait=tmstraits.trabbr order by gdms_qtl.qtl_id");
							System.out.println("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions FROM gdms_qtl_details, gdms_qtl,gdms_map WHERE gdms_qtl.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
							rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions FROM gdms_qtl_details, gdms_qtl,gdms_map WHERE gdms_qtl.qtl_id in("+qtlId.substring(0,qtlId.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
						}else{						
							//rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions, tmstraits.trname, tmstraits.ontology FROM gdms_qtl_details, gdms_qtl, gdms_map, tmstraits WHERE gdms_qtl.qtl_name like '"+qtl.toLowerCase()+"%' AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id and gdms_qtl_details.trait=tmstraits.trabbr order by gdms_qtl.qtl_id");
							rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square,gdms_qtl_details.interactions FROM gdms_qtl_details, gdms_qtl, gdms_map WHERE gdms_qtl.qtl_name like '"+qtl.toLowerCase()+"%' AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
						}
						while(rs.next()){
							//qtlData=qtlData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+"!~!"+rs.getString(14)+"!~!"+rs.getString(15)+";;;";
//							qtlData=qtlData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+";;;";
							qtlDataList.add(rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12));
						}
						
						//System.out.println("..................:"+traitsComList);
						//ArrayList finalQTLList=new ArrayList();
						
						for(int a=0; a<qtlDataList.size();a++){
							String[] argTr=qtlDataList.get(a).toString().split("!~!");
							if(map.containsKey(argTr[5])){
								finalQTLList=finalQTLList+argTr[0]+"!~!"+argTr[1]+"!~!"+argTr[2]+"!~!"+argTr[3]+"!~!"+argTr[4]+"!~!"+argTr[5]+"!~!"+argTr[6]+"!~!"+argTr[7]+"!~!"+argTr[8]+"!~!"+argTr[9]+"!~!"+argTr[10]+"!~!"+argTr[11]+"!~!"+argTr[12]+"!~!"+map.get(argTr[5])+";;;";
							}else{
								finalQTLList=finalQTLList+argTr[0]+"!~!"+argTr[1]+"!~!"+argTr[2]+"!~!"+argTr[3]+"!~!"+argTr[4]+"!~!"+argTr[5]+"!~!"+argTr[6]+"!~!"+argTr[7]+"!~!"+argTr[8]+"!~!"+argTr[9]+"!~!"+argTr[10]+"!~!"+argTr[11]+"!~!"+argTr[12]+"!~! "+"!~! "+";;;";
							}
							
						}
						
						//System.out.println(finalQTLList);
						req.getSession().setAttribute("strdata",finalQTLList);
					}
					req.getSession().setAttribute("qtl", qtl);
					
					str="retQTL";
				}else if(opType.equals("Trait")){
					/** if the option is through trait name **/
					qtlDataList.clear();
					if(qtl.equalsIgnoreCase("*")){
						//System.out.println("***************************");
						//rs1=st.executeQuery("select distinct trait, qtl_id from qtl_linkagemap order by qtl_id");
						rs1=st.executeQuery("select distinct trait, qtl_id from gdms_qtl_details order by qtl_id");
						while(rs1.next()){
							//qtlId=rs1.getInt(1);
							qtl_id=qtl_id+rs1.getInt(2)+",";	
							count=count+1;
						}
					}else{
						//rs1=st.executeQuery("select qtl_id from qtl_linkagemap where trait='"+qtl+"' order by qtl_id");
						rs1=st.executeQuery("select qtl_id from gdms_qtl_details where trait like '"+qtl+"%' order by qtl_id");
					
						while(rs1.next()){
							//qtlId=rs1.getInt(1);
							qtl_id=qtl_id+rs1.getInt(1)+",";	
							count=count+1;
						}
					}
					if(count==0){
						/*ae.add("myerrors", new ActionError("trait.doesnot.exists"));
						saveErrors(req, ae);				
						//msg="chkPlateid";
						return (new ActionForward(am.getInput()));*/
						String ErrMsg = "Trait Name not found";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}
					//System.out.println("QTL S="+qtl_id.substring(0,qtl_id.length()-1));
					req.getSession().setAttribute("qtl", qtl);
					
					//rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.lod, gdms_qtl_details.r_square, gdms_qtl_details.interactions, trait.trname, trait.ontology FROM gdms_qtl_details, gdms_qtl, gdms_map, trait WHERE gdms_qtl_details.trait=trait.trabbr and gdms_qtl_details.qtl_id IN("+qtl_id.substring(0,qtl_id.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
					//rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.lod, gdms_qtl_details.r_square, gdms_qtl_details.interactions, tmstraits.trname, tmstraits.ontology FROM gdms_qtl_details, gdms_qtl, gdms_map, tmstraits WHERE gdms_qtl_details.trait=tmstraits.trabbr and gdms_qtl_details.qtl_id IN("+qtl_id.substring(0,qtl_id.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
					rs=stmt.executeQuery("SELECT gdms_qtl.qtl_name,gdms_map.map_name,gdms_qtl_details.linkage_group AS chromosome, gdms_qtl_details.min_position, gdms_qtl_details.max_position, gdms_qtl_details.trait, gdms_qtl_details.experiment, gdms_qtl_details.left_flanking_marker, gdms_qtl_details.right_flanking_marker, gdms_qtl_details.effect, gdms_qtl_details.score_value, gdms_qtl_details.r_square, gdms_qtl_details.interactions FROM gdms_qtl_details, gdms_qtl, gdms_map WHERE gdms_qtl_details.qtl_id IN("+qtl_id.substring(0,qtl_id.length()-1)+") AND gdms_qtl.qtl_id=gdms_qtl_details.qtl_id AND gdms_qtl_details.map_id=gdms_map.map_id order by gdms_qtl.qtl_id");
					while(rs.next()){
						//strData=strData+rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12)+";;;";
						qtlDataList.add(rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getFloat(4)+"!~!"+rs.getFloat(5)+"!~!"+rs.getString(6)+"!~!"+rs.getString(7)+"!~!"+rs.getString(8)+"!~!"+rs.getString(9)+"!~!"+rs.getFloat(10)+"!~!"+rs.getFloat(11)+"!~!"+rs.getString(13)+"!~!"+rs.getFloat(12));
					}					
					//req.getSession().setAttribute("strdata",strData);
					
					for(int a=0; a<qtlDataList.size();a++){
						String[] argTr=qtlDataList.get(a).toString().split("!~!");
						if(map.containsKey(argTr[5])){
							finalQTLList=finalQTLList+argTr[0]+"!~!"+argTr[1]+"!~!"+argTr[2]+"!~!"+argTr[3]+"!~!"+argTr[4]+"!~!"+argTr[5]+"!~!"+argTr[6]+"!~!"+argTr[7]+"!~!"+argTr[8]+"!~!"+argTr[9]+"!~!"+argTr[10]+"!~!"+argTr[11]+"!~!"+argTr[12]+"!~!"+map.get(argTr[5])+";;;";
						}else{
							finalQTLList=finalQTLList+argTr[0]+"!~!"+argTr[1]+"!~!"+argTr[2]+"!~!"+argTr[3]+"!~!"+argTr[4]+"!~!"+argTr[5]+"!~!"+argTr[6]+"!~!"+argTr[7]+"!~!"+argTr[8]+"!~!"+argTr[9]+"!~!"+argTr[10]+"!~!"+argTr[11]+"!~!"+argTr[12]+"!~! "+"!~! "+";;;";
						}
						
					}
					
					//System.out.println(finalQTLList);
					req.getSession().setAttribute("strdata",finalQTLList);
					
					//System.out.println("strData="+strData);	
					str="retTrait";					
				}else if(opType.equals("maps")){					
					//String map=df.get("maps").toString();
					//rs=stmt.executeQuery("select map_type from gdms_map where );
					//if(format.contains("CMTV")){
					ArrayList strDataM=new ArrayList();
					//rs1=stmtR.executeQuery("SELECT  MAX(`mapping_data`.`start_position`) AS `max` , `mapping_data`.`linkage_group` AS Linkage_group, `mapping_data`.`linkagemap_name` AS map FROM `mapping_data` WHERE mapping_data.linkagemap_name LIKE ('"+qtl+"%') GROUP BY UCASE(`mapping_data`.`linkage_group`)");
					int countF=0;
					if(qtl.equalsIgnoreCase("*")){
						rs1=stmtR.executeQuery("SELECT  COUNT(DISTINCT `gdms_mapping_data`.`marker_id`) AS `marker_count` ,MAX(`gdms_mapping_data`.`start_position`) AS `max` , `gdms_mapping_data`.`linkage_group` AS Linkage_group, `gdms_mapping_data`.`map_name` AS map , gdms_mapping_data.map_unit AS map_unit FROM `gdms_mapping_data`, `gdms_map` WHERE gdms_mapping_data.map_id=gdms_map.map_id GROUP BY UCASE(`gdms_mapping_data`.`linkage_group`),UCASE(gdms_mapping_data.map_name) ORDER BY `gdms_mapping_data`.`map_name`, `gdms_mapping_data`.`linkage_group`");
					}else{
						rs1=stmtR.executeQuery("SELECT  COUNT(DISTINCT `gdms_mapping_data`.`marker_id`) AS `marker_count` ,MAX(`gdms_mapping_data`.`start_position`) AS `max` , `gdms_mapping_data`.`linkage_group` AS Linkage_group, `gdms_mapping_data`.`map_name` AS map , gdms_mapping_data.map_unit AS map_unit FROM `gdms_mapping_data`, `gdms_map` WHERE gdms_mapping_data.map_id=gdms_map.map_id and lower(gdms_mapping_data.map_name) LIKE ('"+qtl.toLowerCase()+"%') GROUP BY UCASE(`gdms_mapping_data`.`linkage_group`),UCASE(gdms_mapping_data.map_name) ORDER BY `gdms_mapping_data`.`map_name`, `gdms_mapping_data`.`linkage_group`");
					}
					while(rs1.next()){
						strDataM.add(rs1.getInt(1)+"!~!"+rs1.getFloat(2)+"!~!"+rs1.getString(3)+"!~!"+rs1.getString(4)+"!~!"+rs1.getString(5));
						countF=countF+1;
					}
					//System.out.println("strDataM="+strDataM);
					if(countF==0){						
						String ErrMsg = "Map Name not found";
						req.getSession().setAttribute("indErrMsg", ErrMsg);
						return am.findForward("ErrMsg");
					}
					String[] strArr=strDataM.get(0).toString().split("!~!");
					
					String chr=strArr[3];
					mCount=Integer.parseInt(strArr[0]);
					distance=Float.parseFloat(strArr[1]);
					count=0;
					int mc=0;
					float d=0;
					String distSTR="";
					String mType="";
					//System.out.println("strDataM.size()=:"+strDataM.size());
					for(int a=0;a<strDataM.size();a++){	
						String mapType="";
						String[] str1=strDataM.get(a).toString().split("!~!");		
						//System.out.println(" a="+a+" ,,,,markerCount="+str1[0]+"    ;startPosition="+str1[1]+"  ;LinkageGroup="+str1[2]+"  ;MapName="+str1[3]);
						if(str1[3].equals(chr)){
							mc=mc+Integer.parseInt(str1[0]);
							d=d+Float.parseFloat(str1[1]);	
							mType=str1[4];
							//System.out.println("..mc="+mc+"   d:"+d);
							if(a==(strDataM.size()-1)){
								//System.out.println("IF in IF "+mapType);
								mCount=mc;
								distance=d;
								mapType=mType;
								//System.out.println("IF in IF "+mapType+ ".... "+chr);
								distSTR=distSTR+mCount+"!~!"+chr+"!~!"+distance+"!~!"+mapType+";;";								
							}
						}else if(!(str1[3].equals(chr))){							
							mCount=mc;
							distance=d;
							mapType=mType;
							//System.out.println("else IF in IF "+mapType+ ".... "+chr);
							distSTR=distSTR+mCount+"!~!"+chr+"!~!"+distance+"!~!"+mapType+";;";
							mc=0;
							d=0;
							mType="";
							chr=str1[3];
							a=a-1;
						}						
					}
					session.setAttribute("mapsSTR", distSTR);
					System.out.println("........&&&&&&&&&&&&........"+distSTR);			
					session.setAttribute("type", "map");
					
					str="map";
						
					}
				
			}
			
			//System.out.println(str);
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		      		conn.close();
		      		factory.close(); 
		      		/*long time = System.currentTimeMillis();
		      	  	System.gc();
		      	  	System.out.println("It took " + (System.currentTimeMillis()-time) + " ms");*/
		         }catch(Exception e){System.out.println(e);}
			}
		return am.findForward(str);
	}
	
	

}
