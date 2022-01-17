package CombatWaffle;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.io.File;
import java.io.IOException;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.awt.Dimension;

public class CombatWaffle {
    
    private final JFrame backgroundFrame;
    private final WeaponPanel weaponPanel;

    private BufferedImage weaponImage = null;

    public CombatWaffle() {
        this.weaponPanel = new WeaponPanel(this);
        this.weaponPanel.setBackground(new Color(0,0,0,0));

        this.backgroundFrame = new JFrame();
        this.backgroundFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.backgroundFrame.setUndecorated(true);
        this.backgroundFrame.setSize(1020,964);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameX = (screenSize.width-this.backgroundFrame.getWidth())/2 - 64;
        int frameY = screenSize.height-this.backgroundFrame.getHeight() + 275;

        this.backgroundFrame.setLocation(frameX, frameY);
        this.backgroundFrame.setBackground(new Color(0,0,0,0));
        this.backgroundFrame.add(this.weaponPanel);
        //this.backgroundFrame.setFocusable(false);
        this.backgroundFrame.setAlwaysOnTop(true);
        this.backgroundFrame.setVisible(true);
    }

    public BufferedImage getWeaponImage() {
        if(this.weaponImage == null){
            try{
                this.weaponImage = ImageIO.read(new File("weapon.png"));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return this.weaponImage;
    }

}

class WeaponPanel extends JPanel {

    public final CombatWaffle instance;

    public WeaponPanel(CombatWaffle instance){
        this.instance = instance;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage weaponImage = this.instance.getWeaponImage();
        if(weaponImage != null){
            g.drawImage(weaponImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }else{
            g.setColor(Color.RED);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }

}