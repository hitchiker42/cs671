package cs671;
//we really have too many import statements
import static cs671.Debug.*;
import static cs671.BoggleUtil.*;
import cs671.Player.message;
import static java.lang.Thread.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import static java.util.Collections.*;

/** A Boggle game manager.  A game manager's responsibilities
 * include: build a board, send it to connected players, check words
 * submitted by players and compute final scores.  A game manager
 * also manages timers to force games to start or to finish after a
 * given delay.
 *
 * <p>Instances of this class are thread-safe.
 *
 *
 * @author Tucker DiNapoli API and Template by Michel Charpentier
 * @version 2.0, 02/27/13
 */
@SuppressWarnings("unchecked")
public class BoggleGameManager {
  DiceBoggleBoard board;
  private ConcurrentHashMap<String,Player> players;
  private boolean running;
  boolean playing;
  boolean stopped;
  boolean alive;
  ConcurrentLinkedDeque<Object> messenger;
  BoggleDictionary dict;
  Timer timer;
  long timeLimit;
  Object stopLock;
  ReentrantLock lock;
  LinkedHashSet<String> currentPlayers;
  TreeSet<String> names;
  int stopLim;
  //we'll try this for now;
  //number of players to start/end a game;
  int startTotal;
  int startCurrent;
  int endTotal;
  int endCurrent;
  boolean server;
  TreeSet<String> currentWords;
  void main(){
    //this is a pretty lame way to do things but eh;
    //should be find for now.
    //really you got a better way?(you can't lock the
    //queue by waiting on it so I'd need to create another
    //object to wait on...unless we can wait on ourselves)
    while(alive){
      if(messenger.isEmpty()){
        //maybe
        //this.wait()
        try {
        sleep(10);
        } catch(InterruptedException ex){
        }
      }
      else{
        Object tmp = messenger.pop();
        here("parsing message");
        //would use a case, but I can't for Objects
        if(tmp instanceof BoggleMessage){
          here("Somehow recieved BoggleMessage");
          //fail somehow(unless he uses this somehow?
        } else if (tmp instanceof SocketChannel){
          //might need this
          here("connecting to Game Manager");
          SocketChannel socket=(SocketChannel)tmp;
          Player temp=new Player (socket,messenger,board);
          new Thread(temp).start();
        } else if (tmp instanceof message){
          here("Recieved message");
          message temp=(message)tmp;
          if (null==temp.message){
            here("recieved remove message");
            if(!isRegistered(temp.player.name)){
              here("Player "+temp.player.name+" not Registered");
            } else {
              remove(temp.player);
            }
          } else if (BoggleMessage.Type.PLAY==temp.message.type){
            here("recieved play message");
            if(!isRegistered(temp.player.name)){
              here("Player "+temp.player.name+" not Registered");
            } else {
              start(temp.player);
            }
          } else if (BoggleMessage.Type.WORD==temp.message.type){
            here("recieved word message");
            if(!isRegistered(temp.player.name)){
              here("Player "+temp.player.name+" not Registered");
            } else {
              submitWord(temp.player,temp.message.body);
            }
          } else if (BoggleMessage.Type.END==temp.message.type){
            here("recieved end message");
            if(!isRegistered(temp.player.name)){
              here("Player "+temp.player.name+" not Registered");
            } else {
              stop(temp.player);
            }
          } else if (BoggleMessage.Type.JOIN==temp.message.type){
            here("Recieved join Message");
            if(isRegistered(temp.player.name)){
              //error? maybe
            } else {
              players.put(temp.player.name,temp.player);
              register(temp.player);
            }
          } else {
            //unknown message type
          }
        } else {
          here("Unknown Object recieved");
          //we got an object we don't know what to do with
          //should we throw it out or raise an exception?
        }
      }
      if (interrupted()){
        synchronized(stopLock){
          try{
            here("stopped for some reason");
            stopped=true;
            while(stopped){
              stopLock.wait();
            }
          } catch (InterruptedException ex){
            alive=false;
          }
        }
      }
    }
  }
  /** Creates a new game manager.
   *
   * @param w the width of the board
   * @param h the height of the board
   * @param dict the dictionary
   * @param d the dice
   */
  public//constructor
  BoggleGameManager (int w, int h, BoggleDictionary dict, Die[] d) {
    this(w,h,dict,d,new ConcurrentLinkedDeque<Object>());
    this.server=false;
    this.lock=new ReentrantLock();
  }
  BoggleGameManager (int N, int M, BoggleDictionary dict, Die[] d,
                     ConcurrentLinkedDeque<Object> messenger){
    this.messenger=messenger;
    this.dict=dict;
    this.server=true;
    //I guess board should be assumed to be dice board
    this.board=new DiceBoggleBoard(N,M,d);
    shuffle(this.board.board,this.board.dice);
    this.players=new ConcurrentHashMap<String,Player>();
    this.startCurrent=0;this.startTotal=0;
    this.endCurrent=0;this.endTotal=0;
    this.alive=true;
    this.currentPlayers=new LinkedHashSet<String>();
    this.timer=new Timer();
    this.playing=false;
    this.names=new TreeSet<String>();
  }
  /** Whether a game is currently going on.
   * @return true iff there is a game running
   */
  public boolean
  playing () {
    return playing;
  }

