package com.android.adbkeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.ExtractEditText;
import android.inputmethodservice.InputMethodService;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdbIME extends InputMethodService {
    private String IME_MESSAGE = "ADB_INPUT_TEXT";
    private String IME_CHARS = "ADB_INPUT_CHARS";
    private String IME_KEYCODE = "ADB_INPUT_CODE";
    private String IME_META_KEYCODE = "ADB_INPUT_MCODE";
    private String IME_EDITORCODE = "ADB_EDITOR_CODE";
    private String IME_MESSAGE_B64 = "ADB_INPUT_B64";
    private String IME_CLEAR_TEXT = "ADB_CLEAR_TEXT";
    private String IME_HIDE_WINDOW = "ADB_HIDE_WINDOW";
    private String IME_SHOW_WINDOW = "ADB_SHOW_WINDOW";
    private BroadcastReceiver mReceiver = null;
	Button button1, button2, button3;
	View mInputView;

	@Override
    public View onCreateInputView() {
		mInputView = getLayoutInflater().inflate(R.layout.view, null);

		//	View xmlView = new View(getApplicationContext()){
		//  	@Override
		//		public boolean dispatchTouchEvent(MotionEvent event) {
		//			return false;
		//		}
		//	};

		button1 = mInputView.findViewById(R.id.btnSelectIme);
		button2 = mInputView.findViewById(R.id.btnClose);
		button3 = mInputView.findViewById(R.id.btnDone);
		button1.setOnHoverListener(new View.OnHoverListener(){
			@Override
			public boolean onHover(View v, MotionEvent me){
				if (me.getActionMasked() == MotionEvent.ACTION_HOVER_ENTER){
					// Toast.makeText(getApplicationContext(),"Show input method picker",Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});

		button1.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				// Toast.makeText(getApplicationContext(),"showInputMethodPicker",Toast.LENGTH_LONG).show();
				// Intent intent = new Intent();
				// intent.setAction("android.settings.INPUT_METHOD_SETTINGS");
				// getApplicationContext().startActivity(intent);
				InputMethodManager imm = ((InputMethodManager)getApplicationContext().getSystemService(INPUT_METHOD_SERVICE));
				imm.showInputMethodPicker();
			}
		});

		button2.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				// Toast.makeText(getApplicationContext(),"hideSoftInputFromWindow", Toast.LENGTH_SHORT).show();
				requestHideSelf(InputMethodManager.HIDE_NOT_ALWAYS);
				InputMethodManager imm = ((InputMethodManager)getApplicationContext().getSystemService(INPUT_METHOD_SERVICE));
				imm.hideSoftInputFromWindow(mInputView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

				// ViewGroup.LayoutParams lp = mInputView.getLayoutParams();
				// if (lp != null){
				// 	 Toast.makeText(getApplicationContext(), "ViewGroup.LayoutParams null", Toast.LENGTH_SHORT).show();
				// 	 lp.height = 25;
				// 	 mInputView.setLayoutParams(lp);
				// }
				// else{
				// 	 Toast.makeText(getApplicationContext(), "ViewGroup.LayoutParams null", Toast.LENGTH_SHORT).show();
				// 				}

				// setExtractViewShown(false);
			}
		});

		button3.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				InputConnection ic = getCurrentInputConnection();
				if (ic != null)
					ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB));
			}
		});

        if (mReceiver == null) {
        	IntentFilter filter = new IntentFilter(IME_MESSAGE);
        	filter.addAction(IME_CHARS);
        	filter.addAction(IME_KEYCODE);
        	filter.addAction(IME_META_KEYCODE);
        	filter.addAction(IME_EDITORCODE);
        	filter.addAction(IME_MESSAGE_B64);
        	filter.addAction(IME_CLEAR_TEXT);
        	filter.addAction(IME_HIDE_WINDOW);
        	filter.addAction(IME_SHOW_WINDOW);
        	mReceiver = new AdbReceiver();
        	registerReceiver(mReceiver, filter);
        }

        return mInputView;
    } 
    
    public void onDestroy() {
    	if (mReceiver != null)
    		unregisterReceiver(mReceiver);
    	super.onDestroy();    	
    }

	@Override
	public void onStartInputView(EditorInfo attribute, boolean restarting) {
		//	ExtractEditText extractEditTextView = new ExtractEditText(this);
		//	extractEditTextView.setHeight(25);
		//	extractEditTextView.setId(android.R.id.inputExtractEditText);
		super.onStartInputView(attribute, restarting);
		//	// Change the key height here dynamically after getting your value from shared preference or something
		//	mCurKeyboard.changeKeyHeight(1.5);
		//	// Apply the selected keyboard to the input view.
		//	mInputView.setKeyboard(mCurKeyboard);
		//	mInputView.closing();
		//	final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
		//	mInputView.setSubtypeOnSpaceKey(subtype);
	}

	public void onWindowShown(){
		// Toast.makeText(getApplicationContext(),"onWindowShown", Toast.LENGTH_LONG).show();
		// hideWindow();
		super.onWindowShown();
		// setExtractViewShown(false);
	}

	@Override
	public boolean onEvaluateFullscreenMode() {
		// Toast.makeText(getApplicationContext(),"onEvaluateFullscreenMode", Toast.LENGTH_LONG).show();
		return false;
	}

	//	@Override
	//	public boolean isShowInputRequested(){
	//		Toast.makeText(getApplicationContext(),"isShowInputRequested", Toast.LENGTH_LONG).show();
	//		return false;
	//	}

	public  boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			v.getLocationInWindow(leftTop); // 获取输入框当前的location位置
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				return false; // 点击的是输入框区域，保留点击EditText的事件
			}
			else {
				return true;
			}
		}
		return false;
	}

	class AdbReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(IME_MESSAGE)) {
				String msg = intent.getStringExtra("msg");
				if (msg != null) {
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.commitText(msg, 1);
				}
			}

			else if (intent.getAction().equals(IME_MESSAGE_B64)) {
				String data = intent.getStringExtra("msg");

				byte[] b64 = Base64.decode(data, Base64.DEFAULT);
				String msg = "NOT SUPPORTED";
				try {
					msg = new String(b64, "UTF-8");
				} catch (Exception e) {

				}

				if (msg != null) {
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.commitText(msg, 1);
				}
			}

			else if (intent.getAction().equals(IME_CHARS)) {
				int[] chars = intent.getIntArrayExtra("chars");				
				if (chars != null) {					
					String msg = new String(chars, 0, chars.length);
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.commitText(msg, 1);
				}
			}

			else if (intent.getAction().equals(IME_KEYCODE)) {
				int code = intent.getIntExtra("code", -1);				
				if (code != -1) {
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
				}
			}

			else if (intent.getAction().equals(IME_META_KEYCODE)) {
				int[] mcodes = intent.getIntArrayExtra("mcode");
				if (mcodes != null) {
					int i;
					InputConnection ic = getCurrentInputConnection();
					for (i = 0; i < mcodes.length - 1; i = i + 2) {
						if (ic != null) {
							KeyEvent ke = new KeyEvent(-1, -1, KeyEvent.ACTION_DOWN, mcodes[i+1], -1, mcodes[i]);
							ic.sendKeyEvent(ke);
						}
					}
				}
			}

			else if (intent.getAction().equals(IME_EDITORCODE)) {
				int code = intent.getIntExtra("code", -1);				
				if (code != -1) {
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.performEditorAction(code);
				}
			}

			else if (intent.getAction().equals(IME_CLEAR_TEXT)) {
				InputConnection ic = getCurrentInputConnection();
				if (ic != null) {
					//REF: stackoverflow/33082004 author: Maxime Epain
					CharSequence curPos = ic.getExtractedText(new ExtractedTextRequest(), 0).text;
					CharSequence beforePos = ic.getTextBeforeCursor(curPos.length(), 0);
					CharSequence afterPos = ic.getTextAfterCursor(curPos.length(), 0);
					ic.deleteSurroundingText(beforePos.length(), afterPos.length());
				}
			}

			else if (intent.getAction().equals(IME_HIDE_WINDOW)) {
				// Toast.makeText(getApplicationContext(),"IME_HIDE_WINDOW onClick",Toast.LENGTH_LONG).show();
				hideWindow();
			}

			else if (intent.getAction().equals(IME_SHOW_WINDOW)) {
				// Toast.makeText(getApplicationContext(),"IME_SHOW_WINDOW onClick",Toast.LENGTH_LONG).show();
				showWindow(true);
			}
		}
    }
}
