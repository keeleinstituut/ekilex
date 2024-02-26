insert into eki_user (name, email, password, is_admin, is_enabled) values ('Test Test', 'test@test.com', '$2a$10$8SwxxliqwpG5N9hK246.R.JNhwLFqACg5HVZtFXBhyb7SwbqKnObm', true, true);

insert into dataset (code, type, name) values ('eos', 'LEX', 'Õigekeelsussõnaraamat ÕS 2013');
insert into dataset (code, type, name) values ('ss_', 'LEX', 'Eesti keele seletav sõnaraamat');
insert into dataset (code, type, name) values ('sys', 'LEX', 'Sünonüümisõnastik');
insert into dataset (code, type, name) values ('evs', 'LEX', 'Eesti-vene sõnaraamat');

insert into word (id, value, value_prese, lang, homonym_nr, is_word, is_collocation) values (1001, 'hall', 'hall', 'est', 1, true, false);
insert into word (id, value, value_prese, lang, homonym_nr, is_word, is_collocation) values (1002, 'hall', 'hall', 'est', 2, true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1003, 'hallakord', 'hallakord', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1004, 'hallaudu', 'hallaudu', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1005, 'hallaöö', 'hallaöö', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1006, 'hallasääsk', 'hallasääsk', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1007, 'kaubahall', 'kaubahall', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1008, 'linnahall', 'linnahall', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1009, 'helehall', 'helehall', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1010, 'tumehall', 'tumehall', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1011, 'hiirhall', 'hiirhall', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1012, 'hiirjas', 'hiirjas', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1013, 'hiirukas', 'hiirukas', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1014, 'väär', 'väär', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1015, 'väär', 'väär', 'est', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1016, 'заморозки', 'заморозки', 'rus', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1017, 'серый', 'серый', 'rus', true, false);
insert into word (id, value, value_prese, lang, is_word, is_collocation) values (1018, 'холл', 'холл', 'rus', true, false);

insert into meaning (id) values (2001);
insert into meaning (id) values (2002);
insert into meaning (id) values (2003);
insert into meaning (id) values (2004);
insert into meaning (id) values (2005);
insert into meaning (id) values (2006);
insert into meaning (id) values (2007);
insert into meaning (id) values (2008);
insert into meaning (id) values (2009);
insert into meaning (id) values (2010);
insert into meaning (id) values (2011);
insert into meaning (id) values (2012);
insert into meaning (id) values (2013);
insert into meaning (id) values (2014);
insert into meaning (id) values (2015);
insert into meaning (id) values (2016);
insert into meaning (id) values (2017);
insert into meaning (id) values (2018);
insert into meaning (id) values (2019);
insert into meaning (id) values (2020);
insert into meaning (id) values (2021);
insert into meaning (id) values (2022);
insert into meaning (id) values (2023);
insert into meaning (id) values (2024);
insert into meaning (id) values (2025);
insert into meaning (id) values (2026);
insert into meaning (id) values (2027);

insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1001, 2001, 'määramata', 'külmunud kaste maas', 'külmunud kaste maas', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1002, 2002, 'määramata', 'avar üldkasutatav hoone v ruum', 'avar üldkasutatav hoone v ruum', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1003, 2002, 'määramata', 'suur esinduslik ruum elamus, kodasaal', 'suur esinduslik ruum elamus, kodasaal', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1004, 2004, 'määramata', 'temperatuuri langemisel alla 0 °C õhus olevast veeaurust tekkinud ebaühtlane jääkristallide kiht maapinnal, taimedel ja esemetel (hrl. kevadel ja sügisel)', 'temperatuuri langemisel alla 0 °C õhus olevast veeaurust tekkinud ebaühtlane jääkristallide kiht maapinnal, taimedel ja esemetel (hrl. kevadel ja sügisel)', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1005, 2005, 'määramata', 'värvuselt musta ja valge vahepealne', 'värvuselt musta ja valge vahepealne', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1006, 2006, 'määramata', 'valge ja pimeda vahepealne, hämar; sombune', 'valge ja pimeda vahepealne, hämar; sombune', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1007, 2007, 'määramata', 'kaugesse minevikku ulatuv, ajaloo hämarusse kaduv', 'kaugesse minevikku ulatuv, ajaloo hämarusse kaduv', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1008, 2008, 'määramata', 'üksluine, ühetooniline, vaheldusetu, tuim, igav', 'üksluine, ühetooniline, vaheldusetu, tuim, igav', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1009, 2009, 'määramata', 'see, kes v. mis on hall (1. täh.)', 'see, kes v. mis on hall (1. täh.)', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1010, 2010, 'määramata', 'kodasaal, elamu suurem siseruum; suur köetav esik (esimesel korrusel)', 'kodasaal, elamu suurem siseruum; suur köetav esik (esimesel korrusel)', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1011, 2011, 'määramata', 'rohkearvulisele publikule mõeldud suur saal v. hoone; suur tootmisruum v. hoone', 'rohkearvulisele publikule mõeldud suur saal v. hoone; suur tootmisruum v. hoone', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1012, 2016, 'määramata', 'suur esik; saal; tootmishoone', 'suur esik; saal; tootmishoone', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1013, 2021, 'määramata', 'suur selvekauplus, kus peale toiduainete müüakse ka muid igapäevakaupu', 'suur selvekauplus, kus peale toiduainete müüakse ka muid igapäevakaupu', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1014, 2025, 'määramata', 'hiirekarva hall', 'hiirekarva hall', 'est', 'DETAIL');
insert into definition (id, meaning_id, definition_type_code, value, value_prese, lang, complexity) values (1015, 2026, 'määramata', 'rõdu, eriti kirikus', 'rõdu, eriti kirikus', 'est', 'DETAIL');

insert into definition_dataset (definition_id, dataset_code) values (1001, 'qq2');
insert into definition_dataset (definition_id, dataset_code) values (1002, 'qq2');
insert into definition_dataset (definition_id, dataset_code) values (1003, 'qq2');
insert into definition_dataset (definition_id, dataset_code) values (1004, 'ss_');
insert into definition_dataset (definition_id, dataset_code) values (1005, 'ss_');
insert into definition_dataset (definition_id, dataset_code) values (1006, 'ss_');
insert into definition_dataset (definition_id, dataset_code) values (1007, 'ss_');
insert into definition_dataset (definition_id, dataset_code) values (1008, 'ss_');
insert into definition_dataset (definition_id, dataset_code) values (1009, 'ss_');
insert into definition_dataset (definition_id, dataset_code) values (1010, 'ss_');
insert into definition_dataset (definition_id, dataset_code) values (1011, 'ss_');
insert into definition_dataset (definition_id, dataset_code) values (1012, 'evs');
insert into definition_dataset (definition_id, dataset_code) values (1013, 'qq2');
insert into definition_dataset (definition_id, dataset_code) values (1014, 'qq2');
insert into definition_dataset (definition_id, dataset_code) values (1015, 'qq2');

insert into paradigm (id, word_id) values (1001, 1001);
insert into paradigm (id, word_id) values (1002, 1002);
insert into paradigm (id, word_id) values (1003, 1003);
insert into paradigm (id, word_id) values (1004, 1004);
insert into paradigm (id, word_id) values (1005, 1005);
insert into paradigm (id, word_id) values (1006, 1006);
insert into paradigm (id, word_id) values (1007, 1007);
insert into paradigm (id, word_id) values (1008, 1008);
insert into paradigm (id, word_id) values (1009, 1009);
insert into paradigm (id, word_id) values (1010, 1010);
insert into paradigm (id, word_id) values (1011, 1011);
insert into paradigm (id, word_id) values (1012, 1012);
insert into paradigm (id, word_id) values (1013, 1013);
insert into paradigm (id, word_id) values (1014, 1014);
insert into paradigm (id, word_id) values (1015, 1015);
insert into paradigm (id, word_id) values (1016, 1015);
insert into paradigm (id, word_id) values (1017, 1016);
insert into paradigm (id, word_id) values (1018, 1017);
insert into paradigm (id, word_id) values (1019, 1018);

