package com.sierratechnologies.safeshield;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;

public class ServiceMine extends Service {

    private boolean isRunning = false;
    private FusedLocationProviderClient fusedLocationClient;
    private MediaPlayer mediaPlayer;
    private boolean isSirenEnabled = true; // Default to true (siren plays)
    private MediaRecorder mediaRecorder;
    private File audioFile;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    SmsManager manager = SmsManager.getDefault();
    String myLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location permissions if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Get last location
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                myLocation = "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
            } else {
                myLocation = "Unable to Find Location :";
            }
        });

        // Set up MediaPlayer for siren sound
        mediaPlayer = MediaPlayer.create(this, R.raw.notification);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // Set up shake detection
        ShakeDetector.create(this, () -> {
            if (isSirenEnabled) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                }

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            String ENUM = sharedPreferences.getString("ENUM", "NONE");
            if (!ENUM.equalsIgnoreCase("NONE")) {
                manager.sendTextMessage(ENUM, null, "I'm in Trouble!\nSending My Location:\n" + myLocation, null, null);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if ("STOP".equalsIgnoreCase(intent.getAction())) {
                stopRecording();
                if (isRunning) {
                    this.stopForeground(true);
                    this.stopSelf();
                }
            } else if ("START".equalsIgnoreCase(intent.getAction())) {
                isSirenEnabled = intent.getBooleanExtra("SIREN_ENABLED", true);
                startRecording(); // Start recording when service starts

                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("MYID", "CHANNELFOREGROUND", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    m.createNotificationChannel(channel);

                    Notification notification = new Notification.Builder(this, "MYID")
                            .setContentTitle("Safeshield")
                            .setContentText("Your motions and activities are detected")
                            .setSmallIcon(R.drawable.img_1)
                            .setContentIntent(pendingIntent)
                            .build();
                    this.startForeground(115, notification);
                    isRunning = true;
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        stopRecording(); // Ensure recording is stopped
    }

    // Start recording audio
    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();

            // Save audio in internal storage
            File internalStorageDir = new File(getFilesDir(), "Safeshield");
            if (!internalStorageDir.exists()) {
                boolean created = internalStorageDir.mkdirs();
                Log.d(TAG, "Output directory creation: " + created);
            }

            audioFile = new File(internalStorageDir, "audio_" + System.currentTimeMillis() + ".3gp");
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                Log.d(TAG, "Recording started. Saving file at: " + audioFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e(TAG, "MediaRecorder prepare failed: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Permissions not granted!");
        }
    }

    // Stop recording audio and save the file
    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                Log.d(TAG, "Recording stopped. File saved at: " + audioFile.getAbsolutePath());
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to stop MediaRecorder: " + e.getMessage());
                if (audioFile != null && audioFile.exists()) {
                    audioFile.delete(); // Cleanup incomplete file
                }
            }
        }
    }
}
