package de.htw.thesis.ba_ai.cryptovalid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import de.htw.thesis.ba_ai.cryptoprovider.R;

public class MainActivity extends Activity {

	private List<String> files;
	private DataHandler dataHandler;
	private CryptoHandler cryptoHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		File sdCard = Environment.getExternalStorageDirectory();
		File fileOutput = new File(sdCard, "TEST/LogFile.txt");
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(fileOutput);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (!fileOutput.exists())
		{
			try {
				fileOutput.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		dataHandler = new DataHandler(this);
		cryptoHandler = new CryptoHandler();
		
		files = new ArrayList<String>();
		files.add("1MB.txt");
//		files.add("2MB.zip");
		files.add("5MB.zip");
//		files.add("10MB.zip");
		files.add("20MB.zip");
//		files.add("100MB.zip");
		
		List<IvParameterSpec> usedIvs = new ArrayList<IvParameterSpec>();
		
		try
		{
			for (int j = 0; j < files.size(); j++) {
				
				// for the files 1 - 20 MB --> 10 Iterations ; for 100MB only 5 Iterations!!
				int iterations = files.get(j).length() <= 8 ? 10 : 5;
				SecretKey key = cryptoHandler.generateKey(128);
				for (int i = 0; i < iterations; i++) {
					dataHandler.Start();
					usedIvs.add(cryptoHandler.encryptAESWithIV("CBC", "PKCS7Padding", files.get(j), key));
					fw.append("AES128/CBC/PKCS7 ENC : " + dataHandler.getData() + "\n");
					Log.i("VALID", "AES128/CBC/PKCS7 ENC : " + dataHandler.getData());
				}
				
				for (int i = 0 ; i < iterations; i++){
					dataHandler.Start();
					cryptoHandler.decryptAESwithIV("CBC", "PKCS7Padding", files.get(j), key, usedIvs.get(i));
					fw.append("AES128/CBC/PKCS7 DEC : " + dataHandler.getData() + "\n");
					Log.i("VALID","AES128/CBC/PKCS7 DEC : " + dataHandler.getData());
				}
				
			}
			
			fw.flush();
			fw.close();
		}
		catch (Exception e) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
