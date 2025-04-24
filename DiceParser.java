import java.util.*;

public class DiceParser {

    private static class StringStream {
        StringBuffer buff;

        public StringStream(String s) {
            buff = new StringBuffer(s);
        }

        private void munchWhiteSpace() {
            int index = 0;
            while (index < buff.length() && Character.isWhitespace(buff.charAt(index))) {
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
            munchWhiteSpace();
            int index = 0;
            while (index < buff.length() && Character.isDigit(buff.charAt(index))) {
                index++;
            }
            if (index == 0) return null;
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
                if (ans != null) return ans;
                restore(state);
                return null;
            }
            if (checkAndEat("-")) {
                Integer ans = readInt();
                if (ans != null) return -ans;
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

    // Main parser function
    public static List<DieRoll> parseRoll(String s) {
        StringStream ss = new StringStream(s.toLowerCase(Locale.ROOT));
        List<DieRoll> v = parseRollInner(ss, new ArrayList<>());
        if (ss.isEmpty()) return v;
        return null;
    }

    private static List<DieRoll> parseRollInner(StringStream ss, List<DieRoll> v) {
        List<DieRoll> r = parseXDice(ss);
        if (r == null) return null;
        v.addAll(r);
        if (ss.checkAndEat(";")) {
            return parseRollInner(ss, v);
        }
        return v;
    }

    private static List<DieRoll> parseXDice(StringStream ss) {
        StringStream saved = ss.save();
        Integer x = ss.getInt();
        int num = 1;
        if (x != null) {
            if (ss.checkAndEat("x")) {
                num = x;
            } else {
                ss.restore(saved);
            }
        }
        DieRoll dr = parseDice(ss);
        if (dr == null) return null;
        List<DieRoll> ans = new ArrayList<>();
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
        int ndice = (num == null) ? 1 : num;
        if (!ss.checkAndEat("d")) return null;

        Integer dsides = ss.getInt();
        if (dsides == null) return null;

        Integer bonus = ss.readSgnInt();
        return new DieRoll(ndice, dsides, (bonus == null) ? 0 : bonus);
    }

    private static DieRoll parseDTail(DieRoll r1, StringStream ss) {
        if (r1 == null) return null;
        if (ss.checkAndEat("&")) {
            DieRoll d2 = parseDice(ss);
            return parseDTail(new DiceSum(r1, d2), ss);
        } else {
            return r1;
        }
    }

    private static void test(String s) {
        List<DieRoll> v = parseRoll(s);
        if (v == null) {
            System.out.println("Failure: " + s);
        } else {
            System.out.println("Results for \"" + s + "\":");
            for (DieRoll dr : v) {
                System.out.println(dr + ": " + dr.makeRoll());
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

// DieRoll class
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
        for (int i = 0; i < ndice; i++) {
            total += rand.nextInt(dsides) + 1;
        }
        return total;
    }

    public String toString() {
        return ndice + "d" + dsides + (bonus != 0 ? (bonus > 0 ? "+" : "") + bonus : "");
    }
}

// DiceSum class
class DiceSum extends DieRoll {
    DieRoll d1, d2;

    public DiceSum(DieRoll d1, DieRoll d2) {
        super(0, 0, 0);
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public int makeRoll() {
        return d1.makeRoll() + d2.makeRoll();
    }

    @Override
    public String toString() {
        return "(" + d1 + " & " + d2 + ")";
    }
}
