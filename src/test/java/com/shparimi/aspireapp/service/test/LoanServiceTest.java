package com.shparimi.aspireapp.service.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.shparimi.aspireapp.dao.LoanDAO;
import com.shparimi.aspireapp.dao.models.Loan;
import com.shparimi.aspireapp.dao.models.LoanInstallment;
import com.shparimi.aspireapp.models.LoanDetails;
import com.shparimi.aspireapp.service.impl.LoanServiceImpl;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

	@Mock
	private LoanDAO loanRepository;

	@InjectMocks
	private LoanServiceImpl loanServiceImpl;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(loanServiceImpl, "loanRepository", loanRepository);
	}

	@Test
	public void testApplyLoan() {
		when(loanRepository.save(any(Loan.class)))
		.thenReturn(Loan.builder().amount(10000).id(1).status("PENDING").term(3).userId(1).lastUpdateDate(new Date()).build());
		LoanDetails ld= loanServiceImpl.applyLoan(LoanDetails.builder().amount(1000).term(3).build());
		verify(loanRepository, times(1)).save(any(Loan.class));
		assertTrue(ld.getId()==1);
	}

	@Test
	public void testLoanDetailsByUserAndLoanId() {
		when(loanRepository.getByIdAndUserId(1,1))
		.thenReturn(Loan.builder().amount(10000).id(1).status("PENDING").term(3).userId(1).lastUpdateDate(new Date()).build());
		LoanDetails ld= loanServiceImpl.getLoanDetails(1, 1);
		verify(loanRepository, times(1)).getByIdAndUserId(1, 1);
		assertTrue(ld.getId()==1);
	}

	@Test
	public void testLoanDetailsByLoanId() {
		when(loanRepository.findById(1))
		.thenReturn(Optional.of(Loan.builder().amount(10000).id(1).status("PENDING").term(3).userId(1).lastUpdateDate(new Date()).build()));
		LoanDetails ld= loanServiceImpl.getLoanDetails(1);
		verify(loanRepository, times(1)).findById(1);
		assertTrue(ld.getId()==1);
	}

	@Test
	public void testLoanDetailsByUser() {
		when(loanRepository.getByUserId(1))
		.thenReturn(List.of(Loan.builder().amount(10000).id(1).status("PENDING").term(3).userId(1).lastUpdateDate(new Date()).build()));
		List<LoanDetails> ld= loanServiceImpl.getAllUserLoanDetails(1);
		verify(loanRepository, times(1)).getByUserId(1);
		assertTrue(ld.size()==1);
	}

	@Test
	public void testLoanDetailsGetAll() {
		when(loanRepository.findAll())
		.thenReturn(List.of(Loan.builder().amount(10000).id(1).status("PENDING").term(3).userId(1).lastUpdateDate(new Date()).build()));
		List<LoanDetails> ld= loanServiceImpl.getAllLoanDetails();
		verify(loanRepository, times(1)).findAll();
		assertTrue(ld.size()==1);
	}

	@Test
	public void testLoanStatusUpdate() {
		when(loanRepository.findById(1))
		.thenReturn(Optional.of(Loan.builder().amount(10000).id(1).status("PENDING").term(3).userId(1).lastUpdateDate(new Date()).build()));
		when(loanRepository.save(any(Loan.class))).thenReturn(null);
		loanServiceImpl.updateLoanStatus(1, "APPROVED", 1);
	}
	
	@Test
	public void testLoanPayment() {
		Loan loan = Loan.builder().amount(1000).id(1).status("APPROVED").term(1).userId(1).lastUpdateDate(new Date()).build();
		when(loanRepository.getByIdAndUserId(1,1))
		.thenReturn(loan);
		LoanInstallment li = LoanInstallment.builder().pendingAmount(1000).status("PENDING").build();
		loan.setInstallments(List.of(li));
		when(loanRepository.save(any(Loan.class))).thenReturn(loan);
		LoanDetails ld = loanServiceImpl.payInstallment(1, 1, 1000);
		assertTrue(ld.getStatus().equals("PAID"));
		
	}
}
