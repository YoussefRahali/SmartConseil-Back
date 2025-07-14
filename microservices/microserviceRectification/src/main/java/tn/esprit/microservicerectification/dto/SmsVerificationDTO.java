package tn.esprit.microservicerectification.dto;

import lombok.Data;

@Data
public class SmsVerificationDTO {
    private Long rectificationId;
    private String smsCode;
}
