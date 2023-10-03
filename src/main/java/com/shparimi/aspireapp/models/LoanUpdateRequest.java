package com.shparimi.aspireapp.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoanUpdateRequest {
	@NotBlank
	private String status;
}
