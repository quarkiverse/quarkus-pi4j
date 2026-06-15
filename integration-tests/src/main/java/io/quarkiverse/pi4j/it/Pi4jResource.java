/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.quarkiverse.pi4j.it;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;

import io.quarkiverse.pi4j.gpio.GpioService;
import io.quarkiverse.pi4j.gpio.NamedGpio;

@Path("/pi4j")
public class Pi4jResource {
    @Inject
    Context context;
    @Inject
    GpioService gpio;
    @Inject
    @NamedGpio("led")
    DigitalOutput led;

    @GET
    public String status() {
        return context != null ? "ok" : "missing";
    }

    @POST
    @Path("/led/on")
    public String ledOn() {
        led.high();
        return "on";
    }

    @POST
    @Path("/pin/13/off")
    public String pinOff() {
        gpio.output(13).low();
        return "off";
    }
}
