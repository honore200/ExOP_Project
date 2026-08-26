package com.omp.api.websocket;

import com.omp.common.dto.AlerteCreeePayload;
import com.omp.common.dto.EvenementCreePayload;
import jakarta.enterprise.event.Observes;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Flux temps reel unique pour tous les dashboards (cf plan §6, endpoint "WS /events/stream").
 * Diffuse les payloads CDI EvenementCreePayload/AlerteCreeePayload publies par
 * EventService/AlertService (omp-common) - le decouplage CDI Event -> WebSocket evite tout appel
 * direct entre omp-common (JAR) et omp-api (WAR).
 */
@ServerEndpoint("/events/stream")
public class EventsWebSocketEndpoint {

    private static final Logger LOG = Logger.getLogger(EventsWebSocketEndpoint.class.getName());
    private static final Set<Session> SESSIONS = new CopyOnWriteArraySet<>();
    private static final Jsonb JSONB = JsonbBuilder.create();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
    }

    public void onEvenementCree(@Observes EvenementCreePayload payload) {
        broadcast(new WsMessage("EVENT_CREATED", JSONB.toJson(payload)));
    }

    public void onAlerteCreee(@Observes AlerteCreeePayload payload) {
        broadcast(new WsMessage("ALERT_CREATED", JSONB.toJson(payload)));
    }

    private void broadcast(WsMessage message) {
        String json = JSONB.toJson(message);
        for (Session session : SESSIONS) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(json);
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "Echec envoi WebSocket, session fermee", e);
                }
            }
        }
    }

    public record WsMessage(String type, String payload) {
    }
}
