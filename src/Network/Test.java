package Network;
import Abitur.Queue;
import Network.Packets.Downstream.GameEnd;
import Network.Packets.Downstream.GameStart;
import Network.Packets.Downstream.RoundEnd;
import Network.Packets.Fields.IntegerField;
import Network.Packets.Packet;
import Network.Packets.Upstream.AbilityUsed;
import Network.Packets.Upstream.RoundFinished;
import logging.Level;
import logging.Logger;

import java.io.IOException;

public class Test {
    Logger root = new Logger("root");
    boolean sentRoundFinish = false;
    boolean doNothing;
    public static void main(String[] args) throws IOException {
        new Test("test", "123", false);
    }
    public Test(String username, String password, boolean doNothing) throws IOException {
        root.setLevel(Level.ALL);
        root.addHandler(new logging.ConsoleLogger());
        Server server = new Server("fms.nrw", 8080);
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
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        root.debug("server not alive anymore");
    }
    private void update(Server server) {
        Queue<Packet> packets = server.getPackets();
        if (packets.isEmpty()) {
//            this.doSomething();
            return;
        }
//        root.debug("packets not empty");
        Packet front = packets.front();
        if (front instanceof GameStart) {
            root.info("got gameStart");
            if (sentRoundFinish)
                return;
            if (!doNothing) server.send(new AbilityUsed(new IntegerField(1)));
            server.send(new RoundFinished());
            sentRoundFinish = true;
        } else if (front instanceof RoundEnd) {
            root.info("got roundEnd");
        } else if (front instanceof GameEnd) {
            root.info("game ended");
            sentRoundFinish = false;
        } else {
            root.info("got packet " + front);
        }
        packets.dequeue();
    }
    private void doSomething() {
//        root.debug("doing something");
    }
}
