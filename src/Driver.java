import java.io.RandomAccessFile;

public class Driver {
    public static void main(String[] args) {
        try{
            RandomAccessFile file = new RandomAccessFile("block2.data", "rw");
            System.out.println(file.length());
            BlockedFile blockedFile = new BlockedFile(file);
            Friend friend = new Friend("Aarsh", "Lardani", "9036022554");
            blockedFile.saveToFile(friend);
            blockedFile.printToConsole();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
