package com.university.educationPackage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/voice-ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();  // Opcional: Si usas SockJS en el cliente
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");  // Para respuestas
        registry.setApplicationDestinationPrefixes("/app");  // Para enviar prompts
    }



    // Opcional: Configurar límites de tamaño de mensajes
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(2 * 1024 * 1024); // 2MB para chunks WebM
        registration.setSendBufferSizeLimit(2 * 1024 * 1024);
        // Mantener el resto de configs existentes
    }
}