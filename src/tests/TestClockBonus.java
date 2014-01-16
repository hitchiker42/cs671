// $Id: TestClockBonus.java 158 2013-02-27 15:14:52Z cs671a $

package tests;

import cs671.Clock;

import charpov.grader.*;
import static org.testng.Assert.*;

@Test(val=10)
class TestClockBonus {

  private Clock clock;

  void BEFORE () {
    new NewTimer().cancel(); // to force recompilation of class NewTimer
  }

  void AFTER () {
    if (clock != null)
      clock.destroy();
  }

  @Test(val=6, timeout=5000) void testBonus1 () throws Exception {
    clock = new Clock(3, "tests.NewTimer");
    clock.start();
    Thread.sleep(2500);
    assertTrue(clock.getBit(1), "bit 1 is true after 2.5 secs");
    clock.stop();
  }

  @Test(val=2, timeout=5000) void testBonus2 () throws Exception {
    clock = new Clock(3, "tests.NewTimer$Timer");
    clock.start();
    Thread.sleep(2500);
    assertTrue(clock.getBit(1), "bit 1 is true after 2.5 secs");
    clock.stop();
  }

  @Test(val=1) void testBonus3 () throws Exception {
    try {
      clock = new Clock(3, "foobar");
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // ok
    }
  }

  @Test(val=1) void testBonus4 () throws Exception {
    try {
      clock = new Clock(3, "tests.NewTimer$Timer2");
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // ok
    }
  }
}
