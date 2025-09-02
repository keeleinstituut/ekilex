
create index dataset_is_public_idx on dataset(is_public);

alter table word_os_recommendation drop column is_public cascade;
alter table word_os_usage drop column is_public cascade;
alter table word_os_morph drop column is_public cascade;
