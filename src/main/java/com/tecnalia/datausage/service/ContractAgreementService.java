/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tecnalia.datausage.service;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import com.tecnalia.datausage.utils.HttpUtils;
import de.fraunhofer.iais.eis.Connector;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import com.tecnalia.datausage.model.ContractStore;
import com.tecnalia.datausage.model.RuleStore;
import com.tecnalia.datausage.repository.ContractRepository;
import com.tecnalia.datausage.repository.RuleRepository;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import org.springframework.web.client.RestTemplate;

/**
 * @author root
 */

@Service
public class ContractAgreementService {

    private static final Logger log = LoggerFactory.getLogger(ContractAgreementService.class);

    @Autowired
    private ContractRepository contractRepository;
    @Autowired

    private RuleRepository ruleRepository;

    private HttpUtils httpUtils;
    private SelfDescriptionServiceImpl getSelfDescription;


    @Autowired
    public ContractAgreementService(ContractRepository contractRepository, RuleRepository ruleRepository, HttpUtils httpUtils, SelfDescriptionServiceImpl getSelfDescription)
            throws IllegalArgumentException {
        // public PolicyService(OdrlPolicyRepo repository) throws
        // IllegalArgumentException {

        if (contractRepository == null)
            throw new IllegalArgumentException("The Contract Repo cannot be null.");

        this.ruleRepository = ruleRepository;
        if (ruleRepository == null)
            throw new IllegalArgumentException("The Contract Repo cannot be null.");

        this.ruleRepository = ruleRepository;
        this.httpUtils = httpUtils;
        this.getSelfDescription = getSelfDescription;

    }

    @Transactional
    public ResponseEntity<String> addOrUpdate(String policy) {

        try {

            System.out.println("Policy--->" + policy);
            Serializer serializer = new Serializer();
            Contract contract = serializer.deserialize(policy, Contract.class);

            Optional<ContractStore> bCheckExistsContract = this.contractRepository
                    .findByContractId(contract.getId().toString());
            ContractStore contractStore;
            UUID contractUuid = null;
            // if exists update
            String message = "";
            if (bCheckExistsContract.isPresent()) {

                message = "Contract Agreement has been updated";
                log.info("PolicyService:::policyId :" + bCheckExistsContract.isPresent());
                contractStore = (ContractStore) bCheckExistsContract.get();
                contractStore.setContractAsString(policy);
                contractStore.setConsumerId(contract.getConsumer().toString());
                contractStore.setProviderId(contract.getProvider().toString());
                this.contractRepository.saveAndFlush(setProfile(policy, contractStore));

            }

            // insert new policy
            else {
                message = "Contract Agreement has been added";
                contractUuid = UUID.randomUUID();
                log.info("PolicyService:::policyId :" + bCheckExistsContract.isPresent());
                contractStore = new ContractStore();
                contractStore.contractUuid(contractUuid.toString());
                contractStore.setContractId(contract.getId().toString());
                contractStore.setContractAsString(policy);
                if (contract.getConsumer() != null) {
                    contractStore.setConsumerId(contract.getConsumer().toString());
                } else {
                    return new ResponseEntity<String>("Consumer URI paramater has not been added", HttpStatus.BAD_REQUEST);
                }

                contractStore.setProviderId(contract.getProvider().toString());
                this.contractRepository.saveAndFlush(setProfile(policy, contractStore));

            }

            // update rules
            Iterable<RuleStore> ruleList = this.ruleRepository.findAllByContractId(contract.getId().toString());

            // if exists delete
            if (ruleList.iterator().hasNext()) {

                List<RuleStore> ruleStores = this.ruleRepository.deleteByContractId(contract.getId().toString());
                log.info("PolicyService:::policyId :" + ruleStores.size());

            }

            if ((contract.getProhibition() != null) && (contract.getProhibition().size() != 0)) {
                // create rules
                for (Prohibition prohibition : contract.getProhibition()) {
                    UUID ruleUuid = UUID.randomUUID();

                    RuleStore ruleStore = new RuleStore();
                    ruleStore.setContractUuid(contractStore.getContractUuid());
                    ruleStore.setRuleUuid(ruleUuid.toString());
                    ruleStore.setContractId(contract.getId().toString());
                    ruleStore.setRuleId(prohibition.getId().toString());
                    ruleStore.setTargetId(prohibition.getTarget().toString());
                    String prohibitionJson = serializer.serialize(prohibition);
                    ruleStore.setRuleContent(prohibitionJson);
                    this.ruleRepository.saveAndFlush(ruleStore);
                }

            } else if ((contract.getPermission() != null) && (contract.getPermission().size() != 0)) {

                for (Permission permission : contract.getPermission()) {

                    UUID ruleUuid = UUID.randomUUID();
                    RuleStore ruleStore = new RuleStore();
                    ruleStore.setContractUuid(contractStore.getContractUuid());
                    ruleStore.setContractId(contract.getId().toString());
                    ruleStore.setRuleUuid(ruleUuid.toString());
                    ruleStore.setRuleId(permission.getId().toString());
                    ruleStore.setTargetId(permission.getTarget().toString());
                    String permissionJson = serializer.serialize(permission);
                    ruleStore.setRuleContent(permissionJson);
                    this.ruleRepository.saveAndFlush(ruleStore);
                }

            }
            return new ResponseEntity<String>(message, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getMessage());

            return new ResponseEntity<String>("Policy has not been added", HttpStatus.BAD_REQUEST);

        }

    }

