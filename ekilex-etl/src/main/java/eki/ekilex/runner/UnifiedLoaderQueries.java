package eki.ekilex.runner;

import java.io.InputStream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class UnifiedLoaderQueries extends AbstractLoaderCommons implements InitializingBean {

	private static final String SQL_SELECT_WORD_IDS_FOR_DATASET_BY_LEX = "sql/select_word_ids_for_dataset_by_lexeme.sql";
	private static final String SQL_SELECT_WORD_IDS_FOR_DATASET_BY_GUID = "sql/select_word_ids_for_dataset_by_guid.sql";
	private static final String SQL_SELECT_MEANING_IDS_FOR_DATASET = "sql/select_meaning_ids_for_dataset.sql";
	private static final String SQL_SELECT_WORD_BY_FORM_LANG_HOMON = "sql/select_word_by_form_lang_homon.sql";
	private static final String SQL_SELECT_WORD_BY_FORM_LANG_HOMON_TYPE = "sql/select_word_by_form_lang_homon_type.sql";
	private static final String SQL_SELECT_WORD_BY_DATASET_AND_GUID = "sql/select_word_by_dataset_and_guid.sql";
	private static final String SQL_SELECT_WORD_MAX_HOMON_BY_WORD_LANG = "sql/select_word_max_homon_by_word_lang.sql";
	private static final String SQL_SELECT_LEXEME_FREEFORM_BY_TYPE_AND_VALUE = "sql/select_lexeme_freeform_by_type_and_value.sql";
	private static final String SQL_SELECT_SOURCE_BY_TYPE_AND_NAME = "sql/select_source_by_type_and_name.sql";
	private static final String SQL_SELECT_WORD_GROUP_WITH_MEMBERS = "sql/select_word_group_with_members.sql";
	private static final String SQL_SELECT_FLOATING_WORD_IDS = "sql/select_floating_word_ids.sql";
	private static final String SQL_DELETE_DEFINITIONS_FOR_DATASET = "sql/delete_definitions_for_dataset.sql";
	private static final String SQL_DELETE_DEFINITION_FF_FOR_DATASET = "sql/delete_definition_freeforms_for_dataset.sql";
	private static final String SQL_DELETE_MEANING_FF_FOR_DATASET = "sql/delete_meaning_freeforms_for_dataset.sql";
	private static final String SQL_DELETE_COLLOCATION_FF_FOR_DATASET = "sql/delete_collocation_freeforms_for_dataset.sql";
	private static final String SQL_DELETE_LEXEME_FF_FOR_DATASET = "sql/delete_lexeme_freeforms_for_dataset.sql";

	private String sqlSelectWordIdsForDatasetByLexeme;
	private String sqlSelectWordIdsForDatasetByGuid;
	private String sqlSelectMeaningIdsForDataset;
	private String sqlSelectWordByFormLangHomon;
	private String sqlSelectWordByFormLangHomonType;
	private String sqlSelectWordByDatasetAndGuid;
	private String sqlSelectWordMaxHomonByWordLang;
	private String sqlSelectLexemeFreeform;
	private String sqlSelectSourceByTypeAndName;
	private String sqlSelectWordGroupWithMembers;
	private String sqlSelectFloatingWordIds;
	private String sqlDeleteDefinitionsForDataset;
	private String sqlDeleteDefinitionFreeformsForDataset;
	private String sqlDeleteMeaningFreeformsForDataset;
	private String sqlDeleteCollocationFreeformsForDataset;
	private String sqlDeleteLexemeFreeformsForDataset;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_IDS_FOR_DATASET_BY_LEX);
		sqlSelectWordIdsForDatasetByLexeme = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_IDS_FOR_DATASET_BY_GUID);
		sqlSelectWordIdsForDatasetByGuid = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_IDS_FOR_DATASET);
		sqlSelectMeaningIdsForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_BY_FORM_LANG_HOMON);
		sqlSelectWordByFormLangHomon = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_BY_FORM_LANG_HOMON_TYPE);
		sqlSelectWordByFormLangHomonType = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_BY_DATASET_AND_GUID);
		sqlSelectWordByDatasetAndGuid = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_MAX_HOMON_BY_WORD_LANG);
		sqlSelectWordMaxHomonByWordLang = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_FREEFORM_BY_TYPE_AND_VALUE);
		sqlSelectLexemeFreeform = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_SOURCE_BY_TYPE_AND_NAME);
		sqlSelectSourceByTypeAndName = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_GROUP_WITH_MEMBERS);
		sqlSelectWordGroupWithMembers = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_FLOATING_WORD_IDS);
		sqlSelectFloatingWordIds = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_DELETE_DEFINITIONS_FOR_DATASET);
		sqlDeleteDefinitionsForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_DELETE_DEFINITION_FF_FOR_DATASET);
		sqlDeleteDefinitionFreeformsForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_DELETE_MEANING_FF_FOR_DATASET);
		sqlDeleteMeaningFreeformsForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_DELETE_COLLOCATION_FF_FOR_DATASET);
		sqlDeleteCollocationFreeformsForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_DELETE_LEXEME_FF_FOR_DATASET);
		sqlDeleteLexemeFreeformsForDataset = getContent(resourceFileInputStream);
	}

	public String getSqlSelectWordIdsForDatasetByLexeme() {
		return sqlSelectWordIdsForDatasetByLexeme;
	}

	public String getSqlSelectWordIdsForDatasetByGuid() {
		return sqlSelectWordIdsForDatasetByGuid;
	}

	public String getSqlSelectMeaningIdsForDataset() {
		return sqlSelectMeaningIdsForDataset;
	}

	public String getSqlSelectWordByFormLangHomon() {
		return sqlSelectWordByFormLangHomon;
	}

	public String getSqlSelectWordByFormLangHomonType() {
		return sqlSelectWordByFormLangHomonType;
	}

	public String getSqlSelectWordByDatasetAndGuid() {
		return sqlSelectWordByDatasetAndGuid;
	}

	public String getSqlSelectWordMaxHomonByWordLang() {
		return sqlSelectWordMaxHomonByWordLang;
	}

	public String getSqlSelectLexemeFreeform() {
		return sqlSelectLexemeFreeform;
	}

	public String getSqlSelectSourceByTypeAndName() {
		return sqlSelectSourceByTypeAndName;
	}

	public String getSqlSelectWordGroupWithMembers() {
		return sqlSelectWordGroupWithMembers;
	}

	public String getSqlSelectFloatingWordIds() {
		return sqlSelectFloatingWordIds;
	}

	public String getSqlDeleteDefinitionsForDataset() {
		return sqlDeleteDefinitionsForDataset;
	}

	public String getSqlDeleteDefinitionFreeformsForDataset() {
		return sqlDeleteDefinitionFreeformsForDataset;
	}

	public String getSqlDeleteMeaningFreeformsForDataset() {
		return sqlDeleteMeaningFreeformsForDataset;
	}

	public String getSqlDeleteCollocationFreeformsForDataset() {
		return sqlDeleteCollocationFreeformsForDataset;
	}

	public String getSqlDeleteLexemeFreeformsForDataset() {
		return sqlDeleteLexemeFreeformsForDataset;
	}

}
