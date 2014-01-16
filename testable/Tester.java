//Main Thing to work is implimenting Annotation stuff
//Trying Lisp-esq syntax for closing braces
package testable;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
//Testable is for classes
//@Test is for methods

/**
 *Test runner. Instances of this class are used to run the tests
 *methods found in classes that implement Testable.
 *@author Tucker DiNapoli using api by Michel Charpentier
 */
public class Tester implements Runnable{
  boolean hasrun=false;
  static ArrayList<Class<? extends Testable>> classes;
  //static ArrayList<Class<? extends Testable>> argClasses;
  PrintWriter output=new PrintWriter(new OutputStreamWriter(System.err),true);
  //macro for my convience
  private static void println(String text){System.out.println(text);}
  /**
   *Creates a tester for the given classes
   */
  @SafeVarargs
  public Tester(Class<? extends Testable>...classes){
    //to test if Class is testable
    for (Class<? extends Testable> i : classes){
      assert(Testable.class.isAssignableFrom(i));
      Tester.classes.add(i);}
    //maybe
  }
  Tester (){}
  /**
   *Creates a tester for the given classes
   */
  public Tester(Collection<Class<? extends Testable>> classes){
    for (Class<? extends Testable> i : classes){
      assert(Testable.class.isAssignableFrom(i));
      Tester.classes.add(i);}
  }
  /**
   *SetPrint Writer Method
   *Sets the tester output. By default, the output is System.err.
   * It is valid to set the output to null,
   * in which case the tester is completely silent.
   *@param w - the output for the tester info; can be null
   */
  public void setPrintWriter(PrintWriter W){output=W;}
  //need to revise comment to fit my implimentation
  /**
   *Run Method
   *Runs the tests. All the classes are processed in the order in which
   *they were given. For each class, all the tests are run
   *on the same instance in order of the method names.
   *If no instance can be created, the class is skipped
   *with an error message. If method beforeMethod fails or
   *returns false, the corresponding test is skipped with
   *a warning message. After each test, method afterMethod
   *is run and a warning is displayed if it fails.
   *
   *In general, no-argument methods are valid test methods
   *if they are annotated, even when they are non-public or non-void.
   *Static method and methods that require arguments are ignored.
   *A warning is displayed if they are test-annotated.
   *@throws IllegalStateException - if this tester has already been run
   */
  public void run(){
    /**
     *Class Testpkg
     */
    class Testpkg implements TestResult{
      //initalized in constructor
      Method method;double weight;String info;
      //initalized later
      boolean success;double duration;
      Object returned;Throwable error;
      /**
       *Testpkg Constructor
       */
      Testpkg(Method method,double weight,String info){
        this.method=method;this.weight=weight;this.info=info;}
      public double getWeight(){return weight;}
      public boolean success(){return success;}
      public String getInfo(){return info;}
      public double getDuration(){return duration;}
      public Throwable error(){return error;}}

    ArrayList<ArrayList<Testpkg>> classTests=new ArrayList<>();
    for(Class<? extends Testable> foo : classes){
      ArrayList<Testpkg> tests=new ArrayList<Testpkg>();
      //do we need this test?(test if class is testable)
      for (Method meth : foo.getDeclaredMethods()){
        try{
          meth.setAccessible(true);}
        catch(SecurityException ex){
          output.println("Security prevents you from running your tests.");}
        Testable temp;
        //put these tests in constructor?(test if method is annotated w/test)
        if(!meth.isAnnotationPresent(Test.class)){
          continue;}
        else if (meth.getParameterTypes().length !=0){
          //test if method takes parameters
          output.println(String.format("Warning method %s is annotated with "
                                       +"@Test but takes parameters",
                                       meth.toString()));
          continue;}
        else if (Modifier.isStatic(meth.getModifiers())){
          //test if method is static
          output.println(String.format("Waring static method %s is annotated"
                                       +" with @Test",meth.toString()));
          continue;}
        //method is ok, moving on
        try {//try to instancate object
          //Constructor cons=foo.getConstructor();
          temp=(Testable)foo.newInstance();}
        catch(InstantiationException | IllegalAccessException
              | LinkageError ex){
          output.println(String.format("Error Could not instantiate %s",
                                       foo.toString()));
          break;}
        try{//try to get annotation parameters(do we need this?)
          Test annotate=meth.getAnnotation(Test.class);
          //also how to deal with this ?
          ArrayList<Field> fields=new
            ArrayList<Field> (Arrays.asList
                              (meth.getClass().getDeclaredFields()));
          assert(fields.contains(annotate.weight()));
          assert(fields.contains(annotate.info()));
          tests.add(new Testpkg(meth,fields.get(0).getDouble(annotate.weight()),
                                (String)fields.get(1).get(annotate.info())));}
        catch(IllegalAccessException | IllegalArgumentException
              | NullPointerException | ClassCastException ex){
        }
        for(Testpkg testpkg : tests){
          if(testpkg.weight <=0){continue;}
          try{//invoke beforeMethod
            boolean check=temp.beforeMethod(testpkg.method);
            if (!check){
              throw new Exception();}}
          catch (Exception ex){
            output.println(String.format("Warning:Before Method for method"
                                           +" %s has failed",
                                           testpkg.method.toString()));
            continue;}
          try{//invoke method
            testpkg.duration=(double)System.nanoTime();
            testpkg.method.invoke(temp);
            testpkg.duration=testpkg.duration-(double)System.nanoTime();}
          catch(Throwable ex){
            testpkg.duration=testpkg.duration-(double)System.nanoTime();
            testpkg.error=ex;}
          try{//invoke afterMethod
            temp.afterMethod(testpkg.method);}
          catch(Exception ex){
            output.println(String.format("Warning:After Method for"
                                         +" method %s has failed",
                                         testpkg.method.toString()));}}}
      classTests.add(tests);}
    hasrun=true;}
  /**
   *get Results Method
   *Test results. This method returns a list that contains
   * a TestResult object for each test that was run
   *(in the order the test method were actually run).
   *The returned list can be modified and modifications will
   *affect subsequent calls to getResults (i.e., this method
   *does not make copies of the list).
   *@throws IllegalStateException - if the tester has not yet been run
   */
  public List<TestResult> getResults(){
    return null;
  }
  /**
   *Main Method
   *Starts a console-based application. 
   *Command line arguments are the names of the classes 
   *to be tested. The application produces a summary output 
   *of tests that succeeded and tests that failed.
   */
  public static void main(String[] args){
    //Need to see if Classes are Testable
    if (args.length<=0 || args[0]=="-h" || args[0]=="--help"){
      println("Call with one or more names of testable classes");
      return;}
    ClassLoader loader=ClassLoader.getSystemClassLoader();
    Class<?> temp;
    for (String i : args){
      try{
        temp=loader.loadClass(i);
        if (Testable.class.isAssignableFrom(temp)){
          //if ^ that passes than v that cast is ok
          classes.add((Class<? extends Testable>)temp);}}
      catch(ClassNotFoundException ex){
        println("Class "+i+" not found");}}
    if (classes == null){
      println("No classes found to test");
      return;}
    Tester testRun=new Tester();
    List<TestResult> results;
    testRun.run();
    testRun.getResults();
    return;}}