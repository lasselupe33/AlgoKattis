import java.util.*;

public class Solution {
  int N;
  Doll[] d;
  S[] dp;
  int W;
  int H;

  private void solve(int W, int H) {
    for (int i = 0; i < d.length; i++) {
      if (i == 0) {
        dp[0] = new S(W, H, 0);
      } else {
        Doll current = d[i - 1];
        S prevS = dp[i - 1];

        if (current.h < prevS.h && current.w < prevS.h) {
          S drop = dp[i - 1];
          S take = new S(current.w, current.h, prevS.inside + 1);
          dp[i] = take;
        } else {
          dp[i] = dp[i - 1];
        }
      }
    }
  }

  private void parseNextCase(Scanner sc) {
    N = sc.nextInt();
    d = new Doll[N];

    int maxHeight = 0;
    int maxWidth = 0;

    for (int i = 0; i < N; i++) {
      int width = sc.nextInt();
      int height = sc.nextInt();

      maxHeight = Math.max(height, maxHeight);
      maxWidth = Math.max(width, maxWidth);
      d[i] = new Doll(width, height);
    }

    dp = new S[N];
    W = maxWidth;
    H = maxHeight;
  }

  public static void main(String[] args) {
    var sc = new Scanner(System.in);
    int cases = sc.nextInt();

    var s = new Solution();
    s.parseNextCase(sc);
    s.solve(s.W, s.H);


    // Sort by highest price
    // 

    sc.close();
  }
}

class S {
  public int w;
  public int h;
  public int inside;

  public S(int w, int h, int inside) {
    this.w = w;
    this.h = h;
    this.inside = inside;
  }
}

class Doll {
  public int w;
  public int h;

  public Doll(int w, int h) {
    this.w = w;
    this.h = h;
  }
}