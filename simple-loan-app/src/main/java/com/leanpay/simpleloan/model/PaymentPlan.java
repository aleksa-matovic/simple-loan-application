package com.leanpay.simpleloan.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment_plans")
public class PaymentPlan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@JoinColumn(name = "loan_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private Loan loan;

	private Integer rate;
	private BigDecimal monthlyPayment;
	private BigDecimal principalPayment;
	private BigDecimal remainingDebt;
	private BigDecimal totalInterest;
}
