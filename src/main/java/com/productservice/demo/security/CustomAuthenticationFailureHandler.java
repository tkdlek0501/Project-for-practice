package com.productservice.demo.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler{
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		
		log.info("로그인 실패!");
		
		String returnUrl = "/login"; // 로그인 실패시 이동시킬 페이지
		
		// redirect할 Url을 request에 담았다면
		// String returnUrlParam = request.getParameter("returnUrl");
		
		String errorMessage = "";
		// provider 에서 회원 조회 안됐을 때 던져준 예외 처리
		if(exception instanceof UsernameNotFoundException) {
			log.info("아이디 조회 실패");
			errorMessage = "입력하신 정보로 조회되는 아이디가 없습니다.";
			response.setContentType("text/html;charset=utf-8");
	        PrintWriter out = response.getWriter();
	        out.println("<script>");
	        out.println("alert('"+ errorMessage +"');");
	        out.println("history.back()");
	        out.println("</script>");
	        out.close();
		} else if(exception instanceof BadCredentialsException) {
			log.info("비밀번호 인증 실패");
			errorMessage = exception.getMessage();
			response.setContentType("text/html;charset=utf-8");
	        PrintWriter out = response.getWriter();
	        out.println("<script>");
	        out.println("alert('"+ errorMessage +"');");
	        out.println("history.back()");
	        out.println("</script>");
	        out.close();
		}
		
		response.setStatus(409);
		response.getWriter().print(errorMessage);
		response.getWriter().flush();
	}
	
//	private void redirect(HttpServletResponse response, String returnUrl, String errorMessage) throws IOException {
//		response.setContentType("text/html;charset=utf-8");
//		PrintWriter out = response.getWriter();
//		out.println("<script>");
//		out.println("alert('" + errorMessage + "');");
//		out.println("location.href=" + returnUrl);
//		out.println("</script>");
//		out.close();
//	}
	
}
