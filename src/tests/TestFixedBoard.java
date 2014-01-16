// $Id: TestFixedBoard.java 189 2013-04-22 14:23:47Z cs671a $

package tests;

import charpov.grader.*;
import static org.testng.Assert.*;

import cs671.FixedBoggleBoard;
import cs671.BoggleDictionary;

import java.util.List;
import java.util.Set;
import java.util.Scanner;
import java.net.URL;
import java.io.Reader;

@Test(val=20)
class TestFixedBoard {

  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    new Tester(TestFixedBoard.class).run();
  }

  static final List<String> WORDS;
  static final BoggleDictionary DICT;
  static {
    List<String> s = new java.util.ArrayList<>(113050);
    WORDS = s;
    try {
      URL url = new URL("http://cs.unh.edu/~cs671/words.txt");
      Scanner in = new Scanner(url.openStream());
      while (in.hasNext()) {
        String w = in.next();
        if (w.length() >= 5)
          s.add(w.toUpperCase());
      }
      in.close();
    } catch (java.io.IOException e) {
      fail("cannot load words");
    }
    DICT = new BoggleDictionary(s);
  }

  @Test(val=1) void test1 () throws Exception {
    String chars = "ABCDEFGHIJKL";
    FixedBoggleBoard board = new FixedBoggleBoard(3, 4, chars.toCharArray());
    assertEquals(board.getWidth(), 3);
    assertEquals(board.getHeight(), 4);
  }

  @Test(val=5) void test2 () throws Exception {
    String chars = "LNREUINOTIFAWTTU";
    FixedBoggleBoard board = new FixedBoggleBoard(4, 4, chars.toCharArray());
    List<String> words = java.util.Arrays.asList
      ("ANNULI", "NITWIT", "TINNER", "TWINER", "AFIRE", "AFORE", "ANNUL",
       "ENFIN", "ENNUI", "FINER", "INNER", "IRONE", "LINER", "RENIN", "TITAN",
       "TWINE", "UNLIT", "WITAN");
    assertEquals(new java.util.ArrayList<>(board.allWords(DICT)), words);
  }

  @Test(val=5) void test3 () throws Exception {
    String chars = "LNREUINOTIFAWTTU";
    FixedBoggleBoard board = new FixedBoggleBoard(4, 4, chars.toCharArray());
    assertTrue(board.containsString("ERNOAFIILUTTTU"));
    assertFalse(board.containsString("ERNOAFIILUTTTA"));
  }

  @Test(val=3) void test4 () throws Exception {
    String chars = "EAAEEYROIUEYTQVGHIOXRTSTRAOFEIEITEOTFALHSQTTSECDMUISEIANARYTECLORVETBUJEILTHGPLNDYEESKGWEOAEVRSELEEE";
    FixedBoggleBoard board = new FixedBoggleBoard(10, 10, chars.toCharArray());
    List<String> words = java.util.Arrays.asList
      ("ILLITERATES", "CATENARIES", "ILLITERATE", "REITERATES", "SEROSITIES",
       "THROATIEST", "VELLEITIES", "CANAILLES", "CATENATES", "ENROLLEES");
    Set<String> s = board.allWords(DICT);
    assertEquals(s.size(), 1100);
    List<String> l = new java.util.ArrayList<>(10);
    int i=0;
    for (String w : s)
      if (i++ < 10)
        l.add(w);
      else
        break;
    assertEquals(l, words);
  }

  FixedBoggleBoard bigBoard;

  FixedBoggleBoard getBigBoard () throws java.io.IOException {
    if (bigBoard != null)
      return bigBoard;
    URL url = new URL("http://cs.unh.edu/~cs671/bigBoard.txt");
    Reader in = new java.io.BufferedReader
      (new java.io.InputStreamReader(url.openStream()));
    int c, n=0;
    char[] chars = new char[10000];
    while ((c = in.read()) != -1)
      if (c >= 'A' && c <= 'Z')
        chars[n++] = (char)c;
    return bigBoard = new FixedBoggleBoard(100, 100, chars);
  }

  @Test(val=3, timeout=10000) void test5 () throws Exception {
    URL url = new URL("http://cs.unh.edu/~cs671/bigBoard.txt");
    Reader in = new java.io.BufferedReader
      (new java.io.InputStreamReader(url.openStream()));
    int c, n=0;
    char[] chars = new char[10000];
    while ((c = in.read()) != -1)
      if (c >= 'A' && c <= 'Z')
        chars[n++] = (char)c;
    FixedBoggleBoard board = getBigBoard();
    List<String> words = java.util.Arrays.asList
      ("UNCLEANNESSES", "AERONAUTICAL", "ATTENUATIONS", "CONDENSATION",
       "FUNCTIONLESS", "INTERMITTENT", "INTERRELATES", "MISCONSTRUES",
       "MOLESTATIONS", "NARROWNESSES");
    Set<String> s = board.allWords(DICT);
    assertEquals(s.size(), 30873);
    List<String> l = new java.util.ArrayList<>(10);
    int i=0;
    for (String w : s)
      if (i++ < 10)
        l.add(w);
      else
        break;
    assertEquals(l, words);
  }

  @Test(val=3, timeout=10000) void test6 () throws Exception {
    assertTrue(bigBoard.containsString("RETUNWTTARELGIFUVLTRRFYJDZESARRY"));
    assertFalse(bigBoard.containsString("RETUNWTTARELGIFUVLTRRFYJDZESARRQ"));
  }

}
