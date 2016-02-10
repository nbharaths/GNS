/*
 *
 *  Copyright (c) 2015 University of Massachusetts
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you
 *  may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Initial developer(s): Westy
 *
 */
package edu.umass.cs.gnsserver.gnsApp.clientCommandProcessor.commands.admin;

import edu.umass.cs.gnsserver.gnsApp.clientCommandProcessor.commandSupport.CommandResponse;
import static edu.umass.cs.gnscommon.GnsProtocol.*;
import edu.umass.cs.gnsserver.gnsApp.clientCommandProcessor.commandSupport.PerformanceTests;
import edu.umass.cs.gnsserver.gnsApp.clientCommandProcessor.commands.CommandModule;
import edu.umass.cs.gnsserver.gnsApp.clientCommandProcessor.commands.GnsCommand;
import edu.umass.cs.gnsserver.gnsApp.clientCommandProcessor.demultSupport.ClientRequestHandlerInterface;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author westy
 */
public class RTT extends GnsCommand {

  /**
   *
   * @param module
   */
  public RTT(CommandModule module) {
    super(module);
  }

  @Override
  public String[] getCommandParameters() {
    return new String[]{N, GUIDCNT};
  }

  @Override
  public String getCommandName() {
    return RTT_TEST;
  }

  @Override
  public CommandResponse<String> execute(JSONObject json, ClientRequestHandlerInterface handler) throws JSONException {
    if (module.isAdminMode()) {
      String sizeString = json.getString(N);
      int size = Integer.parseInt(sizeString);
      String guidCntString = json.getString(GUIDCNT);
      int guidCnt = Integer.parseInt(guidCntString);
      return new CommandResponse<String>(PerformanceTests.runRttPerformanceTest(size, guidCnt, true, handler));
    } else {
      return new CommandResponse<String>(BAD_RESPONSE + " " + OPERATION_NOT_SUPPORTED + " Don't understand " + getCommandName());
    }

  }

  @Override
  public String getCommandDescription() {
    return "Runs the round trip test.";
  }
}
