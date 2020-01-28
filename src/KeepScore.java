//read and write score into score.txt
//two lines in .txt file
//wins(first line)
//looses(second line)
import java.io.*;

public class KeepScore {

	private static PrintWriter write;
	private static BufferedReader read;
	private static PrintWriter temp;
	
	
	//init
	//reset wins and looses to 0
	public static void init() {
		 try {
			write = new PrintWriter(new FileWriter("score.txt"));
			write.println(0);
			write.println(0);
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//update by writing the updated score in temp.txt and rename to score.txt
	//so the previous score is flushed and only one .txt file will be left
	
	//update wins by one 
	public static void win() {
		try {
			temp = new PrintWriter(new FileWriter("temp.txt"));
			int newWin = getWin();
			newWin++;
			temp.println(newWin);
			temp.println(getLose());
			temp.close();
			replace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//update loose by one
	public static void lose() {
		try {
			PrintWriter temp = new PrintWriter(new FileWriter("temp.txt"));
			int newLose = getLose();
			newLose++;
			temp.println(getWin());
			temp.println(newLose);
			temp.close();
			replace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//rename temp.txt to score.txt to update the new score
	private static void replace() {
		File score = new File("score.txt");
		File temp = new File("temp.txt");
		temp.renameTo(score);
	}
	
	
	//reader prep to open file
	private static void openRead() {
		try {
			read = new BufferedReader(new FileReader("score.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//reader prep to close file
	private static void closeRead() {
		try {
			read.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//get number of wins as int
	public static int getWin() {
		openRead();
		try {
			int num = Integer.parseInt(read.readLine());
			closeRead();
			return num;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeRead();
		return -1;
	}
	
	//get number of looses as int
	public static int getLose() {
		openRead();
		try {
			read.readLine();
			int num = Integer.parseInt(read.readLine());
			closeRead();
			return num;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeRead();
		return -1;
	}

}
