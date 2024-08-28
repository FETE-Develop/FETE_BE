package fete.be.domain.email.application;

import fete.be.domain.email.application.dto.request.SendEmailRequest;
import fete.be.domain.email.application.dto.request.VerifyEmailRequest;
import fete.be.domain.email.exception.IncorrectVerifyCodeException;
import fete.be.global.util.RedisUtil;
import fete.be.global.util.ResponseMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisUtil redisUtil;

    private static final String FROM_EMAIL = "hello@fete.kr";
    private static final String TITLE = "안녕하세요, FETE 인증번호입니다.";
    private static final long EXPIRED_TIME = 60 * 5;  // 유효시간 (초 단위) : 5분


    // 인증번호가 담긴 이메일을 전송하는 메서드
    public void sendEmail(SendEmailRequest request) throws MessagingException {
        // 만약 Redis에 해당 이메일로 전송된 old한 인증번호가 있을 경우를 대비
        if (redisUtil.hasData(request.getEmail())) {
            redisUtil.deleteData(request.getEmail());
        }

        String verifyCode = createRandomNumber();  // 6자리 인증번호 생성

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(FROM_EMAIL);  // 발신자 설정
        helper.setTo(request.getEmail());  // 수신자 설정
        helper.setSubject(TITLE);  // 이메일 제목 설정
        String htmlContent = setContext(verifyCode);  // 이메일 내용
        helper.setText(htmlContent, true);  // html 형식으로 된 content, html 여부

        javaMailSender.send(message);  // 이메일 전송

        // Redis에 { email : verifyCode } 형태로 5분 동안 저장
        redisUtil.setData(request.getEmail(), verifyCode, EXPIRED_TIME);
    }

    // Thymeleaf 통해서 verifyCode를 mail.html에 렌더링
    private String setContext(String verifyCode) {
        Context context = new Context();
        context.setVariable("code", verifyCode);
        String htmlContent = templateEngine.process("mail", context);
        return htmlContent;
    }

    // 6자리 인증번호 생성 메서드
    public String createRandomNumber() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    // 인증번호 검증 로직
    public boolean checkVerifyCode(VerifyEmailRequest request) {
        String originCode = redisUtil.getData(request.getEmail());  // Redis에 저장된 인증코드
        String requestCode = request.getVerifyCode();  // 요청으로 들어온 인증코드

        return originCode != null && requestCode.equals(originCode);
    }

    // 사용자로부터 받은 인증번호가 일치하는지 검사하는 메서드
    public void verifyEmail(VerifyEmailRequest request) {
        boolean isValidCode = checkVerifyCode(request);
        // 인증코드가 틀린 경우
        if (!isValidCode) {
            throw new IncorrectVerifyCodeException(ResponseMessage.EMAIL_INCORRECT_CODE.getMessage());
        }
    }
}