  /** Whether a player is currently playing a game.
   *
   * @param p the player
   * @return true iff there is a game running and {@code p} is a
   * participant in that game
   */
  public boolean
  isPlaying (BogglePlayer p) {
    if(playing){
      try {
        return (players.get(p.getName()).playing);
          } catch (NullPointerException ex){
        return false;
      }
    } return false;
  }

  /** Adds a player.  The player is not added if its name is already in
   * use.  For convenience, name comparison is else if (insensitive (i.e.,
   * "John" and "john" are the same player).  The added player is
   * initially "passive", i.e., registered but not wanting to play a
   * game.
   *
   * @param p the player to add
   * @return true iff player {@code p} is effectively registered
   */
  public boolean
  register (BogglePlayer p) {
    if(!server){
      lock.lock();
    }
    try{
      if(!players.containsKey(p.getName())){
        players.put(p.getName(),new Player(p,messenger,board));
      }
      here("Registering Player "+p.getName());
      String name=p.getName();
      Player P=players.get(name);
      try{
        if(names.contains(name.toUpperCase())){
          here("failed to register");
          return false;
        } else {
          P.registered=true;
          startTotal+=1;
          names.add(name.toUpperCase());//to insure only
          //one instance of name regardless of capitalization
          return true;
        }
      } catch(NullPointerException ex){}
      return false;
    } finally {
      if(null!=lock){
        lock.unlock();
      }
    }
  }

  /** Removes a player.  The player can no longer use the services
   * of the server.  If all the remaining players are ready to play/end,
   * a game starts/stops.
   *
   * @param p the player to remove
   * @return true iff the player is effectively removed
   */
  public boolean
  remove (BogglePlayer p) {
    //Conditionals about weather to start a new game or not
    if(!server){
      lock.lock();
    }
    try{
      if(players.get(p.getName()).start=false){
        if(null!=players.remove(p.getName())){
          startTotal-=1;
          if(startTotal==startCurrent && startTotal>0){
            this.beginGame(false);
          }
          names.remove(p.getName().toUpperCase());
          return true;
        } else {return false;}
      } else {
        if(null!=players.remove(p.getName())){
          startTotal-=1;
          startCurrent-=1;
          names.remove(p.getName().toUpperCase());
          return true;
        } else{return false;}
      }
    } finally {
      if(null!=lock && !server){
        lock.unlock();
      }
    }
  }

  /** The board, as a string.
   * @return a string of the form {@code "<width>x<height><letters>"}.
   * Letters are listed from the top row to the bottom row and each
   * row is enumerated from left to right.
   */
  public String
  getBoardString () {
    return BoardString(board);
  }

  /** The number of connected players (whether they are currently
   * playing or not).
   */
  public int
  playerCount () {
    if(!server){
      lock.lock();
    }
    try{
      return startTotal;
    } finally{
      if(lock!=null && !server){
        lock.unlock();
      }
    }
  }

