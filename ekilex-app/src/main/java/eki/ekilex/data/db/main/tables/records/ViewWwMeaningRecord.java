/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.ViewWwMeaning;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwMeaningRecord extends TableRecordImpl<ViewWwMeaningRecord> implements Record11<Long, Timestamp, Timestamp, JSON, JSON, JSON, String[], String[], String[], JSON, JSON> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.view_ww_meaning.meaning_id</code>.
     */
    public void setMeaningId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.meaning_id</code>.
     */
    public Long getMeaningId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.view_ww_meaning.manual_event_on</code>.
     */
    public void setManualEventOn(Timestamp value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.manual_event_on</code>.
     */
    public Timestamp getManualEventOn() {
        return (Timestamp) get(1);
    }

    /**
     * Setter for <code>public.view_ww_meaning.last_approve_or_edit_event_on</code>.
     */
    public void setLastApproveOrEditEventOn(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.last_approve_or_edit_event_on</code>.
     */
    public Timestamp getLastApproveOrEditEventOn() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>public.view_ww_meaning.domain_codes</code>.
     */
    public void setDomainCodes(JSON value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.domain_codes</code>.
     */
    public JSON getDomainCodes() {
        return (JSON) get(3);
    }

    /**
     * Setter for <code>public.view_ww_meaning.meaning_images</code>.
     */
    public void setMeaningImages(JSON value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.meaning_images</code>.
     */
    public JSON getMeaningImages() {
        return (JSON) get(4);
    }

    /**
     * Setter for <code>public.view_ww_meaning.media_files</code>.
     */
    public void setMediaFiles(JSON value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.media_files</code>.
     */
    public JSON getMediaFiles() {
        return (JSON) get(5);
    }

    /**
     * Setter for <code>public.view_ww_meaning.systematic_polysemy_patterns</code>.
     */
    public void setSystematicPolysemyPatterns(String[] value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.systematic_polysemy_patterns</code>.
     */
    public String[] getSystematicPolysemyPatterns() {
        return (String[]) get(6);
    }

    /**
     * Setter for <code>public.view_ww_meaning.semantic_types</code>.
     */
    public void setSemanticTypes(String[] value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.semantic_types</code>.
     */
    public String[] getSemanticTypes() {
        return (String[]) get(7);
    }

    /**
     * Setter for <code>public.view_ww_meaning.learner_comments</code>.
     */
    public void setLearnerComments(String[] value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.learner_comments</code>.
     */
    public String[] getLearnerComments() {
        return (String[]) get(8);
    }

    /**
     * Setter for <code>public.view_ww_meaning.notes</code>.
     */
    public void setNotes(JSON value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.notes</code>.
     */
    public JSON getNotes() {
        return (JSON) get(9);
    }

    /**
     * Setter for <code>public.view_ww_meaning.definitions</code>.
     */
    public void setDefinitions(JSON value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.view_ww_meaning.definitions</code>.
     */
    public JSON getDefinitions() {
        return (JSON) get(10);
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<Long, Timestamp, Timestamp, JSON, JSON, JSON, String[], String[], String[], JSON, JSON> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Long, Timestamp, Timestamp, JSON, JSON, JSON, String[], String[], String[], JSON, JSON> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ViewWwMeaning.VIEW_WW_MEANING.MEANING_ID;
    }

    @Override
    public Field<Timestamp> field2() {
        return ViewWwMeaning.VIEW_WW_MEANING.MANUAL_EVENT_ON;
    }

    @Override
    public Field<Timestamp> field3() {
        return ViewWwMeaning.VIEW_WW_MEANING.LAST_APPROVE_OR_EDIT_EVENT_ON;
    }

    @Override
    public Field<JSON> field4() {
        return ViewWwMeaning.VIEW_WW_MEANING.DOMAIN_CODES;
    }

    @Override
    public Field<JSON> field5() {
        return ViewWwMeaning.VIEW_WW_MEANING.MEANING_IMAGES;
    }

    @Override
    public Field<JSON> field6() {
        return ViewWwMeaning.VIEW_WW_MEANING.MEDIA_FILES;
    }

    @Override
    public Field<String[]> field7() {
        return ViewWwMeaning.VIEW_WW_MEANING.SYSTEMATIC_POLYSEMY_PATTERNS;
    }

    @Override
    public Field<String[]> field8() {
        return ViewWwMeaning.VIEW_WW_MEANING.SEMANTIC_TYPES;
    }

    @Override
    public Field<String[]> field9() {
        return ViewWwMeaning.VIEW_WW_MEANING.LEARNER_COMMENTS;
    }

    @Override
    public Field<JSON> field10() {
        return ViewWwMeaning.VIEW_WW_MEANING.NOTES;
    }

    @Override
    public Field<JSON> field11() {
        return ViewWwMeaning.VIEW_WW_MEANING.DEFINITIONS;
    }

    @Override
    public Long component1() {
        return getMeaningId();
    }

    @Override
    public Timestamp component2() {
        return getManualEventOn();
    }

    @Override
    public Timestamp component3() {
        return getLastApproveOrEditEventOn();
    }

    @Override
    public JSON component4() {
        return getDomainCodes();
    }

    @Override
    public JSON component5() {
        return getMeaningImages();
    }

    @Override
    public JSON component6() {
        return getMediaFiles();
    }

    @Override
    public String[] component7() {
        return getSystematicPolysemyPatterns();
    }

    @Override
    public String[] component8() {
        return getSemanticTypes();
    }

    @Override
    public String[] component9() {
        return getLearnerComments();
    }

    @Override
    public JSON component10() {
        return getNotes();
    }

    @Override
    public JSON component11() {
        return getDefinitions();
    }

    @Override
    public Long value1() {
        return getMeaningId();
    }

    @Override
    public Timestamp value2() {
        return getManualEventOn();
    }

    @Override
    public Timestamp value3() {
        return getLastApproveOrEditEventOn();
    }

    @Override
    public JSON value4() {
        return getDomainCodes();
    }

    @Override
    public JSON value5() {
        return getMeaningImages();
    }

    @Override
    public JSON value6() {
        return getMediaFiles();
    }

    @Override
    public String[] value7() {
        return getSystematicPolysemyPatterns();
    }

    @Override
    public String[] value8() {
        return getSemanticTypes();
    }

    @Override
    public String[] value9() {
        return getLearnerComments();
    }

    @Override
    public JSON value10() {
        return getNotes();
    }

    @Override
    public JSON value11() {
        return getDefinitions();
    }

    @Override
    public ViewWwMeaningRecord value1(Long value) {
        setMeaningId(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value2(Timestamp value) {
        setManualEventOn(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value3(Timestamp value) {
        setLastApproveOrEditEventOn(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value4(JSON value) {
        setDomainCodes(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value5(JSON value) {
        setMeaningImages(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value6(JSON value) {
        setMediaFiles(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value7(String[] value) {
        setSystematicPolysemyPatterns(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value8(String[] value) {
        setSemanticTypes(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value9(String[] value) {
        setLearnerComments(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value10(JSON value) {
        setNotes(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord value11(JSON value) {
        setDefinitions(value);
        return this;
    }

    @Override
    public ViewWwMeaningRecord values(Long value1, Timestamp value2, Timestamp value3, JSON value4, JSON value5, JSON value6, String[] value7, String[] value8, String[] value9, JSON value10, JSON value11) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViewWwMeaningRecord
     */
    public ViewWwMeaningRecord() {
        super(ViewWwMeaning.VIEW_WW_MEANING);
    }

    /**
     * Create a detached, initialised ViewWwMeaningRecord
     */
    public ViewWwMeaningRecord(Long meaningId, Timestamp manualEventOn, Timestamp lastApproveOrEditEventOn, JSON domainCodes, JSON meaningImages, JSON mediaFiles, String[] systematicPolysemyPatterns, String[] semanticTypes, String[] learnerComments, JSON notes, JSON definitions) {
        super(ViewWwMeaning.VIEW_WW_MEANING);

        setMeaningId(meaningId);
        setManualEventOn(manualEventOn);
        setLastApproveOrEditEventOn(lastApproveOrEditEventOn);
        setDomainCodes(domainCodes);
        setMeaningImages(meaningImages);
        setMediaFiles(mediaFiles);
        setSystematicPolysemyPatterns(systematicPolysemyPatterns);
        setSemanticTypes(semanticTypes);
        setLearnerComments(learnerComments);
        setNotes(notes);
        setDefinitions(definitions);
    }
}