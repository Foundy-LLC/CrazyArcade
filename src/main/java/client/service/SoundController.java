package client.service;

import domain.model.Sound;
import lombok.NonNull;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundController {

    public static void play(@NonNull Sound sound) {
        new Thread(()->{
            try {
                File soundFile = new File(sound.path);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.stop();
                clip.open(audioInputStream);
                clip.start();
                clip.drain();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}