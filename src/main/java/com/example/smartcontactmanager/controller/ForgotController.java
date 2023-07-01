package com.example.smartcontactmanager.controller;

import com.example.smartcontactmanager.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotController {
    @Autowired
    private EmailService emailService;
    Random random=new Random(1000);
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session){
        System.out.println("Email "+email);
        //Generating the otp of the 4 digit
        int otp=random.nextInt(999999);
        System.out.println("OTP "+otp);
        //write the code to send the otp to email
        String subject="OTP from SCM";
        String message="OTP = "+otp;
        String to=email;
        boolean flag=this.emailService.sendEmail(subject,message,email);
        if(flag){
            session.setAttribute("otp",otp);
            return "verify_otp";
        }else{
            session.setAttribute("message","Check your email id");
            return "forgot_email_form";
        }
    }
}
