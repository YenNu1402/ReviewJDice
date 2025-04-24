import java.util.*;

public class RollResult {

    private int total;
    private int modifier;
    private Vector<Integer> rolls;

    private RollResult(int total, int modifier, Vector<Integer> rolls) {
        this.total = total;
        this.modifier = modifier;
        this.rolls = rolls;
    }

    public RollResult(int bonus) {
        this.total = bonus;
        this.modifier = bonus;
        rolls = new Vector<>();
    }

    // Thêm logging để theo dõi kết quả thêm vào
    public void addResult(int res) {
        total += res;
        rolls.add(res);
        System.out.println("[LOG] Added roll: " + res + ", new total: " + total);
    }

    // Refactor để dùng biến newTotal, tránh nhầm lẫn với this.total
    public RollResult andThen(RollResult r2) {
        int newTotal = this.total + r2.total;
        int newModifier = this.modifier + r2.modifier;

        Vector<Integer> combinedRolls = new Vector<>();
        combinedRolls.addAll(this.rolls);
        combinedRolls.addAll(r2.rolls);

        System.out.println("[LOG] Merged rolls: " + combinedRolls);
        System.out.println("[LOG] New total: " + newTotal + ", New modifier: " + newModifier);

        return new RollResult(newTotal, newModifier, combinedRolls);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(total).append(" <= ").append(rolls);
        if (modifier != 0) {
            sb.append(" (modifier: ").append(modifier).append(")");
        }
        return sb.toString();
    }
}
