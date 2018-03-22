import java.io.*;

/**
 * Class to converts file to byte Objects
 * @author Roman Gaev
 */
public class ObjectConverter {

    //converting SimpleExample object to byte[].
    public static byte[] getByteArrayObject(File file){

        byte[] byteArrayObject = null;
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(file);
            oos.close();
            bos.close();
            byteArrayObject = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return byteArrayObject;
        }
        return byteArrayObject;
    }
    //converting byte[] to SimpleExample
    public static File getJavaObject(byte[] convertObject){
        File obj = null;

        ByteArrayInputStream bais;
        ObjectInputStream ins;
        try {

            bais = new ByteArrayInputStream(convertObject);

            ins = new ObjectInputStream(bais);
            obj =(File) ins.readObject();

            ins.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}