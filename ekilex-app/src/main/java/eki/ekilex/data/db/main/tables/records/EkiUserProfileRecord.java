/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.EkiUserProfile;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Row16;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EkiUserProfileRecord extends UpdatableRecordImpl<EkiUserProfileRecord> implements Record16<Long, Long, Long, String[], String[], String[], String[], Boolean, Boolean, Boolean, Boolean, String[], String, Boolean, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.eki_user_profile.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.eki_user_profile.user_id</code>.
     */
    public void setUserId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.user_id</code>.
     */
    public Long getUserId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.eki_user_profile.recent_dataset_permission_id</code>.
     */
    public void setRecentDatasetPermissionId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.recent_dataset_permission_id</code>.
     */
    public Long getRecentDatasetPermissionId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.eki_user_profile.preferred_datasets</code>.
     */
    public void setPreferredDatasets(String[] value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.preferred_datasets</code>.
     */
    public String[] getPreferredDatasets() {
        return (String[]) get(3);
    }

    /**
     * Setter for <code>public.eki_user_profile.preferred_part_syn_candidate_langs</code>.
     */
    public void setPreferredPartSynCandidateLangs(String[] value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.preferred_part_syn_candidate_langs</code>.
     */
    public String[] getPreferredPartSynCandidateLangs() {
        return (String[]) get(4);
    }

    /**
     * Setter for <code>public.eki_user_profile.preferred_syn_lex_meaning_word_langs</code>.
     */
    public void setPreferredSynLexMeaningWordLangs(String[] value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.preferred_syn_lex_meaning_word_langs</code>.
     */
    public String[] getPreferredSynLexMeaningWordLangs() {
        return (String[]) get(5);
    }

    /**
     * Setter for <code>public.eki_user_profile.preferred_meaning_relation_word_langs</code>.
     */
    public void setPreferredMeaningRelationWordLangs(String[] value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.preferred_meaning_relation_word_langs</code>.
     */
    public String[] getPreferredMeaningRelationWordLangs() {
        return (String[]) get(6);
    }

    /**
     * Setter for <code>public.eki_user_profile.show_lex_meaning_relation_source_lang_words</code>.
     */
    public void setShowLexMeaningRelationSourceLangWords(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.show_lex_meaning_relation_source_lang_words</code>.
     */
    public Boolean getShowLexMeaningRelationSourceLangWords() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>public.eki_user_profile.show_meaning_relation_first_word_only</code>.
     */
    public void setShowMeaningRelationFirstWordOnly(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.show_meaning_relation_first_word_only</code>.
     */
    public Boolean getShowMeaningRelationFirstWordOnly() {
        return (Boolean) get(8);
    }

    /**
     * Setter for <code>public.eki_user_profile.show_meaning_relation_meaning_id</code>.
     */
    public void setShowMeaningRelationMeaningId(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.show_meaning_relation_meaning_id</code>.
     */
    public Boolean getShowMeaningRelationMeaningId() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>public.eki_user_profile.show_meaning_relation_word_datasets</code>.
     */
    public void setShowMeaningRelationWordDatasets(Boolean value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.show_meaning_relation_word_datasets</code>.
     */
    public Boolean getShowMeaningRelationWordDatasets() {
        return (Boolean) get(10);
    }

    /**
     * Setter for <code>public.eki_user_profile.preferred_tag_names</code>.
     */
    public void setPreferredTagNames(String[] value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.preferred_tag_names</code>.
     */
    public String[] getPreferredTagNames() {
        return (String[]) get(11);
    }

    /**
     * Setter for <code>public.eki_user_profile.active_tag_name</code>.
     */
    public void setActiveTagName(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.active_tag_name</code>.
     */
    public String getActiveTagName() {
        return (String) get(12);
    }

    /**
     * Setter for <code>public.eki_user_profile.is_approve_meaning_enabled</code>.
     */
    public void setIsApproveMeaningEnabled(Boolean value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.is_approve_meaning_enabled</code>.
     */
    public Boolean getIsApproveMeaningEnabled() {
        return (Boolean) get(13);
    }

    /**
     * Setter for <code>public.eki_user_profile.preferred_full_syn_candidate_dataset_code</code>.
     */
    public void setPreferredFullSynCandidateDatasetCode(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.preferred_full_syn_candidate_dataset_code</code>.
     */
    public String getPreferredFullSynCandidateDatasetCode() {
        return (String) get(14);
    }

    /**
     * Setter for <code>public.eki_user_profile.preferred_full_syn_candidate_lang</code>.
     */
    public void setPreferredFullSynCandidateLang(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.eki_user_profile.preferred_full_syn_candidate_lang</code>.
     */
    public String getPreferredFullSynCandidateLang() {
        return (String) get(15);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record16 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row16<Long, Long, Long, String[], String[], String[], String[], Boolean, Boolean, Boolean, Boolean, String[], String, Boolean, String, String> fieldsRow() {
        return (Row16) super.fieldsRow();
    }

    @Override
    public Row16<Long, Long, Long, String[], String[], String[], String[], Boolean, Boolean, Boolean, Boolean, String[], String, Boolean, String, String> valuesRow() {
        return (Row16) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return EkiUserProfile.EKI_USER_PROFILE.ID;
    }

    @Override
    public Field<Long> field2() {
        return EkiUserProfile.EKI_USER_PROFILE.USER_ID;
    }

    @Override
    public Field<Long> field3() {
        return EkiUserProfile.EKI_USER_PROFILE.RECENT_DATASET_PERMISSION_ID;
    }

    @Override
    public Field<String[]> field4() {
        return EkiUserProfile.EKI_USER_PROFILE.PREFERRED_DATASETS;
    }

    @Override
    public Field<String[]> field5() {
        return EkiUserProfile.EKI_USER_PROFILE.PREFERRED_PART_SYN_CANDIDATE_LANGS;
    }

    @Override
    public Field<String[]> field6() {
        return EkiUserProfile.EKI_USER_PROFILE.PREFERRED_SYN_LEX_MEANING_WORD_LANGS;
    }

    @Override
    public Field<String[]> field7() {
        return EkiUserProfile.EKI_USER_PROFILE.PREFERRED_MEANING_RELATION_WORD_LANGS;
    }

    @Override
    public Field<Boolean> field8() {
        return EkiUserProfile.EKI_USER_PROFILE.SHOW_LEX_MEANING_RELATION_SOURCE_LANG_WORDS;
    }

    @Override
    public Field<Boolean> field9() {
        return EkiUserProfile.EKI_USER_PROFILE.SHOW_MEANING_RELATION_FIRST_WORD_ONLY;
    }

    @Override
    public Field<Boolean> field10() {
        return EkiUserProfile.EKI_USER_PROFILE.SHOW_MEANING_RELATION_MEANING_ID;
    }

    @Override
    public Field<Boolean> field11() {
        return EkiUserProfile.EKI_USER_PROFILE.SHOW_MEANING_RELATION_WORD_DATASETS;
    }

    @Override
    public Field<String[]> field12() {
        return EkiUserProfile.EKI_USER_PROFILE.PREFERRED_TAG_NAMES;
    }

    @Override
    public Field<String> field13() {
        return EkiUserProfile.EKI_USER_PROFILE.ACTIVE_TAG_NAME;
    }

    @Override
    public Field<Boolean> field14() {
        return EkiUserProfile.EKI_USER_PROFILE.IS_APPROVE_MEANING_ENABLED;
    }

    @Override
    public Field<String> field15() {
        return EkiUserProfile.EKI_USER_PROFILE.PREFERRED_FULL_SYN_CANDIDATE_DATASET_CODE;
    }

    @Override
    public Field<String> field16() {
        return EkiUserProfile.EKI_USER_PROFILE.PREFERRED_FULL_SYN_CANDIDATE_LANG;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getUserId();
    }

    @Override
    public Long component3() {
        return getRecentDatasetPermissionId();
    }

    @Override
    public String[] component4() {
        return getPreferredDatasets();
    }

    @Override
    public String[] component5() {
        return getPreferredPartSynCandidateLangs();
    }

    @Override
    public String[] component6() {
        return getPreferredSynLexMeaningWordLangs();
    }

    @Override
    public String[] component7() {
        return getPreferredMeaningRelationWordLangs();
    }

    @Override
    public Boolean component8() {
        return getShowLexMeaningRelationSourceLangWords();
    }

    @Override
    public Boolean component9() {
        return getShowMeaningRelationFirstWordOnly();
    }

    @Override
    public Boolean component10() {
        return getShowMeaningRelationMeaningId();
    }

    @Override
    public Boolean component11() {
        return getShowMeaningRelationWordDatasets();
    }

    @Override
    public String[] component12() {
        return getPreferredTagNames();
    }

    @Override
    public String component13() {
        return getActiveTagName();
    }

    @Override
    public Boolean component14() {
        return getIsApproveMeaningEnabled();
    }

    @Override
    public String component15() {
        return getPreferredFullSynCandidateDatasetCode();
    }

    @Override
    public String component16() {
        return getPreferredFullSynCandidateLang();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getUserId();
    }

    @Override
    public Long value3() {
        return getRecentDatasetPermissionId();
    }

    @Override
    public String[] value4() {
        return getPreferredDatasets();
    }

    @Override
    public String[] value5() {
        return getPreferredPartSynCandidateLangs();
    }

    @Override
    public String[] value6() {
        return getPreferredSynLexMeaningWordLangs();
    }

    @Override
    public String[] value7() {
        return getPreferredMeaningRelationWordLangs();
    }

    @Override
    public Boolean value8() {
        return getShowLexMeaningRelationSourceLangWords();
    }

    @Override
    public Boolean value9() {
        return getShowMeaningRelationFirstWordOnly();
    }

    @Override
    public Boolean value10() {
        return getShowMeaningRelationMeaningId();
    }

    @Override
    public Boolean value11() {
        return getShowMeaningRelationWordDatasets();
    }

    @Override
    public String[] value12() {
        return getPreferredTagNames();
    }

    @Override
    public String value13() {
        return getActiveTagName();
    }

    @Override
    public Boolean value14() {
        return getIsApproveMeaningEnabled();
    }

    @Override
    public String value15() {
        return getPreferredFullSynCandidateDatasetCode();
    }

    @Override
    public String value16() {
        return getPreferredFullSynCandidateLang();
    }

    @Override
    public EkiUserProfileRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value2(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value3(Long value) {
        setRecentDatasetPermissionId(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value4(String[] value) {
        setPreferredDatasets(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value5(String[] value) {
        setPreferredPartSynCandidateLangs(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value6(String[] value) {
        setPreferredSynLexMeaningWordLangs(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value7(String[] value) {
        setPreferredMeaningRelationWordLangs(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value8(Boolean value) {
        setShowLexMeaningRelationSourceLangWords(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value9(Boolean value) {
        setShowMeaningRelationFirstWordOnly(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value10(Boolean value) {
        setShowMeaningRelationMeaningId(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value11(Boolean value) {
        setShowMeaningRelationWordDatasets(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value12(String[] value) {
        setPreferredTagNames(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value13(String value) {
        setActiveTagName(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value14(Boolean value) {
        setIsApproveMeaningEnabled(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value15(String value) {
        setPreferredFullSynCandidateDatasetCode(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord value16(String value) {
        setPreferredFullSynCandidateLang(value);
        return this;
    }

    @Override
    public EkiUserProfileRecord values(Long value1, Long value2, Long value3, String[] value4, String[] value5, String[] value6, String[] value7, Boolean value8, Boolean value9, Boolean value10, Boolean value11, String[] value12, String value13, Boolean value14, String value15, String value16) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EkiUserProfileRecord
     */
    public EkiUserProfileRecord() {
        super(EkiUserProfile.EKI_USER_PROFILE);
    }

    /**
     * Create a detached, initialised EkiUserProfileRecord
     */
    public EkiUserProfileRecord(Long id, Long userId, Long recentDatasetPermissionId, String[] preferredDatasets, String[] preferredPartSynCandidateLangs, String[] preferredSynLexMeaningWordLangs, String[] preferredMeaningRelationWordLangs, Boolean showLexMeaningRelationSourceLangWords, Boolean showMeaningRelationFirstWordOnly, Boolean showMeaningRelationMeaningId, Boolean showMeaningRelationWordDatasets, String[] preferredTagNames, String activeTagName, Boolean isApproveMeaningEnabled, String preferredFullSynCandidateDatasetCode, String preferredFullSynCandidateLang) {
        super(EkiUserProfile.EKI_USER_PROFILE);

        setId(id);
        setUserId(userId);
        setRecentDatasetPermissionId(recentDatasetPermissionId);
        setPreferredDatasets(preferredDatasets);
        setPreferredPartSynCandidateLangs(preferredPartSynCandidateLangs);
        setPreferredSynLexMeaningWordLangs(preferredSynLexMeaningWordLangs);
        setPreferredMeaningRelationWordLangs(preferredMeaningRelationWordLangs);
        setShowLexMeaningRelationSourceLangWords(showLexMeaningRelationSourceLangWords);
        setShowMeaningRelationFirstWordOnly(showMeaningRelationFirstWordOnly);
        setShowMeaningRelationMeaningId(showMeaningRelationMeaningId);
        setShowMeaningRelationWordDatasets(showMeaningRelationWordDatasets);
        setPreferredTagNames(preferredTagNames);
        setActiveTagName(activeTagName);
        setIsApproveMeaningEnabled(isApproveMeaningEnabled);
        setPreferredFullSynCandidateDatasetCode(preferredFullSynCandidateDatasetCode);
        setPreferredFullSynCandidateLang(preferredFullSynCandidateLang);
    }
}