and exists (
	select
		lp.id
	from
		lexeme_pos lp
	where
		lp.lexeme_id = l.id
		and lp.pos_code = :posCode
)

