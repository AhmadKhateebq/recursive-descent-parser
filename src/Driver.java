import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) throws ParseException, FileNotFoundException {
        //the path to your file that you want to patse 
        String URL = "C:\\Users\\Ahmad Khateeb\\Downloads\\whatever.txt";
        File file = new File (URL);
        Scanner scanner = new Scanner (file);
        StringBuilder sb = new StringBuilder ();
        while(scanner.hasNextLine ()){
            sb.append (scanner.nextLine ()).append ("\n");
        }
        System.out.println (CustomParser.parseCode (sb.toString ()));
    }
}
