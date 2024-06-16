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
    public static void main(String[] args) throws IOException {
        new Test("test", "123", false);
    }
    public Test(String username, String password, boolean doNothing) throws IOException {
        Logger root = new Logger("root");
        root.setLevel(Level.ALL);
        root.addHandler(new logging.ConsoleLogger());
        Server server = new Server("fms.nrw", 8080);
        server.login(username, password);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Queue<Packet> packets = server.getPackets();
        while (server.alive()) {
            if (packets.isEmpty()) {
                continue;
            }
            Packet front = packets.front();
            switch (front) {
                case GameStart gameStart -> {
                    root.info("got gameStart");
                    if (!doNothing) server.send(new AbilityUsed(new IntegerField(1)));
                    server.send(new RoundFinished());
                }
                case RoundEnd roundEnd -> root.info("got roundEnd");
                case GameEnd gameEnd -> root.info("game ended");
                case null, default -> root.info("got packet " + front);
            }
            packets.dequeue();
        }
    }
}
