------------------------------------------------
--------- tühjade ilmikute kustutamine  --------
------------------------------------------------

delete
from lexeme
where id in (select l.id
             from lexeme l,
                  meaning m
             where l.is_public = true
             and   l.dataset_code != 'ety'
             and   l.meaning_id = m.id
             and   not exists (select lf.id from lexeme_freeform lf where lf.lexeme_id = l.id)
             and   not exists (select cl.id from lexeme_deriv cl where cl.lexeme_id = l.id)
             and   not exists (select cl.id from lexeme_pos cl where cl.lexeme_id = l.id)
             and   not exists (select cl.id from lexeme_region cl where cl.lexeme_id = l.id)
             and   not exists (select cl.id from lexeme_register cl where cl.lexeme_id = l.id)
             and   not exists (select lsl.id
                               from lexeme_source_link lsl
                               where lsl.lexeme_id = l.id)
             and   not exists (select lr.id from lex_relation lr where lr.lexeme1_id = l.id)
             and   not exists (select lr.id from lex_relation lr where lr.lexeme2_id = l.id)
             and   not exists (select l2.id
                               from lexeme l2
                               where l2.meaning_id = m.id
                               and   l2.id != l.id)
             and   not exists (select lt.id from lexeme_tag lt where lt.lexeme_id = l.id)
             and   not exists (select lc.id from lex_colloc lc where lc.lexeme_id = l.id)
             and   not exists (select mf.id
                               from meaning_freeform mf
                               where mf.meaning_id = m.id)
             and   not exists (select mst.id
                               from meaning_semantic_type mst
                               where mst.meaning_id = m.id)
             and   not exists (select mr.id
                               from meaning_relation mr
                               where mr.meaning1_id = m.id)
             and   not exists (select d.id from definition d where d.meaning_id = m.id)
             and   not exists (select md.meaning_id
                               from meaning_domain md
                               where md.meaning_id = m.id));

------------------------------------------------
-------- homonüümide liitmine sss külge --------
------------------------------------------------

create or replace function merge_homonyms_to_sss(included_langs char(3) array)
  returns void
  language plpgsql
as $$
declare
  w_m_row record;
  w_m_cnt bigint;
  any_rec_cnt bigint;
  log_rec_cnt bigint;
  lex_rec_cnt bigint;
  tmp_cnt bigint;
