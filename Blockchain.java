import java.util.ArrayList;
import java.util.List;

public class Blockchain {
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
