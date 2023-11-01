package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_SOURCE_LINK;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectHavingStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityFunct;
import eki.common.constant.ActivityOwner;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.SourceType;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.data.db.tables.ActivityLog;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceActivityLog;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.records.FreeformRecord;
import eki.ekilex.service.db.util.SearchFilterHelper;

@Component
public class SourceDbService implements GlobalConstant, ActivityFunct {

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	@Autowired
	private DSLContext create;

	public eki.ekilex.data.Source getSource(Long sourceId) {

		Source s = SOURCE.as("s");
		return create
				.select(
						s.ID,
						s.TYPE,
						s.NAME,
						s.VALUE,
						s.VALUE_PRESE,
						s.COMMENT,
						s.IS_PUBLIC)
				.from(s)
				.where(s.ID.eq(sourceId))
				.fetchOneInto(eki.ekilex.data.Source.class);
	}

	public List<SourcePropertyTuple> getSourcePropertyTuples(Long sourceId) {

		Source s = SOURCE.as("s");
		SourceFreeform spff = SOURCE_FREEFORM.as("spff");
		Freeform sp = FREEFORM.as("sp");
		Condition where = s.ID.equal(sourceId);
		Field<Boolean> spmf = DSL.field(DSL.val(Boolean.FALSE));

		return getSourcePropertyTuples(s, spff, sp, spmf, where);
	}

	public List<SourcePropertyTuple> getSourcePropertyTuples(String searchFilterWithMetaCharacters, SourceType sourceType) {
		return getSourcePropertyTuples(searchFilterWithMetaCharacters, sourceType, null);
	}

	public List<SourcePropertyTuple> getSourcePropertyTuples(String searchFilter, SourceType sourceType, Long sourceIdToExclude) {

		String maskedSearchFilter = searchFilter.replace(SEARCH_MASK_CHARS, "%").replace(SEARCH_MASK_CHAR, "_");
		Field<String> filterField = DSL.lower(maskedSearchFilter);

		Source s = SOURCE.as("s");
		SourceFreeform spff = SOURCE_FREEFORM.as("spff");
		Freeform sp = FREEFORM.as("sp");
		SourceFreeform spcff = SOURCE_FREEFORM.as("spcff");
		Freeform spc = FREEFORM.as("spc");
		Field<Boolean> spmf = DSL.field(DSL.lower(sp.VALUE_TEXT).like(filterField));

		Condition where1 = spcff.SOURCE_ID.eq(s.ID)
				.and(spcff.FREEFORM_ID.eq(spc.ID))
				.and(DSL.lower(spc.VALUE_TEXT).like(filterField));

		if (sourceType != null) {
			where1 = where1.and(s.TYPE.eq(sourceType.name()));
		}
		if (sourceIdToExclude != null) {
			where1 = where1.and(spcff.SOURCE_ID.notEqual(sourceIdToExclude));
		}
		Condition where = DSL.exists(DSL.select(spcff.ID).from(spcff, spc).where(where1));

		return getSourcePropertyTuples(s, spff, sp, spmf, where);
	}

	private List<SourcePropertyTuple> getSourcePropertyTuples(Source s, SourceFreeform spff, Freeform sp, Field<Boolean> spmf, Condition where) {

		Field<Boolean> sptnf = DSL.field(sp.TYPE.eq(FreeformType.SOURCE_NAME.name()));

		return create
				.select(
						s.ID.as("source_id"),
						s.TYPE.as("source_type"),
						s.NAME.as("source_name"),
						s.VALUE.as("source_value"),
						s.VALUE_PRESE.as("source_value_prese"),
						s.COMMENT.as("source_comment"),
						s.IS_PUBLIC.as("is_source_public"),
						sp.ID.as("source_property_id"),
						sp.TYPE.as("source_property_type"),
						sp.VALUE_TEXT.as("source_property_value_text"),
						sp.VALUE_DATE.as("source_property_value_date"),
						spmf.as("source_property_match"))
				.from(
						s
								.innerJoin(spff).on(spff.SOURCE_ID.eq(s.ID))
								.innerJoin(sp).on(sp.ID.eq(spff.FREEFORM_ID)))
				.where(where)
				.orderBy(
						s.ID,
						sptnf.desc(),
						sp.ORDER_BY)
				.fetchInto(SourcePropertyTuple.class);
	}

