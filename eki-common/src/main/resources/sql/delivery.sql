-- upgrade from ver 1.26.0 to 1.27.0

-- tõlkevastete seaded kasutaja profiilis
alter table eki_user_profile drop column preferred_full_syn_candidate_langs;
alter table eki_user_profile add column preferred_full_syn_candidate_lang char(3) references language(code) null;

-- vastete kandidaadi andmebaasi tüübi täiendus
drop type if exists type_word_rel_meaning;
create type type_word_rel_meaning as (
  meaning_id bigint,
  lexeme_id bigint,
  definition_values text array,
  usage_values text array,
  lex_register_codes varchar(100) array,
  lex_pos_codes varchar(100) array
);
