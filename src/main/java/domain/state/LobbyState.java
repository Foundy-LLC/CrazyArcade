package domain.state;

import domain.model.RoomDto;
import lombok.*;

import java.io.Serializable;
import java.util.List;

public class LobbyState implements Serializable {

    @Getter
    @NonNull
    private final List<RoomDto> roomDtoList;

    public LobbyState(List<RoomDto> roomDtoList) {
        this.roomDtoList = roomDtoList;
    }
}
