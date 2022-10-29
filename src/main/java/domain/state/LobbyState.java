package domain.state;

import lombok.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
public class LobbyState implements Serializable {

    @NonNull
    private final List<String> userNames;

    public void addUserName(String userName) {
        this.userNames.add(userName);
    }

    public LobbyState(List<String> userNames) {
        this.userNames = userNames;
    }

    public int getUserCount() {
        return getUserNames().size();
    }

    public void removeUser(String name) {
        userNames.remove(name);
    }
}
