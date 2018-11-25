/**
 * File : CommunicationEventListener.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 25.11.2018
 *
 * This interface is used to do a call back when the server response arrived
 */
package com.example.mathieu.sym_labo2;

import java.util.EventListener;

public interface CommunicationEventListener extends EventListener {
    boolean handleServerResponse(byte[] response);
}
