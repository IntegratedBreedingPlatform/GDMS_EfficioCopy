package org.icrisat.gdms.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * @author tpraveenreddy
 *
 */

public class MaxIdValue {
	static Map<String, ArrayList<String>> hashMap = new HashMap<String,  ArrayList<String>>();  
	
	public double roundThree(double in){		
		return Math.round(in*1000.0)/1000.0;
	}
	
	
//	To get maximum id value from the database
	public int getMaxIdValue(String fldName, String tblName, Session session){
		
			int intMaxVal=0;
			Object obj=null;
			Iterator itList=null;
			List listValues=null;
			
			//Query query=session.createSQLQuery("select max("+ fldName +") from " + tblName);			
			
			// changed from max to min because now we have all ids as -ve
			//System.out.println("select min("+ fldName +") from " + tblName);
			Query query=session.createSQLQuery("select min("+ fldName +") from " + tblName);
			
			listValues=query.list();
			itList=listValues.iterator();
						
			while(itList.hasNext()){
				obj=itList.next();
				if(obj!=null)
					intMaxVal=Integer.parseInt(obj.toString());
			}
		return intMaxVal;
	}
	
	 public ArrayList getMarkerIds(String fldName, String tblName,String wField, Session session, String mNames){
			
			int intVal=0;
			Object obj=null;
			Iterator itList=null;
			List listValues=new ArrayList<String>();
			//System.out.println("select "+ fldName +" from " + tblName +" where "+ wField.toLowerCase()+" in ("+mNames.toLowerCase()+")");
			SQLQuery query=session.createSQLQuery("select "+ fldName +" from " + tblName +" where "+ wField.toLowerCase()+" in ("+mNames.toLowerCase()+")");
	        //SQLQuery query=session.createSQLQuery("SELECT marker_id, principal_investigator FROM marker_user_info;");
			//String[] fldNames=fldName.split(",");
			
			query.addScalar("marker_id",Hibernate.INTEGER);
	        query.addScalar("marker_name",Hibernate.STRING);
			
			listValues=query.list();
			
	            
			
		return (ArrayList) listValues;
	}
	
	 /////////////////////////////////////////////////
	 public ArrayList getGIds(String fldName, String tblName,String wField, Session session, String pi){
			
			int intVal=0;
			Object obj=null;
			Iterator itList=null;
			List listValues=new ArrayList<String>();
			SQLQuery query=session.createSQLQuery("select "+ fldName +" from " + tblName +" where "+ wField+" in ("+pi+") order by "+wField+" desc");
			//System.out.println(query);
	                //SQLQuery query=session.createSQLQuery("SELECT marker_id, principal_investigator FROM marker_user_info;");
	                query.addScalar("gid",Hibernate.INTEGER);
	                query.addScalar("nval",Hibernate.STRING);
			
			listValues=query.list();
			
	            
			
		return (ArrayList) listValues;
	}
	 
	 
	 /////////////////////////////////////////////////
	 
	 public ArrayList getNids(String fldName, String tblName,String wField, Session session, String pi){
			
			int intVal=0;
			Object obj=null;
			Iterator itList=null;
			List listValues=new ArrayList<String>();
			SQLQuery query=session.createSQLQuery("select "+ fldName +" from " + tblName +" where "+ wField+" in ("+pi+")");
			//System.out.println(query);
	                //SQLQuery query=session.createSQLQuery("SELECT marker_id, principal_investigator FROM marker_user_info;");
	                query.addScalar("gid",Hibernate.INTEGER);
	                query.addScalar("nid",Hibernate.INTEGER);
			
			listValues=query.list();
			
	            
			
		return (ArrayList) listValues;
	}
	 
	 
	 
	 
	 
	public int getUserId(String fldName, String tblName,String wField, Session session, String pi){
		
		int intVal=0;
		Object obj=null;
		Iterator itList=null;
		List listValues=null;
		Query query=session.createSQLQuery("select distinct "+ fldName +" from " + tblName +" where "+ wField+" like '%"+pi+"%'");
		
		listValues=query.list();
		itList=listValues.iterator();
					
		while(itList.hasNext())
		{
			obj=itList.next();
			if(obj!=null)
				intVal=Integer.parseInt(obj.toString());
		}
	return intVal;
}
public int getMapId(String fldName, String tblName,String wField, Session session, String pi){
		
		int intVal=0;
		Object obj=null;
		Iterator itList=null;
		List listValues=null;
		Query query=session.createSQLQuery("select "+ fldName +" from " + tblName +" where "+ wField+" = '"+pi+"'");
		
		listValues=query.list();
		itList=listValues.iterator();
					
		while(itList.hasNext())
		{
			obj=itList.next();
			if(obj!=null)
				intVal=Integer.parseInt(obj.toString());
		}
	return intVal;
}
public boolean isValidDate(String inDate) {

	if (inDate == null)
		return false;

	//set the format to use as a constructor argument
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	if (inDate.trim().length() != dateFormat.toPattern().length())
		return false;

	dateFormat.setLenient(false);

	try {
		//parse the inDate parameter
		dateFormat.parse(inDate.trim());
	}
	catch (ParseException pe) {
		return false;
	}
	return true;
}
/*
private static void addValues(String key, String value)  
{   
 ArrayList<String> tempList = null;        
 if(hashMap.containsKey(key)){    
  tempList=hashMap.get(key);   
  if(tempList == null)      
    tempList = new ArrayList<String>();       
  tempList.add(value);      
 }  
 else  
 {       
  tempList = new ArrayList();    
  tempList.add(value);       
 }       
 hashMap.put(key,tempList);  
} */



}
