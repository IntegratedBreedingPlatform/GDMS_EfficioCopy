package org.icrisat.gdms.retrieve;

import java.io.File;
import java.io.FileInputStream;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.io.FileUtils;
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

public class ExportMarkerTraitsToFileAction extends Action{
	private static WorkbenchDataManager wdm;
	private static HibernateUtil hibernateUtil;
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		// TODO Auto-generated method stub
		String frompage=req.getSession().getAttribute("fromPage").toString();
		req.getSession().setAttribute("exop", "MT");
		HttpSession session = req.getSession(true);
		Calendar now = Calendar.getInstance();
		ArrayList markers=new ArrayList();
		Properties prop=new Properties();
		String pathWB="";
		String filePathWB="";
		//String bPath="C:\\IBWorkflowSystem\\infrastructure\\tomcat\\webapps\\GDMS";
		String bPath=session.getServletContext().getRealPath("//");
	    String opPath=bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1);	       
	    //System.out.println(",,,,,,,,,,,,,  :"+bPath.substring(0, bPath.indexOf("IBWorkflowSystem")-1));	    
		try{
			prop.load(new FileInputStream(session.getServletContext().getRealPath("//")+"//WEB-INF//classes//DatabaseConfig.properties"));
						
			String dbNameL = prop.getProperty("local.dbname");
			
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
	        //System.out.println(".........:"+IBWFProjects.get(dbNameL));
	        pathWB=opPath+"/IBWorkflowSystem/workspace/"+IBWFProjects.get(dbNameL)+"/gdms/output";
	        //pathWB="C:/IBWorkflowSystem/workspace/1-TL1_Groundnut/gdms/output";
	        if(!new File(pathWB+"/MarkerTraitFiles").exists())
		   		new File(pathWB+"/MarkerTraitFiles").mkdir();
	        
			
			if(frompage.equalsIgnoreCase("polymap")){
				//System.out.println(".....**************....."+session.getAttribute("resultM"));
				markers=(ArrayList)session.getAttribute("resultM");	
				//mapName  = map.substring(0,map.lastIndexOf("("));
				req.getSession().setAttribute("result", markers);
			}
			//System.out.println("............  :"+req.getParameter("binSize"));
			String mSec=now.getTimeInMillis()+"";
			req.getSession().setAttribute("msec", mSec);
			String[] strData=req.getParameter("dataToExp").split("~~!!~~");
			String foldername="MarkerTraitFiles";
			String fname1=session.getServletContext().getRealPath("//")+"/"+foldername;
			String fname1WF=pathWB+"/"+foldername;
			if(new File(fname1).exists()){
				//new File(filePath+"/analysisfiles").delete();
				FileUtils.cleanDirectory(new File(fname1)); 
			}
			
			if(!new File(fname1).exists())
		       	new File(fname1).mkdir();
			//System.out.println("fname1="+fname1);
			String createfile=fname1+"/MarkerTrait"+mSec+".xls";
			String createfileWF=fname1WF+"/MarkerTrait"+mSec+".xls";
			
			File file=new File(createfile);
			file.createNewFile();
			
			String op=req.getParameter("option");
			
			WritableWorkbook workbook = Workbook.createWorkbook(new File(createfile));
			WritableSheet sheet=workbook.createSheet("MarkerTraitDetails",0);
			
			WritableWorkbook workbookWF = Workbook.createWorkbook(new File(createfileWF));
			WritableSheet sheetWF=workbookWF.createSheet("MarkerTraitDetails",0);
			
			
			WritableFont wf = new WritableFont(WritableFont.TIMES,WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD,false,UnderlineStyle.SINGLE);
		   	WritableCellFormat cf = new WritableCellFormat(wf);
		    cf.setWrap(true); 
			String filenm=foldername+"/MarkerTrait"+now.getTimeInMillis()+".xls";
			Label ll = new Label(0,0,"Marker");
			sheet.addCell(ll);
			
			Label llWF = new Label(0,0,"Marker");
			sheetWF.addCell(llWF);
			
			ll = new Label(1,0,"Map");
			sheet.addCell(ll);
			llWF = new Label(1,0,"Map");
			sheetWF.addCell(llWF);
			ll = new Label(2,0,"Chromosome");
			sheet.addCell(ll);
			llWF = new Label(2,0,"Chromosome");
			sheetWF.addCell(llWF);
			ll = new Label(3,0,"Position");
			sheet.addCell(ll);
			llWF = new Label(3,0,"Position");
			sheetWF.addCell(llWF);
			ll = new Label(4,0,"Reason");
			sheet.addCell(ll);
			llWF = new Label(4,0,"Reason");
			sheetWF.addCell(llWF);
			int rows=1;
			int col=0;
			String reason="";
			ArrayList finalData=new ArrayList();
			for(int e=0;e<strData.length;e++){
				//System.out.println(strData[e]);
				String[] arrData=strData[e].split("!~!");
				for(int c=0;c<arrData.length;c++){
					if(op.equalsIgnoreCase("yes")){
						if((arrData[2].equals(" "))&&(!(arrData[6].equals(" ")))){
							reason=arrData[6];					
						}else if((!(arrData[2].equals(" ")))&&(arrData[6].equals(" "))){
							reason=arrData[2];					
						}else if((!(arrData[2].equals(" ")))&&(!(arrData[6].equals(" ")))){
							reason=arrData[2]+" and "+arrData[6];
						}
					}else{					
						reason=arrData[2];
					}				
				}
				finalData.add(arrData[1]+"!~!"+arrData[3]+"!~!"+arrData[4]+"!~!"+arrData[5]+"!~!"+reason);
				
			}
			//System.out.println(finalData);
			for(int f=0;f<finalData.size();f++){
				String[] arrFinalData=finalData.get(f).toString().split("!~!");
				for(int a=0;a<arrFinalData.length;a++){
					ll = new Label(col,rows,arrFinalData[a]);
					sheet.addCell(ll);
					
					llWF = new Label(col,rows,arrFinalData[a]);
					sheetWF.addCell(llWF);
					
					col++;				
				}
				col=0;
				rows++;
			}
			
			
			workbook.write();
			workbook.close();
			
			workbookWF.write();
			workbookWF.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		      try{		      		
		      		//if(con!=null) con.close();conn.close();
		      		//factory.close(); 
		         }catch(Exception e){System.out.println(e);}
			}
		return am.findForward("exp");
	}
}
