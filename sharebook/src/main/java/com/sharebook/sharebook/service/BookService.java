package com.sharebook.sharebook.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sharebook.sharebook.domain.Book;
import com.sharebook.sharebook.domain.Funding;
import com.sharebook.sharebook.domain.Likes;
import com.sharebook.sharebook.domain.Member;
import com.sharebook.sharebook.repository.BookRepository;
import com.sharebook.sharebook.repository.LikesRepository;

@Service
public class BookService {
	@Autowired
	public BookRepository bookRepository;
	@Autowired
	public LikesRepository likesRepository;
	
	public void setBookService(BookRepository bookRepository, LikesRepository likesRepository) {
		this.bookRepository = bookRepository;
		this.likesRepository = likesRepository;
	}
	
	/*
	 * CRUDRepository Method 
	 */
	public Book saveBook(Book book) {
		// Update 시에도 사용!
		return bookRepository.save(book);
	}
	
	public void deleteBook(int book_id) {
		bookRepository.deleteById(book_id);
		return;
	}
	
	public Likes saveLikes(Likes likes) {
		// Update 시에도 사용!
		return likesRepository.save(likes);
	}
	
	/* 나중에 수정 필요
	public void deleteLikes(int likes_id) {
		likesRepository.deleteById(likes_id);
		return;
	}
	*/
	
	/*
	 * Book Return Method 
	 */
	public Book findBookById(int bookId) {
		Optional<Book> book = bookRepository.findById(bookId);
		if(book.isPresent())
			return book.get();
		return null;
	}
	
	public Book findBookByTitle(String title) {
		Optional<Book> book = bookRepository.findBookByTitle(title);
		if(book.isPresent())
			return book.get();
		return null;
	}
	
	public Book findBookByAuthor(String author) {
		Optional<Book> book = bookRepository.findBookByTitle(author);
		if(book.isPresent())
			return book.get();
		return null;
	}
	
	public Likes findLikesByMemberAndBook(Member member, Book book) {
		Optional<Likes> likes = likesRepository.findByMemberAndBook(member, book);
		if(likes.isPresent())
			return likes.get();
		return null;
	}
	
	/*
	 * List Return Method 
	 */
	public List<Book> findBookList(){
		return (List<Book>)bookRepository.findAll();
	}
	
	public List<Book> findBookListByMember(Member member){
		return bookRepository.findAllByMember(member);
	}
	
	public List<Book> findRecommendBookList(){
		return bookRepository.findFirst3ByOrderByViewsDesc();
	}
	
	public List<Book> findNearBookList(Member member){
		return bookRepository.findAllByMember_Address1AndMember_Address2(member.getAddress1(), member.getAddress2());
	}
	
	public List<Book> findBookListByTitle(String title){
		return bookRepository.findAllByTitleContaining(title);
	}
	
	public List<Book> findBookListByAuthor(String author){
		return bookRepository.findAllByAuthorContaining(author);
	}
	
	public List<Book> findBookListSorted(int sortType) {
		switch (sortType) {
		case 1:
			return (List<Book>) bookRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
		case 2:
			return (List<Book>) bookRepository.findAll(Sort.by(Sort.Direction.DESC, "views"));
		default:
			return (List<Book>) bookRepository.findAll();
		}
	}
	
	public List<Book> findBookListByTitleSorted(String title, int sortType) {
		switch (sortType) {
		case 1:
			return (List<Book>) bookRepository.findAllByTitleContaining(title, Sort.by(Sort.Direction.ASC, "title"));
		case 2:
			return (List<Book>) bookRepository.findAllByTitleContaining(title, Sort.by(Sort.Direction.DESC, "views"));
		default:
			return (List<Book>) bookRepository.findAllByTitleContaining(title);
		}
	}
	
	public List<Book> findBookListByAuthorSorted(String author, int sortType) {
		switch (sortType) {
		case 1:
			return (List<Book>) bookRepository.findAllByTitleContaining(author, Sort.by(Sort.Direction.ASC, "title"));
		case 2:
			return (List<Book>) bookRepository.findAllByTitleContaining(author, Sort.by(Sort.Direction.DESC, "views"));
		default:
			return (List<Book>) bookRepository.findAllByTitleContaining(author);
		}
	}
	
	
	public List<Likes> findLikesListByMember(Member member){
		return likesRepository.findAllByMember(member);
	}
	
	public List<Likes> findLikesListByBook(Book book){
		return likesRepository.findAllByBook(book);
	}
}
