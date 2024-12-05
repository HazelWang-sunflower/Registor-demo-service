package com.koi.api.entity.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ConfirmResetVO {

    @Email
    String email;
    @Length(max=6, min=6)
    String code;

    public ConfirmResetVO(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