  /** Whether a player is registered with the server.
   *
   * @param p the player
   * @return true iff the player is registered
   */
  public boolean
  isRegistered (BogglePlayer p) {
    if(!server){
      lock.lock();
    }
    try{
      return players.get(p.getName()).registered;
    } catch(Exception ex){
      return false;
    } finally {
      if (null!=lock && !server){
        lock.unlock();
      }
    }
  }
  boolean
  isRegistered (String p) {
    if(players==null) {
      return false;
    }
    try{
      if(players.containsKey(p)){
        return true;
      } else {
        return false;
      }
    } catch (NullPointerException ex){
      return false;
    }
  }
  /** Submits a word.  The word is submitted in the name of player
   * {@code p}.  If there is no game on or {@code p} is not part of
   * the current game, the submission is rejected and the player is
   * notified.
   *
   * @param p the player submitting the word
   * @param word the word submitted, in upper case
   * @see BogglePlayer#sendMessage
   */
  public void
  submitWord (BogglePlayer p, String word) {
    if(!server){
      lock.lock();
    }
    try{
    String name=p.getName();
    if(!playing || !players.get(name).playing){
              players.get(name).sendMessage(new BoggleMessage
                                      (BoggleMessage.Type.TEXT,
                                       "Can't submit word now"));
    } else if (testString(word,board) && currentWords.contains(word)){//not right message
      if(players.get(name).words.add(word)){
        players.get(name).sendMessage(new BoggleMessage
                                      (BoggleMessage.Type.TEXT,
                                       "Word "+word+" accepted"));
    } else{
        players.get(name).sendMessage(new BoggleMessage
                                      (BoggleMessage.Type.TEXT,
                                       "Word "+word+" already submitted"));
      }
    } else{
      players.get(name).sendMessage(new BoggleMessage
                                    (BoggleMessage.Type.TEXT,
                                     "Word "+word+" rejected"));
    }
    }finally {
      if(null!=lock && !server){
        lock.unlock();
      }
    }
  }

