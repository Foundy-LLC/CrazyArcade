package domain.state;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LobbyState implements Serializable {

    private final List<String> userNames;
}
