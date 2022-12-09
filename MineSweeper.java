import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


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

class Board extends JPanel implements MouseListener {
    private Cell[][] board;

    public Board() {
        this.board = new Cell[16][16];
        int i, j, n = 6, k, l;
        for (i = 0; i < 16; i++) {
            for (j = 0; j < 16; j++) {
                board[i][j] = new Cell();
            }
        }
        for (i = 0; i < 16; i++) {
            for (j = 0; j < 16; j++) {
                if (n > 0 && Math.random() < 0.0234375) {
                    board[i][j].bomb = true;
                    for (k = -1; k < 2; k++) {
                        for (l = -1; l < 2; l++) {
                            if (k != 0 || l != 0) {
                                if (i + k >= 0 && i + k < 16 && j + l >= 0 && j + l < 16) {
                                    board[i + k][j + l].num += 1;
                                }
                            }
                        }
                    }
                    n -= 1;
                }
                if (board[i][j].bomb) {
                    //this.add(new JPanel());
                } else {
                    //this.add(new JPanel());
                }
            }
        }
        this.setLayout(new GridLayout(16, 16));
        this.setPreferredSize(new Dimension(660, 660));
        this.addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, 660, 660);

        g.setColor(Color.black);
        int i, j;
        for (i = 0; i < 17; i++) {
            g.drawLine(10, i * 40 + 10, 650, i * 40 + 10);
            g.drawLine(i * 40 + 10, 10, i * 40 + 10, 650);
        }

        for (i = 0; i < 16; i++) {
            for (j = 0; j < 16; j++) {
                if (board[i][j].opened) {
                    if (this.board[i][j].bomb) {
                        g.drawString(board[i][j].num + " b", i * 40 + 30, j * 40 + 30);
                    } else {
                        g.drawString(board[i][j].num + "", i * 40 + 30, j * 40 + 30);
                    }
                } else {
                    if (this.board[i][j].flag == 1) {
                        g.drawString("B", i * 40 + 30, j * 40 + 30);
                    } else if(this.board[i][j].flag == 2) {
                        g.drawString("?", i * 40 + 30, j * 40 + 30);
                    }
                }
            }
        }
    }

    public void open(int i, int j) {
        board[i][j].opened = true;
        int k, l;
        for (k = -1; k < 2; k++) {
            for (l = -1; l < 2; l++) {
                if (k != 0 || l != 0) {
                    if (i + k >= 0 && i + k < 16 && j + l >= 0 && j + l < 16) {
                        if (!board[i + k][j + l].opened && !board[i + k][j + l].bomb) {
                            if (board[i + k][j + l].num == 0) {
                                open(i + k, j + l);
                            } else {
                                board[i + k][j + l].opened = true;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            int x = e.getX() - 10, y = e.getY() - 10;
            if (x >= 0 && x <= 640 && y >= 0 && y <= 640) {
                if (!board[x / 40][y / 40].opened) {
                    if (board[x / 40][y / 40].bomb) {
                        board[x / 40][y / 40].opened = true;
                        System.out.println("game over");
                    } else {
                        open(x / 40, y / 40);
                    }
                }
            }
        } else {
            int x = e.getX() - 10, y = e.getY() - 10;
            if (x >= 0 && x <= 640 && y >= 0 && y <= 640) {
                if (!board[x / 40][y / 40].opened) {
                    board[x / 40][y / 40].flag = (board[x / 40][y / 40].flag + 1) % 3;
                }
            }
        }
        this.repaint();
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
        this.setTitle("MineSweeper");
        this.getContentPane().add(new Board());
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String argv[]) {
        MineSweeper ms = new MineSweeper();
    }
}