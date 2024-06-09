package Server;
import Abitur.*;

import java.nio.charset.StandardCharsets;

public class Packet {
    public interface Element {
        int[] toInt();
    }

    public static class StringElement implements Element {
        private final String value;
        public StringElement(String value) {
            this.value = value;
        }
        public int[] toInt() {
            byte[] bytes = new byte[this.value.length() + 1];
            bytes[0] = (byte) this.value.length();
            System.arraycopy(this.value.getBytes(StandardCharsets.UTF_8), 0, bytes, 1, this.value.length());
            int[] integers = new int[(int) Math.ceil((double) bytes.length / 4)];
            for (int i = 0; i < integers.length; i++) {
                int current = 0;
                for (int j = 0; j < 4; j++) {
                    int arrayPos = i * 4 + j;
                    current += ((int) bytes[arrayPos]) >> (j * 8);
                }
                integers[i] = current;
            }
            return integers;
        }
    }
    public static class IntElement implements Element {
        private final int value;
        public IntElement(int value) {
            this.value = value;
        }
        public int[] toInt() {
            return new int[]{this.value};
        }
    }
    public static class FloatElement implements Element {
        private final float value;
        public FloatElement(float value) {
            this.value = value;
        }
        public int[] toInt() {
            return new int[] {Float.floatToIntBits(this.value)};
        }
    }

    private final int id;
    private final Queue<Element> content;

    public Packet(int id, Queue<Element> content) {
        this.id = id;
        this.content = content;
    }

    public int[] toInt() {
        Queue<Integer> integers = new Queue<>();
        integers.enqueue(this.id);
        while (!content.isEmpty()) {
            Element element = content.front();
            int[] byteRepresentation = element.toInt();
            for (int b : byteRepresentation) {
                integers.enqueue(b);
            }
            content.dequeue();
        }
        return util.toPrimitive(util.toArray(integers));
    }
    public byte[] toBytes() {
        return util.IntArrayToByteArray(this.toInt());
    }
}
