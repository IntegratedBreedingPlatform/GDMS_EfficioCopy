/**
 * Retrieves marker information
 */
package org.icrisat.gdms.retrieve;

import java.io.File;
import java.io.FileInputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.icrisat.gdms.common.HibernateSessionFactory;

public class RetrieveMarkerDetailsAction extends Action{
	java.sql.Connection conn;
	java.sql.Connection con;
	private Session hsession;	
	private Transaction tx;
	private static WorkbenchDataManager wdm;
	private static HibernateUtil hibernateUtil;
	
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		//String crop=req.getSession().getAttribute("crop").toString();
		hsession = HibernateSessionFactory.currentSession();
		tx=hsession.beginTransaction();
		ArrayList al=new ArrayList();
		Query query=null;
		Query querysnp=null;
		Query queryssr=null;
		ArrayList mdet=new ArrayList();
		Statement stCen=null;
		Statement stLoc=null;
		ResultSet rs=null;
		ResultSet rsL=null;
		ResultSet rs2=null;
		Statement st2=null;
		String pathWB="";
		String filePathWB="";
			
		try{			
			Properties prop=new Properties();
			
			prop.load(new FileInputStream(req.getSession().getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
			String host=prop.getProperty("central.host");
			String port=prop.getProperty("central.port");
			String url = "jdbc:mysql://"+host+":"+port+"/";
			String dbName = prop.getProperty("central.dbname");
			String driver = "com.mysql.jdbc.Driver";
			String userName = prop.getProperty("central.username"); 
			String password = prop.getProperty("central.password");
			//System.out.println(host+"   "+port+"   "+url+"   "+dbName+"   "+driver+"   "+userName+"   "+password);
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);
			stCen=conn.createStatement();	
			
			
			String hostL=prop.getProperty("local.host");
			String portL=prop.getProperty("local.port");
			String urlL = "jdbc:mysql://"+hostL+":"+portL+"/";
			String dbNameL = prop.getProperty("local.dbname");
			//String driver = "com.mysql.jdbc.Driver";
			String userNameL = prop.getProperty("local.username"); 
			String passwordL = prop.getProperty("local.password");
			
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(urlL+dbNameL,userNameL,passwordL);
			stLoc=con.createStatement();
			
			st2=con.createStatement();
			DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters("DatabaseConfig.properties", "workbench");
	        hibernateUtil = new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), 
	                                workbenchDb.getUsername(), workbenchDb.getPassword());
	        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
	        wdm = new WorkbenchDataManagerImpl(sessionProvider);	
	        
