package com.ankamagames.dofus.core.network.frames;

import org.apache.log4j.Logger;

import com.ankamagames.dofus.core.network.DofusConnector;

public abstract class Frame {

    protected static final Logger log = Logger.getLogger(Frame.class);

    protected DofusConnector connector;

    public Frame(DofusConnector connector) {
        this.connector = connector;
    }
}