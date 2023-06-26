package com.example.smartcontactmanager.controller;

import com.example.smartcontactmanager.dao.UserRepository;
import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	@RequestMapping("/home")
	public String home(Model m) {
		m.addAttribute("title","Home-Smart Contact Manager");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model m) {
		m.addAttribute("title","About-Smart Contact Manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title","Register-Smart Contact Manager");
		m.addAttribute("user",new User());
		return "signup";
	}
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value="agreement",defaultValue="false") 
	boolean agreement, Model model,HttpSession session) {
		try {
			if(result1.hasErrors()) {
				System.out.println("Error "+result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			if(!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("Agreement"+agreement);
			System.out.println("User"+user);
			User result=this.userRepository.save(user);
			model.addAttribute("user",result);
			session.setAttribute("message",new Message("User registered","alert-success"));
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("User is not registered ","alert-danger"));
			
		}
		return "signup";
	}
    //handler for the custom login page
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","Login Page");
		return "login";
	}

}
