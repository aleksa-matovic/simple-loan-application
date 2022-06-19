package com.leanpay.simpleloan.rep;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leanpay.simpleloan.model.PaymentPlan;

@Repository
public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Long>{

	public List<PaymentPlan> findByLoan_Id(final Long loanId);
		
}