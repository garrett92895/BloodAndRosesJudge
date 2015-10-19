//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.io.IOException;
//import java.io.PipedInputStream;
//import java.io.PipedOutputStream;
//import java.io.PrintStream;
//import java.util.ArrayList;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//
//public class Window extends JFrame implements KeyListener{
//
//    private static final long serialVersionUID = -8226175303388202757L;
//    private JTextArea text;
//    private JPanel jp;
//    private PipedOutputStream pOut;
//    private PrintStream out;
//    private	ExternalClassLoader ecl;
//
//    public Window(){
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setTitle("JConsole V0.1 ~Dm430");
//        setResizable(false);
//
//        jp = new JPanel();
//        jp.setPreferredSize(new Dimension(800, 400));
//        text = new JTextArea(24, 70);
//        text.addKeyListener(this);
//        jp.add(new JScrollPane(text), BorderLayout.CENTER);
//
//        add(jp);
//        pack();
//        setLocationRelativeTo(null);
//        setVisible(true);
//        initializeStreams();
//        setSystem();
//        ecl = new ExternalClassLoader();
//        loadPlugins();
//        new CommandHandler(this);
//    }
//
//    private void initializeStreams(){
//        pOut = new PipedOutputStream();
//        out = new PrintStream(new TextAreaOutputStream(text));
//    }
//
//    private void setSystem(){
//        System.setOut(out);
//        try {
//            System.setIn(new PipedInputStream(pOut));
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }//new ByteArrayInputStream("lol".getBytes()));
//    }
//
//    public void loadPlugins(){
//        ecl.Invoke();
//    }
//
//    public ExternalClassLoader getClassLoader(){
//        return ecl;
//    }
//
//    public JTextArea getTextFeild(){
//        return text;
//    }
//
//    public static void main(String [] args){
//        new Window();
//    }
//
//    ArrayList<Character> ke = new ArrayList<Character>();
//
//    @Override
//    public void keyPressed(KeyEvent e) {}
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//        if (e.getKeyCode() == KeyEvent.VK_ENTER){
//            for (Character c: ke){
//                try { pOut.write(c); } catch (IOException ex) {}
//            }
//            ke.clear();
//        }
//    }
//
//    @Override
//    public void keyTyped(KeyEvent e) {
//        ke.add(e.getKeyChar());
//    }
//
//}