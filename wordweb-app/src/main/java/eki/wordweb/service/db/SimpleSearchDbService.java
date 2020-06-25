package eki.wordweb.service.db;

import org.jooq.Condition;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.wordweb.data.db.tables.MviewWwWordSearch;

@Component
public class SimpleSearchDbService extends AbstractSearchDbService {

	@Override
	protected String[] getFilteringComplexityNames() {
		return new String[] {Complexity.SIMPLE.name(), Complexity.ANY.name()};
	}

	@Override
	protected Condition getWordSearchComplexityCond(MviewWwWordSearch f) {
		return f.SIMPLE_EXISTS.isTrue();
	}

}
