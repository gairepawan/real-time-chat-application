# Java Real-Time Chat Application

A portfolio project using **Java 21, Spring Boot, STOMP over WebSockets, concurrent collections, JUnit 5, and GitHub Actions CI**. It provides a browser-based chat interface with rooms and a bounded, in-memory message history.

**Status: educational demo, not production ready.** There is no authentication, privacy, persistence, rate limiting or moderation. Do not share private information. A room is a public name, not an access control boundary: a connected client can subscribe to any room. Message history disappears on restart.

## Features

- Browser chat UI at `http://localhost:8080/`.
- WebSocket endpoint `/ws`; publish to `/app/chat.send`; subscribe to `/topic/rooms/{room}`.
- Join chat rooms (e.g. `general`, `tech`).
- Fetch up to 100 recent messages per room with `GET /api/rooms/{room}/messages`.
- Validate room names, message text, and sender lengths.
- In-memory concurrent room map and synchronized bounded per-room history.
- Five JUnit tests and a GitHub Actions Maven CI workflow.

## Prerequisites

Install **JDK 21** and **Apache Maven 3.9+**. The frontend loads `@stomp/stompjs` from jsDelivr, so the browser needs Internet access for that script.

```bash
java -version
mvn -version
```

## Run locally

```bash
git clone https://github.com/gairepawan/real-time-chat-application.git
cd real-time-chat-application
mvn clean verify
mvn spring-boot:run
```

Or extract the project ZIP and run the last two commands from the folder containing `pom.xml`. Open **http://localhost:8080/** in two browser tabs, join `general` using different names, and send messages. Both tabs should receive messages immediately while connected.

Run tests only: `mvn test`.

## Project structure

```text
src/main/java/com/portfolio/chat/
  ChatApplication.java     Spring Boot entry point
  WebSocketConfig.java     STOMP endpoint and simple broker
  ChatController.java      Receives and broadcasts messages
  ChatService.java         Concurrent room histories
  ChatMessage.java         Stored message record
  IncomingMessage.java     Incoming payload definition
  HistoryController.java   Read-only history HTTP endpoint
src/main/resources/
  application.properties
  static/index.html        Browser chat interface
src/test/java/com/portfolio/chat/
  ChatServiceTest.java
.github/workflows/ci.yml
pom.xml
```

## DSA and concurrency

| Task | Structure | Typical complexity |
| --- | --- | --- |
| Find a room | `ConcurrentHashMap` | O(1) average |
| Append a message | `ArrayDeque` | O(1) amortized |
| Evict oldest when full | `ArrayDeque.removeFirst` | O(1) |
| Copy history of k messages | `ArrayList` / immutable `List` | O(k) |

A `ConcurrentHashMap` makes room lookup safe across threads. Each room's `ArrayDeque` is protected with synchronization for append, eviction, and history snapshots. Only the latest 100 messages are held **per room**. The number of room names is not capped; production applications need tighter limits and storage policies.

## CI/CD

The `.github/workflows/ci.yml` workflow runs Maven verification on pushes and pull requests to `main`. Inspect **GitHub → Actions**. This project has **CI only**; there is no deployment pipeline yet.

## Security and limitations

- **No authorization:** anyone who can connect can read/send messages in any room.
- **Not private:** demo rooms are public; do not send secrets or personal data.
- **In-memory:** messages are deleted when the server restarts.
- **No production scaling:** Spring's simple broker is for demos and single-node use.
- **No abuse prevention:** add authentication, per-user identity, rate limiting, monitoring, and moderation before deployment.
- The UI uses `textContent`, not `innerHTML`, to show user text safely.

## Roadmap

1. Authentication and authenticated sender identity.
2. Database-backed messages and paginated history.
3. Presence/online status with authenticated sessions.
4. Direct messaging and access-controlled private rooms.
5. Integration tests, CI security checks, and a deployment pipeline.

No open-source license has been selected; default copyright restrictions apply.
