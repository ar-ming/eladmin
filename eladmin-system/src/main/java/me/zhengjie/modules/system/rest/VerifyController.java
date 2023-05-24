/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.system.rest;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.modules.system.service.VerifyService;
import me.zhengjie.service.EmailService;
import me.zhengjie.utils.enums.CodeBiEnum;
import me.zhengjie.utils.enums.CodeEnum;

/**
 * @author Zheng Jie
 * @date 2018-12-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
@Tag(name = "系统：验证码管理")
public class VerifyController {

    private final VerifyService verificationCodeService;
    private final EmailService emailService;

    @PostMapping(value = "/resetEmail")
    @Operation(summary="重置邮箱，发送验证码")
    public ResponseEntity<Object> resetEmail(@RequestParam String email){
        EmailVo emailVo = verificationCodeService.sendEmail(email, CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey());
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/email/resetPass")
    @Operation(summary="重置密码，发送验证码")
    public ResponseEntity<Object> resetPass(@RequestParam String email){
        EmailVo emailVo = verificationCodeService.sendEmail(email, CodeEnum.EMAIL_RESET_PWD_CODE.getKey());
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/validated")
    @Operation(summary="验证码验证")
    public ResponseEntity<Object> validated(@RequestParam String email, @RequestParam String code, @RequestParam Integer codeBi){
        CodeBiEnum biEnum = CodeBiEnum.find(codeBi);
        switch (Objects.requireNonNull(biEnum)){
            case ONE:
                verificationCodeService.validated(CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey() + email ,code);
                break;
            case TWO:
                verificationCodeService.validated(CodeEnum.EMAIL_RESET_PWD_CODE.getKey() + email ,code);
                break;
            default:
                break;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
