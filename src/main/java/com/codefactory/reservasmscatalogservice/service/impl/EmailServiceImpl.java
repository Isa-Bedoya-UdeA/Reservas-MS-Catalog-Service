package com.codefactory.reservasmscatalogservice.service.impl;

import com.codefactory.reservasmscatalogservice.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Implementation of EmailService.
 * Sends category deactivation emails using JavaMailSender and Thymeleaf templates.
 * Email sending is ASYNC to not block the main request thread.
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
    @Async
    public void sendCategoryDeactivationEmail(String to, String nombreComercial, String nombreCategory) {
        log.info("[ASYNC] Iniciando envio de email de desactivacion de categoria a: {}", to);
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
            log.info("[ASYNC] Email de desactivacion de categoria enviado exitosamente a: {}", to);

        } catch (Exception e) {
            log.error("[ASYNC] Error al enviar email de desactivacion de categoria a: {}", to, e);
            // Don't throw - just log the error
        }
    }
}
