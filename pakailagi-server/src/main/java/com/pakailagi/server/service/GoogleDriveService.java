package com.pakailagi.server.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "PakaiLagi App";
    private static final String DRIVE_FOLDER_ID = "1cuKEezp-rkj0fiwL_Hr-vjEi9OtLwJZr";

    /**
     * Inisialisasi koneksi ke Google Drive menggunakan Service Account credentials.
     */
    private Drive getDriveService() throws IOException, GeneralSecurityException {
        InputStream in = null;
        String envCredentials = System.getenv("GOOGLE_DRIVE_CREDENTIALS");
        java.io.File renderSecretFile = new java.io.File("/etc/secrets/google-drive-credentials.json");

        if (renderSecretFile.exists()) {
            in = new java.io.FileInputStream(renderSecretFile);
        } else if (envCredentials != null && !envCredentials.trim().isEmpty()) {
            envCredentials = envCredentials.replace("\\n", "\n");
            in = new java.io.ByteArrayInputStream(envCredentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } else {
            in = getClass().getResourceAsStream("/google-drive-credentials.json");
        }

        if (in == null) {
            throw new IOException(
                    "Kredensial Google Drive tidak ditemukan di env GOOGLE_DRIVE_CREDENTIALS maupun resources!");
        }

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(in)
                .createScoped(Collections.singletonList(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Upload file gambar ke Google Drive dan kembalikan URL publik (webViewLink).
     *
     * @param file MultipartFile yang diterima dari request Android
     * @return URL publik file yang bisa diakses dengan link (webViewLink)
     * @throws IOException              jika terjadi error saat membaca file atau
     *                                  komunikasi dengan Drive
     * @throws GeneralSecurityException jika terjadi error keamanan saat
     *                                  inisialisasi transport
     */
    public String uploadImageToDrive(MultipartFile file) throws IOException, GeneralSecurityException {
        Drive driveService = getDriveService();

        // Siapkan metadata file (nama dan folder tujuan)
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(DRIVE_FOLDER_ID));

        // Siapkan konten file dari InputStream
        InputStreamContent mediaContent = new InputStreamContent(
                file.getContentType(),
                file.getInputStream());

        // Eksekusi upload ke Drive
        File uploadedFile = driveService.files()
                .create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        // Set permission agar file bisa diakses publik via link
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        driveService.permissions()
                .create(uploadedFile.getId(), permission)
                .execute();

        return uploadedFile.getWebViewLink();
    }
}
