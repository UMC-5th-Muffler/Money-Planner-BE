package com.umc5th.muffler.domain.member.service;

import com.umc5th.muffler.domain.member.dto.InquiryRequest;
import com.umc5th.muffler.domain.member.dto.MailConverter;
import com.umc5th.muffler.domain.member.repository.InquiryRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Inquiry;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.exception.MailException;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.umc5th.muffler.global.response.code.ErrorCode.FAIL_SEND_EMAIL;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;

    @Value("${spring.mail.username}")
    private String mufflerEmail;

    @Transactional
    public void sendInquiryEmail(InquiryRequest request, String memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        MimeMessage message = createInquiryMessage(request);
        javaMailSender.send(message);

        saveInquiry(request, member);
    }

    private MimeMessage createInquiryMessage(InquiryRequest request) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("[문의] " + request.getEmail());
            helper.setTo(mufflerEmail);
            helper.setText(request.getContent(), true);
            return message;
        } catch (MessagingException e) {
            throw new MailException(FAIL_SEND_EMAIL);
        }
    }

    private void saveInquiry(InquiryRequest request, Member member) {
        Inquiry inquiry = MailConverter.toEntity(request, member);
        inquiryRepository.save(inquiry);
    }
}
