package org.icrisat.gdms.upload;

public class MapMarkersBean {
	
	private int marker_id;
	private int map_id;
	private String linkage_group;
	private double start_position;
	private double end_position;
	private String map_unit;
	
	
	public int getMarkerId() {
		return marker_id;
	}
	public void setMarkerId(int marker_id) {
		this.marker_id = marker_id;
	}
	
	
	public int getMap_id() {
		return map_id;
	}
	public void setMap_id(int map_id) {
		this.map_id = map_id;
	}
	public String getLinkage_group() {
		return linkage_group;
	}
	public void setLinkage_group(String linkage_group) {
		this.linkage_group = linkage_group;
	}
	public double getStart_position() {
		return start_position;
	}
	public void setStart_position(double start_position) {
		this.start_position = start_position;
	}
	public double getEnd_position() {
		return end_position;
	}
	public void setEnd_position(double end_position) {
		this.end_position = end_position;
	}
	public String getMap_unit() {
		return map_unit;
	}
	public void setMap_unit(String map_unit) {
		this.map_unit = map_unit;
	}
	
	

}
