// $Id: TestSimpleTimers.java 158 2013-02-27 15:14:52Z cs671a $

package tests;

@charpov.grader.Test(val=30)
public class TestSimpleTimers extends TestTimers {

  void BEFORE () {
    timer = new cs671.SimpleClockTimer();
  }
}
