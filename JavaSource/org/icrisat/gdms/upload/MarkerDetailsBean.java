package org.icrisat.gdms.upload;

public class MarkerDetailsBean {
	
	private int marker_id;
	private int no_of_repeats;
	private String motif_type;
	private String sequence;
	private int sequence_length;
	private int min_allele;
	private int max_allele;
	private int ssr_nr;
	private double forward_primer_temp;
	private double reverse_primer_temp;
	
	private double elongation_temp;
	private int fragment_size_expected;
	private int fragment_size_observed;
	private int expected_product_size;
	private int position_on_reference_sequence;
	private String restriction_enzyme_for_assay;
	
	public int getMarker_id() {
		return marker_id;
	}
	public void setMarker_id(int marker_id) {
		this.marker_id = marker_id;
	}
	public int getNo_of_repeats() {
		return no_of_repeats;
	}
	public void setNo_of_repeats(int no_of_repeats) {
		this.no_of_repeats = no_of_repeats;
	}
	public String getMotif_type() {
		return motif_type;
	}
	public void setMotif_type(String motif_type) {
		this.motif_type = motif_type;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public int getSequence_length() {
		return sequence_length;
	}
	public void setSequence_length(int sequence_length) {
		this.sequence_length = sequence_length;
	}
	public int getMin_allele() {
		return min_allele;
	}
	public void setMin_allele(int min_allele) {
		this.min_allele = min_allele;
	}
	public int getMax_allele() {
		return max_allele;
	}
	public void setMax_allele(int max_allele) {
		this.max_allele = max_allele;
	}
	public int getSsr_nr() {
		return ssr_nr;
	}
	public void setSsr_nr(int ssr_nr) {
		this.ssr_nr = ssr_nr;
	}
	public double getForward_primer_temp() {
		return forward_primer_temp;
	}
	public void setForward_primer_temp(double forward_primer_temp) {
		this.forward_primer_temp = forward_primer_temp;
	}
	public double getReverse_primer_temp() {
		return reverse_primer_temp;
	}
	public void setReverse_primer_temp(double reverse_primer_temp) {
		this.reverse_primer_temp = reverse_primer_temp;
	}
	public double getElongation_temp() {
		return elongation_temp;
	}
	public void setElongation_temp(double elongation_temp) {
		this.elongation_temp = elongation_temp;
	}
	public int getFragment_size_expected() {
		return fragment_size_expected;
	}
	public void setFragment_size_expected(int fragment_size_expected) {
		this.fragment_size_expected = fragment_size_expected;
	}
	public int getFragment_size_observed() {
		return fragment_size_observed;
	}
	public void setFragment_size_observed(int fragment_size_observed) {
		this.fragment_size_observed = fragment_size_observed;
	}
	public int getExpected_product_size() {
		return expected_product_size;
	}
	public void setExpected_product_size(int expected_product_size) {
		this.expected_product_size = expected_product_size;
	}
	public int getPosition_on_reference_sequence() {
		return position_on_reference_sequence;
	}
	public void setPosition_on_reference_sequence(int position_on_reference_sequence) {
		this.position_on_reference_sequence = position_on_reference_sequence;
	}
	public String getRestriction_enzyme_for_assay() {
		return restriction_enzyme_for_assay;
	}
	public void setRestriction_enzyme_for_assay(String restriction_enzyme_for_assay) {
		this.restriction_enzyme_for_assay = restriction_enzyme_for_assay;
	}
	

}
