package tests;

import static org.testng.Assert.*;

import cs671.*;

import java.util.*;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

@charpov.grader.Test(val=10)
class HW2Bonus {

  static String x = "/home/csadmin/cs671/public_html/x.jar";
  static String y = "/home/csadmin/cs671/public_html/y.jar";
  static String u = "http://www.cs.unh.edu/~cs671/x.jar";

  @charpov.grader.Test(val=2) void test1 () {
    assertEquals(test("-jar", x, "foo.Bar"), 4);
  }

  @charpov.grader.Test(val=3) void test4 () {
    assertEquals(test("-jar", x, "tests.T8", "foo.Bar"), 5);
  }

  @charpov.grader.Test(val=2) void test2 () {
    assertEquals(test("-jar", u, "foo.Bar"), 4);
  }

  @charpov.grader.Test(val=3) void test3 () {
    assertEquals(test("-jar", x, "-jar", y, "bar.Foo", "foo.Bar"), 6);
  }

  int test (String... args) {
    PrintStream err = System.err;
    PrintStream out = System.out;
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(w);
    System.setOut(p);
    System.setErr(p);
    try {
      Tester.main(args);
    } finally {
      System.setErr(err);
      System.setOut(out);
      p.close();
    }
    int n = 0;
    for (String s : w.toString().split("\n")) {
      if (s.contains("foo.Bar.test1"))
        n++;
      else if (s.contains("foo.Bar.test2"))
        n++;
      else if (s.contains("foo.Bar.test3"))
        n++;
      else if (s.contains("bar.Foo.test1"))
        n++;
      else if (s.contains("bar.Foo.test2"))
        n++;
      else if (s.contains("tests.T8"))
        n++;
      else if (s.contains("SCORE = 50"))
        n++;
    }
    return n;
  }
}