SELECT
  lower(state) AS state,
  lower(city) AS city,
  lower(neighborhood) AS neighborhood,
  array(
    split(hours.Monday, '-'),
    split(hours.Tuesday, '-'),
    split(hours.Wednesday, '-'),
    split(hours.Thursday, '-'),
    split(hours.Friday, '-')
  ) as week_opening_hours
FROM businesses
WHERE state IS NOT NULL
AND city IS NOT NULL
AND neighborhood IS NOT NULL
