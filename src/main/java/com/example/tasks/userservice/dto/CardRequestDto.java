package com.example.tasks.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequestDto {
	@NotBlank(message = "Card number is mandatory!")
	@CreditCardNumber(message = "invalid card number")
	@Size(max = 19, message = "Card number must be no more than 19 digits")
	private String number;
	@NotBlank(message = "Holder name is mandatory!")
	@Pattern(regexp = "^[A-Z\\s]+$", message = "Holder name must be uppercase")
	@Size(min = 2, max = 100, message = "Holder name length should be in range 2-100 characters!")
	private String holder;
	@NotNull(message = "Expiration date should present!")
	@Future(message = "Expiration date should be in future!")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate expirationDate;
}
