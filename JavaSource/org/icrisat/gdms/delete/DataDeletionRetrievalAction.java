package org.icrisat.gdms.delete;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
			ManagerFactory factory = null;
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			
			
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters("DatabaseConfig.properties", "local");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters("DatabaseConfig.properties", "central");
			
			factory = new ManagerFactory(local, central);*/
			/*factory = MiddlewareServletRequestListener.getManagerFactoryForRequest(req);
			GenotypicDataManager gdms=factory.getGenotypicDataManager();
			*/
			
			ResultSet rs=null;
			ResultSet rs1=null;
			Statement stmt=con.createStatement();
			Statement st=con.createStatement();
			ArrayList gList = new ArrayList();
			ArrayList qList = new ArrayList();
			ArrayList mList = new ArrayList();
			DynaActionForm df = (DynaActionForm) af;	
			//String opStr=req.getQueryString().toString();
			String geno="no";
			String qtl="no";
			String map="no";
			String qtlName="";
			/*if(opStr.equals("first")){
				String op=df.get("getOp").toString();
				if(op.equalsIgnoreCase("genos")){*/
			/*int dCount = gdms.countDatasetNames(Database.LOCAL);
			List<String> results = gdms.getDatasetNames(0, dCount, Database.LOCAL);
	        System.out.println("RESULTS (testGetDatasetDetailsByDatasetName): " + results);*/
					rs=stmt.executeQuery("select dataset_name from gdms_dataset where dataset_type !='qtl'");
					while(rs.next()){
						gList.add(rs.getString(1));
						geno="yes";
					}
					
					req.getSession().setAttribute("geno", geno);
					req.getSession().setAttribute("glist", gList);
				//}else if(op.equalsIgnoreCase("qtl")){
					
					rs=stmt.executeQuery("select dataset_name, dataset_id from gdms_dataset where dataset_type ='qtl'");
					while(rs.next()){
						qList.add(rs.getString(1));
						qtl="yes";
						//rs1=st.executeQuery("select qtl_)
						/*rs1=st.executeQuery("select qtl_name from qtl where dataset_id="+rs.getInt(2));
						while(rs1.next()){
							qtlName=qtlName+rs1.getString(1)+", ";
						}
						qList.add(rs.getString(1)+"("+qtlName+")");*/
					}
					
					req.getSession().setAttribute("qtl", qtl);
					req.getSession().setAttribute("qlist", qList);
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
					System.out.println("geno="+geno+"    qtl="+qtl+"    Map="+map);
					if(geno.equalsIgnoreCase("no") && qtl.equalsIgnoreCase("no") && map.equalsIgnoreCase("no")){
						data="no";
					}else{
						data="yes";
							
					}
					session.setAttribute("data", data);
				str="det";
			/*}else if(opStr.equalsIgnoreCase("confirm")){
				
				
				
				str="ret";
			}
		*/
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
