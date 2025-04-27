
import java.util.*;

public class RollResult { // Lỗi cú pháp: Thêm dấu '{' mở đầu classclass

	/**
	  *Refactor: Thêm từ khóa "private" cho các biến instance
	  *Lý do: Baỏ vệ tính đóng gói (encapsulation), chỉ cho phép truy cập trong class.
	 */
    private int total;
    private int modifier;
    private Vector<Integer> rolls;

	/**
	 *Refactor: Sửa lỗi cú pháp: Constructor private bị thiếu dấu ")"
	 */
    private RollResult(int total, int modifier, Vector<Integer> rolls) {
        this.total = total;
        this.modifier = modifier;
		/**
		 *Refactor: Sửa lỗi cú pháp: Thêm dấu "." vào giữa this và rolls
		 *lý do: thisRolls không hợp lệ. Vì cần có dấu "." để truy cập biến instance
		 */
        this.rolls = rolls; 
    }

    public RollResult(int bonus) {
        this.total = bonus;
        this.modifier = bonus;
		/**
		 *Refactor: Sử dụng diamond operator '<>', thay vì 'new Vector<Integer>()'
		 *Lý do: Java hỗ trợ diamond operator giúp code ngắn gọn, rõ ràng hơn.
		 */
        rolls = new Vector<>();
    }

	/**
	 *Refactor: Sửa lỗi logic: Hàm bị comment trong bài
	 *lý do: Hàm này cần để thêm kết quả vào tổng danh sách rollsrolls
	 */
    public void addResult(int res) {
        total += res;
        rolls.add(res);
    }

    public RollResult andThen(RollResult r2) {
		/**
		 *Refactor: Đổi tên biến tránh trùng tên với tên biến 'total' ở trên
		 *Lý do: Tránh hiểu nhầm với biến instance 'this.totaltotal'
		 */
        int newTotal = this.total + r2.total; 
        Vector<Integer> rolls = new Vector<>();
        rolls.addAll(this.rolls);
        rolls.addAll(r2.rolls);
        return new RollResult(newTotal, this.modifier + r2.modifier,rolls);
    }

    public String toString() {
		/**
		 *Refactor: Dùng StringBuilder thay vì nối chuỗi trực tiếp 
		 *Lý do: StringBuilder hiệu quả hơn khi nối nhiều chuỗi
		 */
        StringBuilder sb = new StringBuilder();
        sb.append(total).append(" <= ").append(rolls);
        if (modifier != 0) {
            sb.append(" (modifier: ").append(modifier).append(")");
        }
        return sb.toString();
    }
	/**
     * Refactor: Sửa lỗi cú pháp: Thêm dấu '}' ở cuối class
	 *Lý do: Trình biên dịch Java không tìm thấy dấu '}' để đóng class -> Báo lỗi 
	 */

}

