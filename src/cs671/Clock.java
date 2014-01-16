package cs671;
import static cs671.Debug.*;
import java.util.*;
import java.lang.reflect.*;
import static java.util.Collection.*;
import static java.util.Arrays.*;
import java.util.concurrent.locks.*;
import static java.lang.Thread.sleep;

/** Binary clocks.  These clocks can either be passive objects or
 * include their own timer.  In the latter case, their value is
 * updated every second.  Clock updates (automatic or manual) may
 * increase or decrease the clock value depending on its current
 * "direction".  The direction of a clock can be changed at any time.
 * It is {@link Direction#FORWARD} by default.
 *
 * <p> The index of the least significant bit is 0.  In particular,
 * this means that the value of <code>getBit(0)</code> (or,
 * equivalently, the value of <code>getValues()[0]</code> changes with
 * each call to <code>step()</code> and, if the clock is running, with
 * each tick.
 *
 *<p> Clocks are observable and every state change is forwarded to the
 * clock's observers.  Note that some method calls do not trigger an
 * update, for instance if a clock is set to a value equal to its
 * current value or if the clock's direction is set to a value equal
 * to its current direction.
 *
 *<p> Since a timer thread (on active clocks) needs to access the
 * state of the clock, all state-changing and state-querying methods
 * are thread-safe.
 *
 * @author  Michel Charpentier
 * @version 3.1, 2/11/13
 * @see ClockTimer
 * @see java.util.Observable
 */
@SuppressWarnings("unchecked")
public class Clock extends java.util.Observable {
  static final String DEFAULT_TIMER_CLASS = SimpleClockTimer.class.getName();
  static long time(long scale){
    return (System.currentTimeMillis()/scale);
  }
  static long time(){
    return System.currentTimeMillis();
  }
  static long timediff(long time){
    return System.currentTimeMillis()-time;
  }
  /**
   *Timer event for updating the clock. Updates the clock based on the
   *difference between the current time and the last time the clock was updated.
   *If less than 1 second has passed, nothing it done, if 1 second has passed
   *the clock is incremented and if more than 1 second has passed the clock
   *is incremented to compensate for the time passed(ie incremented 4 times
   *if 4 seconds have passed).
   */
  class ClockRunner implements Runnable{
    long time;
    long diff;
    Clock my_clock;
    ClockRunner(Clock clock){
      this.my_clock=clock;
      this.time=Clock.time();
    }
    public synchronized void run(){
      try{
        diff=Clock.timediff(time)/1000L;
        if (diff>=1){
          synchronized(lock){
            for (int i=0;i<diff;i++){
              my_clock.step();
            }
            time=Clock.time();
          }
        }
      } catch(Exception ex){}
    }
  }
    /**
     *Main loop of the class, checks to see if the clock needs to be
     *modified every 50 milliseconds and incrementing the clock if
     *necessary
     */
    /*    void mainloop(){
      long diff=0L;
    LOOP:while(my_clock.timer.isRunning()){
        while (my_clock.timer.isRunning()){
          try{
            sleep(50);
            diff=Clock.timediff(time)/1000L;
            if (diff>=1){
              synchronized(lock){
                for (int i=0;i<diff;i++){
                  my_clock.step();
                  time+=1000L;
                }
              }
              break;
            }
          } catch(InterruptedException | NullPointerException ex){
            break LOOP;
          }
        }
      }
      }
    */
  /**
   *The actual binary data of the clock
   */
  ClockTimer timer;
  /**
   *The Main data structure of the clock, a bit set
   *from java.util that keeps track of the state of the
   *clock. Internally it uses longs and bitwize operations
   *to set,store and get values making it much more
   *efficent than a boolean array, as it only requires
   *63 excess bits at most whereas each boolean uses
   *an excess 31 bits
   */
  private BitSet clock;
  /**
   *State of the clock, is it active and running or not
   */
  private boolean ticking;
  boolean stopped;
  /**
   *The direction the clock is running
   */
  private Direction direction=Direction.FORWARD;
  //internal parameters only below
  private BitSet temp_clock;
  private final BitSet _true;
  private final BitSet _false;
  private final int size;
  private final int words;
  private boolean temp;
  /**
   *Wrapper around notifyObservers to
   *set the object to send
   */
  private synchronized void update(){
    //here("Update Sent");
    BitSet b=((BitSet)temp_clock.clone());
    b.xor(clock);
    notifyObservers(b);
  }
  /**
   *get clock state before a potential modification
   */
  private synchronized void before(){
    temp_clock=(BitSet)clock.clone();
  }
  /**
   *Test if a Clock operation actually changed the
   *state of the clock and if so call update
   */
  private synchronized void after(){
    if(!(clock.equals(temp_clock))){
      setChanged();
      update();
      clearChanged();
    }
  }
  /** The "lock" that guards all clock state changes.  Every state
   * change, including automatic changes on active clocks, is
   * performed while owning this lock.
   */
  protected final Object lock=new Object();
  /** Constructs a passive clock with <code>nbBits</code> bits.  Initially, all
   * bits are off (false).  The clock has no timer.
   *
   * @param nbBits the number of bits of this clock
   * @throws IllegalArgumentException if <code>nbBits &lt; 1</code>
   */
  public Clock (int nbBits) {
    synchronized(lock){
    if(nbBits<=0){
      throw new IllegalArgumentException();
    }
    this.clock=new BitSet(nbBits);
    this.clock.clear(0,nbBits);
    this.timer=null;
    this.temp_clock=(BitSet)this.clock.clone();
    (this._true=new BitSet(nbBits)).set(0,nbBits);
    (this._false=new BitSet(nbBits)).clear(0,nbBits);
    this.size=nbBits;
    this.words=this.size/64+1;
    }
  }

