package Cluedo;

import Cluedo.Board.RoomTile;
import Cluedo.Board.WallTile;
import Cluedo.Card.PersonCard;
import Cluedo.Card.RoomCard;
import Cluedo.Card.WeaponCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public abstract class GUI {

    public JMenuBar menuBar;
    public JMenuItem exit;
    public JFrame window;
    public JTextArea textArea;
    public JPanel controls;
    public JPanel boardGraphics;
    public JPanel cardGraphics;


    protected ArrayList<JButton> playerNumbers = new ArrayList<>();
    protected JButton yes, no, ready;
    protected JButton left, right, up, down;
    protected Map<PersonCard.PersonType, JRadioButton> people = new HashMap<>();
    protected Map<PersonCard.PersonType, JButton> characters = new HashMap<>();
    protected Map<WeaponCard.WeaponType, JButton> weapons = new HashMap<>();
    protected Map<RoomCard.RoomType, JButton> rooms = new HashMap<>();
    private List<JButton> suggestButtons = new ArrayList<>();
    public boolean mLeft, mRight, mUp, mDown;
    protected Set<JButton> allButtons = new HashSet<>();
    protected SpringLayout layout = new SpringLayout();

    public GUI() {
        initialiseGUI();
    }

    protected abstract void drawBoard(Graphics g);

    protected abstract void doMouseMoved(MouseEvent e);

    protected abstract void drawCards(Graphics g);

    protected abstract void drawDice(Graphics g);

    public abstract void initialise();

    private void setupTextArea() {
        textArea.setBackground(WallTile.wallColor);
        textArea.setFont(new Font("Montserrat", Font.PLAIN, 15));
        textArea.setForeground(WallTile.lightRoomTile);
        textArea.setWrapStyleWord(true);
    }

    private void setupControls() {
        controls.setLayout(layout);

        left = new JButton("←");
        left.setBackground(WallTile.darkRoomTile);
        right = new JButton("→");
        right.setBackground(WallTile.darkRoomTile);
        up = new JButton("↑");
        up.setBackground(WallTile.darkRoomTile);
        down = new JButton("↓");
        down.setBackground(WallTile.darkRoomTile);

        allButtons.add(left);
        allButtons.add(right);
        allButtons.add(down);
        allButtons.add(up);

        layout.putConstraint(SpringLayout.SOUTH, down, 0, SpringLayout.SOUTH, controls);
        layout.putConstraint(SpringLayout.SOUTH, right, 0, SpringLayout.SOUTH, controls);
        layout.putConstraint(SpringLayout.SOUTH, left, 0, SpringLayout.SOUTH, controls);
        layout.putConstraint(SpringLayout.SOUTH, up, 0, SpringLayout.NORTH, down);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, down, 0, SpringLayout.HORIZONTAL_CENTER, controls);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up, 0, SpringLayout.HORIZONTAL_CENTER, controls);
        layout.putConstraint(SpringLayout.WEST, right, 0, SpringLayout.EAST, down);
        layout.putConstraint(SpringLayout.EAST, left, 0, SpringLayout.WEST, down);


        JButton three = new JButton("3");
        JButton four = new JButton("4");
        JButton five = new JButton("5");
        JButton six = new JButton("6");

        playerNumbers.add(three);
        playerNumbers.add(four);
        playerNumbers.add(five);
        playerNumbers.add(six);

        int pad = 55;
        for (JButton number : playerNumbers){
            layout.putConstraint(SpringLayout.VERTICAL_CENTER, number, 30, SpringLayout.NORTH, controls);
            layout.putConstraint(SpringLayout.WEST, number, pad, SpringLayout.WEST, controls);
            pad += 50;
        }

        allButtons.add(three);
        allButtons.add(four);
        allButtons.add(five);
        allButtons.add(six);

        yes = new JButton("Yes");
        no = new JButton("No");
        ready = new JButton("Yes");

        allButtons.add(yes);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, yes, -35, SpringLayout.HORIZONTAL_CENTER, controls);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, yes, 50, SpringLayout.NORTH, controls);
        allButtons.add(no);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, no, 35, SpringLayout.HORIZONTAL_CENTER, controls);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, no, 50, SpringLayout.NORTH, controls);
        allButtons.add(ready);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, ready, 0, SpringLayout.HORIZONTAL_CENTER, controls);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, ready, 50, SpringLayout.NORTH, controls);

        pad = 0;
        int gap = 20;

        //person selection buttons
        int radioPadY = 50;
        for (PersonCard.PersonType pers : PersonCard.PersonType.values()){
            JButton jp = new JButton(pers.toString());
            allButtons.add(jp);
            suggestButtons.add(jp);
            characters.put(pers, jp);
            //Radio buttons:
            JRadioButton jrp = new JRadioButton(pers.toString(), false);
            jrp.setBackground(WallTile.wallColor);
            jrp.setVisible(false);
            jrp.setForeground(RoomTile.lightRoomTile);
            jrp.setFont(new Font("Montserrat", Font.PLAIN, 11));
            jrp.setBorderPainted(false);
            layout.putConstraint(SpringLayout.NORTH, jrp, radioPadY, SpringLayout.NORTH, controls);
            radioPadY += 30;
            layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, jrp, 0, SpringLayout.HORIZONTAL_CENTER, controls);
            people.put(pers, jrp);
            controls.add(jrp);
        }

        for (WeaponCard.WeaponType weap : WeaponCard.WeaponType.values()){
            JButton jw = new JButton(weap.toString());
            allButtons.add(jw);
            suggestButtons.add(jw);
            weapons.put(weap, jw);
        }

        //room selection buttons
        for (RoomCard.RoomType room : RoomCard.RoomType.values()){
            JButton jr = new JButton(room.toString());
            allButtons.add(jr);
            suggestButtons.add(jr);
            rooms.put(room, jr);
        }

        int index = 0;
        for (JButton button : suggestButtons){
            if (index == 6 || index == 12){
                pad += gap;
            }
            button.setPreferredSize(new Dimension(200, 20));
            layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0, SpringLayout.HORIZONTAL_CENTER, controls);
            layout.putConstraint(SpringLayout.NORTH, button, pad, SpringLayout.NORTH, controls);
            pad += gap;
            index++;
        }

        for (JButton button : allButtons){
            button.setBackground(WallTile.darkRoomTile);
            button.setVisible(false);
            controls.add(button);
            button.setForeground(WallTile.wallColor);
            button.setFont(new Font("Montserrat", Font.PLAIN, 15));
            button.setBorderPainted(false);
        }
    }

    private void setupBoardGraphics() {
        boardGraphics.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                doMouseMoved(e);
            }
        });
    }

    private void setupCardGraphics() {}


    private void initialiseGUI(){
        window = new JFrame();
        window.setResizable(false);

        window.setSize(400,400);
        window.setLayout(null);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(350, 500));
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        setupTextArea();

        controls = new JPanel();
        controls.setPreferredSize(new Dimension(300, 500));
        controls.setBackground(WallTile.wallColor);
        setupControls();

        boardGraphics = new JPanel() {
            protected void paintComponent(Graphics g) {
                drawBoard(g);
            }
        };
        boardGraphics.setPreferredSize(new Dimension(500, 500));
        setupBoardGraphics();

        cardGraphics = new JPanel() {
            protected void paintComponent(Graphics g) {
                drawCards(g);
            }
        };
        cardGraphics.setPreferredSize(new Dimension(1150, 200));
        setupCardGraphics();

        window.add(textArea, BorderLayout.WEST);
        window.add(controls, BorderLayout.EAST);
        window.add(boardGraphics, BorderLayout.CENTER);
        window.add(cardGraphics, BorderLayout.SOUTH);
        window.pack();
        window.setVisible(true);
    }
}
