-- upgrade from ver 1.14.0 to 1.15.0
alter table eki_user_profile add column preferred_layer_name varchar(100) null;
alter table eki_user_profile rename column preferred_biling_candidate_langs to preferred_syn_candidate_langs;
alter table eki_user_profile rename column preferred_biling_lex_meaning_word_langs to preferred_syn_lex_meaning_word_langs;

-- prefiksoidide kustutamine 1:
delete
from lexeme l_rus using word w_rus
where l_rus.type = 'PRIMARY'
  and l_rus.dataset_code = 'sss'
  and l_rus.word_id = w_rus.id
  and w_rus.lang = 'rus'
  and exists(select l_est.id
             from lexeme l_est, word w_est
             where l_est.type = 'PRIMARY'
               and l_est.dataset_code = 'sss'
               and l_est.word_id = w_est.id
               and w_est.lang = 'est'
               and l_rus.meaning_id = l_est.meaning_id
               and exists(select wwt.id
                          from word_word_type wwt
                          where wwt.word_id = w_est.id
                            and wwt.word_type_code in ('sf', 'pf'))
               and not exists(select d.id
                              from definition d
                              where d.meaning_id = l_est.meaning_id
                                and d.complexity = 'DETAIL1'
                                and exists(select dd.definition_id
                                           from definition_dataset dd
                                           where dd.definition_id = d.id
                                             and dd.dataset_code = 'sss')));

-- prefiksoidide kustutamine 2:
delete
from lexeme l_est using word w_est
where l_est.type = 'PRIMARY'
  and l_est.dataset_code = 'sss'
  and l_est.word_id = w_est.id
  and w_est.lang = 'est'
  and exists(select wwt.id
             from word_word_type wwt
             where wwt.word_id = w_est.id
               and wwt.word_type_code in ('sf', 'pf'))
  and not exists(select d.id
                 from definition d
                 where d.meaning_id = l_est.meaning_id
                   and d.complexity = 'DETAIL1'
                   and exists(select dd.definition_id
                              from definition_dataset dd
                              where dd.definition_id = d.id
                                and dd.dataset_code = 'sss'));

-- mitteainsad evst tulevad tähendused mitteavalikuks:
update lexeme l
set process_state_code = 'läbi vaatamata'
from (select m.id
      from meaning m
      where exists(select l.id
                   from lexeme l, word w
                   where l.meaning_id = m.id
                     and l.type = 'PRIMARY'
                     and l.dataset_code = 'sss'
                     and l.word_id = w.id
                     and w.lang = 'est'
                     and exists(select l2.id
                                from lexeme l2
                                where l2.word_id = l.word_id
                                  and l2.id != l.id
                                  and l2.type = 'PRIMARY'
                                  and l2.dataset_code = 'sss')
                     and not exists(select l3.id
                                    from lexeme l3, word w3
                                    where l3.meaning_id = l.meaning_id
                                      and l3.id != l.id
                                      and l3.type = 'PRIMARY'
                                      and l3.dataset_code = 'sss'
                                      and l3.word_id = w3.id
                                      and w3.lang = 'est'))
        and exists(select l.id
                   from lexeme l, word w
                   where l.meaning_id = m.id
                     and l.type = 'PRIMARY'
                     and l.dataset_code = 'sss'
                     and l.word_id = w.id
                     and w.lang = 'rus')
        and not exists(select d.id
                       from definition d
                       where d.meaning_id = m.id
                         and d.complexity in ('SIMPLE1', 'DETAIL1', 'DETAIL')
                         and exists(select dd.definition_id
                                    from definition_dataset dd
                                    where dd.definition_id = d.id
                                      and dd.dataset_code = 'sss'))) as meaning_ids
where l.meaning_id = meaning_ids.id;