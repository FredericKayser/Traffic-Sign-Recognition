package de.frederickayser.trafficsignrecognition.signdetection;

import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * by Frederic on 03.05.20(00:19)
 */
public class VideoSignDetector extends SignDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoSignDetector.class);

    private final File file;
    private final String outputPath;

    public VideoSignDetector(String filePath, String outputPath) {
        this.file = new File(filePath);
        this.outputPath = outputPath;
    }

    public void detect() {
        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        OpenCVFrameConverter openCVFrameConverter = new OpenCVFrameConverter.ToMat();
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file.getAbsolutePath());
        try {
            frameGrabber.start();
            FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(outputPath,
                    frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
            frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            frameRecorder.setFormat("mp4");
            frameRecorder.setFrameRate(3);
            MessageBuilder.send(LOGGER, "SF: " + frameGrabber.getSampleFormat() +
                    "; SR: " + frameGrabber.getSampleRate() + "; FR: " + frameGrabber.getFrameRate());
            frameRecorder.setSampleFormat(frameGrabber.getSampleFormat());
            frameRecorder.setSampleRate(frameGrabber.getSampleRate());
            frameRecorder.start();
            for (int i = 0; i < frameGrabber.getLengthInVideoFrames(); i+=10) {
                long start = System.currentTimeMillis();
                frameGrabber.setFrameNumber(i);
                Frame frame = frameGrabber.grab();
                frameRecorder.setTimestamp(frameGrabber.getTimestamp());
                frameRecorder.record(editFrame(frameConverter.convert(frame), openCVFrameConverter, frameConverter));


                long end = System.currentTimeMillis();
                long difference = end - start;

                if(i % 100 == 0) {
                    MessageBuilder.send(LOGGER, MessageBuilder.MessageType.DEBUG, "Frame " + i + " of "
                            + frameGrabber.getLengthInVideoFrames() + " done(" + difference + "ms).");
                }

            }
            frameRecorder.stop();
            frameGrabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

}
