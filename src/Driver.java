import java.io.RandomAccessFile;

public class Driver {
    public static void main(String[] args) {
        try{
            RandomAccessFile file = new RandomAccessFile("block.data", "rw");
            System.out.println(file.length());
            BlockedFile blockedFile = new BlockedFile(file);
            blockedFile.printToConsole();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
