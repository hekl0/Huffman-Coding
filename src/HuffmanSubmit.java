// Import any package as required


import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

class Node {
    public Character key;
    public int value;
    public Node leftChild;
    public Node rightChild;

    public Node(Character key, int value) {
        this.key = key;
        this.value = value;
        this.leftChild = null;
        this.rightChild = null;
    }

    public Node(Character key, int value, Node leftChild, Node rightChild) {
        this.value = value;
        this.key = key;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }
}

public class HuffmanSubmit implements Huffman {
    HashMap<Character, Integer> characterMap;
    HashMap<Character, String> characterEncryptedMap;

    public void encode(String inputFile, String outputFile, String freqFile) {
        //read data and map character with its frequency
        BinaryIn dataIn = new BinaryIn(inputFile);
        characterMap = new HashMap<>();
        while (!dataIn.isEmpty()) {
            char c = dataIn.readChar();

            if (characterMap.containsKey(c))
                characterMap.put(c, characterMap.get(c) + 1);
            else
                characterMap.put(c, 1);
        }

        //print frequency file
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(freqFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (Character c : characterMap.keySet()) {
            //freqOut.write();
            String output = Integer.toBinaryString(c);
            //add leading zero
            while (output.length() < 8)
                output = "0" + output;
            output = output + ":" + characterMap.get(c) + "\n";
            try {
                file.write(output.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //build huffman tree and encrypt char
        Node root = buildHuffmanTree();
        characterEncryptedMap = new HashMap<>();
        dfs(root, "");

        //reread input file and print encrypted-output file
        dataIn = new BinaryIn(inputFile);
        BinaryOut dataOut = new BinaryOut(outputFile);
        while (!dataIn.isEmpty()) {
            char c = dataIn.readChar();

            String code = characterEncryptedMap.get(c);
            for (int i = 0; i < code.length(); i++)
                if (code.charAt(i) == '0')
                    dataOut.write(false);
                else
                    dataOut.write(true);
        }

        dataOut.flush();
    }

    //build huffman tree
    Node buildHuffmanTree() {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(characterMap.keySet().size(), new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.value < o2.value) return -1;
                if (o1.value > o2.value) return 1;
                return 0;
            }
        });

        for (Character c : characterMap.keySet())
            priorityQueue.add(new Node(c, characterMap.get(c)));

        while (priorityQueue.size() > 1) {
            Node leftChild = priorityQueue.poll();
            Node rightChild = priorityQueue.poll();
            priorityQueue.add(new Node(
                    null,
                    leftChild.value + rightChild.value,
                    leftChild,
                    rightChild)
            );
        }

        return priorityQueue.poll();
    }

    void dfs(Node node, String code) {
        if (node.key != null)
            characterEncryptedMap.put(node.key, code);

        if (node.leftChild != null)
            dfs(node.leftChild, code + "0");
        if (node.rightChild != null)
            dfs(node.rightChild, code + "1");
    }


    public void decode(String inputFile, String outputFile, String freqFile) {
        //read data from frequency file and map character with its frequency
        FileInputStream file = null;
        try {
            file = new FileInputStream(freqFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
        characterMap = new HashMap<>();
        do {
            String input = null;
            try {
                input = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input == null) break;
            char key = (char) Integer.parseInt(input.split(":")[0], 2);
            int value = Integer.parseInt(input.split(":")[1]);
            characterMap.put(key, value);
        } while (true);

        //build huffman tree
        Node root = buildHuffmanTree();

        //decode encrypted-input file and print to output file
        BinaryIn dataIn = new BinaryIn(inputFile);
        BinaryOut dataOut = new BinaryOut(outputFile);
        Node currentNode = root;
        while (!dataIn.isEmpty()) {
            boolean b = dataIn.readBoolean();
            if (!b) currentNode = currentNode.leftChild;
            else currentNode = currentNode.rightChild;

            //reach leaf node
            if (currentNode.key != null) {
                dataOut.write(currentNode.key);
                currentNode = root;
            }
        }
        dataOut.flush();
    }


    public static void main(String[] args) {
        Huffman huffman = new HuffmanSubmit();
//        huffman.encode("alice30.txt", "ur.enc", "freq.txt");
//        huffman.decode("ur.enc", "test.txt", "freq.txt");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Your command should be in format (function inputFile outputFile freqFile)");
        System.out.println("Ex: encode alice30.txt ur.enc freq.txt");
        System.out.println("Ex: decode ur.enc test.txt freq.txt");
        System.out.println("Type Quit to quit");
        while (true) {
            String input = scanner.next();
            if (input.equals("encode"))
                huffman.encode(scanner.next(), scanner.next(), scanner.next());
            if (input.equals("decode"))
                huffman.decode(scanner.next(), scanner.next(), scanner.next());
            if (input.equals("Quit"))
                System.exit(0);
        }
    }

}
