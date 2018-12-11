select
    (select array_agg(wg.dataset_code) from word_guid wg where wg.word_id = w.id group by wg.word_id) guid_dataset_codes,
	w.*
from
	word w
where
	exists (
		select l.id
		from lexeme l
		where l.word_id = w.id
		and l.dataset_code = :dataset)
	and exists (
		select g.id
		from word_guid g
		where g.word_id = w.id
		and g.dataset_code = :dataset
		and g.guid = :guid)
	and exists (
		select f.id 
		from
			paradigm p,
			form f
		where p.word_id = w.id
		and f.paradigm_id = p.id
		and f.mode = 'WORD'
		and f.value = :word)
