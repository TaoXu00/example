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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import be.iminds.androsgi.util.OSGiService;

public class HelloService extends OSGiService {

	private boolean running = false;
	
	private Handler handler;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void onCreate() {
		super.onCreate();
		// create a handler for handling stuff on UI thread
		handler = new Handler(Looper.getMainLooper()){
			public void handleMessage(Message msg){
				Toast.makeText(HelloService.this, ""+msg.obj, Toast.LENGTH_LONG).show();
			}
		};
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Thread backgroundThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				running = true;
				while(running){
					try {
						Message helloMessage = new Message();
						helloMessage.obj = "<No service available>";
						
						Collection<ServiceReference<Greeting>> refs = context.getServiceReferences(Greeting.class,  "(greeting.type=hi)");
						if(!refs.isEmpty()){
							Greeting greeting = context.getService(refs.iterator().next());
							// call!
							helloMessage.obj=greeting.greet("Android");
						}
						
						handler.sendMessage(helloMessage);
						
						Thread.sleep(20000);
					} catch(Exception e){}
				}
			}
		});
    	backgroundThread.start();
    	
    	// For time consuming an long tasks you can launch a new thread here...
        Toast.makeText(this, "Hello Service Started", Toast.LENGTH_LONG).show();
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	running = false;
        Toast.makeText(this, "Hello Service Destroyed", Toast.LENGTH_LONG).show();

    }
}
