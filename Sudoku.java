package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

public class Sudoku {
   private final Cell[][] board; // Stores all the cells of a sudoku board

   public Sudoku(String startingConfig) {
      board = new Cell[9][9];

      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            board[j][i] = new Cell(j, i);
         }
      }

      if (startingConfig.length() != 81) return;

      // Removes unwanted characters
      startingConfig = startingConfig.replaceAll("[^0-9--]", "");
      char current;
      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            current = startingConfig.charAt((9 * i) + j); // Gets the next char in the string
            if (current == '-') {
               board[j][i] = new Cell(j, i);
            } else if (Character.getNumericValue(current) > 0
                    && Character.getNumericValue(current) < 10) { // Checks if the char is a number
               ArrayList<Integer> possibleValues = getPossibleValuesFor(board[j][i]);
               if (possibleValues.contains(Character.getNumericValue(current))) // Only allows valid values
                  board[j][i] = new Cell(j, i, Character.getNumericValue(current));
               else
                  board[j][i] = new Cell(j, i);
            }
         }
      }
   }

   public static void start() { // Launches a sudoku GUI
      new SudokuPanel();
   }

   public static void start(String startingConfig) { // Launches a sudoku GUI with a preloaded board state
      new SudokuPanel(startingConfig);
   }

   public void solve() { // Solves the current board state
      ArrayList<Integer> X = new ArrayList<>(); // An array of all the x values of blank cells
      ArrayList<Integer> Y = new ArrayList<>(); // An array of all the y values of blank cells
      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            if (board[j][i].getValue() == 0) { // If blank, store x and y
               X.add(j);
               Y.add(i);
            }
         }
      }

      ArrayList<ArrayList<Integer>> cants = new ArrayList<>(); // Blacklisted values for each blank cell
      for (int i = 0; i < X.size(); i++) {
         cants.add(new ArrayList<>());
      }
      try { // If the configuration is invalid and it backtracks too far it wont break but stop
         for (int i = 0; i < X.size(); i++) { // Goes thought all blank cells
            // All the possible values based on the current board state
            ArrayList<Integer> possibleValues = getPossibleValuesFor(board[X.get(i)][Y.get(i)]);
            possibleValues.removeAll(cants.get(i)); // Removes blacklisted values

            if (possibleValues.size() > 0) { // If there are values to choose from do...
               Collections.sort(possibleValues); // Sort by smallest to greatest
               board[X.get(i)][Y.get(i)].setValue(possibleValues.get(0)); // Chooses smallest
               continue;
            }

            // If there are no values to be chosen from aka, it has hit a dead end and
            // needs to backtrack
            board[X.get(i)][Y.get(i)].setValue(0); // 'Blanks' the cell

            for (int j = i; j < cants.size(); j++) { // Clears blacklists for cells after and including it
               cants.set(j, new ArrayList<>());
            }

            i--; // Moves back a cell
            if (!cants.get(i).contains(board[X.get(i)][Y.get(i)].getValue()))
               cants.get(i).add(board[X.get(i)][Y.get(i)].getValue()); // Adds its current value to
            // its blacklist
            i--; // Subs one to counteract the increment

            // This will go until the last cell is set
         }

         print(); // Once finished prints out the solved board
      } catch (Exception e) {
         JOptionPane.showMessageDialog(null, "Invalid Board");
      }
   }

   public void print() { // Displays to the console the current board state
      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            System.out.print(" " + board[j][i].getValue() + " ");
         }
         System.out.println();
      }
   }

   private ArrayList<Integer> getPossibleValuesFor(Cell cell) {
      ArrayList<Integer> Remainders = new ArrayList<>();
      Remainders.add(1); // Values which appear in
      Remainders.add(2); // the same block
      Remainders.add(3); // the same row
      Remainders.add(4); // or column
      Remainders.add(5); // are removed from this list
      Remainders.add(6); // filtering the possible values
      Remainders.add(7);
      Remainders.add(8);
      Remainders.add(9);

      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) { // Looks at every cell
            if (board[j][i].getRowBlock() == cell.getRowBlock() // If it shares the same block
                    && board[j][i].getColumnBlock() == cell.getColumnBlock() // --------------
                    && !(j == cell.getX() && i == cell.getY()) // Checks that its a different cell
                    && board[j][i].getValue() != 0 // And its not blank
                    && Remainders.contains(board[j][i].getValue())) //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
               Remainders.remove((Integer) board[j][i].getValue()); // Filters that value
         }
      }

      for (int i = 0; i < 9; i++) {
         if (board[i][cell.getY()].getValue() != 0 // Makes sure its not blank
                 && i != cell.getX() // and is not the same cell
                 && Remainders.contains(board[i][cell.getY()].getValue())) //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            Remainders.remove((Integer) board[i][cell.getY()].getValue()); // Filters that value
      }

      for (int i = 0; i < 9; i++) {
         if (board[cell.getX()][i].getValue() != 0 // Makes sure its not blank
                 && i != cell.getY() // and is not the same cell
                 && Remainders.contains(board[cell.getX()][i].getValue())) //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            Remainders.remove((Integer) board[cell.getX()][i].getValue()); // Filters that value
      }

      return Remainders;
   }

   // This is a JPanel class which is for the GUI
   private static class SudokuPanel extends JPanel implements KeyListener, MouseListener {
      private int focusX; // Selected cell's x
      private int focusY; // Selected cell's y
      private Cell[][] board; // Sudoku board
      private JFrame jFrame; // The window itself
      private final int TIME = 5; // The time between the next cell
      private boolean interactive; // Whether the user can value cells or solve
      private boolean reset = false; // A variable to store whether the threads should remain running

      public SudokuPanel() {
         init();
      }

      public SudokuPanel(String startingConfig) {
         jFrame = new JFrame("Sudoku solver"); // The window setup
         jFrame.setSize(613 + 20, 637 + 20); //-----------
         jFrame.setLocationRelativeTo(null); //-----------------------
         jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //----
         jFrame.setResizable(false); //-------------------------------
         jFrame.add(this); //-----------------------------------------

         this.setBounds(10, 10, 600, 600); // Panel setup
         this.setFocusable(true); //--------------------------------------
         this.removeKeyListener(this); //------------------------------
         this.removeMouseListener(this); //----------------------------
         this.addKeyListener(this); //---------------------------------
         this.addMouseListener(this); //-------------------------------

         jFrame.setVisible(true); // Shows the window

         focusX = 0;
         focusY = 0;

         setInteractive(true);

         board = new Cell[9][9]; // Board setup

         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               board[j][i] = new Cell(j, i);
            }
         }

         if (startingConfig.length() != 81) {
            new SwingWorker<>() {
               @Override
               protected Object doInBackground() throws Exception {
                  while (true) {
                     if (false) break;
                     repaint(); // Paints the sudoku board every 'frame'
                  }
                  return null;
               }
            }.execute();
            return;
         }

         startingConfig = startingConfig.replaceAll("[^0-9--]", "");
         char current;
         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               current = startingConfig.charAt((9 * i) + j);
               if (current == '-') {
                  board[j][i] = new Cell(j, i);
               } else if (Character.getNumericValue(current) > 0 && Character.getNumericValue(current) < 10) {
                  ArrayList<Integer> possibleValues = getPossibleValuesFor(board[j][i]);
                  if (possibleValues.contains(Character.getNumericValue(current))) // Only allows valid values
                     board[j][i] = new Cell(j, i, Character.getNumericValue(current));
                  else
                     board[j][i] = new Cell(j, i);
               }
            }
         }


         new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
               while (true) {
                  if (false) break;
                  repaint(); // Paints the sudoku board every 'frame'
               }
               return null;
            }
         }.execute();
      }

      private void init() {
         // Same window setup as before
         jFrame = new JFrame("Sudoku solver");
         jFrame.setSize(613 + 20, 637 + 20);
         jFrame.setLocationRelativeTo(null);
         jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         jFrame.setResizable(false);
         jFrame.add(this);

         this.setBounds(10, 10, 600, 600);
         this.setFocusable(true);
         this.removeKeyListener(this);
         this.removeMouseListener(this);
         this.addKeyListener(this);
         this.addMouseListener(this);

         jFrame.setVisible(true);

         board = new Cell[9][9];

         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               board[j][i] = new Cell(j, i); // Fresh cells initialised
            }
         }

         focusX = 0;
         focusY = 0;

         setInteractive(true);

         new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
               while (true) {
                  if (false) break;
                  repaint();
               }
               return null;
            }
         }.execute();
      }

      public void paintComponent(Graphics g) { // Where the game is drawn
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D) g;

         // Background
         g2.fillRect(0, 0, 620, 620);
         g2.translate(10, 10);
         g2.setColor(Color.LIGHT_GRAY);
         g2.fillRect(0, 0, 600, 600);

         // Cell lines
         g2.setColor(Color.GRAY.brighter());
         g2.setStroke(new BasicStroke(2));
         for (int i = 1; i < 9; i++) {
            if (i == 3 || i == 6) continue;
            g2.drawLine((int) (i * (200.0 / 3.0)), 5, (int) (i * (200.0 / 3.0)), 595);
         }
         for (int i = 1; i < 9; i++) {
            if (i == 3 || i == 6) continue;
            g2.drawLine(5, (int) (i * (200.0 / 3.0)), 595, (int) (i * (200.0 / 3.0)));
         }

         // Selected cell highlight
         g2.setColor(Color.GRAY.brighter());
         g2.fillRect((int) (focusX * (200.0 / 3.0)), (int) (focusY * (200.0 / 3.0)),
                 (int) (200.0 / 3.0), (int) (200.0 / 3.0));

         // Cell values drawn
         g2.setColor(Color.WHITE);
         g2.setFont(new Font("Lucida Console", Font.PLAIN, 32));
         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               if (board[j][i].getValue() == 0 || board[j][i].getValue() > 9) continue;
               if (board[j][i].isPreset()) {
                  g2.setColor(Color.DARK_GRAY);
               } else {
                  g2.setColor(Color.WHITE);
               }
               g2.drawString(String.valueOf(board[j][i].getValue()),
                       25 + (int) (j * (200.0 / 3.0)), 45 + (int) (i * (200.0 / 3.0)));
            }
         }

         // Block lines
         g2.setColor(Color.GRAY);
         g2.setStroke(new BasicStroke(3));
         g2.drawLine(200, 10, 200, 590);
         g2.drawLine(400, 10, 400, 590);
         g2.drawLine(10, 200, 590, 200);
         g2.drawLine(10, 400, 590, 400);

      }

      private ArrayList<Integer> getPossibleValuesFor(Cell cell) { // Same as before
         ArrayList<Integer> Remainders = new ArrayList<>();
         Remainders.add(1);
         Remainders.add(2);
         Remainders.add(3);
         Remainders.add(4);
         Remainders.add(5);
         Remainders.add(6);
         Remainders.add(7);
         Remainders.add(8);
         Remainders.add(9);

         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               if (board[j][i].getRowBlock() == cell.getRowBlock()
                       && board[j][i].getColumnBlock() == cell.getColumnBlock()
                       && !(j == cell.getX() && i == cell.getY())
                       && board[j][i].getValue() != 0
                       && Remainders.contains(board[j][i].getValue()))
                  Remainders.remove((Integer) board[j][i].getValue());
            }
         }

         for (int i = 0; i < 9; i++) {
            if (board[i][cell.getY()].getValue() != 0
                    && i != cell.getX()
                    && Remainders.contains(board[i][cell.getY()].getValue()))
               Remainders.remove((Integer) board[i][cell.getY()].getValue());
         }

         for (int i = 0; i < 9; i++) {
            if (board[cell.getX()][i].getValue() != 0
                    && i != cell.getY()
                    && Remainders.contains(board[cell.getX()][i].getValue()))
               Remainders.remove((Integer) board[cell.getX()][i].getValue());
         }

         return Remainders;
      }

      private void solve() { // Same as before with minor changes
         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               board[j][i] = new Cell(j, i, board[j][i].getValue());
            }
         }

         ArrayList<Integer> X = new ArrayList<>();
         ArrayList<Integer> Y = new ArrayList<>();
         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
               if (board[j][i].getValue() == 0) {
                  X.add(j);
                  Y.add(i);
               }
            }
         }

         ArrayList<ArrayList<Integer>> cants = new ArrayList<>();
         for (int i = 0; i < X.size(); i++) {
            cants.add(new ArrayList<>());
         }

         try {
            for (int i = 0; i < X.size(); i++) {
               if (reset) {
                  reset = false;
                  return;
               }

               ArrayList<Integer> possibleValues = getPossibleValuesFor(board[X.get(i)][Y.get(i)]);
               possibleValues.removeAll(cants.get(i));

               if (possibleValues.size() > 0) {
                  Collections.sort(possibleValues);
                  board[X.get(i)][Y.get(i)].setValue(possibleValues.get(0));
                  repaint();

                  try {
                     Thread.sleep(TIME); // Pauses a short while
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }

                  continue;
               }

               board[X.get(i)][Y.get(i)].setValue(0);
               repaint();

               try {
                  Thread.sleep(TIME); // Pauses a short while
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }

               for (int j = i; j < cants.size(); j++) {
                  cants.set(j, new ArrayList<>());
               }

               i--;
               if (!cants.get(i).contains(board[X.get(i)][Y.get(i)].getValue()))
                  cants.get(i).add(board[X.get(i)][Y.get(i)].getValue());
               i--;
            }
         } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Board");
         }
      }

      private void addListener() { // For re-adding intractability
         this.addKeyListener(this);
         this.addMouseListener(this);
      }

      public boolean isReset() {
         return reset;
      }

      public void setReset(boolean reset) {
         this.reset = reset;
      }

      private boolean isInteractive() {
         return interactive;
      }

      private void setInteractive(boolean isInteractive) {
         interactive = isInteractive;
      }

      @Override
      public void keyPressed(KeyEvent e) {
         switch (e.getKeyCode()) { // Allows the selected cell to move moved with 'wasd' or arrow keys
            case KeyEvent.VK_UP, KeyEvent.VK_W -> {
               focusY--;
               if (focusY < 0) focusY = 8;
            }

            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
               focusY++;
               if (focusY > 8) focusY = 0;
            }

            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
               focusX--;
               if (focusX < 0) focusX = 8;
            }

            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
               focusX++;
               if (focusX > 8) focusX = 0;
            }
         }
         if (isInteractive()) {

            if (Character.getNumericValue(e.getKeyChar()) > 0 // If key pressed is a number between 1 and 9
                    && Character.getNumericValue(e.getKeyChar()) <= 9) { //--------------------------------
               ArrayList<Integer> possibleValues = getPossibleValuesFor(board[focusX][focusY]);
               if (possibleValues.contains(Character.getNumericValue(e.getKeyChar())))
                  board[focusX][focusY] = new Cell(focusX, focusY,
                          Character.getNumericValue(e.getKeyChar())); // Overwrites that cell
               // with its new value
            }

            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
               board[focusX][focusY] = new Cell(focusX, focusY); // 'Deletes' a cell
            }

            if (e.getKeyChar() == '\n') { // If enter pressed...
               setInteractive(false);
               new SwingWorker<>() {
                  @Override
                  protected Object doInBackground() throws Exception {
                     solve();
                     setInteractive(true); // Re-adds intractability
                     return null;
                  }
               }.execute();
            }
         }

         if (e.getKeyChar() == ' ') { // If space pressed...
            jFrame.dispose(); // Close the current window and
            init();           // open a fresh one
            reset = true; // Stops the running thread
         }
      }

      @Override
      public void keyTyped(KeyEvent e) {

      }

      @Override
      public void keyReleased(KeyEvent e) {

      }

      @Override
      public void mouseClicked(MouseEvent e) {

      }

      @Override
      public void mousePressed(MouseEvent e) { // If a cell is clicked select it
         int newX = (int) ((e.getX() - 10) / (200.0 / 3.0)); // Works out which cell
         int newY = (int) ((e.getY() - 10) / (200.0 / 3.0)); // if any was clicked
         if (newX >= 0 && newX < 9 && newY >= 0 && newY < 9) {
            focusX = newX;
            focusY = newY;
         }
      }

      @Override
      public void mouseReleased(MouseEvent e) {

      }

      @Override
      public void mouseEntered(MouseEvent e) {

      }

      @Override
      public void mouseExited(MouseEvent e) {

      }
   }

   public static class Cell { // Class for storing all relevant data
      private final int x; // Coords
      private final int y; //-------
      private final boolean isPreset; // Is part of the starting configuration
      private final int rowBlock; // Its blocks x
      private final int columnBlock; // Its blocks y
      private int value;

      public Cell(int x, int y) {
         this(x, y, 0);
      } // Constructor for a blank cell

      public Cell(int x, int y, int value) { // Constructor for a preset cell
         this.x = x;
         this.y = y;
         if (value == 0) this.isPreset = false;
         else this.isPreset = true;
         this.rowBlock = x / 3;
         this.columnBlock = y / 3;
         this.value = value;
      }

      public String toString() {
         return "rowBlock:\t" + rowBlock
                 + "\tcolumn:\t" + columnBlock
                 + " x:\t" + x
                 + "\ty:\t" + y
                 + "\tvalue:\t" + value;
      }

      public void setValue(int value) {
         this.value = value;
      }

      public int getX() {
         return x;
      }

      public int getY() {
         return y;
      }

      public boolean isPreset() {
         return isPreset;
      }

      public int getRowBlock() {
         return rowBlock;
      }

      public int getColumnBlock() {
         return columnBlock;
      }

      public int getValue() {
         return value;
      }
   }
}