	        HashMap<Object, String> IBWFProjects= new HashMap<Object, String>();
	        List<Project> projects = wdm.getProjects();
	        Long projectId = Long.valueOf(0);
	        //System.out.println("testGetProjects(): ");
	        for (Project project : projects) {
	            //System.out.println("  " + project.getLocalDbName());
	            projectId = project.getProjectId();
	            IBWFProjects.put(project.getLocalDbName(),project.getProjectId()+"-"+project.getProjectName());
	        }
			String bPath=session.getServletContext().getRealPath("//");
	        //String bPath="C:\\IBWorkflowSystem\\infrastructure\\tomcat\\webapps\\GDMS";
	        String opPath=bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1);
	       
	        pathWB=opPath+"/IBWorkflowSystem/workspace/"+IBWFProjects.get(dbNameL)+"/gdms/output/MarkerFiles";
	        //pathWB="C:/IBWorkflowSystem/workspace/1-TL1_Groundnut/gdms/output/MarkerFiles";
	       /* if(!new File(pathWB+"/MarkerFiles").exists())
		   		new File(pathWB+"/MarkerFiles").mkdir();
	        */
	        session.setAttribute("WBPath", pathWB);
	        //System.out.println(",,,,,,,,,,,,,  :"+bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1));
	       
			
			if(session!=null){
				session.removeAttribute("markerType");	
				session.removeAttribute("kbioMarkers");	
			}
			String marker_type="";
			String AnnealingTemp="",ElongationTemp="";
			String SelctedName="";
			String QueryFeilds="";
			String SearchM="";
			String Query="",Query1="";
			String value="" ,MarkerName="";
			String str="",s1="";
			
			//System.out.println("................mcount="+req.getParameter("mcount"));
			String str123="Marker Name";
			//Common feilds for all types of Data
			//String CommonFeildNames="Principal Investigator!!~~Contact!!~~Institute!!~~Incharge Person!!~~Reference!!~~Crop!!~~AccessionID!!~~Genotype";
			String CommonFeildNames="Principal Investigator!!~~Contact!!~~Institute!!~~Reference!!~~Species!!~~AccessionID!!~~Genotype";
			String fStr="";
			String strQuery="";
			String SSR="";
			String SNP="";
			String CISR="";
			String CAP="";
			String DArT="";
			//feilds to suply in query
			//String CommonFeilds="marker_user_info.principal_investigator,marker_user_info.contact,marker_user_info.institute,marker_user_info.incharge_person,marker.reference,marker.crop,marker.accession_id,marker.genotype";
			//String CommonFeilds="principal_investigator,contact,institute,incharge_person,reference,crop,accession_id,genotype";
			String CommonFeilds="principal_investigator,contact,institute,reference,species,db_accession_id,genotype";
			//String beanFeilds="rmib.getPrincipal_investigator(),rmib.getContact(),rmib.getInstitute(),rmib.getIncharge_person(),rmib.getReference(),rmib.getCrop(),rmib.getAccession_id(),rmib.getGenotype()";
			
			
			//retrieve selected feilds
			String SelectedOption=req.getParameter("SelectedOption");
			//System.out.println("SelectedOption="+SelectedOption);
			int a=3;int Geno_position=-1;
			//boolean cmap=false;
			boolean AllFeilds=false;
			
			String[] str123Arr=CommonFeildNames.split("!!~~");				
			String[]CommonFeildsArr=CommonFeilds.split(",");				
			
			String[] SelectedFeildsList=req.getParameter("SelectedFeilds").split(",");			
			if(SelectedFeildsList==null){					
				QueryFeilds="";
			}else{
				for(int k=0;k<SelectedFeildsList.length;k++){
					if(SelectedFeildsList[k].equals("All")){
						//database feilds for SSR table 
						//SSR="Assay Type!!~~Repeat!!~~No of Repeats!!~~SSR Type!!~~Sequence!!~~Sequence Length!!~~Min Allele!!~~Max Allele!!~~SSR number!!~~Size of Repeat Motif!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Primer Length!!~~Forward Primer Temperature!!~~Reverse Primer Temperature!!~~Annealing Temperature!!~~Elongation Temperature!!~~Fragment Size Expected!!~~Fragment Size Observed!!~~Amplification";
						SSR="Assay Type!!~~Motif!!~~No of Repeats!!~~Motif Type!!~~Sequence!!~~Sequence Length!!~~Min Allele!!~~Max Allele!!~~SSR number!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Forward Primer Temperature!!~~Reverse Primer Temperature!!~~Annealing Temperature!!~~Elongation Temperature!!~~Fragment Size Expected!!~~Fragment Size Observed!!~~Amplification";
						//database feilds for SNP table
						SNP="Assay Type!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Expected Product Size!!~~Position on Refrence Sequence!!~~Motif!!~~Annealing Temperature!!~~Sequence";
						
						//CISR="Assay Type!!~~Repeat!!~~No of Repeats!!~~Sequence!!~~Sequence Length!!~~Min Allele!!~~Max Allele!!~~Size of Repeat Motif!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Primer Length!!~~Forward Primer Temperature!!~~Reverse Primer Temperature!!~~Annealing Temperature!!~~Fragment Size Expected!!~~Amplification";
						CISR="Assay Type!!~~Motif!!~~No of Repeats!!~~Sequence!!~~Sequence Length!!~~Min Allele!!~~Max Allele!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Forward Primer Temperature!!~~Reverse Primer Temperature!!~~Annealing Temperature!!~~Fragment Size Expected!!~~Amplification";
						
						//CAP="Assay Type!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Expected Product Size!!~~Restriction enzyme for assay!!~~Position on Refrence Sequence!!~~Motif!!~~Annealing Temperature!!~~Sequence";
						CAP="Assay Type!!~~Forward Primer!!~~Reverse Primer!!~~Product Size!!~~Expected Product Size!!~~Position on Refrence Sequence!!~~Motif!!~~Annealing Temperature!!~~Sequence";
						
						
						
						a=11;
						str123=str123+"!!~~"+"MarkerType"+"!!~~"+CommonFeildNames;
						QueryFeilds=","+CommonFeilds;
						strQuery="select *";
						//cmap=true;
						//cmap_position=12;
						AllFeilds=true;
						break;
					}else{
						a++;
						//System.out.println("str123Arr["+Integer.parseInt(SelectedFeildsList[k])+"] == "+str123Arr[Integer.parseInt(SelectedFeildsList[k])]);
						str123=str123+"!!~~"+str123Arr[Integer.parseInt(SelectedFeildsList[k])];
						QueryFeilds=QueryFeilds+","+CommonFeildsArr[Integer.parseInt(SelectedFeildsList[k])];
						//strQuery="Select marker_id,marker_name,marker_type,"+QueryFeilds.substring(1)+",genotypes_count";
						strQuery="Select "+QueryFeilds.substring(1)+",genotypes_count,marker_type,marker_id,marker_name";
					}
				}
			}
			Geno_position=a-2;
			str123=str123+"!!~~GenotypeCount!!~~";
			
			
			//if the selected Query option is 'Quick Search'
			if(SelectedOption.equals("QuickSearch")){
				session.setAttribute("searchType","QuickSearch");
				String QuickSearch =req.getParameter("QuickSearch");
				if(QuickSearch.equalsIgnoreCase("Accession_ID"))
					//QuickSearch="genotype";
					QuickSearch="db_accession_id";
				
				//System.out.println("..................................:"+QuickSearch);
				SearchM=req.getParameter("SearchMark");
				s1=s1+" LOWER("+QuickSearch+")like LOWER('"+SearchM+"%')";
				s1="where "+s1;
				session.setAttribute("quickSearchArg", SearchM);
			}else if(SelectedOption.equals("main")){
				int count=0;
				String opType="";
				session.setAttribute("searchType","conditional");
				String ampType="";
				//if the selected Query option is 'Conditional Search'				
				String []value12 =req.getParameterValues("main");
				String next="";
				int opCount=value12.length;
				for(int l=0;l<value12.length;l++){
					str=str+value12[l]+",";
				}
				str=str.substring(0,str.length()-1);
				String step="first";
				s1=s1+"inner join gdms_marker_details on gdms_marker_details.Marker_id=gdms_marker_retrieval_info.marker_id where (";
				if(str.contains("SearchMarker")){
					if(session!=null){
						session.removeAttribute("next1");			
					}
					count=count+1;
					if(!(s1.equals(""))){
						s1=s1+next;
					}
					SearchM =req.getParameter("MarkNm");			
					s1=s1+" LOWER(gdms_marker_retrieval_info.marker_name) like LOWER('"+SearchM+"%')";
					next=req.getParameter("MarkerOption");
					opType=opType+"marker"+"!!~~";
					step="second";
					session.setAttribute("next1", next);
				}
				if(str.contains("Amplified")){
					if(session!=null){
						session.removeAttribute("next2");			
					}
					count=count+1;
					String amp =req.getParameter("Amp");
					next=req.getParameter("AmplificationOption");
					if(amp.equals("unAmplified")){
						ampType="NA";
					}else{
						ampType="A";
					}
					
					if(!(step.equalsIgnoreCase("first")))
						s1=s1+next;
					s1=s1+" gdms_marker_retrieval_info.amplification like '"+ampType+"' ";
					
					
					session.setAttribute("quickSearchArg", amp);
					opType=opType+"ampAlso"+"!!~~";
					session.setAttribute("next2", next);
				}			
				if(str.contains("Annealing")){
					count=count+1;
					opType=opType+"annAlso"+"!!~~";
					String StartTemp =req.getParameter("StartRange");
					String EndTemp =req.getParameter("EndRange");
					session.setAttribute("temp", StartTemp+"!!~~!!"+EndTemp);
					if(!(s1.equals(""))){
						s1=s1+next;
					}
					s1=s1+" ((gdms_marker_retrieval_info.Annealing_Temp BETWEEN "+ StartTemp+" AND "+ EndTemp+") or (((gdms_marker_details.Forward_Primer_Temp+gdms_marker_details.Reverse_Primer_Temp)/2)-5) BETWEEN "+ StartTemp+" AND "+ EndTemp+")";
				}
				session.setAttribute("quickSearchOp", opType);
				s1=s1+")";
				/*if(!(s1.equals(""))){
					s1="inner join gdms_marker_details on gdms_marker_details.Marker_id=gdms_marker_retrieval_info.marker_id where "+s1; 
				}*/
				session.setAttribute("marker", SearchM);
				session.setAttribute("count", count);
			}else{
				session.setAttribute("searchType","QuickSearch");
				//if the selected Query option is 'Search By'
				//System.out.println("********************************************************");
				if(req.getParameter("type").equalsIgnoreCase("Accession_ID"))
					//value="genotype";
					value="db_accession_id";
				else
					value =req.getParameter("type");
				SelctedName=req.getParameter("List1");
				/*System.out.println("SelctedName == "+SelctedName+" ,value == "+value);
				
				System.out.println("list2="+req.getParameterValues("List2"));
				System.out.println("list3="+req.getParameterValues("List3"));*/
				//if(!(value.equals("MarkerName"))){
					String[] mname=req.getParameterValues("List3");
					//System.out.println(mname.length);
					//session.setAttribute("mcount", mname.length);
					for(int p=0;p<mname.length;p++){
						//System.out.println("vals[p]");
						MarkerName=MarkerName+"'"+mname[p]+"',";
					}
					MarkerName=MarkerName.substring(0, (MarkerName.length()-1));	
					//System.out.println("MarkerName="+MarkerName);
					
					s1=s1+" "+value+" ='"+SelctedName+"' and Marker_Name in ("+MarkerName+")";
					
					s1="where "+s1;				
			}
			//System.out.println("S1="+s1);
			if(SelctedName.equals("")){		
				if(!(SearchM.equals(""))){
					//Statement markerTypeSt=con.createStatement();
					ResultSet markerTypeRS=stCen.executeQuery("select gdms_marker.marker_type from gdms_marker where gdms_marker.marker_name='"+SearchM+"'");
					while(markerTypeRS.next()){
						SelctedName=markerTypeRS.getString(1);
					}
					ResultSet mTypeRS=stLoc.executeQuery("select gdms_marker.marker_type from gdms_marker where gdms_marker.marker_name='"+SearchM+"'");
					while(mTypeRS.next()){
						if(SelctedName.equals(""))
							SelctedName=mTypeRS.getString(1);
					}
					/*query=hsession.createQuery("from Markers where marker_name ='"+SearchM+"'");
					mdet=(ArrayList)query.list();
					for(Iterator iterator=mdet.iterator();iterator.hasNext();){
						RetrievalMarkers rmb=(RetrievalMarkers) iterator.next();
						//marker_id=rmb.getMarker_id();
						SelctedName=rmb.getMarker_type();
					}*/
					marker_type=SelctedName;
				}else{
					SelctedName="SSR";
					marker_type="SSR";
				}
			}
			marker_type="SSR";
			//System.out.println("marker type="+marker_type);
			Query=strQuery+" from gdms_marker_retrieval_info "+s1;
			//query=hsession.createQuery(strQuery+" from RetrievalMarkerInfoBean where marker_name ='"+SearchM+"'");
			//System.out.println("Query="+Query);
			Statement st=conn.createStatement();			
			//Statement st1=con.createStatement();			
			ArrayList SNPlist=new ArrayList();
			ArrayList SSRlist=new ArrayList();
			ArrayList CAPlist=new ArrayList();
			ArrayList CISRlist=new ArrayList();
			int Count=0;
			int GenotypeCount=0;
			//ArrayList kbioMarkers=new ArrayList();
			String kbioMarkers="";
			rs=st.executeQuery(Query);			
			while (rs.next()){		
				if(Count==0){
					SSRlist.add(str123+SSR);
					SNPlist.add(str123+SNP);
					CAPlist.add(str123+CAP);
					CISRlist.add(str123+CISR);
				}	
				str123="";
				Count++;
				if(rs.getString("marker_type").equalsIgnoreCase("snp")){
					//kbioMarkers.add(rs.getString("marker_name"));
					marker_type="snp";
					kbioMarkers=kbioMarkers+"'"+rs.getString("marker_name")+"',";
					
				}
				GenotypeCount=rs.getInt("genotypes_count");
				//System.out.println("AllFeilds="+AllFeilds);
				if(AllFeilds==true){
					//marker_type=;
					//str123=str123+rs.getString(3)+"!!~~"+rs.getString(9)+"!!~~"+rs.getString(10)+"!!~~"+rs.getString(11)+"!!~~"+rs.getString(12)+"!!~~"+rs.getString(6)+"!!~~"+rs.getString(4)+"!!~~"+rs.getString(5)+"!!~~"+rs.getString(7)+"!!~~"+rs.getString(13)+"!~!"+rs.getString(1);
					//str123=str123+rs.getString(3)+"!!~~"+rs.getString(9)+"!!~~"+rs.getString(10)+"!!~~"+rs.getString(11)+"!!~~"+rs.getString(6)+"!!~~"+rs.getString(4)+"!!~~"+rs.getString(5)+"!!~~"+rs.getString(7)+"!!~~"+rs.getString(12)+"!~!"+rs.getString(1);
					str123=str123+rs.getString(3)+"!!~~"+rs.getString(2)+"!!~~"+rs.getString(15)+"!!~~"+rs.getString(16)+"!!~~"+rs.getString(17)+"!!~~"+rs.getString(6)+"!!~~"+rs.getString(4)+"!!~~"+rs.getString(5)+"!!~~"+rs.getString(7)+"!!~~"+GenotypeCount+"!~!"+rs.getString(1);
				}else{
					String[] qf=QueryFeilds.split(",");
					fStr="";
					for(int i=1;i<=qf.length;i++){
						fStr=fStr+rs.getString(i)+"!!~~";	
						//System.out.println(">>>>>>>>>>>>>>>>   "+MarkerName+"   "+MarkerName1);				
					}
					//System.out.println("fStr="+fStr);
					marker_type=rs.getString("marker_type");				
					str123=rs.getString("marker_name")+"!!~~"+fStr.substring(0,fStr.length()-4)+"!~!"+rs.getString("marker_id")+"!!~~";
				}
					
				//str123=str123+GenotypeCount+"!~!"+rs.getInt(1)+"!!~~";
				if(AllFeilds==true){				
					
					rs2=null;
					if(rs.getString("marker_type").equalsIgnoreCase("SSR")){
						int markerId=rs.getInt(1);
						//rs2=st2.executeQuery("Select assay_type,repeats,no_of_repeats,ssr_type,sequence,sequence_length,min_allele,max_allele,ssr_nr,size_of_repeat_motif,forward_primer,reverse_primer,product_size,primer_length,forward_primer_temp,reverse_primer_temp,annealing_temp,elongation_temp,fragment_size_expected,fragment_size_observed,amplification from ssr_marker where marker_id='"+rs.getString(1)+"'");
						rs2=stCen.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.motif,gdms_marker_details.no_of_repeats,gdms_marker_details.motif_type,gdms_marker_details.sequence,gdms_marker_details.sequence_length,gdms_marker_details.min_allele,gdms_marker_details.max_allele,gdms_marker_details.ssr_nr,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.forward_primer_temp,gdms_marker_details.reverse_primer_temp,gdms_marker.annealing_temp,gdms_marker_details.elongation_temp,gdms_marker_details.fragment_size_expected,gdms_marker_details.fragment_size_observed,gdms_marker.amplification FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rs.getString(1));
						//System.out.println("SELECT gdms_marker.assay_type,gdms_marker.motif,gdms_marker_details.no_of_repeats,gdms_marker_details.motif_type,gdms_marker_details.sequence,gdms_marker_details.sequence_length,gdms_marker_details.min_allele,gdms_marker_details.max_allele,gdms_marker_details.ssr_nr,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.forward_primer_temp,gdms_marker_details.reverse_primer_temp,gdms_marker.annealing_temp,gdms_marker_details.elongation_temp,gdms_marker_details.fragment_size_expected,gdms_marker_details.fragment_size_observed,gdms_marker.amplification FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+markerId);
						ResultSet rs2L=stLoc.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.motif,gdms_marker_details.no_of_repeats,gdms_marker_details.motif_type,gdms_marker_details.sequence,gdms_marker_details.sequence_length,gdms_marker_details.min_allele,gdms_marker_details.max_allele,gdms_marker_details.ssr_nr,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.forward_primer_temp,gdms_marker_details.reverse_primer_temp,gdms_marker.annealing_temp,gdms_marker_details.elongation_temp,gdms_marker_details.fragment_size_expected,gdms_marker_details.fragment_size_observed,gdms_marker.amplification FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+markerId);
						if(rs2.next()){
							if((rs2.getString(15).equals("0"))||(rs2.getString(15).equals(""))||(rs2.getString(15)==null)){								
								double val1=0;
								double val2=0;
								double temp=0;
								if(!rs2.getString(13).equals("0")){
									val1=Double.parseDouble(rs2.getString(14));
								}
								if(!rs2.getString(14).equals("0")){
									val2=Double.parseDouble(rs2.getString(15));
								}
								if((rs2.getString(13).equals("0"))&&(rs2.getString(14).equals("0"))){
									temp=0;
								}else{
									temp= Math.round((((val1+val2)/2)-5)*100.0)/100.0;
								}								
								AnnealingTemp=String.valueOf(temp);
							}else{
								AnnealingTemp=rs2.getString(15);						
							}
							if((rs2.getString(16)==null)||rs2.getString(16).equals("")||rs2.getString(16).equals("null")){
								ElongationTemp="72c";
							}else{
								ElongationTemp=rs2.getString(16);
							}
							//str123="";							
							//str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getString(5)+"!!~~"+rs2.getString(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getString(8)+"!!~~"+rs2.getString(9)+"!!~~"+rs2.getString(10)+"!!~~"+rs2.getString(11)+"!!~~"+rs2.getString(12)+"!!~~"+rs2.getString(13)+"!!~~"+rs2.getString(14)+"!!~~"+rs2.getString(15)+"!!~~"+rs2.getString(16)+"!!~~"+AnnealingTemp+"!!~~"+ElongationTemp+"!!~~"+rs2.getString(19)+"!!~~"+rs2.getString(20)+"!!~~"+rs2.getString(21);
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getString(5)+"!!~~"+rs2.getString(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getString(8)+"!!~~"+rs2.getString(9)+"!!~~"+rs2.getString(10)+"!!~~"+rs2.getString(11)+"!!~~"+rs2.getString(12)+"!!~~"+rs2.getString(13)+"!!~~"+rs2.getString(14)+"!!~~"+AnnealingTemp+"!!~~"+ElongationTemp+"!!~~"+rs2.getString(17)+"!!~~"+rs2.getString(18)+"!!~~"+rs2.getString(19);
							//System.out.println(str123);							
						}
						if(rs2L.next()){
							if((rs2L.getString(15).equals("0"))||(rs2L.getString(15).equals(""))||(rs2L.getString(15)==null)){								
								double val1=0;
								double val2=0;
								double temp=0;
								if(!rs2L.getString(13).equals("0")){
									val1=Double.parseDouble(rs2L.getString(14));
								}
								if(!rs2L.getString(14).equals("0")){
									val2=Double.parseDouble(rs2L.getString(15));
								}
								if((rs2L.getString(13).equals("0"))&&(rs2L.getString(14).equals("0"))){
									temp=0;
								}else{
									temp= Math.round((((val1+val2)/2)-5)*100.0)/100.0;
								}								
								AnnealingTemp=String.valueOf(temp);
							}else{
								AnnealingTemp=rs2L.getString(15);						
							}
							if((rs2L.getString(16)==null)||rs2L.getString(16).equals("")||rs2L.getString(16).equals("null")){
								ElongationTemp="72c";
							}else{
								ElongationTemp=rs2L.getString(16);
							}
							//str123="";							
							//str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getString(5)+"!!~~"+rs2.getString(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getString(8)+"!!~~"+rs2.getString(9)+"!!~~"+rs2.getString(10)+"!!~~"+rs2.getString(11)+"!!~~"+rs2.getString(12)+"!!~~"+rs2.getString(13)+"!!~~"+rs2.getString(14)+"!!~~"+rs2.getString(15)+"!!~~"+rs2.getString(16)+"!!~~"+AnnealingTemp+"!!~~"+ElongationTemp+"!!~~"+rs2.getString(19)+"!!~~"+rs2.getString(20)+"!!~~"+rs2.getString(21);
							str123=str123+"!!~~"+rs2L.getString(1)+"!!~~"+rs2L.getString(2)+"!!~~"+rs2L.getString(3)+"!!~~"+rs2L.getString(4)+"!!~~"+rs2L.getString(5)+"!!~~"+rs2L.getString(6)+"!!~~"+rs2L.getString(7)+"!!~~"+rs2L.getString(8)+"!!~~"+rs2L.getString(9)+"!!~~"+rs2L.getString(10)+"!!~~"+rs2L.getString(11)+"!!~~"+rs2L.getString(12)+"!!~~"+rs2L.getString(13)+"!!~~"+rs2L.getString(14)+"!!~~"+AnnealingTemp+"!!~~"+ElongationTemp+"!!~~"+rs2L.getString(17)+"!!~~"+rs2L.getString(18)+"!!~~"+rs2L.getString(19);
							//System.out.println(str123);							
						}
						SSRlist.add(str123);
					}else if (rs.getString("marker_type").equalsIgnoreCase("SNP")){
						
						rs2=stCen.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.expected_product_size,gdms_marker_details.position_on_reference_sequence,gdms_marker.motif,gdms_marker.annealing_temp,gdms_marker_details.sequence FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rs.getString(1));
						ResultSet rs2L=stLoc.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.expected_product_size,gdms_marker_details.position_on_reference_sequence,gdms_marker.motif,gdms_marker.annealing_temp,gdms_marker_details.sequence FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rs.getString(1));
						if(rs2.next()){
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getInt(5)+"!!~~"+rs2.getInt(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getFloat(8)+"!!~~"+rs2.getString(9);
						}
						if(rs2L.next()){
							str123=str123+"!!~~"+rs2L.getString(1)+"!!~~"+rs2L.getString(2)+"!!~~"+rs2L.getString(3)+"!!~~"+rs2L.getString(4)+"!!~~"+rs2L.getInt(5)+"!!~~"+rs2L.getInt(6)+"!!~~"+rs2L.getString(7)+"!!~~"+rs2L.getFloat(8)+"!!~~"+rs2L.getString(9);
						}
						SNPlist.add(str123);
					}else if(rs.getString("marker_type").equalsIgnoreCase("CISR")){
						//System.out.println("Select assay_type,repeats,no_of_repeats,sequence,sequence_length,min_allele,max_allele,size_of_repeat_motif,forward_primer,reverse_primer,product_size,primer_length,forward_primer_temp,reverse_primer_temp,annealing_temp,fragment_size_expected,amplification from ssr_marker where marker_id='"+rs.getString(1)+"'");
						//rs2=st2.executeQuery("Select assay_type,repeats,no_of_repeats,sequence,sequence_length,min_allele,max_allele,size_of_repeat_motif,forward_primer,reverse_primer,product_size,primer_length,forward_primer_temp,reverse_primer_temp,annealing_temp,fragment_size_expected,amplification from ssr_marker where marker_id='"+rs.getString(1)+"'");
						rs2=st2.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.motif,gdms_marker_details.no_of_repeats,gdms_marker_details.sequence,gdms_marker_details.sequence_length,gdms_marker_details.min_allele,gdms_marker_details.max_allele,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.forward_primer_temp,gdms_marker_details.reverse_primer_temp,gdms_marker.annealing_temp,gdms_marker_details.fragment_size_expected,gdms_marker.amplification FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rs.getString(1));
						if(rs2.next()){
							if((rs2.getString(13).equals("0"))||(rs2.getString(13).equals(""))||(rs2.getString(13)==null)){								
								double val1=0;
								double val2=0;
								double temp=0;
								if(!rs2.getString(11).equals("0")){
									val1=Double.parseDouble(rs2.getString(12));
								}
								if(!rs2.getString(12).equals("0")){
									val2=rs2.getDouble(11);
								}
								if((rs2.getString(11).equals("0"))&&(rs2.getString(12).equals("0"))){
									temp=0;
								}else{
									temp= Math.round((((val1+val2)/2)-5)*100.0)/100.0;
								}								
								AnnealingTemp=String.valueOf(temp);
							}else{
								AnnealingTemp=rs2.getString(13);						
							}
							/*if((rs2.getString(18)==null)||rs2.getString(18).equals("")||rs2.getString(18).equals("null")){
								ElongationTemp="72c";
							}else{
								ElongationTemp=rs2.getString(18);
							}*/
							//str123="";							
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getString(5)+"!!~~"+rs2.getString(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getString(8)+"!!~~"+rs2.getString(9)+"!!~~"+rs2.getString(10)+"!!~~"+rs2.getString(11)+"!!~~"+rs2.getString(12)+"!!~~"+AnnealingTemp+"!!~~"+rs2.getString(14)+"!!~~"+rs2.getString(15);
							//System.out.println(str123);							
						}
						CISRlist.add(str123);
					}else if (rs.getString("marker_type").equalsIgnoreCase("CAP")){						
						rs2=st2.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.expected_product_size,gdms_marker_details.position_on_reference_sequence,gdms_marker.motif,gdms_marker.annealing_temp,gdms_marker_details.sequence FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rs.getString(1));
						if(rs2.next()){
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getInt(5)+"!!~~"+rs2.getInt(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getFloat(8)+"!!~~"+rs2.getString(9);
						}
						
						CAPlist.add(str123);
					}			
				}else{
					if(marker_type.equalsIgnoreCase("SSR")){
						SSRlist.add(str123);
					}else if (marker_type.equalsIgnoreCase("SNP")){
						SNPlist.add(str123);
					}else if(marker_type.equalsIgnoreCase("CISR")){
						CISRlist.add(str123);
					}else if (marker_type.equalsIgnoreCase("CAP")){
						CAPlist.add(str123);
					}					
				}
				session.setAttribute("kbioMarkers", kbioMarkers);
				session.setAttribute("markerType", marker_type);
			}
			rsL=stLoc.executeQuery(Query);
			while (rsL.next()){		
				if(Count==0){
					SSRlist.add(str123+SSR);
					SNPlist.add(str123+SNP);
					CAPlist.add(str123+CAP);
					CISRlist.add(str123+CISR);
				}	
				str123="";
				Count++;
				if(rsL.getString("marker_type").equalsIgnoreCase("snp")){
					//kbioMarkers.add(rs.getString("marker_name"));
					marker_type="snp";
					kbioMarkers=kbioMarkers+"'"+rsL.getString("marker_name")+"',";
					
				}
				GenotypeCount=rsL.getInt("genotypes_count");
				//System.out.println("AllFeilds="+AllFeilds);
				if(AllFeilds==true){
					//marker_type=;
					//str123=str123+rsL.getString(3)+"!!~~"+rsL.getString(9)+"!!~~"+rsL.getString(10)+"!!~~"+rsL.getString(11)+"!!~~"+rsL.getString(12)+"!!~~"+rsL.getString(6)+"!!~~"+rsL.getString(4)+"!!~~"+rsL.getString(5)+"!!~~"+rsL.getString(7)+"!!~~"+rsL.getString(13)+"!~!"+rsL.getString(1);
					//str123=str123+rsL.getString(3)+"!!~~"+rsL.getString(9)+"!!~~"+rsL.getString(10)+"!!~~"+rsL.getString(11)+"!!~~"+rsL.getString(6)+"!!~~"+rsL.getString(4)+"!!~~"+rsL.getString(5)+"!!~~"+rsL.getString(7)+"!!~~"+rsL.getString(12)+"!~!"+rsL.getString(1);
					str123=str123+rsL.getString(3)+"!!~~"+rsL.getString(2)+"!!~~"+rsL.getString(15)+"!!~~"+rsL.getString(16)+"!!~~"+rsL.getString(17)+"!!~~"+rsL.getString(6)+"!!~~"+rsL.getString(4)+"!!~~"+rsL.getString(5)+"!!~~"+rsL.getString(7)+"!!~~"+GenotypeCount+"!~!"+rsL.getString(1);
				}else{
					String[] qf=QueryFeilds.split(",");
					fStr="";
					for(int i=1;i<=qf.length;i++){
						fStr=fStr+rsL.getString(i)+"!!~~";	
						//System.out.println(">>>>>>>>>>>>>>>>   "+MarkerName+"   "+MarkerName1);				
					}
					//System.out.println("fStr="+fStr);
					marker_type=rsL.getString("marker_type");				
					str123=rsL.getString("marker_name")+"!!~~"+fStr.substring(0,fStr.length()-4)+"!~!"+rsL.getString("marker_id")+"!!~~";
				}
					
				//str123=str123+GenotypeCount+"!~!"+rsL.getInt(1)+"!!~~";
				if(AllFeilds==true){				
					
					rs2=null;
					if(rsL.getString("marker_type").equalsIgnoreCase("SSR")){
						//rs2=st2.executeQuery("Select assay_type,repeats,no_of_repeats,ssr_type,sequence,sequence_length,min_allele,max_allele,ssr_nr,size_of_repeat_motif,forward_primer,reverse_primer,product_size,primer_length,forward_primer_temp,reverse_primer_temp,annealing_temp,elongation_temp,fragment_size_expected,fragment_size_observed,amplification from ssr_marker where marker_id='"+rs.getString(1)+"'");
						rs2=st2.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.motif,gdms_marker_details.no_of_repeats,gdms_marker_details.motif_type,gdms_marker_details.sequence,gdms_marker_details.sequence_length,gdms_marker_details.min_allele,gdms_marker_details.max_allele,gdms_marker_details.ssr_nr,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.forward_primer_temp,gdms_marker_details.reverse_primer_temp,gdms_marker.annealing_temp,gdms_marker_details.elongation_temp,gdms_marker_details.fragment_size_expected,gdms_marker_details.fragment_size_observed,gdms_marker.amplification FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rsL.getString(1));
						if(rs2.next()){
							if((rs2.getString(15).equals("0"))||(rs2.getString(15).equals(""))||(rs2.getString(15)==null)){								
								double val1=0;
								double val2=0;
								double temp=0;
								if(!rs2.getString(13).equals("0")){
									val1=Double.parseDouble(rs2.getString(14));
								}
								if(!rs2.getString(14).equals("0")){
									val2=Double.parseDouble(rs2.getString(15));
								}
								if((rs2.getString(13).equals("0"))&&(rs2.getString(14).equals("0"))){
									temp=0;
								}else{
									temp= Math.round((((val1+val2)/2)-5)*100.0)/100.0;
								}								
								AnnealingTemp=String.valueOf(temp);
							}else{
								AnnealingTemp=rs2.getString(15);						
							}
							if((rs2.getString(16)==null)||rs2.getString(16).equals("")||rs2.getString(16).equals("null")){
								ElongationTemp="72c";
							}else{
								ElongationTemp=rs2.getString(16);
							}
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getString(5)+"!!~~"+rs2.getString(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getString(8)+"!!~~"+rs2.getString(9)+"!!~~"+rs2.getString(10)+"!!~~"+rs2.getString(11)+"!!~~"+rs2.getString(12)+"!!~~"+rs2.getString(13)+"!!~~"+rs2.getString(14)+"!!~~"+AnnealingTemp+"!!~~"+ElongationTemp+"!!~~"+rs2.getString(17)+"!!~~"+rs2.getString(18)+"!!~~"+rs2.getString(19);							
						}
						SSRlist.add(str123);
					}else if (rsL.getString("marker_type").equalsIgnoreCase("SNP")){						
						rs2=st2.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.expected_product_size,gdms_marker_details.position_on_reference_sequence,gdms_marker.motif,gdms_marker.annealing_temp,gdms_marker_details.sequence FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rsL.getString(1));
						if(rs2.next()){
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getInt(5)+"!!~~"+rs2.getInt(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getFloat(8)+"!!~~"+rs2.getString(9);
						}
						SNPlist.add(str123);
					}else if(rsL.getString("marker_type").equalsIgnoreCase("CISR")){
						rs2=st2.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.motif,gdms_marker_details.no_of_repeats,gdms_marker_details.sequence,gdms_marker_details.sequence_length,gdms_marker_details.min_allele,gdms_marker_details.max_allele,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.forward_primer_temp,gdms_marker_details.reverse_primer_temp,gdms_marker.annealing_temp,gdms_marker_details.fragment_size_expected,gdms_marker.amplification FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rsL.getString(1));
						if(rs2.next()){
							if((rs2.getString(13).equals("0"))||(rs2.getString(13).equals(""))||(rs2.getString(13)==null)){								
								double val1=0;
								double val2=0;
								double temp=0;
								if(!rs2.getString(11).equals("0")){
									val1=Double.parseDouble(rs2.getString(12));
								}
								if(!rs2.getString(12).equals("0")){
									val2=rs2.getDouble(11);
								}
								if((rs2.getString(11).equals("0"))&&(rs2.getString(12).equals("0"))){
									temp=0;
								}else{
									temp= Math.round((((val1+val2)/2)-5)*100.0)/100.0;
								}								
								AnnealingTemp=String.valueOf(temp);
							}else{
								AnnealingTemp=rs2.getString(13);						
							}														
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getString(5)+"!!~~"+rs2.getString(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getString(8)+"!!~~"+rs2.getString(9)+"!!~~"+rs2.getString(10)+"!!~~"+rs2.getString(11)+"!!~~"+rs2.getString(12)+"!!~~"+AnnealingTemp+"!!~~"+rs2.getString(14)+"!!~~"+rs2.getString(15);
												
						}
						CISRlist.add(str123);
					}else if (rsL.getString("marker_type").equalsIgnoreCase("CAP")){						
						rs2=st2.executeQuery("SELECT gdms_marker.assay_type,gdms_marker.forward_primer,gdms_marker.reverse_primer,gdms_marker.product_size,gdms_marker_details.expected_product_size,gdms_marker_details.position_on_reference_sequence,gdms_marker.motif,gdms_marker.annealing_temp,gdms_marker_details.sequence FROM gdms_marker JOIN gdms_marker_details ON gdms_marker.marker_id=gdms_marker_details.marker_id WHERE gdms_marker.marker_id="+rsL.getString(1));
						if(rs2.next()){
							str123=str123+"!!~~"+rs2.getString(1)+"!!~~"+rs2.getString(2)+"!!~~"+rs2.getString(3)+"!!~~"+rs2.getString(4)+"!!~~"+rs2.getInt(5)+"!!~~"+rs2.getInt(6)+"!!~~"+rs2.getString(7)+"!!~~"+rs2.getFloat(8)+"!!~~"+rs2.getString(9);
						}
						
						CAPlist.add(str123);
					}			
				}else{
					if(marker_type.equalsIgnoreCase("SSR")){
						SSRlist.add(str123);
					}else if (marker_type.equalsIgnoreCase("SNP")){
						SNPlist.add(str123);
					}else if(marker_type.equalsIgnoreCase("CISR")){
						CISRlist.add(str123);
					}else if (marker_type.equalsIgnoreCase("CAP")){
						CAPlist.add(str123);
					}					
				}
				session.setAttribute("kbioMarkers", kbioMarkers);
				session.setAttribute("markerType", marker_type);
			}
			//System.out.println(SSRlist);
			if(SSRlist.size()>1){
				al.add(SSRlist);
			}
			if(SNPlist.size()>1){
				al.add(SNPlist);
			}
			if(CISRlist.size()>1){
				al.add(CISRlist);
			}
			if(CAPlist.size()>1){
				al.add(CAPlist);
			}
			session.setAttribute("al", al);
			/*if(rs!=null) rs.close(); if(rsL!=null) rsL.close(); if(rs2!=null) rs2.close();
			if(stCen!=null) stCen.close(); if(stLoc!=null) stLoc.close(); if(st2!=null) st2.close();*/
			if(conn!=null) con.close(); if(con!=null)con.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(con!=null) con.close();hsession.disconnect();
				if(conn!=null) conn.close();
				if(stCen!=null)stCen.close();
				if(stLoc!=null)stLoc.close();
				if(rs!=null)rs.close();
				if(rsL!=null)rsL.close();
			}catch(Exception e){System.out.println(e);}
		}	
		return am.findForward("ret");
	}

}
