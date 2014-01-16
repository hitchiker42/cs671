package cs671;
import java.util.*;
@SuppressWarnings("unchecked")
class Functional{
  static String value(Object o){
    return String.valueOf(o);
  }
  static String value(int o){
    return String.valueOf(o);
  }
  static String value(char o){
    return String.valueOf(o);
  }
  static String value(double o){
    return String.valueOf(o);
  }
  static String value(long o){
    return String.valueOf(o);
  }
  static String value(byte o){
    return String.valueOf(o);
  }
  static Character car(LinkedList<Character> list){
    Character temp=list.getFirst();
    return (Character)(temp);
  }
  static Character last(LinkedList<Character> list){
    Character temp=list.pollLast();
    return (Character)temp;
  }
  static LinkedList<Character> cdr(LinkedList<Character> list){
    LinkedList<Character> temp = (LinkedList<Character>)list.clone();
    temp.removeFirst();
    return temp;
  }
  static LinkedList<Character> cons(LinkedList<Character> car,LinkedList<Character> cdr){
    LinkedList<Character> temp=new LinkedList<Character>(car);
    temp.addAll(cdr);
    return temp;
  }
  static LinkedList<Character> cons(Character car,LinkedList<Character> cdr){
    LinkedList<Character> temp=new LinkedList<Character>(cdr);
    temp.addFirst(car);
    return temp;
  }
  static LinkedList<Character> cons(Character car,Object o){
    if(!(o==null)){
      return null;
    }
    LinkedList<Character> temp=new LinkedList<Character>();
    temp.addFirst(car);
    return temp;
  }
  static LinkedList<Character> cons(LinkedList<Character> car,Character cdr){
    LinkedList<Character> temp=(LinkedList<Character>)car.clone();
    temp.add(cdr);
    return temp;
  }
  class struct {
    struct(){}
    char[] chars;
    int[] x;
  }
  class CharInt {
    char c;int i;
    CharInt(char c,int i){
      this.c=c;this.i=i;
    }
  }
  CharInt CharInt(char c,int i){
    return new CharInt(c,i);
  }
  //<0 if word, 0 if prefix,>0 otherwise
}
  /*
  private static ArrayList<CharInt>
    getNeighbors(BoggleBoardGeneric board,int k,BitSet marked){//maybe just 1 int?
    //I already wrote it with 2 so eh, but with one int k the index
    //would be i=k/M,j=k%M
    if(j%2!=0||board.size()!=marked.size()||k>board.size()){
      //Fail somehow
    }
    int N=board.N;int M=board.M;
    int M_2=M-2;int N_1=N-1;
    int i=k/M;int j=k%M;
    //  row       collumn
    ByteBuffer b=board.board;
    int[] indices;
    ArrayList<CharInt> neighbors=new ArrayList<>();
    //struct neighbors=new struct();
    if(0==i){//top row
      if(0==j){
        //top left corner, 3 neighbors
          indices=new int[] {2,M,M+2};
      } else if (M_2==j){
        //top right corner, 3 neighbors
          indices=new int[] {j-2,M+j,M+j-2};

      } else{
        //top row, not a corner,5 neighbors
          indices=new int[] {j-2,j+2,j+M,M+j-2,M+j+2};
      }
    } else if (N_1==i){//indices from M*(N-1) to M*N-1
      //aka bottom row
      if(0==j){
        //bottom left corner, 3 eighbors
          indices=new int[] {i*M+2,i*M-M,i*M-M+2};
      } else if (M_2==j){
        //bottom right corner, 3 characters
          indices=new int[] {i*M-4,i*M-M-2,i*M-M-4};
      } else {
        //bottow row, not corner, 5 neighbors
          indices=new int[] {i*M+j-2,i*M+j+2,i*M+j-M,i*M+j-M-2,i*M+j-M+2};
      }
    } else {
      //some middle row
      if(0==j){
        //first column, middle row, 5 neighbors
          indices=new int[] {i*M+2,i*M-M,i*M+M,i*M-M+2,i*M+M+2};
      } else if (M==j){
        //last column,middle row, 5 neighbors
          indices=new int[] {i*M-2,i*M-M,i*M+M,i*M-M-2,i*M+M-2};
      } else {
        //any interior value, 8 neighbors
          indices=new int[] {i*M-2,i*M+2,i*M-M,i*M+M,
                             i*M-M-2,i*M-M+2,i*M+M+2,i*M+M-2};
      }
    }
    for(int i:indices){
      if(marked.get(i)){
        marked.set(i);
        neighbors.add(CharInt(b.getChar(i),i));
      }
    return neighbors;
  }
}

  //Basically lots of pointer arithmitic
  private static struct
  getNeighbors(BoggleBoardGeneric board,int i,int j,BitSet marked){//maybe just 1 int?
    //I already wrote it with 2 so eh, but with one int k the index
    //would be i=k/M,j=k%M
    //This fxn assumes i,j,M,N are in terms of bytes,so
    //this would be a lot eaiser in c
    if(j%2!=0){
      //Fail somehow
    }
    int N=board.N;int M=board.M;
    int M_2=M-2;int N_1=N-1;
    ByteBuffer b=board.board;
    struct neighbors=new struct();
    //struct neighbors=new struct();
    if(0==i){
      if(0==j){
          neighbors.chars=new char[] {b.getChar(2),b.getChar(M),b.getChar(M+2)};
          neighbors.x=new int[] {0,1,1};
          neighbors.y=new int[] {2,0,2};
      } else if (M_2==j){//M-1 is in the middle of a char
          neighbors.chars=new char[] {b.getChar(M-4),b.getChar(2*M-2),b.getChar(2*M-4)};
          neighbors.x=new int[] {0,1,1};
          neighbors.y=new int[] {j-2,j,j-2};
      } else{
          neighbors.chars=new char[] {b.getChar(j-2),b.getChar(j+2),b.getChar(j+M),
                     b.getChar(j+M-2),b.getChar(j+M+2)};
          neighbors.x=new int[] {0,0,1,1,1};
          neighbors.y=new int[] {j-2,j+2,0,j-2,j+2};
      }
    } else if (N_1==i){//indices from M*(N-1) to M*N-1
      if(0==j){
          neighbors.chars=new char[] {b.getChar(i*M+2),
                           b.getChar(i*M-M),b.getChar(i*M-M+2)};
          neighbors.x=new int[] {i,i-1,i-1};
          neighbors.y=new int[] {j+2,j,j+2};
      } else if (M_2==j){
          neighbors.chars=new char[]
            {b.getChar(i*M-4),
             b.getChar(i*M-M-2),b.getChar(i*M-M-4)};
          neighbors.x=new int[] {i,i-1,i-1};
          neighbors.y=new int[] {j-2,j,j-2};
      } else {
          neighbors.chars=new char[]
            {b.getChar(i*M+j-2),b.getChar(i*M+j+2),
             b.getChar(i*M+j-M),b.getChar(i*M+j-M-2),b.getChar(i*M+j-M+2)};
          neighbors.x=new int[] {i,i,i-1,i-1,i-1};
          neighbors.y=new int[] {j-2,j+2,j,j-2,j+2};
      }
    } else {
      if(0==j){
          neighbors.chars=new char[]
            {b.getChar(i*M+2),b.getChar(i*M-M),b.getChar(i*M+M),
             b.getChar(i*M-M+2),b.getChar(i*M+M+2)};
          neighbors.x=new int[] {i,i-1,i+1,i-1,i+1};
          neighbors.y=new int[] {j+2,j,j,j+2,j+2};
      } else if (M==j){
          neighbors.chars=new char[]
            {b.getChar(i*M-2),b.getChar(i*M-M),b.getChar(i*M+M),
             b.getChar(i*M-M-2),b.getChar(i*M+M-2)};
          neighbors.x=new int[] {i,i-1,i+1,i-1,i+1};
          neighbors.y=new int[] {j-2,j,j,j-2,j-2};
      } else {
          neighbors.chars=new char[]
            {b.getChar(i*M-2),b.getChar(i*M+2),b.getChar(i*M-M),
             b.getChar(i*M+M),b.getChar(i*M-M-2),b.getChar(i*M-M+2),
             b.getChar(i*M+M+2),b.getChar(i*M+M-2)};
          neighbors.x=new int[] {i,i,i-1,i+1,i-1,i-1,i+1,i+1};
          neighbors.y=new int[] {j-2,j+2,j,j,j-2,j+2,j+2,j-2};
      }
    }
    return neighbors;
  }*/
/* static T car(LinkedList<T> list){
    return (T)(list.getFirst().clone());
  }
  static T last(LinkedList<T> list){
    T temp=(T)list.pollLast().clone();
    return T;
  }
  static LinkedList<T> cdr(LinkedList<T> list){
    LinkedList<T> temp = (LinkedList<T>)list.clone();
    temp.removeFirst();
    return temp;
  }
  static LinkedList<T> cons(LinkedList<T> car,LinkedList<T> cdr){
    LinkedList<T> temp=new LinkedList<T>(car).addAll(cdr);
    return temp;
  }
  static LinkedList<T> cons(T car,LinkedList<T> cdr){
    LinkedList<T> temp=new LinkedList<T>(cdr);
    temp.addFirst(car);
    return temp;
  }
  static LinkedList<T> cons(T car,Object o){
    if(!o==null){
      throw Exception;
    }
    LinkedList<T> temp=new LinkedList<T>();
    temp.addFirst(car);
    return temp;
  }
  static LinkedList<T> cons(LinkedList<T> car,T cdr){
    LinkedList<T> temp=(T)car.clone();
    temp.add(cdr);
    return temp;
    }*/