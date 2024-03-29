package com.sharebook.sharebook.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.sharebook.sharebook.domain.Book;
import com.sharebook.sharebook.domain.Genre;
import com.sharebook.sharebook.domain.Member;
import com.sharebook.sharebook.domain.Member_genre;
import com.sharebook.sharebook.domain.Notice;
import com.sharebook.sharebook.domain.Review;
import com.sharebook.sharebook.service.BookService;
import com.sharebook.sharebook.service.MemberService;
import com.sharebook.sharebook.service.NoticeService;
import com.sharebook.sharebook.service.ReviewService;

@Controller
public class MainController {
	@Autowired
	public BookService bookService;
	@Autowired
	public MemberService memberService;
	@Autowired
	public ReviewService reviewService;
	@Autowired
	public NoticeService noticeService;

	@RequestMapping("/")
	public ModelAndView handleRequest() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/book.do");
		return mav;
	}

	@RequestMapping("/book.do")
	public ModelAndView viewMain(HttpServletRequest request) {
		UserSession userSession = (UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();

		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.addObject("isLogin", false);
		} else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());
			mav.addObject("isLogin", true);

			List<Member_genre> memberGenreList = memberService.findMember_genreListByMember(member);
			if (memberGenreList.size() > 0) {
				// int randomNum = (int) (Math.random() * memberGenreList.size()) - 1;
				Genre genre = memberGenreList.get(0).getGenre();

				List<Book> allRecommendBookList = bookService.findBookListByGenre(genre);
				List<Book> recommendBookList = new ArrayList<>();
				for (int i = 0; i < allRecommendBookList.size(); i++) {
					if (i >= 5) {
						break;
					}
					recommendBookList.add(allRecommendBookList.get(i));
				}

				mav.addObject("myGenre", genre);
				mav.addObject("recommendBookList", recommendBookList);
			}
		}

		List<Book> popularBookList = bookService.findPopularBookList();
		List<Book> allRecentBookList = bookService.findBookListSorted(3);
		List<Book> recentBookList = new ArrayList<>(allRecentBookList.subList(0, 5));

		// Review
		List<Review> reviewList = reviewService.findRecommendReview();
		String regex = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";

		for (int i = 0; i < reviewList.size(); i++) {
			String content = reviewList.get(i).getContent().replaceAll(regex, "");
			reviewList.get(i).setContent(content);
		}

		List<Review> partialReviewList1 = new ArrayList<>(reviewList.subList(0, 2));
		List<Review> partialReviewList2 = new ArrayList<>(reviewList.subList(2, 4));

		// Notice
		List<Notice> noticeList = noticeService.getpinList();

		mav.setViewName("thymeleaf/main");
		mav.addObject("popularBookList", popularBookList);
		mav.addObject("recentBookList", recentBookList);
		mav.addObject("reviewList1", partialReviewList1);
		mav.addObject("reviewList2", partialReviewList2);
		mav.addObject("noticeList", noticeList);
		return mav;
	}

	/*
	 * header and footer
	 */
	@RequestMapping("/book/header.do")
	public ModelAndView loadHeader() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("thymeleaf/fragments/header");
		return mav;
	}

	@RequestMapping("/book/category.do")
	public ModelAndView loadCategory() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("thymeleaf/category");
		return mav;
	}

	@RequestMapping("/book/brand/menu.do")
	public ModelAndView loadBrandMenu() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("thymeleaf/brandTooltip");
		return mav;
	}

	@RequestMapping("/book/menu.do")
	public ModelAndView loadBookMenu() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("thymeleaf/bookTooltip");
		return mav;
	}

	@RequestMapping("/book/community/menu.do")
	public ModelAndView loadCommunityMenu() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("thymeleaf/communityTooltip");
		return mav;
	}

	@RequestMapping("/book/footer.do")
	public ModelAndView loadFooter() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("thymeleaf/fragments/footer");
		return mav;
	}
}
