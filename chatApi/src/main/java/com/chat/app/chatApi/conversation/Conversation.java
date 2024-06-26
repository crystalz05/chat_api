package com.chat.app.chatApi.conversation;

import java.util.List;

import com.chat.app.chatApi.user.ChatUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;


@Entity
public class Conversation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


    @ManyToMany
	private List<ChatUser> participants;
   

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ChatUser> getParticipants() {
		return participants;
	}

	public void setParticipants(List<ChatUser> participants) {
		this.participants = participants;
	}

}
