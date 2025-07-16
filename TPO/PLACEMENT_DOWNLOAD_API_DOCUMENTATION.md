# Placement Download API Documentation

## New Download Endpoints Added to PlacementController

### Base URL: `/api9/placements`

---

## Download Endpoints

### 1. Download All Placements (Excel)
**Endpoint:** `GET /api9/placements/download/excel`

**Description:** Downloads all placement records in Excel format

**Parameters:** None

**Response:** Excel file (.xlsx) containing all placement data

**Example:**
```
GET /api9/placements/download/excel
```

**File Name Format:** `placements_YYYY-MM-DD.xlsx`

---

### 2. Download Filtered Placements (Excel)
**Endpoint:** `GET /api9/placements/download/excel/filtered`

**Description:** Downloads filtered placement records in Excel format

**Parameters:**
- `filterType` (required): String - Type of filter to apply
  - Valid values: `company`, `department`, `studentyear`, `jobdesignation`
- `filterValue` (required): String - Value to filter by

**Response:** Excel file (.xlsx) containing filtered placement data

**Examples:**
```bash
# Download placements for specific company
GET /api9/placements/download/excel/filtered?filterType=company&filterValue=Google

# Download placements for specific department
GET /api9/placements/download/excel/filtered?filterType=department&filterValue=Computer Science

# Download placements for specific student year
GET /api9/placements/download/excel/filtered?filterType=studentyear&filterValue=BE

# Download placements for specific job designation
GET /api9/placements/download/excel/filtered?filterType=jobdesignation&filterValue=Software Engineer
```

**File Name Format:** `placements_{filterType}_{filterValue}_YYYY-MM-DD.xlsx`

---

## Excel File Structure

Both download endpoints generate Excel files with the following columns:

| Column | Description |
|--------|-------------|
| Placement ID | Unique placement identifier |
| Student Name | Full name of the placed student |
| Company | Company name where student is placed |
| Job Designation | Job title/position |
| Package (LPA) | Salary package in Lakhs Per Annum |
| Placement Date | Date when placement was confirmed |
| Department | Student's department |
| Student Year | Academic year (FE, SE, TE, BE) |
| Remarks | Additional comments or notes |

---

## Usage Examples

### Frontend Integration:

```javascript
// Download all placements
const downloadAllPlacements = async () => {
  const response = await fetch('/api9/placements/download/excel');
  const blob = await response.blob();
  
  // Create download link
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `placements_${new Date().toISOString().split('T')[0]}.xlsx`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
};

// Download filtered placements
const downloadFilteredPlacements = async (filterType, filterValue) => {
  const response = await fetch(
    `/api9/placements/download/excel/filtered?filterType=${filterType}&filterValue=${encodeURIComponent(filterValue)}`
  );
  const blob = await response.blob();
  
  // Create download link
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `placements_${filterType}_${filterValue}_${new Date().toISOString().split('T')[0]}.xlsx`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
};

// Usage examples
downloadAllPlacements();
downloadFilteredPlacements('company', 'Google');
downloadFilteredPlacements('department', 'Computer Science');
```

### Direct Browser Access:

```bash
# Download all placements
http://localhost:8080/api9/placements/download/excel

# Download Google placements
http://localhost:8080/api9/placements/download/excel/filtered?filterType=company&filterValue=Google

# Download Computer Science department placements
http://localhost:8080/api9/placements/download/excel/filtered?filterType=department&filterValue=Computer%20Science
```

---

## Response Headers

Both endpoints return the following response headers:

```
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="placements_[date].xlsx"
```

---

## Filter Types Supported

| Filter Type | Description | Example Values |
|-------------|-------------|----------------|
| `company` | Filter by company name | "Google", "Microsoft", "TCS" |
| `department` | Filter by student department | "Computer Science", "Electronics", "Mechanical" |
| `studentyear` | Filter by academic year | "FE", "SE", "TE", "BE" |
| `jobdesignation` | Filter by job title | "Software Engineer", "Data Analyst", "Developer" |

---

## Error Handling

- If invalid `filterType` is provided, all placements will be downloaded
- Empty results will generate an Excel file with headers only
- IO exceptions during Excel generation will return HTTP 500 error

---

## Technical Implementation

### Service Methods Added:
1. `downloadPlacementsExcel()` - Downloads all placement data
2. `downloadFilteredPlacementsExcel(filterType, filterValue)` - Downloads filtered data

### Dependencies Used:
- **Apache POI**: For Excel file generation
- **ExcelService**: Existing utility service for Excel creation
- **PlacementRepository**: For data retrieval with filters

### File Generation Process:
1. Retrieve placement data (all or filtered)
2. Convert entity data to String arrays
3. Format dates and handle null values
4. Generate Excel using ExcelService
5. Set appropriate response headers
6. Return byte array as downloadable file

---

## Performance Considerations

- **Large datasets**: Consider pagination for very large result sets
- **Memory usage**: Excel generation loads all data into memory
- **Filtering**: Database-level filtering is applied before Excel generation
- **File size**: Typical file size ~50KB for 100 records

---

## Future Enhancements

Potential improvements for download functionality:

1. **CSV Export**: Add CSV download option
2. **PDF Reports**: Generate formatted PDF reports
3. **Custom Date Range**: Filter by placement date range in downloads
4. **Background Processing**: For very large datasets, implement async downloads
5. **Email Export**: Send download links via email
6. **Custom Column Selection**: Allow users to choose which columns to export
