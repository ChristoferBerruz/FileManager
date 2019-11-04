import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

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
                    insertFirst(friend);
                }else{
                    insert(friend);
                }
            }else{
                System.out.println("No more space in file");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void insert(Friend friend){
        long takefrom = fp;
        Block insertBlock  = getBlockAt(takefrom);
        insertBlock.setFriend(friend);
        long lastDataLoc = insertBlock.getPrev();
        Block lastDataBlock = getBlockAt(lastDataLoc);
        fp = insertBlock.getNext();
        Block head = getBlockAt(dp);
        long current = dp;
        while(!lessThan(insertBlock,head) && (head.getNext()!= takefrom)){
            current = head.getNext();
            head = getBlockAt(current);
        }
        if(head.getNext() == takefrom && current==lastDataLoc){
            insertBlock.setPrev(current);
            head.setNext(takefrom);
            writeBlockAt(takefrom, insertBlock);
            writeBlockAt(current, head);
        }else{
            if(current == dp){
                insertBlock.setPrev(-1);
                insertBlock.setNext(current);
                head.setPrev(takefrom);
                dp = takefrom;
                writeBlockAt(current, head);
                writeBlockAt(takefrom, insertBlock);
                lastDataBlock.setNext(fp);
                writeBlockAt(lastDataLoc, lastDataBlock);
            }else{
                long prev = head.getPrev();
                Block prevBlock = getBlockAt(prev);
                insertBlock.setPrev(prev);
                insertBlock.setNext(current);
                head.setPrev(takefrom);
                if(lastDataLoc==current){
                    head.setNext(fp);
                    prevBlock.setNext(takefrom);
                    writeBlockAt(current, head);
                    writeBlockAt(prev, prevBlock);
                    writeBlockAt(takefrom, insertBlock);
                }else{
                    prevBlock.setNext(takefrom);
                    writeBlockAt(current, head);
                    writeBlockAt(prev, prevBlock);
                    writeBlockAt(takefrom, insertBlock);
                    lastDataBlock.setNext(fp);
                    writeBlockAt(lastDataLoc, lastDataBlock);
                }
            }
        }
        try{
            file.seek(0L);
            file.writeLong(dp);
            file.writeLong(fp);
        }catch (Exception e){

        }

    }

    public void insertFirst(Friend friend){
        long saveLoc = fp;
        Block block = getBlockAt(saveLoc);
        block.setFriend(friend);
        writeBlockAt(saveLoc, block);
        dp = fp;
        fp = block.getNext();
        try {
            file.seek(0L);
            file.writeLong(dp);
            file.writeLong(fp);
        }catch (IOException e){
            System.out.println(e.getMessage());
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
        int n1 = newBlock.getFriend().getLastName().charAt(0);
        int n2 = nextBlock.getFriend().getLastName().charAt(0);
        return n1 <= n2;
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
