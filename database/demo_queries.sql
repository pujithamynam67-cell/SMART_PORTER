-- ============================================================
-- SMART PORTER - 15+ Demonstration Queries
-- Demonstrates: SELECT, JOIN, Aggregate, GROUP BY, HAVING,
-- Subquery, ORDER BY, Views, etc.
-- ============================================================
USE smart_porter;

-- 1. Find all available verified porters at a particular station (New Delhi = 1)
SELECT * FROM v_available_porters WHERE station_id = 1;

-- 2. Display passenger booking history
SELECT booking_code, status, station_name, pickup_location, drop_location,
       estimated_fare, booked_at, completed_at
FROM v_booking_details
WHERE passenger_id = 1
ORDER BY booked_at DESC;

-- 3. Calculate total revenue
SELECT SUM(final_fare) AS total_revenue
FROM bookings
WHERE status = 'SERVICE_COMPLETED';

-- 4. Find the most active porters (by completed bookings)
SELECT porter_id, full_name, total_completed_bookings, total_earnings
FROM v_porter_earnings
ORDER BY total_completed_bookings DESC
LIMIT 5;

-- 5. Find the highest-rated porter
SELECT porter_id, full_name, average_rating, total_ratings
FROM porters
WHERE verification_status = 'VERIFIED' AND total_ratings > 0
ORDER BY average_rating DESC, total_ratings DESC
LIMIT 1;

-- 6. Find passengers who booked more than three times
SELECT pas.passenger_id, pas.full_name, COUNT(b.booking_id) AS total_bookings
FROM passengers pas
JOIN bookings b ON pas.passenger_id = b.passenger_id
GROUP BY pas.passenger_id, pas.full_name
HAVING COUNT(b.booking_id) > 3
ORDER BY total_bookings DESC;

-- 7. Display completed bookings with payment details
SELECT b.booking_code, pas.full_name AS passenger, por.full_name AS porter,
       b.final_fare, p.payment_method, p.payment_status, p.paid_at
FROM bookings b
JOIN passengers pas ON b.passenger_id = pas.passenger_id
LEFT JOIN porters por ON b.porter_id = por.porter_id
LEFT JOIN payments p ON b.booking_id = p.booking_id
WHERE b.status = 'SERVICE_COMPLETED'
ORDER BY b.completed_at DESC;

-- 8. INNER JOIN: Bookings with both passenger and porter details
SELECT b.booking_code, pas.full_name AS passenger, por.full_name AS porter, b.status
FROM bookings b
INNER JOIN passengers pas ON b.passenger_id = pas.passenger_id
INNER JOIN porters por ON b.porter_id = por.porter_id
WHERE b.status = 'SERVICE_COMPLETED';

-- 9. LEFT JOIN: All porters and their completed booking count (including zero)
SELECT p.full_name, COUNT(b.booking_id) AS completed_count
FROM porters p
LEFT JOIN bookings b ON p.porter_id = b.porter_id AND b.status = 'SERVICE_COMPLETED'
GROUP BY p.porter_id, p.full_name
ORDER BY completed_count DESC;

-- 10. Subquery: Porters who have rating higher than average
SELECT full_name, average_rating
FROM porters
WHERE average_rating > (SELECT AVG(average_rating) FROM porters WHERE total_ratings > 0)
  AND verification_status = 'VERIFIED';

-- 11. Aggregate + GROUP BY: Revenue by station
SELECT s.station_name, COUNT(b.booking_id) AS bookings, SUM(b.final_fare) AS revenue
FROM stations s
JOIN bookings b ON s.station_id = b.station_id
WHERE b.status = 'SERVICE_COMPLETED'
GROUP BY s.station_id, s.station_name
ORDER BY revenue DESC;

-- 12. HAVING: Stations with more than 5 completed bookings
SELECT s.station_name, COUNT(*) AS completed
FROM stations s
JOIN bookings b ON s.station_id = b.station_id
WHERE b.status = 'SERVICE_COMPLETED'
GROUP BY s.station_id, s.station_name
HAVING COUNT(*) > 5;

-- 13. Booking status distribution
SELECT status, COUNT(*) AS count
FROM bookings
GROUP BY status
ORDER BY count DESC;

-- 14. Feedback summary with porter name
SELECT por.full_name, f.rating, f.review_text, f.created_at
FROM feedback f
JOIN porters por ON f.porter_id = por.porter_id
ORDER BY f.created_at DESC
LIMIT 10;

-- 15. Use function: Calculate fare for a hypothetical booking
SELECT fn_calculate_fare(1, 3, 1) AS estimated_fare_for_3_bags_heavy;

-- 16. Daily revenue from view
SELECT * FROM v_daily_revenue LIMIT 10;

-- 17. Pending payments
SELECT b.booking_code, pas.full_name, p.amount, p.payment_status
FROM payments p
JOIN bookings b ON p.booking_id = b.booking_id
JOIN passengers pas ON b.passenger_id = pas.passenger_id
WHERE p.payment_status = 'PENDING';

-- 18. Active bookings currently in progress
SELECT booking_code, passenger_name, porter_name, status, station_name
FROM v_booking_details
WHERE status IN ('PORTER_ASSIGNED','PORTER_ACCEPTED','PORTER_ARRIVING','SERVICE_STARTED');

SELECT 'Demo queries ready!' AS status;