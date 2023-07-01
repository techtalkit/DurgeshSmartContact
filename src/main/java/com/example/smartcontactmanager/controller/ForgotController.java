package com.example.smartcontactmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotController {
    Random random=new Random(1000);
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email){
        System.out.println("Email "+email);
        //Generating the otp of the 4 digit
        int otp=random.nextInt(999999);
        System.out.println("OTP "+otp);
        //write the code to send the otp to email
        return "verify_otp";
    }
}
