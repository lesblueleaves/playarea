package com.cisco.d3a.filemon.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

public class FileHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);

	public static File download(File to, final InputStream in) throws Exception {
		Files.copy(new InputSupplier<InputStream>() {
			public InputStream getInput() {
				return in;
			}
		}, to);
		LOGGER.trace(to.getCanonicalPath() + " downloaded");
		return to;
	}

	public static File download(File to, String urlString, String authToken) throws Exception {
		URL url = new URL(UrlUtils.encodeUrl(urlString));
		
		URLConnection urlc = url.openConnection();
		urlc.setRequestProperty("Authorization", "Basic " + authToken);
		return download(to, urlc.getInputStream());
	}

    public static void sort(File baseFile, List<File> files) {
        Collections.sort(files, getFilenameComparator(baseFile));
    }

    public static List<File> sort(File baseFile, File folder) {
        return sort(baseFile, folder, null);
    }

    public static List<File> sort(File baseFile, File folder, FileFilter filter) {
        File[] files = folder.listFiles();
        if(files != null && files.length != 0) {
            List<File> tFiles = new ArrayList<File>();
            if(filter != null) {
            	for(File f : files) {
            		if(filter.accept(f)) tFiles.add(f);
            	}
            } else {
                tFiles.addAll(Arrays.asList(files));
            }
            FileHelper.sort(baseFile, tFiles);
            return tFiles;
        }
        return Collections.emptyList();
    }

    public static Comparator<File> getFilenameComparator(final File baseFile) {
    	final String baseName = baseFile != null ? FilenameUtils.getBaseName(baseFile.getName()) : "";
        return new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                String n1 = FilenameUtils.getBaseName(f1.getName());
                String n2 = FilenameUtils.getBaseName(f2.getName());
                try {
                    int i1 = Integer.parseInt(trimStart(baseName, n1));
                    int i2 = Integer.parseInt(trimStart(baseName, n2));
                    return i1 - i2;
                } catch(NumberFormatException nfe) {
                	LOGGER.error("Unexcepted file name: " + baseName + "[" + f1.getName() + ":" + f2.getName() + "]");
                	throw nfe;
                }
            }
        };
    }

    public static String trimStart(String baseName, String name) {
    	if(baseName.length() != 0 && name.startsWith(baseName)) {
    		name = name.substring(baseName.length());
    	}
    	return name;
    }
    
    public static void deleteFile(File file) {
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
        	try {
				FileUtils.forceDeleteOnExit(file);
			} catch (IOException e1) {
			}
            LOGGER.error(e.getMessage());
        }
    }
}
