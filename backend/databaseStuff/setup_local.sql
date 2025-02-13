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
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Add test data only if table is empty
INSERT INTO course (name, user_id)
SELECT 'course1', id FROM user WHERE oid = '72ca0144-6e9b-4b3b-819a-370429b1a692'
AND NOT EXISTS (SELECT 1 FROM course WHERE name = 'course1');

INSERT INTO course (name, user_id)
SELECT 'course2', id FROM user WHERE oid = '72ca0144-6e9b-4b3b-819a-370429b1a692'
AND NOT EXISTS (SELECT 1 FROM course WHERE name = 'course2');

INSERT INTO course (name, user_id)
SELECT 'course3', id FROM user WHERE oid = '1'
AND NOT EXISTS (SELECT 1 FROM course WHERE name = 'course3');

INSERT INTO course (name, user_id)
SELECT 'course4', id FROM user WHERE oid = '1'
AND NOT EXISTS (SELECT 1 FROM course WHERE name = 'course4');

-- Create assignment table
CREATE TABLE IF NOT EXISTS assignment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    weight DECIMAL(5, 2),
    grade DECIMAL(5, 2),
    course_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

-- Add test data only if table is empty
INSERT INTO assignment (name, weight, grade, course_id)
SELECT 'Assignment 1', 20.00, 85.00, id FROM course WHERE name = 'course1'
AND NOT EXISTS (SELECT 1 FROM assignment WHERE name = 'Assignment 1' AND course_id = id);

INSERT INTO assignment (name, weight, grade, course_id)
SELECT 'Assignment 2', 100.00, 90.00, id FROM course WHERE name = 'course2'
AND NOT EXISTS (SELECT 1 FROM assignment WHERE name = 'Assignment 2' AND course_id = id);

INSERT INTO assignment (name, weight, grade, course_id)
SELECT 'Assignment 3', 30.00, 75.00, id FROM course WHERE name = 'course3'
AND NOT EXISTS (SELECT 1 FROM assignment WHERE name = 'Assignment 3' AND course_id = id);

INSERT INTO assignment (name, weight, grade, course_id)
SELECT 'Assignment 4', 50.00, 95.00, id FROM course WHERE name = 'course4'
AND NOT EXISTS (SELECT 1 FROM assignment WHERE name = 'Assignment 4' AND course_id = id);