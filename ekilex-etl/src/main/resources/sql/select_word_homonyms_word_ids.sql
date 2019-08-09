select distinct word.id word_id
from form, paradigm, word
where form.value = :wordValue
	and form.mode = 'WORD'
	and paradigm.id = form.paradigm_id
	and word.id = paradigm.word_id
	and word.lang = :lang
	and exists (
		select lexeme.id
		from lexeme
		where lexeme.dataset_code = :datasetCode
			and lexeme.word_id = word.id)
	and not exists (
		select word_word_type.word_type_code
		from word_word_type
		where word_word_type.word_id = word.id
			and word_word_type.word_type_code in (:excludedWordTypeCodes))