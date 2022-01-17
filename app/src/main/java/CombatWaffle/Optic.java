package CombatWaffle;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class Optic extends Canvas implements Runnable {
    
    Dimension screenDimensions;
    JFrame frame;

    Thread captureThread = new Thread(this);

    double magnification = 1.0;
    double savedMagnification = 1.0;

    Point savedLocation = new Point(0,0);

    Point globalMousePosition = new Point(0,0);
    Point localMousePosition = new Point(0,0);

    boolean lockedLocalMousePosition = false;

    boolean bigPictureMode = false;

    public Optic() {
        this.screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.addWindowListener(new WindowClosingEvent());
        this.frame.setSize(this.screenDimensions.width/40, this.screenDimensions.width/40);
        this.frame.setLocationRelativeTo(null);
        this.frame.add(this);
        this.frame.setFocusable(false);
        this.frame.setAlwaysOnTop(true);
        this.frame.setUndecorated(true);
        this.frame.setShape(new Ellipse2D.Double(0,0,this.frame.getWidth(),this.frame.getHeight()));
        this.frame.setVisible(true);

        this.addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_F3) {
                    System.exit(0);
                }
                if(e.getKeyCode() == KeyEvent.VK_F1) {
                    toggleBigPictureMode();
                }
            }
        });

        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                lockedLocalMousePosition = true;
            }
            public void mouseReleased(MouseEvent e) {
                lockedLocalMousePosition = false;
            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                frame.setLocation(globalMousePosition.x - localMousePosition.x, globalMousePosition.y - localMousePosition.y);
            }

            public void mouseMoved(MouseEvent e) {
                
            }
        });

        this.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() > 0){
                    magnification -= 0.5;
                    if(magnification < 1) {
                        magnification = 1;
                    }
                }else if(e.getWheelRotation() < 0){
                    magnification += 0.5;
                }
            }
        });

        this.captureThread.start();
    }

    public void toggleBigPictureMode() {
        this.bigPictureMode = !this.bigPictureMode;

        if(!this.bigPictureMode) {
            this.frame.setSize(this.screenDimensions.width/40, this.screenDimensions.width/40);
            this.frame.setShape(new Ellipse2D.Double(0,0,this.frame.getWidth(),this.frame.getHeight()));
        }else{
            this.frame.setSize(this.screenDimensions.width/2, this.screenDimensions.width/2);
            this.frame.setShape(new Ellipse2D.Double(0,0,this.frame.getWidth(),this.frame.getHeight()));
        }

        double tempMagnification = this.magnification;
        this.magnification = this.savedMagnification;
        this.savedMagnification = tempMagnification;

        Point tempLocation = this.frame.getLocation();
        this.frame.setLocation(this.savedLocation);
        this.savedLocation = tempLocation;
    }

    public void run() {
        long last = 0;
        while(WindowClosingEvent.running){
            Point p = null;
            try{
                p = this.getMousePosition();
            }catch(Exception e){}
            if(p != null){
                if(!lockedLocalMousePosition) {
                    this.localMousePosition = p;
                }
            }
            this.globalMousePosition = MouseInfo.getPointerInfo().getLocation();
            if(System.currentTimeMillis()-last > (1000/60)){
                render();
            }
        }
    }

    Robot robot;

    int screenCaptureModifier = 5;

    private BufferedImage capture() { 
        if(this.robot == null){
            try{
                this.robot = new Robot();
            }catch(AWTException e){
                e.printStackTrace();
                return null;
            }
        }

        int xOffset = screenDimensions.width / 2 - (screenDimensions.width / screenCaptureModifier / 2);
        int yOffset = screenDimensions.height / 2 - (screenDimensions.width / screenCaptureModifier / 2);
        return this.robot.createScreenCapture(new Rectangle(xOffset, yOffset, screenDimensions.width/screenCaptureModifier, screenDimensions.width/screenCaptureModifier));
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        BufferedImage screen = capture();

        Graphics g = bs.getDrawGraphics();
        if(screen != null){
            int xOffset = (int) - this.magnification;
            int yOffset = (int) - this.magnification;
            int width = (int) (this.getWidth() + this.magnification * 2);
            int height = (int) (this.getHeight() + this.magnification * 2);
            g.drawImage(screen, xOffset, yOffset, width, height, null);
        }

        g.setColor(Color.RED);
        g.drawOval(0, 0, this.getWidth()-1, this.getHeight()-1);
        g.drawOval(1, 1, this.getWidth()-3, this.getHeight()-3);

        g.dispose();
        bs.show();
    }

}
