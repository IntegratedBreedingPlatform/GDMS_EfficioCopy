package org.icrisat.gdms.upload;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RetrieveDataAction extends Action{
	
	Connection con=null;
	ResultSet rs=null;
	Statement st=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		PrintWriter pr = res.getWriter();
		res.setContentType("text/xml");
		res.setHeader("Cache-Control", "no-cache");
		String data = req.getParameter("data");
		
		String type = req.getParameter("type");
		System.out.println("value == "+data+"    type=="+type);
		String sqlQuery = "";
		//String crop=req.getSession().getAttribute("crop").toString();
		try{
		ServletContext context = servlet.getServletContext();
		DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
		con=dataSource.getConnection();
		
		
		st = con.createStatement();
		if(type.equalsIgnoreCase("mapping")){
			rs = st.executeQuery("select map_name from gdms_map");
			//query=hsession.createQuery(sqlQuery);
			pr.println("<data>");
			pr.println("<details><![CDATA[- Select -]]></details>");
			while (rs.next()) {
				String str = rs.getString(1);	
				System.out.println(str);
				pr.println("<details><![CDATA[" + str + "]]></details>");
				
			}
			
			pr.println("</data>");	 
		
			return null;
		}else if(type.equalsIgnoreCase("map")){
			rs = st.executeQuery("select dataset_name from gdms_dataset where dataset_type='mapping'");
			//query=hsession.createQuery(sqlQuery);
			pr.println("<data>");
			pr.println("<details><![CDATA[- Select -]]></details>");
			while (rs.next()) {
				String str = rs.getString(1);	
				System.out.println(str);
				pr.println("<details><![CDATA[" + str + "]]></details>");
				
			}
			
			pr.println("</data>");	 
		
			return null;
		}
		}catch(Exception e){
			e.printStackTrace();
			//hsession.clear();		
			//tx.rollback();
		}finally{
			try{
				//hsession.clear();
				//closing open connection, Resultset, Statement objects.
				if(con!=null)con.close();
				if(st!=null)st.close();
				if(rs!=null)rs.close();
			}catch(Exception e){
				System.out.println("Exception :"+e);
			}
		}
		
		return am.findForward("test");
	}
	

}
