package com.sharebook.sharebook.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.sharebook.sharebook.service.CommunityService;
import com.sharebook.sharebook.service.MemberService;
import com.sharebook.sharebook.service.RentService;
import com.sharebook.sharebook.service.ReviewService;
import com.sharebook.sharebook.service.BookService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sharebook.sharebook.domain.Comments;
import com.sharebook.sharebook.domain.Community;
import com.sharebook.sharebook.domain.Genre;
import com.sharebook.sharebook.domain.Likes;
import com.sharebook.sharebook.domain.Member;
import com.sharebook.sharebook.domain.Member_genre;
import com.sharebook.sharebook.domain.Rent;
import com.sharebook.sharebook.domain.Review;

@Controller
public class MypageController implements ApplicationContextAware{
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private RentService rentService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Value("/upload/")
	private String uploadDirLocal;
	
	private String uploadDir;
	private WebApplicationContext context;	
	
	public void setApplicationContext(ApplicationContext appContext)
		throws BeansException {
		this.context = (WebApplicationContext) appContext;
		this.uploadDir = context.getServletContext().getRealPath(this.uploadDirLocal);
		System.out.println(this.uploadDir);
	}
	
	@GetMapping("/book/mypage.do")
	public ModelAndView showMypage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("thymeleaf/login");
		}
		else { // 로그인이 되어있는 경우;
			Member member = memberService.getMember(userSession.getMember().getMember_id());
//			System.out.println(member.getMember_id());
			List<Rent> rentList = rentService.getRentList(member);
			List<Rent> myRentList = rentService.getMyRentList(member);
			List<Likes> likeList = bookService.findLikesListByMember(member);
			int reviewCount = reviewService.getReviewByMember(member).size();
			int commentCount = communityService.findCommentByMember(member).size();
			int  communityCount =  communityService.findCommunityByUser(member).size();
			
			mav.setViewName("thymeleaf/myPage");
			mav.addObject("member", member);
			mav.addObject("rentList",rentList);
			mav.addObject("myRentList", myRentList);
			mav.addObject("likeList", likeList);
			mav.addObject("reviewCount", reviewCount);
			mav.addObject("commentCount", commentCount);
			mav.addObject("communityCount", communityCount);
			
		}	
		
		return mav;
	}
	@GetMapping("/book/myPage/listPartial.do")
	public String listPartial(@RequestParam(value = "page", required = false, defaultValue = "0") int page
			,@RequestParam(value = "category", required = false, defaultValue = "1") int category
			, ModelMap model,HttpServletRequest request)
	{
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		Member member = memberService.getMember(userSession.getMember().getMember_id());
		if(category == 2) {
			List<Review> reviewList = reviewService.getReviewByMember(member);
			model.addAttribute("reviewList", reviewList);
			return"thymeleaf/listPartial2";
		}
		else if(category == 3) {
			List<Comments> commentList = communityService.findCommentByMember(member);
			model.addAttribute("commentList", commentList);
			return"thymeleaf/listPartial3";
		}
		else {
		List<Community> communityList =  communityService.findCommunityByUser(member);
		model.addAttribute("communityList", communityList);
		return"thymeleaf/listPartial";}
	}
	
	@GetMapping("/book/mypage/member/check.do")
	public ModelAndView showMemberCheckPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
		
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("login");
		}
		else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());

			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","memberCheck");
		}	
		
		return mav;
	}
	
	@PostMapping("/book/mypage/member/check.do")
	public ModelAndView showMemberUpdatePage (HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("password") String password) throws IOException {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
		
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("thymeleaf/Login");
		}
		else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());
			boolean isMember = member.getPassword().equals(password);
			
			if (isMember == false) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>alert('비밀번호가 올바르지 않습니다.'); location.href='/book/mypage.do';</script>");
				out.flush();
			}
			List<Genre> genreList= bookService.findGenreList();
			mav.addObject("genreList",genreList);
			mav.setViewName("updateForm");
			mav.addObject("member", member);
		}	
		
		return mav;
	}
	
	@PostMapping("/book/mypage/member/update.do")
	public ModelAndView showMemberUpdatePage (HttpServletRequest request, 
			HttpServletResponse response,
			MemberCommand memberCommand,@RequestParam("genreList") int[] genres) throws IOException {		
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
		List<Member_genre> memberSelect = new ArrayList<Member_genre>();
		
	
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("login");
		}
		else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());
			Member updateMember = memberService.getMember(userSession.getMember().getMember_id());
			
			System.out.println(memberCommand);
			System.out.println(memberCommand.getPasswordCheck());
			
			if (memberCommand.getPassword().length() != 0) {
				if (memberCommand.getPassword().equals(memberCommand.getPasswordCheck())) { // 비밀번호 변경
					updateMember.setPassword(memberCommand.getPassword());
				}
			}
			if (!member.getName().equals(memberCommand.getName())) { // 이름 변경
				updateMember.setName(memberCommand.getName());
			}
			if (!member.getNickname().equals(memberCommand.getNickname())) { // 닉네임 변경
				updateMember.setNickname(memberCommand.getNickname());
			}
			if (!member.getPhone().equals(memberCommand.getPhone())) { // 전화번호 변경
				updateMember.setPhone(memberCommand.getPhone());
			}
			if (!member.getAddress1().equals(memberCommand.getAddress1())) { // 주소1 변경
				updateMember.setAddress1(memberCommand.getAddress1());
			}
			if (!member.getAddress2().equals(memberCommand.getAddress2())) { // 주소2 변경
				updateMember.setAddress2(memberCommand.getAddress2());
			}
			
			memberService.updateMember(updateMember);
			for(int i = 0; i < genres.length; i++) {
				memberSelect.add(new Member_genre(member,bookService.getGenre(genres[i])));
			}
			if(memberSelect != null) {
				memberService.createMemberGenre(memberSelect);
			}
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>alert('회원정보가 성공적으로 수정되었습니다'); location.href='/book/mypage/member/check.do';</script>");
			out.flush();
			
			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","memberUpdate");
		}	
		
		return mav;
	}
	@ResponseBody
	@PostMapping("/book/mypage/imgupload.do")
	public ModelAndView imgUpload(HttpServletRequest request,HttpServletResponse response, @RequestParam("upload") MultipartFile image) throws IOException{
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("thymeleaf/Login");
		}
		else { // 로그인이 되어있는 경우
		Member member = memberService.getMember(userSession.getMember().getMember_id());
		Map<String, String> file = uploadFile(image);
		String url = file.get("url");
		member.setImage(url);
		memberService.updateMember(member);
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<script>alert('성공적으로 수정되었습니다'); location.href='/book/mypage.do';</script>");
		out.flush();
		}
		return mav;
	}
	private Map<String,String> uploadFile(MultipartFile image) throws IOException{
		if(image.isEmpty()) {
			return null;
		}
		Map<String,String> map = new HashMap<>();
		String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
		String folder = this.uploadDir;
		System.out.print(false);
		File file = new File(folder + filename);

		try {
			image.transferTo(file);
			map.put("url","/book/images/upload/"+filename);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@GetMapping("/book/mypage/likes/book.do")
	public ModelAndView showlikesBookPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("login");
		}
		else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());

			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","likesBook");
			
			List<Likes> bookList = bookService.findLikesListByMember(member);
			System.out.println(bookList.size());
			System.out.println(bookList);
			mav.addObject("bookList", bookList);
		}	

		return mav;
	}
	
	@GetMapping("/book/mypage/likes/funding.do")
	public ModelAndView showlikesFundingPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("login");
		}
		else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());

			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","likesFunding");

		}	

		return mav;
	}
	
	@GetMapping("/book/mypage/rent.do")
	public ModelAndView showRentPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("login");
		}
		else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());

			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","rent");
			
			List<Rent> rentList = rentService.getRentList(member);
			System.out.println(rentList.size());
			System.out.println(rentList);
			mav.addObject("rentList", rentList);
		}	

		return mav;
	}
	
	@GetMapping("/book/mypage/funding.do")
	public ModelAndView showFundingPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("login");
		}
		else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());

			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","funding");
		
		}	

		return mav;
	}
	
	@GetMapping("/book/mypage/community.do")
	public ModelAndView showCommunityPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("login");
		}
		else { // 로그인이 되어있는 경우
			Member member = memberService.getMember(userSession.getMember().getMember_id());

			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","community");
			
			List<Community> communityList =  communityService.findCommunityByUser(member);
			System.out.println(communityList.size());
			System.out.println(communityList);
			mav.addObject("communityList", communityList);
		}	

		return mav;
	}
	
	@GetMapping("/book/mypage/withdrawal.do")
	public ModelAndView withdrawal (HttpServletRequest request, 
			HttpSession session) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // 로그인이 안되어있는 경우
			mav.setViewName("login");
		}
		else { // 로그인이 되어있는 경우
			session.removeAttribute("userSession");
			session.invalidate();
			
			Member member = memberService.getMember(userSession.getMember().getMember_id());
			
			mav.setViewName("redirect:/book.do");
			memberService.deleteMember(member);
		}	

		return mav;
	}
	
}
