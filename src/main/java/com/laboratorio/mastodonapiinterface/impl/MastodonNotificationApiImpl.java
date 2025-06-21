package com.laboratorio.mastodonapiinterface.impl;

import com.google.gson.reflect.TypeToken;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.mastodonapiinterface.MastodonNotificationApi;
import com.laboratorio.mastodonapiinterface.exception.MastondonApiException;
import com.laboratorio.mastodonapiinterface.model.MastodonNotification;
import com.laboratorio.mastodonapiinterface.model.response.MastodonNotificationListResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rafael
 * @version 1.3
 * @created 25/07/2024
 * @updated 21/06/2025
 */
public class MastodonNotificationApiImpl extends MastodonBaseApi implements MastodonNotificationApi {
    public MastodonNotificationApiImpl(String urlBase, String accessToken) {
        super(urlBase, accessToken);
    }
    
    @Override
    public MastodonNotificationListResponse getAllNotifications() throws Exception {
        return this.getAllNotifications(0);
    }
    
    @Override
    public MastodonNotificationListResponse getAllNotifications(int limit) throws Exception {
        return this.getAllNotifications(limit, 0);
    }

    @Override
    public MastodonNotificationListResponse getAllNotifications(int limit, int quantity) throws Exception {
        return this.getAllNotifications(limit, quantity, null);
    }
    
    // Función que devuelve una página de notificaciones de una cuenta
    private MastodonNotificationListResponse getNotificationPage(String uri, int limit, int okStatus, String posicionInicial) throws Exception {
        try {
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("limit", Integer.toString(limit));
            request.addApiPathParam("min_id", posicionInicial);
            
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            String minId = posicionInicial;
            List<MastodonNotification> notifications = this.gson.fromJson(response.getResponseStr(), new TypeToken<List<MastodonNotification>>(){}.getType());
            if (!notifications.isEmpty()) {
                log.debug("Se ejecutó la query: " + uri);
                log.debug("Resultados encontrados: " + notifications.size());

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
                    minId = this.extractMinId(linkHeader);
                    log.debug("Valor del min_id: " + minId);
                }
            }

            // return accounts;
            return new MastodonNotificationListResponse(minId, notifications);
        } catch (Exception e) {
            throw new MastondonApiException("Error recuperando una página de notificaciones en Mastodon", e);
        }
    }

    @Override
    public MastodonNotificationListResponse getAllNotifications(int limit, int quantity, String posicionInicial) throws Exception {
        String endpoint = this.apiConfig.getProperty("getNotifications_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getNotifications_ok_status"));
        int defaultLimit = Integer.parseInt(this.apiConfig.getProperty("getNotifications_default_limit"));
        int maxLimit = Integer.parseInt(this.apiConfig.getProperty("getNotifications_max_limit"));
        int usedLimit = limit;
        if ((limit == 0) || (limit > maxLimit)) {
            usedLimit = defaultLimit;
        }
        List<MastodonNotification> notifications = null;
        boolean continuar = true;
        String min_id = "0";
        if (posicionInicial != null) {
            min_id = posicionInicial;
        }
        
        if (quantity > 0) {
            usedLimit = Math.min(usedLimit, quantity);
        }
        
        String uri = this.urlBase + endpoint;
        
        try {
            do {
                MastodonNotificationListResponse notificationListResponse = this.getNotificationPage(uri, usedLimit, okStatus, min_id);
                if (notifications == null) {
                    notifications = notificationListResponse.getNotifications();
                } else {
                    notifications.addAll(notificationListResponse.getNotifications());
                }
                
                min_id = notificationListResponse.getMinId();
                log.debug("getFollowers. Cantidad: " + quantity + ". Recuperados: " + notifications.size() + ". Min_id: " + min_id);
                if (notificationListResponse.getNotifications().isEmpty()) {
                    continuar = false;
                } else {
                    if (quantity > 0) {
                        if (notifications.size() >= quantity) {
                            continuar = false;
                        }
                    } else {
                        if (notificationListResponse.getNotifications().size() < usedLimit) {
                            continuar = false;
                        }
                    }
                }
            } while (continuar);

            if (quantity == 0) {
                return new MastodonNotificationListResponse(min_id, notifications);
            }
            
            return new MastodonNotificationListResponse(min_id, notifications.subList(0, Math.min(quantity, notifications.size())));
        } catch (Exception e) {
            throw e;
        }
    }
}