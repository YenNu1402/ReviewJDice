import java.util.*;
import java.util.logging.Logger; // Thêm import cho logging

public class DieRoll {
    private int ndice;
    private int nsides;
    private int bonus;
    private static Random rnd;
    private static final Logger LOGGER = Logger.getLogger(DieRoll.class.getName()); // Khởi tạo Logger

    static {
        rnd = new Random();
    }

    public DieRoll(int ndice, int nsides, int bonus) {
        // Cải thiện thông báo lỗi với chi tiết hơn
        if (ndice <= 0) {
            throw new IllegalArgumentException("Number of dice must be greater than 0, got: " + ndice);
        }
        if (nsides <= 0) {
            throw new IllegalArgumentException("Number of sides must be greater than 0, got: " + nsides);
        }

        this.ndice = ndice;
        this.nsides = nsides;
        this.bonus = bonus;
    }

    public RollResult makeRoll() {
        // Thêm logging và thu thập kết quả
        RollResult result = new RollResult(bonus);
        List<Integer> rolls = new ArrayList<>(); // Lưu trữ kết quả để log
        for (int i = 0; i < ndice; i++) {
            int roll = rnd.nextInt(nsides) + 1;
            result.addResult(roll);
            rolls.add(roll); // Thu thập kết quả để log
        }
        // Log kết quả các lần roll
        LOGGER.info(String.format("Rolled %dd%d+%d: %s", ndice, nsides, bonus, rolls));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        ans.append(ndice).append("d").append(nsides);
        // Kiểm tra bonus khác 0 để tránh thêm dấu không cần thiết
        if (bonus != 0) {
            ans.append(bonus > 0 ? "+" : "").append(bonus);
        }
        return ans.toString();
    }
}