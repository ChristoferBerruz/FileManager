import java.io.RandomAccessFile;
public class Driver {
    public static void main(String[] args) {
        try{

            RandomAccessFile file = new RandomAccessFile("block2.data", "rw");
            BlockedFile blockedFile = new BlockedFile(file);
            Friend friend = new Friend("Anastasiia", "Babenko", "2038937940");
            blockedFile.saveToFile(friend);
            blockedFile.deleteFromBlock("2038937939");
            String dataLinkedList = blockedFile.getDataLL(true);
            System.out.println(dataLinkedList);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
