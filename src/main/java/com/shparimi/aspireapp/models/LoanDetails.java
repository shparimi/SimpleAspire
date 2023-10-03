package com.shparimi.aspireapp.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class LoanDetails {
	private int id;
	private double amount;
	private int term;
	private int userId;
	private String status;
	private int approverId;
	private Date lastUpdateDate;
	private List<LoanInstallment> installments;
}
