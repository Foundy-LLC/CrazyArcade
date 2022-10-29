package domain.state;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
public class LobbyState implements Serializable {

    @Getter
    @NonNull
    private final List<String> userNames;

    public LobbyState(List<String> userNames) {
        this.userNames = userNames;
    }

    public void addUserName(String userName) {
        this.userNames.add(userName);
    }

    public int getUserCount() {
        return userNames.size();
    }

    public void removeUser(String name) {
        userNames.remove(name);
    }
}
