select
	osw.word_id,
	osw.value,
	osw.homonym_nr,
	osw.os_homonym_nr,
	osw.calc_os_homonym_nr
from
	(
	select
		w.id word_id,
		w.value,
		w.homonym_nr,
		wohn.homonym_nr os_homonym_nr,
		(row_number() over (partition by w.value order by w.value, w.homonym_nr)) calc_os_homonym_nr
	from
		word w
		left outer join word_os_homonym_nr wohn on wohn.word_id = w.id
	where
		w.is_public = true
		and w.lang = 'est'
		and exists (
			select
				1
			from
				lexeme l
			where
				l.word_id = w.id
				and l.is_public = true
				and l.is_word = true
				and l.dataset_code = 'eki'
				and exists (
					select
						1
					from
						publishing p
					where
						p.target_name = 'ww_os'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id
				)
		)
	order by
		w.value,
		w.homonym_nr,
		w.id
) osw
where
	(osw.os_homonym_nr is null)
	or (osw.os_homonym_nr != osw.calc_os_homonym_nr)
	-- or (osw.homonym_nr != osw.os_homonym_nr)
;