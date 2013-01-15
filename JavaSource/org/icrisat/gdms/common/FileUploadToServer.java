package org.icrisat.gdms.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadToServer implements java.io.Serializable{
	
	String strReturnValue="";
	
	
	/** 
	 * for writing the file to the server
	 *    
	 **/	
	public String createFile(InputStream stream, String fileName){
		
		try	{			
			FileOutputStream fos=null;
			byte[] buffer = new byte[8192];
			int bytesRead = 0;
			fos = new FileOutputStream(fileName);			
			while ((bytesRead = stream.read(buffer, 0, 8192)) != -1){
					fos.write(buffer, 0, bytesRead);												
				}
			fos.close();
			stream.close();
			strReturnValue="success";
		}catch(Exception e){
			e.toString();
			strReturnValue="Exception"+e;
		}	
			return strReturnValue;	
	}	
	public String getFileUpload(HttpServletRequest request, String fldName) throws Exception{
		String strResult = "uploaded"; 
	
			try{
				FileItemFactory factory = new DiskFileItemFactory();
			    ServletFileUpload sfu = new ServletFileUpload(factory);
			    // If file size exceeds, a FileUploadException will be thrown
			    //sfu.setSizeMax(1000000);
			      sfu.setSizeMax(1000000000);
			
				List fileItems = sfu.parseRequest(request);
			    Iterator itr = fileItems.iterator();
			    File fNew = null;
			    File fNew1 =null;
			    while(itr.hasNext()) {
			    	FileItem fi = (FileItem)itr.next();			          
			          if(!fi.isFormField()) {
			        	String strFileName = fi.getName();
			        	
			            HttpSession session = request.getSession();
			            String realContextPath =  session.getServletContext().getRealPath("/");
			            
			          		          
			            if(fi.getName().lastIndexOf("\\")!=-1){
			            	strFileName = fi.getName().substring(fi.getName().lastIndexOf("\\"));
			            	strFileName=strFileName.substring(1);
			            }else{
			            	if(fi.getName().lastIndexOf("/")!=-1)
				            	strFileName = fi.getName().substring(fi.getName().lastIndexOf("/"));
			            }
			           
			            //To make file name as unique, add the username and time to the file name.
			            String strUserName = (String)session.getAttribute("username");
			          
			           if((strUserName!=null) || (strUserName!="")){
			        	   //timestamp code
							java.util.Date utilDate=new java.util.Date(System.currentTimeMillis());
							Timestamp sqldate=new Timestamp(utilDate.getTime());
							Timestamp ts=new Timestamp(sqldate.getTime());
							String strFN = "_"+strUserName+ts+strFileName.substring(strFileName.lastIndexOf("."));
													
							strFileName=strFileName.replaceFirst(strFileName.substring(strFileName.lastIndexOf(".")), strFN.replaceAll(":", "-"));
							String filePath= realContextPath+"/"+fldName;
							if(!new File(filePath).exists())
								   new File(filePath).mkdir();
							fNew= new File(realContextPath+"/"+fldName, strFileName);
							fi.write(fNew);
				            fNew1 = new File(realContextPath+"/"+fldName, strFileName);
				            fNew.renameTo(fNew1);				         
			            }
			           strResult = strFileName;
			          }
			          else {			            
			        	 //System.out.println(fi.getFieldName()+"="+request.getParameter("+fi.getFieldName()+"));
			        	 // System.out.println("Field valueString="+fi.getString());
			        	 //System.out.println("Field ="+fi.getFieldName());
			          }			          
			    }
				
			    
			}catch(FileNotFoundException fe){
				fe.printStackTrace();
			}
		return strResult;
	}
	
	// to delete the file from the server
	public String deleteFile(String fileName){
		try{
			File f=new File(fileName);
			if(f.exists()){
				boolean flag=f.delete();

				if(flag==true){
					 strReturnValue="success";
				}else{
					strReturnValue="failure";
				}
			}
		}catch(Exception e){
			e.toString();
			strReturnValue="Exception"+e;
			}
		return strReturnValue;	
	}
	
}
