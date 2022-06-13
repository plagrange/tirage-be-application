package com.dictao.dtp.persistence;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import java.io.IOException;

public class MetadataWriter {

    static public byte[] write(Map<String, Serializable> mds) throws IOException
    {
        byte[] md = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(mds);
        oos.close();
        baos.close();
        md = baos.toByteArray();

        return md;
    }
}
