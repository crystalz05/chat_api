package com.chat.app.chatApi.conversation;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chat.app.chatApi.repository.ConversationRepository;
import com.chat.app.chatApi.repository.MessageRepository;
import com.chat.app.chatApi.repository.UserRepository;
import com.chat.app.chatApi.user.ChatUser;

import jakarta.validation.Valid;

@RestController
public class MessageController {
	
	private MessageRepository messageRepository;
	private ConversationRepository conversationRepository;
	private UserRepository userRepository;
	
	
	public MessageController(MessageRepository messageRepository, UserRepository userRepository, ConversationRepository conversationRepository) {
		super();
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
		this.conversationRepository = conversationRepository;
	}


	@GetMapping("/messages")
	public List<Message> retriveAllMessages(){
		return messageRepository.findAll();
	}
	
	@PostMapping("/messages/{userId}/{conversationId}")
	public ResponseEntity<Void> createMessage(@Valid @PathVariable("userId") Long userId, @PathVariable("conversationId") Long conversationId, @RequestBody Message message) {		
		if(userRepository.existsById(userId)) {
			if(conversationRepository.existsById(conversationId)) {
								
				Optional<ChatUser> chatUser = userRepository.findById(userId);
				Optional<Conversation> conversation = conversationRepository.findById(conversationId);
				
					message.setSender(chatUser.get());
					message.setConversation(conversation.get());
					messageRepository.save(message);
					return ResponseEntity.ok().build();					
			}
		}
		return ResponseEntity.notFound().build();
	}
	
	
//	@PostMapping("/messages")
//	public ResponseEntity<Void> createMessage(@Valid @RequestBody Message message){
//		messageRepository.save(message);
//		return ResponseEntity.ok().build();
//		
//	}
	
	@DeleteMapping("/messages/{id}")
	public ResponseEntity<Void> deleteMessageById(@PathVariable("id") Long id){
		if(!messageRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}else {
			
			messageRepository.deleteById(id);
		}
		return ResponseEntity.ok().build();
	}

}
