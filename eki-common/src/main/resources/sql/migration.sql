-- tõlkevastete seaded kasutaja profiilis
alter table eki_user_profile drop column preferred_full_syn_candidate_langs;
alter table eki_user_profile add column preferred_full_syn_candidate_lang char(3) references language(code) null;