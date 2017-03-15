/*
(C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package eu.supersede.dm.rest;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.DMLibrary;
import eu.supersede.dm.DMMethod;

@RestController
@RequestMapping("processes/methods")
public class ProcessMethodsRest
{
    @RequestMapping(value = "/{methodname}/{action}", method = RequestMethod.POST)
    public void postToMethod(@PathVariable String methodName, @PathVariable String action,
            @RequestParam Map<String, String> args)
    {
        DMMethod m = DMLibrary.get().getMethod(methodName);
        if (m != null)
        {
            // m.post( action, args );
        }
    }
}