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

package eu.supersede.gr.model.id;

import java.io.Serializable;

public class ProcessMemberId implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long processId;

    public ProcessMemberId()
    {

    }

    public ProcessMemberId(Long id, Long userId, Long processId)
    {
        this.id = id;
        this.userId = userId;
        this.processId = processId;
    }

    public Long getId()
    {
        return id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public Long getProcessId()
    {
        return processId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        ProcessMemberId processMemberId = (ProcessMemberId) o;

        if (id != processMemberId.getId())
        {
            return false;
        }

        if (userId != processMemberId.getUserId())
        {
            return false;
        }

        if (processId != processMemberId.getProcessId())
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (processId ^ (processId >>> 32));
        return result;
    }
}