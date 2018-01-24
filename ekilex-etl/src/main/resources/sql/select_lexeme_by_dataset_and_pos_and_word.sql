select
	l.id lexeme_id,
	array_agg(l.meaning_id) meaning_ids
from
	lexeme l,
	word w
where
	l.dataset_code = :dataset
	and l.word_id = w.id
	and w.lang = :lang
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
and exists (
	select
		lp.id
	from
		lexeme_pos lp
	where
		lp.lexeme_id = l.id
		and lp.pos_code = :posCode
)
group by l.id
