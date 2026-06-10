package eki.ekilex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.etym2.WordEtymTree;
import eki.ekilex.service.db.EtymDbService;

@Component
public class EtymService implements SystemConstant {

	@Autowired
	private EtymDbService etymDbService;

	@Transactional
	public WordEtymTree getWordEtymTree(Long wordId) {
		return etymDbService.getWordEtymTree(wordId, CLASSIF_LABEL_LANG_EST);
	}
}
