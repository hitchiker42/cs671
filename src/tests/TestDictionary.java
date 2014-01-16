// $Id: TestDictionary.java 189 2013-04-22 14:23:47Z cs671a $

package tests;

import charpov.grader.*;
import static org.testng.Assert.*;

import cs671.BoggleDictionary;

import java.util.List;
import java.util.Iterator;
import java.util.Scanner;
import java.net.URL;

@Test(val=20)
class TestDictionary {

  static final List<String> WORDS;
  static {
    List<String> s = new java.util.ArrayList<>(117969);
    WORDS = s;
    try {
      URL url = new URL("http://cs.unh.edu/~cs671/words.txt");
      Scanner in = new Scanner(url.openStream());
      while (in.hasNext())
        s.add(in.next().toUpperCase());
      in.close();
    } catch (java.io.IOException e) {
      fail("cannot load words");
    }
  }

  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    new Tester(TestDictionary.class).run();
  }

  BoggleDictionary dict;

  void BEFORE () {
    dict = new BoggleDictionary(WORDS);
  }

  @Test(val=2) void test1 () throws Exception {
    assertEquals(dict.size(), 117969);
  }

  @Test(val=3) void test2 () throws Exception {
    List<String> s = new java.util.ArrayList<>(117969);
    for (String w : dict)
      s.add(w);
    assertEquals(s, WORDS);
  }

  @Test(val=3) void test3 () throws Exception {
    List<String> s = new java.util.ArrayList<>(117969);
    Iterator<String> iter = dict.iterator();
    for (int i=0; i<117969; i++)
      s.add(iter.next());
    assertEquals(s, WORDS);
  }

  @Test(val=5) void test4 () throws Exception {
    assertTrue(dict.find("MAIEUTIC") > 0);
    assertTrue(dict.find("maieutic") < 0);
    assertTrue(dict.find("MAIEUT") == 0);
    assertTrue(dict.find("MAIEUTIX") < 0);
  }

  @Test(val=4) void test5 () throws Exception {
    assertTrue(dict.hasWord("DINGDONG"));
    assertFalse(dict.hasWord("dingdong"));
    assertFalse(dict.hasWord("DINGDON"));
    assertTrue(dict.hasPrefix("DINGDON"));
    assertFalse(dict.hasPrefix("DINGDONK"));
  }

  @Test(val=3) void test6 () throws Exception {
    List<String> l = new java.util.ArrayList<>(1000);
    for (int i=0; i<500; i++) {
      l.add("FOO");
      l.add("BAR");
    }
    dict = new BoggleDictionary(l);
    assertEquals(dict.size(), 2);
    assertTrue(dict.hasWord("FOO"));
    assertTrue(dict.hasWord("BAR"));
    StringBuilder b = new StringBuilder();
    for (String w : dict)
      b.append(w);
    assertEquals(b.toString(), "BARFOO");
  }
}