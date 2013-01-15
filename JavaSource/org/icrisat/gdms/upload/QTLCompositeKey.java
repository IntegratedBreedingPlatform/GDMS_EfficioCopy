package org.icrisat.gdms.upload;

import java.io.Serializable;

public class QTLCompositeKey implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private int qtl_id;	
	private int linkagemap_id;
	public int getQtl_id() {
		return qtl_id;
	}
	public void setQtl_id(int qtl_id) {
		this.qtl_id = qtl_id;
	}
	public int getLinkagemap_id() {
		return linkagemap_id;
	}
	public void setLinkagemap_id(int linkagemap_id) {
		this.linkagemap_id = linkagemap_id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
