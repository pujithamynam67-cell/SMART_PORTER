-- ============================================================
-- SMART PORTER - Triggers
-- ============================================================
USE smart_porter;

DELIMITER //

-- Trigger 1: After feedback insert → update porter rating
CREATE TRIGGER trg_after_feedback_insert
AFTER INSERT ON feedback
FOR EACH ROW
BEGIN
    UPDATE porters
    SET average_rating = (
            SELECT ROUND(AVG(rating), 2) FROM feedback WHERE porter_id = NEW.porter_id
        ),
        total_ratings = (
            SELECT COUNT(*) FROM feedback WHERE porter_id = NEW.porter_id
        )
    WHERE porter_id = NEW.porter_id;
END //

-- Trigger 2: Before booking insert → generate booking_code if null
CREATE TRIGGER trg_before_booking_insert
BEFORE INSERT ON bookings
FOR EACH ROW
BEGIN
    IF NEW.booking_code IS NULL OR NEW.booking_code = '' THEN
        SET NEW.booking_code = CONCAT('SP', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(FLOOR(RAND()*100), 2, '0'));
    END IF;
END //

-- Trigger 3: After booking status change to COMPLETED → create payment record if missing
CREATE TRIGGER trg_after_booking_completed
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    IF NEW.status = 'SERVICE_COMPLETED' AND OLD.status != 'SERVICE_COMPLETED' THEN
        -- Ensure a payment record exists (pending)
        IF NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = NEW.booking_id) THEN
            INSERT INTO payments (booking_id, amount, payment_method, payment_status)
            VALUES (NEW.booking_id, COALESCE(NEW.final_fare, NEW.estimated_fare), 'CASH', 'PENDING');
        END IF;

        -- Notification to passenger
        INSERT INTO notifications (user_id, title, message, related_booking)
        SELECT pas.user_id,
               'Service Completed',
               CONCAT('Your booking ', NEW.booking_code, ' has been completed. Please rate your experience.'),
               NEW.booking_id
        FROM passengers pas
        WHERE pas.passenger_id = NEW.passenger_id;
    END IF;
END //

DELIMITER ;

SELECT 'Triggers created successfully!' AS status;