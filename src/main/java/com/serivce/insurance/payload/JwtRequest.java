package com.serivce.insurance.payload;



import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data

public class JwtRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;
    String username;
	private String password;
	



}

