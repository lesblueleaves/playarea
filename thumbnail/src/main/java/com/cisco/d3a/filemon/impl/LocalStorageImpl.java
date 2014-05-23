package com.cisco.d3a.filemon.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.cisco.d3a.filemon.api.LocalStorage;

public class LocalStorageImpl implements LocalStorage, InitializingBean {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String tempFolder = ".";
    private File _tempFolder;

    private String localFolder = ".";
    private File _localFolder;

    private String imageFolder = ".";
	private File _imageFolder;

	@Override
	public void afterPropertiesSet() throws Exception {
        checkFolder("Local folder", _localFolder = new File(localFolder));
        checkFolder("Image folder", _imageFolder = new File(imageFolder));
        checkFolder("Temp folder", _tempFolder = new File(tempFolder));
    }

	private void checkFolder(String name, File file) {
		if(file.isFile()) {
            throw new RuntimeException(file.getAbsolutePath() + " is not a valid folder");
        } else if(!file.exists()) {
        	file.mkdirs();
        }
		LOGGER.info(name + " is set to " + file.getAbsolutePath());
	}
	
    public void setLocalFolder(String localFolder) {
        this.localFolder = localFolder;
    }

    public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}
	
	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}


	@Override
	public File getTempFile(String name, boolean asFolder) {
		File f = new File(_tempFolder, name);
		if(asFolder) {
			f.mkdirs();
		} else {
			f.getParentFile().mkdirs();
		}
		return f;
	}

	@Override
	public File getThumbnailFile(String name, boolean asFolder) {
		File f = new File(_imageFolder, name);
		if(asFolder) {
			f.mkdirs();
		} else {
			f.getParentFile().mkdirs();
		}
		return f;
	}

    @Override
    public File getLocalFile(String name, boolean asFolder) {
        File f = new File(_localFolder, name);
        if(asFolder) {
            f.mkdirs();
        } else {
            f.getParentFile().mkdirs();
        }
        return f;
    }
}
