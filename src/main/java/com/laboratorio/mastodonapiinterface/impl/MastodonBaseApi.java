package com.laboratorio.mastodonapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laboratorio.clientapilibrary.ApiClient;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.mastodonapiinterface.exception.MastondonApiException;
import com.laboratorio.mastodonapiinterface.model.MastodonAccount;
import com.laboratorio.mastodonapiinterface.model.response.MastodonAccountListResponse;
import com.laboratorio.mastodonapiinterface.utils.InstruccionInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael
 * @version 1.4
 * @created 24/07/2024
 * @updated 13/12/2025
 */
public class MastodonBaseApi {
    protected static final Logger log = LogManager.getLogger(MastodonBaseApi.class);
    protected final ApiClient client;
    protected final String urlBase;
    protected final String accessToken;
    protected ReaderConfig apiConfig;
    protected final Gson gson;

    public MastodonBaseApi(String urlBase, String accessToken) {
        this.urlBase = urlBase;
        this.accessToken = accessToken;
        this.apiConfig = new ReaderConfig("config//mastodon_api.properties");
        this.gson = new Gson();
        String proxyHost = this.apiConfig.getProperty("mastodon_proxy_host");
        String proxyPortStr = this.apiConfig.getProperty("mastodon_proxy_port");
        String certificatePath = this.apiConfig.getProperty("mastodon_proxy_certificate");
        if (proxyHost != null && !proxyHost.isBlank() && proxyPortStr != null && !proxyPortStr.isBlank()
                && certificatePath != null && !certificatePath.isBlank()) {
            int proxyPort = Integer.parseInt(proxyPortStr);
            this.client = new ApiClient(proxyHost, proxyPort, certificatePath);
        } else {
            this.client = new ApiClient();
        }
    }
    
    // Función que extrae el max_id de la respuesta
    protected String extractMaxId(String str) {
        String maxId = null;
        String regex = "max_id=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        
        if (matcher.find()) {
            maxId = matcher.group(1); // El primer grupo de captura contiene el valor de max_id
        }
        
        return maxId;
    }
    
    // Función que extrae el min_id de la respuesta
    protected String extractMinId(String str) {
        String maxId = null;
        String regex = "min_id=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        
        if (matcher.find()) {
            maxId = matcher.group(1); // El primer grupo de captura contiene el valor de max_id
        }
        
        return maxId;
    }
    
    // Función que devuelve una página de seguidores o seguidos de una cuenta
    private MastodonAccountListResponse getAccountPage(String uri, int okStatus, int limit, String posicionInicial) throws Exception {
        try {
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("limit", Integer.toString(limit));
            if (posicionInicial != null) {
                request.addApiPathParam("max_id", posicionInicial);
            }
            
            request.addApiHeader("Content-Type", "application/json");
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            List<MastodonAccount> accounts = this.gson.fromJson(response.getResponseStr(), new TypeToken<List<MastodonAccount>>(){}.getType());
            String maxId = null;
            if (!accounts.isEmpty()) {
                log.debug("Se ejecutó la query: " + uri);
                log.debug("Resultados encontrados: " + accounts.size());

                List<String> linkHeaderList = new ArrayList<>();
                List<String> tempLinkHeaderList = response.getHttpHeaders().get("link");
                if (tempLinkHeaderList != null) {
                    linkHeaderList.addAll(tempLinkHeaderList);
                }
                tempLinkHeaderList = response.getHttpHeaders().get("Link");
                if (tempLinkHeaderList != null) {
                    linkHeaderList.addAll(tempLinkHeaderList);
                }
                if (!linkHeaderList.isEmpty()) {
                    String linkHeader = linkHeaderList.get(0);
                    log.debug("Recibí este Link: " + linkHeader);
                    maxId = this.extractMaxId(linkHeader);
                    log.debug("Valor del max_id: " + maxId);
                }
            }

            return new MastodonAccountListResponse(maxId, accounts);
        } catch (Exception e) {
            throw new MastondonApiException("Error recuperando una página de una cuenta en Mastodon. Uri: " + uri, e);
        }
    }
    
    private boolean isContinuar(int quantity, List<MastodonAccount> accounts, String maxId,
            MastodonAccountListResponse accountListResponse, int limit) {
        log.debug("getMastodonAccountList. Cantidad: " + quantity + ". Recuperados: " + accounts.size() + ". Max_id: " + maxId);
        if (quantity > 0) {
            if ((accounts.size() >= quantity) || (maxId == null)) {
                return false;
            }
        } else {
            if ((maxId == null) || (accountListResponse.getAccounts().size() < limit)) {
                return false;
            }
        }

        return true;
    }
    
    protected MastodonAccountListResponse getMastodonAccountList(InstruccionInfo instruccionInfo, String userId, int quantity, String posicionInicial) throws Exception {
        List<MastodonAccount> accounts = null;
        boolean continuar;
        String endpoint = instruccionInfo.getEndpoint();
        String complemento = instruccionInfo.getComplementoUrl();
        int limit = instruccionInfo.getLimit();
        int okStatus = instruccionInfo.getOkStatus();
        String maxId = posicionInicial;
        
        if (quantity > 0) {
            limit = Math.min(limit, quantity);
        }
        
        String uri = this.urlBase + endpoint + "/" + userId + "/" + complemento;
        
        do {
            MastodonAccountListResponse accountListResponse = this.getAccountPage(uri, okStatus, limit, maxId);
            if (accounts == null) {
                accounts = accountListResponse.getAccounts();
            } else {
                accounts.addAll(accountListResponse.getAccounts());
            }

            maxId = accountListResponse.getMaxId();
            continuar = this.isContinuar(quantity, accounts, maxId, accountListResponse, limit);
        } while (continuar);

        if (quantity == 0) {
            return new MastodonAccountListResponse(maxId, accounts);
        }

        return new MastodonAccountListResponse(maxId, accounts.subList(0, Math.min(quantity, accounts.size())));
    }
}