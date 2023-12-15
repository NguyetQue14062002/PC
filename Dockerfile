# Sử dụng một hình ảnh cơ sở có hỗ trợ Java, ví dụ: OpenJDK
FROM openjdk:21

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép tệp pom.xml vào thư mục làm việc
COPY pom.xml .

# Tải các phụ thuộc Maven
RUN ["mvn", "dependency:go-offline"]

# Sao chép tất cả các tệp còn lại vào thư mục làm việc
COPY . .

# Xây dựng ứng dụng Java
RUN ["mvn", "package"]

# Chạy ứng dụng
CMD ["java", "-jar", "target/your-app.jar"]