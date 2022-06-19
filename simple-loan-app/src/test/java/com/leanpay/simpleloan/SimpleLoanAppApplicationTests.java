package com.leanpay.simpleloan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.leanpay.simpleloan.controller.LoanController;
import com.leanpay.simpleloan.model.Loan;
import com.leanpay.simpleloan.service.LoanService;

@SpringBootTest
class SimpleLoanAppApplicationTests {

	@Autowired
	private LoanService loanservice;
	
	@Autowired
	private LoanController loanController;
	
	@Test
	public void testCreateLoan() {
		Loan loan = new Loan();
		loan.setId(1l);
		loan.setInterestRate(new BigDecimal(5));
		loan.setLoanAmount(30000l);
		loan.setLoanTerm(7l);
		loan.setMonths(null);
		loan.setCreatedAt(LocalDateTime.now());
		
		loanservice.saveLoan(loan);
		assertNotNull(loanservice.findLoan(30000l, new BigDecimal(5), 7l, null));
	}
	
	
	@Test
	public void testMonthlyPayment() {
		double monthlyPayment1 = loanController.monthlyPayment(200000l, 6, 15, 0);
		double monthlyPayment2 = loanController.monthlyPayment(300000l, 6, 15, 0);
		double monthlyPayment3 = loanController.monthlyPayment(300000l, 7, 0, 180);
		double monthlyPayment4 = loanController.monthlyPayment(150000l, 7, 15, 0);

		assertEquals(1,687.71, monthlyPayment1);
		assertEquals(2,531.57, monthlyPayment2);
		assertEquals(2,696.48, monthlyPayment3);
		assertEquals(1,348.24, monthlyPayment4);
	}
}
