import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.util.ArrayList;

class Observable {

    private ArrayList<Observer> observers = new ArrayList<Observer>();
    private Boolean changed = false;

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void deleteObserver(Observer observer) {
        observers.remove(observer);
    }

    public void setChanged() {
        changed = true;
    }

    public void notifyObservers() {
        if (changed) {
            for (Observer observer : observers) {
                observer.update(this, null);
            }
            changed = false;
        }
    }

    public void notifyObservers(Object arg) {
        if (changed) {
            for (Observer observer : observers) {
                observer.update(this, arg);
            }
            changed = false;
        }
    }

}

interface Observer {
    public void update(Observable o, Object arg);
}

class Cell {
    protected boolean opened;
    protected boolean bomb;
    protected int flag;
    protected int num;

    public Cell() {
        opened = false;
        bomb = false;
        flag = 0;
        num = 0;
    }
}

class MSModel extends Observable {
    private ArrayList<ArrayList<Cell>> board;
    private int width;
    private int height;
    private int bomb;
    private Cell tmp;

    public MSModel(int w, int h, int b) {
        width = w;
        height = h;
        bomb = b;
        board = new ArrayList<ArrayList<Cell>>();
        int i, j, k, l;
        for (i = 0; i < h; i++) {
            board.add(new ArrayList<Cell>());
            for (j = 0; j < w; j++) {
                board.get(i).add(new Cell());
            }
        }
        for (i = 0; i < b; i++) {
            j = (int) Math.ceil(Math.random() * w * h);
            while (board.get(j / w).get(j % w).bomb) {
                j = (int) Math.ceil(Math.random() * w * h);
            }
            board.get(j / w).get(j % w).bomb = true;
            for (k = -1; k < 2; k++) {
                for (l = -1; l < 2; l++) {
                    if (j / w + k >= 0 && j / w + k < h && j % w + l >= 0 && j % w + l < w) {
                        tmp = board.get(j / w + k).get(j % w + l);
                        if (!tmp.bomb) {
                            tmp.num += 1;
                        }
                    }
                }
            }
        }
    }

    public Cell getCell(int i, int j) {
        return board.get(i).get(j);
    }

    public void open(int i, int j) {
        Cell tmp = board.get(i).get(j);
        if (tmp.opened) {
            return;
        }
        tmp.opened = true;
        if (tmp.bomb) {
            System.out.println("game over");
            return;
        }
        if (tmp.num > 0) {
            return;
        }
        int k, l;
        for (k = -1; k < 2; k++) {
            for (l = -1; l < 2; l++) {
                if (k != 0 || l != 0) {
                    if (i + k >= 0 && i + k < height && j + l >= 0 && j + l < width) {
                        tmp = board.get(i + k).get(j + l);
                        if (!tmp.opened && !tmp.bomb) {
                            if (tmp.num == 0) {
                                open(i + k, j + l);
                            } else {
                                tmp.opened = true;
                            }
                        }
                    }
                }
            }
        }
        setChanged();
        notifyObservers();
    }

    public void flag(int i, int j) {
        Cell tmp = board.get(i).get(j);
        if (!tmp.opened) {
            tmp.flag = (tmp.flag + 1) % 3;
            setChanged();
            notifyObservers();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBomb() {
        return bomb;
    }
}

class MSView extends JPanel implements Observer {
    private MSModel model;
    private MSController controller;

    public MSView(MSModel mm, MSController mc) {
        model = mm;
        controller = mc;
        mm.addObserver(this);
        this.setLayout(new GridLayout(model.getHeight(), model.getWidth()));
        this.setPreferredSize(new Dimension(model.getWidth() * 40 + 20, model.getHeight() * 40 + 20));
        this.addMouseListener(mc);
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, model.getWidth() * 40 + 20, model.getHeight() * 40 + 20);

        g.setColor(Color.black);
        int i, j;
        for (i = 0; i < model.getHeight() + 2; i++) {
            g.drawLine(10, i * 40 + 10, model.getWidth() * 40 + 10, i * 40 + 10);
        }
        for (i = 0; i < model.getWidth() + 2; i++) {
            g.drawLine(i * 40 + 10, 10, i * 40 + 10, model.getHeight() * 40 + 10);
        }
        Cell tmp;
        for (i = 0; i < model.getHeight(); i++) {
            for (j = 0; j < model.getWidth(); j++) {
                tmp = model.getCell(i, j);
                if (tmp.opened) {
                    if (tmp.bomb) {
                        g.drawString(tmp.num + " b", j * 40 + 30, i * 40 + 30);
                    } else {
                        g.drawString(tmp.num + "", j * 40 + 30, i * 40 + 30);
                    }
                } else {
                    if (tmp.flag == 1) {
                        g.drawString("B", j * 40 + 30, i * 40 + 30);
                    } else if (tmp.flag == 2) {
                        g.drawString("?", j * 40 + 30, i * 40 + 30);
                    }
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }

}

class MSController implements MouseListener {
    private MSModel model;

    public MSController(MSModel mm) {
        model = mm;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            int x = e.getX() - 10, y = e.getY() - 10;
            if (x >= 0 && x <= model.getWidth() * 40 && y >= 0 && y <= model.getHeight() * 40) {
                model.open(y / 40, x / 40);
            }
        } else {
            int x = e.getX() - 10, y = e.getY() - 10;
            if (x >= 0 && x <= model.getWidth() * 40 && y >= 0 && y <= model.getHeight() * 40) {
                model.flag(y / 40, x / 40);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
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

}

class MineSweeper extends JFrame {
    public MineSweeper() {
        MSModel model = new MSModel(8, 8, 8);
        MSController controller = new MSController(model);
        MSView view = new MSView(model, controller);
        this.setTitle("MineSweeper");
        this.getContentPane().add(view);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String argv[]) {
        new MineSweeper();
    }
}