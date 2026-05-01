-- ============================================================
-- Vehicle Identification System - PostgreSQL Schema
-- Run this script in psql or pgAdmin before starting the app
-- ============================================================

CREATE DATABASE vehicle_db;
\c vehicle_db;

-- -------------------------------------------------------
-- Customer Table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS Customer (
    customer_id  SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    address      VARCHAR(255),
    phone        VARCHAR(20),
    email        VARCHAR(100)
);

-- -------------------------------------------------------
-- Vehicle Table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS Vehicle (
    vehicle_id          SERIAL PRIMARY KEY,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    make                VARCHAR(50),
    model               VARCHAR(50),
    year                INT,
    owner_id            INT REFERENCES Customer(customer_id) ON DELETE SET NULL
);

-- -------------------------------------------------------
-- ServiceRecord Table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS ServiceRecord (
    service_id   SERIAL PRIMARY KEY,
    vehicle_id   INT REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    service_date DATE,
    service_type VARCHAR(100),
    description  TEXT,
    cost         NUMERIC(10,2)
);

-- -------------------------------------------------------
-- CustomerQuery Table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS CustomerQuery (
    query_id      SERIAL PRIMARY KEY,
    customer_id   INT REFERENCES Customer(customer_id) ON DELETE CASCADE,
    vehicle_id    INT REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    query_date    DATE,
    query_text    TEXT,
    response_text TEXT
);

-- -------------------------------------------------------
-- PoliceReport Table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS PoliceReport (
    report_id    SERIAL PRIMARY KEY,
    vehicle_id   INT REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    report_date  DATE,
    report_type  VARCHAR(50),
    description  TEXT,
    officer_name VARCHAR(100)
);

-- -------------------------------------------------------
-- Violation Table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS Violation (
    violation_id   SERIAL PRIMARY KEY,
    vehicle_id     INT REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    violation_date DATE,
    violation_type VARCHAR(100),
    fine_amount    NUMERIC(10,2),
    status         VARCHAR(10) CHECK (status IN ('Paid','Unpaid')) DEFAULT 'Unpaid'
);

-- -------------------------------------------------------
-- Users Table (Admin/Login)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS Users (
    user_id   SERIAL PRIMARY KEY,
    username  VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(100) NOT NULL,
    role      VARCHAR(20) CHECK (role IN ('ADMIN','WORKSHOP','POLICE','CUSTOMER','INSURANCE'))
);

-- -------------------------------------------------------
-- Stored Procedure: Add Vehicle
-- -------------------------------------------------------
CREATE OR REPLACE PROCEDURE add_vehicle(
    p_reg  VARCHAR, p_make VARCHAR, p_model VARCHAR,
    p_year INT,     p_owner INT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO Vehicle(registration_number, make, model, year, owner_id)
    VALUES (p_reg, p_make, p_model, p_year, p_owner);
END;
$$;

-- -------------------------------------------------------
-- View: Full Vehicle Details
-- -------------------------------------------------------
CREATE OR REPLACE VIEW VehicleDetails AS
SELECT
    v.vehicle_id,
    v.registration_number,
    v.make,
    v.model,
    v.year,
    c.name        AS owner_name,
    c.phone       AS owner_phone,
    c.email       AS owner_email
FROM Vehicle v
LEFT JOIN Customer c ON v.owner_id = c.customer_id;

-- -------------------------------------------------------
-- View: Active Violations
-- -------------------------------------------------------
CREATE OR REPLACE VIEW ActiveViolations AS
SELECT
    vl.violation_id,
    v.registration_number,
    vl.violation_date,
    vl.violation_type,
    vl.fine_amount,
    vl.status
FROM Violation vl
JOIN Vehicle v ON vl.vehicle_id = v.vehicle_id
WHERE vl.status = 'Unpaid';

-- -------------------------------------------------------
-- Seed Data
-- -------------------------------------------------------
INSERT INTO Users(username, password, role) VALUES
    ('admin',     'admin123',    'ADMIN'),
    ('workshop1', 'workshop123', 'WORKSHOP'),
    ('police1',   'police123',   'POLICE'),
    ('customer1', 'cust123',     'CUSTOMER');

INSERT INTO Customer(name, address, phone, email) VALUES
    ('John Mokoena',  '12 Main St, Maseru',  '+266 5111 0001', 'john@email.com'),
    ('Mary Ntšekhe',  '45 Kingsway, Maseru', '+266 5111 0002', 'mary@email.com'),
    ('David Letsie',  '7 Airport Rd',        '+266 5111 0003', 'david@email.com'),
    ('Sara Mofolo',   '33 Tšepong Ave',      '+266 5111 0004', 'sara@email.com'),
    ('Peter Tau',     '9 Industrial Rd',     '+266 5111 0005', 'peter@email.com');

INSERT INTO Vehicle(registration_number, make, model, year, owner_id) VALUES
    ('A 123 LS', 'Toyota',   'Corolla',   2018, 1),
    ('B 456 LS', 'Honda',    'Civic',     2020, 2),
    ('C 789 LS', 'Ford',     'Ranger',    2019, 3),
    ('D 321 LS', 'Hyundai',  'i20',       2021, 4),
    ('E 654 LS', 'Nissan',   'Navara',    2017, 5),
    ('F 987 LS', 'Toyota',   'Hilux',     2022, 1),
    ('G 111 LS', 'Chevrolet','Cruze',     2016, 2),
    ('H 222 LS', 'Kia',      'Sportage',  2023, 3),
    ('I 333 LS', 'Mazda',    'CX-5',      2020, 4),
    ('J 444 LS', 'BMW',      '3 Series',  2019, 5);

INSERT INTO ServiceRecord(vehicle_id, service_date, service_type, description, cost) VALUES
    (1, '2024-01-10', 'Oil Change',     'Routine oil change 5000km',    450.00),
    (2, '2024-02-15', 'Brake Service',  'Front brake pads replaced',   1200.00),
    (3, '2024-03-20', 'Tyre Rotation',  'All four tyres rotated',       300.00),
    (4, '2024-04-05', 'Full Service',   '100 000km major service',     3500.00),
    (5, '2024-05-12', 'Battery',        'Battery replaced 12V 65Ah',    800.00);

INSERT INTO PoliceReport(vehicle_id, report_date, report_type, description, officer_name) VALUES
    (1, '2024-06-01', 'Accident', 'Minor rear-end collision at roundabout', 'Sgt. Mosotho'),
    (3, '2024-07-14', 'Theft',    'Vehicle stolen from parking lot overnight', 'Cpl. Theko'),
    (5, '2024-08-22', 'Accident', 'Hit pothole on Main South One road',       'Sgt. Mosotho');

INSERT INTO Violation(vehicle_id, violation_date, violation_type, fine_amount, status) VALUES
    (1, '2024-09-01', 'Speeding',          500.00, 'Unpaid'),
    (2, '2024-09-15', 'Illegal Parking',   200.00, 'Paid'),
    (4, '2024-10-03', 'Running Red Light', 750.00, 'Unpaid'),
    (6, '2024-10-20', 'Speeding',          500.00, 'Unpaid'),
    (8, '2024-11-05', 'No Seatbelt',       150.00, 'Paid');
