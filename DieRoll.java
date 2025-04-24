import java.util.*;

public class DieRoll {
    private int ndice;
    private int nsides;
    private int bonus;
    private static Random rnd;

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
        RollResult result = new RollResult(bonus);
        for (int i = 0; i < ndice; i++) {
            int roll = rnd.nextInt(nsides) + 1;
            result.addResult(roll);
        }
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