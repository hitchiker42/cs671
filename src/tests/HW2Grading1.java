package tests;

import static org.testng.Assert.*;

import cs671.*;

import java.util.*;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

@charpov.grader.Test(val=24)
class HW2Grading1{
 @SafeVarargs
  final String[] createAndRun (boolean silent, Class<? extends Testable>... c) {
    PrintStream err = System.err;
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(w);
    System.setErr(p);
    try {
      @SuppressWarnings("varargs") // why is this still needed?
      Tester t = new Tester(c);
      if (silent)
        t.setPrintWriter(null);
      t.run();
    } finally {
      System.setErr(err);
      p.close();
    }
    return w.toString().split("\n");
  }

  @charpov.grader.Test(val=1) void test1 () {
    for (String s : createAndRun(false, tests.T1.class)) {
      if (s.contains("ERROR")
          && (s.contains("T1"))
          && (s.contains("foo") || s.contains("Error")))
        return;
    }
    fail("no suitable error message");
  }

  @charpov.grader.Test(val=1) void test2 () {
    for (String s : createAndRun(false, tests.T3.class)) {
      if (s.contains("ERROR")
          && (s.contains("T3")))
        return;
    }
    fail("no suitable error message");
  }

  @charpov.grader.Test(val=1) void test3 () {
    for (String s : createAndRun(false, tests.T4.class)) {
      if (s.contains("ERROR")
          && (s.contains("T4")))
        return;
    }
    fail("no suitable error message");
  }

  @charpov.grader.Test(val=1) void test4 () {
    for (String s : createAndRun(false, tests.T5.class)) {
      if (s.contains("WARNING")
          && (s.contains("foo"))
          && (s.contains("static")))
        return;
    }
    fail("no suitable warning message");
  }

  @charpov.grader.Test(val=1) void test5 () {
    for (String s : createAndRun(false, tests.T6.class)) {
      if (s.contains("WARNING")
          && (s.contains("foo")))
        return;
    }
    fail("no suitable warning message");
  }

  @charpov.grader.Test(val=1) void test6 () {
    for (String s : createAndRun(false, tests.T2.class)) {
      if (s.contains("WARNING")
          && (s.contains("d")))
        return;
    }
    fail("no suitable warning message");
  }

  @charpov.grader.Test(val=2) void test7 () {
    for (String s : createAndRun(true,
                                 tests.T1.class,
                                 tests.T2.class,
                                 tests.T3.class,
                                 tests.T4.class,
                                 tests.T5.class,
                                 tests.T6.class))
      assertTrue(s.trim().isEmpty());
  }


}