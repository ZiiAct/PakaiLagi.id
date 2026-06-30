package com.pakailagi.server.controller;

import com.pakailagi.server.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/hibah")
public class HibahReqController {

    @Autowired
    private GoogleDriveService driveService;

    /**
     * Endpoint untuk menerima upload gambar dari Android,
     * menyimpannya ke Google Drive, dan mengembalikan URL publik.
     *
     * @param file File gambar yang dikirim sebagai multipart
     * @return URL gambar di Google Drive jika berhasil, atau pesan error jika gagal
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadHibah(
            @RequestParam("file") MultipartFile file) {
        try {
            // Upload gambar ke Google Drive dan dapatkan URL publik
            String imageUrl = driveService.uploadImageToDrive(file);

            // Kembalikan hanya URL agar Android bisa langsung memakainya
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload gagal: " + e.getMessage());
        }
    }
}