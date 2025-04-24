import java.util.*;

class DieRoll {
    int ndice, dsides, bonus;

    public DieRoll(int ndice, int dsides, int bonus) {
        this.ndice = ndice;
        this.dsides = dsides;
        this.bonus = bonus;
    }

    public int makeRoll() {
        Random rand = new Random();
        int total = bonus;
        System.out.println("Rolling " + ndice + "d" + dsides + (bonus != 0 ? (bonus > 0 ? "+" : "") + bonus : ""));

        for (int i = 1; i <= ndice; i++) {
            int roll = rand.nextInt(dsides) + 1;
            System.out.println("  Die " + i + ": " + roll);
            total += roll;
        }

        System.out.println("  Bonus: " + bonus + " → Total: " + total);
        return total;
    }

    public String toString() {
        return ndice + "d" + dsides + (bonus != 0 ? (bonus > 0 ? "+" : "") + bonus : "");
    }
}

class DiceSum extends DieRoll {
    DieRoll d1, d2;

    public DiceSum(DieRoll d1, DieRoll d2) {
        super(0, 0, 0);
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public int makeRoll() {
        System.out.println("Rolling combined: " + d1 + " & " + d2);
        int total1 = d1.makeRoll();
        int total2 = d2.makeRoll();
        int combined = total1 + total2;
        System.out.println("  Sum: " + total1 + " + " + total2 + " = " + combined);
        return combined;
    }

    @Override
    public String toString() {
        return "(" + d1 + " & " + d2 + ")";
    }
}

public class DiceParser {

    private static class StringStream {
        StringBuffer buff;

        public StringStream(String s) {
            buff = new StringBuffer(s);
        }

        private void munchWhiteSpace() {
            int index = 0;
            char curr;
            while (index < buff.length()) {
                curr = buff.charAt(index);
                if (!Character.isWhitespace(curr))
                    break;
                index++;
            }
            buff = buff.delete(0, index);
        }

        public boolean isEmpty() {
            munchWhiteSpace();
            return buff.toString().equals("");
        }

        public Integer getInt() {
            return readInt();
        }

        public Integer readInt() {
            int index = 0;
            char curr;
            munchWhiteSpace();
            while (index < buff.length()) {
                curr = buff.charAt(index);
                if (!Character.isDigit(curr))
                    break;
                index++;
            }
            try {
                Integer ans = Integer.parseInt(buff.substring(0, index));
                buff = buff.delete(0, index);
                return ans;
            } catch (Exception e) {
                return null;
            }
        }

        public Integer readSgnInt() {
            munchWhiteSpace();
            StringStream state = save();
            if (checkAndEat("+")) {
                Integer ans = readInt();
                if (ans != null)
                    return ans;
                restore(state);
                return null;
            }
            if (checkAndEat("-")) {
                Integer ans = readInt();
                if (ans != null)
                    return -ans;
                restore(state);
                return null;
            }
            return readInt();
        }

        public boolean checkAndEat(String s) {
            munchWhiteSpace();
            if (buff.indexOf(s) == 0) {
                buff = buff.delete(0, s.length());
                return true;
            }
            return false;
        }

        public StringStream save() {
            return new StringStream(buff.toString());
        }

        public void restore(StringStream ss) {
            this.buff = new StringBuffer(ss.buff);
        }

        public String toString() {
            return buff.toString();
        }
    }

    public static Vector<DieRoll> parseRoll(String s) {
        StringStream ss = new StringStream(s.toLowerCase());
        Vector<DieRoll> v = parseRollInner(ss, new Vector<DieRoll>());
        if (ss.isEmpty())
            return v;
        return null;
    }

    private static Vector<DieRoll> parseRollInner(StringStream ss, Vector<DieRoll> v) {
        Vector<DieRoll> r = parseXDice(ss);
        if (r == null) {
            return null;
        }
        v.addAll(r);
        if (ss.checkAndEat(";")) {
            return parseRollInner(ss, v);
        }
        return v;
    }

    private static Vector<DieRoll> parseXDice(StringStream ss) {
        StringStream saved = ss.save();
        Integer x = ss.getInt();
        int num;
        if (x == null) {
            num = 1;
        } else {
            if (ss.checkAndEat("x")) {
                num = x;
            } else {
                num = 1;
                ss.restore(saved);
            }
        }
        DieRoll dr = parseDice(ss);
        if (dr == null) {
            return null;
        }
        Vector<DieRoll> ans = new Vector<DieRoll>();
        for (int i = 0; i < num; i++) {
            ans.add(dr);
        }
        return ans;
    }

    private static DieRoll parseDice(StringStream ss) {
        return parseDTail(parseDiceInner(ss), ss);
    }

    private static DieRoll parseDiceInner(StringStream ss) {
        Integer num = ss.getInt();
        int dsides;
        int ndice;
        if (num == null) {
            ndice = 1;
        } else {
            ndice = num;
        }
        if (ss.checkAndEat("d")) {
            num = ss.getInt();
            if (num == null)
                return null;
            dsides = num;
        } else {
            return null;
        }
        num = ss.readSgnInt();
        int bonus = (num == null) ? 0 : num;
        return new DieRoll(ndice, dsides, bonus);
    }

    private static DieRoll parseDTail(DieRoll r1, StringStream ss) {
        if (r1 == null)
            return null;
        if (ss.checkAndEat("&")) {
            DieRoll d2 = parseDice(ss);
            return parseDTail(new DiceSum(r1, d2), ss);
        } else {
            return r1;
        }
    }

    private static void test(String s) {
        Vector<DieRoll> v = parseRoll(s);
        if (v == null)
            System.out.println("Failure: " + s);
        else {
            System.out.println("Results for " + s + ":");
            for (DieRoll dr : v) {
                System.out.print(dr);
                System.out.print(": ");
                System.out.println(dr.makeRoll());
            }
        }
    }

    public static void main(String[] args) {
        test("d6");
        test("2d6");
        test("d6+5");
        test("4X3d8-5");
        test("12d10+5 & 4d6+2");
        test("d6 ; 2d4+3");
        test("4d6+3 ; 8d12 -15 ; 9d10 & 3d6 & 4d12 +17");
        test("4d6 + xyzzy");
        test("hi");
        test("4d4d4");
    }
}
