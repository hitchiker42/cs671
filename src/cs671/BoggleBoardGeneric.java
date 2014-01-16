package cs671;

import java.util.SortedSet;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import static cs671.Debug.*;
import static cs671.BoggleUtil.*;
import static cs671.Functional.*;

/** A Boggle board (or grid).  Two types of boards can be created in
 * this package.  Instances of {@code DiceBoggleBoard} are intended
 * for actual play; they contain dice and can be "rattled" to get
 * random configurations.  Boards created with
 * <code>FixedBoggleBoard</code>, on the other hand, are intended for
 * puzzle solving and cannot be rattled.  Boards offer methods to
 * check if a string can be found and to produce all the words on the
 * board that belong to a dictionary.
 *
 * @author Tucker DiNapoli API and Template by Michel Charpentier
 * @version 2.0, 02/27/13
 */
class BoggleBoardGeneric
implements BoggleBoard{
  ByteBuffer board;
  int N;
  int M;
  int h;int w;
  int size;
  BoggleBoardGeneric(int height,int width) {
    synchronized (this){
    this.N=height;
    this.M=width;;
    this.size=this.N*this.M;
    this.board=ByteBuffer.allocate(this.size);
    }
  }

  /** Number of columns in board. */
  public int
  getWidth (){
    return M;
  }

  /** Number of rows in board. */
  public int
  getHeight (){
    return N;
  }

  /** The board letter at the designated position.  Indexes start with 0.
   *
   * @param col the column
   * @param row the row
   * @return the letter at (row,column)
   */
  public char
  letterAt (int row, int col){
    synchronized (this){
      return (char)board.get(M*row+(col-1));
    }
  }

  /** Checks if a string can be found on the board, according to the
   * rules of Boggle.  The string does not have to be a valid word.
   *
   * @param string the string to look for
   * @return true iff the string is found on the board
   */
  public boolean
  containsString (String string){
    return testString(string,this);
  }

  /** All the dictionary words that can be found on the board.  Words
   * are retuned in order: longest words first and all the words of a
   * given length in alphabetical order.  Dictionary words are
   * expected to be in uppercase letters; words are also returned by
   * this method in uppercase letters.
   *
   * @param dict a dictionary of words
   * @return all the words from the the dictionary that can be found
   * on the board, in order
   */
  public SortedSet<String>
  allWords (BoggleDictionary dict){
    return getWords(this,dict);
  }
}
