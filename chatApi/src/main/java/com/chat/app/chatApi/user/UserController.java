package com.chat.app.chatApi.user;

import java.util.List;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chat.app.chatApi.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserController {
	
	private UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


	private UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping("/users")
	public List<ChatUser> getAllUsers(){
		return userRepository.findAll();
	}
	
	@GetMapping("/basicauth")
	public String basicAuthCheck() {
		return "success";
	}
	
	@GetMapping("/users/{email}")
	public ChatUser findUserByEmail(@PathVariable("email") String email){
		return userRepository.findByEmail(email);
	}
	
	@GetMapping("/users/id/{id}")
	public Optional<ChatUser> findUserById(@PathVariable("id") Long id){
		Optional<ChatUser> user = userRepository.findById(id);
		return user;
	}
	
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody ChatUser user) {
    	ChatUser loginAttempt = userRepository.findByEmail(user.getEmail());
    	
    	if(loginAttempt != null && loginAttempt.getPassword().equals(user.getPassword())) {
    		return ResponseEntity.ok().build();
    	}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
	
	@PostMapping("/create/users")
	public ResponseEntity<Void> createUser(@Valid @RequestBody ChatUser user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
		userRepository.save(user);
		return ResponseEntity.created(null).build();
	}
	
	@PutMapping("/users/updateUsername/{id}")
	public ResponseEntity<Void> updateUserName(@RequestBody ChatUser user, @PathVariable("id") Long id){
		
		Optional<ChatUser> updateUser = userRepository.findById(id);
		ChatUser update = updateUser.get();
		if(update.getUserName()==null) {
			return ResponseEntity.notFound().build();
		}else 
		{

			update.setUserName(user.getUserName());
			userRepository.save(update);
		}
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/users/updatePassword/{id}")
	public ResponseEntity<Void> updatePassword(@RequestBody ChatUser user, @PathVariable("id") Long id){
		if(!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}else {
	        String encodedPassword = passwordEncoder.encode(user.getPassword());
			Optional<ChatUser> updateUser = userRepository.findById(id);
			ChatUser update = updateUser.get();
			update.setPassword(encodedPassword);
			userRepository.save(update);
		}
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/user/deleteUser/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
		if(!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}else {
			ChatUser update = userRepository.findById(id).get();
			update.setEmail(null);
			update.setUserName(null);
			update.setPassword(null);
			userRepository.save(update);
		}
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUserbyId(@PathVariable("id") Long id){
		if(!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}else {
			
			userRepository.deleteById(id);
		}
		return ResponseEntity.ok().build();
	}
	
}
