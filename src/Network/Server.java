package Network;


import Abitur.List;
import Abitur.Queue;
import Network.Connection.Connection;
import Network.Connection.State;
import Network.Packets.Downstream.*;
import Network.Packets.Fields.*;
import Network.Packets.Heartbeat;
import Network.Packets.Packet;
import Network.Packets.Upstream.AbilityUsed;
import Network.Packets.Upstream.RoundFinished;
import Network.dataStructures.Ability;
import Network.dataStructures.Effect;
import Network.dataStructures.Game;
import logging.Logger;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Connects to a server.
 * Used to transport data such as player data to the server where it will be stored and processed.
 * Required for online play.
 */
@SuppressWarnings("unused")
public class Server {
    /**
     * The internal connection.
     */
    private final Connection connection;

    private Game game;
    private final List<Game> previousGames = new List<>();

    /**
     * The logger for the Server module.
     */
    private final Logger logger = new Logger("Server");

    /**
     * The {@link MessageDigest}, that can hash byte arrays with <a href="https://wikipedia.org/wiki/SHA-2">SHA-256</a>.
     */
    private static final MessageDigest md;
    // required for to get the SHA-256 hash.
    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor for {@link Server}.
     * @param host The ip or url of the server to connect to.
     * @param port The port of the server. (e.g. 8080)
     */
    public Server(String host, int port) {
        this.connection = new Connection(host, port, false);
    }

    /**
     * Logs the current user in and start the connection.
     * @param username The username of the user.
     * @param password The password to the account of the user.
     */
    public void login(String username, String password) throws IOException {
        String hash = hash(password);
        logger.debug("logging in with hash: " + hash);
        this.connection.login(username, hash);
        this.connection.start();
    }

    /**
     * Similar to {@link #login}, but creates a new account.
     * @param username The name of the player.
     * @param password The password to the account.
     * @throws IOException When an IOException occurs while trying to sign up.
     */
    public void signup(String username, String password) throws IOException {
        String hash = hash(password);
        logger.debug("signing up with hash: " + hash);
        this.connection.signup(username, hash);
        this.connection.start();
    }

    /**
     * Helper function for {@link #login} and {@link #signup}, because the passwords are hashed (not transmitting and storing plaintext passwords, duh).
     * @param string The password to be hashed.
     * @return The hexadecimal representation of the bytes.
     */
    private static String hash(String string) {
        byte[] digest = md.digest(string.getBytes());
        byte[] b64Encoded = Base64.getEncoder().encode(digest);
        return new String(b64Encoded);
    }

    public boolean alive() {
        return this.connection.status != State.DISCONNECTED && this.connection.status != State.NOT_CONNECTED;
    }

    public void update() {
        if (!this.alive()) {
            this.logger.warn("Server is not connected");
            return;
        }
        this.connection.update();
        Queue<Packet> packets = this.connection.incoming;
        Queue<Packet> unhandledPackets = new Queue<>();
        while (!packets.isEmpty()) {
            Packet packet = packets.front();
            if (!this.handlePacket(packet))
                unhandledPackets.enqueue(packet);
            packets.dequeue();
        }
        while (!unhandledPackets.isEmpty()) {
            Packet packet = unhandledPackets.front();
            unhandledPackets.dequeue();
            this.connection.incoming.enqueue(packet);
        }
    }

    /**
     * handles a packet sent by the server
     * @param packet The packet sent by the server
     * @return Whether the packet has been handled by this piece of code.
     */
    private boolean handlePacket(Packet packet) {
        if (packet instanceof Heartbeat) {
            return true;
        } else if (packet instanceof GameStart) {
            this.game = new Game((GameStart) packet, this.connection.getName());
        } else if (packet instanceof Abilities) {
            Field[] abilities = ((Abilities) packet).abilities.fields;
            for (Field ability : abilities) {
                Ability.create((AbilityField) ability);
            }
        } else if (packet instanceof Effects) {
            Field[] effectFields = ((Effects) packet).effects.fields;
            for (Field effect : effectFields) {
                Effect.create((EffectField) effect);
            }
        } else if (packet instanceof GameEnd) {
            this.previousGames.append(this.game);
            this.game = null;
        } else if (packet instanceof RoundEnd) {
            RoundEnd roundEnd = (RoundEnd) packet;
            Network.Packets.Fields.Field[] abilitiesUsed = roundEnd.abilitiesUsed.fields;
            for (Network.Packets.Fields.Field abilityUsed : abilitiesUsed) {
                this.game.abilitiesUsed.append(new Network.dataStructures.AbilityUsed((Network.Packets.Fields.AbilityUsed) abilityUsed));
            }
        } else {
            return false;
        }
        return true;
    }
    public Ability getAbility(int id) {
        return Ability.load(id);
    }
    public Ability[] getAbilities() {
        return Ability.getAll();
    }

    /**
     * Gets all active effects for this player.
     * @return The active effects for this player.
     */
    public Effect[] getActiveEffects() {
        return this.game.getActiveEffects(true);
    }
    public Effect[] getActiveEffectsFromLastGame() {
        this.previousGames.toLast();
        if (this.previousGames.isEmpty())
            return null;
        return this.previousGames.getContent().getActiveEffects(true);
    }
    public int getRound() {
        return this.game.round;
    }
    public void useAbility(Ability ability) {
        this.connection.outgoing.enqueue(new AbilityUsed(new IntegerField(ability.id)));
    }
    public void finishRound() {
        this.connection.outgoing.enqueue(new RoundFinished());
    }
    public Game getCurrentGame() {
        return this.game;
    }
    public boolean inGame() {
        return this.game != null;
    }
}
