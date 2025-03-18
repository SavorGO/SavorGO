package iuh.fit.se.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) { // Kiểm tra xem Firebase đã khởi tạo chưa
                InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("Không tìm thấy serviceAccountKey.json trong resources");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase đã được khởi tạo!");
            } else {
                log.info("⚠️ Firebase đã được khởi tạo trước đó, không khởi tạo lại.");
            }
        } catch (IOException e) {
            log.error("Lỗi khi khởi tạo Firebase: ", e);
        }
    }
}
