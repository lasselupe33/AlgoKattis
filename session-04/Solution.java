import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

// 4 3 1
// 4 children - 3 toys - 1 category
// n lines
// 2 1 2
// child[0] likes toys [1,2]
// chlid[1] likes toys [1,2]
// child[2] likes toys [3]
// child[3] likes toys [3]
// categories
// category1 has toys [1,2] and has a capacity of 1
// all has toys [3] and a capacity of 1

public class Solution {
  HashMap<Integer, Child> children;
  HashMap<Integer, Toy> toys;
  HashMap<Integer, Category> categories;
  Vertex start;
  Vertex dest;
  HashSet<Vertex> vertices;
  HashMap<String, Edge> edges;

  public static void main(final String[] args) {
    final var s = new Solution();
    s.parse();
    s.constructGraph();
    s.fordFulkerson(s.start, s.dest);
    System.out.println(s.getRes(s.start));
  }

  private int getRes(Vertex v) {
    int maxFlow = 0;

    for (var edge : v.out) {
      maxFlow += edge.getFlow();
    }

    return maxFlow;
  }

  private void fordFulkerson(final Vertex start, final Vertex dest) {
    ArrayList<Vertex> currPath = dfs(start, dest);

    while (currPath.size() > 0) {
      augment(currPath);
      currPath = dfs(start, dest);
    }
  }

  private void augment(final ArrayList<Vertex> path) {
    // We cannot work without any edges...
    if (path.size() <= 2) {
      return;
    }

    int bottleneck = bottleneck(path);

    for (int i = 1; i < path.size(); i++) {
      Vertex v = path.get(i - 1);
      Vertex u = path.get(i);

      boolean isForward = true;
      Edge e = edges.get(v.id + " " + u.id);

      if (e == null) {
        isForward = false;
        e = edges.get(u.id + " " + v.id);
      }

      if (isForward) {
        e.setRes(e.res - bottleneck);
      } else {
        e.setReverseRes(e.reverseRes - bottleneck);
      }
    }
  }

  private int bottleneck(final ArrayList<Vertex> path) {
    // We need at least to vertices to be able to look at edges..
    if (path.size() <= 2) {
      return 0;
    }

    int bottleneck = Integer.MAX_VALUE;

    for (int i = 1; i < path.size(); i++) {
      Vertex v = path.get(i - 1);
      Vertex u = path.get(i);

      boolean isForward = true;
      Edge e = edges.get(v.id + " " + u.id);

      if (e == null) {
        isForward = false;
        e = edges.get(u.id + " " + v.id);
      }

      bottleneck = Math.min(isForward ? e.res : e.reverseRes, bottleneck);
    }

    return bottleneck;
  }

  private ArrayList<Vertex> dfs(final Vertex start, final Vertex dest) {
    final HashSet<String> visited = new HashSet<>();
    final HashMap<Vertex, Vertex> mapping = new HashMap<>();

    var stack = new ArrayDeque<Vertex>();
    stack.add(start);
    visited.add(start.id);

    while (stack.size() > 0) {
      Vertex v = stack.pollLast();

      // Forward edges
      for (Edge e : v.out) {
        // We cannot use edges that have been filled..
        if (e.res == 0) {
          continue;
        }

        Vertex u = e.to;

        if (u == dest) {
          // Found dest!!
          mapping.put(dest, v);
          return dfsBacktrack(dest, start, mapping);
        }

        if (!visited.contains(u.id)) {
          visited.add(u.id);
          stack.add(u);
          mapping.put(u, v);
        }
      }

      // Backward edges
      for (Edge e : v.rev) {
        // Don't bother using a backward edge that is unusable
        if (e.reverseRes == 0) {
          continue;
        }

        Vertex u = e.from;

        if (u == dest) {
          return new ArrayList<Vertex>();
        }

        if (!visited.contains(u.id)) {
          visited.add(u.id);
          stack.add(u);
          mapping.put(u, v);
        }
      }
    }

    return new ArrayList<Vertex>();
  }

  private ArrayList<Vertex> dfsBacktrack(Vertex dest, Vertex start, HashMap<Vertex, Vertex> mapping) {
    ArrayList<Vertex> path = new ArrayList<>();
    Vertex curr = dest;

    while (curr != null) {
      path.add(curr);
      curr = mapping.get(curr);
    }

    // Reorder from end->start to start->end
    Collections.reverse(path);

    return path;
  }

