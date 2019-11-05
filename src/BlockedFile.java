/**
 * Mimics the behavior on how the OS stores and deals with files.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class BlockedFile {
    private RandomAccessFile file;
    private final int NBLOCKS = 10;
    private int blockSize;
    private long dp, fp, prev, next;
    private long initOf = 16;

    /**
     * If the length of the file is 0, it is a new file. We built a blocked file from scratch if so.
     * @param file RandomAccessFile used to store data.
     */
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

    /**
     * We retrieve a String representation of both the Data Linked List and the Empty blocks Linked Lists.
     * @param isData boolean true to dump data, false to dump dummy values.
     * @return a String representation of the double LL.
     */
    public String getDataLL(boolean isData){
        try{
            StringBuilder out = new StringBuilder("");
            file.seek(0L);
            long dp = file.readLong();
            long fp = file.readLong();
            out.append("Data pointer: " + dp + " Free Pointer: " + fp + "\n");
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

    /**
     * Saving to a file requires different cases to be tested.
     * @param friend
     */
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

    /**
     * Deleting from the data DLL requires a phone number. Hence, each phone number of the friends must
     * be unique.
     * @param phoneNumber String of the phone number of the contact to delete.
     * @return boolean specifying if deletion was done properly.
     */
    public boolean deleteFromBlock(String phoneNumber){
        try{
            file.seek(0L);
            dp = file.readLong();
            fp = file.readLong();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        long head = dp;
        if(dp != -1){
            /*
            The data DLL is not empty, so we can search for the contact.
             */
            Block headBlock = getBlockAt(head);
            while(!isMatch(headBlock, phoneNumber)){
                if(isTail(headBlock)){
                    //If we are at the tail and we are still here, we have not found a match.
                    //Record does not exists, so we return false.
                    return false;
                }
                head = headBlock.getNext();
                headBlock = getBlockAt(head);
            }
            //There has been a match
            if(isHead(headBlock)){
                /*
                At this point dp -> HEAD <-> D2 ...
                What we want is dp -> D2...
                Because HEAD should be removed.
                 */
                long next = headBlock.getNext();
                dp = next;
                if(next != -1){
                    //HEAD indeed points to another node. We need to update this node.
                    Block nextBlock  = getBlockAt(next);
                    nextBlock.setPrev(-1);
                    writeBlockAt(next, nextBlock);
                }
            }else if(isTail(headBlock)){
                /*
                What we have D1 <-> D2 ... <-> HEAD
                What we want is D1 <-> D2 ... <-> PREV_HEAD
                 */
                long prevToLast = headBlock.getPrev();
                Block prevBlock = getBlockAt(prevToLast);
                prevBlock.setNext(-1);
                writeBlockAt(prevToLast, prevBlock);
            }else{
                /*
                We have the case D1 <-> D2 <-> D3 ... <-> HEAD <-> ... DN
                We want D1 <-> D2 <-> D3 ... <-> NEXT_HEAD <-> ... DN
                 */
                long prev = headBlock.getPrev();
                Block prevBlock = getBlockAt(prev);
                long next  =headBlock.getNext();
                Block nextBlock = getBlockAt(next);
                prevBlock.setNext(next);
                nextBlock.setPrev(prev);
                writeBlockAt(prev, prevBlock);
                writeBlockAt(next, nextBlock);
            }
            /*
            We need to attach this HEAD node to the empty list
             */
            long headOfFree = fp;
            if(headOfFree != -1){
                //The free is not empty
                Block freeBlock = getBlockAt(fp);
                freeBlock.setPrev(head);
                headBlock.setNext(headOfFree);
                writeBlockAt(headOfFree, freeBlock);
            }else{
                //The HEAD Node is the only new available space. So it does to nowhere.
                headBlock.setNext(-1);
            }
            /*
            We always add the HEAD NODE to the beginning of the empty block lists. Think about disk fragmentation or
            heap implementation.
             */
            headBlock.setPrev(-1);
            fp = head;
            try{
                file.seek(0L);
                file.writeLong(dp);
                file.writeLong(fp);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
            return true;
        }else{
            return false; //File is empty, so we cannot delete the record.
        }
    }

    /**
     * Insert method to insert friends
     * @param friend Friend that needs to be saved
     */
    public void insert(Friend friend){
        //We take head of the empty nodes
        long takefrom = fp;
        Block insertBlock  = getBlockAt(takefrom);
        insertBlock.setFriend(friend);
        fp = insertBlock.getNext(); //next empty node is the next of the first empty node
        Block head = getBlockAt(dp);
        long current = dp; //current head of the data DLL
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
                //We are dealing with the head
                insertBlock.setPrev(-1);
                insertBlock.setNext(current);
                head.setPrev(takefrom);
                dp = takefrom;
            }
            else if(isTail(head) && lessThan(head, insertBlock) ) {
                //We are adding the node at the right of the tail.
                insertBlock.setNext(-1);
                insertBlock.setPrev(current);
                head.setNext(takefrom);
            }else{
                //Adding the element either at the left of the tail, which involves at least two nodes.
                //So, we treat it as a normal insertion between two nodes.
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

    /**
     * Inserting the first element is quite easy.
     * @param friend
     */
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

    /**
     * Imitates memory addressing. First load address, then read/write
     * @param address long representing address
     * @return a read from file block object
     */
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

    /**
     * Used to sort the nodes in the data DLL. Compares leftBlock <= rightBlock
     * @param newBlock left block
     * @param nextBlock right block
     * @return
     */
    public boolean lessThan(Block newBlock, Block nextBlock){
        int n1 = newBlock.getFriend().getLastName().charAt(0);
        int n2 = nextBlock.getFriend().getLastName().charAt(0);
        return n1 <= n2;
    }

    /**
     * Same as getBlockAt.
     * @param address long address
     * @param block block to be read from file.
     */
    public void writeBlockAt(long address, Block block){
        try{
            file.seek(address);
            block.write(file);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Test if a node is the head of the corresponding DLL.
     * @param block Block to be tested
     * @return boolean isHead
     */
    public boolean isHead(Block block){
        return block.getPrev() == -1;
    }

    /**
     * Tests if a node is the tail of a corresponding DLL.
     * @param block Block to be tested
     * @return boolean isTail
     */

    public boolean isTail(Block block){
        return block.getNext() == -1;
    }

    /**
     * Test if a block has a phone number
     * @param block Block to test
     * @param phone that is the test criteria
     * @return boolean testing isMatch
     */
    public boolean isMatch(Block block, String phone){
        return Long.parseLong(block.getFriend().getPhone().trim()) == Long.parseLong(phone.trim());
    }


}
