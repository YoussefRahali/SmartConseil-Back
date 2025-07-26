package tn.esprit.microserviceplanification.WebSocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
@Component
public class WebSocketEventListener {

    private final RoomStorage roomStorage;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(RoomStorage roomStorage, SimpMessagingTemplate messagingTemplate) {
        this.roomStorage = roomStorage;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        if (username != null && roomId != null) {
            System.out.println("Déconnexion de: " + username + " de la room: " + roomId);

            // Remove user from room
            roomStorage.removeUserFromRoom(roomId, username);

            // Notify all clients in that room
            messagingTemplate.convertAndSend("/topic/room", roomStorage.getUsersInRoom(roomId));
        }
    }
}