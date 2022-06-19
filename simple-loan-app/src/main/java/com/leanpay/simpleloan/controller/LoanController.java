package com.leanpay.simpleloan.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leanpay.simpleloan.model.Loan;
import com.leanpay.simpleloan.model.PaymentPlan;
import com.leanpay.simpleloan.service.LoanService;
import com.leanpay.simpleloan.service.PaymentPlanService;

@RestController
@RequestMapping("/loans")
public class LoanController {	
	
	@Autowired
	private LoanService loanService;
	
	@Autowired
	private PaymentPlanService paymentPlanService;
	
	
	/**
     * @return Loan List 
     */
	@GetMapping("/all")
	public List<Loan> getAllLoans(){
		return loanService.getAllLoans();
	}
	
	
	/**
     * @return ResponseEntity object 
     */
	@DeleteMapping("/all")
	public ResponseEntity<?> deleteAllLoans(){
		loanService.deleteLoans();
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	
	/**
     * @param id of Loan object
     * @return ResponseEntity object 
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteLoan(@PathVariable Long id){
		
		Loan loan = loanService.getLoan(id);
		
		if (loan != null) {
			loanService.deleteLoan(id);
			return ResponseEntity.status(HttpStatus.OK).body("Loan with id=" + id + " deleted!");
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Loan found - can not delete!");
	}

	
	/**
     * @param loanAmount
     * @param interestRate
     * @param loanTerm
     * @param months
     * @return ResponseEntity object 
     */
	@GetMapping("/getPaymentPlansForLoan/{loanAmount}/{interestRate}/{loanTerm}/{months}")
	public ResponseEntity<?> getAllPaymentPlansForLoan(@PathVariable Long loanAmount, @PathVariable String interestRate, @PathVariable Long loanTerm, @PathVariable Long months){
		
		List<PaymentPlan> paymentPlans = paymentPlanService.findPaymentPlans(loanAmount, new BigDecimal(interestRate), loanTerm, months);
		
		if (paymentPlans != null && !paymentPlans.isEmpty()) {
			return ResponseEntity.accepted().body(paymentPlans);
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Payment Plans found!");
	}
	
	
	/**
     * @param loanAmount
     * @param interestRate
     * @param loanTerm
     * @param months
     * @return Payment Plans List
     */
	@GetMapping("/paymentPlans")
	public List<PaymentPlan> getPaymentPlans(@RequestParam Long loanAmount, @RequestParam BigDecimal interestRate, @RequestParam Long loanTerm, @RequestParam Long months) {
		
        Loan loanExist = loanService.findLoan(loanAmount, interestRate, loanTerm, months);
        Loan loan = null;
        if (loanExist != null) {
        	
        	List<PaymentPlan> paymentPlansDB = paymentPlanService.findPaymentPlans(loanExist);
        	
        	if (paymentPlansDB != null && !paymentPlansDB.isEmpty()) {
        		return paymentPlansDB;
        	}
        } else {
        	loan = new Loan();
        	loan.setLoanAmount(loanAmount);
        	loan.setLoanTerm(loanTerm);
        	loan.setMonths(months);
        	loan.setInterestRate(interestRate);
        	loan.setCreatedAt(LocalDateTime.now());
        	loanService.saveLoan(loan);
        }
		
		double principal = loan.getLoanAmount().doubleValue();
		double annualInterestRate = loan.getInterestRate().doubleValue();
		
		int numYears = loan.getLoanTerm() != null ? loan.getLoanTerm().intValue() : 0;
		
        double interestPaid, principalPaid, newBalance;
        double monthlyInterestRate, monthlyPayment;
        int numMonths, month;
        
        if (numYears != 0) {
        	numMonths = numYears * 12;
        } else {
        	numMonths = loan.getMonths() != null ? loan.getMonths().intValue() : 0;
        }
       
        // Output monthly payment and total payment
        monthlyInterestRate = annualInterestRate / 12;
        monthlyPayment = monthlyPayment(principal, monthlyInterestRate, numYears, numMonths);

        double totalInterestPaid = (numMonths * monthlyPayment) - loan.getLoanAmount();
        BigDecimal totalInterest = new BigDecimal(totalInterestPaid).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal monthly_Payment = new BigDecimal(monthlyPayment).setScale(2, BigDecimal.ROUND_HALF_EVEN);  
        
		List<PaymentPlan> paymentPlans = new ArrayList<PaymentPlan>();
        
		System.out.println("# | Payment Amount | Principal Amount | Interest Amount | Balance Owed | Total Interest \n");
		
        for (month = 1; month <= numMonths; month++) {
        	
            // Compute amount paid and new balance for each payment period
            interestPaid = principal * (monthlyInterestRate / 100);
            principalPaid = monthlyPayment - interestPaid;
            newBalance = principal - principalPaid;            
            
    		PaymentPlan paymentPlan = new PaymentPlan();
    		paymentPlan.setLoan(loan);
    		paymentPlan.setMonthlyPayment(monthly_Payment);
    		paymentPlan.setRate(month);
    		paymentPlan.setPrincipalPayment(new BigDecimal(principalPaid).setScale(2, BigDecimal.ROUND_HALF_EVEN));
    		paymentPlan.setRemainingDebt(new BigDecimal(newBalance).setScale(2, BigDecimal.ROUND_HALF_EVEN));
    		paymentPlan.setTotalInterest(totalInterest);
    		paymentPlans.add(paymentPlan);
            
            System.out.format("%8d%10.2f%10.2f%10.2f%12.2f%12.2f\n", month, monthlyPayment, interestPaid, principalPaid, newBalance, totalInterest);

            // Update the balance
            principal = newBalance;
        }
		
        paymentPlanService.savePaymentPlans(paymentPlans);
		
		return paymentPlans;
	}
	
	
	/**
     * @param loan
     * @return Payment Plans as a String result
     */
	@PostMapping("/calculate")
	public String getPaymentPlan(@RequestBody Loan loan) {
		
        Loan loanExist = loanService.findLoan(loan.getLoanAmount(), loan.getInterestRate(), loan.getLoanTerm(), loan.getMonths());
        if (loanExist != null) {
        	loan = loanExist;
        } else {
        	loan.setCreatedAt(LocalDateTime.now());
        	loanService.saveLoan(loan);
        }

		String returnValue = "    #     | Monthly | Principal | Interest | Balance | Total Interest \n";
		
		double principal = loan.getLoanAmount().doubleValue();
		double annualInterestRate = loan.getInterestRate().doubleValue();
		int numYears = loan.getLoanTerm() != null ? loan.getLoanTerm().intValue() : 0;
		
        double interestPaid, principalPaid, newBalance;
        double monthlyInterestRate, monthlyPayment;
        int month;
        int numMonths = numYears * 12;
    
        if (numYears != 0) {
        	numMonths = numYears * 12;
        } else {
        	numMonths = loan.getMonths() != null ? loan.getMonths().intValue() : 0;
        }
        
        // Output monthly payment and total payment
        monthlyInterestRate = annualInterestRate / 12;
        monthlyPayment = monthlyPayment(principal, monthlyInterestRate, numYears, numMonths);

        double totalInterestPaid = (numMonths * monthlyPayment) - loan.getLoanAmount();
        BigDecimal totalInterest = new BigDecimal(totalInterestPaid).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal monthly_Payment = new BigDecimal(monthlyPayment).setScale(2, BigDecimal.ROUND_HALF_EVEN);  
        
		List<PaymentPlan> paymentPlans = new ArrayList<PaymentPlan>();

        for (month = 1; month <= numMonths; month++) {
        	
            // Compute amount paid and new balance for each payment period
            interestPaid = principal * (monthlyInterestRate / 100);
            principalPaid = monthlyPayment - interestPaid;
            newBalance = principal - principalPaid;
            
    		PaymentPlan paymentPlan = new PaymentPlan();
    		paymentPlan.setLoan(loan);
    		paymentPlan.setMonthlyPayment(monthly_Payment);
    		paymentPlan.setRate(month);
    		paymentPlan.setPrincipalPayment(new BigDecimal(principalPaid));
    		paymentPlan.setRemainingDebt(new BigDecimal(newBalance));
    		paymentPlan.setTotalInterest(totalInterest);
    		paymentPlans.add(paymentPlan);
            
            returnValue += String.format("%8d",month) + String.format("%10.2f",monthlyPayment) + String.format("%10.2f",principalPaid) + String.format("%10.2f",interestPaid) + String.format("%12.2f",newBalance) + String.format("%12.2f",totalInterest) + "\n";
            System.out.format("%8d%10.2f%10.2f%10.2f%12.2f%12.2f\n", month, monthlyPayment, interestPaid, principalPaid, newBalance, totalInterest);

            // Update the balance
            principal = newBalance;
        }
        
    	paymentPlanService.savePaymentPlans(paymentPlans);

		return returnValue;
	}
	
	
	
    /**
     * @param loanAmount
     * @param monthlyInterestRate in percent
     * @param numberOfYears
     * @return the amount of the monthly payment of the loan
     */
    public double monthlyPayment(double loanAmount, double monthlyInterestRate, int numberOfYears, int numberOfMonths) {
        monthlyInterestRate /= 100;  // e.g. 5% => 0.05
        
        if (numberOfYears != 0) {
        	return loanAmount * monthlyInterestRate / ( 1 - 1 / Math.pow(1 + monthlyInterestRate, numberOfYears * 12) );
        }
        
    	return loanAmount * monthlyInterestRate / ( 1 - 1 / Math.pow(1 + monthlyInterestRate, numberOfMonths) );
    }
}
