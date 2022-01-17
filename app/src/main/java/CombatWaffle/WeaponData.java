package CombatWaffle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.google.gson.Gson;

import java.awt.Point;

class WeaponData {

    double mod = 0.12;

    Weapon[] weapons;

    public WeaponData() throws IOException {
        File file = new File("weapons.json");
        InputStream in = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()]; 
        in.read(buffer);
        in.close();
        String raw = new String(buffer);
        Gson gson = new Gson();
        this.weapons = gson.fromJson(raw, Weapon[].class);
        for(Weapon weapon : this.weapons){
            weapon.normalize();
        }
    }

    public Weapon getWeapon(String name) {
        for(Weapon weapon : weapons){
            if(weapon.name.equals(name)){
                return weapon;
            }
        }
        return null;
    }

    public Point update(String name, boolean firing) {
        Weapon weapon = getWeapon(name);
        if(weapon == null){
            System.err.println("No such weapon: " + name);
            return new Point(0,0);
        }
        return weapon.getOffset(firing);
    }

    public void reset(String name){
        Weapon weapon = getWeapon(name);
        if(weapon == null){
            System.err.println("No such weapon: " + name);
            return;
        }
        weapon.reset();
    }

}

class Weapon {

    public String name;
    public String description;
    public String viewmodel;

    public double standingRecoveryTime;
    public double crouchingRecoveryTime;

    public double fireRate;
    public double recoilFactor;

    public Point crosshairOffset;

    public int[] x, y;

    private long last = 0;
    private double recoilTime = 0.0;

    public Weapon(){}

    public Weapon(double fireRate, int[] x, int[] y) {
        this.fireRate = fireRate;
        this.x = x;
        this.y = y;
    }

    public Point getOffset(boolean firing) {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - last;

        if(!firing){
            recoilTime = Math.max(0, recoilTime - (recoilTime/standingRecoveryTime) * deltaTime);
            last = currentTime;
            int i = getIndex();
            return new Point(x[i], y[i]);
        }        
        
        if(deltaTime > standingRecoveryTime) {
            recoilTime = 0;
        }else{
            recoilTime += deltaTime;
        }        
        
        last = currentTime;
        int i = getIndex();
        return new Point(x[i], y[i]);
    }

    public int getIndex(){
        int i = (int) Math.floor((recoilTime/1000)*fireRate);
        if(i >= x.length) {
            i = x.length-1;
        }
        return i;
    }

    public void reset(){
        this.last = 0;
        this.recoilTime = 0;
    }

    public void normalize(){
        int originX = x[0];
        int originY = y[0];

        for(int i = 0; i < x.length; i++){
            x[i] -= originX;
            y[i] -= originY;
            x[i] *= this.recoilFactor;
            y[i] *= this.recoilFactor;
        }
        System.out.println("New weapon ready for use: " + name + ", RPS: " + this.fireRate + ", recoil: " + this.recoilFactor);
    }

}