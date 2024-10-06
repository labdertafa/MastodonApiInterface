package com.laboratorio.mastodonapiinterface.impl;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.laboratorio.clientapilibrary.exceptions.ApiClientException;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.mastodonapiinterface.MastodonAccountApi;
import com.laboratorio.mastodonapiinterface.exception.MastondonApiException;
import com.laboratorio.mastodonapiinterface.model.MastodonAccount;
import com.laboratorio.mastodonapiinterface.model.MastodonRelationship;
import com.laboratorio.mastodonapiinterface.model.MastodonSuggestion;
import com.laboratorio.mastodonapiinterface.model.response.MastodonAccountListResponse;
import com.laboratorio.mastodonapiinterface.utils.InstruccionInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Rafael
 * @version 1.3
 * @created 10/07/2024
 * @updated 04/10/2024
 */
public class MastodonAccountApiImpl extends MastodonBaseApi implements MastodonAccountApi {
    public MastodonAccountApiImpl(String urlBase, String accessToken) {
        super(urlBase, accessToken);
    }
    
    @Override
    public MastodonAccount getAccountById(String id) {
        String endpoint = this.apiConfig.getProperty("getAccountById_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getAccountById_ok_status"));
        
        try {
            String url = this.urlBase + endpoint + "/" + id;
            ApiRequest request = new ApiRequest(url, okStatus, ApiMethodType.GET);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            return this.gson.fromJson(response.getResponseStr(), MastodonAccount.class);
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            logException(e);
            throw new MastondonApiException(MastodonAccountApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public MastodonAccount getAccountByUsername(String username) {
        String endpoint = this.apiConfig.getProperty("getAccountByUsername_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getAccountByUsername_ok_status"));
        
        try {
            String url = this.urlBase + endpoint;
            ApiRequest request = new ApiRequest(url, okStatus, ApiMethodType.GET);
            request.addApiPathParam("acct", username);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            return this.gson.fromJson(response.getResponseStr(), MastodonAccount.class);
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            logException(e);
            throw new MastondonApiException(MastodonAccountApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public MastodonAccountListResponse getFollowers(String id) throws Exception {
        return this.getFollowers(id, 0);
    }
    
    @Override
    public MastodonAccountListResponse getFollowers(String id, int limit) throws Exception {
        return this.getFollowers(id, limit, 0);
    }
    
    @Override
    public MastodonAccountListResponse getFollowers(String id, int limit, int quantity) throws Exception {
        return getFollowers(id, limit, quantity, null);
    }
    
    @Override
    public MastodonAccountListResponse getFollowers(String id, int limit, int quantity, String posicionInicial) throws Exception {
        String endpoint = this.apiConfig.getProperty("getFollowers_endpoint");
        String complementoUrl = this.apiConfig.getProperty("getFollowers_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getFollowers_ok_status"));
        int defaultLimit = Integer.parseInt(this.apiConfig.getProperty("getFollowers_default_limit"));
        int maxLimit = Integer.parseInt(this.apiConfig.getProperty("getFollowers_max_limit"));
        int usedLimit = limit;
        if ((limit == 0) || (limit > maxLimit)) {
            usedLimit = defaultLimit;
        }
        InstruccionInfo instruccionInfo = new InstruccionInfo(endpoint, complementoUrl, okStatus, usedLimit);
        return this.getMastodonAccountList(instruccionInfo, id, quantity, posicionInicial);
    }
    
    @Override
    public MastodonAccountListResponse getFollowings(String id) throws Exception {
        return this.getFollowings(id, 0);
    }
    
    @Override
    public MastodonAccountListResponse getFollowings(String id, int limit) throws Exception {
        return this.getFollowings(id, limit, 0);
    }
    
    @Override
    public MastodonAccountListResponse getFollowings(String id, int limit, int quantity) throws Exception {
        return this.getFollowings(id, limit, quantity, null);
    }
    
    @Override
    public MastodonAccountListResponse getFollowings(String id, int limit, int quantity, String posicionInicial) throws Exception {
        String endpoint = this.apiConfig.getProperty("getFollowings_endpoint");
        String complementoUrl = this.apiConfig.getProperty("getFollowings_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getFollowings_ok_status"));
        int defaultLimit = Integer.parseInt(this.apiConfig.getProperty("getFollowings_default_limit"));
        int maxLimit = Integer.parseInt(this.apiConfig.getProperty("getFollowings_max_limit"));
        int usedLimit = limit;
        if ((limit == 0) || (limit > maxLimit)) {
            usedLimit = defaultLimit;
        }
        InstruccionInfo instruccionInfo = new InstruccionInfo(endpoint, complementoUrl, okStatus, usedLimit);
        return this.getMastodonAccountList(instruccionInfo, id, quantity, posicionInicial);
    }
    
    @Override
    public boolean followAccount(String id) {
        String endpoint = this.apiConfig.getProperty("followAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("followAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("followAccount_ok_status"));
        
        try {
            String uri = this.urlBase + endpoint + "/" + id + "/" + complementoUrl;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            request.addApiHeader("Content-Type", "application/json");
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            MastodonRelationship relationship = this.gson.fromJson(response.getResponseStr(), MastodonRelationship.class);
            
            return relationship.isFollowing();
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (Exception e) {
            logException(e);
            throw new MastondonApiException(MastodonAccountApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public boolean unfollowAccount(String id) {
        String endpoint = this.apiConfig.getProperty("unfollowAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("unfollowAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("unfollowAccount_ok_status"));
        
        try {
            String uri = this.urlBase + endpoint + "/" + id + "/" + complementoUrl;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            request.addApiHeader("Content-Type", "application/json");
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            MastodonRelationship relationship = this.gson.fromJson(response.getResponseStr(), MastodonRelationship.class);
            
            return !relationship.isFollowing();
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (Exception e) {
            logException(e);
            throw new MastondonApiException(MastodonAccountApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public List<MastodonRelationship> checkrelationships(List<String> ids) {
        String endpoint = this.apiConfig.getProperty("checkrelationships_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("checkrelationships_ok_status"));
        
        try {
            String uri = this.urlBase + endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            for (String id : ids) {
                request.addApiPathParam("id[]", id);
            }
            request.addApiHeader("Content-Type", "application/json");
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            return this.gson.fromJson(response.getResponseStr(), new TypeToken<List<MastodonRelationship>>(){}.getType());
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            logException(e);
            throw new MastondonApiException(MastodonAccountApiImpl.class.getName(), e.getMessage());
        }
    }

    @Override
    public List<MastodonAccount> getSuggestions() {
        return this.getSuggestions(0);
    }

    @Override
    public List<MastodonAccount> getSuggestions(int limit) {
        String endpoint = this.apiConfig.getProperty("getSuggestions_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getSuggestions_ok_status"));
        int defaultLimit = Integer.parseInt(this.apiConfig.getProperty("getSuggestions_default_limit"));
        int maxLimit = Integer.parseInt(this.apiConfig.getProperty("getSuggestions_max_limit"));
        int usedLimit = limit;
        if ((limit == 0) || (limit > maxLimit)) {
            usedLimit = defaultLimit;
        }
        
        try {
            String uri = this.urlBase + endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("limit", Integer.toString(usedLimit));
         
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            List<MastodonSuggestion> suggestions = this.gson.fromJson(response.getResponseStr(), new TypeToken<List<MastodonSuggestion>>(){}.getType());
            
            return suggestions.stream()
                    .map(s -> s.getAccount())
                    .collect(Collectors.toList());
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            logException(e);
            throw new MastondonApiException(MastodonAccountApiImpl.class.getName(), e.getMessage());
        }
    }

    @Override
    public boolean deleteSuggestion(String userId) {
        String endpoint = this.apiConfig.getProperty("deleteSuggestion_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("deleteSuggestion_ok_status"));
        
        try {
            String uri = this.urlBase + endpoint + "/" + userId;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.DELETE);
            request.addApiHeader("Content-Type", "application/json");
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            this.client.executeApiRequest(request);
            
            return true;
        } catch (ApiClientException e) {
            throw e;
        } catch (Exception e) {
            logException(e);
            throw new MastondonApiException(MastodonAccountApiImpl.class.getName(), e.getMessage());
        }
    }
}