begin
  w_m_cnt := 0;
  any_rec_cnt := 0;
  log_rec_cnt := 0;
  lex_rec_cnt := 0;
  for w_m_row in
    (select w_xxx.word_id src_word_id,
            w_xxx.lexeme_id src_lexeme_id,
            w_sss.word_id tgt_word_id
    from (select w.id word_id,
                 w.value word,
                 w.lang,
                 l.id lexeme_id,
                 (exists (select wt.id
                          from word_word_type wt
                          where wt.word_id = w.id
                          and   wt.word_type_code = 'pf')) is_pf,
                 (exists (select wt.id
                          from word_word_type wt
                          where wt.word_id = w.id
                          and   wt.word_type_code = 'sf')) is_sf,
                 (exists (select wt.id
                          from word_word_type wt
                          where wt.word_id = w.id
                          and   wt.word_type_code = 'th')) is_th,
                 (exists (select wt.id
                          from word_word_type wt
                          where wt.word_id = w.id
                          and   wt.word_type_code = 'l')) is_l
          from word w,
               lexeme l
          where w.lang = any(included_langs)
          and   l.word_id = w.id
          and   l.dataset_code not in ('sss', 'ety')
          and   not exists (select l2.id
                            from lexeme l2
                            where l2.word_id = w.id
                            and   l2.dataset_code = 'sss')) w_xxx,
         (select w1.id word_id,
                 w1.value word,
                 w1.lang,
                 (exists (select wt.id
                          from word_word_type wt
                          where wt.word_id = w1.id
                          and   wt.word_type_code = 'pf')) is_pf,
                 (exists (select wt.id
                          from word_word_type wt
                          where wt.word_id = w1.id
                          and   wt.word_type_code = 'sf')) is_sf,
                 (exists (select wt.id
                          from word_word_type wt
                          where wt.word_id = w1.id
                          and   wt.word_type_code = 'th')) is_th,
                 (exists (select wt.id
                          from word_word_type wt
                          where wt.word_id = w1.id
                          and   wt.word_type_code = 'l')) is_l
          from word w1
          where w1.lang = any(included_langs)
          and   exists (select l.id
                        from lexeme l
                        where l.word_id = w1.id
                        and   l.dataset_code = 'sss'
                        and   l.is_public = true)
          and   not exists (select w2.id
                            from word w2
                            where w2.value = w1.value
                            and   w2.lang = w1.lang
                            and   w2.id != w1.id
                            and   exists (select l.id
                                          from lexeme l
                                          where l.word_id = w2.id
                                          and   l.dataset_code = 'sss'
                                          and   l.is_public = true))) w_sss
    where w_xxx.word = w_sss.word
    and   w_xxx.lang = w_sss.lang
    and   w_xxx.word_id != w_sss.word_id
    and   w_xxx.is_pf = w_sss.is_pf
    and   w_xxx.is_sf = w_sss.is_sf
    and   w_xxx.is_th = w_sss.is_th
    and   w_xxx.is_l = w_sss.is_l
    order by w_xxx.word_id, w_xxx.lexeme_id, w_sss.word_id)
  loop
    -- any rels
    update word_relation r1
       set word1_id = w_m_row.tgt_word_id
    where r1.word1_id = w_m_row.src_word_id
    and   not exists (select r2.id
                      from word_relation r2
                      where r2.word1_id = w_m_row.tgt_word_id
                      and   r2.word2_id = r1.word2_id
                      and   r2.word_rel_type_code = r1.word_rel_type_code);
    get diagnostics tmp_cnt = row_count;
    any_rec_cnt := any_rec_cnt + tmp_cnt;
    update word_relation r1
       set word2_id = w_m_row.tgt_word_id
    where r1.word2_id = w_m_row.src_word_id
    and   not exists (select r2.id
                      from word_relation r2
                      where r2.word2_id = w_m_row.tgt_word_id
                      and   r2.word1_id = r1.word1_id
                      and   r2.word_rel_type_code = r1.word_rel_type_code);
    get diagnostics tmp_cnt = row_count;
    any_rec_cnt := any_rec_cnt + tmp_cnt;
    update word_etymology we set word_id = w_m_row.tgt_word_id where we.word_id = w_m_row.src_word_id;
    get diagnostics tmp_cnt = row_count;
    any_rec_cnt := any_rec_cnt + tmp_cnt;
    -- log rels
    update word_lifecycle_log wll set word_id = w_m_row.tgt_word_id where wll.word_id = w_m_row.src_word_id;
    get diagnostics tmp_cnt = row_count;
    log_rec_cnt := log_rec_cnt + tmp_cnt;
    update word_activity_log wal1
       set word_id = w_m_row.tgt_word_id
    where wal1.word_id = w_m_row.src_word_id
    and   not exists (select wal2.id
                      from word_activity_log wal2
                      where wal2.word_id = w_m_row.tgt_word_id
                      and   wal2.activity_log_id = wal1.activity_log_id);
    get diagnostics tmp_cnt = row_count;
    log_rec_cnt := log_rec_cnt + tmp_cnt;
    update activity_log al set owner_id = w_m_row.tgt_word_id where al.owner_id = w_m_row.src_word_id and al.owner_name = 'WORD';
    get diagnostics tmp_cnt = row_count;
    log_rec_cnt := log_rec_cnt + tmp_cnt;
    -- lex
    update lexeme l set word_id = w_m_row.tgt_word_id where l.id = w_m_row.src_lexeme_id;
    get diagnostics tmp_cnt = row_count;
    lex_rec_cnt := lex_rec_cnt + tmp_cnt;
    w_m_cnt := w_m_cnt + 1;
  end loop;
  raise info 'Word merge rows: %', w_m_cnt;
  raise info 'Moved any relations: %', any_rec_cnt;
  raise info 'Moved log relations: %', log_rec_cnt;
  raise info 'Moved lexemes: %', lex_rec_cnt;
