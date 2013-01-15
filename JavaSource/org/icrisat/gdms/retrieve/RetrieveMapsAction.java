package org.icrisat.gdms.retrieve;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

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

public class RetrieveMapsAction extends Action{
	Connection con=null;
	
	public ActionForward execute(ActionMapping am, ActionForm af,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		HttpSession session = req.getSession(true);
		ActionErrors ae = new ActionErrors();	
		if(session!=null){
			//session.removeAttribute("markers");
			session.removeAttribute("map_data");
		}
		
		
		try{
			//String crop=req.getSession().getAttribute("crop").toString();
			//System.out.println("****************************"+marker);
			ServletContext context = servlet.getServletContext();
			DataSource dataSource = (DataSource)context.getAttribute(Globals.DATA_SOURCE_KEY);
			con=dataSource.getConnection();	
			String traitsExists="yes";
			
			ResultSet rs=null;
			ResultSet rs1=null;
			Statement stmt=con.createStatement();
			Statement stmtR=con.createStatement();
			
			ResultSet rsT=null;
			Statement st=con.createStatement();
			
			ArrayList traits=new ArrayList();
			
			ArrayList map_data= new ArrayList();
			ArrayList retMarkers=new ArrayList();
			ArrayList markers=(ArrayList)session.getAttribute("result");	
			//System.out.println(".........."+markers);
			String marker="";
			
			ArrayList traitsList=new ArrayList();
			String traitsData="";
			HashMap traitsmap = new HashMap();
			//System.out.println("*******  :"+markers.size());
			for(int m=1;m<markers.size();m++){
				marker=marker+"'"+markers.get(m)+"',";
			}
			String fData="";
			ArrayList mapData=new ArrayList();
			ArrayList finalMapData=new ArrayList();
			//System.out.println("SELECT distinct marker_name, map_name, start_position, linkage_group FROM gdms_mapping_data where marker_name in("+marker.substring(0,marker.length()-1)+") order BY map_name, linkage_group,start_position asc");
			rs1=stmtR.executeQuery("SELECT distinct marker_name, map_name, start_position, linkage_group FROM gdms_mapping_data where marker_name in("+marker.substring(0,marker.length()-1)+") order BY map_name, linkage_group,start_position asc");
			while(rs1.next()){					
				mapData.add(rs1.getString(1)+"!~!"+rs1.getString(2)+"!~!"+rs1.getString(3)+"!~!"+rs1.getString(4));
				fData=fData+rs1.getString(1)+"!~!"+rs1.getString(2)+"!~!"+rs1.getString(3)+"!~!"+rs1.getString(4)+"~~!!~~";
				if(!(retMarkers.contains(rs1.getString(1))))
					retMarkers.add(rs1.getString(1));
			}
			/*System.out.println("SELECT DISTINCT gdms_mapping_data.marker_name, gdms_mapping_data.map_name, gdms_mapping_data.start_position, gdms_mapping_data.linkage_group, gdms_qtl_details.trait FROM gdms_mapping_data RIGHT OUTER JOIN gdms_qtl_details ON gdms_mapping_data.linkage_group=gdms_qtl_details.linkage_group WHERE gdms_mapping_data.marker_name in("+marker.substring(0,marker.length()-1)+") AND gdms_mapping_data.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position ASC");
			rs=stmt.executeQuery("SELECT DISTINCT gdms_mapping_data.marker_name, gdms_mapping_data.map_name, gdms_mapping_data.start_position, gdms_mapping_data.linkage_group, gdms_qtl_details.trait FROM gdms_mapping_data RIGHT OUTER JOIN gdms_qtl_details ON gdms_mapping_data.linkage_group=gdms_qtl_details.linkage_group WHERE gdms_mapping_data.marker_name in("+marker.substring(0,marker.length()-1)+") AND gdms_mapping_data.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position ASC");
			while(rs.next()){
				traitsList.add(rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getString(4)+"!~!"+rs.getString(5));
			}*/
			
			rs=stmt.executeQuery("SELECT DISTINCT gdms_mapping_data.marker_name, gdms_mapping_data.map_name, gdms_mapping_data.start_position, gdms_mapping_data.linkage_group, gdms_qtl_details.trait FROM gdms_mapping_data, gdms_qtl_details WHERE gdms_mapping_data.marker_name IN("+marker.substring(0,marker.length()-1)+") AND gdms_mapping_data.map_id=gdms_qtl_details.map_id AND gdms_mapping_data.linkage_group=gdms_qtl_details.linkage_group AND gdms_mapping_data.start_position BETWEEN gdms_qtl_details.min_position AND gdms_qtl_details.max_position ORDER BY map_name, linkage_group,start_position, marker_name ASC, trait");
			while(rs.next()){
				traitsList.add(rs.getString(1)+"!~!"+rs.getString(2)+"!~!"+rs.getString(3)+"!~!"+rs.getString(4)+"!~!"+rs.getString(5));
			}
			
			
			/*System.out.println(".............."+traitsList.size());
			System.out.println(".............."+mapData.size());*/
			ArrayList fList=new ArrayList();
			
			if(traitsList.size()>0){
				String[] str1=traitsList.get(0).toString().split("!~!");
				String markerT=str1[0];
				String trait=str1[4];
				String LG=str1[3];
				String Map=str1[1];
				String SPos=str1[2];
				String ins="no";
				for(int t=0;t<traitsList.size();t++){
					String[] str2=traitsList.get(t).toString().split("!~!");
					if(marker.equalsIgnoreCase(str2[0])){
						if(Map.equalsIgnoreCase(str2[1])){
							if(LG.equalsIgnoreCase(str2[3])){
								if(!(trait.equalsIgnoreCase(str2[4]))){
									trait=trait+","+str2[4];
									ins="yes";
								}else{
									trait=str2[4];
									ins="no";
								}							
							}
						}					
					}else{
						ins="no";					
						trait=str2[4];
						fList.add(str2[0]+"!~!"+str2[1]+"!~!"+str2[2]+"!~!"+str2[3]+"!~!"+trait);
					}
					if(ins.equalsIgnoreCase("yes")){
						fList.remove(fList.size()-1);
						fList.add(str2[0]+"!~!"+str2[1]+"!~!"+str2[2]+"!~!"+str2[3]+"!~!"+trait);
					}
						
					marker=str2[0];
					LG=str2[3];
					Map=str2[1];
					SPos=str2[2];
				}
				for(int f=0;f<fList.size();f++){
					 String arrP[]=new String[5];
					 StringTokenizer stzP = new StringTokenizer(fList.get(f).toString(), "!~!");
					 int iP=0;
					 while(stzP.hasMoreTokens()){
						 arrP[iP] = stzP.nextToken();
						 iP++;
					 }	
					 traitsmap.put(arrP[0]+"!!~~!!"+arrP[1], arrP[2]+"!~!"+arrP[3]+"!~!"+arrP[4]);
				}
				
				String finalData="";
				for(int l=0;l<mapData.size();l++){
					String arrP[]=new String[5];
					 StringTokenizer stzP = new StringTokenizer(mapData.get(l).toString(), "!~!");
					 int iP=0;
					 while(stzP.hasMoreTokens()){
						 arrP[iP] = stzP.nextToken();
						 iP++;
					 }	
					if(traitsmap.containsKey(arrP[0]+"!!~~!!"+arrP[1])){
						finalMapData.add(arrP[0]+"!~!"+arrP[1]+"!~!"+traitsmap.get(arrP[0]+"!!~~!!"+arrP[1]));
						finalData=finalData+arrP[0]+"!~!"+arrP[1]+"!~!"+traitsmap.get(arrP[0]+"!!~~!!"+arrP[1])+"~~!!~~";
					}else{
						finalMapData.add(arrP[0]+"!~!"+arrP[1]+"!~!"+arrP[2]+"!~!"+arrP[3]+"!~!"+" ");
						finalData=finalData+arrP[0]+"!~!"+arrP[1]+"!~!"+arrP[2]+"!~!"+arrP[3]+"!~!"+" "+"~~!!~~";
					}				
				}
				
				//System.out.println("finalMapData="+finalMapData);
				rsT=st.executeQuery("select distinct trait from gdms_qtl_details order by trait");
				while(rsT.next()){
					traits.add(rsT.getString(1));
				}
				traitsExists="yes";
				session.setAttribute("traits", traits);
				req.getSession().setAttribute("finalData",finalData);
				req.getSession().setAttribute("map_data", finalMapData);
				req.getSession().setAttribute("showTraits", traitsExists);
			}else{
				traitsExists="no";
				req.getSession().setAttribute("finalData",fData);
				req.getSession().setAttribute("map_data", mapData);
				req.getSession().setAttribute("showTraits", traitsExists);
			}
			//req.getSession().setAttribute("map_data", mapData);
			
			
			//return null;
			session.setAttribute("recCount", markers.size());
			session.setAttribute("missingCount", req.getParameter("missCount"));
			session.setAttribute("result", req.getParameter("nData"));
			session.setAttribute("retCount", retMarkers.size());

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

