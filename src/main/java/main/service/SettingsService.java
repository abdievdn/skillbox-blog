package main.service;

import main.Blog;
import main.api.request.general.SettingsRequest;
import main.api.response.general.SettingsResponse;
import main.model.GlobalSettings;
import main.model.repository.SettingsRepository;
import main.service.enums.SettingsCode;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
        createSettingsIfNoExist();
    }

    private void createSettingsIfNoExist() {
        Iterable<GlobalSettings> globalSettings = settingsRepository.findAll();
        if (globalSettings.iterator().hasNext()) {
            return;
        }
        insertSettings(SettingsCode.MULTIUSER_MODE.name(), Blog.SETTINGS_MULTIUSER_MODE_TEXT, Blog.YES_VALUE);
        insertSettings(SettingsCode.POST_PREMODERATION.name(), Blog.SETTINGS_POST_PREMODERATION_TEXT, Blog.YES_VALUE);
        insertSettings(SettingsCode.STATISTICS_IS_PUBLIC.name(), Blog.SETTINGS_STATISTICS_IS_PUBLIC_TEXT, Blog.NO_VALUE);
    }

    private void insertSettings(String code, String name, String value) {
        GlobalSettings globalSettings = new GlobalSettings();
        globalSettings.setCode(code);
        globalSettings.setName(name);
        globalSettings.setValue(value);
        settingsRepository.save(globalSettings);
    }

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        Iterable<GlobalSettings> globalSettings = settingsRepository.findAll();
        for (GlobalSettings settings : globalSettings) {
            switch (SettingsCode.valueOf(settings.getCode())) {
                case MULTIUSER_MODE :
                    settingsResponse.setMultiuserMode(stringToBoolean(settings.getValue()));
                    break;
                case POST_PREMODERATION:
                    settingsResponse.setPostPremoderation(stringToBoolean(settings.getValue()));
                    break;
                case STATISTICS_IS_PUBLIC:
                    settingsResponse.setStatisticsIsPublic(stringToBoolean(settings.getValue()));
                    break;
                default:
                    break;
            }
        }
        return settingsResponse;
    }

    public void setGlobalSettings(SettingsRequest settingsRequest) {
        Iterable<GlobalSettings> globalSettingsIterable = settingsRepository.findAll();
        for (GlobalSettings settings : globalSettingsIterable) {
            switch (SettingsCode.valueOf(settings.getCode())) {
                case MULTIUSER_MODE:
                    settings.setValue(booleanToString(settingsRequest.isMultiuserMode()));
                    break;
                case POST_PREMODERATION:
                    settings.setValue(booleanToString(settingsRequest.isPostPremoderation()));
                    break;
                case STATISTICS_IS_PUBLIC:
                    settings.setValue(booleanToString(settingsRequest.isStatisticsIsPublic()));
                    break;
                default: break;
            }
            settingsRepository.save(settings);
        }
    }

    private boolean stringToBoolean(String value) {
        return value.equals("YES");
    }

    private String booleanToString (boolean value) {
        return value ? "YES" : "NO";
    }
}