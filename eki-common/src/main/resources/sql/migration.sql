-- tähenduse sildid
alter table tag add column type varchar(10) not null default 'LEX';
alter table tag alter column type drop default;

create table meaning_tag
(
  id bigserial primary key,
  meaning_id bigint references meaning(id) on delete cascade not null,
  tag_name varchar(100) references tag(name) on delete cascade not null,
  created_on timestamp not null default statement_timestamp(),
  unique(meaning_id, tag_name)
);
alter sequence meaning_tag_id_seq restart with 10000;

create index meaning_tag_meaning_id_idx on meaning_tag(meaning_id);
create index meaning_tag_tag_name_idx on meaning_tag(tag_name);

-- ilmiku siltide kolimine tähenduse siltideks
do $$
declare
  meaning_tag_names text[] := '{001, 002, 003, 004, 005, 006, 007, 008, 009, 010, 011, 012, 013, 014, 015,' ||
                              ' 016, 017, 018, 019, 020, 021, 022, 023, 024, 025, 026, 027, 028, 029, 030,' ||
                              ' 031, 032, 033, 034, 035, 036, 037, 038, 039, 040, 041, 042, 043, 044, 045,' ||
                              ' 046, 047, 048, 049, 050, 051, 052, 053, 054, 055, 056, 057, 058, 059, 060,' ||
                              ' 061, 062, 063, 064, 065, 066, 067, 068, 069, 070, 071, 072, 073, 074, 075,' ||
                              ' 076, 077, 078, 079, 080, 081, 082, 083, 084, 085, 086, 087, 088, 089, 090,' ||
                              ' 091, 092, 093, 094, 095, 096, 097, 098, 099, 100, 101, 102, 103, 104}';
begin
  insert into meaning_tag (meaning_id, tag_name)
  select l.meaning_id, lt.tag_name
  from lexeme l, lexeme_tag lt
  where lt.tag_name = any(meaning_tag_names) and l.id = lt.lexeme_id
  on conflict do nothing;

  delete from lexeme_tag where tag_name = any(meaning_tag_names);

  update tag set type = 'MEANING' where name = any(meaning_tag_names);
end $$;

-- ilmiku usaldusväärsus
alter table lexeme add column reliability integer null;

-- täiendavad indeksid tegevuslogide ajaloo päringuteks
create index activity_entity_id_idx on activity_log(entity_id);
create index activity_curr_data_word_id_idx on activity_log(cast(curr_data ->> 'wordId' as bigint));
create index activity_curr_data_meaning_id_idx on activity_log(cast(curr_data ->> 'meaningId' as bigint));
create index activity_curr_data_lexeme_id_idx on activity_log(cast(curr_data ->> 'lexemeId' as bigint));

