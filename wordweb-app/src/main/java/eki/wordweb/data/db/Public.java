/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db;


import eki.wordweb.data.db.tables.LexicalDecisionData;
import eki.wordweb.data.db.tables.LexicalDecisionResult;
import eki.wordweb.data.db.tables.MviewWwClassifier;
import eki.wordweb.data.db.tables.MviewWwCollocation;
import eki.wordweb.data.db.tables.MviewWwCounts;
import eki.wordweb.data.db.tables.MviewWwDataset;
import eki.wordweb.data.db.tables.MviewWwDatasetWordMenu;
import eki.wordweb.data.db.tables.MviewWwForm;
import eki.wordweb.data.db.tables.MviewWwLexeme;
import eki.wordweb.data.db.tables.MviewWwLexemeRelation;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwMeaningRelation;
import eki.wordweb.data.db.tables.MviewWwNewsArticle;
import eki.wordweb.data.db.tables.MviewWwWord;
import eki.wordweb.data.db.tables.MviewWwWordEtymology;
import eki.wordweb.data.db.tables.MviewWwWordRelation;
import eki.wordweb.data.db.tables.MviewWwWordSearch;
import eki.wordweb.data.db.tables.SimilarityJudgementData;
import eki.wordweb.data.db.tables.SimilarityJudgementResult;
import eki.wordweb.data.db.udt.TypeLangComplexity;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.UDT;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.lexical_decision_data</code>.
     */
    public final LexicalDecisionData LEXICAL_DECISION_DATA = LexicalDecisionData.LEXICAL_DECISION_DATA;

    /**
     * The table <code>public.lexical_decision_result</code>.
     */
    public final LexicalDecisionResult LEXICAL_DECISION_RESULT = LexicalDecisionResult.LEXICAL_DECISION_RESULT;

    /**
     * The table <code>public.mview_ww_classifier</code>.
     */
    public final MviewWwClassifier MVIEW_WW_CLASSIFIER = MviewWwClassifier.MVIEW_WW_CLASSIFIER;

    /**
     * The table <code>public.mview_ww_collocation</code>.
     */
    public final MviewWwCollocation MVIEW_WW_COLLOCATION = MviewWwCollocation.MVIEW_WW_COLLOCATION;

    /**
     * The table <code>public.mview_ww_counts</code>.
     */
    public final MviewWwCounts MVIEW_WW_COUNTS = MviewWwCounts.MVIEW_WW_COUNTS;

    /**
     * The table <code>public.mview_ww_dataset</code>.
     */
    public final MviewWwDataset MVIEW_WW_DATASET = MviewWwDataset.MVIEW_WW_DATASET;

    /**
     * The table <code>public.mview_ww_dataset_word_menu</code>.
     */
    public final MviewWwDatasetWordMenu MVIEW_WW_DATASET_WORD_MENU = MviewWwDatasetWordMenu.MVIEW_WW_DATASET_WORD_MENU;

    /**
     * The table <code>public.mview_ww_form</code>.
     */
    public final MviewWwForm MVIEW_WW_FORM = MviewWwForm.MVIEW_WW_FORM;

    /**
     * The table <code>public.mview_ww_lexeme</code>.
     */
    public final MviewWwLexeme MVIEW_WW_LEXEME = MviewWwLexeme.MVIEW_WW_LEXEME;

    /**
     * The table <code>public.mview_ww_lexeme_relation</code>.
     */
    public final MviewWwLexemeRelation MVIEW_WW_LEXEME_RELATION = MviewWwLexemeRelation.MVIEW_WW_LEXEME_RELATION;

    /**
     * The table <code>public.mview_ww_meaning</code>.
     */
    public final MviewWwMeaning MVIEW_WW_MEANING = MviewWwMeaning.MVIEW_WW_MEANING;

    /**
     * The table <code>public.mview_ww_meaning_relation</code>.
     */
    public final MviewWwMeaningRelation MVIEW_WW_MEANING_RELATION = MviewWwMeaningRelation.MVIEW_WW_MEANING_RELATION;

    /**
     * The table <code>public.mview_ww_news_article</code>.
     */
    public final MviewWwNewsArticle MVIEW_WW_NEWS_ARTICLE = MviewWwNewsArticle.MVIEW_WW_NEWS_ARTICLE;

    /**
     * The table <code>public.mview_ww_word</code>.
     */
    public final MviewWwWord MVIEW_WW_WORD = MviewWwWord.MVIEW_WW_WORD;

    /**
     * The table <code>public.mview_ww_word_etymology</code>.
     */
    public final MviewWwWordEtymology MVIEW_WW_WORD_ETYMOLOGY = MviewWwWordEtymology.MVIEW_WW_WORD_ETYMOLOGY;

    /**
     * The table <code>public.mview_ww_word_relation</code>.
     */
    public final MviewWwWordRelation MVIEW_WW_WORD_RELATION = MviewWwWordRelation.MVIEW_WW_WORD_RELATION;

    /**
     * The table <code>public.mview_ww_word_search</code>.
     */
    public final MviewWwWordSearch MVIEW_WW_WORD_SEARCH = MviewWwWordSearch.MVIEW_WW_WORD_SEARCH;

    /**
     * The table <code>public.similarity_judgement_data</code>.
     */
    public final SimilarityJudgementData SIMILARITY_JUDGEMENT_DATA = SimilarityJudgementData.SIMILARITY_JUDGEMENT_DATA;

    /**
     * The table <code>public.similarity_judgement_result</code>.
     */
    public final SimilarityJudgementResult SIMILARITY_JUDGEMENT_RESULT = SimilarityJudgementResult.SIMILARITY_JUDGEMENT_RESULT;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        return Arrays.<Sequence<?>>asList(
            Sequences.LEXICAL_DECISION_DATA_ID_SEQ,
            Sequences.LEXICAL_DECISION_RESULT_ID_SEQ,
            Sequences.SIMILARITY_JUDGEMENT_DATA_ID_SEQ,
            Sequences.SIMILARITY_JUDGEMENT_RESULT_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            LexicalDecisionData.LEXICAL_DECISION_DATA,
            LexicalDecisionResult.LEXICAL_DECISION_RESULT,
            MviewWwClassifier.MVIEW_WW_CLASSIFIER,
            MviewWwCollocation.MVIEW_WW_COLLOCATION,
            MviewWwCounts.MVIEW_WW_COUNTS,
            MviewWwDataset.MVIEW_WW_DATASET,
            MviewWwDatasetWordMenu.MVIEW_WW_DATASET_WORD_MENU,
            MviewWwForm.MVIEW_WW_FORM,
            MviewWwLexeme.MVIEW_WW_LEXEME,
            MviewWwLexemeRelation.MVIEW_WW_LEXEME_RELATION,
            MviewWwMeaning.MVIEW_WW_MEANING,
            MviewWwMeaningRelation.MVIEW_WW_MEANING_RELATION,
            MviewWwNewsArticle.MVIEW_WW_NEWS_ARTICLE,
            MviewWwWord.MVIEW_WW_WORD,
            MviewWwWordEtymology.MVIEW_WW_WORD_ETYMOLOGY,
            MviewWwWordRelation.MVIEW_WW_WORD_RELATION,
            MviewWwWordSearch.MVIEW_WW_WORD_SEARCH,
            SimilarityJudgementData.SIMILARITY_JUDGEMENT_DATA,
            SimilarityJudgementResult.SIMILARITY_JUDGEMENT_RESULT);
    }

    @Override
    public final List<UDT<?>> getUDTs() {
        return Arrays.<UDT<?>>asList(
            TypeLangComplexity.TYPE_LANG_COMPLEXITY);
    }
}
