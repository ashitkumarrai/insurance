package com.serivce.insurance.payload;



import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data

public class JwtRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;
	 @Schema( example = "userDemo")
		String username;
	@Schema( example = "userDemo123")
	private String password;
	



}

