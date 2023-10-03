package com.shparimi.aspireapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class User {
	private int id;
	private final String username;
	@JsonIgnore
	private final String password;
	private final String email;
	private final String role;
}
