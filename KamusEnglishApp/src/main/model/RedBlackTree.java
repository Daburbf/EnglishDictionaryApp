package main.model;

import java.util.List;
import java.util.ArrayList;

public class RedBlackTree {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private Node root;
    private final Node NIL;
    private int size;
    
    public RedBlackTree() {
        NIL = new Node(null, null);
        NIL.color = BLACK;
        root = NIL;
        size = 0;
    }

    public static class Node {
        String key;
        Word word;
        Node left, right, parent;
        boolean color;
        
        Node(String key, Word word) {
            this.key = key;
            this.word = word;
            this.color = RED;
            this.left = null;
            this.right = null;
            this.parent = null;
        }
        
        public boolean hasGimmick() {
            return word != null && word.hasGimmick();
        }
        
        public String getGimmickType() {
            return word != null ? word.getGimmickType() : null;
        }
        
        public boolean hasDefinition() {
            return word != null && word.hasDefinition();
        }
    }

    public void insert(String key, Word word) {
        Node newNode = new Node(key.toLowerCase(), word);
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
                current.word = word;
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
    
    public Word search(String key) {
        Node current = root;
        String searchKey = key.toLowerCase();
        
        while (current != NIL) {
            int cmp = searchKey.compareTo(current.key);
            if (cmp == 0) {
                return current.word;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }
    
    public Node searchNode(String key) {
        Node current = root;
        String searchKey = key.toLowerCase();
        
        while (current != NIL) {
            int cmp = searchKey.compareTo(current.key);
            if (cmp == 0) {
                return current;
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
    
    public List<Word> getRecommendations(String prefix) {
        List<Word> recommendations = new ArrayList<>();
        findRecommendationsInOrder(root, prefix.toLowerCase(), recommendations);
        return recommendations;
    }
    
    private void findRecommendationsInOrder(Node node, String prefix, List<Word> recommendations) {
        if (node == NIL) {
            return;
        }
        
        findRecommendationsInOrder(node.left, prefix, recommendations);
        
        if (recommendations.size() < 10 && node.key.startsWith(prefix)) {
            recommendations.add(node.word);
        }
        
        if (recommendations.size() < 10) {
            findRecommendationsInOrder(node.right, prefix, recommendations);
        }
    }
    
    public List<Word> getAllWordsInOrder() {
        List<Word> words = new ArrayList<>();
        collectInOrder(root, words);
        return words;
    }
    
    private void collectInOrder(Node node, List<Word> words) {
        if (node == NIL) {
            return;
        }
        
        collectInOrder(node.left, words);
        words.add(node.word);
        collectInOrder(node.right, words);
    }
    
    public List<String> getAllKeysInOrder() {
        List<String> keys = new ArrayList<>();
        collectKeysInOrder(root, keys);
        return keys;
    }
    
    private void collectKeysInOrder(Node node, List<String> keys) {
        if (node == NIL) {
            return;
        }
        
        collectKeysInOrder(node.left, keys);
        keys.add(node.key);
        collectKeysInOrder(node.right, keys);
    }
}