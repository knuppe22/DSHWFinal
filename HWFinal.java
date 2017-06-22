import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class HWFinal {
    public static void main(String[] args) throws IOException {
        BufferedReader read = new BufferedReader(new FileReader("input.txt"));
        BufferedReader search = new BufferedReader(new FileReader("search.txt"));
        PrintWriter write = new PrintWriter("output.txt");

        RBTree t = new RBTree();
        int n;
        while (true) {
            String line = read.readLine();
            line.replaceAll(" ", "");
            if (line == null)
                break;
            n = Integer.parseInt(line);

            if (n == 0) {
                break;
            }
            else {
                if (n > 0)
                    t.rbInsert(n);
                else {
                    if (!t.isWrongDeletion(Math.abs(n)))
                        t.rbDelete(-1 * n);
                }
            }
        }
        while (true) {
            String line = search.readLine();
            line.replaceAll(" ", "");
            if (line == null)
                break;
            n = Integer.parseInt(line);

            if (n == 0) {
                break;
            }
            else {
                write.println(t.findnum(n));
            }
        }

        read.close();
        search.close();
        write.close();
    }
}

class RBTree {
    class Node {
        int val;
        Node left, right;
        Node parent;
        int color;

        Node(int newval) {
            val = newval;
            left = nil;
            right = nil;
            parent = nil;
            color = black;
        }
    }

    int nodeNum = 0;
    int insertCount = 0;
    int deleteCount = 0;

    final int red = 0;
    final int black = 1;
    public Node root;
    final Node nil = new Node(-1);
    public RBTree() {
        root = nil;
    }

    public void rbInsert(int newval) {
        rbInsert(new Node(newval));
        nodeNum++;
        insertCount++;
    }
    public void rbInsert(Node z) {
        Node y = nil;
        Node x = root;
        while (x != nil) {
            y = x;
            if (z.val < x.val)
                x = x.left;
            else
                x = x.right;
        }
        z.parent = y;
        if (y == nil)
            root = z;
        else if (z.val < y.val)
            y.left = z;
        else
            y.right = z;
        z.color = red;
        rbInsertFixup(z);
    }
    public void rbInsertFixup(Node z) {
        Node y;
        while (z.parent.color == red) {
            if (z.parent == z.parent.parent.left) {
                y = z.parent.parent.right;
                if (y.color == red) {
                    z.parent.color = black;
                    y.color = black;
                    z.parent.parent.color = red;
                    z = z.parent.parent;
                }
                else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = black;
                    z.parent.parent.color = red;
                    rightRotate(z.parent.parent);
                }
            }
            else {
                y = z.parent.parent.left;
                if (y.color == red) {
                    z.parent.color = black;
                    y.color = black;
                    z.parent.parent.color = red;
                    z = z.parent.parent;
                }
                else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = black;
                    z.parent.parent.color = red;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = black;
    }

    public void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != nil) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil)
            root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;
        y.left = x;
        x.parent = y;
    }
    public void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != nil) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil)
            root = y;
        else if (x == x.parent.right)
            x.parent.right = y;
        else
            x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    public Node treeSearch (Node tree, int val) {
        if (tree == nil)
            return nil;
        else if (val == tree.val)
            return tree;
        else if (val < tree.val)
            return treeSearch(tree.left,val);
        else
            return treeSearch(tree.right,val);
    }
    public void rbDelete(int newval) {
        Node z = treeSearch(root, newval);
        rbDelete(z);
        nodeNum--;
        deleteCount++;
    }
    public void rbDelete(Node z) {
        Node x;
        Node y = z;
        int originalColor = y.color;
        if (z.left == nil) {
            x = z.right;
            rbTransplant(z, z.right);
        }
        else if (z.right == nil) {
            x = z.left;
            rbTransplant(z, z.left);
        }
        else {
            y = treeMinimum(z.right);
            originalColor = y.color;
            x = y.right;
            if (y.parent == z)
                x.parent = y;
            else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            rbTransplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if (originalColor == black)
            rbDeleteFixup(x);
    }
    public void rbDeleteFixup(Node x) {
        Node w;
        while (x != root && x.color == black) {
            if (x == x.parent.left) {
                w = x.parent.right;
                if (w.color == red) {
                    w.color = black;
                    x.parent.color = red;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == black && w.right.color == black) {
                    w.color = red;
                    x = x.parent;
                }
                else {
                    if (w.right.color == black) {
                        w.left.color = black;
                        w.color = red;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = black;
                    w.right.color = black;
                    leftRotate(x.parent);
                    x = root;
                }
            }
            else {
                w = x.parent.left;
                if (w.color == red) {
                    w.color = black;
                    x.parent.color = red;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (w.right.color == black && w.left.color == black) {
                    w.color = red;
                    x = x.parent;
                }
                else {
                    if (w.left.color == black) {
                        w.right.color = black;
                        w.color = red;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = black;
                    w.left.color = black;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = black;
    }
    public void rbTransplant(Node u, Node v) {
        if (u.parent == nil)
            root = v;
        else if (u == u.parent.left)
            u.parent.left = v;
        else
            u.parent.right = v;
        v.parent = u.parent;
    }
    public Node treeMinimum(Node x) {
        while (x.left != nil)
            x = x.left;
        return x;
    }

    public Node successor(Node x) {
        if(x.right != nil) {
            return treeMinimum(x.right);
        }
        Node y = x.parent;
        while (y != nil && x == y.right) {
            x = y;
            y = y.parent;
        }
        return y;
    }

    public Node first() {
        Node x = root;
        while (x.left != nil) {
            x = x.left;
        }
        return x;
    }

    public String findnum(int n) {
        int prev = -1;
        int next = -1;
        Node tmp = first();
        next = tmp.val;
        while (next < n) {
            if(next == -1)
                break;
            prev = next;
            tmp = successor(tmp);
            next = tmp.val;
        }
        String str = "";
        if(next == n) {
            if (prev == -1)
                str += "NIL ";
            else
                str += (prev + " ");
            str += next;
            if (successor(tmp).val == -1)
                str += " NIL";
            else
                str += (" " + successor(tmp).val);
            return str;
        }
        else {
            if (prev == -1) {
                str += "NIL ";
            }
            else
                str += (prev + " ");
            str += "NIL";
            if (tmp.val == -1)
                str += " NIL";
            else
                str += (" " + tmp.val);
            return str;
        }
    }
    public boolean isWrongDeletion(int n) {
        return treeSearch(root, n) == nil;
    }
}