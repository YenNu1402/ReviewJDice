import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * 
 * Refactor + Thêm Logging:
 * Sửa lỗi đánh máy và cú pháp.
 * Cải thiện cấu trúc mã nguồn cho dễ đọc và dễ bảo trì.
 * Thêm chức năng ghi log các sự kiện chính (Roll, Clear) bằng java.util.logging.
 * 
 */
public class JDice {
    static final String CLEAR = "Clear";
    static final String ROLL = "Roll Selection";
    static final Logger logger = Logger.getLogger(JDice.class.getName());

    /**
     * Hiển thị hộp thoại báo lỗi cho người dùng.
     * @param s Nội dung thông báo lỗi.
     */
    static void showError(String s) {
        JOptionPane.showMessageDialog(null, s, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Lớp lắng nghe sự kiện cho các thành phần giao diện JDice.
     */
    private static class JDiceListener implements ActionListener {
        Vector<String> listItems;
        JList<String> resultList;
        JComboBox<String> inputBox;
        long lastEvent; // Hack để ngăn xử lý trùng lặp sự kiện nhập liệu

        /**
         * Khởi tạo JDiceListener.
         * @param resultList Danh sách kết quả tung xúc xắc.
         * @param inputBox Ô nhập/cửa sổ chọn biểu thức xúc xắc.
         */
        public JDiceListener(JList<String> resultList, JComboBox<String> inputBox) {
            this.listItems = new Vector<>();
            this.resultList = resultList;
            this.inputBox = inputBox;
            lastEvent = 0;
        }

        /**
         * Xử lý các sự kiện nút nhấn và thay đổi lựa chọn.
         * Ghi log các hành động của người dùng.
         * 
         * @param e Đối tượng sự kiện được kích hoạt.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getWhen() == lastEvent)
                return;
            lastEvent = e.getWhen();
            
            if (e.getSource() instanceof JComboBox || e.getActionCommand().equals(ROLL)) {
                String s = inputBox.getSelectedItem().toString();
                String[] arr = s.split("=");
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < arr.length - 2; i++) {
                    name.append(arr[i]).append("=");
                }
                if (arr.length >= 2)
                    name.append(arr[arr.length - 2]);
                doRoll(name.toString(), arr[arr.length - 1]);
                logger.info("Người dùng thực hiện Roll với input: " + s);
            } else if (e.getActionCommand().equals(CLEAR)) {
                doClear();
                logger.info("Người dùng thực hiện Clear danh sách.");
            } else {
                doRoll(null, e.getActionCommand());
                logger.info("Người dùng thực hiện roll nhanh: " + e.getActionCommand());
            }
        }

        /**
         * Xóa toàn bộ kết quả trong danh sách.
         */
        private void doClear() {
            resultList.clearSelection();
            listItems.clear();
            resultList.setListData(listItems);
        }

        /**
         * Thực hiện phân tích cú pháp chuỗi xúc xắc và cập nhật kết quả tung.
         * @param name Tên nhãn (nếu có) cho kết quả.
         * @param diceString Biểu thức xúc xắc cần tung (ví dụ: "2d6+3").
         */
        private void doRoll(String name, String diceString) {
            String prepend = "";
            int start = 0;

            Vector<DieRoll> rolls = DiceParser.parseRoll(diceString);
            if (rolls == null) {
                showError("Biểu thức xúc xắc không hợp lệ: " + diceString);
                return;
            }

            if (name != null) {
                listItems.add(0, name);
                start = 1;
                prepend = "  ";
            }

            int[] selectionIndices = new int[start + rolls.size()];
            for (int i = 0; i < rolls.size(); i++) {
                DieRoll dr = rolls.get(i);
                RollResult rr = dr.makeRoll();
                String toAdd = prepend + dr + " => " + rr;
                listItems.add(i + start, toAdd);
            }
            for (int i = 0; i < selectionIndices.length; i++) {
                selectionIndices[i] = i;
            }

            resultList.setListData(listItems);
            resultList.setSelectedIndices(selectionIndices);
        }
    }

    /**
     * Hàm main: Tạo giao diện người dùng và chạy chương trình.
     * 
     * @param args Đối số dòng lệnh, nếu có thể chỉ định file input.
     */
    public static void main(String[] args) {
        Vector<String> v = new Vector<>();
        if (args.length >= 1) {
            try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
                String s;
                while ((s = br.readLine()) != null) {
                    v.add(s);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.err.println("Không thể đọc file đầu vào: " + args[0]);
            }
        }

        JFrame jf = new JFrame("Dice Roller");
        Container c = jf.getContentPane();
        c.setLayout(new BorderLayout());

        JList<String> jl = new JList<>();
        c.add(jl, BorderLayout.CENTER);

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
