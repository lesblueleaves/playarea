package helper.filemon.api

import java.io.File

trait LocalStorage {
  def getTempFile(name: String, asFolder: Boolean): File
  def getLocalFile(name: String, asFolder: Boolean): File
  def getThumbnailFile(name: String, asFolder: Boolean): File
}