package tests;

import static org.testng.Assert.*;

import cs671.*;

import java.util.*;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

@charpov.grader.Test(val=9)
class HW2Grading3 {

  Tester tester;

  void BEFORE () {
    tester = new Tester(tests.Timing.class);
    tester.setPrintWriter(null);
  }

  @charpov.grader.Test(timeout=7500, val=3) void test () {
    long time = System.nanoTime();
    tester.run();
    time = System.nanoTime() - time;
    double rTime = time / 1e9;
    assertTrue(rTime >= 7 && rTime < 7.1);
    List<TestResult> results = tester.getResults();
    rTime = results.get(0).getDuration();
    assertTrue(rTime >= 1 && rTime < 1.1);
    rTime = results.get(1).getDuration();
    assertTrue(rTime < 0.1);
  }
}

class Timing implements Testable {
  @Test void shortTest () {
  }
  @Test void longTest () throws Exception {
    Thread.sleep(1000);
  }
  public boolean beforeMethod (Method m) throws Exception {
    Thread.sleep(1500);
    return true;
  }
  public void afterMethod (Method m) throws Exception {
    Thread.sleep(1500);
  }
}