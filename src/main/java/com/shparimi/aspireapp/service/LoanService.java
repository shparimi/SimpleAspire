package com.shparimi.aspireapp.service;

import java.util.List;

import com.shparimi.aspireapp.models.LoanDetails;

public interface LoanService {
	public LoanDetails applyLoan(LoanDetails loanDetails);

	public LoanDetails getLoanDetails(int userId, int loanId);

	public LoanDetails getLoanDetails(int loanId);

	public List<LoanDetails> getAllUserLoanDetails(int userId);
	
	public List<LoanDetails> getAllLoanDetails();
	
	public void updateLoanStatus(int loanId, String status, int userId);
	
	public LoanDetails payInstallment(int userId, int loanId, double amount);
}
