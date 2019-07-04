package com.ankamagames.dofus.core.network.frames;

import com.ankamagames.dofus.core.network.DofusConnector;
import com.ankamagames.dofus.network.messages.game.basic.BasicLatencyStatsMessage;
import com.ankamagames.dofus.network.messages.game.basic.BasicLatencyStatsRequestMessage;
import com.ankamagames.dofus.network.messages.game.basic.SequenceNumberMessage;
import com.ankamagames.dofus.network.messages.game.basic.SequenceNumberRequestMessage;
import com.ankamagames.dofus.network.messages.web.haapi.HaapiApiKeyRequestMessage;
import com.ankamagames.dofus.network.messages.web.haapi.HaapiSessionMessage;

public class SynchronisationFrame extends Frame {

    public SynchronisationFrame(final DofusConnector connector) {
        super(connector);
    }

    public void process(final SequenceNumberRequestMessage message) throws Exception {
        this.connector.sendToServer(new SequenceNumberMessage(this.connector.getLatencyFrame().sequence++));
    }

    public void process(final BasicLatencyStatsRequestMessage message) throws Exception {
        BasicLatencyStatsMessage basicLatencyStatsMessage = new BasicLatencyStatsMessage(
            this.connector.getLatencyFrame().getLatencyAvg(),
            this.connector.getLatencyFrame().getSamplesCount(),
            this.connector.getLatencyFrame().GetSamplesMax()
        );
        this.connector.sendToServer(basicLatencyStatsMessage);
    }

    public void process(final HaapiSessionMessage message) throws Exception {
        this.connector.sendToServer(new HaapiApiKeyRequestMessage());
    }
}
