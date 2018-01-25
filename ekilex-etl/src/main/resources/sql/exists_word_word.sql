and exists (
	select
		f1.id
	from
		paradigm p1,
		form f1
	where
		p1.word_id = w.id
		and f1.paradigm_id = p1.id
		and f1.value = :word
		and f1.is_word = true
)
