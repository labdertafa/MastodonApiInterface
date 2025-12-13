package com.laboratorio.mastodonapiinterface.impl;

import com.google.gson.reflect.TypeToken;
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
 * @version 1.6
 * @created 10/07/2024
 * @updated 13/12/2025
 */
public class MastodonAccountApiImpl extends MastodonBaseApi implements MastodonAccountApi {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    
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
            log.debug("Response getAccountById: {}", response.getResponseStr());
            
            return this.gson.fromJson(response.getResponseStr(), MastodonAccount.class);
        } catch (Exception e) {
            throw new MastondonApiException("Error recuperando los datos de la cuenta Mastodon con id: " + id, e);
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
            log.debug("Response getAccountByUsername: {}", response.getResponseStr());
            
            return this.gson.fromJson(response.getResponseStr(), MastodonAccount.class);
        } catch (Exception e) {
            throw new MastondonApiException("Error recuperando los datos de la cuenta Mastodon con username: " + username, e);
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
    public List<String> getFollowersIds(String userId) throws Exception {
        return this.getFollowersIds(userId, 0);
    }
    @Override
    public List<String> getFollowersIds(String userId, int limit) throws Exception {
        MastodonAccountListResponse response = this.getFollowers(userId, limit);
        return response.getAccounts().stream()
                .map(account -> account.getId())
                .collect(Collectors.toList());
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
    public List<String> getFollowingsIds(String userId) throws Exception {
        return this.getFollowingsIds(userId, 0);
    }
    
    @Override
    public List<String> getFollowingsIds(String userId, int limit) throws Exception {
        MastodonAccountListResponse response = this.getFollowings(userId, limit);
        return response.getAccounts().stream()
                .map(account -> account.getId())
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean followAccount(String id) {
        String endpoint = this.apiConfig.getProperty("followAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("followAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("followAccount_ok_status"));
        
        try {
            String uri = this.urlBase + endpoint + "/" + id + "/" + complementoUrl;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            request.addApiHeader(CONTENT_TYPE, APPLICATION_JSON);
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response followAccount: {}", response.getResponseStr());
            MastodonRelationship relationship = this.gson.fromJson(response.getResponseStr(), MastodonRelationship.class);
            
            return relationship.isFollowing();
        } catch (Exception e) {
            throw new MastondonApiException("Error siguiendo la cuenta Mastodon con id: " + id, e);
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
            request.addApiHeader(CONTENT_TYPE, APPLICATION_JSON);
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response unfollowAccount: {}", response.getResponseStr());
            MastodonRelationship relationship = this.gson.fromJson(response.getResponseStr(), MastodonRelationship.class);
            
            return !relationship.isFollowing();
        } catch (Exception e) {
            throw new MastondonApiException("Error dejando de seguir la cuenta Mastodon con id: " + id, e);
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
            request.addApiHeader(CONTENT_TYPE, APPLICATION_JSON);
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response checkrelationships: {}", response.getResponseStr());
            
            return this.gson.fromJson(response.getResponseStr(), new TypeToken<List<MastodonRelationship>>(){}.getType());
        } catch (Exception e) {
            throw new MastondonApiException("Error recuperando la relación entre cuentas Mastodon", e);
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
         
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response getSuggestions: {}", response.getResponseStr());
            List<MastodonSuggestion> suggestions = this.gson.fromJson(response.getResponseStr(), new TypeToken<List<MastodonSuggestion>>(){}.getType());
            
            return suggestions.stream()
                    .map(s -> s.getAccount())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MastondonApiException("Error recuperando las sugerencias de seguimiento en Mastodon", e);
        }
    }

    @Override
    public boolean deleteSuggestion(String userId) {
        String endpoint = this.apiConfig.getProperty("deleteSuggestion_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("deleteSuggestion_ok_status"));
        
        try {
            String uri = this.urlBase + endpoint + "/" + userId;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.DELETE);
            request.addApiHeader(CONTENT_TYPE, APPLICATION_JSON);
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            this.client.executeApiRequest(request);
            
            return true;
        } catch (Exception e) {
            throw new MastondonApiException("Error eliminando la sugerencia de seguimiento en Mastodon con el id: " + userId, e);
        }
    }
}