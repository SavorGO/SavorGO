package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;

public class TokenManager {
    private static final String KEYSTORE_FILE = "keystore.jks";
    private static final String KEY_ALIAS = "aes_key";
    private static final String KEYSTORE_PASSWORD = "changeit";
    private static final String TOKEN_FILE_PATH = "src/main/resources/encrypted_token.txt";
    private static final long EXPIRATION_TIME_MS = 3600 * 1000; // 1 giờ (3600 giây)

    // Tạo KeyStore và lưu khóa AES (chỉ chạy lần đầu)
    public static void createAndStoreAESKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(null, null);

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();

        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter passwordParam = new KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray());
        keyStore.setEntry(KEY_ALIAS, secretKeyEntry, passwordParam);

        try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
            keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray());
        }

        System.out.println("Đã tạo và lưu khóa AES vào KeyStore.");
    }
    private static SecretKey getAESKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        File keystoreFile = new File(KEYSTORE_FILE);

        if (!keystoreFile.exists()) {
            System.err.println("Keystore file not found: " + KEYSTORE_FILE);
            return null;
        }

        try (FileInputStream fis = new FileInputStream(keystoreFile)) {
            keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
        }

        KeyStore.ProtectionParameter passwordParam = new KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray());
        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, passwordParam);

        return secretKeyEntry != null ? secretKeyEntry.getSecretKey() : null;
    }


 // Mã hóa token cùng với timestamp
    public static String encryptToken(String token) throws Exception {
        SecretKey secretKey = getAESKey();
        if (secretKey == null) {
            throw new IllegalStateException("AES Key is missing. Please initialize the KeyStore.");
        }

        long timestamp = System.currentTimeMillis();
        String tokenWithTimestamp = timestamp + ":" + token;

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(tokenWithTimestamp.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Giải mã token và kiểm tra thời gian hết hạn
    public static String decryptToken(String encryptedToken) throws Exception {
        SecretKey secretKey = getAESKey();
        if (secretKey == null) {
            throw new IllegalStateException("AES Key is missing. Please initialize the KeyStore.");
        }

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));

        String tokenWithTimestamp = new String(decryptedBytes, StandardCharsets.UTF_8);
        String[] parts = tokenWithTimestamp.split(":", 2);

        if (parts.length != 2) {
            throw new IllegalStateException("Token is not valid");
        }

        long timestamp = Long.parseLong(parts[0]);
        long currentTime = System.currentTimeMillis();

        if (currentTime - timestamp > EXPIRATION_TIME_MS) {
            throw new IllegalStateException("Token has expired");
        }

        return parts[1]; 
    }


    // Lưu token đã mã hóa vào file
    public static void writeEncryptedTokenToFile(String encryptedToken) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TOKEN_FILE_PATH))) {
            writer.write(encryptedToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Đọc token đã mã hóa từ file
    public static String readEncryptedTokenFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TOKEN_FILE_PATH))) {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            // Tạo KeyStore nếu chưa có
            if (!new File(KEYSTORE_FILE).exists()) {
                createAndStoreAESKey();
            }

            // Giả sử đây là ID Token nhận từ Firebase
            String idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

            // Mã hóa và lưu token
            String encryptedToken = encryptToken(idToken);
            writeEncryptedTokenToFile(encryptedToken);
            System.out.println("Token save: " + encryptedToken);

            // Đọc và giải mã token
            try {
                String decryptedToken = decryptToken(readEncryptedTokenFromFile());
                System.out.println("Token hợp lệ, nội dung: " + decryptedToken);
            } catch (IllegalStateException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteTokenFile() {
        File tokenFile = new File(TOKEN_FILE_PATH);
        if (tokenFile.exists()) {
            if (tokenFile.delete()) {
                System.out.println("File token đã được xóa.");
            } else {
                System.out.println("Không thể xóa file token.");
            }
        } else {
            System.out.println("File token không tồn tại.");
        }
    }

}
