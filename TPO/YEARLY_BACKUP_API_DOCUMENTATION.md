# TPO Yearly Backup System - API Documentation

## Overview
The Yearly Backup System provides comprehensive data export functionality for Training and Placement Office (TPO) administrators. This system generates detailed Excel spreadsheets containing all placement-related data for academic year analysis and record-keeping.

## Features

### 📊 Comprehensive Data Export
- **Student Records**: Complete profiles, academic performance, placement status
- **Company Data**: Industry details, HR contacts, hiring statistics  
- **Placement Records**: All placements with packages, dates, and locations
- **Job Posts**: Historical job opportunities and requirements
- **Applications**: Student application history and status tracking
- **Analytics**: Department-wise and company-wise performance metrics
- **Package Distribution**: Salary statistics and trends

### 🔐 Security
- Admin-only access with JWT token authentication
- Secure data handling and export

### 📁 Export Formats
- **Excel (.xlsx)**: Multi-sheet workbook with detailed data
- **PDF**: Summary report (placeholder for future implementation)

## API Endpoints

### Get Yearly Backup
```http
GET /api7/dashboard/export/yearly-backup
```

#### Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `year` | integer | 2024 | Academic year to export |
| `format` | string | "excel" | Export format ("excel" or "pdf") |

#### Headers
```http
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Response
- **Success (200)**: Binary file download
- **Unauthorized (401)**: Invalid or missing authentication
- **Internal Server Error (500)**: Processing error

#### Example Request
```bash
curl -X GET "http://localhost:8080/api7/dashboard/export/yearly-backup?year=2024&format=excel" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -o yearly_backup_2024.xlsx
```

### Get Company Analytics
```http
GET /api7/dashboard/analytics/company
```

#### Description
Returns detailed company-wise analytics including hiring statistics and package information.

#### Response Example
```json
{
  "totalCompanies": 45,
  "companyHiringCount": {
    "TCS": 25,
    "Infosys": 18,
    "Google": 3
  },
  "companyAveragePackage": {
    "TCS": 3.5,
    "Infosys": 4.2,
    "Google": 18.5
  }
}
```

## Excel Workbook Structure

The generated Excel file contains the following sheets:

### 1. Yearly Summary
- Total students registered and placed
- Overall placement rate
- Average and highest packages
- Company participation statistics
- Key performance indicators

### 2. Students Data
- Student ID, Name, Contact details
- Department and Academic year
- Academic performance (SSC, HSC, CGPA)
- Verification status
- Placement details (if placed)

### 3. Companies Data
- Company ID, Name, Industry type
- HR contact information
- MNC status and location
- Job posts and hiring statistics

### 4. Placements Data
- Placement ID and date
- Student and company details
- Job title and package
- Location and offer type

### 5. Job Posts Data
- Job post details and requirements
- Application deadlines
- Package ranges and skills needed

### 6. Job Applications
- Application history and status
- Interview dates and outcomes
- Assessment scores

### 7. Department Analytics
- Department-wise placement rates
- Average packages by department
- Top performing departments

### 8. Company Analytics
- Hiring statistics per company
- Package distribution by company
- Selection rates

### 9. Package Distribution
- Salary range analysis
- Package distribution across departments
- Industry-wise compensation trends

## Data Filtering Logic

### Year-based Filtering
- **Students**: Filtered by academic year field
- **Companies**: Filtered by association date
- **Placements**: Filtered by placement date
- **Job Posts**: Filtered by application start date
- **Applications**: Filtered by application date

### Data Relationships
The system maintains data relationships through:
- Student ↔ User (authentication)
- Student ↔ JobApplication ↔ JobPost ↔ Company
- JobApplication ↔ Placement (placement records)

## Usage Examples

### Frontend Integration
```html
<!-- Include the yearly-backup.html in your static resources -->
<iframe src="/yearly-backup.html" width="100%" height="600px"></iframe>
```

### JavaScript API Call
```javascript
async function downloadYearlyBackup(year, format, token) {
    const response = await fetch(`/api7/dashboard/export/yearly-backup?year=${year}&format=${format}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    
    if (response.ok) {
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `yearly_backup_${year}.xlsx`;
        a.click();
        URL.revokeObjectURL(url);
    }
}
```

## System Requirements

### Dependencies
- Apache POI (5.2.3+) for Excel generation
- Spring Boot 3.4.2+
- MySQL database with TPO schema

### Database Tables Required
- `students` - Student records
- `companies` - Company information  
- `placements` - Placement records
- `job_posts` - Job postings
- `job_applications` - Application history
- `users` - Authentication data

## Error Handling

The system handles various error scenarios:

### Authentication Errors
- Missing authorization header
- Invalid JWT token
- Insufficient permissions (non-admin users)

### Data Errors
- Missing or invalid year parameter
- Empty datasets for specified year
- Database connection issues

### Processing Errors
- Excel generation failures
- Memory limitations for large datasets
- File I/O errors

## Performance Considerations

### Optimization Features
- Data caching for frequently accessed information
- Stream processing for large datasets
- Efficient database queries with filtering

### Recommended Usage
- Run during off-peak hours for large datasets
- Consider pagination for very large academic years
- Monitor memory usage for institutions with 10,000+ records

## Security Best Practices

### Access Control
- Admin-only endpoint access
- JWT token validation
- Role-based authorization

### Data Protection
- No sensitive data in logs
- Secure file handling
- Proper memory cleanup after export

## Future Enhancements

### Planned Features
- PDF report generation
- Scheduled automatic backups
- Email delivery of reports
- Custom date range selection
- Additional analytics sheets

### Extensibility
The system is designed to be easily extended with:
- Additional data sheets
- Custom filtering options
- Multiple export formats
- Integration with external systems

## Support

For technical support or feature requests:
- Check system logs for error details
- Verify database connectivity
- Ensure proper authentication setup
- Contact system administrator for access issues
