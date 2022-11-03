/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ViewWwClassifierRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
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
public class ViewWwClassifier extends TableImpl<ViewWwClassifierRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_classifier</code>
     */
    public static final ViewWwClassifier VIEW_WW_CLASSIFIER = new ViewWwClassifier();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwClassifierRecord> getRecordType() {
        return ViewWwClassifierRecord.class;
    }

    /**
     * The column <code>public.view_ww_classifier.name</code>.
     */
    public final TableField<ViewWwClassifierRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_classifier.origin</code>.
     */
    public final TableField<ViewWwClassifierRecord, String> ORIGIN = createField(DSL.name("origin"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_classifier.code</code>.
     */
    public final TableField<ViewWwClassifierRecord, String> CODE = createField(DSL.name("code"), SQLDataType.CHAR, this, "");

    /**
     * The column <code>public.view_ww_classifier.value</code>.
     */
    public final TableField<ViewWwClassifierRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_classifier.lang</code>.
     */
    public final TableField<ViewWwClassifierRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), this, "");

    /**
     * The column <code>public.view_ww_classifier.type</code>.
     */
    public final TableField<ViewWwClassifierRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.view_ww_classifier.order_by</code>.
     */
    public final TableField<ViewWwClassifierRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT, this, "");

    private ViewWwClassifier(Name alias, Table<ViewWwClassifierRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwClassifier(Name alias, Table<ViewWwClassifierRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_classifier\" as ( SELECT 'LANGUAGE'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM language c,\n    language_label cl\n  WHERE ((c.code = cl.code) AND ((cl.type)::text = ANY ((ARRAY['wordweb'::character varying, 'iso2'::character varying])::text[])))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'MORPH'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM morph c,\n    morph_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'DISPLAY_MORPH'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM display_morph c,\n    display_morph_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'WORD_TYPE'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM word_type c,\n    word_type_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'GENDER'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM gender c,\n    gender_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'ASPECT'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM aspect c,\n    aspect_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'POS'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM pos c,\n    pos_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'REGISTER'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM register c,\n    register_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'DERIV'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM deriv c,\n    deriv_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'DOMAIN'::text AS name,\n    c.origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    'wordweb'::character varying AS type,\n    c.order_by\n   FROM domain c,\n    domain_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((c.origin)::text = (cl.origin)::text) AND ((cl.type)::text = 'descrip'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'USAGE_TYPE'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM usage_type c,\n    usage_type_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'POS_GROUP'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM pos_group c,\n    pos_group_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'WORD_REL_TYPE'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM word_rel_type c,\n    word_rel_type_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'LEX_REL_TYPE'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM lex_rel_type c,\n    lex_rel_type_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'MEANING_REL_TYPE'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM meaning_rel_type c,\n    meaning_rel_type_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type)\nUNION ALL\n( SELECT 'VALUE_STATE'::text AS name,\n    NULL::text AS origin,\n    c.code,\n    cl.value,\n    cl.lang,\n    cl.type,\n    c.order_by\n   FROM value_state c,\n    value_state_label cl\n  WHERE (((c.code)::text = (cl.code)::text) AND ((cl.type)::text = 'wordweb'::text))\n  ORDER BY c.order_by, cl.lang, cl.type);"));
    }

    /**
     * Create an aliased <code>public.view_ww_classifier</code> table reference
     */
    public ViewWwClassifier(String alias) {
        this(DSL.name(alias), VIEW_WW_CLASSIFIER);
    }

    /**
     * Create an aliased <code>public.view_ww_classifier</code> table reference
     */
    public ViewWwClassifier(Name alias) {
        this(alias, VIEW_WW_CLASSIFIER);
    }

    /**
     * Create a <code>public.view_ww_classifier</code> table reference
     */
    public ViewWwClassifier() {
        this(DSL.name("view_ww_classifier"), null);
    }

    public <O extends Record> ViewWwClassifier(Table<O> child, ForeignKey<O, ViewWwClassifierRecord> key) {
        super(child, key, VIEW_WW_CLASSIFIER);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwClassifier as(String alias) {
        return new ViewWwClassifier(DSL.name(alias), this);
    }

    @Override
    public ViewWwClassifier as(Name alias) {
        return new ViewWwClassifier(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwClassifier rename(String name) {
        return new ViewWwClassifier(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwClassifier rename(Name name) {
        return new ViewWwClassifier(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, String, String, String, String, String, Long> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
