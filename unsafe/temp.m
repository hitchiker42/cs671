package cs671;
include java.util.*;
include static cs671.functional.*;
clone(Object,thing)
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
public class Clock extends java.util.Observable {

  static final String DEFAULT_TIMER_CLASS = SimpleClockTimer.class.getName();
  /**
   *The actual binary data of the clock
   */
  private BitSet clock;
  private final BitSet _true;
  private final BitSet _false;
  /** The "lock" that guards all clock state changes.  Every state
   * change, including automatic changes on active clocks, is
   * performed while owning this lock.
   */
  protected final Object lock;

  /** Constructs a passive clock with <code>nbBits</code> bits.  Initially, all
   * bits are off (false).  The clock has no timer.
   *
   * @param nbBits the number of bits of this clock
   * @throws IllegalArgumentException if <code>nbBits &lt; 1</code>
   */
  public Clock (int nbBits) {
    clock=new BitSet(nbBits);
    _true=new BitSet(nbBits).set(0,nbBits);
    _false=new BitSet(nbBits).clear(0,nbBits);
    throw new UnsupportedOperationException();
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
    clock=new BitSet(nbBits);
    _true=new BitSet(nbBits).set(0,nbBits);
    _false=new BitSet(nbBits).clear(0,nbBits);
    /*case class in
        UtilClockTimer) ;;
        SimpleClockTimer) ;;
        Default) try to get class timerClass,
          assert timerClass implements ClockTimer
          try to instantiate timerClass
    */
    throw new UnsupportedOperationException();
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
    clock=new BitSet(nbBits);
    _true=new BitSet(nbBits).set(0,nbBits);
    _false=new BitSet(nbBits).clear(0,nbBits);
    throw new UnsupportedOperationException();
  }

  /** Clock size
   * @return the number of bits in the clock
   */
  public sync int size () {
    return clock.size();
    throw new UnsupportedOperationException();
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
  public sync void destroy () {
    throw new UnsupportedOperationException();
  }

  /** Starts the clock.  The first bit update occurs after 1
   * second and every second after that, until the clock is stopped.
   *
   * @throws IllegalStateException if the clock is passive or is already running
   */
  public sync void start () {
    throw new UnsupportedOperationException();
  }

  /** Stops the clock.  Bit updates stop occurring immediately.  The
   * clock is <em>not</em> passive; it can be restarted later.
   *
   * @throws IllegalStateException if the clock is passive or is not running
   */
  public sync void stop () {
    throw new UnsupportedOperationException();
  }

  /** The status of the clock, as a boolean.
   *
   * @return true iff the clock is currently running.
   */
  public sync boolean isTicking () {
    throw new UnsupportedOperationException();
  }

  /** Resets the clock.  All bits are set to zero.  If the clock is
   * running, the next bit update will happen 1 second after bits are
   * cleared.  Observers are notified if the clock is running or if it
   * was non-zero.
   */
  public sync void clear () {
    clock.xor(clock);
    throw new UnsupportedOperationException();
  }

  /** The value of bit number <code>n</code>.  Least significant bit is bit
   * number 0; most significat bit is bit <code>size()-1</code>.
   *
   * @param n bit number
   * @return boolean value of that bit.
   * @throws IndexOutOfBoundsException if no such bit exists
   */
  public boolean getBit (int n) {
    try{
      return clock.get(n);
      throw new UnsupportedOperationException();
    }
  }

  /** Sets bit number <code>n</code> to true.  Least significant bit is bit
   * number 0; most significat bit is bit <code>size()-1</code>.
   *
   * @param n bit number to be set to true
   * @return boolean value of that bit before it is set.
   * @throws IndexOutOfBoundsException if no such bit exists
   */
  public sync boolean setBit (int n) {
    try{
      boolean temp=clock.get(n);
      clock.set(n);
      return temp;
      throw new UnsupportedOperationException();
    }
  }

  /** Sets bit number <code>n</code> to false.  Least significant bit is bit
   * number 0; most significat bit is bit <code>size()-1</code>.
   *
   * @param n bit number to be set to false
   * @return boolean value of that bit before it is set.
   * @throws IndexOutOfBoundsException if no such bit exists
   */
  public sync boolean clearBit (int n) {
    try{
      boolean temp=clock.get(n);
      clock.clear(n);
      return temp;
      throw new UnsupportedOperationException();
    }
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
  public sync boolean nextBit (int n) {
    clock.flip(n);
    throw new UnsupportedOperationException();
    return flip(clock).get(n);
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
  public sync void setDirection (Direction d) {
    throw new UnsupportedOperationException();
  }

  /** Gets the clock direction.
   * @return the clock's current direction
   */
  public sync Direction getDirection () {
    throw new UnsupportedOperationException();
  }

  /** Steps the clock.  This method increases or decreases the value
   * of the clock by one, according to the current direction.  Note
   * that bit number 0 is guaranteed to change as a result of calling
   * this method and therefore observers are always notified.
   *
   * @see #setDirection
   */
  public sync void step () {
    if (Direction.getDirection()=BACKWARD){
      clock.xor(_true);
    }
    for (int i=0;i<clock.size();i++){
      if(clock.get(i)){
        clock.flip(i);
      } else {
        clock.flip(i);
        break;
      }
    }
    if (Direction.getDirection()=BACKWARD){
      clock.xor(_true);
    }
  }
    throw new UnsupportedOperationException();
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
  public sync void setValue (boolean[] v) {
    for (int i=0;i<v.length;i++);
    throw new UnsupportedOperationException();
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
  public sync void setLongValue (long v) {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  /** All bit values, as a long.  The least
   * significant bit of the long is also the least significant bit of
   * the clock.
   * @return boolean value for each bit
   * @throws IllegalStateException ff the clock has more
   * than 64 bits <em>and</em> at least one bit beyond 63 is set
   */
  public long getLongValue () {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }
}
