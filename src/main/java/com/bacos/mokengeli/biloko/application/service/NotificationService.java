package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.domain.EmailPriorityEnum;
import com.bacos.mokengeli.biloko.application.domain.TemplateCodeEnum;
import com.bacos.mokengeli.biloko.infrastructure.feign.EmailServiceClient;
import com.bacos.mokengeli.biloko.infrastructure.feign.dto.EmailResponse;
import com.bacos.mokengeli.biloko.infrastructure.feign.dto.SendEmailRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service pour gérer l'envoi de notifications (emails).
 * Encapsule les appels vers l'email-service.
 */
@Slf4j
@Service
public class NotificationService {

    private final EmailServiceClient emailServiceClient;
    private final String loginUrl;

    @Autowired
    public NotificationService(@Value("${app.login-url}") String loginUrl,
                               EmailServiceClient emailServiceClient) {
        this.emailServiceClient = emailServiceClient;
        this.loginUrl = loginUrl;
    }

    /**
     * Envoie un email de création de compte à un utilisateur.
     *
     * @param tenantCode     Code du restaurant
     * @param userEmail      Email de l'utilisateur
     * @param firstName      Prénom
     * @param lastName       Nom
     * @param employeeNumber Numéro d'employé
     * @param userName       Nom d'utilisateur
     */
    public void sendAccountCreationEmail(
            String tenantCode,
            String userEmail,
            String firstName,
            String lastName,
            String employeeNumber,
            String userName

    ) {
        try {
            log.debug("Sending account creation email to {} for tenant {}",
                    userEmail, tenantCode);

            SendEmailRequest request = SendEmailRequest.builder()
                    .templateCode(TemplateCodeEnum.ACCOUNT_CREATION.name())
                    .tenantCode(tenantCode)
                    .recipients(List.of(userEmail))
                    .variables(Map.of(
                            "firstName", firstName,
                            "lastName", lastName,
                            "employeeNumber", employeeNumber,
                            "userName", userName,
                            "loginUrl", loginUrl
                    ))
                    .priority(EmailPriorityEnum.HIGH.name())
                    .build();

            EmailResponse response = emailServiceClient.sendEmail(request).getBody();

            if (response != null) {
                log.info("Account creation email sent successfully for user {} (email ID: {})",
                        userName, response.getId());
            }

        } catch (FeignException.NotFound e) {
            log.error("Email service endpoint not found: {}", e.getMessage());
        } catch (FeignException.ServiceUnavailable e) {
            log.error("Email service unavailable: {}", e.getMessage());
        } catch (FeignException e) {
            log.error("Error calling email service (status {}): {}",
                    e.status(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending account creation email: {}",
                    e.getMessage(), e);
        }

        // Note : On ne lève pas d'exception pour ne pas bloquer la création de l'utilisateur
        // L'email est un "nice to have", pas un "must have"
    }

    /**
     * Envoie un email de réinitialisation de mot de passe.
     *
     * @param tenantCode Code du restaurant
     * @param userEmail  Email de l'utilisateur
     * @param firstName  Prénom
     * @param resetUrl   URL de réinitialisation
     */
    public void sendPasswordResetEmail(
            String tenantCode,
            String userEmail,
            String firstName,
            String resetUrl
    ) {
        try {
            log.debug("Sending password reset email to {}", userEmail);

            SendEmailRequest request = SendEmailRequest.builder()
                    .templateCode("PASSWORD_RESET")
                    .tenantCode(tenantCode)
                    .recipients(List.of(userEmail))
                    .variables(Map.of(
                            "firstName", firstName,
                            "resetUrl", resetUrl
                    ))
                    .priority("URGENT")
                    .build();

            EmailResponse response = emailServiceClient.sendEmail(request).getBody();

            if (response != null) {
                log.info("Password reset email sent successfully (email ID: {})",
                        response.getId());
            }

        } catch (Exception e) {
            log.error("Error sending password reset email: {}", e.getMessage(), e);
        }
    }
}