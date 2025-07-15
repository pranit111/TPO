# Placement Filter API Documentation

## New Filter Endpoints Added to PlacementController

### Base URL: `/api9/placements`

---

## Filter Endpoints

### 1. Filter by Company Name
**Endpoint:** `GET /api9/placements/filter/company`

**Parameters:**
- `companyName` (required): String - Company name to search for
- `page` (optional): int - Page number (default: 0)
- `size` (optional): int - Page size (default: 20)

**Example:**
```
GET /api9/placements/filter/company?companyName=Google&page=0&size=10
```

---

### 2. Filter by Job Designation
**Endpoint:** `GET /api9/placements/filter/job-designation`

**Parameters:**
- `jobDesignation` (required): String - Job designation to search for
- `page` (optional): int - Page number (default: 0)
- `size` (optional): int - Page size (default: 20)

**Example:**
```
GET /api9/placements/filter/job-designation?jobDesignation=Software Engineer&page=0&size=10
```

---

### 3. Filter by Package Range
**Endpoint:** `GET /api9/placements/filter/package-range`

**Parameters:**
- `minPackage` (required): double - Minimum package amount
- `maxPackage` (required): double - Maximum package amount
- `page` (optional): int - Page number (default: 0)
- `size` (optional): int - Page size (default: 20)

**Example:**
```
GET /api9/placements/filter/package-range?minPackage=500000&maxPackage=1000000&page=0&size=10
```

---

### 4. Filter by Date Range
**Endpoint:** `GET /api9/placements/filter/date-range`

**Parameters:**
- `fromDate` (required): LocalDate (YYYY-MM-DD) - Start date
- `toDate` (required): LocalDate (YYYY-MM-DD) - End date
- `page` (optional): int - Page number (default: 0)
- `size` (optional): int - Page size (default: 20)

**Example:**
```
GET /api9/placements/filter/date-range?fromDate=2024-01-01&toDate=2024-12-31&page=0&size=10
```

---

### 5. Filter by Student Name
**Endpoint:** `GET /api9/placements/filter/student`

**Parameters:**
- `studentName` (required): String - Student name to search for
- `page` (optional): int - Page number (default: 0)
- `size` (optional): int - Page size (default: 20)

**Example:**
```
GET /api9/placements/filter/student?studentName=John Doe&page=0&size=10
```

---

### 6. Filter by Department
**Endpoint:** `GET /api9/placements/filter/department`

**Parameters:**
- `department` (required): String - Department name to search for (e.g., Computer Science, Electronics, Mechanical)
- `page` (optional): int - Page number (default: 0)
- `size` (optional): int - Page size (default: 20)

**Example:**
```
GET /api9/placements/filter/department?department=Computer Science&page=0&size=10
```

---

### 7. Filter by Student Year
**Endpoint:** `GET /api9/placements/filter/student-year`

**Parameters:**
- `studentYear` (required): String - Student year (FE, SE, TE, BE)
- `page` (optional): int - Page number (default: 0)
- `size` (optional): int - Page size (default: 20)

**Example:**
```
GET /api9/placements/filter/student-year?studentYear=BE&page=0&size=10
```

---

## Response Format

All filter endpoints return paginated results in the following format:

```json
{
  "content": [
    {
      "id": 1,
      "placementDate": "2024-06-15",
      "placedPackage": 800000.0,
      "remarks": "Excellent performance",
      "application": {
        "id": 1,
        "student": {
          "firstName": "John",
          "lastName": "Doe"
        },
        "jobPost": {
          "jobDesignation": "Software Engineer",
          "company": {
            "name": "Google"
          },
          "studentYear": "BE"
        }
      }
    }
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 20,
  "number": 0
}
```

---

## Usage Examples

### Frontend Integration:
```javascript
// Filter by company
const filterByCompany = async (companyName, page = 0, size = 10) => {
  const response = await fetch(
    `/api9/placements/filter/company?companyName=${encodeURIComponent(companyName)}&page=${page}&size=${size}`
  );
  return response.json();
};

// Filter by package range
const filterByPackageRange = async (minPackage, maxPackage, page = 0, size = 10) => {
  const response = await fetch(
    `/api9/placements/filter/package-range?minPackage=${minPackage}&maxPackage=${maxPackage}&page=${page}&size=${size}`
  );
  return response.json();
};

// Filter by date range
const filterByDateRange = async (fromDate, toDate, page = 0, size = 10) => {
  const response = await fetch(
    `/api9/placements/filter/date-range?fromDate=${fromDate}&toDate=${toDate}&page=${page}&size=${size}`
  );
  return response.json();
};

// Filter by department
const filterByDepartment = async (department, page = 0, size = 10) => {
  const response = await fetch(
    `/api9/placements/filter/department?department=${encodeURIComponent(department)}&page=${page}&size=${size}`
  );
  return response.json();
};

// Filter by student year
const filterByStudentYear = async (studentYear, page = 0, size = 10) => {
  const response = await fetch(
    `/api9/placements/filter/student-year?studentYear=${studentYear}&page=${page}&size=${size}`
  );
  return response.json();
};
```

---

## Repository Queries Added

The following queries were added to `PlacementRepository.java`:

1. **filterByCompany**: Joins with JobPost and Company entities to filter by company name
2. **filterByJobDesignation**: Joins with JobPost to filter by job designation
3. **filterByPackageRange**: Direct filter on placement package amount
4. **filterByDateRange**: Direct filter on placement date
5. **filterByStudentName**: Joins with JobApplication and Student to filter by student name
6. **filterByDepartment**: Joins with JobApplication and Student to filter by department
7. **filterByStudentYear**: Joins with JobPost to filter by target student year

All queries support case-insensitive search and pagination.
