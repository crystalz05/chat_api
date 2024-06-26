package com.chat.app.chatApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chat.app.chatApi.user.ChatUser;

public interface UserRepository extends JpaRepository<ChatUser, Long>{

	ChatUser findByEmail(String email);

}
