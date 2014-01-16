package cs671;

import java.util.Set;
import java.io.InputStream;//Don't need for my code
import java.util.Scanner;//Don't need for my code
import static cs671.Debug.*;
import static cs671.BoggleUtil.*;
import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.net.InetSocketAddress;
import java.io.IOException;

/** A multi-threaded server for the Boggle game.  This server
 * implements the server side of the Boggle protocol described below.
 *
 * <p>Messages are made of 2 parts: a header and a body, separated by
 * a colon (:).  There are 6 different headers: <tt>BOARD</tt>,
 * <tt>TEXT</tt>, <tt>JOIN</tt>, <tt>PLAY</tt>, <tt>WORD</tt> and
 * <tt>END</tt>.</p>
 *
 * <p>From server to client:
 * <ul>
 * <li><tt><b>BOARD</b></tt>: the board (letter grid) at the beginning
 * of a new game.  The body of this message is structured as follows:
 * <tt>&lt;width&gt;x&lt;height&gt;&lt;LETTERS&gt;</tt>.  Letters are
 * uppercase and are listed top-to-bottom and, within each row,
 * left-to-right (like English reading).</li>
 *
 * <li><tt><b>TEXT</b></tt>: a string of text, which represents
 * information sent by the server to a player.  The body of the
 * message is the string.  It cannot contain newlines.</li>
 * </ul></p>
 *
 * <p>From client to server:
 * <ul>
 * <li><tt><b>JOIN</b></tt>: the player wants to join the server.  The
 * body of the message is the player's name.  It cannot be an empty
 * string.  This must be the first message sent by a client to the
 * server to initiate a connection.</li>
 *
 * <li><tt><b>PLAY</b></tt>: the player is ready to play.  The body of
 * this message is an empty string.  A game starts when all the
 * players are ready, or after a timeout.</li>
 *
 * <li><tt><b>WORD</b></tt>: the player submits a word to the server.
 * The body of the message is the word being submitted, in uppercase.
 * A player cannot submit words before a game has started.</li>
 *
 * <li><tt><b>END</b></tt>: the player is ready to end the game.  The
 * body of this message is an empty string.  A game ends when all the
 * players are ready to end, or after a timeout.</li>
 * </ul>
 * </p>
 *
 * @author Michel Charpentier extended by Tucker DiNapoli
 * @version 2.0, 02/27/13
 */
public class BoggleServer {
  private static final int defaultPort=58839;
  ServerSocketChannel listener;
  BoggleGameManager manager;
  int port;
  ConcurrentLinkedDeque<Object> messenger;
  Listener serv;
  final Object listenerStopLock=new Object();
  final Object managerStopLock=new Object();
  boolean started;

  class Listener extends Thread {
    ServerSocketChannel listener;
    ConcurrentLinkedDeque<Object> messenger;
    boolean alive=true;
    boolean stopped=false;
    Listener(ServerSocketChannel listener, ConcurrentLinkedDeque<Object> messenger){
      this.listener=listener;
      this.messenger=messenger;
    }
    public void run(){
      while(alive){
        try{
        SocketChannel client=listener.accept();
        messenger.addFirst(client);
        here("client"+String.valueOf(client)+"added");
        } catch (ClosedByInterruptException ex) {
          synchronized(listenerStopLock){
            try{
              stopped=true;
              while(stopped){
                listenerStopLock.wait();
              }
            } catch (InterruptedException ext){
              alive=false;}
          }
        } catch (IOException ext2){}
      }
      try{
      listener.close();
      } catch (IOException ext3){}
    }
  }

  /*
    Here's how this works:
      Start with a server socket, using ServerSocket(Channel)
      this is the listening thread, it listens for clients
      when a client is found we create a new socket for said
      client. Then we create a new thread for that socket, this
      new thread handles read/write with the client.
      Then the listening thread goes back to listening.

      The trick is we need to communcate with clients, so we use
      a shared non blocking queue or something to transfer
      data. In this case we'll do that with the BoggleGameManager,
      which is in its own thread.
   */
  /** Creates a server
   *
   * @param port the port number ([0..65535])
   * @param g a game manager
   */
  public //constructor
  BoggleServer (int port, BoggleGameManager g) {
    try{
    this.listener=(ServerSocketChannel.open()).bind(new InetSocketAddress(port));//need to add hostname
    } catch(IOException ex){};
    this.manager=g;
    this.manager.server=true;
    this.manager.lock=null;
    this.messenger=this.manager.messenger;
    this.manager.stopLock=this.managerStopLock;
    this.port=port;
  }

  /** Starts listening and accepting connections. */
  public void
  start () throws java.io.IOException {
    if(!started){
    //start game manager
      new Thread( new Runnable(){
        public void
        run (){
          manager.main();
        }
      }).start();
      //Start listener
      serv=new Listener(listener,messenger);
      serv.start();
    }
    if(serv.stopped){
      synchronized(managerStopLock){
        managerStopLock.notifyAll();
      }
    }
    if(manager.stopped){
      synchronized(listenerStopLock){
        listenerStopLock.notifyAll();
      }
    }
      //if started and not stopped
      //? throw an exception?
    }

