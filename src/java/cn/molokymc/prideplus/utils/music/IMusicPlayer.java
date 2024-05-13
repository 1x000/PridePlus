package cn.molokymc.prideplus.utils.music;

public interface IMusicPlayer {

    void setup(String pathToAudio) throws Throwable;
    void play();
    void stop();
    void playLooping();

}
