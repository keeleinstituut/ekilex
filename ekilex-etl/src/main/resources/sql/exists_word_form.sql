and exists (
	select
		f2.id
	from
		paradigm p2,
		form f2
	where
		p2.word_id = w.id
		and f2.paradigm_id = p2.id
		and f2.value = :form
)
