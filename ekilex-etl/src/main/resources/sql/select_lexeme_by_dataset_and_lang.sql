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
{placeholder}
group by l.id
