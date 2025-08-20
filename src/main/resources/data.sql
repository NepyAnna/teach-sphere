-- USERS
INSERT INTO users (id, username, email, password) VALUES
 (1, 'admin', 'admin@mail.com', '$2a$10$7QhRj2zsyjUzzW6EN0pOe.QK6.fJQkQHCpxrPL0.hI9jIDB5SbFJe'),
 (2, 'mentor1', 'mentor1@mail.com', '$2a$10$7QhRj2zsyjUzzW6EN0pOe.QK6.fJQkQHCpxrPL0.hI9jIDB5SbFJe'),
 (3, 'student1', 'student1@mail.com', '$2a$10$7QhRj2zsyjUzzW6EN0pOe.QK6.fJQkQHCpxrPL0.hI9jIDB5SbFJe');

-- USER ROLES (pivot)
INSERT INTO user_roles (user_id, role) VALUES
 (1, 'ADMIN'),
 (2, 'MENTOR'),
 (3, 'STUDENT');

INSERT INTO categories (id, name) VALUES
 (1, 'Programming'),
 (2, 'Science'),
 (3, 'Language');

-- SUBJECTS (довідник)
INSERT INTO subjects (id, name, category_id) VALUES
 (1, 'Mathematics',2),
 (2, 'Physics',2),
 (3, 'Java',1),
 (4, 'English',3);

-- MENTOR SUBJECTS (що викладає mentor1)
INSERT INTO mentor_subjects (id, mentor_id, subject_id, description) VALUES
 (1, 2, 1, 'Algebra and Geometry basics'),
 (2, 2, 3, 'Java and Spring Boot fundamentals');

