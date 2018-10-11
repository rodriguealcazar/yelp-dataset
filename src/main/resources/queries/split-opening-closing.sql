SELECT
  state,
  city,
  neighborhood,
  to_minutes(day_operating_hours[0]) AS day_opening,
  to_minutes(day_operating_hours[1]) AS day_closing
FROM exploded_opening_hours
WHERE day_operating_hours IS NOT NULL
