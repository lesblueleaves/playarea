package com.cisco.d3a.filemon.api;

import java.io.File;

public interface LocalStorage {
    public File getTempFile(String name, boolean asFolder);
    public File getLocalFile(String name, boolean asFolder);
    public File getThumbnailFile(String name, boolean asFolder);
}
