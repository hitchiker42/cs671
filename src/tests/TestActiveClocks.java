// $Id: TestActiveClocks.java 158 2013-02-27 15:14:52Z cs671a $

package tests;

import cs671.Clock;

import charpov.grader.*;
import static org.testng.Assert.*;

@Test(val=10)
class TestActiveClocks {

  private Clock clock64, clock4;
  private boolean[] init64, init4;

  void BEFORE () {
    clock64 = new Clock(64, new cs671.UtilClockTimer());
    clock4 = new Clock(4, new cs671.UtilClockTimer());
    init64 = new boolean[64];
    for (int i : new int[] {1,2,5,11,12,13,42,56,61})
      init64[i] = true;
    init4 = new boolean[4];
    for (int i : new int[] {0,1,3})
      init4[i] = true;
  }

  void AFTER () {
    clock64.destroy();
    clock4.destroy();
  }

  @Test(val=1) void testToString () {
    clock4.start();
    assertTrue(clock4.toString().endsWith("[ON]"),
               "toString() of running clock doesn't end with '[ON]'");
  }

  @Test(val=2, timeout=10000) void testStartStop () throws Exception {
    clock4.start();
    Thread.sleep(3000);
    assertTrue(clock4.getLongValue() > 0,
               "clock value is still zero 3 seconds after start");
    assertEquals(clock4.getBit(3), false,
                 "fourth bit already 1 after 3 seconds");
    clock4.stop();
    boolean[] v1 = clock4.getValue().clone();
    Thread.sleep(3000);
    assertTrue(java.util.Arrays.equals(v1, clock4.getValue()),
               "clock value is still changing after stop");
  }

  @Test(val=2, timeout=5000) void testStart () throws Exception {
    clock4.start();
    while (!clock4.getBit(2))
      Thread.sleep(100);
  }

  @Test(val=5, timeout=7000) void testCatchUp () throws Exception {
    class SpecialClock extends Clock {
      public int wasteTime (long millis) throws InterruptedException {
        synchronized (lock) {
          long v = getLongValue();
          Thread.sleep(millis);
          return (int)(getLongValue() - v);
        }
      }
      public SpecialClock (int n) {
        super(n, new cs671.SimpleClockTimer());
      }
    }
    SpecialClock c = new SpecialClock(5);
    c.start(); // fast
    int n = c.wasteTime(5500); // takes 5.5 secs
    assertEquals(n, 0); // no ticks 5 secs
    Thread.sleep(300);
    assertEquals(c.getLongValue(), 5); // clock has ticked 5 times in 0.3 sec
    c.destroy();
  }

  @Test(val=2, timeout=5000) void testStartString () throws Exception {
    clock4.start();
    while (!clock4.toString().equals("0100 [ON]"))
      Thread.sleep(100);
  }

  @Test(val=2) void testIsTicking () {
    assertFalse(clock4.isTicking(), "clock is ticking initially");
    clock4.start();
    assertTrue(clock4.isTicking(), "clock is not ticking after start");
    clock4.stop();
    assertFalse(clock4.isTicking(), "clock is ticking after stop");
  }

}