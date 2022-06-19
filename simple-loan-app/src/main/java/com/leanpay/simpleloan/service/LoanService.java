package com.leanpay.simpleloan.service;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leanpay.simpleloan.model.Loan;
import com.leanpay.simpleloan.model.PaymentPlan;
import com.leanpay.simpleloan.rep.LoanRepository;
import com.leanpay.simpleloan.rep.PaymentPlanRepository;

@Service
@Transactional
public class LoanService {

	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private PaymentPlanRepository paymentPlanRepository;
	
	public List<Loan> getAllLoans(){
		return loanRepository.findAll();
	}
	
	public void saveLoan(Loan loan) {
		loanRepository.save(loan);
	}
	
	public Loan getLoan(Long id) {
		return loanRepository.findById(id).orElse(null);
	}
	
	public Loan findLoan(Long loanAmount, BigDecimal interestRate, Long loanTerm, Long months) {
		return loanRepository.findByLoanAmountAndInterestRateAndLoanTermAndMonths(loanAmount, interestRate, loanTerm, months);
	}
	
	public void deleteLoan(Long id) {
		
		List<PaymentPlan> paymentPlans = paymentPlanRepository.findByLoan_Id(id);
		
		if (paymentPlans != null && !paymentPlans.isEmpty()) {
			paymentPlanRepository.deleteAll(paymentPlans);
		}
		
		loanRepository.deleteById(id);
	}
	
	public void deleteLoans() {
		paymentPlanRepository.deleteAll();
		loanRepository.deleteAll();
	}
}
