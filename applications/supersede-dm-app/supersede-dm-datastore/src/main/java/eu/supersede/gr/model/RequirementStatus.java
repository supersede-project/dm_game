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

package eu.supersede.gr.model;

import java.util.HashSet;
import java.util.Set;

public enum RequirementStatus
{
    Unconfirmed(0), Editable(1), Confirmed(2), Enacted(3), Discarded(4);
    private Integer value;

    RequirementStatus(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return this.value;
    }

    public static RequirementStatus valueOf(Integer status)
    {
        switch (status)
        {
            case 0:
                return Unconfirmed;
            case 1:
                return Editable;
            case 2:
                return Confirmed;
            case 3:
                return Enacted;
            case 4:
                return Discarded;
            default:
                return Unconfirmed;
        }
    }

    public static Set<RequirementStatus> next(RequirementStatus status)
    {
        Set<RequirementStatus> set = new HashSet<RequirementStatus>();
        switch (status)
        {
            case Unconfirmed:
                set.add(RequirementStatus.Editable);
                set.add(RequirementStatus.Discarded);
                break;
            case Editable:
                set.add(RequirementStatus.Confirmed);
                set.add(RequirementStatus.Discarded);
                break;
            case Confirmed:
                set.add(RequirementStatus.Enacted);
                break;
            case Discarded:
                break;
            case Enacted:
                break;
        }
        return set;
    }
}