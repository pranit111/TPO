package com.example.TPO.Student.StudentDTO;


import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.Student.StudentDTO.StudentBasicDTO;

public class StudentBasicMapper {

    public static StudentBasicDTO toStudentBasicDTO(Student student) {
        return new StudentBasicDTO(
                student.getId(),
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
                student.getSem1Marks(),
                student.getSem2Marks(),
                student.getSem3Marks(),
                student.getSem4Marks(),
                student.getSem5Marks(),
                student.getSem6Marks(),
                student.getAcademicyear(),
                student.getNoOfBacklogs(),
                student.getUser().getEmail(),
                student.getPhoneNumber(),
                student.getYearOfPassing(),
                student.isSem1KT(),
                student.isSem2KT(),
                student.isSem3KT(),
                student.isSem4KT(),
                student.isSem5KT(),
                student.isSem6KT(),
                student.isResults_verified()


        );
    }
}
