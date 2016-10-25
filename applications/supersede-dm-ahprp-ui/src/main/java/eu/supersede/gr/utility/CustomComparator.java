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

package eu.supersede.gr.utility;

import java.util.Comparator;

import eu.supersede.gr.model.GamePlayerPoint;

public class CustomComparator implements Comparator<GamePlayerPoint> {
    @Override
    public int compare(GamePlayerPoint o1, GamePlayerPoint o2) {
        return o2.getPoints().compareTo(o1.getPoints());
    }
}