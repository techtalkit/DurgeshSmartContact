package com.example.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.razorpay.*;
import com.example.smartcontactmanager.dao.ContactRepository;
import com.example.smartcontactmanager.dao.UserRepository;
import com.example.smartcontactmanager.entities.Contact;
import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.helper.Message;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private ContactRepository contactRepository;

	//Method to adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("Username" + userName);
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER " + user);
		model.addAttribute("user", user);
	}
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// Open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {
		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("imageFile") MultipartFile file,
			BindingResult bindingResult,
			Principal principal,
			HttpSession session){
		try {
			String name=principal.getName();
			User user=userRepository.getUserByUserName(name);
			//processing and uploading the file
			if(file.isEmpty()) {
				//if the file is empty then below message will execute
				System.out.println("Nothing to be uploaded");
				contact.setImage("default.png");
				}
			else {
				//upload the file to the folder and update the name in contact table
				contact.setImage(file.getOriginalFilename());
				File saveFile=new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}
			user.getContacts().add(contact);
			contact.setUser(user);
			userRepository.save(user);
			System.out.println("Data"+contact);
			System.out.println("Added to database");
			//message success...
			session.setAttribute("message",new Message("Your contact is added successfully","success"));
		}catch(Exception e) {
			System.out.println("Error "+e.getMessage());
			e.printStackTrace();
			//message error....
			session.setAttribute("message",new Message("Something went wrong Try again","danger"));
		}
		return "normal/add_contact_form";
	}
	//Show Contacts Handler
	//per page =5[n]
	//current page=0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal) {
		m.addAttribute("title","Show Contacts");
		//List of contacts will send from here
		String userName=principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		//contacts per page
		//current page
		Pageable pageable = PageRequest.of(page,5);
		Page<Contact> contacts = this.contactRepository.findContactsbyUser(user.getId(),pageable);
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalpages",contacts.getTotalPages());
		return "normal/show_contacts";
	}
	//Showing Particulr Contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		System.out.println("CID"+cId);
		Optional<Contact> contactOption = this.contactRepository.findById(cId);
		Contact contact = contactOption.get();
		
		//We will get the loggedin user from below
		String userName = principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
		}
		return "normal/contact_detail";
	}
	//Delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,Model model,Principal principal,HttpSession session) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		String userName = principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		if(user.getId()==contact.getUser().getId()){
			 //contact.setUser(null);
	         //this.contactRepository.delete(contact);
			 User user2=this.userRepository.getUserByUserName(principal.getName());
			 user2.getContacts().remove(contact);
			 this.userRepository.save(user2);
		     session.setAttribute("message", new Message("Contact deleted successfully","success"));
		}
		return "redirect:/user/show-contacts/0";
	}
	
	//Open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model model) {
		model.addAttribute("title","Update Contact");
		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact",contact);
		return "normal/update_form";
	}
	//update hanlder
	@RequestMapping(value="process-update",method= RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("imageFile") MultipartFile file,
			Model m,HttpSession session,Principal principal) {
		try {
			Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();
			//image..
			if(!file.isEmpty()) {
				//file work..
				//delete photo
				File deleteFile=new ClassPathResource("static/images").getFile();
				File file1=new File(deleteFile,oldContactDetail.getImage());
				file1.delete();
				//update new photo
				File saveFile=new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}else {
				contact.setImage(oldContactDetail.getImage());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		User user=this.userRepository.getUserByUserName(principal.getName());
		contact.setUser(user);
		this.contactRepository.save(contact);
		session.setAttribute("message",new Message("Your contact is updated","success"));
		System.out.println("Contact Name"+contact.getName());
		System.out.println("Contact Id"+contact.getCid());
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	//Your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title","Profile page");
		return "normal/profile";
	}
	//Open Settings Handler
	@GetMapping("/settings")
	public String openSettings(){
		return "normal/settings";
	}

	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
								 @RequestParam("newPassword") String newPassword,
								 Principal principal,
								 HttpSession session){
		System.out.println("Old Password "+oldPassword);
		System.out.println("New Password "+newPassword);
		String username=principal.getName();
		User currentUser = this.userRepository.getUserByUserName(username);
		System.out.println(currentUser.getPassword());
		if(this.bCryptPasswordEncoder.matches(oldPassword,currentUser.getPassword())){
			//change password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message",new Message("Your password is updated","success"));
		}else{
			//error...
			session.setAttribute("message",new Message("Please enter correct old password","danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/index";
	}
	//Creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String,Object> data) throws Exception {
		//System.out.println("Hey order function is executed");
		int amt=Integer.parseInt(data.get("amount").toString());
		RazorpayClient client=new RazorpayClient("rzp_test_NymB8bXkgJ7kXD","3s8OUU423WHpbTkDTzuIXL3n");
		//order place by using the JsonObject
		JSONObject ob=new JSONObject();
		ob.put("amount",amt*100);
		ob.put("currency","INR");
		ob.put("receipt","txn_235425");
		//Creating new order-->Request will go to Razorpay server
		Order order=client.orders.create(ob);
		System.out.println(order);
		return order.toString();
	}
}
