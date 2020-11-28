package net.thevpc.app.vainruling.core.service.fs;

import net.thevpc.common.vfs.VFile;

import java.io.File;

public class MirroredPath {
    private VFile path;
    private File nativePath;

    public MirroredPath(VFile path, File nativePath) {
        this.path = path;
        this.nativePath = nativePath;
    }

    public VFile getPath() {
        return path;
    }

    public File getNativePath() {
        return nativePath;
    }
}
