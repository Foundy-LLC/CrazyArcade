package client.core;

import client.service.Api;
import client.service.MessageListener;

import javax.swing.*;

public abstract class ApiListenerView extends BaseView {

    public ApiListenerView(ImageIcon backgroundImageIcon) {
        super(backgroundImageIcon);

        Api.getInstance().addListener(listener);
    }

    protected abstract void onMessageReceived(String message);

    @Override
    protected void onDestroyed() {
        Api.getInstance().removeListener(listener);
    }

    private final MessageListener listener = this::onMessageReceived;
}
