import java.util.*;
import java.util.logging.Logger;

/**
 * JDice: Java Dice Rolling Program
 * Refactor stage 1: Rename helper class and fix core stream handling.
 * 
 * @modifiedBy Lê Trung Kiên 
 */
public class DiceParser {

    private static final Logger logger = Logger.getLogger(DiceParser.class.getName());

    // ✅ Refactored: Renamed from StringStream → InputStreamWrapper
    private static class InputStreamWrapper {
        private StringBuffer buffer;

        public InputStreamWrapper(String s) {
            buffer = new StringBuffer(s);
        }

        private void munchWhiteSpace() {
            int index = 0;
            while (index < buffer.length() && Character.isWhitespace(buffer.charAt(index))) {
                index++;
            }
            buffer.delete(0, index);
        }

        public boolean isEmpty() {
            munchWhiteSpace();
            return buffer.length() == 0;
        }

        public Integer getInt() {
            return readInt();
        }

        public Integer readInt() {
            munchWhiteSpace();
            int index = 0;
            while (index < buffer.length() && Character.isDigit(buffer.charAt(index))) {
                index++;
            }
            try {
                Integer result = Integer.parseInt(buffer.substring(0, index));
                buffer.delete(0, index);
                return result;
            } catch (Exception e) {
                logger.warning("Invalid integer format.");
                return null;
            }
        }

        public Integer readSignedInt() {
            munchWhiteSpace();
            InputStreamWrapper saved = save();
            if (checkAndEat("+")) {
                Integer result = readInt();
                if (result != null) return result;
                restore(saved);
                return null;
            }
            if (checkAndEat("-")) {
                Integer result = readInt();
                if (result != null) return -result;
                restore(saved);
                return null;
            }
            return readInt();
        }

        public boolean checkAndEat(String s) {
            munchWhiteSpace();
            if (buffer.indexOf(s) == 0) {
                buffer.delete(0, s.length());
                return true;
            }
            return false;
        }

        public InputStreamWrapper save() {
            return new InputStreamWrapper(buffer.toString());
        }

        public void restore(InputStreamWrapper saved) {
            this.buffer = new StringBuffer(saved.buffer.toString());
        }

        public String toString() {
            return buffer.toString();
        }
    }

    // Placeholder for future parse logic
}
