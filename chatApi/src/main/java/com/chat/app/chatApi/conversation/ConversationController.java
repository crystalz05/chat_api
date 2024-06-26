package com.chat.app.chatApi.conversation;

import java.util.ArrayList;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chat.app.chatApi.repository.ConversationRepository;
import com.chat.app.chatApi.repository.UserRepository;
import com.chat.app.chatApi.user.ChatUser;

import jakarta.validation.Valid;

@RestController
public class ConversationController {
	
	private ConversationRepository conversationRepository;
	private UserRepository userRepository;

	public ConversationController(ConversationRepository conversationRepository, UserRepository userRepository) {
		super();
		this.conversationRepository = conversationRepository;
		this.userRepository = userRepository;
	}
	
	@GetMapping("/conversations")
	public List<Conversation> getAllConversations(){
		return conversationRepository.findAll();
	}
	
	@GetMapping("/conversations/{id}")
	public ResponseEntity<Conversation> retriveConversationById(@PathVariable("id") Long id){
		if(!conversationRepository.existsById(id)){
			return ResponseEntity.notFound().build();
		}else {
			Conversation conversation = conversationRepository.findById(id).get();
			return ResponseEntity.ok(conversation);
		}
	}
	
	@PostMapping("/conversations/{participant1}/{participant2}")
	public ResponseEntity<Void> createConversation(@Valid @RequestBody Conversation conversation, @PathVariable("participant1") long participant1, @PathVariable("participant2") long participant2 ){
		
		List<ChatUser> participantList = new ArrayList<>();
		if(userRepository.existsById(participant1) && userRepository.existsById(participant2)) {
			participantList.add(userRepository.findById(participant1).get());
			participantList.add(userRepository.findById(participant2).get());			
			conversation.setParticipants(participantList);
		}
		
		conversationRepository.save(conversation);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/conversations/{id}")
	public ResponseEntity<Void> deleteConversationById(@PathVariable("id") Long id){
		if(!conversationRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}else {
			
			conversationRepository.deleteById(id);
		}
		return ResponseEntity.ok().build();
	}

}
