package com.sharebook.sharebook.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor 
@NoArgsConstructor
@Table(name="RENT")
public class Rent {
	@Id
	@Column(name="RENT_ID")
	private int rent_id;
	
	@Column(name="START_DAY")
	private Date start_day;
	
	@Column(name="END_DAY")
	private Date end_day;
	
	@OneToOne
	@JoinColumn(name="BOOK_ID")
	@Column(name="BOOK_ID")
	private int book_id;
	
	@ManyToOne
	@JoinColumn(name="MEMBER_ID")
	@Column(name="MEMBER_ID")
	private int member_id;
}