end $$;

------------------------------------------------
-- osasünode tähendusseosteks konverteerimine --
------------------------------------------------

-- eeldused
insert into meaning_rel_type (code, datasets) values ('sarnane', '{}');
insert into meaning_rel_mapping (code1, code2) values ('sarnane', 'sarnane');
insert into meaning_rel_type_label (code, value, lang, type)  values ('sarnane', 'sarnane', 'est', 'descrip');
insert into tag (name) values ('süno migra');
alter table meaning_relation add column weight numeric(5,4);

-- konverteerimise ajutine abifunktsioon
create or replace function convert_lexeme_to_meaning_relation(m1_id bigint, m2_id bigint, m_rel_type_code varchar(100), l_sec_weigth numeric(5,4), l_sec_order_by bigint)
  returns void
  language plpgsql
as
$$
declare
  m_rel_row record;
  m_rel_exist boolean;
begin
  select count(mr.id) > 0 from meaning_relation mr where mr.meaning1_id = m1_id and mr.meaning2_id = m2_id and mr.meaning_rel_type_code = m_rel_type_code into m_rel_exist;
  if m_rel_exist = true then
    select mr.id, coalesce(mr.weight, 0) weight from meaning_relation mr where mr.meaning1_id = m1_id and mr.meaning2_id = m2_id and mr.meaning_rel_type_code = m_rel_type_code into m_rel_row;
    if m_rel_row.weight < l_sec_weigth then
      update meaning_relation set weight = l_sec_weigth where id = m_rel_row.id;
    end if;
  else
    if l_sec_order_by is null then
      insert into meaning_relation (meaning1_id, meaning2_id, meaning_rel_type_code, weight) values (m1_id, m2_id, m_rel_type_code, l_sec_weigth);
    else
      insert into meaning_relation (meaning1_id, meaning2_id, meaning_rel_type_code, weight, order_by) values (m1_id, m2_id, m_rel_type_code, l_sec_weigth, l_sec_order_by);
    end if;
  end if;
end $$;

-- konverteerimise põhiprotseduur
do $$
declare
  m_rel_type_code_sim constant varchar(100) := 'sarnane';
  lt_name_pse constant varchar(100) := 'süno migra';
  l_sec_row record;
  l_pri_row record;
  l_pri_cnt bigint;
  m_id bigint;
  l_id bigint;
  l_lvl integer;
  m_ext_fw_cnt bigint;
  m_ext_op_cnt bigint;
  m_ext_pse_cnt bigint;
  m_new_pse_cnt bigint;
