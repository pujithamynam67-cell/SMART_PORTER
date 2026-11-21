-- ============================================================
-- SMART PORTER - Stored Procedures
-- ============================================================
USE smart_porter;

DELIMITER //

-- Procedure 1: Create a new booking with transaction safety
CREATE PROCEDURE sp_create_booking(
    IN p_passenger_id INT,
    IN p_station_id INT,
    IN p_pickup_loc_id INT,
    IN p_drop_loc_id INT,
    IN p_service_type_id INT,
    IN p_num_bags INT,
    IN p_luggage_type VARCHAR(80),
    IN p_weight DECIMAL(6,2),
    IN p_is_heavy TINYINT,
    IN p_special_handling VARCHAR(255),
    IN p_special_instructions VARCHAR(500),
    OUT p_booking_id INT,
    OUT p_booking_code VARCHAR(20),
    OUT p_estimated_fare DECIMAL(10,2),
    OUT p_message VARCHAR(200)
)
BEGIN
    DECLARE v_base_fare DECIMAL(10,2);
    DECLARE v_per_bag DECIMAL(10,2);
    DECLARE v_heavy_charge DECIMAL(10,2);
    DECLARE v_luggage_charge DECIMAL(10,2) DEFAULT 0;
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_code VARCHAR(20);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_message = 'Error creating booking. Transaction rolled back.';
        SET p_booking_id = NULL;
    END;

    START TRANSACTION;

    -- Get service type fares
    SELECT base_fare, per_bag_charge, heavy_luggage_charge
    INTO v_base_fare, v_per_bag, v_heavy_charge
    FROM service_types
    WHERE service_type_id = p_service_type_id AND is_active = 1;

    IF v_base_fare IS NULL THEN
        SET p_message = 'Invalid service type';
        ROLLBACK;
    ELSE
        SET v_luggage_charge = (p_num_bags * v_per_bag);
        IF p_is_heavy = 1 THEN
            SET v_luggage_charge = v_luggage_charge + v_heavy_charge;
        END IF;
        SET v_total = v_base_fare + v_luggage_charge;

        -- Generate booking code
        SET v_code = CONCAT('SP', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(FLOOR(RAND()*10000), 4, '0'));

        INSERT INTO bookings (
            booking_code, passenger_id, station_id, pickup_location_id,
            drop_location_id, service_type_id, status, estimated_fare,
            special_instructions
        ) VALUES (
            v_code, p_passenger_id, p_station_id, p_pickup_loc_id,
            p_drop_loc_id, p_service_type_id, 'SEARCHING', v_total,
            p_special_instructions
        );

        SET p_booking_id = LAST_INSERT_ID();
        SET p_booking_code = v_code;
        SET p_estimated_fare = v_total;

        -- Insert luggage
        INSERT INTO luggage_details (
            booking_id, number_of_bags, luggage_type, approximate_weight,
            is_heavy, special_handling
        ) VALUES (
            p_booking_id, p_num_bags, p_luggage_type, p_weight,
            p_is_heavy, p_special_handling
        );

        -- Insert fare breakdown
        INSERT INTO fare_details (
            booking_id, base_fare, luggage_charge, heavy_charge, total_fare
        ) VALUES (
            p_booking_id, v_base_fare, (p_num_bags * v_per_bag),
            IF(p_is_heavy=1, v_heavy_charge, 0), v_total
        );

        -- Status history
        INSERT INTO booking_status_history (booking_id, old_status, new_status, remarks)
        VALUES (p_booking_id, NULL, 'SEARCHING', 'Booking created by passenger');

        COMMIT;
        SET p_message = 'Booking created successfully';
    END IF;
END //

