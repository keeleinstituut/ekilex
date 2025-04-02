/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main;


import eki.ekilex.data.db.main.tables.ActivityLog;
import eki.ekilex.data.db.main.tables.ApiErrorCount;
import eki.ekilex.data.db.main.tables.ApiRequestCount;
import eki.ekilex.data.db.main.tables.Aspect;
import eki.ekilex.data.db.main.tables.AspectLabel;
import eki.ekilex.data.db.main.tables.Collocation;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.DataRequest;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.DatasetFreeformType;
import eki.ekilex.data.db.main.tables.DatasetPermission;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.DefinitionFreeform;
import eki.ekilex.data.db.main.tables.DefinitionNote;
import eki.ekilex.data.db.main.tables.DefinitionNoteSourceLink;
import eki.ekilex.data.db.main.tables.DefinitionSourceLink;
import eki.ekilex.data.db.main.tables.DefinitionType;
import eki.ekilex.data.db.main.tables.DefinitionTypeLabel;
import eki.ekilex.data.db.main.tables.Deriv;
import eki.ekilex.data.db.main.tables.DerivLabel;
import eki.ekilex.data.db.main.tables.DisplayMorph;
import eki.ekilex.data.db.main.tables.DisplayMorphLabel;
import eki.ekilex.data.db.main.tables.Domain;
import eki.ekilex.data.db.main.tables.DomainLabel;
import eki.ekilex.data.db.main.tables.EkiUser;
import eki.ekilex.data.db.main.tables.EkiUserApplication;
import eki.ekilex.data.db.main.tables.EkiUserProfile;
import eki.ekilex.data.db.main.tables.EtymologyType;
import eki.ekilex.data.db.main.tables.FeedbackLog;
import eki.ekilex.data.db.main.tables.FeedbackLogComment;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.FormFreq;
import eki.ekilex.data.db.main.tables.Freeform;
import eki.ekilex.data.db.main.tables.FreeformSourceLink;
import eki.ekilex.data.db.main.tables.FreeformType;
import eki.ekilex.data.db.main.tables.FreeformTypeLabel;
import eki.ekilex.data.db.main.tables.FreqCorp;
import eki.ekilex.data.db.main.tables.GameNonword;
import eki.ekilex.data.db.main.tables.Gender;
import eki.ekilex.data.db.main.tables.GenderLabel;
import eki.ekilex.data.db.main.tables.GovernmentType;
import eki.ekilex.data.db.main.tables.GovernmentTypeLabel;
import eki.ekilex.data.db.main.tables.LabelType;
import eki.ekilex.data.db.main.tables.Language;
import eki.ekilex.data.db.main.tables.LanguageLabel;
import eki.ekilex.data.db.main.tables.LexColloc;
import eki.ekilex.data.db.main.tables.LexCollocPosGroup;
import eki.ekilex.data.db.main.tables.LexCollocRelGroup;
import eki.ekilex.data.db.main.tables.LexRelMapping;
import eki.ekilex.data.db.main.tables.LexRelType;
import eki.ekilex.data.db.main.tables.LexRelTypeLabel;
import eki.ekilex.data.db.main.tables.LexRelation;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeActivityLog;
import eki.ekilex.data.db.main.tables.LexemeDeriv;
import eki.ekilex.data.db.main.tables.LexemeFreeform;
import eki.ekilex.data.db.main.tables.LexemeNote;
import eki.ekilex.data.db.main.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.main.tables.LexemePos;
import eki.ekilex.data.db.main.tables.LexemeRegion;
import eki.ekilex.data.db.main.tables.LexemeRegister;
import eki.ekilex.data.db.main.tables.LexemeSourceLink;
import eki.ekilex.data.db.main.tables.LexemeTag;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.MeaningActivityLog;
import eki.ekilex.data.db.main.tables.MeaningDomain;
import eki.ekilex.data.db.main.tables.MeaningForum;
import eki.ekilex.data.db.main.tables.MeaningFreeform;
import eki.ekilex.data.db.main.tables.MeaningImage;
import eki.ekilex.data.db.main.tables.MeaningImageSourceLink;
import eki.ekilex.data.db.main.tables.MeaningLastActivityLog;
import eki.ekilex.data.db.main.tables.MeaningNote;
import eki.ekilex.data.db.main.tables.MeaningNoteSourceLink;
import eki.ekilex.data.db.main.tables.MeaningNr;
import eki.ekilex.data.db.main.tables.MeaningRelMapping;
import eki.ekilex.data.db.main.tables.MeaningRelType;
import eki.ekilex.data.db.main.tables.MeaningRelTypeLabel;
import eki.ekilex.data.db.main.tables.MeaningRelation;
import eki.ekilex.data.db.main.tables.MeaningSemanticType;
import eki.ekilex.data.db.main.tables.MeaningTag;
import eki.ekilex.data.db.main.tables.Morph;
import eki.ekilex.data.db.main.tables.MorphFreq;
import eki.ekilex.data.db.main.tables.MorphLabel;
import eki.ekilex.data.db.main.tables.NewsArticle;
import eki.ekilex.data.db.main.tables.Paradigm;
import eki.ekilex.data.db.main.tables.ParadigmForm;
import eki.ekilex.data.db.main.tables.Pos;
import eki.ekilex.data.db.main.tables.PosGroup;
import eki.ekilex.data.db.main.tables.PosGroupLabel;
import eki.ekilex.data.db.main.tables.PosLabel;
import eki.ekilex.data.db.main.tables.ProficiencyLevel;
import eki.ekilex.data.db.main.tables.ProficiencyLevelLabel;
import eki.ekilex.data.db.main.tables.Region;
import eki.ekilex.data.db.main.tables.Register;
import eki.ekilex.data.db.main.tables.RegisterLabel;
import eki.ekilex.data.db.main.tables.RelGroup;
import eki.ekilex.data.db.main.tables.RelGroupLabel;
import eki.ekilex.data.db.main.tables.SemanticType;
import eki.ekilex.data.db.main.tables.SemanticTypeLabel;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.SourceActivityLog;
import eki.ekilex.data.db.main.tables.SourceFreeform;
import eki.ekilex.data.db.main.tables.Tag;
import eki.ekilex.data.db.main.tables.TempDsImportPkMap;
import eki.ekilex.data.db.main.tables.TempDsImportQueue;
import eki.ekilex.data.db.main.tables.TermsOfUse;
import eki.ekilex.data.db.main.tables.Usage;
import eki.ekilex.data.db.main.tables.UsageDefinition;
import eki.ekilex.data.db.main.tables.UsageSourceLink;
import eki.ekilex.data.db.main.tables.UsageTranslation;
import eki.ekilex.data.db.main.tables.UsageType;
import eki.ekilex.data.db.main.tables.UsageTypeLabel;
import eki.ekilex.data.db.main.tables.ValueState;
import eki.ekilex.data.db.main.tables.ValueStateLabel;
import eki.ekilex.data.db.main.tables.ViewWwClassifier;
import eki.ekilex.data.db.main.tables.ViewWwCollocPosGroup;
import eki.ekilex.data.db.main.tables.ViewWwDataset;
import eki.ekilex.data.db.main.tables.ViewWwDatasetWordMenu;
import eki.ekilex.data.db.main.tables.ViewWwForm;
import eki.ekilex.data.db.main.tables.ViewWwLexeme;
import eki.ekilex.data.db.main.tables.ViewWwLexemeRelation;
import eki.ekilex.data.db.main.tables.ViewWwLexicalDecisionData;
import eki.ekilex.data.db.main.tables.ViewWwMeaning;
import eki.ekilex.data.db.main.tables.ViewWwMeaningRelation;
import eki.ekilex.data.db.main.tables.ViewWwNewsArticle;
import eki.ekilex.data.db.main.tables.ViewWwSimilarityJudgementData;
import eki.ekilex.data.db.main.tables.ViewWwWord;
import eki.ekilex.data.db.main.tables.ViewWwWordEtymology;
import eki.ekilex.data.db.main.tables.ViewWwWordRelation;
import eki.ekilex.data.db.main.tables.ViewWwWordSearch;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordActivityLog;
import eki.ekilex.data.db.main.tables.WordEtymology;
import eki.ekilex.data.db.main.tables.WordEtymologyRelation;
import eki.ekilex.data.db.main.tables.WordEtymologySourceLink;
import eki.ekilex.data.db.main.tables.WordForum;
import eki.ekilex.data.db.main.tables.WordFreeform;
import eki.ekilex.data.db.main.tables.WordFreq;
import eki.ekilex.data.db.main.tables.WordGroup;
import eki.ekilex.data.db.main.tables.WordGroupMember;
import eki.ekilex.data.db.main.tables.WordGuid;
import eki.ekilex.data.db.main.tables.WordLastActivityLog;
import eki.ekilex.data.db.main.tables.WordOdRecommendation;
import eki.ekilex.data.db.main.tables.WordRelMapping;
import eki.ekilex.data.db.main.tables.WordRelType;
import eki.ekilex.data.db.main.tables.WordRelTypeLabel;
import eki.ekilex.data.db.main.tables.WordRelation;
import eki.ekilex.data.db.main.tables.WordRelationParam;
import eki.ekilex.data.db.main.tables.WordTag;
import eki.ekilex.data.db.main.tables.WordType;
import eki.ekilex.data.db.main.tables.WordTypeLabel;
import eki.ekilex.data.db.main.tables.WordWordType;