  /** Stops the server.  The server can later be restarted. */
  public void
  stop () {
    serv.interrupt();
    manager.alive=false;
  }

  private static void
  usage () {
    System.out.println
      ("port number required, followed by options:\n"+
       "-size <number> : creates a square board\n"+
       "-size <number>x<number> : creates a rectangular board\n"+
       "-length <number> : minimal length for valid words\n"+
       "-dict <file> : dictionary filename\n"+
       "-dice <file> : dice definition filename\n"+
       "-time <time> : timers, in seconds\n\n"+
       "default is: "+
       "-size 4 -length 3 -dict words.txt -dice dice.txt -time 180");
  }

  private final static java.util.regex.Pattern gridSize =
    java.util.regex.Pattern.compile("(?i:([0-9]+)x([0-9]+))");

  /** Starts the server with a new game manager.  The first
   * command-line argument is the port number to listen to.  It can
   * be followed by several options:
   * <pre>
   -size &lt;number&gt; : creates a square board
   -size &lt;number&gt;x&lt;number&gt; : creates a rectangular board
   -length &lt;number&gt; : minimal length for valid words
   -dict &lt;file&gt; : dictionary filename
   -dice &lt;file&gt; : dice definition filename
   -time &lt;time&gt; : timers, in seconds
   default is: -size 4 -length 3 -dict words.txt -dice dice.txt -time 180
   </pre>
   * @see BoggleGameManager
   */
  public static void
  main (String[] args) {
    int width = 4;
    int height = 4;
    String dictFile = "/words.txt";
    String diceFile = "/dice.txt";
    int minLength = 3;
    int time = 180;
    BoggleDictionary dict;
    Die[] dice;
    if (args.length < 1) {
      usage();
      return;
    }
    int port;
    try {
      port = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      usage();
      return;
    }
    for (int i=1; i<args.length; i++) {
      try {
        if (args[i].equals("-help")) {
          usage();
          return;
        }
        if (args[i].equals("-time")) {
          try {
            time = Integer.parseInt(args[++i]);
          } catch (NumberFormatException e) {
            System.err.printf("Unrecognized time; using %d%n", time);
          }
          continue;
        }
        if (args[i].equals("-size")) {
          String size = args[++i];
          try {
            java.util.regex.Matcher m = gridSize.matcher(size);
            if (m.matches()) {
              width = Integer.parseInt(m.group(1));
              height = Integer.parseInt(m.group(2));
            } else {
              width = height = Integer.parseInt(size);
            }
          } catch (NumberFormatException e) {
            System.err.printf("Unrecognized size; using %dx%d%n"
                              , width, height);
          }
          continue;
        }
        if (args[i].equals("-dict")) {
          dictFile = args[++i];
          continue;
        }
        if (args[i].equals("-dice")) {
          diceFile = args[++i];
          continue;
        }
        if (args[i].equals("-length")) {
          try {
            minLength = Integer.parseInt(args[++i]);
          } catch (NumberFormatException e) {
            System.err.printf("Unrecognized length; using %d%n", minLength);
          }
          continue;
        }
        System.err.printf("Unknown option: %s%n", args[i]);
      } catch (IndexOutOfBoundsException e) {
        System.err.printf("Incomplete option: %s%n", args[i-1]);
        break;
      }
    }
    try {
      InputStream dictStream
        = SingleBoggleGame.class.getResourceAsStream(dictFile);
      if (dictStream == null)
        dictStream = new java.io.FileInputStream(dictFile);
      Set<String> words = new java.util.HashSet<>();
      Scanner in = new Scanner(dictStream);
      while (in.hasNext()) {
        String w = in.next();
        if (w.length() >= minLength)
          words.add(w.toUpperCase());
      }
      in.close();
      dict = new BoggleDictionary(words);
    } catch (java.io.IOException e) {
      System.err.printf("Cannot open dictionary file: %s%n", e.getMessage());
      return;
    }
    try {
      InputStream diceStream
        = SingleBoggleGame.class.getResourceAsStream(diceFile);
      if (diceStream == null)
        diceStream = new java.io.FileInputStream(diceFile);
      dice = Die.makeDice(new java.io.InputStreamReader(diceStream));
    } catch (java.io.IOException e) {
      System.err.printf("Cannot open dice file: %s%n", e.getMessage());
      return;
    }
    System.out.printf("Dictionary has %d words.%n", dict.size());
    BoggleGameManager game = new BoggleGameManager(width, height, dict, dice);
    game.setTimer(time);
    BoggleServer server = new BoggleServer(port, game);
    try {
      server.start();
    } catch (java.io.IOException e) {
      System.err.printf("Cannot start server: %s%n", e.getMessage());
    }
  }
}
