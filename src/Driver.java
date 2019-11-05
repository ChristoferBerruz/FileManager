import java.io.RandomAccessFile;

public class Driver {
    public static void main(String[] args) {
        try{
            RandomAccessFile file = new RandomAccessFile("block2.data", "rw");
            BlockedFile blockedFile = new BlockedFile(file);
            String dataLinkedList = blockedFile.getDataLL(true);
            System.out.println(dataLinkedList);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
