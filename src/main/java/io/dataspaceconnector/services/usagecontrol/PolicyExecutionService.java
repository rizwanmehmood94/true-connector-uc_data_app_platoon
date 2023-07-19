/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.utils.RuleUtils;
import org.springframework.stereotype.Service;

import io.dataspaceconnector.exceptions.PolicyExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//TECNALIA-ICT-OPTIMA: Comment out the methods, so that they do nothing.
/**
 * Executes policy conditions. Refers to the ids policy enforcement point (PEP).
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PolicyExecutionService {

    /**
     * Service for ids log messages.
     */
    //private final @NonNull LogMessageService logMessageService;

  /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     *
     * @param target The target object.
     * @throws PolicyExecutionException if the access could not be successfully logged.
     */
    public void logDataAccess(final String target) throws PolicyExecutionException {
/*        final var recipient = connectorConfig.getClearingHouse();
        final var logItem = buildLog(target).toString();

        if (!recipient.equals(URI.create(""))) {
            logMessageService.sendMessage(recipient, logItem);
        }*/
    }


  /**
   * Send a message to the URL. Allow the access only if that operation was successful.
   *
   * @param target The target object.
   * @throws PolicyExecutionException if the access could not be successfully logged.
   */
  public void remoteNotifyDataAccess(final String target) throws PolicyExecutionException {
/*        final var recipient = connectorConfig.getClearingHouse();
        final var logItem = buildLog(target).toString();

        if (!recipient.equals(URI.create(""))) {
            logMessageService.sendMessage(recipient, logItem);
        }*/
  }


  /**
   * Send a message to the clearing house. Allow the access only if that operation was successful.
   *
   * @param rule    The ids rule.
   * @param element The accessed element.
   * @throws PolicyExecutionException If the notification has not been successful.
   */
  public void reportDataAccess(final Rule rule, final String element)
          throws PolicyExecutionException {
/*    if (rule instanceof Permission permission) {
      final var postDuty = permission.getPostDuty().get(0);
      final var recipient = RuleUtils.getEndpoint(postDuty);

      //notificationSvc.sendMessage(URI.create(recipient), buildLog(element));
    } else if (log.isWarnEnabled()) {
      log.warn("Reporting data access is only supported for permissions.");
    }*/
  }


    /**
     * Build a log information object.
     *
     * @param target The accessed element.
     * @return The log line.
     */
   private Map<String, Object> buildLog(final String target, String connectorId) {

        return new HashMap<>() {{
            put("target", target);
            put("issuerConnector", connectorId);
            put("accessed", new Date());
        }};
    }
}
