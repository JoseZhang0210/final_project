package com.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableScheduling
public class HotelBackendApplication {

	public static void main(String[] args) {
		loadDotenv();
		SpringApplication.run(HotelBackendApplication.class, args);
	}

	/**
	 * 本地開發啟動時自動尋找並載入 .env 檔案中的環境變數
	 */
	private static void loadDotenv() {
		File[] candidateFiles = new File[] {
			new File(".env"),
			new File("../.env"),
			new File("hotel-backend/.env")
		};

		for (File file : candidateFiles) {
			if (file.exists() && file.isFile()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
							continue;
						}
						int eqIdx = line.indexOf('=');
						String key = line.substring(0, eqIdx).trim();
						String value = line.substring(eqIdx + 1).trim();
						if ((value.startsWith("\"") && value.endsWith("\"")) ||
							(value.startsWith("'") && value.endsWith("'"))) {
							if (value.length() >= 2) {
								value = value.substring(1, value.length() - 1);
							}
						}
						if (!key.isEmpty() && System.getProperty(key) == null && System.getenv(key) == null) {
							System.setProperty(key, value);
						}
					}
					System.out.println("[Dotenv] Loaded environment variables from " + file.getAbsolutePath());
					break;
				} catch (Exception e) {
					System.err.println("[Dotenv] Failed to load .env file: " + e.getMessage());
				}
			}
		}
	}

}