  /** Constructs an active clock with <code>nbBits</code> bits.  Initially,
   * all bits are off (false) and the clock is associated with a new
   * timer of the specified class, if one can be constructed using a
   * no-argument constructor.  The clock is initially stopped.
   *
   * @param nbBits the number of bits of this clock
   * @param timerClass the name of a timer-implementing class.  The
   * class must implement the {@code ClockTimer} interface and it must
   * have a public, no-argument constructor.  Clock implementations
   * <em>must</em> at least accept {@code "cs671.SimpleClockTimer"} and
   * {@code "cs671.UtilClockTimer"} as valid timer classes.
   * @throws IllegalArgumentException if <code>nbBits &lt; 1</code> or if
   * the specified class cannot be loaded, cannot be instantiated or
   * is not of type {@code ClockTimer}
   * @see ClockTimer
   */
  public Clock (int nbBits, String timerClass) { // bonus question
    synchronized (lock){
      if(nbBits<=0){
        throw new IllegalArgumentException();
      }
      this.clock=new BitSet(nbBits);
      this.temp_clock=(BitSet)this.clock.clone();
      (this._true=new BitSet(nbBits)).set(0,nbBits);
      (this._false=new BitSet(nbBits)).clear(0,nbBits);
      this.size=nbBits;
      this.words=(this.size/64)+1;
      switch(timerClass){
      case("cs671.UtilClockTimer"):
        this.timer=new UtilClockTimer(new ClockRunner(this),1000);
        break;
      case("cs671.SimpleClockTimer"):
        this.timer=new SimpleClockTimer(new ClockRunner(this),1000);
        break;
      default:
        try{
          ClassLoader loader=ClassLoader.getSystemClassLoader();
          Class<?> tmp=null;
          Class<?> temp=null;
          Class<ClockTimer> q=ClockTimer.class;
          tmp=loader.loadClass(timerClass);
          temp=tmp.asSubclass(q);
          this.timer=(ClockTimer)tmp.newInstance();
        }
        catch(ClassCastException | ClassNotFoundException |
              IllegalAccessException | InstantiationException |
              ExceptionInInitializerError | SecurityException ex){
          throw new IllegalArgumentException();
        }
        break;
      }
    }
  }

  /** Constructs an active clock with <code>nbBits</code> bits.  Initially,
   * all bits are off (false) and the clock is associated with the given
   * timer.  The clock is initially stopped.
   *
   * @param nbBits the number of bits of this clock
   * @param t a timer; if the timer already has a delay and a
   * runnable, they will be reset
   * @throws IllegalArgumentException if <code>nbBits &lt; 1</code> or
   * if timer {@code t} is running
   */
  public Clock (int nbBits, ClockTimer t) {
    synchronized(lock){
      if(nbBits<=0 || t.isRunning()){
        throw new IllegalArgumentException();
      }
      this.clock=new BitSet(nbBits);
      this.clock.clear(0,nbBits);
      this.temp_clock=(BitSet)this.clock.clone();
      this.size=nbBits;
      this.words=(this.size/64)+1;
      (this._true=new BitSet(nbBits)).set(0,nbBits);
      (this._false=new BitSet(nbBits)).clear(0,nbBits);
      t.setRunnable(new ClockRunner(this));
      t.setDelay(1000);
      this.timer=t;
    }
  }

  /** Clock size
   * @return the number of bits in the clock
   */
  public int size () {
    return size;
  }

  /** Permanently terminates the timer of this clock.  A terminated
   * clock cannot be restarted and its timer becomes garbage.  The
   * clock is now a passive object.  The state of a passive clock can
   * still be changed with the various "set" methods but won't change
   * on its own.  If the clock was already passive, the method has no
   * effect and observers are not notified.
   *
   * @see ClockTimer#cancel
   */
  public void destroy () {
    synchronized(lock){
      try{
        timer.cancel();
        ticking=false;
        timer=null;
      } catch(NullPointerException ex){}
    }
  }

