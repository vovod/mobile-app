-- Xóa dữ liệu cũ nếu có để tránh trùng lặp
DELETE FROM videos;

-- Thêm video mẫu
INSERT INTO videos (title, thumbnail, video_url) VALUES ('English Alphabet Song', 'https://img.youtube.com/vi/75p-N9YKqNo/0.jpg', '75p-N9YKqNo');
INSERT INTO videos (title, thumbnail, video_url) VALUES ('Numbers 1-20 for Children', 'https://img.youtube.com/vi/_awKlEMyleA/0.jpg', '_awKlEMyleA');
INSERT INTO videos (title, thumbnail, video_url) VALUES ('Colors Song for Kids', 'https://img.youtube.com/vi/BGa3AqeqRy0/0.jpg', 'BGa3AqeqRy0');
INSERT INTO videos (title, thumbnail, video_url) VALUES ('Days of the Week Song', 'https://img.youtube.com/vi/mXMofxtDPUQ/0.jpg', 'mXMofxtDPUQ');
INSERT INTO videos (title, thumbnail, video_url) VALUES ('Months of the Year Song', 'https://img.youtube.com/vi/Fe9bnYRzFvk/0.jpg', 'Fe9bnYRzFvk');
