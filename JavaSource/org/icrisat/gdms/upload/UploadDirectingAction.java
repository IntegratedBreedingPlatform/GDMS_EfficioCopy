package org.icrisat.gdms.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class UploadDirectingAction extends Action{

	String str="";
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String op=req.getQueryString();
		System.out.println("######################   "+op);
		if(op.equalsIgnoreCase("markers")){
			str="markers";
		}else if(op.equalsIgnoreCase("geno")){
			str="geno";
		}else if(op.equalsIgnoreCase("mapsQtls")){
			str="mapsQtls";
		}/*else if(op.equalsIgnoreCase("qtl")){
			str="qtl";
		}
		*/
		
		return am.findForward(str);
	}
	
	

}
