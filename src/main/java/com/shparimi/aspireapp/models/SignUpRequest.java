package com.shparimi.aspireapp.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

	@NotBlank(message = "username is mandatory")
	@Size(min = 3, max = 20, message = "username should be between 3 to 20 characters")
	private String username;

	@NotBlank(message = "email is mandatory")
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank(message = "role is mandatory")
	private String role;

	@NotBlank(message = "password is mandatory")
	@Size(min = 6, max = 40)
	private String password;

}
