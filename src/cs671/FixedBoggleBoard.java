package cs671;

import java.util.SortedSet;
import static cs671.BoggleUtil.*;
import static cs671.Debug.*;
import java.nio.*;

/** A fixed Boggle board.  These boards are intended for puzzle
 * solving and cannot be "rattled" (the {@code rattle} method has no
 * effect).
 *
 * @author Tucker DiNapoli API and Template by Michel Charpentier
 * @version 2.0, 02/27/13
 */
public class FixedBoggleBoard
extends BoggleBoardGeneric
implements BoggleBoard{
  /** Builds a fixed board.  The array of letters must contain
   * exactly <code>w*h</code> characters, which are used to fill the
   * board in a top-to-bottom, left-to-right fashion.
   *
   * @param w the width of the board
   * @param h the height of the board
   * @param letters exactly enough letters to fill the board
   */
  public//constructor
  FixedBoggleBoard (int w, int h, char[] letters) {
    super(0,0);
    if (letters.length!=w*h){
      throw new RuntimeException();
    }
    this.N=h;
    this.M=w;
    this.size=h*w;
    byte[] bytes=toByteArray(letters);
    this.board=ByteBuffer.wrap(bytes);
  }
  public void setLetter (int row, int col, char c) {
    byte b=(byte)c;
    board.put((row*M)+(col-1),b);
  }
  //in super class
  /*public char letterAt (int row, int col) {}
    public int getWidth () {}
    public int getHeight () {}
    public boolean containsString (String string) {}
    public SortedSet<String> allWords (BoggleDictionary dict) {}*/
}