insert into form (id, morph_code, value, value_prese) values (1001, 'SgN', 'hall', 'hall');
insert into form (id, morph_code, value, value_prese) values (1002, 'SgG', 'halla', 'halla');
insert into form (id, morph_code, value, value_prese) values (1003, 'SgP', 'halla', 'halla');
insert into form (id, morph_code, value, value_prese) values (1004, 'Neg', '-', '-');
insert into form (id, morph_code, value, value_prese) values (1005, 'SgN', 'hall', 'hall');
insert into form (id, morph_code, value, value_prese) values (1006, 'SgG', 'halli', 'halli');
insert into form (id, morph_code, value, value_prese) values (1007, 'SgP', 'halli', 'halli');
insert into form (id, morph_code, value, value_prese) values (1008, 'Neg', '-', '-');
insert into form (id, morph_code, value, value_prese) values (1009, 'SgN', 'hallakord', 'hallakord');
insert into form (id, morph_code, value, value_prese) values (1010, 'SgN', 'hallaudu', 'hallaudu');
insert into form (id, morph_code, value, value_prese) values (1011, 'SgN', 'hallaöö', 'hallaöö');
insert into form (id, morph_code, value, value_prese) values (1012, 'SgN', 'hallasääsk', 'hallasääsk');
insert into form (id, morph_code, value, value_prese) values (1013, 'SgN', 'kaubahall', 'kaubahall');
insert into form (id, morph_code, value, value_prese) values (1014, 'SgN', 'linnahall', 'linnahall');
insert into form (id, morph_code, value, value_prese) values (1015, 'SgN', 'helehall', 'helehall');
insert into form (id, morph_code, value, value_prese) values (1016, 'SgN', 'tumehall', 'tumehall');
insert into form (id, morph_code, value, value_prese) values (1017, 'SgN', 'hiirhall', 'hiirhall');
insert into form (id, morph_code, value, value_prese) values (1018, 'SgN', 'hiirjas', 'hiirjas');
insert into form (id, morph_code, value, value_prese) values (1019, 'SgN', 'hiirukas', 'hiirukas');
insert into form (id, morph_code, value, value_prese) values (1020, 'SgN', 'väär', 'väär');
insert into form (id, morph_code, value, value_prese) values (1021, 'SgG', 'vääri', 'vääri');
insert into form (id, morph_code, value, value_prese) values (1022, 'SgP', 'vääri', 'vääri');
insert into form (id, morph_code, value, value_prese) values (1023, 'SgN', 'väär', 'väär');
insert into form (id, morph_code, value, value_prese) values (1024, 'SgG', 'väära', 'väära');
insert into form (id, morph_code, value, value_prese) values (1025, 'SgP', 'väära', 'väära');
insert into form (id, morph_code, value, value_prese) values (1026, 'SgN', 'väär', 'väär');
insert into form (id, morph_code, value, value_prese) values (1027, 'SgG', 'väära', 'väära');
insert into form (id, morph_code, value, value_prese) values (1028, 'SgP', 'väärat', 'väärat');
insert into form (id, morph_code, value, value_prese) values (1029, 'PlN', 'заморозки', 'заморозки');
insert into form (id, morph_code, value, value_prese) values (1030, 'SgN', 'серый', 'серый');
insert into form (id, morph_code, value, value_prese) values (1031, 'SgN', 'холл', 'холл');

insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1001, 1001, 'hall', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1001, 1002, 'halla', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1001, 1003, 'h`alla', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1001, 1004, null, false);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1002, 1005, 'hall', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1002, 1006, 'halli', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1002, 1007, 'h`alli', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1002, 1008, null, false);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1003, 1009, 'halla+k`ord', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1004, 1010, 'halla+udu', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1005, 1011, 'halla+`öö', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1006, 1012, 'halla+s`ääsk', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1007, 1013, 'kauba+h`al''l', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1008, 1014, 'linna+h`al''l', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1009, 1015, 'hele+h`al''l', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1010, 1016, 'tume+h`al''l', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1011, 1017, 'h`iir+h`al''l', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1012, 1018, 'h`iirjas', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1013, 1019, 'hiirukas', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1014, 1020, 'v`äär', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1014, 1021, 'vääri', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1014, 1022, 'v`ääri', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1015, 1023, 'v`äär', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1015, 1024, 'väära', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1015, 1025, 'v`äära', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1016, 1026, 'v`äär', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1016, 1027, 'v`äära', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1016, 1028, 'v`äärat', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1017, 1029, 'заморозки', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1018, 1030, 'серый', true);
insert into paradigm_form (paradigm_id, form_id, display_form, morph_exists) values (1019, 1031, 'холл', true);

insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4001, 1001, 2001, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4002, 1002, 2002, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4003, 1002, 2003, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4004, 1001, 2004, 'ss_', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4005, 1002, 2005, 'ss_', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4006, 1002, 2006, 'ss_', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4007, 1002, 2007, 'ss_', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4008, 1002, 2008, 'ss_', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4009, 1002, 2009, 'ss_', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4010, 1002, 2010, 'ss_', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4011, 1002, 2011, 'ss_', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4012, 1001, 2012, 'sys', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4013, 1002, 2013, 'sys', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4014, 1001, 2014, 'evs', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4015, 1002, 2015, 'evs', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4016, 1002, 2016, 'evs', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4017, 1003, 2017, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4018, 1004, 2018, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4019, 1005, 2019, 'qq2', false, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4020, 1006, 2020, 'qq2', false, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4021, 1007, 2021, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4022, 1008, 2022, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4023, 1009, 2023, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4024, 1010, 2024, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4025, 1011, 2025, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4026, 1012, 2025, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4027, 1013, 2025, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4028, 1014, 2026, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4029, 1015, 2027, 'qq2', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4031, 1016, 2014, 'evs', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4032, 1017, 2015, 'evs', true, 'DETAIL');
insert into lexeme (id, word_id, meaning_id, dataset_code, is_public, complexity) values (4033, 1018, 2016, 'evs', true, 'DETAIL');

insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1001, 4001, 4017, 'comp');
insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1002, 4001, 4018, 'comp');
insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1003, 4001, 4019, 'comp');
insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1004, 4001, 4020, 'comp');
insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1005, 4002, 4021, 'comp');
insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1006, 4002, 4022, 'comp');
insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1007, 4003, 4023, 'comp');
insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1008, 4003, 4024, 'comp');
insert into lex_relation (id, lexeme1_id, lexeme2_id, lex_rel_type_code) values (1009, 4003, 4025, 'comp');

insert into freeform (id, parent_id, type, value_text, lang, complexity) values (1001, null, 'CONCEPT_ID', '123456', null, 'DETAIL');
insert into freeform (id, parent_id, type, value_text, lang, complexity) values (1002, null, 'GOVERNMENT', 'keda mida', 'est', 'DETAIL');
insert into freeform (id, parent_id, type, value_text, lang, complexity) values (1003, null, 'USAGE', 'Hommikul oli hall maas', 'est', 'DETAIL');
insert into freeform (id, parent_id, type, value_text, lang, complexity) values (1004, null, 'USAGE', 'Haned lähevad, hallad taga', 'est', 'DETAIL');

insert into meaning_freeform (id, meaning_id, freeform_id) values (1001, 2024, 1001);
insert into lexeme_freeform (id, lexeme_id, freeform_id) values (1002, 4001, 1003);
insert into lexeme_freeform (id, lexeme_id, freeform_id) values (1003, 4001, 1004);

insert into word_relation(id, word1_id, word2_id, word_rel_type_code, relation_status) values (1001, 1003, 1004, 'raw', 'UNDEFINED');
insert into word_relation(id, word1_id, word2_id, word_rel_type_code, relation_status) values (1002, 1003, 1005, 'deriv', 'UNDEFINED');
insert into word_relation(id, word1_id, word2_id, word_rel_type_code, relation_status) values (1003, 1003, 1006, 'raw', 'UNDEFINED');
insert into word_relation(id, word1_id, word2_id, word_rel_type_code, relation_status) values (1004, 1006, 1003, 'raw', 'UNDEFINED');
insert into word_relation(id, word1_id, word2_id, word_rel_type_code, relation_status) values (1005, 1004, 1003, 'deriv', 'UNDEFINED');

insert into word_relation_param(id, word_relation_id, name, value) values (1001, 1001, 'param1.1', '0.001');
insert into word_relation_param(id, word_relation_id, name, value) values (1002, 1002, 'param1.2', '0.002');
insert into word_relation_param(id, word_relation_id, name, value) values (1003, 1001, 'param2.2', '0.003');
