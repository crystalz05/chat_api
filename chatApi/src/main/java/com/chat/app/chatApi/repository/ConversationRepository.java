package com.chat.app.chatApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chat.app.chatApi.conversation.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
	


}
