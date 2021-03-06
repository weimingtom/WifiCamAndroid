package com.vless.wificam.Control;

import java.net.MalformedURLException;
import java.net.URL;

import com.vless.wificam.R;
import com.vless.wificam.CameraCommand;
import com.vless.wificam.CameraPeeker;
import com.vless.wificam.CameraSniffer;
import com.vless.wificam.MainActivity;
import com.vless.wificam.contants.Contants;
import com.vless.wificam.frags.WifiCamFragment;
import com.vless.wificam.impls.OnEcarInfoListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

public class CameraSettingsFragment extends WifiCamFragment implements
		OnClickListener, Contants {
	private TextView tvHigh, tvMid, tvLow;
	private ImageView ivMotDet, ivVoicCtl, ivFormatCamSD;
	private View view, sureView;
	private ImageView ivLeft;
	private OnEcarInfoListener onEcarInfoListener;
	private Dialog dialog;
	private Button btn_ok, btn_cnl;
	private boolean isWifiEnabled;
	private int netWorkId;

	@Override
	public void setOnEcarFragListener(OnEcarInfoListener l) {
		onEcarInfoListener = l;
	}

	private class CameraSettingsSendRequest extends CameraCommand.SendRequest {

		@Override
		protected void onPreExecute() {
			setWaitingState(true);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			setWaitingState(false);
			super.onPostExecute(result);
		}
	}

	private class GetMenuSettingsValues extends AsyncTask<URL, Integer, String> {

		protected void onPreExecute() {
			setWaitingState(true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandGetMenuSettingsValuesUrl();
			if (url != null) {
				return CameraCommand.sendRequest(url);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			int VideoRes = 0, MTD = 0, voiceRes = 0;

			Activity activity = getActivity();
			Log.d("GetMenuSettingsValues", "result property : >" + result + "<");
			if (result != null) {
				String[] lines;
				String[] lines_temp;
				lines_temp = result.split("VideoRes=");
				lines = lines_temp[1].split(System
						.getProperty("line.separator"));
				VideoRes = Integer.parseInt(lines[0].substring(0, 1));

				// lines_temp = result.split("ImageRes=");
				// lines = lines_temp[1].split(System
				// .getProperty("line.separator"));
				// ImageRes = Integer.parseInt(lines[0].substring(0, 1));
				if (VideoRes == 0) {
					setSettStat(0);
				} else if (VideoRes == 1) {
					setSettStat(1);
				} else if (VideoRes == 3) {
					setSettStat(2);
				} else {
					setSettStat(0);
				}
				lines_temp = result.split("MTD=");
				lines = lines_temp[1].split(System
						.getProperty("line.separator"));
				MTD = Integer.parseInt(lines[0].substring(0, 1));
				switch (MTD) {
				case 0:
					isMotDet = true;
					setSettStat(3);
					break;
				case 1:
					isMotDet = false;
					setSettStat(3);
					break;
				default:
					break;
				}
				lines_temp = result.split("Flicker=");
				lines = lines_temp[1].split(System
						.getProperty("line.separator"));
				voiceRes = Integer.parseInt(lines[0].substring(0, 1));
				switch (voiceRes) {
				case 0:
					isVoicOpen = false;
					setSettStat(4);
					break;
				case 1:
					isVoicOpen = true;
					setSettStat(4);
					break;
				default:
					break;
				}
			} else if (activity != null) {
				Toast.makeText(
						activity,
						activity.getResources().getString(
								R.string.message_fail_get_info),
						Toast.LENGTH_LONG).show();
			}
			setWaitingState(false);
			super.onPostExecute(result);
		}
	}

	private boolean mWaitingState = false;
	private boolean mWaitingVisible = false;

	private void setWaitingState(boolean waiting) {
		if (mWaitingState != waiting) {
			mWaitingState = waiting;
			setWaitingIndicator(mWaitingState, mWaitingVisible);
		}
	}

	private void setWaitingIndicator(boolean waiting, boolean visible) {

		if (!visible)
			return;
		Activity activity = getActivity();
		if (activity != null) {
			activity.setProgressBarIndeterminate(true);
			activity.setProgressBarIndeterminateVisibility(waiting);
		}
	}

	@Override
	public void onDestroy() {
		CameraSniffer sniffer = MainActivity.sniffer;
		sniffer.SetPeeker(null);
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		res = getActivity().getResources();
		view = inflater.inflate(R.layout.camera_settings, container, false);
		TextView tvHeaderTitle = (TextView) view.findViewById(R.id.frag_header)
				.findViewById(R.id.header_title);
		tvHeaderTitle.setText(R.string.end_title_setting_face);
		ivLeft = (ImageView) view.findViewById(R.id.frag_header).findViewById(
				R.id.header_left);
		ivLeft.setImageResource(R.drawable.left_back);
		ivLeft.setVisibility(View.VISIBLE);
		tvHigh = (TextView) view.findViewById(R.id.tv_high);
		tvMid = (TextView) view.findViewById(R.id.tv_mindle);
		tvLow = (TextView) view.findViewById(R.id.tv_low);
		ivMotDet = (ImageView) view.findViewById(R.id.ivSettMotDet);
		ivVoicCtl = (ImageView) view.findViewById(R.id.ivCamSettVoic);
		ivFormatCamSD = (ImageView) view.findViewById(R.id.ivFormatCamSD);
		ivLeft.setOnClickListener(this);
		tvHigh.setOnClickListener(this);
		tvMid.setOnClickListener(this);
		tvLow.setOnClickListener(this);
		tvLow.setOnClickListener(this);
		tvLow.setOnClickListener(this);
		ivMotDet.setOnClickListener(this);
		ivVoicCtl.setOnClickListener(this);
		ivFormatCamSD.setOnClickListener(this);

		sureView = getActivity().getLayoutInflater().inflate(
				R.layout.set_format_cam_sd, null, false);
		btn_ok = (Button) sureView.findViewById(R.id.btn_set_ok);
		btn_cnl = (Button) sureView.findViewById(R.id.btn_set_cancel);
		btn_ok.setOnClickListener(this);
		btn_cnl.setOnClickListener(this);
		dialog = new AlertDialog.Builder(getActivity()).setView(sureView)
				.create();

		return view;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isWifiEnabled = getArguments().getBoolean("isWifiEnabled", false);
		netWorkId = getArguments().getInt("netWorkId", -1);
		
		if (isWifiEnabled && netWorkId != -1) {
			new GetMenuSettingsValues().execute();
		} else {
			new AlertDialog.Builder(getActivity()).setTitle(R.string.dialog_no_connection_title)
			.setMessage(R.string.dialog_no_connection_message).setPositiveButton("ȷ��", null).show();
		}
		
		CameraSniffer sniffer = MainActivity.sniffer;
		MyPeeker peeker = new MyPeeker(this);
		sniffer.SetPeeker(peeker);
	}

	class MyPeeker extends CameraPeeker {
		CameraSettingsFragment theFrag;

		MyPeeker(CameraSettingsFragment frag) {
			theFrag = frag;
		}

		public void SetReceiver(CameraSettingsFragment frag) {
			theFrag = frag;
		}

		public void Update(String s) {
			// theFrag.Reception(s);
		}
	}

	private URL url;
	private Resources res;
	private static boolean isMotDet, isVoicOpen;

	@Override
	public void onClick(View v) {
		int what = 0;
		boolean isBack = false;
		try {
			switch (v.getId()) {
			case R.id.header_left:
				ivLeft.setVisibility(View.INVISIBLE);
				onEcarInfoListener.setOnEcarInfoListener(0x03);
				isBack = true;
				break;
			case R.id.tv_high:
				what = 0;
				url = new URL(VIDEO_RESOLUTION_HIGH);
				break;
			case R.id.tv_mindle:
				what = 1;
				url = new URL(VIDEO_RESOLUTION_MIDDLE);
				break;
			case R.id.tv_low:
				what = 2;
				url = new URL(VIDEO_RESOLUTION_LOW);
				break;
			case R.id.ivSettMotDet:
				what = 3;
				if (isMotDet)
					url = new URL(MOTION_SENSE_OFF);
				else
					url = new URL(MOTION_SENSE_ON);
				break;
			case R.id.ivCamSettVoic:
				what = 4;
				if (isVoicOpen)
					url = new URL(VOICE_RECORD_OFF);
				else
					url = new URL(VOICE_RECORD_ON);
				break;
			case R.id.ivFormatCamSD:
				dialog.show();
				return;
			case R.id.btn_set_ok:
				url = new URL(WIFI_SD_FORMARTTING);
				dialog.dismiss();
				break;
			case R.id.btn_set_cancel:
				dialog.dismiss();
				return;
			default:
				break;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (isBack) {
			return;
		}
		setSettStat(what);
		new CameraSettingsSendRequest().execute(url);
	}

	// http://192.72.1.1/cgi-bin/Config.cgi?action=set&property=Videores&value=1080P30fps&property=Imageres&value=14M

	// http://192.72.1.1/cgi-bin/Config.cgi?action=set&property=Videores&value=720P30fps&property=Imageres&value=5M

	// http://192.72.1.1/cgi-bin/Config.cgi?action=set&property=Videores&value=VGA&property=Imageres&value=1.2M

	// http://192.72.1.1/cgi-bin/Config.cgi?action=set&property=MTD&value=Off

	// http://192.72.1.1/cgi-bin/Config.cgi?action=set&property=MTD&value=Low

	private void setSettStat(int what) {
		switch (what) {
		case 0:
			tvHigh.setBackgroundColor(res.getColor(R.color.green));
			tvMid.setBackgroundColor(res.getColor(R.color.light_white));
			tvLow.setBackgroundColor(res.getColor(R.color.light_white));
			break;
		case 1:
			tvHigh.setBackgroundColor(res.getColor(R.color.light_white));
			tvMid.setBackgroundColor(res.getColor(R.color.green));
			tvLow.setBackgroundColor(res.getColor(R.color.light_white));
			break;
		case 2:
			tvHigh.setBackgroundColor(res.getColor(R.color.light_white));
			tvMid.setBackgroundColor(res.getColor(R.color.light_white));
			tvLow.setBackgroundColor(res.getColor(R.color.green));
			break;
		case 3:
			if (isMotDet) {
				isMotDet = false;
				ivMotDet.setImageResource(R.drawable.off_switch);
			} else {
				isMotDet = true;
				ivMotDet.setImageResource(R.drawable.on_switch);
			}
			break;
		case 4:
			if (isVoicOpen) {
				isVoicOpen = false;
				ivVoicCtl.setImageResource(R.drawable.off_switch);
			} else {
				isVoicOpen = true;
				ivVoicCtl.setImageResource(R.drawable.on_switch);
			}
			break;
		default:
			tvHigh.setBackgroundColor(res.getColor(R.color.green));
			tvMid.setBackgroundColor(res.getColor(R.color.light_white));
			tvLow.setBackgroundColor(res.getColor(R.color.light_white));
			ivMotDet.setImageResource(R.drawable.off_switch);
			ivVoicCtl.setImageResource(R.drawable.on_switch);
			break;
		}
	}
}
