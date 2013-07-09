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
package com.cloudhopper.sxmp;

/**
 * Priority for internal priority queuing and SMPP priority_flag.
 *
 * From SMPP 3.4 issue 1.2 spec (p. 123/169):
 * 5.2.14 priority_flag
 *  The priority_flag parameter allows the originating SME to assign a priority level
 *  to the short message.
 *  Four Priority Levels are supported:
 *   0 = Level 0 (lowest) priority
 *   1 = Level 1 priority
 *   2 = Level 2 priority
 *   3 = Level 3 (highest) priority
 *   >3= Reserved
 *  These are applied in different networks as follows:-
 *  Priority Level    GSMa   ANSI-136       IS-95
 *   0        non-priority    Bulk           Normal
 *   1        priority        Normal         Interactive
 *   2        priority        Urgent         Urgent
 *   3        priority        Very Urgent    Emergency
 *   All other values reserved
 *   For GSM mobile terminated, messages with priority greater than Level 0 are
 *   treated as priority when making a delivery attempt (i.e. a delivery attempt
 *   is made even when MWD is set in the HLR).
 *
 * @author garth
 */
public enum Priority {
    
    NORMAL(0, "Normal", "Bulk", 127),
    INTERACTIVE(1, "Interactive", "Normal", 126),
    URGENT(2, "Urgent", "Urgent", 1),
    EMERGENCY(3, "Emergency", "Very Urgent", 0);

    private Integer priorityFlag;
    private String is95;
    private String ansi136;
    private Integer chmqPriority;

    Priority(int priorityFlag, String is95, String ansi136, int chmqPriority) {
	this.priorityFlag = priorityFlag;
	this.is95 = is95;
	this.ansi136 = ansi136;
	this.chmqPriority = chmqPriority;
    }

    public static Priority fromPriorityFlag(int priorityFlag) {
	switch (priorityFlag) {
	case 0: return NORMAL;
	case 1: return INTERACTIVE;
	case 2: return URGENT;
	case 3: return EMERGENCY;
	default: throw new IllegalArgumentException("priority_flag must be between 0 and 3");
        }
    }

    public Integer getPriorityFlag() {
        return priorityFlag;
    }
 
    public String getIs95() {
        return is95;
    }
 
    public String getAnsi136() {
        return ansi136;
    }

    public Integer getChmqPriority() {
        return chmqPriority;
    }

    @Override
    public String toString() {
        final StringBuilder o = new StringBuilder();
        o.append("Priority");
        o.append("{priority_flag=").append(priorityFlag);
        o.append(", IS-95='").append(is95).append('\'');
        o.append(", ANSI-136='").append(ansi136).append('\'');
        o.append(", chmqPriority='").append(chmqPriority).append('\'');
        o.append('}');
        return o.toString();
    }
 
}
