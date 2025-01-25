package de.htwsaar.carpool.config;

import de.htwsaar.carpool.websocket.ChatWebSocketHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


/**
 * Configures WebSocket endpoints and settings for the application.
 * <p>
 * This configuration sets up the WebSocket handler to handle real-time
 * messaging between clients using the {@link ChatWebSocketHandler}.
 * </p>
 *
 * <p>EndPoint: <code>/chat</code></p>
 * <p>Allowed Origins: All origins (*). Update this in production to specific origins.</p>
 *
 * For Security Configurations, please refer to further documentation in
 * <a href="https://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/spring-framework-reference/html/websocket.html">...</a>
 */
@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/chat")
                .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

}
