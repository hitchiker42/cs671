package cs671;
/*Idea, Use a btree-esque structure, looks something like this:
  top level [A...Z], only letters with actual words in the dictionary
  next n levels, also [A...Z], keep a bitset to indicate if a word or prefix
  terminate will null to indicate end of possible words.

  ex we have words ant bat cat cats ball and car
  the tree would be
  A   B   C
  N   A   A
  T  T L T R
  \0 \0 L S \0
  \0 \0   .
  we don't need to use characters actually,
  I mean we have 26 possible characters
  we can fit that in a byte eaisly...
*/

import java.util.*;
import static java.util.Arrays.*;
import static cs671.BoggleUtil.*;
import static cs671.Debug.*;
import static cs671.Functional.*;
/** A dictionary, specialized for the Boggle game.  In addition to
 * ususal word lookup, a dictionary offers a prefix lookup to decide
 * if a string is a prefix of an existing word.  In this class, a
 * dictionary is implemented as a sorted array of words (a better
 * implementation would use tries).  Method <code>find</code> is
 * implemented as a single binary search, and <code>hasWord</code> and
 * <code>hasPrefix</code> are implemented in terms of
 * <code>find</code> (a call to <code>find</code> should be used
 * preferably to a call to <code>hasWord</code> <em>and</em> a call to
 * <code>hasPrefix</code>.)  Dictionary words are stored in capital
 * letters; a word/prefix that contains lowercase letters will never
 * be found.
 *
 * @author Michel Charpentier
 * @version 2.0, 02/27/13
 * @see Arrays#binarySearch(Object[],Object)
 */
@SuppressWarnings("unchecked")
public class BoggleDictionary
implements Iterable<String> {
  byte[][] dict;//might make this a byte array to be compatable
  //with all the nio stuff
  TreeSet<String> words;
  int size;
  int max;
  /** Constructs a new dictionary by iterating over a collection of words.
   *
   * @param c a collections of words
   */
  public BoggleDictionary (java.util.Collection<String> c) {
    synchronized (this){
      this.size=c.size();
      this.max=0;
      this.words=new TreeSet<String>(c);
      this.dict=new byte[this.size][];
      String[] temp={""};
      String [] strings=c.toArray(temp);
      sort(strings);
      for(int i=0;i<this.size;i++){
        this.dict[i]=toByteArray(strings[i].toCharArray());
        if(strings[i].length()>this.max){
          this.max=strings[i].length();
        }
      }
    }
  }
  /** An iterator over the dictionary.  Words are returned in
   * alphabetical order.  The iterator does not support word removal.
   *
   * @return an iterator over the dictionary, in order
   */
  public Iterator<String>
  iterator (){
    return new staticIterator(words.iterator());
  }
  /** Word/prefix lookup.  This is implemented as a single binary
   * search, so this method should be preferred to
   * <code>hasWord</code> and <code>hasPrefix</code> when both
   * answers are needed.
   *
   * @param string the word/prefix to look for
   * @return a positive value if the string is a dictionary word, 0
   * if the string is not a word but is a prefix of a word, and a
   * negative value otherwise.
   * @see #hasWord
   * @see #hasPrefix
   * @see Arrays#binarySearch(Object[],Object)
   */
  public int
  find (String string) {
    int ans=-1;
    byte[] temp=toByteArray(string.toCharArray());
  OUTER:for (byte[] i : dict){
      if(i.length < temp.length){
        continue OUTER;
      }
    INNER:for(int j=0;j<temp.length;j++){
        if(i[j]!=temp[j]){
          break INNER;
        }
        if(j==temp.length-1){
          if(j==i.length-1){
            ans=1;
          } else {
            ans=0;
          }
          break OUTER;
        }
      }
    }
    return ans;
  }

  /** Dictionary size.
   *
   * @return the number of words in the dictionary
   */
  public int
  size () {
    synchronized (this){
      int temp=this.size;
      return temp;
    }
  }

  /** Whether the given string is a word in the dictionary.
   *
   * @param word the word to look for
   * @return true iff the string belongs to the dictionary, as a word
   * @see #find
   */
  public boolean
  hasWord (String word) {
    byte[] temp=toByteArray(word.toCharArray());
    boolean ans=false;
    for(byte[] i : dict){
      if(Arrays.equals(i,temp)){
        ans=true;
        break;
      }
    }
    return ans;
  }

  /** Whether the given string is a prefix of a word in the
   * dictionary.  Note that if the string is a dictionary word, the
   * method returns true.
   *
   * @param prefix the prefix to look for
   * @return true iff the string is a prefix of a word that belongs
   * to the dictionary
   * @see #find
   */
  public boolean
  hasPrefix (String prefix) {
    byte[] temp= toByteArray(prefix.toCharArray());
    boolean ans=false;
  OUTER:for (byte[] i : dict){
      if(i.length < temp.length){
        continue OUTER;
      }
    INNER:for(int j=0;j<temp.length;j++){
        if(i[j]!=temp[j]){
          break INNER;
        }
        if(j==temp.length-1){
          ans=true;
          break OUTER;
        }
      }
    }
    return ans;
  }
  /*Alternate way of doing prefix search, using an array of strings
    insted of an array of char*, is to do same top loop (replacing
    char[] i with string[i]) but replace the inner loop with
    if(binarySearch(i,0,prefix.size(),prefix)>=0){
    ans=true;
    break;
    }*/
}
