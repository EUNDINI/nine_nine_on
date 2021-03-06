<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="targetUrl"><c:url value="/book/register.do" /></c:set>

<!DOCTYPE html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원가입</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
	crossorigin="anonymous">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
	integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
	crossorigin="anonymous"></script>
</head>
<body>
	<%@ include file="header.jsp"%>
	<div class="container w-75 py-5">
	<div><h2>회원가입</h2><br></div>	 
	 <form:form modelAttribute="memberCommand" action="${targetUrl}" method="post">
			 <div class="row mb-3">
				<label for="inputId" class="col-sm-2 col-form-label">아이디(이메일)</label>
				<div class="col-sm-10">
					<form:input path="email" class="form-control" placeholder="ex) name@example.com" />
					<form:errors path="email" />
				</div>
			 </div>
			 
			 <div class="row mb-3">
				<label for="inputPassword" class="col-sm-2 col-form-label">비밀번호</label>
				<div class="col-sm-10">
					<form:password path="password" class="form-control" />
					<form:errors path="password" />
				</div>
			</div>
			<div class="row mb-3">
				<label for="inputPasswordCheck" class="col-sm-2 col-form-label">비밀번호
					확인</label>
				<div class="col-sm-10">
					<form:password path="passwordCheck" class="form-control" />
					<form:errors path="passwordCheck" />
				</div>
			</div>
			<div class="row mb-3">
				<label for="inputName" class="col-sm-2 col-form-label">이름</label>
				<div class="col-sm-10">
					<form:input path="name" class="form-control" />
					<form:errors path="name" />
				</div>
			</div>
			<div class="row mb-3">
				<label for="inputNickname" class="col-sm-2 col-form-label">닉네임</label>
				<div class="col-sm-10">
					<form:input path="nickname" class="form-control" />
					<form:errors path="nickname" />
				</div>
			</div>
			<div class="row mb-3">
				<label for="inputTel" class="col-sm-2 col-form-label">전화번호</label>
				<div class="col-sm-10">
					<form:input path="phone" class="form-control" placeholder="ex) 010-0000-0000" />
					<form:errors path="phone" />
				</div>
			</div>
			<div class="row mb-3">
				<label for="inputAddress" class="col-sm-2 col-form-label">주소1</label>
				<div class="col-sm-10">
					<form:input path="address1" class="form-control" placeholder="ex) 서울특별시" />
					<form:errors path="address1" />
				</div>
			</div>
			<div class="row mb-3">
				<label for="inputAddress" class="col-sm-2 col-form-label">주소2</label>
				<div class="col-sm-10">
					<form:input path="address2" class="form-control" placeholder="ex) 성북구" />
					<form:errors path="address2" />
				</div>
			</div>
			<div class="d-grid gap-2 col-2 mx-auto">
				<button type="submit" class="btn btn-primary">가입 완료</button>
			</div>
		</form:form>	
	</div>
</body>
</html>