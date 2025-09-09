
import java.util.ArrayList;

/**
 * The world class represents a collection of rooms in a game environment. It
 * manages the current room, allows navigation between rooms, and provides
 * methods to access and modify room-related information.
 *
 * Key features: - Stores a list of room objects and tracks the current room. -
 * Allows advancing to the next room and adding new rooms. - Provides accessors
 * and mutators for room properties.
 *
 * @author Simon
 */
public class world {

    // Variabeln
    final int room_count;
    ArrayList<room> roomList;
    room current_room;
    int current_room_number;

    /**
     * Constructs a new world with the specified number of rooms and a list of
     * rooms.
     *
     * @param room_count The total number of rooms in the world.
     * @param roomList The list of rooms that make up the world. The first room
     * in the list will be set as the current room.
     */
    public world(int room_count, ArrayList<room> roomList) {
        this.room_count = room_count;
        this.roomList = roomList;
        current_room = roomList.get(0);
        current_room_number = 0;
    }

    /**
     * Advances the game to the next room if the current room number is less
     * than the total room count. Increments the current room number and updates
     * the current room reference. If already at the last room, no action is
     * taken.
     */
    public void advance_to_next_room() {
        if (current_room_number < room_count) {
            current_room_number++;
            current_room = roomList.get(current_room_number + 1);
        } else {
        }
    }

    /**
     * Adds a room to the list of rooms in the world.
     *
     * @param room the room to be added
     */
    public void add_room(room room) {
        roomList.add(room);
    }

    /**
     * Returns the current room in the world.
     *
     * @return the current room instance.
     */
    public room getCurrent_room() {
        return current_room;
    }

    /**
     * Returns the number of the current room.
     *
     * @return the current room number as an integer
     */
    public int getCurrent_room_number() {
        return current_room_number;
    }

    /**
     * Returns the total number of rooms in the world.
     *
     * @return the number of rooms
     */
    public int getRoom_count() {
        return room_count;
    }
}
