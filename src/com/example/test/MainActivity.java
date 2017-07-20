package com.example.test;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {
	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri[]> mUploadMessage5;
	public static final int FILECHOOSER_RESULTCODE = 5173;
	public static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5174;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		WebView webview = (WebView) findViewById(R.id.web_view);
		assert webview != null;
		WebSettings settings = webview.getSettings();
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		webview.setWebChromeClient(new WebChromeClient() {
			// For Android < 3.0
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				this.openFileChooser(uploadMsg, "*/*");
			}

			// For Android >= 3.0
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				this.openFileChooser(uploadMsg, acceptType, null);
			}

			// For Android >= 4.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				startActivityForResult(Intent.createChooser(i, "File Browser"),
						FILECHOOSER_RESULTCODE);
			}

			// For Lollipop 5.0+ Devices
			@TargetApi(Build.VERSION_CODES.LOLLIPOP)
			public boolean onShowFileChooser(WebView mWebView,
					ValueCallback<Uri[]> filePathCallback,
					WebChromeClient.FileChooserParams fileChooserParams) {
				if (mUploadMessage5 != null) {
					mUploadMessage5.onReceiveValue(null);
					mUploadMessage5 = null;
				}
				mUploadMessage5 = filePathCallback;
				Intent intent = fileChooserParams.createIntent();
				try {
					startActivityForResult(intent,
							FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
				} catch (ActivityNotFoundException e) {
					mUploadMessage5 = null;
					return false;
				}
				return true;
			}
		});
		String targetUrl = "file:///android_asset/up.html";
		webview.loadUrl(targetUrl);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		} else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
			if (null == mUploadMessage5) {
				return;
			}
			mUploadMessage5.onReceiveValue(WebChromeClient.FileChooserParams
					.parseResult(resultCode, intent));
			mUploadMessage5 = null;
		}
	}
}
