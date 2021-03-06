package com.sharebook.sharebook.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.sharebook.sharebook.service.CommunityService;
import com.sharebook.sharebook.service.FundingService;
import com.sharebook.sharebook.service.MemberService;
import com.sharebook.sharebook.service.RentService;
import com.sharebook.sharebook.service.BookService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.sharebook.sharebook.domain.Community;
import com.sharebook.sharebook.domain.Funding;
import com.sharebook.sharebook.domain.Funding_order;
import com.sharebook.sharebook.domain.Likes;
import com.sharebook.sharebook.domain.Member;
import com.sharebook.sharebook.domain.Rent;

@Controller
public class MypageController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private RentService rentService;
	
	@Autowired
	private FundingService fundingService;
	
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
	
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
			Member member = memberService.getMember(userSession.getMember().getMember_id());
//			System.out.println(member.getMember_id());
			mav.setViewName("myPage");
			mav.addObject("member", member);
		}	
		
		return mav;
	}
	
	@GetMapping("/book/mypage/member/check.do")
	public ModelAndView showMemberCheckPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
		
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
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
			@RequestParam("id") String id,
			@RequestParam("password") String password) throws IOException {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
		
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
			Member member = memberService.getMember(userSession.getMember().getMember_id());
			Member isMember = memberService.findByEmailAndPassword(id, password);
			
			if (isMember == null || member != isMember) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>alert('???????????? ??????????????? ???????????? ????????????.'); location.href='/book/mypage/member/check.do';</script>");
				out.flush();
			}
			
			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","memberUpdate");
		}	
		
		return mav;
	}
	
	@PostMapping("/book/mypage/member/update.do")
	public ModelAndView showMemberUpdatePage (HttpServletRequest request, 
			HttpServletResponse response,
			MemberCommand memberCommand,
			@RequestParam("uploadImage") MultipartFile uploadImage) throws IOException {		
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
			Member member = memberService.getMember(userSession.getMember().getMember_id());
			Member updateMember = memberService.getMember(userSession.getMember().getMember_id());
			
			System.out.println(memberCommand);
			System.out.println(memberCommand.getPasswordCheck());
			
			if (memberCommand.getPassword().length() != 0) {
				if (memberCommand.getPassword().equals(memberCommand.getPasswordCheck())) { // ???????????? ??????
					updateMember.setPassword(memberCommand.getPassword());
				}
			}
			if (!member.getName().equals(memberCommand.getName())) { // ?????? ??????
				updateMember.setName(memberCommand.getName());
			}
			if (!member.getNickname().equals(memberCommand.getNickname())) { // ????????? ??????
				updateMember.setNickname(memberCommand.getNickname());
			}
			if (!member.getPhone().equals(memberCommand.getPhone())) { // ???????????? ??????
				updateMember.setPhone(memberCommand.getPhone());
			}
			if (!member.getAddress1().equals(memberCommand.getAddress1())) { // ??????1 ??????
				updateMember.setAddress1(memberCommand.getAddress1());
			}
			if (!member.getAddress2().equals(memberCommand.getAddress2())) { // ??????2 ??????
				updateMember.setAddress2(memberCommand.getAddress2());
			}
			if (!member.getImage().equals(memberCommand.getImage())) { // ????????? ?????? ??????
				String filename = uploadFile(uploadImage);
				updateMember.setImage(this.uploadDirLocal + filename);
				System.out.println(updateMember.getImage());
			} 
			
			memberService.updateMember(updateMember);
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>alert('??????????????? ??????????????? ?????????????????????'); location.href='/book/mypage/member/check.do';</script>");
			out.flush();
			
			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","memberUpdate");
		}	
		
		return mav;
	}
	
	private String uploadFile(MultipartFile image) {
		String filename = image.getOriginalFilename();
		System.out.println(filename);
		File file = new File(this.uploadDir + filename);
		try {
			image.transferTo(file);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	@GetMapping("/book/mypage/likes/book.do")
	public ModelAndView showlikesBookPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
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
	
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
			Member member = memberService.getMember(userSession.getMember().getMember_id());

			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","likesFunding");
			
			// ??????????????? ?????? ??????
			List<Funding_order> FundingOrderList = fundingService.searchFunding_orderListByMember_id(member);
			mav.addObject("FundingOrderList", FundingOrderList);
		}	

		return mav;
	}
	
	@GetMapping("/book/mypage/rent.do")
	public ModelAndView showRentPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
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
	
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
			Member member = memberService.getMember(userSession.getMember().getMember_id());

			mav.setViewName("myPage");
			mav.addObject("member", member);
			mav.addObject("category","funding");
			
			List<Funding> fundingList = fundingService.searchFundingListByMember_id(member);
			System.out.println(fundingList.size());
			System.out.println(fundingList);
			mav.addObject("fundingList", fundingList);
		}	

		return mav;
	}
	
	@GetMapping("/book/mypage/community.do")
	public ModelAndView showCommunityPage (HttpServletRequest request) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		ModelAndView mav = new ModelAndView();
	
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
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
	
		if (userSession == null) { // ???????????? ??????????????? ??????
			mav.setViewName("login");
		}
		else { // ???????????? ???????????? ??????
			session.removeAttribute("userSession");
			session.invalidate();
			
			Member member = memberService.getMember(userSession.getMember().getMember_id());
			
			mav.setViewName("redirect:/book.do");
			memberService.deleteMember(member);
		}	

		return mav;
	}
	
}
