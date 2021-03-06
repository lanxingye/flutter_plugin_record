package record.wilson.flutter.com.flutter_plugin_record.utils;

import android.os.Environment;
import android.util.Log;

import com.maple.recorder.player.PlayUtils;
import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;

import java.io.File;
import java.util.Date;


public class RecorderUtil {

    Recorder recorder;
    public static String rootPath = "/yun_ke_fu/flutter/wav_file/";
    String voicePath;
    PlayUtils playUtils;

    RecordListener recordListener;

    public RecorderUtil() {
        initVoice();
    }



    public RecorderUtil(String path) {
        voicePath =path;
    }


    public void addPlayAmplitudeListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    private void initVoice() {
        initPath();
        initVoicePath();
        initRecorder();
    }



    //初始化存储路径
    private void initPath() {
        String ROOT = "";// /storage/emulated/0
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ROOT = Environment.getExternalStorageDirectory().getPath();
            Log.e("voice", "系统方法：" + ROOT);
        }
        rootPath = ROOT + rootPath;

        File lrcFile = new File(rootPath);
        if (!lrcFile.exists()) {
            lrcFile.mkdirs();
        }

        Log.e("voice", "初始存储路径" + rootPath);
    }


    private void initVoicePath() {
        String forDate = DateUtils.dateToString(new Date());
        String name = "wav-" + forDate;
        voicePath = rootPath + name + ".wav";
        Log.e("voice", "初始化语音路径" + voicePath);


    }


    private void initRecorder() {
        recorder = MsRecorder.wav(
                new File(voicePath),
                new AudioRecordConfig.Default(),
                new PullTransport.Default()
                        .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                if (recordListener != null) {
                                    recordListener.onPlayAmplitude(audioChunk.maxAmplitude());
                                }
                            }
                        })

        );
    }

    public void startRecord() {
        if (recordListener != null) {
            recordListener.onVoicePathSuccess(voicePath);
        }

        recorder.stopRecording();
        recorder.startRecording();
    }

    public void stopRecord() {
        recorder.stopRecording();
    }

    public void playVoice() {
        if (playUtils == null) {
            playUtils = new PlayUtils();
            playUtils.setPlayStateChangeListener(new PlayUtils.PlayStateChangeListener() {
                @Override
                public void onPlayStateChange(boolean isPlay) {
                }
            });
        }
        if (playUtils.isPlaying()) {
            playUtils.stopPlaying();
        } else {
            playUtils.startPlaying(voicePath);
        }
    }



    public void playVoice(String path) {
        if (playUtils == null) {
            playUtils = new PlayUtils();
            playUtils.setPlayStateChangeListener(new PlayUtils.PlayStateChangeListener() {
                @Override
                public void onPlayStateChange(boolean isPlay) {
                }
            });
        }
        if (playUtils.isPlaying()) {
            playUtils.stopPlaying();
        } else {
            playUtils.startPlaying(path);
        }
    }

    public interface RecordListener {
        void onPlayAmplitude(Double amplitude);

        void onVoicePathSuccess(String voicePath);

    }
}
