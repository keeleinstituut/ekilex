-- mõiste viimane muutmise ja kinnitamise kuupäev 
alter table meaning_last_activity_log add column type varchar(100);
update meaning_last_activity_log set type = 'EDIT';
alter table meaning_last_activity_log alter column type set not null;
alter table meaning_last_activity_log drop constraint meaning_last_activity_log_meaning_id_key;
alter table meaning_last_activity_log add constraint meaning_last_activity_log_meaning_id_type_key unique (meaning_id, type);
alter table eki_user_profile add column is_approve_meaning_enabled boolean default false;

-- otsevaste seos
insert into lex_rel_type (code, datasets) values ('otse', '{}');
insert into lex_rel_type_label (code, value, lang, type) values ('otse', 'otsevaste', 'est', 'descrip');
insert into lex_rel_type_label (code, value, lang, type) values ('otse', 'otsevaste', 'est', 'wordweb');
insert into lex_rel_mapping (code1, code2) values ('otse', 'otse');

-- kustutab seosed iseendaga
delete from word_relation where word1_id = word2_id;
delete from lex_relation where lexeme1_id = lexeme2_id;
delete from meaning_relation where meaning1_id = meaning2_id;

alter table lexeme alter column level1 set not null;
alter table lexeme alter column level2 set not null;

-- ühendsõnastiku sõnakogu koodi muutmine
alter table dataset_permission drop constraint dataset_permission_dataset_code_fkey;
alter table dataset_permission
  add constraint dataset_permission_dataset_code_fkey foreign key (dataset_code)
    references dataset(code)
    on update cascade
    on delete cascade;

alter table word_guid drop constraint word_guid_dataset_code_fkey;
alter table word_guid
  add constraint word_guid_dataset_code_fkey foreign key (dataset_code)
    references dataset(code)
    on update cascade
    on delete cascade;

alter table meaning_nr drop constraint meaning_nr_dataset_code_fkey;
alter table meaning_nr
  add constraint meaning_nr_dataset_code_fkey foreign key (dataset_code)
    references dataset(code)
    on update cascade
    on delete cascade;

alter table definition_dataset drop constraint definition_dataset_dataset_code_fkey;
alter table definition_dataset
  add constraint definition_dataset_dataset_code_fkey foreign key (dataset_code)
    references dataset(code)
    on update cascade
    on delete cascade;

alter table lexeme drop constraint lexeme_dataset_code_fkey;
alter table lexeme
  add constraint lexeme_dataset_code_fkey foreign key (dataset_code)
    references dataset(code)
    on update cascade
    on delete no action;

update dataset set code = 'eki' where code = 'sss';

update eki_user_application set datasets = array_replace(datasets, 'sss', 'eki');
update eki_user_profile set preferred_datasets = array_replace(preferred_datasets, 'sss', 'eki');
update language set datasets = array_replace(datasets, 'sss', 'eki');
update domain set datasets = array_replace(datasets, 'sss', 'eki');

update value_state set datasets = array_replace(datasets, 'sss', 'eki');
update government_type set datasets = array_replace(datasets, 'sss', 'eki');
update register set datasets = array_replace(datasets, 'sss', 'eki');
update semantic_type set datasets = array_replace(datasets, 'sss', 'eki');
update word_type set datasets = array_replace(datasets, 'sss', 'eki');
update aspect set datasets = array_replace(datasets, 'sss', 'eki');
update gender set datasets = array_replace(datasets, 'sss', 'eki');
update pos set datasets = array_replace(datasets, 'sss', 'eki');
update pos_group set datasets = array_replace(datasets, 'sss', 'eki');
update morph set datasets = array_replace(datasets, 'sss', 'eki');
update display_morph set datasets = array_replace(datasets, 'sss', 'eki');
update deriv set datasets = array_replace(datasets, 'sss', 'eki');
update lex_rel_type set datasets = array_replace(datasets, 'sss', 'eki');
update word_rel_type set datasets = array_replace(datasets, 'sss', 'eki');
update meaning_rel_type set datasets = array_replace(datasets, 'sss', 'eki');
update usage_type set datasets = array_replace(datasets, 'sss', 'eki');
update etymology_type set datasets = array_replace(datasets, 'sss', 'eki');
update definition_type set datasets = array_replace(datasets, 'sss', 'eki');
update region set datasets = array_replace(datasets, 'sss', 'eki');

