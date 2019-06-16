package com.ankamagames.dofus.core.mapper;

import com.ankamagames.dofus.network.types.version.Version;
import com.ankamagames.dofus.network.types.version.VersionExtended;

public class VersionMapper {

    public static VersionExtended versionToVersionExtended(final Version version){
        VersionExtended versionExtended = new VersionExtended();
        versionExtended.setMajor(version.getMajor());
        versionExtended.setMinor(version.getMinor());
        versionExtended.setRelease(version.getRelease());
        versionExtended.setRevision(version.getRevision());
        versionExtended.setPatch(version.getPatch());
        versionExtended.setBuildType(version.getBuildType());
        return versionExtended;
    }
}
