package com.dictao.dtp.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

public class MetadataReader {

    public static Map<String, Serializable> read(byte[] metadatas) throws IOException, ClassNotFoundException{
        if (metadatas == null) return null;
        Map<String, Serializable> md = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(metadatas);
        ObjectInputStream in = new ObjectInputStream(bais);
        md = (Map<String, Serializable>)in.readObject();
        return md;
    }
}
