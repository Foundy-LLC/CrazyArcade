package client.service;

import domain.model.Sound;
import lombok.NonNull;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundController {

    private static Clip loopClip = null;
    private static boolean loopPlaying = false;

    public static void play(@NonNull Sound sound) {
        new Thread(() -> play(sound, 0)).start();
    }

    private static Clip play(@NonNull Sound sound, int loopCount) {
        try {
            File soundFile = new File(sound.path);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.stop();
            clip.open(audioInputStream);
            clip.loop(loopCount);
            clip.drain();
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void playLoop(@NonNull Sound sound) {
        stopLoop();
        loopPlaying = true;
        new Thread(() -> loopClip = SoundController.play(sound, Integer.MAX_VALUE)).start();
    }

    public static void changeLoopIfPlaying(@NonNull Sound sound) {
        if (!loopPlaying) {
            return;
        }
        playLoop(sound);
    }

    public static void pauseLoop() {
        if (loopClip != null) {
            loopClip.stop();
        }
    }

    public static void stopLoop() {
        if (loopClip != null) {
            loopPlaying = false;
            loopClip.stop();
            loopClip = null;
        }
    }

    public static boolean isLoopPlaying() {
        return loopPlaying;
    }
}
