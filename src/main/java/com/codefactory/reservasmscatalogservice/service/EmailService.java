package com.codefactory.reservasmscatalogservice.service;

/**
 * Service interface for email operations.
 * Handles sending notification emails to providers.
 */
public interface EmailService {

    /**
     * Sends a category deactivation email to a provider.
     *
     * @param to Recipient email address
     * @param nombreComercial Provider's commercial name for personalization
     * @param nombreCategory Name of the deactivated category
     */
    void sendCategoryDeactivationEmail(String to, String nombreComercial, String nombreCategory);
}
