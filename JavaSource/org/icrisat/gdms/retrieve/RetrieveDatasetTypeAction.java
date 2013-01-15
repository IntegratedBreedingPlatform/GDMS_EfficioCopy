package org.icrisat.gdms.retrieve;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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

public class RetrieveDatasetTypeAction extends Action{

	Connection con=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		System.out.println("............."+req.getParameter("str"));
		
		String datasetName=req.getParameter("str");
		int dataset_id=0;
		String dType="";
		String mappingType="no";
		try{
			HttpSession session = req.getSession(true);
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			
			ResultSet rs=null;
			ResultSet rs1=null;
			Statement stmt=con.createStatement();
			Statement stmt1=con.createStatement();
			
			rs=stmt.executeQuery("select dataset_id, dataset_type from gdms_dataset where dataset_name='"+datasetName+"'");
			while(rs.next()){
				dataset_id=rs.getInt(1);
				dType=rs.getString(2);
			}
			if(dType.equalsIgnoreCase("mapping")){
				rs1=stmt1.executeQuery("select mapping_type from gdms_mapping_pop where dataset_id="+dataset_id);
				while(rs1.next()){
					mappingType=rs1.getString(1);
				}				
			}
			
			session.setAttribute("mType", mappingType);
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		      		
		         }catch(Exception e){System.out.println(e);}
			}	
		
		return am.findForward("ret");
	}
	

}
