package domain.model;

import lombok.Getter;

import static domain.state.RoomState.MAX_USER;

public class RoomDto {

    @Getter
    private final String id;

    @Getter
    private final String roomName;

    @Getter
    private final int userCount;

    public RoomDto(String id, String name, int userCount) {
        this.id = id;
        this.roomName = name;
        this.userCount = userCount;
    }

    @Override
    public String toString() {
        return roomName + " (" + userCount + "/" + MAX_USER + ")";
    }
}
