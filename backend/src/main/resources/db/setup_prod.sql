-- Create database and use it
DROP DATABASE IF EXISTS `course_genius_prod`;
CREATE DATABASE IF NOT EXISTS `course_genius_prod`;
USE `course_genius_prod`;

-- Create users table
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    oid VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    username VARCHAR(100)
) AUTO_INCREMENT = 1;

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