drop function if exists merge_homonyms_to_sss(char(3) array);
create or replace function merge_homonyms_to_eki(included_langs char(3) array)
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
            w_eki.word_id tgt_word_id
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
          and   l.dataset_code not in ('eki', 'ety')
          and   not exists (select l2.id
                            from lexeme l2
                            where l2.word_id = w.id
                            and   l2.dataset_code = 'eki')) w_xxx,
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
                        and   l.dataset_code = 'eki'
                        and   l.is_public = true)
          and   not exists (select w2.id
                            from word w2
                            where w2.value = w1.value
                            and   w2.lang = w1.lang
                            and   w2.id != w1.id
                            and   exists (select l.id
                                          from lexeme l
                                          where l.word_id = w2.id
                                          and   l.dataset_code = 'eki'
                                          and   l.is_public = true))) w_eki
    where w_xxx.word = w_eki.word
    and   w_xxx.lang = w_eki.lang
    and   w_xxx.word_id != w_eki.word_id
    and   w_xxx.is_pf = w_eki.is_pf
    and   w_xxx.is_sf = w_eki.is_sf
    and   w_xxx.is_th = w_eki.is_th
    and   w_xxx.is_l = w_eki.is_l
    order by w_xxx.word_id, w_xxx.lexeme_id, w_eki.word_id)
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

-- sõnakogu pilt
alter table dataset add column image_url text;

-- Sõnaveebi tagasiside vorm
alter table feedback_log rename column usages to usage;
alter table feedback_log
  add column description text null,
  add column definition_source text null,
  add column usage_source text null,
  add column company text null,
  add column domain text null,
  alter column word drop not null;

-- homonüüminumbrite järjestamine
create type type_word_hom_nr_data_tuple as (word_id bigint, homonym_nr integer);

create or replace function adjust_homonym_nrs()
  returns void
  language plpgsql
as $$
declare
  ordered_homonym_nrs_str_pattern text := array_to_string(array(select generate_series(1, 100)), '-', '');
  word_row                        record;
  adj_word_ids                    type_word_hom_nr_data_tuple;
  homonym_nr_iter                 integer;
begin
  for word_row in
    (select w.value,
            w.lang,
            w.word_ids
     from (select w.value,
                  w.lang,
                  array_agg(row (w.id, w.homonym_nr)::type_word_hom_nr_data_tuple order by w.ds_order_by, w.af_order_by, w.homonym_nr, w.id) word_ids,
                  array_to_string(array_agg(w.homonym_nr order by w.ds_order_by, w.af_order_by, w.homonym_nr), '-', '') homonym_nrs_str
           from (select w.id,
                        w.value,
                        w.homonym_nr,
                        w.lang,
                        (select case
                                  when count(l.id) > 0 then 1
                                  else 2
                                  end
                         from lexeme l
                         where l.word_id = w.id and l.dataset_code = 'eki') ds_order_by,
                        (select case
                                  when count(wt.id) > 0 then 2
                                  else 1
                                  end
                         from word_word_type wt
                         where wt.word_id = w.id and wt.word_type_code in ('pf', 'sf')) af_order_by
                 from word w
                 where exists(select l.id from lexeme l where l.word_id = w.id)) w
           group by w.value,
                    w.lang) w
     where ordered_homonym_nrs_str_pattern not like w.homonym_nrs_str || '%'
     order by w.lang, w.value)
    loop
      homonym_nr_iter := 1;
      foreach adj_word_ids in array word_row.word_ids
        loop
          if homonym_nr_iter != adj_word_ids.homonym_nr then
            update word set homonym_nr = homonym_nr_iter where id = adj_word_ids.word_id;
          end if;
          homonym_nr_iter := homonym_nr_iter + 1;
        end loop;
    end loop;
end $$;

select adjust_homonym_nrs();
