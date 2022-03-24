package service;

import api.response.SettingsResponse;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(false);
        settingsResponse.setPostPremoderation(true);
        settingsResponse.setStatisticsIsPublic(true);
        return settingsResponse;
    }
}
