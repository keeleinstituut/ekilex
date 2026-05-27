package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.ekilex.data.db.main.tables.ActivityLog;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.MeaningActivityLog;
import eki.ekilex.data.db.main.tables.Word;

@Component
public class TermDatasetReportDbService {

  @Autowired
  private DSLContext mainDb;

  public List<eki.ekilex.data.Dataset> getDatasets(List<String> datasetCodes) {

    return mainDb
        .select(DATASET.CODE, DATASET.NAME)
        .from(DATASET)
        .where(DATASET.CODE.in(datasetCodes))
        .orderBy(DATASET.NAME)
        .fetchInto(eki.ekilex.data.Dataset.class);
  }

  public Map<String, Integer> getPublicMeaningCounts(List<String> datasetCodes) {

    Dataset ds = DATASET.as("ds");
    Lexeme l = LEXEME.as("l");
    Meaning m = MEANING.as("m");
    Word w = WORD.as("w");

    Field<Integer> publicMeaningCount = DSL.selectCount()
        .from(m)
        .where(DSL.exists(
            DSL.selectOne()
                .from(l, w)
                .where(
                    l.MEANING_ID.eq(m.ID)
                        .and(l.WORD_ID.eq(w.ID))
                        .and(l.DATASET_CODE.eq(ds.CODE))
                        .and(l.IS_WORD.isTrue())
                        .and(l.IS_PUBLIC.isTrue())
                        .and(w.IS_PUBLIC.isTrue()))))
        .asField();

    return mainDb
        .select(ds.CODE, publicMeaningCount)
        .from(ds)
        .where(ds.CODE.in(datasetCodes))
        .fetchMap(ds.CODE, publicMeaningCount);
  }

  public Map<String, Integer> getAllMeaningCounts(List<String> datasetCodes) {

    Dataset ds = DATASET.as("ds");
    Lexeme l = LEXEME.as("l");
    Meaning m = MEANING.as("m");

    Field<Integer> allMeaningCount = DSL.selectCount()
        .from(m)
        .where(DSL.exists(
            DSL.selectOne()
                .from(l)
                .where(
                    l.MEANING_ID.eq(m.ID)
                        .and(l.DATASET_CODE.eq(ds.CODE))
                        .and(l.IS_WORD.isTrue()))))
        .asField();

    return mainDb
        .select(ds.CODE, allMeaningCount)
        .from(ds)
        .where(ds.CODE.in(datasetCodes))
        .fetchMap(ds.CODE, allMeaningCount);
  }

  public Map<String, Integer> getPublicTermCounts(List<String> datasetCodes) {

    Dataset ds = DATASET.as("ds");
    Lexeme l = LEXEME.as("l");
    Word w = WORD.as("w");

    Field<Integer> publicTermCount = DSL.selectCount()
        .from(w)
        .where(
            w.IS_PUBLIC.isTrue()
                .and(DSL.exists(
                    DSL.selectOne()
                        .from(l)
                        .where(
                            l.WORD_ID.eq(w.ID)
                                .and(l.DATASET_CODE.eq(ds.CODE))
                                .and(l.IS_WORD.isTrue())
                                .and(l.IS_PUBLIC.isTrue())))))
        .asField();

    return mainDb
        .select(ds.CODE, publicTermCount)
        .from(ds)
        .where(ds.CODE.in(datasetCodes))
        .fetchMap(ds.CODE, publicTermCount);
  }

  public Map<String, Integer> getAllTermCounts(List<String> datasetCodes) {

    Dataset ds = DATASET.as("ds");
    Lexeme l = LEXEME.as("l");
    Word w = WORD.as("w");

    Field<Integer> allTermCount = DSL.selectCount()
        .from(w)
        .where(DSL.exists(
            DSL.selectOne()
                .from(l)
                .where(
                    l.WORD_ID.eq(w.ID)
                        .and(l.DATASET_CODE.eq(ds.CODE))
                        .and(l.IS_WORD.isTrue()))))
        .asField();

    return mainDb
        .select(ds.CODE, allTermCount)
        .from(ds)
        .where(ds.CODE.in(datasetCodes))
        .fetchMap(ds.CODE, allTermCount);
  }

