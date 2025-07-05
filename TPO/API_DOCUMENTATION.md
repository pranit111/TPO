# TPO Admin Dashboard Backend API Documentation

## Overview

This document provides comprehensive documentation for the enhanced TPO Admin Dashboard backend API. The backend has been transformed from a static, hardcoded implementation to a fully dynamic, data-driven administrative interface.

## Base URL

All API endpoints are prefixed with `/api7/dashboard`

## Authentication

All endpoints require JWT token authentication via the `Authorization` header:
```
Authorization: Bearer <jwt-token>
```

Only users with `TPO_Role.ADMIN` role can access these endpoints.

## Core Dashboard Endpoints

### 1. Get Dashboard Data
**GET** `/api7/dashboard`

Returns complete dashboard data with real-time statistics.

**Response:**
```json
{
  "studentData": {
    "total": 150,
    "placed": 120,
    "remaining": 30,
    "placementRate": 80.0,
    "averagePackage": 8.5
  },
  "companiesData": {
    "total": 25,
    "totalOpenings": 45,
    "remaining": 15,
    "companies": [
      {
        "name": "TechCorp",
        "openings": 5
      }
    ]
  },
  "insightsData": {
    "highestPackage": "25.0 LPA",
    "averagePackage": "8.5 LPA",
    "topHiringCompany": "TechCorp",
    "topDepartment": "Computer Science"
  },
  "departmentsData": {
    "totalDepartments": 8,
    "eligibleStudents": 140,
    "remaining": 20
  },
  "topStudents": [
    {
      "rank": 1,
      "name": "John Doe",
      "department": "Computer Science",
      "package": "25.0 LPA",
      "company": "TechCorp"
    }
  ]
}
```

## Real-time Analytics Endpoints

### 2. Real-time Statistics
**GET** `/api7/dashboard/stats/real-time`

Returns live dashboard statistics with recent activity.

**Parameters:**
- None

**Response:**
```json
{
  "totalStudents": 150,
  "totalPlacements": 120,
  "totalCompanies": 25,
  "activeApplications": 45,
  "newPlacementsThisMonth": 15,
  "placementRate": 80.0,
  "lastUpdated": "2025-01-15"
}
```

### 3. Growth Analytics
**GET** `/api7/dashboard/analytics/growth`

Returns growth analytics for specified time period.

**Parameters:**
- `months` (optional): Number of months to analyze (default: 6)

**Response:**
```json
{
  "labels": ["Jul 2024", "Aug 2024", "Sep 2024", ...],
  "placementCounts": [10, 15, 20, ...],
  "averagePackages": [8.2, 8.5, 9.1, ...],
  "growthRate": 12.5,
  "totalPlacements": 120
}
```

### 4. Recent Activities
**GET** `/api7/dashboard/activities/recent`

Returns recent placement activities.

**Parameters:**
- `limit` (optional): Number of activities to return (default: 10)

**Response:**
```json
{
  "activities": [
    {
      "type": "PLACEMENT",
      "description": "Student placed at TechCorp",
      "date": "2025-01-15",
      "status": "COMPLETED",
      "studentName": "John Doe",
      "companyName": "TechCorp"
    }
  ],
  "totalCount": 25
}
```

## Department & Company Analytics

### 5. Department Analytics
**GET** `/api7/dashboard/analytics/departments`

Returns comprehensive department-wise analytics.

**Response:**
```json
{
  "departments": [
    {
      "name": "Computer Science",
      "totalStudents": 60,
      "placedStudents": 50,
      "placementRate": 83.33,
      "averagePackage": 9.2,
      "highestPackage": 25.0
    }
  ],
  "totalDepartments": 8
}
```

### 6. Company Analytics
**GET** `/api7/dashboard/analytics/companies`

Returns detailed company performance analytics.

**Response:**
```json
{
  "totalCompanies": 25,
  "activeCompanies": 20,
  "newCompaniesThisYear": 8,
  "averagePackageOffered": 8.5,
  "topPerformingCompanies": [
    {
      "companyName": "TechCorp",
      "totalHires": 15,
      "averagePackage": 12.5
    }
  ]
}
```

## Student Management Endpoints

### 7. Get Students
**GET** `/api7/dashboard/students`

Returns paginated list of students with filtering options.

**Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `search` (optional): Search term for name/email
- `department` (optional): Filter by department
- `status` (optional): Filter by placement status ("placed", "unplaced")

**Response:**
```json
{
  "students": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "department": "Computer Science",
      "phone": "1234567890",
      "avgMarks": 85.5,
      "verified": true,
      "batch": "2024"
    }
  ],
  "totalElements": 150,
  "totalPages": 15,
  "currentPage": 0,
  "pageSize": 10
}
```

