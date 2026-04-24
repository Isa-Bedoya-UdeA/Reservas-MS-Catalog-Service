package com.codefactory.reservasmscatalogservice.service.impl;

import com.codefactory.reservasmscatalogservice.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Implementation of EmailService.
 * Sends category deactivation emails using JavaMailSender and Thymeleaf templates.
 */
@Service
@ConditionalOnBean(name = "javaMailSender")
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${platform.name:Plataforma de Reservas}")
    private String appName;

    @Value("${email.username:}")
    private String emailUsername;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendCategoryDeactivationEmail(String to, String nombreComercial, String nombreCategory) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Categoría de servicio desactivada - " + appName);
            helper.setFrom(emailUsername);

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("nombreComercial", nombreComercial);
            context.setVariable("nombreCategory", nombreCategory);
            context.setVariable("appName", appName);
            context.setVariable("frontendUrl", frontendUrl);

            // Process HTML template
            String htmlContent = templateEngine.process("category-deactivation", context);
            helper.setText(htmlContent, true);

            // Send email
            javaMailSender.send(message);
            log.info("Category deactivation email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send category deactivation email to: {}", to, e);
            throw new RuntimeException("Failed to send category deactivation email: " + e.getMessage(), e);
        }
    }
}
