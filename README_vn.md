
Repeat
======

Đây là chương trình để lưu và chạy lại các tương tác trên bàn phím/chuột, tạo macro bằng cách lập trình, và nhiều tính năng nâng cao khác. Chương trình này chạy trên Windows, Linux, và cả OSX.

[![SourceForge](https://sourceforge.net/sflogo.php?type=11&group_id=3172773)](https://sourceforge.net/projects/repeat1/)
========================================================================================================================

Demo
====

Lưu ý là các gifs sau đều được chạy ở tốc độ thực (1x speedup).
![Word expansion demo](https://raw.githubusercontent.com/repeats/Repeat/master/demo_key_expansion.gif)

![Recording & playback](https://raw.githubusercontent.com/repeats/Repeat/master/demo_record_replay.gif)

![Mouse gesture activation](https://raw.githubusercontent.com/repeats/Repeat/master/demo_gesture.gif)

![Multi clipboard](https://raw.githubusercontent.com/repeats/Repeat/master/demo_multi_clipboard.gif)

![Simple manually build task](https://raw.githubusercontent.com/repeats/Repeat/master/demo_manually_build_task.gif)


[Viết hoa tất cả các chữ tô đậm](https://youtu.be/wICRVQNVNSM)

[Sửa lỗi đánh máy](https://youtu.be/oCCyYbj198U)

[Tạo clipboard với nhiều ngăn](https://youtu.be/dqNckwIPjCE)

[Marco để chơi Collapse 3](https://youtu.be/19i5ZlZvsAc)

[Marco để chơi Plants vs Zombies](https://youtu.be/7pQHcFfrpDI)

Các tính năng chính
===================
1. Lưu và chạy lại các task trên máy tính.
2. Lưu các task và chạy lại chúng sau khi khởi động lại.
3. Viết và compile các task bằng **text editor tùy chọn** bằng Python hoặc Java. Cơ bản là lập trình được là chạy được.
4. Đặt hotkey bất kỳ để kích hoạt task đã lưu. 
5. Đặt chuyển động chuột (VD như vẽ vòng tròn) để kích hoạt task đã lưu.
6. Lưu và chạy các task trên một máy khác (hoặc một nhóm máy khác) trong cùng mạng.
7. Quản lý các task (task được lưu hoặc được biên dịch từ code).

Lưu ý
==========
1. Đây không phải chương trình quản lý mật khẩu như LastPass. Đoạn mã lệnh viết sẽ không được encrypt.
2. Đây là chương trình chạy code của người viết. Sử dụng tính năng compile và chạy lại cẩn thận không sẽ dễ bị treo máy.

Hướng dẫn sử dụng và các tính năng nâng cao
===========================================

Xem thêm tại [trang wiki](https://github.com/repeats/Repeat/wiki).

Yêu cầu tối thiểu
============

JDK 8.0 hoặc mới hơn. JDK của Oracle hay OpenJDK đều được.

Nếu muốn viết và chạy các task bằng Python thì phải có Python 3.

Trên Windows không cần đặc quyền admin nào.

Trên Linux, chương trình chỉ chạy trên X11. Nếu hệ điều hành chạy Wayland chương trình sẽ không nhận các tương tác qua chuột và bàn phím.

Trên OSX, chương trình cần "accessibility permission" để nhận các tương tác ở level thấp. Chỉnh quyền này bằng cách vào System Preference --> Security & Privacy --> Accessibilty --> Privacy.

Cài đặt
============
Download [file JAR mới nhất](https://github.com/repeats/Repeat/releases/latest), đặt file jar vào một thư mục **riêng** rồi chạy bằng Java! Chương trình có thể cần một số đặc quyền vì nó theo dõi các hoạt động của chuột và bàn phím.

Cách tốt nhất để chạy trên Linux/OSX là dùng terminal:

     $cd <jar_directory>
     $java -jar Repeat.jar

**Lưu ý:** Thư mục chứa JAR file **không được** có ký tự trắng (space) trong tên.

Từ bản **Java 9** trở lên, file JAR phải được chạy từ JDK (thay vì từ JRE) thì mới compile được code. Cái hack xài ở Java 8 và trước đó để chỉnh Java home không hoạt động ở Java 9 về sau.

Các câu hỏi thường gặp
======================

## Chương trình này khác [AutoHotkey](https://autohotkey.com/) or [AutoKey](https://github.com/autokey/autokey) chỗ nào?
1. Repeat chạy trên các hệ điều hành lớn, miễn có Java và không phải [headless](https://en.wikipedia.org/wiki/Headless_software). AutoHotkey chỉ chạy trên Windows, và AutoKey chỉ trạy trên Linux. Repeat chạy trên cả Windows, Linux, và OSX. **Đặc biệt là các đoạn code viết ra có thể được xài lại trên các hệ điều hành khác nhau.**
2. Vì chương trình là đoạn code, nếu bạn viết code được (Python, Java, hay C#) là chạy được, không bị giới hạn bởi API. Bạn không phải học ngôn ngữ lập trình của AutoHotkey. Code bằng các ngôn ngữ lập trình lớn sẽ dễ hơn nếu bạn có sẵn kinh nghiệm, và có nhiều ví dụ từ Stackoverflow hơn nếu không biết làm cái gì đó.

## Tại sao chương trình này chỉ chạy được trên máy không phải headless?
Hệ thống headless không có bàn phím và chuột, vì vậy không thể theo dõi và lưu lại, hay điều khiển hoạt động của bàn phím và chuột.

## Module C# có chạy trên Linux được không?
Module C# chỉ chạy trên Windows. Nếu không phải Windows thì module C# sẽ không khởi động.

## Tại sao chương trình này không host trên trang web được? Nếu được thì đỡ mất công tải file JAR về máy và chạy.
Chương trình này theo dõi các hoạt động của bàn phím và chuột, và có thể tự động chèn thêm các hoạt động của bàn phím và chuột. Nếu mà trang web làm được mấy cái này từ trình duyệt, các hacker đã thu thập mật khẩu và thông tin ngân hàng của người ta lâu rồi.

Các library sử dụng trong Repeat
================================
1. [Simple Native Hooks](https://github.com/repeats/SimpleNativeHooks)
2. [Argo JSON](http://argo.sourceforge.net)
3. [Jama - Library ma trận](https://math.nist.gov/javanumerics/jama/)
4. [Apache HttpComponents Core](https://hc.apache.org/httpcomponents-core-ga/index.html)
5. [Apache HttpClient](https://hc.apache.org/httpcomponents-client-ga/index.html)
6. [FreeMarker Java Template Engine](https://freemarker.apache.org/)
7. [Light Bootstrap Dashboard](https://creative-tim.com/product/light-bootstrap-dashboard)
8. [CodeMirror: text editor trên trình duyệt bằng Javascript](http://codemirror.net)
9. [Chart.js: vẽ đồ thị bằng JavaScript](https://chartjs.org)
