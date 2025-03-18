package iuh.fit.se.service;

import feign.FeignException;
import io.github.cdimascio.dotenv.Dotenv;
import iuh.fit.se.dto.reponse.EmailReponse;
import iuh.fit.se.dto.request.EmailRequest;
import iuh.fit.se.dto.request.SendEmailRequest;
import iuh.fit.se.dto.request.Sender;
import iuh.fit.se.repository.httpclient.EmailClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;
    Dotenv dotenv = Dotenv.load();
    String apiKey = dotenv.get("API_KEY");

    public EmailReponse sendEmail(SendEmailRequest sendEmailRequest) {
        {
            EmailRequest emailRequest = EmailRequest.builder()
                    .sender(Sender.builder()
                            .name("SavarGO")
                            .email("thinh183tt@gmail.com")
                            .build())
                    .to(sendEmailRequest.getTo())
                    .subject(sendEmailRequest.getSubject())
                    .htmlContent(sendEmailRequest.getHtmlContent())
                    .build();
            try {
                return emailClient.sendEmail(apiKey, emailRequest);
            } catch (FeignException e) {
                throw new RuntimeException("Failed to send email" + e.contentUTF8());
            }
        }
    }

}