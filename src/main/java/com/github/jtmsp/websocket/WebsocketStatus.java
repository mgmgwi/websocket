package com.github.jtmsp.websocket;

import javax.websocket.CloseReason;

public interface WebsocketStatus {

    default void wasOpened() {

    }

    default void wasClosed(CloseReason cr) {

    }

    default void hadError(Throwable t) {
        t.printStackTrace();
    }

}
