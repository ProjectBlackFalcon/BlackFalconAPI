package com.ankamagames.dofus.network;


import com.ankamagames.dofus.network.utils.DofusDataReader;
import com.ankamagames.dofus.network.utils.DofusDataWriter;

public abstract class NetworkMessage {

	public abstract void serialize(DofusDataWriter writer);
	public abstract void deserialize(DofusDataReader reader);

}