-- Procedure 2: Assign / Accept porter for a booking (with locking)
CREATE PROCEDURE sp_assign_porter(
    IN p_booking_id INT,
    IN p_porter_id INT,
    OUT p_success TINYINT,
    OUT p_message VARCHAR(200)
)
BEGIN
    DECLARE v_status VARCHAR(40);
    DECLARE v_porter_available TINYINT;
    DECLARE v_porter_verified VARCHAR(20);
    DECLARE v_active_bookings INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = 0;
        SET p_message = 'Transaction failed. Rolled back.';
    END;

    START TRANSACTION;

    -- Lock the booking row
    SELECT status INTO v_status FROM bookings WHERE booking_id = p_booking_id FOR UPDATE;

    IF v_status IS NULL THEN
        SET p_success = 0;
        SET p_message = 'Booking not found';
        ROLLBACK;
    ELSEIF v_status NOT IN ('SEARCHING', 'PORTER_ASSIGNED') THEN
        SET p_success = 0;
        SET p_message = CONCAT('Cannot assign. Current status: ', v_status);
        ROLLBACK;
    ELSE
        -- Check porter
        SELECT is_available, verification_status
        INTO v_porter_available, v_porter_verified
        FROM porters WHERE porter_id = p_porter_id FOR UPDATE;

        IF v_porter_verified != 'VERIFIED' THEN
            SET p_success = 0;
            SET p_message = 'Porter is not verified';
            ROLLBACK;
        ELSEIF v_porter_available = 0 THEN
            SET p_success = 0;
            SET p_message = 'Porter is currently unavailable';
            ROLLBACK;
        ELSE
            -- Check if porter already has an active booking
            SELECT COUNT(*) INTO v_active_bookings
            FROM bookings
            WHERE porter_id = p_porter_id
              AND status IN ('PORTER_ASSIGNED','PORTER_ACCEPTED','PORTER_ARRIVING','SERVICE_STARTED');

            IF v_active_bookings > 0 THEN
                SET p_success = 0;
                SET p_message = 'Porter already has an active booking';
                ROLLBACK;
            ELSE
                UPDATE bookings
                SET porter_id = p_porter_id,
                    status = 'PORTER_ACCEPTED',
                    accepted_at = NOW()
                WHERE booking_id = p_booking_id;

                UPDATE porters SET is_available = 0 WHERE porter_id = p_porter_id;

                INSERT INTO booking_status_history (booking_id, old_status, new_status, remarks)
                VALUES (p_booking_id, v_status, 'PORTER_ACCEPTED', CONCAT('Porter ', p_porter_id, ' accepted'));

                COMMIT;
                SET p_success = 1;
                SET p_message = 'Porter assigned and accepted successfully';
            END IF;
        END IF;
    END IF;
END //

-- Procedure 3: Update booking status
CREATE PROCEDURE sp_update_booking_status(
    IN p_booking_id INT,
    IN p_new_status VARCHAR(40),
    IN p_changed_by INT,
    IN p_remarks VARCHAR(255),
    OUT p_success TINYINT,
    OUT p_message VARCHAR(200)
)
BEGIN
    DECLARE v_old_status VARCHAR(40);
    DECLARE v_porter_id INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = 0;
        SET p_message = 'Status update failed';
    END;

    START TRANSACTION;

    SELECT status, porter_id INTO v_old_status, v_porter_id
    FROM bookings WHERE booking_id = p_booking_id FOR UPDATE;

    IF v_old_status IS NULL THEN
        SET p_success = 0;
        SET p_message = 'Booking not found';
        ROLLBACK;
    ELSE
        UPDATE bookings SET status = p_new_status WHERE booking_id = p_booking_id;

        IF p_new_status = 'SERVICE_STARTED' THEN
            UPDATE bookings SET started_at = NOW() WHERE booking_id = p_booking_id;
        ELSEIF p_new_status = 'SERVICE_COMPLETED' THEN
            UPDATE bookings SET completed_at = NOW(), final_fare = estimated_fare
            WHERE booking_id = p_booking_id;
            -- Free the porter
            IF v_porter_id IS NOT NULL THEN
                UPDATE porters SET is_available = 1 WHERE porter_id = v_porter_id;
            END IF;
        ELSEIF p_new_status = 'CANCELLED' THEN
            UPDATE bookings SET cancelled_at = NOW() WHERE booking_id = p_booking_id;
            IF v_porter_id IS NOT NULL THEN
                UPDATE porters SET is_available = 1 WHERE porter_id = v_porter_id;
            END IF;
        END IF;

        INSERT INTO booking_status_history (booking_id, old_status, new_status, changed_by, remarks)
        VALUES (p_booking_id, v_old_status, p_new_status, p_changed_by, p_remarks);

        COMMIT;
        SET p_success = 1;
        SET p_message = 'Status updated successfully';
    END IF;
END //

DELIMITER ;

SELECT 'Stored procedures created successfully!' AS status;