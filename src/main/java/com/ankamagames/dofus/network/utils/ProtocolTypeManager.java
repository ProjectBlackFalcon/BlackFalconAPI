package com.ankamagames.dofus.network.utils;

import org.apache.log4j.Logger;

import com.ankamagames.dofus.core.network.DofusConnector;
import com.ankamagames.dofus.network.NetworkMessage;

public class ProtocolTypeManager {

    private static final Logger log = Logger.getLogger(ProtocolTypeManager.class);


    public static NetworkMessage getInstance(int id) {
        try {
            Object clazz = Class.forName(DofusConnector.nameId.get(String.valueOf(id))).getConstructor().newInstance();
            return NetworkMessage.class.cast(clazz);
        } catch (Exception e) {
            log.error("Class not found", e);
        }
        return null;
    }
}
