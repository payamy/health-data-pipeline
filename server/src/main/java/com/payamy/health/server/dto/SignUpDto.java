package com.payamy.health.server.dto;

import com.payamy.health.server.type.BloodType;
import com.payamy.health.server.type.EyeColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {

    private String username;
    private String password;
    private String name;
    private BloodType bloodType;
    private EyeColor eyeColor;
}
