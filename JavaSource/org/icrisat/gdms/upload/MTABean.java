package org.icrisat.gdms.upload;

public class MTABean {
	
	private int mta_id;
	private int marker_id;
	private int dataset_id;
	private int map_id;
	private String linkage_group;
	private float position;
	private int tid;
	private int effect;
	private String hv_allele;
	private String experiment;
	private float score_value;
	private float r_square;
	public int getMta_id() {
		return mta_id;
	}
	public void setMta_id(int mta_id) {
		this.mta_id = mta_id;
	}
	public int getMarker_id() {
		return marker_id;
	}
	public void setMarker_id(int marker_id) {
		this.marker_id = marker_id;
	}
	public int getDataset_id() {
		return dataset_id;
	}
	public void setDataset_id(int dataset_id) {
		this.dataset_id = dataset_id;
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
	public float getPosition() {
		return position;
	}
	public void setPosition(float position) {
		this.position = position;
	}
	/*public String getTrait() {
		return trait;
	}
	public void setTrait(String trait) {
		this.trait = trait;
	}*/
	public int getEffect() {
		return effect;
	}
	public void setEffect(int effect) {
		this.effect = effect;
	}
	public String getHv_allele() {
		return hv_allele;
	}
	public void setHv_allele(String hv_allele) {
		this.hv_allele = hv_allele;
	}
	public String getExperiment() {
		return experiment;
	}
	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}
	public float getScore_value() {
		return score_value;
	}
	public void setScore_value(float score_value) {
		this.score_value = score_value;
	}
	public float getR_square() {
		return r_square;
	}
	public void setR_square(float r_square) {
		this.r_square = r_square;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	
}
