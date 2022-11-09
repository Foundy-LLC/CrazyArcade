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

    @Getter
    private final boolean gameInProgress;

    public RoomDto(String id, String name, int userCount, boolean gameInProgress) {
        this.id = id;
        this.roomName = name;
        this.userCount = userCount;
        this.gameInProgress = gameInProgress;
    }

    @Override
    public String toString() {
        return (gameInProgress ? "(게임중)" : "") + roomName + " (" + userCount + "/" + MAX_USER + ")";
    }
}
