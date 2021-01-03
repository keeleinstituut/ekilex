
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
                  and   l3.type = 'SECONDARY'
                  and   l3.word_id = w1.id
                  and   l4.word_id = w1.id
                  and   w1.lang = w2.lang
                  and   w1.id != w2.id
                  and   l4.meaning_id = l_sec_row.meaning_id
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
      and   l2.type = 'PRIMARY'
      and   exists (select *
                    from lexeme l3,
                         word w1,
                         lexeme l4
                    where l3.meaning_id = l2.meaning_id
                    and   l3.type = 'SECONDARY'
                    and   l3.word_id = w1.id
                    and   l4.word_id = w1.id
                    and   w1.lang = w2.lang
                    and   w1.id != w2.id
                    and   l4.meaning_id = l_sec_row.meaning_id
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



