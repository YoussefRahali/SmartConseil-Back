package tn.esprit.microserviceplanification.WebSocket;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.1.13:4200"})
@AllArgsConstructor
public class RoomController {
    private final RoomStorage roomStorage;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/join")
    public void joinRoom(@Payload JoinRequest request,
                         @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {

            System.err.println(request.getUsername());
            System.err.println(request.getIdRoom());


        sessionAttributes.put("username", request.getUsername());
        sessionAttributes.put("roomId", request.getIdRoom());

        roomStorage.joinUser(request.getIdRoom(), request.getUsername());
        messagingTemplate.convertAndSend("/topic/room/" + request.getIdRoom(),
                roomStorage.getUsersInRoom(request.getIdRoom()));
    }



    @MessageMapping("/leave")
    @SendTo("/topic/room")
    public List<String> handleLeave(@Payload JoinRequest request) {
        RoomStorage.leaveUser(request.getIdRoom(), request.getUsername());
        return RoomStorage.getParticipants(request.getIdRoom());
    }
}
