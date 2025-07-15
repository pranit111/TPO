# Database Schema Fix for Large Text Fields

## Problem
Hibernate is trying to alter LONGTEXT columns to TINYTEXT, causing data truncation errors.

## Root Cause
The @Lob annotation alone is not sufficient for MySQL with some Hibernate versions. Need explicit columnDefinition.

## Solution

### 1. Updated Entity Annotations

**JobPost.java** - Added explicit `columnDefinition = "LONGTEXT"`:
```java
@Lob
@Column(nullable = false, columnDefinition = "LONGTEXT")
private String description;

@Lob
@Column(nullable = false, columnDefinition = "LONGTEXT")
private String skillRequirements;

@Lob
@Column(columnDefinition = "LONGTEXT")
private String selectionRounds;
```

**Student.java** - Added explicit `columnDefinition = "LONGTEXT"`:
```java
@Lob
@Column(columnDefinition = "LONGTEXT")
private String address;
```

**JobApplication.java** - Added explicit `columnDefinition = "LONGTEXT"`:
```java
@Lob
@Column(columnDefinition = "LONGTEXT")
private String feedback;
```

### 2. Database Manual Fix (Run if needed)

```sql
-- Ensure all text fields are LONGTEXT
USE tpo;

-- Fix JobPost text fields
ALTER TABLE job_posts MODIFY COLUMN description LONGTEXT NOT NULL;
ALTER TABLE job_posts MODIFY COLUMN skill_requirements LONGTEXT NOT NULL;
ALTER TABLE job_posts MODIFY COLUMN selection_rounds LONGTEXT;

-- Fix Student address field
ALTER TABLE students MODIFY COLUMN address LONGTEXT;

-- Fix JobApplication feedback field
ALTER TABLE job_applications MODIFY COLUMN feedback LONGTEXT;

-- Verify changes
DESCRIBE job_posts;
DESCRIBE students;
DESCRIBE job_applications;
```

### 3. Application Properties Update

Add this to prevent future issues:
```properties
# Prevent Hibernate from trying to validate/update schema for large text fields
spring.jpa.hibernate.ddl-auto=validate
# OR use update but with explicit column definitions
```

### 4. Alternative: Disable Schema Validation Temporarily

If you want to start the application without schema validation:
```properties
spring.jpa.hibernate.ddl-auto=none
```

Then manually ensure your database schema matches your entities.

## Current Database State

Record causing the issue:
- Table: `job_posts`
- Record ID: 9
- Description length: 607 characters

## Verification Commands

```sql
-- Check current schema
SHOW CREATE TABLE job_posts;

-- Check long descriptions
SELECT id, LENGTH(description) as desc_length 
FROM job_posts 
WHERE LENGTH(description) > 255;

-- Check if all text fields are LONGTEXT
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'job_posts' 
AND TABLE_SCHEMA = 'tpo' 
AND COLUMN_NAME IN ('description', 'skill_requirements', 'selection_rounds');
```

## Next Steps

1. ✅ Updated entity annotations with explicit columnDefinition
2. 🔄 Restart application to test the fix
3. 📊 If issue persists, run the manual SQL commands above
4. ✅ Verify all large text fields work correctly