  private void constructGraph() {
    start = new Vertex("start");
    dest = new Vertex("dest");
    edges = new HashMap<>();

    // Add start-node to children
    final var childVertices = new HashMap<Child, Vertex>();
    final var toyVertices = new HashMap<Toy, Vertex>();
    final var categoryVertices = new HashMap<Category, Vertex>();

    for (final var childEntry : children.entrySet()) {
      final var child = childEntry.getValue();

      final Vertex childVertex = new Vertex("child-" + child.childId);
      childVertices.put(child, childVertex);

      final Edge edge = new Edge(1, start, childVertex);
      edges.put(start.id + " " + childVertex.id, edge);
      start.out.add(edge);
      childVertex.rev.add(edge);

      // Add children to toys
      for (final var toyId : child.likes) {
        final Toy toy = toys.get(toyId);
        Vertex toyVertex = toyVertices.get(toy);

        if (toyVertex == null) {
          toyVertex = new Vertex("toy-" + toyId);
          toyVertices.put(toy, toyVertex);
        }

        final Edge childToToyEdge = new Edge(1, childVertex, toyVertex);
        edges.put(childVertex.id + " " + toyVertex.id, childToToyEdge);
        childVertex.out.add(childToToyEdge);
        toyVertex.rev.add(childToToyEdge);
      }
    }

    // Add toys to categories
    for (final var toyVertexEntry : toyVertices.entrySet()) {
      final Toy toy = toyVertexEntry.getKey();
      final Category category = categories.get(toy.belongsToId);
      final Vertex toyVertex = toyVertexEntry.getValue();
      Vertex catVertex = categoryVertices.get(category);

      if (catVertex == null) {
        catVertex = new Vertex("category-" + category.categoryId);
        categoryVertices.put(category, catVertex);
      }

      final Edge toyToCatEdge = new Edge(1, toyVertex, catVertex);
      edges.put(toyVertex.id + " " + catVertex.id, toyToCatEdge);
      toyVertex.out.add(toyToCatEdge);
      catVertex.rev.add(toyToCatEdge);
    }

    // Add categories to dest
    for (final var categoryVertexEntry : categoryVertices.entrySet()) {
      final Category cat = categoryVertexEntry.getKey();
      final Vertex catVertex = categoryVertexEntry.getValue();
      final Edge toDest = new Edge(cat.capacity, catVertex, dest);
      edges.put(catVertex.id + " " + dest.id, toDest);
      catVertex.out.add(toDest);
      dest.rev.add(toDest);
    }

    // Combine vertices to create graph!
    vertices = new HashSet<>();
    vertices.add(start);
    vertices.addAll(childVertices.values());
    vertices.addAll(toyVertices.values());
    vertices.addAll(categoryVertices.values());
    vertices.add(dest);
  }

  private void parse() {
    final var sc = new Scanner(System.in);

    final int N = sc.nextInt();
    final int M = sc.nextInt();
    final int P = sc.nextInt();

    children = new HashMap<>();
    toys = new HashMap<>();
    categories = new HashMap<>();

    // Parse children
    for (int i = 0; i < N; i++) {
      final int toScan = sc.nextInt();
      final Child child = new Child(i);

      for (int j = 0; j < toScan; j++) {
        final int toyId = sc.nextInt();
        child.addToyPreference(toyId);
      }

      children.put(i, child);
    }

    final HashMap<Integer, Toy> uncategorizedToys = new HashMap<>();
    for (int i = 1; i <= M; i++) {
      final Toy toy = new Toy(i);
      uncategorizedToys.put(i, toy);
    }

    // Categorize toys (Create edges)
    for (int i = 0; i < P; i++) {
      final int toScan = sc.nextInt();
      final Category category = new Category(i);

      for (int j = 0; j < toScan; j++) {
        final int toyId = sc.nextInt();
        final Toy toy = uncategorizedToys.get(toyId);
        uncategorizedToys.remove(toyId);
        toy.belongsToId = i;
        toys.put(toyId, toy);
      }

      final int cap = sc.nextInt();
      category.capacity = cap;
      categories.put(i, category);
    }

    // Finally assign remainging toys to uncategorized category
    final Category uncategorizedCategory = new Category(-1);
    uncategorizedCategory.capacity = uncategorizedToys.size();
    for (final var entry : uncategorizedToys.entrySet()) {
      final Toy toy = entry.getValue();
      toy.belongsToId = -1;
      toys.put(toy.toyId, toy);
    }
    categories.put(-1, uncategorizedCategory);

    sc.close();
  }
}

class Child {
  public int childId;
  public Set<Integer> likes = new HashSet<>();

  public Child(final int id) {
    this.childId = id;
  }

  public void addToyPreference(final int toyId) {
    likes.add(toyId);
  }

  public String toString() {
    return "Child(ID=" + childId + ", TL=" + likes.toString() + ")";
  }
}

class Vertex {
  public String id;
  public ArrayList<Edge> out = new ArrayList<>();
  public ArrayList<Edge> rev = new ArrayList<>();

  public Vertex(final String id) {
    this.id = id;
  }

  public String toString() {
    return "Vertex(" + id + "): Out = " + out.size();
  }
}

class Edge {
  public int cap;
  public int reverseRes;
  public int res;
  public Vertex to;
  public Vertex from;

  public Edge(final int cap, final Vertex from, final Vertex to) {
    this.cap = cap;
    this.res = cap;
    this.reverseRes = 0;
    this.to = to;
    this.from = from;
  }

  public int getFlow() {
    return this.cap - this.res;
  }

  public void setRes(int res) {
    this.res = res;
    this.reverseRes = this.cap - this.res;
  }

  public void setReverseRes(int res) {
    this.reverseRes = res;
    this.res = this.cap - this.reverseRes;
  }

  public String toString() {
    return "Edge(C=" + cap + ", R=" + res + ", RR=" + reverseRes + "): From [" + this.from.toString() + "], To [" + this.to.toString() + "]";
  }
}

class Toy {
  public int toyId;
  public int belongsToId;

  public Toy(final int id) {
    this.toyId = id;
  }

  public String toString() {
    return "Toy(ID=" + toyId + ", BT=" + belongsToId + ")";
  }
}

class Category {
  public int categoryId;
  public int capacity;

  public Category(final int id) {
    this.categoryId = id;
  }

  public String toString() {
    return "Category(ID=" + categoryId + ", C=" + capacity + ")";
  }
}