package Server;

import Abitur.List;
import Abitur.Queue;

public class util {
    public static <T> T[] toArray(List<T> list) {
        list.toFirst();
        int size = 0;
        while (list.hasAccess()) {
            size++;
        }
        T[] array = genericArray(size);
        list.toFirst();
        for (int i = 0; i < size; i++) {
            array[i] = list.getContent();
            list.next();
        }
        return array;
    }
    public static <T> T[] toArray(Queue<T> queue) {
        List<T> list = new List<>();
        while (!queue.isEmpty()) {
            list.append(queue.front());
            queue.dequeue();
        }
        return toArray(list);
    }

    public static <T> T[] genericArray(int length) {
        //noinspection unchecked
        return (T[]) new Object[length];
    }
    public static int[] ByteArrayToIntArray(byte[] bytes) {
        int[] intArray = new int[ceil(((float) bytes.length) / 4)];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = ((int) bytes[i * 4] >> 24);
            intArray[i] += ((int) bytes[i * 4 + 1] >> 16);
            intArray[i] += ((int) bytes[i * 4 + 2] >> 8);
            intArray[i] += bytes[i * 4 + 3];
        }
        return intArray;
    }
    public static byte[] IntArrayToByteArray(int[] intArray) {
        byte[] bytes = new byte[intArray.length * 4];
        for (int i = 0; i < intArray.length; i++) {
            bytes[i * 4] = (byte) (intArray[i] >> 24);
            bytes[i * 4 + 1] = (byte) (intArray[i] >> 16);
            bytes[i * 4 + 2] = (byte) (intArray[i] >> 8);
            bytes[i * 4 + 3] = (byte) (intArray[i]);
        }
        return bytes;
    }
    public static int ceil(float a) {
        return (int) ((a) + (a % 1 == 0 ? 0 : 1));
    }

    public static int[] toPrimitive(Integer[] integers) {
        int[] result = new int[integers.length];
        for (int i = 0; i < integers.length; i++) {
            result[i] = integers[i];
        }
        return result;
    }
    public static byte[] toPrimitive(Byte[] bytes) {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        return result;
    }
}
