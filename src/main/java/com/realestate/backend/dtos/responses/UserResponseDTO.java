package com.realestate.backend.dtos.responses;

import com.realestate.backend.entities.SubscriptionPlan;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
    private String phone;
    private String role;
    private String tcNo;
    private SubscriptionPlan subscriptionPlan;
}