package eki.wordweb.service.db;

import org.jooq.Condition;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.wordweb.data.db.tables.MviewWwWordSearch;

@Component
public class UnifSearchDbService extends AbstractSearchDbService {

	@Override
	protected String[] getFilteringComplexityNames() {
		return new String[] {Complexity.DETAIL.name(), Complexity.ANY.name()};
	}

	@Override
	protected Condition getWordSearchComplexityCond(MviewWwWordSearch f) {
		return f.DETAIL_EXISTS.isTrue();
	}

}
