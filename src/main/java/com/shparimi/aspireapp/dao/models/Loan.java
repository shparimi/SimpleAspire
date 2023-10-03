package com.shparimi.aspireapp.dao.models;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column
	private double amount;
	@Column
	private int term;
	@Column
	private int userId;
	@Column
	private String status;
	@Column
	private int approverId;
	@LastModifiedDate
	@Column
	private Date lastUpdateDate;
	@OneToMany(cascade = CascadeType.ALL)
	private List<LoanInstallment> installments;

}
