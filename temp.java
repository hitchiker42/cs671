import static java.util.Arrays.*;
import java.util.List;
import java.nio.*;
public class temp {
	temp(){}
	public static void main (String[] args){
		ByteBuffer bb=ByteBuffer.allocate(64);
  		try{
    			System.in.read(bb.array());
			bb.flip();
			String string = new String(bb.array());
			System.out.print(string);
		} catch (Exception ex){
	System.out.println("Caught IO Error");}
	String[] strings={"this","is","an","array","of","strings"};
	char[][] chars=new char[strings.length][];
	for(int i=0;i<strings.length;i++){
		chars[i]=strings[i].toCharArray();
	}
	for(char[] i : chars){
	System.out.print(new String (i));
}
	System.out.println();
}
}