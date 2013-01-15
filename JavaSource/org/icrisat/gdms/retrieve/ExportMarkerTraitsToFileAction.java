package org.icrisat.gdms.retrieve;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ExportMarkerTraitsToFileAction extends Action{

	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		// TODO Auto-generated method stub
		
		HttpSession session = req.getSession(true);
		Calendar now = Calendar.getInstance();
		//System.out.println(req.getParameter("dataToExp"));
		//System.out.println("............  :"+req.getParameter("binSize"));
		String mSec=now.getTimeInMillis()+"";
		req.getSession().setAttribute("msec", mSec);
		String[] strData=req.getParameter("dataToExp").split("~~!!~~");
		String foldername="MarkerTraitFiles";
		String fname1=session.getServletContext().getRealPath("//")+"/"+foldername;
		if(!new File(fname1).exists())
	       	new File(fname1).mkdir();
		//System.out.println("fname1="+fname1);
		String createfile=fname1+"/MarkerTrait"+mSec+".xls";
		File file=new File(createfile);
		file.createNewFile();
		
		String op=req.getParameter("option");
		
		WritableWorkbook workbook = Workbook.createWorkbook(new File(createfile));
		WritableSheet sheet=workbook.createSheet("MarkerTraitDetails",0);
		WritableFont wf = new WritableFont(WritableFont.TIMES,WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD,false,UnderlineStyle.SINGLE);
	   	WritableCellFormat cf = new WritableCellFormat(wf);
	    cf.setWrap(true); 
		String filenm=foldername+"/MarkerTrait"+now.getTimeInMillis()+".xls";
		Label ll = new Label(0,0,"Marker");
		sheet.addCell(ll);
		ll = new Label(1,0,"Map");
		sheet.addCell(ll);
		ll = new Label(2,0,"Chromosome");
		sheet.addCell(ll);
		ll = new Label(3,0,"Position");
		sheet.addCell(ll);
		ll = new Label(4,0,"Reason");
		sheet.addCell(ll);
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
				col++;				
			}
			col=0;
			rows++;
		}
		
		
		workbook.write();
		workbook.close();
		
		return am.findForward("exp");
	}
}
