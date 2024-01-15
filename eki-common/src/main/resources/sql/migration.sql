-- Allikaviidetes autori ja tõlkija parameetrist loobumine
update freeform_source_link set type = 'ANY' where type in ('AUTHOR', 'TRANSLATOR');
update word_etymology_source_link set type = 'ANY' where type = 'AUTHOR';

-- Ajutine lühikese kuju väärtus allikatele millel see väärtus puudub
update source set name = 'source-name-placeholder' where name is null;

-- Allika lühike kuju kohustuslikuks väljaks
alter table source alter column name set not null;

-- Loo uuesti ekilexi baasi tüübid (types) ja vaated (views)