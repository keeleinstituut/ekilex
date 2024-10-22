package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.data.Word;

@Component
public class WordStressDbService implements GlobalConstant {

	private static final char APOSTROPHE = '\'';

	private static final String EKI_STRESS_MARKUP = "<eki-stress>";

	private static final String IGNORED_MARKUPS_REGEX = "<eki-foreign>|<eki-highlight>|<eki-sub>|<eki-sup>|<eki-meta>|<eki-link>|<eki-form>";

	private static final String RUS_VOCALS_REGEX = "[аеёиоуыэюяАЕЁИОУЫЭЮЯ]";

	@Autowired
	private DSLContext mainDb;

	public List<Word> getWordsWithMissingValuePreseStress() {

		// word value does not contain apostrophe
		Condition valueCond = WORD.VALUE.notLike("%" + APOSTROPHE + "%");

		// word value prese does not contain stress markup
		Condition valuePreseCond = WORD.VALUE_PRESE.notLike("%" + EKI_STRESS_MARKUP + "%");

		// morphophono form contains apostrophe (stress symbol) after vocal
		Condition vocalStressCond = WORD.MORPHOPHONO_FORM.likeRegex(RUS_VOCALS_REGEX + APOSTROPHE);

		// more than one vocal
		Condition multipleVocalsCond = WORD.MORPHOPHONO_FORM.likeRegex(RUS_VOCALS_REGEX + ".*" + RUS_VOCALS_REGEX);

		return mainDb
				.select(
						WORD.ID.as("word_id"),
						WORD.VALUE.as("word_value"),
						WORD.VALUE_PRESE.as("word_value_prese"),
						WORD.MORPHOPHONO_FORM.as("morphophono_form"),
						DSL.arrayAggDistinct(LEXEME.DATASET_CODE).as("dataset_codes"))
				.from(WORD, LEXEME)
				.where(
						WORD.LANG.eq(LANGUAGE_CODE_RUS)
								.and(WORD.IS_PUBLIC.isTrue())
								.and(valueCond)
								.and(valuePreseCond)
								.and(vocalStressCond)
								.and(multipleVocalsCond)
								.and(LEXEME.WORD_ID.eq(WORD.ID)))
				.groupBy(WORD.ID)
				.orderBy(WORD.VALUE)
				.fetchInto(Word.class);
	}

	public List<Word> getWordsWithMissingMorphophonoFormStress() {

		// word value does not contain apostrophe
		Condition valueCond = WORD.VALUE.notLike("%" + APOSTROPHE + "%");

		// word value prese contains stress markup
		Condition valuePreseMarkupCond = WORD.VALUE_PRESE.like("%" + EKI_STRESS_MARKUP + "%");

		// word value prese does not contain any other markup
		Condition valuePreseIgnoredMarkupCond = WORD.VALUE_PRESE.notLikeRegex(IGNORED_MARKUPS_REGEX);

		// morphophono form does not contain apostrophe (stress symbol)
		Condition vocalStressCond = WORD.MORPHOPHONO_FORM.notLike("%" + APOSTROPHE + "%");

		return mainDb
				.select(
						WORD.ID.as("word_id"),
						WORD.VALUE.as("word_value"),
						WORD.VALUE_PRESE.as("word_value_prese"),
						WORD.MORPHOPHONO_FORM.as("morphophono_form"),
						DSL.arrayAggDistinct(LEXEME.DATASET_CODE).as("dataset_codes"))
				.from(WORD, LEXEME)
				.where(
						WORD.LANG.eq(LANGUAGE_CODE_RUS)
								.and(WORD.IS_PUBLIC.isTrue())
								.and(valueCond)
								.and(valuePreseMarkupCond)
								.and(valuePreseIgnoredMarkupCond)
								.and(vocalStressCond)
								.and(LEXEME.WORD_ID.eq(WORD.ID)))
				.groupBy(WORD.ID)
				.orderBy(WORD.VALUE)
				.fetchInto(Word.class);
	}
}