begin
  m_ext_fw_cnt := 0;
  m_ext_op_cnt := 0;
  m_ext_pse_cnt := 0;
  m_new_pse_cnt := 0;
  for l_sec_row in 
    (select * from lexeme l where l.type = 'SECONDARY' order by l.order_by)
  loop
    -- look for single forward, single opposite lexeme
    select count(l2.id)
    from lexeme l2,
         word w2
    where w2.id = l_sec_row.word_id
    and   l2.word_id = w2.id
    and   l2.meaning_id != l_sec_row.meaning_id
    and   l2.dataset_code = l_sec_row.dataset_code
    and   l2.type = 'PRIMARY'
    and   not exists (select lt.id
                      from lexeme_tag lt
                      where lt.lexeme_id = l2.id
                      and   lt.tag_name = lt_name_pse)
    and   exists (select *
                  from lexeme l3,
                       word w1,
                       lexeme l4
                  where l3.meaning_id = l2.meaning_id
                  and   l3.dataset_code = l_sec_row.dataset_code
                  and   l3.type = 'SECONDARY'
                  and   l3.word_id = w1.id
                  and   l4.word_id = w1.id
                  and   w1.lang = w2.lang
                  and   w1.id != w2.id
                  and   l4.meaning_id = l_sec_row.meaning_id
                  and   l4.dataset_code = l_sec_row.dataset_code
                  and   l4.type = 'PRIMARY'
                  and   not exists (select lt.id
                                    from lexeme_tag lt
                                    where lt.lexeme_id = l4.id
                                    and   lt.tag_name = lt_name_pse)) into l_pri_cnt;
    if l_pri_cnt = 1 then
      -- select existing single forward, single opposite primary lexeme/meaning
      select l2.*
      from lexeme l2,
           word w2
      where w2.id = l_sec_row.word_id
      and   l2.word_id = w2.id
      and   l2.meaning_id != l_sec_row.meaning_id
      and   l2.dataset_code = l_sec_row.dataset_code
      and   l2.type = 'PRIMARY'
      and   exists (select *
                    from lexeme l3,
                         word w1,
                         lexeme l4
                    where l3.meaning_id = l2.meaning_id
                    and   l3.dataset_code = l_sec_row.dataset_code
                    and   l3.type = 'SECONDARY'
                    and   l3.word_id = w1.id
                    and   l4.word_id = w1.id
                    and   w1.lang = w2.lang
                    and   w1.id != w2.id
                    and   l4.meaning_id = l_sec_row.meaning_id
                    and   l4.dataset_code = l_sec_row.dataset_code
                    and   l4.type = 'PRIMARY'
                    and   not exists (select lt.id
                                      from lexeme_tag lt
                                      where lt.lexeme_id = l4.id
                                      and   lt.tag_name = lt_name_pse)) into l_pri_row;
      -- handle meaning relations 1 -> 2 (l_sec/existing valid l_prim)
      perform convert_lexeme_to_meaning_relation(l_sec_row.meaning_id, l_pri_row.meaning_id, m_rel_type_code_sim, l_sec_row.weight, l_sec_row.order_by);
      m_ext_op_cnt := m_ext_op_cnt + 1;
    else
      -- look for single forward lexeme/meaning
      select count(l2.id)
      from lexeme l2
      where l2.type = 'PRIMARY'
      and   l2.word_id = l_sec_row.word_id
      and   l2.meaning_id != l_sec_row.meaning_id
      and   l2.dataset_code = l_sec_row.dataset_code
      and   not exists (select lt.id
                        from lexeme_tag lt
                        where lt.lexeme_id = l2.id
                        and   lt.tag_name = lt_name_pse) into l_pri_cnt;
      if l_pri_cnt = 1 then
        -- single existing primary lexeme; simulated opposite lexeme
        select l2.*
        from lexeme l2
        where l2.type = 'PRIMARY'
        and   l2.word_id = l_sec_row.word_id
        and   l2.meaning_id != l_sec_row.meaning_id
        and   l2.dataset_code = l_sec_row.dataset_code
        and   not exists (select lt.id
                          from lexeme_tag lt
                          where lt.lexeme_id = l2.id
                          and   lt.tag_name = lt_name_pse) into l_pri_row;
        -- handle meaning relations 1 -> 2 (l_sec/existing valid l_prim)
        perform convert_lexeme_to_meaning_relation(l_sec_row.meaning_id, l_pri_row.meaning_id, m_rel_type_code_sim, l_sec_row.weight, l_sec_row.order_by);
        -- handle meaning relations 2 -> 1 (l_sec/existing valid l_prim)
        perform convert_lexeme_to_meaning_relation(l_pri_row.meaning_id, l_sec_row.meaning_id, m_rel_type_code_sim, l_sec_row.weight, null);
        m_ext_fw_cnt := m_ext_fw_cnt + 1;
      else
        -- look for existing pseudo lexeme/meaning
        select l2.*
        from lexeme l2
        where l2.type = 'PRIMARY'
        and   l2.word_id = l_sec_row.word_id
        and   l2.meaning_id != l_sec_row.meaning_id
        and   l2.dataset_code = l_sec_row.dataset_code
        and   exists (select lt.id
                      from lexeme_tag lt
                      where lt.lexeme_id = l2.id
                      and   lt.tag_name = lt_name_pse) into l_pri_row;
        if l_pri_row is null then
          -- new pseudo lexeme/meaning
          select max(l.level1)
          from lexeme l
          where l.type = 'PRIMARY'
          and   l.word_id = l_sec_row.word_id
          and   l.meaning_id != l_sec_row.meaning_id into l_lvl;
          l_lvl := l_lvl + 1;
          insert into meaning default values returning id into m_id;
          insert into lexeme (word_id, meaning_id, dataset_code, type, level1, complexity, is_public) values (l_sec_row.word_id, m_id, l_sec_row.dataset_code, 'PRIMARY', l_lvl, l_sec_row.complexity, false) returning id into l_id;
          insert into lexeme_tag (lexeme_id, tag_name) values (l_id, lt_name_pse);
          -- handle meaning relations 1 -> 2 (l_sec/new pseudo l_prim)
          perform convert_lexeme_to_meaning_relation(l_sec_row.meaning_id, m_id, m_rel_type_code_sim, l_sec_row.weight, l_sec_row.order_by);
          -- handle meaning relations 2 -> 1 (l_sec/new pseudo l_prim)
          perform convert_lexeme_to_meaning_relation(m_id, l_sec_row.meaning_id, m_rel_type_code_sim, l_sec_row.weight, null);
          m_new_pse_cnt := m_new_pse_cnt + 1;
        else
          -- handle meaning relations 1 -> 2 (l_sec/existig pseudo l_prim)
          perform convert_lexeme_to_meaning_relation(l_sec_row.meaning_id, l_pri_row.meaning_id, m_rel_type_code_sim, l_sec_row.weight, l_sec_row.order_by);
          -- handle meaning relations 2 -> 1 (l_sec/existing pseudo l_prim)
          perform convert_lexeme_to_meaning_relation(l_pri_row.meaning_id, l_sec_row.meaning_id, m_rel_type_code_sim, l_sec_row.weight, null);
          m_ext_pse_cnt := m_ext_pse_cnt + 1;
        end if;
      end if;
    end if;
  end loop;
  raise info 'Single forward and single opposite lexeme exists cases: %', m_ext_op_cnt;
  raise info 'Single forward lexeme exists cases: %', m_ext_fw_cnt;
  raise info 'Pseudo lexeme reuse cases: %', m_ext_pse_cnt;
  raise info 'Pseudo lexeme create cases: %', m_new_pse_cnt;
  raise info 'All done!';
end $$;

-- kustuta abifunktsioon
drop function convert_lexeme_to_meaning_relation(bigint,bigint,varchar(100),numeric(5,4),bigint);

------------------------------------------------
------------------ muu migra -------------------
------------------------------------------------

create type type_word_rel_meaning as (meaning_id bigint, definitions text array, lex_register_codes varchar(100) array);

-- terminivõrgustiku sõnakogu
insert into dataset (code, type, name) values ('vrk', 'TERM', 'Terminivõrgustik');

-- pildi failinimed URL-ideks
update freeform
set value_text = 'https://sonaveeb.ee/files/images/' || value_text
where type = 'IMAGE_FILE'
  and value_text not like '%https://sonaveeb.ee/files/images/%';

update freeform
set value_prese = 'https://sonaveeb.ee/files/images/' || value_prese
where type = 'IMAGE_FILE'
  and value_prese not like '%https://sonaveeb.ee/files/images/%';