package com.chat.app.chatApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chat.app.chatApi.conversation.Message;

public interface MessageRepository extends JpaRepository<Message, Long>{

}
