/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cloudhopper.httpclient.util;

import org.apache.log4j.Logger;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import java.io.IOException;

/**
 *
 * @author joelauer
 */
public class HttpServerMain extends GrizzlyAdapter {
    private static final Logger logger = Logger.getLogger(HttpServerMain.class);

    static public void main(String[] args) throws Exception {
        GrizzlyWebServer ws = new GrizzlyWebServer(9080);
        try {
            GrizzlyAdapter ga = new HttpServerMain();
            ws.addGrizzlyAdapter(ga, new String[] {"/"});
            ws.start();
        } catch (IOException e){
            logger.error("", e);
        }
    }

    @Override
    public void service(GrizzlyRequest request, GrizzlyResponse response) {
        try {
            //
            // process input stream
            //
            //boolean ok = ParlayXSession.process(this, request.getInputStream(), response.getWriter(), true);

            //if (!ok) {
            //    response.setStatus(500);
            //}
        } catch (Exception e) {
            logger.error("Failed to process request", e);
        }
    }
}
