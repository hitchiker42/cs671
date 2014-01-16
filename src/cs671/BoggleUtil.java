package cs671;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.*;
import java.nio.charset.*;
import static cs671.BoggleMessage.*;
import static cs671.Functional.*;
import static cs671.Debug.*;
//import BoggleMessage.*;
/**
 *A Collection of static methods to be used in the various boggle classes
 *@author Tucker DiNapoli
 */
//Neighbors are all touching die, ie, including diagonial
// Need to remember to mark die in search fxns

@SuppressWarnings("unchecked")
class BoggleUtil {
  static Charset utf16=StandardCharsets.UTF_16;
  static  Charset iso_8=StandardCharsets.ISO_8859_1;
  static Charset utf8=StandardCharsets.UTF_8;
  static  Charset ascii=StandardCharsets.US_ASCII;
  private static boolean
  stringTest(ByteBuffer bb,int i,int j,int k,char[] guess){
    //parse args ang call testString
    return false;
  }
  CharInt CharInt(char c,int i){
    return new CharInt(c,i);
  }
  private static ArrayList<CharInt>
    getNeighbors(BoggleBoardGeneric board,int k,BitSet marked){//maybe just 1 int?
    //I already wrote it with 2 so eh, but with one int k the index
    //would be i=k/M,j=k%M
    assert(marked!=null);
    try{
    if(k%2!=0||board.size!=marked.size()||k>board.size){
      //Fail somehow
    }}catch(java.lang.Exception ex){
      ex.printStackTrace();
    }
    int N=board.N;int M=board.M;
    int M_1=M-1;int N_1=N-1;
    int i=k/M;int j=k%M;
    //  row       collumn
    ByteBuffer b=board.board;
    int[] indices;
    ArrayList<CharInt> neighbors=new ArrayList<>();
    if(0==i){//top row
      if(0==j){
        indices=new int[3];
        indices[0]=1;indices[1]=M;indices[2]=M+1;
        //here(Arrays.toString(indices));
      } else if (M_1==j){
        //top right corner, 3 neighbors
          indices=new int[] {j-1,M+j,M+j-1};
          //here(Arrays.toString(indices));

      } else{
        //top row, not a corner,5 neighbors
          indices=new int[] {j-1,j+1,j+M,M+j-1,M+j+1};
          //here(Arrays.toString(indices));
      }
    } else if (N_1==i){//indices from M*(N-1) to M*N-1
      //aka bottom row
      if(0==j){
        //bottom left corner, 3 eighbors
          indices=new int[] {i*M+1,i*M-M,i*M-M+1};
          //here(Arrays.toString(indices));
      } else if (M_1==j){
        //bottom right corner, 3 characters
          indices=new int[] {i*M-1+j,i*M-M+j,i*M-M-1+j};
          //here(Arrays.toString(indices));
      } else {
        //bottow row, not corner, 5 neighbors
          indices=new int[] {i*M+j-1,i*M+j+1,i*M+j-M,i*M+j-M-1,i*M+j-M+1};
          //here(Arrays.toString(indices));
      }
    } else {
      //some middle row
      if(0==j){
        //first column, middle row, 5 neighbors
          indices=new int[] {i*M+1,i*M-M,i*M+M,i*M-M+1,i*M+M+1};
          //here(Arrays.toString(indices));
      } else if (M_1==j){
        //last column,middle row, 5 neighbors
          indices=new int[] {i*M+j-1,i*M+j-M,i*M+j+M,i*M+j-M-1,i*M+j+M-1};
          //here(Arrays.toString(indices));
      } else {
        //any interior value, 8 neighbors
        indices=new int[] {i*M+j-1,i*M+j+1,i*M+j-M,i*M+j+M,i*M+j-M-1,
                            i*M+j-M+1,i*M+j+M+1,i*M+j+M-1};
        //here(Arrays.toString(indices));
      }
    }
    StringBuilder build=new StringBuilder();
    for(int a=0;a<indices.length;a++){
      int q=indices[a];
      build.append(String.valueOf(q)+',');
      try{
        if(marked.get(q)){
          neighbors.add(new CharInt((char)b.get(q),q));
        }
      } catch (java.lang.Exception ex){
        //ex.printStackTrace();
        here("Neighbor Search Fail");
        here(build.toString());
      }
    }
    //here(build.toString());
    return neighbors;
  }
  static void
  parseBoard(LinkedList<Character> list,int k,BoggleBoardGeneric Board,
             BoggleDictionary dict,TreeSet<String> words,BitSet marked){
    if(list.isEmpty()){
      marked=new BitSet(Board.size);
      list.add((char)Board.board.get(k));
      marked.set(0,marked.size());
    }
    ArrayList<CharInt> neighbors=getNeighbors(Board,k,marked);
    marked.clear(k);
    for (CharInt i:neighbors){
      LinkedList<Character> temp=cons(list,i.c);
      String word=new String(charArray(CharArray(temp)));
      if (dict.find(word)>0){
        words.add(word);
      }
      if (dict.find(word)>=0){
        parseBoard(temp,i.i,Board,dict,words,(BitSet)marked.clone());
      }
    }
  }
  //recursion is stupid in java
  //for(char i : getNeighbors(board,i,j)){
  //    if(dict.containsString(new String(
  static SortedSet<String>
  getWords(BoggleBoardGeneric board,BoggleDictionary dict){
    TreeSet<String> words=new TreeSet<String>();
    for(int i=0;i<board.size;i++){
      LinkedList<Character> temp=new LinkedList<>();
      BitSet marked=null;
      parseBoard(temp,i,board,dict,words,marked);
    }
    return words;
  }
  //Needs to Be Written
  static boolean testResult;
  static boolean
  testString (String guess,BoggleBoardGeneric board){
    testResult=false;
  TOP:while(!testResult){
    for(int i=0;i<board.size;i++){
      LinkedList<Character> temp=new LinkedList<Character>();
      BitSet marked=null;
      boolean test=stringSearch(temp,i,board,guess,marked);
      here(test);
      if(testResult){
        here("Success still");
        break TOP;
      }
    }
    return false;
    }
    return true;
  }
  static boolean
  stringSearch(LinkedList<Character> list,int k,
               BoggleBoardGeneric Board,String string,BitSet marked){;
    if(list.isEmpty()){
      marked=new BitSet(Board.size);
      marked.set(0,marked.size());
      list.add((char)Board.board.get(k));
    }
    if(new String(charArray(CharArray(list))).toUpperCase().equals(string)){
      return true;
    }
    if (!string.startsWith(String.valueOf(list.get(0)))){
      return false;
    }
    here(list.toString());
    ArrayList<CharInt> neighbors=getNeighbors(Board,k,marked);
    marked.clear(k);
  TOP:while(!testResult){
    for (CharInt i:neighbors){
      LinkedList<Character> temp=cons(list,i.c);
      here(list.toString());
      String word=new String(charArray(CharArray(temp)));
      here(word);
      if(word.toUpperCase().equals(string)){
        here("Success");
        testResult=true;
        break TOP;
      } else if (string.startsWith(word)){
        stringSearch(temp,i.i,Board,string,marked);
      }
    }
    return false;
    }
    return true;
  }
  //don't know why I wrote this, it seems a bit silly now
  private static CharBuffer
  alphabet () {
    ByteBuffer alpha=ByteBuffer.allocateDirect(64);
    int j=0;
    for(byte i=0x41;i<0x5B;i++){
      alpha.put(j,i);
      j++;
    }
    return alpha.asCharBuffer();
  }
  static final CharBuffer alphabet=alphabet();

