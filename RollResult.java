
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

    public void addResult(int res) {
        total += res;
        rolls.add(res);
    }

    public RollResult andThen(RollResult r2) {
        int newTotal = this.total + r2.total;
        Vector<Integer> rolls = new Vector<>();
        rolls.addAll(this.rolls);
        rolls.addAll(r2.rolls);
        return new RollResult(newTotal, this.modifier + r2.modifier,rolls);
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
