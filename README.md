# Learning English App (Ứng dụng học tiếng Anh)

## Giới thiệu (Introduction)

Đây là một ứng dụng di động Android được thiết kế để giúp người dùng học tiếng Anh thông qua nhiều tính năng tương tác khác nhau. Ứng dụng này phù hợp cho người mới bắt đầu và cả những người muốn cải thiện kỹ năng tiếng Anh của mình. Dự án được phát triển bởi Nhóm 13.

## Tính năng (Features)

Ứng dụng bao gồm các tính năng dành cho cả người dùng thông thường và quản trị viên.

### Tính năng người dùng (User Features)
* **Đăng nhập & Đăng ký (Login & Registration):** Người dùng có thể tạo tài khoản mới hoặc đăng nhập bằng tài khoản hiện có.
* **Trang chủ & Menu chính (Homepage & Main Menu):** Giao diện thân thiện để điều hướng các tính năng học tập.
* **Học theo chủ đề/chương (Learning by Chapters/Topics):** Nội dung học được tổ chức thành các chương/chủ đề.
* **Từ điển (Dictionary):** Tra cứu từ vựng tiếng Anh với nghĩa và ví dụ.
    * Quản lý từ vựng đã học.
* **Trò chơi tương tác (Interactive Games):**
    * **Đoán hình ảnh (Guess the Image):** Học từ vựng qua hình ảnh.
    * **Nhìn và Chọn (See and Choose):** Chọn câu trả lời đúng dựa trên gợi ý.
    * Nhiều trò chơi khác để luyện tập kỹ năng.
* **Bài học qua Video (Video Lessons):**
    * Xem danh sách video bài học.
    * Phát video trực tiếp trong ứng dụng.
* **Bài kiểm tra (Quizzes):**
    * Làm các bài kiểm tra trắc nghiệm để đánh giá kiến thức.
    * Xem điểm số và kết quả sau khi hoàn thành.
* **Chatbot:** Luyện tập giao tiếp tiếng Anh với chatbot thông minh.
* **Cài đặt (Settings):** Tùy chỉnh các thiết lập ứng dụng.

### Tính năng quản trị viên (Admin Features)
* **Trang chủ quản trị (Admin Homepage):** Bảng điều khiển dành cho quản trị viên.
* **Quản lý người dùng (User Management):**
    * Xem danh sách người dùng.
    * Xem chi tiết và quản lý thông tin người dùng.
* **Quản lý nội dung (Content Management):**
    * **Quản lý chương học (Manage Chapters):** Thêm, sửa, xóa các chương học.
    * **Quản lý bài kiểm tra (Manage Quizzes):** Tạo và quản lý các bài kiểm tra, câu hỏi.
    * **Quản lý video (Manage Videos):** Thêm, sửa, xóa các video bài học.
    * **Quản lý từ vựng (Manage Words/Vocabulary):** Thêm, sửa, xóa từ vựng trong từ điển.
* **Xem thống kê (View Statistics):** Theo dõi số liệu thống kê liên quan đến hoạt động của ứng dụng và người dùng.

## Công nghệ sử dụng (Tech Stack)

* **Nền tảng (Platform):** Android
* **Ngôn ngữ (Language):** Java
* **Cơ sở dữ liệu (Database):** SQLite (lưu trữ cục bộ trên thiết bị)
* **Giao diện người dùng (UI):** XML Layouts
* **Định dạng dữ liệu (Data Formats):**
    * JSON (cho dữ liệu ban đầu như chương học, bài kiểm tra, từ vựng - lưu trong `assets/data`)
    * SQL (cho dữ liệu mẫu - lưu trong `assets`)
* **Thư viện (Libraries - Dự đoán/Predicted):**
    * AndroidX Libraries (AppCompat, RecyclerView, ConstraintLayout, etc.)
    * Material Components for Android (cho các thành phần UI hiện đại)
    * Thư viện phát video YouTube (ví dụ: YouTube Android Player API hoặc giải pháp thay thế)
    * (các thư viện khác cho Chatbot, xử lý ảnh, v.v.)

## Cấu trúc dự án (Project Structure)

Dự án được tổ chức theo cấu trúc chuẩn của một ứng dụng Android:

