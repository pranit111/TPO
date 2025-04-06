package com.example.TPO.Tpo.DashBoard;

import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.Tpo.TPO_Role;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api7/dashboard")
public class DashboardController {
@Autowired
    TpoRepository tpoRepository;
@Autowired
    UserRepo userRepo;
@Autowired
    JWTService jwtService;
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardData getDashboardData(@RequestHeader("Authorization") String authheader) {
        String authToken="";
        if (authheader  != null && authheader.startsWith("Bearer ")) {
            authToken = authheader.substring(7);}
        User dbUser=userRepo.findById(jwtService.extractUserId(authToken)).get();
        Optional<TPOUser> tpoUser=tpoRepository.findByUser(dbUser);
        TPOUser tpoUser1=tpoUser.get();
        if(tpoUser1.getRole()== TPO_Role.ADMIN){
        return dashboardService.getDashboardData();}
        DashboardData dash= new  DashboardData();
        return dash;

    }
}
