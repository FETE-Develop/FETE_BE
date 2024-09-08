package fete.be.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Bean
    FirebaseApp initFirebase() throws IOException {
        InputStream serviceAccount;

        // 환경 변수를 통해 json 문자열 받기
        String firebaseConfig = System.getenv("FIREBASE_CONFIG");

        if (firebaseConfig != null) {
            // 환경 변수로 받은 json 문자열 읽기
            serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
        } else {
            // 로컬에서 json 파일 읽기
            serviceAccount = new ClassPathResource("firebase/firebase-adminsdk.json").getInputStream();
        }


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