```text
mobile-app-main/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/nhom13/learningenglishapp/  
│   │   │   │   ├── activity/                           
│   │   │   │   │   ├── admin/                     
│   │   │   │   │   └── user/                       
│   │   │   │   ├── database/                    
│   │   │   │   │   ├── dao/                       
│   │   │   │   │   └── models/                     
│   │   │   │   └── utils/                           
│   │   │   ├── res/                                
│   │   │   │   ├── layout/                         
│   │   │   │   ├── drawable/                       
│   │   │   │   ├── values/                         
│   │   │   │   ├── xml/                            
│   │   │   │   ├── raw/                            
│   │   │   │   ├── anim/                           
│   │   │   │   └── mipmap/                         
│   │   │   ├── assets/                             
│   │   │   │   ├── data/                           
│   │   │   │   └── *.sql                           
│   │   │   └── AndroidManifest.xml                 
│   │   ├── test/                                 
│   │   └── androidTest/                          
│   └── build.gradle                              
├── gradle/                                         
│   └── wrapper/
│       └── gradle-wrapper.properties             
├── build.gradle                                    
├── gradle.properties                               
├── local.properties                                
└── README.md               

## Yêu cầu và Cài đặt (Requirements and Setup)

### Yêu cầu (Requirements)
* **Android Studio:** Phiên bản mới nhất được khuyến nghị (ví dụ: Android Studio Hedgehog trở lên).
* **Android SDK:**
    * SDK Platforms: API Level phù hợp (ví dụ: MinSDK 21+, TargetSDK 33+ - *kiểm tra `app/build.gradle` để biết chi tiết chính xác*).
    * SDK Tools: Android SDK Build-Tools, Android Emulator, Android SDK Platform-Tools.
* **Java Development Kit (JDK):** Thường được tích hợp sẵn với Android Studio, hoặc có thể cài đặt riêng (ví dụ: JDK 11 hoặc JDK 17).
* **Git:** Để clone repository.

### Cài đặt và Chạy ứng dụng (Setup and Run)
1.  **Clone Repository:**
    ```bash
    git clone https://github.com/vovod/mobile-app
    cd mobile-app-main
    ```
2.  **Mở bằng Android Studio:**
    * Mở Android Studio.
    * Chọn "Open an Existing Project" và trỏ đến thư mục `mobile-app-main` đã clone.
3.  **Cấu hình Android SDK:**
    * Android Studio thường sẽ nhắc bạn cài đặt SDK còn thiếu. Bạn cũng có thể quản lý SDK thông qua SDK Manager (Tools > SDK Manager).
    * Đảm bảo đường dẫn SDK được cấu hình đúng trong `local.properties` (thường Android Studio tự động quản lý file này).
4.  **Đồng bộ Gradle (Gradle Sync):**
    * Android Studio sẽ tự động đồng bộ dự án với các file Gradle. Quá trình này sẽ tải về các dependencies cần thiết.
    * Nếu không tự động, bạn có thể kích hoạt thủ công (File > Sync Project with Gradle Files hoặc biểu tượng con voi với mũi tên xanh).
5.  **Chạy ứng dụng (Run the App):**
    * Chọn một thiết bị Android ảo (Emulator) đã được cấu hình trong AVD Manager (Tools > AVD Manager).
    * Hoặc kết nối một thiết bị Android thật với máy tính (đảm bảo đã bật USB Debugging trên thiết bị).
    * Nhấn nút "Run 'app'" (biểu tượng tam giác màu xanh) trên thanh công cụ của Android Studio hoặc Shift+F10.

## Tài nguyên dự án (Project Resources)

Dự án sử dụng đa dạng các loại tài nguyên để xây dựng giao diện và cung cấp nội dung:

* **Giao diện người dùng (UI Layouts):**
    * Được định nghĩa bằng các file XML trong thư mục `app/src/main/res/layout/`.
    * Ví dụ: `activity_main.xml`, `item_chapter.xml`, `dialog_add_quiz.xml`.
* **Hình ảnh và Icons (Images and Icons):**
    * **Drawables:** Các tài nguyên đồ họa như hình nền tùy chỉnh, hình dạng (shapes), vector drawables (ví dụ: `icon_delete.xml`, `bg_user_message_bubble.xml`) được lưu trữ trong `app/src/main/res/drawable/`.
    * **Mipmap:** Chủ yếu chứa các icon launcher của ứng dụng (ví dụ: `ic_launcher.xml`, `ic_launcher_round.xml`) cho các mật độ màn hình khác nhau, nằm trong các thư mục `app/src/main/res/mipmap-*/`.
* **Chuỗi ký tự (Strings):**
    * Tất cả các chuỗi văn bản hiển thị trong ứng dụng được tập trung tại `app/src/main/res/values/strings.xml` để dễ dàng quản lý và bản địa hóa.
* **Màu sắc (Colors):**
    * Các giá trị màu được định nghĩa trong `app/src/main/res/values/colors.xml`.
* **Chủ đề và Kiểu (Themes and Styles):**
    * Chủ đề chính của ứng dụng và các kiểu giao diện tùy chỉnh được xác định trong `app/src/main/res/values/themes.xml` 
* **Animations:**
    * Các file XML định nghĩa animations (ví dụ: slide, fade) được đặt trong `app/src/main/res/anim/` 
* **Dữ liệu tĩnh (Static Data):**
    * **JSON:** Dữ liệu ban đầu cho các chương học, bài kiểm tra, từ vựng được lưu dưới dạng file JSON trong `app/src/main/assets/data/` (ví dụ: `chapters.json`, `quiz.json`, `vocabulary.json`).
    * **Raw JSON:** File JSON thô khác như `travelling.json` nằm trong `app/src/main/res/raw/`.
    * **SQL:** Các file SQL chứa dữ liệu mẫu (`sample_data.sql`, `sample_videos.sql`) để khởi tạo cơ sở dữ liệu, nằm trong `app/src/main/assets/`.
* **Cấu hình XML (XML Configurations):**
    * Các file cấu hình bổ sung như quy tắc sao lưu (`backup_rules.xml`) và quy tắc trích xuất dữ liệu (`data_extraction_rules.xml`) nằm trong `app/src/main/res/xml/`.
* **Âm thanh (Audio Files - nếu có/if any):**
    * Nếu ứng dụng sử dụng các file âm thanh tùy chỉnh (nhạc nền, hiệu ứng âm thanh), chúng có thể được đặt trong `app/src/main/res/raw/` hoặc `app/src/main/assets/`. 

## Cách sử dụng (Usage)

1.  **Khởi chạy ứng dụng:** Mở ứng dụng "Learning English App" trên thiết bị của bạn.
2.  **Đăng ký/Đăng nhập:**
    * Nếu bạn là người dùng mới, hãy đăng ký một tài khoản.
    * Nếu đã có tài khoản, hãy đăng nhập.
3.  **Sử dụng các tính năng:**
    * **Người dùng:** Sau khi đăng nhập, bạn sẽ được chuyển đến màn hình chính của người dùng. Tại đây, bạn có thể chọn các mục như "Học từ vựng", "Xem video", "Làm bài kiểm tra", "Chơi game", hoặc "Chat với bot".
    * **Quản trị viên:** Nếu đăng nhập bằng tài khoản quản trị, bạn sẽ có quyền truy cập vào các tính năng quản lý nội dung và người dùng.

## Quản lý dữ liệu (Data Management)

* **Dữ liệu ban đầu:** Các dữ liệu như danh sách chương, câu hỏi trắc nghiệm, và từ vựng cơ bản được tải từ các file JSON nằm trong thư mục `app/src/main/assets/data/` và `app/src/main/res/raw/`.
* **Dữ liệu mẫu:** Một số dữ liệu video mẫu và dữ liệu khác được khởi tạo từ các file SQL trong `app/src/main/assets/`.
* **Dữ liệu người dùng:** Thông tin tài khoản người dùng, tiến trình học tập, và kết quả bài kiểm tra được lưu trữ cục bộ trong cơ sở dữ liệu SQLite trên thiết bị của người dùng.

## Thành viên nhóm (Team Members - Dự đoán/Predicted)

*  Nhóm 13 (Nhom 13)
    * Hồ Trọng Nhật Minh
    * Nguyễn Hải Dương
    * Lê Đình Hảo
    * Nguyễn Tất Bình
    * Lê Văn Duy
