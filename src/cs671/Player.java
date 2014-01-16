package cs671;
import static cs671.Debug.*;
import static cs671.BoggleUtil.*;
import java.nio.channels.*;
import java.nio.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import sun.misc.*;
/* we get name via first sent message
   which has our name in the body*/
@SuppressWarnings("unchecked")
class Player implements BogglePlayer, Runnable{
  /* public static Unsafe getUnsafe() {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      return (Unsafe)f.get(null);
    } catch (Exception e) {
      System.err.println("FAIL");
      System.exit(1);}
    return null;
    }*/
  //Will likely add more fields
  //to indicate different flags(playing, wants to play,wants to stop etc...)
  //Unsafe unsafe = getUnsafe();
  BogglePlayer player;
  String name;
  SocketChannel client;
  boolean playing=false;
  boolean start=false;
  boolean stop=false;
  boolean registered=false;
  DataInputStream in=new DataInputStream(System.in);
  ConcurrentLinkedDeque<Object> messenger;
  LinkedHashSet<String> words;
  //Set<String> words;
  BoggleBoardGeneric board;
  ByteBuffer bb;
  int bbSize;
  int score;
  class message{
    message(BoggleMessage msg,Player player){
      this.player=player;
      this.message=msg;
    }
    BoggleMessage message;
    Player player;
  }
  Player(SocketChannel socket,
         ConcurrentLinkedDeque<Object> deque,BoggleBoardGeneric board){
    here("Player is Alive");
    this.client=socket;
    this.messenger=deque;
    this.player=null;
    this.board=board;
    //this.bbSize=board.size;
    //this.bbSize+=64-(this.bbSize%64)+1024;
    this.bbSize=2097152;
    this.bb=ByteBuffer.allocate(this.bbSize);
    this.words=new LinkedHashSet<String>();
  }
  Player(BogglePlayer p,ConcurrentLinkedDeque<Object> deque,BoggleBoardGeneric board){
    this.player=p;
    this.client=null;//if this constructor gets called
    //we're not running a server so no socket.
    this.name=p.getName();
    this.messenger=deque;
    this.board=board;
    this.bbSize=2097152;
    this.bb=ByteBuffer.allocate(this.bbSize);
    this.words=new LinkedHashSet<String>();
  }
  Player(){}
  @Override
  public String
  getName(){return name;}
  @Override
  public void
  sendMessage(BoggleMessage msg){
    synchronized (this){
      String msgString=msg.toString();
      if (BoggleMessage.Type.BOARD==msg.type){
        bb.clear();
        for (char i:"BOARD".toCharArray()){
          bb.put((byte)i);
        }
      bb.put((byte)0x3A);
      for (char i:(String.valueOf(board.N)).toCharArray()){
        bb.put((byte)i);
      }
      bb.put((byte)0x78);
      for (char i:(String.valueOf(board.M)).toCharArray()){
        bb.put((byte)i);
      }
      bb.put(board.board);
      bb.put((byte)'\n');
      bb.flip();
      try{
        if(player!=null){
          player.sendMessage(BoggleMessage.parse(new String(bb.array())));
          return;
        } else {
        here("writing message"+(new String(bb.array())));
        client.write(bb);
        here("message Written");
        }
      } catch(Exception ex){
        here("IO-ERROR");
      }
      bb.clear();
      } else if (BoggleMessage.Type.TEXT==msg.type){
        if(player!=null){
          player.sendMessage(msg);
          return;
        }
        bb.clear();
        char[] text=msg.body.toCharArray();
        for (char i:"TEXT".toCharArray()){
          bb.put((byte)i);
        }
        bb.put((byte)(0x3A));
        for (char i:text){
          bb.put((byte)i);
        }
        bb.flip();
        /* try{
           string = new String(bb.array(),0,bb.limit());
           assert(string.equals(msgString));
           BoggleMessage.parse(msgString);
           string=msgString;
           BoggleMessage.parse(string);
           } catch (Exception ex){
           here("message parse failure ");
           here("original message:\n"+msg.toString());
           here("final    message:\n"+string);
           ex.printStackTrace();
           System.exit(1);
           }*/
        try{
          client.write(bb);
          bb.clear();
          bb.put((byte)'\n');
          bb.flip();
          client.write(bb);
        } catch(IOException ex){}
        bb.clear();
      } else {
        //throw error
      }
    }
  }
  public void
  run(){
    int test=0;
    //Need to make sure that messages get sent
    //when sendMessage is called
    //might need to interupt this
    while(client.isConnected()&&test>=0){
      bb.clear();
      try{
        here("trying to read");
        test=client.read(bb);//make sure this words
        here(String.format("actually read %d bytes",test));
        if(test==-1){break;}
        bb.flip();
        String string = new String(bb.array(),0,bb.limit()-1);
        //System.err.println(string);
        /*      System.out.print(string);
                System.out.write(bb.array());*/
        BoggleMessage msg=BoggleMessage.parse(string);
        //System.err.println(msg.type);
        if(null==name){
          if (msg.type!=BoggleMessage.Type.JOIN){
            //disconnect
          } else {
            name = msg.body;
          }
        }
        messenger.offer(new message(msg,this));
        assert(!messenger.isEmpty());
      } catch (Exception ex){
        ex.printStackTrace();
        trace(ex);
        here("error sending message");
      } catch (Error err){
        trace(err);
      }
      here(messenger.toString());
    }
    messenger.offer(new message(null,this));
  }
  boolean start(){
    if (start==true){
      return false;
    }
    else {
      return(start=true);
    }
  }
  Boolean stop(){
    if (stop==true){
      return false;
    }
    else {
      return(stop=true);
    }
  }
}
