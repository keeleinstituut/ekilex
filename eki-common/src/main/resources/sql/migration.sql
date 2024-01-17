-- Allikaviidetes autori ja tõlkija parameetrist loobumine
update freeform_source_link set type = 'ANY' where type in ('AUTHOR', 'TRANSLATOR');
update word_etymology_source_link set type = 'ANY' where type = 'AUTHOR';

-- keelendid võivad olla ka kollokatsioonid
alter table word add column is_word boolean;
alter table word add column is_collocation boolean;
update word set is_word = true;
update word set is_collocation = false;
alter table word alter column is_word set not null;
alter table word alter column is_collocation set not null;
create index word_is_word_idx on word(is_word);
create index word_is_collocation_idx on word(is_collocation);
