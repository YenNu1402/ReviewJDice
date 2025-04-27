import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


/*
JDice: Java Dice Rolling Program
Copyright (C) 2006 Andrew D. Hilton  (adhilton@cis.upenn.edu)


This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 */
public class JDice {

	static final String CLEAR = "Clear";
	static final String ROLL = "Roll Selection";

	/**
	 * Hiển thị thông báo lỗi. 
	 * Refactored: Đơn giản hoá việc gọi JOptionPane để hiển thị lỗi. 
	 * Lý do: Giảm sự phức tạp và làm rõ ràng mục đích của phương thức.
	 */
	static void showError(String s) {
		JOptionPane.showConfirmDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Refactor:
	 * - Sửa lỗi thiếu dấu chấm (.) giữa this và inputBox. 
	 * - Đổi tên biến cho rõ ràng hơn(listItems->resultItems).
	 * - Tách logic phân tích cú pháp ra thành phương thức trợ giúp parseInput(). 
	 * - Đơn giản hoá phương thức actionPerformed. 
	 * Lý do: 
	 * - Tránh lỗi biên dịch 'cannot find symbol'.
	 * - Cấu trúc có một số chỗ dư thừa, tên biến không rõ ràng, và logic phân tích cú pháp bị trùng lặp trong xử lý sự kiên.
	 */
	private static class JDiceListener implements ActionListener {

		private Vector<String> resultItems; // Danh sách để hiển thị kết quả cuộn
		private JList<String> resultList;   // Thành phần GUI để hiển thị kết quả
		private JComboBox<String> inputComboBox; // Trường nhập liệu cho chuỗi xúc xắc
		private long lastEventTime;          // Để tránh xử lý sự kiện trùng lặp

		public JDiceListener(JList<String> resultList, JComboBox<String> inputComboBox) {
			this.resultItems = new Vector<>();
			this.resultList = resultList;
			this.inputComboBox = inputComboBox;
			this.lastEventTime = 0;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getWhen() == lastEventTime) {
				return; // Ngừng xử lý sự kiện trùng lặp
			}
			lastEventTime = e.getWhen();

			Object source = e.getSource();
			String command = e.getActionCommand();

			if (source instanceof JComboBox || ROLL.equals(command)) {
				String[] parsed = parseInput((String) inputComboBox.getSelectedItem());
				doRoll(parsed[0], parsed[1]);
			} else if (CLEAR.equals(command)) {
				doClear();
			} else {
				doRoll(null, command);
			}
		}

		/**
		 * Xoá danh sách kết quả 
		 * Refactor:
		 * - Đổi tên phương thức từ do_Clear()thành doClear()
		 * Lý do: Tuân thủ quy tắc đặt tên camelCase trong Java
		 */
		private void doClear() {
			resultList.clearSelection();
			resultItems.clear();
			resultList.setListData(resultItems);
		}

		/**
		 * Thực hiện cuộn xúc xắc và cập nhật danh sách hiển thị Refactor: - Kết
		 * hợp mã trùng lặp - Cải thiện xử lý khi có tên được cung cấp
		 */
		private void doRoll(String name, String diceString) {
			Vector<DieRoll> rolls = DiceParser.parseRoll(diceString);
			if (rolls == null) {
				showError("Chuỗi xúc xắc không hợp lệ: " + diceString);
				return;
			}

			int startIndex = 0;
			if (name != null) {
				resultItems.add(0, name);
				startIndex = 1;
			}

			for (int i = 0; i < rolls.size(); i++) {
				DieRoll dieRoll = rolls.get(i);
				RollResult result = dieRoll.makeRoll();
				String display = (name != null ? "  " : "") + dieRoll + "  =>  " + result;
				resultItems.add(startIndex + i, display);
			}

			int[] selectedIndices = new int[resultItems.size()];
			for (int i = 0; i < selectedIndices.length; i++) {
				selectedIndices[i] = i;
			}

			resultList.setListData(resultItems);
			resultList.setSelectedIndices(selectedIndices);
		}

		/**
		 * Phân tích cú pháp đầu vào từ combo box thành tên và biểu thức xúc xắc.
		 * Refactor: Tách phân tích cú pháp ra khỏi actionPerformed để đơn giản hóa xử lý sự kiện.
		 */
		private String[] parseInput(String input) {
			String[] parts = input.split("=");
			if (parts.length >= 2) {
				String name = String.join("=", Arrays.copyOf(parts, parts.length - 1));
				String dice = parts[parts.length - 1];
				return new String[]{name, dice};
			}
			return new String[]{null, input};
		}
	}

	/**
	 * Điểm bắt đầu của chương trình. Xây dựng giao diện người dùng và khởi động ứng dụng.
	 * Refactor: - Định dạng mã nhỏ cho dễ đọc hơn.
	 */
	public static void main(String[] args) {
		Vector<String> v = new Vector<String>();
		if (args.length >= 1) {
			try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {

				String s;
				while ((s = br.readLine()) != null) {
					v.add(s);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.err.println("***********\n**********\n");
				System.err.println("Could not read input file: " + args[0]);
				System.err.println("***********\n**********\n");
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
		rightSide.setLayout(new BoxLayout(rightSide,
				BoxLayout.Y_AXIS));
		String[] buttons = {ROLL,
			"d4",
			"d6",
			"d8",
			"d10",
			"d12",
			"d20",
			"d100",
			CLEAR};
		for (int i = 0; i < buttons.length; i++) {
			JButton newButton = new JButton(buttons[i]);
			rightSide.add(newButton);
			newButton.addActionListener(jdl);
		}
		c.add(rightSide, BorderLayout.EAST);
		jf.setSize(450, 500);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);

	}

}
