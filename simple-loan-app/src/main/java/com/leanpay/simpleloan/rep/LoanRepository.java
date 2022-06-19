package com.leanpay.simpleloan.rep;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leanpay.simpleloan.model.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>{

	public Loan findByLoanAmountAndInterestRateAndLoanTermAndMonths(Long loanAmount, BigDecimal interestRate, Long loanTerm, Long months);
	
}
