// $Id: TestPassiveClocks.java 158 2013-02-27 15:14:52Z cs671a $

package tests;

import cs671.Clock;

import charpov.grader.*;
import static org.testng.Assert.*;

@Test(val=30)
class TestPassiveClocks {

  private Clock clock64, clock4;
  private boolean[] init64, init4;


  void BEFORE () {
    clock64 = new Clock(64);
    clock4 = new Clock(4);
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

  @Test(val=2) void testToString () {
    clock4.setValue(init4);
    assertEquals(clock4.toString(), "1011 [OFF]");
  }

  @Test(val=2) void testGetBit () {
    clock64.setValue(init64);
    for (int i=0; i<init64.length; i++)
      assertEquals(clock64.getBit(i), init64[i]);
  }

  @Test(val=1) void testClear () {
    clock64.clear();
    for (int i=0; i<init64.length; i++)
      assertEquals(clock64.getBit(i), false);
  }

  @Test(val=2) void testClearSetBit () {
    for (int i=0; i<init64.length; i++)
      if (init64[i])
        clock64.setBit(i);
    for (int i=0; i<init64.length; i++)
      assertEquals(clock64.getBit(i), init64[i]);
    clock64.clearBit(42);
    assertEquals(clock64.getBit(42), false);
  }

  @Test(val=2) void testGetValue () {
    boolean[] v1 = init64.clone();
    clock64.setValue(v1);
    boolean[] v2 = clock64.getValue();
    java.util.Arrays.fill(v1, false);
    clock64.clear();
    assertTrue(java.util.Arrays.equals(init64, v2),
               "arrays used by setValue and getValue() are not tied to clock");
  }

  @Test(val=2) void testGetLongValue () {
    clock64.setValue(init64);
    assertEquals(clock64.getLongValue(), 2377905001298147366L);
  }

  @Test(val=2) void testSetLongValue () {
    clock64.setLongValue(2377905001298147366L);
    assertTrue(java.util.Arrays.equals(init64, clock64.getValue()),
               "boolean[] incorrect");
  }

  @Test(val=1) void testIsTicking () {
    assertFalse(clock4.isTicking(), "clock is ticking initially");
  }

  @Test(val=2) void testNextBit () {
    assertEquals(clock4.getBit(0), false);
    assertEquals(clock4.nextBit(0), false);
    assertEquals(clock4.getBit(0), true);
    assertEquals(clock4.nextBit(0), true);
    assertEquals(clock4.getBit(0), false);
    assertEquals(clock4.getBit(1), false);
  }

  @Test(val=1) void testsize () {
    assertEquals(clock64.size(), 64);
  }

  @Test(val=2) void testStop () throws Exception {
    try {
      clock4.stop();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }

  @Test(val=2) void testDestroy () throws Exception {
    clock4.destroy();
  }

  @Test(val=1) void testStep () {
    for (int i=0; i<16; i++) {
      assertEquals(clock4.getLongValue(), i);
      clock4.step();
    }
    assertEquals(clock4.getLongValue(), 0);
  }

  @Test(val=1) void testStep3 () {
    while (!clock4.toString().equals("1111 [OFF]"))
      clock4.step();
  }

  @Test(val=1) void testStep4 () {
    int n = 0;
    do {
      clock4.step();
      n++;
    } while (clock4.getLongValue() > 0);
    assertEquals(n, 16);
  }

  @Test(val=2, timeout=5000) void testObserver () {
    class O implements java.util.Observer {
      boolean bit, started;
      int updates;
      public void update (java.util.Observable c, Object o) {
        updates++;
        assertSame(c, clock4);
        if (started) {
          bit = !bit;
          assertEquals(clock4.getBit(0), bit,
                       "too many updates:"+
                       " value of bit 0 has not changed since last step()");
        } else {
          started = true;
          bit = clock4.getBit(0);
        }
      }
    }
    O o = new O();
    clock4.addObserver(o);
    for (int i=0; i<100; i++)
      clock4.step();
    assertTrue(o.updates > 99, "observers not notified of all changes");
  }

  @Test(val=1) void testExSetValue () {
    try {
      clock4.setValue(init64);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // ok
    }
  }

  @Test(val=2) void testGetValue2 () {
    clock64.setValue(init64);
    assertEquals(clock64.getBit(42), true);
    init64[42] = false;
    assertEquals(clock64.getBit(42), true);
    clock64.getValue()[42] = false;
    assertEquals(clock64.getBit(42), true);
  }

  @Test(val=2) void testSetLongValue2 () {
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

  @Test(val=1) void testExSetLongValue () {
    try {
      clock4.setLongValue(42L);
      fail("expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // ok
    }
  }

  @Test(val=1, timeout=10000) void testStep2 () {
    while (!clock64.getBit(24))
      clock64.step();
  }

  @Test(val=2) void testStart () throws Exception {
    try {
      clock64.start();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // ok
    }
  }


}
