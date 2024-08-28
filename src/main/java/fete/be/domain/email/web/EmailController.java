package fete.be.domain.email.web;

import fete.be.domain.email.application.EmailService;
import fete.be.domain.email.application.dto.request.SendEmailRequest;
import fete.be.domain.email.application.dto.request.VerifyEmailRequest;
import fete.be.domain.email.exception.IncorrectVerifyCodeException;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emails")
@Slf4j
public class EmailController {

    private final EmailService emailService;


    /**
     * 이메일로 인증번호 전송 API
     *
     * @param SendEmailRequest request
     * @return ApiResponse
     */
    @PostMapping
    public ApiResponse sendEmail(@RequestBody SendEmailRequest request) {
        try {
            log.info("SendEmail API: request={}", request);
            Logging.time();

            // 이메일 전송
            emailService.sendEmail(request);
            return new ApiResponse<>(ResponseMessage.EMAIL_SEND_SUCCESS.getCode(), ResponseMessage.EMAIL_SEND_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.EMAIL_SEND_FAIL.getCode(), e.getMessage());
        } catch (MessagingException e) {
            return new ApiResponse<>(ResponseMessage.EMAIL_SEND_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 인증번호 검사 API
     *
     * @param VerifyEmailRequest request
     * @return ApiResponse
     */
    @PostMapping("/verify")
    public ApiResponse verifyEmail(@RequestBody VerifyEmailRequest request) {
        try {
            log.info("VerifyEmail API: request={}", request);
            Logging.time();

            // 이메일 검사
            emailService.verifyEmail(request);

            return new ApiResponse<>(ResponseMessage.EMAIL_CORRECT_CODE.getCode(), ResponseMessage.EMAIL_CORRECT_CODE.getMessage());
        } catch (IncorrectVerifyCodeException e) {
            return new ApiResponse<>(ResponseMessage.EMAIL_INCORRECT_CODE.getCode(), e.getMessage());
        }catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.EMAIL_INCORRECT_CODE.getCode(), e.getMessage());
        }
    }
}
