-- #1 --

delete
from
	word_suggestion
where
	id in (
		select
			ws.id
		from (
			select
				(array_agg(ws.id) order by ws.id)[2] id
			from
				word_suggestion ws
			group by
				ws.feedback_log_id 
		) ws
		where
			ws.id is not null
	)
;

delete
from
	feedback_log_comment
where
	id in (
		select
			flc.id
		from (
			select
				(array_agg(flc.id order by flc.id))[2] id
			from
				feedback_log fl,
				feedback_log_comment flc 
			where
				flc.feedback_log_id = fl.id
				and fl.feedback_type = 'WORD_SUGGESTION'
				and flc."comment" like 'Avaldatakse%'
			group by
				fl.id
		) flc
		where
			flc.id is not null
	)
;