/**
 * Convenience access to all tables in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>public.activity_log</code>.
     */
    public static final ActivityLog ACTIVITY_LOG = ActivityLog.ACTIVITY_LOG;

    /**
     * The table <code>public.api_error_count</code>.
     */
    public static final ApiErrorCount API_ERROR_COUNT = ApiErrorCount.API_ERROR_COUNT;

    /**
     * The table <code>public.api_request_count</code>.
     */
    public static final ApiRequestCount API_REQUEST_COUNT = ApiRequestCount.API_REQUEST_COUNT;

    /**
     * The table <code>public.aspect</code>.
     */
    public static final Aspect ASPECT = Aspect.ASPECT;

    /**
     * The table <code>public.aspect_label</code>.
     */
    public static final AspectLabel ASPECT_LABEL = AspectLabel.ASPECT_LABEL;

    /**
     * The table <code>public.collocation</code>.
     */
    public static final Collocation COLLOCATION = Collocation.COLLOCATION;

    /**
     * The table <code>public.collocation_member</code>.
     */
    public static final CollocationMember COLLOCATION_MEMBER = CollocationMember.COLLOCATION_MEMBER;

    /**
     * The table <code>public.data_request</code>.
     */
    public static final DataRequest DATA_REQUEST = DataRequest.DATA_REQUEST;

    /**
     * The table <code>public.dataset</code>.
     */
    public static final Dataset DATASET = Dataset.DATASET;

    /**
     * The table <code>public.dataset_freeform_type</code>.
     */
    public static final DatasetFreeformType DATASET_FREEFORM_TYPE = DatasetFreeformType.DATASET_FREEFORM_TYPE;

    /**
     * The table <code>public.dataset_permission</code>.
     */
    public static final DatasetPermission DATASET_PERMISSION = DatasetPermission.DATASET_PERMISSION;

    /**
     * The table <code>public.definition</code>.
     */
    public static final Definition DEFINITION = Definition.DEFINITION;

    /**
     * The table <code>public.definition_dataset</code>.
     */
    public static final DefinitionDataset DEFINITION_DATASET = DefinitionDataset.DEFINITION_DATASET;

    /**
     * The table <code>public.definition_freeform</code>.
     */
    public static final DefinitionFreeform DEFINITION_FREEFORM = DefinitionFreeform.DEFINITION_FREEFORM;

    /**
     * The table <code>public.definition_note</code>.
     */
    public static final DefinitionNote DEFINITION_NOTE = DefinitionNote.DEFINITION_NOTE;

    /**
     * The table <code>public.definition_note_source_link</code>.
     */
    public static final DefinitionNoteSourceLink DEFINITION_NOTE_SOURCE_LINK = DefinitionNoteSourceLink.DEFINITION_NOTE_SOURCE_LINK;

    /**
     * The table <code>public.definition_source_link</code>.
     */
    public static final DefinitionSourceLink DEFINITION_SOURCE_LINK = DefinitionSourceLink.DEFINITION_SOURCE_LINK;

    /**
     * The table <code>public.definition_type</code>.
     */
    public static final DefinitionType DEFINITION_TYPE = DefinitionType.DEFINITION_TYPE;

    /**
     * The table <code>public.definition_type_label</code>.
     */
    public static final DefinitionTypeLabel DEFINITION_TYPE_LABEL = DefinitionTypeLabel.DEFINITION_TYPE_LABEL;

    /**
     * The table <code>public.deriv</code>.
     */
    public static final Deriv DERIV = Deriv.DERIV;

    /**
     * The table <code>public.deriv_label</code>.
     */
    public static final DerivLabel DERIV_LABEL = DerivLabel.DERIV_LABEL;

    /**
     * The table <code>public.display_morph</code>.
     */
    public static final DisplayMorph DISPLAY_MORPH = DisplayMorph.DISPLAY_MORPH;

    /**
     * The table <code>public.display_morph_label</code>.
     */
    public static final DisplayMorphLabel DISPLAY_MORPH_LABEL = DisplayMorphLabel.DISPLAY_MORPH_LABEL;

    /**
     * The table <code>public.domain</code>.
     */
    public static final Domain DOMAIN = Domain.DOMAIN;

    /**
     * The table <code>public.domain_label</code>.
     */
    public static final DomainLabel DOMAIN_LABEL = DomainLabel.DOMAIN_LABEL;

    /**
     * The table <code>public.eki_user</code>.
     */
    public static final EkiUser EKI_USER = EkiUser.EKI_USER;

    /**
     * The table <code>public.eki_user_application</code>.
     */
    public static final EkiUserApplication EKI_USER_APPLICATION = EkiUserApplication.EKI_USER_APPLICATION;

    /**
     * The table <code>public.eki_user_profile</code>.
     */
    public static final EkiUserProfile EKI_USER_PROFILE = EkiUserProfile.EKI_USER_PROFILE;

    /**
     * The table <code>public.etymology_type</code>.
     */
    public static final EtymologyType ETYMOLOGY_TYPE = EtymologyType.ETYMOLOGY_TYPE;

    /**
     * The table <code>public.feedback_log</code>.
     */
    public static final FeedbackLog FEEDBACK_LOG = FeedbackLog.FEEDBACK_LOG;

    /**
     * The table <code>public.feedback_log_comment</code>.
     */
    public static final FeedbackLogComment FEEDBACK_LOG_COMMENT = FeedbackLogComment.FEEDBACK_LOG_COMMENT;

    /**
     * The table <code>public.form</code>.
     */
    public static final Form FORM = Form.FORM;

    /**
     * The table <code>public.form_freq</code>.
     */
    public static final FormFreq FORM_FREQ = FormFreq.FORM_FREQ;

    /**
     * The table <code>public.freeform</code>.
     */
    public static final Freeform FREEFORM = Freeform.FREEFORM;

    /**
     * The table <code>public.freeform_source_link</code>.
     */
    public static final FreeformSourceLink FREEFORM_SOURCE_LINK = FreeformSourceLink.FREEFORM_SOURCE_LINK;

    /**
     * The table <code>public.freeform_type</code>.
     */
    public static final FreeformType FREEFORM_TYPE = FreeformType.FREEFORM_TYPE;

    /**
     * The table <code>public.freeform_type_label</code>.
     */
    public static final FreeformTypeLabel FREEFORM_TYPE_LABEL = FreeformTypeLabel.FREEFORM_TYPE_LABEL;

    /**
     * The table <code>public.freq_corp</code>.
     */
    public static final FreqCorp FREQ_CORP = FreqCorp.FREQ_CORP;

    /**
     * The table <code>public.game_nonword</code>.
     */
    public static final GameNonword GAME_NONWORD = GameNonword.GAME_NONWORD;

    /**
     * The table <code>public.gender</code>.
     */
    public static final Gender GENDER = Gender.GENDER;

    /**
     * The table <code>public.gender_label</code>.
     */
    public static final GenderLabel GENDER_LABEL = GenderLabel.GENDER_LABEL;

    /**
     * The table <code>public.government_type</code>.
     */
    public static final GovernmentType GOVERNMENT_TYPE = GovernmentType.GOVERNMENT_TYPE;

    /**
     * The table <code>public.government_type_label</code>.
     */
    public static final GovernmentTypeLabel GOVERNMENT_TYPE_LABEL = GovernmentTypeLabel.GOVERNMENT_TYPE_LABEL;

    /**
     * The table <code>public.label_type</code>.
     */
    public static final LabelType LABEL_TYPE = LabelType.LABEL_TYPE;

    /**
     * The table <code>public.language</code>.
     */
    public static final Language LANGUAGE = Language.LANGUAGE;

    /**
     * The table <code>public.language_label</code>.
     */
    public static final LanguageLabel LANGUAGE_LABEL = LanguageLabel.LANGUAGE_LABEL;

    /**
     * The table <code>public.lex_colloc</code>.
     */
    public static final LexColloc LEX_COLLOC = LexColloc.LEX_COLLOC;

    /**
     * The table <code>public.lex_colloc_pos_group</code>.
     */
    public static final LexCollocPosGroup LEX_COLLOC_POS_GROUP = LexCollocPosGroup.LEX_COLLOC_POS_GROUP;

    /**
     * The table <code>public.lex_colloc_rel_group</code>.
     */
    public static final LexCollocRelGroup LEX_COLLOC_REL_GROUP = LexCollocRelGroup.LEX_COLLOC_REL_GROUP;

    /**
     * The table <code>public.lex_rel_mapping</code>.
     */
    public static final LexRelMapping LEX_REL_MAPPING = LexRelMapping.LEX_REL_MAPPING;

    /**
     * The table <code>public.lex_rel_type</code>.
     */
    public static final LexRelType LEX_REL_TYPE = LexRelType.LEX_REL_TYPE;

    /**
     * The table <code>public.lex_rel_type_label</code>.
     */
    public static final LexRelTypeLabel LEX_REL_TYPE_LABEL = LexRelTypeLabel.LEX_REL_TYPE_LABEL;

    /**
     * The table <code>public.lex_relation</code>.
     */
    public static final LexRelation LEX_RELATION = LexRelation.LEX_RELATION;

    /**
     * The table <code>public.lexeme</code>.
     */
    public static final Lexeme LEXEME = Lexeme.LEXEME;

    /**
     * The table <code>public.lexeme_activity_log</code>.
     */
    public static final LexemeActivityLog LEXEME_ACTIVITY_LOG = LexemeActivityLog.LEXEME_ACTIVITY_LOG;

    /**
     * The table <code>public.lexeme_deriv</code>.
     */
    public static final LexemeDeriv LEXEME_DERIV = LexemeDeriv.LEXEME_DERIV;

    /**
     * The table <code>public.lexeme_freeform</code>.
     */
    public static final LexemeFreeform LEXEME_FREEFORM = LexemeFreeform.LEXEME_FREEFORM;

    /**
     * The table <code>public.lexeme_note</code>.
     */
    public static final LexemeNote LEXEME_NOTE = LexemeNote.LEXEME_NOTE;

    /**
     * The table <code>public.lexeme_note_source_link</code>.
     */
    public static final LexemeNoteSourceLink LEXEME_NOTE_SOURCE_LINK = LexemeNoteSourceLink.LEXEME_NOTE_SOURCE_LINK;

    /**
     * The table <code>public.lexeme_pos</code>.
     */
    public static final LexemePos LEXEME_POS = LexemePos.LEXEME_POS;

    /**
     * The table <code>public.lexeme_region</code>.
     */
    public static final LexemeRegion LEXEME_REGION = LexemeRegion.LEXEME_REGION;

    /**
     * The table <code>public.lexeme_register</code>.
     */
    public static final LexemeRegister LEXEME_REGISTER = LexemeRegister.LEXEME_REGISTER;

    /**
     * The table <code>public.lexeme_source_link</code>.
     */
    public static final LexemeSourceLink LEXEME_SOURCE_LINK = LexemeSourceLink.LEXEME_SOURCE_LINK;

    /**
     * The table <code>public.lexeme_tag</code>.
     */
    public static final LexemeTag LEXEME_TAG = LexemeTag.LEXEME_TAG;

    /**
     * The table <code>public.meaning</code>.
     */
    public static final Meaning MEANING = Meaning.MEANING;

    /**
     * The table <code>public.meaning_activity_log</code>.
     */
    public static final MeaningActivityLog MEANING_ACTIVITY_LOG = MeaningActivityLog.MEANING_ACTIVITY_LOG;

    /**
     * The table <code>public.meaning_domain</code>.
     */
    public static final MeaningDomain MEANING_DOMAIN = MeaningDomain.MEANING_DOMAIN;

    /**
     * The table <code>public.meaning_forum</code>.
     */
    public static final MeaningForum MEANING_FORUM = MeaningForum.MEANING_FORUM;

    /**
     * The table <code>public.meaning_freeform</code>.
     */
    public static final MeaningFreeform MEANING_FREEFORM = MeaningFreeform.MEANING_FREEFORM;

    /**
     * The table <code>public.meaning_image</code>.
     */
    public static final MeaningImage MEANING_IMAGE = MeaningImage.MEANING_IMAGE;

    /**
     * The table <code>public.meaning_image_source_link</code>.
     */
    public static final MeaningImageSourceLink MEANING_IMAGE_SOURCE_LINK = MeaningImageSourceLink.MEANING_IMAGE_SOURCE_LINK;

    /**
     * The table <code>public.meaning_last_activity_log</code>.
     */
    public static final MeaningLastActivityLog MEANING_LAST_ACTIVITY_LOG = MeaningLastActivityLog.MEANING_LAST_ACTIVITY_LOG;

    /**
     * The table <code>public.meaning_note</code>.
     */
    public static final MeaningNote MEANING_NOTE = MeaningNote.MEANING_NOTE;

    /**
     * The table <code>public.meaning_note_source_link</code>.
     */
    public static final MeaningNoteSourceLink MEANING_NOTE_SOURCE_LINK = MeaningNoteSourceLink.MEANING_NOTE_SOURCE_LINK;

    /**
     * The table <code>public.meaning_nr</code>.
     */
    public static final MeaningNr MEANING_NR = MeaningNr.MEANING_NR;

    /**
     * The table <code>public.meaning_rel_mapping</code>.
     */
    public static final MeaningRelMapping MEANING_REL_MAPPING = MeaningRelMapping.MEANING_REL_MAPPING;

    /**
     * The table <code>public.meaning_rel_type</code>.
     */
    public static final MeaningRelType MEANING_REL_TYPE = MeaningRelType.MEANING_REL_TYPE;

    /**
     * The table <code>public.meaning_rel_type_label</code>.
     */
    public static final MeaningRelTypeLabel MEANING_REL_TYPE_LABEL = MeaningRelTypeLabel.MEANING_REL_TYPE_LABEL;

    /**
     * The table <code>public.meaning_relation</code>.
     */
    public static final MeaningRelation MEANING_RELATION = MeaningRelation.MEANING_RELATION;

    /**
     * The table <code>public.meaning_semantic_type</code>.
     */
    public static final MeaningSemanticType MEANING_SEMANTIC_TYPE = MeaningSemanticType.MEANING_SEMANTIC_TYPE;

    /**
     * The table <code>public.meaning_tag</code>.
     */
    public static final MeaningTag MEANING_TAG = MeaningTag.MEANING_TAG;

    /**
     * The table <code>public.morph</code>.
     */
    public static final Morph MORPH = Morph.MORPH;

    /**
     * The table <code>public.morph_freq</code>.
     */
    public static final MorphFreq MORPH_FREQ = MorphFreq.MORPH_FREQ;

    /**
     * The table <code>public.morph_label</code>.
     */
    public static final MorphLabel MORPH_LABEL = MorphLabel.MORPH_LABEL;

    /**
     * The table <code>public.news_article</code>.
     */
    public static final NewsArticle NEWS_ARTICLE = NewsArticle.NEWS_ARTICLE;

    /**
     * The table <code>public.paradigm</code>.
     */
    public static final Paradigm PARADIGM = Paradigm.PARADIGM;

    /**
     * The table <code>public.paradigm_form</code>.
     */
    public static final ParadigmForm PARADIGM_FORM = ParadigmForm.PARADIGM_FORM;

    /**
     * The table <code>public.pos</code>.
     */
    public static final Pos POS = Pos.POS;

    /**
     * The table <code>public.pos_group</code>.
     */
    public static final PosGroup POS_GROUP = PosGroup.POS_GROUP;

    /**
     * The table <code>public.pos_group_label</code>.
     */
    public static final PosGroupLabel POS_GROUP_LABEL = PosGroupLabel.POS_GROUP_LABEL;

    /**
     * The table <code>public.pos_label</code>.
     */
    public static final PosLabel POS_LABEL = PosLabel.POS_LABEL;

    /**
     * The table <code>public.proficiency_level</code>.
     */
    public static final ProficiencyLevel PROFICIENCY_LEVEL = ProficiencyLevel.PROFICIENCY_LEVEL;

    /**
     * The table <code>public.proficiency_level_label</code>.
     */
    public static final ProficiencyLevelLabel PROFICIENCY_LEVEL_LABEL = ProficiencyLevelLabel.PROFICIENCY_LEVEL_LABEL;

    /**
     * The table <code>public.region</code>.
     */
    public static final Region REGION = Region.REGION;

    /**
     * The table <code>public.register</code>.
     */
    public static final Register REGISTER = Register.REGISTER;

    /**
     * The table <code>public.register_label</code>.
     */
    public static final RegisterLabel REGISTER_LABEL = RegisterLabel.REGISTER_LABEL;

    /**
     * The table <code>public.rel_group</code>.
     */
    public static final RelGroup REL_GROUP = RelGroup.REL_GROUP;

    /**
     * The table <code>public.rel_group_label</code>.
     */
    public static final RelGroupLabel REL_GROUP_LABEL = RelGroupLabel.REL_GROUP_LABEL;

    /**
     * The table <code>public.semantic_type</code>.
     */
    public static final SemanticType SEMANTIC_TYPE = SemanticType.SEMANTIC_TYPE;

    /**
     * The table <code>public.semantic_type_label</code>.
     */
    public static final SemanticTypeLabel SEMANTIC_TYPE_LABEL = SemanticTypeLabel.SEMANTIC_TYPE_LABEL;

    /**
     * The table <code>public.source</code>.
     */
    public static final Source SOURCE = Source.SOURCE;

    /**
     * The table <code>public.source_activity_log</code>.
     */
    public static final SourceActivityLog SOURCE_ACTIVITY_LOG = SourceActivityLog.SOURCE_ACTIVITY_LOG;

    /**
     * The table <code>public.source_freeform</code>.
     */
    public static final SourceFreeform SOURCE_FREEFORM = SourceFreeform.SOURCE_FREEFORM;

    /**
     * The table <code>public.tag</code>.
     */
    public static final Tag TAG = Tag.TAG;

    /**
     * The table <code>public.temp_ds_import_pk_map</code>.
     */
    public static final TempDsImportPkMap TEMP_DS_IMPORT_PK_MAP = TempDsImportPkMap.TEMP_DS_IMPORT_PK_MAP;

    /**
     * The table <code>public.temp_ds_import_queue</code>.
     */
    public static final TempDsImportQueue TEMP_DS_IMPORT_QUEUE = TempDsImportQueue.TEMP_DS_IMPORT_QUEUE;

    /**
     * The table <code>public.terms_of_use</code>.
     */
    public static final TermsOfUse TERMS_OF_USE = TermsOfUse.TERMS_OF_USE;

    /**
     * The table <code>public.usage</code>.
     */
    public static final Usage USAGE = Usage.USAGE;

    /**
     * The table <code>public.usage_definition</code>.
     */
    public static final UsageDefinition USAGE_DEFINITION = UsageDefinition.USAGE_DEFINITION;

    /**
     * The table <code>public.usage_source_link</code>.
     */
    public static final UsageSourceLink USAGE_SOURCE_LINK = UsageSourceLink.USAGE_SOURCE_LINK;

    /**
     * The table <code>public.usage_translation</code>.
     */
    public static final UsageTranslation USAGE_TRANSLATION = UsageTranslation.USAGE_TRANSLATION;

    /**
     * The table <code>public.usage_type</code>.
     */
    public static final UsageType USAGE_TYPE = UsageType.USAGE_TYPE;

    /**
     * The table <code>public.usage_type_label</code>.
     */
    public static final UsageTypeLabel USAGE_TYPE_LABEL = UsageTypeLabel.USAGE_TYPE_LABEL;

    /**
     * The table <code>public.value_state</code>.
     */
    public static final ValueState VALUE_STATE = ValueState.VALUE_STATE;

    /**
     * The table <code>public.value_state_label</code>.
     */
    public static final ValueStateLabel VALUE_STATE_LABEL = ValueStateLabel.VALUE_STATE_LABEL;

    /**
     * The table <code>public.view_ww_classifier</code>.
     */
    public static final ViewWwClassifier VIEW_WW_CLASSIFIER = ViewWwClassifier.VIEW_WW_CLASSIFIER;

    /**
     * The table <code>public.view_ww_colloc_pos_group</code>.
     */
    public static final ViewWwCollocPosGroup VIEW_WW_COLLOC_POS_GROUP = ViewWwCollocPosGroup.VIEW_WW_COLLOC_POS_GROUP;

    /**
     * The table <code>public.view_ww_dataset</code>.
     */
    public static final ViewWwDataset VIEW_WW_DATASET = ViewWwDataset.VIEW_WW_DATASET;

    /**
     * The table <code>public.view_ww_dataset_word_menu</code>.
     */
    public static final ViewWwDatasetWordMenu VIEW_WW_DATASET_WORD_MENU = ViewWwDatasetWordMenu.VIEW_WW_DATASET_WORD_MENU;

    /**
     * The table <code>public.view_ww_form</code>.
     */
    public static final ViewWwForm VIEW_WW_FORM = ViewWwForm.VIEW_WW_FORM;

    /**
     * The table <code>public.view_ww_lexeme</code>.
     */
    public static final ViewWwLexeme VIEW_WW_LEXEME = ViewWwLexeme.VIEW_WW_LEXEME;

    /**
     * The table <code>public.view_ww_lexeme_relation</code>.
     */
    public static final ViewWwLexemeRelation VIEW_WW_LEXEME_RELATION = ViewWwLexemeRelation.VIEW_WW_LEXEME_RELATION;

    /**
     * The table <code>public.view_ww_lexical_decision_data</code>.
     */
    public static final ViewWwLexicalDecisionData VIEW_WW_LEXICAL_DECISION_DATA = ViewWwLexicalDecisionData.VIEW_WW_LEXICAL_DECISION_DATA;

    /**
     * The table <code>public.view_ww_meaning</code>.
     */
    public static final ViewWwMeaning VIEW_WW_MEANING = ViewWwMeaning.VIEW_WW_MEANING;

    /**
     * The table <code>public.view_ww_meaning_relation</code>.
     */
    public static final ViewWwMeaningRelation VIEW_WW_MEANING_RELATION = ViewWwMeaningRelation.VIEW_WW_MEANING_RELATION;

    /**
     * The table <code>public.view_ww_news_article</code>.
     */
    public static final ViewWwNewsArticle VIEW_WW_NEWS_ARTICLE = ViewWwNewsArticle.VIEW_WW_NEWS_ARTICLE;

    /**
     * The table <code>public.view_ww_similarity_judgement_data</code>.
     */
    public static final ViewWwSimilarityJudgementData VIEW_WW_SIMILARITY_JUDGEMENT_DATA = ViewWwSimilarityJudgementData.VIEW_WW_SIMILARITY_JUDGEMENT_DATA;

    /**
     * The table <code>public.view_ww_word</code>.
     */
    public static final ViewWwWord VIEW_WW_WORD = ViewWwWord.VIEW_WW_WORD;

    /**
     * The table <code>public.view_ww_word_etymology</code>.
     */
    public static final ViewWwWordEtymology VIEW_WW_WORD_ETYMOLOGY = ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY;

    /**
     * The table <code>public.view_ww_word_relation</code>.
     */
    public static final ViewWwWordRelation VIEW_WW_WORD_RELATION = ViewWwWordRelation.VIEW_WW_WORD_RELATION;

    /**
     * The table <code>public.view_ww_word_search</code>.
     */
    public static final ViewWwWordSearch VIEW_WW_WORD_SEARCH = ViewWwWordSearch.VIEW_WW_WORD_SEARCH;

    /**
     * The table <code>public.word</code>.
     */
    public static final Word WORD = Word.WORD;

    /**
     * The table <code>public.word_activity_log</code>.
     */
    public static final WordActivityLog WORD_ACTIVITY_LOG = WordActivityLog.WORD_ACTIVITY_LOG;

    /**
     * The table <code>public.word_etymology</code>.
     */
    public static final WordEtymology WORD_ETYMOLOGY = WordEtymology.WORD_ETYMOLOGY;

    /**
     * The table <code>public.word_etymology_relation</code>.
     */
    public static final WordEtymologyRelation WORD_ETYMOLOGY_RELATION = WordEtymologyRelation.WORD_ETYMOLOGY_RELATION;

    /**
     * The table <code>public.word_etymology_source_link</code>.
     */
    public static final WordEtymologySourceLink WORD_ETYMOLOGY_SOURCE_LINK = WordEtymologySourceLink.WORD_ETYMOLOGY_SOURCE_LINK;

    /**
     * The table <code>public.word_forum</code>.
     */
    public static final WordForum WORD_FORUM = WordForum.WORD_FORUM;

    /**
     * The table <code>public.word_freeform</code>.
     */
    public static final WordFreeform WORD_FREEFORM = WordFreeform.WORD_FREEFORM;

    /**
     * The table <code>public.word_freq</code>.
     */
    public static final WordFreq WORD_FREQ = WordFreq.WORD_FREQ;

    /**
     * The table <code>public.word_group</code>.
     */
    public static final WordGroup WORD_GROUP = WordGroup.WORD_GROUP;

    /**
     * The table <code>public.word_group_member</code>.
     */
    public static final WordGroupMember WORD_GROUP_MEMBER = WordGroupMember.WORD_GROUP_MEMBER;

    /**
     * The table <code>public.word_guid</code>.
     */
    public static final WordGuid WORD_GUID = WordGuid.WORD_GUID;

    /**
     * The table <code>public.word_last_activity_log</code>.
     */
    public static final WordLastActivityLog WORD_LAST_ACTIVITY_LOG = WordLastActivityLog.WORD_LAST_ACTIVITY_LOG;

    /**
     * The table <code>public.word_od_recommendation</code>.
     */
    public static final WordOdRecommendation WORD_OD_RECOMMENDATION = WordOdRecommendation.WORD_OD_RECOMMENDATION;

    /**
     * The table <code>public.word_rel_mapping</code>.
     */
    public static final WordRelMapping WORD_REL_MAPPING = WordRelMapping.WORD_REL_MAPPING;

    /**
     * The table <code>public.word_rel_type</code>.
     */
    public static final WordRelType WORD_REL_TYPE = WordRelType.WORD_REL_TYPE;

    /**
     * The table <code>public.word_rel_type_label</code>.
     */
    public static final WordRelTypeLabel WORD_REL_TYPE_LABEL = WordRelTypeLabel.WORD_REL_TYPE_LABEL;

    /**
     * The table <code>public.word_relation</code>.
     */
    public static final WordRelation WORD_RELATION = WordRelation.WORD_RELATION;

    /**
     * The table <code>public.word_relation_param</code>.
     */
    public static final WordRelationParam WORD_RELATION_PARAM = WordRelationParam.WORD_RELATION_PARAM;

    /**
     * The table <code>public.word_tag</code>.
     */
    public static final WordTag WORD_TAG = WordTag.WORD_TAG;

    /**
     * The table <code>public.word_type</code>.
     */
    public static final WordType WORD_TYPE = WordType.WORD_TYPE;

    /**
     * The table <code>public.word_type_label</code>.
     */
    public static final WordTypeLabel WORD_TYPE_LABEL = WordTypeLabel.WORD_TYPE_LABEL;

    /**
     * The table <code>public.word_word_type</code>.
     */
    public static final WordWordType WORD_WORD_TYPE = WordWordType.WORD_WORD_TYPE;
}