  public Map<String, Integer> getCreateMeaningCounts(List<String> datasetCodes, LocalDateTime from, LocalDateTime until) {

    Dataset ds = DATASET.as("ds");
    Lexeme l = LEXEME.as("l");
    Meaning m = MEANING.as("m");
    Word w = WORD.as("w");
    ActivityLog al = ACTIVITY_LOG.as("al");
    MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");

    Condition publicTermExistsCondition = DSL.exists(
        DSL.selectOne()
            .from(l, w)
            .where(
                l.MEANING_ID.eq(m.ID)
                    .and(l.WORD_ID.eq(w.ID))
                    .and(l.DATASET_CODE.eq(ds.CODE))
                    .and(l.IS_WORD.isTrue())
                    .and(l.IS_PUBLIC.isTrue())
                    .and(w.IS_PUBLIC.isTrue())));

    Field<LocalDateTime> firstEventOn = DSL
        .select(al.EVENT_ON)
        .from(mal, al)
        .where(
            mal.MEANING_ID.eq(m.ID)
                .and(mal.ACTIVITY_LOG_ID.eq(al.ID)))
        .orderBy(al.EVENT_ON.asc())
        .limit(1)
        .asField("first_event_on");

    Field<Integer> createMeaningCount = DSL.selectCount()
        .from(DSL.select(m.ID, firstEventOn)
            .from(m)
            .where(publicTermExistsCondition)
            .asTable("m"))
        .where(firstEventOn.ge(from))
        .and(firstEventOn.lt(until))
        .asField();

    return mainDb
        .select(ds.CODE, createMeaningCount)
        .from(ds)
        .where(ds.CODE.in(datasetCodes))
        .fetchMap(ds.CODE, createMeaningCount);
  }

  public Map<String, Integer> getUpdateMeaningCounts(List<String> datasetCodes, LocalDateTime from, LocalDateTime until) {

    Dataset ds = DATASET.as("ds");
    Lexeme l = LEXEME.as("l");
    Meaning m = MEANING.as("m");
    Word w = WORD.as("w");
    ActivityLog al = ACTIVITY_LOG.as("al");
    MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");

    Condition meaningUpdatedCondition = getMeaningUpdatedInPeriodCondition(m, al, mal, from, until);

    Field<Integer> updateMeaningCount = DSL.selectCount()
        .from(m)
        .where(DSL.exists(
                DSL.selectOne()
                    .from(l, w)
                    .where(
                        l.MEANING_ID.eq(m.ID)
                            .and(l.WORD_ID.eq(w.ID))
                            .and(l.DATASET_CODE.eq(ds.CODE))
                            .and(l.IS_WORD.isTrue())
                            .and(l.IS_PUBLIC.isTrue())
                            .and(w.IS_PUBLIC.isTrue())))
            .and(meaningUpdatedCondition))
        .asField();

    return mainDb
        .select(ds.CODE, updateMeaningCount)
        .from(ds)
        .where(ds.CODE.in(datasetCodes))
        .fetchMap(ds.CODE, updateMeaningCount);
  }

  private Condition getMeaningUpdatedInPeriodCondition(
      Meaning m, ActivityLog al, MeaningActivityLog mal, LocalDateTime from, LocalDateTime until) {

    return DSL.or(
        DSL.exists(
            DSL.selectOne()
                .from(mal, al)
                .where(
                    mal.MEANING_ID.eq(m.ID)
                        .and(mal.ACTIVITY_LOG_ID.eq(al.ID))
                        .and(al.OWNER_NAME.in(
                            ActivityOwner.MEANING.name(),
                            ActivityOwner.LEXEME.name()))
                        .and(al.EVENT_ON.ge(from))
                        .and(al.EVENT_ON.lt(until)))),
        DSL.exists(
            DSL.selectOne()
                .from(mal, al)
                .where(
                    mal.MEANING_ID.eq(m.ID)
                        .and(mal.ACTIVITY_LOG_ID.eq(al.ID))
                        .and(al.OWNER_NAME.eq(ActivityOwner.WORD.name()))
                        .and(al.ENTITY_NAME.notIn(
                            ActivityEntity.GRAMMAR.name(),
                            ActivityEntity.WORD_TYPE.name(),
                            ActivityEntity.WORD_TAG.name(),
                            ActivityEntity.WORD_NOTE.name(),
                            ActivityEntity.WORD_RELATION.name(),
                            ActivityEntity.WORD_RELATION_GROUP_MEMBER.name(),
                            ActivityEntity.WORD_ETYMOLOGY.name(),
                            ActivityEntity.WORD_OS_MORPH.name(),
                            ActivityEntity.WORD_OS_USAGE.name(),
                            ActivityEntity.PARADIGM.name(),
                            ActivityEntity.FORM.name(),
                            ActivityEntity.WORD_EKI_RECOMMENDATION.name(),
                            ActivityEntity.TAG.name(),
                            ActivityEntity.PUBLISHING.name()))
                        .and(al.FUNCT_NAME.notLike("%join%"))
                        .and(al.EVENT_ON.ge(from))
                        .and(al.EVENT_ON.lt(until)))));
  }
}
