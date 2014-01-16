package cs671;
import java.io.*;
import java.util.Date;
public class Debug
{
  final static boolean DEBUG = false ;
  public static String timeD (){
    return new Date(System.currentTimeMillis()).toString();
  }
  static FileWriter logger;
  static PrintWriter tracer;
  public static void here (){
    if (DEBUG) {
      String fullClassName = Thread.currentThread().
        getStackTrace()[2].getClassName();
      String className = fullClassName.
        substring(fullClassName.lastIndexOf(".") + 1);
      String methodName = Thread.currentThread().
        getStackTrace()[2].getMethodName();
      int lineNumber = Thread.currentThread().
        getStackTrace()[2].getLineNumber();
      String message=("Here at: "+className + "."+
                      methodName + "():" + lineNumber+" in "+
                      Thread.currentThread().toString());
      System.err.println(message);
      try{
        logger=new FileWriter("debug.log",true);
        logger.write(message+"\n");
        logger.close();}
      catch(IOException ex){}
    }
  }
  public static void here (Object here_){
    String here_string=String.valueOf(here_);
    if (DEBUG) {
      String fullClassName = Thread.currentThread().
        getStackTrace()[2].getClassName();
      String className = fullClassName.
        substring(fullClassName.lastIndexOf(".") + 1);
      String methodName = Thread.currentThread().
        getStackTrace()[2].getMethodName();
      int lineNumber = Thread.currentThread().
        getStackTrace()[2].getLineNumber();
      String message=("Here at: "+className + "."+
                      methodName + "():" + lineNumber +" in "+
                      Thread.currentThread().toString()+" "+
                      here_string);
      System.err.println(message);
      try{
        logger=new FileWriter("debug.log",true);
        logger.write(message+"\n");
        logger.close();}
      catch(IOException ex){}
    }
  }
  public static void log (Object log_){
    if(DEBUG){
    String log_string=String.valueOf(log_);
    String fullClassName = Thread.currentThread().
      getStackTrace()[2].getClassName();
    String className = fullClassName.
      substring(fullClassName.lastIndexOf(".") + 1);
    String methodName = Thread.currentThread().
      getStackTrace()[2].getMethodName();
    int lineNumber = Thread.currentThread().
      getStackTrace()[2].getLineNumber();
    String message=(className + "."+ 
                    methodName + "():" + lineNumber+
                    "\nMessage: "+log_string);
    try{
      logger=new FileWriter("debug.log",true);
      logger.write(message+"\n");
      logger.close();
    }
    catch(IOException ex){}
    }
  }
  public static void log (){
    if(DEBUG){
    String fullClassName = Thread.currentThread().
      getStackTrace()[2].getClassName();
    String className = fullClassName.
      substring(fullClassName.lastIndexOf(".") + 1);
    String methodName = Thread.currentThread().
      getStackTrace()[2].getMethodName();
    int lineNumber = Thread.currentThread().
      getStackTrace()[2].getLineNumber();
    String message=(className + "."+ 
                    methodName + "():" + lineNumber);
    try{
      logger=new FileWriter("debug.log",true);
      logger.write(message+"\n");
      logger.close();
    }
    catch(IOException ex){}
    }
  }
  public static void trace (Throwable trace){
    try{
      tracer=new PrintWriter(new FileWriter("debug.log",true));
      tracer.println(trace.getLocalizedMessage());
      trace.fillInStackTrace();
      trace.printStackTrace(tracer);
    }
    catch(IOException ex){}
  }
  public static void here_time (){
    if (DEBUG) {
      String fullClassName = Thread.currentThread().
        getStackTrace()[2].getClassName();
      String className = fullClassName.
        substring(fullClassName.lastIndexOf(".") + 1);
      String methodName = Thread.currentThread().
        getStackTrace()[2].getMethodName();
      int lineNumber = Thread.currentThread().
        getStackTrace()[2].getLineNumber();
      String message=("Here at: "+className + "."+ 
                      methodName + "():" + lineNumber+" in "+
                      Thread.currentThread().toString()+" at "
                      +timeD());
      System.err.println(message);
      try{
        logger=new FileWriter("debug.log",true);
        logger.write(message+"\n");
        logger.close();}
      catch(IOException ex){}
    }
  }
}
