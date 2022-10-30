package client.core;

import client.service.MessageListener;

import java.util.ArrayList;
import java.util.List;

public abstract class ChangeNotifier {

    private final List<MessageListener> listeners = new ArrayList<>(4);

    public synchronized void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    protected synchronized void notifyListeners(String message) {
        for (MessageListener listener : listeners) {
            listener.onReceive(message);
        }
    }

    public synchronized void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }
}
