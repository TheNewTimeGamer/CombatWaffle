package CombatWaffle;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Canvas;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

import java.io.File;
import java.io.IOException;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Font;

import java.awt.Dimension;
import java.awt.Point;

public class CombatWaffle {

    public BufferedImage weaponImage = null;

    public final Point center = new Point(0,0);

    public final WeaponData weaponData;
    public Weapon currentWeapon;

    private volatile boolean firing = false;
    private boolean ads = false;

    public boolean searching = false;
    public String searchQuery = "";
    public Color searchQueryColor = Color.WHITE;

    public Font font = new Font("San Serif", Font.BOLD, 18);

    public CombatWaffle(WeaponData weaponData) {
        this.weaponData = weaponData;
        this.currentWeapon = weaponData.getWeapon("Dual Berettas");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.center.x = screenSize.width/2;
        this.center.y = screenSize.height/2;

        try{
            GlobalScreen.registerNativeHook();
        }catch(Exception e){
            System.err.println("There was a problem registering the native hook.");
			System.err.println(e.getMessage());
			System.exit(1);
        }

        GlobalMouseListener globalMouseListener = new GlobalMouseListener(this);
        GlobalKeyListener globalKeyListener = new GlobalKeyListener(this);

        GlobalScreen.addNativeMouseListener(globalMouseListener);
        GlobalScreen.addNativeKeyListener(globalKeyListener);
    }

    public BufferedImage getWeaponImage() {
        if(this.weaponImage == null){
            try{
                this.weaponImage = ImageIO.read(new File(currentWeapon.viewmodel));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return this.weaponImage;
    }

    public boolean isFiring(){
        return this.firing;
    }

    public void setFiring(boolean firing){
        this.firing = firing;
    }

    public boolean isAds(){
        return this.ads;
    }

    public void setAds(boolean ads){
        this.ads = ads;
    }

    public void render(Graphics2D g) {
        BufferedImage weaponImage = this.getWeaponImage();
        if(isAds()){
            if(weaponImage != null){
                int xPos = this.center.x - this.currentWeapon.crosshairOffset.x;
                int yPos = this.center.y - this.currentWeapon.crosshairOffset.y;
                Point offset = this.currentWeapon.getOffset(this.firing);
                g.drawImage(weaponImage, xPos + offset.x, yPos + offset.y, null);
            }
        }else{
            Point offset = this.currentWeapon.getOffset(this.firing);
            g.setColor(Color.BLACK);
            g.fillRect(this.center.x + offset.x - 3, this.center.y + offset.y - 3 + 25, 6, 6);
            g.setColor(Color.RED);
            g.fillRect(this.center.x + offset.x - 2, this.center.y + offset.y - 2 + 25, 4, 4);
        }

        g.setFont(this.font);
        g.setColor(Color.BLACK);
        g.drawString(this.searchQuery, 5, 16);
        g.setColor(this.searchQueryColor);
        g.drawString(this.searchQuery, 5, 16);
    }
}

class GlobalMouseListener implements NativeMouseInputListener {

    private final CombatWaffle combatWaffle;

    public GlobalMouseListener(CombatWaffle combatWaffle){
        this.combatWaffle = combatWaffle;
    }

    @Override
	public void nativeMousePressed(NativeMouseEvent e) {
        if(e.getButton() == NativeMouseEvent.BUTTON1){
            this.combatWaffle.setFiring(true);
        }
        if(e.getButton() == NativeMouseEvent.BUTTON2){
            this.combatWaffle.setAds(true);
        }
	}

    @Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		if(e.getButton() == NativeMouseEvent.BUTTON1){
            this.combatWaffle.setFiring(false);
        }
        if(e.getButton() == NativeMouseEvent.BUTTON2){
            this.combatWaffle.setAds(false);
        }
	}
    
}

class GlobalKeyListener implements NativeKeyListener {

    CombatWaffle combatWaffle;

    public GlobalKeyListener(CombatWaffle combatWaffle){
        this.combatWaffle = combatWaffle;
    }

    public void nativeKeyPressed(NativeKeyEvent e){
        if(this.combatWaffle.searching){
            if(e.getKeyCode() == NativeKeyEvent.VC_ENTER){
                Weapon weapon = this.combatWaffle.weaponData.find(this.combatWaffle.searchQuery);
                if(weapon != null){
                    this.combatWaffle.searchQueryColor = Color.GREEN;
                    this.combatWaffle.currentWeapon = weapon;
                    this.combatWaffle.searchQuery = weapon.name;
                }else{
                    this.combatWaffle.searchQueryColor = Color.RED;
                }
                this.combatWaffle.searching = false;
            }
            if(e.getKeyCode() == NativeKeyEvent.VC_BACKSPACE){
                if(this.combatWaffle.searching){
                    this.combatWaffle.searchQuery = this.combatWaffle.searchQuery.substring(0, this.combatWaffle.searchQuery.length() - 1);
                    return;
                }
            }
            String key = NativeKeyEvent.getKeyText(e.getKeyCode());
            if(key.length() == 1){
                this.combatWaffle.searchQuery = this.combatWaffle.searchQuery + key;
            }
        }else{
            if(e.getKeyCode() == NativeKeyEvent.VC_F12){
                this.combatWaffle.searchQueryColor = Color.WHITE;
                this.combatWaffle.searching = true;
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e){
        
    }

}