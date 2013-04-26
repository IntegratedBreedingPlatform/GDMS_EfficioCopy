package org.icrisat.gdms.retrieve;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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
import org.hibernate.Query;

public class RetrieveMarkerInfoAction extends Action{
	Connection con=null;
	ResultSet rs=null;
	Statement st=null;
	String str1="";
	Query query=null;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		
		try{
			System.out.println("USER="+req.getSession().getAttribute("user"));
		
			PrintWriter pr = res.getWriter();
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");
			String ORGvalue = req.getParameter("data");
			String[] value=ORGvalue.split("!!");
			String type = req.getParameter("type");
			//System.out.println("value == "+value[0]+"  "+value[1]+"  type=="+type);
			String sqlQuery = "";
			//String crop=req.getSession().getAttribute("crop").toString();
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			
			
			
		//if (value[0].equals("Marker_Type")) {
		if (ORGvalue.contains("Marker_Type")) {	
			//System.out.println("1st if");
			if (type.equals("")) {
				//System.out.println("if inside of 1st if");
				sqlQuery = "Select Distinct Marker_Type from gdms_marker where marker_type != 'UA' and marker_type !='DArT'";
			} else {
				//System.out.println("else if inside of 1st if");
				//System.out.println("Select Marker_Name from marker where Lower(Marker_Type)='"+ type.toLowerCase() + "'");
				//Query = "Select Marker_Name from marker_info inner Join marker_type on Marker_Type.marker_type_id=marker_info.marker_type_id where Marker_Type.Marker_Type='"	+ type + "'";
				sqlQuery = "Select Marker_Name from gdms_marker where Lower(Marker_Type)='"+ type.toLowerCase() + "'";
			}
		}else if(ORGvalue.contains("Crop")){
			//System.out.println("else of 1st if");
			//System.out.println("...............type="+type);
			if(value[1].equalsIgnoreCase("list3")){
				//System.out.println("if inside of 1st if");
				String[] ret=type.split("!~!");
				sqlQuery="select distinct marker_name from gdms_marker where crop='"+ ret[1]+"' and marker_type='"+ret[0]+"'";
			}else{
				//System.out.println("else if inside of 1st if");
				if (type.equals("")) {
					sqlQuery = "Select Distinct(" + value[0] + ") from gdms_marker";
				} else {
					sqlQuery = "Select distinct marker_type from gdms_marker where "
							+ value[0] + "='" + type + "' and marker_type !='UA'";
				}
			}
		} 
		else {
			if (type.equals("")) {
				sqlQuery = "Select Distinct(" + value[0] + ") from gdms_marker where "+value[0]+" != 'null' or "+value[0]+" !=''";
			} else {
				sqlQuery = "Select Marker_Name from gdms_marker where "
						+ value[0] + "='" + type + "'";
			}
		}
		System.out.println("query="+sqlQuery);
		st = con.createStatement();
		//resultset object 
		rs = st.executeQuery(sqlQuery);
		//query=hsession.createQuery(sqlQuery);
		pr.println("<data>");
		pr.println("<details><![CDATA[- Select -]]></details>");
		while (rs.next()) {
			String str = rs.getString(1);	
			
			pr.println("<details><![CDATA[" + str + "]]></details>");
			
		}
		
		pr.println("</data>");	 
		
		return null;
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
		str1="return";
		
		return am.findForward(str1);
	}
	
	
	

}
