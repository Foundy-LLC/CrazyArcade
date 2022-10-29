package server;

import domain.state.LobbyState;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LobbyStateRepository {
    private final List<String> userNames = new ArrayList<>(8);

    @Getter
    private LobbyState lobbyState;

    public LobbyStateRepository() {
    }

    public static LobbyStateRepository getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final LobbyStateRepository INSTANCE = new LobbyStateRepository();
    }

    public void addUserName(String name) {
        this.lobbyState.addUserName(name);
    }

    public void initState() {
        lobbyState = new LobbyState(this.userNames);
    }

    public List<String> getLobbyUserNames() {
        return lobbyState.getUserNames();
    }

    public int getLobbyUserCounts() {
        return lobbyState.getUserCount();
    }

    public void removeLobbyUser(String user) {
        lobbyState.removeUser(user);
    }
}