  /** Starts the clock.  The first bit update occurs after 1
   * second and every second after that, until the clock is stopped.
   *
   * @throws IllegalStateException if the clock is passive or is already running
   */
  public void start () {
    synchronized(lock){
    if (timer==null || ticking){
      throw new IllegalStateException();
    }
      ticking=true;
      timer.setDelay(1000L);
      timer.setRunnable(new ClockRunner(this));
      timer.start();
      /*new Thread(new Runnable(){
          public synchronized void run(){
            timer.start();
          }
          }).start();*/
    }
  }

  /** Stops the clock.  Bit updates stop occurring immediately.  The
   * clock is <em>not</em> passive; it can be restarted later.
   *
   * @throws IllegalStateException if the clock is passive or is not running
   */
  public void stop () {
    synchronized(lock){
    if(timer==null || !isTicking()){
      throw new IllegalStateException();
    }
      //here("stopping Timer");
      timer.stop();
      ticking=false;
    }
  }

  /** The status of the clock, as a boolean.
   *
   * @return true iff the clock is currently running.
   */
  public boolean isTicking () {
    synchronized(lock){
      return ticking;
    }
  }

  /** Resets the clock.  All bits are set to zero.  If the clock is
   * running, the next bit update will happen 1 second after bits are
   * cleared.  Observers are notified if the clock is running or if it
   * was non-zero.
   */
  public void clear () {
    synchronized (lock){

      before();
      clock=(BitSet)_false.clone();
      after();

    }
  }

  /** The value of bit number <code>n</code>.  Least significant bit is bit
   * number 0; most significat bit is bit <code>size()-1</code>.
   *
   * @param n bit number
   * @return boolean value of that bit.
   * @throws IndexOutOfBoundsException if no such bit exists
   */
  public boolean getBit (int n) {
    if(n<0 || n>=size){
      throw new IndexOutOfBoundsException();
    }
    synchronized (lock){
      return clock.get(n);
    }
  }

  /** Sets bit number <code>n</code> to true.  Least significant bit is bit
   * number 0; most significat bit is bit <code>size()-1</code>.
   *
   * @param n bit number to be set to true
   * @return boolean value of that bit before it is set.
   * @throws IndexOutOfBoundsException if no such bit exists
   */
  public boolean setBit (int n) {
    if(n<0 || n>=size){
      throw new IndexOutOfBoundsException();
    }
    synchronized (lock){
      before();
      temp=clock.get(n);
      clock.set(n);
      after();
    }
    return temp;
  }
  /** Sets bit number <code>n</code> to false.  Least significant bit is bit
   * number 0; most significat bit is bit <code>size()-1</code>.
   *
   * @param n bit number to be set to false
   * @return boolean value of that bit before it is set.
   * @throws IndexOutOfBoundsException if no such bit exists
   */
  public boolean clearBit (int n) {
    if(n<0 || n>=size){
      throw new IndexOutOfBoundsException();
    }
    synchronized (lock){
      before();
      temp=clock.get(n);
      clock.clear(n);
      after();
    }
    return temp;
  }
  /** Sets bit number <code>n</code> to its next value.  If the bit
   * was false, it is now true; if it was true, it is now false.  The
   * method returns a "carry" (true when the bit changes from true to
   * false).  Roughly speaking, this is a <code>+1</code> operation on
   * the given bit.  Since the state of the clock is guaranteed to
   * change, observers are always notified.
   *
   * @param n bit number to be set to next value
   * @return carry after the bit is set.
   * @throws IndexOutOfBoundsException if no such bit exists
   */
  public boolean nextBit (int n) {
    if(n<0 || n>=size){
      throw new IndexOutOfBoundsException();
    }
    synchronized (lock){
      before();
      temp=clock.get(n);
      clock.flip(n);
      after();
    }
    return temp;
  }
  /** Clock direction: FORWARD or BACKWARD.
   * @see #setDirection
   */
  public enum Direction {
    /** Forward direction.  Forward steps increase the binary value of
     * the clock by one.  "111111" becomes "000000".
     */
    FORWARD {
      public Direction reverse () {
        throw new UnsupportedOperationException();
      }
    },
    /** Backward direction.  Backward steps decrease the binary value of
     * the clock by one.  "000000" becomes "111111".
     */
    BACKWARD {
      public Direction reverse () {
        throw new UnsupportedOperationException();
      }
    };
    /** Reverses the direction.
     * @return the other direction, i.e., {@code FORWARD.reverse()}
     * returns {@code BACKWARD} and vice-versa.
     */
    abstract public Direction reverse ();
  }
  /** Sets the clock direction, FORWARD or BACKWARD. */
  public void setDirection (Direction d) {
    direction=d;
  }
  /** Gets the clock direction.
   * @return the clock's current direction
   */
  public Direction getDirection () {
    return direction;
  }
  /** Steps the clock.  This method increases or decreases the value
   * of the clock by one, according to the current direction.  Note
   * that bit number 0 is guaranteed to change as a result of calling
   * this method and therefore observers are always notified.
   *
   * @see #setDirection
   */
  public void step () {
    synchronized (lock){
      before();
      if (direction==Direction.BACKWARD){
        clock.xor(_true);
      }
      for (int i=0;i<size;i++){
        if(clock.get(i)){
          clock.flip(i);
        } else {
          clock.flip(i);
          break;
        }
      }
      if (direction==Direction.BACKWARD){
        clock.xor(_true);
      }
      after();
    }
  }

