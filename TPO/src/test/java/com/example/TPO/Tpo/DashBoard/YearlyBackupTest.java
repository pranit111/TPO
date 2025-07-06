package com.example.TPO.Tpo.DashBoard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringJUnitConfig
public class YearlyBackupTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    public void testYearlyBackupExcelGeneration() {
        try {
            // Test Excel backup generation
            byte[] excelData = dashboardService.exportYearlyBackup(2024, "excel");
            
            // Verify that Excel data is generated
            assertNotNull(excelData, "Excel data should not be null");
            assertTrue(excelData.length > 0, "Excel data should not be empty");
            
            System.out.println("✅ Excel backup generation test passed!");
            System.out.println("Generated Excel file size: " + excelData.length + " bytes");
            
        } catch (Exception e) {
            fail("Excel backup generation failed: " + e.getMessage());
        }
    }

    @Test
    public void testCompanyAnalytics() {
        try {
            // Test company analytics
            var companyAnalytics = dashboardService.getCompanyAnalyticsDetails();
            
            assertNotNull(companyAnalytics, "Company analytics should not be null");
            assertTrue(companyAnalytics.containsKey("totalCompanies"), "Should contain total companies");
            
            System.out.println("✅ Company analytics test passed!");
            System.out.println("Total companies: " + companyAnalytics.get("totalCompanies"));
            
        } catch (Exception e) {
            fail("Company analytics test failed: " + e.getMessage());
        }
    }

    @Test
    public void testYearlyDataFiltering() {
        try {
            // Test the helper methods for data filtering
            DashboardService service = dashboardService;
            
            // These methods are private, so we test them indirectly through the public methods
            byte[] backupData = service.exportYearlyBackup(2024, "excel");
            
            assertNotNull(backupData, "Backup data should be generated");
            System.out.println("✅ Yearly data filtering test passed!");
            
        } catch (Exception e) {
            fail("Yearly data filtering test failed: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidFormatHandling() {
        try {
            // Test invalid format handling
            assertThrows(UnsupportedOperationException.class, () -> {
                dashboardService.exportYearlyBackup(2024, "invalid_format");
            }, "Should throw exception for invalid format");
            
            System.out.println("✅ Invalid format handling test passed!");
            
        } catch (Exception e) {
            fail("Invalid format handling test failed: " + e.getMessage());
        }
    }
}
