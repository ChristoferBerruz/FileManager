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

    public String getDataLL(boolean isData){
        try{
            StringBuilder out = new StringBuilder("");
            file.seek(0L);
            long dp = file.readLong();
            long fp = file.readLong();
            if(!isData){
               dp = fp;
            }
            out.append(dp + " ");
            file.seek(dp);
            Block block;
            do{
                block = new Block(-1,-1);
                block.read(file);
                out.append(block.toString() + "\n");
                if(block.getNext() != -1){
                    file.seek(block.getNext());
                    out.append(block.getNext() + " ");
                }else{
                    break;
                }
            }while(true);
            return out.toString();
        }catch (IOException e){
            System.out.println("BlockedFile | printToConsole: " + e.getMessage());
        }
        return "DUMMY";
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
        fp = insertBlock.getNext();
        Block head = getBlockAt(dp);
        long current = dp;
        while(!lessThan(insertBlock,head) && !isTail(head)){
            current = head.getNext();
            head = getBlockAt(current);
        }
        if(isTail(head) && isHead(head)) //Only one element
        {
            if(lessThan(insertBlock, head))
            {
                insertBlock.setPrev(-1);
                insertBlock.setNext(current);
                head.setPrev(takefrom);
                head.setNext(-1);
                dp = takefrom;
            }else {
                head.setNext(takefrom);
                insertBlock.setPrev(current);
                insertBlock.setNext(-1);
            }
        }else {
            //Multiple elements
            if(isHead(head)){
                insertBlock.setPrev(-1);
                insertBlock.setNext(current);
                head.setPrev(takefrom);
                dp = takefrom;
            }
            else if(isTail(head) && lessThan(head, insertBlock) ) {
                insertBlock.setNext(-1);
                insertBlock.setPrev(current);
                head.setNext(takefrom);
            }else{
                long prev = head.getPrev();
                Block prevBlock = getBlockAt(prev);
                prevBlock.setNext(takefrom);
                insertBlock.setPrev(prev);
                insertBlock.setNext(current);
                head.setPrev(takefrom);
                writeBlockAt(prev, prevBlock);
            }
        }
        writeBlockAt(current, head);
        writeBlockAt(takefrom, insertBlock);
        try{
            file.seek(0L);
            file.writeLong(dp);
            file.writeLong(fp);
        }catch (Exception e){

        }

    }

    public void insertFirst(Friend friend){
        long saveLoc = fp;
        dp = fp;
        Block block = getBlockAt(saveLoc);
        block.setFriend(friend);
        fp = block.getNext();
        block.setNext(-1);
        writeBlockAt(saveLoc, block);
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

    public boolean isHead(Block block){
        return block.getPrev() == -1;
    }

    public boolean isTail(Block block){
        return block.getNext() == -1;
    }


}
