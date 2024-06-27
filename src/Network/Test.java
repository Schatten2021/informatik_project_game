package Network;
import logging.Level;
import logging.Logger;

import java.io.IOException;

@SuppressWarnings("ALL")
public class Test {
    Logger root = new Logger("root");
    Logger networkPacketLogger = new Logger("Network.Packets");
    Logger networkLogger = new Logger("Network");
    boolean inGame = false;
    boolean doNothing;
    int round = 0;
    public static void main(String[] args) throws IOException {
        new Test("test", "123", false);
    }
    public Test(String username, String password, boolean doNothing) throws IOException {
        root.setLevel(Level.ALL);
        root.addHandler(new logging.ConsoleLogger());
        networkLogger.setLevel(Level.INFO);
        Server server = new Server("localhost", 8080);
        server.login(username, password);
        this.doNothing = doNothing;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (server.alive()) {
            update(server);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        root.debug("server not alive anymore");
    }
    private void update(Server server) {
        server.update();
        if (!server.inGame()) {
            if (inGame)
                root.info("was in game!");
            inGame = false;
            return;
        }
        inGame = true;
        if (round == server.getRound()) {
            return;
        }
        server.useAbility(server.getAbilities()[0]);
        server.finishRound();
        round++;
    }
    private void doSomething() {
//        root.debug("doing something");
    }
}
