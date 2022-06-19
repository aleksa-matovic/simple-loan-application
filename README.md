# simple-loan-application

	## REST API information:

1) Get mapping - Get Payment Plans for Loan if already exist, else genereta Payment Plan and return 

	http://localhost:8080/loans/paymentPlans?loanAmount=25000&interestRate=5&loanTerm=5&months=60

2) Get mapping with Path Variables - Get Loan if already exist else do nothing

	http://localhost:8080/loans/getPaymentPlansForLoan/20000/5/5/60

3) Get mapping - Get all loans 

	http://localhost:8080/loans/all
	
4) Post mapping - Return String representation of Payment Plans

	http://localhost:8080/loans/calculate
	
{
	"loanAmount": 20000,
	"interestRate": 5,
	"loanTerm": 5,
	"months": 60
}

5) Delete mapping - delete all Loans and Payment Plans
	
	http://localhost:8080/loans/all
