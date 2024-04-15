/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ViewWwMeaningRecord;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JSON;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row11;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwMeaning extends TableImpl<ViewWwMeaningRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_meaning</code>
     */
    public static final ViewWwMeaning VIEW_WW_MEANING = new ViewWwMeaning();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwMeaningRecord> getRecordType() {
        return ViewWwMeaningRecord.class;
    }

    /**
     * The column <code>public.view_ww_meaning.meaning_id</code>.
     */
    public final TableField<ViewWwMeaningRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_meaning.manual_event_on</code>.
     */
    public final TableField<ViewWwMeaningRecord, Timestamp> MANUAL_EVENT_ON = createField(DSL.name("manual_event_on"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>public.view_ww_meaning.last_approve_or_edit_event_on</code>.
     */
    public final TableField<ViewWwMeaningRecord, Timestamp> LAST_APPROVE_OR_EDIT_EVENT_ON = createField(DSL.name("last_approve_or_edit_event_on"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>public.view_ww_meaning.domain_codes</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> DOMAIN_CODES = createField(DSL.name("domain_codes"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_meaning.image_files</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> IMAGE_FILES = createField(DSL.name("image_files"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_meaning.media_files</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> MEDIA_FILES = createField(DSL.name("media_files"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_meaning.systematic_polysemy_patterns</code>.
     */
    public final TableField<ViewWwMeaningRecord, String[]> SYSTEMATIC_POLYSEMY_PATTERNS = createField(DSL.name("systematic_polysemy_patterns"), SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_meaning.semantic_types</code>.
     */
    public final TableField<ViewWwMeaningRecord, String[]> SEMANTIC_TYPES = createField(DSL.name("semantic_types"), SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_meaning.learner_comments</code>.
     */
    public final TableField<ViewWwMeaningRecord, String[]> LEARNER_COMMENTS = createField(DSL.name("learner_comments"), SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_meaning.notes</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> NOTES = createField(DSL.name("notes"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_meaning.definitions</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> DEFINITIONS = createField(DSL.name("definitions"), SQLDataType.JSON, this, "");

    private ViewWwMeaning(Name alias, Table<ViewWwMeaningRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwMeaning(Name alias, Table<ViewWwMeaningRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_meaning\" as  SELECT m.id AS meaning_id,\n    m.manual_event_on,\n    m.last_approve_or_edit_event_on,\n    m_dom.domain_codes,\n    m_img.image_files,\n    m_media.media_files,\n    m_spp.systematic_polysemy_patterns,\n    m_smt.semantic_types,\n    m_lcm.learner_comments,\n    m_pnt.notes,\n    d.definitions\n   FROM ((((((((( SELECT m_1.id,\n            m_1.manual_event_on,\n            ( SELECT al.event_on\n                   FROM meaning_last_activity_log_fdw mlal,\n                    activity_log_fdw al\n                  WHERE ((mlal.meaning_id = m_1.id) AND (mlal.activity_log_id = al.id))\n                  ORDER BY mlal.type\n                 LIMIT 1) AS last_approve_or_edit_event_on\n           FROM meaning m_1\n          WHERE (EXISTS ( SELECT l.id\n                   FROM lexeme l,\n                    dataset ds\n                  WHERE ((l.meaning_id = m_1.id) AND (l.is_public = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true))))) m\n     LEFT JOIN ( SELECT m_dom_1.meaning_id,\n            json_agg(ROW(m_dom_1.domain_origin, m_dom_1.domain_code)::type_domain ORDER BY m_dom_1.order_by) AS domain_codes\n           FROM meaning_domain m_dom_1\n          GROUP BY m_dom_1.meaning_id) m_dom ON ((m_dom.meaning_id = m.id)))\n     LEFT JOIN ( SELECT d_1.meaning_id,\n            json_agg(ROW(NULL::bigint, d_1.meaning_id, d_1.id, d_1.value, d_1.value_prese, d_1.lang, d_1.complexity, d_1.source_links, d_1.notes)::type_definition ORDER BY d_1.order_by) AS definitions\n           FROM ( SELECT d_2.meaning_id,\n                    d_2.id,\n                    \"substring\"(d_2.value, 1, 2000) AS value,\n                    \"substring\"(d_2.value_prese, 1, 2000) AS value_prese,\n                    d_2.lang,\n                    d_2.complexity,\n                    d_2.order_by,\n                    source_links.source_links,\n                    ( SELECT json_agg(notes.*) AS notes\n                           FROM ( SELECT ff.value_prese AS value,\n                                    json_agg(ROW('FREEFORM'::character varying(100), ffsl.freeform_id, ffsl.source_link_id, ffsl.type, ffsl.name, ffsl.order_by, ffsl.source_id, ffsl.source_name, ffsl.source_value, ffsl.source_value_prese, ffsl.is_source_public)::type_source_link ORDER BY ffsl.freeform_id, ffsl.order_by) AS source_links\n                                   FROM definition_freeform dff,\n                                    (freeform ff\n                                     LEFT JOIN ( SELECT ffsl_1.freeform_id,\n    ffsl_1.id AS source_link_id,\n    ffsl_1.type,\n    ffsl_1.name,\n    ffsl_1.value,\n    ffsl_1.order_by,\n    s.id AS source_id,\n    s.name AS source_name,\n    s.value AS source_value,\n    s.value_prese AS source_value_prese,\n    s.is_public AS is_source_public\n   FROM freeform_source_link ffsl_1,\n    source s\n  WHERE (ffsl_1.source_id = s.id)) ffsl ON ((ffsl.freeform_id = ff.id)))\n                                  WHERE ((dff.definition_id = d_2.id) AND (ff.id = dff.freeform_id) AND ((ff.type)::text = 'NOTE'::text) AND (ff.is_public = true))\n                                  GROUP BY ff.id, ff.value_prese) notes) AS notes\n                   FROM (definition d_2\n                     LEFT JOIN ( SELECT dsl.definition_id,\n                            json_agg(ROW('DEFINITION'::character varying(100), dsl.definition_id, dsl.id, dsl.type, dsl.name, dsl.order_by, s.id, s.name, s.value, s.value_prese, s.is_public)::type_source_link ORDER BY dsl.definition_id, dsl.order_by) AS source_links\n                           FROM definition_source_link dsl,\n                            source s\n                          WHERE (dsl.source_id = s.id)\n                          GROUP BY dsl.definition_id) source_links ON ((source_links.definition_id = d_2.id)))\n                  WHERE (d_2.is_public = true)) d_1\n          GROUP BY d_1.meaning_id) d ON ((d.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mff.meaning_id,\n            json_agg(ROW(ff_if.id, ff_if.value_text, ff_it.value_text, ff_if.complexity)::type_media_file ORDER BY ff_if.order_by, ff_it.order_by) AS image_files\n           FROM ((meaning_freeform mff\n             JOIN freeform ff_if ON (((ff_if.id = mff.freeform_id) AND ((ff_if.type)::text = 'IMAGE_FILE'::text))))\n             LEFT JOIN freeform ff_it ON (((ff_it.parent_id = ff_if.id) AND ((ff_it.type)::text = 'IMAGE_TITLE'::text))))\n          GROUP BY mff.meaning_id) m_img ON ((m_img.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mff.meaning_id,\n            json_agg(ROW(ff_mf.id, ff_mf.value_text, NULL::text, ff_mf.complexity)::type_media_file ORDER BY ff_mf.order_by) AS media_files\n           FROM (meaning_freeform mff\n             JOIN freeform ff_mf ON (((ff_mf.id = mff.freeform_id) AND ((ff_mf.type)::text = 'MEDIA_FILE'::text))))\n          GROUP BY mff.meaning_id) m_media ON ((m_media.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mf.meaning_id,\n            array_agg(ff.value_text ORDER BY ff.order_by) AS systematic_polysemy_patterns\n           FROM meaning_freeform mf,\n            freeform ff\n          WHERE ((mf.freeform_id = ff.id) AND ((ff.type)::text = 'SYSTEMATIC_POLYSEMY_PATTERN'::text))\n          GROUP BY mf.meaning_id) m_spp ON ((m_spp.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mf.meaning_id,\n            array_agg(ff.value_text ORDER BY ff.order_by) AS semantic_types\n           FROM meaning_freeform mf,\n            freeform ff\n          WHERE ((mf.freeform_id = ff.id) AND ((ff.type)::text = 'SEMANTIC_TYPE'::text))\n          GROUP BY mf.meaning_id) m_smt ON ((m_smt.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mf.meaning_id,\n            array_agg(ff.value_prese ORDER BY ff.order_by) AS learner_comments\n           FROM meaning_freeform mf,\n            freeform ff\n          WHERE ((mf.freeform_id = ff.id) AND ((ff.type)::text = 'LEARNER_COMMENT'::text))\n          GROUP BY mf.meaning_id) m_lcm ON ((m_lcm.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mf.meaning_id,\n            json_agg(ROW(ff.id, ff.type, ff.value_prese, ff.lang, ff.complexity, ff.created_by, ff.created_on, ff.modified_by, ff.modified_on)::type_freeform ORDER BY ff.order_by) AS notes\n           FROM meaning_freeform mf,\n            freeform ff\n          WHERE ((mf.freeform_id = ff.id) AND ((ff.type)::text = 'NOTE'::text) AND (ff.is_public = true))\n          GROUP BY mf.meaning_id) m_pnt ON ((m_pnt.meaning_id = m.id)))\n  ORDER BY m.id;"));
    }

    /**
     * Create an aliased <code>public.view_ww_meaning</code> table reference
     */
    public ViewWwMeaning(String alias) {
        this(DSL.name(alias), VIEW_WW_MEANING);
    }

    /**
     * Create an aliased <code>public.view_ww_meaning</code> table reference
     */
    public ViewWwMeaning(Name alias) {
        this(alias, VIEW_WW_MEANING);
    }

    /**
     * Create a <code>public.view_ww_meaning</code> table reference
     */
    public ViewWwMeaning() {
        this(DSL.name("view_ww_meaning"), null);
    }

    public <O extends Record> ViewWwMeaning(Table<O> child, ForeignKey<O, ViewWwMeaningRecord> key) {
        super(child, key, VIEW_WW_MEANING);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwMeaning as(String alias) {
        return new ViewWwMeaning(DSL.name(alias), this);
    }

    @Override
    public ViewWwMeaning as(Name alias) {
        return new ViewWwMeaning(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwMeaning rename(String name) {
        return new ViewWwMeaning(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwMeaning rename(Name name) {
        return new ViewWwMeaning(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<Long, Timestamp, Timestamp, JSON, JSON, JSON, String[], String[], String[], JSON, JSON> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}
