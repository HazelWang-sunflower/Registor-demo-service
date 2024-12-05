package com.koi.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.koi.api.entity.dto.Account;
import com.koi.api.entity.request.ConfirmResetVO;
import com.koi.api.entity.request.EmailRegisterVO;
import com.koi.api.entity.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findUserByNameOrEmail(String parameter);

    String registerEmailVerifyCode(String type, String email_address, String ip);
    String registerEmailAccount(EmailRegisterVO vo);

    String resetConfirm(ConfirmResetVO vo);
    String resetEmailAccountPassword(EmailResetVO vo);
}
