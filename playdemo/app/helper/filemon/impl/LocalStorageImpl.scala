package helper.filemon.impl

import helper.filemon.api.LocalStorage
import java.io.File
import play.api.Logger

class LocalStorageImpl extends LocalStorage {

  val tempFolder = "data/temp"
  val _tempFolder: File = new File(tempFolder)

  val localFolder = "data/local"
  val _localFolder: File = new File(localFolder)

  val imageFolder = "data/prod"
  val _imageFolder: File= new File(imageFolder)
  
  def LocalStorageImpl(){
    checkFolder("Local folder", _localFolder)
  }

  def checkFolder(name: String, file: File) {
    if (file.isFile()) {
      throw new RuntimeException(file.getAbsolutePath() + " is not a valid folder");
    } else if (!file.exists()) {
      file.mkdirs();
    }
    Logger.info(name + " is set to " + file.getAbsolutePath());
  }

  override def getTempFile(name: String, asFolder: Boolean): File = {
    val f = new File(_tempFolder, name);
    if (asFolder) {
      f.mkdirs();
    } else {
      f.getParentFile().mkdirs();
    }
    return f;
  }

  override def getThumbnailFile(name: String, asFolder: Boolean): File = {
    val f = new File(_imageFolder, name);
    if (asFolder) {
      f.mkdirs();
    } else {
      f.getParentFile().mkdirs();
    }
    return f;
  }

  override def getLocalFile(name: String, asFolder: Boolean): File = {
    val f = new File(_localFolder, name);
    if (asFolder) {
      f.mkdirs();
    } else {
      f.getParentFile().mkdirs();
    }
    return f;
  }
}