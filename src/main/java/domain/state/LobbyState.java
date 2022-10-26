package domain.state;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@ToString
public class LobbyState implements Serializable {

    @NonNull
    private final List<String> userNames;

    public LobbyState(List<String> userNames) {
        this.userNames = userNames;
    }
}
