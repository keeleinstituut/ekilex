package eki.ekilex.data.api;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public class FreqCorpId extends FreqCorp {

	private static final long serialVersionUID = 1L;

	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
