-- Create database and use it
DROP DATABASE IF EXISTS `course_genius_local`;
CREATE DATABASE IF NOT EXISTS `course_genius_local`;
USE `course_genius_local`;

-- Create users table
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    oid VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    username VARCHAR(100)
) AUTO_INCREMENT = 1;

-- Add my data if not in table
INSERT INTO user (oid, email, username)
SELECT '72ca0144-6e9b-4b3b-819a-370429b1a692', 'jrlockhart04@gmail.com', 'Jack Lockhart'
WHERE NOT EXISTS (SELECT 1 FROM user WHERE oid = '72ca0144-6e9b-4b3b-819a-370429b1a692');

-- Add test data only if table is empty
INSERT INTO user (oid, email, username)
SELECT '1', 'user1@gmail.com', 'user1'
WHERE NOT EXISTS (SELECT 1 FROM user WHERE oid = '1');

INSERT INTO user (oid, email, username)
SELECT '2', 'user2@gmail.com', 'user2'
WHERE NOT EXISTS (SELECT 1 FROM user WHERE oid = '2');

-- Create courses table
CREATE TABLE IF NOT EXISTS course (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id INT NOT NULL,
    credit_hours DECIMAL(3,1) NOT NULL DEFAULT 0.0,
    gpa DECIMAL(10,2),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Create assignment groups table
CREATE TABLE IF NOT EXISTS assignment_group (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    course_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

-- Create assignment table
CREATE TABLE IF NOT EXISTS assignment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    points_earned DECIMAL(10,2),
    points_possible DECIMAL(10,2),
    percentage_grade DECIMAL(5,2),
    assignment_group_id INT NOT NULL,
    FOREIGN KEY (assignment_group_id) REFERENCES assignment_group(id) ON DELETE CASCADE
);

-- Add test data only if table is empty
INSERT INTO course (name, user_id, credit_hours)
SELECT 'course1', id, 3.0 FROM user WHERE oid = '72ca0144-6e9b-4b3b-819a-370429b1a692'
AND NOT EXISTS (SELECT 1 FROM course WHERE name = 'course1');

INSERT INTO course (name, user_id, credit_hours)
SELECT 'course2', id, 4.0 FROM user WHERE oid = '72ca0144-6e9b-4b3b-819a-370429b1a692'
AND NOT EXISTS (SELECT 1 FROM course WHERE name = 'course2');

-- Add test assignment groups
INSERT INTO assignment_group (name, weight, course_id)
SELECT 'Homework', 30.00, id FROM course WHERE name = 'course1'
AND NOT EXISTS (SELECT 1 FROM assignment_group WHERE name = 'Homework' AND course_id = id);

INSERT INTO assignment_group (name, weight, course_id)
SELECT 'Exams', 70.00, id FROM course WHERE name = 'course1'
AND NOT EXISTS (SELECT 1 FROM assignment_group WHERE name = 'Exams' AND course_id = id);

-- Add test assignments
INSERT INTO assignment (name, points_earned, points_possible, percentage_grade, assignment_group_id)
SELECT 'HW1', 9.00, 10.00, 90.00, id FROM assignment_group WHERE name = 'Homework' AND course_id = (SELECT id FROM course WHERE name = 'course1')
AND NOT EXISTS (SELECT 1 FROM assignment WHERE name = 'HW1' AND assignment_group_id = id);

INSERT INTO assignment (name, points_earned, points_possible, percentage_grade, assignment_group_id)
SELECT 'Midterm', 85.00, 100.00, 85.00, id FROM assignment_group WHERE name = 'Exams' AND course_id = (SELECT id FROM course WHERE name = 'course1')
AND NOT EXISTS (SELECT 1 FROM assignment WHERE name = 'Midterm' AND assignment_group_id = id);