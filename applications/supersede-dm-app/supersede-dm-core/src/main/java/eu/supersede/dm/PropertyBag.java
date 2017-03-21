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

package eu.supersede.dm;

import java.util.List;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProperty;

public class PropertyBag
{
    Long id;

    public PropertyBag()
    {
    }

    public PropertyBag(HActivity a)
    {
        this.id = a.getPropertyBag(); // .getId();
    }

    public void set(String key, String value)
    {
        HProperty p = DMGame.get().jpa.properties.findOne(id);
        if (p == null)
        {
            p = new HProperty();
        }
        p.setPropertyBagId(this.id);
        p.setKey(key);
        p.setValue(value);
        DMGame.get().jpa.properties.save(p);
    }

    public String get(String key, String defaultValue)
    {
        List<HProperty> list = DMGame.get().jpa.properties.find(id, key);

        if (list == null)
        {
            return defaultValue;
        }

        if (list.size() < 1)
        {
            return defaultValue;
        }

        return list.get(0).getValue();
    }

    public List<HProperty> getProperties()
    {
        return DMGame.get().getJpa().properties.findByPropertyBag(id);
    }
}