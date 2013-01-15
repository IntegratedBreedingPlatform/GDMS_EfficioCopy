/**
 * for logout
 */
package org.icrisat.gdms.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author PSrikalyani
 *
 */
public class ForwardingAction extends Action{
	String str="";
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		HttpSession session = req.getSession(true);
		if(session!=null){
			session.removeAttribute("user");			
		}
		str="logout";
		return am.findForward(str);
	}
	

}
