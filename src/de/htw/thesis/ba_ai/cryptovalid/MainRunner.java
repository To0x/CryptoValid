package de.htw.thesis.ba_ai.cryptovalid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

public class MainRunner extends Thread
{
	private List<String> files;
	private DataHandler dataHandler;
	private CryptoHandler cryptoHandler;
	private MainActivity myMain;
	
	public MainRunner(MainActivity m)
	{
		myMain = m;
	}
	@Override
	public void run() {
		
	
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
				
		//String[] blockCipherAlgorithms = { "AES" , "Blowfish" };
		String[] blockCipherAlgorithms = { "Blowfish" };
		//String[] modesAndPadding = { "CBC/PKCS7Padding", "OFB/NoPadding","CFB/NoPadding", "CTR/NoPadding" };
		String[] modesAndPadding = { "CTR/NoPadding" };

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
								Message msg = Message.obtain();
								Bundle b = new Bundle();
								b.putCharSequence("VALID", String.format("%d/%d %s: %s%d/%s ENC",(l+1),iterations, files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k]));
								msg.setData(b);
								msg.what = 1;
								myMain.myHandler.sendMessage(msg);
								
								dataHandler.Start();
								
								if (needIV)
									usedIvs.add(cryptoHandler.encryptBlockCipherWithIV(blockCipherAlgorithms[m],modesAndPadding[k],files.get(i), key));
								else
									cryptoHandler.encryptBlockCipherWihtoutIV(blockCipherAlgorithms[m], modesAndPadding[k], files.get(i), key);
								
								Log.i("VALID", String.format("%s: %s%d/%s ENC : %s", files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k],dataHandler.getData(myMain)));
								fw.append(String.format("%s: %s%d/%s ENC : %s\r\n",files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k],dataHandler.getData(myMain)));
								fw.flush();
							}
							for (int l = 0; l < iterations; l++) {
								Message msg = Message.obtain();
								Bundle b = new Bundle();
								b.putCharSequence("VALID", String.format("%d/%d %s: %s%d/%s DEC",(l+1),iterations, files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k]));
								msg.setData(b);
								msg.what = 1;
								myMain.myHandler.sendMessage(msg);
								
								dataHandler.Start();
								
								if (needIV)
									cryptoHandler.decryptBlockCipherWithIV(blockCipherAlgorithms[m],modesAndPadding[k], files.get(i), key,usedIvs.get(l));
								else
									cryptoHandler.decryptBlockCipherWihtoutIV(blockCipherAlgorithms[m], modesAndPadding[k], files.get(i), key);
									
								Log.i("VALID", String.format("%s: %s%d/%s DEC : %s", files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k],dataHandler.getData(myMain)));
								fw.append(String.format("%s: %s%d/%s DEC : %s\r\n",files.get(i).split("\\.")[0], blockCipherAlgorithms[m],keySizes[j], modesAndPadding[k],dataHandler.getData(myMain)));
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
	}
}