//MineSweeper 3.0
//finished

//please look in console for debug print outs

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class MineSweeper extends JFrame implements ActionListener, MouseListener{
	
	//final int for easy, mid and hard versions of size and number of bombs
	private final int EASY_R = 8;
	private final int EASY_C = 10;
	private final int EASY_BOMB = 10;
	
	private final int MID_R = 14;
	private final int MID_C = 18;
	private final int MID_BOMB = 40;
	
	private final int HARD_R = 20;
	private final int HARD_C = 24;
	private final int HARD_BOMB = 99;
	
	//display variables 
	//will be init in constructor 
	private JFrame frame;
	private JPanel gamePan, menuPan, finishPan;
	private JButton easyBtn, midBtn, hardBtn;
	private JLabel fLbl, flag, tLbl, time, w, winLbl, l, loseLbl, finishLbl;
	
	private Timer timer;
	
	private ImageIcon zero, one, two, three, four, five, six, seven, eight, flagged, bomb, blank;
	
	private int timeCount = 0;
	
	//information about buttons stored in name
	//-1 bomb
	//0 blank tile
	//1 ~ 8 numbered tile
	private JButton[][] btns;
	
	//use -1, 0, 1 to represent status of buttons
	//0 not pressed
	//-1 flagged
	//1 pressed
	//no need to init status since all elements start with 0
	private int[][] status;
	private boolean win, lose;

	
	public MineSweeper(String type) {
		System.out.println(type);
		//init variable for display
		//in the order as they were declared
		frame = new JFrame();
		gamePan = new JPanel();
		menuPan = new JPanel();
		finishPan = new JPanel();
		easyBtn = new JButton("easy");
		midBtn = new JButton("mid");
		hardBtn = new JButton("hard");
		fLbl = new JLabel("flag");
		flag = new JLabel();
		tLbl = new JLabel("time");
		time = new JLabel("0");
		w = new JLabel("wins");
		winLbl = new JLabel(KeepScore.getWin() + "");
		l = new JLabel("losses");
		loseLbl = new JLabel(KeepScore.getLose() + "");
		finishLbl = new JLabel();
		
		//set timer to check every second
		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				timeCount++;
				time.setText("" + timeCount);
			}
			
		});
		
		//add action listener for menu buttons
		easyBtn.addActionListener(this);
		midBtn.addActionListener(this);
		hardBtn.addActionListener(this);
		
		//init frame position
		frame.setBounds(300, 20, 600, 600);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//set layout for menu bar
		menuPan.setLayout(new GridLayout(1, 11));
		
		//check for different difficulties
		if(type.equals("easy")) {
			flag.setText("" + 10);
			gamePan.setLayout(new GridLayout(8, 10));
			initAll(EASY_R, EASY_C);
			initBomb(EASY_BOMB);
			//end of easy
		}else if(type.equals("mid")) {
			flag.setText("" + 40);
			gamePan.setLayout(new GridLayout(14, 18));
			initAll(MID_R, MID_C);
			initBomb(MID_BOMB);
			//end of mid
		}else if(type.equals("hard")){
			flag.setText("" + 99);
			gamePan.setLayout(new GridLayout(20, 24));
			initAll(HARD_R, HARD_C);
			initBomb(HARD_BOMB);
			//end of hard
		}
		
		//add buttons into game panel 
		initBtns();
		for(JButton[] row : btns) {
			for(JButton jb : row) {
				gamePan.add(jb);
			}
		}
		
		//debug printout for whole game map
		for(int i = 0; i < btns.length; i++) {
			for(int j = 0; j < btns[0].length; j++) {
				System.out.print(btns[i][j].getName() + " ");
			}
			System.out.println();
		}
		
		//menu bar add in order 
		menuPan.add(easyBtn);
		menuPan.add(midBtn);
		menuPan.add(hardBtn);
		menuPan.add(fLbl);
		menuPan.add(flag);
		menuPan.add(tLbl);
		menuPan.add(time);
		menuPan.add(w);
		menuPan.add(winLbl);
		menuPan.add(l);
		menuPan.add(loseLbl);
		
		frame.add(menuPan, BorderLayout.NORTH);
		frame.add(gamePan, BorderLayout.CENTER);
		
		frame.setVisible(true);
		//set frame visible first so size of buttons are set
		//then init image with the set size
		timer.start();
		initImg();
		for(int i = 0; i < btns.length; i++) {
			for(int j = 0; j < btns[0].length; j++) {
				btns[i][j].setIcon(blank);
			}
		}
		
		//will not be used until game is over
		finishPan.setLayout(new BorderLayout());
		finishPan.add(finishLbl, BorderLayout.CENTER);
	}
	
	//
	//init
	//
	
	//set all buttons' name to 0 as blank tiles
	public void initAll(int r, int c) {
		btns = new JButton[r][c];
		status = new int[r][c];
		for(int i = 0; i < btns.length; i++) {
			for(int j = 0; j < btns[i].length; j++) {
				//0 means blank
				btns[i][j] = new JButton();
				btns[i][j].setName("0");
				btns[i][j].addMouseListener(this);
			}
		}
	}
	
	//random generate bombs and set name to -1
	public void initBomb(int mine) {
		int counter = 0;
		while(counter < mine) {
			int rdm = (int)(Math.random() * (btns.length * btns[0].length - 0)) + 0;
			int r = rdm / btns[0].length;
			int c = rdm % btns[0].length;
			//-1 means bomb
			if(!btns[r][c].getName().equals("-1")) {
				btns[r][c].setName("-1");
				counter++;
			}
		}
	}
	
	//check how many bombs are in the surrounding 8 tiles
	//return int from 0 to 8
	//will be called up in initBtns()
	public int checkSur(int r, int c) {
		try {
			if(btns[r][c].getName().equals("-1")) {
				return 1;
			}
			return 0;
		}catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}

	//init buttons, change name to 1 ~ 8 if a bomb is inthe surrounding 
	public void initBtns() {
		int counter = 0;
		for(int i = 0; i < btns.length; i++) {
			for(int j = 0; j < btns[0].length; j++) {
				if(!btns[i][j].getName().equals("-1")) {
					for(int a = -1; a < 2; a++) {
						for(int b = -1; b < 2; b++) {
							counter += checkSur(i + a, j + b);
						}
					}
					btns[i][j].setName(Integer.toString(counter));
					counter = 0;
				}
			}
		}
	}
	
	//get images with the name and resize into an image icon with the size of each tile buttons
	//will be called up in initImg()
	public ImageIcon process(String type) {
		Image img = null;
		try {
			img = ImageIO.read(getClass().getResource(type + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image resize = img.getScaledInstance(btns[0][0].getWidth(), btns[0][0].getHeight(), Image.SCALE_SMOOTH);
		return new ImageIcon(resize);
	}
	
	//init each image icon with the corresponding image
	public void initImg() {
		zero = process("0");
		one = process("1");
		two = process("2");
		three = process("3");
		four = process("4");
		five = process("5");
		six = process("6");
		seven = process("7");
		eight = process("8");
		flagged = process("flagged");
		bomb = process("bomb");
		blank = process("blank");
	}
	
	//
	//end of init
	//
	
	//action listener for menu bar(panel)
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == easyBtn) {
			frame.dispose();
			new MineSweeper("easy");
		}else if(e.getSource() == midBtn) {
			frame.dispose();
			new MineSweeper("mid");
		}else if(e.getSource() == hardBtn) {
			frame.dispose();
			new MineSweeper("hard");
		}
	}
	
	//mouse listener for game panel to distinguish left and right clicks
	//only mousePressed is used
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		//check if game is over
		if(win || lose) {
			over();
			return;

		}
		//loop through all buttons to find which is pressed 
		for(int i = 0; i < btns.length; i++) {
			for(int j = 0; j < btns[0].length; j++) {
				if(e.getSource() == btns[i][j]) {
					System.out.println("\n" + i + " " + j);
					//4 == right click
					//16 == left click
					int modifier = e.getModifiers();
					//1 means already pressed
					//return so no action will take place
					if(status[i][j] == 1) {
						break;
					}
					//right click
					if(modifier == 4) {
						System.out.println("right click");
						right(i, j);
					}else if(modifier == 16) {
						//left click
						System.out.println("left click");
						left(i, j);
					}
					//break after find the button
					break;
				}
			}
		}
	}
	
	//logic when a button is pressed by right click
	private void right(int i, int j) {
		//not flagged and have flags remain then flag the tile
		//not flagged but have no flags remain then no action will take place
		if(status[i][j] == 0 && Integer.parseInt(flag.getText()) > 0) {
			//flag
			flag.setText("" + (Integer.parseInt(flag.getText()) - 1));
			status[i][j] = -1;
			btns[i][j].setIcon(flagged);
			System.out.println("flag");
		}else if(status[i][j] == -1){
			//unflag
			flag.setText("" + (Integer.parseInt(flag.getText()) + 1));
			status[i][j] = 0;
			btns[i][j].setIcon(blank);
			System.out.println("unflag");
		}
	}

	//logic when a button is pressed by left click
	private void left(int i, int j) {
		//only proceed if not flagged or pressed
		if(status[i][j] == 0) {
			//GG if pressed on bomb(name == -1)
			if(btns[i][j].getName().equals("-1")) {
				//stop time count
				timer.stop();
				//reveal all bombs
				for(int r = 0; r < btns.length; r++) {
					for(int c = 0; c < btns[0].length; c++) {
						if(btns[r][c].getName().equals("-1")) {
							btns[r][c].setIcon(bomb);
						}
					}
				}
				//game over
				lose = true;
				//update looses in score.txt
				KeepScore.lose();
				System.out.println("lose record" + KeepScore.getLose());
				//update looses in menu bar
				loseLbl.setText(KeepScore.getLose() + "");
				System.out.println("lost");
			}else {
				reveal(i, j);
				check();
			}
		}
	}
	
	//reveal all blank tiles until meet a number tile
	//or reveal a single numbered tile
	private void reveal(int i, int j) {
		System.out.println("reveal");
		//check if [i][j] is out of bounds
		if(i < 0 || i > btns.length - 1 || j < 0 || j > btns[0].length - 1) {
			System.out.println("out of bounds");
			return;
		}
		//check if [i][j] is already visited
		if(status[i][j] == 1) {
			System.out.println("already visited");	
			return;
		}
		
		//passed check 
		//reveal tiles
		
		//if a tile is flagged 
		//it still will reveal after blank tile is pressed and the flagged tile in the surrounding 
		//flag number goes up by one
		//this will not happen if a numbered tile is clicked, since the the situation is eliminated in left()
		if(status[i][j] == -1) {
			flag.setText("" + (Integer.parseInt(flag.getText()) + 1));
		}
		
		//mark as revealed
		status[i][j] = 1;
		//change image icon on buttons accroding to their type
		int type = Integer.parseInt(btns[i][j].getName());
		if(type == 0) {
			btns[i][j].setIcon(zero);
		}else if(type == 1) {
			btns[i][j].setIcon(one);
		}else if(type == 2) {
			btns[i][j].setIcon(two);
		}else if(type == 3) {
			btns[i][j].setIcon(three);
		}else if(type == 4) {
			btns[i][j].setIcon(four);
		}else if(type == 5) {
			btns[i][j].setIcon(five);
		}else if(type == 6) {
			btns[i][j].setIcon(six);
		}else if(type == 7) {
			btns[i][j].setIcon(seven);
		}else if(type == 8) {
			btns[i][j].setIcon(eight);
		}
		
		//if is not blank(0) -> return
		if(type != 0) {
			System.out.println("number tile");
			return;
		}
		
		//if is blank(0) then reveal surroundings
		for(int r = -1; r < 2; r++) {
			for(int c = -1; c < 2; c++) {
				reveal(i + r, j + c);
			}
		}
	}
	
	//check if all tiles, both numbered and blank ones are revealed 
	private void check() {
		int counter = 0;
		int bomb = 0;
		for(int i = 0; i < btns.length; i++) {
			for(int j = 0; j < btns[0].length; j++) {
				//count how many bomb are in the map
				if(btns[i][j].getName().equals("-1")) {
					bomb++;
					continue;
				}
				//count how many non bomb tiles are revealed
				if(status[i][j] == 1) {
					counter++;
				}
			}
		}
		
		//win if total tiles - number of bombs == revealed tiles
		if(counter == btns.length * btns[0].length - bomb) {
			timer.stop();
			win = true;
			KeepScore.win();
			winLbl.setText(KeepScore.getWin() + "");
		}
	}
	
	//handle finish panel
	private void over(){
		try {
			Thread.sleep(1000);
			gamePan.setVisible(false);
			frame.setVisible(false);
			frame.add(finishPan, BorderLayout.CENTER);
			frame.setVisible(true);			
			Image img = null;
			try {
				if(lose) {
					img = ImageIO.read(getClass().getResource("lose.jpg"));
				}else {
					img = ImageIO.read(getClass().getResource("win.jpg"));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Image resize = img.getScaledInstance(finishLbl.getWidth(), finishLbl.getHeight(), Image.SCALE_SMOOTH);
			finishLbl.setIcon(new ImageIcon(resize));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
