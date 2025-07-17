-- kollokatsioonide poolt hõivatud määramata morfo vormide vabastamine

insert into morph (code, datasets, order_by) values ('!!', '{}', 999999);
insert into morph_label (code, value, lang, type) values ('!!', 'kollide teadmatu', 'est', 'comment');


