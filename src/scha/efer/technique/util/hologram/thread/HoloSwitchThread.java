package scha.efer.technique.util.hologram.thread;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HoloSwitchThread extends Thread {

    @Override
    public void run() {
    }


    public void tick() {
        this.stop();
    }
}
