package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.COLLOCATION;
import static eki.ekilex.data.db.Tables.COLLOCATION_FREEFORM;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEX_COLLOC;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Dataset;
import eki.ekilex.data.db.tables.Collocation;
import eki.ekilex.data.db.tables.CollocationFreeform;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionDataset;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.LexColloc;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Word;

@Component
public class DatasetDbService {

	private DSLContext create;

	public DatasetDbService(DSLContext context) {
		create = context;
	}

	public List<Dataset> getDatasets() {

		return create
				.select(DATASET.CODE,
						DATASET.NAME,
						DATASET.DESCRIPTION,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC)
				.from(DATASET)
				.orderBy(DATASET.ORDER_BY)
				.fetchInto(Dataset.class);

	}

	public void createDataset(Dataset dataset) {

		create
				.insertInto(DATASET,
						DATASET.CODE,
						DATASET.NAME,
						DATASET.DESCRIPTION,
						DATASET.IS_VISIBLE,
						DATASET.IS_PUBLIC)
				.values(dataset.getCode(),
						dataset.getName(),
						dataset.getDescription(),
						dataset.isVisible(),
						dataset.isPublic())
				.execute();
	}

	public void updateDataset(Dataset dataset) {

		create
				.update(DATASET)
				.set(DATASET.NAME, dataset.getName())
				.set(DATASET.DESCRIPTION, dataset.getDescription())
				.set(DATASET.IS_VISIBLE, dataset.isVisible())
				.set(DATASET.IS_PUBLIC, dataset.isPublic())
				.where(DATASET.CODE.eq(dataset.getCode()))
				.execute();
	}

	public void deleteDataset(String datasetCode) {

		Word w = WORD.as("w");
		Meaning m = MEANING.as("m");
		Definition d = DEFINITION.as("d");
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Collocation c = COLLOCATION.as("c");
		Freeform ff = FREEFORM.as("ff");
		LexColloc lc = LEX_COLLOC.as("lc");
		LexColloc lc1 = LEX_COLLOC.as("lc1");
		LexColloc lc2 = LEX_COLLOC.as("lc2");
		CollocationFreeform cff = COLLOCATION_FREEFORM.as("cff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		DefinitionFreeform dff = DEFINITION_FREEFORM.as("dff");
		DefinitionDataset dd1 = DEFINITION_DATASET.as("dd1");
		DefinitionDataset dd2 = DEFINITION_DATASET.as("dd2");

		// collect word ids
		List<Long> wordIds = create
				.select(w.ID)
				.from(w)
				.whereExists(DSL
						.select(l1.ID)
						.from(l1)
						.where(l1.WORD_ID.eq(w.ID).and(l1.DATASET_CODE.eq(datasetCode))))
				.andNotExists(DSL
						.select(l2.ID)
						.from(l2)
						.where(l2.WORD_ID.eq(w.ID).and(l2.DATASET_CODE.ne(datasetCode))))
				.fetchInto(Long.class);

		// collect meaning ids
		List<Long> meaningIds = create
				.select(m.ID)
				.from(m)
				.whereExists(DSL
						.select(l1.ID)
						.from(l1)
						.where(l1.MEANING_ID.eq(m.ID).and(l1.DATASET_CODE.eq(datasetCode))))
				.andNotExists(DSL
						.select(l2.ID)
						.from(l2)
						.where(l2.MEANING_ID.eq(m.ID).and(l2.DATASET_CODE.ne(datasetCode))))
				.fetchInto(Long.class);

		// delete definition freeforms
		create
				.deleteFrom(ff)
				.where(ff.ID.in(DSL
						.select(dff.FREEFORM_ID)
						.from(dff)
						.whereExists(DSL
								.select(dd1.DEFINITION_ID)
								.from(dd1)
								.where(dd1.DEFINITION_ID.eq(dff.DEFINITION_ID).and(dd1.DATASET_CODE.eq(datasetCode))))
						.andNotExists(DSL
								.select(dd2.DEFINITION_ID)
								.from(dd2)
								.where(dd2.DEFINITION_ID.eq(dff.DEFINITION_ID).and(dd2.DATASET_CODE.ne(datasetCode))))))
				.execute();

		// delete meaning freeforms
		create
				.deleteFrom(ff)
				.where(ff.ID.in(DSL
						.select(mff.FREEFORM_ID)
						.from(mff)
						.whereExists(DSL
								.select(l1.ID)
								.from(l1)
								.where(l1.MEANING_ID.eq(mff.MEANING_ID).and(l1.DATASET_CODE.eq(datasetCode))))
						.andNotExists(DSL
								.select(l2.ID)
								.from(l2)
								.where(l2.MEANING_ID.eq(mff.MEANING_ID).and(l2.DATASET_CODE.ne(datasetCode))))))
				.execute();

		// delete lexeme freeforms
		create
				.deleteFrom(ff)
				.where(ff.ID.in(DSL
						.select(lff.FREEFORM_ID)
						.from(lff)
						.whereExists(DSL
								.select(l1.ID)
								.from(l1)
								.where(l1.ID.eq(lff.LEXEME_ID).and(l1.DATASET_CODE.eq(datasetCode))))
						.andNotExists(DSL
								.select(l2.ID)
								.from(l2)
								.where(l2.ID.eq(lff.LEXEME_ID).and(l2.DATASET_CODE.ne(datasetCode))))))
				.execute();

		// delete colloc freeforms
		create
				.deleteFrom(ff)
				.where(ff.ID.in(DSL
						.select(cff.FREEFORM_ID)
						.from(cff)
						.whereExists(DSL
								.select(lc1.ID)
								.from(l1, lc1)
								.where(
										lc1.LEXEME_ID.eq(l1.ID)
												.and(lc1.COLLOCATION_ID.eq(cff.COLLOCATION_ID))
												.and(l1.DATASET_CODE.eq(datasetCode))))
						.andNotExists(DSL
								.select(lc2.ID)
								.from(l2, lc2)
								.where(
										lc2.LEXEME_ID.eq(l2.ID)
												.and(lc2.COLLOCATION_ID.eq(cff.COLLOCATION_ID))
												.and(l2.DATASET_CODE.ne(datasetCode))))))
				.execute();

		// delete definitions
		create
				.deleteFrom(d)
				.whereExists(DSL
						.select(dd1.DEFINITION_ID)
						.from(dd1)
						.where(dd1.DEFINITION_ID.eq(d.ID).and(dd1.DATASET_CODE.eq(datasetCode))))
				.andNotExists(DSL
						.select(dd2.DEFINITION_ID)
						.from(dd2)
						.where(dd2.DEFINITION_ID.eq(d.ID).and(dd2.DATASET_CODE.ne(datasetCode))))
				.execute();

		// delete collocations
		create
				.deleteFrom(c)
				.where(c.ID.in(DSL
						.select(lc.COLLOCATION_ID)
						.from(lc)
						.whereExists(DSL
								.select(l1.ID)
								.from(l1)
								.where(l1.ID.eq(lc.LEXEME_ID).and(l1.DATASET_CODE.eq(datasetCode))))
						.andNotExists(DSL
								.select(l2.ID)
								.from(l2)
								.where(l2.ID.eq(lc.LEXEME_ID).and(l2.DATASET_CODE.ne(datasetCode))))))
				.execute();

		//TODO under construction...

		create
				.delete(DATASET)
				.where(DATASET.CODE.eq(datasetCode))
				.execute();
	}

	public boolean datasetCodeExists(String datasetCode) {
		return
			create.fetchExists(
				create.select()
						.from(DATASET)
						.where(DATASET.CODE.equalIgnoreCase(datasetCode))
			);
	}

}
