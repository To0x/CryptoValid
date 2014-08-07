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

		if (!fileOutput.exists()) {
			try {
				fileOutput.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			dataHandler = new DataHandler(this);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		cryptoHandler = new CryptoHandler();

		files = new ArrayList<String>();
		files.add("1MB.txt");
		// files.add("2MB.zip");
		files.add("5MB.zip");
		// files.add("10MB.zip");
		files.add("20MB.zip");
		// files.add("100MB.zip");

		List<IvParameterSpec> usedIvs = new ArrayList<IvParameterSpec>();
		int[] keySizes = { 128, 192, 256 };
		String[] modesAndPadding = {"CBC/PKCS7Padding", "OFB/NoPadding","CFB/NoPadding","CTR/NoPadding"};

		try {
			for (int i = 0; i < files.size(); i++) {
				int iterations = files.get(i).length() <= 8 ? 2 : 1; // for the files 1 - 20 MB --> 10 Iterations ; for 100MB only 5 Iterations!
				for (int j = 0; j < keySizes.length; j++) {
					SecretKey key = cryptoHandler.generateAESKey(keySizes[j]);
					for (int k = 0; k < modesAndPadding.length; k++) {
						usedIvs.clear();
						for (int l = 0; l < iterations; l++) {
							dataHandler.Start();
							usedIvs.add(cryptoHandler.encryptAESWithIV(modesAndPadding[k], files.get(i), key));
							Log.i("VALID", String.format("%s: AES%d/%s ENC : %s",files.get(i).split("\\.")[0], keySizes[j],modesAndPadding[k],dataHandler.getData()));
							fw.append(String.format("%s: AES%d/%s ENC : %s\r\n",files.get(i).split("\\.")[0], keySizes[j],modesAndPadding[k],dataHandler.getData()));
						}
						for (int l = 0; l < iterations; l++) {
							dataHandler.Start();
							cryptoHandler.decryptAESwithIV(modesAndPadding[k], files.get(i), key,usedIvs.get(l));
							Log.i("VALID", String.format("%s: AES%d/%s DEC : %s",files.get(i).split("\\.")[0], keySizes[j], modesAndPadding[k],dataHandler.getData()));
							fw.append(String.format("%s: AES%d/%s DEC : %s\r\n",files.get(i).split("\\.")[0], keySizes[j],modesAndPadding[k],dataHandler.getData()));
						}
					}
				}
			}

			fw.flush();
			fw.close();
			
		} catch (Exception e) {
			try {
				fw.append(e.getMessage());
				fw.flush();
				fw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
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