	public List<SourcePropertyTuple> getSourcePropertyTuples(SearchFilter searchFilter) throws Exception {

		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();

		Source s = SOURCE.as("s");
		SourceFreeform spff = SOURCE_FREEFORM.as("spff");
		Freeform sp = FREEFORM.as("sp");
		SourceFreeform spcff = SOURCE_FREEFORM.as("spcff");
		Freeform spc = FREEFORM.as("spc");
		Field<Boolean> spmf = DSL.field(DSL.falseCondition());

		Condition where = DSL.noCondition();

		for (SearchCriterionGroup searchCriterionGroup : searchCriteriaGroups) {
			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (CollectionUtils.isEmpty(searchCriteria)) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();
			if (SearchEntity.SOURCE.equals(searchEntity)) {

				boolean containsSearchKeys;

				containsSearchKeys = searchFilterHelper.containsSearchKeys(searchCriteria, SearchKey.VALUE);
				if (containsSearchKeys) {
					Condition where1 = spcff.SOURCE_ID.eq(s.ID).and(spcff.FREEFORM_ID.eq(spc.ID));
					where1 = searchFilterHelper.applyValueFilters(SearchKey.VALUE, searchCriteria, spc.VALUE_TEXT, where1, true);
					where = where.andExists(DSL.select(spcff.ID).from(spcff, spc).where(where1));
				}

				where = applySourceLinkDatasetFilters(searchCriteria, s.ID, where);
				where = applySourceActivityLogFilters(searchCriteria, s.ID, where);
			}
		}

		return getSourcePropertyTuples(s, spff, sp, spmf, where);
	}

	private Condition applySourceLinkDatasetFilters(List<SearchCriterion> searchCriteria, Field<Long> sourceIdField, Condition where) {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKey(searchCriteria, SearchKey.DATASET_USAGE);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		Definition d = DEFINITION.as("d");
		Lexeme l = LEXEME.as("l");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		FreeformSourceLink ffsl = FREEFORM_SOURCE_LINK.as("ffsl");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		DefinitionFreeform dff = DEFINITION_FREEFORM.as("dff");

		for (SearchCriterion criterion : filteredCriteria) {

			if (SearchOperand.EQUALS.equals(criterion.getSearchOperand())) {

				String datasetCode = criterion.getSearchValue().toString();

				SelectHavingStep<Record1<Long>> selectLexemeSourceLinks = DSL
						.select(l.ID)
						.from(l, lsl)
						.where(
								l.DATASET_CODE.eq(datasetCode)
										.and(lsl.LEXEME_ID.eq(l.ID))
										.and(lsl.SOURCE_ID.eq(sourceIdField)));

				SelectHavingStep<Record1<Long>> selectDefinitionSourceLinks = DSL
						.select(l.ID)
						.from(l, d, dsl)
						.where(
								l.DATASET_CODE.eq(datasetCode)
										.and(d.MEANING_ID.eq(l.MEANING_ID))
										.and(dsl.DEFINITION_ID.eq(d.ID))
										.and(dsl.SOURCE_ID.eq(sourceIdField)));

				SelectHavingStep<Record1<Long>> selectLexemeFreeformSourceLinks = DSL
						.select(l.ID)
						.from(l, lff, ffsl)
						.where(
								l.DATASET_CODE.eq(datasetCode)
										.and(lff.LEXEME_ID.eq(l.ID))
										.and(ffsl.FREEFORM_ID.eq(lff.FREEFORM_ID))
										.and(ffsl.SOURCE_ID.eq(sourceIdField)));

				SelectHavingStep<Record1<Long>> selectMeaningFreeformSourceLinks = DSL
						.select(l.ID)
						.from(l, mff, ffsl)
						.where(
								l.DATASET_CODE.eq(datasetCode)
										.and(mff.MEANING_ID.eq(l.MEANING_ID))
										.and(ffsl.FREEFORM_ID.eq(mff.FREEFORM_ID))
										.and(ffsl.SOURCE_ID.eq(sourceIdField)));

				SelectHavingStep<Record1<Long>> selectDefinitionFreeformSourceLinks = DSL
						.select(l.ID)
						.from(l, d, dff, ffsl)
						.where(
								l.DATASET_CODE.eq(datasetCode)
										.and(d.MEANING_ID.eq(l.MEANING_ID))
										.and(dff.DEFINITION_ID.eq(d.ID))
										.and(ffsl.FREEFORM_ID.eq(dff.FREEFORM_ID))
										.and(ffsl.SOURCE_ID.eq(sourceIdField)));

				Table<Record1<Long>> all = selectLexemeSourceLinks
						.unionAll(selectDefinitionSourceLinks)
						.unionAll(selectLexemeFreeformSourceLinks)
						.unionAll(selectMeaningFreeformSourceLinks)
						.unionAll(selectDefinitionFreeformSourceLinks)
						.asTable("all");

				where = where.andExists(DSL.select(all.field("id")).from(all));
			}
		}
		return where;
	}

