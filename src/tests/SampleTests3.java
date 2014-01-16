// $Id: SampleTests3.java 142 2013-02-21 16:03:43Z cs671a $

package tests;

import cs671.Clock;
import cs671.ClockTimer;
import static cs671.Debug.*;
import charpov.grader.*;
import static org.testng.Assert.*;

class SampleTests3 {

  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    new Tester(SampleTests3.class).run();
  }

  private Clock clock64, clock4;
  private boolean[] init64;

  void BEFORE () {
    clock64 = new Clock(64);
    clock4 = new Clock(4, new cs671.SimpleClockTimer());
    init64 = new boolean[64];
    for (int i : new int[] {1,2,5,11,12,13,42,56,61})
      init64[i] = true;
  }

  void AFTER () {
    clock64.destroy();
    clock4.destroy();
  }

  @Test(timeout=5000) void testTimer1 () throws Exception {
    class R implements Runnable {
      volatile long start;
      public void run () {
        start = System.currentTimeMillis();
      }
    }
    R task = new R();
    ClockTimer timer = new cs671.SimpleClockTimer();
    timer.setRunnable(task);
    timer.setDelay(200);
    long now = System.currentTimeMillis();
    timer.start();
    Thread.sleep(100);
    for (int i=1; i<=10; i++) {
      Thread.sleep(200);
      assertEquals(task.start-now, 200*i, 20);
    }
    timer.cancel();
  }

  @Test void testTimer2 () throws Exception {
    class R implements Runnable {
      Thread t1, t2;
      public void run () {
        if (t1 == null)
          t1 = Thread.currentThread();
        else
          t2 = Thread.currentThread();
      }
    }
    R task = new R();
    ClockTimer timer = new cs671.SimpleClockTimer();
    timer.setRunnable(task);
    timer.setDelay(100);
    timer.start();
    Thread.sleep(500);
    timer.cancel();
    assertSame(task.t1, task.t2);
  }

  @Test void testTimer3 () throws Exception {
    class R implements Runnable {
      Thread t1, t2;
      public void run () {
        if (t1 == null)
          t1 = Thread.currentThread();
        else
          t2 = Thread.currentThread();
      }
    }
    R task = new R();
    ClockTimer timer = new cs671.SimpleClockTimer();
    timer.setRunnable(task);
    timer.setDelay(100);
    timer.start();
    Thread.sleep(350);
    timer.stop();
    timer.start();
    Thread.sleep(350);
    timer.cancel();
    assertSame(task.t1, task.t2);
  }

  @Test(timeout=6000) void testTimerBusy () throws Exception {
    class R implements Runnable {
      volatile Thread thread;
      public void run () {
        thread = Thread.currentThread();
      }
    }
    R task = new R();
    ClockTimer timer = new cs671.SimpleClockTimer();
    timer.setRunnable(task);
    timer.setDelay(1);
    timer.start();
    Thread.sleep(500);
    timer.stop();
    Thread thread = task.thread;
    timer.setDelay(10000);
    timer.start();
    Thread.sleep(500);
    long end = System.currentTimeMillis() + 3000;
    while (System.currentTimeMillis() < end) // that's busy!
      assertNotSame(thread.getState(), Thread.State.RUNNABLE);
    timer.cancel();
  }
  @Test void testGetLongValue () {
    clock64.setValue(init64);
    assertEquals(clock64.getLongValue(), 2377905001298147366L);
  }

  @Test void testSetLongValue () {
    clock64.setLongValue(2377905001298147366L);
    assertTrue(java.util.Arrays.equals(init64, clock64.getValue()),
               "boolean[] incorrect");
  }

  @Test void testStart () throws Exception {
    try {
      clock64.start();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(timeout=5000) void testStep2 () {
    while (!clock64.getBit(24))
      clock64.step();
  }

  @Test void testExSetLongValue () {
    try {
      clock4.setLongValue(42L);
      fail("expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // ok
    }
  }

  @Test void testSetLongValue2 () {
    Clock c = null;
    try {
      c = new Clock(100);
      for (int i=0; i<100; i++)
        c.setBit(i);
      c.setLongValue(1L);
      assertEquals(c.getBit(0), true);
      for (int i=1; i<100; i++)
        assertEquals(c.getBit(i), false);
    } finally {
      if (c != null)
        c.destroy();
    }
  }

  @Test void testGetValue2 () {
    clock64.setValue(init64);
    assertEquals(clock64.getBit(42), true);
    init64[42] = false;
    assertEquals(clock64.getBit(42), true);
    clock64.getValue()[42] = false;
    assertEquals(clock64.getBit(42), true);
  }

  @Test void testIsTicking () {
    assertFalse(clock4.isTicking(), "clock is ticking initially");
    clock4.start();
    assertTrue(clock4.isTicking(), "clock is not ticking after start");
    clock4.stop();
    assertFalse(clock4.isTicking(), "clock is ticking after stop");
  }

  @Test(timeout=6000) void testStartString () throws Exception {
    clock4.start();
    while (!clock4.toString().equals("0100 [ON]"))
      Thread.sleep(100);
  }

  @Test(timeout=7000) void testCatchUp () throws Exception {
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
}