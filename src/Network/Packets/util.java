package Network.Packets;

import Abitur.Queue;
import Network.Packets.Fields.Field;

public class util {
    public static byte[] concat(byte[] ... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
        }
        return result;
    }
    public static byte[] concatPacket(byte id, byte[]  ... data) {
        int length = 1;
        for (byte[] array : data) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int offset = 1;
        result[0] = id;
        for (byte[] array : data) {
            System.arraycopy(array, 0, result, offset, array.length);
        }
        return result;
    }
    public static byte[] generateBytes(byte id, Field ... fields) {
        int length = 1;
        Queue<byte[]> arrays = new Queue<>();
        for (Field field : fields) {
            byte[] array = field.getBytes();
            length += array.length;
            arrays.enqueue(array);
        }
        byte[] result = new byte[length];
        int offset = 1;
        result[0] = id;
        while (!arrays.isEmpty()) {
            byte[] data = arrays.front();
            System.arraycopy(data, 0, result, offset, data.length);
            offset += data.length;
            arrays.dequeue();
        }
        return result;
    }
}
