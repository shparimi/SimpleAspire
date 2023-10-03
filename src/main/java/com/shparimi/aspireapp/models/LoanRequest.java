package com.shparimi.aspireapp.models;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class LoanRequest {
	@Min(value = 0L, message = "Amount must be positive")
	private double amount;

	@Range(min = 1, max = 10)
	private int term;
}
