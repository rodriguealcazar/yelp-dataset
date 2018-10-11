SELECT
  state,
  city,
  neighborhood,
  EXPLODE(week_opening_hours) AS day_operating_hours
FROM opening_hours
ORDER BY 1
