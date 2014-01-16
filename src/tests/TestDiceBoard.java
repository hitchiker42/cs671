// $Id: TestDiceBoard.java 189 2013-04-22 14:23:47Z cs671a $

package tests;

import charpov.grader.*;
import static org.testng.Assert.*;

import cs671.DiceBoggleBoard;
import cs671.Die;
import cs671.BoggleDictionary;

import java.util.Set;

@Test(val=20)
class TestDiceBoard {

  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    new Tester(TestDiceBoard.class).run();
  }

  static final Die AB = new Die(new char[] {'A', 'B'});
  static final Die CD = new Die(new char[] {'C', 'D'});

  DiceBoggleBoard board;

  void BEFORE () {
    board = new DiceBoggleBoard(10, 20, new Die[] {AB, CD});
  }

  @Test(val=2) void test1 () throws Exception {
    assertEquals(board.getWidth(), 10);
    assertEquals(board.getHeight(), 20);
  }

  @Test(val=2) void test2 () throws Exception {
    char c = board.letterAt(16, 8);
    assertTrue(c == 'A' || c == 'B' || c == 'C' || c == 'D');
  }

  @Test(val=5) void test3 () throws Exception {
    while (board.letterAt(11, 7) != 'A')
      board.rattle();
    while (board.letterAt(11, 7) != 'B')
      board.rattle();
    while (board.letterAt(11, 7) != 'C')
      board.rattle();
    while (board.letterAt(11, 7) != 'D')
      board.rattle();
  }

  @Test(val=5) void test4 () throws Exception {
    int[] counts = new int[4];
    for (int i=0; i<10000; i++) {
      board.rattle();
      for (int r=0, h=board.getHeight(); r<h; r++)
        for (int c=0, w=board.getWidth(); c<w; c++)
          counts[board.letterAt(r, c) - 'A']++;
    }
    for (int x : counts)
      assertEquals(x, 500000, 5000);
  }

  @Test(val=6, timeout=60000) void test5 () throws Exception {
    Die A = new Die(new char[] {'A'});
    board = new DiceBoggleBoard(5, 5, new Die[] {A});
    StringBuilder b = new StringBuilder();
    for (int r=0; r<5; r++) {
      for (int c=0; c<5; c++) {
        b.append('A');
        assertEquals(board.letterAt(r,c), 'A');
        assertTrue(board.containsString(b.toString()));
      }
    }
    assertFalse(board.containsString("B"));
    assertFalse(board.containsString("AAAAAAAAAAB"));
    Set<String> s = new java.util.HashSet<>();
    s.add("A");
    s.add("AA");
    s.add("AB");
    s = board.allWords(new BoggleDictionary(s));
    assertEquals(s.size(), 2);
    b = new StringBuilder();
    for (String w : s)
      b.append(w).append("/");
    assertEquals(b.toString(), "AA/A/");
  }
}
