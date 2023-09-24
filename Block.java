import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Block {
    private String previousHash;
    private List<Transaction> transactions;
    private String merkleRoot;
    private String hash;
    private long timestamp;
    private int nonce;
    private int difficulty;

    public Block(String previousHash, int difficulty) {
        this.previousHash = previousHash;
        this.transactions = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0;
        this.difficulty = difficulty;
        this.hash = mineBlock();
    }
    // Func adding Transaction
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        merkleRoot = calculateMerkleRoot();
    }
    // Func Calculate root
    public String calculateMerkleRoot() {
        List<String> tree = new ArrayList<>();
        for (Transaction transaction : transactions) {
            tree.add(transaction.toString());
        }

        while (tree.size() > 1) {
            List<String> newTree = new ArrayList<>();
            for (int i = 0; i < tree.size() - 1; i += 2) {
                String combined = tree.get(i) + tree.get(i + 1);
                String hash = applySHA256(combined);
                newTree.add(hash);
            }
            if (tree.size() % 2 == 1) {
                newTree.add(tree.get(tree.size() - 1));
            }
            tree = newTree;
        }

        return tree.get(0);
    }

    private String applySHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String mineBlock() {
        String target = new String(new char[difficulty]).replace('\0', '0');
        String data = previousHash + merkleRoot + timestamp + nonce;
        String hashedData = applySHA256(data);
        while (!hashedData.substring(0, difficulty).equals(target)) {
            nonce++;
            data = previousHash + merkleRoot + timestamp + nonce;
            hashedData = applySHA256(data);
        }
        return hashedData;
    }

    public String calculateHash() {
        String data = previousHash + merkleRoot + timestamp + nonce;
        return applySHA256(data);
    }

    public boolean isBlockValid() {
        return hash.substring(0, difficulty).equals(new String(new char[difficulty]).replace('\0', '0'));
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void mine() {
        hash = mineBlock();
    }

}
