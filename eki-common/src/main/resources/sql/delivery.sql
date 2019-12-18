-- upgrade from ver 1.10.0 to 1.11.0

alter table lexeme add column weight numeric(5,4) default 1;
alter table word_relation_param alter column value type numeric(5,4) using value::numeric(5,4);

delete from word_relation_param wrp using word_relation_param wrp2
where wrp.word_relation_id = wrp2.word_relation_id
  and wrp.name = wrp2.name
  and wrp.id < wrp2.id;

alter table word_relation_param add unique (word_relation_id, name);