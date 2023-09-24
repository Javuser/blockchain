import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain(2);

        Block block1 = new Block(blockchain.blockchain.get(blockchain.blockchain.size() - 1).getHash(), 2);
        block1.addTransaction(new Transaction("Nurbakyt", "Abylaykhan", 80));
        block1.addTransaction(new Transaction("Abylaykhan", "Mahdi", 50));
        blockchain.addBlock(block1);

        //Scanner sc = new Scanner(System.in);
        //System.out.println(block1.calculateHash());

        boolean isBlockchainValid = blockchain.isChainValid();
        //System.out.println("Is blockchain valid? " + isBlockchainValid);
    }
}

class Transaction {
    private String sender;
    private String recipient;
    private double amount;

    public Transaction(String sender, String recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return sender + " -> " + recipient + ": " + amount + " BTC";
    }
}

class Block {
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

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        merkleRoot = calculateMerkleRoot();
    }

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

class Blockchain {
    List<Block> blockchain;
    private int difficulty;

    public Blockchain(int difficulty) {
        this.blockchain = new ArrayList<>();
        this.difficulty = difficulty;
        createGenesisBlock();
    }

    private void createGenesisBlock() {
        Block genesisBlock = new Block("0", difficulty);
        blockchain.add(genesisBlock);

    }

    public void addBlock(Block block) {
        blockchain.add(block);
        block.mine();
    }

    public boolean isChainValid() {
        for (int i = 1; i < blockchain.size(); i++) {
            Block currentBlock = blockchain.get(i);
            Block previousBlock = blockchain.get(i - 1);


            if (!currentBlock.isBlockValid()) {
                return false;
            }


            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
        }
        return true;
    }
}

