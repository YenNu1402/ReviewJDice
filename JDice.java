import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/*
JDice: Java Dice Rolling Program
Copyright (C) 2006 Andrew D. Hilton

This program is free software...

 */
public class JDice {
    static final String CLEAR = "Clear";
    static final String ROLL = "Roll Selection";

    static void showError(String s) {
        JOptionPane.showConfirmDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
        // Đã loại bỏ logging nâng cao, chỉ hiện dialog
    }

    private static class JDiceListener implements ActionListener {
        Vector<String> listItems;
        JList<String> resultList;
        JComboBox<String> inputBox;
        long lastEvent;

        public JDiceListener(JList<String> resultList, JComboBox<String> inputBox) {
            this.listItems = new Vector<>();
            this.resultList = resultList;
            this.inputBox = inputBox;
            lastEvent = 0;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getWhen() == lastEvent) return;
            lastEvent = e.getWhen();

            if (e.getSource() instanceof JComboBox || e.getActionCommand().equals(ROLL)) {
                String s = inputBox.getSelectedItem().toString();
                String[] arr = s.split("=");
                String name = "";
                for (int i = 0; i < arr.length - 2; i++) {
                    name += arr[i] + "=";
                }
                if (arr.length >= 2) {
                    name += arr[arr.length - 2];
                }
                doRoll(name, arr[arr.length - 1]);
            } else if (e.getActionCommand().equals(CLEAR)) {
                doClear();
            } else {
                doRoll(null, e.getActionCommand());
            }
        }

        private void doClear() {
            resultList.clearSelection();
            listItems.clear();
            resultList.setListData(listItems);
        }

        private void doRoll(String name, String diceString) {
            String prepend = "";
            int start = 0;
            Vector<DieRoll> v = DiceParser.parseRoll(diceString);
            if (v == null) {
                showError("Invalid dice string: " + diceString);
                return;
            }

            if (name != null) {
                listItems.add(0, name);
                start = 1;
                prepend = "  ";
            }

            int[] selectionIndices = new int[start + v.size()];
            for (int i = 0; i < v.size(); i++) {
                DieRoll dr = v.get(i);
                RollResult rr = dr.makeRoll();
                String toAdd = prepend + dr + "  =>  " + rr;
                listItems.add(i + start, toAdd);
            }

            for (int i = 0; i < selectionIndices.length; i++) {
                selectionIndices[i] = i;
            }

            resultList.setListData(listItems);
            resultList.setSelectedIndices(selectionIndices);
        }
    }

    public static void main(String[] args) {
        Vector<String> v = new Vector<>();
        if (args.length >= 1) {
            try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
                String s;
                while ((s = br.readLine()) != null) {
                    v.add(s);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace(); // Logging đơn giản thông qua console
                System.err.println("Could not read input file: " + args[0]);
            }
        }

        JFrame jf = new JFrame("Dice Roller");
        Container c = jf.getContentPane();
        c.setLayout(new BorderLayout());

        JList<String> jl = new JList<>();
        JScrollPane scrollPane = new JScrollPane(jl); // Vẫn giữ UI cải tiến
        c.add(scrollPane, BorderLayout.CENTER);

        JComboBox<String> jcb = new JComboBox<>(v);
        jcb.setEditable(true);
        c.add(jcb, BorderLayout.NORTH);

        JDiceListener jdl = new JDiceListener(jl, jcb);
        jcb.addActionListener(jdl);

        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
        String[] buttons = {ROLL, "d4", "d6", "d8", "d10", "d12", "d20", "d100", CLEAR};

        for (String button : buttons) {
            JButton newButton = new JButton(button);
            rightSide.add(newButton);
            newButton.addActionListener(jdl);
        }

        c.add(rightSide, BorderLayout.EAST);

        jf.setSize(450, 500);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
