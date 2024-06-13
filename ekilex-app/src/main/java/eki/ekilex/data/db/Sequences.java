/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db;


import org.jooq.Sequence;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;


/**
 * Convenience access to all sequences in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>public.activity_log_id_seq</code>
     */
    public static final Sequence<Long> ACTIVITY_LOG_ID_SEQ = Internal.createSequence("activity_log_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.aspect_order_by_seq</code>
     */
    public static final Sequence<Long> ASPECT_ORDER_BY_SEQ = Internal.createSequence("aspect_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.collocation_id_seq</code>
     */
    public static final Sequence<Long> COLLOCATION_ID_SEQ = Internal.createSequence("collocation_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.collocation_member_id_seq</code>
     */
    public static final Sequence<Long> COLLOCATION_MEMBER_ID_SEQ = Internal.createSequence("collocation_member_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.data_request_id_seq</code>
     */
    public static final Sequence<Long> DATA_REQUEST_ID_SEQ = Internal.createSequence("data_request_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.dataset_order_by_seq</code>
     */
    public static final Sequence<Long> DATASET_ORDER_BY_SEQ = Internal.createSequence("dataset_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.dataset_permission_id_seq</code>
     */
    public static final Sequence<Long> DATASET_PERMISSION_ID_SEQ = Internal.createSequence("dataset_permission_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.definition_freeform_id_seq</code>
     */
    public static final Sequence<Long> DEFINITION_FREEFORM_ID_SEQ = Internal.createSequence("definition_freeform_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.definition_id_seq</code>
     */
    public static final Sequence<Long> DEFINITION_ID_SEQ = Internal.createSequence("definition_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.definition_order_by_seq</code>
     */
    public static final Sequence<Long> DEFINITION_ORDER_BY_SEQ = Internal.createSequence("definition_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.definition_source_link_id_seq</code>
     */
    public static final Sequence<Long> DEFINITION_SOURCE_LINK_ID_SEQ = Internal.createSequence("definition_source_link_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.definition_source_link_order_by_seq</code>
     */
    public static final Sequence<Long> DEFINITION_SOURCE_LINK_ORDER_BY_SEQ = Internal.createSequence("definition_source_link_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.definition_type_order_by_seq</code>
     */
    public static final Sequence<Long> DEFINITION_TYPE_ORDER_BY_SEQ = Internal.createSequence("definition_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.deriv_order_by_seq</code>
     */
    public static final Sequence<Long> DERIV_ORDER_BY_SEQ = Internal.createSequence("deriv_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.display_morph_order_by_seq</code>
     */
    public static final Sequence<Long> DISPLAY_MORPH_ORDER_BY_SEQ = Internal.createSequence("display_morph_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.domain_order_by_seq</code>
     */
    public static final Sequence<Long> DOMAIN_ORDER_BY_SEQ = Internal.createSequence("domain_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.eki_user_application_id_seq</code>
     */
    public static final Sequence<Long> EKI_USER_APPLICATION_ID_SEQ = Internal.createSequence("eki_user_application_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.eki_user_id_seq</code>
     */
    public static final Sequence<Long> EKI_USER_ID_SEQ = Internal.createSequence("eki_user_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.eki_user_profile_id_seq</code>
     */
    public static final Sequence<Long> EKI_USER_PROFILE_ID_SEQ = Internal.createSequence("eki_user_profile_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.etymology_type_order_by_seq</code>
     */
    public static final Sequence<Long> ETYMOLOGY_TYPE_ORDER_BY_SEQ = Internal.createSequence("etymology_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.feedback_log_comment_id_seq</code>
     */
    public static final Sequence<Long> FEEDBACK_LOG_COMMENT_ID_SEQ = Internal.createSequence("feedback_log_comment_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.feedback_log_id_seq</code>
     */
    public static final Sequence<Long> FEEDBACK_LOG_ID_SEQ = Internal.createSequence("feedback_log_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.form_freq_id_seq</code>
     */
    public static final Sequence<Long> FORM_FREQ_ID_SEQ = Internal.createSequence("form_freq_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.form_id_seq</code>
     */
    public static final Sequence<Long> FORM_ID_SEQ = Internal.createSequence("form_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.freeform_id_seq</code>
     */
    public static final Sequence<Long> FREEFORM_ID_SEQ = Internal.createSequence("freeform_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.freeform_order_by_seq</code>
     */
    public static final Sequence<Long> FREEFORM_ORDER_BY_SEQ = Internal.createSequence("freeform_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.freeform_source_link_id_seq</code>
     */
    public static final Sequence<Long> FREEFORM_SOURCE_LINK_ID_SEQ = Internal.createSequence("freeform_source_link_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.freeform_source_link_order_by_seq</code>
     */
    public static final Sequence<Long> FREEFORM_SOURCE_LINK_ORDER_BY_SEQ = Internal.createSequence("freeform_source_link_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.freq_corp_id_seq</code>
     */
    public static final Sequence<Long> FREQ_CORP_ID_SEQ = Internal.createSequence("freq_corp_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.game_nonword_id_seq</code>
     */
    public static final Sequence<Long> GAME_NONWORD_ID_SEQ = Internal.createSequence("game_nonword_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.gender_order_by_seq</code>
     */
    public static final Sequence<Long> GENDER_ORDER_BY_SEQ = Internal.createSequence("gender_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.government_type_order_by_seq</code>
     */
    public static final Sequence<Long> GOVERNMENT_TYPE_ORDER_BY_SEQ = Internal.createSequence("government_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.language_order_by_seq</code>
     */
    public static final Sequence<Long> LANGUAGE_ORDER_BY_SEQ = Internal.createSequence("language_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lex_colloc_id_seq</code>
     */
    public static final Sequence<Long> LEX_COLLOC_ID_SEQ = Internal.createSequence("lex_colloc_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lex_colloc_pos_group_id_seq</code>
     */
    public static final Sequence<Long> LEX_COLLOC_POS_GROUP_ID_SEQ = Internal.createSequence("lex_colloc_pos_group_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lex_colloc_pos_group_order_by_seq</code>
     */
    public static final Sequence<Long> LEX_COLLOC_POS_GROUP_ORDER_BY_SEQ = Internal.createSequence("lex_colloc_pos_group_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lex_colloc_rel_group_id_seq</code>
     */
    public static final Sequence<Long> LEX_COLLOC_REL_GROUP_ID_SEQ = Internal.createSequence("lex_colloc_rel_group_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lex_colloc_rel_group_order_by_seq</code>
     */
    public static final Sequence<Long> LEX_COLLOC_REL_GROUP_ORDER_BY_SEQ = Internal.createSequence("lex_colloc_rel_group_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lex_rel_type_order_by_seq</code>
     */
    public static final Sequence<Long> LEX_REL_TYPE_ORDER_BY_SEQ = Internal.createSequence("lex_rel_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lex_relation_id_seq</code>
     */
    public static final Sequence<Long> LEX_RELATION_ID_SEQ = Internal.createSequence("lex_relation_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lex_relation_order_by_seq</code>
     */
    public static final Sequence<Long> LEX_RELATION_ORDER_BY_SEQ = Internal.createSequence("lex_relation_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_activity_log_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_ACTIVITY_LOG_ID_SEQ = Internal.createSequence("lexeme_activity_log_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_deriv_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_DERIV_ID_SEQ = Internal.createSequence("lexeme_deriv_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_deriv_order_by_seq</code>
     */
    public static final Sequence<Long> LEXEME_DERIV_ORDER_BY_SEQ = Internal.createSequence("lexeme_deriv_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_freeform_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_FREEFORM_ID_SEQ = Internal.createSequence("lexeme_freeform_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_freeform_order_by_seq</code>
     */
    public static final Sequence<Long> LEXEME_FREEFORM_ORDER_BY_SEQ = Internal.createSequence("lexeme_freeform_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_ID_SEQ = Internal.createSequence("lexeme_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_order_by_seq</code>
     */
    public static final Sequence<Long> LEXEME_ORDER_BY_SEQ = Internal.createSequence("lexeme_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_pos_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_POS_ID_SEQ = Internal.createSequence("lexeme_pos_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_pos_order_by_seq</code>
     */
    public static final Sequence<Long> LEXEME_POS_ORDER_BY_SEQ = Internal.createSequence("lexeme_pos_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_region_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_REGION_ID_SEQ = Internal.createSequence("lexeme_region_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_region_order_by_seq</code>
     */
    public static final Sequence<Long> LEXEME_REGION_ORDER_BY_SEQ = Internal.createSequence("lexeme_region_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_register_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_REGISTER_ID_SEQ = Internal.createSequence("lexeme_register_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_register_order_by_seq</code>
     */
    public static final Sequence<Long> LEXEME_REGISTER_ORDER_BY_SEQ = Internal.createSequence("lexeme_register_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_source_link_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_SOURCE_LINK_ID_SEQ = Internal.createSequence("lexeme_source_link_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_source_link_order_by_seq</code>
     */
    public static final Sequence<Long> LEXEME_SOURCE_LINK_ORDER_BY_SEQ = Internal.createSequence("lexeme_source_link_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.lexeme_tag_id_seq</code>
     */
    public static final Sequence<Long> LEXEME_TAG_ID_SEQ = Internal.createSequence("lexeme_tag_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_activity_log_id_seq</code>
     */
    public static final Sequence<Long> MEANING_ACTIVITY_LOG_ID_SEQ = Internal.createSequence("meaning_activity_log_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_domain_id_seq</code>
     */
    public static final Sequence<Long> MEANING_DOMAIN_ID_SEQ = Internal.createSequence("meaning_domain_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_domain_order_by_seq</code>
     */
    public static final Sequence<Long> MEANING_DOMAIN_ORDER_BY_SEQ = Internal.createSequence("meaning_domain_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_forum_id_seq</code>
     */
    public static final Sequence<Long> MEANING_FORUM_ID_SEQ = Internal.createSequence("meaning_forum_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_forum_order_by_seq</code>
     */
    public static final Sequence<Long> MEANING_FORUM_ORDER_BY_SEQ = Internal.createSequence("meaning_forum_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_freeform_id_seq</code>
     */
    public static final Sequence<Long> MEANING_FREEFORM_ID_SEQ = Internal.createSequence("meaning_freeform_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_id_seq</code>
     */
    public static final Sequence<Long> MEANING_ID_SEQ = Internal.createSequence("meaning_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_last_activity_log_id_seq</code>
     */
    public static final Sequence<Long> MEANING_LAST_ACTIVITY_LOG_ID_SEQ = Internal.createSequence("meaning_last_activity_log_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_nr_id_seq</code>
     */
    public static final Sequence<Long> MEANING_NR_ID_SEQ = Internal.createSequence("meaning_nr_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_rel_type_order_by_seq</code>
     */
    public static final Sequence<Long> MEANING_REL_TYPE_ORDER_BY_SEQ = Internal.createSequence("meaning_rel_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_relation_id_seq</code>
     */
    public static final Sequence<Long> MEANING_RELATION_ID_SEQ = Internal.createSequence("meaning_relation_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_relation_order_by_seq</code>
     */
    public static final Sequence<Long> MEANING_RELATION_ORDER_BY_SEQ = Internal.createSequence("meaning_relation_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_semantic_type_id_seq</code>
     */
    public static final Sequence<Long> MEANING_SEMANTIC_TYPE_ID_SEQ = Internal.createSequence("meaning_semantic_type_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_semantic_type_order_by_seq</code>
     */
    public static final Sequence<Long> MEANING_SEMANTIC_TYPE_ORDER_BY_SEQ = Internal.createSequence("meaning_semantic_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.meaning_tag_id_seq</code>
     */
    public static final Sequence<Long> MEANING_TAG_ID_SEQ = Internal.createSequence("meaning_tag_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.morph_freq_id_seq</code>
     */
    public static final Sequence<Long> MORPH_FREQ_ID_SEQ = Internal.createSequence("morph_freq_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.morph_order_by_seq</code>
     */
    public static final Sequence<Long> MORPH_ORDER_BY_SEQ = Internal.createSequence("morph_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.news_article_id_seq</code>
     */
    public static final Sequence<Long> NEWS_ARTICLE_ID_SEQ = Internal.createSequence("news_article_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.paradigm_form_id_seq</code>
     */
    public static final Sequence<Long> PARADIGM_FORM_ID_SEQ = Internal.createSequence("paradigm_form_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.paradigm_form_order_by_seq</code>
     */
    public static final Sequence<Long> PARADIGM_FORM_ORDER_BY_SEQ = Internal.createSequence("paradigm_form_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.paradigm_id_seq</code>
     */
    public static final Sequence<Long> PARADIGM_ID_SEQ = Internal.createSequence("paradigm_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.pos_group_order_by_seq</code>
     */
    public static final Sequence<Long> POS_GROUP_ORDER_BY_SEQ = Internal.createSequence("pos_group_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.pos_order_by_seq</code>
     */
    public static final Sequence<Long> POS_ORDER_BY_SEQ = Internal.createSequence("pos_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.proficiency_level_order_by_seq</code>
     */
    public static final Sequence<Long> PROFICIENCY_LEVEL_ORDER_BY_SEQ = Internal.createSequence("proficiency_level_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.region_order_by_seq</code>
     */
    public static final Sequence<Long> REGION_ORDER_BY_SEQ = Internal.createSequence("region_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.register_order_by_seq</code>
     */
    public static final Sequence<Long> REGISTER_ORDER_BY_SEQ = Internal.createSequence("register_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.rel_group_order_by_seq</code>
     */
    public static final Sequence<Long> REL_GROUP_ORDER_BY_SEQ = Internal.createSequence("rel_group_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.semantic_type_order_by_seq</code>
     */
    public static final Sequence<Long> SEMANTIC_TYPE_ORDER_BY_SEQ = Internal.createSequence("semantic_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.source_activity_log_id_seq</code>
     */
    public static final Sequence<Long> SOURCE_ACTIVITY_LOG_ID_SEQ = Internal.createSequence("source_activity_log_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.source_freeform_id_seq</code>
     */
    public static final Sequence<Long> SOURCE_FREEFORM_ID_SEQ = Internal.createSequence("source_freeform_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.source_id_seq</code>
     */
    public static final Sequence<Long> SOURCE_ID_SEQ = Internal.createSequence("source_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.tag_order_by_seq</code>
     */
    public static final Sequence<Long> TAG_ORDER_BY_SEQ = Internal.createSequence("tag_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.temp_ds_import_pk_map_id_seq</code>
     */
    public static final Sequence<Long> TEMP_DS_IMPORT_PK_MAP_ID_SEQ = Internal.createSequence("temp_ds_import_pk_map_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.temp_ds_import_queue_id_seq</code>
     */
    public static final Sequence<Long> TEMP_DS_IMPORT_QUEUE_ID_SEQ = Internal.createSequence("temp_ds_import_queue_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.terms_of_use_id_seq</code>
     */
    public static final Sequence<Long> TERMS_OF_USE_ID_SEQ = Internal.createSequence("terms_of_use_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.usage_type_order_by_seq</code>
     */
    public static final Sequence<Long> USAGE_TYPE_ORDER_BY_SEQ = Internal.createSequence("usage_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.value_state_order_by_seq</code>
     */
    public static final Sequence<Long> VALUE_STATE_ORDER_BY_SEQ = Internal.createSequence("value_state_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_activity_log_id_seq</code>
     */
    public static final Sequence<Long> WORD_ACTIVITY_LOG_ID_SEQ = Internal.createSequence("word_activity_log_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_etymology_id_seq</code>
     */
    public static final Sequence<Long> WORD_ETYMOLOGY_ID_SEQ = Internal.createSequence("word_etymology_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_etymology_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_ETYMOLOGY_ORDER_BY_SEQ = Internal.createSequence("word_etymology_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_etymology_relation_id_seq</code>
     */
    public static final Sequence<Long> WORD_ETYMOLOGY_RELATION_ID_SEQ = Internal.createSequence("word_etymology_relation_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_etymology_relation_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_ETYMOLOGY_RELATION_ORDER_BY_SEQ = Internal.createSequence("word_etymology_relation_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_etymology_source_link_id_seq</code>
     */
    public static final Sequence<Long> WORD_ETYMOLOGY_SOURCE_LINK_ID_SEQ = Internal.createSequence("word_etymology_source_link_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_etymology_source_link_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_ETYMOLOGY_SOURCE_LINK_ORDER_BY_SEQ = Internal.createSequence("word_etymology_source_link_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_forum_id_seq</code>
     */
    public static final Sequence<Long> WORD_FORUM_ID_SEQ = Internal.createSequence("word_forum_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_forum_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_FORUM_ORDER_BY_SEQ = Internal.createSequence("word_forum_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_freeform_id_seq</code>
     */
    public static final Sequence<Long> WORD_FREEFORM_ID_SEQ = Internal.createSequence("word_freeform_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_freeform_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_FREEFORM_ORDER_BY_SEQ = Internal.createSequence("word_freeform_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_freq_id_seq</code>
     */
    public static final Sequence<Long> WORD_FREQ_ID_SEQ = Internal.createSequence("word_freq_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_group_id_seq</code>
     */
    public static final Sequence<Long> WORD_GROUP_ID_SEQ = Internal.createSequence("word_group_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_group_member_id_seq</code>
     */
    public static final Sequence<Long> WORD_GROUP_MEMBER_ID_SEQ = Internal.createSequence("word_group_member_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_group_member_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_GROUP_MEMBER_ORDER_BY_SEQ = Internal.createSequence("word_group_member_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_guid_id_seq</code>
     */
    public static final Sequence<Long> WORD_GUID_ID_SEQ = Internal.createSequence("word_guid_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_id_seq</code>
     */
    public static final Sequence<Long> WORD_ID_SEQ = Internal.createSequence("word_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_last_activity_log_id_seq</code>
     */
    public static final Sequence<Long> WORD_LAST_ACTIVITY_LOG_ID_SEQ = Internal.createSequence("word_last_activity_log_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_rel_type_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_REL_TYPE_ORDER_BY_SEQ = Internal.createSequence("word_rel_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_relation_id_seq</code>
     */
    public static final Sequence<Long> WORD_RELATION_ID_SEQ = Internal.createSequence("word_relation_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_relation_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_RELATION_ORDER_BY_SEQ = Internal.createSequence("word_relation_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_relation_param_id_seq</code>
     */
    public static final Sequence<Long> WORD_RELATION_PARAM_ID_SEQ = Internal.createSequence("word_relation_param_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_type_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_TYPE_ORDER_BY_SEQ = Internal.createSequence("word_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_word_type_id_seq</code>
     */
    public static final Sequence<Long> WORD_WORD_TYPE_ID_SEQ = Internal.createSequence("word_word_type_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.word_word_type_order_by_seq</code>
     */
    public static final Sequence<Long> WORD_WORD_TYPE_ORDER_BY_SEQ = Internal.createSequence("word_word_type_order_by_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);
}