	private Condition applySourceActivityLogFilters(List<SearchCriterion> searchCriteria, Field<Long> sourceIdField, Condition where) throws Exception {

		List<SearchCriterion> filteredCriteria = searchFilterHelper.filterCriteriaBySearchKeys(searchCriteria, SearchKey.CREATED_BY, SearchKey.CREATED_ON, SearchKey.UPDATED_BY, SearchKey.UPDATED_ON);

		if (CollectionUtils.isEmpty(filteredCriteria)) {
			return where;
		}

		SourceActivityLog sal = SOURCE_ACTIVITY_LOG.as("sal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		Condition where1 = sal.SOURCE_ID.eq(sourceIdField).and(sal.ACTIVITY_LOG_ID.eq(al.ID)).and(al.OWNER_NAME.eq(ActivityOwner.SOURCE.name()));

		for (SearchCriterion criterion : filteredCriteria) {
			String critValue = criterion.getSearchValue().toString();
			if (SearchKey.CREATED_BY.equals(criterion.getSearchKey())) {
				where1 = where1.and(al.ENTITY_NAME.eq(ActivityEntity.SOURCE.name())).and(al.FUNCT_NAME.like(LIKE_CREATE));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_BY, where1, true);
			} else if (SearchKey.CREATED_ON.equals(criterion.getSearchKey())) {
				where1 = where1.and(al.ENTITY_NAME.eq(ActivityEntity.SOURCE.name())).and(al.FUNCT_NAME.like(LIKE_CREATE));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			} else if (SearchKey.UPDATED_BY.equals(criterion.getSearchKey())) {
				where1 = where1.and(al.FUNCT_NAME.like(LIKE_UPDATE));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_BY, where1, true);
			} else if (SearchKey.UPDATED_ON.equals(criterion.getSearchKey())) {
				where1 = where1.and(al.FUNCT_NAME.like(LIKE_UPDATE));
				where1 = searchFilterHelper.applyValueFilter(critValue, criterion.isNot(), criterion.getSearchOperand(), al.EVENT_ON, where1, false);
			}
		}
		where = where.andExists(DSL.select(sal.ID).from(sal, al).where(where1));
		return where;
	}

	public Long getSourceId(Long sourcePropertyId) {

		return create
				.select(SOURCE_FREEFORM.SOURCE_ID)
				.from(SOURCE_FREEFORM, FREEFORM)
				.where(
						SOURCE_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)
								.and(FREEFORM.ID.eq(sourcePropertyId)))
				.fetchSingleInto(Long.class);
	}

	public SourceProperty getSourceProperty(Long sourcePropertyId) {

		return create
				.select(
						SOURCE_FREEFORM.SOURCE_ID,
						FREEFORM.ID,
						FREEFORM.TYPE,
						FREEFORM.VALUE_TEXT)
				.from(SOURCE_FREEFORM, FREEFORM)
				.where(
						SOURCE_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)
								.and(FREEFORM.ID.eq(sourcePropertyId)))
				.fetchOptionalInto(SourceProperty.class)
				.orElse(null);
	}

	public String getSourceNameValue(Long sourceId) {

		return create
				.select(FREEFORM.VALUE_TEXT)
				.from(SOURCE_FREEFORM, FREEFORM)
				.where(SOURCE_FREEFORM.SOURCE_ID.eq(sourceId)
						.and(FREEFORM.ID.eq(SOURCE_FREEFORM.FREEFORM_ID))
						.and(FREEFORM.TYPE.eq(FreeformType.SOURCE_NAME.name())))
				.limit(1)
				.fetchOneInto(String.class);
	}

	public List<String> getSourceNames(String nameSearchFilter, int limit) {

		String nameSearchFilterCrit = '%' + StringUtils.lowerCase(nameSearchFilter) + '%';
		return create
				.selectDistinct(FREEFORM.VALUE_TEXT)
				.from(FREEFORM)
				.where(FREEFORM.TYPE.eq(FreeformType.SOURCE_NAME.name())
						.and(DSL.lower(FREEFORM.VALUE_TEXT).like(nameSearchFilterCrit)))
				.orderBy(FREEFORM.VALUE_TEXT)
				.limit(limit)
				.fetchInto(String.class);
	}

	public Long createSource(SourceType type, String name, String value, String valuePrese, String comment, boolean isPublic, List<SourceProperty> sourceProperties) {

		Long sourceId = create.insertInto(SOURCE, SOURCE.TYPE, SOURCE.NAME, SOURCE.VALUE, SOURCE.VALUE_PRESE, SOURCE.COMMENT, SOURCE.IS_PUBLIC)
				.values(type.name(), name, value, valuePrese, comment, isPublic)
				.returning(SOURCE.ID)
				.fetchOne()
				.getId();

		if (CollectionUtils.isNotEmpty(sourceProperties)) {
			for (SourceProperty sourceProperty : sourceProperties) {
				createSourceProperty(sourceId, sourceProperty.getType(), sourceProperty.getValueText());
			}
		}
		return sourceId;
	}

	@Deprecated
	public Long createSourceDeprecated(SourceType sourceType, List<SourceProperty> sourceProperties) {

		Long sourceId = create.insertInto(SOURCE, SOURCE.TYPE)
				.values(sourceType.name())
				.returning(SOURCE.ID)
				.fetchOne()
				.getId();

		for (SourceProperty sourceProperty : sourceProperties) {
			createSourceProperty(sourceId, sourceProperty.getType(), sourceProperty.getValueText());
		}
		return sourceId;
	}

	public Long createSourceProperty(Long sourceId, FreeformType type, String valueText) {

		Long sourceFreeformId = create
				.insertInto(FREEFORM, FREEFORM.TYPE, FREEFORM.VALUE_TEXT, FREEFORM.VALUE_PRESE)
				.values(type.name(), valueText, valueText)
				.returning(FREEFORM.ID)
				.fetchOne()
				.getId();
		create.insertInto(SOURCE_FREEFORM, SOURCE_FREEFORM.SOURCE_ID, SOURCE_FREEFORM.FREEFORM_ID).values(sourceId, sourceFreeformId).execute();
		return sourceFreeformId;
	}

	public void updateSourceProperty(Long sourceFreeformId, String valueText) {

		create.update(FREEFORM)
				.set(FREEFORM.VALUE_TEXT, valueText)
				.set(FREEFORM.VALUE_PRESE, valueText)
				.where(FREEFORM.ID.eq(sourceFreeformId))
				.execute();
	}

	public void deleteSourceProperty(Long sourceFreeformId) {

		create.delete(FREEFORM)
				.where(FREEFORM.ID.eq(sourceFreeformId))
				.execute();
		create.delete(SOURCE_FREEFORM)
				.where(SOURCE_FREEFORM.FREEFORM_ID.eq(sourceFreeformId))
				.execute();
	}

	public void updateSource(Long sourceId, SourceType type, String name, String value, String valuePrese, String comment, boolean isPublic) {

		create.update(SOURCE)
				.set(SOURCE.TYPE, type.name())
				.set(SOURCE.NAME, name)
				.set(SOURCE.VALUE, value)
				.set(SOURCE.VALUE_PRESE, valuePrese)
				.set(SOURCE.COMMENT, comment)
				.set(SOURCE.IS_PUBLIC, isPublic)
				.where(SOURCE.ID.eq(sourceId))
				.execute();
	}

	// TODO delete later
	public void updateSourceType(Long sourceId, SourceType type) {

		create.update(SOURCE)
				.set(SOURCE.TYPE, type.name())
				.where(SOURCE.ID.eq(sourceId))
				.execute();
	}

	public void deleteSource(Long sourceId) {

		List<Long> freeformIds = create
				.select(SOURCE_FREEFORM.FREEFORM_ID)
				.from(SOURCE_FREEFORM)
				.where(SOURCE_FREEFORM.SOURCE_ID.eq(sourceId))
				.fetchInto(Long.class);

		for (Long freeformId : freeformIds) {
			create.delete(FREEFORM)
					.where(FREEFORM.ID.eq(freeformId))
					.execute();
		}

		create.delete(SOURCE)
				.where(SOURCE.ID.eq(sourceId))
				.execute();
	}

	public void joinSources(Long targetSourceId, Long originSourceId) {

		Result<FreeformRecord> targetSourceFreeforms = getSourceFreeformRecords(targetSourceId);
		Result<FreeformRecord> originSourceFreeforms = getSourceFreeformRecords(originSourceId);

		List<Long> uniqueFreeformsIds = originSourceFreeforms.stream()
				.filter(origin -> targetSourceFreeforms.stream()
						.noneMatch(target -> target.getType().equals(origin.getType())
								&& Objects.nonNull(target.getValueText())
								&& target.getValueText().equals(origin.getValueText())))
				.map(FreeformRecord::getId)
				.collect(Collectors.toList());

		for (Long freeformId : uniqueFreeformsIds) {
			create.update(SOURCE_FREEFORM)
					.set(SOURCE_FREEFORM.SOURCE_ID, targetSourceId)
					.where(
							SOURCE_FREEFORM.SOURCE_ID.eq(originSourceId)
									.and(SOURCE_FREEFORM.FREEFORM_ID.eq(freeformId)))
					.execute();
		}

		create.update(FREEFORM_SOURCE_LINK)
				.set(FREEFORM_SOURCE_LINK.SOURCE_ID, targetSourceId)
				.where(FREEFORM_SOURCE_LINK.SOURCE_ID.eq(originSourceId))
				.execute();

		create.update(DEFINITION_SOURCE_LINK)
				.set(DEFINITION_SOURCE_LINK.SOURCE_ID, targetSourceId)
				.where(DEFINITION_SOURCE_LINK.SOURCE_ID.eq(originSourceId))
				.execute();

		create.update(LEXEME_SOURCE_LINK)
				.set(LEXEME_SOURCE_LINK.SOURCE_ID, targetSourceId)
				.where(LEXEME_SOURCE_LINK.SOURCE_ID.eq(originSourceId))
				.execute();

		create.update(WORD_ETYMOLOGY_SOURCE_LINK)
				.set(WORD_ETYMOLOGY_SOURCE_LINK.SOURCE_ID, targetSourceId)
				.where(WORD_ETYMOLOGY_SOURCE_LINK.SOURCE_ID.eq(originSourceId))
				.execute();

		create.update(SOURCE_ACTIVITY_LOG)
				.set(SOURCE_ACTIVITY_LOG.SOURCE_ID, targetSourceId)
				.where(SOURCE_ACTIVITY_LOG.SOURCE_ID.eq(originSourceId))
				.execute();

		deleteSource(originSourceId);
	}

	public boolean validateSourceDelete(Long sourceId) {

		return create.select(DSL.field(DSL.count(SOURCE.ID).gt(0)).as("is_unbinded"))
				.from(SOURCE)
				.where(
						SOURCE.ID.eq(sourceId)
								.andNotExists(DSL
										.select(DEFINITION_SOURCE_LINK.ID)
										.from(DEFINITION_SOURCE_LINK)
										.where(DEFINITION_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
								.andNotExists(DSL
										.select(FREEFORM_SOURCE_LINK.ID)
										.from(FREEFORM_SOURCE_LINK)
										.where(FREEFORM_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
								.andNotExists(DSL
										.select(LEXEME_SOURCE_LINK.ID)
										.from(LEXEME_SOURCE_LINK)
										.where(LEXEME_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
								.andNotExists(DSL
										.select(WORD_ETYMOLOGY_SOURCE_LINK.ID)
										.from(WORD_ETYMOLOGY_SOURCE_LINK)
										.where(WORD_ETYMOLOGY_SOURCE_LINK.SOURCE_ID.eq(sourceId))))
				.fetchSingleInto(Boolean.class);
	}

	private Result<FreeformRecord> getSourceFreeformRecords(Long sourceId) {

		return create
				.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(SOURCE_FREEFORM.FREEFORM_ID)
						.from(SOURCE_FREEFORM)
						.where(SOURCE_FREEFORM.SOURCE_ID.eq(sourceId))))
				.fetch();
	}

}
