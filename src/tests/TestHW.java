

package tests;

import charpov.grader.*;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

class TestHW{
    public static void main (String[] args) throws Exception {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
	boolean first = true;
	int hwnum = -1;
	for( String clazz : args){
	    if ( first ){
		hwnum = Integer.parseInt(clazz);
		first = false;
		continue;
	    }
            if ( clazz.equals("SampleTests1") ) continue;
            if ( clazz.equals("TestHW")) continue;
	    if ( clazz.equals("TestTester")) continue;
	    if ( clazz.equals("TestTimers")) continue;
	    if ( clazz.equals("NewTimer")) continue;


            list.add(Class.forName("tests."+clazz));
        }
        java.util.logging.Logger.getLogger("charpov.grader")
          .setLevel(java.util.logging.Level.WARNING);
	Class<?>[] testList = new Class<?>[list.size()];
        list.toArray(testList);
        Tester tester = new Tester(testList);
	if ( hwnum == 2 || hwnum == 3 || hwnum == 4 ){
	    tester.setConcurrencyLevel(1);
	}
	FileOutputStream fos = new FileOutputStream("../run" + hwnum + ".out");
        tester.setOutputStream(fos);
        double result = tester.call();
        PrintWriter pw = new PrintWriter(fos, true);
        pw.printf("Total: %2.3f/%s%n", result * tester.getTotalPoints(), 100);
    }
}
