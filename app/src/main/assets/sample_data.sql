-- Xóa dữ liệu cũ nếu có để tránh trùng lặp khóa chính khi chạy lại script (tùy chọn)
DELETE FROM quiz;
DELETE FROM vocabulary;
DELETE FROM chapters;
DELETE FROM users;

-- Chèn dữ liệu cho bảng users
INSERT INTO users (username, password, score) VALUES ('admin', 'admin', 0);
INSERT INTO users (username, password, score) VALUES ('user', 'user', 100);
INSERT INTO users (username, password, score) VALUES ('huhu', '123', 100);


-- Chèn dữ liệu cho bảng chapters từ chapters.json
INSERT INTO chapters (id, name, image_path) VALUES (1, 'Alphabet', 'images/chapters/alphabet.png');
INSERT INTO chapters (id, name, image_path) VALUES (2, 'Colors', 'images/chapters/color.png');
INSERT INTO chapters (id, name, image_path) VALUES (3, 'Fruits', 'images/chapters/fruits.png');
INSERT INTO chapters (id, name, image_path) VALUES (4, 'Animals', 'images/chapters/animal.png');
INSERT INTO chapters (id, name, image_path) VALUES (5, 'Shapes', 'images/chapters/shapes.png');

-- Chèn dữ liệu cho bảng vocabulary từ vocabulary.json
-- Chapter 1: Alphabet
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('A', 1, 'images/alphabet/a.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('B', 1, 'images/alphabet/b.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('C', 1, 'images/alphabet/c.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('D', 1, 'images/alphabet/d.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('E', 1, 'images/alphabet/e.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('F', 1, 'images/alphabet/f.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('G', 1, 'images/alphabet/g.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('H', 1, 'images/alphabet/h.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('I', 1, 'images/alphabet/i.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('J', 1, 'images/alphabet/j.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('K', 1, 'images/alphabet/k.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('L', 1, 'images/alphabet/l.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('M', 1, 'images/alphabet/m.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('N', 1, 'images/alphabet/n.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('O', 1, 'images/alphabet/o.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('P', 1, 'images/alphabet/p.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('Q', 1, 'images/alphabet/q.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('R', 1, 'images/alphabet/r.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('S', 1, 'images/alphabet/s.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('T', 1, 'images/alphabet/t.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('U', 1, 'images/alphabet/u.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('V', 1, 'images/alphabet/v.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('W', 1, 'images/alphabet/w.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('X', 1, 'images/alphabet/x.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('Y', 1, 'images/alphabet/y.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('Z', 1, 'images/alphabet/z.png');

-- Chapter 2: Colors
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('black', 2, 'images/colors/black.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('blue', 2, 'images/colors/blue.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('brown', 2, 'images/colors/brown.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('green', 2, 'images/colors/green.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('orange', 2, 'images/colors/colorOrange.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('pink', 2, 'images/colors/pink.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('purple', 2, 'images/colors/purple.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('red', 2, 'images/colors/red.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('violet', 2, 'images/colors/Violet.jpg');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('white', 2, 'images/colors/white.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('yellow', 2, 'images/colors/yellow.png');

-- Chapter 3: Fruits
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('apple', 3, 'images/fruits/apple.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('avocado', 3, 'images/fruits/avocado.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('banana', 3, 'images/fruits/banana.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('cherry', 3, 'images/fruits/cherry.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('coconut', 3, 'images/fruits/coconut.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('grapefruit', 3, 'images/fruits/grapefruit.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('grapes', 3, 'images/fruits/grapes.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('kiwi', 3, 'images/fruits/kiwi.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('lemon', 3, 'images/fruits/lemon.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('lime', 3, 'images/fruits/lime.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('mango', 3, 'images/fruits/mango.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('melon', 3, 'images/fruits/melon.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('orange', 3, 'images/fruits/orange.png'); -- Note: 'orange' word also exists in Colors
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('papaya', 3, 'images/fruits/papaya.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('peach', 3, 'images/fruits/peach.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('pear', 3, 'images/fruits/pear.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('pineapple', 3, 'images/fruits/pineapple.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('plum', 3, 'images/fruits/plum.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('pomegranate', 3, 'images/fruits/pomegranate.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('raspberry', 3, 'images/fruits/raspberry.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('strawberry', 3, 'images/fruits/strawberry.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('watermelon', 3, 'images/fruits/watermelon.png');

-- Chapter 4: Animals
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('bear', 4, 'images/animals/bear.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('chicken', 4, 'images/animals/chicken.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('cow', 4, 'images/animals/cow.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('donkey', 4, 'images/animals/donkey.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('duck', 4, 'images/animals/duck.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('lion', 4, 'images/animals/lion.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('pig', 4, 'images/animals/pig.png');

-- Chapter 5: Shapes
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('circle', 5, 'images/shapes/circle.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('cross', 5, 'images/shapes/cross.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('diamond', 5, 'images/shapes/diamond.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('ellipse', 5, 'images/shapes/ellipse.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('heart', 5, 'images/shapes/heart.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('heptagon', 5, 'images/shapes/heptagon.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('hexagon', 5, 'images/shapes/hexagon.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('octagon', 5, 'images/shapes/octagon.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('rectangle', 5, 'images/shapes/rectangle.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('square', 5, 'images/shapes/square.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('star', 5, 'images/shapes/star.png');
INSERT INTO vocabulary (word, chapter_id, image_path) VALUES ('triangle', 5, 'images/shapes/triangle.png');

-- Chèn dữ liệu cho bảng quiz từ quiz.json
-- Lưu ý: Cột 'id' trong bảng quiz là AUTOINCREMENT, nên không cần chèn giá trị cho nó.
-- Giá trị 'id' từ quiz.json sẽ không được sử dụng trực tiếp cho cột id của bảng SQL.
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('He''s crying', 'He''s drinking', 'images/quiz/cry.png');
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('He''s drinking', 'He''s eating', 'images/quiz/drinking.png');
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('He''s eating', 'He''s reading', 'images/quiz/eating.png');
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('He''s reading', 'He''s eating', 'images/quiz/reading.png');
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('I''m brushing my teeth', 'I''m sleeping', 'images/quiz/brushurteeth.png');
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('I''m sleeping', 'He''s reading', 'images/quiz/sleeping.png');
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('This is a car', 'This is a cat', 'images/quiz/car.png');
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('This is a chair', 'This is a pen', 'images/quiz/chair.png');
INSERT INTO quiz (correct_answer, wrong_answer, image_path) VALUES ('This is a lion', 'This is a Tiger', 'images/quiz/lion.png');