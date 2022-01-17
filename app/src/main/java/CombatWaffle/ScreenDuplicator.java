package CombatWaffle;

import javax.swing.JFrame;

import java.awt.Canvas;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferStrategy;

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

class ScreenDuplicator extends Canvas implements Runnable {

    public final JFrame frame;

    public final Rectangle input;
    public final Rectangle output;

    public final Robot robot;

    public Thread captureThread = new Thread(this);

    public CombatWaffle combatWaffle;

    public ScreenDuplicator(Rectangle input, Rectangle output, CombatWaffle combatWaffle) throws AWTException {
        this.combatWaffle = combatWaffle;
        
        this.input = input;
        this.output = output;
        this.robot = new Robot();

        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.addWindowListener(new WindowClosingEvent());

        this.frame.setUndecorated(true);
        this.frame.setLocation(output.x, output.y);
        this.frame.setSize(output.width, output.height);
        this.frame.add(this);
        this.frame.setVisible(true);

        this.captureThread.start();
    }

    public BufferedImage preformScreenCapture() {
        return this.robot.createScreenCapture(this.input);
    }

    public void run() {
        while(WindowClosingEvent.running) {
            BufferedImage capture = this.preformScreenCapture();                        
            render(capture);
        }
    }

    public void render(BufferedImage capture) {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        
        g.drawImage(capture, 0, 0, this.getWidth(), this.getHeight(), null);

        if(this.combatWaffle != null){
            this.combatWaffle.render(g);
        }

        g.dispose();
        bs.show();
    }

}
