package com.cisco.d3a.filemon.api;

import java.io.File;

public interface FileProcessor {
	public void onModified(File file, ActionContext context) throws Exception;
}
