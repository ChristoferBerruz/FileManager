import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.RandomAccessFile;

public class MainMenu extends JFrame{
    private JTextField txtFName;
    private JPanel mainPanel;
    private JTextField txtLName;
    private JTextField txtPhone;
    private JButton searchButton;
    private JButton saveButton;
    private JTextArea resultTxtA;
    private RandomAccessFile file;
    private final int BLOCK_SIZE = 88;
    private final int MAX_BLOCKS = 100;

    public MainMenu(){
        initFrame();
        try{
            file = new RandomAccessFile("block.dat", "rw"); // File that we want to write to
            ActionListener listener = buttonListener(); //Action listener for the two buttons
            saveButton.addActionListener(listener);
            searchButton.addActionListener(listener);
        }catch (Exception e){
            JOptionPane.showMessageDialog(mainPanel, e.getMessage());
        }
    }


    public void initFrame(){
        this.setTitle("File Manage Application");
        this.setSize(650, 650);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(this);
        this.add(mainPanel);
    }

    public ActionListener buttonListener(){
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                switch (command){
                    case "Save":
                        String fname = txtFName.getText();
                        String lname = txtLName.getText();
                        String phone = txtPhone.getText();
                        try{
                            Friend friend = new Friend(fname, lname, phone);
                            saveBlock(friend);
                        }catch (FriendException fe){
                            JOptionPane.showMessageDialog(mainPanel, fe.getMessage());
                        }
                        resultTxtA.setText("");
                        break;
                    case "Search":
                        /**
                         * Search uses a phone number to be used for searching.
                         */
                        String ph = txtPhone.getText();
                        if(ph.length() != 10){
                            JOptionPane.showMessageDialog(mainPanel, "Search uses a 10 digit number.");
                        }else{
                        findBlock(ph);
                        }
                        break;
                }
            }
        };
        return listener;
    }

    /**
     * This method tries to save to a RandomAccessFile
     * We set the pointer to the current length of the file. Let loc be the pointer.
     * 1) Loc == 0: No blocks, prev = -1, next = BLOCK_SIZE
     * 2) Loc == BLOCK_SIZE*MAX_BLOCKS: File is full. No writing allowed.
     * 3) Loc == BLOCK_SIZE*(MAX_BLOCKS-1): Last possible block; next = -1;
     * All other cases for loc is normal insertion.
     * @param friend friend that we are trying to save.
     */
    public void saveBlock(Friend friend){
        try{
            file.seek(file.length());
            int loc = (int)file.getFilePointer();
            Block block = new Block(-11,-11); //Used as an Object reference.
            switch(loc){
                case BLOCK_SIZE*MAX_BLOCKS:
                    JOptionPane.showMessageDialog(mainPanel, "No more space in file.");
                    break;
                case 0:
                    block = new Block(friend, -1, BLOCK_SIZE);
                    break;
                case BLOCK_SIZE*(MAX_BLOCKS-1):
                    block = new Block(friend, loc-BLOCK_SIZE, -1);
                    break;
                default:
                    block = new Block(friend, loc-BLOCK_SIZE, loc+BLOCK_SIZE);
                    break;
            }
            if(block.getPrev() != -11){
                //Block instance was not referenced to a valid block object.
                block.write(file);
                JOptionPane.showMessageDialog(mainPanel, "Your friend was successfully saved.");
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(mainPanel, e.getMessage());
        }
    }

    public static void main(String[] args){
        try{
            new MainMenu().setVisible(true);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * We are trying to find a block object by only using a phone number.
     * @param phone string that we are trying to look for.
     */
    public void findBlock(String phone){
        try{
            file.seek(0L);
            Block block = new Block(-1, 1);
            while(true){
                block = block.read(file);
                String phone1 = block.getFriend().getPhone();
                if(phone1.equals(phone)){
                    break;
                }
            }
            resultTxtA.setText(block.getFriend().toString());
        }catch (Exception e){
            resultTxtA.setText("Your friend was not found!");
        }
    }

}
