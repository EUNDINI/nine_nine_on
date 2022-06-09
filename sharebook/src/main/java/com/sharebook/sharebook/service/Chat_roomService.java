package com.sharebook.sharebook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sharebook.sharebook.domain.Chat_room;
import com.sharebook.sharebook.domain.Member;
import com.sharebook.sharebook.domain.Membership;
import com.sharebook.sharebook.domain.Message;
import com.sharebook.sharebook.repository.Chat_roomRepository;
import com.sharebook.sharebook.repository.MembershipRepository;
import com.sharebook.sharebook.repository.MessageRepository;

@Service
public class Chat_roomService {
	@Autowired
	public Chat_roomRepository chat_roomRepository;
	@Autowired
	public MembershipRepository membershipRepository;
	@Autowired
	public MessageRepository messageRepository;
	
	public void setChat_roomService(Chat_roomRepository chat_roomRepository,
			MembershipRepository membershipRepository, MessageRepository messageRepository) {
		this.chat_roomRepository = chat_roomRepository;
		this.membershipRepository = membershipRepository;
		this.messageRepository = messageRepository;
	}
	
	/*
	 * CRUDRepository Method 
	 */
	public Chat_room saveChat_room(Chat_room chat_room) {
		return chat_roomRepository.save(chat_room);
	}
	
	public Membership saveMembership(Membership membership) {
		return membershipRepository.save(membership);
	}
	
	public Message saveMessage(Message message) {
		return messageRepository.save(message);
	}
	
	/*
	 * List Return Method 
	 */
	public List<Membership> findMembershipListByMember(Member member){
		return membershipRepository.findAllByMember(member);
	}
	
	public List<Message> findMessageListByMember(Member member){
		return messageRepository.findAllByMember(member);
	}
}
