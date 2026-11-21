-- ============================================================
-- SMART PORTER - Complete Database Schema
-- MySQL 8.0+ Compatible | Normalized to 3NF
-- ============================================================

DROP DATABASE IF EXISTS smart_porter;
CREATE DATABASE smart_porter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_porter;

-- ------------------------------------------------------------
-- 1. USERS (base authentication table)
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id         INT AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    phone           VARCHAR(15)  NOT NULL UNIQUE,
    role            ENUM('PASSENGER', 'PORTER', 'ADMIN') NOT NULL,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_role (role),
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2. PASSENGERS
-- ------------------------------------------------------------
CREATE TABLE passengers (
    passenger_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL UNIQUE,
    full_name       VARCHAR(120) NOT NULL,
    gender          ENUM('Male', 'Female', 'Other') DEFAULT NULL,
    date_of_birth   DATE DEFAULT NULL,
    address         VARCHAR(255) DEFAULT NULL,
    profile_photo   VARCHAR(255) DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3. PORTERS
-- ------------------------------------------------------------
CREATE TABLE porters (
    porter_id           INT AUTO_INCREMENT PRIMARY KEY,
    user_id             INT NOT NULL UNIQUE,
    full_name           VARCHAR(120) NOT NULL,
    experience_years    INT NOT NULL DEFAULT 0 CHECK (experience_years >= 0),
    profile_photo       VARCHAR(255) DEFAULT NULL,
    id_proof_type       VARCHAR(50)  DEFAULT NULL,
    id_proof_number     VARCHAR(50)  DEFAULT NULL,
    verification_status ENUM('PENDING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    verified_by         INT DEFAULT NULL,
    verified_at         TIMESTAMP NULL,
    average_rating      DECIMAL(3,2) DEFAULT 0.00,
    total_ratings       INT DEFAULT 0,
    is_available        TINYINT(1) NOT NULL DEFAULT 0,
    current_station_id  INT DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_porters_status (verification_status),
    INDEX idx_porters_available (is_available)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 4. ADMIN
-- ------------------------------------------------------------
CREATE TABLE admin (
    admin_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL UNIQUE,
    full_name   VARCHAR(120) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 5. STATIONS
-- ------------------------------------------------------------
CREATE TABLE stations (
    station_id      INT AUTO_INCREMENT PRIMARY KEY,
    station_code    VARCHAR(10)  NOT NULL UNIQUE,
    station_name    VARCHAR(150) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    state           VARCHAR(100) NOT NULL,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Add FK for porters.current_station_id after stations exists
ALTER TABLE porters
    ADD CONSTRAINT fk_porters_station
    FOREIGN KEY (current_station_id) REFERENCES stations(station_id)
    ON DELETE SET NULL;

-- ------------------------------------------------------------
-- 6. STATION LOCATIONS (pickup / drop points)
-- ------------------------------------------------------------
CREATE TABLE station_locations (
    location_id     INT AUTO_INCREMENT PRIMARY KEY,
    station_id      INT NOT NULL,
    location_name   VARCHAR(100) NOT NULL,
    location_type   ENUM('ENTRANCE', 'PLATFORM', 'PARKING', 'WAITING_HALL', 'OTHER') NOT NULL,
    description     VARCHAR(255) DEFAULT NULL,
    is_active       TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (station_id) REFERENCES stations(station_id) ON DELETE CASCADE,
    UNIQUE KEY uk_station_location (station_id, location_name)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 7. SERVICE TYPES
-- ------------------------------------------------------------
CREATE TABLE service_types (
    service_type_id     INT AUTO_INCREMENT PRIMARY KEY,
    service_name        VARCHAR(80)  NOT NULL UNIQUE,
    description         VARCHAR(255) DEFAULT NULL,
    base_fare           DECIMAL(10,2) NOT NULL CHECK (base_fare >= 0),
    per_bag_charge      DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (per_bag_charge >= 0),
    heavy_luggage_charge DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (heavy_luggage_charge >= 0),
    is_active           TINYINT(1) NOT NULL DEFAULT 1,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 8. FARE SETTINGS (configurable global / extra charges)
-- ------------------------------------------------------------
CREATE TABLE fare_settings (
    setting_id      INT AUTO_INCREMENT PRIMARY KEY,
    setting_key     VARCHAR(50)  NOT NULL UNIQUE,
    setting_value   DECIMAL(10,2) NOT NULL,
    description     VARCHAR(255) DEFAULT NULL,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 9. BOOKINGS
-- ------------------------------------------------------------
CREATE TABLE bookings (
    booking_id          INT AUTO_INCREMENT PRIMARY KEY,
    booking_code        VARCHAR(20)  NOT NULL UNIQUE,
    passenger_id        INT NOT NULL,
    porter_id           INT DEFAULT NULL,
    station_id          INT NOT NULL,
    pickup_location_id  INT NOT NULL,
    drop_location_id    INT NOT NULL,
    service_type_id     INT NOT NULL,
    status              ENUM(
                            'SEARCHING',
                            'PORTER_ASSIGNED',
                            'PORTER_ACCEPTED',
                            'PORTER_ARRIVING',
                            'SERVICE_STARTED',
                            'SERVICE_COMPLETED',
                            'CANCELLED'
                        ) NOT NULL DEFAULT 'SEARCHING',
    estimated_fare      DECIMAL(10,2) NOT NULL,
    final_fare          DECIMAL(10,2) DEFAULT NULL,
    special_instructions VARCHAR(500) DEFAULT NULL,
    booked_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at         TIMESTAMP NULL,
    started_at          TIMESTAMP NULL,
    completed_at        TIMESTAMP NULL,
    cancelled_at        TIMESTAMP NULL,
    cancellation_reason VARCHAR(255) DEFAULT NULL,
    FOREIGN KEY (passenger_id)       REFERENCES passengers(passenger_id),
    FOREIGN KEY (porter_id)          REFERENCES porters(porter_id),
    FOREIGN KEY (station_id)         REFERENCES stations(station_id),
    FOREIGN KEY (pickup_location_id) REFERENCES station_locations(location_id),
    FOREIGN KEY (drop_location_id)   REFERENCES station_locations(location_id),
    FOREIGN KEY (service_type_id)    REFERENCES service_types(service_type_id),
    INDEX idx_bookings_status (status),
    INDEX idx_bookings_passenger (passenger_id),
    INDEX idx_bookings_porter (porter_id),
    INDEX idx_bookings_station (station_id),
    INDEX idx_bookings_date (booked_at)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 10. LUGGAGE DETAILS
-- ------------------------------------------------------------
CREATE TABLE luggage_details (
    luggage_id          INT AUTO_INCREMENT PRIMARY KEY,
    booking_id          INT NOT NULL,
    number_of_bags      INT NOT NULL CHECK (number_of_bags > 0),
    luggage_type        VARCHAR(80) DEFAULT 'General',
    approximate_weight  DECIMAL(6,2) DEFAULT NULL COMMENT 'in kg',
    is_heavy            TINYINT(1) NOT NULL DEFAULT 0,
    special_handling    VARCHAR(255) DEFAULT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 11. PORTER AVAILABILITY (current status + history helper)
-- ------------------------------------------------------------
CREATE TABLE porter_availability (
    availability_id INT AUTO_INCREMENT PRIMARY KEY,
    porter_id       INT NOT NULL,
    station_id      INT NOT NULL,
    is_online       TINYINT(1) NOT NULL DEFAULT 0,
    last_updated    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (porter_id)  REFERENCES porters(porter_id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES stations(station_id),
    UNIQUE KEY uk_porter_station (porter_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 12. BOOKING STATUS HISTORY
-- ------------------------------------------------------------
CREATE TABLE booking_status_history (
    history_id      INT AUTO_INCREMENT PRIMARY KEY,
    booking_id      INT NOT NULL,
    old_status      VARCHAR(40) DEFAULT NULL,
    new_status      VARCHAR(40) NOT NULL,
    changed_by      INT DEFAULT NULL COMMENT 'user_id who changed status',
    remarks         VARCHAR(255) DEFAULT NULL,
    changed_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    INDEX idx_history_booking (booking_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 13. PAYMENTS
-- ------------------------------------------------------------
CREATE TABLE payments (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    booking_id      INT NOT NULL UNIQUE,
    amount          DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    payment_method  ENUM('CASH', 'UPI', 'ONLINE') NOT NULL,
    payment_status  ENUM('PENDING', 'SUCCESSFUL', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(100) DEFAULT NULL,
    paid_at         TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    INDEX idx_payments_status (payment_status)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 14. FEEDBACK / RATINGS
-- ------------------------------------------------------------
CREATE TABLE feedback (
    feedback_id     INT AUTO_INCREMENT PRIMARY KEY,
    booking_id      INT NOT NULL UNIQUE,
    passenger_id    INT NOT NULL,
    porter_id       INT NOT NULL,
    rating          TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text     VARCHAR(500) DEFAULT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id)   REFERENCES bookings(booking_id),
    FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id),
    FOREIGN KEY (porter_id)    REFERENCES porters(porter_id),
    INDEX idx_feedback_porter (porter_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 15. NOTIFICATIONS
-- ------------------------------------------------------------
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    title           VARCHAR(150) NOT NULL,
    message         VARCHAR(500) NOT NULL,
    is_read         TINYINT(1) NOT NULL DEFAULT 0,
    related_booking INT DEFAULT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id, is_read)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 16. FARE DETAILS (breakdown per booking)
-- ------------------------------------------------------------
CREATE TABLE fare_details (
    fare_detail_id  INT AUTO_INCREMENT PRIMARY KEY,
    booking_id      INT NOT NULL UNIQUE,
    base_fare       DECIMAL(10,2) NOT NULL,
    luggage_charge  DECIMAL(10,2) NOT NULL DEFAULT 0,
    heavy_charge    DECIMAL(10,2) NOT NULL DEFAULT 0,
    service_charge  DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount        DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_fare      DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Useful Indexes for performance
-- ------------------------------------------------------------
CREATE INDEX idx_porters_verified_available ON porters (verification_status, is_available);
CREATE INDEX idx_bookings_active ON bookings (status, station_id);

SELECT 'Schema created successfully!' AS status;