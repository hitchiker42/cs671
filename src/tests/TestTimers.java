// $Id: TestTimers.java 158 2013-02-27 15:14:52Z cs671a $

package tests;

import cs671.ClockTimer;

import charpov.grader.*;
import static org.testng.Assert.*;

class Task implements Runnable {

  public final StringBuffer out = new StringBuffer();
  public volatile int count;

  public void run () {
    delay();
    out.append(++count);
    out.append(doTask(count));
  }

  public Object doTask (int n) {
    return "";
  }

  public void delay () {
  }
}

public abstract class TestTimers {

  ClockTimer timer;
  Task task;

  void AFTER () {
    if (timer != null)
      timer.cancel();
  }

  static void sleep (long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      fail("thread interrupted");
    }
  }

  @Test(val=10, timeout=5000) void test1 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    sleep(1000);
    timer.start();
    sleep(550);
    timer.stop();
    sleep(1000);
    assertEquals(task.out.toString(), "12345");
  }

  @Test(val=10, timeout=5000) void test3 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    sleep(1000);
    timer.start();
    sleep(250);
    timer.stop();
    sleep(1000);
    assertEquals(task.out.toString(), "12");
    timer.start();
    sleep(250);
    timer.stop();
    assertEquals(task.out.toString(), "1234");
  }

  @Test(val=10, timeout=5000) void test2 () {
    Task task = new Task() {
        public void delay () {
          if (count == 1)
            sleep(300);
        }
      };
    timer.setRunnable(task);
    timer.setDelay(100);
    sleep(1000);
    timer.start();
    sleep(550);
    timer.stop();
    sleep(1000);
    assertEquals(task.out.toString(), "12345");
  }

  @Test(val=10, timeout=5000) void test4 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(1000);
    timer.start();
    sleep(1200);
    timer.stop();
    timer.setDelay(100);
    timer.start();
    sleep(550);
    assertEquals(task.out.toString(), "123456");
  }

  @Test(val=10, timeout=5000) void test20 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    timer.start();
    sleep(250);
    timer.stop();
    timer.setDelay(1000);
    timer.start();
    sleep(250);
    assertEquals(task.out.toString(), "12");
  }

  class ThreadTask extends Task {
    volatile Thread thread;
    @Override public Boolean doTask (int n) {
      Thread t = Thread.currentThread();
      return Boolean.valueOf(thread == null | thread == (thread = t));
    }
  }

  @Test(val=10, timeout=5000) void test7 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(1000);
    timer.start();
    sleep(300);
    timer.stop();
    assertEquals(task.out.toString(), "");
  }

  @Test(val=10, timeout=5000) void test8 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(1000);
    assertFalse(timer.isRunning());
    timer.start();
    assertTrue(timer.isRunning());
    sleep(100);
    assertTrue(timer.isRunning());
    timer.stop();
    assertFalse(timer.isRunning());
  }

  @Test(val=1) void test9 () {
    try {
      timer.start();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test10 () {
    timer.setRunnable(task = new Task());
    try {
      timer.start();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test11 () {
    timer.setDelay(100);
    try {
      timer.start();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test12 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    timer.start();
    try {
      timer.start();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test23 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    timer.start();
    try {
      timer.setRunnable(task);
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test24 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    timer.start();
    try {
      timer.setDelay(100);
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test13 () {
    try {
      timer.setDelay(0);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // ok
    }
  }

  @Test(val=1) void test14 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    timer.start();
    try {
      timer.setDelay(100);
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test15 () {
    timer.cancel();
    try {
      timer.setDelay(100);
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test16 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    timer.cancel();
    try {
      timer.start();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=1) void test17 () {
    timer.isRunning();
    timer.stop();
    timer.cancel();
    timer.cancel();
    timer.stop();
    timer.isRunning();
  }

  @Test(val=5, timeout=5000) void test18 () {
    class R implements Runnable {
      volatile int count;
      public void run () {
        count++;
      }
    }
    R task = new R();
    timer.setRunnable(task);
    timer.setDelay(10);
    sleep(1000);
    timer.start();
    sleep(3005);
    timer.stop();
    assertEquals(task.count, 300);
  }

  @Test(val=5) void test19 () {
    timer.setRunnable(task = new Task());
    timer.setDelay(100);
    timer.start();
    sleep(150);
    timer.cancel();
    sleep(500);
    assertEquals(task.out.toString(), "1");
  }

  @Test(val=5) void test21 () throws InterruptedException {
    ThreadTask task = new ThreadTask();
    timer.setRunnable(task);
    timer.setDelay(100);
    timer.start();
    sleep(150);
    timer.cancel();
    task.thread.join();
  }

  @Test(val=5) void test22 () {
    ThreadTask task = new ThreadTask();
    timer.setRunnable(task);
    timer.setDelay(100);
    timer.start();
    sleep(150);
    timer.stop();
    sleep(300);
    assertTrue(task.thread.isAlive());
  }

  @Test(val=10, timeout=5000) void testTimerBusy () throws Exception {
    ThreadTask task = new ThreadTask();
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

  @Test(val=10) void testTimer3 () throws Exception {
    ThreadTask task = new ThreadTask();
    timer.setRunnable(task);
    timer.setDelay(100);
    timer.start();
    Thread.sleep(350);
    timer.stop();
    Thread t1 = task.thread;
    timer.start();
    Thread.sleep(350);
    timer.cancel();
    Thread t2 = task.thread;
    assertSame(t1, t2);
  }

  @Test(val=10) void testTimer2 () throws Exception {
    ThreadTask task = new ThreadTask();
    timer.setRunnable(task);
    timer.setDelay(100);
    timer.start();
    Thread.sleep(350);
    Thread t1 = task.thread;
    Thread.sleep(350);
    timer.cancel();
    Thread t2 = task.thread;
    assertSame(t1, t2);
  }

  @Test(val=5, timeout=5000) void testTimer1 () throws Exception {
    class R implements Runnable {
      volatile long start;
      public void run () {
        start = System.currentTimeMillis();
      }
    }
    R task = new R();
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

}
