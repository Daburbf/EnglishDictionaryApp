package main.model;

public class RedBlackTree {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private Node root;
    private final Node NIL;
    private int size;
    
    public RedBlackTree() {
        NIL = new Node("", "");
        NIL.color = BLACK;
        root = NIL;
        size = 0;
    }

    public static class Node {
        String key;
        String value;
        Node left, right, parent;
        boolean color;
        
        Node(String key, String value) {
            this.key = key;
            this.value = value;
            this.color = RED;
        }
    }

    
    public void insert(String key, String value) {
        Node newNode = new Node(key.toLowerCase(), value);
        newNode.left = NIL;
        newNode.right = NIL;
        
        Node parent = null;
        Node current = root;

        while (current != NIL) {
            parent = current;
            int cmp = key.toLowerCase().compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {

                current.value = value;
                return;
            }
        }
        
        newNode.parent = parent;
        
        if (parent == null) {
            root = newNode;
        } else if (key.toLowerCase().compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        if (newNode.parent == null) {
            newNode.color = BLACK;
            size++;
            return;
        }
        
        if (newNode.parent.parent == null) {
            size++;
            return;
        }
        
        fixInsert(newNode);
        size++;
    }
    
    public String search(String key) {
        Node current = root;
        String searchKey = key.toLowerCase();
        
        while (current != NIL) {
            int cmp = searchKey.compareTo(current.key);
            if (cmp == 0) {
                return current.value;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }
    
    public boolean contains(String key) {
        return search(key) != null;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return root == NIL;
    }
    
    public void clear() {
        root = NIL;
        size = 0;
    }
    
    private void fixInsert(Node node) {
        while (node.parent != null && node.parent.color == RED) {
            if (node.parent == node.parent.parent.right) {
                Node uncle = node.parent.parent.left;
                if (uncle.color == RED) {
                    uncle.color = BLACK;
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rightRotate(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    leftRotate(node.parent.parent);
                }
            } else {
                Node uncle = node.parent.parent.right;
                if (uncle.color == RED) {
                    uncle.color = BLACK;
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        leftRotate(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rightRotate(node.parent.parent);
                }
            }
            if (node == root) break;
        }
        root.color = BLACK;
    }
    
    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        
        if (y.left != NIL) {
            y.left.parent = x;
        }
        
        y.parent = x.parent;
        
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        
        y.left = x;
        x.parent = y;
    }
    
    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        
        if (y.right != NIL) {
            y.right.parent = x;
        }
        
        y.parent = x.parent;
        
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        
        y.right = x;
        x.parent = y;
    }
}