-- ============================================================
-- SMART PORTER - Useful Views
-- ============================================================
USE smart_porter;

-- View 1: Available verified porters with station info
CREATE OR REPLACE VIEW v_available_porters AS
SELECT 
    p.porter_id,
    p.full_name,
    p.experience_years,
    p.average_rating,
    p.total_ratings,
    p.is_available,
    s.station_id,
    s.station_name,
    s.city,
    u.phone,
    u.email
FROM porters p
JOIN users u ON p.user_id = u.user_id
LEFT JOIN stations s ON p.current_station_id = s.station_id
WHERE p.verification_status = 'VERIFIED'
  AND p.is_available = 1
  AND u.is_active = 1;

-- View 2: Complete booking details (passenger + porter + station + fare)
CREATE OR REPLACE VIEW v_booking_details AS
SELECT 
    b.booking_id,
    b.booking_code,
    b.status,
    b.estimated_fare,
    b.final_fare,
    b.booked_at,
    b.completed_at,
    b.cancelled_at,
    b.special_instructions,
    -- Passenger
    pas.passenger_id,
    pas.full_name AS passenger_name,
    up.phone AS passenger_phone,
    -- Porter
    por.porter_id,
    por.full_name AS porter_name,
    upor.phone AS porter_phone,
    por.average_rating AS porter_rating,
    -- Station & locations
    st.station_name,
    st.city,
    pl.location_name AS pickup_location,
    dl.location_name AS drop_location,
    -- Service
    stype.service_name,
    -- Luggage
    ld.number_of_bags,
    ld.luggage_type,
    ld.approximate_weight,
    ld.is_heavy,
    -- Payment
    pay.payment_status,
    pay.payment_method,
    pay.amount AS paid_amount
FROM bookings b
JOIN passengers pas ON b.passenger_id = pas.passenger_id
JOIN users up ON pas.user_id = up.user_id
LEFT JOIN porters por ON b.porter_id = por.porter_id
LEFT JOIN users upor ON por.user_id = upor.user_id
JOIN stations st ON b.station_id = st.station_id
JOIN station_locations pl ON b.pickup_location_id = pl.location_id
JOIN station_locations dl ON b.drop_location_id = dl.location_id
JOIN service_types stype ON b.service_type_id = stype.service_type_id
LEFT JOIN luggage_details ld ON b.booking_id = ld.booking_id
LEFT JOIN payments pay ON b.booking_id = pay.booking_id;

-- View 3: Porter earnings summary
CREATE OR REPLACE VIEW v_porter_earnings AS
SELECT 
    p.porter_id,
    p.full_name,
    COUNT(b.booking_id) AS total_completed_bookings,
    COALESCE(SUM(b.final_fare), 0) AS total_earnings,
    COALESCE(AVG(b.final_fare), 0) AS avg_fare_per_booking,
    p.average_rating,
    p.total_ratings
FROM porters p
LEFT JOIN bookings b ON p.porter_id = b.porter_id 
    AND b.status = 'SERVICE_COMPLETED'
GROUP BY p.porter_id, p.full_name, p.average_rating, p.total_ratings;

-- View 4: Daily revenue summary (bonus)
CREATE OR REPLACE VIEW v_daily_revenue AS
SELECT 
    DATE(b.completed_at) AS service_date,
    COUNT(*) AS completed_bookings,
    SUM(b.final_fare) AS total_revenue,
    AVG(b.final_fare) AS avg_fare
FROM bookings b
WHERE b.status = 'SERVICE_COMPLETED'
  AND b.completed_at IS NOT NULL
GROUP BY DATE(b.completed_at)
ORDER BY service_date DESC;

SELECT 'Views created successfully!' AS status;