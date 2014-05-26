package helper.filemon.api

import org.joda.time.DateTime

//case class ActionContext(
//    path:String,
////    action:FileAction,
//    server:String,
//    user:String,
//    token:String,
//    etag:String,
//    properties:Map[String, String],
//    time:DateTime
// ){
//  def getServiceLocation( endpoint:String):String= server+endpoint+path
//}

 class ActionContext(path:String,server:String,user:String,token:String,etag:String,properties:Option[Map[String, String]],time:DateTime){

  def this(path:String,server:String,user:String,token:String,etag:String)
  					= this(path,server,user,token,etag,None,DateTime.now())
  def getServiceLocation( endpoint:String):String= server+endpoint+path
  
  def getPath:String=path
}


