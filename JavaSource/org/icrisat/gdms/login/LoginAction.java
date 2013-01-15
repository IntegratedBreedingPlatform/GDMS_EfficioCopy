/**
 * Login Action for checking password & putting user name in session
 */
package org.icrisat.gdms.login;

import java.net.InetAddress;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.hibernate.Session;
import org.icrisat.gdms.common.HibernateSessionFactory;

public class LoginAction extends Action{
	String str="";
	Session hsession= null;
	String user="";
	String pass="";
	
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		if(session!=null){
			session.removeAttribute("user");			
		}
		/*String crop=req.getParameter("crop");
		System.out.println("************** crop="+crop);
		hsession = HibernateSessionFactory.currentSession(crop);	*/
		hsession = HibernateSessionFactory.currentSession();
		
		ArrayList<String> loginrecords1=new ArrayList<String>();
		
		/** Retrieving the inputs from the login page **/
		DynaActionForm dyna = (DynaActionForm) af;
		user = (String) dyna.get("uname");
		pass = (String) dyna.get("password");
		
		
		
		
		
		String path=req.getSession().getServletContext().getRealPath("//");
		session.setAttribute("path", path);
		loginrecords1=(ArrayList)hsession.createQuery("from LoginForm where uname='"+user+"' and upswd='"+pass+"'").list();

		if(loginrecords1.size()==1){			
			str="correct";
			req.getSession().setAttribute("user", user);
			//req.getSession().setAttribute("crop", crop);
		}else{			
			str="incorrect";
		}
		return am.findForward(str);
	}
	

}
