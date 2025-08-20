SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE mentor_subjects;
TRUNCATE TABLE subjects;
TRUNCATE TABLE categories;
TRUNCATE TABLE profiles;
TRUNCATE TABLE users;
TRUNCATE TABLE user_roles;

SET FOREIGN_KEY_CHECKS = 1;
-- Categories
INSERT INTO categories (id, name) VALUES (1, 'Mathematics');
INSERT INTO categories (id, name) VALUES (2, 'Science');
INSERT INTO categories (id, name) VALUES (3, 'Programming');

-- Subjects
INSERT INTO subjects (id, name, category_id) VALUES (1, 'Algebra', 1);
INSERT INTO subjects (id, name, category_id) VALUES (2, 'Calculus', 1);
INSERT INTO subjects (id, name, category_id) VALUES (3, 'Physics', 2);
INSERT INTO subjects (id, name, category_id) VALUES (4, 'Chemistry', 2);
INSERT INTO subjects (id, name, category_id) VALUES (5, 'Java', 3);
INSERT INTO subjects (id, name, category_id) VALUES (6, 'Python', 3);


-- Users
INSERT INTO users (id, username, email, password) VALUES (1, 'admin', 'admin@example.com', '$2a$10$7QhRj2zsyjUzzW6EN0pOe.QK6.fJQkQHCpxrPL0.hI9jIDB5SbFJe');
INSERT INTO users (id, username, email, password) VALUES (2, 'mentor', 'mentor@example.com', '$2a$10$7QhRj2zsyjUzzW6EN0pOe.QK6.fJQkQHCpxrPL0.hI9jIDB5SbFJe');
INSERT INTO users (id, username, email, password) VALUES (3, 'student', 'student@example.com', '$2a$10$7QhRj2zsyjUzzW6EN0pOe.QK6.fJQkQHCpxrPL0.hI9jIDB5SbFJe');

-- User Roles
INSERT INTO user_roles (user_id, role) VALUES (1, 'ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (2, 'MENTOR');
INSERT INTO user_roles (user_id, role) VALUES (3, 'STUDENT');

-- Profiles
INSERT INTO profiles (id, avatar_url, bio, location, photo_public_id, user_id) VALUES
(1, '/images/default-profile.jpg', 'Math enthusiast', 'Kyiv', 'avatar1_pubid', 1),
(2, '/images/default-profile.jpg', 'Science lover', 'Lviv', 'avatar2_pubid', 2),
(3, '/images/default-profile.jpg', 'Aspiring programmer', 'Odessa', 'avatar3_pubid', 3);

-- Mentor Subjects
INSERT INTO mentor_subjects (id, user_id, subject_id) VALUES (1, 1, 1);
INSERT INTO mentor_subjects (id, user_id, subject_id) VALUES (2, 1, 2);
INSERT INTO mentor_subjects (id, user_id, subject_id) VALUES (3, 2, 3);
INSERT INTO mentor_subjects (id, user_id, subject_id) VALUES (4, 2, 4);
INSERT INTO mentor_subjects (id, user_id, subject_id) VALUES (5, 2, 5);
