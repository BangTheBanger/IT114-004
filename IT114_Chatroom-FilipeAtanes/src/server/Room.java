package server;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Font;

public class Room implements AutoCloseable {
    private static SocketServer server;// used to refer to accessible server functions
    private String name;
    
    public final static Font defaultFont = new java.awt.Font("Consolas", 0, 12);
    public final static Font boldFont = new java.awt.Font("Consolas", Font.BOLD, 12);
    public final static Font italicFont = new java.awt.Font("Consolas", Font.ITALIC, 12);
    public final static Font commandFont = new java.awt.Font("Consolas", Font.BOLD, 14);
    
    private final static Logger log = Logger.getLogger(Room.class.getName());

    // Commands
    private final static String COMMAND_TRIGGER = "/";
    private final static String CREATE_ROOM = "createroom";
    private final static String JOIN_ROOM = "joinroom";
    private final static String ROLL = "roll";
    private final static String FLIP = "flip";
    private List<ServerThread> clients = new ArrayList<ServerThread>();
    static Dimension gameAreaSize = new Dimension(400, 600);

    public Room(String name) {
	this.name = name;
    }
    
    public static void setServer(SocketServer server) {
	Room.server = server;
    }

    public String getName() {
	return name;
    }

    protected synchronized void addClient(ServerThread client) {
	client.setCurrentRoom(this);
	boolean exists = false;
	// since we updated to a different List type, we'll need to loop through to find
	// the client to check against
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) {
	    ServerThread c = iter.next();
	    if (c == client) {
		exists = true;
		break;
	    }
	}

	if (exists) {
	    log.log(Level.INFO, "Attempting to add a client that already exists");
	}
	else {
	    clients.add(client);
	    syncClient(client);

	}
    }

    private void syncClient(ServerThread client) {
	if (client.getClientName() != null) {
	    client.sendClearList();
	    sendConnectionStatus(client, true, "joined the room " + getName());
	    // get the list of connected clients (for ui panel)
	    updateClientList(client);
	}
    }

    /**
     * Syncs the existing clients in the room with our newly connected client
     * 
     * @param client
     */
    private synchronized void updateClientList(ServerThread client) {
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) {
	    ServerThread c = iter.next();
	    if (c != client) {
		boolean messageSent = client.sendConnectionStatus(c.getClientName(), true, null);
	    }
	}
    }

    protected synchronized void removeClient(ServerThread client) {
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) {
	    ServerThread c = iter.next();
	    if (c == client) {
		iter.remove();
		log.log(Level.INFO, "Removed client " + c.getClientName() + " from " + getName());
	    }
	}
	if (clients.size() > 0) {
	    sendConnectionStatus(client, false, "left the room " + getName());
	}
	else {
	    cleanupEmptyRoom();
	}
    }

    private void cleanupEmptyRoom() {
	// If name is null it's already been closed. And don't close the Lobby
	if (name == null || name.equalsIgnoreCase(SocketServer.LOBBY)) {
	    return;
	}
	try {
	    log.log(Level.INFO, "Closing empty room: " + name);
	    close();
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    protected void joinRoom(String room, ServerThread client) {
	server.joinRoom(room, client);
    }

    protected void joinLobby(ServerThread client) {
	server.joinLobby(client);
    }

    
    protected int roll(int x) {
    	int r;
    	Random rand = new Random();
    	r = rand.nextInt(x + 1);
    	return r;
    }
    
    protected String flip() {
    	int r;
    	String coin;
    	Random rand = new Random();
    	r = rand.nextInt(2);
    	
    	if (r == 1) {
    		coin = "Heads";
    	} else {
    		coin = "Tails";
    	}
    	
    	return coin;
    }
    
    public List<String> getRooms() {
    	return server.getRooms();
        }

    /***
     * Helper function to process messages to trigger different functionality.
     * 
     * @param message The original message being sent
     * @param client  The sender of the message (since they'll be the ones
     *                triggering the actions)
     */
    private boolean processCommands(String message, ServerThread client) {
	boolean wasCommand = false;
	try {
	    if (message.indexOf(COMMAND_TRIGGER) > -1) {
		String[] comm = message.split(COMMAND_TRIGGER);
		log.log(Level.INFO, message);
		String part1 = comm[1];
		String[] comm2 = part1.split(" ");
		String command = comm2[0];
		if (command != null) {
		    command = command.toLowerCase();
		}
		String roomName;
		switch (command) {
		case CREATE_ROOM:
		    roomName = comm2[1];
		    if (server.createNewRoom(roomName)) {
			joinRoom(roomName, client);
		    }
		    wasCommand = true;
		    break;
		case JOIN_ROOM:
		    roomName = comm2[1];
		    joinRoom(roomName, client);
		    wasCommand = true;
		    break;
		case ROLL:
			if ((Integer.parseInt(comm2[1]) < 1) || (comm2[1] != null)) {
				try {sendMessage(client, commandFont, "You rolled the value: " + String.valueOf(roll(Integer.parseInt(comm2[1]))));
				} catch (Exception e) {e.printStackTrace();}
			} else {
				log.log(Level.INFO,"Debug message");
				sendMessage(client, commandFont, "sendMessage");
			}
		    wasCommand = true;
		    break;
		case FLIP:
			sendMessage(client, commandFont, "You have flipped: " + flip());
		    wasCommand = true;
		    break;
		}
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	return wasCommand;
    }

    // TODO changed from string to ServerThread
    protected void sendConnectionStatus(ServerThread client, boolean isConnect, String message) {
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) {
	    ServerThread c = iter.next();
	    boolean messageSent = c.sendConnectionStatus(client.getClientName(), isConnect, message);
	    if (!messageSent) {
		iter.remove();
		log.log(Level.INFO, "Removed client " + c.getId());
	    }
	}
    }
    
    protected void sendCommand() {
    	
    }

    /***
     * Takes a sender and a message and broadcasts the message to all clients in
     * this room. Client is mostly passed for command purposes but we can also use
     * it to extract other client info.
     * 
     * @param sender  The client sending the message
     * @param message The message to broadcast inside the room
     */
    protected void sendMessage(ServerThread sender, Font style, String message) {
	log.log(Level.INFO, getName() + ": Sending message to " + clients.size() + " clients");
	if (processCommands(message, sender)) {
	    // it was a command, don't broadcast
	    return;
	}
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) {
	    ServerThread client = iter.next();
	    boolean messageSent = client.send(sender.getClientName(), message);
	    if (!messageSent) {
		iter.remove();
		log.log(Level.INFO, "Removed client " + client.getId());
	    }
	}
    }

    /***
     * Will attempt to migrate any remaining clients to the Lobby room. Will then
     * set references to null and should be eligible for garbage collection
     */
    @Override
    public void close() throws Exception {
	int clientCount = clients.size();
	if (clientCount > 0) {
	    log.log(Level.INFO, "Migrating " + clients.size() + " to Lobby");
	    Iterator<ServerThread> iter = clients.iterator();
	    Room lobby = server.getLobby();
	    while (iter.hasNext()) {
		ServerThread client = iter.next();
		lobby.addClient(client);
		iter.remove();
	    }
	    log.log(Level.INFO, "Done Migrating " + clients.size() + " to Lobby");
	}
	server.cleanupRoom(this);
	name = null;
	// should be eligible for garbage collection now
    }

}