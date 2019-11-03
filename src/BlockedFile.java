import java.io.IOException;
import java.io.RandomAccessFile;

public class BlockedFile {
    private RandomAccessFile file;
    private final int NBLOCKS = 10;
    private int blockSize;
    private long dp, fp, prev, next;
    private long initOf = 16;

    public BlockedFile(RandomAccessFile file){
        this.file = file;
        blockSize = new Block(-1, -1).getBlockSize();
        try{
            if(file.length() == 0){
                // File is not a blockedFile
                file.writeLong((long)-1);
                file.writeLong(16L);
                while(true){
                    long loc = file.getFilePointer();
                    if(loc == initOf){
                        Block block = new Block(-1, loc+blockSize);
                        block.write(file);
                    }else if(loc == (NBLOCKS-1)*blockSize + initOf){
                        Block block = new Block(loc - blockSize, -1);
                        block.write(file);
                        break;
                    }else{
                        Block block = new Block(loc - blockSize, loc + blockSize);
                        block.write(file);
                    }
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void printToConsole(){
        try{
            file.seek(0L);
            long dp = file.readLong();
            long fp = file.readLong();
            System.out.println(dp);
            System.out.println(fp);
            file.seek(dp);
            Block block;
            do{
                block = new Block(-1,-1);
                block.read(file);
                System.out.println(block.toString());
                if(block.getNext() != -1){
                    file.seek(block.getNext());
                }else{
                    break;
                }
            }while(true);
        }catch (IOException e){
            System.out.println("BlockedFile | printToConsole: " + e.getMessage());
        }
    }

    public void saveToFile(Friend friend){
        try{
            file.seek(0L);
            dp = file.readLong();
            fp = file.readLong();
            if(fp != -1){
                if(dp == -1){
                    //There is nothing in the file yet
                    long saveLoc = fp;
                    Block block = getBlockAt(saveLoc);
                    block.setFriend(friend);
                    writeBlockAt(saveLoc, block);
                    dp = fp;
                    fp = block.getNext();
                    file.seek(0L);
                    file.writeLong(dp);
                    file.writeLong(fp);
                }else{
                    insert(friend);
                }
            }else{
                System.out.println("No more space in file");
            }
        }catch (Exception e){

        }
    }

    public void insert(Friend friend){
        long origin = fp;
        Block block = getBlockAt(origin);
        block.setFriend(friend);
        fp = block.getNext();
        long cur = dp;
        Block currentBlock = getBlockAt(dp);
        long next = currentBlock.getNext();
        Block nextBlock = getBlockAt(next);
        if(!lessThan(currentBlock, block)){
            dp = origin;
            block.setPrev(-1);
            block.setNext(cur);

        }

    }

    public Block getBlockAt(long address){
        try{
            Block nBlock = new Block(-1,-1);
            file.seek(address);
            nBlock.read(file);
            return nBlock;
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    public boolean lessThan(Block newBlock, Block nextBlock){
        return Integer.parseInt(newBlock.getFriend().getPhone()) <=
                Integer.parseInt(nextBlock.getFriend().getPhone());
    }
    public void writeBlockAt(long address, Block block){
        try{
            file.seek(address);
            block.write(file);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