-- html entity asendamised
do $$
declare
  html_entity           text;
  sym                   text;
  html_entity_sym_row   text[];
  html_entity_sym_array text[][] := array [
    ['&otilde;','õ'],['&Otilde;','Õ'],
    ['&auml;','ä'],['&Auml;','Ä'],
    ['&ouml;','ö'],['&Ouml;','Ö'],
    ['&uuml;','ü'],['&Uuml;','Ü'],
    ['&scaron;','š'],['&Scaron;','Š'],
    ['&ocirc;','ô'],['&Ocirc;','Ô'],
    ['&oacute;','ó'],['&Oacute;','Ó'],
    ['&ograve;','ò'],['&Ograve;','Ò'],
    ['&ocirc;','ô'],['&Ocirc;','Ô'],
    ['&ntilde;','ñ'],['&Ntilde;','Ñ'],
    ['&quot;','"'],['&#39;',''''],['&bdquo;','„'],
    ['&ldquo;','“'],['&rdquo;','”'],
    ['&lsquo;','‘'],['&rsquo;','’'],
    ['&laquo;','«'],['raquo','»'],
    ['&ndash;','–'],
    ['&gt;','>'],['&lt;','<'],
    ['&nbsp;',' '],
    ['&alpha;','α'],['&Alpha;','Α'],
    ['&beta;','β'],['&Beta;','Β'],
    ['&gamma;','γ'],['&Gamma;','Γ'],
    ['&delta;','δ'],['&Delta;','Δ'],
    ['&epsilon;','ε'],['&Epsilon;','Ε'],
    ['&zeta;','ζ'],['&Zeta;','Ζ'],
    ['&eta;','η'],['&Eta;','Η'],
    ['&theta;','θ'],['&Theta;','Θ'],
    ['&iota;','ι'],['&Iota;','Ι'],
    ['&kappa;','κ'],['&Kappa;','Κ'],
    ['&lambda;','λ'],['&Lambda;','Λ'],
    ['&mu;','μ'],['&Mu;','Μ'],
    ['&nu;','ν'],['&Nu;','Ν'],
    ['&xi;','ξ'],['&Xi;','Ξ'],
    ['&omicron;','ο'],['&Omicron;','Ο'],
    ['&pi;','π'],['&Pi;','Π'],
    ['&Rho;','ρ'],['&Zeta;','Ρ'],
    ['&sigmaf;','ς'],
    ['&sigma;','σ'],['&Sigma;','Σ'],
    ['&tau;','τ'],['&Tau;','Τ'],
    ['&upsilon;','υ'],['&Upsilon;','Υ'],
    ['&phi;','φ'],['&Phi;','Φ'],
    ['&chi;','χ'],['&Chi;','Χ'],
    ['&psi;','ψ'],['&Psi;','Ψ'],
    ['&omega;','ω'],['&Omega;','Ω'],
    ['&hellip;','…'],
    ['&micro;','µ'],
    ['&minus;','−']];
begin
  foreach html_entity_sym_row slice 1 in array html_entity_sym_array
    loop
      html_entity := html_entity_sym_row[1];
      sym := html_entity_sym_row[2];
      update freeform set value_text = replace(value_text, html_entity, sym) where value_text like '%'||html_entity||'%';
      update freeform set value_prese = replace(value_prese, html_entity, sym) where value_prese like '%'||html_entity||'%';
      update definition set value = replace(value, html_entity, sym) where value like '%'||html_entity||'%';
      update definition set value_prese = replace(value_prese, html_entity, sym) where value_prese like '%'||html_entity||'%';
      update word set value = replace(value, html_entity, sym) where value like '%'||html_entity||'%';
      update word set value_prese = replace(value_prese, html_entity, sym) where value_prese like '%'||html_entity||'%';
      raise notice 'replaced % to %',html_entity, sym;
    end loop;
end $$;

-- eemaldab tühikud keelendi ees ja lõpus
update word set value = trim(value), value_prese = trim(value_prese);

-- asünkroonsed datapäringud
create table data_request
(
  id bigserial primary key,
  user_id bigint references eki_user(id) on delete cascade not null,
  request_key varchar(60) not null,
  content text not null,
  accessed timestamp,
  created timestamp not null default statement_timestamp()
);
alter sequence data_request_id_seq restart with 10000;
create index data_request_user_id_idx on data_request(user_id);

-- definitsioonide puhastamine html kehadest
update definition d
   set value_prese = trim(substring(d.value_prese, position('<body>' in d.value_prese) + 7, position('</body>' in d.value_prese) - position('<body>' in d.value_prese) - 7))
where d.value_prese like '%<html>%';

-- merge_homonyms_to_eki protseduuri parandus lexeme_word_id_meaning_id_dataset_code_key constraint kaitseks
drop function if exists merge_homonyms_to_eki(char(3) array);
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
    update lexeme l1
       set word_id = w_m_row.tgt_word_id
    where l1.id = w_m_row.src_lexeme_id
    and   not exists (select l2.id
                      from lexeme l2
                      where l2.word_id = w_m_row.tgt_word_id
                      and   l2.meaning_id = l1.meaning_id
                      and   l2.dataset_code = l1.dataset_code);
    get diagnostics tmp_cnt = row_count;
    lex_rec_cnt := lex_rec_cnt + tmp_cnt;
    w_m_cnt := w_m_cnt + 1;
  end loop;
  raise info 'Word merge rows: %', w_m_cnt;
  raise info 'Moved any relations: %', any_rec_cnt;
  raise info 'Moved log relations: %', log_rec_cnt;
  raise info 'Moved lexemes: %', lex_rec_cnt;
end $$;

-- kth sõnakogu detailsus
update lexeme set complexity = 'DETAIL' where complexity = 'DEFAULT';
update definition set complexity = 'DETAIL' where complexity = 'DEFAULT';

-- ilmiku keeletase
create table proficiency_level
(
  code varchar(100) primary key,
  datasets varchar(10) array not null,
  order_by bigserial
);

create table proficiency_level_label
(
  code varchar(100) references proficiency_level(code) on delete cascade not null,
  value text not null,
  lang char(3) references language(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

alter table lexeme add column proficiency_level_code varchar(100) references proficiency_level(code) null;

insert into proficiency_level (code, datasets) values ('A1', '{}');
insert into proficiency_level (code, datasets) values ('A2', '{}');
insert into proficiency_level (code, datasets) values ('B1', '{}');
insert into proficiency_level (code, datasets) values ('B2', '{}');
insert into proficiency_level (code, datasets) values ('C1', '{}');
insert into proficiency_level (code, datasets) values ('C2', '{}');

insert into proficiency_level_label (code, value, lang, type) values ('A1', 'A1', 'est', 'descrip');
insert into proficiency_level_label (code, value, lang, type) values ('A2', 'A2', 'est', 'descrip');
insert into proficiency_level_label (code, value, lang, type) values ('B1', 'B1', 'est', 'descrip');
insert into proficiency_level_label (code, value, lang, type) values ('B2', 'B2', 'est', 'descrip');
insert into proficiency_level_label (code, value, lang, type) values ('C1', 'C1', 'est', 'descrip');
insert into proficiency_level_label (code, value, lang, type) values ('C2', 'C2', 'est', 'descrip');
insert into proficiency_level_label (code, value, lang, type) values ('A1', 'A1', 'est', 'wordweb');
insert into proficiency_level_label (code, value, lang, type) values ('A2', 'A2', 'est', 'wordweb');
insert into proficiency_level_label (code, value, lang, type) values ('B1', 'B1', 'est', 'wordweb');
insert into proficiency_level_label (code, value, lang, type) values ('B2', 'B2', 'est', 'wordweb');
insert into proficiency_level_label (code, value, lang, type) values ('C1', 'C1', 'est', 'wordweb');
insert into proficiency_level_label (code, value, lang, type) values ('C2', 'C2', 'est', 'wordweb');