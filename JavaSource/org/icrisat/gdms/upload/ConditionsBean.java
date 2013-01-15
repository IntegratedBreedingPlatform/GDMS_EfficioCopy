/**
 * 
 */
package org.icrisat.gdms.upload;

public class ConditionsBean {
	private int dataset_id;	
	private String method_name;
	private String method_desc;
	private String missing_data;
	public int getDataset_id() {
		return dataset_id;
	}
	public void setDataset_id(int dataset_id) {
		this.dataset_id = dataset_id;
	}
	public String getMethod_name() {
		return method_name;
	}
	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}
	public String getMethod_desc() {
		return method_desc;
	}
	public void setMethod_desc(String method_desc) {
		this.method_desc = method_desc;
	}
	public String getMissing_data() {
		return missing_data;
	}
	public void setMissing_data(String missing_data) {
		this.missing_data = missing_data;
	}
	
	

}
