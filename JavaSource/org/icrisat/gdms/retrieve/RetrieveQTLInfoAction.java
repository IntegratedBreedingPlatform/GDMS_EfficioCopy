/**
 * Retrieves markers under a particular QTL
 */
package org.icrisat.gdms.retrieve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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

public class RetrieveQTLInfoAction extends Action{
	Connection con=null;
	String qtlName="";
	String map="";String chromosome="";
	float min=0;float max=0;
	int linkageMapId=0;
	String markers="";
	String qtl="";
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		ActionErrors ae = new ActionErrors();	
		if(session!=null){
			session.removeAttribute("markers");
			session.removeAttribute("qtlName");
		}
		
		qtl=req.getParameter("str");
		String[] data=qtl.split("!~!");
		qtlName=data[0];
		map=data[1];
		chromosome=data[2];
		min=Float.parseFloat(data[3]);
		max=Float.parseFloat(data[4]);
		
		try{
			//String crop=req.getSession().getAttribute("crop").toString();
			
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			
			
			ResultSet rs=null;
			ResultSet rs1=null;
			Statement stmt=con.createStatement();
			Statement stmtR=con.createStatement();
			/** Retrieve qtl id of a particular qtl **/
			
			//System.out.println("select map_id from qtl_details where qtl_id=(select qtl_id from qtl where qtl_name='"+qtlName+"')");
			rs=stmt.executeQuery("select map_id from gdms_qtl_details where qtl_id=(select qtl_id from gdms_qtl where qtl_name='"+qtlName+"')");
			while(rs.next()){
				linkageMapId=rs.getInt(1);
			}
			markers="";
			
			/** Retrieves markers under a QTL  **/
			
			//System.out.println("select marker_name from marker where marker_id in(select marker_id from markers_onmap where map_id="+linkageMapId+" and linkage_group='"+chromosome+"' and start_position between "+min+" AND "+max+")");
			rs1=stmtR.executeQuery("select marker_name from gdms_marker where marker_id in(select marker_id from gdms_markers_onmap where map_id="+linkageMapId+" and linkage_group='"+chromosome+"' and start_position between "+min+" AND "+max+")");
			while(rs1.next()){
				//System.out.println(rs1.getString(1));
				markers=markers+rs1.getString(1)+"!~!";				
			}
			//System.out.println("markers="+markers);
			req.getSession().setAttribute("markers", markers);
			session.setAttribute("qtlName", qtlName);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		if(con!=null) con.close();
		      		
		         }catch(Exception e){System.out.println(e);}
			}	
		return am.findForward("det");
	}
	

}
