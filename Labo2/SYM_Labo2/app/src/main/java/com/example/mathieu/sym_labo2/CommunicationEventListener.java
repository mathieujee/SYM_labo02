package com.example.mathieu.sym_labo2;

import java.util.EventListener;

public interface CommunicationEventListener extends EventListener {
    boolean handleServerResponse(byte[] response);
}
