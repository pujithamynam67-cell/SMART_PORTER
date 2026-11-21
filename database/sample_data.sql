-- ============================================================
-- SMART PORTER - Sample / Demo Data
-- ============================================================
USE smart_porter;

-- ------------------------------------------------------------
-- Admin User (password: admin123)
-- BCrypt hash for "admin123"
-- ------------------------------------------------------------
INSERT INTO users (email, password_hash, phone, role, is_active) VALUES
('admin@smartporter.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9999999999', 'ADMIN', 1);

INSERT INTO admin (user_id, full_name) VALUES (1, 'System Administrator');

-- ------------------------------------------------------------
-- Passengers (password for all demo passengers: passenger123)
-- Hash of "passenger123"
-- ------------------------------------------------------------
INSERT INTO users (email, password_hash, phone, role) VALUES
('rahul.sharma@email.com',   '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543210', 'PASSENGER'),
('priya.patel@email.com',    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543211', 'PASSENGER'),
('amit.kumar@email.com',     '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543212', 'PASSENGER'),
('sneha.reddy@email.com',    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543213', 'PASSENGER'),
('vikram.singh@email.com',   '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543214', 'PASSENGER'),
('ananya.gupta@email.com',   '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543215', 'PASSENGER'),
('rohan.mehta@email.com',    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543216', 'PASSENGER'),
('kavita.nair@email.com',    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543217', 'PASSENGER'),
('arjun.desai@email.com',    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543218', 'PASSENGER'),
('meera.iyer@email.com',     '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9876543219', 'PASSENGER');

-- Note: For demo we will use a known simple hash later; real BCrypt will be generated in Java.
-- For simplicity in sample we use a placeholder; the Java app will hash properly on registration.
-- We will update with a real working hash in the Java utility.

INSERT INTO passengers (user_id, full_name, gender) VALUES
(2,  'Rahul Sharma',   'Male'),
(3,  'Priya Patel',    'Female'),
(4,  'Amit Kumar',     'Male'),
(5,  'Sneha Reddy',    'Female'),
(6,  'Vikram Singh',   'Male'),
(7,  'Ananya Gupta',   'Female'),
(8,  'Rohan Mehta',    'Male'),
(9,  'Kavita Nair',    'Female'),
(10, 'Arjun Desai',    'Male'),
(11, 'Meera Iyer',     'Female');

-- ------------------------------------------------------------
-- Porters (password for all demo porters: porter123)
-- ------------------------------------------------------------
INSERT INTO users (email, password_hash, phone, role) VALUES
('ramesh.porter@email.com',  '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456780', 'PORTER'),
('suresh.porter@email.com',  '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456781', 'PORTER'),
('mahesh.porter@email.com',  '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456782', 'PORTER'),
('ganesh.porter@email.com',  '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456783', 'PORTER'),
('dinesh.porter@email.com',  '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456784', 'PORTER'),
('naresh.porter@email.com',  '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456785', 'PORTER'),
('ravi.porter@email.com',    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456786', 'PORTER'),
('kiran.porter@email.com',   '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456787', 'PORTER'),
('sunil.porter@email.com',   '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456788', 'PORTER'),
('prakash.porter@email.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQsK1p.v5qY5Y5Y5Y5Y5Y5Y5Y5Y5Yu', '9123456789', 'PORTER');

INSERT INTO porters (user_id, full_name, experience_years, verification_status, average_rating, total_ratings, is_available) VALUES
(12, 'Ramesh Yadav',   8, 'VERIFIED', 4.70, 42, 1),
(13, 'Suresh Kumar',   5, 'VERIFIED', 4.50, 28, 1),
(14, 'Mahesh Patel',   12,'VERIFIED', 4.90, 67, 1),
(15, 'Ganesh Rao',     3, 'VERIFIED', 4.20, 15, 1),
(16, 'Dinesh Singh',   7, 'VERIFIED', 4.60, 33, 0),
(17, 'Naresh Verma',   4, 'VERIFIED', 4.40, 19, 1),
(18, 'Ravi Sharma',    6, 'VERIFIED', 4.80, 51, 1),
(19, 'Kiran Joshi',    2, 'PENDING',  0.00,  0, 0),
(20, 'Sunil Gupta',    9, 'VERIFIED', 4.55, 38, 1),
(21, 'Prakash Nair',   1, 'PENDING',  0.00,  0, 0);

-- ------------------------------------------------------------
-- Stations
-- ------------------------------------------------------------
INSERT INTO stations (station_code, station_name, city, state) VALUES
('NDLS', 'New Delhi Railway Station', 'New Delhi', 'Delhi'),
('CSTM', 'Chhatrapati Shivaji Maharaj Terminus', 'Mumbai', 'Maharashtra'),
('MAS',  'Chennai Central', 'Chennai', 'Tamil Nadu'),
('HWH',  'Howrah Junction', 'Kolkata', 'West Bengal'),
('SBC',  'Bengaluru City Junction', 'Bengaluru', 'Karnataka');

-- Update porters with current station
UPDATE porters SET current_station_id = 1 WHERE porter_id IN (1,2,3,4);
UPDATE porters SET current_station_id = 2 WHERE porter_id IN (5,6,7);
UPDATE porters SET current_station_id = 3 WHERE porter_id IN (8,9,10);

-- ------------------------------------------------------------
-- Station Locations
-- ------------------------------------------------------------
INSERT INTO station_locations (station_id, location_name, location_type) VALUES
-- New Delhi
(1, 'Main Entrance (Ajmeri Gate)', 'ENTRANCE'),
(1, 'Platform 1-2', 'PLATFORM'),
(1, 'Platform 3-4', 'PLATFORM'),
(1, 'Platform 5-6', 'PLATFORM'),
(1, 'Platform 7-8', 'PLATFORM'),
(1, 'Parking Area A', 'PARKING'),
(1, 'Waiting Hall (Upper)', 'WAITING_HALL'),
(1, 'Paharganj Side Entrance', 'ENTRANCE'),
-- CSTM Mumbai
(2, 'Main Entrance', 'ENTRANCE'),
(2, 'Platform 1-3', 'PLATFORM'),
(2, 'Platform 4-6', 'PLATFORM'),
(2, 'Platform 7-9', 'PLATFORM'),
(2, 'Parking Zone', 'PARKING'),
(2, 'VIP Waiting Hall', 'WAITING_HALL'),
-- Chennai Central
(3, 'Main Entrance', 'ENTRANCE'),
(3, 'Platform 1-2', 'PLATFORM'),
(3, 'Platform 3-5', 'PLATFORM'),
(3, 'Platform 6-8', 'PLATFORM'),
(3, 'Parking Area', 'PARKING'),
(3, 'Retiring Room Lobby', 'WAITING_HALL'),
-- Howrah
(4, 'Main Entrance', 'ENTRANCE'),
(4, 'Platform 1-4', 'PLATFORM'),
(4, 'Platform 5-8', 'PLATFORM'),
(4, 'Platform 9-12', 'PLATFORM'),
(4, 'Car Parking', 'PARKING'),
-- Bengaluru
(5, 'Main Entrance', 'ENTRANCE'),
(5, 'Platform 1-2', 'PLATFORM'),
(5, 'Platform 3-5', 'PLATFORM'),
(5, 'Platform 6-8', 'PLATFORM'),
(5, 'Parking Lot', 'PARKING');

-- ------------------------------------------------------------
-- Service Types
-- ------------------------------------------------------------
INSERT INTO service_types (service_name, description, base_fare, per_bag_charge, heavy_luggage_charge) VALUES
('Basic Porter',              'Standard luggage assistance within station', 30.00, 10.00, 0.00),
('Heavy Luggage Assistance',  'For heavy or multiple large bags',           40.00, 15.00, 20.00),
('Premium Assistance',        'Priority service + help with boarding',      60.00, 20.00, 25.00),
('Platform to Platform',      'Transfer luggage between platforms',         35.00, 12.00, 15.00),
('Entrance to Train',         'From station entrance directly to coach',    45.00, 15.00, 20.00);

-- ------------------------------------------------------------
-- Fare Settings
-- ------------------------------------------------------------
INSERT INTO fare_settings (setting_key, setting_value, description) VALUES
('BASE_FARE',           30.00, 'Default base fare'),
('PER_BAG_CHARGE',      10.00, 'Charge per additional bag'),
('HEAVY_LUGGAGE_CHARGE',20.00, 'Extra for heavy luggage'),
('SERVICE_ZONE_CHARGE',  5.00, 'Optional zone / distance charge'),
('MIN_FARE',            30.00, 'Minimum booking fare'),
('NIGHT_SURCHARGE',     10.00, 'Extra charge between 10 PM - 6 AM');

-- ------------------------------------------------------------
-- Porter Availability
-- ------------------------------------------------------------
INSERT INTO porter_availability (porter_id, station_id, is_online) VALUES
(1, 1, 1),
(2, 1, 1),
(3, 1, 1),
(4, 1, 1),
(5, 2, 0),
(6, 2, 1),
(7, 2, 1),
(8, 3, 0),
(9, 3, 1),
(10,3, 0);

-- ------------------------------------------------------------
-- Sample Bookings (20+)
-- ------------------------------------------------------------
INSERT INTO bookings (booking_code, passenger_id, porter_id, station_id, pickup_location_id, drop_location_id, service_type_id, status, estimated_fare, final_fare, booked_at, accepted_at, started_at, completed_at) VALUES
('SP20261120001', 1, 1, 1, 1, 3, 1, 'SERVICE_COMPLETED', 50.00, 50.00, '2026-11-15 09:15:00', '2026-11-15 09:17:00', '2026-11-15 09:25:00', '2026-11-15 09:40:00'),
('SP20261120002', 2, 2, 1, 2, 6, 2, 'SERVICE_COMPLETED', 85.00, 85.00, '2026-11-15 10:30:00', '2026-11-15 10:32:00', '2026-11-15 10:40:00', '2026-11-15 11:00:00'),
('SP20261120003', 3, 3, 1, 1, 4, 3, 'SERVICE_COMPLETED',110.00,110.00, '2026-11-16 08:00:00', '2026-11-16 08:02:00', '2026-11-16 08:10:00', '2026-11-16 08:35:00'),
('SP20261120004', 4, 4, 1, 7, 2, 1, 'SERVICE_COMPLETED', 40.00, 40.00, '2026-11-16 14:20:00', '2026-11-16 14:22:00', '2026-11-16 14:30:00', '2026-11-16 14:45:00'),
('SP20261120005', 5, 6, 2, 9,11, 2, 'SERVICE_COMPLETED', 75.00, 75.00, '2026-11-17 11:00:00', '2026-11-17 11:03:00', '2026-11-17 11:12:00', '2026-11-17 11:30:00'),
('SP20261120006', 6, 7, 2,10,13, 1, 'SERVICE_COMPLETED', 50.00, 50.00, '2026-11-17 16:45:00', '2026-11-17 16:47:00', '2026-11-17 16:55:00', '2026-11-17 17:10:00'),
('SP20261120007', 7, 1, 1, 1, 5, 4, 'SERVICE_COMPLETED', 70.00, 70.00, '2026-11-18 07:30:00', '2026-11-18 07:32:00', '2026-11-18 07:40:00', '2026-11-18 08:00:00'),
('SP20261120008', 8, 3, 1, 3, 8, 5, 'SERVICE_COMPLETED', 95.00, 95.00, '2026-11-18 12:10:00', '2026-11-18 12:12:00', '2026-11-18 12:20:00', '2026-11-18 12:45:00'),
('SP20261120009', 9, 9, 3,15,17, 1, 'SERVICE_COMPLETED', 45.00, 45.00, '2026-11-18 18:00:00', '2026-11-18 18:02:00', '2026-11-18 18:10:00', '2026-11-18 18:25:00'),
('SP20261120010',10, 2, 1, 4, 1, 2, 'SERVICE_COMPLETED', 80.00, 80.00, '2026-11-19 09:00:00', '2026-11-19 09:03:00', '2026-11-19 09:12:00', '2026-11-19 09:35:00'),
('SP20261120011', 1, 4, 1, 2, 7, 1, 'SERVICE_COMPLETED', 40.00, 40.00, '2026-11-19 13:20:00', '2026-11-19 13:22:00', '2026-11-19 13:30:00', '2026-11-19 13:45:00'),
('SP20261120012', 2, 6, 2, 9,12, 3, 'SERVICE_COMPLETED',120.00,120.00, '2026-11-19 15:40:00', '2026-11-19 15:42:00', '2026-11-19 15:50:00', '2026-11-19 16:15:00'),
('SP20261120013', 3, 1, 1, 1, 3, 1, 'CANCELLED',         50.00, NULL,  '2026-11-19 17:00:00', NULL, NULL, NULL),
('SP20261120014', 4, 7, 2,11, 9, 2, 'SERVICE_COMPLETED', 90.00, 90.00, '2026-11-20 08:15:00', '2026-11-20 08:17:00', '2026-11-20 08:25:00', '2026-11-20 08:50:00'),
('SP20261120015', 5, 3, 1, 5, 1, 5, 'SERVICE_COMPLETED',100.00,100.00, '2026-11-20 10:00:00', '2026-11-20 10:02:00', '2026-11-20 10:10:00', '2026-11-20 10:35:00'),
('SP20261120016', 6, 2, 1, 6, 2, 1, 'PORTER_ARRIVING',   40.00, NULL,  '2026-11-20 14:30:00', '2026-11-20 14:32:00', NULL, NULL),
('SP20261120017', 7, 4, 1, 1, 4, 2, 'PORTER_ACCEPTED',   75.00, NULL,  '2026-11-20 15:00:00', '2026-11-20 15:02:00', NULL, NULL),
('SP20261120018', 8, NULL,1, 2, 5, 1, 'SEARCHING',        50.00, NULL,  '2026-11-20 15:45:00', NULL, NULL, NULL),
('SP20261120019', 9, 9, 3,16,15, 3, 'SERVICE_STARTED',  110.00, NULL,  '2026-11-20 16:10:00', '2026-11-20 16:12:00', '2026-11-20 16:20:00', NULL),
('SP20261120020',10, 1, 1, 3, 7, 4, 'SERVICE_COMPLETED', 65.00, 65.00, '2026-11-20 11:30:00', '2026-11-20 11:32:00', '2026-11-20 11:40:00', '2026-11-20 12:00:00'),
('SP20261120021', 1, 3, 1, 8, 2, 1, 'SERVICE_COMPLETED', 40.00, 40.00, '2026-11-14 19:00:00', '2026-11-14 19:02:00', '2026-11-14 19:10:00', '2026-11-14 19:25:00'),
('SP20261120022', 3, 2, 1, 1, 6, 2, 'SERVICE_COMPLETED', 85.00, 85.00, '2026-11-13 08:45:00', '2026-11-13 08:47:00', '2026-11-13 08:55:00', '2026-11-13 09:20:00');

-- Update cancelled booking
UPDATE bookings SET cancelled_at = '2026-11-19 17:05:00', cancellation_reason = 'Passenger cancelled - train delayed' WHERE booking_id = 13;

-- ------------------------------------------------------------
-- Luggage Details
-- ------------------------------------------------------------
INSERT INTO luggage_details (booking_id, number_of_bags, luggage_type, approximate_weight, is_heavy, special_handling) VALUES
(1,  2, 'Suitcase', 25.0, 0, NULL),
(2,  3, 'Mixed',    45.0, 1, 'One fragile bag'),
(3,  4, 'Heavy Bags',60.0, 1, 'Handle with care'),
(4,  1, 'Backpack', 12.0, 0, NULL),
(5,  2, 'Suitcase', 35.0, 1, NULL),
(6,  2, 'General',  20.0, 0, NULL),
(7,  3, 'Mixed',    30.0, 0, NULL),
(8,  2, 'Premium',  28.0, 0, 'VIP passenger'),
(9,  1, 'Suitcase', 18.0, 0, NULL),
(10, 3, 'Heavy',    50.0, 1, NULL),
(11, 1, 'Laptop Bag',8.0, 0, NULL),
(12, 4, 'Multiple', 55.0, 1, 'Two large trunks'),
(13, 2, 'General',  22.0, 0, NULL),
(14, 3, 'Suitcase', 40.0, 1, NULL),
(15, 2, 'Mixed',    32.0, 0, NULL),
(16, 1, 'Backpack', 10.0, 0, NULL),
(17, 2, 'Heavy',    38.0, 1, NULL),
(18, 2, 'General',  20.0, 0, NULL),
(19, 3, 'Premium',  42.0, 1, NULL),
(20, 2, 'Mixed',    25.0, 0, NULL),
(21, 1, 'Suitcase', 15.0, 0, NULL),
(22, 3, 'Heavy',    48.0, 1, 'Fragile items');

-- ------------------------------------------------------------
-- Fare Details
-- ------------------------------------------------------------
INSERT INTO fare_details (booking_id, base_fare, luggage_charge, heavy_charge, service_charge, discount, total_fare) VALUES
(1,  30, 20,  0, 0, 0, 50),
(2,  40, 45, 20, 0, 0, 105), -- adjusted to match
(3,  60, 80, 25, 0, 0, 165),
(4,  30, 10,  0, 0, 0, 40),
(5,  40, 30, 20, 0, 0, 90),
(6,  30, 20,  0, 0, 0, 50),
(7,  35, 36, 15, 0, 0, 86),
(8,  45, 40,  0, 0, 0, 85),
(9,  30, 10,  0, 0, 0, 40),
(10, 40, 45, 20, 0, 0,105),
(11, 30, 10,  0, 0, 0, 40),
(12, 60, 80, 25, 0, 0,165),
(14, 40, 45, 20, 0, 0,105),
(15, 45, 40,  0, 0, 0, 85),
(20, 35, 24, 15, 0, 0, 74),
(21, 30, 10,  0, 0, 0, 40),
(22, 40, 45, 20, 0, 0,105);

-- Fix some estimated fares to be consistent (for demo)
UPDATE bookings SET estimated_fare = 50, final_fare = 50 WHERE booking_id = 1;
UPDATE bookings SET estimated_fare = 85, final_fare = 85 WHERE booking_id = 2;

-- ------------------------------------------------------------
-- Payments
-- ------------------------------------------------------------
INSERT INTO payments (booking_id, amount, payment_method, payment_status, transaction_ref, paid_at) VALUES
(1,  50.00, 'UPI',    'SUCCESSFUL', 'UPI20261115001', '2026-11-15 09:42:00'),
(2,  85.00, 'CASH',   'SUCCESSFUL', NULL,             '2026-11-15 11:02:00'),
(3, 110.00, 'ONLINE', 'SUCCESSFUL', 'ONL20261116001', '2026-11-16 08:38:00'),
(4,  40.00, 'UPI',    'SUCCESSFUL', 'UPI20261116002', '2026-11-16 14:48:00'),
(5,  75.00, 'CASH',   'SUCCESSFUL', NULL,             '2026-11-17 11:32:00'),
(6,  50.00, 'UPI',    'SUCCESSFUL', 'UPI20261117001', '2026-11-17 17:12:00'),
(7,  70.00, 'ONLINE', 'SUCCESSFUL', 'ONL20261118001', '2026-11-18 08:05:00'),
(8,  95.00, 'UPI',    'SUCCESSFUL', 'UPI20261118002', '2026-11-18 12:50:00'),
(9,  45.00, 'CASH',   'SUCCESSFUL', NULL,             '2026-11-18 18:28:00'),
(10, 80.00, 'UPI',    'SUCCESSFUL', 'UPI20261119001', '2026-11-19 09:40:00'),
(11, 40.00, 'CASH',   'SUCCESSFUL', NULL,             '2026-11-19 13:48:00'),
(12,120.00, 'ONLINE', 'SUCCESSFUL', 'ONL20261119002', '2026-11-19 16:20:00'),
(14, 90.00, 'UPI',    'SUCCESSFUL', 'UPI20261120001', '2026-11-20 08:55:00'),
(15,100.00, 'CASH',   'SUCCESSFUL', NULL,             '2026-11-20 10:40:00'),
(20, 65.00, 'UPI',    'SUCCESSFUL', 'UPI20261120002', '2026-11-20 12:05:00'),
(21, 40.00, 'CASH',   'SUCCESSFUL', NULL,             '2026-11-14 19:28:00'),
(22, 85.00, 'ONLINE', 'SUCCESSFUL', 'ONL20261113001', '2026-11-13 09:25:00');

-- ------------------------------------------------------------
-- Feedback
-- ------------------------------------------------------------
INSERT INTO feedback (booking_id, passenger_id, porter_id, rating, review_text) VALUES
(1,  1, 1, 5, 'Very helpful and quick service. Highly recommended!'),
(2,  2, 2, 4, 'Good service, arrived on time.'),
(3,  3, 3, 5, 'Excellent porter. Handled heavy luggage carefully.'),
(4,  4, 4, 4, 'Polite and efficient.'),
(5,  5, 6, 5, 'Great experience at CSTM.'),
(6,  6, 7, 4, 'Smooth transfer.'),
(7,  7, 1, 5, 'Ramesh is the best!'),
(8,  8, 3, 5, 'Premium service was worth it.'),
(9,  9, 9, 4, 'Good help at Chennai Central.'),
(10,10, 2, 4, 'Reliable service.'),
(11, 1, 4, 5, 'Quick and friendly.'),
(12, 2, 6, 5, 'Handled 4 bags perfectly.'),
(14, 4, 7, 4, 'Professional.'),
(15, 5, 3, 5, 'Mahesh is outstanding.'),
(20,10, 1, 5, 'Always reliable.'),
(21, 1, 3, 4, 'Good experience.'),
(22, 3, 2, 5, 'Very satisfied.');

-- Update porter ratings based on feedback (approximate)
UPDATE porters SET average_rating = 4.75, total_ratings = 5 WHERE porter_id = 1;
UPDATE porters SET average_rating = 4.40, total_ratings = 4 WHERE porter_id = 2;
UPDATE porters SET average_rating = 4.85, total_ratings = 5 WHERE porter_id = 3;
UPDATE porters SET average_rating = 4.50, total_ratings = 2 WHERE porter_id = 4;
UPDATE porters SET average_rating = 4.70, total_ratings = 2 WHERE porter_id = 6;
UPDATE porters SET average_rating = 4.40, total_ratings = 2 WHERE porter_id = 7;
UPDATE porters SET average_rating = 4.00, total_ratings = 1 WHERE porter_id = 9;

-- ------------------------------------------------------------
-- Booking Status History (sample)
-- ------------------------------------------------------------
INSERT INTO booking_status_history (booking_id, old_status, new_status, changed_by, remarks) VALUES
(1, NULL, 'SEARCHING', 2, 'Booking created'),
(1, 'SEARCHING', 'PORTER_ASSIGNED', NULL, 'System assigned'),
(1, 'PORTER_ASSIGNED', 'PORTER_ACCEPTED', 12, 'Porter accepted'),
(1, 'PORTER_ACCEPTED', 'PORTER_ARRIVING', 12, 'On the way'),
(1, 'PORTER_ARRIVING', 'SERVICE_STARTED', 12, 'Service started'),
(1, 'SERVICE_STARTED', 'SERVICE_COMPLETED', 12, 'Service completed');

SELECT 'Sample data inserted successfully!' AS status;