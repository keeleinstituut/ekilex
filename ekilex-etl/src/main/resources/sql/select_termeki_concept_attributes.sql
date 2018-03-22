SELECT * FROM termeki_concept_attributes_varchar WHERE attribute_id = :attributeId
UNION
SELECT * FROM termeki_concept_attributes_html WHERE attribute_id = :attributeId