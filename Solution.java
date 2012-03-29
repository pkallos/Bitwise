import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author phil
 */
public class Solution {

    static int NBITS = 31;
    static int[] a, b, c;
    static boolean[] carries;
    static int min_bit = 0;
    static int max_bit = Integer.MAX_VALUE;

    public static void initC(int size) {
        c = new int[size / NBITS + 2];
        carries = new boolean[size / NBITS + 2];
    }

    public static void computeC(int[] a, int[] b, boolean force) {
        int size = a.length;

        int start = Math.max(min_bit / NBITS - 1, 0);
        int end = Math.min(max_bit / NBITS + 1, size - 1);
        int index = start;
        int result;

        boolean carry_changed = false;

        while (index <= end || index < size && (carries[index] || carry_changed)) {
            result = a[index] + b[index];
            if (carries[index]) {
                result += 1;
            }
            if (result < 0) {
                carry_changed = carries[index + 1] == false;
                carries[index + 1] = true;
                c[index] = result ^ (1 << 31);
            } else {
                carry_changed = carries[index + 1] == true;
                carries[index + 1] = false;
                c[index] = result;
            }
            index++;
        }

        min_bit = Integer.MAX_VALUE;
        max_bit = 0;
    }

    public static int getBit(int[] n, int bit) {
        int target = bit / NBITS;
        int bit_target = bit % NBITS;

        int mask = 1 << bit_target;

        if ((n[target] & mask) == 0) {
            return 0;
        }
        return 1;
    }

    public static void setBit(int[] n, int bit, int value) {
        int target = bit / NBITS;
        int bit_target = bit % NBITS;

        int current_value = getBit(n, bit);

        if (current_value != value) {
            if (value == 0) {
                int mask = (1 << bit_target) ^ Integer.MAX_VALUE;
                n[target] &= mask;
            } else {
                n[target] |= 1 << bit_target;
            }

            min_bit = Math.min(bit, min_bit);
            max_bit = Math.max(bit, max_bit);
        }
    }

    public static int[] readNumber(String n, int size) {

        int[] ret = new int[size / NBITS + 1];

        int target = 0;
        int count = 0;
        for (int i = size - 1; i >= 0; i--) {

            char c = n.charAt(i);
            if (c == '1') {
                ret[target] |= 1 << (count % NBITS);
            }

            count++;
            if (count > 0 && count % NBITS == 0) {
                target++;
            }
        }

        return ret;
    }

    public static void main(String args[]) throws Exception {

        String tokens[];
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 50 * 1024 * 1024);

        tokens = br.readLine().split(" ");

        int size = Integer.parseInt(tokens[0]);
        int n_lines = Integer.parseInt(tokens[1]);

        String a_str = br.readLine();
        String b_str = br.readLine();
        a = readNumber(a_str, size);
        b = readNumber(b_str, size);

        initC(size);
        computeC(a, b, true);

        int count = 0;
        while (count < n_lines) {
            tokens = br.readLine().split(" ");

            if (tokens[0].equals("set_a")) {
                setBit(a, Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
            } else if (tokens[0].equals("set_b")) {
                setBit(b, Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
            } else if (tokens[0].equals("get_c")) {
                computeC(a, b, false);
                System.out.print(getBit(c, Integer.parseInt(tokens[1])));
            }
            count++;
        }

    }
}
