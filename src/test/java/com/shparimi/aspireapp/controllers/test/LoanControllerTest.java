package com.shparimi.aspireapp.controllers.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.shparimi.aspireapp.controller.LoanController;
import com.shparimi.aspireapp.exceptions.BadRequestException;
import com.shparimi.aspireapp.exceptions.NotFoundException;
import com.shparimi.aspireapp.exceptions.WebApplicationException;
import com.shparimi.aspireapp.models.LoanDetails;
import com.shparimi.aspireapp.models.LoanRequest;
import com.shparimi.aspireapp.models.LoanUpdateRequest;
import com.shparimi.aspireapp.models.RepaymentRequest;
import com.shparimi.aspireapp.security.UserDetailsImpl;
import com.shparimi.aspireapp.service.LoanService;

@ExtendWith(MockitoExtension.class)
public class LoanControllerTest {

	@Mock
	private LoanService loanService;

	@InjectMocks
	private LoanController loanController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(loanController, "loanService", loanService);
	}

	@Test
	public void testApplyLoan() {
		LoanRequest lr = new LoanRequest();
		lr.setAmount(10000);
		lr.setTerm(3);
		when(loanService.applyLoan(any(LoanDetails.class))).thenReturn(LoanDetails.builder().userId(1).id(1).build());
		LoanDetails ld = loanController.applyLoan(1, lr);
		verify(loanService, times(1)).applyLoan(any(LoanDetails.class));
		assertTrue(ld.getId() == 1);
	}

	@Test
	public void testApplyLoanWithException() {
		LoanRequest lr = new LoanRequest();
		lr.setAmount(10000);
		lr.setTerm(3);
		when(loanService.applyLoan(any(LoanDetails.class))).thenThrow(RuntimeException.class);
		WebApplicationException ex = assertThrows(WebApplicationException.class, () -> loanController.applyLoan(1, lr));
		verify(loanService, times(1)).applyLoan(any(LoanDetails.class));
		assertTrue(ex != null);
	}

	@Test
	public void testUpdateLoanStatus() {
		LoanUpdateRequest lur = new LoanUpdateRequest();
		lur.setStatus("APPROVED");
		doNothing().when(loanService).updateLoanStatus(1, "APPROVED", 1);
		loanController.updateLoanStatus(1, 1, lur);
		verify(loanService, times(1)).updateLoanStatus(1, "APPROVED", 1);
	}

	@Test
	public void testUpdateLoanStatusBadRequestException() {
		LoanUpdateRequest lur = new LoanUpdateRequest();
		lur.setStatus("TEST");
		BadRequestException ex = assertThrows(BadRequestException.class,
				() -> loanController.updateLoanStatus(1, 1, lur));
		assertTrue(ex.getMessage().equals("User can only set APPROVED or REJECTED"));
	}

	@Test
	public void testLoanPayment() {
		RepaymentRequest rr = new RepaymentRequest();
		rr.setInstallmentAmout(1000);
		when(loanService.payInstallment(1, 1, 1000)).thenReturn(LoanDetails.builder().id(1).build());
		LoanDetails ld = loanController.payInstallment(1, 1, rr);
		verify(loanService, times(1)).payInstallment(1, 1, 1000);
		assertTrue(ld.getId() == 1);
	}

	@Test
	public void testLoanPaymentWithException() {
		RepaymentRequest rr = new RepaymentRequest();
		rr.setInstallmentAmout(1000);
		when(loanService.payInstallment(1, 1, 1000)).thenThrow(NotFoundException.class);
		NotFoundException ex = assertThrows(NotFoundException.class, () -> loanController.payInstallment(1, 1, rr));
		verify(loanService, times(1)).payInstallment(1, 1, 1000);
		assertTrue(ex != null);
	}

}
