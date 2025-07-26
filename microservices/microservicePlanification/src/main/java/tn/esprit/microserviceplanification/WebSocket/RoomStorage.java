package tn.esprit.microserviceplanification.WebSocket;


import org.springframework.stereotype.Component;

import java.util.*;
@Component  // ✅ This makes it a Spring Bean


public class RoomStorage {
    private static final Map<String, Set<String>> rooms = new HashMap<>();

    public static void joinUser(String roomId, String username) {

        rooms.computeIfAbsent(roomId, k -> new HashSet<>()).add(username);
    }

    public static void leaveUser(String roomId, String username) {
        if (rooms.containsKey(roomId)) {
            rooms.get(roomId).remove(username);
        }
    }

    public static List<String> getParticipants(String roomId) {
        return new ArrayList<>(rooms.getOrDefault(roomId, new HashSet<>()));
    }


    public void removeUserFromRoom(String roomId, String username) {
        Set<String> users = rooms.get(roomId);
        if (users != null) {
            users.remove(username);
            if (users.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    public List<String> getUsersInRoom(String roomId) {
        return new ArrayList<>(rooms.getOrDefault(roomId, Collections.emptySet()));
    }

}
