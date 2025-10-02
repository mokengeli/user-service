package com.bacos.mokengeli.biloko.infrastructure.feign;

import com.bacos.mokengeli.biloko.config.feign.FeignClientConfiguration;
import com.bacos.mokengeli.biloko.infrastructure.feign.dto.EmailResponse;
import com.bacos.mokengeli.biloko.infrastructure.feign.dto.SendEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "email-service",
        path = "/api/email",
        configuration = FeignClientConfiguration.class
)
public interface EmailServiceClient {

    /**
     * Envoie un email depuis un template.
     * <p>
     * POST /api/email/send
     *
     * @param request Les données de l'email
     * @return L'email créé avec status PENDING
     */
    @PostMapping("/send")
    ResponseEntity<EmailResponse> sendEmail(@RequestBody SendEmailRequest request);
}