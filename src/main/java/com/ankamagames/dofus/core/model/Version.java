package com.ankamagames.dofus.core.model;

public class Version {

    private int major;
    private int minor;
    private int release;
    private int revision;
    private int patch;

    public Version(final int major, final int minor, final int release, final int revision, final int patch) {
        this.major = major;
        this.minor = minor;
        this.release = release;
        this.revision = revision;
        this.patch = patch;
    }

    public Version() {
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(final int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(final int minor) {
        this.minor = minor;
    }

    public int getRelease() {
        return release;
    }

    public void setRelease(final int release) {
        this.release = release;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(final int revision) {
        this.revision = revision;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(final int patch) {
        this.patch = patch;
    }
}
