package com.wtl.collab.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FirestoreConfig {

//    @Value("${firebase.credentials.path}") // Use application.properties or environment variable
    private String credentialsPath = "src/main/resources/serviceAccountKey.json";

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) { // Prevent duplicate initialization
            Path filePath = Paths.get(credentialsPath);
            FileInputStream serviceAccount = new FileInputStream(filePath.toFile());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
