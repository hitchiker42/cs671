// $Id: SampleTests4.java 168 2013-03-18 20:01:35Z cs671a $

package tests;

import charpov.grader.*;
import static org.testng.Assert.*;

import cs671.BoggleDictionary;
import cs671.DiceBoggleBoard;
import cs671.FixedBoggleBoard;
import cs671.Die;
import static cs671.Debug.*;
import java.util.Set;
import java.util.List;
import java.util.Scanner;
import java.net.URL;

class SampleTests4 {

  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    new Tester(SampleTests4.class).run();
  }

  static final List<String> WORDS;
  static final BoggleDictionary DICT, DICT5;
  static {
    List<String> s = new java.util.ArrayList<>(117969);
    List<String> s5 = new java.util.ArrayList<>(113050);
    WORDS = s;
    try {
      URL url = new URL("http://cs.unh.edu/~cs671/words.txt");
      Scanner in = new Scanner(url.openStream());
      while (in.hasNext()) {
        String w = in.next().toUpperCase();
        s.add(w);
        if (w.length() >= 5)
          s5.add(w);
      }
      in.close();
    } catch (java.io.IOException e) {
      fail("cannot load words");
    }
    DICT = new BoggleDictionary(s);
    DICT5 = new BoggleDictionary(s5);
  }


  @Test void testDictionary () throws Exception {
    assertEquals(DICT.size(), 117969);
    List<String> s = new java.util.ArrayList<>(117969);
    for (String w : DICT)
      s.add(w);
    assertEquals(s, WORDS);
    assertTrue(DICT.hasWord("DINGDONG"));
    assertFalse(DICT.hasWord("dingdong"));
    assertFalse(DICT.hasWord("DINGDON"));
    assertTrue(DICT.hasPrefix("DINGDON"));
    assertFalse(DICT.hasPrefix("DINGDONK"));
  }

  @Test void testDiceBoard () throws Exception {
    Die AB = new Die(new char[] {'A', 'B'});
    Die CD = new Die(new char[] {'C', 'D'});
    DiceBoggleBoard board = new DiceBoggleBoard(10, 20, new Die[] {AB, CD});
    while (board.letterAt(11, 7) != 'A')
      board.rattle();
    while (board.letterAt(11, 7) != 'B')
      board.rattle();
    while (board.letterAt(11, 7) != 'C')
      board.rattle();
    while (board.letterAt(11, 7) != 'D')
      board.rattle();
  }

  @Test void testFixedBoard () throws Exception {
    String chars = "EAAEEYROIUEYTQVGHIOXRTSTRAOFEIEITEOTFALHSQTTSECDMUISEIANARYTECLORVETBUJEILTHGPLNDYEESKGWEOAEVRSELEEE";
    FixedBoggleBoard board = new FixedBoggleBoard(10, 10, chars.toCharArray());
    List<String> words = java.util.Arrays.asList
      ("ILLITERATES", "CATENARIES", "ILLITERATE", "REITERATES", "SEROSITIES",
       "THROATIEST", "VELLEITIES", "CANAILLES", "CATENATES", "ENROLLEES");
    here("making string set");
    Set<String> s = board.allWords(DICT5);
    assertEquals(s.size(), 1100);
    here("made string set");
    List<String> l = new java.util.ArrayList<>(10);
    int i=0;
    for (String w : s)
      if (i++ < 10)
        l.add(w);
      else
        break;
    assertEquals(l, words);
  }
}