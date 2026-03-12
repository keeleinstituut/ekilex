package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET_WORD_MENU;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.DatasetStat;
import eki.wordweb.data.db.tables.MviewWwDataset;
import eki.wordweb.data.db.tables.MviewWwDatasetWordMenu;

@Component
public class DatasetContentDbService {

	@Autowired
	private DSLContext create;

	public DatasetStat getDatasetStat(String datasetCode) {

		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");

		return create
				.selectFrom(ds)
				.where(ds.CODE.eq(datasetCode))
				.fetchOptionalInto(DatasetStat.class)
				.orElse(null);
	}

	public List<Character> getDatasetFirstLetters(String datasetCode) {

		MviewWwDatasetWordMenu dswm = MVIEW_WW_DATASET_WORD_MENU.as("dswm");
		return create
				.select(dswm.FIRST_LETTER)
				.from(dswm)
				.where(dswm.DATASET_CODE.eq(datasetCode))
				.orderBy(dswm.FIRST_LETTER)
				.fetchInto(Character.class);
	}

	public List<String> getDatasetWords(String datasetCode, Character firstLetter) {

		MviewWwDatasetWordMenu dswm = MVIEW_WW_DATASET_WORD_MENU.as("dswm");
		String[] wordValues = create
				.select(dswm.WORD_VALUES)
				.from(dswm)
				.where(
						dswm.DATASET_CODE.eq(datasetCode)
								.and(dswm.FIRST_LETTER.eq(String.valueOf(firstLetter))))
				.fetchOne(record -> (String[]) record.into(Object.class));

		if (ArrayUtils.isEmpty(wordValues)) {
			return Collections.emptyList();
		}
		return Arrays.asList(wordValues);
	}
}
