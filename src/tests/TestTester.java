// $Id: TestTester.java 148 2013-02-22 19:18:33Z cs671a $

package tests;

import static org.testng.Assert.*;

import cs671.*;

import java.util.*;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

class TestTester {

  public static void main (String[] args) throws Exception {
      //    java.util.logging.Logger.getLogger("charpov.grader")
      //.setLevel(java.util.logging.Level.WARNING);
    // Note: these classes shouldn't run concurrently because of the tricks
    // they do with System.out and System.err.
      //    charpov.grader.Tester tester = new charpov.grader.Tester(HW2Grading1.class,
      //                                                       HW2Grading2.class,
      //                                                       HW2Grading3.class,
      //                                                       HW2Bonus.class);
      //tester.setConcurrencyLevel(1);
      //tester.run();
  }
}

class TestableAdapter implements Testable {
  public boolean beforeMethod (Method m) throws Exception {
    return true;
  }
  public void afterMethod (Method m) throws Exception {
  }
}

class T1 extends TestableAdapter {
  T1 () {
    throw new Error("foo");
  }
}

abstract class T3 extends TestableAdapter {
}

class T4 extends TestableAdapter {
  static {
    Object x = null;
    x.toString();
  }
}

class T5 extends TestableAdapter {
  @Test static void foo () {
  }
}

class T6 extends TestableAdapter {
  @Test void foo (int x) {
  }
}

class T2 extends TestableAdapter {
  int i;
  static boolean aRan, dRan;
  T2 () {
    aRan = dRan = false;
  }
  @Override public boolean beforeMethod (Method m) {
    return i++ < 2;
  }
  @Test(weight=-2.3) void a () {
    aRan = true;
  }
  @Test(weight=2.3) int b () {
    assertTrue(i < 2);
    return i;
  }
  @Test(weight=3.7) void c () {
    assertTrue(i < 2);
  }
  @Test(weight=7) boolean d () {
    return (dRan = true);
  }
}

class T7 extends TestableAdapter {
  @Test(weight=3.3) void foo () {
  }
  @Test(weight=7.1, info="bad test") void bar () throws Exception {
    throw new java.io.IOException("I/O");
  }
  @Test(weight=-1) void noTest () {
  }
}

class T8 extends TestableAdapter {
  T8 () {
    fooRan = false;
  }
  @Test void foo () {
    fooRan = true;
  }
  static boolean fooRan;
}



