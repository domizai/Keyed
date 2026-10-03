package ch.domizai.keyed.lerps;

import java.util.Arrays;

// Morphs a into b with the fewest single-character edits (Levenshtein), applied left to right.
public class StringLerp implements Lerp<String> {
    private static final byte KEEP = 0, SUB = 1, INS = 2, DEL = 3;

    // Edit script of the last pair; lerp is called every frame with the same keys.
    private String cachedA, cachedB;
    private byte[] ops;
    private char[] from, to;
    private int edits;

    public String lerp(String a, String b, float t) {
        if (!a.equals(cachedA) || !b.equals(cachedB)) {
            build(a, b);
        }
        int applied = Math.round(Math.max(0, Math.min(1, t)) * edits);

        StringBuilder sb = new StringBuilder();
        int done = 0;
        for (int i = 0; i < ops.length; i++) {
            if (ops[i] == KEEP) {
                sb.append(from[i]);
                continue;
            }
            boolean apply = done++ < applied;
            if (ops[i] == SUB) {
                sb.append(apply ? to[i] : from[i]);
            } else if (ops[i] == INS) {
                if (apply) sb.append(to[i]);
            } else if (!apply) {
                sb.append(from[i]);
            }
        }
        return sb.toString();
    }

    private void build(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                d[i][j] = Math.min(d[i - 1][j - 1] + cost, Math.min(d[i - 1][j], d[i][j - 1]) + 1);
            }
        }

        // Backtrack from the end, filling the arrays from the back so they read left to right.
        int len = n + m;
        byte[] o = new byte[len];
        char[] f = new char[len], g = new char[len];
        int k = len, i = n, j = m;
        while (i > 0 || j > 0) {
            k--;
            if (i > 0 && j > 0 && d[i][j] == d[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)) {
                o[k] = a.charAt(i - 1) == b.charAt(j - 1) ? KEEP : SUB;
                f[k] = a.charAt(--i);
                g[k] = b.charAt(--j);
            } else if (i > 0 && d[i][j] == d[i - 1][j] + 1) {
                o[k] = DEL;
                f[k] = a.charAt(--i);
            } else {
                o[k] = INS;
                g[k] = b.charAt(--j);
            }
        }

        ops = Arrays.copyOfRange(o, k, len);
        from = Arrays.copyOfRange(f, k, len);
        to = Arrays.copyOfRange(g, k, len);
        edits = d[n][m];
        cachedA = a;
        cachedB = b;
    }
}
