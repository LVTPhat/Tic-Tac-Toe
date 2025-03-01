import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Something something = new Something();
            WindowConfigurer windowConfigurer = new DefaultWindowConfigurer();
            windowConfigurer.configure(something);
            something.setVisible(true);
        });

    }
}

interface WindowConfigurer {
    void configure(JFrame window);
}

class DefaultWindowConfigurer implements WindowConfigurer {
    private Font font = new Font("Arial", Font.PLAIN, 12); // Default font

    public void configure(JFrame window) {
        for (Component c : window.getContentPane().getComponents()) {
            c.setFont(font);
        }
    }

    public void setFont(Font font) {
        this.font = font;
    }
}

class CenteredTableCellRenderer extends DefaultTableCellRenderer {
    public CenteredTableCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa ngang
        setVerticalAlignment(SwingConstants.CENTER);   // Căn giữa dọc
    }
}
class Something extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private boolean gameOver = false;
    private boolean isXTurn = true;

    public Something() {
        setTitle("Caro");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Object[][] data = {
                {"", "", ""},
                {"", "", ""},
                {"", "", ""}
        };

        // Create a table model and JTable
        model = new DefaultTableModel(data, new String[]{"", "", ""}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        table = new JTable(model);
        table.setRowHeight(70);
        table.setFont(new Font("Arial", Font.BOLD, 30)); // Chữ to hơn
        table.setCellSelectionEnabled(true);

        DefaultTableCellRenderer centerForChildren = new CenteredTableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerForChildren);
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if (row >= 0 && column >= 0) {
                    Object value = model.getValueAt(row, column);
                    if (value == null || value.toString().isEmpty()) {
                        if (isXTurn) {
                            model.setValueAt("X", row, column);
                        } else {
                            model.setValueAt("O", row, column);
                        }
                        if (checkWinner()) {
                            gameOver = true;
                            JOptionPane.showMessageDialog(Something.this,
                                    (isXTurn ? "X" : "O") + " thắng!", "Trò chơi kết thúc",
                                    JOptionPane.INFORMATION_MESSAGE);
                            resetGame();
                            return;
                        } else if (isDraw()) {
                            gameOver = true;
                            JOptionPane.showMessageDialog(Something.this,
                                    "Hòa!", "Trò chơi kết thúc",
                                    JOptionPane.INFORMATION_MESSAGE);
                            resetGame();
                            return;
                        }
                        isXTurn = !isXTurn;
                        robotCaro();
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    private boolean checkWinner() {
        String currentPlayer = isXTurn ? "X" : "O";

        // rowandcolumn
        for (int i = 0; i < 3; i++) {
            if (model.getValueAt(i, 0).equals(currentPlayer) &&
                    model.getValueAt(i, 1).equals(currentPlayer) &&
                    model.getValueAt(i, 2).equals(currentPlayer)) {
                return true; //win row
            }

            if (model.getValueAt(0, i).equals(currentPlayer) &&
                    model.getValueAt(1, i).equals(currentPlayer) &&
                    model.getValueAt(2, i).equals(currentPlayer)) {
                return true; //win column
            }
        }

        //cheochinh
        if (model.getValueAt(0, 0).equals(currentPlayer) &&
                model.getValueAt(1, 1).equals(currentPlayer) &&
                model.getValueAt(2, 2).equals(currentPlayer)) {
            return true;
        }

        // cheophu
        if (model.getValueAt(0, 2).equals(currentPlayer) &&
                model.getValueAt(1, 1).equals(currentPlayer) &&
                model.getValueAt(2, 0).equals(currentPlayer)) {
            return true;
        }

        return false;
    }

    //Draw
    private boolean isDraw() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (model.getValueAt(row, col).equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                model.setValueAt("", row, col);
            }
        }
        isXTurn = true;
        gameOver = false;
    }

    private void robotCaro() {
        int bestRow = -1, bestColumn = -1;
        int bestDiem = Integer.MIN_VALUE;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (model.getValueAt(row, col).toString().isEmpty()) {
                    model.setValueAt("O", row, col);
                    //Win ngay thi danh ngay lap tuc
                    if (checkWinner()) {
                        model.setValueAt("", row, col);
                        model.setValueAt("O", row, col);
                        gameOver=true;
                        JOptionPane.showMessageDialog(Something.this,
                                (isXTurn ? "X" : "O") + " thắng!", "Trò chơi kết thúc",
                                JOptionPane.INFORMATION_MESSAGE);
                        resetGame();
                        return;
                    }
                    //Hoan tac
                    model.setValueAt("", row, col);
                    model.setValueAt("X", row, col);
                    boolean testEWin = checkWinner();
                    model.setValueAt("", row, col);

                    int point = 0;
                    if (testEWin) point = 100;
                    else point = trongSo(row, col);
                    if(point > bestDiem){
                        bestDiem =point;
                        bestRow =row;
                        bestColumn =col;
                    }
                }
            }
        }
        if (bestRow != -1 && bestColumn != -1) {
            model.setValueAt("O", bestRow, bestColumn);
            isXTurn = !isXTurn;
        }
    }


    private int trongSo(int row, int col) {
        int size =3;
        int point = 0;
        //check row
        for (int i = 0; i < size; i++) {
            if (model.getValueAt(row, i).equals("O")) point += 10;
            if (model.getValueAt(row, i).equals("X")) point -= 5;
        }
        //check column
        for (int i = 0; i < size; i++) {
            if (model.getValueAt(i, col).equals("O")) point += 10;
            if (model.getValueAt(i, col).equals("X")) point -= 5;
        }

        //check duong cheo chinh
        if (row == col) {
            for (int i = 0; i < size; i++) {
                if (model.getValueAt(i, i).equals("O")) point += 10;
                if (model.getValueAt(i, i).equals("X")) point -= 5;
            }
        }

        //check duong cheo phu
        if (row + col == size - 1) {  // Với size = 5 thì size - 1 = 4
            for (int i = 0; i < size; i++) {
                if (model.getValueAt(i, size - 1 - i).equals("O")) point += 10;
                if (model.getValueAt(i, size - 1 - i).equals("X")) point -= 5;
            }
        }
        return point;
    }
}
