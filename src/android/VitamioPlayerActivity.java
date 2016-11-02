package gr.kostaspl.vitamio;

import android.app.Activity;
import android.os.Bundle;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.net.Uri;
import android.graphics.Point;
import android.view.Display;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.TextView;
import android.widget.SeekBar;
import android.util.Log;

public class VitamioPlayerActivity extends Activity {

	private io.vov.vitamio.widget.VideoView mVideoView = null;
    private ProgressBar mProgressBar = null;
    private TextView mLoadingText = null;
    private boolean mHandled = false;
    private boolean mNotSeekable = false;
	//private MediaPlayer mp = null;
    
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Vitamio.isInitialized(this);
        
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(getResources().getIdentifier("vitamioplayer", "layout", getPackageName()), null);
        
        mVideoView = (io.vov.vitamio.widget.VideoView) relativeLayout.findViewById(getResources().getIdentifier("vitamioplayer_videoview", "id", getPackageName()));
        mVideoView.setBufferSize(10 * 1024*1024); // 10MB buffer
        
        mProgressBar = (ProgressBar) relativeLayout.findViewById(getResources().getIdentifier("vitamioplayer_loading", "id", getPackageName()));
		mLoadingText = (TextView) relativeLayout.findViewById(getResources().getIdentifier("vitamioplayer_loadingText", "id", getPackageName()));
        
        String loadingText = getIntent().getStringExtra("loadingText");
        if (loadingText != null && !loadingText.equals("null") && !loadingText.isEmpty()){
            mLoadingText.setText(loadingText);
        }
        
		setContentView(relativeLayout);

		playfunction();
	}

	void playfunction() {
		String path = getIntent().getStringExtra("mediaUrl");

        if (path == null || path.trim().isEmpty()){
            finish();
            return;
        }
        
        path = path.trim();
        
		mVideoView.setVideoURI(Uri.parse(path));
		//mVideoView.setVideoPath(path);
        
        if (getIntent().getBooleanExtra("useMediaController", true)){
            MediaController mc = new MediaController(this);
            mc.setUserOnSeekListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            
                }
                
                @Override
                public void onStartTrackingTouch(SeekBar seekBar){
                    
                }
                
                @Override
                public void onStopTrackingTouch(SeekBar seekBar){
                    if (!mNotSeekable){
                        mProgressBar.setVisibility(View.VISIBLE);
                        mLoadingText.setVisibility(View.VISIBLE);
                    }
                }
            });
            
            mVideoView.setMediaController(mc);
        }
        
		mVideoView.requestFocus();
        
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mLoadingText.setVisibility(View.INVISIBLE);
                mp.setPlaybackSpeed(1.0f);
                mp.start();
			}
		});
          
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer){
                VitamioPlayerActivity.this.finish();
            }
        });
        
        mVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener(){
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent){
                if (!mHandled){
                    mHandled = true;
                    mp.start();
                }
            }
        });
        
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener(){
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra){
                if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE){
                    mNotSeekable = true;
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                }
				else if(what == MediaPlayer.MEDIA_INFO_FILE_OPEN_OK){
					Log.d("XXX", ">>>>>>>>>>>>>>>>>>>>>>>> onInfo: MEDIA_INFO_FILE_OPEN_OK");
                    long buffersize = mp.audioTrackInit(); // line added 2
                    mp.audioInitedOk(buffersize); // line added 3
				}
                return false;
            }
        });
        
        mVideoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener(){
           @Override
           public void onSeekComplete(MediaPlayer mp){
                mProgressBar.setVisibility(View.INVISIBLE);
                mLoadingText.setVisibility(View.INVISIBLE);
           }
        });
	}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateSize();
    }
    
    private void updateSize() {
        if (mVideoView == null)
            return;
            
        int width = mVideoView.getVideoWidth();
        int height = mVideoView.getVideoHeight();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        android.view.ViewGroup.LayoutParams videoParams = mVideoView.getLayoutParams();

        if (width > 0 && height > 0) {
            if (width > height) {
                videoParams.width = screenWidth;
                videoParams.height = screenWidth * height / width;
            } else {
                videoParams.width = screenHeight * width / height;
                videoParams.height = screenHeight;
            }
        } else {
            videoParams.width = 0;
            videoParams.height = 0;
        }

        mVideoView.setLayoutParams(videoParams);
    }
}