  /** Sets each bit value according to the array of booleans.  The
   * array size <em>must</em> equal the number of bits in the clock.
   * Bit number <code>i</code> in the clock is set to the value of
   * boolean number <code>i</code> in the array (i.e., the clock and
   * the array store bits in the same direction).  Note that observers
   * are notified only if parameter {@code v} and the clock differ by
   * at least one bit.
   *
   * Likely rather inefficent compared to setLongValue
   * @param v boolean value for each bit
   * @throws IllegalArgumentException if the size of the array is
   * different from the number of bits in the clock.
   */
  public void setValue (boolean[] v) {
    if(v.length!=size){
      throw new IllegalArgumentException();
    }
    synchronized (lock){
      before();
      for (int i=0;i<v.length;i++){
        if(v[i]){clock.set(i);}else{clock.clear(i);}
      }
      after();
    }
  }

  /** Sets each bit value according the long parameter.  If the clock
   * has more than 64 bits, bits beyond 63 are set to zero.  The least
   * significant bit of the long is also the least significant bit of
   * the clock, e.g., after <code>setLongValue(1L)</code>,
   * <code>getBit(0)</code> is true.  Note that observers
   * are notified only if parameter {@code v} and the clock differ by
   * at least one bit.
   *
   * @param v value for each bit of the clock
   * @throws IndexOutOfBoundsException if <code>value</value> has a
   * bit set to true beyond the clock's capacity.
   */
  public void setLongValue (long v) {
    if (size<63){
      if(Math.abs(v)>=Math.pow(2,size-1)){
        throw new IndexOutOfBoundsException();
      }
    }
    long[] temp_long=new long[1];
    temp_long[0]=v;
    synchronized (lock){
      before();
      clock=BitSet.valueOf(temp_long);
      after();
    }
  }
  /** Boolean value for each bit, as an array.  Modifications to this
   * array do not change the clock value.  Boolean number
   * <code>i</code> in the array is equal to bit number <code>i</code>
   * in the clock (i.e., the clock and the array store bits in the same
   * direction).
   *
   * @return boolean value for each bit
   */
  public boolean[] getValue () {
    boolean[] val;
    byte[] bytes;
    synchronized (lock){
      val=new boolean[size];
      bytes=clock.toByteArray();
    }
    for(int i=0;i<size;i++){
      val[i]=((bytes[i/8] & (1<<(i%8))) != 0);
    }
    return val;
  }
  /** All bit values, as a long.  The least
   * significant bit of the long is also the least significant bit of
   * the clock.
   * @return boolean value for each bit
   * @throws IllegalStateException ff the clock has more
   * than 64 bits <em>and</em> at least one bit beyond 63 is set
   */
  public long getLongValue () {
    long temp_long=0;
    synchronized (lock){
      if (!(size<=0) && size<63 || clock.get(64,size).isEmpty()){
        if(clock.equals(_false)){
          return 0;
        }
        temp_long=clock.toLongArray()[0];
        return temp_long;
      } else {
        throw new IllegalStateException();
      }
    }
  }
  /** A string representation of the clock.  This is a string of the
   * form <code>"101010 [ON]"</code> (running clock) or <code>"101010
   * [OFF]"</code> (stopped clock).  The first character of the string
   * is the value of the most significant bit of the clock.  There is
   * a single space between the last bit of the clock and the opening
   * square bracket.  {@code ON} and {@code OFF} are in uppercase.
   * The closing square bracket is the last character in the string
   * (no newline).
   *
   * @return a string representation of the clock
   */
  @Override public String toString () {
    StringBuilder k=new StringBuilder(size);
    synchronized (lock){
      for (int i=size-1;i>=0;i--){
        if(clock.get(i)){
          k.append('1');
        } else {
          k.append('0');
        }
      }
      k.append((this.isTicking())?" [ON]":" [OFF]");
      return k.toString();
    }
  }
}
