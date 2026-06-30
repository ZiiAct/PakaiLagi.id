# Menggunakan mesin Java 17 (sesuai setting build.gradle kamu)
FROM eclipse-temurin:17-jdk-jammy

# Menentukan folder kerja di dalam server Render
WORKDIR /app

# Meng-copy seluruh file servermu ke dalam mesin Render
COPY . .

# Memastikan perintah gradlew bisa dieksekusi
RUN chmod +x ./gradlew

# Menjalankan proses build (mengubah kodingan menjadi file .jar)
RUN ./gradlew build -x test

# Menghapus file sisa (plain.jar) bawaan Spring Boot agar tidak bentrok
RUN rm -f build/libs/*-plain.jar

# Beri tahu Render bahwa aplikasi kita default berjalan di port 8080
EXPOSE 8080

# Perintah untuk menyalakan server
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]
