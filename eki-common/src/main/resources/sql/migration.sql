-- Kasutusnäite autori ja tõlkija parameetrist loobumine
update freeform_source_link set type = 'ANY' where type in ('AUTHOR', 'TRANSLATOR');