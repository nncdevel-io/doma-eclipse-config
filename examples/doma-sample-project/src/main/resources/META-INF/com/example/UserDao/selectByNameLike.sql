/* 名前部分一致検索 */
SELECT
  id,
  name,
  email
FROM
  users
WHERE
  name LIKE /* @infix(name) */'%test%'
ORDER BY id