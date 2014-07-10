package de.htw.thesis.ba_ai.cryptovalid;

import java.security.Provider;
import java.security.Security;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import de.htw.thesis.ba_ai.cryptoprovider.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		//CryptoHandler ch = new CryptoHandler();
		//ch.EncryptAES();
		
		this.printCryptoProvider();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void printCryptoProvider()
	{
		Provider[] providers = Security.getProviders();
		for (Provider provider : providers) {
		    Log.i("CRYPTO","provider: "+provider.getName());
		    Set<Provider.Service> services = provider.getServices();
		    for (Provider.Service service : services) {
		        Log.i("CRYPTO","type: " + service.getType() + "  algorithm: "+service.getAlgorithm());
		    }
		}
	}
}
