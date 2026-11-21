-- ============================================================
-- SMART PORTER - Stored Functions
-- ============================================================
USE smart_porter;

DELIMITER //

-- Function 1: Calculate estimated fare
CREATE FUNCTION fn_calculate_fare(
    p_service_type_id INT,
    p_num_bags INT,
    p_is_heavy TINYINT
) RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_base DECIMAL(10,2);
    DECLARE v_per_bag DECIMAL(10,2);
    DECLARE v_heavy DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);

    SELECT base_fare, per_bag_charge, heavy_luggage_charge
    INTO v_base, v_per_bag, v_heavy
    FROM service_types
    WHERE service_type_id = p_service_type_id AND is_active = 1;

    IF v_base IS NULL THEN
        RETURN 0;
    END IF;

    SET v_total = v_base + (p_num_bags * v_per_bag);
    IF p_is_heavy = 1 THEN
        SET v_total = v_total + v_heavy;
    END IF;

    RETURN v_total;
END //

-- Function 2: Get porter average rating (recalculate)
CREATE FUNCTION fn_get_porter_rating(p_porter_id INT)
RETURNS DECIMAL(3,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_avg DECIMAL(3,2);

    SELECT COALESCE(AVG(rating), 0)
    INTO v_avg
    FROM feedback
    WHERE porter_id = p_porter_id;

    RETURN ROUND(v_avg, 2);
END //

DELIMITER ;

SELECT 'Functions created successfully!' AS status;