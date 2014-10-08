/*
 * Copyright (c) 2014, Tim Verbelen
 * Internet Based Communication Networks and Services research group (IBCN),
 * Department of Information Technology (INTEC), Ghent University - iMinds.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Ghent University - iMinds, nor the names of its 
 *      contributors may be used to endorse or promote products derived from 
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.example.android;

import java.util.Collection;

import org.example.api.Greeting;
import org.osgi.framework.ServiceReference;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import be.iminds.androsgi.util.OSGiActivity;

public class HelloActivity extends OSGiActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hello);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hello, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_start:
	        	startService(new Intent(this, HelloService.class));
	        	return true;
	        case R.id.action_stop:
	        	stopService(new Intent(this, HelloService.class));
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public void greet(View view) {
		
		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>(){
			@Override
			protected String doInBackground(String... params) {
				String result = null;

				try {
					String name = params[0];
					String type = params[1];
					
					Collection<ServiceReference<Greeting>> refs = context.getServiceReferences(Greeting.class,  "(greeting.type="+type+")");
					if(!refs.isEmpty()){
						Greeting greeting = context.getService(refs.iterator().next());
						// call!
						result=greeting.greet(name);
					}
				} catch(Exception e){
					e.printStackTrace();
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				TextView textView = (TextView) findViewById(R.id.textView);
				textView.setText(result);
			}
		};
		
		EditText textField   = (EditText)findViewById(R.id.editText);
		String name = textField.getText().toString();
		
		RadioGroup selection = (RadioGroup)findViewById(R.id.radioGroup);
		RadioButton radio = (RadioButton) findViewById(selection.getCheckedRadioButtonId());
		String type = radio.getText().toString().toLowerCase();
		
		task.execute(name, type);
		
	}
}
