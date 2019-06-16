package com.ankamagames.dofus.network.utils;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ankamagames.dofus.network.NetworkMessage;
import com.ankamagames.dofus.util.FilesUtils;

public class ProtocolTypeManager {

    private static final Logger log = Logger.getLogger(ProtocolTypeManager.class);

    private static final Map<String, String> nameId = FilesUtils.parseTypeNameId();


    public static NetworkMessage getInstance(int id) {
        try {
            Object clazz = Class.forName(nameId.get(String.valueOf(id))).getConstructor().newInstance();
            return NetworkMessage.class.cast(clazz);
        } catch (Exception e) {
            log.error("Class not found", e);
        }
        return null;
    }
}
