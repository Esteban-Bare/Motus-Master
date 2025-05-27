-- (password is 'password123')
INSERT INTO users (username, password, email) VALUES
                                                  ('player1', '$2a$10$EA3O2M3MhvKg5/QLwzIkAu5b9/ZrPgrdlQ2mE/A3XJu48Afv3CDzG', 'player1@email.com'),
                                                  ('player2', '$2a$10$EA3O2M3MhvKg5/QLwzIkAu5b9/ZrPgrdlQ2mE/A3XJu48Afv3CDzG', 'player2@email.com'),
                                                  ('player3', '$2a$10$EA3O2M3MhvKg5/QLwzIkAu5b9/ZrPgrdlQ2mE/A3XJu48Afv3CDzG', 'player3@email.com'),
                                                  ('champion', '$2a$10$EA3O2M3MhvKg5/QLwzIkAu5b9/ZrPgrdlQ2mE/A3XJu48Afv3CDzG', 'champion@email.com'),
                                                  ('gamer123', '$2a$10$EA3O2M3MhvKg5/QLwzIkAu5b9/ZrPgrdlQ2mE/A3XJu48Afv3CDzG', 'gamer123@email.com');

INSERT INTO words (word, length, difficulty) VALUES
                                                ('JAVA', 4, 1),
                                                ('PYTHON', 5, 1),
                                                ('DOCKER', 5, 2),
                                                ('JAVASCRIPT', 10, 3),
                                                ('FRAMEWORK', 9, 3),
                                                ('DATABASE', 8, 2),
                                                ('ALGORITHM', 9, 4),
                                                ('ARCHITECTURE', 12, 4),
                                                ('PROGRAMMING', 11, 3),
                                                ('COMPUTER', 8, 2);


-- (random scores between 50-1000)
INSERT INTO scores (score, user_id, word_id, date_time) VALUES
                                                            (850, 1, 1, '2024-02-15 10:30:00'),
                                                            (920, 1, 3, '2024-02-16 11:20:00'),
                                                            (750, 2, 2, '2024-02-15 14:15:00'),
                                                            (680, 2, 4, '2024-02-17 16:45:00'),
                                                            (950, 3, 5, '2024-02-18 09:30:00'),
                                                            (590, 3, 6, '2024-02-19 13:20:00'),
                                                            (780, 4, 7, '2024-02-20 15:10:00'),
                                                            (830, 4, 8, '2024-02-21 17:30:00'),
                                                            (990, 5, 9, '2024-02-22 12:00:00'),
                                                            (880, 5, 10, '2024-02-23 14:40:00'),
                                                            (760, 1, 5, '2024-02-24 10:15:00'),
                                                            (840, 2, 7, '2024-02-25 11:45:00'),
                                                            (910, 3, 8, '2024-02-26 16:20:00'),
                                                            (730, 4, 9, '2024-02-27 13:50:00'),
                                                            (870, 5, 1, '2024-02-28 15:30:00');