package server;

import domain.model.Sound;
import domain.state.GameState;
import lombok.NonNull;

import java.util.List;

public class GameStateTicker extends Thread {

    @NonNull
    private final GameState gameState;

    @NonNull
    private final Callback<Sound> onSoundShouldBePlayed;

    @NonNull
    private final Callback<Void> onGameStateUpdated;

    @NonNull
    private final Callback<Void> onGameEnded;

    public GameStateTicker(
            @NonNull GameState gameState,
            @NonNull Callback<Sound> onSoundShouldPlay,
            @NonNull Callback<Void> onGameStateUpdated,
            @NonNull Callback<Void> onGameEnded
    ) {
        this.gameState = gameState;
        this.onSoundShouldBePlayed = onSoundShouldPlay;
        this.onGameStateUpdated = onGameStateUpdated;
        this.onGameEnded = onGameEnded;
    }

    private void playSoundIfExists() {
        List<Sound> sounds = gameState.getShouldBePlayedSounds();
        sounds.forEach(onSoundShouldBePlayed::call);
        gameState.didPlaySounds(sounds);
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                break;
            }

            boolean didUpdate = gameState.updateState();
            if (didUpdate) {
                onGameStateUpdated.call(null);
            }

            playSoundIfExists();

            if (gameState.isEnded()) {
                onGameEnded.call(null);
                break;
            }
        }
    }
}
