package org.icrisat.gdms.delete;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener;

public class DataDeletionRetrievalAction extends Action{

	
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		//System.out.println("*************************************");
		Connection con=null;
		Connection conn=null;
		HttpSession session = req.getSession(true);
		String str="";
		if(session!=null){
			session.removeAttribute("qlist");	
			session.removeAttribute("glist");
			session.removeAttribute("mlist");
			session.removeAttribute("geno");
			session.removeAttribute("qtl");
			session.removeAttribute("map");
			session.removeAttribute("data");
		}
		//String crop=req.getSession().getAttribute("crop").toString();
		try{
			
			Properties p=new Properties();
			
			p.load(new FileInputStream(session.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
			
			String driver = "com.mysql.jdbc.Driver";
			String host=p.getProperty("central.host");
			String port=p.getProperty("central.port");
			String url = "jdbc:mysql://"+host+":"+port+"/";
			String dbName = p.getProperty("central.dbname");
			//String driver = "com.mysql.jdbc.Driver";
			String userName = p.getProperty("central.username"); 
			String password = p.getProperty("central.password");
			
			Class.forName(driver).newInstance();
			 conn = DriverManager.getConnection(url+dbName,userName,password);
			 
			 ResultSet rsC=null;
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
			 
			ResultSet rs=null;			
			Statement stmt=con.createStatement();
			
			ArrayList gList = new ArrayList();
			ArrayList qList = new ArrayList();
			ArrayList mtaList = new ArrayList();
			ArrayList mList = new ArrayList();
			DynaActionForm df = (DynaActionForm) af;	
			//String opStr=req.getQueryString().toString();
			String geno="no";
			String qtl="no";
			String map="no";
			String mta="no";
			String qtlName="";
			
			
			ArrayList gListC = new ArrayList();
			ArrayList qListC = new ArrayList();
			ArrayList mtaListC = new ArrayList();
			ArrayList mListC = new ArrayList();
			
			String genoC="no";
			String qtlC="no";
			String mapC="no";
			String mtaC="no";
			String qtlNameC="";
			
			
			
			/*if(opStr.equals("first")){
				String op=df.get("getOp").toString();
				if(op.equalsIgnoreCase("genos")){*/
			/*int dCount = gdms.countDatasetNames(Database.LOCAL);
			List<String> results = gdms.getDatasetNames(0, dCount, Database.LOCAL);
	        System.out.println("RESULTS (testGetDatasetDetailsByDatasetName): " + results);*/
					rs=stmt.executeQuery("select dataset_name from gdms_dataset where dataset_type !='qtl' and dataset_type!='MTA' ");
					while(rs.next()){
						gList.add(rs.getString(1));
						geno="yes";
					}
					
					req.getSession().setAttribute("geno", geno);
					req.getSession().setAttribute("glist", gList);
				//}else if(op.equalsIgnoreCase("qtl")){
					
					rs=stmt.executeQuery("select dataset_name, dataset_id from gdms_dataset where dataset_type ='QTL'");
					while(rs.next()){
						qList.add(rs.getString(1));
						qtl="yes";						
					}					
					req.getSession().setAttribute("qtl", qtl);
					req.getSession().setAttribute("qlist", qList);
					rs=stmt.executeQuery("select dataset_name, dataset_id from gdms_dataset where dataset_type ='MTA'");
					while(rs.next()){
						mtaList.add(rs.getString(1));
						mta="yes";						
					}
					req.getSession().setAttribute("mta", mta);
					req.getSession().setAttribute("mtalist", mtaList);
					
				//}else if(op.equalsIgnoreCase("maps")){
					rs=stmt.executeQuery("select map_name from gdms_map");
					while(rs.next()){
						mList.add(rs.getString(1));
						map="yes";
					}
					//map="yes";
					req.getSession().setAttribute("map", map);
					req.getSession().setAttribute("mlist", mList);
				//}
					String data="";
					//System.out.println("geno="+geno+"    qtl="+qtl+"    Map="+map);
					//if(geno.equalsIgnoreCase("no") && qtl.equalsIgnoreCase("no") && map.equalsIgnoreCase("no")){
					if(geno.equalsIgnoreCase("no") && qtl.equalsIgnoreCase("no") && map.equalsIgnoreCase("no")&& mta.equalsIgnoreCase("no")){
						data="no";
					}else{
						data="yes";
							
					}
					session.setAttribute("data", data);
					
					///* from central
					
					
					
					
					rsC=stCen.executeQuery("select dataset_name from gdms_dataset where dataset_type !='qtl' and dataset_type!='MTA' ");
					while(rsC.next()){
						gListC.add(rsC.getString(1));
						genoC="yes";
					}
					
					req.getSession().setAttribute("genoC", genoC);
					req.getSession().setAttribute("glistC", gListC);
				//}else if(op.equalsIgnoreCase("qtl")){
					
					rsC=stCen.executeQuery("select dataset_name, dataset_id from gdms_dataset where dataset_type ='QTL'");
					while(rsC.next()){
						qListC.add(rsC.getString(1));
						qtlC="yes";						
					}					
					req.getSession().setAttribute("qtlC", qtlC);
					req.getSession().setAttribute("qlistC", qListC);
					rsC=stCen.executeQuery("select dataset_name, dataset_id from gdms_dataset where dataset_type ='MTA'");
					while(rsC.next()){
						mtaListC.add(rsC.getString(1));
						mtaC="yes";						
					}
					req.getSession().setAttribute("mtaC", mtaC);
					req.getSession().setAttribute("mtalistC", mtaListC);
					
				//}else if(op.equalsIgnoreCase("maps")){
					rsC=stCen.executeQuery("select map_name from gdms_map");
					while(rsC.next()){
						mListC.add(rsC.getString(1));
						mapC="yes";
					}
					//map="yes";
					req.getSession().setAttribute("mapC", mapC);
					req.getSession().setAttribute("mlistC", mListC);
				//}
					String dataC="";
					//System.out.println("genoC="+genoC+"    qtlC="+qtlC+"    MapC="+mapC);
					if(genoC.equalsIgnoreCase("no") && qtlC.equalsIgnoreCase("no") && mapC.equalsIgnoreCase("no")&& mtaC.equalsIgnoreCase("no")){
						dataC="no";
					}else{
						dataC="yes";
							
					}
					session.setAttribute("dataC", dataC);					
					
					
				str="det";
			/*if(rs!=null) rs.close(); if(stmt!=null) stmt.close();*/ if(con!=null) con.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		         }catch(Exception e){System.out.println(e);}
			}
		
		return am.findForward(str);
	}
	

}
