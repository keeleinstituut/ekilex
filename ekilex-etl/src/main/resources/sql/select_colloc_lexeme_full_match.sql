select
  c.collocation_id,
  lc.lexeme_id,
  lc.rel_group_id
from
  (
    select
      c.collocation_id,
      c.colloc_lexeme_ids_str,
      (
        select
          array_to_string(array_agg(l.lexeme_ids_to_match order by l.lexeme_ids_to_match),',','*') lexeme_ids_to_match_str
        from
          (
            select
              unnest(array [:collocMemberLexemeIds]) lexeme_ids_to_match
          ) l
      ) lexeme_ids_to_match_str
    from
      (
        select
          c.id collocation_id,
          array_to_string(array_agg(lc1.lexeme_id order by lc1.lexeme_id),',','*') colloc_lexeme_ids_str
        from
          collocation c,
          lex_colloc  lc1
        where
          lc1.collocation_id = c.id
          and exists
          (
            select
              lc2.id
            from
              lex_colloc lc2
            where
              lc2.collocation_id = c.id
              and lc2.lexeme_id in (:collocMemberLexemeIds)
          )
        group by
          c.id
      ) c
  ) c,
  lex_colloc lc
where
  lc.collocation_id = c.collocation_id
  and c.colloc_lexeme_ids_str = c.lexeme_ids_to_match_str
