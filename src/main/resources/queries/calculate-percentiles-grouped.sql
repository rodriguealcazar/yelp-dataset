SELECT
  state,
  city,
  neighborhood,
  to_time(percentile(day_opening, 0.5)) AS opening_median,
  to_time(percentile(day_opening, 0.95)) AS opening_95th,
  to_time(percentile(day_closing, 0.5)) AS closing_median,
  to_time(percentile(day_closing, 0.95)) AS closing_95th
FROM split_opening_hours
GROUP BY 1, 2, 3
HAVING count(3) > 5
ORDER BY 1, 2, 3