  /** Indicates that player {@code p} wants to play.  All registered
   * players are notified.  If all the registered players want to
   * play, a game starts immediately.  If at least one player wants to
   * play and timers are used, a game will start after the timer delay
   * or after all other players have indicated their desire to play,
   * whichever comes first.  If the player is not registered with the
   * server of is already in the game, the player is notified and the
   * method has no effect.
   *
   * <p>If a game that does not involve the player is already on, this
   * game will have to finish first.  After it is done, a new game
   * will start when all other players are ready or a timer expires.
   * The timer is only started at the end of the current game.
   *
   * @param p the player
   * @see #setTimer
   * @see BogglePlayer#sendMessage
   */
  public void
  start (BogglePlayer p) {
    if(!server){
      lock.lock();
    }
    try{
    String name=p.getName();
    Player P=players.get(name);
    //if game running do stuff
    if(P.start()){
      startCurrent+=1;
      if (startCurrent == 1 && timeLimit>0 && startTotal!=1){
        timer=new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
              beginGame(true);
            }
          }
          ,timeLimit);
      }
      //send messages here I think
        for (Player i : players.values()){
          i.sendMessage(new BoggleMessage
                        (BoggleMessage.Type.TEXT,"Player "+name+" is ready to play."));
        }
        if(startCurrent==startTotal){
          this.beginGame(false);
        }
    }
    }  finally {
      if(null!=lock && !server){
        lock.unlock();
      }
    }
  }
  /** Starts a game.  If all the players are ready, a game starts and
   * players are notified.  If the boolean {@code force} is {@code
   * true}, a game starts even it some registered players are not
   * ready.  These players are not included in the game.  If a game is
   * actually started, the board is sent to all participating players
   * and the method returns {@code true}.
   *
   * @see BogglePlayer#sendMessage
   */
  public boolean
  beginGame (boolean force) {
    synchronized (this){
      if(!server){
        lock.lock();
      }
      try{
        if(playing){
          return false;
        }
        int temp=0;
        for (Player i : players.values()){
          if(i.start){
            currentPlayers.add(i.name);
            i.sendMessage(new BoggleMessage(BoggleMessage.Type.BOARD,""));
            temp+=1;
          } else if(!force) {
            return false;
          }
          if(!force){
            for (Player q : players.values()){
              q.sendMessage(new BoggleMessage
                            (BoggleMessage.Type.TEXT,"Lets Play!"));
              q.playing=true;
            }
          }
          currentWords=(TreeSet<String>)getWords(board,dict);
          playing=true;
          endTotal=currentPlayers.size();
          endCurrent=0;
          return true;
        }
        return false;
      } finally {
        if(null!=lock){
          lock.unlock();
        }
      }
    }
  }
  /** Indicates that player {@code p} wants to end the game.  All
   * players in the current game are notified.  If all the players
   * involved in a game indicate their desire to end the game, the
   * game finishes immediately.  Otherwise, the game finishes after
   * the timer delay, if timers are used.
   *
   * @param p the player
   * @see #setTimer
   * @see BogglePlayer#sendMessage
   */
  public void
  stop (BogglePlayer p) {
    if(!server){
      lock.lock();
    } try{
    String name=p.getName();
    Player P=players.get(name);
    if(!playing){
      return;
    }
    if(!P.playing){
      return;
    }
    if(P.stop()){
      endCurrent+=1;
      if (endCurrent == 1 && timeLimit>0 && endTotal!=1){
        timer=new Timer();
        timer.schedule(
                       new TimerTask(){
                         public void run(){
                           endGame(true);}
                       }
                       ,timeLimit);
      }
      for(Player i : players.values()){
        i.sendMessage(new BoggleMessage(BoggleMessage.Type.TEXT,
                                        "Player "+name+" ready to stop"));
      }
      P.stop=true;
      if(endCurrent==endTotal){
        this.endGame(false);
      }
    }
    }finally {
      if(null!=lock && !server){
        lock.unlock();
      }
    }
  }

  /** Ends a game.  If a game is currently running and all the players
   * are ready, the game stops and players are notified.  If the
   * boolean {@code force} is {@code true}, the game stops even it
   * some players are not ready.  If a game is actually stopped,
   * scores are sent to players and the method returns {@code true}.
   *
   * @see BogglePlayer#sendMessage
   */
  public boolean
  endGame (boolean force) {
    synchronized(this){
      if(!server){
        lock.lock();
      } try {
        if(!playing){
          return false;
        }
        //if game not going on return false
        //if game going on check if all players are ready to stop
        //unless force == true
        //send messages
        if (endTotal==1) {
          Player p=null;
          for (String i : currentPlayers){
            p=players.get(i);
          }
          for (String i : p.words){
            p.score += score(i);
          }
        } else {
          TreeMap<String,String> playerDups =new TreeMap<>();
          LinkedHashSet<String> dups=new LinkedHashSet<>();
          LinkedHashMap<String,LinkedHashSet<String>> playerWords=
            new LinkedHashMap<>();
          int i=0;
          for(String s : currentPlayers){
            playerWords.put(s,players.get(s).words);
            i++;
          }
          dups=intersect(playerWords,(String[])currentPlayers.toArray(new String[1]));
          for(String s : currentPlayers){
            Player p=players.get(s);
            p.score=0;
            for (String word : p.words){
              currentWords.remove(word);
              if (dups.contains(word)){
                if (playerDups.containsKey(word)){//should use a string builder but eh
                  playerDups.put(word,playerDups.get(word)+p.name+",");
                } else{
                  playerDups.put(word,p.name);
                }
              } else {
                p.score +=score(word);
              }
            }
            p.words=null;
          }
        }
        for (Player q : players.values()){
          ArrayList<BoggleMessage> scores=new ArrayList<>();
          scores.add(new BoggleMessage(BoggleMessage.Type.TEXT,
                                       "The player's scores are:"));
          for (Player r: players.values()){
            scores.add(new BoggleMessage(BoggleMessage.Type.TEXT,r.getName()+':'+
                                         String.valueOf(r.score)+" point(s)"));
          }
          for (BoggleMessage m : scores){
            q.sendMessage(m);
          }
          q.sendMessage(new BoggleMessage(BoggleMessage.Type.TEXT,"the following "+
                                          "words were not found by any player"+
                                          currentWords.toString()));
          q.playing=false;
          q.start=false;
          q.stop=false;
        }
        currentPlayers.clear();
        startCurrent=0;
        endCurrent=0;
        board.rattle();
        playing=false;
        timer.cancel();
        currentWords=(TreeSet<String>)(board.allWords(dict));
        return true;
      } finally {
        if(!server&& lock!=null){
          lock.unlock();
        }
      }
    }
  }
  /** Sets the delay for timers.  A value of 0 means timers are not
   * used.  Otherwise, the value becomes the delay used to start or
   * to end games when not all players are ready.
   *
   * @param seconds the new delay, in seconds
   * @return the previous timer value
   */
  public long
  setTimer (int seconds) {
    synchronized (this) {
    long temp=timeLimit;
    timeLimit=(long)seconds;
    if(seconds == 0){
      timer=null;
    }
    return temp;
    }
  }
}
