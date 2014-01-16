package tests;

import static org.testng.Assert.*;

import cs671.*;

import java.util.*;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

@charpov.grader.Test(val=38)
class HW2Grading2{

Tester tester;

  void BEFORE () {
    tester = new Tester(tests.T2.class);
    tester.setPrintWriter(null);
  }

  @charpov.grader.Test(val=1) void test1 () {
    try {
      tester.getResults();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // OK
    }
  }

  @charpov.grader.Test(val=1) void test2 () {
    tester.run();
    tester.getResults();
    try {
      tester.run();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // OK
    }
  }

  @charpov.grader.Test(val=2) void test3 () {
    tester.run();
    List<TestResult> results = tester.getResults();
    assertEquals(results.size(), 2);
    assertFalse(T2.aRan);
    assertFalse(T2.dRan);
  }

  @charpov.grader.Test(val=2) void test4 () {
    tester.run();
    TestResult r = tester.getResults().get(0);
    assertEquals(r.getInfo(), "tests.T2.b");
    assertEquals(r.getWeight(), 2.3, 1e-5);
    assertNull(r.error());
    assertTrue(r.getDuration() < .01);
  }

  @charpov.grader.Test(val=2) void test5 () {
    tester.run();
    TestResult r = tester.getResults().get(1);
    assertEquals(r.getInfo(), "tests.T2.c");
    assertEquals(r.getWeight(), 3.7, 1e-5);
    assertTrue(r.error() instanceof AssertionError);
    assertTrue(r.getDuration() < .01);
  }

  @charpov.grader.Test(val=3) void test6 () throws Exception {
    PrintStream err = System.err;
    PrintStream out = System.out;
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(w);
    System.setOut(p);
    PrintStream e = new PrintStream("/dev/null");
    System.setErr(e);
    try {
      Tester.main(new String[] {"tests.T7"});
    } finally {
      System.setErr(err);
      System.setOut(out);
      p.close();
      e.close();
    }
    int line = 1;
    for (String s : w.toString().split("\n")) {
      s = s.trim();
      if (s.isEmpty())
        continue;
      switch (line) {
      case 1:
        assertTrue(s.contains("SUCCESSFUL TESTS:"));
        break;
      case 2:
        assertTrue(s.startsWith("tests.T7.foo (3.3) in "));
        assertTrue(s.endsWith("milliseconds"));
        break;
      case 3:
        assertTrue(s.contains("FAILED TESTS:"));
        break;
      case 4:
        assertTrue(s.startsWith("tests.T7.bar: bad test (7.1) from java.io.IOException"));
        break;
      case 5:
        assertTrue(s.contains("SCORE ="));
        assertTrue(s.contains("31") || s.contains("32"));
        break;
      default:
        fail();
      }
      line++;
    }
  }

  @charpov.grader.Test(val=1) void test7 () throws Exception {
    PrintStream err = System.err;
    PrintStream out = System.out;
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(w);
    System.setOut(p);
    System.setErr(p);
    try {
      Tester.main(new String[] {"java.util.ArrayList", "tests.T8"});
    } finally {
      System.setErr(err);
      System.setOut(out);
      p.close();
    }
    boolean ok = false;
    for (String s : w.toString().split("\n"))
      if (s.contains("java.util.ArrayList"))
        ok = true;
    assertTrue(ok);
    assertFalse(T8.fooRan);
  }

  @charpov.grader.Test(val=1) void test8 () throws Exception {
    PrintStream err = System.err;
    PrintStream out = System.out;
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(w);
    System.setOut(p);
    System.setErr(p);
    try {
      Tester.main(new String[] {"foo.Bar", "tests.T8"});
    } finally {
      System.setErr(err);
      System.setOut(out);
      p.close();
    }
    boolean ok = false;
    for (String s : w.toString().split("\n"))
      if (s.contains("foo.Bar"))
        ok = true;
    assertTrue(ok);
    assertFalse(T8.fooRan);
  }



}