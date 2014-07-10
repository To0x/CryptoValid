package de.htw.thesis.ba_ai.cryptovalid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import android.os.Environment;
import android.util.Log;

public class CryptoHandler {

	private List<String> files;
	
	public CryptoHandler()
	{
		files = new ArrayList<String>();
		files.add("5MB.zip");
		files.add("20MB.zip");
	}
	
	public void EncryptAES()
	{
		FileInputStream fis;
		FileOutputStream fos;
		CipherOutputStream cos;
		CipherInputStream cis;
		
		
		
		try {
			Timer t = new Timer();

			File sdCard = Environment.getExternalStorageDirectory();
			File fileInput = new File(sdCard, "TEST/20MB.zip");
			File fileOutput = new File(sdCard, "TEST/enc");
			
			
			Cipher cip = Cipher.getInstance("RSA/None/PKCS1Padding");
			Cipher cip2 = Cipher.getInstance("RSA/None/PKCS1Padding");
			//KeyGenerator kgen = KeyGenerator.getInstance("AES");
			KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA");
			kgen.initialize(4096);
			KeyPair kpair = kgen.generateKeyPair();
			PrivateKey priv = kpair.getPrivate();
			PublicKey pub = kpair.getPublic();
			
			if (!fileInput.exists())
				fileInput.createNewFile();
			
			if (!fileOutput.exists())
				fileOutput.createNewFile();
			
			fis = new FileInputStream(fileInput);
			fos = new FileOutputStream(fileOutput);
		
//			kgen.init(256);
//			SecretKey skey = kgen.generateKey();
			
			cip.init(Cipher.ENCRYPT_MODE, pub);
			cos = new CipherOutputStream(fos, cip);
			
			
			cip2.init(Cipher.DECRYPT_MODE, priv);
			
			//byte[] input = "bla".getBytes("UTF-8");
			//byte[] encrypted = cip.doFinal(input);
			
			
			t.startTimer();
			/// ENCRYPT ///
			int read;
			byte[] buffer = new byte[1024];
			
			while ((read = fis.read(buffer)) != -1) 
			{
				cos.write(buffer, 0 , read);
			}
			
			cos.flush();
			cos.close();
			fos.close();
			fis.close();
			
			System.out.println(t.stopTimer());
			Log.i("MY", String.format("%d ms ENCRYPT", t.stopTimer()));
			
			t.startTimer();
			/// DECRYPT ///
			FileInputStream fis2 = new FileInputStream(new File(sdCard, "TEST/enc"));
			FileOutputStream fos2 = new FileOutputStream(new File(sdCard, "TEST/20MB2.zip"));
			
			cis = new CipherInputStream(fis2, cip2);
			while ((read = cis.read(buffer)) != -1)
			{
				fos2.write(buffer,0,read);
			}
			
			fos2.flush();
			fos2.close();
			cis.close();
			fis2.close();
			
			System.out.println(t.stopTimer());
			Log.i("MY", String.format("%d ms DECRYPT", t.stopTimer()));
			
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
	