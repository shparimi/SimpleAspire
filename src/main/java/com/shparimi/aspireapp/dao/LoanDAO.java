package com.shparimi.aspireapp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shparimi.aspireapp.dao.models.Loan;

public interface LoanDAO extends JpaRepository<Loan, Integer>{
	
	public Loan getByIdAndUserId(int id, int userId);
	
	public List<Loan> getByUserId(int userId);
}
