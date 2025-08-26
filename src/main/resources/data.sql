-- Categories
INSERT INTO categories (name) VALUES ('Mathematics');
INSERT INTO categories (name) VALUES ('Science');
INSERT INTO categories (name) VALUES ('Programming');

-- Subjects
INSERT INTO subjects (name, category_id) VALUES ('Algebra', 1);
INSERT INTO subjects (name, category_id) VALUES ('Calculus', 1);
INSERT INTO subjects (name, category_id) VALUES ('Physics', 2);
INSERT INTO subjects (name, category_id) VALUES ('Chemistry', 2);
INSERT INTO subjects (name, category_id) VALUES ('Java', 3);
INSERT INTO subjects (name, category_id) VALUES ('Python', 3);

-- Users
INSERT INTO users (username, email, password) VALUES ('admin', 'admin@example.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2');
INSERT INTO users (username, email, password) VALUES ('mentor', 'mentor@example.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2');
INSERT INTO users (username, email, password) VALUES ('student', 'student@example.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2');

-- User Roles
INSERT INTO user_roles (user_id, role) VALUES (1, 'ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (2, 'MENTOR');
INSERT INTO user_roles (user_id, role) VALUES (3, 'STUDENT');

-- Profiles
INSERT INTO profiles (avatar_url, bio, location, photo_public_id, user_id) VALUES
('/images/default-profile.jpg', 'Math enthusiast', 'Kyiv', 'avatar1_pubid', 1);
INSERT INTO profiles (avatar_url, bio, location, photo_public_id, user_id) VALUES
('/images/default-profile.jpg', 'Science lover', 'Lviv', 'avatar2_pubid', 2);
INSERT INTO profiles (avatar_url, bio, location, photo_public_id, user_id) VALUES
('/images/default-profile.jpg', 'Aspiring programmer', 'Odessa', 'avatar3_pubid', 3);

-- Mentor Subjects
INSERT INTO mentor_subjects (user_id, subject_id) VALUES (1, 1);
INSERT INTO mentor_subjects (user_id, subject_id) VALUES (1, 2);
INSERT INTO mentor_subjects (user_id, subject_id) VALUES (2, 3);
INSERT INTO mentor_subjects (user_id, subject_id) VALUES (2, 4);
INSERT INTO mentor_subjects (user_id, subject_id) VALUES (2, 5);
