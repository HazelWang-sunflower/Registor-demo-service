package com.koi.api.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.koi.api.entity.dto.Account;
import com.koi.api.entity.request.ConfirmResetVO;
import com.koi.api.entity.request.EmailRegisterVO;
import com.koi.api.entity.request.EmailResetVO;
import com.koi.api.mapper.AccountMapper;
import com.koi.api.service.AccountService;
import com.koi.api.utils.Const;
import com.koi.api.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FlowUtils utils;

    // This password encoder cannot use in SecurityConfiguration class, it will cause circular reference
    @Resource
    PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String nameOrEmail) throws UsernameNotFoundException {
        Account account = this.findUserByNameOrEmail(nameOrEmail);
        if(account == null) {
            throw new UsernameNotFoundException("User name or password is not correct!");
        }
        return User
                .withUsername(nameOrEmail)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account findUserByNameOrEmail(String parameter) {
        return this.query()
                .eq("username", parameter).or()
                .eq("email",parameter)
                .one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email_address, String ip) {
        // 锁防same ip 同时多次请求
        synchronized (ip.intern()){
            if(!this.verifyLimit(ip)){
                return "Please waiting...";
            }
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            //放到消息队列 去消费
            Map<String, Object> data = Map.of("type", type, "email", email_address, "code", code);
            amqpTemplate.convertAndSend("mail", data);
            // save in redis for verify when sign in
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email_address, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        String code_key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(code_key);
        if(code == null) return "Please request verify code first";
        if(!code.equals(vo.getCode())) return "Verify code is not correct, please input again.";
        if(this.isExistedEmail(email)) return "This email is already registered.";
        if(this.isExistedUsername(username)) return "This username is already registered.";
        //考虑并发问题
        // Start register
        String password = encoder.encode(vo.getPassword());
        Account account = new Account(null, username, password, email, "username", new Date());
        //mybatis-plus, we do not write mapper and sql
        if(this.save(account)){
            // delete current code, after register success
            stringRedisTemplate.delete(code_key);
            return null;
        }else{
            return "Something is wrong, please connect with admin.";
        }
    }

    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if(code == null) {
            return "Please request code first";
        }
        if(!code.equals(vo.getCode())){
            return "Please input correct code!";
        }
        return null;
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String email = vo.getEmail();

        String verify = this.resetConfirm(new ConfirmResetVO(email, vo.getCode()));
        if(verify != null) {
            return verify;
        }
        String password = encoder.encode(vo.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if(update) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
        }
        return null;
    }

    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return utils.limitOnceCheck(key, 60);
    }

    private boolean isExistedEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }

    private boolean isExistedUsername(String username) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }

}
