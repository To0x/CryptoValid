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
import android.widget.TextView;
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

		dataHandler = new DataHandler();
		cryptoHandler = new CryptoHandler();

		files = new ArrayList<String>();
		files.add("1MB.txt");
		files.add("5MB.zip");
		files.add("20MB.zip");

		List<IvParameterSpec> usedIvs = new ArrayList<IvParameterSpec>();
		int[] AESkeySizes = { 128, 192, 256 };
		int[] BlowfishKeySizes = { 128, 256, 448 };
				
		String[] blockCipherAlgorithms = { "AES" , "Blowfish" };
		String[] modesAndPadding = { "CBC/PKCS7Padding", "OFB/NoPadding","CFB/NoPadding", "CTR/NoPadding" };

		try {
			for (int m = 0; m < blockCipherAlgorithms.length; m++) {
				for (int k = 0; k < modesAndPadding.length; k++) {
							
					int[] keySizes;
					boolean needIV;
					if (blockCipherAlgorithms[m].equals("AES"))
					{
						keySizes = AESkeySizes;
						needIV = true;
					}
					else
					{
						keySizes = BlowfishKeySizes;
						needIV = false;
					}
					
					for (int j = 0; j < keySizes.length; j++) {
						for (int i = 0; i < files.size(); i++) {

							int iterations = files.get(i).length() <= 8 ? 10 : 5;
							SecretKey key = cryptoHandler.generateBlockCipherKey(blockCipherAlgorithms[m],keySizes[j]);

							for (int l = 0; l < iterations; l++) {
								dataHandler.Start();
								
								if (needIV)
									usedIvs.add(cryptoHandler.encryptBlockCipherWithIV(blockCipherAlgorithms[m],modesAndPadding[k],files.get(i), key));
								else
									cryptoHandler.encryptBlockCipherWihtoutIV(blockCipherAlgorithms[m], modesAndPadding[k], files.get(i), key);
								
								Log.i("VALID", String.format("%s: %s%d/%s ENC : %s", files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k],dataHandler.getData(this)));
								fw.append(String.format("%s: %s%d/%s ENC : %s\r\n",files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k],dataHandler.getData(this)));
								fw.flush();
							}
							for (int l = 0; l < iterations; l++) {
								dataHandler.Start();
								
								if (needIV)
									cryptoHandler.decryptBlockCipherWithIV(blockCipherAlgorithms[m],modesAndPadding[k], files.get(i), key,usedIvs.get(l));
								else
									cryptoHandler.decryptBlockCipherWihtoutIV(blockCipherAlgorithms[m], modesAndPadding[k], files.get(i), key);
									
								Log.i("VALID", String.format("%s: %s%d/%s DEC : %s", files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k],dataHandler.getData(this)));
								fw.append(String.format("%s: %s%d/%s DEC : %s\r\n",files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k],dataHandler.getData(this)));
								fw.flush();
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			try {
				fw.append(e.getMessage());
				fw.flush();
				fw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

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
