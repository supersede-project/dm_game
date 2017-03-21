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

public class RankingId implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Long gameId;
    private Long userId;

    public RankingId()
    {

    }

    public RankingId(Long gameId, Long userId)
    {
        this.gameId = gameId;
        this.userId = userId;
    }

    public Long getGameId()
    {
        return gameId;
    }

    public Long getUserId()
    {
        return userId;
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

        RankingId rankingId = (RankingId) o;

        if (gameId != rankingId.getGameId())
        {
            return false;
        }

        if (userId != rankingId.getUserId())
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (gameId ^ (gameId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
}