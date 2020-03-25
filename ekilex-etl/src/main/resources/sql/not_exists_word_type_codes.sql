and not exists(select wwt.id
               from word_word_type wwt
               where wwt.word_id = w1.id
                 and wwt.word_type_code in (:excludedWordTypeCodes))
and not exists(select wwt.id
               from word_word_type wwt
               where wwt.word_id = w2.id
                 and wwt.word_type_code in (:excludedWordTypeCodes))