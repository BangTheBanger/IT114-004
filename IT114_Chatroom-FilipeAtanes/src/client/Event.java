package client;

import java.awt.Point;

public interface Event {
    default void onClientConnect(String clientName, String message) {
    	
    }

    default void onClientDisconnect(String clientName, String message) {
    	
    }

    default void onMessageReceive(String clientName, String message) {
    	
    }

    default void onChangeRoom() {
    	
    }

    default void onGetRoom(String roomName) {
    	
    }

    
}