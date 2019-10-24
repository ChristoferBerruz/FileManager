/**
 * Block Object that is actually stored in a file.
 */

import java.io.RandomAccessFile;

public class Block {
    private Friend friend;
    private int prev, next;
    private int blockSize = 88;

    public Block(int prev, int next){
        friend = new Friend();
        this.prev = prev;
        this.next = next;
    }

    public Block(Friend friend, int prev, int next){
        this.friend = friend;
        this.prev = prev;
        this.next = next;
    }

    public void write(RandomAccessFile file){
        friend.write(file);
        try{
            file.writeInt(prev);
            file.writeInt(next);
        }catch (Exception e){

        }
    }

    public int getPrev() {
        return prev;
    }

    public Friend getFriend() {
        return friend;
    }

    public int getNext() {
        return next;
    }

    public Block read(RandomAccessFile file){
        Friend friend = new Friend().read(file);
        try{
            int prev = file.readInt();
            int next = file.readInt();
            return new Block(friend, prev, next);
        }catch (Exception e){

        }
        return null;
    }

    public int getBlockSize() {
        return blockSize;
    }
}
