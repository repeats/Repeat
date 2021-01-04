package staticResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Utility class to play short sounds.
 */
public class SoundUtility {

	private static final Logger LOGGER = Logger.getLogger(SoundUtility.class.getName());

	public static enum BuiltInSound {
		POSITIVE1(BootStrapResources.SOUND_POSITIVE1_PATH),
		POSITIVE2(BootStrapResources.SOUND_POSITIVE2_PATH),
		POSITIVE3(BootStrapResources.SOUND_POSITIVE3_PATH),
		POSITIVE4(BootStrapResources.SOUND_POSITIVE4_PATH),
		NEGATIVE1(BootStrapResources.SOUND_NEGATIVE1_PATH),
		NEGATIVE2(BootStrapResources.SOUND_NEGATIVE2_PATH),
		;

		private final String value;
		BuiltInSound(String value) { this.value = value; }
	}

	/**
	 * Play a built in sound.
	 */
	public static void play(BuiltInSound sound) {
		try {
			play(BootStrapResources.getStaticContentStream(sound.value));
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
			LOGGER.log(Level.WARNING, "Encounter exception when playing sound " + sound + ".", e);
		}
	}

	/**
	 * Play a sound from a file path.
	 * Only support wav files.
	 *
	 * This is only meant to play very short sound files.
	 *
	 * @param path path to the sound file.
	 * @throws LineUnavailableException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public static void playShortSound(String path) {
		File f = new File(path);
		try {
			play(new FileInputStream(f));
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
			LOGGER.log(Level.WARNING, "Encounter exception when playing sound at " + path + ".", e);
		}
	}

	/**
	 * Play a sound from an input stream.
	 * Only support wav files.
	 *
	 * This is only meant to play very short sound files.
	 *
	 * @param stream stream to the sound file.
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	private static void play(InputStream stream) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		AudioInputStream ais = AudioSystem.getAudioInputStream(stream);
        Clip clip = AudioSystem.getClip();

        clip.open(ais);
        clip.start();

        while (!clip.isRunning()) {
			Thread.sleep(100);
		}
        while (clip.isRunning()) {
			Thread.sleep(100);
		}

        clip.close();
	}

	private SoundUtility() {}
}
