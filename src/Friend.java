import java.io.RandomAccessFile;

public class Friend {
    /*
    * Friend uses StringBuffers to set a limit to the length of the properties.
    * */
    private StringBuffer firstName, lastName, phone;
    private int nameLength = 15;
    private int phoneLength = 10;
    private int friendSize = 80;

    public Friend(){
        this.firstName = new StringBuffer("VOID");
        this.lastName = new StringBuffer("VOID");
        this.phone = new StringBuffer("9999999999");
        this.firstName.setLength(nameLength);
        this.lastName.setLength(nameLength);
        this.phone.setLength(phoneLength);
    }

    /**
     * Default constructor of the friend class
     * @param firstName firstName
     * @param lastName lastName
     * @param phone phone
     * @throws FriendException: we ensure a 10-digit phone is entered (used for searching)
     */
    public Friend(String firstName, String lastName, String phone)throws FriendException{
        if(phone.length() != 10){
            throw new FriendException("A numbers MUST be entered.");
        }
        if(firstName.length() < 1){
            this.firstName = new StringBuffer("FNAME");
        }else{
            this.firstName = new StringBuffer(firstName);
        }
        if(lastName.length() < 1){
            this.lastName = new StringBuffer("LNAME");
        }else{
            this.lastName = new StringBuffer(lastName);
        }
        this.phone = new StringBuffer(phone);
        this.firstName.setLength(nameLength);
        this.lastName.setLength(nameLength);
        this.phone.setLength(phoneLength);
    }

    /**
     * Writing a Friend object to a file.
     * @param file RandomAccessFile
     */
    public void write(RandomAccessFile file){
        try{
            file.writeChars(firstName.toString());
            file.writeChars(lastName.toString());
            file.writeChars(phone.toString());
        }catch (Exception e){
        }
    }

    /**
     * Read method to a file
     * @param file RandomAcessFile
     * @return a Friend instance (maybe null)
     */
    public Friend read(RandomAccessFile file){
        try{
            char[] fname = new char[nameLength];
            for(int i = 0; i < nameLength; i++){
                fname[i] = file.readChar();
            }
            char[] lname = new char[nameLength];
            for(int i = 0; i < nameLength; i++){
                lname[i] = file.readChar();
            }
            char[] phone = new char[phoneLength];
            for(int i = 0; i < phoneLength; i++){
                phone[i] = file.readChar();
            }
            String first = new String(fname);
            String last = new String(lname);
            String phoneN = new String(phone);
            return new Friend(first, last, phoneN);
        }catch (Exception e){
        }
        return null;
    }

    /**
     * toString() overriding
     * @return String representation of Friend
     */
    public String toString(){
        return String.format("%s, %s: %s\n", firstName.toString().trim(),
                lastName.toString().trim(), phone.toString().trim());
    }

    public int getFriendSize() {
        return friendSize;
    }

    public String getPhone() {
        return phone.toString();
    }

    public String getFirstName() {
        return firstName.toString();
    }

    public String getLastName() {
        return lastName.toString();
    }
}//End of class
