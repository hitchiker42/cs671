package cs671;
import java.awt.event.*;
import java.util.*;
import static java.lang.Thread.sleep;
import static cs671.Debug.*;
/** Graphical representation of a binary clock.
 *
 * @author  Michel Charpentier
 * @version 3.1, 02/12/13
 * @see Clock
 */
public class GraphicsClock extends javax.swing.JComponent implements Observer {

  private static final long serialVersionUID = -4385200557405026128L;

  private final Dot[] dots;
  private final Clock clock;

  /** Builds a graphical representation of the given clock.
   *
   * @param clock the clock to be displayed
   * @param width the width of the component
   */
  public GraphicsClock (Clock clock, int width) {
    this.clock = clock;
    this.clock.addObserver(this);
    assert(this.clock.countObservers()>=1);
    int nbBits = clock.size();
    dots = new Dot[nbBits];
    double r = (width - 10) / (2.25 * nbBits);
    if (r < 10) r = 10;
    double y = r * 1.25;
    double x = 2.25 * r;
    for (int i=0; i<dots.length; i++) {
      Dot d = dots[i] = new Dot(r*1.25+i*x, y, r);
      if (clock.getBit(nbBits - i - 1))
        d.set();
    }
    setPreferredSize(new java.awt.Dimension(width,(int)(1.1*width/nbBits)));
    addMouseListener(new ClockListener());
  }

  /** Paints the clock as a line of big dots.
   * @see <a href="Dot.java">Dot.java</a>
   */
  protected void paintComponent (java.awt.Graphics  g) {
    for (Dot dot : dots)
      dot.paint(g);
  }
  class ClockListener implements MouseListener{
    //This V is why interfaces are a ridculous concept
    ClockListener(){};
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseClicked(MouseEvent e){
      switch(e.getButton()) {
      case(1):
        if (e.getClickCount()==1){
          /*if(e.isControlDown()){
            if(clock.isTicking()){
              clock.stop();
            } else{
              clock.start();
            }
            break;
          }
          if(e.isAltDown()){
            clock.setDirection((clock.getDirection()==Clock.Direction.FORWARD)?Clock.Direction.BACKWARD:Clock.Direction.FORWARD);
            break;
            }*/
          for (int i=0;clock.size()>i;i++){
            if (dots[i].contains(e.getX(),e.getY())){
              clock.nextBit(i);
              break;
            }
          }
        } else if (e.getClickCount()==2){
          if (e.isShiftDown()){
            long temp=Clock.time();
            if (Math.abs(temp)>Math.pow(2,(clock.size()-1)-1)){
              if(clock.size()>=32){
                temp=(long)((int)temp);}
              else if(clock.size()>=16){
                temp=(long)((short)temp);}
              else if(clock.size()>=8){
                temp=(long)((byte)temp);
              } else{temp=0;}
            }
            clock.setLongValue(temp);
          } else{
            clock.clear();
          }
        } else{}
        break;
      case(2):
        clock.setDirection((clock.getDirection()==Clock.Direction.FORWARD)?Clock.Direction.BACKWARD:Clock.Direction.FORWARD);
        break;
      case(3):
        if(clock.isTicking()){
          clock.stop();
        } else{
          clock.start();
        }
        break;
      }
    }
  }
  @Override
  public synchronized void update(Observable o, Object arg){
    Clock c=null;BitSet b=null;
    try{
      c=(Clock)o;
      b=(BitSet)arg;
    } catch(ClassCastException ex)
      {}
    for (int i=b.nextSetBit(0);i>=0;i=b.nextSetBit(i+1)){
      dots[i].flip();
    }
    this.repaint();
  }
}
