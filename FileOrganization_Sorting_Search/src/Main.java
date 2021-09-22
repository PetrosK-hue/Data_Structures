import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import tuc.ece.cs102.util.StandardInputRead;

public class Main {
	
	
	
	
	  public Main() { }
	  
	  public static void main(String[] args) throws FileNotFoundException, IOException {
	  Random rand = new Random();
	  int check;
	  FileManager fm = new FileManager();
	  byte[] buffer = new byte[10];
	  byte[] info;
	  rand.nextBytes(buffer);
	  System.out.println("Running File Managing...");
	  info = fm.FileHandling("newFile.txt");
	  check = fm.CreateFile( 0, 4);
	  check = fm.OpenFile("newFile.txt");
	  check = fm.WriteBlock("newFile.txt", 2, buffer);
	  check = fm.ReadBlock("newFile.txt", 1);
	  check = fm.WriteNextBlock("newFile.txt", 2, buffer); 
	  check = fm.ReadNextBlock("newFile.txt", 1); 
	  check = fm.AppendBlock("newFile.txt", buffer);
	  check = fm.ReadPrevBlock("newFile.txt", 2); 
	  check = fm.deleteBlock("newFile.txt", 1);
	  check = fm.CloseFile("test1.txt", info); 
	  System.out.println("All done. Goodbye");
	  
	  }
	 
}
