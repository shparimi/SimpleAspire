package com.shparimi.aspireapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.shparimi.aspireapp.exceptions.BadRequestException;
import com.shparimi.aspireapp.exceptions.NotFoundException;
import com.shparimi.aspireapp.exceptions.PaymentException;
import com.shparimi.aspireapp.exceptions.WebApplicationException;
import com.shparimi.aspireapp.models.LoanDetails;
import com.shparimi.aspireapp.models.LoanRequest;
import com.shparimi.aspireapp.models.LoanUpdateRequest;
import com.shparimi.aspireapp.models.RepaymentRequest;
import com.shparimi.aspireapp.service.LoanService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LoanController {

	@Autowired
	private LoanService loanService;

	@PostMapping("/user/{userId}/applyLoan")
	@PreAuthorize("hasRole('CUSTOMER') AND @userServiceImpl.isUser(#userId)")
	@ResponseBody
	public LoanDetails applyLoan(@PathVariable int userId, @Valid @RequestBody LoanRequest loanRequest) {
		try {
			LoanDetails loanDetails = LoanDetails.builder().amount(loanRequest.getAmount()).term(loanRequest.getTerm())
					.userId(userId).build();
			loanDetails = loanService.applyLoan(loanDetails);
			log.info("Successfully applied for loan {} by user ", loanDetails.getId(), userId);
			return loanDetails;
		} catch (Exception e) {
			log.error("Error while trying to apply for loan {}", e);
			throw new WebApplicationException(e.getMessage());
		}
	}

	@GetMapping("/user/{userId}/loans")
	@PreAuthorize("(hasRole('CUSTOMER') AND @userServiceImpl.isUser(#userId)) OR (hasRole('MANAGER'))")
	@ResponseBody
	public List<LoanDetails> getAllLoans(@PathVariable int userId) {
		try {
			SimpleGrantedAuthority authority = (SimpleGrantedAuthority) SecurityContextHolder.getContext()
					.getAuthentication().getAuthorities().toArray()[0];
			List<LoanDetails> loanDetails = new ArrayList<>();
			if (authority.getAuthority().equals("ROLE_CUSTOMER")) {
				loanDetails = loanService.getAllUserLoanDetails(userId);
			} else {
				loanDetails = loanService.getAllLoanDetails();
			}
			return loanDetails;
		} catch (Exception e) {
			log.error("Error while trying to fetch all loans", e);
			throw new WebApplicationException(e.getMessage());
		}
	}

	@GetMapping("/user/{userId}/loan/{loanId}")
	@PreAuthorize("(hasRole('CUSTOMER') AND @userServiceImpl.isUser(#userId)) OR (hasRole('MANAGER'))")
	@ResponseBody
	public LoanDetails getLoan(@PathVariable int userId, @PathVariable int loanId) {
		LoanDetails loanDetails = null;
		try {
			SimpleGrantedAuthority authority = (SimpleGrantedAuthority) SecurityContextHolder.getContext()
					.getAuthentication().getAuthorities().toArray()[0];
			if (authority.getAuthority().equals("ROLE_CUSTOMER")) {
				loanDetails = loanService.getLoanDetails(userId, loanId);
			} else {
				loanDetails = loanService.getLoanDetails(loanId);
			}
			return loanDetails;
		} catch (Exception e) {
			log.error("Error while trying to fetch loan {}", loanId, e);
			throw new WebApplicationException(e.getMessage());
		}
	}

	@PatchMapping("/user/{userId}/loan/{loanId}/status")
	@PreAuthorize("hasRole('MANAGER') AND @userServiceImpl.isUser(#userId)")
	@ResponseBody
	public void updateLoanStatus(@PathVariable int userId, @PathVariable int loanId,
			@Valid @RequestBody LoanUpdateRequest loanUpdateRequest) {
		try {
			if (!loanUpdateRequest.getStatus().equals("APPROVED")
					&& !loanUpdateRequest.getStatus().equals("REJECTED")) {
				throw new BadRequestException("User can only set APPROVED or REJECTED");
			}
			loanService.updateLoanStatus(loanId, loanUpdateRequest.getStatus(), userId);
		} catch (Exception e) {
			log.error("Error while trying to update loan {}", loanId, e);
			if (e instanceof NotFoundException || e instanceof BadRequestException) {
				throw e;
			}
			throw new WebApplicationException(e.getMessage());
		}
	}

	@PostMapping("/user/{userId}/loan/{loanId}/installment")
	@PreAuthorize("hasRole('CUSTOMER') AND @userServiceImpl.isUser(#userId)")
	@ResponseBody
	public LoanDetails payInstallment(@PathVariable int userId, @PathVariable int loanId,
			@Valid @RequestBody RepaymentRequest repaymentRequest) {
		try {
			return loanService.payInstallment(userId, loanId, repaymentRequest.getInstallmentAmout());
		} catch (Exception e) {
			log.error("Error while trying to update loan {}", loanId, e);
			if (e instanceof NotFoundException || e instanceof PaymentException) {
				throw e;
			}
			throw new WebApplicationException(e.getMessage());
		}
	}

}
