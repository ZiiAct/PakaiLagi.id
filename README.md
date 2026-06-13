# PakaiLagi.id

Proyek Tugas Pemrograman Berorientasi Objek (OOP) / Rekayasa Perangkat Lunak.

## Struktur Repository

Repository ini menggunakan struktur multi-module untuk memisahkan frontend (aplikasi mobile Android) dengan backend (layanan Spring Boot API):

* **[pakailagi-app/](file:///c:/Users/Indomie%20Ayam%20Bawang/Documents/item%20tugas/semester%204/PakaiLagi/pakailagi-app)**: Folder proyek Android Studio yang dikembangkan menggunakan Kotlin dan Jetpack Compose.
* **[pakailagi-server/](file:///c:/Users/Indomie%20Ayam%20Bawang/Documents/item%20tugas/semester%204/PakaiLagi/pakailagi-server)**: Folder proyek backend Spring Boot (Java 17, Gradle) yang menyediakan REST API untuk aplikasi mobile.

## Cara Menjalankan

### Backend (Spring Boot Server)
1. Buka folder [pakailagi-server/](file:///c:/Users/Indomie%20Ayam%20Bawang/Documents/item%20tugas/semester%204/PakaiLagi/pakailagi-server) menggunakan IDE pilihan Anda (misalnya **IntelliJ IDEA** atau **VS Code**).
2. Konfigurasikan koneksi database di file [application.properties](file:///c:/Users/Indomie%20Ayam%20Bawang/Documents/item%20tugas/semester%204/PakaiLagi/pakailagi-server/src/main/resources/application.properties).
3. Jalankan aplikasi menggunakan Gradle wrapper:
   ```bash
   ./gradlew bootRun
   ```

### Frontend (Android Studio App)
1. Buka folder [pakailagi-app/](file:///c:/Users/Indomie%20Ayam%20Bawang/Documents/item%20tugas/semester%204/PakaiLagi/pakailagi-app) menggunakan **Android Studio**.
2. Pastikan Android SDK yang dibutuhkan sudah terpasang.
3. Hubungkan perangkat emulator atau *physical device* untuk menjalankan aplikasi.
