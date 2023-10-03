package com.shparimi.aspireapp.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shparimi.aspireapp.dao.LoanDAO;
import com.shparimi.aspireapp.dao.models.Loan;
import com.shparimi.aspireapp.dao.models.LoanInstallment;
import com.shparimi.aspireapp.exceptions.BadRequestException;
import com.shparimi.aspireapp.exceptions.NotFoundException;
import com.shparimi.aspireapp.exceptions.PaymentException;
import com.shparimi.aspireapp.models.LoanDetails;
import com.shparimi.aspireapp.service.LoanService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {
	private enum LOAN_STATUS {
		PENDING, APPROVED, PAID, PARTIALLY_PAID, REJECTED
	}

	@Autowired
	private LoanDAO loanRepository;

	@Override
	public LoanDetails applyLoan(LoanDetails loanDetails) {
		Loan loan = Loan.builder().amount(loanDetails.getAmount()).term(loanDetails.getTerm())
				.userId(loanDetails.getUserId()).status(LOAN_STATUS.PENDING.toString()).build();
		loan = loanRepository.save(loan);
		return convertToLoanDetails(loan);
	}

	@Override
	public LoanDetails getLoanDetails(int userId, int loanId) {
		Loan loan = loanRepository.getByIdAndUserId(loanId, userId);
		return convertToLoanDetails(loan);
	}

	@Override
	public LoanDetails getLoanDetails(int loanId) {
		Optional<Loan> loan = loanRepository.findById(loanId);
		if (loan.isPresent()) {
			return convertToLoanDetails(loan.get());
		} else {
			throw new NotFoundException("Loan " + loanId + " not found");
		}
	}

	@Override
	public List<LoanDetails> getAllUserLoanDetails(int userId) {
		List<Loan> loans = loanRepository.getByUserId(userId);
		return convertToLoanDetails(loans);
	}

	@Override
	public List<LoanDetails> getAllLoanDetails() {
		List<Loan> loans = loanRepository.findAll();
		return convertToLoanDetails(loans);
	}

	@Override
	@Transactional
	public void updateLoanStatus(int loanId, String status, int userId) {
		Optional<Loan> optionalLoan = loanRepository.findById(loanId);
		if (optionalLoan.isPresent()) {
			Loan loan = optionalLoan.get();
			if (!loan.getStatus().equals("PENDING")) {
				throw new BadRequestException("Loan is not in pending state");
			}
			loan.setStatus(status);
			if (status.equals(LOAN_STATUS.APPROVED.toString())
					&& (loan.getInstallments() == null || loan.getInstallments().size() == 0)) {
				loan.setInstallments(createInstallments(loan));
				loan.setApproverId(userId);
			}
			log.debug("Updating state of loan {}", loanId);
			loanRepository.save(loan);
		} else {
			throw new NotFoundException("Loan " + loanId + " not found");
		}
	}

	@Override
	@Transactional
	public LoanDetails payInstallment(int userId, int loanId, double amount) {
		Loan loan = loanRepository.getByIdAndUserId(loanId, userId);
		if (loan.getStatus().equals(LOAN_STATUS.PENDING.toString())
				|| loan.getStatus().equals(LOAN_STATUS.PAID.toString())
				|| loan.getStatus().equals(LOAN_STATUS.REJECTED.toString())) {
			throw new PaymentException("Cannot make payment");
		}
		for (int i = 0; i < loan.getInstallments().size(); i++) {
			LoanInstallment li = loan.getInstallments().get(i);
			if (li.getStatus().equals(LOAN_STATUS.PENDING.toString())) {
				if (amount > li.getPendingAmount()) {
					if (i < loan.getInstallments().size() - 1) {
						li.setStatus(LOAN_STATUS.PAID.toString());
						li.setAmountPaid(amount);
						li.setPaymentDate(new Date());
						reOrganizePayments(loan.getInstallments(), loan.getAmount());
					} else {
						throw new PaymentException("Cannot pay more than " + li.getPendingAmount());
					}
				} else if (amount == li.getPendingAmount()) {
					li.setStatus(LOAN_STATUS.PAID.toString());
					li.setAmountPaid(amount);
					li.setPaymentDate(new Date());
				} else {
					throw new PaymentException("Cannot pay less than " + li.getPendingAmount());
				}
				if (i == loan.getInstallments().size() - 1) {
					loan.setStatus(LOAN_STATUS.PAID.toString());
				} else {
					loan.setStatus(LOAN_STATUS.PARTIALLY_PAID.toString());
				}
				break;
			}
		}

		loan = loanRepository.save(loan);
		return convertToLoanDetails(loan);
	}

	private List<LoanDetails> convertToLoanDetails(List<Loan> loan) {
		return loan.stream().map(this::convertToLoanDetails).collect(Collectors.toList());
	}

	private LoanDetails convertToLoanDetails(Loan loan) {
		LoanDetails details = LoanDetails.builder().amount(loan.getAmount()).id(loan.getId())
				.lastUpdateDate(loan.getLastUpdateDate()).status(loan.getStatus()).term(loan.getTerm())
				.userId(loan.getUserId()).approverId(loan.getApproverId()).build();
		if (loan.getStatus().equals(LOAN_STATUS.APPROVED.toString())
				|| loan.getStatus().equals(LOAN_STATUS.PAID.toString())
				|| loan.getStatus().equals(LOAN_STATUS.PARTIALLY_PAID.toString())) {
			details.setInstallments(
					loan.getInstallments().stream().map(this::convertToLIModel).collect(Collectors.toList()));

		}
		return details;
	}

	private com.shparimi.aspireapp.models.LoanInstallment convertToLIModel(LoanInstallment li) {
		return com.shparimi.aspireapp.models.LoanInstallment.builder().amountPaid(li.getAmountPaid())
				.paymentDate(li.getPaymentDate()).pendingAmount(li.getPendingAmount()).pendingDate(li.getPendingDate())
				.status(li.getStatus()).build();
	}

	private List<LoanInstallment> createInstallments(Loan loan) {
		List<LoanInstallment> result = new ArrayList<>();
		int term = loan.getTerm();
		double amount = loan.getAmount();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		for (int i = 0; i < loan.getTerm(); i++) {
			LoanInstallment li = LoanInstallment.builder().loan(loan).pendingAmount(amount / term)
					.status(LOAN_STATUS.PENDING.toString()).pendingDate(calendar.getTime()).build();
			calendar.add(Calendar.DAY_OF_MONTH, 7);
			result.add(li);
		}
		return result;
	}

	private void reOrganizePayments(List<LoanInstallment> installments, double totalLoanAmount) {
		double amountPaid = 0;
		int paidIndex = 0;
		for (int i = 0; i < installments.size(); i++) {
			if (installments.get(i).getStatus().equals(LOAN_STATUS.PAID.toString())) {
				amountPaid += installments.get(i).getAmountPaid();
			} else {
				paidIndex = i;
				break;
			}
		}
		int remainingInstallments = installments.size() - paidIndex;
		double remainingEMI = (totalLoanAmount - amountPaid) / remainingInstallments;
		for (int i = paidIndex; i < installments.size(); i++) {
			installments.get(i).setPendingAmount(remainingEMI);
		}
	}

}
