package com.example.TPO.Student.StudentDTO;

import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.UserManagement.UserDTO.UserDTO;
import com.example.TPO.UserManagement.entity.User;

public class StudentMapper {
    public static StudentDTO toStudentDTO(Student student) {
        if (student == null) return null;

        User user = student.getUser();
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );

        return new StudentDTO(
                student.getId(),
                userDTO,
                student.getFirstName(),
                student.getMiddleName(),
                student.getLastName(),
                student.getDateOfBirth(),
                student.getGender().toString(),
                student.getPhoneNumber(),
                student.getAddress(),
                student.getDepartment(),
                student.getAcademicyear(),
                student.getSscMarks(),
                student.getHscMarks(),
                student.getDiplomaMarks(),
                student.getSem1Marks(),
                student.getSem2Marks(),
                student.getSem3Marks(),
                student.getSem4Marks(),
                student.getSem5Marks(),
                student.getSem6Marks(),
                student.getNoOfBacklogs(),
                student.getAvgMarks(),
                student.getGrNo(),
                student.getProfileimagedata(),
                student.isSem1KT(),
                student.isSem2KT(),
                student.isSem3KT(),
                student.isSem4KT(),
                student.isSem5KT(),
                student.isSem6KT(),
                student.getYearOfPassing(),
                student.isResults_verified()
        );
    }
}
