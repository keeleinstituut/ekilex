select count(code) cnt
from domain
where code = :code
and   origin = :origin
