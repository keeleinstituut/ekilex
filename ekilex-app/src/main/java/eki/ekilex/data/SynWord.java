package eki.ekilex.data;

import java.util.List;

import javax.persistence.Column;

public class SynWord extends Word {

	@Column(name = "layer_process_state_codes")
	List<String> layerProcessStateCodes;

	public List<String> getLayerProcessStateCodes() {
		return layerProcessStateCodes;
	}

	public void setLayerProcessStateCodes(List<String> layerProcessStateCodes) {
		this.layerProcessStateCodes = layerProcessStateCodes;
	}
}
