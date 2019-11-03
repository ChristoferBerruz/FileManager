/**
 * Block Object that is actually stored in a file.
 */

import java.io.RandomAccessFile;

public class Block {
    private Friend friend;
    private long prev, next;
    private int blockSize = 96;

    public Block(long prev, long next){
        friend = new Friend();
        this.prev = prev;
        this.next = next;
    }

    public Block(Friend friend, long prev, long next){
        this.friend = friend;
        this.prev = prev;
        this.next = next;
    }

    public void write(RandomAccessFile file){
        friend.write(file);
        try{
            file.writeLong(prev);
            file.writeLong(next);
        }catch (Exception e){

        }
    }

    public long getPrev() {
        return prev;
    }

    public Friend getFriend() {
        return friend;
    }

    public long getNext() {
        return next;
    }

    public void read(RandomAccessFile file){
        try{
            Friend friend = new Friend();
            friend.read(file);
            long prev = file.readLong();
            long next = file.readLong();
            setFriend(friend);
            setPrev(prev);
            setNext(next);
        }catch (Exception e){
            System.out.println("Block class | read : " + e.getMessage());
        }
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public void setNext(long next) {
        this.next = next;
    }

    public void setPrev(long prev) {
        this.prev = prev;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public String toString(){
        return String.format("%s\nprev: %s next: %s", friend.toString(), prev, next);
    }
}
