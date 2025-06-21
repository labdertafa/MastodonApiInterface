package com.laboratorio.api;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.mastodonapiinterface.MastodonNotificationApi;
import com.laboratorio.mastodonapiinterface.exception.MastondonApiException;
import com.laboratorio.mastodonapiinterface.impl.MastodonNotificationApiImpl;
import com.laboratorio.mastodonapiinterface.model.response.MastodonNotificationListResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 25/07/2024
 * @updated 21/06/2025
 */
public class MastodonNotificationApiTest {
    private String accessToken;
    private MastodonNotificationApi notificationApi;
    
    @BeforeEach
    private void initNotificationApi() {
        ReaderConfig config = new ReaderConfig("config//mastodon_api.properties");
        this.accessToken = config.getProperty("access_token");
        String urlBase = config.getProperty("instancia_test");
        this.notificationApi = new MastodonNotificationApiImpl(urlBase, this.accessToken);
    }
    
    @Test
    public void get20Notifications() throws Exception { // Con default limit
        int cantidad  = 20;
        
        MastodonNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(0, cantidad);

        assertEquals(cantidad, notificationListResponse.getNotifications().size());
        assertTrue(notificationListResponse.getMinId() != null);
    }
    
    @Test
    public void get20NotificationsWithLimit() throws Exception { // Con limit
        int cantidad  = 20;
        int limit = 50;
        
        MastodonNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(limit, cantidad);

        assertEquals(cantidad, notificationListResponse.getNotifications().size());
        assertTrue(notificationListResponse.getMinId() != null);
    }
    
    @Test
    public void get110Notifications() throws Exception {
        int cantidad  = 50;
        
        MastodonNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(0, cantidad);

        assertEquals(cantidad, notificationListResponse.getNotifications().size());
        assertTrue(notificationListResponse.getMinId() != null);
    }
    
    @Test
    public void getAllNotifications() throws Exception {
        MastodonNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(80);

        assertTrue(notificationListResponse.getNotifications().size() > 20);
        assertTrue(notificationListResponse.getMinId() != null);
    }
    
    @Test
    public void getNotificationError() {
        ReaderConfig config = new ReaderConfig("config//mastodon_api.properties");
        String urlBase = config.getProperty("instancia_test");
        
        this.notificationApi = new MastodonNotificationApiImpl(urlBase, "INVALID_TOKEN");

        assertThrows(MastondonApiException.class, () -> {
            this.notificationApi.getAllNotifications();
        });
    }
    
    @Test
    public void getNotificationsWithSinceId() throws Exception {
        String sinceId = "31536449";

        MastodonNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(0, 0, sinceId);

        assertTrue(notificationListResponse.getNotifications().size() >= 0);
        assertTrue(notificationListResponse.getMinId() != null);
    }
}