  /*  static String BoardString(BoggleBoardGeneric bb){
    StringBuilder string=new StringBuilder();
    string.append(bb.M);
    string.append('x');
    string.append(bb.N);
    ByteBuffer board=bb.board;
    byte[] row=new byte[bb.M];
    for(int i=0;i<bb.N;i++){
      board.get(row,0,bb.M);
      string.append(String.valueOf(row)+'\n');
    }
    return string.toString();
  }
  */
  static String BoardString(BoggleBoardGeneric board){
    ByteBuffer bb=ByteBuffer.allocate(board.size+32);
      bb.clear();
      for (char i:(String.valueOf(board.N)).toCharArray()){
        bb.put((byte)i);
      }
      bb.put((byte)0x78);
      for (char i:(String.valueOf(board.M)).toCharArray()){
        bb.put((byte)i);
      }
      bb.put(board.board);
      bb.flip();
      return new String(bb.array(),ascii);
    }
  static LinkedHashSet<String> lhsetXor(LinkedHashSet<String> a,
                                        LinkedHashSet<String> b){
    LinkedHashSet<String> c=new LinkedHashSet<>();
    for (String i : a){
      if(b.contains(i)){
        c.add(i);
      }
    }
    return c;
  }
  static void sendMessages(Set<Player> players,
                           Type type,String message){
    for (Player i : players){
      i.sendMessage(new BoggleMessage(type,message));
    }
  }
  //check to make sure limits are right
  static int score(String word){
    if (word.length()<3){return 0;}
    else if (word.length()<9){return word.length()-2;}
    else {return 6;}
  }
  static LinkedHashSet<String>
  intersect(LinkedHashMap<String,LinkedHashSet<String>> sets,
            String[] players){
    int n = sets.size();
    if (n == 1){return new LinkedHashSet<String> ();}
    here(sets);
    LinkedHashSet<String> all=
      new LinkedHashSet<String>(sets.get(players[0]));
    LinkedHashSet<String> intersect=new LinkedHashSet<>();
    for(int i=1;i<n;i++){
      for(String s : sets.get(players[i])){
        if(!all.add(s)){
          intersect.add(s);
        }
      }
    }
    return intersect;
  }
  static char roll(Die d){
    d.roll();
    return d.top();
  }
  static void shuffle (ByteBuffer board,Die[] dice){
    int i=0;
    //board.clear();
    for(;i<dice.length;i++){
      board.put(i,(byte)roll(dice[i]));
    }
    //board.flip();
  }
  static Die Die(Die die){
    return new Die(die);
  }
  static Character[] CharArray(Collection<Character> array){
    Character[] temp=new Character[] {'0'};
    return array.toArray(temp);
  }
  static char[] charArray(Character[] Chars){
    int len=Chars.length;
    char[] chars= new char[len];
    for (int i=0;i<len;i++){
      chars[i]=Chars[i];
    }
    return chars;
  }
  static Random rand=new Random(System.currentTimeMillis());
  static char
  randChar(){
    return (char)(rand.nextInt(0x1A)+0x41);
  }
  static void permute(Die[] dice){
    int k=dice.length;
    BitSet used=new BitSet(k);
    used.set(0,k);
    Die[] newDice=Arrays.copyOf(dice,k);
    int i=0;
    for(Die d : newDice){
      for(;;){
        i=rand.nextInt(k);
        if(used.get(i)){
          used.clear(i);
          dice[i]=d;
          if (i==(k-1)){
            k--;
          }
          break;
        }
      }
    }
    dice=newDice;
  }
  static byte[] toByteArray(char[] chars){
    byte[] bytes=new byte[chars.length];
    for (int i=0;i<chars.length;i++){
      bytes[i]=(byte)chars[i];
    }
    return bytes;
  }
  static char[] toCharArray(byte[] bytes){
    char[] chars=new char[bytes.length];
    for (int i=0;i<bytes.length;i++){
      chars[i]=(char)bytes[i];
    }
    return chars;
  }
}
class charArrayIterator
implements Iterator<String>{
  char[][] chars;
  int i=0;
  int len=0;
  charArrayIterator(char[][] chars){
    this.chars=chars;
    this.len=chars.length;
  }
  public boolean hasNext(){
    return(i==len);
  }
  public String next(){
    String temp=new String(chars[i]);
    i++;
    return temp;
  }
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
class CharInt {
  char c;int i;
  CharInt(char c,int i){
    this.c=c;this.i=i;
  }
}
class staticIterator
implements Iterator<String> {
  Iterator<String> iter;
  staticIterator(Iterator<String> iter){
    this.iter=iter;
  }
  public boolean hasNext(){return iter.hasNext();}
  public String next(){return iter.next();}
  public void remove(){
    throw new UnsupportedOperationException();
  }
}
