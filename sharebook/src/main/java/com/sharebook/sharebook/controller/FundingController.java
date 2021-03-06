package com.sharebook.sharebook.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import com.sharebook.sharebook.domain.Funding;
import com.sharebook.sharebook.domain.Funding_order;
import com.sharebook.sharebook.domain.Likes_funding;
import com.sharebook.sharebook.domain.Member;
import com.sharebook.sharebook.domain.Reward;
import com.sharebook.sharebook.service.FundingService;

@Controller
public class FundingController {
	
	@Autowired
	private FundingService fundingService;
	
	@Value("/upload/")
	private String uploadDirLocal;
	
	private String uploadDir;
	private WebApplicationContext context;	
	
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		this.context = (WebApplicationContext) appContext;
		this.uploadDir = context.getServletContext().getRealPath(this.uploadDirLocal);
		System.out.println(this.uploadDir);
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

	@GetMapping("/book/funding/view/create.do")
	public String createFunding(HttpServletRequest request,
			@ModelAttribute("funding") Funding funding) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		if (userSession == null) {
			return "redirect:/book/login.do";
		}
		return "thymeleaf/createFunding";
	}
	
	@PostMapping("/book/funding/create.do")
	public String createFunding(HttpServletRequest request,
			@ModelAttribute("funding") Funding funding, BindingResult bindingResult,
			@RequestParam("file") MultipartFile image) throws ParseException {
		if (!StringUtils.hasText(funding.getTitle())) {
		    bindingResult.addError(new FieldError("funding", "title", "????????? ????????? ?????????"));
		}
		if (!StringUtils.hasText(funding.getAuthor())) {
		    bindingResult.addError(new FieldError("funding", "author", "????????? ????????? ?????????"));
		}
		if (!StringUtils.hasText(funding.getDescription())) {
		    bindingResult.addError(new FieldError("funding", "description", "????????? ????????? ?????????"));
		}
		if (funding.getGoal_amount() <= 0) {
		    bindingResult.addError(new FieldError("funding", "goal_amount", "????????? ????????? ????????? ?????????"));
		}
		if (funding.getDeadline() == null) {
		    bindingResult.addError(new FieldError("funding", "deadline", "?????? ????????? ????????? ?????????"));
		}
		
		String filename = uploadFile(image);
		if (filename.trim().equals("")) {
			bindingResult.addError(new FieldError("funding", "image", "???????????? ????????? ?????????"));
		}
		funding.setImage(this.uploadDirLocal + filename);
		
		if (bindingResult.hasErrors()) {
			return "thymeleaf/createFunding";
		}
		
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		funding.setMember(userSession.getMember());
		
		fundingService.insertFunding(funding);
		
		int funding_id = funding.getFunding_id();
		
		int reward_index = 1;
		String reward_price = "";
		String reward_prize = "";
		while (reward_price != null) {
			reward_price = request.getParameter("reward" + reward_index);
			if (reward_price == null) {
				continue;
			}
			reward_prize = request.getParameter("reward" + reward_index + "_description");
			
			Reward reward = new Reward();
			reward.setPrice(Integer.parseInt(reward_price));
			reward.setPrize(reward_prize);
			reward.setImage("/images/ex_image.png");
			reward.setFunding(fundingService.getFunding(funding_id));
			
			fundingService.insertReward(reward);
			
			reward_index++;
		}
		
		return "redirect:/book/funding/" + funding_id + ".do";
	}
	
	@GetMapping("/book/funding.do")
	public ModelAndView fundingList(HttpServletRequest request) {
		String sortType = request.getParameter("sortType");
		
		ModelAndView modelAndView = new ModelAndView("fundingList");
		modelAndView.addObject("sortingType", sortType);
		
		if (sortType == null) {
			modelAndView.addObject("fundingList", fundingService.getFundingList());
			return modelAndView;
		}
		
		modelAndView.addObject("fundingList", fundingService.getFundingListSorted(Integer.parseInt(sortType)));
		return modelAndView;
	}

	@PostMapping("/book/funding/search.do")
	public ModelAndView searchFunding(HttpServletRequest request) {
		String keyword = request.getParameter("keyword");
		List<Funding> fundingList = new ArrayList<>();
		if(keyword == null) {
			fundingList.addAll(fundingService.getFundingList());
		}
		else {
			fundingList.addAll(fundingService.searchFundingListByTitle(keyword));
			fundingList.addAll(fundingService.searchFundingListByAuthor(keyword));
		}
		return new ModelAndView("fundingList", "fundingList", fundingList);
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/book/funding/{fundingId}.do")
	public ModelAndView detailFunding(HttpServletRequest request, @PathVariable int fundingId) {
		ModelAndView modelAndView = new ModelAndView("detailFunding");
		
		Funding funding = fundingService.getFunding(fundingId);
		funding.setViews(funding.getViews() + 1);
		fundingService.updateFunding(funding);
		
		long deadline = funding.getDeadline().getTime() / 1000 / 60 / 60 / 24;
		long today = System.currentTimeMillis() / 1000 / 60 / 60 / 24;
		
		modelAndView.addObject("funding", funding);
		modelAndView.addObject("dDay", (int) (deadline - today) + 1);
		modelAndView.addObject("likesCount", fundingService.getLikes_fundingCount(funding));
		modelAndView.addObject("rewardList", fundingService.getRewardList(funding));
		
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		String likes = "";
		if (userSession == null) {
			likes = "/images/blank_likes.png";
		} else {
			if (fundingService.getLikes_funding(funding, userSession.getMember()) != null) {
				likes = "/images/filled_likes.png";
			} else {
				likes = "/images/blank_likes.png";
			}
		}
		modelAndView.addObject("likes", likes);
		
		List<Funding_order> orderList = fundingService.searchFunding_orderListByFunding_id(funding);
		double sum = 0;
		for (Funding_order order : orderList) {
			sum += order.getPrice();
		}
		modelAndView.addObject("achievementRate", sum / funding.getGoal_amount() * 100);
		
//		/book/funding/order.do ?????? redirect??? ????????? ???
		Map<String, ?> redirectRequest = RequestContextUtils.getInputFlashMap(request);
		Map<String, String> orderInfo = null;
		if (redirectRequest != null) {
			orderInfo = (Map<String, String>) redirectRequest.get("orderInfo");
			
			modelAndView.addObject("orderSuccess", orderInfo.get("orderSuccess"));
			modelAndView.addObject("orderSuccessPrice", orderInfo.get("orderSuccessPrice"));
			modelAndView.addObject("getReward", orderInfo.get("getReward"));
		}
		
		return modelAndView;
	}
	
	@GetMapping("/book/funding/{fundingId}/likes.do")
	public String likesFunding(HttpServletRequest request, @PathVariable int fundingId) {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		if (userSession == null) {
			return "redirect:/book/login.do";
		}
		
		Funding funding = fundingService.getFunding(fundingId);
		
		Member member = userSession.getMember();
		
		Likes_funding likes_funding = fundingService.getLikes_funding(funding, member);
		if (likes_funding != null) {
			fundingService.deleteLikes_funding(likes_funding);
		} else {
			likes_funding = new Likes_funding(member, funding);
			fundingService.insertLikes_funding(likes_funding);
		}
		
		return "redirect:/book/funding/" + fundingId + ".do";
	}
	
	@PostMapping("/book/funding/order.do")
	public String orderFunding(HttpServletRequest request, RedirectAttributes redirect) throws ParseException {
		UserSession userSession = 
				(UserSession) WebUtils.getSessionAttribute(request, "userSession");
		if (userSession == null) {
			return "redirect:/book/login.do";
		}
		
		int price = Integer.parseInt(request.getParameter("price"));
		int fundingId = Integer.parseInt(request.getParameter("fundingId"));
		
		Funding funding = fundingService.getFunding(fundingId);
		
		Member member = userSession.getMember();
		
		List<Reward> rewardList = fundingService.getRewardList(funding);
		Reward getReward = null;
		int prizePrice = 0;
		for (Reward reward : rewardList) {
			if (price >= reward.getPrice() && reward.getPrice() > prizePrice) {
				getReward = reward;
				prizePrice = reward.getPrice();
			}
		}
		
		Funding_order funding_order = new Funding_order();
		funding_order.setFunding(funding);
		funding_order.setMember(member);
		funding_order.setPrice(price);
		funding_order.setReward(getReward);
		
		fundingService.insertFunding_order(funding_order);
		
		Map<String, String> orderInfo = new HashMap<String, String>();
		orderInfo.put("orderSuccess", String.valueOf(1));
		orderInfo.put("orderSuccessPrice", String.valueOf(price));
		orderInfo.put("getReward", getReward != null ? getReward.getPrize() : "");
		redirect.addFlashAttribute("orderInfo", orderInfo);
		
		return "redirect:/book/funding/" + fundingId + ".do";
	}
	
}
