package com.leanpay.simpleloan.service;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leanpay.simpleloan.model.Loan;
import com.leanpay.simpleloan.model.PaymentPlan;
import com.leanpay.simpleloan.rep.PaymentPlanRepository;

@Service
@Transactional
public class PaymentPlanService {

	@Autowired
	private PaymentPlanRepository paymentPlanRepository;
	
	@Autowired
	private LoanService loanService;
	
	public List<PaymentPlan> findPaymentPlans(Loan loan){
		return paymentPlanRepository.findByLoan_Id(loan.getId());
	}
	
	public List<PaymentPlan> findPaymentPlans(Long loanAmount, BigDecimal interestRate, Long loanTerm, Long months) {
		
		Loan loan = loanService.findLoan(loanAmount, interestRate, loanTerm, months);
		
		if (loan != null) {
			return paymentPlanRepository.findByLoan_Id(loan.getId());
		}
		
		return null;
	}
	
	public void savePaymentPlan(PaymentPlan paymentPlan) {
		paymentPlanRepository.save(paymentPlan);
	}
	
	public void savePaymentPlans(List<PaymentPlan> paymentPlans) {
		paymentPlanRepository.saveAll(paymentPlans);
	}
	
	public void deleteLoan(Long id) {
		paymentPlanRepository.deleteById(id);
	}
}
