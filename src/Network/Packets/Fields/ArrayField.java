package Network.Packets.Fields;

import Abitur.Queue;
import logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ArrayField <T extends Field> implements Field {
    public final T[] fields;
    public ArrayField(T[] fields) {
        this.fields = fields;
    }

    public T[] getValue() {
        return this.fields;
    }

    @Override
    public byte[] getBytes() {
        Queue<byte[]> queue = new Queue<>();
        int length = 0;
        int count = 0;
        for (T field : this.fields) {
            byte[] fieldBytes = field.getBytes();
            queue.enqueue(fieldBytes);
            length += fieldBytes.length;
            count++;
        }
        byte[] bytes = new byte[length + 4];
        System.arraycopy(new IntegerField(count).getBytes(), 0, bytes, 0, 4);
        int pos = 4;
        while (!queue.isEmpty()) {
            byte[] elementBytes = queue.front();
            System.arraycopy(elementBytes, 0, bytes, pos, elementBytes.length);
            pos += elementBytes.length;
            queue.dequeue();
        }
        return bytes;
    }
    public static <T extends Field> ArrayField<T> fromStream(InputStream stream, Class<T> clazz) throws IOException {
        IntegerField length = IntegerField.fromStream(stream);
        //noinspection unchecked
        T[] data = (T[]) new Field[length.value];
        for (int i = 0; i < length.value; i++) {
            //noinspection unchecked
            try {
                //noinspection unchecked
                data[i] = (T) clazz.getMethod("fromStream", InputStream.class).invoke(null, stream);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                new Logger("Server.Packets.Fields.ArrayField").ferror("couldn't find method \"fromStream\" on class %s", clazz.getName());
            }
        }
        return new ArrayField<>(data);
    }
    public String toString() {
        StringBuilder res = new StringBuilder("[");
        for (int i = 0; i < this.fields.length; i++) {
            res.append(fields[i] == null ? "null" : fields[i].toString());
            if (i < (this.fields.length - 1))
                res.append(", ");
        }
        res.append("]");
        return res.toString();
    }
}
