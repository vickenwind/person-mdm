package com.forsuntech.prisonmdm.policy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by vicken on 2017/11/9.
 */

public class ReadPolicy {



    public String readSDFile(String fileName) throws IOException {

        File file = new File(fileName);

        FileInputStream fis = new FileInputStream(file);

        int length = fis.available();

        byte [] buffer = new byte[length];
        fis.read(buffer);

        String res = new String(buffer, "UTF-8");

        fis.close();
        return res;
    }
}
