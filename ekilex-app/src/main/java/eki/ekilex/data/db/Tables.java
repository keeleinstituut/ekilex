/*
 * This file is generated by jOOQ.
*/
package eki.ekilex.data.db;


import eki.ekilex.data.db.tables.Collocation;
import eki.ekilex.data.db.tables.CollocationPosGroup;
import eki.ekilex.data.db.tables.CollocationRelGroup;
import eki.ekilex.data.db.tables.CollocationUsage;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionDataset;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.DefinitionRefLink;
import eki.ekilex.data.db.tables.Deriv;
import eki.ekilex.data.db.tables.DerivLabel;
import eki.ekilex.data.db.tables.DisplayMorph;
import eki.ekilex.data.db.tables.DisplayMorphLabel;
import eki.ekilex.data.db.tables.Domain;
import eki.ekilex.data.db.tables.DomainLabel;
import eki.ekilex.data.db.tables.EkiUser;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.FormRelType;
import eki.ekilex.data.db.tables.FormRelTypeLabel;
import eki.ekilex.data.db.tables.FormRelation;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformRefLink;
import eki.ekilex.data.db.tables.Gender;
import eki.ekilex.data.db.tables.GenderLabel;
import eki.ekilex.data.db.tables.GovernmentType;
import eki.ekilex.data.db.tables.GovernmentTypeLabel;
import eki.ekilex.data.db.tables.LabelType;
import eki.ekilex.data.db.tables.Lang;
import eki.ekilex.data.db.tables.LangLabel;
import eki.ekilex.data.db.tables.LexRelType;
import eki.ekilex.data.db.tables.LexRelTypeLabel;
import eki.ekilex.data.db.tables.LexRelation;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeDeriv;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemeFrequency;
import eki.ekilex.data.db.tables.LexemePos;
import eki.ekilex.data.db.tables.LexemeRegister;
import eki.ekilex.data.db.tables.LifecycleLog;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.MeaningRelType;
import eki.ekilex.data.db.tables.MeaningRelTypeLabel;
import eki.ekilex.data.db.tables.MeaningRelation;
import eki.ekilex.data.db.tables.MeaningType;
import eki.ekilex.data.db.tables.Morph;
import eki.ekilex.data.db.tables.MorphLabel;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Person;
import eki.ekilex.data.db.tables.Pos;
import eki.ekilex.data.db.tables.PosLabel;
import eki.ekilex.data.db.tables.ProcessState;
import eki.ekilex.data.db.tables.Register;
import eki.ekilex.data.db.tables.RegisterLabel;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.UsageType;
import eki.ekilex.data.db.tables.UsageTypeLabel;
import eki.ekilex.data.db.tables.ValueState;
import eki.ekilex.data.db.tables.ValueStateLabel;
import eki.ekilex.data.db.tables.ViewWwClassifier;
import eki.ekilex.data.db.tables.ViewWwDataset;
import eki.ekilex.data.db.tables.ViewWwForm;
import eki.ekilex.data.db.tables.ViewWwLexeme;
import eki.ekilex.data.db.tables.ViewWwMeaning;
import eki.ekilex.data.db.tables.ViewWwWord;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordGuid;
import eki.ekilex.data.db.tables.WordRelType;
import eki.ekilex.data.db.tables.WordRelTypeLabel;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordType;
import eki.ekilex.data.db.tables.WordTypeLabel;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in public
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>public.collocation</code>.
     */
    public static final Collocation COLLOCATION = eki.ekilex.data.db.tables.Collocation.COLLOCATION;

    /**
     * The table <code>public.collocation_pos_group</code>.
     */
    public static final CollocationPosGroup COLLOCATION_POS_GROUP = eki.ekilex.data.db.tables.CollocationPosGroup.COLLOCATION_POS_GROUP;

    /**
     * The table <code>public.collocation_rel_group</code>.
     */
    public static final CollocationRelGroup COLLOCATION_REL_GROUP = eki.ekilex.data.db.tables.CollocationRelGroup.COLLOCATION_REL_GROUP;

    /**
     * The table <code>public.collocation_usage</code>.
     */
    public static final CollocationUsage COLLOCATION_USAGE = eki.ekilex.data.db.tables.CollocationUsage.COLLOCATION_USAGE;

    /**
     * The table <code>public.dataset</code>.
     */
    public static final Dataset DATASET = eki.ekilex.data.db.tables.Dataset.DATASET;

    /**
     * The table <code>public.definition</code>.
     */
    public static final Definition DEFINITION = eki.ekilex.data.db.tables.Definition.DEFINITION;

    /**
     * The table <code>public.definition_dataset</code>.
     */
    public static final DefinitionDataset DEFINITION_DATASET = eki.ekilex.data.db.tables.DefinitionDataset.DEFINITION_DATASET;

    /**
     * The table <code>public.definition_freeform</code>.
     */
    public static final DefinitionFreeform DEFINITION_FREEFORM = eki.ekilex.data.db.tables.DefinitionFreeform.DEFINITION_FREEFORM;

    /**
     * The table <code>public.definition_ref_link</code>.
     */
    public static final DefinitionRefLink DEFINITION_REF_LINK = eki.ekilex.data.db.tables.DefinitionRefLink.DEFINITION_REF_LINK;

    /**
     * The table <code>public.deriv</code>.
     */
    public static final Deriv DERIV = eki.ekilex.data.db.tables.Deriv.DERIV;

    /**
     * The table <code>public.deriv_label</code>.
     */
    public static final DerivLabel DERIV_LABEL = eki.ekilex.data.db.tables.DerivLabel.DERIV_LABEL;

    /**
     * The table <code>public.display_morph</code>.
     */
    public static final DisplayMorph DISPLAY_MORPH = eki.ekilex.data.db.tables.DisplayMorph.DISPLAY_MORPH;

    /**
     * The table <code>public.display_morph_label</code>.
     */
    public static final DisplayMorphLabel DISPLAY_MORPH_LABEL = eki.ekilex.data.db.tables.DisplayMorphLabel.DISPLAY_MORPH_LABEL;

    /**
     * The table <code>public.domain</code>.
     */
    public static final Domain DOMAIN = eki.ekilex.data.db.tables.Domain.DOMAIN;

    /**
     * The table <code>public.domain_label</code>.
     */
    public static final DomainLabel DOMAIN_LABEL = eki.ekilex.data.db.tables.DomainLabel.DOMAIN_LABEL;

    /**
     * The table <code>public.eki_user</code>.
     */
    public static final EkiUser EKI_USER = eki.ekilex.data.db.tables.EkiUser.EKI_USER;

    /**
     * The table <code>public.form</code>.
     */
    public static final Form FORM = eki.ekilex.data.db.tables.Form.FORM;

    /**
     * The table <code>public.form_rel_type</code>.
     */
    public static final FormRelType FORM_REL_TYPE = eki.ekilex.data.db.tables.FormRelType.FORM_REL_TYPE;

    /**
     * The table <code>public.form_rel_type_label</code>.
     */
    public static final FormRelTypeLabel FORM_REL_TYPE_LABEL = eki.ekilex.data.db.tables.FormRelTypeLabel.FORM_REL_TYPE_LABEL;

    /**
     * The table <code>public.form_relation</code>.
     */
    public static final FormRelation FORM_RELATION = eki.ekilex.data.db.tables.FormRelation.FORM_RELATION;

    /**
     * The table <code>public.freeform</code>.
     */
    public static final Freeform FREEFORM = eki.ekilex.data.db.tables.Freeform.FREEFORM;

    /**
     * The table <code>public.freeform_ref_link</code>.
     */
    public static final FreeformRefLink FREEFORM_REF_LINK = eki.ekilex.data.db.tables.FreeformRefLink.FREEFORM_REF_LINK;

    /**
     * The table <code>public.gender</code>.
     */
    public static final Gender GENDER = eki.ekilex.data.db.tables.Gender.GENDER;

    /**
     * The table <code>public.gender_label</code>.
     */
    public static final GenderLabel GENDER_LABEL = eki.ekilex.data.db.tables.GenderLabel.GENDER_LABEL;

    /**
     * The table <code>public.government_type</code>.
     */
    public static final GovernmentType GOVERNMENT_TYPE = eki.ekilex.data.db.tables.GovernmentType.GOVERNMENT_TYPE;

    /**
     * The table <code>public.government_type_label</code>.
     */
    public static final GovernmentTypeLabel GOVERNMENT_TYPE_LABEL = eki.ekilex.data.db.tables.GovernmentTypeLabel.GOVERNMENT_TYPE_LABEL;

    /**
     * The table <code>public.label_type</code>.
     */
    public static final LabelType LABEL_TYPE = eki.ekilex.data.db.tables.LabelType.LABEL_TYPE;

    /**
     * The table <code>public.lang</code>.
     */
    public static final Lang LANG = eki.ekilex.data.db.tables.Lang.LANG;

    /**
     * The table <code>public.lang_label</code>.
     */
    public static final LangLabel LANG_LABEL = eki.ekilex.data.db.tables.LangLabel.LANG_LABEL;

    /**
     * The table <code>public.lex_rel_type</code>.
     */
    public static final LexRelType LEX_REL_TYPE = eki.ekilex.data.db.tables.LexRelType.LEX_REL_TYPE;

    /**
     * The table <code>public.lex_rel_type_label</code>.
     */
    public static final LexRelTypeLabel LEX_REL_TYPE_LABEL = eki.ekilex.data.db.tables.LexRelTypeLabel.LEX_REL_TYPE_LABEL;

    /**
     * The table <code>public.lex_relation</code>.
     */
    public static final LexRelation LEX_RELATION = eki.ekilex.data.db.tables.LexRelation.LEX_RELATION;

    /**
     * The table <code>public.lexeme</code>.
     */
    public static final Lexeme LEXEME = eki.ekilex.data.db.tables.Lexeme.LEXEME;

    /**
     * The table <code>public.lexeme_deriv</code>.
     */
    public static final LexemeDeriv LEXEME_DERIV = eki.ekilex.data.db.tables.LexemeDeriv.LEXEME_DERIV;

    /**
     * The table <code>public.lexeme_freeform</code>.
     */
    public static final LexemeFreeform LEXEME_FREEFORM = eki.ekilex.data.db.tables.LexemeFreeform.LEXEME_FREEFORM;

    /**
     * The table <code>public.lexeme_frequency</code>.
     */
    public static final LexemeFrequency LEXEME_FREQUENCY = eki.ekilex.data.db.tables.LexemeFrequency.LEXEME_FREQUENCY;

    /**
     * The table <code>public.lexeme_pos</code>.
     */
    public static final LexemePos LEXEME_POS = eki.ekilex.data.db.tables.LexemePos.LEXEME_POS;

    /**
     * The table <code>public.lexeme_register</code>.
     */
    public static final LexemeRegister LEXEME_REGISTER = eki.ekilex.data.db.tables.LexemeRegister.LEXEME_REGISTER;

    /**
     * The table <code>public.lifecycle_log</code>.
     */
    public static final LifecycleLog LIFECYCLE_LOG = eki.ekilex.data.db.tables.LifecycleLog.LIFECYCLE_LOG;

    /**
     * The table <code>public.meaning</code>.
     */
    public static final Meaning MEANING = eki.ekilex.data.db.tables.Meaning.MEANING;

    /**
     * The table <code>public.meaning_domain</code>.
     */
    public static final MeaningDomain MEANING_DOMAIN = eki.ekilex.data.db.tables.MeaningDomain.MEANING_DOMAIN;

    /**
     * The table <code>public.meaning_freeform</code>.
     */
    public static final MeaningFreeform MEANING_FREEFORM = eki.ekilex.data.db.tables.MeaningFreeform.MEANING_FREEFORM;

    /**
     * The table <code>public.meaning_rel_type</code>.
     */
    public static final MeaningRelType MEANING_REL_TYPE = eki.ekilex.data.db.tables.MeaningRelType.MEANING_REL_TYPE;

    /**
     * The table <code>public.meaning_rel_type_label</code>.
     */
    public static final MeaningRelTypeLabel MEANING_REL_TYPE_LABEL = eki.ekilex.data.db.tables.MeaningRelTypeLabel.MEANING_REL_TYPE_LABEL;

    /**
     * The table <code>public.meaning_relation</code>.
     */
    public static final MeaningRelation MEANING_RELATION = eki.ekilex.data.db.tables.MeaningRelation.MEANING_RELATION;

    /**
     * The table <code>public.meaning_type</code>.
     */
    public static final MeaningType MEANING_TYPE = eki.ekilex.data.db.tables.MeaningType.MEANING_TYPE;

    /**
     * The table <code>public.morph</code>.
     */
    public static final Morph MORPH = eki.ekilex.data.db.tables.Morph.MORPH;

    /**
     * The table <code>public.morph_label</code>.
     */
    public static final MorphLabel MORPH_LABEL = eki.ekilex.data.db.tables.MorphLabel.MORPH_LABEL;

    /**
     * The table <code>public.paradigm</code>.
     */
    public static final Paradigm PARADIGM = eki.ekilex.data.db.tables.Paradigm.PARADIGM;

    /**
     * The table <code>public.person</code>.
     */
    public static final Person PERSON = eki.ekilex.data.db.tables.Person.PERSON;

    /**
     * The table <code>public.pos</code>.
     */
    public static final Pos POS = eki.ekilex.data.db.tables.Pos.POS;

    /**
     * The table <code>public.pos_label</code>.
     */
    public static final PosLabel POS_LABEL = eki.ekilex.data.db.tables.PosLabel.POS_LABEL;

    /**
     * The table <code>public.process_state</code>.
     */
    public static final ProcessState PROCESS_STATE = eki.ekilex.data.db.tables.ProcessState.PROCESS_STATE;

    /**
     * The table <code>public.register</code>.
     */
    public static final Register REGISTER = eki.ekilex.data.db.tables.Register.REGISTER;

    /**
     * The table <code>public.register_label</code>.
     */
    public static final RegisterLabel REGISTER_LABEL = eki.ekilex.data.db.tables.RegisterLabel.REGISTER_LABEL;

    /**
     * The table <code>public.source</code>.
     */
    public static final Source SOURCE = eki.ekilex.data.db.tables.Source.SOURCE;

    /**
     * The table <code>public.source_freeform</code>.
     */
    public static final SourceFreeform SOURCE_FREEFORM = eki.ekilex.data.db.tables.SourceFreeform.SOURCE_FREEFORM;

    /**
     * The table <code>public.usage_type</code>.
     */
    public static final UsageType USAGE_TYPE = eki.ekilex.data.db.tables.UsageType.USAGE_TYPE;

    /**
     * The table <code>public.usage_type_label</code>.
     */
    public static final UsageTypeLabel USAGE_TYPE_LABEL = eki.ekilex.data.db.tables.UsageTypeLabel.USAGE_TYPE_LABEL;

    /**
     * The table <code>public.value_state</code>.
     */
    public static final ValueState VALUE_STATE = eki.ekilex.data.db.tables.ValueState.VALUE_STATE;

    /**
     * The table <code>public.value_state_label</code>.
     */
    public static final ValueStateLabel VALUE_STATE_LABEL = eki.ekilex.data.db.tables.ValueStateLabel.VALUE_STATE_LABEL;

    /**
     * The table <code>public.view_ww_classifier</code>.
     */
    public static final ViewWwClassifier VIEW_WW_CLASSIFIER = eki.ekilex.data.db.tables.ViewWwClassifier.VIEW_WW_CLASSIFIER;

    /**
     * The table <code>public.view_ww_dataset</code>.
     */
    public static final ViewWwDataset VIEW_WW_DATASET = eki.ekilex.data.db.tables.ViewWwDataset.VIEW_WW_DATASET;

    /**
     * The table <code>public.view_ww_form</code>.
     */
    public static final ViewWwForm VIEW_WW_FORM = eki.ekilex.data.db.tables.ViewWwForm.VIEW_WW_FORM;

    /**
     * The table <code>public.view_ww_lexeme</code>.
     */
    public static final ViewWwLexeme VIEW_WW_LEXEME = eki.ekilex.data.db.tables.ViewWwLexeme.VIEW_WW_LEXEME;

    /**
     * The table <code>public.view_ww_meaning</code>.
     */
    public static final ViewWwMeaning VIEW_WW_MEANING = eki.ekilex.data.db.tables.ViewWwMeaning.VIEW_WW_MEANING;

    /**
     * The table <code>public.view_ww_word</code>.
     */
    public static final ViewWwWord VIEW_WW_WORD = eki.ekilex.data.db.tables.ViewWwWord.VIEW_WW_WORD;

    /**
     * The table <code>public.word</code>.
     */
    public static final Word WORD = eki.ekilex.data.db.tables.Word.WORD;

    /**
     * The table <code>public.word_guid</code>.
     */
    public static final WordGuid WORD_GUID = eki.ekilex.data.db.tables.WordGuid.WORD_GUID;

    /**
     * The table <code>public.word_rel_type</code>.
     */
    public static final WordRelType WORD_REL_TYPE = eki.ekilex.data.db.tables.WordRelType.WORD_REL_TYPE;

    /**
     * The table <code>public.word_rel_type_label</code>.
     */
    public static final WordRelTypeLabel WORD_REL_TYPE_LABEL = eki.ekilex.data.db.tables.WordRelTypeLabel.WORD_REL_TYPE_LABEL;

    /**
     * The table <code>public.word_relation</code>.
     */
    public static final WordRelation WORD_RELATION = eki.ekilex.data.db.tables.WordRelation.WORD_RELATION;

    /**
     * The table <code>public.word_type</code>.
     */
    public static final WordType WORD_TYPE = eki.ekilex.data.db.tables.WordType.WORD_TYPE;

    /**
     * The table <code>public.word_type_label</code>.
     */
    public static final WordTypeLabel WORD_TYPE_LABEL = eki.ekilex.data.db.tables.WordTypeLabel.WORD_TYPE_LABEL;
}