### 8. Get Student Details
**GET** `/api7/dashboard/students/{id}`

Returns detailed information about a specific student.

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Computer Science",
  "phone": "1234567890",
  "avgMarks": 85.5,
  "verified": true,
  "batch": "2024",
  "placementStatus": "PLACED",
  "placementPackage": 12.5,
  "placementCompany": "TechCorp",
  "placementDate": "2025-01-10"
}
```

## Company Management Endpoints

### 9. Get Companies
**GET** `/api7/dashboard/companies`

Returns paginated list of companies with filtering options.

**Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `search` (optional): Search term for company name/HR name
- `industry` (optional): Filter by industry type

**Response:**
```json
{
  "companies": [
    {
      "id": 1,
      "name": "TechCorp",
      "industry": "Information Technology",
      "hrName": "Jane Smith",
      "hrContact": "9876543210",
      "location": "Bangalore",
      "website": "https://techcorp.com",
      "isMNC": true,
      "associatedSince": "2023-01-15"
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "currentPage": 0,
  "pageSize": 10
}
```

### 10. Get Company Details
**GET** `/api7/dashboard/companies/{id}`

Returns detailed information about a specific company.

**Response:**
```json
{
  "id": 1,
  "name": "TechCorp",
  "industry": "Information Technology",
  "hrName": "Jane Smith",
  "hrContact": "9876543210",
  "location": "Bangalore",
  "website": "https://techcorp.com",
  "isMNC": true,
  "associatedSince": "2023-01-15",
  "totalHires": 15,
  "averagePackage": 12.5
}
```

## Activity Logs & Monitoring

### 11. Get Activity Logs
**GET** `/api7/dashboard/logs`

Returns paginated activity logs with filtering options.

**Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `action` (optional): Filter by action type
- `entity` (optional): Filter by entity type
- `dateFrom` (optional): Start date filter
- `dateTo` (optional): End date filter

**Response:**
```json
{
  "logs": [
    {
      "id": 1,
      "action": "PLACEMENT_CREATED",
      "entity": "PLACEMENT",
      "entityId": 123,
      "performedBy": "SYSTEM",
      "timestamp": "2025-01-15T10:30:00",
      "details": "Student placed at TechCorp"
    }
  ],
  "totalElements": 500,
  "totalPages": 25,
  "currentPage": 0,
  "pageSize": 20
}
```

## Data Export Endpoints

### 12. Export Dashboard Data
**GET** `/api7/dashboard/export/dashboard-data`

Exports complete dashboard data in specified format.

**Parameters:**
- `format` (optional): Export format ("excel" or "pdf", default: "excel")

**Response:**
- Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet (Excel)
- Content-Type: application/pdf (PDF)
- Content-Disposition: attachment; filename=dashboard_report.xlsx

### 13. Export Student Data
**GET** `/api7/dashboard/export/students`

Exports student data with optional filters.

**Parameters:**
- `format` (optional): Export format ("excel" or "pdf", default: "excel")
- `department` (optional): Filter by department
- `status` (optional): Filter by placement status

### 14. Export Company Data
**GET** `/api7/dashboard/export/companies`

Exports company data.

**Parameters:**
- `format` (optional): Export format ("excel" or "pdf", default: "excel")

## Advanced Analytics Endpoints

### 15. Performance Analytics
**GET** `/api7/dashboard/analytics/performance`

Returns detailed performance analytics.

**Parameters:**
- `months` (optional): Analysis period in months (default: 12)

### 16. Package Distribution
**GET** `/api7/dashboard/analytics/package-distribution`

Returns detailed package distribution analytics.

**Response:**
```json
{
  "packageRanges": [
    {
      "range": "0-5 LPA",
      "count": 30,
      "percentage": 25.0
    },
    {
      "range": "5-10 LPA",
      "count": 50,
      "percentage": 41.67
    }
  ],
  "totalPlacements": 120,
  "minPackage": 3.5,
  "maxPackage": 25.0,
  "medianPackage": 8.5,
  "averagePackage": 8.75
}
```

### 17. Hiring Trends
**GET** `/api7/dashboard/analytics/hiring-trends`

Returns hiring trends analysis.

**Parameters:**
- `months` (optional): Analysis period in months (default: 12)

**Response:**
```json
{
  "topHiringCompanies": [
    {
      "company": "TechCorp",
      "hires": 15
    }
  ],
  "departmentTrends": [
    {
      "department": "Computer Science",
      "hires": 50
    }
  ]
}
```

## Real-time Features

### 18. Real-time Dashboard Summary
**GET** `/api7/dashboard/realtime/dashboard-summary`

Returns real-time dashboard summary with current metrics.

### 19. Real-time Alerts
**GET** `/api7/dashboard/realtime/alerts`

Returns real-time alerts and notifications.

**Response:**
```json
{
  "alerts": [
    {
      "type": "warning",
      "message": "Placement rate is below 50%",
      "severity": "high"
    },
    {
      "type": "success",
      "message": "Great week! 10 new placements",
      "severity": "low"
    }
  ],
  "totalAlerts": 2
}
```

### 20. Get Notifications
**GET** `/api7/dashboard/notifications`

Returns dashboard notifications and alerts.

## System Management Endpoints

### 21. System Health
**GET** `/api7/dashboard/system/health`

Returns system health status.

**Response:**
```json
{
  "database": "healthy",
  "uptime": "Available",
  "cacheSize": 15,
  "lastCacheRefresh": "Recently"
}
```

### 22. Data Integrity Check
**GET** `/api7/dashboard/validation/data-integrity`

Performs data integrity validation.

**Response:**
```json
{
  "issues": [
    "5 students without department information",
    "2 companies without HR contact"
  ],
  "totalIssues": 2,
  "status": "issues_found"
}
```

### 23. Refresh Cache
**POST** `/api7/dashboard/refresh`

Refreshes cached dashboard data.

**Response:**
```json
{
  "message": "Dashboard data refreshed successfully"
}
```

## Custom Reports

### 24. Generate Custom Report
**POST** `/api7/dashboard/reports/custom`

Generates custom reports based on parameters.

**Request Body:**
```json
{
  "type": "department",
  "department": "Computer Science",
  "dateFrom": "2024-01-01",
  "dateTo": "2024-12-31"
}
```

**Response:**
```json
{
  "type": "department_report",
  "department": "Computer Science",
  "data": {
    "totalStudents": 60,
    "totalPlacements": 50,
    "placementRate": 83.33,
    "averagePackage": 9.2
  },
  "generatedAt": "2025-01-15",
  "parameters": {
    "type": "department",
    "department": "Computer Science"
  }
}
```

## Error Handling

All endpoints return appropriate HTTP status codes:
- `200 OK`: Successful request
- `400 Bad Request`: Invalid parameters
- `401 Unauthorized`: Authentication required or invalid
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

Error responses include:
```json
{
  "error": "Error message describing the issue",
  "timestamp": "2025-01-15T10:30:00",
  "path": "/api7/dashboard/students"
}
```

## Data Caching

The backend implements intelligent caching for performance:
- Cache duration: 5 minutes for most data
- Real-time data bypasses cache
- Cache can be manually refreshed via `/refresh` endpoint
- Cache size monitoring available via `/system/health`

## Security Features

1. **JWT Token Authentication**: All endpoints require valid JWT tokens
2. **Role-based Access Control**: Only ADMIN users can access dashboard endpoints
3. **Input Validation**: All parameters are validated before processing
4. **Data Sanitization**: Output data is sanitized to prevent XSS attacks

## Performance Optimizations

1. **Pagination**: Large datasets are paginated for better performance
2. **Lazy Loading**: Data is loaded on demand
3. **Caching**: Frequently accessed data is cached
4. **Database Optimization**: Efficient queries with proper indexing
5. **Memory Management**: Proper resource cleanup and garbage collection

## Real-time Features Implementation

The backend supports real-time features through:
1. **Live Data Refresh**: Endpoints provide latest data without caching
2. **Alert System**: Automated alerts based on predefined thresholds
3. **Activity Tracking**: Real-time activity logging and monitoring
4. **Progressive Data Loading**: Incremental data loading for better UX

## Future Enhancements

Planned features for future releases:
1. **WebSocket Integration**: Real-time push notifications
2. **Advanced PDF Reports**: Rich PDF generation with charts and graphs
3. **Email Notifications**: Automated email alerts for important events
4. **API Rate Limiting**: Request rate limiting for better resource management
5. **Advanced Analytics**: Machine learning-based predictive analytics
6. **Multi-tenant Support**: Support for multiple institutions
7. **Mobile API**: Dedicated mobile app endpoints
8. **Audit Logging**: Comprehensive audit trail for all actions

## Migration Notes

For existing implementations:
1. All hardcoded values have been replaced with dynamic data
2. New endpoints are backward compatible
3. Response formats maintain consistency
4. Legacy endpoints are deprecated but still functional
5. Database schema remains unchanged

This enhanced backend provides a robust, scalable, and feature-rich foundation for the TPO Admin Dashboard, supporting all modern requirements for placement management and analytics.
