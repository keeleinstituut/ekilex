select form.value as value, array_agg(distinct word.id) as word_ids
from word, paradigm, form
where word.lang = :lang
	and paradigm.word_id = word.id
	and form.paradigm_id = paradigm.id
	and form.mode = 'WORD'
	and (exists (
		select lexeme.id
		from lexeme
		    left join dataset
		    	on lexeme.dataset_code = dataset.code
		where dataset.type = :datasetType
			and lexeme.word_id = word.id)
	    or
	    exists (
	    select lexeme.id
	    from lexeme
	    where lexeme.dataset_code = :includedDatasetCode
	        and lexeme.word_id = word.id))
	and not exists (
		select lexeme.id
		from lexeme
		where lexeme.dataset_code = :excludedDatasetCode
			and lexeme.word_id = word.id)
	and not exists (
		select word_word_type.word_type_code
		from word_word_type
		where word_word_type.word_id = word.id
			and word_word_type.word_type_code in (:excludedWordTypeCodes))
group by(form.value)