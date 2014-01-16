package cs671;

import java.util.SortedSet;
import static cs671.BoggleUtil.*;
import static cs671.Debug.*;
/** A playable Boggle board (or grid).  The board contains dice and
 * can be "rattled" to get random configurations.
 *
 * @author Tucker DiNapoli API and Template by Michel Charpentier
 * @version 2.0, 02/27/13
 */
public class DiceBoggleBoard
extends BoggleBoardGeneric
implements BoggleBoard{
  Die[] dice;
  /** Creates a new board.  The board has the specified dimensions
   * and uses <em>copies</em> of the dice given in argument.  If
   * there aren't enough dice in the array, some dice will be copied
   * more than once.
   *
   * @param w the width of the board
   * @param h the height of the board
   * @param d an array of dice
   * @throws IllegalArgumentException if the width, the height or
   * the number of dice is less than 1
   */
  public
  DiceBoggleBoard (int w, int h, Die[] d) {
    super(h,w);
    synchronized (this){
    if(1>w||1>h||1>d.length){
      throw new IllegalArgumentException();
    }
    this.dice=new Die[w*h];
    int i=0;
    int dieSize=w*h;
    while (i<dieSize){
      for (Die die : d){
        if (i<dieSize){
          this.dice[i]=Die(die);
          i++;
        }
      }
    }
    shuffle(this.board,this.dice);
    }
  }
  /** "Rattles" the board by shaking it to randomize its letters.
   * This method involves two operations: first, dice are randomly
   * permuted, then each die is "rolled".
   *
   * @see Die#roll
   */
  public void rattle () {
    permute(dice);
    shuffle(board,dice);
  }
  //in super class
  /*public char letterAt (int row, int col) {}
    public void setLetter (int row, int col, char c) {}
    public int getWidth () {}
    public int getHeight () {}
    public boolean containsString (String string) {}
    public SortedSet<String> allWords (BoggleDictionary dict) {}*/
}
