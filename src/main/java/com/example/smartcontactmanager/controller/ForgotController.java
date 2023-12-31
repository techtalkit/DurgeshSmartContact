package com.example.smartcontactmanager.controller;

import com.example.smartcontactmanager.dao.UserRepository;
import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    Random random=new Random(1000);
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session){
        System.out.println("Email "+email);
        //Generating the otp of the 4 digit
        long otp=random.nextLong(999999);
        System.out.println("OTP "+otp);
        System.out.println();
        //write the code to send the otp to email
        String subject="OTP from SCM";
        String message=""+
                       "<div style='border:1px solid #e2e2e2; padding:20px'>"+
                       "<h1>"+
                       "OTP is"+
                       "<b>"+otp
                       +"</b>"+
                       "</div>";
        String to=email;
        boolean flag=this.emailService.sendEmail(subject,message,email);
        if(flag){
            session.setAttribute("myotp",otp);
            session.setAttribute("email",email);
            return "verify_otp";
        }else{
            session.setAttribute("message","Check your email id");
            return "forgot_email_form";
        }
    }
    //verify otp
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") long otp,HttpSession session){
           long myOtp=(long)session.getAttribute("myotp");
           String email=(String) session.getAttribute("email");
           if(myOtp==otp){
               //password change form
               User user=this.userRepository.getUserByUserName(email);
               if(user==null){
                   session.setAttribute("message","User does not exists with this email id");
                   return "forgot_email_form";
                   //send error message
               }else{
                   //send change password form
               }
               return "password_change_form";
           }else{
               session.setAttribute("message","You have entered wrong OTP");
               return "verify_otp";
           }
    }
    //Change Password Handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session){
        String email=(String) session.getAttribute("email");
        User user=this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
        this.userRepository.save(user);
        return "redirect:/signin?change=password changed successfully..";
    }
}