    @Transactional
    public ResponseEntity<String> deletePolicy(String contractUuid) {
        try {

            Optional<ContractStore> bCheckExistsContract = this.contractRepository.findByContractUuid(contractUuid);

            // if exists delete
            if (bCheckExistsContract.isPresent()) {

                log.info("PolicyService:::deleting policy :" + contractUuid);

                List<RuleStore> ruleStores = this.ruleRepository.deleteByContractUuid(contractUuid);
                log.info("PolicyService:::policyId :" + ruleStores.size());

                if (ruleStores.size() != 0) {
                    Long num = this.contractRepository.deleteByContractUuid(contractUuid);
                    log.info("PolicyService:::policyId :" + num);
                }
                return new ResponseEntity<String>("Contract Agreement has been deleted", HttpStatus.OK);
            } else
                return new ResponseEntity<String>("Contract Agreement does not exists", HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            return new ResponseEntity<String>("An error happened during contract agreement deletion process",
                    HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<List<ContractStore>> getAllOdrlPolicyPersistence() {

        log.info("PolicyService:::getAllOdrlPolicyPersistence:");
        try {
            return new ResponseEntity<List<ContractStore>>(this.contractRepository.findAll(), HttpStatus.OK);
            // return new
            // ResponseEntity<List<OdrlPolicy>>(this.repository.getPolicyAndPolicyString(),
            // HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<List<ContractStore>>(this.contractRepository.findAll(), HttpStatus.BAD_REQUEST);
            // return new
            // ResponseEntity<List<OdrlPolicy>>(this.repository.getPolicyAndPolicyString(),
            // HttpStatus.OK);
        }

        // return this.repository.findAll();

        // return this.repository.findAll();

    }

    private ContractStore setProfile(String jsonString, ContractStore contractStore) throws URISyntaxException {
        try {
            Connector connector = this.getSelfDescription.getSelfDescription();
            if (connector != null && connector.getSecurityProfile() != null & !connector.getSecurityProfile().toString().isEmpty()) {
                contractStore.setProfile(connector.getSecurityProfile().toString());
                return contractStore;
            }

            /*JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject profileObj = jsonObject.optJSONObject("ids:profile");

            if (profileObj != null && profileObj.has("@id")) {
                return contractStore.setProfile(profileObj.getString("@id"));
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return contractStore;
    }


}
