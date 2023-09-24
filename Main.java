import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain(2);

        Block block1 = new Block(blockchain.blockchain.get(blockchain.blockchain.size() - 1).getHash(), 2);
        block1.addTransaction(new Transaction("Nurba", "Ablik", 80));
        block1.addTransaction(new Transaction("Ablik", "MahdiBrow", 50));
        blockchain.addBlock(block1);

        //Scanner sc = new Scanner(System.in);
        //System.out.println(block1.calculateHash());


        boolean isBlockchainValid = blockchain.isChainValid();
        //System.out.println("Is blockchain valid? " + isBlockchainValid);
    }
}

