import java.util.*;

class Scheduler {
  public static void main(String[] args) {
    Scheduler s = new Scheduler();
    s.parseInput();
    s.sortByEnd();
    s.extractOptimalSequence();

    System.out.println(s.optimal.size());
  }

  public Interval[] intervals;
  public ArrayList<Interval> optimal = new ArrayList<>();

  private void parseInput() {
    var sc = new Scanner(System.in);

    int n = sc.nextInt();
    intervals = new Interval[n];

    for (int i = 0; i < n; i++) {
      int start = sc.nextInt();
      int end = sc.nextInt();
      intervals[i] = new Interval(start, end);
    }

    sc.close();
  }

  private void sortByEnd() {
    Arrays.sort(intervals, new IntervalSorter());
  }

  private void extractOptimalSequence() {
    if (intervals.length == 0) {
      return;
    }

    var compareTo = intervals[0];
    optimal.add(compareTo);

    for (int i = 1; i < intervals.length; i++) {
      var current = intervals[i];

      if (compareTo.end <= current.start) {
        optimal.add(current);
        compareTo = current;
      }
    }
  }
}

class Interval {
  public int start;
  public int end;

  public Interval (int start, int end) {
    this.start = start;
    this.end = end;
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