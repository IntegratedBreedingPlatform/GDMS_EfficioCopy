package org.icrisat.gdms.retrieve;

public class Markers {
	private String marker_name;
	private int marker_id;
	
	private String species;
	private String marker_type;
	public String getMarker_name() {
		return marker_name;
	}
	public void setMarker_name(String marker_name) {
		this.marker_name = marker_name;
	}
	public int getMarker_id() {
		return marker_id;
	}
	public void setMarker_id(int marker_id) {
		this.marker_id = marker_id;
	}
	
	/*public String getCrop() {
		return crop;
	}
	public void setCrop(String crop) {
		this.crop = crop;
	}*/
	
	public String getMarker_type() {
		return marker_type;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public void setMarker_type(String marker_type) {
		this.marker_type = marker_type;
	}
	
	
	
	

}
