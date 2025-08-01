/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ViewWwMeaningRecord;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JSON;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row10;
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
    public final TableField<ViewWwMeaningRecord, LocalDateTime> MANUAL_EVENT_ON = createField(DSL.name("manual_event_on"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.view_ww_meaning.last_approve_or_edit_event_on</code>.
     */
    public final TableField<ViewWwMeaningRecord, LocalDateTime> LAST_APPROVE_OR_EDIT_EVENT_ON = createField(DSL.name("last_approve_or_edit_event_on"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.view_ww_meaning.domain_codes</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> DOMAIN_CODES = createField(DSL.name("domain_codes"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_meaning.definitions</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> DEFINITIONS = createField(DSL.name("definitions"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_meaning.meaning_images</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> MEANING_IMAGES = createField(DSL.name("meaning_images"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_meaning.meaning_medias</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> MEANING_MEDIAS = createField(DSL.name("meaning_medias"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_meaning.semantic_types</code>.
     */
    public final TableField<ViewWwMeaningRecord, String[]> SEMANTIC_TYPES = createField(DSL.name("semantic_types"), SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_meaning.learner_comments</code>.
     */
    public final TableField<ViewWwMeaningRecord, String[]> LEARNER_COMMENTS = createField(DSL.name("learner_comments"), SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_meaning.meaning_notes</code>.
     */
    public final TableField<ViewWwMeaningRecord, JSON> MEANING_NOTES = createField(DSL.name("meaning_notes"), SQLDataType.JSON, this, "");

    private ViewWwMeaning(Name alias, Table<ViewWwMeaningRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwMeaning(Name alias, Table<ViewWwMeaningRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_meaning\" as  SELECT m.meaning_id,\n    m.manual_event_on,\n    m.last_approve_or_edit_event_on,\n    m_dom.domain_codes,\n    m_def.definitions,\n    m_img.meaning_images,\n    m_med.meaning_medias,\n    m_smt.semantic_types,\n    m_lcm.learner_comments,\n    m_not.meaning_notes\n   FROM (((((((( SELECT m_1.id AS meaning_id,\n            m_1.manual_event_on,\n            ( SELECT al.event_on\n                   FROM meaning_last_activity_log mlal,\n                    activity_log al\n                  WHERE ((mlal.meaning_id = m_1.id) AND (mlal.activity_log_id = al.id))\n                  ORDER BY mlal.type\n                 LIMIT 1) AS last_approve_or_edit_event_on\n           FROM meaning m_1\n          WHERE (EXISTS ( SELECT l.id\n                   FROM lexeme l,\n                    dataset ds\n                  WHERE ((l.meaning_id = m_1.id) AND (l.is_public = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true) AND ((EXISTS ( SELECT 1\n                           FROM publishing p\n                          WHERE (((p.entity_name)::text = 'lexeme'::text) AND (p.entity_id = l.id)))) OR ((l.dataset_code)::text <> 'eki'::text)))))) m\n     LEFT JOIN ( SELECT m_dom_1.meaning_id,\n            json_agg(ROW(m_dom_1.domain_origin, m_dom_1.domain_code)::type_domain ORDER BY m_dom_1.order_by) AS domain_codes\n           FROM meaning_domain m_dom_1\n          GROUP BY m_dom_1.meaning_id) m_dom ON ((m_dom.meaning_id = m.meaning_id)))\n     LEFT JOIN ( SELECT md.meaning_id,\n            json_agg(json_build_object('id', md.definition_id, 'meaningId', md.meaning_id, 'value', md.value, 'valuePrese', md.value_prese, 'lang', md.lang, 'wwUnif', md.is_ww_unif, 'wwLite', md.is_ww_lite, 'wwOd', md.is_ww_od, 'notes', ( SELECT json_agg(json_build_object('id', dn.id, 'value', dn.value, 'valuePrese', dn.value_prese, 'lang', dn.lang, 'createdBy', dn.created_by, 'createdOn', dn.created_on, 'modifiedBy', dn.modified_by, 'modifiedOn', dn.modified_on, 'orderBy', dn.order_by, 'sourceLinks', ( SELECT json_agg(json_build_object('sourceLinkId', dnsl.id, 'sourceLinkName', dnsl.name, 'orderBy', dnsl.order_by, 'sourceId', s.id, 'sourceName', s.name, 'sourceValue', s.value, 'sourceValuePrese', s.value_prese, 'sourcePublic', s.is_public) ORDER BY dnsl.order_by) AS json_agg\n                           FROM definition_note_source_link dnsl,\n                            source s\n                          WHERE ((dnsl.definition_note_id = dn.id) AND (dnsl.source_id = s.id)))) ORDER BY dn.order_by) AS json_agg\n                   FROM definition_note dn\n                  WHERE ((dn.definition_id = md.definition_id) AND (dn.is_public = true))), 'sourceLinks', ( SELECT json_agg(json_build_object('sourceLinkId', dsl.id, 'sourceLinkName', dsl.name, 'orderBy', dsl.order_by, 'sourceId', s.id, 'sourceName', s.name, 'sourceValue', s.value, 'sourceValuePrese', s.value_prese, 'sourcePublic', s.is_public) ORDER BY dsl.order_by) AS json_agg\n                   FROM definition_source_link dsl,\n                    source s\n                  WHERE ((dsl.definition_id = md.definition_id) AND (dsl.source_id = s.id)))) ORDER BY md.order_by) AS definitions\n           FROM ( SELECT d.id AS definition_id,\n                    d.meaning_id,\n                    d.value,\n                    d.value_prese,\n                    d.lang,\n                    d.order_by,\n                    ((EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_unif'::text) AND ((p.entity_name)::text = 'definition'::text) AND (p.entity_id = d.id)))) OR (EXISTS ( SELECT 1\n                           FROM definition_dataset dd\n                          WHERE ((dd.definition_id = d.id) AND ((dd.dataset_code)::text <> 'eki'::text))))) AS is_ww_unif,\n                    (EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_lite'::text) AND ((p.entity_name)::text = 'definition'::text) AND (p.entity_id = d.id)))) AS is_ww_lite,\n                    (EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_od'::text) AND ((p.entity_name)::text = 'definition'::text) AND (p.entity_id = d.id)))) AS is_ww_od\n                   FROM definition d\n                  WHERE ((d.is_public = true) AND ((EXISTS ( SELECT 1\n                           FROM publishing p\n                          WHERE (((p.entity_name)::text = 'definition'::text) AND (p.entity_id = d.id)))) OR (EXISTS ( SELECT 1\n                           FROM definition_dataset dd\n                          WHERE ((dd.definition_id = d.id) AND ((dd.dataset_code)::text <> 'eki'::text))))))) md\n          GROUP BY md.meaning_id) m_def ON ((m_def.meaning_id = m.meaning_id)))\n     LEFT JOIN ( SELECT mi.meaning_id,\n            json_agg(json_build_object('id', mi.meaning_image_id, 'meaningId', mi.meaning_id, 'url', mi.url, 'title', mi.title, 'wwUnif', mi.is_ww_unif, 'wwLite', mi.is_ww_lite, 'wwOd', mi.is_ww_od, 'sourceLinks', ( SELECT json_agg(json_build_object('sourceLinkId', misl.id, 'sourceLinkName', misl.name, 'orderBy', misl.order_by, 'sourceId', s.id, 'sourceName', s.name, 'sourceValue', s.value, 'sourceValuePrese', s.value_prese, 'sourcePublic', s.is_public) ORDER BY misl.order_by) AS json_agg\n                   FROM meaning_image_source_link misl,\n                    source s\n                  WHERE ((misl.meaning_image_id = mi.meaning_image_id) AND (misl.source_id = s.id)))) ORDER BY mi.order_by) AS meaning_images\n           FROM ( SELECT mi_1.id AS meaning_image_id,\n                    mi_1.meaning_id,\n                    mi_1.url,\n                    mi_1.title,\n                    mi_1.order_by,\n                    ((EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_unif'::text) AND ((p.entity_name)::text = 'meaning_image'::text) AND (p.entity_id = mi_1.id)))) OR (EXISTS ( SELECT 1\n                           FROM lexeme l\n                          WHERE ((l.meaning_id = mi_1.meaning_id) AND ((l.dataset_code)::text <> ALL ((ARRAY['eki'::character varying, 'ety'::character varying])::text[])))))) AS is_ww_unif,\n                    (EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_lite'::text) AND ((p.entity_name)::text = 'meaning_image'::text) AND (p.entity_id = mi_1.id)))) AS is_ww_lite,\n                    (EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_od'::text) AND ((p.entity_name)::text = 'meaning_image'::text) AND (p.entity_id = mi_1.id)))) AS is_ww_od\n                   FROM meaning_image mi_1\n                  WHERE ((EXISTS ( SELECT 1\n                           FROM publishing p\n                          WHERE (((p.entity_name)::text = 'meaning_image'::text) AND (p.entity_id = mi_1.id)))) OR (EXISTS ( SELECT 1\n                           FROM lexeme l\n                          WHERE ((l.meaning_id = mi_1.meaning_id) AND ((l.dataset_code)::text <> ALL ((ARRAY['eki'::character varying, 'ety'::character varying])::text[]))))))) mi\n          GROUP BY mi.meaning_id) m_img ON ((m_img.meaning_id = m.meaning_id)))\n     LEFT JOIN ( SELECT mm.meaning_id,\n            json_agg(json_build_object('id', mm.meaning_media_id, 'meaningId', mm.meaning_id, 'url', mm.url, 'wwUnif', mm.is_ww_unif, 'wwLite', mm.is_ww_lite, 'wwOd', mm.is_ww_od) ORDER BY mm.order_by) AS meaning_medias\n           FROM ( SELECT mm_1.id AS meaning_media_id,\n                    mm_1.meaning_id,\n                    mm_1.url,\n                    mm_1.order_by,\n                    ((EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_unif'::text) AND ((p.entity_name)::text = 'meaning_media'::text) AND (p.entity_id = mm_1.id)))) OR (EXISTS ( SELECT 1\n                           FROM lexeme l\n                          WHERE ((l.meaning_id = mm_1.meaning_id) AND ((l.dataset_code)::text <> ALL ((ARRAY['eki'::character varying, 'ety'::character varying])::text[])))))) AS is_ww_unif,\n                    (EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_lite'::text) AND ((p.entity_name)::text = 'meaning_media'::text) AND (p.entity_id = mm_1.id)))) AS is_ww_lite,\n                    (EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_od'::text) AND ((p.entity_name)::text = 'meaning_media'::text) AND (p.entity_id = mm_1.id)))) AS is_ww_od\n                   FROM meaning_media mm_1\n                  WHERE ((EXISTS ( SELECT 1\n                           FROM publishing p\n                          WHERE (((p.entity_name)::text = 'meaning_media'::text) AND (p.entity_id = mm_1.id)))) OR (EXISTS ( SELECT 1\n                           FROM lexeme l\n                          WHERE ((l.meaning_id = mm_1.meaning_id) AND ((l.dataset_code)::text <> ALL ((ARRAY['eki'::character varying, 'ety'::character varying])::text[]))))))) mm\n          GROUP BY mm.meaning_id) m_med ON ((m_med.meaning_id = m.meaning_id)))\n     LEFT JOIN ( SELECT mst.meaning_id,\n            array_agg(mst.semantic_type_code ORDER BY mst.order_by) AS semantic_types\n           FROM meaning_semantic_type mst\n          GROUP BY mst.meaning_id) m_smt ON ((m_smt.meaning_id = m.meaning_id)))\n     LEFT JOIN ( SELECT lc.meaning_id,\n            array_agg(lc.value_prese ORDER BY lc.order_by) AS learner_comments\n           FROM learner_comment lc\n          GROUP BY lc.meaning_id) m_lcm ON ((m_lcm.meaning_id = m.meaning_id)))\n     LEFT JOIN ( SELECT mn.meaning_id,\n            json_agg(json_build_object('id', mn.meaning_note_id, 'value', mn.value, 'valuePrese', mn.value_prese, 'lang', mn.lang, 'createdBy', mn.created_by, 'createdOn', mn.created_on, 'modifiedBy', mn.modified_by, 'modifiedOn', mn.modified_on, 'orderBy', mn.order_by, 'wwUnif', mn.is_ww_unif, 'wwLite', mn.is_ww_lite, 'wwOd', mn.is_ww_od, 'sourceLinks', ( SELECT json_agg(json_build_object('sourceLinkId', mnsl.id, 'sourceLinkName', mnsl.name, 'orderBy', mnsl.order_by, 'sourceId', s.id, 'sourceName', s.name, 'sourceValue', s.value, 'sourceValuePrese', s.value_prese, 'sourcePublic', s.is_public) ORDER BY mnsl.order_by) AS json_agg\n                   FROM meaning_note_source_link mnsl,\n                    source s\n                  WHERE ((mnsl.meaning_note_id = mn.meaning_note_id) AND (mnsl.source_id = s.id)))) ORDER BY mn.order_by) AS meaning_notes\n           FROM ( SELECT mn_1.id AS meaning_note_id,\n                    mn_1.meaning_id,\n                    mn_1.value,\n                    mn_1.value_prese,\n                    mn_1.lang,\n                    mn_1.created_by,\n                    mn_1.created_on,\n                    mn_1.modified_by,\n                    mn_1.modified_on,\n                    mn_1.order_by,\n                    ((EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_unif'::text) AND ((p.entity_name)::text = 'meaning_note'::text) AND (p.entity_id = mn_1.id)))) OR (EXISTS ( SELECT 1\n                           FROM lexeme l\n                          WHERE ((l.meaning_id = mn_1.meaning_id) AND ((l.dataset_code)::text <> ALL ((ARRAY['eki'::character varying, 'ety'::character varying])::text[])))))) AS is_ww_unif,\n                    (EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_lite'::text) AND ((p.entity_name)::text = 'meaning_note'::text) AND (p.entity_id = mn_1.id)))) AS is_ww_lite,\n                    (EXISTS ( SELECT p.id\n                           FROM publishing p\n                          WHERE (((p.target_name)::text = 'ww_od'::text) AND ((p.entity_name)::text = 'meaning_note'::text) AND (p.entity_id = mn_1.id)))) AS is_ww_od\n                   FROM meaning_note mn_1\n                  WHERE ((mn_1.is_public = true) AND ((EXISTS ( SELECT 1\n                           FROM publishing p\n                          WHERE (((p.entity_name)::text = 'meaning_note'::text) AND (p.entity_id = mn_1.id)))) OR (EXISTS ( SELECT 1\n                           FROM lexeme l\n                          WHERE ((l.meaning_id = mn_1.meaning_id) AND ((l.dataset_code)::text <> ALL ((ARRAY['eki'::character varying, 'ety'::character varying])::text[])))))))) mn\n          GROUP BY mn.meaning_id) m_not ON ((m_not.meaning_id = m.meaning_id)))\n  ORDER BY m.meaning_id;"));
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
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<Long, LocalDateTime, LocalDateTime, JSON, JSON, JSON, JSON, String[], String[], JSON> fieldsRow() {
        return (Row10) super.fieldsRow();
    }
}
