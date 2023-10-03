package com.shparimi.aspireapp.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class LoanInstallment {
	private Date pendingDate;
	private String status;
	private double pendingAmount;
	private double amountPaid;
	private Date paymentDate;
}
