// $Id: TestUtilTimers.java 158 2013-02-27 15:14:52Z cs671a $

package tests;

@charpov.grader.Test(val=20)
public class TestUtilTimers extends TestTimers {

  void BEFORE () {
    timer = new cs671.UtilClockTimer();
  }
}
