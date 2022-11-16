package client.component;

import client.constant.ImageIcons;
import client.service.SoundController;
import domain.model.Sound;
import lombok.NonNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BackgroundSoundToggleButton extends JLabel {

    @NonNull
    private final Sound backgroundSound;

    private boolean enabled;

    public BackgroundSoundToggleButton(@NonNull Sound backgroundSound, boolean enabled) {
        this.backgroundSound = backgroundSound;
        this.enabled = enabled;
        this.addMouseListener(new MouseClickListener());
        this.setBounds(970, 20, 48, 48);
        this.setIcon(enabled ? ImageIcons.ENABLED_SOUND : ImageIcons.DISABLED_SOUND);
    }

    private class MouseClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            enabled = !enabled;
            if (enabled) {
                SoundController.playLoop(backgroundSound);
                setIcon(ImageIcons.ENABLED_SOUND);
            } else {
                SoundController.stopLoop();
                setIcon(ImageIcons.DISABLED_SOUND);
            }
        }
    }
}
