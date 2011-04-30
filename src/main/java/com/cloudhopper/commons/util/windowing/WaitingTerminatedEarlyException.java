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

package com.cloudhopper.commons.util.windowing;

/**
 * Thrown when all waiting callers/threads were canceled and they were signaled
 * to timeout early.  A subclass of MaxWindowSizeTimeoutException since it
 * more or less indicates a timeout occurred, just a little earlier than the 
 * caller may have expected.
 * 
 * @author joelauer
 */
public class WaitingTerminatedEarlyException extends MaxWindowSizeTimeoutException {
    
    public WaitingTerminatedEarlyException(String msg) {
        super(msg);
    }
    
}