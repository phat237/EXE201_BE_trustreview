package com.trustreview.trustreview.API;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RestController
public class TestController {

    @GetMapping("/test-conn")
    public ResponseEntity<String> testConnection() {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=echoeval;encrypt=true;trustServerCertificate=true",
                    "sa",
                    "trantanphat"
            );

            if (conn != null && !conn.isClosed()) {
                return ResponseEntity.ok("✅ Connected to: " + conn.getMetaData().getURL());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Connection is closed or null.");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Connection failed: " + e.getMessage());
        }
    }
}
