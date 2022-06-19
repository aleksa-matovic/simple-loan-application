package com.leanpay.simpleloan.service;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import com.leanpay.simpleloan.model.Request;
import com.leanpay.simpleloan.rep.RequestRepository;

@Component
public class HttpRequestLoggingFilter extends AbstractRequestLoggingFilter {

	@Autowired
	private RequestRepository requestRepository;
	
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		Request requestObj = new Request();
		requestObj.setMethod(request.getMethod());
		requestObj.setRequestUri(request.getRequestURI());
		requestObj.setRequestUrl(request.getRequestURL().toString());
		requestObj.setParameters(request.getQueryString());
		requestObj.setCreatedAt(LocalDateTime.now());
		requestRepository.save(requestObj);
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
	}

}
