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
        if (ndice <= 0) {
            throw new IllegalArgumentException("Số xúc xắc phải > 0");
        }
        if (nsides <= 0) {
            throw new IllegalArgumentException("Số mặt xúc xắc phải > 0");
        }

        this.ndice = ndice;
        this.nsides = nsides;
        this.bonus = bonus;
    }

    public RollResult makeRoll() {
        // Refactor: Tối ưu tên biến cho rõ ràng
        RollResult result = new RollResult(bonus);
        for (int i = 0; i < ndice; i++) {
            int roll = rnd.nextInt(nsides) + 1;
            result.addResult(roll);
        }
        return result;
    }

    @Override
    public String toString() {
        // Refactor: Sử dụng StringBuilder để tối ưu chuỗi
        StringBuilder ans = new StringBuilder();
        ans.append(ndice).append("d").append(nsides);
        if (bonus > 0) {
            ans.append("+").append(bonus);
        } else if (bonus < 0) {
            ans.append(bonus);
        }
        return ans.toString();
    }
}