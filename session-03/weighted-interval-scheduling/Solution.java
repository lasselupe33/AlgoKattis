import java.util.*;

public class Solution {
  private int N;
  private int[] M;
  private Interval[] intervals;

  private void parseInput() {
    var sc = new Scanner(System.in);

    N = sc.nextInt();
    intervals = new Interval[N];

    for (int i = 0; i < N; i++) {
      int start = sc.nextInt();
      int end = sc.nextInt();
      int weight = sc.nextInt();
      intervals[i] = new Interval(start, end, weight);
    }

    M = new int[N + 1];

    sc.close();
  }

  private void sortByEnd() {
    Arrays.sort(intervals, new IntervalSorter());
  }

  private int getP(int i) {
    int low = 0;
    int high = i;

    Interval target = intervals[i];

    while (low <= high) {
      int mid = (low + high) / 2;
      Interval candidate = intervals[mid];

      if (candidate.end <= target.start) {
        Interval plusOne = intervals[mid + 1];
        if (plusOne.end <= target.start) {
          low = mid + 1;
        } else {
          return mid + 1;
        }
      } else {
        high = mid - 1;
      }
    }

    return 0;
  }

  private void constructM() {
    M[0] = 0;
    for (int i = 1; i < M.length; i++) {
      int drop = M[i - 1];
      int take = intervals[i - 1].weight + M[getP(i - 1)];
      M[i] = Math.max(drop, take);
    }
  }

  public static void main(String[] args) {
    Solution s = new Solution();
    s.parseInput();
    s.sortByEnd();
    s.constructM();
    System.out.println(s.M[s.M.length - 1]);
  }
}


class Interval {
  public int start;
  public int end;
  public int weight;

  public Interval(int start, int end, int weight) {
    this.start = start;
    this.end = end;
    this.weight = weight;
  }

  public String toString() {
    return start + " " + end + " " + weight;
  }
}

class IntervalSorter implements Comparator<Interval> {
  public int compare(Interval a, Interval b) {
    int byEnd = a.end - b.end;

    if (byEnd == 0) {
      return a.start - b.start;
    }

    return byEnd;
  }
}