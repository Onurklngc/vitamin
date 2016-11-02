package gr.kostaspl.vitamio;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import gr.kostaspl.vitamio.VitamioPlayerActivity;

public class VitamioPlugin extends CordovaPlugin {
	private CallbackContext callbackContext;

	private static final String TAG = "VitamioPlugin";

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		return playVideo(args.getString(0), args.getString(1), args.getBoolean(2));
	}

	private boolean playVideo(String url, String loadingText, boolean useMediaController) throws JSONException {
		return play(VitamioPlayerActivity.class, url, loadingText, useMediaController);
	}

	private boolean play(final Class activityClass, final String url, final String loadingText, final boolean useMediaController) {
		final CordovaInterface cordovaObj = cordova;
		final CordovaPlugin plugin = this;

		cordova.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				final Intent streamIntent = new Intent(cordovaObj.getActivity().getApplicationContext(), activityClass);
                streamIntent.putExtra("mediaUrl", url);
                streamIntent.putExtra("loadingText", loadingText);
                streamIntent.putExtra("useMediaController", useMediaController);
				cordovaObj.getActivity().startActivity(streamIntent);
			}
		});
		return true;
	}
}