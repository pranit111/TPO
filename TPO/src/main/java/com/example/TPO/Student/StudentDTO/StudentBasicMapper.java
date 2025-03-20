package com.example.TPO.Student.StudentDTO;


import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.Student.StudentDTO.StudentBasicDTO;

public class StudentBasicMapper {

    public static StudentBasicDTO toStudentBasicDTO(Student student) {
        return new StudentBasicDTO(
                student.getFirstName(),
                student.getMiddleName(),
                student.getLastName(),
                student.getGender().toString(),
                student.getDateOfBirth(),
                student.getGrNo(),
                student.getSscMarks(),
                student.getHscMarks(),
                student.getDiplomaMarks(),
                student.getAvgMarks(),
                student.getDepartment(),
                student.getAcademicyear(),
                student.getNoOfBacklogs(),
                student.getUser().getEmail(),
                student.getPhoneNumber(),
                student.getYearOfPassing()
        );
    }
}
