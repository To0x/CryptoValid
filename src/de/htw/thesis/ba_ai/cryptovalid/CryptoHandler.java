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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.os.Environment;
import android.util.Log;

public class CryptoHandler {

	private List<String> files;
	private Timer t;
	private  SecureRandom rnd;
	
	public CryptoHandler()
	{
		try {
			files = new ArrayList<String>();
			files.add("5MB.zip");
			files.add("20MB.zip");
			
			rnd = SecureRandom.getInstance("SHA1PRNG");
		}
		catch (Exception e) {
			rnd = null;
		}
	}
	
	public SecretKey generateBlockCipherKey(String algorithm, int keySize)
	{
		try{
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(keySize);
			return kgen.generateKey();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private void decryptBlockCipher(String algorithm, String modeAndPadding, String fileName, SecretKey key, IvParameterSpec iv) throws Exception
	{
		CipherInputStream cis;
		FileInputStream fis;
		FileOutputStream fos;
		
		String cipherAlgo = algorithm;
		if (modeAndPadding.length() < 1)
		{
			cipherAlgo = String.format("%s/%s", algorithm, modeAndPadding);
		}
		
		// generate cipher
		File sdCard = Environment.getExternalStorageDirectory();
		Cipher cipDecrypt = Cipher.getInstance(cipherAlgo);
		
		if (iv == null)
			cipDecrypt.init(Cipher.DECRYPT_MODE, key, iv);
		else
			cipDecrypt.init(Cipher.DECRYPT_MODE, key);
		

		fis = new FileInputStream(new File(sdCard, String.format("TEST/%sEnc",fileName)));
		fos = new FileOutputStream(new File(sdCard, String.format("TEST/%sDec",fileName)));
		cis = new CipherInputStream(fis, cipDecrypt);

		/// DECRYPT ///
		int read;
		byte[] buffer = new byte[cipDecrypt.getBlockSize()];
		while ((read = cis.read(buffer)) != -1)
		{
			fos.write(buffer,0,read);
		}
		
		fos.flush();
		fos.close();
		cis.close();
		fis.close();
	}
	
	private IvParameterSpec encryptBlockCipher(String algorithm, String modeAndPadding, String fileName, SecretKey key, boolean withIV) throws Exception {
		FileInputStream fis;
		FileOutputStream fos;
		CipherOutputStream cos;
		
		File sdCard = Environment.getExternalStorageDirectory();
		File fileInput = new File(sdCard, String.format("TEST/%s",fileName));
		File fileOutput = new File(sdCard, String.format("TEST/%sEnc", fileName));
		
		String cipherAlgo = algorithm;
		if (modeAndPadding.length() < 1)
		{
			cipherAlgo = String.format("%s/%s", algorithm, modeAndPadding);
		}
		
		// generate Cipher
		Cipher cipEncrypt = Cipher.getInstance(cipherAlgo);		
        final byte[] ivData = new byte[cipEncrypt.getBlockSize()];
        rnd.nextBytes(ivData);
        final IvParameterSpec iv = new IvParameterSpec(ivData);
        
        if (withIV)
        	cipEncrypt.init(Cipher.ENCRYPT_MODE, key, iv);
        else
        	cipEncrypt.init(Cipher.ENCRYPT_MODE, key);
        
		if (!fileInput.exists())
			fileInput.createNewFile();
		
		if (!fileOutput.exists())
			fileOutput.createNewFile();
		
		fis = new FileInputStream(fileInput);
		fos = new FileOutputStream(fileOutput);
		

		cos = new CipherOutputStream(fos, cipEncrypt);
				
		
		/// ENCRYPT ///
		int read;
		byte[] buffer = new byte[cipEncrypt.getBlockSize()];
		
		while ((read = fis.read(buffer)) != -1) 
		{
			cos.write(buffer, 0 , read);
		}
		
		cos.flush();
		cos.close();
		fos.close();
		fis.close();
		
		return iv;
	}
	
	public void decryptBlockCipherWithIV(String algorithm, String modeAndPadding, String fileName, SecretKey key, IvParameterSpec iv) throws Exception {
		decryptBlockCipher(algorithm, modeAndPadding, fileName, key, iv);
	}
	
	public void decryptBlockCipherWihtoutIV(String algorithm, String modeAndPadding, String fileName, SecretKey key) throws Exception {
		decryptBlockCipher(algorithm, modeAndPadding, fileName, key, null);
	}
	
	public IvParameterSpec encryptBlockCipherWithIV(String algorithm, String modeAndPadding, String fileName, SecretKey key) throws Exception
	{
		return this.encryptBlockCipher(algorithm, modeAndPadding, fileName, key, true);
	}
	
	public void encryptBlockCipherWihtoutIV(String algorithm, String modeAndPadding, String fileName, SecretKey key) throws Exception {
		this.encryptBlockCipher(algorithm, modeAndPadding, fileName, key, false);
	}
	
	/*
	public void EncryptAES()
	{
		FileInputStream fis;
		FileOutputStream fos;
		CipherOutputStream cos;
		CipherInputStream cis;
		
		
		try {

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
	*/
}
	