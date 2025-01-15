/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ViewWwMeaningRecord;

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
     * The column <code>public.view_ww_meaning.meaning_images</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> MEANING_IMAGES = createField(DSL.name("meaning_images"), SQLDataType.JSON, this, "");

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
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_meaning\" as  SELECT m.id AS meaning_id,\n    m.manual_event_on,\n    m.last_approve_or_edit_event_on,\n    m_dom.domain_codes,\n    m_img.meaning_images,\n    m_media.media_files,\n    m_spp.systematic_polysemy_patterns,\n    m_smt.semantic_types,\n    m_lcm.learner_comments,\n    m_pnt.notes,\n    d.definitions\n   FROM ((((((((( SELECT m_1.id,\n            m_1.manual_event_on,\n            ( SELECT al.event_on\n                   FROM meaning_last_activity_log mlal,\n                    activity_log al\n                  WHERE ((mlal.meaning_id = m_1.id) AND (mlal.activity_log_id = al.id))\n                  ORDER BY mlal.type\n                 LIMIT 1) AS last_approve_or_edit_event_on\n           FROM meaning m_1\n          WHERE (EXISTS ( SELECT l.id\n                   FROM lexeme l,\n                    dataset ds\n                  WHERE ((l.meaning_id = m_1.id) AND (l.is_public = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true))))) m\n     LEFT JOIN ( SELECT m_dom_1.meaning_id,\n            json_agg(ROW(m_dom_1.domain_origin, m_dom_1.domain_code)::type_domain ORDER BY m_dom_1.order_by) AS domain_codes\n           FROM meaning_domain m_dom_1\n          GROUP BY m_dom_1.meaning_id) m_dom ON ((m_dom.meaning_id = m.id)))\n     LEFT JOIN ( SELECT d_1.meaning_id,\n            json_agg(ROW(NULL::bigint, d_1.meaning_id, d_1.id, d_1.value, d_1.value_prese, d_1.lang, d_1.complexity, d_1.source_links, d_1.notes)::type_definition ORDER BY d_1.order_by) AS definitions\n           FROM ( SELECT d_2.meaning_id,\n                    d_2.id,\n                    \"substring\"(d_2.value, 1, 2000) AS value,\n                    \"substring\"(d_2.value_prese, 1, 2000) AS value_prese,\n                    d_2.lang,\n                    d_2.complexity,\n                    d_2.order_by,\n                    dsl.source_links,\n                    ( SELECT json_agg(notes.*) AS notes\n                           FROM ( SELECT dn.value,\n                                    dn.value_prese,\n                                    json_agg(ROW(dnsl.source_link_id, dnsl.name, dnsl.order_by, dnsl.source_id, dnsl.source_name, dnsl.source_value, dnsl.source_value_prese, dnsl.is_source_public)::type_source_link ORDER BY dnsl.order_by) AS source_links\n                                   FROM (definition_note dn\n                                     LEFT JOIN ( SELECT dnsl_1.definition_note_id,\n    dnsl_1.id AS source_link_id,\n    dnsl_1.name,\n    dnsl_1.order_by,\n    s.id AS source_id,\n    s.name AS source_name,\n    s.value AS source_value,\n    s.value_prese AS source_value_prese,\n    s.is_public AS is_source_public\n   FROM definition_note_source_link dnsl_1,\n    source s\n  WHERE (dnsl_1.source_id = s.id)) dnsl ON ((dnsl.definition_note_id = dn.id)))\n                                  WHERE ((dn.definition_id = d_2.id) AND (dn.is_public = true))\n                                  GROUP BY dn.id, dn.value_prese) notes) AS notes\n                   FROM (definition d_2\n                     LEFT JOIN ( SELECT dsl_1.definition_id,\n                            json_agg(ROW(dsl_1.id, dsl_1.name, dsl_1.order_by, s.id, s.name, s.value, s.value_prese, s.is_public)::type_source_link ORDER BY dsl_1.definition_id, dsl_1.order_by) AS source_links\n                           FROM definition_source_link dsl_1,\n                            source s\n                          WHERE (dsl_1.source_id = s.id)\n                          GROUP BY dsl_1.definition_id) dsl ON ((dsl.definition_id = d_2.id)))\n                  WHERE (d_2.is_public = true)) d_1\n          GROUP BY d_1.meaning_id) d ON ((d.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mi.meaning_id,\n            json_agg(ROW(mi.id, mi.url, mi.title, mi.complexity, misl.source_links)::type_media_file ORDER BY mi.order_by) AS meaning_images\n           FROM (meaning_image mi\n             LEFT JOIN ( SELECT misl_1.meaning_image_id,\n                    json_agg(ROW(misl_1.id, misl_1.name, misl_1.order_by, s.id, s.name, s.value, s.value_prese, s.is_public)::type_source_link ORDER BY misl_1.meaning_image_id, misl_1.order_by) AS source_links\n                   FROM meaning_image_source_link misl_1,\n                    source s\n                  WHERE (misl_1.source_id = s.id)\n                  GROUP BY misl_1.meaning_image_id) misl ON ((misl.meaning_image_id = mi.id)))\n          GROUP BY mi.meaning_id) m_img ON ((m_img.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mff.meaning_id,\n            json_agg(ROW(ff_mf.id, ff_mf.value, NULL::text, ff_mf.complexity, NULL::json)::type_media_file ORDER BY ff_mf.order_by) AS media_files\n           FROM meaning_freeform mff,\n            freeform ff_mf\n          WHERE ((ff_mf.id = mff.freeform_id) AND ((ff_mf.freeform_type_code)::text = 'MEDIA_FILE'::text))\n          GROUP BY mff.meaning_id) m_media ON ((m_media.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mf.meaning_id,\n            array_agg(ff.value ORDER BY ff.order_by) AS systematic_polysemy_patterns\n           FROM meaning_freeform mf,\n            freeform ff\n          WHERE ((mf.freeform_id = ff.id) AND ((ff.freeform_type_code)::text = 'SYSTEMATIC_POLYSEMY_PATTERN'::text))\n          GROUP BY mf.meaning_id) m_spp ON ((m_spp.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mf.meaning_id,\n            array_agg(ff.value ORDER BY ff.order_by) AS semantic_types\n           FROM meaning_freeform mf,\n            freeform ff\n          WHERE ((mf.freeform_id = ff.id) AND ((ff.freeform_type_code)::text = 'SEMANTIC_TYPE'::text))\n          GROUP BY mf.meaning_id) m_smt ON ((m_smt.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mf.meaning_id,\n            array_agg(ff.value_prese ORDER BY ff.order_by) AS learner_comments\n           FROM meaning_freeform mf,\n            freeform ff\n          WHERE ((mf.freeform_id = ff.id) AND ((ff.freeform_type_code)::text = 'LEARNER_COMMENT'::text))\n          GROUP BY mf.meaning_id) m_lcm ON ((m_lcm.meaning_id = m.id)))\n     LEFT JOIN ( SELECT mn.meaning_id,\n            json_agg(ROW(mn.meaning_note_id, mn.value, mn.value_prese, mn.lang, mn.complexity, mn.created_by, mn.created_on, mn.modified_by, mn.modified_on, mn.source_links)::type_note ORDER BY mn.order_by) AS notes\n           FROM ( SELECT mn_1.meaning_id,\n                    mn_1.id AS meaning_note_id,\n                    mn_1.value,\n                    mn_1.value_prese,\n                    mn_1.lang,\n                    mn_1.complexity,\n                    mn_1.created_by,\n                    mn_1.created_on,\n                    mn_1.modified_by,\n                    mn_1.modified_on,\n                    mn_1.order_by,\n                    mnsl.source_links\n                   FROM (meaning_note mn_1\n                     LEFT JOIN ( SELECT mnsl_1.meaning_note_id,\n                            json_agg(ROW(mnsl_1.id, mnsl_1.name, mnsl_1.order_by, s.id, s.name, s.value, s.value_prese, s.is_public)::type_source_link ORDER BY mnsl_1.meaning_note_id, mnsl_1.order_by) AS source_links\n                           FROM meaning_note_source_link mnsl_1,\n                            source s\n                          WHERE (mnsl_1.source_id = s.id)\n                          GROUP BY mnsl_1.meaning_note_id) mnsl ON ((mnsl.meaning_note_id = mn_1.id)))\n                  WHERE (mn_1.is_public = true)) mn\n          GROUP BY mn.meaning_id) m_pnt ON ((m_pnt.meaning_id = m.id)))\n  ORDER BY m.id;"));
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
