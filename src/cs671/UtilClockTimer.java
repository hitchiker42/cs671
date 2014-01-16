package cs671;
import java.util.*;
import java.util.concurrent.*;
import static cs671.Debug.*;
import static java.lang.Thread.sleep;
/** Simple wrapping on general-purpose timers.  This implementation
 * relies on {@code java.util.Timer} and adapts it to the
 * specification of {@code ClockTimer}.  Timers can be stopped and
 * restarted, <em>without switching to a new thread</em>.  The thread
 * is terminated when the timer is cancelled.
 *
 * <p>Instances of the class <em>are not thread-safe</em> (i.e., a
 * timer instance should not be shared among multiple threads).
 *
 * @author  Michel Charpentier
 * @version 3.1, 2/12/13
 * @see #cancel
 * @see java.util.Timer
 */
public class UtilClockTimer extends Timer implements ClockTimer {
  /**
   *Implimentation of TimerTask for ClockTimer
   *{@inheritDoc}
   */
  class TimerThread extends Thread{
    volatile State_ state;
    private final Object lock;
    volatile TimerTask task;
    TimerThread(TimerTask task, Object lock,State_ state){
      this.state=state;
      this.lock=lock;
      this.task=task;
    }
    public void run(){
      mainloop();
    }
    void mainloop() {
      synchronized (this){
      TOP:for(;;){
          try {
            if(!state.alive){break TOP;}
            if (state.stopped){
              //here("waiting ");
              this.wait();
            } else {
              //here("delaying "+String.valueOf(state.delay));
              // while(!(interrupted())){
                this.wait(state.delay);
                task.run();
                state.count++;
                //}
              //state.ran=true;
            }
          }
          catch(InterruptedException ex){}
        }
      }
    }
  }
  TimerTask task;
  long time;
  TimerThread thread;
  Runnable runner;
  private final Object lock=new Object();
  State_ state=new State_(true,true,0,false,false);
  /** Creates a new timer.  The timer is initially stopped.
   * @param r the timer task
   * @param d the timer delay, in milliseconds
   */
  public UtilClockTimer (Runnable r, long d) {
    synchronized (lock){
      this.state.delay=d;
    this.runner=r;
    this.task=new TimerTask(){
        public void run(){
          runner.run();
        }};
    this.thread=new TimerThread (this.task,this.lock,this.state);
    thread.start();
    super.cancel();
    }
  }

  /** Creates a new timer.  The timer has no task and no delay. */
  public UtilClockTimer () {
    synchronized(lock){
    this.runner=null;
    this.task=new TimerTask(){
        public void run(){
          runner.run();
        }};
    this.thread=new TimerThread(this.task,this.lock,this.state);
    thread.start();
    super.cancel();
    }
  }
  /**
   *{@inheritDoc}
   */
  public boolean isRunning () {
    synchronized (this){
      return state.running;
    }
  }
  /**
   *{@inheritDoc}
   */
  public Runnable setRunnable (Runnable r) {
    synchronized (this) {
    if(!state.alive || state.running){
      throw new IllegalStateException();
    }
    try {
      return runner;
    } finally {
      runner=r;
      //state.ran=false;
    }
    }
  }
  /**
   *{@inheritDoc}
   */
  public void setDelay (long d) {
    synchronized (this) {
    if(!state.alive || state.running){
      throw new IllegalStateException();
    }
    if(d<=0){
      throw new IllegalArgumentException();
    }
    state.delay=d;
    //here("Delay "+String.valueOf(state.delay));
    }
  }
  /**
   *{@inheritDoc}
   */
  public void start () {
    synchronized(this){
      if(runner==null || !state.alive || 0>=state.delay || state.running){
        throw new IllegalStateException();
      }
      //here("starting");
      state.stopped=false;
      thread.interrupt();
      //state.ran=false;
      state.running=true;
      time=Clock.time();
      /*task=new TimerTask(){
          public void run(){
            runner.run();
          }
          };*/
      //here("Staring");
    }
  }
  /**
   *{@inheritDoc}
   */
  @SuppressWarnings("Unchecked")
  public void stop () {
    synchronized (this){
      if(!state.alive || state.stopped || runner==null){return;}
      else {
        //here("stopping");
        state.stopped=true;
        thread.interrupt();
        state.running=false;
        long t2=Clock.time();
        long skipped=(t2-(time+(state.delay*state.count)))/state.delay;
        if(skipped<20){
        while (skipped>0){
          runner.run();
          skipped--;
        }
        }
        state.count=0;
      }
    }
  }
  /**
   *{@inheritDoc}
   */
  public void cancel () {
    synchronized (this){
      if(!state.alive){return;}
      else{
        //here("Stopping");
        state.alive=false;
        thread.interrupt();
        state.stopped=false;
        state.running=false;
        task=null;
        super.cancel();
      }
    }
  }
}
class State_ {
  boolean alive;
  boolean stopped;
  long delay;
  boolean ran;
  boolean running;
  int count;
  State_(boolean a,boolean b,long c,boolean d,boolean e){
    this.alive=a;
    this.stopped=b;
    this.delay=c;
    this.ran=d;
    this.running=e;
    this.count=0;
  }
}