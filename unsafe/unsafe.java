import sun.misc.*;
import java.lang.reflect.*;
@SuppressWarnings("Unsafe")
public class unsafe{
  public static Unsafe getUnsafe() {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      return (Unsafe)f.get(null);
    } catch (Exception e) {
      System.err.println("FAIL");
      System.exit(1);}
    return null;
  }
  
  public static long sizeOf(Object object) {
    Unsafe unsafe = getUnsafe();
    return unsafe.getAddress( normalize( unsafe.getInt(object, 8L) ) + 24L );
  }
  public static long normalize(int value) {
    if(value >= 0) return value;
    return (~0L >>> 64) & value;
  }
  public static void main (String[] args){
    Unsafe unsafe = getUnsafe();
    long addr=unsafe.allocateMemory(64L);
    unsafe.putLong(addr,42L);
    System.out.println(String.format("Value of long: %d",
                                     unsafe.getLong(addr)));
    unsafe.putDouble(addr+8L,3.14159);
    System.out.println(String.format("Value of double: %f",
                                     unsafe.getDouble(addr+8L)));
    unsafe.putInt(addr+16L,1718);
    System.out.println(String.format("Value of int: %d",
                                     unsafe.getInt(addr+16L)));
    unsafe.putChar(addr+20L,'?');
    System.out.println(String.format("Value of char: %c",
                                     unsafe.getChar(addr+20L)));
    unsafe.freeMemory(addr);
    unsafe.putInt(addr+16L,1718);
    System.out.println(String.format("Value of undef: %d",
                                     unsafe.getAddress(addr+128L)));
    return;
  }
}