package de.htw.thesis.ba_ai.cryptovalid;

import java.security.Provider;
import java.security.Security;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import de.htw.thesis.ba_ai.cryptoprovider.R;



public class MainActivity extends Activity {
	
	private MainRunner t;
	private TextView tv;
	private boolean threadIsWaiting, threadIsRunning;
		
	public Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			tv.setText(msg.getData().getCharSequence("VALID").toString());
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onPause() {
			try {
				synchronized (t) {
					t.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threadIsWaiting = true;
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		if (!threadIsRunning)
		{
			t.start();
			threadIsRunning = true;
		}
		
		if (threadIsWaiting)
		{
			synchronized (t) {
				t.notify();
			}
			threadIsWaiting = false;
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView)findViewById(R.id.textView);
				
		t = new MainRunner(this);
		t.setPriority(Thread.MAX_PRIORITY);
		threadIsWaiting = false;
		threadIsRunning = false;

		/*
		 * 
		 * try { for (int i = 0; i < files.size(); i++) { int iterations =
		 * files.get(i).length() <= 8 ? 2 : 1; // for the files 1 - 20 MB --> 10
		 * Iterations ; for 100MB only 5 Iterations! for (int j = 0; j <
		 * keySizes.length; j++) { SecretKey key =
		 * cryptoHandler.generateAESKey(keySizes[j]); for (int k = 0; k <
		 * modesAndPadding.length; k++) { usedIvs.clear(); for (int l = 0; l <
		 * iterations; l++) { dataHandler.Start();
		 * usedIvs.add(cryptoHandler.encryptAESWithIV(modesAndPadding[k],
		 * files.get(i), key)); Log.i("VALID",
		 * String.format("%s: AES%d/%s ENC : %s",files.get(i).split("\\.")[0],
		 * keySizes[j],modesAndPadding[k],dataHandler.getData(this)));
		 * fw.append(
		 * String.format("%s: AES%d/%s ENC : %s\r\n",files.get(i).split
		 * ("\\.")[0],
		 * keySizes[j],modesAndPadding[k],dataHandler.getData(this))); } for
		 * (int l = 0; l < iterations; l++) { dataHandler.Start();
		 * cryptoHandler.decryptAESwithIV(modesAndPadding[k], files.get(i),
		 * key,usedIvs.get(l)); Log.i("VALID",
		 * String.format("%s: AES%d/%s DEC : %s",files.get(i).split("\\.")[0],
		 * keySizes[j], modesAndPadding[k],dataHandler.getData(this)));
		 * fw.append
		 * (String.format("%s: AES%d/%s DEC : %s\r\n",files.get(i).split
		 * ("\\.")[0],
		 * keySizes[j],modesAndPadding[k],dataHandler.getData(this))); } } } }
		 * 
		 * fw.flush(); fw.close();
		 * 
		 * } catch (Exception e) { try { fw.append(e.getMessage()); fw.flush();
		 * fw.close(); } catch (IOException e1) { e1.printStackTrace(); } }
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void printCryptoProvider() {
		Provider[] providers = Security.getProviders();
		for (Provider provider : providers) {
			Log.i("CRYPTO", "provider: " + provider.getName());
			Set<Provider.Service> services = provider.getServices();
			for (Provider.Service service : services) {
				Log.i("CRYPTO", "type: " + service.getType() + "  algorithm: "
						+ service.getAlgorithm());
			}
		}
	}
}
