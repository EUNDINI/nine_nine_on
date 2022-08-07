package com.sharebook.sharebook.domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor 
@NoArgsConstructor
class MemberGenrePK implements Serializable {
	int member;
	int genre;
}

public class Member_genre {
	@Id
	@ManyToOne(targetEntity = Member.class)
	@JoinColumn(name="member_id")
	Member member;
	
	@Id
	@ManyToOne(targetEntity = Genre.class)
	@JoinColumn(name="genre_id")
	Genre genre;
}
